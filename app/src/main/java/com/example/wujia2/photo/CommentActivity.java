package com.example.wujia2.photo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wujia2.LoginActivity;
import com.example.wujia2.R;
import com.example.wujia2.pojo.Reply;
import com.example.wujia2.pojo.ReplyUserVo;
import com.example.wujia2.utils.DateConverter;
import com.example.wujia2.utils.HttpUtil;
import com.example.wujia2.utils.JsonUtils;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

import static com.example.wujia2.MyApplication.SERVER_MICRO_URL;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    private float mFirstY;
    private float mCurrentY;
    private int direction;
    private int mTouchSlop;
    private ArrayList<Comment> list = new ArrayList();
    private CommentAdapter adapter;
    private MyListview listView;
    RoundedImageView head;
    private Post post = new Post();
    TextView tv_name, tv_time, tv_content,num_reply,num_likes;
    private NineGridView nineGridView;
    private Button btn_reply;
    private EditText et_reply;
    private AlertDialog al;
    private ArrayList<String> picList = new ArrayList<>();
    private LinearLayout ly_opte, area_commit;
    private ImageView im_reply, back_deal;//返回
    private Boolean isHaven;//是否存在图片
    private String auhthor_url;//帖子作者id
    private Long circle_id;
    private Long user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_comment);
        setSoftInput();
        setNavigation();
        init();
        initListener();
    }

    /*
    初始化数据
     */
    void init() {

        listView = findViewById(R.id.mylv);
        tv_name = findViewById(R.id.tv_comment_username);
        tv_time = findViewById(R.id.tv_comment_time);
        tv_content = findViewById(R.id.tv_comment_content);
        head = findViewById(R.id.comment_friend_icon);
        btn_reply = findViewById(R.id.btn_comm);
        et_reply = findViewById(R.id.et_reply);
        nineGridView = findViewById(R.id.comm_nine);
        area_commit = findViewById(R.id.area_commit);
        back_deal = findViewById(R.id.back_deal);
        im_reply = findViewById(R.id.comm_repy);
        ly_opte = findViewById(R.id.ly_opte);
        num_reply = findViewById(R.id.num_reply2);
        num_likes = findViewById(R.id.num_likes2);


        tv_name.setText(getIntent().getStringExtra("username"));
        tv_time.setText(getIntent().getStringExtra("time"));
        tv_content.setText(getIntent().getStringExtra("content"));
        String headurl = getIntent().getStringExtra("head");
        circle_id = getIntent().getLongExtra("circle_id", 1);
        user_id = getIntent().getLongExtra("user_id", 1);
        num_reply.setText(getIntent().getStringExtra("num_reply"));
        num_likes.setText(getIntent().getStringExtra("num_likes"));



        if (getIntent().getStringExtra("isHaven").equals("true")) {
            isHaven = true;
        }
        post.setId(circle_id);
        Glide.with(CommentActivity.this).load(headurl).into(head);

        if (getIntent().getStringArrayListExtra("infoList") == null) {
            nineGridView.setVisibility(View.GONE);
        } else {
            picList = getIntent().getStringArrayListExtra("infoList");
            initPics(picList);
        }
        findComments();


    }

    void initListener() {

        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //评论
                String content = et_reply.getText().toString();
                publishComment(content);
            }
        });

        im_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reply2();
            }
        });
        back_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentActivity.this.finish();//关闭详情页
            }
        });
    }

    private void toast(String date) {
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
    }


    private void publishComment(String content) {
        if (TextUtils.isEmpty(content)) {
            toast("发表评论不能为空");
            return;
        }


        new Thread() {
            @Override
            public void run() {
                Response response = null;


                Reply reply = new Reply();
                reply.setCircleId(circle_id);
                reply.setContent(et_reply.getText().toString());
                reply.setUserTo(user_id);
                String requestPara = JsonUtils.serialize(reply);

                SharedPreferences preferences;
                preferences = getSharedPreferences("user", MODE_PRIVATE);
                // 读取SharedPreferences里的token数据
                String token = preferences.getString("token", "");

                if (token.equals("") || token == null) {
                    Intent intent = new Intent(CommentActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                try {
                    response = HttpUtil.requestPutBySyn(SERVER_MICRO_URL + "api/item/reply", requestPara, token);
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(CommentActivity.this, "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                if (response != null && (response.code() == 406 || response.code() == 403)) {
                    Looper.prepare();
                    Toast.makeText(CommentActivity.this, "无权访问 未登陆！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CommentActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Looper.loop();

                }
                if (response != null && response.code() == 201) {

                    Message msg = new Message();
                    msg.what = 2;
                    uiHandler.sendMessage(msg);
                }

            }
        }.start();


    }

    /*
    查询评论
     */
    private void findComments() {
        showDialog();

        list.clear();

        new Thread() {

            @Override
            public void run() {

                SharedPreferences preferences;
                preferences = getSharedPreferences("user", MODE_PRIVATE);
                // 读取SharedPreferences里的token数据
                String token = preferences.getString("token", "");

                if (token.equals("") || token == null) {
                    Intent intent = new Intent(CommentActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                Response response = null;
                try {
                    response = HttpUtil.requestGetBySyn(SERVER_MICRO_URL + "api/item/reply/list?circleId=" + circle_id, token);
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(CommentActivity.this, "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                if (response != null && response.code() == 403) {
                    Looper.prepare();
                    Toast.makeText(CommentActivity.this, "无权访问 未登陆！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CommentActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Looper.loop();

                }
                if(response !=null && response.code() ==400){
                    al.dismiss();
                    return;
                }
                if (response != null && response.code() == 200) {
                    // 存入数据
                    List<ReplyUserVo> replyUserVolist = null;
                    try {
                        String rpStr = response.body().string();
                        replyUserVolist = JsonUtils.parseList(rpStr, ReplyUserVo.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ArrayList<Comment> lists = new ArrayList();

                    for (ReplyUserVo replyUserVo : replyUserVolist) {
                        Comment comment = new Comment();
                        comment.setCircleId(replyUserVo.getReply().getCircleId());
                        comment.setContent(replyUserVo.getReply().getContent());
                        comment.setCreateTime(DateConverter.dateTimeToStr(replyUserVo.getReply().getCreated()));
                        comment.setId(replyUserVo.getReply().getId());
                        comment.setUserHead(replyUserVo.getUser().getImageUrl());
                        comment.setName(replyUserVo.getUser().getUsername());
                        lists.add(comment);

                    }
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = lists;
                    uiHandler.sendMessage(msg);
                }

            }
        }.start();


    }


    /*
    加载帖子图片集合
     */
    public void initPics(List<String> picList) {

        if (picList.size() > 0) {//判断是否有图片
            ArrayList<ImageInfo> imageInfo = new ArrayList<>();
            for (int j = 0; j < picList.size(); j++) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(picList.get(j));
                info.setBigImageUrl(picList.get(j));
                imageInfo.add(info);
            }
            nineGridView.setAdapter(new NineGridViewClickAdapter(CommentActivity.this, imageInfo));
        } else {
            nineGridView.setVisibility(View.GONE);
        }
    }

    /*
    弹出输入框
     */
    public void reply2() {
        et_reply.requestFocus();
        InputMethodManager imm = (InputMethodManager) et_reply.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }
    /*
设置输入框
 */
    private void setSoftInput() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


    }



    /*
    点赞
     */
    public void updates() {

    }


    /*
    加载框
     */
    private void showDialog() {
        // 首先得到整个View
        LayoutInflater inflater = getLayoutInflater();
        al = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("数据装载中...")
                .setView(R.layout.photo_dialog_com)
                .setCancelable(true)
                .show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_reply:
                reply2();
                break;
        }
    }


    //uiHandler在主线程中创建，所以自动绑定主线程
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    list = (ArrayList<Comment>) msg.obj;
                    al.dismiss();
                    adapter = new CommentAdapter(list, CommentActivity.this);
                    adapter.notifyDataSetInvalidated();
                    listView.setAdapter(adapter);
                    break;
                case 2:
                    findComments();
                    toast("评论成功");
                    adapter.notifyDataSetInvalidated();
                    et_reply.setText("");
                    break;
            }
        }
    };

    private void setNavigation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//隐藏状态栏但不隐藏状态栏字体
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏，并且不显示字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏文字颜色为黑色

        }

    }

}