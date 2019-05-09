package com.example.wujia2.photo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.example.wujia2.LoginActivity;
import com.example.wujia2.R;
import com.example.wujia2.pojo.Circle;
import com.example.wujia2.utils.HttpUtil;
import com.example.wujia2.utils.JsonUtils;
import com.example.wujia2.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

import static com.example.wujia2.MyApplication.SERVER_MICRO_URL;
import static com.example.wujia2.MyApplication.SERVER_UPLOAD_URL;

public class EditCameraActivity extends AppCompatActivity {
    private EditText et_content;
    private TextView tv_send, tv_cancle;
    private String content;
    ProgressDialog dialog = null;//进度条
    private List<String> imagesUrl = new ArrayList<>();
    private String videoLocalPath;
    private FrameLayout frameLayout;
    private ImageView pic_iv, start_iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigation();
        setContentView(R.layout.video_edit);
        intiView();
    }

    private void intiView() {
        videoLocalPath = getIntent().getStringExtra("videoLocalPath");
        et_content = (EditText) findViewById(R.id.et_content2);
        tv_send = (Button) findViewById(R.id.tv_send2);
        tv_cancle = findViewById(R.id.tv_cancle2);
        pic_iv = (ImageView)findViewById(R.id.pic_iiv2);
        start_iv = (ImageView) findViewById(R.id.start_iv2);
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout2);
        frameLayout.setVisibility(View.VISIBLE);

        Bitmap bitmap = Utils.getLocalVideoBitmap(videoLocalPath);
        pic_iv.setImageBitmap(bitmap);

        findViewById(R.id.tv_send2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = et_content.getText().toString();

                if (content.length() < 1) {
                    toast("发表不能为空");
                } else {
                    tv_send.setEnabled(false);
                    tv_upload_database();
                }
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditCameraActivity.this, MainActivity.class);
                startActivity(intent);
                EditCameraActivity.this.finish();
            }
        });



        SuperTextView tV_choose_group = (SuperTextView)findViewById(R.id.choose_group_video);


        tV_choose_group.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {

                Intent intent = new Intent(EditCameraActivity.this, GroupChooseActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 上传图片
     */
    private void tv_upload_database() {
        //隐藏软硬盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);


        dialog = new ProgressDialog(EditCameraActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("上传视频中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        new Thread() {
            @Override
            public void run() {
                Response response = null;

                String fileName = videoLocalPath.substring(videoLocalPath.lastIndexOf("/") + 1);
                try {
                    response = HttpUtil.requestPostBySynWithFormData(SERVER_UPLOAD_URL + "upload/image", videoLocalPath, fileName);
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(EditCameraActivity.this, "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    Looper.loop();

                }
                if (response != null && response.code() != 200) {
                    // 存入数据
                    System.out.println(response);
                    Looper.prepare();
                    Toast.makeText(EditCameraActivity.this, "上传文件失败，请稍后再试！", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    Looper.loop();
                }
                if (response != null && response.code() == 200) {
                    // 存入数据
                    try {
                        String url = response.body().string();
                        imagesUrl.add(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                        dialog.dismiss();

                    }
                }

                dialog.dismiss();

                Circle circle = new Circle();
                circle.setImages(JsonUtils.serialize(imagesUrl));
                circle.setContent(content);
                circle.setGroupIds("[5000000,5000001]");
                String requestPara = JsonUtils.serialize(circle);
                SharedPreferences preferences;
                preferences = getSharedPreferences("user", MODE_PRIVATE);
                // 读取SharedPreferences里的token数据
                String token = preferences.getString("token", "");

                if (token.equals("") || token == null) {
                    Intent intent = new Intent(EditCameraActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                try {
                    response = HttpUtil.requestPutBySyn(SERVER_MICRO_URL + "api/item/circle", requestPara, token);
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(EditCameraActivity.this, "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                if (response != null && (response.code() == 406 || response.code() == 403)) {
                    Looper.prepare();
                    Toast.makeText(EditCameraActivity.this, "无权访问 未登陆！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditCameraActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Looper.loop();

                }
                if (response != null && response.code() == 201) {
                    et_content.setText("");
                    Looper.prepare();
                    toast("发表成功");
                    Intent intent = new Intent(EditCameraActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Looper.loop();
                }

            }
        }.start();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    //Toast
    private void toast(String date) {
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
    }

    private void setNavigation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white)); // 设置状态栏颜色
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
