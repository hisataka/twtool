package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.util.List;
import java.util.ArrayList;

import twitter4j.*;
import twitter4j.conf.*;
import oauth.signpost.*;
import oauth.signpost.basic.*;
import oauth.signpost.http.HttpParameters;

import com.model.Auth;
import com.model.TwitterForm;
import com.model.Tweet;
import com.model.TwitterConfig;

@Controller
public class TwitterController {
  @Autowired
  Auth auth;

  @Autowired
  TwitterConfig twitterConfig;

  @ModelAttribute
  TwitterForm setUpForm() {
    TwitterForm form = new TwitterForm();
    form.setMessage("off");
    form.setFavoriteCount(Integer.parseInt(twitterConfig.getDefaultFavoriteCount()));
    return form;
  }

  @RequestMapping("/")
  String index(@ModelAttribute TwitterForm form, Model model) {
    auth.setConsumer(new DefaultOAuthConsumer(twitterConfig.getConsumerKey(), twitterConfig.getConsumerSecret()));
    auth.setProvider(new DefaultOAuthProvider(twitterConfig.getRequestTokenUri(), twitterConfig.getAccessTokenUri(), twitterConfig.getAuthorizeUri()));
    try {
      form.setAuthUri(auth.getProvider().retrieveRequestToken(auth.getConsumer(), twitterConfig.getCallbackUri()));
    } catch (Exception e) {
      form.setMessage(e.getMessage());
    }
    model.addAttribute("form", form);
    model.addAttribute("auth", auth);
    return "twitter/favbom";
  }

  @RequestMapping("/auth")
  String auth(
    @RequestParam("oauth_token") String oauth_token
    , @RequestParam("oauth_verifier") String oauth_verifier
    , @ModelAttribute TwitterForm form, Model model) {

    try {
      auth.getProvider().retrieveAccessToken(auth.getConsumer(), oauth_verifier);
      HttpParameters hp = auth.getProvider().getResponseParameters();
      auth.setUserId(hp.get("user_id").first());
      auth.setUserName(hp.get("screen_name").first());

      ConfigurationBuilder cb = new ConfigurationBuilder();
      cb.setDebugEnabled(true)
        .setOAuthConsumerKey(twitterConfig.getConsumerKey())
        .setOAuthConsumerSecret(twitterConfig.getConsumerSecret())
        .setOAuthAccessToken(auth.getConsumer().getToken())
        .setOAuthAccessTokenSecret(auth.getConsumer().getTokenSecret());

      auth.setTwitter(new TwitterFactory(cb.build()).getInstance());
    } catch (Exception e) {
      form.setMessage(e.getMessage());
    }
    model.addAttribute("form", form);
    model.addAttribute("auth", auth);
    return "twitter/favbom";
  }

  @RequestMapping("/fav")
  String fav(@ModelAttribute TwitterForm form, Model model) {
    try{
      List<Tweet> tweets = new ArrayList<Tweet>();
      int pageCounter = 1;
      int favCounter = 0;
      while(favCounter < form.getFavoriteCount()) {
        ResponseList<Status> statuses = auth.getTwitter().getUserTimeline(form.getToUserName(), new Paging(pageCounter ++, Integer.parseInt(twitterConfig.getPagingCount())));
        if (statuses.size() == 0) {
          break;
        }

        for(Status status : statuses){
          if(favCounter >= form.getFavoriteCount()) {
            break;
          }
          if(!status.isFavorited() && status.getInReplyToStatusId() == -1) {
            Tweet tweet = new Tweet();
            tweet.setId(status.getId());
            tweet.setFavorited(status.isFavorited());
            tweet.setText(status.getText());
            tweets.add(tweet);

            auth.getTwitter().createFavorite(tweet.getId());
            favCounter ++;
          }
        }
      }
      form.setTweets(tweets);
    }  catch (Exception e) {
      form.setMessage(e.getMessage());
    }
    model.addAttribute("form", form);
    model.addAttribute("auth", auth);
    return "twitter/favbom";
  }

  
/*
  

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