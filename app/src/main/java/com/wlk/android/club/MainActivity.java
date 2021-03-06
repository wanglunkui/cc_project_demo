package com.wlk.android.club;

import android.animation.ObjectAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wlk.android.club.ui.BaseActivity;
import com.wlk.android.club.ui.MyF;
import com.wlk.android.club.ui.RecyclerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view2;
    private RecyclerView recyclerView;
    private List<Map<String, Object>> data = new ArrayList<>();
    private RecyclerAdapter adapter = new RecyclerAdapter(this, data);
    private Handler handler = new Handler();
    boolean isLoading;
    DrawerLayout drawerLayout;
    private int lastX;
    private FrameLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasToolbar(true);
        setContentView(R.layout.activity_main);
        findViewById(R.id.login).setOnClickListener(this);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        view2 = findViewById(R.id.layout2);
        recyclerView = findViewById(R.id.recycler_view);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        initSlidingMenu();
        initFresco();
    }

    private void initViewPagerInsideScrollView(){
        try{
            TabLayout tab = findViewById(R.id.tab);
            ViewPager viewPager = findViewById(R.id.view_pager);
            final String[] titles = new String[]{"1","2"};
            viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return MyF.newInstance(position);
                }

                @Override
                public int getCount() {
                    return titles.length;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    return titles[position];
                }
            });
            tab.setupWithViewPager(viewPager);

        }catch (Exception e){
            Log.w("wlk","", e);
        }

    }

    private void initFresco(){
        final SimpleDraweeView simpleDraweeView = findViewById(R.id.fresco_img);
        simpleDraweeView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse("res://com.wlk.android.job_club/"+R.drawable.test2);
                simpleDraweeView.setImageURI(uri);
            }
        }, 3000);
    }

    private void initRecyclerView(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        getData();
                    }
                }, 2000);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("test", "StateChanged = " + newState);


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("test", "onScrolled");

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getData();
                                Log.d("test", "load more completed");
                                isLoading = false;
                            }
                        }, 1000);
                    }
                }
            }
        });
    }

    private EditText getAccount(){
        return (EditText) findViewById(R.id.account);
    }

    private EditText getPassword(){
        return (EditText) findViewById(R.id.password);
    }

    private void initRawImg(){
        getResources().openRawResource(R.raw.ic_launcher);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
//                HttpUtil.login(getAccount().getText().toString(),
//                        getPassword().getText().toString(), this);
                showAnimation();
                break;
            case R.id.btn_1:
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                view2.setVisibility(View.GONE);
                break;
            case R.id.btn_2:
                swipeRefreshLayout.setVisibility(View.GONE);
                view2.setVisibility(View.VISIBLE);
                initRecyclerView();
                initData();
                break;
            case R.id.btn_3:
                initViewPagerInsideScrollView();
                break;
        }
    }

    private void initSlidingMenu(){
        drawerLayout = findViewById(R.id.drawable_layout);
        mainLayout = findViewById(R.id.main_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                null, R.string.action_settings,R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                int width = drawerView.getWidth();
                int left = (int) (width * slideOffset);
                int scrollX = left - lastX;
                mainLayout.scrollBy(-scrollX, 0);
                lastX = left;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void showAnimation(){
        mainLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                float x = findViewById(R.id.btn_1).getPivotX();
                float y = findViewById(R.id.btn_1).getPivotY();
                TextView textView = new TextView(mainLayout.getContext());
                textView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams( 200, 90);
                mainLayout.addView(textView, layoutParams);
                ObjectAnimator animatorX = ObjectAnimator.ofFloat(textView, "translationX",
                        x,
                        x+10,
                        x+2,
                        x-3,
                        x-9,
                        x-1,
                        x+2,
                        x);
                animatorX.setDuration(2000);
                animatorX.start();
            }
        }, 3000);
    }

    public void initData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1500);

    }

    /**
     * 获取测试数据
     */
    private void getData() {
        for (int i = 0; i < 6; i++) {
            Map<String, Object> map = new HashMap<>();
            data.add(map);
        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyItemRemoved(adapter.getItemCount());
    }
}


