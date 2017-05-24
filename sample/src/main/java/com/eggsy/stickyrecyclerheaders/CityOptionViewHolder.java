package com.eggsy.stickyrecyclerheaders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author chenyongkang
 * @Date 2017/5/22 9:33
 */
public class CityOptionViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.tv_city_name)
    TextView mTvCityName;

    public CityOptionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }


}
