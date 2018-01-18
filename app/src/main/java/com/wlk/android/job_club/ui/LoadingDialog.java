package com.wlk.android.job_club.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wlk.android.job_club.R;

/**
 * Created by wanglunkui on 2018/1/18.
 */

public class LoadingDialog extends Dialog{
    private Activity mContext;
    private View contentView;
    private View loadingView;

    public LoadingDialog(Context context) {
        super(context);
        this.mContext = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = View.inflate(mContext, R.layout.loading_view, null);
        loadingView = contentView.findViewById(R.id.loading_view);
        setContentView(contentView);
        this.setCanceledOnTouchOutside(false);

    }

    ObjectAnimator animator;

    @Override
    public void show() {
        super.show();
        animator = ObjectAnimator.ofFloat(loadingView, "rotation", 0f, 360f);
        animator.setRepeatCount(-1);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    @Override
    public void dismiss() {
        if (animator != null) animator.setRepeatCount(0);
        super.dismiss();
    }
}
