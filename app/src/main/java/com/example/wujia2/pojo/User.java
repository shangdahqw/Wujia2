package com.example.wujia2.pojo;


import java.util.Date;

public class User {

  private Long id;
  private String username; // 用户名
  private String phone; // 电话
  private String imageUrl; // 头像地址
  private Date birthday;
  private String password; // 密码
  private Date created; // 创建时间
  private String salt; // 密码的盐值


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }


  @Override
  public String toString() {
    return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", phone='" + phone + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", birthday=" + birthday +
            ", password='" + password + '\'' +
            ", created=" + created +
            ", salt='" + salt + '\'' +
            '}';
  }
}
