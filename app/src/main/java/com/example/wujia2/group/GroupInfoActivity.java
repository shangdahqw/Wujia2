package com.example.wujia2.group;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.allen.library.SuperTextView;
import com.example.wujia2.R;
import com.example.wujia2.pojo.Group;
import com.squareup.picasso.Picasso;

/** Created by bruce on 2016/11/1. HomeActivity 主界面 */
public class GroupInfoActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      getWindow().setStatusBarColor(getResources().getColor(R.color.white)); // 设置状态栏颜色
      getWindow()
              .getDecorView()
              .setSystemUiVisibility(
                      View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    setContentView(R.layout.activity_group_info);
    SuperTextView tV_group = (SuperTextView)findViewById(R.id.group_stv);
    SuperTextView tV_group_head = (SuperTextView)findViewById(R.id.group_head);
    SuperTextView tV_groupid = (SuperTextView)findViewById(R.id.group_id);
    SuperTextView tV_groupname = (SuperTextView)findViewById(R.id.group_name);
    SuperTextView tV_group_nickname = (SuperTextView)findViewById(R.id.group_nickname);


    Group group =new Group();
    group= (Group) getIntent().getSerializableExtra("groupinfo");
    tV_group.setCenterBottomString(group.getGroupName());
    tV_group_head.setLeftString(group.getIntroduce());
    Picasso.with(GroupInfoActivity.this)
            .load(group.getImageUrl())
            .placeholder(R.mipmap.image_head)
            .into(tV_group_head.getLeftIconIV());

    tV_groupid.setRightString(group.getId().toString());
    tV_groupname.setRightString(group.getGroupName());
    tV_group_nickname.setRightString(group.getNickname());


  }
}
