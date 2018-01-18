package com.wlk.android.job_club;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.app.Activity;
import android.widget.EditText;

import com.wlk.android.job_club.http.AuthorizationJSON;
import com.wlk.android.job_club.http.HttpUtil;
import com.wlk.android.job_club.http.ServiceFactory;
import com.wlk.android.job_club.ui.BaseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasToolbar(true);
        setContentView(R.layout.activity_main);
        findViewById(R.id.login).setOnClickListener(this);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
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
        }
    }


}
