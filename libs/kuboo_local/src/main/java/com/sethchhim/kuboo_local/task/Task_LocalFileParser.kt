package com.sethchhim.kuboo_local.task

import com.sethchhim.kuboo_local.parser.*
import timber.log.Timber
import java.io.File

class Task_LocalFileParser(filePath: String) {

    lateinit var parser: Parser

    init {
        when {
            isZip(filePath) -> parser = ParserZip()
            isRar(filePath) -> parser = ParserRar()
            isTarball(filePath) -> parser = ParserTar()
            isSevenZ(filePath) -> parser = ParserSevenZip()
            else -> Timber.e("Unsupported file type!")
        }

        try {
            parser.filePath = filePath
            parser.parse(File(filePath))
            Timber.d("File parse: parserSize[${parser.numPages()}] path[$filePath]")
        } catch (e: Exception) {
            Timber.e("message[${e.message}] path[$filePath]")
            e.printStackTrace()
        }
    }

    private fun isZip(filename: String) = filename.toLowerCase().matches(".*\\.(zip|cbz)$".toRegex())

    private fun isRar(filename: String) = filename.toLowerCase().matches(".*\\.(rar|cbr)$".toRegex())

    private fun isTarball(filename: String) = filename.toLowerCase().matches(".*\\.(cbt)$".toRegex())

    private fun isSevenZ(filename: String) = filename.toLowerCase().matches(".*\\.(cb7|7z)$".toRegex())

}