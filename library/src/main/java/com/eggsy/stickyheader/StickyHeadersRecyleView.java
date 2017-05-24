package com.eggsy.stickyheader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SectionIndexer;

/**
 * 带有粘性头部的RecyclerView
 *
 * @author eggsy
 *         created at 2017/5/19 19:21
 */
public class StickyHeadersRecyleView extends FrameLayout {

    private static final String TAG = StickyHeadersRecyleView.class.getSimpleName();

    /**
     * 线性布局的RecylerView包装类
     */
    private WrapperLinearRecycleView mRecylerViewLinear;
    /**
     * 粘性头部对应的View
     */
    private View mHeader;

    /**
     * 当前头部对应的ID
     */
    private Long mHeaderId;
    /**
     * 头部位置
     */
    private Integer mHeaderPosition;
    /**
     * 头部距离父视图的偏移量
     */
    private Integer mHeaderOffset;

    /**
     * 外部滑动监听的委托对象
     */
    private RecyclerView.OnScrollListener mOnScrollListenerDelegate;

    /**
     * RecylerView对应的线性布局的适配器
     */
    private WrapperLinearRecycleAdapter mAdapter;

    /**
     * ------ 设置 ------
     */

    /**
     * 是否粘性头部
     */
    private boolean mAreHeadersSticky = true;

    /**
     * 滑动时候是否滑入padding区域
     * true表示不滑入padding区域，反之false表示滑入padding区域，
     * 对应的计算header视图距离顶部距离时需要相关的计算加减值
     */
    private boolean mClippingToPadding = true;

    /**
     * 粘性头部距离顶部偏移量
     */
    private int mStickyHeaderTopOffset = 0;

    /**
     * 外部设置的padding值
     */
    private int mPaddingLeft = 0;
    private int mPaddingTop = 0;
    private int mPaddingRight = 0;
    private int mPaddingBottom = 0;

    /**
     * 触摸处理
     */
//    private float mDownY;
//    private boolean mHeaderOwnsTouch;
    private float mTouchSlop;

    private boolean mIsPullUp;

    /**
     * 外部监听类
     */
    private OnHeaderClickListener mOnHeaderClickListener;
    private OnStickyHeaderOffsetChangedListener mOnStickyHeaderOffsetChangedListener;
    private OnStickyHeaderChangedListener mOnStickyHeaderChangedListener;
    //    private AdapterWrapperDataSetObserver mDataSetObserver;

    public StickyHeadersRecyleView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public StickyHeadersRecyleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StickyHeadersRecyleView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        mRecylerViewLinear = new WrapperLinearRecycleView(context);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StickyHeadersRecyleView);  // , defStyleAttr, 0
            if (a != null) {
                try {
                    // 视图属性
                    int padding = a.getDimensionPixelSize(R.styleable.StickyHeadersRecyleView_android_padding, 0);
                    mPaddingLeft = a.getDimensionPixelSize(R.styleable.StickyHeadersRecyleView_android_paddingLeft, padding);
                    mPaddingTop = a.getDimensionPixelSize(R.styleable.StickyHeadersRecyleView_android_paddingTop, padding);
                    mPaddingRight = a.getDimensionPixelSize(R.styleable.StickyHeadersRecyleView_android_paddingRight, padding);
                    mPaddingBottom = a.getDimensionPixelSize(R.styleable.StickyHeadersRecyleView_android_paddingBottom, padding);

                    setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);

                    mClippingToPadding = a.getBoolean(R.styleable.StickyHeadersRecyleView_android_clipToPadding, true);
                    super.setClipToPadding(true);
                    mRecylerViewLinear.setClipToPadding(mClippingToPadding);

                    // 滚动条
                    final int scrollBars = a.getInt(R.styleable.StickyHeadersRecyleView_android_scrollbars, 0x00000200);
                    mRecylerViewLinear.setVerticalScrollBarEnabled((scrollBars & 0x00000200) != 0);
                    mRecylerViewLinear.setHorizontalScrollBarEnabled((scrollBars & 0x00000100) != 0);

                    // 设置over-scroll模式
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                        mRecylerViewLinear.setOverScrollMode(a.getInt(R.styleable.StickyHeadersRecyleView_android_overScrollMode, 0));
                    }

                    // 粘性头部属性
                    mAreHeadersSticky = a.getBoolean(R.styleable.StickyHeadersRecyleView_hasStickyHeaders, true);
                } finally {
                    a.recycle();
                }
            }
            // 添加滚动监听
            if (mAreHeadersSticky) {
                mRecylerViewLinear.addOnScrollListener(new WrapperRecyclerViewScrollListener());
            }
            addView(mRecylerViewLinear);
        }

        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        manager.setSmoothScrollbarEnabled(false);
        setLayoutManager(manager);
    }

    private class WrapperRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (mOnScrollListenerDelegate != null) {
                mOnScrollListenerDelegate.onScrollStateChanged(recyclerView, newState);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mOnScrollListenerDelegate != null) {
                mOnScrollListenerDelegate.onScrolled(recyclerView, dx, dy);
            }
            if (dy == 0) {
                // 如果是头部视图或者尾部视图是第一个可见视图，那么清除header
                clearHeader();
            } else {
                mIsPullUp = dy > 0 ? true : false;
                updateOrClearHeader(getFirstVisableItemPostion());
            }
        }
    }

    private void updateOrClearHeader(int firstVisableItemPosition) {

        final int adapterCount = mAdapter == null ? 0 : mAdapter.getItemCount();
        if (adapterCount == 0 || !mAreHeadersSticky || firstVisableItemPosition < 0) {
            clearHeader();
            return;
        }

        updateHeader(firstVisableItemPosition, mAdapter.mDelegate);
    }

    /**
     * 根据第一个可视的item位置，更新header视图
     *
     * @param headerPosition 当前列表第一个可见item的position
     * @param adapter        适配器
     */
    private void updateHeader(int headerPosition, StickyRecyclerHeadersAdapter adapter) {
        // 检查是否有新的标题
        if (mHeaderPosition == null || mHeaderPosition != headerPosition) {
            mHeaderPosition = headerPosition;
            final long headerId = adapter.getHeaderId(headerPosition);
            if (mHeaderId == null || mHeaderId != headerId) {
                mHeaderId = headerId;

                View newHeaderView = adapter.getHeaderView(headerPosition);
                if (mHeader != newHeaderView) {
                    if (newHeaderView == null) {
                        throw new NullPointerException("header may not be null");
                    }
                    swapHeader(newHeaderView);
                }
            }

            ensureHeaderHasCorrectLayoutParams(mHeader);
            measureHeader(mHeader);
            if (mOnStickyHeaderChangedListener != null) {
                mOnStickyHeaderChangedListener.onStickyHeaderChanged(this, mHeader, headerPosition, mHeaderId);
            }
        }

        // 设置header的位置是否需要滚动
        int headerOffset = stickyHeaderTop();
        View view = mRecylerViewLinear.getLayoutManager().findViewByPosition(headerPosition);

        if (view == null) {
            clearHeader();
        } else if (view instanceof WrapperLinearItemView) {
            /**
             * 寻找下一个header位置
             */
            int firstVisablePosition = getFirstVisableItemPostion();
            int lastVisablePosition = getLastVisableItemPostion();

            Log.d(TAG, "first visable position : " + firstVisablePosition + " , " + "last visable position : " + lastVisablePosition);

            // 遍历当前页面，找到下一个拥有header的item位置
            if (firstVisablePosition >= 0 && lastVisablePosition >= 0) {
                for (int index = firstVisablePosition; index <= lastVisablePosition; index++) {
                    View findItemView = mRecylerViewLinear.getLayoutManager().findViewByPosition(index);
                    if (findItemView instanceof WrapperLinearItemView) {
//                        Log.d(TAG, "find item view position : " + index);
                        if (headerPosition > 0 && findItemView.getTop() >= 0 && findItemView.getTop() <= (stickyHeaderTop() + getHeaderMeasuredHeight())
                                && ((WrapperLinearItemView) findItemView).hasHeader()) {
                            Log.d(TAG, "find item view Top : " + findItemView.getTop() + " , measured height : " + getHeaderMeasuredHeight());
                            headerOffset = Math.min(findItemView.getTop() - getHeaderMeasuredHeight(), headerOffset);
                        }
                    }
                }

                Log.d(TAG, "headerOffset : " + headerOffset +" , isPullUp : "+mIsPullUp);
                setHeaderOffet(headerOffset);

//                if(firstVisablePosition == 0 && !mIsPullUp){
//                    clearHeader();
//                }else{
//
//                }
            }

            updateHeaderVisibilities();
        }
    }

    /**
     * 更新头部视图可见性
     */
    private void updateHeaderVisibilities() {
        int top = stickyHeaderTop();
        int childCount = mRecylerViewLinear.getChildCount();
        for (int i = 0; i < childCount; i++) {

            View child = mRecylerViewLinear.getChildAt(i);
            if (!(child instanceof WrapperLinearItemView)) {
                continue;
            }

            WrapperLinearItemView wrapperViewChild = (WrapperLinearItemView) child;
            if (!wrapperViewChild.hasHeader()) {
                continue;
            }

            View childHeader = wrapperViewChild.mHeader;
            if (wrapperViewChild.getTop() < top) {
                if (childHeader.getVisibility() != View.INVISIBLE) {
                    childHeader.setVisibility(View.INVISIBLE);
                }
            } else {
                if (childHeader.getVisibility() != View.VISIBLE) {
                    childHeader.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /*
     * 根据不同版本的API，设置不同方式的偏移量方法
     */
    @SuppressLint("NewApi")
    private void setHeaderOffet(int offset) {
        if (mHeaderOffset == null || mHeaderOffset != offset) {
            mHeaderOffset = offset;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (mHeader != null) {
                    mHeader.setTranslationY(offset);
                }
            } else if (mHeader != null) {
                MarginLayoutParams params = (MarginLayoutParams) mHeader.getLayoutParams();
                params.topMargin = mHeaderOffset;
                mHeader.setLayoutParams(params);
            }
            if (mOnStickyHeaderOffsetChangedListener != null) {
                mOnStickyHeaderOffsetChangedListener.onStickyHeaderOffsetChanged(this, mHeader, -mHeaderOffset);
            }
        }
    }

    private int getHeaderMeasuredHeight() {
        return mHeader == null ? 0 : mHeader.getMeasuredHeight();
    }

    /**
     * 获取当前视图第一个可见的item位置
     *
     * @return
     */
    private int getFirstVisableItemPostion() {
        return (mRecylerViewLinear == null || mAdapter.getItemCount() == 0) ? -1 :
                ((LinearLayoutManager) mRecylerViewLinear.getLayoutManager()).findFirstVisibleItemPosition();
    }

    /**
     * 获取当前视图最后一个可见的item位置
     *
     * @return
     */
    private int getLastVisableItemPostion() {
        return (mRecylerViewLinear == null || mAdapter.getItemCount() == 0) ? -1 :
                ((LinearLayoutManager) mRecylerViewLinear.getLayoutManager()).findLastVisibleItemPosition();
    }

    /**
     * 设置头部的偏移量
     *
     * @param stickyHeaderTopOffset
     */
    public void setStickyHeaderTopOffset(int stickyHeaderTopOffset) {
        mStickyHeaderTopOffset = stickyHeaderTopOffset;
        updateOrClearHeader(getFirstVisableItemPostion());
    }

    /**
     * 计算粘性头部视图距离父视图顶部距离
     * 需要根据mClippingToPadding计算是否加上paddingTop属性
     *
     * @return 距离父视图顶部距离
     */
    private int stickyHeaderTop() {
        return mStickyHeaderTopOffset + (mClippingToPadding ? mPaddingTop : 0);
    }

    private void measureHeader(View header) {
        if (header != null) {
            final int width = getMeasuredWidth() - mPaddingLeft - mPaddingRight;
            final int parentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    width, MeasureSpec.EXACTLY);
            final int parentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
            measureChild(header, parentWidthMeasureSpec,
                    parentHeightMeasureSpec);
        }
    }

    private void swapHeader(View newHeader) {
        if (mHeader != null) {
            removeView(mHeader);
        }
        mHeader = newHeader;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(mPaddingLeft, 0, mPaddingRight, 0);
        addView(mHeader, layoutParams);
        if (mOnHeaderClickListener != null) {
            mHeader.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnHeaderClickListener.onHeaderClick(
                            StickyHeadersRecyleView.this, mHeader,
                            mHeaderPosition, mHeaderId, true);
                }
            });
        }
        mHeader.setClickable(true);
    }

    private void ensureHeaderHasCorrectLayoutParams(View header) {
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            header.setLayoutParams(lp);
        } else if (lp.height == LayoutParams.MATCH_PARENT || lp.width == LayoutParams.WRAP_CONTENT) {
            lp.height = LayoutParams.WRAP_CONTENT;
            lp.width = LayoutParams.MATCH_PARENT;
            header.setLayoutParams(lp);
        }
    }

    private void clearHeader() {
        if (mHeader != null) {
            removeView(mHeader);
            mHeader = null;
            mHeaderId = null;
            mHeaderPosition = null;
            mHeaderOffset = null;

            updateHeaderVisibilities();
        }
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        mOnScrollListenerDelegate = onScrollListener;
    }

    public void setAdapter(@NonNull StickyRecyclerHeadersAdapter adapter) {
        if (adapter == null && mAdapter != null) {
            if (mAdapter instanceof SectionIndexerAdapterWrapper) {
                ((SectionIndexerAdapterWrapper) mAdapter).mSectionIndexerDelegate = null;
            } else {
                mAdapter.mDelegate = null;
            }
            mRecylerViewLinear.setAdapter(null);
            clearHeader();
            return;
        }

        if (adapter instanceof SectionIndexer) {
            mAdapter = new SectionIndexerAdapterWrapper(getContext(), (SectionIndexer) adapter, adapter);
        } else {
            mAdapter = new WrapperLinearRecycleAdapter(getContext(), adapter);
        }

        if (mOnHeaderClickListener != null) {
            mAdapter.setOnHeaderClickListener(new AdapterWrapperHeaderClickHandler());
        } else {
            mAdapter.setOnHeaderClickListener(null);
        }

        mRecylerViewLinear.setAdapter(mAdapter);
        clearHeader();
    }

    private class AdapterWrapperHeaderClickHandler implements
            WrapperLinearRecycleAdapter.OnHeaderClickListener {

        @Override
        public void onHeaderClick(View header, int itemPosition, long headerId) {
            mOnHeaderClickListener.onHeaderClick(
                    StickyHeadersRecyleView.this, header, itemPosition,
                    headerId, false);
        }

    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        mOnHeaderClickListener = listener;
        if (mAdapter != null) {
            if (mOnHeaderClickListener != null) {
                mAdapter.setOnHeaderClickListener(new AdapterWrapperHeaderClickHandler());

                if (mHeader != null) {
                    mHeader.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnHeaderClickListener.onHeaderClick(
                                    StickyHeadersRecyleView.this, mHeader,
                                    mHeaderPosition, mHeaderId, true);
                        }
                    });
                }
            } else {
                mAdapter.setOnHeaderClickListener(null);
            }
        }
    }

    public void setOnStickyHeaderChangedListener(OnStickyHeaderChangedListener listener) {
        mOnStickyHeaderChangedListener = listener;
    }

    public void setOnStickyHeaderOffsetChangedListener(OnStickyHeaderOffsetChangedListener listener) {
        mOnStickyHeaderOffsetChangedListener = listener;
    }

    @Override
    public void setOnTouchListener(final OnTouchListener l) {
        if (l != null) {
            mRecylerViewLinear.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return l.onTouch(StickyHeadersRecyleView.this, event);
                }
            });
        } else {
            mRecylerViewLinear.setOnTouchListener(null);
        }
    }

    /**
     * 对外暴露的头部点击接口
     */
    public interface OnHeaderClickListener {
        /**
         * 头部点击
         *
         * @param l               控件的顶级父视图
         * @param header          被点击的头部视图
         * @param itemPosition    item在RecyclerView.Adapter中的位置
         * @param headerId        点击的头部视图ID
         * @param currentlySticky 当前是否需要粘性头部
         */
        void onHeaderClick(StickyHeadersRecyleView l, View header,
                           int itemPosition, long headerId, boolean currentlySticky);
    }

    /**
     * 对外暴露的粘性头部变化接口
     */
    public interface OnStickyHeaderChangedListener {
        /**
         * @param l            控件的顶级父视图
         * @param header       新的粘性头部视图
         * @param itemPosition 当前产生粘性头部的数据在数据集中的item位置
         * @param headerId     新的粘性头部的ID
         */
        void onStickyHeaderChanged(StickyHeadersRecyleView l, View header,
                                   int itemPosition, long headerId);

    }

    /**
     * 对外暴露的头部位置变化接口
     */
    public interface OnStickyHeaderOffsetChangedListener {
        /**
         * @param l      控件的顶级父视图
         * @param header The currently sticky header being offset.
         *               This header is not guaranteed to have it's measurements set.
         *               It is however guaranteed that this view has been measured,
         *               therefor you should user getMeasured* methods instead of
         *               get* methods for determining the view's size.
         * @param offset 粘性头部距离头部的总距离
         */
        void onStickyHeaderOffsetChanged(StickyHeadersRecyleView l, View header, int offset);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecylerViewLinear.setLayoutManager(layoutManager);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecylerViewLinear.addItemDecoration(itemDecoration);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingTop = top;
        mPaddingRight = right;
        mPaddingBottom = bottom;

        if (mRecylerViewLinear != null) {
            mRecylerViewLinear.setPadding(left, top, right, bottom);
        }
        super.setPadding(0, 0, 0, 0);
        requestLayout();
    }
}
