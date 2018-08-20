package com.sethchhim.kuboo_local

import com.sethchhim.kuboo_local.model.ComicInfo
import com.sethchhim.kuboo_local.service.local.Parser
import com.sethchhim.kuboo_local.task.Task_LocalFileParser
import com.sethchhim.kuboo_local.task.Task_LocalImageInputStream
import java.util.concurrent.Executor

class KubooLocal(val diskIO: Executor, val mainThread: Executor) {

    lateinit var parser: Parser

    fun initParser(filePath: String): Parser {
        parser = Task_LocalFileParser(filePath).parser
        return parser
    }

    fun cleanupParser() {
        parser.filePath = ""
        parser.comicInfo = ComicInfo()
        parser.destroy()
    }

    fun getLocalComicInfo() = parser.comicInfo

    fun getLocalInputStreamAt(position: Int) = Task_LocalImageInputStream(this, position).liveData

}
