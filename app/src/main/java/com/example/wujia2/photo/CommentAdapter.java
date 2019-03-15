package com.example.wujia2.photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wujia2.R;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {
   private ArrayList<Comment> list;
   private Context context;

    public CommentAdapter(ArrayList<Comment> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.photo_comm_item, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) view.findViewById(R.id.tv_comm_author);
            holder.head = view.findViewById(R.id.comment_item_icon);
            holder.tvTime = (TextView) view.findViewById(R.id.comm_time);
            holder.tvContent = (TextView) view.findViewById(R.id.tv_comm_content);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final Comment comment=list.get(i);
        String mycontent = list.get(i).getContent();
        if (mycontent == null || mycontent.length() <= 0) {
            holder.tvContent.setVisibility(View.GONE);
        } else {
            holder.tvContent.setVisibility(View.VISIBLE);
            holder.tvContent.setText(mycontent);
        }
        holder.tvName.setText(list.get(i).getName());
        holder.tvTime.setText(list.get(i).getCreateTime());
        Picasso.with(context).load(list.get(i).getUserHead()).into(holder.head);

        return view;
    }
    class ViewHolder{
        TextView tvName;
        TextView tvContent;
        TextView tvTime;
        RoundedImageView head;



    }
}
