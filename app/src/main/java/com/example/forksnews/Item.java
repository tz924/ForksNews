package com.example.forksnews;

public class Item {

  protected String title;
  protected String url;


  public Item(String title, String url) {
    this.title = title;
    this.url = url;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }
}
