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
     * 头部的包装类，用于在RecyclerView有设置mPaddingTop的时候，并且clipToPadding为true时使用
     */
//    private WrapperHeaderView mWrapperHeaderView;

    /**
     * 当前头部对应的ID
     */
    private Long mHeaderId;
    /**
     * 当前可见的头部位置，如果是系统带有paddingTop
     * 默认会识别到paddingTop下面的第一个可见位置为当前值
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
     * RecyclerView的线性布局
     */
    LinearLayoutManager mLinearLayoutManager;

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
     * 滚动条样式
     */
    private int mScrollbarStyle;

    /**
     * 触摸处理
     */
    private float mTouchSlop;

    /**
     * 是否用户向上拉取
     */
    private boolean mIsPullUp;

    /**
     * 外部监听类
     */
    private OnHeaderClickListener mOnHeaderClickListener;
    private OnStickyHeaderOffsetChangedListener mOnStickyHeaderOffsetChangedListener;
    private OnStickyHeaderChangedListener mOnStickyHeaderChangedListener;
    //    private AdapterWrapperDataSetObserver mDataSetObserver;

    public StickyHeadersRecyleView(@NonNull Context context) {
        this(context, null);
    }

    public StickyHeadersRecyleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
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

                    mScrollbarStyle = a.getInt(R.styleable.StickyHeadersRecyleView_android_scrollbarStyle, View.SCROLLBARS_OUTSIDE_OVERLAY);
                    if (mScrollbarStyle == View.SCROLLBARS_INSIDE_INSET) {
                        mRecylerViewLinear.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                    } else if (mScrollbarStyle == View.SCROLLBARS_INSIDE_OVERLAY) {
                        mRecylerViewLinear.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    } else if (mScrollbarStyle == View.SCROLLBARS_OUTSIDE_INSET) {
                        mRecylerViewLinear.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
                    } else if (mScrollbarStyle == View.SCROLLBARS_OUTSIDE_OVERLAY) {
                        mRecylerViewLinear.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
                    }

                    // 滚动条
                    final int scrollBars = a.getInt(R.styleable.StickyHeadersRecyleView_android_scrollbars, 0x00000000);
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
            addView(mRecylerViewLinear, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        mLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//        manager.setSmoothScrollbarEnabled(false);
        mRecylerViewLinear.setLayoutManager(mLinearLayoutManager);
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
//            if (dy == 0) {
//                // 如果是头部视图或者尾部视图是第一个可见视图，那么清除header
//                clearHeader();
//            } else {
            mIsPullUp = dy > 0 ? true : false;
            updateOrClearHeader(getFirstVisableItemPostion());
//            }
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
//        Log.d(TAG, "translation Y " + (mHeader != null ? mHeader.getTranslationY() : "null"));

        // 检查是否有新的标题
        if (mHeaderPosition == null || mHeaderPosition != headerPosition) {
            mHeaderPosition = headerPosition;
//            View firstVisableItemView = mRecylerViewLinear.getLayoutManager().findViewByPosition(headerPosition);
            final long headerId = adapter.getHeaderId(headerPosition);
            if ((mHeaderId == null || mHeaderId != headerId)) {
                mHeaderId = headerId;

                View newHeaderView = adapter.getHeaderView(headerPosition);
                if (mHeader != newHeaderView) {
                    if (newHeaderView == null) {
                        throw new NullPointerException("header may not be null");
                    }
                    swapHeader(newHeaderView);
                }
            }
            if (mHeader != null) {
                ensureHeaderHasCorrectLayoutParams(mHeader);
                measureHeader(mHeader);
                if (mOnStickyHeaderChangedListener != null) {
                    mOnStickyHeaderChangedListener.onStickyHeaderChanged(this, mHeader, headerPosition, mHeaderId);
                }
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

            // 遍历当前页面，找到下一个拥有header的item位置
            if (firstVisablePosition >= 0 && lastVisablePosition >= 0) {
//                Log.d(TAG, "firstVisablePosition " + firstVisablePosition + " , lastVisablePosition " + lastVisablePosition);
                for (int index = firstVisablePosition; index <= lastVisablePosition; index++) {
                    View findItemView = mRecylerViewLinear.getLayoutManager().findViewByPosition(index);
                    if (findItemView instanceof WrapperLinearItemView) {
                        if (headerPosition > 0 && findItemView.getTop() >= 0 && findItemView.getTop() <= (stickyHeaderTop() + getHeaderMeasuredHeight())
                                && ((WrapperLinearItemView) findItemView).hasHeader()) {
                            headerOffset = Math.min(findItemView.getTop() - getHeaderMeasuredHeight(), headerOffset);
                            break;
                        } else if (headerPosition == 0) {
                            if (findItemView.getTop() > stickyHeaderTop()) {
                                clearHeader();
                            }
                            break;
                        }
                    }
                }
//                Log.d(TAG, "headerOffset " + headerOffset);
                setHeaderOffet(headerOffset);
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
            break;
        }
    }

    /**
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
        addView(mHeader);

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

        mAdapter.setOnHeaderClickListener(mOnHeaderClickListener != null ? new AdapterWrapperHeaderClickHandler() : null);

        mRecylerViewLinear.setAdapter(mAdapter);
        clearHeader();
    }

    private class AdapterWrapperHeaderClickHandler implements
            WrapperLinearRecycleAdapter.OnHeaderClickListener {

        @Override
        public void onHeaderClick(View header, int itemPosition, long headerId) {
            if (mOnHeaderClickListener != null) {
                mOnHeaderClickListener.onHeaderClick(
                        StickyHeadersRecyleView.this, header, itemPosition,
                        headerId, false);
            }
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

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mAdapter.setOnItemClickListener(listener);
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

    @Override
    public void setClipToPadding(boolean clipToPadding) {
        if (mRecylerViewLinear != null) {
            mRecylerViewLinear.setClipToPadding(clipToPadding);
        }
        mClippingToPadding = clipToPadding;
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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mRecylerViewLinear.layout(0, 0, mRecylerViewLinear.getMeasuredWidth(), getHeight());
        if (mHeader != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mHeader.getLayoutParams();
            int headerTop = lp.topMargin;
            mHeader.layout(mPaddingLeft, headerTop, mHeader.getMeasuredWidth() - mPaddingLeft
                    , headerTop + mHeader.getMeasuredHeight());
        }
    }

    /**
     * 设置是否启用粘性头部
     *
     * @param areHeadersSticky
     */
    public void setAreHeadersSticky(boolean areHeadersSticky) {
        mAreHeadersSticky = areHeadersSticky;
        if (!areHeadersSticky) {
            clearHeader();
        } else {
            updateOrClearHeader(getFirstVisableItemPostion());
        }
        mRecylerViewLinear.invalidate();
    }

    public void removeItem(int position) {
        RecyclerView.ViewHolder holder = mRecylerViewLinear.findViewHolderForLayoutPosition(position);
        boolean positionHasHeader = false;
        WrapperLinearItemView wrapperLinearView = null;
//        holder.get
        if (holder != null && holder.itemView instanceof WrapperLinearItemView) {
            wrapperLinearView = (WrapperLinearItemView) holder.itemView;
            if (wrapperLinearView != null && wrapperLinearView.hasHeader()) {
                positionHasHeader = true;
            }
        }
        // 删除数据和界面上的item
        mAdapter.itemRemoveWithData(position);

        // 刷新数据
        if (positionHasHeader) {
            mAdapter.notifyItemChanged(position);
        }
        /**
         * 用于滑动到底部时，进行删除操作，需要刷新头部的显示与否
         */
        updateOrClearHeader(getFirstVisableItemPostion());
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
     * 主动绑定的点击事件
     */
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean isDispatch = super.dispatchTouchEvent(ev);
//        Log.d(TAG, "Touch[dispatchTouchEvent][StickyHeadersRecyleView] " + isDispatch);
        return isDispatch;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean isIntercept = super.onInterceptTouchEvent(event);
//        Log.d(TAG, "Touch[onInterceptTouchEvent][StickyHeadersRecyleView] " + isIntercept);
        Log.d(TAG, "Touch[Action][StickyHeadersRecyleView] " + event.getAction());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getParent() != null) {
                    if (mLinearLayoutManager != null && mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                        if (getParent() != null) {
                            Log.d(TAG, "Touch request disallow interceptTouchEvent ");
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                if (getParent() != null) {
//                    Log.d(TAG, "Touch request disallow interceptTouchEvent ");
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                }
                break;
            case MotionEvent.ACTION_UP:
                if (mLinearLayoutManager != null && mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    if (getParent() != null) {
                        Log.d(TAG, "Touch request cancel interceptTouchEvent ");
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                break;
        }

        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isTouch = super.dispatchTouchEvent(event);
//        Log.d(TAG, "Touch[onTouchEvent][StickyHeadersRecyleView] " + isTouch);
        return isTouch;
    }


//    @Override
//    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//        super.requestDisallowInterceptTouchEvent(disallowIntercept);
//    }

}
