package com.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Log {
  @Id
  private long id;
  private String userName;
  private String text;

  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }
  public String getUserName() {
    return userName;
  }
  public void setUserName(String userName) {
    this.userName = userName;
  }
  public String getText() {
    return text;
  }
  public void setText(String text) {
    this.text = text;
  }
}