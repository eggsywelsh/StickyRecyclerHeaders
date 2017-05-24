package com.eggsy.stickyrecyclerheaders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 城市选项适配器
 *
 * @author chenyongkang
 * @Date 2017/5/22 9:31
 */
public class CityOptionAdapter extends RecyclerView.Adapter<CityOptionViewHolder> {

    private final Context mContext;
    private String[] mCountries;
    private LayoutInflater mInflater;

    public CityOptionAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCountries = context.getResources().getStringArray(R.array.countries);
    }

    public CityOptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.city_item, parent, false);
        return new CityOptionViewHolder(view);
    }

    public void onBindViewHolder(CityOptionViewHolder holder, int position) {
        if (position >= 0 && position < mCountries.length && holder != null) {
            holder.mTvCityName.setText(mCountries[position]);
        } else {
            holder.mTvCityName.setText("");
        }
    }

    public int getItemCount() {
        return mCountries.length;
    }
}
