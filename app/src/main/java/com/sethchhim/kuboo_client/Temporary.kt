package com.sethchhim.kuboo_client

import com.sethchhim.kuboo_remote.model.Book

object Temporary {

    internal val USER_API_UPDATE_LIST = mutableListOf<Book>() // These items require an pull from the remote user api to update the ui.

}