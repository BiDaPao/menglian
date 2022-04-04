package com.aihuan.main.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aihuan.common.event.MatchEvent;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.adapter.ViewPagerAdapter;
import com.aihuan.common.bean.ChatAnchorParam;
import com.aihuan.common.bean.ChatAudienceParam;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.custom.TabButtonGroup;
import com.aihuan.common.http.CommonHttpConsts;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.LocationUtil;
import com.aihuan.common.utils.ProcessResultUtil;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.ScreenDimenUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.VersionUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.dynamic.activity.AbsDynamicActivity;
import com.aihuan.dynamic.activity.DynamicPublishActivity;
import com.aihuan.im.event.ImUnReadCountEvent;
import com.aihuan.im.utils.ImMessageUtil;
import com.aihuan.main.R;
import com.aihuan.main.dialog.AgentDialogFragment;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.aihuan.main.interfaces.MainAppBarLayoutListener;
import com.aihuan.main.views.AbsMainViewHolder;
import com.aihuan.main.views.MainDynamicViewHolder;
import com.aihuan.main.views.MainFindViewHolder;
import com.aihuan.main.views.MainHomeViewHolder;
import com.aihuan.main.views.MainMeViewHolder;
import com.aihuan.main.views.MainMessageViewHolder;
import com.aihuan.video.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

@Route(path = RouteUtil.PATH_MAIN)
public class MainActivity extends AbsDynamicActivity implements MainAppBarLayoutListener {

    private TabButtonGroup mTabButtonGroup;
    private ViewPager mViewPager;
    private TextView mRedPoint;
    private List<FrameLayout> mViewList;
    private MainHomeViewHolder mHomeViewHolder;
    private MainDynamicViewHolder mMainDynamicViewHolder;
    private MainFindViewHolder mMainFindViewHolder;
    private MainMessageViewHolder mMessageViewHolder;
    private MainMeViewHolder mMeViewHolder;
    private AbsMainViewHolder[] mViewHolders;
    private View mBottom;
    private int mDp70;
//    private ObjectAnimator mUpAnimator;//向上动画
//    private ObjectAnimator mDownAnimator;//向下动画
//    private boolean mAnimating;
    private boolean mShowed = true;
    private boolean mHided;
    private ProcessResultUtil mProcessResultUtil;
    private boolean mFristLoad;
    private long mLastClickBackTime;//上次点击back键的时间
    private boolean mFirstLogin;
    private int matchType = 1;//匹配类型
//    private RotateAnimation mRotateAnimation;
//    private ScaleAnimation mScaleAnimation;
//    private RotateAnimation mCenterRotateAnimation;
//    private View mBtnMatchBg;
//    private View mBtnMatchCenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void main() {
        super.main();
        Intent intent = getIntent();
        mFirstLogin = intent.getBooleanExtra(Constants.FIRST_LOGIN, false);
        mTabButtonGroup = (TabButtonGroup) findViewById(R.id.tab_group);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
//        //匹配按钮
//        mBtnMatchBg = findViewById(R.id.bg);
//        mBtnMatchCenter = findViewById(R.id.center);
        mViewPager.setOffscreenPageLimit(5);
        mViewList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position, true);
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
        mTabButtonGroup.setViewPager(mViewPager);
        mViewHolders = new AbsMainViewHolder[5];
        mDp70 = DpUtil.dp2px(70);
        mBottom = findViewById(R.id.bottom);
        mRedPoint = findViewById(R.id.red_point);
//        mUpAnimator = ObjectAnimator.ofFloat(mBottom, "translationY", mDp70, 0);
//        mDownAnimator = ObjectAnimator.ofFloat(mBottom, "translationY", 0, mDp70);
//        mUpAnimator.setDuration(250);
//        mDownAnimator.setDuration(250);
//        mUpAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                mAnimating = true;
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mAnimating = false;
//                mShowed = true;
//                mHided = false;
//            }
//        });
//        mDownAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                mAnimating = true;
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mAnimating = false;
//                mShowed = false;
//                mHided = true;
//            }
//        });
        mProcessResultUtil = new ProcessResultUtil(this);
        EventBus.getDefault().register(this);
        checkVersion();
        checkAgent();
        CommonAppConfig.getInstance().setLaunched(true);
        CommonAppConfig.getInstance().setLaunchTime(System.currentTimeMillis() / 1000);
        mFristLoad = true;

        int chatParamType = intent.getIntExtra(Constants.CHAT_PARAM_TYPE, 0);
        if (chatParamType == Constants.CHAT_PARAM_TYPE_ANC) {
            ChatAnchorParam param = intent.getParcelableExtra(Constants.CHAT_PARAM_ANC);
            if (param != null) {
                RouteUtil.forwardAnchorActivity(param);
            }
        } else if (chatParamType == Constants.CHAT_PARAM_TYPE_AUD) {
            ChatAudienceParam param = intent.getParcelableExtra(Constants.CHAT_PARAM_AUD);
            if (param != null) {
                RouteUtil.forwardAudienceActivity(param);
            }
        }
        int left = (ScreenDimenUtil.getInstance().getScreenWdith()) / 5 + DpUtil.dp2px(3) + ScreenDimenUtil.getInstance().getScreenWdith() / 2;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRedPoint.getLayoutParams();
        lp.setMargins(left, DpUtil.dp2px(4), 0, 0);
        mRedPoint.setLayoutParams(lp);

//        mRotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        mRotateAnimation.setInterpolator(new LinearInterpolator());
//        mRotateAnimation.setDuration(3000);
//        mRotateAnimation.setRepeatCount(-1);
//        mBtnMatchBg.startAnimation(mRotateAnimation);
//        mScaleAnimation = new ScaleAnimation(1, 0.7f, 1, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f, 1, 0.7f);
//        mScaleAnimation.setInterpolator(new LinearInterpolator());
//        mScaleAnimation.setDuration(500);
//        mScaleAnimation.setRepeatCount(0);
//        mScaleAnimation.setRepeatMode(Animation.REVERSE);
//        mBtnMatchCenter.startAnimation(mScaleAnimation);
//        mCenterRotateAnimation = new RotateAnimation(-20, 20, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        mCenterRotateAnimation.setInterpolator(new LinearInterpolator());
//        mCenterRotateAnimation.setDuration(500);
//        mCenterRotateAnimation.setRepeatCount(0);
//        mCenterRotateAnimation.setRepeatMode(Animation.REVERSE);
//        mScaleAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (mCenterRotateAnimation != null) {
//                    mBtnMatchCenter.startAnimation(mCenterRotateAnimation);
//                }
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        mCenterRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (mScaleAnimation != null) {
//                    mBtnMatchCenter.startAnimation(mScaleAnimation);
//                }
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

    }

    public void mainClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_search) {
            SearchActivity.forward(mContext);
        } else if (i == R.id.btn_to_publish) {
            startActivity(new Intent(mContext, DynamicPublishActivity.class));
        }
    }

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (configBean.getMaintainSwitch() == 1) {//开启维护
                        DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.main_maintain_notice), configBean.getMaintainTips());
                    }
                    if (!VersionUtil.isLatest(configBean.getVersion())) {
                        VersionUtil.showDialog(mContext, configBean.getUpdateDes(), configBean.getDownloadApkUrl());
                    }
                }
            }
        });
    }

    /**
     * 检查是否要弹邀请码的弹窗
     */
    private void checkAgent() {
        MainHttpUtil.checkAgent(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (mFirstLogin || obj.getIntValue("isfill") == 0) {
                        AgentDialogFragment fragment = new AgentDialogFragment();
                        fragment.setCancelable(obj.getIntValue("ismust") == 0);
                        fragment.show(getSupportFragmentManager(), "AgentDialogFragment");
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mFristLoad) {
            mFristLoad = false;
            checkPermissions();
            loadPageData(0, false);
            if (mHomeViewHolder != null) {
                mHomeViewHolder.setShowed(true);
            }
            if (mHomeViewHolder != null) {
                mHomeViewHolder.setCurrentPage(0);
            }
        }
    }

    /**
     * 检查权限
     */
    private void checkPermissions() {
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
        }, new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    loginIM();
                    getLocation();
                }
            }
        });

    }

    /**
     * 获取所在位置, 启动定位
     */
    private void getLocation() {
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
        }, new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    LocationUtil.getInstance().startLocation();
                }
            }
        });
    }


    /**
     * 登录IM
     */
    private void loginIM() {
        String uid = CommonAppConfig.getInstance().getUid();
        ImMessageUtil.getInstance().loginImClient(uid);
    }


    @Override
    protected void onDestroy() {
        if (mTabButtonGroup != null) {
            mTabButtonGroup.cancelAnim();
        }
        EventBus.getDefault().unregister(this);
        MainHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        MainHttpUtil.cancel(MainHttpConsts.CHECK_AGENT);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_LOCAITON);
        LocationUtil.getInstance().stopLocation();
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
//        if (mUpAnimator != null) {
//            mUpAnimator.cancel();
//            mUpAnimator.removeAllListeners();
//        }
//        mUpAnimator = null;
//        if (mDownAnimator != null) {
//            mDownAnimator.cancel();
//            mDownAnimator.removeAllListeners();
//        }
//        mUpAnimator = null;
//        if (mBtnMatchCenter != null && mScaleAnimation != null) {
//            mScaleAnimation.cancel();
//            mScaleAnimation.setAnimationListener(null);
//            mBtnMatchCenter.clearAnimation();
//        }
//        mScaleAnimation = null;
//        if (mCenterRotateAnimation != null && mBtnMatchCenter != null) {
//            mCenterRotateAnimation.cancel();
//            mCenterRotateAnimation.setAnimationListener(null);
//            mBtnMatchCenter.clearAnimation();
//        }
//        mCenterRotateAnimation = null;
//        if (mRotateAnimation != null && mBtnMatchBg != null) {
//            mRotateAnimation.cancel();
//            mRotateAnimation.setAnimationListener(null);
//            mBtnMatchBg.clearAnimation();
//        }
//        mRotateAnimation = null;
        CommonAppConfig.getInstance().setLaunched(false);
        VideoStorge.getInstance().clear();
        super.onDestroy();
    }

    public static void forward(Context context) {
        forward(context, false);
    }

    public static void forward(Context context, boolean firstLogin) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.FIRST_LOGIN, firstLogin);
        context.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        String unReadCount = e.getUnReadCount();
        if (!TextUtils.isEmpty(unReadCount)) {
            setUnReadCount(unReadCount);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMatchEvent(MatchEvent event) {
        matchType = event.getType();
        mTabButtonGroup.setCurPosition(2);
    }

    /**
     * 显示未读消息
     */
    private void setUnReadCount(String unReadCount) {
        if (mRedPoint != null) {
            if ("0".equals(unReadCount)) {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mRedPoint.getVisibility() != View.VISIBLE) {
                    mRedPoint.setVisibility(View.VISIBLE);
                }
            }
            mRedPoint.setText(unReadCount);
        }
    }

    @Override
    public void onBackPressed() {
        if (isBackVideo()) {
            videoBack();
        } else {
            long curTime = System.currentTimeMillis();
            if (curTime - mLastClickBackTime > 2000) {
                mLastClickBackTime = curTime;
                ToastUtil.show(R.string.main_click_next_exit);
                return;
            }
            super.onBackPressed();
        }
    }


    private void loadPageData(int position, boolean needlLoadData) {
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
                    mHomeViewHolder = new MainHomeViewHolder(mContext, parent);
                    mHomeViewHolder.setAppBarLayoutListener(this);
                    vh = mHomeViewHolder;
                } else if (position == 1) {
                    mMainDynamicViewHolder = new MainDynamicViewHolder(mContext, parent);
                    vh = mMainDynamicViewHolder;
                } else if (position == 2) {
                    mMainFindViewHolder = new MainFindViewHolder(mContext, parent);
                    vh = mMainFindViewHolder;
                } else if (position == 3) {
                    mMessageViewHolder = new MainMessageViewHolder(mContext, parent);
                    vh = mMessageViewHolder;
                } else if (position == 4) {
                    mMeViewHolder = new MainMeViewHolder(mContext, parent);
                    vh = mMeViewHolder;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (position == 2) {
            mMainFindViewHolder.matchType(matchType);
        }
        if (needlLoadData && vh != null) {
            vh.loadData();
        }
    }

    @Override
    public void onOffsetChanged(boolean up) {
//        if (!mAnimating) {
//            if (up) {
//                if (mShowed && mDownAnimator != null) {
//                    mDownAnimator.start();
//                }
//            } else {
//                if (mHided && mUpAnimator != null) {
//                    mUpAnimator.start();
//                }
//            }
//        }
    }

    /**
     * 进入个人主页
     */
    public void forwardUserHome(String toUid) {
        RouteUtil.forwardUserHome(toUid);
    }
}
