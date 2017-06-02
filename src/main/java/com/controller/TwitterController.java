package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import twitter4j.*;
import twitter4j.conf.*;
import oauth.signpost.*;
import oauth.signpost.basic.*;
import oauth.signpost.http.HttpParameters;

import javax.servlet.http.HttpSession;

import com.model.Auth;
import com.model.TwitterForm;

@Controller
public class TwitterController {

	@Autowired
	HttpSession session;

  @Autowired
  Auth auth;

  public static final String CONCUMER_KEY = "2sEcph9BOK5Hk412wQ8qJaSI1";
  public static final String CONSUMER_SECRET = "IKHsWgt959b693MTCYc5BYMQOXsDqR4I3m9VXqW0zk7sKvyXdA";
  public static final String REQUEST_TOKEN_URI = "https://api.twitter.com/oauth/request_token";
  public static final String ACCESS_TOKEN_URI = "https://api.twitter.com/oauth/access_token";
  public static final String AUTHORIZE_URI = "https://api.twitter.com/oauth/authorize";
  public static final String CALLBACK_URI = "https://glacial-river-49306.herokuapp.com/auth/";
//  public static final String CALLBACK_URI = "http://localhost:5000/auth/";
  
  public static final String SK_CONSUMER = "consumer";
  public static final String SK_PROVIDER = "provider";
  public static final String SK_ACCESS_TOKEN = "accessToken";
  public static final String SK_TOKEN_SECRET = "tokenSecret";
  public static final String SK_USER_ID = "user_id";
  public static final String SK_SCREEN_NAME = "screen_name";

  public static final String RK_AUTH_URI = "authUri";
  public static final String RK_OAUTH_TOKEN = "oauth_token";
  public static final String RK_OAUTH_VERIFIER = "oauth_verifier";
  public static final String RK_USER_ID = "user_id";
  public static final String RK_SCREEN_NAME = "screen_name";
  public static final String RK_F_NAME = "f_name";
  public static final String RK_TWEETS = "tweets";
  public static final String RK_IS_ERROR = "isError";
  public static final String RK_MESSAGE = "message";
  public static final String RK_FAV_COUNT = "fav_count";
  
  public static final int TW_PAGING_COUNT = 20;

  @ModelAttribute
  TwitterForm setUpForm() {
    TwitterForm form = new TwitterForm();
    form.setMessage("off");
    return form;
  }

  @RequestMapping("/")

  String index(@ModelAttribute TwitterForm form, Model model) {
    auth.setConsumer(new DefaultOAuthConsumer(CONCUMER_KEY, CONSUMER_SECRET));
    auth.setProvider(new DefaultOAuthProvider(REQUEST_TOKEN_URI, ACCESS_TOKEN_URI, AUTHORIZE_URI));
    try {
      form.setAuthUri(auth.getProvider().retrieveRequestToken(auth.getConsumer(), CALLBACK_URI));
    } catch (Exception e) {
      form.setMessage(e.getMessage());
    }
    model.addAttribute("form", form);
    model.addAttribute("auth", auth);
    return "twitter/favbom";
  }

  @RequestMapping("/auth")
  String auth(
    @RequestParam(RK_OAUTH_TOKEN) String oauth_token
    , @RequestParam(RK_OAUTH_VERIFIER) String oauth_verifier
    , @ModelAttribute TwitterForm form, Model model) {

    OAuthConsumer consumer = auth.getConsumer();
    OAuthProvider provider = auth.getProvider();

    try {
      provider.retrieveAccessToken(consumer, oauth_verifier);
      HttpParameters hp = provider.getResponseParameters();
      auth.setUserId(hp.get("user_id").first());
      auth.setUserName(hp.get("screen_name").first());
      auth.setAccessToken(consumer.getToken());
      auth.setTokenSecret(consumer.getTokenSecret());
    } catch (Exception e) {
      form.setMessage(e.getMessage());
    }
    model.addAttribute("form", form);
    model.addAttribute("auth", auth);
    return "twitter/favbom";
  }
/*
  @RequestMapping("/auth")
  String auth(@RequestParam(RK_OAUTH_TOKEN) String oauth_token, @RequestParam(RK_OAUTH_VERIFIER) String oauth_verifier, ModelMap modelMap) {
    try {
      // セッションからOAuthオブジェクトを取得
      OAuthConsumer consumer = (OAuthConsumer) session.getAttribute(SK_CONSUMER);
      OAuthProvider provider = (OAuthProvider) session.getAttribute(SK_PROVIDER);

      // アクセストークンを取得
      provider.retrieveAccessToken(consumer, oauth_verifier);
      String accessToken = consumer.getToken();
      String tokenSecret = consumer.getTokenSecret();

      // ユーザ情報を取得
      HttpParameters hp = provider.getResponseParameters();
      String user_id = hp.get("user_id").first();
      String screen_name = hp.get("screen_name").first();

      // セッションへアクセストークン・ユーザ情報をセット
      session.setAttribute(SK_ACCESS_TOKEN, accessToken);
      session.setAttribute(SK_TOKEN_SECRET, tokenSecret);
      session.setAttribute(SK_USER_ID, user_id);
      session.setAttribute(SK_SCREEN_NAME, screen_name);

      // リクエストをセット
      modelMap.addAttribute(RK_USER_ID, user_id);
      modelMap.addAttribute(RK_SCREEN_NAME, screen_name);
      modelMap.addAttribute(RK_AUTH_URI, "#");
      
    } catch (Exception e) {
      modelMap.addAttribute(RK_IS_ERROR, true);
      modelMap.addAttribute(RK_MESSAGE, e.getMessage());
    }

    return "twitter/favbom";
  }
/*
  @RequestMapping("/fav")
  String fav(@RequestParam(RK_F_NAME) String fname, @RequestParam(RK_FAV_COUNT) String favCount, ModelMap modelMap) {
    try {
      // セッションからアクセストークンなどを取得
      String accessToken = (String) session.getAttribute(SK_ACCESS_TOKEN);
      String tokenSecret = (String) session.getAttribute(SK_TOKEN_SECRET);
      String user_id = (String) session.getAttribute(SK_USER_ID);
      String screen_name = (String) session.getAttribute(SK_SCREEN_NAME);

      // twitter4jオブジェクトを作成
      ConfigurationBuilder cb = new ConfigurationBuilder();
      cb.setDebugEnabled(true).setOAuthConsumerKey(CONCUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET).setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(tokenSecret);
      TwitterFactory tf = new TwitterFactory(cb.build());
      Twitter twitter = tf.getInstance();

      int _favCount = Integer.parseInt(favCount);
      List<String> tweets = new ArrayList<String>();

      int pageCounter = 1;
      int favCounter = 0;
      while(favCounter < _favCount) {
        // 指定ユーザのタイムラインを取得
        ResponseList<Status> statuses = twitter.getUserTimeline(fname, new Paging(pageCounter ++, TW_PAGING_COUNT));
        if (statuses.size() == 0) {
          break;
        }

        // タイムラインにイイネを作成する
        for(Status status : statuses){
          if(favCounter >= _favCount) {
            break;
          }
          if(!status.isFavorited()) {
            twitter.createFavorite(status.getId());
            tweets.add(status.getText());
            favCounter ++;
          }
        }
      }

      // リクエストをセット
      modelMap.addAttribute(RK_TWEETS, tweets);
      modelMap.addAttribute(RK_USER_ID, user_id);
      modelMap.addAttribute(RK_SCREEN_NAME, screen_name);
      modelMap.addAttribute(RK_F_NAME, fname);
      modelMap.addAttribute(RK_FAV_COUNT, favCount);
      modelMap.addAttribute(RK_AUTH_URI, "#");
    } catch (Exception e) {
      modelMap.addAttribute(RK_IS_ERROR, true);
      modelMap.addAttribute(RK_MESSAGE, e.getMessage());
    }
    return "twitter/favbom";
  }
  

  @RequestMapping("/watch")
  String watch(ModelMap modelMap) {
      // セッションからアクセストークンなどを取得
      String accessToken = (String) session.getAttribute(SK_ACCESS_TOKEN);
      String tokenSecret = (String) session.getAttribute(SK_TOKEN_SECRET);
      String user_id = (String) session.getAttribute(SK_USER_ID);
      String screen_name = (String) session.getAttribute(SK_SCREEN_NAME);

      // リクエストをセット
      modelMap.addAttribute(RK_USER_ID, user_id);
      modelMap.addAttribute(RK_SCREEN_NAME, screen_name);
    return "twitter/watch";
  }

  @RequestMapping("/gettweets")
  String gettweets(@RequestParam(RK_F_NAME) String fname, @RequestParam(RK_FAV_COUNT) String favCount, ModelMap modelMap) {
      // セッションからアクセストークンなどを取得
      String accessToken = (String) session.getAttribute(SK_ACCESS_TOKEN);
      String tokenSecret = (String) session.getAttribute(SK_TOKEN_SECRET);
      String user_id = (String) session.getAttribute(SK_USER_ID);
      String screen_name = (String) session.getAttribute(SK_SCREEN_NAME);

      // twitter4jオブジェクトを作成
      ConfigurationBuilder cb = new ConfigurationBuilder();
      cb.setDebugEnabled(true).setOAuthConsumerKey(CONCUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET).setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(tokenSecret);
      TwitterFactory tf = new TwitterFactory(cb.build());
      Twitter twitter = tf.getInstance();

      try {
        int _favCount = Integer.parseInt(favCount);
        List<Map> tweets = new ArrayList<Map>();

        int pageCounter = 1;
        int favCounter = 0;
        while(favCounter < _favCount) {
          // 指定ユーザのタイムラインを取得
          ResponseList<Status> statuses = twitter.getUserTimeline(fname, new Paging(pageCounter ++, TW_PAGING_COUNT));
          if (statuses.size() == 0) {
            break;
          }

          for(Status status : statuses){
            if(favCounter >= _favCount) {
              break;
            }
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("id", String.valueOf(status.getId()));
            map.put("tweet", status.getText());
            if(status.isFavorited()) {
              map.put("faved", "on");
            } else {
              map.put("faved", "off");
            }
            tweets.add(map);
            favCounter ++;
          }
        }
        // リクエストをセット
        modelMap.addAttribute(RK_TWEETS, tweets);
        modelMap.addAttribute(RK_F_NAME, fname);
        modelMap.addAttribute(RK_FAV_COUNT, favCount);
        modelMap.addAttribute(RK_USER_ID, user_id);
        modelMap.addAttribute(RK_SCREEN_NAME, screen_name);
        modelMap.addAttribute(RK_AUTH_URI, "#");
      } catch (Exception e) {
        modelMap.addAttribute(RK_IS_ERROR, true);
        modelMap.addAttribute(RK_MESSAGE, e.getMessage());
      }
    return "twitter/watch";
  }

  
  @RequestMapping("/fav2")
  String fav2(@RequestParam("statusId") String statusId, @RequestParam(RK_F_NAME) String fname, @RequestParam(RK_FAV_COUNT) String favCount, ModelMap modelMap) {
    try {
      // セッションからアクセストークンなどを取得
      String accessToken = (String) session.getAttribute(SK_ACCESS_TOKEN);
      String tokenSecret = (String) session.getAttribute(SK_TOKEN_SECRET);
      String user_id = (String) session.getAttribute(SK_USER_ID);
      String screen_name = (String) session.getAttribute(SK_SCREEN_NAME);

      // twitter4jオブジェクトを作成
      ConfigurationBuilder cb = new ConfigurationBuilder();
      cb.setDebugEnabled(true).setOAuthConsumerKey(CONCUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET).setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(tokenSecret);
      TwitterFactory tf = new TwitterFactory(cb.build());
      Twitter twitter = tf.getInstance();


      twitter.createFavorite(Long.parseLong(statusId));
        int _favCount = Integer.parseInt(favCount);
        List<Map> tweets = new ArrayList<Map>();

        int pageCounter = 1;
        int favCounter = 0;
        while(favCounter < _favCount) {
          // 指定ユーザのタイムラインを取得
          ResponseList<Status> statuses = twitter.getUserTimeline(fname, new Paging(pageCounter ++, TW_PAGING_COUNT));
          if (statuses.size() == 0) {
            break;
          }

          for(Status status : statuses){
            if(favCounter >= _favCount) {
              break;
            }
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("id", String.valueOf(status.getId()));
            map.put("tweet", status.getText());
            if(status.isFavorited()) {
              map.put("faved", "on");
            } else {
              map.put("faved", "off");
            }
            tweets.add(map);
            favCounter ++;
          }
        }
        // リクエストをセット
        modelMap.addAttribute(RK_TWEETS, tweets);
        modelMap.addAttribute(RK_F_NAME, fname);
        modelMap.addAttribute(RK_FAV_COUNT, favCount);
        modelMap.addAttribute(RK_USER_ID, user_id);
        modelMap.addAttribute(RK_SCREEN_NAME, screen_name);
        modelMap.addAttribute(RK_AUTH_URI, "#");
      } catch (Exception e) {
        modelMap.addAttribute(RK_IS_ERROR, true);
        modelMap.addAttribute(RK_MESSAGE, e.getMessage());
      }
    return "twitter/watch";
  }
  */
}