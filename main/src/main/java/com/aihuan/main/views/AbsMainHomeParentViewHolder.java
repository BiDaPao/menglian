package com.aihuan.main.views;

import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aihuan.common.adapter.ViewPagerAdapter;
import com.aihuan.common.custom.ScaleTransitionPagerTitleView;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.main.R;
import com.aihuan.main.interfaces.MainAppBarLayoutListener;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/2/20.
 */

public abstract class AbsMainHomeParentViewHolder extends AbsMainViewHolder {

    private AppBarLayout mAppBarLayout;
    protected ViewPager mViewPager;
    private MagicIndicator mIndicator;
    protected AbsMainHomeChildViewHolder[] mViewHolders;
    private MainAppBarLayoutListener mAppBarLayoutListener;
    private boolean mPaused;
    protected List<FrameLayout> mViewList;
    private int mAppLayoutOffestY;

    public AbsMainHomeParentViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mAppBarLayoutListener != null) {
                    if (verticalOffset > mAppLayoutOffestY) {
                        mAppBarLayoutListener.onOffsetChanged(false);
                    } else if (verticalOffset < mAppLayoutOffestY) {
                        mAppBarLayoutListener.onOffsetChanged(true);
                    }
                    mAppLayoutOffestY = verticalOffset;
                }
            }
        });
        mViewList = new ArrayList<>();
        int pageCount = getPageCount();
        for (int i = 0; i < pageCount; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewHolders = new AbsMainHomeChildViewHolder[pageCount];
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        if (pageCount > 1) {
            mViewPager.setOffscreenPageLimit(pageCount - 1);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        final String[] titles = getTitles();
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.textColor));
                simplePagerTitleView.setSelectedColor(0xff1e1e1e);
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(20);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mViewPager != null) {
                            mViewPager.setCurrentItem(index);
                        }
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setLineWidth(DpUtil.dp2px(15));
                linePagerIndicator.setLineHeight(DpUtil.dp2px(3));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.global));
                return linePagerIndicator;
            }
        });
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
    }


    /**
     * 设置AppBarLayout滑动监听
     */
    public void setAppBarLayoutListener(MainAppBarLayoutListener appBarLayoutListener) {
        mAppBarLayoutListener = appBarLayoutListener;
    }


    @Override
    public void loadData() {
        if (mViewPager != null) {
            loadPageData(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowed() && mPaused) {
            loadData();
        }
        mPaused = false;
    }


    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppBarLayoutListener = null;
        super.onDestroy();
    }

    public void setCurrentPage(int position) {
        if (mViewPager == null) {
            return;
        }
        if (mViewPager.getCurrentItem() == position) {
            loadPageData(position);
        } else {
            mViewPager.setCurrentItem(position, false);
        }
    }


    protected abstract void loadPageData(int position);

    protected abstract int getPageCount();

    protected abstract String[] getTitles();


}
