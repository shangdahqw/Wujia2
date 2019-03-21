package com.example.wujia2.photo;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wechatsmallvideoview.SurfaceVideoViewCreator;
import com.example.wujia2.R;

public class PlayVideoActivity extends AppCompatActivity {

        private SurfaceVideoViewCreator surfaceVideoViewCreator;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.play_video);

            surfaceVideoViewCreator =
                    new SurfaceVideoViewCreator(this,(RelativeLayout)findViewById(R.id.play_video)) {
                        @Override
                        protected Activity getActivity() {
                            return PlayVideoActivity.this;     /** 当前的 Activity */
                        }

                        @Override
                        protected boolean setAutoPlay() {
                            return false;                 /** true 适合用于，已进入就自动播放的情况 */
                        }

                        @Override
                        protected int getSurfaceWidth() {
                            return 0;                     /** Video 的显示区域宽度，0 就是适配手机宽度 */
                        }
                        @Override
                        protected int getSurfaceHeight() {
                            return 250;                   /** Video 的显示区域高度，dp 为单位 */
                        }
                        @Override
                        protected void setThumbImage(ImageView thumbImageView) {
//                            Glide.with(PlayVideoActivity.this)
//                                    .load("http://192.168.43.131/group1/M00/00/00/wKgrg1yPHAKAazTOAAQ26iUWXW447.jpeg")
//                                    .centerCrop()
//                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                    .placeholder(R.drawable.all_darkbackground)
//                                    .dontAnimate()
//                                    .into(thumbImageView);
                        }

                        /** 这个是设置返回自己的缓存路径，
                         * 应对这种情况：
                         *     录制的时候是在另外的目录，播放的时候默认是在下载的目录，所以可以在这个方法处理返回缓存
                         * */
                        @Override
                        protected String getSecondVideoCachePath() {
                            return null;
                        }

                        @Override
                        protected String getVideoPath() {
                            return getIntent().getStringExtra("videoPath");
                        }
                    };
            surfaceVideoViewCreator.debugModel = true;
            surfaceVideoViewCreator.setUseCache(getIntent().getBooleanExtra("useCache",false));
        }

        @Override
        protected void onPause() {
            super.onPause();
            surfaceVideoViewCreator.onPause();
        }

        @Override
        protected void onResume() {
            super.onResume();
            surfaceVideoViewCreator.onResume();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            surfaceVideoViewCreator.onDestroy();
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            surfaceVideoViewCreator.onKeyEvent(event); /** 声音的大小调节 */
            return super.dispatchKeyEvent(event);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            switch (requestCode) {
                case 1:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        break;
                    }
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }



}
