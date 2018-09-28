package com.example.bolek.ftplclient.model

import io.reactivex.Single
import java.io.IOException

interface IExplorer {

    /**
     * Metoda pobierająca aktualną ścieżkę z obiektu explorera
     *
     * @return ścieżka
     */
    fun getDir(): Single<String>

    /**
     * Metoda zmieniająca aktualną ścieżkę w obiekcie explorera
     *
     * @param dir Ścieżka do katalogu
     * @return sukces
     * @throws IOException wyjątek
     */
    @Throws(IOException::class)
    fun setDir(dir: String): Single<Boolean>

    /**
     * Metoda służąca do kopiowania plików między katalogami.
     * Metoda nie służy do pobierania i wysyłania plików.
     *
     * @param path1 Ścieżka źródłowa
     * @param path2 Ścieżka docelowa
     * @throws IOException wyjątek
     */
    @Throws(IOException::class)
    fun copy(path1: String, path2: String)

    /**
     * Metoda aktualizująca ścieżkę katalogu roboczego
     *
     * @throws IOException wyjątek
     */
    @Throws(IOException::class)
    fun pwd()

    /**
     * Metoda tworząca pusty plik
     *
     * @param name Nazwa nowego pliku
     * @return sukces
     * @throws IOException wyjątek
     */
    @Throws(IOException::class)
    fun touch(name: String): Single<Boolean>

    /**
     * Metoda dopisująca ciąg znaków do pliku
     *
     * @param fileName Nazwa pliku
     * @param data     Ciąg znaków
     * @return sukces
     * @throws IOException wyjątek
     */
    @Throws(IOException::class)
    fun append(fileName: String, data: String): Single<Boolean>

    /**
     * Metoda negująca wartość atrybutu hidden
     */
    fun invertHidden()

    /**
     * Metoda do pobierania pliku z serwera
     *
     * @param path      Ścieżka zdalna
     * @param localPath Ścieżka lokalna
     */
    fun getFile(path: String, localPath: String)

    /**
     * Metoda do wysyłania pliku na serwer
     *
     * @param path      Ścieżka zdalna
     * @param localPath Ścieżka lokalna
     */
    fun putFile(path: String, localPath: String)

    /**
     * Metoda do otwierania katalogu
     *
     * @param directory Nazwa katalogu
     */
    fun cd(directory: String) : Single<Boolean>

    /**
     * Metoda służąca do przechodzenia do katalogu nadrzędnego
     *
     * @return sukces
     */
    fun cdParent(): Single<Boolean>

    /**
     * Metoda do usuwania pliku
     *
     * @param name Nazwa pliku
     * @return sukces
     * @throws IOException wyjątek
     */
    @Throws(IOException::class)
    fun rm(name: String): Single<Boolean>

    /**
     * Metoda listująca katalog roboczy
     *
     * @return Lista elementów w katalogu
     */
    fun listFiles(): Single<List<FileInfo>>

    /**
     * Metoda służąca do logowania na serwerze
     *
     * @param login Login
     * @param pass  Hasło
     * @return sukces
     * @throws IOException wyjątek
     */
    @Throws(IOException::class)
    fun login(login: String, pass: String): Single<Boolean>

    /**
     * Metoda do połączenia w trybie pasywnym
     *
     * @return sukces
     * @throws IOException wyjątek
     */
    @Throws(IOException::class)
    fun connectPassive(): Single<Boolean>

    /**
     * Metoda do połączenia w trybie aktywnym
     *
     * @return sukces
     * @throws IOException wyjątek
     */
    @Throws(IOException::class)
    fun connectActive(): Single<Boolean>

    /**
     * Metoda do tworzenia katalogu
     *
     * @param dir Nazwa katalogu
     * @return sukces
     * @throws IOException wyjątek
     */
    @Throws(IOException::class)
    fun mkdir(dir: String): Single<Boolean>

    /**
     * Metoda do przenoszenia i zmiany nazwy plików i katalogów
     *
     * @param oldFile Stara ścieżka z nazwą
     * @param newFile Nowa ścieżka z nazwą
     * @return sukces
     * @throws IOException wyjątek
     */
    @Throws(IOException::class)
    fun mv(oldFile: String, newFile: String): Single<Boolean>

    /**
     * Metoda kończąca połączenie
     */
    fun disconnect()
}