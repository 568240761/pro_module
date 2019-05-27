package com.ly.widget.recycler.wrapper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.ColorRes;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import com.ly.widget.R;
import com.ly.widget.recycler.base.ViewHolder;
import com.ly.widget.recycler.util.WrapperUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhy on 16/6/23.
 */
public class LoadMoreWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE;
    public static final int ITEM_TYPE_LOAD_COMPLETE = ITEM_TYPE_LOAD_MORE - 1;
    public static final int ITEM_TYPE_LOAD_NULL = ITEM_TYPE_LOAD_COMPLETE - 1;
    private RecyclerView.Adapter mInnerAdapter;
    private View mLoadMoreView;
    private View mFailureView;
    private View mView;
    private int mLayoutMoreId;
    private int mLayoutCompleteId;

    /**
     * 最后一个布局类型
     */
    @Type
    private int mItemType = ITEM_TYPE_LOAD_MORE;

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

    public LoadMoreWrapper(RecyclerView.Adapter adapter, @LayoutRes int id, @LayoutRes int completeId, OnLoadMoreListener loadMoreListener) {
        mInnerAdapter = adapter;
        mLayoutMoreId = id == -1 ? R.layout.widget_recycler_load_more : id;
        mLayoutCompleteId = completeId == -1 ? R.layout.widget_recycler_load_completed : completeId;
        mOnLoadMoreListener = loadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_LOAD_MORE) {
            if (mView == null) {
                mView = LayoutInflater.from(parent.getContext()).inflate(mLayoutMoreId, parent,
                        false);
                if (mBgColor != 0) {
                    mView.setBackgroundResource(mBgColor);
                }
                mLoadMoreView = mView.findViewById(R.id.load_more_group);
                mFailureView = mView.findViewById(R.id.tv_failure);
                mFailureView.setOnClickListener(v -> {
                    if (mOnLoadMoreListener != null) {
                        loadMoreViewVisible();
                        mOnLoadMoreListener.onLoadMoreRequested();
                    }
                });
            }
            return ViewHolder.createViewHolder(mView);
        } else if (viewType == ITEM_TYPE_LOAD_COMPLETE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutCompleteId, parent, false);
            if (mBgColor != 0) {
                view.setBackgroundResource(mBgColor);
            }
            return ViewHolder.createViewHolder(view);
        } else if (viewType == ITEM_TYPE_LOAD_NULL) {
            return ViewHolder.createViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_recycler_load_null, parent, false));
        } else {
            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isShowLoadMore(position)) {
            if (mOnLoadMoreListener != null && !mIsLoading && mItemType == ITEM_TYPE_LOAD_MORE) {
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
            if (mItemType == ITEM_TYPE_LOAD_MORE)
                return ITEM_TYPE_LOAD_MORE;
            else if (mItemType == ITEM_TYPE_LOAD_COMPLETE)
                return ITEM_TYPE_LOAD_COMPLETE;
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
            mLoadMoreView.setVisibility(View.GONE);
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

    public void setItemType(@Type int mItemType) {
        this.mItemType = mItemType;
    }

    public boolean isIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean mIsLoading) {
        this.mIsLoading = mIsLoading;
    }

    @IntDef(value = {
            ITEM_TYPE_LOAD_MORE,
            ITEM_TYPE_LOAD_COMPLETE,
            ITEM_TYPE_LOAD_NULL
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public interface OnLoadMoreListener {
        void onLoadMoreRequested();
    }
}
