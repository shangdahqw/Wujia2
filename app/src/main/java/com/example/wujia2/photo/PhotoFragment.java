package com.example.wujia2.photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    SuperTextView  tV_photo= (SuperTextView) view.findViewById(R.id.photoview);
    tV_photo.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
      @Override
      public void onClickListener(SuperTextView superTextView) {

        Intent intent = new Intent(getActivity(), com.example.wujia2.photo.MainActivity.class);
        startActivity(intent);
      }
    });

    return view;
  }
}
