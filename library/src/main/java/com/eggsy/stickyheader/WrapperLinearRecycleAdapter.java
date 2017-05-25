package com.eggsy.stickyheader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * RecyclerView线性布局对应的适配器
 *
 * @author chenyongkang
 * @Date 2017/5/22 20:35
 */
class WrapperLinearRecycleAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements StickyHeadersAdapter {

    interface OnHeaderClickListener {
        void onHeaderClick(View header, int itemPosition, long headerId);
    }

    private Context mContext;

    protected StickyRecyclerHeadersAdapter mDelegate;

    private OnHeaderClickListener mOnHeaderClickListener;

    private StickyHeadersRecyleView.OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    /**
     * 构造器
     *
     * @param context                上下文
     * @param recyclerHeadersAdapter 粘性头部的适配器接口
     */
    public WrapperLinearRecycleAdapter(Context context, StickyRecyclerHeadersAdapter recyclerHeadersAdapter) {
        super();
        mContext = context;
        mDelegate = recyclerHeadersAdapter;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        WrapperLinearItemView wrapperLinearView = new WrapperLinearItemView(mContext);
        wrapperLinearView.mItemViewHolder = mDelegate.createViewHolder(parent, viewType);
        wrapperLinearView.mItem = wrapperLinearView.mItemViewHolder.itemView;
        return (VH) WrapperLinearStickyHeadersViewHolder.createViewHolder(wrapperLinearView);
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {

        WrapperLinearItemView wrapperLinearView = (WrapperLinearItemView) holder.itemView;

        mDelegate.onBindViewHolder(wrapperLinearView.mItemViewHolder, position);

        updateHeaderView(wrapperLinearView,position,holder);
    }

    @Override
    public int getItemCount() {
        return mDelegate == null ? 0 : mDelegate.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mDelegate.getItemViewType(position);
    }

    @Override
    public View getHeaderView(int position) {
        return mDelegate.getHeaderView(position);
    }

    @Override
    public long getHeaderId(int position) {
        return mDelegate.getHeaderId(position);
    }

    void setOnHeaderClickListener(OnHeaderClickListener onHeaderClickListener) {
        this.mOnHeaderClickListener = onHeaderClickListener;
    }

    void setOnItemClickListener(StickyHeadersRecyleView.OnRecyclerViewItemClickListener listener) {
        this.mOnRecyclerViewItemClickListener = listener;
    }

    @Override
    public void itemRemoveWithData(int position) {
        mDelegate.itemRemoveWithData(position);
        notifyItemRemoved(position);
    }

    void updateHeaderView(WrapperLinearItemView wrapperLinearView,final int position,final VH holder){
        if (position == 0 || mDelegate.getHeaderId(position) != mDelegate.getHeaderId(position - 1)) {
            addHeaderView(wrapperLinearView,position);
        } else {
            wrapperLinearView.mHeader = null;
        }

        if (mOnRecyclerViewItemClickListener != null) {
            wrapperLinearView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnRecyclerViewItemClickListener != null) {
                        mOnRecyclerViewItemClickListener.onItemClick(v, holder.getLayoutPosition());
                    }
                }
            });
        }

        wrapperLinearView.update();
    }

    void addHeaderView(WrapperLinearItemView wrapperLinearView,final int position){
        wrapperLinearView.mHeader = mDelegate.getHeaderView(position);
        if (mOnHeaderClickListener != null && wrapperLinearView.mHeader != null) {
            wrapperLinearView.mHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long headerId = mDelegate.getHeaderId(position);
                    mOnHeaderClickListener.onHeaderClick(v, position, headerId);
                }
            });
        }
    }
}
