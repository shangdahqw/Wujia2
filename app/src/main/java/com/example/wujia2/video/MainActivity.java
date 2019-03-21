//package com.example.wujia2.video;
//
//import android.Manifest;
//import android.annotation.TargetApi;
//import android.app.FragmentTransaction;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Shader;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.cjt2325.cameralibrary.util.DeviceUtil;
//import com.example.wujia2.R;
//
//public class MainActivity extends AppCompatActivity {
//    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
//    private ImageButton photo_play;
//    private String videoPath;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camera_main);
//        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getPermissions();
//            }
//        });
//        photo_play = (ImageButton) findViewById(R.id.image_photo);
//
//        photo_play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 显示播放页面
//                VideoFragment bigPic = VideoFragment.newInstance(videoPath);
//                android.app.FragmentManager mFragmentManager = getFragmentManager();
//                FragmentTransaction transaction = mFragmentManager.beginTransaction();
//                transaction.replace(R.id.main_menu, bigPic);
//                transaction.commit();
//            }
//        });
//
//
//
//    }
//
//    /**
//     * 获取权限
//     */
//    private void getPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
//                    .PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager
//                            .PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager
//                            .PERMISSION_GRANTED) {
//                startActivityForResult(new Intent(MainActivity.this, CameraActivity.class), 100);
//            } else {
//                //不具有获取权限，需要进行权限申请
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.RECORD_AUDIO,
//                        Manifest.permission.CAMERA}, GET_PERMISSION_REQUEST);
//            }
//        } else {
//            startActivityForResult(new Intent(MainActivity.this, CameraActivity.class), 100);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == 101) {
//            Log.i("CJT", "picture");
//            String path = data.getStringExtra("path");
//            photo_play.setImageBitmap(BitmapFactory.decodeFile(path));
//        }
//        if (resultCode == 102) {
//            Log.i("CJT", "video");
//             videoPath = data.getStringExtra("path");
//
//            // 通过路径获取第一帧的缩略图并显示
//            Bitmap bitmap = Utils.getLocalVideoBitmap(videoPath);
//            BitmapDrawable drawable = new BitmapDrawable(bitmap);
//            drawable.setTileModeXY(Shader.TileMode.REPEAT , Shader.TileMode.REPEAT);
//            drawable.setDither(true);
//            photo_play.setBackgroundDrawable(drawable);
//
//
//        }
//        if (resultCode == 103) {
//            Toast.makeText(this, "请检查相机权限~", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @TargetApi(23)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == GET_PERMISSION_REQUEST) {
//            int size = 0;
//            if (grantResults.length >= 1) {
//                int writeResult = grantResults[0];
//                //读写内存权限
//                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
//                if (!writeGranted) {
//                    size++;
//                }
//                //录音权限
//                int recordPermissionResult = grantResults[1];
//                boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
//                if (!recordPermissionGranted) {
//                    size++;
//                }
//                //相机权限
//                int cameraPermissionResult = grantResults[2];
//                boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
//                if (!cameraPermissionGranted) {
//                    size++;
//                }
//                if (size == 0) {
//                    startActivityForResult(new Intent(MainActivity.this, CameraActivity.class), 100);
//                } else {
//                    Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
//}
