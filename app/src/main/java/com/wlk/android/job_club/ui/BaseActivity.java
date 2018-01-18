package com.wlk.android.job_club.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wlk.android.job_club.R;


/**
 * Created by wanglunkui on 2018/1/18.
 */

public class BaseActivity extends RxAppCompatActivity {
    View rootView;
    FrameLayout contentFrameLayout;
    Context mContext;
    LayoutInflater inflater;
    boolean hasToolbar;
    LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mContext = this;
        StatusBarUtil.setDarkModel(this);//处理状态栏颜色
    }

    public void showLoadingDialog(){
        if (loadingDialog == null) loadingDialog = new LoadingDialog(this);
        if (!loadingDialog.isShowing())
            loadingDialog.show();
    }

    public void closeLoadingDialog(){
        if (loadingDialog == null) loadingDialog = new LoadingDialog(this);
        if (!loadingDialog.isShowing())
            loadingDialog.show();
    }

    public void setHasToolbar(boolean hasToolbar) {
        this.hasToolbar = hasToolbar;
    }

    @Override
    public void setContentView(int layoutResID) {
        inflater = LayoutInflater.from(this);
        Log.w("wlk","inflater is null "+(inflater==null));
        rootView = inflater.inflate(R.layout.base_activity, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(params);
        contentFrameLayout = rootView.findViewById(R.id.content_layout);
        View userView = inflater.inflate(layoutResID, null);
        FrameLayout.LayoutParams userParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int toolBarSize = this.getResources().getDimensionPixelSize(R.dimen.common_48);
        /*如果是悬浮状态，则不需要设置间距*/
        if (hasToolbar) {
            View toolbarLayout = inflater.inflate(R.layout.base_toolbar, null);
            View lineView = toolbarLayout.findViewById(R.id.id_tool_bar_line_view);
            lineView.setVisibility(View.VISIBLE);
            View backImg = (ImageView) toolbarLayout.findViewById(R.id.back_img);
            Toolbar toolbar = (Toolbar) toolbarLayout.findViewById(R.id.id_tool_bar);
            toolbar.setTitle("");
            contentFrameLayout.addView(toolbarLayout);
            userParams.topMargin = toolBarSize;
        }
        else userParams.topMargin = toolBarSize / 2;
        contentFrameLayout.addView(userView, params);
        setContentView(rootView);
    }
}
