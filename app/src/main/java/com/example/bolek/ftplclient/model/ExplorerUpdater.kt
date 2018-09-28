package com.example.bolek.ftplclient.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.bolek.ftplclient.ExplorerAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class ExplorerUpdater(private val context: Context,
                      private val explorer: IExplorer,
                      private val adapter: ExplorerAdapter) : AutoCloseable {

    private val disposables = CompositeDisposable()

    fun refresh() {
        val single = explorer.listFiles()
        val observer = object : DisposableSingleObserver<List<FileInfo>>() {
            override fun onSuccess(t: List<FileInfo>) {
                adapter.list = t
                adapter.notifyDataSetChanged()
            }

            override fun onError(e: Throwable) {
                Toast.makeText(context, "Błąd połączenia", Toast.LENGTH_SHORT).show()
                Log.e("ERROR", e.localizedMessage)
            }
        }
        disposables.add(single
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer)
        )
    }

    fun cd(directory: String) {
        val single = explorer.cd(directory)
        val observer = object : DisposableSingleObserver<Boolean>() {
            override fun onSuccess(t: Boolean) {
                refresh()
            }

            override fun onError(e: Throwable) {
                Toast.makeText(context, "Nie można otworzyć", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun close() {
        disposables.clear()
    }
}