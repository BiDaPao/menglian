package com.aihuan.main.views;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aihuan.common.adapter.ViewPagerAdapter;
import com.aihuan.common.event.FollowEvent;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 排行
 */

public class MainListViewHolder extends AbsMainViewHolder {
    private static final int PAGE_COUNT = 2;
    private List<FrameLayout> mViewList;
    private AbsMainListChildViewHolder[] mViewHolders;
    private MainListProfitViewHolder mProfitViewHolder;//收益榜
    private MainListContributeViewHolder mContributeViewHolder;//贡献榜
    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    private int mDefaultPos;
    public MainListViewHolder(Context context, ViewGroup parentView,int defaultPos) {
        super(context, parentView,defaultPos);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_main_list;
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        mDefaultPos= (int) args[0];
    }

    @Override
    public void init() {
        mViewList = new ArrayList<>();
        for (int i = 0; i < PAGE_COUNT; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewHolders = new AbsMainListChildViewHolder[PAGE_COUNT];
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(PAGE_COUNT - 1);
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
        final String[] titles = new String[]{
                WordUtil.getString(R.string.consume_rank),
                WordUtil.getString(R.string.profit_rank)
        };

        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(0xff969696);
                simplePagerTitleView.setSelectedColor(0xff323232);
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.getPaint().setFakeBoldText(true);
                simplePagerTitleView.setPadding(DpUtil.dp2px(20),0,DpUtil.dp2px(20),0);
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
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setXOffset(DpUtil.dp2px(13));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.global));
                return linePagerIndicator;
            }
        });
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
        mViewPager.setCurrentItem(mDefaultPos);
        EventBus.getDefault().register(MainListViewHolder.this);
    }

    private void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsMainListChildViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mContributeViewHolder = new MainListContributeViewHolder(mContext, parent);
                    vh = mContributeViewHolder;
                } else if (position == 1) {
                    mProfitViewHolder = new MainListProfitViewHolder(mContext, parent);
                    vh = mProfitViewHolder;
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
        if (mViewPager != null) {
            loadPageData(mViewPager.getCurrentItem());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MainListViewHolder.this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (mViewHolders != null) {
            for (AbsMainListChildViewHolder vh : mViewHolders) {
                if (vh != null) {
                    vh.onFollowEvent(e.getToUid(), e.getIsAttention());
                }
            }
        }
    }

}
