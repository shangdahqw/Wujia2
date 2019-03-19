package com.example.wujia2;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.wujia2.group.GroupFragment;
import com.example.wujia2.message.MessageFragment;
import com.example.wujia2.myindex.MyIndexFragment;
import com.example.wujia2.navigation.BottomNavigationViewHelper;
import com.example.wujia2.navigation.ViewPagerAdapter;
import com.example.wujia2.photo.PhotoFragment;

public class HomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setNavigation();

        setContentView(R.layout.activity_home);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        // 默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_news:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.item_lib:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.item_find:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.item_more:
                                viewPager.setCurrentItem(3);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(
                            int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (menuItem != null) {
                            menuItem.setChecked(false);
                        } else {
                            bottomNavigationView.getMenu().getItem(0).setChecked(false);
                        }
                        menuItem = bottomNavigationView.getMenu().getItem(position);
                        menuItem.setChecked(true);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

        setupViewPager(viewPager);
    }


    private void setNavigation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white)); // 设置状态栏颜色
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new PhotoFragment());
        adapter.addFragment(new GroupFragment());
        adapter.addFragment(new MessageFragment());
        adapter.addFragment(new MyIndexFragment());

        viewPager.setAdapter(adapter);
    }
}
