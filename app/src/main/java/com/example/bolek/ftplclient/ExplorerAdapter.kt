package com.example.bolek.ftplclient

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import com.example.bolek.ftplclient.model.FileInfo
import kotlinx.android.synthetic.main.file_item.view.*

class ExplorerAdapter(private val context: Context,
                      var selected : ArrayList<FileInfo>) :
        RecyclerView.Adapter<ExplorerAdapter.ViewHolder>() {

    var list: List<FileInfo> = emptyList()

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val icon = v.icon!!
        val checked = v.checked!!
        val fileName = v.fileName!!
        val fileSize = v.fileSize!!
        val popupButton = v.popupButton!!
    }

//    fun updateAll() {
//        if (local) {
//            list = LocalExplorer.listFiles()
//        } else {
//            //TODO
//        }
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.file_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {

        holder.fileName.text = list[i].fileName

        //TODO
//        val bt = holder.popupButton
//        bt.setOnClickListener{
//            val popup = PopupMenu(context, bt)
//            popup.inflate(R.menu.menu_popup)
//
//            popup.setOnMenuItemClickListener {
//                when(it.itemId){
//                    R.id.action_open ->{
//
//                    }
//                    else -> false
//
//                }
//            }
//        }

        if(selected.contains(list[i])){
            holder.checked.visibility = View.VISIBLE
        }else{
            holder.checked.visibility = View.GONE
        }

        if (list[i].isDir) {
            holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.folder))
            holder.fileSize.text = ""
        } else {
            holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.file))

            val length = list[i].length
            if (length < 1000) {
                holder.fileSize.text = String.format(context.resources.getString(R.string.size_b), length)
                return
            }

            var length2 = length.toDouble() / 1000
            if (length2 < 1000) {
                holder.fileSize.text = String.format(context.resources.getString(R.string.size_kb), length2)
                return
            }

            length2 /= 1000.0
            if (length2 < 1000) {
                holder.fileSize.text = String.format(context.resources.getString(R.string.size_mb), length2)
                return
            }

            length2 /= 1000.0
            if (length2 < 1000) {
                holder.fileSize.text = String.format(context.resources.getString(R.string.size_gb), length2)
            }
        }
    }
}