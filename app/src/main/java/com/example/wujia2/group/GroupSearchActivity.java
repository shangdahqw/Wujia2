package com.example.wujia2.group;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.wujia2.R;

/** Created by bruce on 2016/11/1. HomeActivity 主界面 */
public class GroupSearchActivity extends AppCompatActivity {

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

//    setContentView(R.layout.activity_group_search_before);
    setContentView(R.layout.activity_group_create);


  }
}



