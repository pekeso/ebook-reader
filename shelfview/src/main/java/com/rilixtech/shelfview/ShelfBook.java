package com.rilixtech.shelfview;

/**
 * Represent book in a single shelf
 */
public class ShelfBook {
  private String coverSource;
  private String id;
  private String title;
  private BookSourceType bookSourceType;
  private int progress;

  public ShelfBook() {
    coverSource = "";
    id = "";
    title = "";
    bookSourceType = BookSourceType.NONE;
    progress = 0;
  }

  private ShelfBook(String coverSource, String id, String title, BookSourceType bookSourceType,
      int progress) {
    this.coverSource = coverSource;
    this.id = id;
    this.title = title;
    this.bookSourceType = bookSourceType;
    this.progress = progress;
  }

  public String getCoverSource() {
    return coverSource;
  }

  public void setCoverSource(String coverSource) {
    this.coverSource = coverSource;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public BookSourceType getBookSourceType() {
    return bookSourceType;
  }

  public void setBookSourceType(BookSourceType bookSourceType) {
    this.bookSourceType = bookSourceType;
  }

  /**
   * Set book model from path of image file from external or internal sdcard
   *
   * @param coverImagePath path of cover image.
   * @param bookId id of the book
   * @param bookTitle title of the book
   * @return model of the book
   */
  public static ShelfBook createBookWithFile(String coverImagePath, String bookId, String bookTitle) {
    return new ShelfBook(coverImagePath, bookId, bookTitle, BookSourceType.FILE, 0);
  }

  public static ShelfBook createBookWithFile(String coverImagePath, String bookId, String bookTitle,
      int progress) {
    return new ShelfBook(coverImagePath, bookId, bookTitle, BookSourceType.FILE, progress);
  }

  public static ShelfBook createBookWithUrl(String bookCoverSource, String bookId, String bookTitle) {
    return new ShelfBook(bookCoverSource, bookId, bookTitle, BookSourceType.URL, 0);
  }

  public static ShelfBook createBookWithUrl(String bookCoverSource, String bookId, String bookTitle,
      int progress) {
    return new ShelfBook(bookCoverSource, bookId, bookTitle, BookSourceType.URL, progress);
  }

  /**
   * Add book with image from assets folder.
   *
   * @param assetName Name of assets file. Must be the complete name with extension.
   * @param bookId id of book.
   * @param bookTitle name of book.
   * @return model of book
   */
  public static ShelfBook createBookWithAsset(String assetName, String bookId, String bookTitle) {
    return new ShelfBook(assetName, bookId, bookTitle, BookSourceType.ASSET_FOLDER, 0);
  }

  public static ShelfBook createBookWithAsset(String assetName, String bookId, String bookTitle,
      int progress) {
    return new ShelfBook(assetName, bookId, bookTitle, BookSourceType.ASSET_FOLDER, progress);
  }

  public static ShelfBook createBookWithDrawable(String drawableName, String bookId, String bookTitle) {
    return new ShelfBook(drawableName, bookId, bookTitle, BookSourceType.DRAWABLE_NAME, 0);
  }

  public static ShelfBook createBookWithDrawable(String drawableName, String bookId, String bookTitle,
      int progress) {
    return new ShelfBook(drawableName, bookId, bookTitle, BookSourceType.DRAWABLE_NAME, progress);
  }

  public static ShelfBook createBookWithDrawable(int drawableResId, String bookId, String bookTitle) {
    return new ShelfBook(String.valueOf(drawableResId), bookId, bookTitle, BookSourceType.DRAWABLE_ID,
        0);
  }

  public static ShelfBook createBookWithDrawable(int drawableResId, String bookId, String bookTitle,
      int progress) {
    return new ShelfBook(String.valueOf(drawableResId), bookId, bookTitle, BookSourceType.DRAWABLE_ID,
        progress);
  }

  public static ShelfBook createBookWithRaw(int rawResId, String bookId, String bookTitle) {
    return new ShelfBook(String.valueOf(rawResId), bookId, bookTitle, BookSourceType.RAW, 0);
  }

  public static ShelfBook createBookWithRaw(int rawResId, String bookId, String bookTitle, int progress) {
    return new ShelfBook(String.valueOf(rawResId), bookId, bookTitle, BookSourceType.RAW, progress);
  }

  public int getProgress() {
    return progress;
  }

  public void setProgress(int progress) {
    this.progress = progress;
  }
}

