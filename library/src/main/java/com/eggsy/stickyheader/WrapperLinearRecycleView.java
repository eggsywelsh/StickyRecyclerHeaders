package com.eggsy.stickyheader;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * RecycleView的包装类
 *
 * @author chenyongkang
 * @Date 2017/5/19 19:45
 */
public class WrapperLinearRecycleView extends RecyclerView {

    ArrayList<ItemDecoration> mItemDecorations = new ArrayList<>();

    public WrapperLinearRecycleView(Context context) {
        super(context);
    }

    public WrapperLinearRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapperLinearRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addItemDecoration(ItemDecoration decor, int index) {
        super.addItemDecoration(decor, index);
        if (index < 0) {
            mItemDecorations.add(decor);
        } else {
            mItemDecorations.add(index, decor);
        }
    }

}
