package com.ly.widget.recycler.wrapper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import com.ly.widget.R;
import com.ly.widget.recycler.base.ViewHolder;
import com.ly.widget.recycler.util.WrapperUtils;

/**
 * Created by zhy on 16/6/23.
 */
public class LoadMoreWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE;
    public static final int ITEM_TYPE_LOAD_NULL = ITEM_TYPE_LOAD_MORE - 1;

    private RecyclerView.Adapter mInnerAdapter;
    private View mLoadMoreView;
    private View mFailureView;
    private View mView;
    private int mLayoutId;

    /**
     * 是否显示加载更多布局
     */
    private boolean mIsShow;

    /**
     * 是否正在加载
     */
    private boolean mIsLoading;
    /**
     * mView的背景颜色
     */
    @ColorRes
    private int mBgColor;

    private OnLoadMoreListener mOnLoadMoreListener;

    public LoadMoreWrapper(RecyclerView.Adapter adapter, @LayoutRes int id, OnLoadMoreListener loadMoreListener) {
        mInnerAdapter = adapter;
        mLayoutId = id == -1 ? R.layout.widget_recycler_load_more : id;
        mOnLoadMoreListener = loadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_LOAD_MORE) {
            if (mView == null) {
                mView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent,
                        false);
                if (mBgColor != 0) {
                    mView.setBackgroundResource(mBgColor);
                }
                mLoadMoreView = mView.findViewById(R.id.load_more_group);
                mFailureView = mView.findViewById(R.id.tv_failure);
                mFailureView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnLoadMoreListener != null) {
                            loadMoreViewVisible();
                            mOnLoadMoreListener.onLoadMoreRequested();
                        }
                    }
                });
            }
            return ViewHolder.createViewHolder(mView);
        } else if (viewType == ITEM_TYPE_LOAD_NULL) {
            return ViewHolder.createViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_recycler_load_null, parent, false));
        } else {
            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isShowLoadMore(position)) {
            if (mOnLoadMoreListener != null && !mIsLoading && mIsShow) {
                mIsLoading = true;
                mLoadMoreView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOnLoadMoreListener.onLoadMoreRequested();
                    }
                }, 500);
            }
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowLoadMore(position)) {
            if (mIsShow)
                return ITEM_TYPE_LOAD_MORE;
            else
                return ITEM_TYPE_LOAD_NULL;
        }
        return mInnerAdapter.getItemViewType(position);
    }

    private boolean isShowLoadMore(int position) {
        return position >= mInnerAdapter.getItemCount();
    }

    @Override
    public int getItemCount() {
        return mInnerAdapter.getItemCount() + 1;
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow(holder);
        if (isShowLoadMore(holder.getLayoutPosition())) {
            WrapperUtils.setFullSpan(holder);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(mInnerAdapter, recyclerView, (layoutManager, oldLookup, position) -> {
            if (isShowLoadMore(position)) {
                return layoutManager.getSpanCount();
            }
            if (oldLookup != null) {
                return oldLookup.getSpanSize(position);
            }
            return 1;
        });
    }


    public LoadMoreWrapper setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        if (loadMoreListener != null) {
            mOnLoadMoreListener = loadMoreListener;
        }
        return this;
    }

    public void failureViewVisible() {
        if (mIsLoading) {
            mLoadMoreView.setVisibility(View.INVISIBLE);
            mFailureView.setVisibility(View.VISIBLE);
        }
    }

    public void loadMoreViewVisible() {
        mLoadMoreView.setVisibility(View.VISIBLE);
        mFailureView.setVisibility(View.GONE);
    }

    public void setBgColor(@ColorRes int mBgColor) {
        if (mView != null) {
            mView.setBackgroundResource(mBgColor);
        }
        this.mBgColor = mBgColor;
    }

    public void setIsShow(boolean mIsShow) {
        this.mIsShow = mIsShow;
    }

    public boolean isIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean mIsLoading) {
        this.mIsLoading = mIsLoading;
    }

    public interface OnLoadMoreListener {
        void onLoadMoreRequested();
    }
}
