package com.model;

import java.io.Serializable;
import oauth.signpost.*;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import twitter4j.Twitter;

import java.util.List;
import java.util.Collections;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Auth implements Serializable {
  private static final long serialVersionUID = 1L;

  private OAuthConsumer consumer;
  private OAuthProvider provider;
  private Twitter twitter;
  private String userId;
  private String userName;
  private String authUri;
  private List<TwitterUser> friends;
  private String imageUri;
  public OAuthConsumer getConsumer() {
    return consumer;
  }
  public void setConsumer(OAuthConsumer consumer) {
    this.consumer = consumer;
  }
  public OAuthProvider getProvider() {
    return provider;
  }
  public void setProvider(OAuthProvider provider) {
    this.provider = provider;
  }
  public Twitter getTwitter() {
    return twitter;
  }
  public void setTwitter(Twitter twitter) {
    this.twitter = twitter;
  }
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public String getUserName() {
    return userName;
  }
  public void setUserName(String userName) {
    this.userName = userName;
  }
  public String getAuthUri() {
    return authUri;
  }
  public void setAuthUri(String authUri) {
    this.authUri = authUri;
  }
  public List<TwitterUser> getFriends() {
    if(friends == null) {
      friends = Collections.emptyList();
    }
    return friends;
  }
  public void setFriends(List<TwitterUser> friends) {
    this.friends = friends;
  }
  public String getImageUri() {
    return imageUri;
  }
  public void setImageUri(String imageUri) {
    this.imageUri = imageUri;
  }
}