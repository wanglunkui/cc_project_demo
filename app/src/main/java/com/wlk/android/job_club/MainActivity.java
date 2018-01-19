package com.wlk.android.job_club;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.wlk.android.job_club.http.HttpUtil;
import com.wlk.android.job_club.ui.BaseActivity;
import com.wlk.android.job_club.ui.RecyclerAdapter;

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                HttpUtil.login(getAccount().getText().toString(),
                        getPassword().getText().toString(), this);
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
        }
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
