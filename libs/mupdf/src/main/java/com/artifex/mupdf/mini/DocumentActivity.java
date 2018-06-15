package com.artifex.mupdf.mini;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdf.fitz.Document;
import com.artifex.mupdf.fitz.Link;
import com.artifex.mupdf.fitz.Matrix;
import com.artifex.mupdf.fitz.Outline;
import com.artifex.mupdf.fitz.Page;
import com.artifex.mupdf.fitz.Rect;
import com.artifex.mupdf.fitz.android.AndroidDrawDevice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class DocumentActivity extends AppCompatActivity {
    private final String APP = "MuPDF";

    public final int NAVIGATE_REQUEST = 1;

    protected Worker worker;
    protected SharedPreferences prefs;

    protected Document doc;

    protected String key;
    protected String path;
    protected String mimetype;
    protected byte[] buffer;

    protected boolean hasLoaded;
    protected boolean isReflowable;
    protected boolean fitPage;
    protected String title;
    protected ArrayList<OutlineActivity.Item> flatOutline;
    protected float layoutW, layoutH, layoutEm;
    protected float displayDPI;
    protected int canvasW, canvasH;

    protected View currentBar;
    protected PageView pageView;
    protected View actionBar;
    protected TextView titleLabel;
    protected View searchButton;
    protected View searchBar;
    protected EditText searchText;
    protected View searchCloseButton;
    protected View searchBackwardButton;
    protected View searchForwardButton;
    protected View zoomButton;
    protected View layoutButton;
    protected PopupMenu layoutPopupMenu;
    protected View outlineButton;
    protected View navigationBar;
    protected TextView pageLabel;
    protected SeekBar pageSeekbar;

    protected int pageCount;
    protected int currentPage;
    protected int searchHitPage;
    protected String searchNeedle;
    protected boolean stopSearch;
    protected boolean wentBack;

    protected boolean isRequestRelay = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        displayDPI = metrics.densityDpi;

        setContentView(R.layout.document_activity);
        actionBar = findViewById(R.id.action_bar);
        searchBar = findViewById(R.id.search_bar);
        navigationBar = findViewById(R.id.navigation_bar);

        currentBar = actionBar;

        Uri uri = getIntent().getData();
        mimetype = getIntent().getType();
        key = uri.toString();
        if (uri.getScheme().equals("file")) {
            title = uri.getLastPathSegment();
            path = uri.getPath();
        } else {
            title = uri.toString();
            try {
                InputStream stm = getContentResolver().openInputStream(uri);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[16384];
                int n;
                while ((n = stm.read(buf)) != -1)
                    out.write(buf, 0, n);
                out.flush();
                buffer = out.toByteArray();
            } catch (IOException x) {
                Log.e(APP, x.toString());
                Toast.makeText(this, x.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        titleLabel = findViewById(R.id.title_label);
        titleLabel.setText(title);

        worker = new Worker(this);
        worker.start();

        prefs = getPreferences(Context.MODE_PRIVATE);
        layoutEm = prefs.getFloat("layoutEm", 8);
        fitPage = prefs.getBoolean("fitPage", false);
        currentPage = prefs.getInt(key, 0);
        searchHitPage = -1;
        hasLoaded = false;

        pageView = findViewById(R.id.page_view);
        pageView.setActionListener(this);

        pageLabel = findViewById(R.id.page_label);
        pageSeekbar = findViewById(R.id.page_seekbar);
        pageSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int newProgress = -1;

            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                if (fromUser) {
                    newProgress = progress;
                    pageLabel.setText((progress + 1) + " / " + pageCount);
                }
            }

            public void onStartTrackingTouch(SeekBar seekbar) {}

            public void onStopTrackingTouch(SeekBar seekbar) {
                gotoPage(newProgress);
            }
        });

        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> showSearch());
        searchText = findViewById(R.id.search_text);
        searchText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                search(1);
                return true;
            }
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search(1);
                return true;
            }
            return false;
        });
        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resetSearch();
            }
        });
        searchCloseButton = findViewById(R.id.search_close_button);
        searchCloseButton.setOnClickListener(v -> hideSearch());
        searchBackwardButton = findViewById(R.id.search_backward_button);
        searchBackwardButton.setOnClickListener(v -> search(-1));
        searchForwardButton = findViewById(R.id.search_forward_button);
        searchForwardButton.setOnClickListener(v -> search(1));

        outlineButton = findViewById(R.id.outline_button);
        outlineButton.setOnClickListener(v -> {
            Intent intent = new Intent(DocumentActivity.this, OutlineActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("POSITION", currentPage);
            bundle.putSerializable("OUTLINE", flatOutline);
            intent.putExtras(bundle);
            startActivityForResult(intent, NAVIGATE_REQUEST);
        });

        zoomButton = findViewById(R.id.zoom_button);
        zoomButton.setOnClickListener(v -> {
            fitPage = !fitPage;
            loadPage();
        });

        layoutButton = findViewById(R.id.layout_button);
        layoutPopupMenu = new PopupMenu(this, layoutButton);
        layoutPopupMenu.getMenuInflater().inflate(R.menu.layout_menu, layoutPopupMenu.getMenu());
        layoutPopupMenu.setOnMenuItemClickListener(item -> {
            float oldLayoutEm = layoutEm;
            int id = item.getItemId();
            if (id == R.id.action_layout_6pt) layoutEm = 6;
            else if (id == R.id.action_layout_7pt) layoutEm = 7;
            else if (id == R.id.action_layout_8pt) layoutEm = 8;
            else if (id == R.id.action_layout_9pt) layoutEm = 9;
            else if (id == R.id.action_layout_10pt) layoutEm = 10;
            else if (id == R.id.action_layout_11pt) layoutEm = 11;
            else if (id == R.id.action_layout_12pt) layoutEm = 12;
            else if (id == R.id.action_layout_13pt) layoutEm = 13;
            else if (id == R.id.action_layout_14pt) layoutEm = 14;
            else if (id == R.id.action_layout_15pt) layoutEm = 15;
            else if (id == R.id.action_layout_16pt) layoutEm = 16;
            if (oldLayoutEm != layoutEm)
                relayoutDocument();
            return true;
        });
        layoutButton.setOnClickListener(v -> layoutPopupMenu.show());
    }

    public void onPageViewSizeChanged(int w, int h) {
        canvasW = w;
        canvasH = h;
        layoutW = canvasW * 72 / displayDPI;
        layoutH = canvasH * 72 / displayDPI;
        if (!hasLoaded) {
            hasLoaded = true;
            openDocument();
        } else if (isReflowable) {
            relayoutDocument();
        } else {
            loadPage();
        }
    }

    protected void openDocument() {
        worker.add(new Worker.Task() {
            boolean needsPassword;

            public void work() {
                Log.i(APP, "open document");
                if (path != null)
                    doc = Document.openDocument(path);
                else
                    doc = Document.openDocument(buffer, mimetype);
                needsPassword = doc.needsPassword();
            }

            public void run() {
                if (needsPassword)
                    askPassword(R.string.dlog_password_message);
                else
                    loadDocument();
            }
        });
    }

    protected void askPassword(int message) {
        final EditText passwordView = new EditText(this);
        passwordView.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        passwordView.setTransformationMethod(PasswordTransformationMethod.getInstance());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dlog_password_title);
        builder.setMessage(message);
        builder.setView(passwordView);
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> checkPassword(passwordView.getText().toString()));
        builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> finish());
        builder.setOnCancelListener(dialog -> finish());
        builder.create().show();
    }

    protected void checkPassword(final String password) {
        worker.add(new Worker.Task() {
            boolean passwordOkay;

            public void work() {
                Log.i(APP, "check password");
                passwordOkay = doc.authenticatePassword(password);
            }

            public void run() {
                if (passwordOkay)
                    loadDocument();
                else
                    askPassword(R.string.dlog_password_retry);
            }
        });
    }

    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("layoutEm", layoutEm);
        editor.putBoolean("fitPage", fitPage);
        editor.putInt(key, currentPage);
        editor.putBoolean(key.concat("orientation"), isOrientationPortrait());
        editor.commit();
    }

    public void onBackPressed() {
        if (searchBar.isShown()) {
            hideSearch();
        } else if (navigationBar.isShown()) {
            toggleUI();
        } else {
            super.onBackPressed();
        }
    }

    public void onActivityResult(int request, int result, Intent data) {
        if (request == NAVIGATE_REQUEST && result >= RESULT_FIRST_USER)
            gotoPage(result - RESULT_FIRST_USER);
    }

    protected void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(searchText, 0);
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
    }

    protected void resetSearch() {
        stopSearch = true;
        searchHitPage = -1;
        searchNeedle = null;
        pageView.resetHits();
    }

    protected void runSearch(final int startPage, final int direction, final String needle) {
        stopSearch = false;
        worker.add(new Worker.Task() {
            int searchPage = startPage;

            public void work() {
                if (stopSearch || !needle.equals(searchNeedle))
                    return;
                for (int i = 0; i < 9; ++i) {
                    Log.i(APP, "search page " + searchPage);
                    Page page = doc.loadPage(searchPage);
                    Rect[] hits = page.search(searchNeedle);
                    page.destroy();
                    if (hits != null && hits.length > 0) {
                        searchHitPage = searchPage;
                        break;
                    }
                    searchPage += direction;
                    if (searchPage < 0 || searchPage >= pageCount)
                        break;
                }
            }

            public void run() {
                if (stopSearch || !needle.equals(searchNeedle)) {
                    pageLabel.setText((currentPage + 1) + " / " + pageCount);
                } else if (searchHitPage == currentPage) {
                    loadPage();
                } else if (searchHitPage >= 0) {
                    currentPage = searchHitPage;
                    loadPage();
                } else {
                    if (searchPage >= 0 && searchPage < pageCount) {
                        pageLabel.setText((searchPage + 1) + " / " + pageCount);
                        worker.add(this);
                    } else {
                        pageLabel.setText((currentPage + 1) + " / " + pageCount);
                        Log.i(APP, "search not found");
                        Toast.makeText(DocumentActivity.this, getString(R.string.toast_search_not_found), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    protected void search(int direction) {
        hideKeyboard();
        int startPage;
        if (searchHitPage == currentPage)
            startPage = currentPage + direction;
        else
            startPage = currentPage;
        searchHitPage = -1;
        searchNeedle = searchText.getText().toString();
        if (searchNeedle.length() == 0)
            searchNeedle = null;
        if (searchNeedle != null)
            if (startPage >= 0 && startPage < pageCount)
                runSearch(startPage, direction, searchNeedle);
    }

    protected void loadDocument() {
        worker.add(new Worker.Task() {
            public void work() {
                try {
                    Log.i(APP, "load document");
                    String metaTitle = doc.getMetaData(Document.META_INFO_TITLE);
                    if (metaTitle != null)
                        title = metaTitle;
                    isReflowable = doc.isReflowable();
                    if (isReflowable) {
                        Log.i(APP, "layout document");
                        doc.layout(layoutW, layoutH, layoutEm);
                    }
                    pageCount = doc.countPages();
                } catch (Throwable x) {
                    doc = null;
                    pageCount = 1;
                    currentPage = 0;
                    throw x;
                }
            }

            public void run() {
                if (currentPage < 0 || currentPage >= pageCount)
                    currentPage = 0;
                titleLabel.setText(title);
                if (isReflowable)
                    layoutButton.setVisibility(View.VISIBLE);
                else
                    zoomButton.setVisibility(View.VISIBLE);
                loadOutline();
                onLoadDocumentSuccess();
                loadPage();
            }
        });
    }

    protected void onLoadDocumentSuccess() {
    }

    protected void relayoutDocument() {
        worker.add(new Worker.Task() {
            public void work() {
                isRequestRelay = true;
                try {
                    Log.i(APP, "relayout document");
                    long mark = doc.makeBookmark(currentPage);
                    doc.layout(layoutW, layoutH, layoutEm);
                    pageCount = doc.countPages();
                    currentPage = doc.findBookmark(mark);
                } catch (Throwable x) {
                    pageCount = 1;
                    currentPage = 0;
                    throw x;
                }
            }

            public void run() {
                loadOutline();
                loadPage();
            }
        });
    }

    private void loadOutline() {
        Log.i(APP, "load outline");
        Outline[] outline = doc.loadOutline();
        if (outline != null) {
            flatOutline = new ArrayList<>();
            flattenOutline(outline, "");
            Collections.sort(flatOutline, (o1, o2) -> o1.page - o2.page);
            loadOutlineTotal();
        } else {
            flatOutline = null;
        }

        if (flatOutline != null) {
            if (!isRequestRelay) onLoadOutlineSuccess();
            outlineButton.setVisibility(View.VISIBLE);
        } else {
            if (!isRequestRelay) onLoadOutlineFail();
        }
    }

    private void flattenOutline(Outline[] outline, String indent) {
        for (Outline node : outline) {
            if (node.title != null)
                flatOutline.add(new OutlineActivity.Item(indent + node.title, node.page));
            if (node.down != null)
                flattenOutline(node.down, indent + "    ");
        }
    }

    private void loadOutlineTotal() {
        for (int i = 0; i < flatOutline.size(); i++) {
            if (i != flatOutline.size() - 1) {
                int currentStart = flatOutline.get(i).page;
                int nextPosition = i + 1;
                int currentEnd = flatOutline.get(nextPosition).page - 1;
                flatOutline.get(i).totalPages = currentEnd - (currentStart - 1);
            } else {
                int currentStart = flatOutline.get(i).page;
                int currentEnd = doc.countPages() - 1;
                flatOutline.get(i).totalPages = currentEnd - (currentStart - 1);
            }
        }
    }

    protected void onLoadOutlineSuccess() {
    }

    protected void onLoadOutlineFail() {
    }

    protected void loadPage() {
        final int pageNumber = currentPage;
        stopSearch = true;
        worker.add(new Worker.Task() {
            Bitmap bitmap;
            Link[] links;
            Rect[] hits;

            public void work() {
                try {
                    Log.i(APP, "load page " + pageNumber);
                    Page page = doc.loadPage(pageNumber);
                    Log.i(APP, "draw page " + pageNumber);
                    Matrix ctm;
                    if (fitPage)
                        ctm = AndroidDrawDevice.fitPage(page, canvasW, canvasH);
                    else
                        ctm = AndroidDrawDevice.fitPageWidth(page, canvasW);
                    bitmap = AndroidDrawDevice.drawPage(page, ctm);
                    links = page.getLinks();
                    if (links != null)
                        for (Link link : links)
                            link.bounds.transform(ctm);
                    if (searchNeedle != null) {
                        hits = page.search(searchNeedle);
                        if (hits != null)
                            for (Rect hit : hits)
                                hit.transform(ctm);
                    }
                } catch (Throwable x) {
                    Log.e(APP, x.getMessage());
                }
            }

            public void run() {
                if (bitmap != null) {
                    pageView.setBitmap(bitmap, wentBack, links, hits);
                    onLoadPageSuccess();
                } else {
                    pageView.setError();
                }
                pageLabel.setText((currentPage + 1) + " / " + pageCount);
                pageSeekbar.setMax(pageCount - 1);
                pageSeekbar.setProgress(pageNumber);
                wentBack = false;
                isRequestRelay = false;
            }
        });
    }

    protected void onLoadPageSuccess() {
    }

    protected void showSearch() {
        currentBar = searchBar;
        actionBar.setVisibility(View.GONE);
        searchBar.setVisibility(View.VISIBLE);
        searchBar.requestFocus();
        showKeyboard();
    }

    protected void hideSearch() {
        currentBar = actionBar;
        actionBar.setVisibility(View.VISIBLE);
        searchBar.setVisibility(View.GONE);
        hideKeyboard();
        resetSearch();
    }

    public void toggleUI() {
        if (navigationBar.getVisibility() == View.VISIBLE) {
            currentBar.setVisibility(View.GONE);
            navigationBar.setVisibility(View.GONE);
            if (currentBar == searchBar)
                hideKeyboard();
        } else {
            currentBar.setVisibility(View.VISIBLE);
            navigationBar.setVisibility(View.VISIBLE);
            if (currentBar == searchBar) {
                searchBar.requestFocus();
                showKeyboard();
            }
        }
    }

    public void goBackward() {
        if (currentPage > 0) {
            wentBack = true;
            currentPage--;
            loadPage();
        }
    }

    public void goForward() {
        if (currentPage < pageCount - 1) {
            currentPage++;
            loadPage();
        }
    }

    public void gotoPage(int p) {
        if (p >= 0 && p < pageCount && p != currentPage) {
            currentPage = p;
            loadPage();
        }
    }

    public void gotoURI(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        try {
            startActivity(intent);
        } catch (Throwable x) {
            Log.e(APP, x.getMessage());
            Toast.makeText(DocumentActivity.this, x.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isOrientationPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

}
