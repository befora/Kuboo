package com.sethchhim.epublibdroid_kotlin

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sethchhim.epublibdroid_kotlin.Settings.SCROLL_DURATION
import com.sethchhim.epublibdroid_kotlin.model.Chapter
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.Spine
import nl.siegmann.epublib.domain.TOCReference
import nl.siegmann.epublib.epub.EpubReader
import nl.siegmann.epublib.service.MediatypeService
import org.json.JSONObject
import java.io.*
import java.util.*

open class EpubReaderViewImpl1_Content @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : EpubReaderViewImpl0_View(context, attrs, defStyleAttr) {

    private var isLoading = false
    private var chapterNumber = 0
    private var selectedText = ""
    private var chapterList = ArrayList<Chapter>()
    private var progress = 0f
    private var pageNumber = 0
    private var resourceLocation = ""

    private var METHOD_HIGHLIGHT = 1
    private var METHOD_UNDERLINE = 2
    private var METHOD_STRIKETHROUGH = 3

    private var textSelectionMode = false
    private var mActionMode: android.view.ActionMode? = null
    private var actionModeCallback: SelectActionModeCallback? = null

    private lateinit var book: Book

    init {
        this.addJavascriptInterface(JavaScriptInterface(), "js")
    }

    inner class SelectActionModeCallback : android.view.ActionMode.Callback {
        override fun onCreateActionMode(mode: android.view.ActionMode, menu: Menu): Boolean {
            Log.d("onCreateActionMode", "triggered")
            mActionMode = mode
            textSelectionMode = true
            epubReaderListener.onTextSelectionModeChangeListener(true)
            return true
        }

        override fun onPrepareActionMode(mode: android.view.ActionMode, menu: Menu): Boolean {
            Log.d("onPrepareActionMode", "triggered")
            return false
        }

        override fun onActionItemClicked(mode: android.view.ActionMode, item: MenuItem): Boolean {
            Log.d("onActionItemClicked", "triggered")
            return false
        }

        override fun onDestroyActionMode(mode: android.view.ActionMode) {
            Log.d("onDestroyActionMode", "triggered")
            epubReaderListener.onTextSelectionModeChangeListener(false)
            textSelectionMode = false
        }
    }

    fun openEpubFile(epubLocation: String) {
        var epubInputStream: InputStream? = null
        try {
            epubInputStream = BufferedInputStream(FileInputStream(epubLocation))
            this.book = EpubReader().readEpub(epubInputStream)
            val epub_temp_extraction_location = context.cacheDir.toString() + "/epub_temp_files"
            deleteFiles(File(epub_temp_extraction_location))
            if (!File(epub_temp_extraction_location).exists())
                File(epub_temp_extraction_location).mkdirs()
            try {
                downloadResource(epub_temp_extraction_location)
            } catch (e: Exception) {
                Log.e("Exception", e.message)
            }

            val dir1 = File(epub_temp_extraction_location + File.separator + "OEBPS")
            val resource_folder = book.opfResource.href.replace("content.opf", "").replace("/", "")
            val dir2 = File(epub_temp_extraction_location + File.separator + resource_folder)
            resourceLocation = if (dir1.exists() && dir1.isDirectory) {
                "file://" + epub_temp_extraction_location + File.separator + "OEBPS" + File.separator
            } else if (dir2.exists() && dir2.isDirectory && resource_folder != "") {
                "file://" + epub_temp_extraction_location + File.separator + resource_folder + File.separator
            } else {
                "file://" + epub_temp_extraction_location + File.separator
            }
            //Log.d("EpubReaderRL",resourceLocation);
            chapterList.clear()

            when (book.tableOfContents.tocReferences.isNotEmpty()) {
                true -> processChaptersByTOC(book.tableOfContents.tocReferences)
                false -> processChaptersBySpine(book.spine)
            }
        } catch (e: Exception) {

        } finally {
            if (epubInputStream != null) {
                try {
                    epubInputStream.close()
                } catch (e: IOException) {
                    // ignore
                }

            }
        }
    }

    fun loadPosition(chapterNumber: Int, chapterProgress: Float) {
        epubReaderListener.onPositionLoading()
        validate(chapterNumber, chapterProgress)

        val htmlData = chapterList[this.chapterNumber].content.formatHtmlContent()
        val mimeType = "text/html; charset=UTF-8"
        val historyUrl = "about:blank"

        loadDataWithBaseURL(resourceLocation, htmlData.applyCssStyle(), mimeType, null, historyUrl)

        webViewClient = object : WebViewClient() {
            //TODO This callback for onPageFinished is not reliable. A delay is required because the TotalContentHeight is not loaded properly. Sometimes this gets called too early and the TotalContentHeight is incorrect resulting in wrong bookmark position.
            override fun onPageFinished(view: WebView, url: String) {
                launch(UI) {

                    //apply all javascript based styling here
                    applyMargin(Settings.MARGIN_SIZE)

                    delay(1000)
                    scrollToCurrentPosition()
                    epubReaderListener.onLoadPositionSuccess()
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                epubReaderListener.onLinkClicked(request.url.toString())
                return true
            }
        }
    }

    fun goToNextPage() {
        if (!isLoading) {
            val pageHeight = getPageHeight()
            val totalHeight = getTotalContentHeight()
            if (totalHeight > this.scrollY + this.height) {
                isLoading = true
                progress = (this.scrollY + pageHeight).toFloat() / totalHeight
                pageNumber = (this.scrollY + pageHeight) / pageHeight
                //this.scrollTo(0, pageNumber * pageHeight);
                val anim = ObjectAnimator.ofInt(this, "scrollY", (pageNumber - 1) * pageHeight, pageNumber * pageHeight)
                anim.duration = SCROLL_DURATION.toLong()
                anim.start()
                epubReaderListener.onPageChangeListener(chapterNumber, pageNumber, this.getProgressStart(), this.getProgressEnd())
                isLoading = false
            } else {
                loadNextChapter()
            }
        }
    }

    fun goToPreviousPage() {
        if (!isLoading) {
            val pageHeight = getPageHeight()
            val totalHeight = getTotalContentHeight()
            //Log.d("epubPagePre", this.getScrollY() + "\t" + pageHeight);
            if (this.scrollY - pageHeight >= 0) {
                isLoading = true
                progress = (this.scrollY - pageHeight).toFloat() / totalHeight
                //Log.d("EpubReaderProgress", progress + " " + pageHeight + " " + this.getScrollY() + " " + TotalHeight);
                pageNumber = (this.scrollY - pageHeight) / pageHeight
                //this.scrollTo(0, ((int) (pageNumber * pageHeight)));
                val anim = ObjectAnimator.ofInt(this, "scrollY",
                        (pageNumber + 1) * pageHeight, pageNumber * pageHeight)
                anim.duration = SCROLL_DURATION.toLong()
                anim.start()
                epubReaderListener.onPageChangeListener(chapterNumber, pageNumber, this.getProgressStart(), this.getProgressEnd())
                isLoading = false
            } else if (this.scrollY > 0) {
                isLoading = true
                progress = 0f
                //Log.d("EpubReaderProgress", progress + " " + pageHeight + " " + this.getScrollY() + " " + TotalHeight);
                pageNumber = 0
                val anim = ObjectAnimator.ofInt(this, "scrollY", (pageNumber + 1) * pageHeight, pageNumber * pageHeight)
                anim.duration = SCROLL_DURATION.toLong()
                anim.start()
                epubReaderListener.onPageChangeListener(chapterNumber, pageNumber, this.getProgressStart(), this.getProgressEnd())
                isLoading = false
            } else {
                loadPreviousChapter()
            }
        }
    }

    private fun loadNextChapter() {
        if (chapterList.size > chapterNumber + 1 && !isLoading) {
            isLoading = true
            loadPosition(chapterNumber + 1, 0f)
            isLoading = false
        } else if (chapterList.size <= chapterNumber + 1) {
            epubReaderListener.onBookEndReached()
        }
    }

    private fun loadPreviousChapter() {
        if (chapterNumber - 1 >= 0 && !isLoading) {
            isLoading = true
            loadPosition(chapterNumber - 1, 1f)
            isLoading = false
        } else if (chapterNumber - 1 < 0) {
            epubReaderListener.onBookStartReached()
        }
    }

    fun applyMargin(marginSize: Int) {
        val margin = "\"" + marginSize + "px\""
        loadJavascript("document.body.style.margin=$margin;")
    }

    fun applyTextZoom(textZoom: Int) {
        settings.textZoom = textZoom
    }

    fun getCoverImage() = try {
        BitmapFactory.decodeStream(book.coverImage.inputStream)
    } catch (e: Exception) {
        null
    }

    fun getChapterContent(): String {
        return chapterList[chapterNumber].content
    }

    fun getTotalContentHeight(): Int {
        return (this.contentHeight * resources.displayMetrics.density).toInt()
    }

    fun getPageHeight(): Int {
        return this.height - 50
    }

    fun getProgressStart(): Float {
        return progress
    }

    fun getProgressEnd(): Float {
        return if (getTotalContentHeight() <= 0)
            progress
        else if (progress + getPageHeight() / getTotalContentHeight() < 1)
            progress + getPageHeight().toFloat() / getTotalContentHeight().toFloat()
        else
            1f
    }

    fun scrollToCurrentPosition() {
        val pageHeight = getPageHeight()
        val scrollY = (getTotalContentHeight() * progress).toInt()
        pageNumber = scrollY / pageHeight

        val anim = ObjectAnimator.ofInt(this, "scrollY", pageNumber * pageHeight)
        anim.duration = 0
        anim.start()

        scrollTo(0, pageNumber * pageHeight)
        epubReaderListener.onPageChangeListener(this@EpubReaderViewImpl1_Content.chapterNumber, this@EpubReaderViewImpl1_Content.pageNumber, this@EpubReaderViewImpl1_Content.progress, this@EpubReaderViewImpl1_Content.getProgressEnd())
    }

    private fun downloadResource(directory: String) {
        //Log.d("epubResourcePath", directory);
        try {
            val rst = book.resources
            val clrst = rst.all
            val itr = clrst.iterator()
            while (itr.hasNext()) {
                val rs = itr.next()
                if (rs.mediaType === MediatypeService.JPG || rs.mediaType === MediatypeService.PNG || rs.mediaType === MediatypeService.GIF || rs.mediaType === MediatypeService.CSS) {
                    val oppath1 = File(directory + File.separator + rs.href)
                    //Log.d("EpubReaderRD", rs.getHref()+"\t"+oppath1.getAbsolutePath()+"\t"+rs.getSize());
                    val dir = File(oppath1.absolutePath.substring(0, oppath1.absolutePath.lastIndexOf("/")))
                    if (!dir.exists())
                        dir.mkdirs()
                    oppath1.createNewFile()
                    val fos1 = FileOutputStream(oppath1)
                    fos1.write(rs.data)
                    fos1.close()
                    //Log.d("EpubReaderFileE",oppath1.getAbsoluteFile()+" "+oppath1.exists());
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("error", e.message)
        }
    }


    private fun listChaptersDialog() {
        try {
            val ChapterListString = ArrayList<String>()
            for (i in chapterList.indices) {
                ChapterListString.add(chapterList[i].name)
            }
            val items = ChapterListString.toTypedArray()
            val alertbuilder = AlertDialog.Builder(context, R.style.DarkDialog)
            alertbuilder.setTitle("Select the Chapter")
            alertbuilder.setItems(items) { dialog, item -> loadPosition(item, 0f) }
            val alert = alertbuilder.create()
            alert.show()
        } catch (e: Exception) {
        }
    }

    private fun processChaptersByTOC(tocReferences: List<TOCReference>) {
        tocReferences.forEach {
            val builder = StringBuilder()
            try {
                val reader = BufferedReader(InputStreamReader(it.resource.inputStream))
                while (true) {
                    val line = reader.readLine() ?: break
                    builder.append(line)
                }
            } catch (e: Exception) {
            }

            chapterList.add(Chapter(it.title, builder.toString(), it.completeHref))
            if (it.children.size > 0) {
                processChaptersByTOC(it.children)
            }
        }
    }

    private fun processChaptersBySpine(spine: Spine?) {
        var chapterNumber = 1
        if (spine != null) {
            for (i in 0 until spine.size()) {
                val builder = StringBuilder()
                try {
                    val reader = BufferedReader(InputStreamReader(spine.getResource(i).inputStream))
                    while (true) {
                        val line = reader.readLine() ?: break
                        builder.append(line)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                chapterList.add(Chapter(if (spine.getResource(i).title != null) spine.getResource(i).title else chapterNumber.toString() + "", builder.toString(), spine.getResource(i).href))
                //Log.d("EpubReaderContent",builder.toString());
                chapterNumber++
            }
        } else {
            Log.d("EpubReader", "Spine is null!")
        }
    }

    private fun convertIntoPixel(dp: Int) = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics))

    private fun deleteFiles(file: File) {
        if (file.isDirectory) {
            val files = file.listFiles()   //All files and sub folders
            var x = 0
            while (files != null && x < files.size) {
                deleteFiles(files[x])
                x++
            }
            file.delete()
        } else
            file.delete()
    }

    fun processTextSelection() {
        val js = "\tvar sel = window.getSelection();\n" +
                "\tvar jsonData ={};\n" +
                "\tif(!sel.isCollapsed) {\n" +
                "\t\tvar range = sel.getRangeAt(0);\n" +
                "\t\tstartNode = range.startContainer;\n" +
                "\t\tendNode = range.endContainer;\n" +
                "\t\tjsonData['selectedText'] = range.toString();\n" +
                "\t\tjsonData['startOffset'] = range.startOffset;  // where the range starts\n" +
                "\t\tjsonData['endOffset'] = range.endOffset;      // where the range ends\n" +
                "\t\tjsonData['startNodeData'] = startNode.data;                       // the actual selected text\n" +
                "\t\tjsonData['startNodeHTML'] = startNode.parentElement.innerHTML;    // parent element innerHTML\n" +
                "\t\tjsonData['startNodeTagName'] = startNode.parentElement.tagName;   // parent element tag name\n" +
                "\t\tjsonData['endNodeData'] = endNode.data;                       // the actual selected text\n" +
                "\t\tjsonData['endNodeHTML'] = endNode.parentElement.innerHTML;    // parent element innerHTML\n" +
                "\t\tjsonData['endNodeTagName'] = endNode.parentElement.tagName;   // parent element tag name\n" +
                "\t\tjsonData['status'] = 1;\n" +
                "\t}else{\n" +
                "\t\tjsonData['status'] = 0;\n" +
                "\t}\n" +
                "\treturn (JSON.stringify(jsonData));"
        this.evaluateJavascript("(function(){$js})()"
        ) { value ->
            //Log.v("EpubReader", "SELECTION>19:" + value);
            //Log.v("EpubReader", "SELECTION_P>19:" +  value.substring(1,value.length()-1).replaceAll("\\\\\"","\""));
            //Log.v("EpubReader", "SELECTION_P>19:" +  value.substring(1,value.length()-1).replaceAll("\\\\\"","\"").replaceAll("\\\\\\\\\"","\\\\\"").replaceAll("\\\\\\\"","\\\\\"").replaceAll("\\\\\\\\\\\"","\\\\\""));
            var text = ""
            try {
                val json = value.substring(1, value.length - 1).replace("\\\\\"".toRegex(), "\"").replace("\\\\\\\\\"".toRegex(), "\\\\\"").replace("\\\\\\\"".toRegex(), "\\\\\"").replace("\\\\\\\\\\\"".toRegex(), "\\\\\"")
                val `object` = JSONObject(json)
                text = `object`.getString("selectedText")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val selectedTextJson = JSONObject()
            try {
                selectedTextJson.put("DataString", value)
                selectedTextJson.put("chapterNumber", chapterNumber)
                selectedTextJson.put("SelectedText", text)
            } catch (e: Exception) {
                selectedText = ""
            }

            selectedText = selectedTextJson.toString()
        }
    }

    fun Annotate(jsonData: String, selectionMethod: Int, hashcolor: String) {
        //Log.d("EpubReader","AnnotateCalled");
        //Log.d("Annotate",jsonData);
        //Log.d("Annotate",jsonData.replace("'", "\\'").replace("\"","\\\""));
        var js: String
        js = "\tvar data = JSON.parse($jsonData);\n"
        js = js + "\tvar selectedText = data['selectedText'];\n" +
                "\tvar startOffset = data['startOffset'];\n" +
                "\tvar endOffset = data['endOffset'];\n" +
                "\tvar startNodeData = data['startNodeData'];\n" +
                "\tvar startNodeHTML = data['startNodeHTML'];\n" +
                "\tvar startNodeTagName = data['startNodeTagName'];\n" +
                "\tvar endNodeData = data['endNodeData'];\n" +
                "\tvar endNodeHTML = data['endNodeHTML'];\n" +
                "\tvar endNodeTagName = data['endNodeTagName'];\n" +
                "    var tagList = document.getElementsByTagName(startNodeTagName);\n" +
                "    for (var i = 0; i < tagList.length; i++) {\n" +
                "        if (tagList[i].innerHTML == startNodeHTML) {\n" +
                "            var startFoundEle = tagList[i];\n" +
                "        }\n" +
                "    }\n" +
                "\tvar nodeList = startFoundEle.childNodes;\n" +
                "    for (var i = 0; i < nodeList.length; i++) {\n" +
                "        if (nodeList[i].data == startNodeData) {\n" +
                "            var startNode = nodeList[i];\n" +
                "        }\n" +
                "    }\n" +
                "\tvar tagList = document.getElementsByTagName(endNodeTagName);\n" +
                "    for (var i = 0; i < tagList.length; i++) {\n" +
                "        if (tagList[i].innerHTML == endNodeHTML) {\n" +
                "            var endFoundEle = tagList[i];\n" +
                "        }\n" +
                "    }\n" +
                "    var nodeList = endFoundEle.childNodes;\n" +
                "    for (var i = 0; i < nodeList.length; i++) {\n" +
                "        if (nodeList[i].data == endNodeData) {\n" +
                "            var endNode = nodeList[i];\n" +
                "        }\n" +
                "    }\n" +
                "    var range = document.createRange();\n" +
                "\trange.setStart(startNode, startOffset);\n" +
                "    range.setEnd(endNode, endOffset);\n" +
                "    var sel = window.getSelection();\n" +
                "\tsel.removeAllRanges();\n" +
                "\tdocument.designMode = \"on\";\n" +
                "\tsel.addRange(range);\n"
        if (selectionMethod == METHOD_HIGHLIGHT)
            js = "$js\tdocument.execCommand(\"HiliteColor\", false, \"$hashcolor\");\n"
        if (selectionMethod == METHOD_UNDERLINE)
            js = "$js\tdocument.execCommand(\"underline\");\n"
        if (selectionMethod == METHOD_STRIKETHROUGH)
            js = "$js\tdocument.execCommand(\"strikeThrough\");\n"
        js = js + "\tsel.removeAllRanges();\n" +
                "\tdocument.designMode = \"off\";\n" +
                "\treturn \"{\\\"status\\\":1}\";\n"
        loadJavascript(js)
    }

    override fun startActionMode(callback: android.view.ActionMode.Callback, ModeType: Int): android.view.ActionMode? {
        Log.d("startActionMode", "triggered")
        val parent = parent ?: return null
        actionModeCallback = SelectActionModeCallback()
        return parent.startActionModeForChild(this, actionModeCallback)
    }

    fun ExitSelectionMode() {
        mActionMode!!.finish()
        val js = "window.getSelection().removeAllRanges();"
        loadJavascript(js)
    }

    protected fun String.applyCssStyle(): String? {
        val addBodyStart = !this.toLowerCase().contains("<body>")
        val addBodyEnd = !this.toLowerCase().contains("</body")
        val cssStyle = "<style type=\"text/css\">" +
                "@font-face {font-family: CustomFont;" + "src: url(\"" + Settings.FONT_PATH + "\")}" +
                "body {" +
                "font-family: CustomFont;" +
                "color: " + Settings.FONT_COLOR + ";" +
                "background-color:" + Settings.BACKGROUND_COLOR + ";" +
                "text-align: justify;" +
                "line-height: " + Settings.LINE_HEIGHT + "px}" +
                "</style>"
        return cssStyle + (if (addBodyStart) "<body>" else "") + this + if (addBodyEnd) "</body>" else ""
    }

    inner class JavaScriptInterface {
        @JavascriptInterface
        fun selection(value: String) {
            //Log.v("EpubReader", "SELECTION<=19:" + value);
            var text = ""
            try {
                val `object` = JSONObject(value)
                if (`object`.has("selectedText"))
                    text = `object`.getString("selectedText")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (text != "") {
                val selectedTextJson = JSONObject()
                try {
                    selectedTextJson.put("DataString", value)
                    selectedTextJson.put("chapterNumber", chapterNumber)
                    selectedTextJson.put("SelectedText", text)
                } catch (e: Exception) {
                    selectedText = ""
                }

                selectedText = selectedTextJson.toString()
            }
        }

        fun selection2(value: String) {
            //Log.v("EpubReader", "SELECTION2<=19:" + value);
        }

        @JavascriptInterface
        fun annotate(response: String) {
            //Log.v("EpubReader","annotate<=19 "+response);
        }

        @JavascriptInterface
        fun deselect(response: String) {
            //Log.v("EpubReader","Deselect<=19 "+response);
        }
    }

    private fun loadJavascript(js: String) = evaluateJavascript("(function(){$js})()") {
        //result callback
    }

    private fun String.formatHtmlContent() = replace("href=\"http".toRegex(), "hreflink=\"http").replace("<a href=\"[^\"]*".toRegex(), "<a ").replace("hreflink=\"http".toRegex(), "href=\"http")

    private fun validate(chapterNumber: Int, chapterProgress: Float) {
        this.chapterNumber = chapterNumber
        this.progress = chapterProgress

        if (chapterList.isNotEmpty() && chapterNumber >= chapterList.size) this.chapterNumber = chapterList.size - 1
        if (chapterNumber < 0) this.chapterNumber = 0
        if (chapterProgress < 0f || chapterProgress > 1f) this.progress = 0f
    }

}