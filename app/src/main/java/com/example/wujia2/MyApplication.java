package com.example.wujia2;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wujia2.photo.ImageLoader;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.ninegrid.NineGridView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyApplication extends Application {

    public static String SERVER_MICRO_URL = null;
    public static String SERVER_IMAGE_URL = null;
    public static String SERVER_UPLOAD_URL = null;


    @Override
    public void onCreate() {
        super.onCreate();
        NineGridView.setImageLoader(new GlideImageLoader());
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素

        init();
    }

    private void init() {
        Properties properties = new Properties();

        try {
            InputStream in = getAssets().open("config.properties");  //打开assets目录下的config.properties文件
            properties.load(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        // 获取key对应的value值
        SERVER_MICRO_URL = properties.getProperty("SERVER_MICRO_URL");
        SERVER_IMAGE_URL = properties.getProperty("SERVER_IMAGE_URL");
        SERVER_UPLOAD_URL = properties.getProperty("SERVER_UPLOAD_URL");


    }

    private class GlideImageLoader implements NineGridView.ImageLoader {
        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Glide.with(context).load(url)//
                    .placeholder(R.drawable.ic_default_image)//
                    .error(R.drawable.ic_default_image)//
                    .into(imageView);

        }
        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }


}
