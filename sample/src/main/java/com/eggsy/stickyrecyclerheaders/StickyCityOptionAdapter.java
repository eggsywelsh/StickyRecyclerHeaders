package com.eggsy.stickyrecyclerheaders;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.eggsy.stickyheader.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;

/**
 * @author chenyongkang
 * @Date 2017/5/22 16:06
 */
public class StickyCityOptionAdapter extends StickyRecyclerHeadersAdapter<CityOptionViewHolder> implements SectionIndexer {

    private final Context mContext;
    private String[] mCountries;
    private LayoutInflater mInflater;
    private int[] mSectionIndices;
    private Character[] mSectionLetters;

    public StickyCityOptionAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCountries = context.getResources().getStringArray(R.array.countries);
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
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
    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public View getHeaderView(int position) {
        View convertView = mInflater.inflate(R.layout.city_item, null, false);

        CharSequence headerChar = mCountries[position].subSequence(0, 1);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
        }else{
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent,null));
        }
        ((TextView)convertView.findViewById(R.id.tv_city_name)).setText(headerChar);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return getSectionForPosition(position);
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<>();
        char lastFirstChar = mCountries[0].charAt(0);
        sectionIndices.add(0);
        for (int i = 1; i < mCountries.length; i++) {
            if (mCountries[i].charAt(0) != lastFirstChar) {
                lastFirstChar = mCountries[i].charAt(0);
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }

    private Character[] getSectionLetters() {
        Character[] letters = new Character[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++) {
            letters[i] = mCountries[mSectionIndices[i]].charAt(0);
        }
        return letters;
    }
}
