package com.model;

import java.io.Serializable;
import oauth.signpost.*;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Auth implements Serializable {
    private static final long serialVersionUID = 1L;

    private OAuthConsumer consumer;
    private OAuthProvider provider;
    private String accessToken;
    private String tokenSecret;
    private String userId;
    private String userName;

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
    public String getAccessToken() {
      return accessToken;
    }
    public void setAccessToken(String accessToken) {
      this.accessToken = accessToken;
    }
    public String getTokenSecret() {
      return tokenSecret;
    }
    public void setTokenSecret(String tokenSecret) {
      this.tokenSecret = tokenSecret;
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
}