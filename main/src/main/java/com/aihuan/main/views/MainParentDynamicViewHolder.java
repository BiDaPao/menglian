package com.aihuan.main.views;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aihuan.common.adapter.ViewPagerAdapter;
import com.aihuan.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 动态
 */

public class MainParentDynamicViewHolder extends AbsMainViewHolder implements View.OnClickListener {

    private static final int PAGE_COUNT = 2;
    private List<FrameLayout> mViewList;
    private AbsMainViewHolder[] mViewHolders;
    private ViewPager mViewPager;
    private MainItemDynamicViewHolder mNewDynamicViewHolder;
    private MainItemDynamicViewHolder mAttenDynamicViewHolder;

    public MainParentDynamicViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_parent_dynamic;
    }

    @Override
    public void init() {
        mViewList = new ArrayList<>();
        findViewById(R.id.btn_new).setOnClickListener(this);
        findViewById(R.id.btn_atten).setOnClickListener(this);
        for (int i = 0; i < PAGE_COUNT; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewHolders = new AbsMainViewHolder[PAGE_COUNT];
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        if (PAGE_COUNT > 1) {
            mViewPager.setOffscreenPageLimit(PAGE_COUNT - 1);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position);
                if (mViewHolders != null) {
                    for (int i = 0, length = mViewHolders.length; i < length; i++) {
                        AbsMainViewHolder vh = mViewHolders[i];
                        if (vh != null) {
                            vh.setShowed(position == i);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    private void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsMainViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mNewDynamicViewHolder = new MainItemDynamicViewHolder(mContext, parent, MainItemDynamicViewHolder.NEW);
                    vh = mNewDynamicViewHolder;
                } else if (position == 1) {
                    mAttenDynamicViewHolder = new MainItemDynamicViewHolder(mContext, parent, MainItemDynamicViewHolder.ATTEN);
                    vh = mAttenDynamicViewHolder;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (vh != null) {
            vh.loadData();
        }
    }


    @Override
    public void loadData() {
        if (isFirstLoadData()) {
            if (mViewPager != null) {
                loadPageData(mViewPager.getCurrentItem());
            }
        }
    }


    @Override
    public void setShowed(boolean showed) {
        super.setShowed(showed);
        if (mViewHolders != null && mViewPager != null) {
            mViewHolders[mViewPager.getCurrentItem()].setShowed(showed);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_new) {
            if (mViewPager != null) {
                mViewPager.setCurrentItem(0);
            }
        } else if (i == R.id.btn_atten) {
            if (mViewPager != null) {
                mViewPager.setCurrentItem(1);
            }
        }
    }
}
