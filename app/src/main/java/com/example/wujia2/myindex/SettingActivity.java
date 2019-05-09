package com.example.wujia2.myindex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.example.wujia2.HomeActivity;
import com.example.wujia2.LoginActivity;
import com.example.wujia2.R;
import com.example.wujia2.utils.HttpUtil;


public class SettingActivity extends AppCompatActivity {

    private SuperTextView tv_logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.setting);

        tv_logout = (SuperTextView) findViewById(R.id.logout);
        tv_logout.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                SharedPreferences preferences;
                preferences = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token", null);
                // 提交修改
                editor.commit();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


}



