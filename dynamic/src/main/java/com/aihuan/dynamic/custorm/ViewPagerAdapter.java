package com.aihuan.dynamic.custorm;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.aihuan.common.utils.L;

import java.util.List;

/**
 * Created by debug on 2019/8/1.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private List<? extends View> mViewList;

    public ViewPagerAdapter(List<? extends View> list) {
        mViewList = list;
    }

    public int  delItem(int pos, ViewPager viewPager){
        destroyItem(viewPager,pos,null);
        mViewList.remove(pos);
        notifyDataSetChanged();
        return mViewList.size();
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = mViewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        L.e("--position---"+position);
        try {
            container.removeView(mViewList.get(position));
        }catch (Exception e){
        }
    }
    @Override
    public int getItemPosition(@NonNull Object object) {
    //        return super.getItemPosition(object);
        return POSITION_NONE;
    }
}
