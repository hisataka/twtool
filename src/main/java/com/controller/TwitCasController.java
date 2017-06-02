package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.ModelMap;

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

@Controller
@SpringBootApplication
public class TwitCasController {

  public static final String CLIENT_ID = "3016797086.5191731039b84b69797f1ee09ab0ffd6c287a8d81e4a870e55b7cf2c4a9923f3";
//https://apiv2.twitcasting.tv/oauth2/authorize?client_id={YOUR_CLINET_ID}&response_type=code&state={CSRF_TOKEN}
  @RequestMapping("/twicas")
  String twicas() {
    return "twicas/twicas";
  }  
}