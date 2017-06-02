package com.logic;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import twitter4j.*;
import com.model.Tweet;
import com.model.TwitterUser;

@Component
public class TwitterLogic {

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

        if(!doFavorited) {
          Tweet tweet = new Tweet();
          tweet.setId(status.getId());
          tweet.setFavorited(status.isFavorited());
          tweet.setText(status.getText());
          tweets.add(tweet);
          favoriteCounter ++;
        } else if (!status.isFavorited() && status.getInReplyToStatusId() == -1) {
          twitter.createFavorite(status.getId());
          Tweet tweet = new Tweet();
          tweet.setId(status.getId());
          tweet.setFavorited(true);
          tweet.setText(status.getText());
          tweets.add(tweet);
          favoriteCounter ++;
        }
      }
    }
    return tweets;
  }

  
}