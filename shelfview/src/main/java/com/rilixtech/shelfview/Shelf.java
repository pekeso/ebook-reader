package com.rilixtech.shelfview;

class Shelf {
  private final ShelfBook shelfBook;
  private final ShelfType type;

  Shelf(ShelfBook shelfBook, ShelfType type) {
    this.shelfBook = shelfBook;
    this.type = type;
  }

  public ShelfBook getShelfBook() {
    return shelfBook;
  }

  public ShelfType getType() {
    return type;
  }
}

