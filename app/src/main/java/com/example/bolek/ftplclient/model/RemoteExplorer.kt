package com.example.bolek.ftplclient.model

import com.example.bolek.ftplclient.Protocol
import com.example.bolek.ftplclient.lib.Base64Coder
import java.io.*
import java.net.Socket

object RemoteExplorer : AutoCloseable{

    private var socket: Socket? = null
    private var datasocket: Socket? = null
    private var input: BufferedReader? = null
    private var output: BufferedWriter? = null
    private var binary = false
    private var connected = false

    private var inASCII: BufferedReader? = null
    private var inBinary: BufferedInputStream? = null
    private var outASCII: BufferedWriter? = null
    private var outBinary: BufferedOutputStream? = null

    private lateinit var path: String
    var showHidden = true

    fun connect(host: String, port: Int, login: String, pass: String,
                binary: Boolean, passive: Boolean): Boolean {

        if (connected) return connected

        try {
            socket = Socket(host, port)

            input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
            output = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream()))

            connected = (login(login, pass)
                            && transfer(binary)
                            && mode(host, passive))

        } catch (e: IOException) {
            e.printStackTrace()
            connected = false
        }

        return connected
    }

    override fun close() {
        disconnect()
    }

    fun disconnect(){
        Thread {
            write(Protocol.EXIT)
            input?.close()
            output?.close()
            inASCII?.close()
            inBinary?.close()
            outASCII?.close()
            outBinary?.close()
            socket?.close()
            datasocket?.close()
            connected = false
        }.start()
    }

    fun login(login: String, pass: String): Boolean {
        write("${Protocol.USER} $login")
        if (input?.readLine() != Protocol.OK) return false

        write("${Protocol.PASSWORD} ${Base64Coder.encodeString(pass)}")

        return input?.readLine() == Protocol.OK
    }

    fun transfer(binary: Boolean): Boolean {
        RemoteExplorer.binary = binary
        if (binary)
            write("${Protocol.TRANSFER} ${Protocol.BINARY}")
        else
            write("${Protocol.TRANSFER} ${Protocol.ASCII}")

        return input?.readLine() == Protocol.OK
    }

    fun mode(host: String, passive: Boolean): Boolean {

        if (passive) {
            write(Protocol.PASSIV)
            val args = input!!.readLine().split(" ")

            if (args[0] == Protocol.PORT && args.size == 2) {
                val port = args[1]

                datasocket = Socket(host, port.toInt())
                if(binary){
                    inBinary = BufferedInputStream(datasocket!!.getInputStream())
                    outBinary = BufferedOutputStream(datasocket!!.getOutputStream())
                }else{
                    inASCII = BufferedReader(InputStreamReader(datasocket!!.getInputStream()))
                    outASCII = BufferedWriter(OutputStreamWriter(datasocket!!.getOutputStream()))
                }
            }

        } else {
            //TODO
        }

        return input?.readLine() == Protocol.OK
    }

    @Throws(IOException::class)
    fun write(s: String) {
        output?.write(s)
        output?.newLine()
        output?.flush()
    }
}