package com.eggsy.stickyheader;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView粘性头部的ViewHolder
 * 用于RecyclerView线性布局时，包含组合的ViewGroup{@link WrapperLinearItemView}的itemview
 *
 * @author chenyongkang
 * @Date 2017/5/22 20:48
 */
public class WrapperLinearStickyHeadersViewHolder extends RecyclerView.ViewHolder {

    public WrapperLinearStickyHeadersViewHolder(View itemView) {
        super(itemView);
    }

    public static WrapperLinearStickyHeadersViewHolder createViewHolder(View view){
        return new WrapperLinearStickyHeadersViewHolder(view);
    }
}
