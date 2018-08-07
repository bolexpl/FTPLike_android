package com.example.bolek.ftplclient.lib

import java.nio.ByteBuffer

/**
 * Klasa konwersji między typem long i talbicą bajtów
 */
object ByteUtils {

    private val buffer = ByteBuffer.allocate(java.lang.Long.SIZE / 8)

    /**
     * Metoda zamienia long na tablicę bajtów
     *
     * @param l liczba typu long
     * @return tablica bajtów
     */
    fun longToByte(l: Long): ByteArray {
        buffer.clear()
        buffer.putLong(0, l)
        return buffer.array()
    }

    /**
     * Metoda zamieniająca tablicę bajtów na typ long
     *
     * @param bytes tablica bajtów
     * @return liczba typu long
     */
    fun byteToLong(bytes: ByteArray): Long {
        buffer.clear()
        buffer.put(bytes, 0, bytes.size)
        buffer.flip()
        return buffer.long
    }
}
