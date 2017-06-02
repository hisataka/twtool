package com.model;

import java.util.List;

public class Tweets {
  private long id;
  private boolean favorited;
  private String text;

  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }
  public boolean isFavorited() {
    return favorited;
  }
  public void setFavorited(boolean favorited) {
    this.favorited = favorited;
  }
  public String getText() {
    return text;
  }
  public void setText(String text) {
    this.text = text;
  }
}