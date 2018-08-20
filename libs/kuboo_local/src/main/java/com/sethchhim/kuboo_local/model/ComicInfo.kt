package com.sethchhim.kuboo_local.model

class ComicInfo {
    var bookmarks: MutableList<Pair<Int, String>> = mutableListOf()

    fun containsBookmarks() = bookmarks.isNotEmpty()
}