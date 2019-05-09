package com.example.wujia2.group;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.example.wujia2.HomeActivity;
import com.example.wujia2.LoginActivity;
import com.example.wujia2.R;
import com.example.wujia2.adapter.GroupAdapter;
import com.example.wujia2.myindex.SettingActivity;
import com.example.wujia2.pojo.Group;
import com.example.wujia2.utils.HttpUtil;
import com.example.wujia2.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.wujia2.MyApplication.SERVER_MICRO_URL;

public class GroupFragment extends Fragment {


  private GroupAdapter adapter;
  private ListView listView;
  List<Group> grouplist =new ArrayList<>();

  @Nullable
  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_group, container, false);

    listView = (ListView) view.findViewById(R.id.mylist);

    new Thread(new NetThread()).start();




    SuperTextView tV_id_search = (SuperTextView) view.findViewById(R.id.id_search);


    tV_id_search.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
      @Override
      public void onClickListener(SuperTextView superTextView) {

        Intent intent = new Intent(getActivity(),GroupSearchActivity.class);
        startActivity(intent);
      }
    });


    return view;
  }


  //uiHandler在主线程中创建，所以自动绑定主线程
  private Handler uiHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case 1:
          grouplist = (List<Group>) msg.obj;
          adapter=new GroupAdapter(grouplist,getActivity());
          listView.setAdapter(adapter);
          break;
      }
    }
  };


  private class NetThread implements Runnable {

    @Override
    public void run() {

      SharedPreferences preferences;
      preferences = getActivity().getSharedPreferences("user", MODE_PRIVATE);
      // 读取SharedPreferences里的token数据
      String token = preferences.getString("token", "");

      if (token.equals("") || token == null) {
        Intent intent = new Intent((HomeActivity) getActivity(), LoginActivity.class);
        startActivity(intent);
      }

      Response response = null;
      try {
        response = HttpUtil.requestGetBySyn(SERVER_MICRO_URL+"api/group/list", token);
      } catch (IOException e) {
        Looper.prepare();
        Toast.makeText(getActivity(), "内部错误，请稍后再试！", Toast.LENGTH_SHORT).show();
        Looper.loop();
      }

      if (response != null && response.code() == 406) {
        Looper.prepare();
        Toast.makeText((HomeActivity) getActivity(), "无权访问 未登陆！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        Looper.loop();

      }
      if (response != null && response.code() == 200) {
        // 存入数据
        List<Group> grouplist =null;
        try {
          String groupStr = response.body().string();
          grouplist = JsonUtils.parseList(groupStr, Group.class);
        } catch (IOException e) {
          e.printStackTrace();
        }
        Message msg = new Message();
        msg.what = 1;
        msg.obj = grouplist;
        uiHandler.sendMessage(msg);

      }

    }
  }

}
