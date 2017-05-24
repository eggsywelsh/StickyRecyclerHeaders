package com.eggsy.stickyheader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.SectionIndexer;

/**
 * 实现了包含SectionIndexer接口的RecyclerView数据适配器
 *
 * @author chenyongkang
 * @Date 2017/5/22 14:32
 */
public class SectionIndexerAdapterWrapper<VH extends RecyclerView.ViewHolder> extends WrapperLinearRecycleAdapter<VH> implements SectionIndexer {

    SectionIndexer mSectionIndexerDelegate;

    StickyRecyclerHeadersAdapter mStickyRecyclerHeadersAdapter;

    Context mContext;

    public SectionIndexerAdapterWrapper(Context context, SectionIndexer delegate,StickyRecyclerHeadersAdapter stickyRecyclerHeadersAdapter) {
        super(context,stickyRecyclerHeadersAdapter);
        mContext = context;
        mSectionIndexerDelegate = delegate;
        mStickyRecyclerHeadersAdapter = stickyRecyclerHeadersAdapter;
    }

    @Override
    public Object[] getSections() {
        return mSectionIndexerDelegate.getSections();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionIndexerDelegate.getPositionForSection(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return mSectionIndexerDelegate.getPositionForSection(position);
    }


}
