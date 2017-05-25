package com.eggsy.stickyrecyclerheaders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.eggsy.stickyheader.StickyHeadersRecyleView;
import com.yalantis.phoenix.PullToRefreshView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * 可添加头部和底部的RecyclerView使用
 *
 * @author chenyongkang
 * @Date 2017/5/22 9:03
 */
public class StickyCityActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview_content)
    StickyHeadersRecyleView mRecyclerViewContent;

    @BindView(R.id.ck_clip_padding)
    CheckBox mCkClipPadding;

    @BindView(R.id.ck_sticky_header)
    CheckBox mCkStickyHeader;

    @BindView(R.id.pull_to_refresh)
    PullToRefreshView mPullToRefreshView;

    private StickyCityOptionAdapter mCityOptionAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sticky_city);

        ButterKnife.bind(this);

        initData();

        initView();
    }

    private void initData() {
        mCityOptionAdapter = new StickyCityOptionAdapter(this);
    }

    private void initView() {
        mRecyclerViewContent.setAdapter(mCityOptionAdapter);
        mRecyclerViewContent.setOnHeaderClickListener(new StickyHeadersRecyleView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyHeadersRecyleView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                Toast.makeText(StickyCityActivity.this, "itemPosition " + itemPosition + " Header " + headerId + " currentlySticky ? " + currentlySticky, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerViewContent.setOnItemClickListener(new StickyHeadersRecyleView.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(StickyCityActivity.this, "position " + position, Toast.LENGTH_SHORT).show();
                mRecyclerViewContent.removeItem(position);
            }
        });

        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 2500);
            }
        });

    }

    @OnCheckedChanged(R.id.ck_clip_padding)
    public void clipPaddingChange(CheckBox v, boolean isCheck) {
        mRecyclerViewContent.setClipToPadding(isCheck);
    }

    @OnCheckedChanged(R.id.ck_sticky_header)
    public void stickyHeaderChange(CheckBox v, boolean isCheck) {
        mRecyclerViewContent.setAreHeadersSticky(isCheck);
    }

}
