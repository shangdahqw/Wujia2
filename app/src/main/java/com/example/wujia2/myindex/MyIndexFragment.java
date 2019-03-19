package com.example.wujia2.myindex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.example.wujia2.LoginActivity;
import com.example.wujia2.R;
import com.example.wujia2.pojo.User;
import com.example.wujia2.utils.DateConverter;
import com.example.wujia2.utils.HttpUtil;
import com.example.wujia2.utils.JsonUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.wujia2.MyApplication.SERVER_IMAGE_URL;
import static com.example.wujia2.MyApplication.SERVER_MICRO_URL;

public class MyIndexFragment extends Fragment {


    private SuperTextView tV_head, tV_username, tV_iphone, tV_birthday;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myindex, container, false);

        tV_head = (SuperTextView) view.findViewById(R.id.head);
        tV_username = (SuperTextView) view.findViewById(R.id.username);
        tV_iphone = (SuperTextView) view.findViewById(R.id.iphone);
        tV_birthday = (SuperTextView) view.findViewById(R.id.birthday);
        new Thread(new NetThread()).start();

        return view;

    }


    //uiHandler在主线程中创建，所以自动绑定主线程
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    User user = (User) msg.obj;
                    tV_head.setLeftTopString(user.getUsername());
                    tV_head.setLeftBottomString(user.getId().toString());
                    tV_iphone.setRightString(user.getPhone());
                    tV_username.setRightString(user.getUsername());
                    tV_birthday.setRightString(DateConverter.dateToStr(user.getBirthday()));
                    Picasso.with(getActivity())
                            .load(SERVER_IMAGE_URL+"group1/M00/00/00/wKgBklyKNWSAFbMbAABpDDWjHHg08.jpeg")
                            .placeholder(R.mipmap.image_head)
                            .into(tV_head.getLeftIconIV());
                    break;
            }
        }
    };


    private class NetThread implements Runnable {

        @Override
        public void run() {

            {

                SharedPreferences preferences;
                preferences = getActivity().getSharedPreferences("user", MODE_PRIVATE);
                // 读取SharedPreferences里的token数据
                String token = preferences.getString("token", "");

                if (token.equals("") || token == null) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }

                Response response = null;
                try {
                    response = HttpUtil.requestGetBySyn(SERVER_MICRO_URL + "api/user", token);
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                if (response != null && response.code() == 406) {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "无权访问 未登陆！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    Looper.loop();

                }
                if (response != null && response.code() == 200) {
                    // 存入数据
                    User user = null;
                    try {
                        String userStr = response.body().string();
                        user = JsonUtils.parse(userStr, User.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = user;
                    uiHandler.sendMessage(msg);

                }

            }

        }


    }
}
