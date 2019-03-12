package com.example.wujia2.pojo;

import java.util.Date;

public class Group {


  private Long id;
  private Long createdUserId; // 创建用户

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

  public String getNotice() {
    return notice;
  }

  public void setNotice(String notice) {
    this.notice = notice;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  private String groupName;
  private String notice;
  private Date created;


  @Override
  public String toString() {
    return "Group{" +
            "id=" + id +
            ", createdUserId=" + createdUserId +
            ", groupName='" + groupName + '\'' +
            ", notice='" + notice + '\'' +
            ", created=" + created +
            '}';
  }
}
