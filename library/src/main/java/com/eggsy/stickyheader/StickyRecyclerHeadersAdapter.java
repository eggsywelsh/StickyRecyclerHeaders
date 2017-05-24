package com.eggsy.stickyheader;

import android.support.v7.widget.RecyclerView;

/**
 * RecyclerView粘性头部适配器接口
 * 承接了RecyclerView.Adapter类和StickyHeadersAdapter{@link StickyHeadersAdapter}接口
 * StickyHeadersAdapter主要用于获取对应的header view和header id标识，判断item是否显示header view使用
 *
 * @author chenyongkang
 * @Date 2017/5/22 14:40
 */
public abstract class StickyRecyclerHeadersAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements StickyHeadersAdapter{



}
