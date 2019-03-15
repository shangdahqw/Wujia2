package com.example.wujia2.pojo;

import java.util.Date;

public class Reply {

  private Long id;
  private Long userId;
  private Long userTo;
  private String content;
  private Date created;
  private Long circleId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getUserTo() {
    return userTo;
  }

  public void setUserTo(Long userTo) {
    this.userTo = userTo;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Long getCircleId() {
    return circleId;
  }

  public void setCircleId(Long circleId) {
    this.circleId = circleId;
  }


}
