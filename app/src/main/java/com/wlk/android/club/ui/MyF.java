package com.wlk.android.club.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wlk.android.club.R;

/**
 * Created by wanglunkui on 2018/1/29.
 */

public class MyF extends Fragment {

    private int type;

    public static MyF newInstance(int type){
        MyF myF = new MyF();
        myF.type = type;
        return myF;
    }

    public MyF(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_toolbar, container, false);
        Log.w("wlk","type "+type);
        if(type == 0) view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        else view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        return view;
    }
}