package com.aihuan.main.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aihuan.common.adapter.ViewPagerAdapter;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.ScreenDimenUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.activity.UserHomeNewActivity;
import com.aihuan.one.views.AbsUserHomeViewHolder;
import com.alibaba.fastjson.JSONObject;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saint  2022/2/16 11:45
 * @DESC: 用户主页 -- 新
 */
public class UserHomeViewHolder extends AbsUserHomeViewHolder {
    private static final int PAGE_COUNT = 4;
    private List<FrameLayout> mViewList;
    private AbsUserHomeViewHolder[] mViewHolders;
    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    private UserHomeDetailViewHolder mDetailViewHolder;
    private UserHomeVideoViewHolder mVideoViewHolder;
    private UserHomeAlbumViewHolder mAlumbViewHolder;
    private UserDynamicViewHolder mDynamicViewHolder;
    private String mToUid;
    private TextView mTitle;
    private ImageView mBtnFollow;
    private Drawable mUnFollowDrawable;
    private Drawable mFollowDrawable;
    private ImageView mBtnChat;
    private FrameLayout topContainer;

    public UserHomeViewHolder(Context context, ViewGroup parentView, String toUid) {
        super(context, parentView, toUid);
    }

    @Override
    protected void processArguments(Object... args) {
        mToUid = (String) args[0];
    }


    @Override
    protected int getLayoutId() {
        return R.layout.view_user_home_new;
    }

    @Override
    public void init() {
        View statusView = findViewById(R.id.space_status_bar);
        ScreenDimenUtil.getInstance().setStatusBar(statusView);
        mTitle = (TextView) findViewById(R.id.titleView);
        topContainer = (FrameLayout) findViewById(R.id.top_container);
        mBtnFollow = (ImageView) findViewById(R.id.btn_follow);
        mUnFollowDrawable = ContextCompat.getDrawable(mContext, R.mipmap.o_user_btn_follow_2_0);
        mFollowDrawable = ContextCompat.getDrawable(mContext, R.mipmap.o_user_btn_follow_2_1);
        mBtnChat = (ImageView) findViewById(R.id.btn_chat);
        mViewList = new ArrayList<>();
        for (int i = 0; i < PAGE_COUNT; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
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
                        AbsUserHomeViewHolder vh = mViewHolders[i];
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
        mViewHolders = new AbsUserHomeViewHolder[PAGE_COUNT];
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        final String[] titles = new String[]{WordUtil.getString(R.string.user_detail), WordUtil.getString(R.string.video), WordUtil.getString(R.string.alumb), WordUtil.getString(R.string.dynamic)};
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.gray1));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.textColor));
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(14);
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
                linePagerIndicator.setLineWidth(DpUtil.dp2px(14));
                linePagerIndicator.setLineHeight(DpUtil.dp2px(2));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(1));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.global));
                return linePagerIndicator;
            }

        });
        mIndicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer();
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return DpUtil.dp2px(35);
            }
        });
        ViewPagerHelper.bind(mIndicator, mViewPager);
        UserHomeFirstViewHolder holder = new UserHomeFirstViewHolder(mContext, null, mToUid);
        topContainer.addView(holder.getContentView());
        holder.loadData();
    }

    private void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsUserHomeViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mDetailViewHolder = new UserHomeDetailViewHolder(mContext, parent, mToUid);
                    vh = mDetailViewHolder;
                } else if (position == 1) {
                    mVideoViewHolder = new UserHomeVideoViewHolder(mContext, parent, mToUid);
                    vh = mVideoViewHolder;
                } else if (position == 2) {
                    mAlumbViewHolder = new UserHomeAlbumViewHolder(mContext, parent, mToUid);
                    vh = mAlumbViewHolder;
                } else if (position == 3) {
                    mDynamicViewHolder = new UserDynamicViewHolder(mContext, parent, mToUid, DpUtil.dp2px(106), DpUtil.dp2px(50));
                    vh = mDynamicViewHolder;
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
        JSONObject obj = ((UserHomeNewActivity) mContext).getUserObj();
        if (obj == null) {
            return;
        }
        if (!isFirstLoadData()) {
            return;
        }
        if (mTitle != null) {
            mTitle.setText(obj.getString("user_nickname"));
        }
        setFollow(obj.getIntValue("isattent") == 1);
        boolean openVideo = obj.getIntValue("isvideo") == 1;
        boolean openVoice = obj.getIntValue("isvoice") == 1;
        if (mBtnChat != null) {
            if (!openVideo && openVoice) {
                mBtnChat.setImageResource(R.mipmap.o_user_btn_chat_voice);
            } else if (!openVoice && openVideo) {
                mBtnChat.setImageResource(R.mipmap.o_user_btn_chat_video);
            }
        }
        if (mViewPager != null) {
            loadPageData(mViewPager.getCurrentItem());
        }
    }

    public void setFollow(boolean follow) {
        if (mBtnFollow != null) {
            mBtnFollow.setImageDrawable(follow ? mFollowDrawable : mUnFollowDrawable);
        }
    }

    @Override
    public void setShowed(boolean show) {
        if (mViewPager != null) {
            if (mViewPager.getCurrentItem() == 3) {
                if (mDynamicViewHolder != null) {
                    mDynamicViewHolder.setShowed(show);
                }
            }
        }
    }
}
