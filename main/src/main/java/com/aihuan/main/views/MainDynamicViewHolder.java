package com.aihuan.main.views;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.adapter.ViewPagerAdapter;
import com.aihuan.common.custom.ScaleTransitionPagerTitleView;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.dynamic.event.DynamicTabChangeEvent;
import com.aihuan.main.R;
import com.aihuan.main.presenter.OnCheckBack;
import com.aihuan.main.presenter.UserStatusP;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 动态
 */

public class MainDynamicViewHolder extends AbsMainViewHolder {
    private static final int PAGE_COUNT = 2;
    private List<FrameLayout> mViewList;
    private AbsMainViewHolder[] mViewHolders;
    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    //private MainParentDynamicViewHolder mMainParentDynamicViewHolder;
    private MainItemDynamicViewHolder mNewDynamicViewHolder;
    private MainItemDynamicViewHolder mAttenDynamicViewHolder;
    private View mBtnPublish;


    public MainDynamicViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_dynamic;
    }

    @Override
    public void init() {
        mViewList = new ArrayList<>();
        for (int i = 0; i < PAGE_COUNT; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewHolders = new AbsMainViewHolder[PAGE_COUNT];
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mBtnPublish = findViewById(R.id.btn_to_publish);
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
                for (int i = 0, length = mViewHolders.length; i < length; i++) {
                    AbsMainViewHolder vh = mViewHolders[i];
                    if (vh != null) {
                        vh.setShowed(position == i);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        final String[] titles = new String[]{
                WordUtil.getString(R.string.newest),
                WordUtil.getString(R.string.atten)
        };
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
        EventBus.getDefault().post(new DynamicTabChangeEvent(position));
        if (vh != null) {
            vh.loadData();
        }
        new UserStatusP().checkStatus(mContext, () -> {

        });
    }



    @Override
    public void setShowed(boolean showed) {
        super.setShowed(showed);
        L.e("---showedshowed--" + showed);
        if (mViewHolders != null && mViewPager != null && mViewHolders.length > 1) {
            mViewHolders[mViewPager.getCurrentItem()].setShowed(showed);
        }
//        if (showed) {
//            if (CommonAppConfig.getInstance().getUserBean().isAlAuth()) {
//                if (mBtnPublish != null) {
//                    mBtnPublish.setVisibility(View.VISIBLE);
//                }
//            }
//        }
    }

    @Override
    public void loadData() {
        if (mViewPager != null) {
            loadPageData(mViewPager.getCurrentItem());
        }
    }
}
