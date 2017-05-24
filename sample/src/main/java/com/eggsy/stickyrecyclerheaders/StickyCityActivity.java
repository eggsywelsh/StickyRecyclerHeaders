package com.eggsy.stickyrecyclerheaders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.eggsy.stickyheader.StickyHeadersRecyleView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 可添加头部和底部的RecyclerView使用
 *
 * @author chenyongkang
 * @Date 2017/5/22 9:03
 */
public class StickyCityActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview_content)
    StickyHeadersRecyleView mRecyclerViewContent;

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
    }



}
