package com.hts.demo.ebookreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.folioreader.Config;
import com.folioreader.FolioReader;
import com.folioreader.model.HighLight;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.util.AppUtil;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadLocatorListener;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;
import com.rilixtech.shelfview.ShelfBook;
import com.rilixtech.shelfview.ShelfView;
import com.shockwave.pdfium.PdfDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OnHighlightListener, ReadLocatorListener, FolioReader.OnClosedListener, ShelfView.BookListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private FolioReader folioReader;
    private ReadLocator globalReadLocator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        folioReader = FolioReader.get()
                .setOnHighlightListener(this)
                .setReadLocatorListener(this)
                .setOnClosedListener(this);

        ShelfView shelfView = findViewById(R.id.shelfView);

        shelfView.setBookListener(this);
        List<ShelfBook> books = new ArrayList<>();

        books.add(ShelfBook.createBookWithAsset("the_silver_chair.jpg", "1",
                "The Silver Chair"));
        books.add(ShelfBook.createBookWithAsset("how_to_talk_to_anyone.jpg", "2",
                "How To Talk To Anyone"));
        books.add(ShelfBook.createBookWithAsset("startup.jpg", "3",
                "The 100$ Startup"));
        books.add(ShelfBook.createBookWithAsset("learn_python_in_one_day.jpg", "4",
                "Learn Python In One Day and Learn It Well"));
        books.add(ShelfBook.createBookWithAsset("finish_what_you_start.jpg", "5",
                "Finish What You Start"));
        books.add(ShelfBook.createBookWithAsset("self_discipline.jpg", "6",
                "Self Discipline"));
        books.add(ShelfBook.createBookWithAsset("book_cover_8.jpg", "7",
                "How To Talk To Anyone"));
        books.add(ShelfBook.createBookWithAsset("book_cover_6.jpg", "8",
                "PDF Book"));
        shelfView.loadBooks(books);

        //onBookClicked(1, "1", "TheSilverChair.epub");
    }

    @Override
    public void onBookClicked(int position, ShelfBook shelfBook) {
        ReadLocator readLocator = getLastReadLocator();
        Config config = AppUtil.getSavedConfig(getApplicationContext());
        switch (position){
            case 0:
                if(config == null)
                    config = new Config();
                config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

                folioReader.setReadLocator(readLocator);
                folioReader.setConfig(config, true)
                        .openBook("file:///android_asset/TheSilverChair.epub");

                break;
            case 1:
                if(config == null)
                    config = new Config();
                config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

                folioReader.setReadLocator(readLocator);
                folioReader.setConfig(config, true)
                        .openBook("file:///android_asset/HowToTalkToAnyone.epub");
                break;
            case 2:
                if(config == null)
                    config = new Config();
                config.setAllowedDirection(Config.AllowedDirection.ONLY_HORIZONTAL);

                folioReader.setReadLocator(readLocator);
                folioReader.setConfig(config, true)
                        .openBook("file:///android_asset/The100Startup.epub");
                break;
            case 3:
                if(config == null)
                    config = new Config();
                config.setAllowedDirection(Config.AllowedDirection.ONLY_HORIZONTAL);

                folioReader.setReadLocator(readLocator);
                folioReader.setConfig(config, true)
                        .openBook("file:///android_asset/LearnPythonInOneDayAndLearnItWell.epub");
                break;
            case 4:
                if(config == null)
                    config = new Config();
                config.setAllowedDirection(Config.AllowedDirection.ONLY_HORIZONTAL);

                folioReader.setReadLocator(readLocator);
                folioReader.setConfig(config, true)
                        .openBook("file:///android_asset/FinishWhatYouStart.epub");
                break;
            case 5:
                if(config == null)
                    config = new Config();
                config.setAllowedDirection(Config.AllowedDirection.ONLY_HORIZONTAL);

                folioReader.setReadLocator(readLocator);
                folioReader.setConfig(config, true)
                        .openBook("file:///android_asset/SelfDiscipline.epub");
                break;
            case 6:
                if(config == null)
                    config = new Config();
                config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);
                FileLoader.with(getApplicationContext())
                        .load("https://firebasestorage.googleapis.com/v0/b/lisoloapp-project.appspot.com/o/HowToTalkToAnyone.epub?alt=media&token=501988db-291e-4a19-84ff-d6b571ba3b3d",false)
                        .fromDirectory("Documents", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .asFile(new FileRequestListener<File>() {
                            @Override
                            public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                File epubFile = response.getBody();
                                String filePath = epubFile.getPath();

                                FolioReader folioReader = FolioReader.get();
                                folioReader.openBook(filePath);
                            }

                            @Override
                            public void onError(FileLoadRequest request, Throwable t) {
                                Toast.makeText(MainActivity.this, "Epub Error: "+ t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case 7:
                startActivity(new Intent(MainActivity.this, PDFReaderActivity.class));
                //displayFromAsset("sample.pdf");
                break;
            default: break;
        }
    }

    private ReadLocator getLastReadLocator() {

        String jsonString = loadAssetTextAsString("Locators/LastReadLocators/last_read_locator_1.json");
        return ReadLocator.fromJson(jsonString);
    }

    private String loadAssetTextAsString(String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("HomeActivity", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("HomeActivity", "Error closing asset " + name);
                }
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FolioReader.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onFolioReaderClosed() {
                Log.v(LOG_TAG, "-> onFolioReaderClosed");
    }

    @Override
    public void onHighlight(HighLight highlight, HighLight.HighLightAction type) {

    }

    @Override
    public void saveReadLocator(ReadLocator readLocator) {
        Log.i(LOG_TAG, "-> saveReadLocator -> " + readLocator.toJson());
    }

    @Override
    public void onBookLongClicked(int i, ShelfBook shelfBook) {

    }
}
