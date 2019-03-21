package com.example.wujia2.photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wujia2.R;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyAdapter  extends BaseAdapter {
    private List<Post> list;
    private Context context;
    private Bitmap bitmap;
    private String videoPath;


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
        final ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_post, null);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.post_username);
            holder.time = (TextView) view.findViewById(R.id.post_time);
            holder.content = (TextView) view.findViewById(R.id.post_content);
            holder.icon = (ImageView) view.findViewById(R.id.headIcon);
            holder.nineGrid = (NineGridView) view.findViewById(R.id.post_nineGrid);
            holder.pic_iv = (ImageView) view.findViewById(R.id.pic_iiv);
            holder.start_iv = (ImageView) view.findViewById(R.id.start_iv);
            holder.frameLayout = (FrameLayout) view.findViewById(R.id.frameLayout);

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


        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        holder.pic_iv.setImageBitmap((Bitmap) msg.obj);
                        break;
                }
                super.handleMessage(msg);
            }
        };

        if (list.get(i).isHaveIcon()) {//判断是否有图片

            if (list.get(i).getHeadImgUrl().size() != 1) {
                ArrayList<ImageInfo> imageInfo = new ArrayList<>();
                for (int j = 0; j < list.get(i).getHeadImgUrl().size(); j++) {
                    ImageInfo info = new ImageInfo();
                    info.setThumbnailUrl(list.get(i).getHeadImgUrl().get(j));
                    info.setBigImageUrl(list.get(i).getHeadImgUrl().get(j));
                    imageInfo.add(info);
                }
                holder.nineGrid.setAdapter(new NineGridViewClickAdapter(context, imageInfo));
                holder.frameLayout.setVisibility(View.GONE);


            } else {
                holder.nineGrid.setVisibility(View.GONE);
                holder.frameLayout.setVisibility(View.VISIBLE);

                final String videoPath = list.get(i).getHeadImgUrl().get(0);

                Thread t = new Thread(new Runnable(){
                    public void run(){
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        //获取网络视频
                        try {
                            retriever.setDataSource(videoPath, new HashMap<String, String>());
                            bitmap = retriever.getFrameAtTime();
                            if (bitmap!=null){
                                Message message = handler.obtainMessage();
                                message.what = 1;
                                message.obj = bitmap;
                                handler.sendMessage(message);

                            }

                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                retriever.release();
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                        }
                    }});
                t.start();
                holder.start_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PlayVideoActivity.class);
                        intent.putExtra("useCache",false);
                        intent.putExtra("videoPath",videoPath);
                        context.startActivity(intent);
                    }
                });
            }

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
        private FrameLayout frameLayout;
        private ImageView pic_iv, start_iv;
    }



}