package com.model;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;

public class TwitterForm implements Serializable {
  private static final long serialVersionUID = 1L;

  private String message;
  private String toUserName;
  private long favoriteCount;
  private List<Tweets> tweets;
  private String authUri;

  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
  public String getToUserName() {
    return toUserName;
  }
  public void setToUserName(String toUserName) {
    this.toUserName = toUserName;
  }
  public long getFavoriteCount() {
    return favoriteCount;
  }
  public void setFavoriteCount(long favoriteCount) {
    this.favoriteCount = favoriteCount;
  }
  public List<Tweets> getTweets() {
    if(tweets == null) {
      tweets = Collections.emptyList();
    }
    return tweets;
  }
  public void setTweets(List<Tweets> tweets) {
    this.tweets = tweets;
  }
  public String getAuthUri() {
    return authUri;
  }
  public void setAuthUri(String authUri) {
    this.authUri = authUri;
  }
}