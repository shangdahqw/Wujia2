package com.example.wujia2.photo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.wujia2.LoginActivity;
import com.example.wujia2.R;
import com.example.wujia2.pojo.Circle;
import com.example.wujia2.pojo.Reply;
import com.example.wujia2.pojo.User;
import com.example.wujia2.utils.DateConverter;
import com.example.wujia2.utils.HttpUtil;
import com.example.wujia2.utils.JsonUtils;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Response;


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
    TextView tv_name, tv_time, tv_content, tv_good;
    private NineGridView nineGridView;
    private Button btn_reply;
    private EditText repy_content, ed_comm;
    private AlertDialog al;
    private ArrayList<String> picList = new ArrayList<>();
    private LinearLayout ly_opte, area_commit;
    private ImageView et_reply, back_deal, comm_share, comm_del;//返回
    private Boolean isHaven;//是否存在图片
    private String auhthor_url;//帖子作者id
    private User user;
    private Long circle_id;
    private Long user_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_comment);
        init();
        initListener();
        getUrl();
    }

    /*
    初始化数据
     */
    void init() {

        listView = findViewById(R.id.mylv);
        tv_name = findViewById(R.id.tv_comment_username);
        tv_time = findViewById(R.id.tv_comment_time);
        tv_content = findViewById(R.id.tv_comment_content);
        tv_good = findViewById(R.id.item_good_comment);
        head = findViewById(R.id.comment_friend_icon);
        btn_reply = findViewById(R.id.btn_comm);
        repy_content = findViewById(R.id.ed_comm);
        nineGridView = findViewById(R.id.comm_nine);
        area_commit = findViewById(R.id.area_commit);
        back_deal = findViewById(R.id.back_deal);
        et_reply = findViewById(R.id.comm_repy);
        ly_opte = findViewById(R.id.ly_opte);
        comm_del = findViewById(R.id.comm_del);
        tv_name.setText(getIntent().getStringExtra("username"));
        tv_time.setText(getIntent().getStringExtra("time"));
        tv_content.setText(getIntent().getStringExtra("content"));
        String headurl = getIntent().getStringExtra("head");
        tv_good.setText(getIntent().getStringExtra("goods"));
        circle_id = getIntent().getLongExtra("circle_id", 1);
        user_id = getIntent().getLongExtra("user_id", 1);

        if (getIntent().getStringExtra("isHaven").equals("true")) {
            isHaven = true;
        }
        post.setId(circle_id);
        Glide.with(CommentActivity.this).load(headurl).into(head);
        user = new User();  //todo

        if (getIntent().getStringArrayListExtra("infoList") == null) {
            nineGridView.setVisibility(View.GONE);
        } else {
            picList = getIntent().getStringArrayListExtra("infoList");
            initPics(picList);
        }

        findComments();


    }

    void initListener() {
        et_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reply2();
            }
        });
        tv_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updates();//点赞
            }
        });
        back_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentActivity.this.finish();//关闭详情页
            }
        });

        comm_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除帖子
                del();
            }
        });
    }

    private void toast(String date) {
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
    }


    private void publishComment(String content) {

        if (user == null) {
            toast("发表评论前请先登陆");
            return;
        } else if (TextUtils.isEmpty(content)) {
            toast("发表评论不能为空");
            return;
        }
        showDialog_com();




        new Thread() {
            @Override
            public void run() {
                Response response = null;


                Reply reply =new Reply();
                reply.setCircleId(circle_id);
                reply.setContent(repy_content.getText().toString());
                reply.setUserTo(user_id);
                String requestPara =JsonUtils.serialize(reply);

                SharedPreferences preferences;
                preferences = getSharedPreferences("user", MODE_PRIVATE);
                // 读取SharedPreferences里的token数据
                String token = preferences.getString("token", "");

                if (token.equals("") || token == null) {
                    Intent intent = new Intent(CommentActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                try {
                    response = HttpUtil.requestPutBySyn("http://192.168.1.136:10010/api/item/reply", requestPara,token);
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(CommentActivity.this, "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                if (response != null && (response.code() == 406||response.code() == 403)) {
                    Looper.prepare();
                    Toast.makeText( CommentActivity.this, "无权访问 未登陆！", Toast.LENGTH_SHORT).show();
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
                    response = HttpUtil.requestGetBySyn("http://192.168.1.136:10010/api/item/reply/list?circleId=" + circle_id, token);
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
                if (response != null && response.code() == 200) {
                    // 存入数据
                    List<Reply> relist = null;
                    try {
                        String rpStr = response.body().string();
                        relist = JsonUtils.parseList(rpStr, Reply.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ArrayList<Comment> lists = new ArrayList();

                    for (Reply reply : relist) {
                        Comment comment = new Comment();
                        comment.setCircleId(reply.getCircleId());
                        comment.setContent(reply.getContent());
                        comment.setCreateTime(DateConverter.dateToStr(reply.getCreated()));
                        comment.setId(reply.getId());
                        comment.setUserHead("http://192.168.1.146/group1/M00/00/00/wKgBklyKNWSAFbMbAABpDDWjHHg08.jpeg");
                        comment.setName(reply.getUserId().toString());
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
         获取时间
     */
    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 hh点");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    private void showDialog_com() {
        LayoutInflater inflater = getLayoutInflater();
        al = new AlertDialog.Builder(this)
                .setTitle("回复评论中...")
                .setView(R.layout.photo_dialog_com)
                .show();

    }

    /*
    隐藏输入框
     */
    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(ed_comm.getWindowToken(), 0);
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
        repy_content.requestFocus();
        InputMethodManager imm = (InputMethodManager) repy_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    /*
    删除帖子
     */
    private void del() {
//        Post p = new Post();
//        p.setObjectId(obj);
//        if (this.user.getObjectId().equals(auhthor_url)){
//            p.delete(new UpdateListener() {
//                @Override
//                public void done(BmobException e) {
//                    if(e==null){
//                        toast("删除成功");
//                        CommentActivity.this.finish();
//                    }else{
//                        toast("失败："+e.getMessage()+","+e.getErrorCode());
//                    }
//                }
//            });
//        }else {
//            toast("您无权限删除别人发的帖子哦");
//        }


    }


    /*
    获取帖子作者信息objid
     */
    public void getUrl() {
//        final String[] obj_info = {""};
//        final BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
//        query.addWhereEqualTo("username", tv_name.getText().toString());
//        query.findObjects(new FindListener<BmobUser>() {
//            @Override
//            public void done(List<BmobUser> list, BmobException e) {
//                al.dismiss();
//                for (BmobUser data : list) {
//                    obj_info[0] = data.getObjectId();
//                    auhthor_url = obj_info[0];
//                }
//            }
//
//        });
    }

    /*
    点赞
     */
    public void updates() {
//        Post post = new Post();
//        post.setObjectId(obj);
//        // TODO Auto-generated method stub
//        post.increment("praise");
//        //不知道什么原因点赞后图片会显消失，所以标记一下
//        post.setHaveIcon(isHaven);
//        post.update(new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                if (e == null) {
//                    toast("点赞成功！");
//
//                } else {
//                    toast("点赞失败！");
//                }
//            }
//        });

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
            case R.id.ed_comm:
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
                    btn_reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //评论
                            String content = repy_content.getText().toString();
                            publishComment(content);
                        }
                    });
                    break;
                case 2:
                    findComments();
                    toast("评论成功");
                    adapter.notifyDataSetInvalidated();
                    repy_content.setText("");
                    break;
            }
        }
    };

}