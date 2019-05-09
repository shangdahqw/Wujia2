package com.example.wujia2.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.allen.library.SuperTextView;
import com.example.wujia2.R;
import com.example.wujia2.group.GroupInfoActivity;
import com.example.wujia2.pojo.Group;
import com.example.wujia2.utils.DateConverter;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import static com.example.wujia2.MyApplication.SERVER_IMAGE_URL;


/**
 * Created by allen on 2016/10/31.
 */

public class GroupAdapter extends BaseAdapter {


    private List<Group> groupList;
    private LayoutInflater inflater;
    private Context context;

    public GroupAdapter() {}

    public GroupAdapter(List<Group> groupList,Context context) {
        this.groupList = groupList;
        this.inflater=LayoutInflater.from(context);
        this.context=context;
    }

    public void addGroup(List<Group> list) {
        this.groupList = list;

    }

    @Override
    public int getCount() {
        return groupList==null?0:groupList.size();

    }

    @Override
    public Object getItem(int position) {
        return groupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view=inflater.inflate(R.layout.item_group,null);
        final Group group=(Group) getItem(position);
        SuperTextView superTextView = view.findViewById(R.id.group_tv);
        superTextView.setLeftTopString(group.getGroupName());
        superTextView.setLeftString(group.getIntroduce());
        superTextView.setLeftBottomString("共 "+group.getNumPost()+" 条动态                 创建时间: "+DateConverter.dateToStr(group.getCreated()));
        Picasso.with(context)
                .load(group.getImageUrl())
                .placeholder(R.mipmap.group_head)
                .into(superTextView.getLeftIconIV());

        superTextView.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                Intent intent = new Intent(context, GroupInfoActivity.class);
                intent.putExtra("groupinfo", group);
                context.startActivity(intent);
            }
        });

        return view;
    }

}
