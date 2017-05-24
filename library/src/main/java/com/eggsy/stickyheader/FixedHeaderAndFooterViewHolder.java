package com.eggsy.stickyheader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView中使用的固定头部或者底部的ViewHolder
 * 用于RecyclerView线性布局时，包含头部或者底部View{@link WrapperHeaderAndFooterAdapter}
 *
 * @author chenyongkang
 * @Date 2017/5/19 20:25
 */
public class FixedHeaderAndFooterViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;

    public FixedHeaderAndFooterViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
    }

    protected static FixedHeaderAndFooterViewHolder createViewHolder(Context context, View itemView) {
        FixedHeaderAndFooterViewHolder viewHolder = new FixedHeaderAndFooterViewHolder(context, itemView);
        return viewHolder;
    }

}
