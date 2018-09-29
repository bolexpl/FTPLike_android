package com.example.bolek.ftplclient.activities

import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.bolek.ftplclient.*
import com.example.bolek.ftplclient.lib.RecyclerItemClickListener
import com.example.bolek.ftplclient.model.FileInfo
import com.example.bolek.ftplclient.model.LocalExplorer
import com.example.bolek.ftplclient.model.RemoteExplorer
import com.example.bolek.ftplclient.model.ExplorerUpdater

import kotlinx.android.synthetic.main.activity_explorer.*
import kotlinx.android.synthetic.main.fragment_explorer.view.*

class ExplorerActivity : AppCompatActivity() {

    lateinit var localUpdater: ExplorerUpdater
    lateinit var remoteAdapter: ExplorerAdapter
    private var back = false
    private var thread: Thread? = null


    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorer)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, this)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_explorer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
            R.id.action_settings -> {
                return true
            }
            R.id.action_disconnect -> {
                RemoteExplorer.disconnect()
                val i = Intent(this, LoginActivity::class.java)
                startActivity(i)
                finish()
                return true
            }
            R.id.action_show_hidden -> {
                LocalExplorer.showHidden = !LocalExplorer.showHidden
//                RemoteExplorer.showHidden = !RemoteExplorer.showHidden
                localUpdater.refresh()
//                remoteAdapter.updateAll()
                return true
            }
            R.id.action_debug -> {
                //debug
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager, private val activity: ExplorerActivity) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            // getItem is called to instantiate the fragment for the given page.
            if (position == 0) {
                val l = LocalFragment()

                l.activity = activity
                return l
            }

            val r = RemoteFragment()
            r.activity = activity
            return r
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 2
        }
    }

    override fun onBackPressed() {
        if (back) {
            thread?.interrupt()
//            val intent = Intent(Intent.ACTION_MAIN)
//            intent.addCategory(Intent.CATEGORY_HOME)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
            finish()
            System.exit(0)
//            moveTaskToBack(true)
        } else {
            Toast.makeText(applicationContext, resources.getString(R.string.back_press), Toast.LENGTH_SHORT).show()
            back = true
            thread?.interrupt()
            thread = Thread(Runnable {
                try {
                    Thread.sleep(3000)
                    back = false
                } catch (e: InterruptedException) {
                }
            })
            thread!!.start()
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class LocalFragment : Fragment() {

        lateinit var activity: ExplorerActivity
        lateinit var updater: ExplorerUpdater
        lateinit var adapter: ExplorerAdapter
        lateinit var path: EditText
        var isMultiSelect = false
        var mActionMode: ActionMode? = null
        var selected: MutableList<FileInfo> = ArrayList()
        var contextMenu: Menu? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_explorer, container, false)
            val r = rootView.recycler

            path = rootView.findViewById(R.id.path)

            adapter = ExplorerAdapter(context!!, selected)
            updater = ExplorerUpdater(context!!, LocalExplorer, adapter, path)
            adapter.updater = updater
            updater.refresh()

            activity.localUpdater = updater
            r.setHasFixedSize(true)
            r.layoutManager = LinearLayoutManager(context)
            r.itemAnimator = DefaultItemAnimator()
            r.adapter = adapter

            r.addOnItemTouchListener(RecyclerItemClickListener(context!!, r,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            if (isMultiSelect) {
                                if (adapter.list[position].fileName != "..")
                                    multiSelect(position)
                            } else {
                                updater.cd(adapter.list[position].fileName)
                            }
                        }

                        override fun onItemLongClick(view: View, position: Int) {
                            if (!isMultiSelect) {
                                selected = ArrayList()
                                isMultiSelect = true

                                if (mActionMode == null) {
                                    mActionMode =
                                            (context as ExplorerActivity)
                                                    .startActionMode(mActionModeCallback)
                                }
                            }

                            multiSelect(position)
                        }

                        override fun onSpecialButtonClick(view: View, position: Int) {
                            if (adapter.list[position].fileName == ".." ||
                                    isMultiSelect) return

                            val popup = PopupMenu(context!!, view)
                            popup.inflate(R.menu.menu_popup)

                            popup.setOnMenuItemClickListener {
                                val item = adapter.list[position]
                                when (it.itemId) {
                                    R.id.action_open -> {
                                        updater.cd(item.fileName)
                                        true
                                    }
                                    R.id.action_rename -> {
                                        true
                                    }
                                    R.id.action_attach -> {
                                        true
                                    }
                                    R.id.action_delete -> {
                                        adapter.list.removeAt(position)
                                        adapter.notifyItemRemoved(position)
                                        true
                                    }
                                    else -> false

                                }
                            }
                            popup.show()
                        }
                    }))

            return rootView
        }

        fun multiSelect(position: Int) {
            if (mActionMode != null) {
                if (selected.contains(adapter.list[position]))
                    selected.remove(adapter.list[position])
                else
                    selected.add(adapter.list[position])

                if (selected.size > 0)
                    mActionMode!!.title = selected.size.toString()
                else {
                    mActionMode!!.finish()
                }
                refreshAdapter()
            }
        }

        private val mActionModeCallback = object : ActionMode.Callback {

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                // Inflate a menu resource providing context menu items
                val inflater = mode.menuInflater
                inflater.inflate(R.menu.menu_multi_select, menu)
                contextMenu = menu
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false // Return false if nothing is done
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_delete -> {
                        Toast.makeText(context!!, "Klik", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                mActionMode = null
                isMultiSelect = false
                selected = ArrayList()
                refreshAdapter()
            }
        }

        fun refreshAdapter() {
            adapter.selected = selected
            adapter.notifyDataSetChanged()
        }
    }

    class RemoteFragment : Fragment() {

        lateinit var activity: ExplorerActivity
        lateinit var path: EditText

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_explorer, container, false)

            path = rootView.findViewById(R.id.path)

//            val r = rootView.recycler
//            r.setHasFixedSize(true)
//            r.layoutManager = LinearLayoutManager(context)
//            r.itemAnimator = DefaultItemAnimator()
//
//            val list= LocalExplorer.listFiles()
//
//            r.adapter = ExplorerAdapter(list, context!!)

            return rootView
        }
    }
}
