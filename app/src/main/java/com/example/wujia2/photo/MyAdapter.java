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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter  extends BaseAdapter {
    private List<Post> list;
    private Context context;

    public MyAdapter(Context context, List<Post> list) {
        this.context = context;
        this.list = list;
    }

    public void addPost(List<Post> list) {
        this.list = list;

    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        else
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
            view = LayoutInflater.from(context).inflate(R.layout.item_post, null);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.post_username);
            holder.time = (TextView) view.findViewById(R.id.post_time);
            holder.content = (TextView) view.findViewById(R.id.post_content);
            holder.icon = (ImageView) view.findViewById(R.id.headIcon);
            holder.nineGrid = (NineGridView) view.findViewById(R.id.post_nineGrid);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Picasso.with(context).load(list.get(i).getUserIcon()).into(holder.icon);
        String mycontent = list.get(i).getContent();
        if (mycontent == null || mycontent.length() <= 0) {
            holder.content.setVisibility(View.GONE);
        } else {
            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(mycontent);
        }
        holder.name.setText(list.get(i).getUserName());
        holder.time.setText(list.get(i).getTime());
        if (list.get(i).isHaveIcon()) {//判断是否有图片
            ArrayList<ImageInfo> imageInfo = new ArrayList<>();
            for (int j = 0; j < list.get(i).getHeadImgUrl().size(); j++) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(list.get(i).getHeadImgUrl().get(j));
                info.setBigImageUrl(list.get(i).getHeadImgUrl().get(j));
                imageInfo.add(info);
            }
            holder.nineGrid.setAdapter(new NineGridViewClickAdapter(context, imageInfo));
        } else {
            holder.nineGrid.setVisibility(View.GONE);
        }
        return view;
    }

    private class ViewHolder {
        private TextView name;
        private TextView time;
        private TextView content;
        private ImageView icon;
        private com.lzy.ninegrid.NineGridView nineGrid;
    }
}
