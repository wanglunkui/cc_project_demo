package com.wlk.android.job_club.ui;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wlk.android.job_club.R;

/**
 * Created by wanglunkui on 2018/1/18.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = RecyclerAdapter.class.getSimpleName();
    private static final byte TYPE_FOOTER = -2;

    private RecyclerView.Adapter mAdapter;
    private View mFooterView;
    private int mFooterResId = View.NO_ID;

    private RecyclerView mRecyclerView;
    private OnLoadMoreListener mOnLoadMoreListener;

    private Enabled mEnabled;
    private boolean mIsLoading;
    private boolean mShouldRemove;

    public RecyclerAdapter(@NonNull RecyclerView.Adapter adapter) {
        registerAdapter(adapter);
    }

    public RecyclerAdapter(@NonNull RecyclerView.Adapter adapter, View footerView) {
        registerAdapter(adapter);
        mFooterView = footerView;
    }

    public RecyclerAdapter(@NonNull RecyclerView.Adapter adapter, @LayoutRes int resId) {
        registerAdapter(adapter);
        mFooterResId = resId;
    }

    private void registerAdapter(RecyclerView.Adapter adapter) {
        if (adapter == null) {
            throw new NullPointerException("adapter can not be null!");
        }

        mAdapter = adapter;
        mAdapter.registerAdapterDataObserver(mObserver);
        mEnabled = new Enabled(mOnEnabledListener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            if (mFooterResId != View.NO_ID) {
                mFooterView = LayoutInflater.from(parent.getContext()).inflate(mFooterResId, parent, false);
            }
            if (mFooterView != null) {
                return new FooterHolder(mFooterView);
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.base_footer, parent, false);
            return new FooterHolder(view);
        }

        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterHolder) {
            // 当recyclerView不能滚动的时候(item不能铺满屏幕的时候也是不能滚动的)
            // 触发loadMore
            if (!canScroll() && mOnLoadMoreListener != null && !mIsLoading) {
                mIsLoading = true;
                mOnLoadMoreListener.onLoadMore(mEnabled);
            }
        } else {
            mAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        int count = mAdapter.getItemCount();
        return getLoadMoreEnabled()? count + 1 : count + (mShouldRemove ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mAdapter.getItemCount() && (getLoadMoreEnabled() || mShouldRemove)) {
            return TYPE_FOOTER;
        }
        return mAdapter.getItemViewType(position);
    }

    public boolean canScroll() {
        if (mRecyclerView == null) {
            throw new NullPointerException("mRecyclerView is null, you should setAdapter(recyclerAdapter);");
        }
        return ViewCompat.canScrollVertically(mRecyclerView, -1);
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
    }

    public void setFooterView(@LayoutRes int resId) {
        mFooterResId = resId;
    }

    static class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(View itemView) {
            super(itemView);

            //当为StaggeredGridLayoutManager的时候,设置footerView占据整整一行
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        recyclerView.addOnScrollListener(mOnScrollListener);

        //当为GridLayoutManager的时候, 设置footerView占据整整一行.
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = ((GridLayoutManager) layoutManager);
            //获取原来的SpanSizeLookup,当不为null的时候,除了footerView都应该返回原来的spanSize
            final GridLayoutManager.SpanSizeLookup originalSizeLookup = gridLayoutManager.getSpanSizeLookup();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (getItemViewType(position) == TYPE_FOOTER) {
                        return gridLayoutManager.getSpanCount();
                    } else if (originalSizeLookup != null) {
                        return originalSizeLookup.getSpanSize(position);
                    }

                    return 1;
                }
            });
        }
    }

    /**
     * Deciding whether to trigger loading
     * 判断是否触发加载更多
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (!getLoadMoreEnabled() || mIsLoading) {
                return;
            }

            if (newState == RecyclerView.SCROLL_STATE_IDLE && mOnLoadMoreListener != null) {
                boolean isBottom;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    isBottom = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition()
                            >= layoutManager.getItemCount() - 1;
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager sgLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                    int[] into = new int[sgLayoutManager.getSpanCount()];
                    sgLayoutManager.findLastVisibleItemPositions(into);

                    isBottom = last(into) >= layoutManager.getItemCount() - 1;
                }else {
                    isBottom = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition()
                            >= layoutManager.getItemCount() - 1;
                }

                if (isBottom) {
                    mIsLoading = true;
                    mOnLoadMoreListener.onLoadMore(mEnabled);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    /**
     * 取到最后的一个节点
     */
    private static int last(int[] lastPositions) {
        int last = lastPositions[0];
        for (int value : lastPositions) {
            if (value > last) {
                last = value;
            }
        }
        return last;
    }

    /**
     * clean
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        recyclerView.removeOnScrollListener(mOnScrollListener);
        mAdapter.unregisterAdapterDataObserver(mObserver);
        mRecyclerView = null;
    }

    public void setLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    public interface OnLoadMoreListener{
        void onLoadMore(Enabled enabled);
    }

    public void setLoadMoreEnabled(boolean enabled) {
        mEnabled.setLoadMoreEnabled(enabled);
    }

    public boolean getLoadMoreEnabled() {
        return mEnabled.getLoadMoreEnabled() && mAdapter.getItemCount() > 0;
    }

    private interface OnEnabledListener {
        void notifyChanged();
    }

    private OnEnabledListener mOnEnabledListener = new OnEnabledListener() {
        @Override
        public void notifyChanged() {
            mShouldRemove = true;
        }
    };

    /**
     * 控制加载更多的开关, 作为 {@link OnLoadMoreListener onLoadMore(Enabled enabled) 的参数}
     */
    public static class Enabled {
        private boolean mLoadMoreEnabled = true;
        private OnEnabledListener mListener;

        public Enabled(OnEnabledListener listener) {
            mListener = listener;
        }

        /**
         * 设置是否启用加载更多
         * @param enabled 是否启用
         */
        public void setLoadMoreEnabled(boolean enabled) {
            final boolean canNotify = mLoadMoreEnabled;
            mLoadMoreEnabled = enabled;

            if (canNotify && !mLoadMoreEnabled) {
                mListener.notifyChanged();
            }
        }

        /**
         * 获取是否启用了加载更多,默认是true
         * @return boolean
         */
        public boolean getLoadMoreEnabled() {
            return mLoadMoreEnabled;
        }
    }

    private RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (mShouldRemove) {
                mShouldRemove = false;
            }
            RecyclerAdapter.this.notifyDataSetChanged();
            mIsLoading = false;
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (mShouldRemove && positionStart == mAdapter.getItemCount()) {
                mShouldRemove = false;
            }
            RecyclerAdapter.this.notifyItemRangeChanged(positionStart, itemCount);
            mIsLoading = false;
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (mShouldRemove && positionStart == mAdapter.getItemCount()) {
                mShouldRemove = false;
            }
            RecyclerAdapter.this.notifyItemRangeChanged(positionStart, itemCount, payload);
            mIsLoading = false;
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            RecyclerAdapter.this.notifyItemRangeInserted(positionStart, itemCount);
            notifyFooterHolderChanged();
            mIsLoading = false;
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (mShouldRemove && positionStart == mAdapter.getItemCount()) {
                mShouldRemove = false;
            }
            RecyclerAdapter.this.notifyItemRangeRemoved(positionStart, itemCount);
            mIsLoading = false;
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (mShouldRemove && (fromPosition == mAdapter.getItemCount() || toPosition == mAdapter.getItemCount())) {
                throw new IllegalArgumentException("can not move last position after setLoadMoreEnabled(false)");
            }
            RecyclerAdapter.this.notifyItemMoved(fromPosition, toPosition);
            mIsLoading = false;
        }
    };

    /**
     *  update last item
     */
    private void notifyFooterHolderChanged() {
        if (getLoadMoreEnabled()) {
            RecyclerAdapter.this.notifyItemChanged(mAdapter.getItemCount());
        } else if (mShouldRemove) {
            mShouldRemove = false;

            /**
             * fix IndexOutOfBoundsException when setLoadMoreEnabled(false) and then use onItemRangeInserted
             * @see android.support.v7.widget.RecyclerView.Recycler
             * boolean validateViewHolderForOffsetPosition(ViewHolder holder)
             */
            int position = mAdapter.getItemCount();
            RecyclerView.ViewHolder viewHolder =
                    mRecyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder instanceof FooterHolder) {
                RecyclerAdapter.this.notifyItemRemoved(position);
            }
        }
    }
}
