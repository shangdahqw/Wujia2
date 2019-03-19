package com.example.wujia2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.example.wujia2.utils.HttpUtil;
import java.io.IOException;
import okhttp3.Response;
import static com.example.wujia2.MyApplication.SERVER_MICRO_URL;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new NetThread()) {
        }.start();

    }


    private class NetThread implements Runnable {

        @Override
        public void run() {

            SharedPreferences preferences;
            preferences = getSharedPreferences("user", MODE_PRIVATE);
            // 读取SharedPreferences里的token数据
            String token = preferences.getString("token", "");

            if (token.equals("") || token == null) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            Response response = null;
            try {
                response = HttpUtil.requestGetBySyn(SERVER_MICRO_URL + "api/auth/verify", token);
            } catch (IOException e) {
                Looper.prepare();
                Toast.makeText(MainActivity.this, "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                Looper.loop();

            }
            if (response != null && response.code() == 406) {
                Looper.prepare();
                Toast.makeText(MainActivity.this, "无权访问 未登陆！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                Looper.loop();

            }
            if (response != null && response.code() == 200) {
                SharedPreferences.Editor editor = preferences.edit();
                // 存入数据
                try {
                    editor.putString("token", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 提交修改
                editor.commit();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);

            }


        }


    }
}


