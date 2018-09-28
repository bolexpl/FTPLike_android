package com.example.bolek.ftplclient.model

import android.os.Build
import android.os.Environment
import com.example.bolek.ftplclient.lib.Utils
import io.reactivex.Single
import java.io.*
import java.nio.file.Files


object LocalExplorer : IExplorer {

    var path: String = Environment.getExternalStorageDirectory().absolutePath
    var showHidden = true

    override fun getDir(): String {
        return path
    }

    override fun listFiles(): Single<List<FileInfo>> {
        return Single.create { emitter ->
            val list = ArrayList<FileInfo>()
            list.add(FileInfo("..", true, false, 0))

            val files: Array<File>
            val dirs: Array<File>
            if (showHidden) {
                dirs = File(path).listFiles(FileFilter {
                    it.isDirectory
                })
                files = File(path).listFiles(FileFilter {
                    !it.isDirectory
                })
            } else {
                dirs = File(path).listFiles(FileFilter {
                    it.isDirectory && !it.isHidden
                })
                files = File(path).listFiles(FileFilter {
                    !it.isDirectory && !it.isHidden
                })
            }

            dirs.sortBy { it.name.toLowerCase() }
            files.sortBy { it.name.toLowerCase() }

            for (f in dirs) {
                list.add(FileInfo(f.name, f.isDirectory, f.isHidden, f.length()))
            }
            for (f in files) {
                list.add(FileInfo(f.name, f.isDirectory, f.isHidden, f.length()))
            }

            emitter.onSuccess(list)
        }
    }

    @Throws(IOException::class)
    override fun login(login: String, pass: String): Single<Boolean> {
        return Single.just(true)
    }

    @Throws(IOException::class)
    override fun connectPassive(): Single<Boolean> {
        return Single.just(true)
    }

    @Throws(IOException::class)
    override fun connectActive(): Single<Boolean> {
        return Single.just(true)
    }

    override fun disconnect() {}

    override fun setDir(dir: String): Single<Boolean> {
        if (Utils.isAccess(dir)) {
            this.path = dir
            return Single.just(true)
        }
        return Single.just(false)
    }

    override fun copy(path1: String, path2: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val f1 = File(path1)
            val f2 = File(path2)
            Files.copy(f1.toPath(), f2.toPath())
            return
        }

        val input = FileInputStream(path1)
        val output = FileOutputStream(path2)

        // Copy the bits from instream to outstream
        val buf = ByteArray(1024)
        var len: Int

        while (true) {
            len = input.read(buf)
            if(len <= 0) break
            output.write(buf, 0, len)
        }

        input.close()
        output.close()
    }

    override fun pwd() {}

    override fun touch(name: String): Single<Boolean> {
        val f = File("$path/$name")
        return Single.just(f.createNewFile())
    }

    override fun append(fileName: String, data: String): Single<Boolean> {
        val f = File("$path/$fileName")

        if (!f.exists() || !f.canWrite()) return Single.just(false)

        val writer = FileWriter(f, true)
        val buff = BufferedWriter(writer)
        val printWriter = PrintWriter(buff)

        printWriter.write(data + "\n")

        printWriter.close()
        return Single.just(true)
    }

    override fun invertHidden() {
        showHidden = !showHidden
    }

    override fun getFile(path: String, localPath: String) {}

    override fun putFile(path: String, localPath: String) {}

    override fun cd(directory: String): Single<Boolean> {
        if (path[path.length - 1] != '/')
            path += "/"
        path += directory
        return Single.just(Utils.isAccess(path))
    }

    override fun cdParent(): Single<Boolean> {
        val s = File(path).parent
        if (s != null) path = s
        return Single.just(Utils.isAccess(path))
    }

    override fun rm(name: String): Single<Boolean> {
        val f = File(name)

        if (!f.exists()) return Single.just(false)

        if (f.isDirectory) {
            val entries = f.list()
            if (entries != null) {
                for (s in entries) {
                    rm("$name/$s")
                }
            }
        }

        return Single.just(f.delete())
    }

    override fun mkdir(dir: String): Single<Boolean> {
        val f = File("$path/$dir")
        return Single.just(f.mkdir())
    }

    override fun mv(oldFile: String, newFile: String): Single<Boolean> {
        val f = File(oldFile)
        return Single.just(f.renameTo(File(newFile)))
    }
}
