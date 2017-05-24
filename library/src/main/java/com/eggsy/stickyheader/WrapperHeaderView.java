package com.eggsy.stickyheader;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/5/24.
 */
public class WrapperHeaderView extends LinearLayout{

    View mHeader;

    Context mContext;

    public WrapperHeaderView(Context context) {
        this(context,null);
    }

    public WrapperHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapperHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void setWrapperHeader(View headerView){
        if(getChildCount()>0){
            removeAllViews();
        }
        mHeader = headerView;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mHeader,layoutParams);
    }

    public View getHeader(){
        return mHeader;
    }

}
