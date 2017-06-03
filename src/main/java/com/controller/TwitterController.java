package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.ui.Model;

import twitter4j.*;
import twitter4j.conf.*;
import oauth.signpost.basic.*;
import oauth.signpost.http.HttpParameters;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.model.Auth;
import com.model.TwitterForm;
import com.logic.TwitterLogic;
import com.repository.SystemValueRepository;
import com.entity.SystemValue;
import com.repository.LogRepository;
import com.entity.Log;

@Controller
@SessionAttributes("scopedTarget.auth")
public class TwitterController {
  @Autowired
  Auth auth;

  @Autowired
  TwitterLogic twitterLogic;

  @Autowired
  SystemValueRepository systemValueRepository;

  @ModelAttribute
  TwitterForm setUpForm() {
    TwitterForm form = new TwitterForm();
    form.setMessage("off");
  
    if(auth.getConsumer() == null || auth.getProvider() == null) {
      List<SystemValue> list = systemValueRepository.findAll();
      Map<String, String> systemConfig = new HashMap<String, String>();
      for(SystemValue l: list) {
        systemConfig.put(l.getKey(), l.getValue());
      }
      auth.setSystemConfig(systemConfig);
      auth.setConsumer(new DefaultOAuthConsumer(systemConfig.get("CONSUMER_KEY"), systemConfig.get("CONSUMER_SECRET")));
      auth.setProvider(new DefaultOAuthProvider(systemConfig.get("REQUEST_TOKEN_URI"), systemConfig.get("ACCESS_TOKEN_URI"), systemConfig.get("AUTHORIZE_URI")));
      try {
        auth.setAuthUri(auth.getProvider().retrieveRequestToken(auth.getConsumer(), systemConfig.get("CALLBACK_URI")));
      } catch (Exception e) {
        form.setMessage(e.getMessage());
      }
    }
    return form;
  }

  @RequestMapping("/")
  String index(@ModelAttribute TwitterForm form, Model model) {
    model.addAttribute("form", form);
    model.addAttribute("auth", auth);
    return "twitter/favbom";
  }

  @RequestMapping("/logout")
  String logout(@ModelAttribute TwitterForm form, Model model, SessionStatus sessionStatus) {
    twitterLogic.logging(auth.getUserName(), "ログアウト");
    sessionStatus.setComplete();
    model.addAttribute("form", form);
    model.addAttribute("auth", auth);
    return "redirect:/";
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
        .setOAuthConsumerKey(auth.getSystemConfig().get("CONSUMER_KEY"))
        .setOAuthConsumerSecret(auth.getSystemConfig().get("CONSUMER_SECRET"))
        .setOAuthAccessToken(auth.getConsumer().getToken())
        .setOAuthAccessTokenSecret(auth.getConsumer().getTokenSecret());

      auth.setTwitter(new TwitterFactory(cb.build()).getInstance());
      auth.setFriends(twitterLogic.getFriends(auth.getTwitter(), auth.getUserName()));
      auth.setImageUri(auth.getTwitter().showUser(auth.getUserName()).getProfileImageURL());

      twitterLogic.logging(auth.getUserName(), "ログイン");
      
    } catch (Exception e) {
      form.setMessage(e.getMessage());
    }
    model.addAttribute("form", form);
    model.addAttribute("auth", auth);
    return "twitter/favbom";
  }

  @RequestMapping(value="/doSomething", params="doFavorite")
  String doFavorite(@ModelAttribute TwitterForm form, Model model) {
    try{
      form.setTweets(twitterLogic.doFavorite(auth.getTwitter(), form.getFavoriteCount(), form.getToUserName(), Integer.parseInt(auth.getSystemConfig().get("PAGING_COUNT")), true));
    }  catch (Exception e) {
      form.setMessage(e.getMessage());
    }
    twitterLogic.logging(auth.getUserName(), "doFavorite: " + form.getFavoriteCount() + "件: " + form.getToUserName());
    model.addAttribute("form", form);
    model.addAttribute("auth", auth);
    return "twitter/favbom";
  }
  
  @RequestMapping(value="/doSomething", params="getTweet")
  String getTweet(@ModelAttribute TwitterForm form, Model model) {
    try{
      form.setTweets(twitterLogic.doFavorite(auth.getTwitter(), form.getFavoriteCount(), form.getToUserName(), Integer.parseInt(auth.getSystemConfig().get("PAGING_COUNT")), false));
    }  catch (Exception e) {
      form.setMessage(e.getMessage());
    }
    twitterLogic.logging(auth.getUserName(), "getTweet: " + form.getFavoriteCount() + "件: " + form.getToUserName());
    model.addAttribute("form", form);
    model.addAttribute("auth", auth);
    return "twitter/favbom";
  }

  @RequestMapping(value="/singlefav")
	@ResponseBody
	public String singlefav(@ModelAttribute TwitterForm form, Model model) {
    try {
      auth.getTwitter().createFavorite(form.getFavoriteId());
    }  catch (Exception e) {
      return e.toString();
    }
    twitterLogic.logging(auth.getUserName(), "singlefav: " + form.getFavoriteId());
		return "";
	}
}