package com.sethchhim.kuboo_remote.model

abstract class BookData {
    abstract var autoId: Int
    abstract var id: Int
    abstract var title: String
    abstract var author: String
    abstract var content: String
    abstract var linkAcquisition: String
    abstract var linkSubsection: String
    abstract var linkThumbnail: String
    abstract var linkXmlPath: String
    abstract var linkPrevious: String
    abstract var linkNext: String
    abstract var linkPse: String
    abstract var currentPage: Int
    abstract var totalPages: Int
    abstract var server: String
    abstract var filePath: String
    abstract var bookMark: String
    abstract var isFavorite: Boolean
    abstract var isFinished: Boolean
    abstract var timeAccessed: Int
}