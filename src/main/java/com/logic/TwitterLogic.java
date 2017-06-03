package com.logic;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import twitter4j.*;
import com.model.Tweet;
import com.model.TwitterUser;

import com.repository.LogRepository;
import com.entity.Log;

@Component
public class TwitterLogic {

  @Autowired
  LogRepository logRepository;

  public void logging(String userName, String text) {
    Log log = new Log();


    System.out.println();

    log.setText(text);
    log.setUserName(new Date().toString() + " : " + userName);
    logRepository.save(log);
  }

  public List<TwitterUser> getFriends(Twitter twitter, String userName) throws Exception {
    
    List<TwitterUser> friends = new ArrayList<TwitterUser>();
    long cursor = - 1L;
    do {
        PagableResponseList<User> response = twitter.getFriendsList(userName, cursor);
        for (User user : response) {
          TwitterUser twitterUser = new TwitterUser();
          twitterUser.setId(user.getId());
          twitterUser.setImageUri(user.getProfileImageURL());
          twitterUser.setName(user.getName());
          twitterUser.setScreenName(user.getScreenName());

          friends.add(twitterUser);
        }
        cursor = response.getNextCursor();
    } while (cursor > 0);

    return friends;
  }

  public List<Tweet> doFavorite(Twitter twitter, long favoriteCount, String toUserName, int pagingCount, boolean doFavorited, boolean isExcludeRep) throws Exception {
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

        if(isExcludeRep && status.getInReplyToStatusId() != -1) {
          continue;
        }

        Tweet tweet = new Tweet();
        tweet.setId(status.getId());
        tweet.setFavorited(status.isFavorited());
        tweet.setText(status.getText());
        tweets.add(tweet);
        favoriteCounter ++;
        if(doFavorited) {
          twitter.createFavorite(status.getId());
          tweet.setFavorited(true);
        }
      }
    }
    return tweets;
  }

  
}