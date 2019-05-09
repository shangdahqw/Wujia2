package com.example.wujia2.pojo;

import java.util.Date;

public class Circle {

  private Long id;

  private String content; // 家人圈文字部分
  private Date created; // 创建时间
  private String images; // 家人圈图片部分
  private Long userId;

  public Long getNumReply() {
    return numReply;
  }

  public void setNumReply(Long numReply) {
    this.numReply = numReply;
  }

  public Long getNumLikes() {
    return numLikes;
  }

  public void setNumLikes(Long numLikes) {
    this.numLikes = numLikes;
  }

  private Long numReply;
  private Long numLikes;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getImages() {
    return images;
  }

  public void setImages(String images) {
    this.images = images;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getGroupIds() {
    return groupIds;
  }

  public void setGroupIds(String groupIds) {
    this.groupIds = groupIds;
  }

  private String groupIds; // 群组ids
}
