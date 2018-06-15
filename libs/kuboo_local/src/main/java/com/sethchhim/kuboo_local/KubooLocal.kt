package com.sethchhim.kuboo_local

import com.sethchhim.kuboo_local.parser.Parser
import com.sethchhim.kuboo_local.task.Task_LocalFileParser
import com.sethchhim.kuboo_local.task.Task_LocalImageInputStream
import com.sethchhim.kuboo_local.util.AppExecutors

class KubooLocal {

    val appExecutors = AppExecutors()

    lateinit var parser: Parser

    fun initParser(filePath: String): Parser {
        parser = Task_LocalFileParser(filePath).parser
        return parser
    }

    fun cleanupParser() = parser.destroy()

    fun getLocalInputStreamAt(position: Int) = Task_LocalImageInputStream(this, position).liveData

    fun getPage(position: Int) = parser.getPage(position)

}
