package com.example.wujia2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/** Created by bruce on 2016/11/1. HomeActivity 主界面 */
public class RegisterActivity extends AppCompatActivity {

  private Button btn_loginUI;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (Build.VERSION.SDK_INT >= 21) {
      View decorView = getWindow().getDecorView();
      decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
      getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    setContentView(R.layout.activity_register);
    btn_loginUI = (Button) findViewById(R.id.btn_loginUI);
    btn_loginUI.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
      }
    });


  }
}
