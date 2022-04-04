package com.aihuan.main.views;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.aihuan.common.adapter.ViewPagerAdapter;
import com.aihuan.common.custom.ScaleTransitionPagerTitleView;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.ScreenDimenUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.event.ImUnReadCountEvent;
import com.aihuan.im.event.ImUnReadIntimacyCountEvent;
import com.aihuan.main.R;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/4/1.
 * 首页 消息
 */

public class MainMessageViewHolder extends AbsMainViewHolder {

    private static final int PAGE_COUNT = 2;
    private List<FrameLayout> mViewList;
    private AbsMainViewHolder[] mViewHolders;
    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    private MainMessageMsgViewHolder mMsgViewHolder;
    private MainMessageIntimacyViewHolder intimacyViewHolder;
    private View mRedPoint;

    public MainMessageViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_msg;
    }

    @Override
    public void init() {
        mViewList = new ArrayList<>();
        for (int i = 0; i < PAGE_COUNT; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mRedPoint = findViewById(R.id.view_red_point);
        int left = (ScreenDimenUtil.getInstance().getScreenWdith()) / 4 + DpUtil.dp2px(25);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mRedPoint.getLayoutParams();
        lp.setMargins(left, DpUtil.dp2px(35), 0, 0);
        mRedPoint.setLayoutParams(lp);
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
                if (position == 1) {
                    mRedPoint.setVisibility(View.GONE);
                }
                loadPageData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        final String[] titles = new String[]{
                WordUtil.getString(R.string.im_msg),
                WordUtil.getString(R.string.intimacy)
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
        EventBus.getDefault().register(this);
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
                    mMsgViewHolder = new MainMessageMsgViewHolder(mContext, parent);
                    vh = mMsgViewHolder;
                } else if (position == 1) {
                    intimacyViewHolder = new MainMessageIntimacyViewHolder(mContext, parent);
                    vh = intimacyViewHolder;
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
        new UserStatusP().checkStatus(mContext, () -> {

        });
    }


    @Override
    public void loadData() {
        if (mViewPager != null) {
            loadPageData(mViewPager.getCurrentItem());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadIntimacyCountEvent e) {
        int unReadCount = e.getUnReadCount();
        if (unReadCount > 0) {
            mRedPoint.setVisibility(View.VISIBLE);
        } else {
            mRedPoint.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
