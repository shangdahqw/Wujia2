package com.example.wujia2.photo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.allen.library.SuperTextView;
import com.example.wujia2.R;

public class PhotoFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        SuperTextView tV_photo = (SuperTextView) view.findViewById(R.id.photoview);
        SuperTextView tV_photo_head = (SuperTextView) view.findViewById(R.id.photo_stv);


        tV_photo.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {

                Intent intent = new Intent(getActivity(), com.example.wujia2.photo.MainActivity.class);
                startActivity(intent);
            }
        });
        tV_photo_head.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                dialogAlbum();
            }
        });
        return view;
    }

    /**
     * 在普通的dialog.show下面添加东西
     */
    private void dialogAlbum() {

        final String[] items = {"                     从相册中选择", "                       拍摄视频"};
        AlertDialog.Builder listdialog = new AlertDialog.Builder(getActivity());
        listdialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    Intent intent = new Intent(getActivity(), EditActivity.class);
                    startActivity(intent);

                } else if (which == 1) {
                    Intent intent = new Intent(getActivity(), CameraActivity.class);
                    startActivity(intent);

                } else {

                }
            }
        });
        AlertDialog dialog = listdialog.create();
        //显示
        dialog.show();
        //自定义的东西
        //放在show()之后，不然有些属性是没有效果的，比如height和width
        Window dialogWindow = dialog.getWindow();
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // 设置高度和宽度
//        p.height = (int) (d.getHeight() * 0.2); // 高度设置为屏幕的0.6
//        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65

        p.gravity = Gravity.BOTTOM;//设置位置
//        p.alpha = 0.8f;//设置透明度
//        p.x=80;
//        p.y =60;
        dialogWindow.setAttributes(p);
    }

}
