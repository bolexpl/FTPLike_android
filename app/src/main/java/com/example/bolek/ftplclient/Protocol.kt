package com.example.bolek.ftplclient

object Protocol {

    val USER = "USER"
    val PASSWORD = "PASS"
    val OK = "OK"
    val ERROR = "ERROR"
    val ACTIVE = "ACTIVE"
    val PASSIV = "PASSIV"
    val PORT = "PORT"
    val LIST = "LIST"
    val CD = "CD"
    val MKDIR = "MK"
    val RM = "RM"
    val EOF = "EOF"
    val EXIT = "EXIT"
    val GET = "GET"
    val PUT = "PUT"
    val MV = "MV"
    val TRUE = "TRUE"
    val FALSE = "FALSE"
    val DIR = "DIR"
    val FILE = "FILE"
    val TRANSFER = "TRANSFER"
    val BINARY = "BINARY"
    val ASCII = "ASCII"
    val PWD = "PWD"
    val CP = "CP"
    val TOUCH = "TOUCH"
    val APPEND = "APPEND"


    val MIN_PORT_NUMBER = 1024
    val MAX_PORT_NUMBER = 65535

    /**
     * Rozmiar pakietu w transferze binarnym
     */
    val PACKET_LENGTH = 512
}