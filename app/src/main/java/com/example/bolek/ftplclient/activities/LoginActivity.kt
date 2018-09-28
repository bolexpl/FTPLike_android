package com.example.bolek.ftplclient.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.example.bolek.ftplclient.R
import com.example.bolek.ftplclient.model.RemoteExplorer
import java.io.IOException
import java.lang.ref.WeakReference

class LoginActivity : AppCompatActivity() {

    val REQUEST_CODE = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val bt = findViewById<Button>(R.id.connect_button)
        bt.setOnClickListener { connect() }

        setupPermissions()

        val i = Intent(this, ExplorerActivity::class.java)
        startActivity(i)
    }

    private fun setupPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                Log.e("DEBUG", grantResults.size.toString())
                if (grantResults.isNotEmpty()
                        && (grantResults[0] == PackageManager.PERMISSION_DENIED
                                || grantResults[1] == PackageManager.PERMISSION_DENIED
                                )) {
                    finishAndRemoveTask()
                    System.exit(0)
                }
            }
        }
    }

    fun connect() {
        val progress = findViewById<ProgressBar>(R.id.login_progress)
        val host = findViewById<TextInputEditText>(R.id.host)
        val port = findViewById<TextInputEditText>(R.id.port)
        val login = findViewById<TextInputEditText>(R.id.login)
        val password = findViewById<TextInputEditText>(R.id.password)
        var valid = true

        if (TextUtils.isEmpty(host.text)) {
            host.error = resources.getString(R.string.error_field_required)
            valid = false
        }
        if (TextUtils.isEmpty(port.text)) {
            port.error = resources.getString(R.string.error_field_required)
            valid = false
        }
        if (TextUtils.isEmpty(login.text)) {
            login.error = resources.getString(R.string.error_field_required)
            valid = false
        }
        if (TextUtils.isEmpty(password.text)) {
            password.error = resources.getString(R.string.error_field_required)
            valid = false
        }
        if (valid) {
            progress.visibility = View.VISIBLE
        }

        val h = host.text.toString()
        val p = port.text.toString().toInt()
        val l = login.text.toString()
        val pass = password.text.toString()

        val th = ConnectThread(h, p, l, pass, WeakReference(this), WeakReference(progress))
        th.execute()
    }

    companion object {
        private class ConnectThread(private val host: String,
                                    private val port: Int,
                                    private val login: String,
                                    private val password: String,
                                    private val loginActivity: WeakReference<LoginActivity>,
                                    private val progress: WeakReference<ProgressBar>)
            : AsyncTask<Void, Void, Boolean>() {

            override fun doInBackground(vararg voids: Void): Boolean {

                try {
                    if (RemoteExplorer.connect(host, port, login, password, false, true)) {
                        return true
                    }
                    return false
                } catch (e: IOException) {
                    return false
                }
            }

            override fun onPostExecute(result: Boolean) {
                progress.get()?.visibility = View.GONE
                if (result) {
                    val i = Intent(loginActivity.get(), ExplorerActivity::class.java)
                    loginActivity.get()?.startActivity(i)
                } else {
                    Toast.makeText(loginActivity.get(), "Błąd połączenia", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
