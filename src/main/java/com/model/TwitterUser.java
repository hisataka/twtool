package com.model;

public class TwitterUser {
  private long id;
  private String screenName;
  private String name;
  private String imageUri;

  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }
  public String getScreenName() {
    return screenName;
  }
  public void setScreenName(String screenName) {
    this.screenName = screenName;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getImageUri() {
    return imageUri;
  }
  public void setImageUri(String imageUri) {
    this.imageUri = imageUri;
  }
}