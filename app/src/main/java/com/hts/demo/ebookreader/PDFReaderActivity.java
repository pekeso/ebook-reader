package com.hts.demo.ebookreader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;

public class PDFReaderActivity extends AppCompatActivity implements
        OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {

    private static final String LOG_TAG = PDFReaderActivity.class.getSimpleName();

    Boolean horizontalSwipe = false;

    Integer pageNumber = 0;
    PDFView pdfView;
    File pdfFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_reader);
        pdfView = findViewById(R.id.pdfView);
        Intent intent = getIntent();
        File pdfBookFile = (File) intent.getSerializableExtra("PDF_BOOK_FILE");
        displayFromFile(pdfBookFile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.vertical_param:
                horizontalSwipe = false;
                Intent intent = getIntent();
                File pdfBookFile = (File)intent.getSerializableExtra("PDF_BOOK_FILE");
                displayFromFile(pdfBookFile);
                return true;
            case R.id.horizontal_param:
                horizontalSwipe = true;
                intent = getIntent();
                pdfBookFile = (File)intent.getSerializableExtra("PDF_BOOK_FILE");
                displayFromFile(pdfBookFile);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayFromAsset(String pdfBookTitle) {
        pdfView.fromAsset(pdfBookTitle)
                .defaultPage(pageNumber)
                .swipeHorizontal(horizontalSwipe)
                .pageSnap(true)
                .autoSpacing(true)
                .pageFling(true)
                .enableDoubletap(true)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10)
                .onPageError(this)
                .pageFitPolicy(FitPolicy.BOTH)
                .load();
    }

    private void displayFromFile(File pdfBookFile) {
        pdfFile = pdfBookFile;

        pdfView.fromFile(pdfBookFile)
                .defaultPage(pageNumber)
                .swipeHorizontal(horizontalSwipe)
                .pageSnap(true)
                .autoSpacing(true)
                .pageFling(true)
                .enableDoubletap(true)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10)
                .onPageError(this)
                .pageFitPolicy(FitPolicy.BOTH)
                .load();
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();

        Log.d(LOG_TAG, "title = " + meta.getTitle());
        Log.d(LOG_TAG, "author = " + meta.getAuthor());
        Log.d(LOG_TAG, "subject = " + meta.getSubject());
        Log.d(LOG_TAG, "keywords = " + meta.getKeywords());
        Log.d(LOG_TAG, "creator = " + meta.getCreator());
        Log.d(LOG_TAG, "producer = " + meta.getProducer());
        Log.d(LOG_TAG, "creationDate = " + meta.getCreationDate());
        Log.d(LOG_TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(LOG_TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFile.toString(), page + 1, pageCount));
    }

    @Override
    public void onPageError(int page, Throwable t) {
        Log.e(LOG_TAG, "Cannot load page " + page);
    }
}
