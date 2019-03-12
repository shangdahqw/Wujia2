package com.example.wujia2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.allen.library.SuperTextView;
import com.example.wujia2.R;
import com.example.wujia2.pojo.Group;

import java.util.List;


/**
 * Created by allen on 2016/10/31.
 */

public class GroupAdapter extends BaseAdapter {


    private List<Group> groupList;
    private LayoutInflater inflater;
    public GroupAdapter() {}

    public GroupAdapter(List<Group> groupList,Context context) {
        this.groupList = groupList;
        this.inflater=LayoutInflater.from(context);
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
        Group group=(Group) getItem(position);
        SuperTextView superTextView = view.findViewById(R.id.super_tv);
        superTextView.setLeftTopString(group.getGroupName());

        return view;
    }
}
