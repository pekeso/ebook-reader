package com.rilixtech.shelfview;

public final class Configuration {

  private static String sNoImageFound = "NO IMAGE FOUND";
  private static boolean titleAsPlaceHolder;

  static String getNoImageFound() {
    return sNoImageFound;
  }

  static void setNoImageFound(String noImageFound) {
    sNoImageFound = noImageFound == null || noImageFound.isEmpty() ? "NO IMAGE FOUND": noImageFound;
  }

  static boolean isTitleAsPlaceHolder() {
    return titleAsPlaceHolder;
  }

  static void setTitleAsPlaceHolder(boolean titleAsPlaceHolder) {
    Configuration.titleAsPlaceHolder = titleAsPlaceHolder;
  }
}
