package com.eggsy.stickyheader;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * 支持添加头部和尾部的RecycleView
 *
 * @author chenyongkang
 * @Date 2017/5/19 19:52
 */
public class WrapperHeaderAndFooterAdapter<HV extends FixedHeaderAndFooterViewHolder, FV extends FixedHeaderAndFooterViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();

    private RecyclerView.Adapter mInnerAdapter;

    public WrapperHeaderAndFooterAdapter(@NonNull RecyclerView.Adapter adapter) {
        mInnerAdapter = adapter;
    }

    /**
     * 是否是头部视图位置
     *
     * @param position
     * @return
     */
    public boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    /**
     * 是否是底部视图位置
     *
     * @param position
     * @return
     */
    public boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFooterView(View view) {
        mFooterViews.put(mFooterViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFooterViews.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            RecyclerView.ViewHolder holder = HV.createViewHolder(parent.getContext(), mHeaderViews.get(viewType));
            return holder;
        } else if (mFooterViews.get(viewType) != null) {
            RecyclerView.ViewHolder holder = FV.createViewHolder(parent.getContext(), mFooterViews.get(viewType));
            return holder;
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFooterViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return mInnerAdapter.getItemViewType(position - getHeadersCount());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderViewPos(position)) {
            return;
        }
        if (isFooterViewPos(position)) {
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }

    private int getRealItemCount() {
        return mInnerAdapter != null ? mInnerAdapter.getItemCount() : 0;
    }

    @Override
    public int getItemCount() {
        return getHeadersCount() + getRealItemCount() + getFootersCount();
    }

    public RecyclerView.Adapter getInnerAdapter() {
        return mInnerAdapter;
    }

}
