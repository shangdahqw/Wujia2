package com.example.wujia2.photo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.wujia2.LoginActivity;
import com.example.wujia2.R;
import com.example.wujia2.pojo.Circle;
import com.example.wujia2.pojo.CircleUserVo;
import com.example.wujia2.pojo.User;
import com.example.wujia2.utils.DateConverter;
import com.example.wujia2.utils.HttpUtil;
import com.example.wujia2.utils.JsonUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.ninegrid.ImageInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Response;
import static com.example.wujia2.MyApplication.SERVER_MICRO_URL;
public class MainActivity extends AppCompatActivity implements View.OnClickListener, GradScrollView.ScrollViewListener {

    private ImageView backGroundImg;
    private GradScrollView scrollView;
    private RelativeLayout spaceTopChange;
    private int height;
    private MyAdapter adapter;
    private List<Post> list;
    private MyListview lv;
    private SwipeRefreshLayout refresh;
    private AlertDialog al;
    private com.makeramen.roundedimageview.RoundedImageView userIcon;
    private ArrayList<ImageItem> imageItems;
    private String head_url_res = "";//获得后的头像url

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_main);
        setNavigation();
        intiView();
        new Thread(new NetThreadUser()).start();
        intiData(0);
        initListeners();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //转到详情页
                intentToComm(i);
            }
        });
    }

    /**
     * 初始化控件
     */
    private void intiView() {

        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        lv = (MyListview) findViewById(R.id.lv);
        userIcon = findViewById(R.id.userIcon);
        backGroundImg = (ImageView) findViewById(R.id.headBkg);
        backGroundImg.setFocusable(true);
        backGroundImg.setFocusableInTouchMode(true);
        backGroundImg.requestFocus();
        scrollView = (GradScrollView) findViewById(R.id.scrollview);
        spaceTopChange = (RelativeLayout) findViewById(R.id.spaceTopChange);
        list = new ArrayList<>();
        adapter = new MyAdapter(MainActivity.this, list);
        lv.setAdapter(adapter);
        //点击头像的处理
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewUser();
            }
        });
    }

    /**
     * 查询数据
     */
    private void intiData(int tag) {
        if (tag == 0) {
            showDialog();
        }
        list.clear();
        new Thread(new NetThread()).start();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                if (head_url_res.equals("")) {
                    toast("等待数据初始化中...");
                    return;
                }
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("headUrl", head_url_res);
                startActivity(intent);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.userIcon:
                viewUser();
                break;

        }
    }

    /**
     * 获取顶部图片高度后，设置滚动监听
     */
    private void initListeners() {

        ViewTreeObserver vto = backGroundImg.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                spaceTopChange.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
                height = backGroundImg.getHeight();

                scrollView.setScrollViewListener(MainActivity.this);
            }
        });
    }

    /**
     * 滑动监听
     * 根据滑动的距离动态改变标题栏颜色
     */
    @Override
    public void onScrollChanged(GradScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y <= 0) {   //设置标题的背景颜色
            spaceTopChange.setBackgroundColor(Color.argb(0, 144, 151, 166));
        } else if (y > 0 && y <= height - 10) { //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变
            float scale = (float) y / height;
            float alpha = (255 * scale);
            spaceTopChange.setBackgroundColor(Color.argb((int) alpha, 130, 117, 140));
        } else {    //滑动到banner下面设置普通颜色
            spaceTopChange.setBackgroundColor(Color.parseColor("#584f60"));
        }
    }

    /*
      传递数据到详情页
     */
    public void intentToComm(int i) {
        //传递数据到评论页面
        Intent intent = new Intent(MainActivity.this, CommentActivity.class);
        intent.putExtra("username", list.get(i).getUserName());
        intent.putExtra("content", list.get(i).getContent());
        intent.putExtra("time", list.get(i).getTime());
        intent.putExtra("head", list.get(i).getUserIcon());
        intent.putExtra("circle_id", list.get(i).getId());
        intent.putExtra("user_id", list.get(i).getUserId());
        Boolean isHaven = list.get(i).isHaveIcon();
        if (isHaven) {
            intent.putExtra("isHaven", "true");
        } else {
            intent.putExtra("isHaven", "false");
        }
        String good = list.get(i).getPraise().toString();
        intent.putExtra("goods", good);

        //如果帖子没有图片就做处理 传入空
        if (list.get(i).getHeadImgUrl() != null) {
            intent.putStringArrayListExtra("infoList", (ArrayList<String>) list.get(i).getHeadImgUrl());
        } else {
            intent.putStringArrayListExtra("infoList", null);
        }
        startActivity(intent);

    }

    public void showDialog() {
        LayoutInflater inflater = getLayoutInflater();

        al = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setView(R.layout.photo_dialog)
                .show();
    }

    private void viewUser() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageInfo> imageInfo = new ArrayList<>();
                imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            } else {
                toast("没有选择图片");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        intiData(1);
    }

    //Toast
    private void toast(String date) {
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
    }




    //uiHandler在主线程中创建，所以自动绑定主线程
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    List<Post> circlelist = (List<Post>) msg.obj;
                    list = circlelist;
                    adapter.addPost(list);
                    adapter.notifyDataSetChanged();
                    al.dismiss();
                    break;
                case 2:
                    head_url_res = user.getImageUrl();
                    Glide.with(MainActivity.this).load(user.getImageUrl()).into(userIcon);
                    break;
            }
        }
    };


    private class NetThread implements Runnable {

        @Override
        public void run() {
            {

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
                    response = HttpUtil.requestGetBySyn(SERVER_MICRO_URL + "api/item/circle/list", token);
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                if (response != null && response.code() == 403) {
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "无权访问 未登陆！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Looper.loop();

                }

                List<CircleUserVo> circleUserlist = null;

                if (response != null && response.code() == 200) {
                    // 存入数据
                    try {
                        String circleStr = response.body().string();
                        circleUserlist = JsonUtils.parseList(circleStr, CircleUserVo.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                List<Post> lists = new ArrayList<>();
                for (CircleUserVo circleUserVo : circleUserlist) {

                    List<String> listimg = JsonUtils.parseList(circleUserVo.getCircle().getImages(), String.class);
                    Post post = new Post();
                    post.setUserId(circleUserVo.getCircle().getUserId());
                    post.setId(circleUserVo.getCircle().getId());
                    post.setUserIcon(circleUserVo.getUser().getImageUrl());
                    post.setContent(circleUserVo.getCircle().getContent());
                    post.setUserName(circleUserVo.getUser().getUsername());
                    post.setHaveIcon(true);
                    post.setTime(DateConverter.dateTimeToStr(circleUserVo.getCircle().getCreated()));
                    post.setHeadImgUrl(listimg);
                    post.setPraise(3);
                    lists.add(post);

                }
                Message msg = new Message();
                msg.what = 1;
                msg.obj = lists;
                uiHandler.sendMessage(msg);

            }

        }
    }

    private class NetThreadUser implements Runnable {

        @Override
        public void run() {
            {

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
                    response = HttpUtil.requestGetBySyn(SERVER_MICRO_URL + "api/user", token);
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
                    // 存入数据
                    try {
                        String userStr = response.body().string();
                        user = JsonUtils.parse(userStr, User.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }

                }

                Message msg = new Message();
                msg.what = 2;
                msg.obj = user;
                uiHandler.sendMessage(msg);
            }
        }
    }

    private void setNavigation() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//隐藏状态栏但不隐藏状态栏字体
                //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏，并且不显示字体
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//实现状态栏文字颜色为白色

            }

    }

}
