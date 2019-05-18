package com.example.wujia2.photo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.example.wujia2.LoginActivity;
import com.example.wujia2.R;
import com.example.wujia2.pojo.Circle;
import com.example.wujia2.pojo.User;
import com.example.wujia2.utils.HttpUtil;
import com.example.wujia2.utils.JsonUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.ninegrid.ImageInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Response;
import static com.example.wujia2.MyApplication.SERVER_MICRO_URL;
import static com.example.wujia2.MyApplication.SERVER_UPLOAD_URL;

public class EditActivity extends AppCompatActivity {
    private EditText et_send;
    private GridView publishGridView;
    private GridAdapter gridAdapter;
    private TextView tv_upload, tv_cancle;
    private int size = 0;
    private String content;
    private ArrayList<ImageItem> imageItems;
    ProgressDialog dialog = null;//进度条
    private List<String> imagesUrl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigation();
        setContentView(R.layout.photo_edit);
        intiView();
    }

    private void intiView() {
        et_send = (EditText) findViewById(R.id.et_content);
        tv_upload = (Button) findViewById(R.id.tv_send);
        tv_cancle = findViewById(R.id.tv_cancle);
        publishGridView = (GridView) findViewById(R.id.publishGridView);
        gridAdapter = new GridAdapter();
        publishGridView.setAdapter(gridAdapter);

        findViewById(R.id.tv_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = et_send.getText().toString();

                if (content.length() < 1 && size == 0) {
                    toast("发表不能为空");
                } else {
                    tv_upload.setEnabled(false);
                    tv_upload_database();
                }
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditActivity.this.finish();
            }
        });


        SuperTextView tV_choose_group = (SuperTextView)findViewById(R.id.choose_group);


        tV_choose_group.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {

                Intent intent = new Intent(EditActivity.this, GroupChooseActivity.class);
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

        size = 0;
        final String[] filePaths = new String[imageItems.size()];
        for (int i = 0; i < imageItems.size(); i++) {
            filePaths[i] = imageItems.get(i).path;
        }
        dialog = new ProgressDialog(EditActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("上传图片中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        new Thread() {
            @Override
            public void run() {
                Response response = null;
                for (int j = 0; j < filePaths.length; j++) {

                    String fileName = filePaths[j].substring(filePaths[j].lastIndexOf("/") + 1);
                    try {
                        response = HttpUtil.requestPostBySynWithFormData(SERVER_UPLOAD_URL+"upload/image", filePaths[j], fileName);
                    } catch (IOException e) {
                        Looper.prepare();
                        Toast.makeText(EditActivity.this, "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Looper.loop();
                        break;

                    }
                    if (response != null && response.code() != 200) {
                        // 存入数据
                        System.out.println(response);
                        Looper.prepare();
                        Toast.makeText(EditActivity.this, "上传文件失败，请稍后再试！", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Looper.loop();
                        break;
                    }
                    if (response != null && response.code() == 200) {
                        // 存入数据
                        try {
                            String url = response.body().string();
                            imagesUrl.add(url);
                            dialog.setProgress((int)(j/filePaths.length*100));
                        } catch (IOException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                            break;

                        }
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
                    Intent intent = new Intent(EditActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                try {
                    response = HttpUtil.requestPutBySyn(SERVER_MICRO_URL+"api/item/circle", requestPara, token);
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(EditActivity.this, "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                if (response != null && (response.code() == 406 || response.code() == 403)) {
                    Looper.prepare();
                    Toast.makeText(EditActivity.this, "无权访问 未登陆！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Looper.loop();

                }
                if (response != null && response.code() == 201) {
                    et_send.setText("");
                    Looper.prepare();
                    toast("发表成功");
                    finish();
                    Looper.loop();
                }

            }
        }.start();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageInfo> imageInfo = new ArrayList<>();
                imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                gridAdapter.notifyDataSetChanged();
                size = imageItems.size();
            } else {
                toast("没有选择图片");
            }
        }
    }

    private class GridAdapter extends BaseAdapter {
        public GridAdapter() {
        }

        @Override
        public int getCount() {
            if (imageItems == null)
                return 1;
            else
                return imageItems.size() + 1;
        }

        @Override
        public Object getItem(int i) {
            return imageItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(EditActivity.this).inflate(R.layout.photo_grid_layout, null);
                holder.image_voice = (ImageView) view.findViewById(R.id.gird_img);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (imageItems == null) {
                holder.image_voice.setImageResource(R.mipmap.add_icon);
            } else {
                if (i == imageItems.size()) {
                    holder.image_voice.setImageResource(R.mipmap.add_icon);
                } else {
                    File file = new File(imageItems.get(i).path);
                    if (file.exists()) {
                        Bitmap bm = BitmapFactory.decodeFile(imageItems.get(i).path);
                        holder.image_voice.setImageBitmap(CircleTransform.centerSquareScaleBitmap(bm, 100));
                    }
                }
            }
            holder.image_voice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((imageItems != null && i == imageItems.size()) || imageItems == null) {
                        addImage();
                    }
                }
            });
            return view;
        }

        class ViewHolder {
            private ImageView image_voice;
        }
    }

    /**
     * 添加图片哦
     */
    private void addImage() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoader());
        imagePicker.setMultiMode(true);   //多选
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setSelectLimit(18);    //最多选择X张
        imagePicker.setCrop(false);       //不进行裁剪
        Intent intent = new Intent(EditActivity.this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
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
