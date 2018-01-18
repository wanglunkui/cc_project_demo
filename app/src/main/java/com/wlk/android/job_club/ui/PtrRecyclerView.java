package com.wlk.android.job_club.ui;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.wlk.android.job_club.R;

/**
 * Created by wanglunkui on 2018/1/18.
 */


public class PtrRecyclerView extends FrameLayout implements NestedScrollView.OnScrollChangeListener {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private OnRefreshListener mRefreshListener;
    private OnLoadMoreListener mLoadMoreListener;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerAdapter mRecyclerAdapter;
    private boolean isLoadingMore;

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public PtrRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public PtrRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        inflate(mContext, R.layout.ptr_recycler_view, this);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.cd_swipe_refresh_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mRefreshListener != null)
                    mRefreshListener.onRefresh();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.cd_recycle_view);
        mLinearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
    }


    /**
     * 设置允许加载更多
     *
     * @param enableLoadMore
     */
    public void setEnableLoadMore(boolean enableLoadMore) {
        if(mRecyclerAdapter == null) return;
        mRecyclerAdapter.setLoadMoreEnabled(enableLoadMore);
    }

    /**
     * 设置允许下拉刷新
     * @param enableRefresh
     */
    public void setEnableRefresh(boolean enableRefresh){
        mRefreshLayout.setEnabled(enableRefresh);
    }

    public void setAdapter(RecyclerView.Adapter adater) {
        this.mAdapter = adater;

        mRecyclerAdapter = new RecyclerAdapter(adater);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setLoadMoreEnabled(false);
        mRecyclerAdapter.setFooterView(R.layout.custom_bottom_progressbar);
        mRecyclerAdapter.setLoadMoreListener(mOnLoadMoreListener);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor){
        mRecyclerView.addItemDecoration(decor);
    }

    private RecyclerAdapter.OnLoadMoreListener mOnLoadMoreListener = new RecyclerAdapter.OnLoadMoreListener() {
        @Override
        public void onLoadMore(RecyclerAdapter.Enabled enabled) {
            if (mLoadMoreListener != null && enabled.getLoadMoreEnabled())
                mLoadMoreListener.onLoadMore();
        }
    };

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mRefreshListener = onRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mLoadMoreListener = onLoadMoreListener;
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener listener){
        mRecyclerView.addOnScrollListener(listener);
    }

    public void setRefreshing(boolean refreshing) {
        mRefreshLayout.setRefreshing(refreshing);
    }

    public boolean isRefreshing(){
        return mRefreshLayout.isRefreshing();
    }

}
