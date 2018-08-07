package com.example.bolek.ftplclient

import android.os.Environment
import java.io.File
import java.io.FileFilter

object LocalExplorer {

    var path = Environment.getExternalStorageDirectory().absolutePath
    var showHidden = true

    fun listFiles(): List<FileInfo> {
        val list = ArrayList<FileInfo>()
        list.add(FileInfo("..", true, false, 0))

        val files: Array<File>
        val dirs: Array<File>
        if(showHidden) {
            dirs = File(path).listFiles(FileFilter {
                it.isDirectory
            })
            files = File(path).listFiles(FileFilter {
                !it.isDirectory
            })
        }else{
            dirs = File(path).listFiles(FileFilter {
                it.isDirectory && !it.isHidden
            })
            files = File(path).listFiles(FileFilter {
                !it.isDirectory && !it.isHidden
            })
        }

        dirs.sortBy{ it.name.toLowerCase() }
        files.sortBy{ it.name.toLowerCase() }

        for (f in dirs) {
            list.add(FileInfo(f.name, f.isDirectory, f.isHidden, f.length()))
        }
        for (f in files) {
            list.add(FileInfo(f.name, f.isDirectory, f.isHidden, f.length()))
        }

        return list
    }
}
