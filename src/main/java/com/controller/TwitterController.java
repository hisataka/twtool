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

import com.model.Auth;
import com.model.TwitterForm;
import com.model.TwitterConfig;
import com.logic.TwitterLogic;

@Controller
@SessionAttributes("scopedTarget.auth")
public class TwitterController {
  @Autowired
  Auth auth;

  @Autowired
  TwitterConfig twitterConfig;

  @Autowired
  TwitterLogic twitterLogic;

  @ModelAttribute
  TwitterForm setUpForm() {
    TwitterForm form = new TwitterForm();
    form.setMessage("off");
    form.setFavoriteCount(Integer.parseInt(twitterConfig.getDefaultFavoriteCount()));
    
    if(auth.getConsumer() == null || auth.getProvider() == null) {
      auth.setConsumer(new DefaultOAuthConsumer(twitterConfig.getConsumerKey(), twitterConfig.getConsumerSecret()));
      auth.setProvider(new DefaultOAuthProvider(twitterConfig.getRequestTokenUri(), twitterConfig.getAccessTokenUri(), twitterConfig.getAuthorizeUri()));
      try {
        auth.setAuthUri(auth.getProvider().retrieveRequestToken(auth.getConsumer(), twitterConfig.getCallbackUri()));
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
        .setOAuthConsumerKey(twitterConfig.getConsumerKey())
        .setOAuthConsumerSecret(twitterConfig.getConsumerSecret())
        .setOAuthAccessToken(auth.getConsumer().getToken())
        .setOAuthAccessTokenSecret(auth.getConsumer().getTokenSecret());

      auth.setTwitter(new TwitterFactory(cb.build()).getInstance());
      auth.setFriends(twitterLogic.getFriends(auth.getTwitter(), auth.getUserName()));
      auth.setImageUri(auth.getTwitter().showUser(auth.getUserName()).getProfileImageURL());
      
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
      form.setTweets(twitterLogic.doFavorite(auth.getTwitter(), form.getFavoriteCount(), form.getToUserName(), Integer.parseInt(twitterConfig.getPagingCount()), true));
    }  catch (Exception e) {
      form.setMessage(e.getMessage());
    }
    model.addAttribute("form", form);
    model.addAttribute("auth", auth);
    return "twitter/favbom";
  }
  
  @RequestMapping(value="/doSomething", params="getTweet")
  String getTweet(@ModelAttribute TwitterForm form, Model model) {
    try{
      form.setTweets(twitterLogic.doFavorite(auth.getTwitter(), form.getFavoriteCount(), form.getToUserName(), Integer.parseInt(twitterConfig.getPagingCount()), false));
    }  catch (Exception e) {
      form.setMessage(e.getMessage());
    }
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
		return "";
	}
}