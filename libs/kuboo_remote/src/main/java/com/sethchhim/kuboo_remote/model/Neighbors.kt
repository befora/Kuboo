package com.sethchhim.kuboo_remote.model

data class Neighbors(var currentBook: Book? = null,
                     var previousBook: Book? = null,
                     var nextBook: Book? = null)