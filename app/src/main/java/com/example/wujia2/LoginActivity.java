package com.example.wujia2;

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

import com.example.wujia2.utils.HttpUtil;

import java.io.IOException;

import okhttp3.Response;

import static com.example.wujia2.MyApplication.SERVER_MICRO_URL;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login;
    private EditText ed_text_phone;
    private EditText ed_text_password;
    private Button btn_registerUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_login);

        btn_login = (Button) findViewById(R.id.btn_login);
        ed_text_phone = findViewById(R.id.ed_text_login_username);
        ed_text_password = findViewById(R.id.ed_text_login_password);
        btn_registerUI = (Button) findViewById(R.id.btn_registerUI);

        btn_login.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        new Thread(new NetThread()).start();

                    }
                });

        btn_registerUI.setOnClickListener(

                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);


                    }
                });
    }


    private class NetThread implements Runnable {

        @Override
        public void run() {

            // 把网络访问的代码放在这里
            Response response =
                    HttpUtil.login(
                            SERVER_MICRO_URL + "api/auth/accredit",
                            ed_text_phone.getText().toString(),
                            ed_text_password.getText().toString());
            if (response.code() == 401) {

                // 解决在子线程中调用Toast的异常情况处理
                Looper.prepare();
                Toast.makeText(LoginActivity.this, "用户名或密码错误！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            if (response.code() == 500) {
                Looper.prepare();
                Toast.makeText(LoginActivity.this, "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            if (response.code() == 200) {
                SharedPreferences preferences;
                preferences = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                // 存入数据
                String token = "";
                try {
                    token = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                editor.putString("token", token);
                // 提交修改
                editor.commit();
            }

            Looper.prepare();
            Toast.makeText(LoginActivity.this, "登入成功！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            Looper.loop();
        }
    }

}



