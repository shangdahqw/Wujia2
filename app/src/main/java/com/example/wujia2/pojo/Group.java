package com.example.wujia2.pojo;

import java.io.Serializable;
import java.util.Date;

public class Group implements Serializable {


  private Long id;
  private Long createdUserId; // 创建用户
  private String groupName;
  private String nickname;
  private Date created;
  private String imageUrl;
  private String introduce;
  private Long numPost;


  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
  public String getIntroduce() {
    return introduce;
  }

  public void setIntroduce(String introduce) {
    this.introduce = introduce;
  }

  public Long getNumPost() {
    return numPost;
  }

  public void setNumPost(Long numPost) {
    this.numPost = numPost;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCreatedUserId() {
    return createdUserId;
  }

  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }


  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }



}
