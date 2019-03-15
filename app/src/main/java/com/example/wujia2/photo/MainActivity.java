package com.example.wujia2.photo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.wujia2.HomeActivity;
import com.example.wujia2.LoginActivity;
import com.example.wujia2.R;
import com.example.wujia2.pojo.Circle;
import com.example.wujia2.pojo.Group;
import com.example.wujia2.pojo.User;
import com.example.wujia2.utils.DateConverter;
import com.example.wujia2.utils.HttpUtil;
import com.example.wujia2.utils.JsonUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.ninegrid.ImageInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Response;


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
    private String headUrl = "";
    private ArrayList<ImageItem> imageItems;
    private String head_url_res = "";//获得后的头像url


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_main);
        intiView();
        intiData(0);
        getUserInfo();
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
        //点击头像的处理，我这里是注销与切换用户，我下边写了更换头像的方法，根据自己情况选择
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  quit();
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


        new Thread() {

            @Override
            public void run() {

                SharedPreferences preferences;
                preferences = getSharedPreferences("user", MODE_PRIVATE);
                // 读取SharedPreferences里的token数据
                String token = preferences.getString("token", "");

                if (token.equals("") || token == null) {
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }

                Response response = null;
                try {
                    response = HttpUtil.requestGetBySyn("http://192.168.1.136:10010/api/item/circle/list", token);
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                if (response != null && response.code() == 403 ) {
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "无权访问 未登陆！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Looper.loop();

                }
                if (response != null && response.code() == 200) {
                    // 存入数据
                    List<Circle> circlelist =null;
                    try {
                        String circleStr = response.body().string();
                        circlelist = JsonUtils.parseList(circleStr, Circle.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                    List<Post> lists = new ArrayList<>();
                    for(Circle circle :circlelist){

                        List<String > listimg =JsonUtils.parseList(circle.getImages(),String.class);
                        Post post= new Post();
                        post.setUserId(circle.getUserId());
                        post.setId(circle.getId());
                        post.setUserIcon("http://192.168.1.146/group1/M00/00/00/wKgBklyKNWSAJ0xZAADBIJaJ2FQ46.jpeg");
                        post.setContent(circle.getContent());
                        post.setUserName("忘了爱");
                        post.setHaveIcon(true);
                        post.setTime(DateConverter.dateToStr(circle.getCreated()));
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
        }.start();


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
                //打开相机选择头像
                addHead();

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
public void intentToComm(int i){
    //传递数据到评论页面
    Intent intent = new Intent(MainActivity.this, CommentActivity.class);
    intent.putExtra("username", list.get(i).getUserName());
    intent.putExtra("content", list.get(i).getContent());
    intent.putExtra("time", list.get(i).getTime());
    intent.putExtra("head", list.get(i).getUserIcon());
    Boolean isHaven=list.get(i).isHaveIcon();
    if (isHaven){
        intent.putExtra("isHaven","true");
    }else {
        intent.putExtra("isHaven","false");
    }

    String good = list.get(i).getPraise().toString();
    intent.putExtra("goods", good);

    //如果帖子没有图片就做处理 传入空
    if (list.get(i).getHeadImgUrl() != null) {
        intent.putStringArrayListExtra("infoList", (ArrayList<String>) list.get(i).getHeadImgUrl());
    } else {
        intent.putStringArrayListExtra("infoList", null);

    }
    intent.putExtra("circle_id", list.get(i).getId());
    intent.putExtra("user_id", list.get(i).getUserId());


    startActivity(intent);
}
    public void showDialog() {
        LayoutInflater inflater = getLayoutInflater();

        al = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setView(R.layout.photo_dialog)

                .show();

    }

    /**
     * 添加头像哦 这个自己按情况选择是否使用
     */
    private void addHead() {
//        ImagePicker imagePicker = ImagePicker.getInstance();
//        imagePicker.setImageLoader(new ImageLoader());
//        imagePicker.setMultiMode(false);   //多选
//        imagePicker.setSelectLimit(1);    //最多选择X张
//        imagePicker.setCrop(true);       //不进行裁剪
//        Intent intent = new Intent(MainActivity.this, ImageGridActivity.class);
//        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageInfo> imageInfo = new ArrayList<>();
                imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                changeHead();
            } else {
                toast("没有选择图片");
            }
        }
    }

    /*
    获取图片url然后上传
     */
    public void changeHead() {
//        final String[] filePaths = new String[imageItems.size()];
//        for (int i = 0; i < imageItems.size(); i++) {
//            filePaths[i] = imageItems.get(i).path;
//            headUrl = filePaths[0];
//            final User users = new User();
//            final BmobFile bmobFile = new BmobFile(new File(headUrl));
//            bmobFile.uploadblock(new UploadFileListener() {
//
//                @Override
//                public void done(BmobException e) {
//                    if (e == null) {
//                        users.setHead(bmobFile.getFileUrl());
//                        users.update(user.getObjectId(), new UpdateListener() {
//                            @Override
//                            public void done(BmobException e) {
//                                if (e == null) {
//                                    toast("修改成功");
//
//                                }
//                            }
//                        });
//                    } else {
//                        toast("上传失败：" + e.getMessage());
//                    }
//
//                }
//
//                @Override
//                public void onProgress(Integer value) {
//                    // 返回的上传进度（百分比）
//                }
//            });
//
//
//        }
//
    }

    /*
    获取用户信息
     */
    public void getUserInfo() {
        head_url_res = "http://192.168.1.146/group1/M00/00/00/wKgBklyKNWSAJ0xZAADBIJaJ2FQ46.jpeg";
        Glide.with(MainActivity.this).load(head_url_res).into(userIcon);
    }

    /*
      根据用户名查询id
       */
//    public String queryHeadByName(String usernameq) {
//        final String[] res = {""};
//        BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
//        query.addWhereEqualTo("username", usernameq);
//        query.findObjects(new FindListener<BmobUser>() {
//            @Override
//            public void done(List<BmobUser> object, BmobException e) {
//                if (e == null) {
//                    res[0] = object.get(0).getObjectId();
//
//                } else {
//                    toast("更新用户信息失败:" + e.getMessage());
//                }
//            }
//        });
//
//        return res[0];
//    }

    /*
      根据用户id获取头像
     */
//    public String getHeadUrl(String objId) {
//        final String[] res_head = {""};
//        BmobQuery<User> query = new BmobQuery<User>();
//        query.getObject(objId, new QueryListener<User>() {
//
//            @Override
//            public void done(User object, BmobException e) {
//                if (e == null) {
//                    //获得USER的信息
//                    res_head[0] = object.getHead();
//
//
//                } else {
//
//                }
//            }
//
//        });
//        return res_head[0];
//    }

    @Override
    protected void onResume() {
        super.onResume();
        intiData(1);
    }

    //Toast
    private void toast(String date) {
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
    }
    /*
    注销操作
     */
    public void quit(){
//        al = new AlertDialog.Builder(this)
//                .setTitle("确定注销吗")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        BmobUser.logOut();   //清除缓存用户对象
//                        toast("注销成功");
//                        Intent intent = new Intent();
//                        intent.setClass(MainActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                })
//                .setNegativeButton("取消",null)
//                .show();
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
            }
        }
    };
}
