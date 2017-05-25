package com.eggsy.stickyheader;

import android.view.View;

/**
 * RecyclerView粘性头部获取头部视图接口
 * 主要用于获取对应的header view和header id标识，用于判断头部是否存在是否显示更新等信息
 *
 * @author chenyongkang
 * @Date 2017/5/22 14:40
 */
interface StickyHeadersAdapter {

    View getHeaderView(int position);

    long getHeaderId(int position);

    void itemRemoveWithData(int position);
}
