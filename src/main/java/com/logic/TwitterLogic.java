package com.logic;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import twitter4j.*;
import twitter4j.conf.*;
import oauth.signpost.*;
import oauth.signpost.basic.*;
import oauth.signpost.http.HttpParameters;
import com.model.Tweet;

@Component
public class TwitterLogic {
    public List<Tweet> doFavorite(Twitter twitter, long favoriteCount, String toUserName, int pagingCount, boolean doFavorited) throws Exception {
    List<Tweet> tweets = new ArrayList<Tweet>();
    int pageCounter = 1;
    long favoriteCounter = 0;
    while(favoriteCounter < favoriteCount) {
      ResponseList<Status> statuses = twitter.getUserTimeline(toUserName, new Paging(pageCounter ++, pagingCount));
      if (statuses.size() == 0) {
        break;
      }

      for(Status status : statuses){
        if(favoriteCounter >= favoriteCount) {
          break;
        }
        if(!status.isFavorited() && status.getInReplyToStatusId() == -1) {
          Tweet tweet = new Tweet();
          tweet.setId(status.getId());
          tweet.setFavorited(status.isFavorited());
          tweet.setText(status.getText());
          tweets.add(tweet);

          if(doFavorited) {
            twitter.createFavorite(tweet.getId());
          }
          favoriteCounter ++;
        }
      }
    }
    return tweets;
  }

  
}