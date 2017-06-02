package com.model;

import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Component
@ConfigurationProperties(prefix = "twitter")
public class TwitterConfig {
  private String consumerKey;
  private String consumerSecret;
  private String requestTokenUri;
  private String accessTokenUri;
  private String authorizeUri;
  private String callbackUri;
  private String pagingCount;
  private String defaultFavoriteCount;

  public String getConsumerKey() {
    return consumerKey;
  }
  public void setConsumerKey(String consumerKey) {
    this.consumerKey = consumerKey;
  }
  public String getConsumerSecret() {
    return consumerSecret;
  }
  public void setConsumerSecret(String consumerSecret) {
    this.consumerSecret = consumerSecret;
  }
  public String getRequestTokenUri() {
    return requestTokenUri;
  }
  public void setRequestTokenUri(String requestTokenUri) {
    this.requestTokenUri = requestTokenUri;
  }
  public String getAccessTokenUri() {
    return accessTokenUri;
  }
  public void setAccessTokenUri(String accessTokenUri) {
    this.accessTokenUri = accessTokenUri;
  }
  public String getAuthorizeUri() {
    return authorizeUri;
  }
  public void setAuthorizeUri(String authorizeUri) {
    this.authorizeUri = authorizeUri;
  }
  public String getCallbackUri() {
    return callbackUri;
  }
  public void setCallbackUri(String callbackUri) {
    this.callbackUri = callbackUri;
  }
  public String getPagingCount() {
    return pagingCount;
  }
  public void setPagingCount(String pagingCount) {
    this.pagingCount = pagingCount;
  }
  public String getDefaultFavoriteCount() {
    return defaultFavoriteCount;
  }
  public void setDefaultFavoriteCount(String defaultFavoriteCount) {
    this.defaultFavoriteCount = defaultFavoriteCount;
  }
}