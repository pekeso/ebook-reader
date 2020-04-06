package com.rilixtech.shelfview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.ArrayList;
import java.util.List;

public class ShelfView extends GridView {
  private static final String TAG = "ShelfView";

  private ShelfAdapter mShelfAdapter;
  private List<ShelfBook> mBooks;
  private List<Shelf> mShelves;
  private int mNumberOfTilesPerRow;
  private int mShelfHeight;
  private int mShelfWidth;
  private int mGridViewColumnWidth;
  private int mGridItemHeight;
  private boolean mIsFinishInitializing = false;

  private BookListener mBookListener;

  /**
   * Callback when book is clicked from the shelf
   */
  public interface BookListener {
    void onBookClicked(int position, ShelfBook book);
    void onBookLongClicked(int position, ShelfBook book);
  }

  public void setBookListener(BookListener bookListener) {
    mBookListener = bookListener;
  }

  public ShelfView(Context context) {
    this(context, null);
  }

  public ShelfView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ShelfView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ShelfView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    TypedArray arr =
        context.getTheme().obtainStyledAttributes(attrs, R.styleable.ShelfView, defStyleAttr, 0);
    String noImageFound = arr.getString(R.styleable.ShelfView_shv_no_image_found);
    boolean titleAsPlaceHolder =
        arr.getBoolean(R.styleable.ShelfView_shv_title_as_placeholder, false);
    arr.recycle();

    Configuration.setNoImageFound(noImageFound);
    Configuration.setTitleAsPlaceHolder(titleAsPlaceHolder);

    mGridViewColumnWidth = getContext().getResources().getInteger(R.integer.shelf_column_width);
    mGridItemHeight = getContext().getResources().getInteger(R.integer.shelf_list_item);
    mShelves = new ArrayList<>();
    mBooks = new ArrayList<>();
    mShelfAdapter = new ShelfAdapter(context, mShelves);
    setAdapter(mShelfAdapter);
    initListener();
    initShelf(mBooks);
  }

  private void initListener() {
    AdapterView.OnItemClickListener itemClickListener = new OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // An empty shelf, don't process the click.
        if (mShelves.get(position).getShelfBook() == null) return;

        if (mBookListener != null) {
          mBookListener.onBookClicked(position, mShelves.get(position).getShelfBook());
        }
      }
    };

    AdapterView.OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // An empty shelf, don't process the click.
        if (mShelves.get(position).getShelfBook() == null) return false;

        if (mBookListener != null) {
          mBookListener.onBookLongClicked(position, mShelves.get(position).getShelfBook());
        }

        return true;
      }
    };

    setOnItemClickListener(itemClickListener);
    setOnItemLongClickListener(itemLongClickListener);
    setLongClickable(true);
  }

  /**
   * Populate shelf with books
   */
  public void loadBooks(List<ShelfBook> books) {
    mBooks = books;
    // wait until shelf finished initialized.
    if(!mIsFinishInitializing) return;

    mShelves.clear();
    for (int i = 0; i < books.size(); i++) {
      ShelfBook book = books.get(i);
      if ((i % mNumberOfTilesPerRow) == 0) {
        mShelves.add(new Shelf(book, ShelfType.START));
      } else if ((i % mNumberOfTilesPerRow) == (mNumberOfTilesPerRow - 1)) {
        mShelves.add(new Shelf(book, ShelfType.END));
      } else {
        mShelves.add(new Shelf(book, ShelfType.NONE));
      }
    }

    int sizeOfModel = books.size();
    int numberOfRows = sizeOfModel / mNumberOfTilesPerRow;
    int remainderTiles = sizeOfModel % mNumberOfTilesPerRow;

    if (remainderTiles > 0) {
      numberOfRows = numberOfRows + 1;
      int fillUp = mNumberOfTilesPerRow - remainderTiles;
      for (int i = 0; i < fillUp; i++) {
        if (i == (fillUp - 1)) {
          mShelves.add(new Shelf(null, ShelfType.END));
        } else {
          mShelves.add(new Shelf(null, ShelfType.NONE));
        }
      }
    }

    if ((numberOfRows * mGridItemHeight) < mShelfHeight) {
      int remainderRowHeight = (mShelfHeight - (numberOfRows * mGridItemHeight)) / mGridItemHeight;

      if (remainderRowHeight == 0) {
        for (int i = 0; i < mNumberOfTilesPerRow; i++) {
          if (i == 0) {
            mShelves.add(new Shelf(null, ShelfType.START));
          } else if (i == (mNumberOfTilesPerRow - 1)) {
            mShelves.add(new Shelf(null, ShelfType.END));
          } else {
            mShelves.add(new Shelf(null, ShelfType.NONE));
          }
        }
      } else if (remainderRowHeight > 0) {
        int fillUp = mNumberOfTilesPerRow * (remainderRowHeight + 1);
        for (int i = 0; i < fillUp; i++) {
          if ((i % mNumberOfTilesPerRow) == 0) {
            mShelves.add(new Shelf(null, ShelfType.START));
          } else if ((i % mNumberOfTilesPerRow) == (mNumberOfTilesPerRow - 1)) {
            mShelves.add(new Shelf(null, ShelfType.END));
          } else {
            mShelves.add(new Shelf(null, ShelfType.NONE));
          }
        }
      }
    }
    mShelfAdapter.notifyDataSetChanged();
  }

  /**
   * Create an empty shelf, in preparation for the books
   */
  private void initShelf(final List<ShelfBook> books) {
    setColumnWidth(Utils.dpToPixels(getContext(), getResources().getInteger(R.integer.shelf_column_width)));
    setHorizontalSpacing(0);
    setVerticalSpacing(0);
    setNumColumns(AUTO_FIT);
    setVerticalScrollBarEnabled(false);
    setHorizontalScrollBarEnabled(false);

    // width & height of the shelfView will always return 0 because view is yet to be "shown" after creation;
    // Their actual values need to be captured here
    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
          getViewTreeObserver().removeGlobalOnLayoutListener(this);
        } else {
          getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        mShelfWidth = Utils.pixelsToDp(getContext(), getWidth());
        mShelfHeight = Utils.pixelsToDp(getContext(), getHeight());
        mNumberOfTilesPerRow = mShelfWidth / mGridViewColumnWidth;

        int sizeOfModel = books.size();
        int numberOfRows = sizeOfModel / mNumberOfTilesPerRow;
        int remainderTiles = sizeOfModel % mNumberOfTilesPerRow;

        if (remainderTiles > 0) {
          numberOfRows = numberOfRows + 1;
          int fillUp = mNumberOfTilesPerRow - remainderTiles;
          for (int i = 0; i < fillUp; i++) {
            if (i == (fillUp - 1)) {
              mShelves.add(new Shelf(null, ShelfType.END));
            } else {
              mShelves.add(new Shelf(null, ShelfType.NONE));
            }
          }
        }

        if ((numberOfRows * mGridItemHeight) < mShelfHeight) {
          int remainderRowHeight =
              (mShelfHeight - (numberOfRows * mGridItemHeight)) / mGridItemHeight;

          if (remainderRowHeight == 0) {
            for (int i = 0; i < mNumberOfTilesPerRow; i++) {
              if (i == 0) {
                mShelves.add(new Shelf(null, ShelfType.START));
              } else if (i == (mNumberOfTilesPerRow - 1)) {
                mShelves.add(new Shelf(null, ShelfType.END));
              } else {
                mShelves.add(new Shelf(null, ShelfType.NONE));
              }
            }
          } else if (remainderRowHeight > 0) {
            int fillUp = mNumberOfTilesPerRow * (remainderRowHeight + 1);
            for (int i = 0; i < fillUp; i++) {
              if ((i % mNumberOfTilesPerRow) == 0) {
                mShelves.add(new Shelf(null, ShelfType.START));
              } else if ((i % mNumberOfTilesPerRow) == (mNumberOfTilesPerRow - 1)) {
                mShelves.add(new Shelf(null, ShelfType.END));
              } else {
                mShelves.add(new Shelf(null, ShelfType.NONE));
              }
            }
          }
        }
        // tell that we can load the book again.
        mIsFinishInitializing = true;
        loadBooks(mBooks);
      }
    });
  }

  /**
   * Update book progress in the shelf
   *
   * @param bookId id of the book
   * @param progress progress of the book, minimum is 0 maximum is 100
   */
  public void updateBookProgress(String bookId, int progress) {
    if (mBooks == null) return;
    if(progress < 0 || progress > 100) return;

    List<View> views = new ArrayList<>();
    for (int i = getChildCount(); --i >= 0; ) {
      views.add(getChildAt(i));
    }
    if(views.size() == 0) return;

    // update the book on the shelves
    for(int i = 0; i < mShelves.size(); i++) {
      ShelfBook book = mShelves.get(i).getShelfBook();
      if(book != null) {
        if(book.getId().equals(bookId)) {
          Log.d(TAG, "Found the book on the shelves with book id = " + bookId);
          book.setProgress(progress);
          break;
        }
      }
    }

    mShelfAdapter.setBookProgress(views, bookId, progress);
    invalidate();
  }

  public List<ShelfBook> getBooks() {
    return mBooks;
  }
}
