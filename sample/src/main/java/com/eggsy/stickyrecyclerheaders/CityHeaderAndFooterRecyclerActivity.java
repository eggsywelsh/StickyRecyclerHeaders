package com.eggsy.stickyrecyclerheaders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eggsy.stickyheader.WrapperHeaderAndFooterAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 可添加头部和底部的RecyclerView使用
 *
 * @author chenyongkang
 * create at 2017/5/22 9:03
 */
public class CityHeaderAndFooterRecyclerActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview_content)
    RecyclerView mRecyclerViewContent;

    private CityOptionAdapter mCityOptionAdapter;

    private WrapperHeaderAndFooterAdapter mWrapperHeaderAndFooterAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_header_footer);

        ButterKnife.bind(this);

        initData();

        initView();
    }

    private void initData() {
        mCityOptionAdapter = new CityOptionAdapter(this);
        mWrapperHeaderAndFooterAdapter = new WrapperHeaderAndFooterAdapter<>(mCityOptionAdapter);
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        manager.setSmoothScrollbarEnabled(false);
        mRecyclerViewContent.setLayoutManager(manager);
        addHeaderView();
        addFooterView();
        mRecyclerViewContent.setAdapter(mWrapperHeaderAndFooterAdapter);
        mRecyclerViewContent.addItemDecoration(new SpaceItemDecoration(0));
    }

    private void addHeaderView() {
        LinearLayout layoutHeaderOne = (LinearLayout)getLayoutInflater().inflate(R.layout.city_item, mRecyclerViewContent, false);
        ((TextView)layoutHeaderOne.findViewById(R.id.tv_city_name)).setText("header one");
        mWrapperHeaderAndFooterAdapter.addHeaderView(layoutHeaderOne);

        LinearLayout layoutHeaderSecond = (LinearLayout)getLayoutInflater().inflate(R.layout.city_item, mRecyclerViewContent, false);
        ((TextView)layoutHeaderSecond.findViewById(R.id.tv_city_name)).setText("header second");
        mWrapperHeaderAndFooterAdapter.addHeaderView(layoutHeaderSecond);

    }

    private void addFooterView() {
//        TextView tvHeaderOne = (TextView)getLayoutInflater().inflate(R.layout.city_item, null, false);
//        tvHeaderOne.setText("footer one");
//        mWrapperHeaderAndFooterAdapter.addFooterView(tvHeaderOne);
//
//        TextView tvHeaderSecond = (TextView)getLayoutInflater().inflate(R.layout.city_item, null, false);
//        tvHeaderSecond.setText("footer second");
//        mWrapperHeaderAndFooterAdapter.addFooterView(tvHeaderSecond);

        LinearLayout layoutHeaderOne = (LinearLayout)getLayoutInflater().inflate(R.layout.city_item, mRecyclerViewContent, false);
        ((TextView)layoutHeaderOne.findViewById(R.id.tv_city_name)).setText("footer one");
        mWrapperHeaderAndFooterAdapter.addFooterView(layoutHeaderOne);

        LinearLayout layoutHeaderSecond = (LinearLayout)getLayoutInflater().inflate(R.layout.city_item, mRecyclerViewContent, false);
        ((TextView)layoutHeaderSecond.findViewById(R.id.tv_city_name)).setText("footer second");
        mWrapperHeaderAndFooterAdapter.addFooterView(layoutHeaderSecond);
    }


}
