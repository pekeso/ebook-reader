package com.hts.demo.ebookreader;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

public class PDFReaderActivity extends AppCompatActivity implements
        OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {

    private static final String LOG_TAG = PDFReaderActivity.class.getSimpleName();

    Integer pageNumber = 0;
    PDFView pdfView;
    String pdfFileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_reader);
        pdfView = findViewById(R.id.pdfView);
        displayFromAsset("sample.pdf");
    }

    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromAsset(pdfFileName)
                .defaultPage(pageNumber)
                .swipeHorizontal(false)
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

        Log.e(LOG_TAG, "title = " + meta.getTitle());
        Log.e(LOG_TAG, "author = " + meta.getAuthor());
        Log.e(LOG_TAG, "subject = " + meta.getSubject());
        Log.e(LOG_TAG, "keywords = " + meta.getKeywords());
        Log.e(LOG_TAG, "creator = " + meta.getCreator());
        Log.e(LOG_TAG, "producer = " + meta.getProducer());
        Log.e(LOG_TAG, "creationDate = " + meta.getCreationDate());
        Log.e(LOG_TAG, "modDate = " + meta.getModDate());

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
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void onPageError(int page, Throwable t) {
        Log.e(LOG_TAG, "Cannot load page " + page);
    }
}
