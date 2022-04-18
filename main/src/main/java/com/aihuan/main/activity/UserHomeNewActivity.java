package com.aihuan.main.activity;

import android.Manifest;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;

import android.text.Layout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.adapter.ViewPagerAdapter;
import com.aihuan.common.bean.ChatReceiveGiftBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.event.BlackEvent;
import com.aihuan.common.event.FollowEvent;
import com.aihuan.common.http.CommonHttpConsts;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.http.HttpClient;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.presenter.GiftAnimViewHolder;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.ProcessResultUtil;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.dynamic.activity.AbsDynamicActivity;
import com.aihuan.im.activity.ChatRoomActivity;
import com.aihuan.im.bean.ImMessageBean;
import com.aihuan.im.dialog.ChatGiftDialogFragment;
import com.aihuan.im.http.ImHttpUtil;
import com.aihuan.im.presenter.CheckChatPresenter;
import com.aihuan.main.R;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.aihuan.main.views.UserHomeFirstViewHolder;
import com.aihuan.main.views.UserHomeViewHolder;
import com.aihuan.one.views.AbsUserHomeViewHolder;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/25.
 * 用户主页
 */
@Route(path = RouteUtil.PATH_USER_HOME)
public class UserHomeNewActivity extends AbsDynamicActivity implements ChatGiftDialogFragment.ActionListener {

    private static final int PAGE_COUNT = 1;
    private ViewGroup mRoot;
    private ViewPager mViewPager;
    private List<FrameLayout> mViewList;
    private AbsUserHomeViewHolder[] mViewHolders;
    private UserHomeFirstViewHolder mFirstViewHolder;
    private UserHomeViewHolder mSecondViewHolder;
    private String mToUid;
    private JSONObject mUserObj;
    private UserBean mUserBean;
    private CheckChatPresenter mCheckChatPresenter;
    private ProcessResultUtil mProcessResultUtil;
    private GiftAnimViewHolder mGiftAnimViewHolder;
    private boolean mPaused;
    private View mChooseCallTypeView;
    private PopupWindow mPopupWindow;


    //当前用户和对方亲密度等级
    private int mIntimacyLevel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_home;
    }

    @Override
    protected void main() {
        super.main();
        mRoot = findViewById(R.id.root);
        mToUid = getIntent().getStringExtra(Constants.TO_UID);
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mProcessResultUtil = new ProcessResultUtil(this);
        mViewList = new ArrayList<>();
        for (int i = 0; i < PAGE_COUNT; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewPager = findViewById(R.id.verticalViewPager);
        if (PAGE_COUNT > 1) {
            mViewPager.setOffscreenPageLimit(PAGE_COUNT - 1);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mFirstViewHolder != null) {
                    mFirstViewHolder.setVideoPause(position != 0);
                }
                if (mSecondViewHolder != null) {
                    mSecondViewHolder.setShowed(position == 1);
                }
                loadPageData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewHolders = new AbsUserHomeViewHolder[PAGE_COUNT];
        EventBus.getDefault().register(this);
        getData();
        getIntimacyLevel();
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
//                if (position == 0) {
//                    mFirstViewHolder = new UserHomeFirstViewHolder(mContext, parent, mToUid);
//                    vh = mFirstViewHolder;
//                } else if (position == 1) {
                mSecondViewHolder = new UserHomeViewHolder(mContext, parent, mToUid);
                vh = mSecondViewHolder;
//                }
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

    private void getData() {
        MainHttpUtil.getUserHome(mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    setUserObj(JSON.parseObject(info[0]));
                }
            }
        });
    }

    private boolean assetNullKeyValue(JSONObject obj ,String key ){
      return   !obj.containsKey(key)||obj.get(key)==null||TextUtils.isEmpty(obj.get(key).toString());
    }
    private void setUserObj(JSONObject obj) {
        if (obj == null) {
            return;
        }
        mUserObj = obj;

        if (assetNullKeyValue(obj,"sex")){
            obj.put("sex",-1);
        }
        mUserBean = JSON.toJavaObject(obj, UserBean.class);
        if (mViewPager != null) {
            loadPageData(mViewPager.getCurrentItem());
        }
    }

    public JSONObject getUserObj() {
        return mUserObj;
    }

    public UserBean getUserBean() {
        return mUserBean;
    }


    public void userHomeClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_msg) {
            msgClick();
        } else if (i == R.id.btn_follow) {
            followClick();
        } else if (i == R.id.btn_gift) {
            giftClick();
        } else if (i == R.id.btn_chat) {
            checkPermissions();
        } else if (i == R.id.btn_more) {
            moreClick();
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
                    chatClick();
                }
            }
        });
    }

    /**
     * 点击通话
     */
    private void chatClick() {
        if (mUserObj == null) {
            return;
        }
        if (mIntimacyLevel < 1) {
            ToastUtil.show(R.string.user_home_intimacy_tips);
            return;
        }
        if (mUserObj.getIntValue("isdisturb") == 1) {
            ToastUtil.show(R.string.user_home_disturb);
            return;
        }
        boolean openVideo = mUserObj.getIntValue("isvideo") == 1;
        boolean openVoice = mUserObj.getIntValue("isvoice") == 1;
        if (openVideo && openVoice) {
            String coinName = CommonAppConfig.getInstance().getCoinName();
            chooseCallType(StringUtil.contact(mUserObj.getString("video_value"), coinName),
                    StringUtil.contact(mUserObj.getString("voice_value"), coinName));
        } else if (openVideo) {
            chatAudToAncStart(Constants.CHAT_TYPE_VIDEO);
        } else if (openVoice) {
            chatAudToAncStart(Constants.CHAT_TYPE_VOICE);
        } else {
            ToastUtil.show(R.string.user_home_close_all);
        }

    }

    /**
     * 选择通话类型
     */
    private void chooseCallType(String videoPrice, String voicePrice) {
        if (mChooseCallTypeView == null) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.view_user_home_choose_call, null, false);
            mChooseCallTypeView = v;
            TextView priceVideo = v.findViewById(R.id.price_video);
            TextView priceVoice = v.findViewById(R.id.price_voice);
            priceVideo.setText(String.format(WordUtil.getString(R.string.user_home_price_video), videoPrice));
            priceVoice.setText(String.format(WordUtil.getString(R.string.user_home_price_voice), voicePrice));
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = v.getId();
                    if (i == R.id.btn_video) {
                        chatAudToAncStart(Constants.CHAT_TYPE_VIDEO);
                    } else if (i == R.id.btn_voice) {
                        chatAudToAncStart(Constants.CHAT_TYPE_VOICE);
                    }
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                }
            };
            v.findViewById(R.id.btn_video).setOnClickListener(clickListener);
            v.findViewById(R.id.btn_voice).setOnClickListener(clickListener);
            v.findViewById(R.id.btn_cancel).setOnClickListener(clickListener);
        }
        mPopupWindow = new PopupWindow(mChooseCallTypeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.bottomToTopAnim);
        mPopupWindow.showAtLocation(mRoot, Gravity.BOTTOM, 0, 0);
    }


    /**
     * 点击消息
     */
    private void msgClick() {
        if (mUserObj == null || mUserBean == null) {
            return;
        }
        ChatRoomActivity.forward(mContext, mUserBean, mUserBean.isFollowing(), mUserBean.isBlacking(), true, true);
    }

    /**
     * 点击礼物
     */
    private void giftClick() {
        if (mUserBean == null) {
            return;
        }
        ChatGiftDialogFragment fragment = new ChatGiftDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mUserBean.getId());
        bundle.putString(Constants.CHAT_SESSION_ID, "0");
        fragment.setArguments(bundle);
        fragment.setActionListener(this);
        fragment.show(getSupportFragmentManager(), "ChatGiftDialogFragment");
    }

    /**
     * 点击关注
     */
    private void followClick() {
        getData();
        mFirstViewHolder = new UserHomeFirstViewHolder(mContext,null,mToUid);
//        JSONObject obj = ((UserHomeNewActivity) mContext).getUserObj();
//        Toast.makeText(mContext, "进入方法", Toast.LENGTH_SHORT).show();
        CommonHttpUtil.setAttention(mToUid, null);
//        System.out.println("ssss"+obj.getIntValue("isattent"));
//            mFirstViewHolder.onResume();
    }

    /**
     * 拉黑
     */
    private void moreClick() {
        if (mUserBean == null) {
            return;
        }
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{mUserBean.isBlacking() ? R.string.black_ing : R.string.black}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                CommonHttpUtil.setBlack(mToUid);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (mFirstViewHolder != null) {
            getData();
            mFirstViewHolder.setFollow(e.getIsAttention() == 1);
        }
        if (mSecondViewHolder != null) {
            getData();
            mSecondViewHolder.setFollow(e.getIsAttention() == 1);
        }
        if (mUserBean != null) {
            mUserBean.setAttent(e.getIsAttention());
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBlackEvent(BlackEvent e) {
        if (!TextUtils.isEmpty(mToUid) && mToUid.equals(e.getToUid())) {
            if (mUserBean != null) {
                mUserBean.setIsblack(e.getIsBlack());
            }
        }
    }

    /**
     * 观众向主播发起通话邀请
     *
     * @param type 通话类型
     */
    private void chatAudToAncStart(int type) {
        if (mUserBean == null) {
            return;
        }
        if (mCheckChatPresenter == null) {
            mCheckChatPresenter = new CheckChatPresenter(mContext);
        }
        mCheckChatPresenter.chatAudToAncStart(mToUid, type, mUserBean);
    }

    /**
     * 收到消息的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImMessageBean(ImMessageBean bean) {
        if (mPaused) {
            return;
        }
        if (!TextUtils.isEmpty(mToUid) && mToUid.equals(bean.getUid())) {
            ChatReceiveGiftBean giftBean = bean.getGiftBean();
            if (giftBean != null) {
                showGift(bean.getGiftBean());
            }
        }
    }


    /**
     * 显示礼物动画
     */
    public void showGift(ChatReceiveGiftBean bean) {
        if (mGiftAnimViewHolder == null) {
            mGiftAnimViewHolder = new GiftAnimViewHolder(mContext, mRoot);
            mGiftAnimViewHolder.addToParent();
        }
        mGiftAnimViewHolder.showGiftAnim(bean);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
    }

    @Override
    public void onDestroy() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        mPopupWindow = null;
        EventBus.getDefault().unregister(this);
        MainHttpUtil.cancel(MainHttpConsts.GET_USER_HOME);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_BLACK);
        if (mCheckChatPresenter != null) {
            mCheckChatPresenter.cancel();
        }
        mCheckChatPresenter = null;
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        mProcessResultUtil = null;
        if (mGiftAnimViewHolder != null) {
            mGiftAnimViewHolder.release();
        }
        mGiftAnimViewHolder = null;
        super.onDestroy();
    }


    @Override
    public void onChargeClick() {
        RouteUtil.forwardMyCoin();
    }


    @Override
    public void onBackPressed() {
        if (isBackVideo()) {
            videoBack();
        } else {
            super.onBackPressed();
        }
    }


    /**
     * 获取当前用户和对方的亲密度等级
     */
    private void getIntimacyLevel() {
        if (android.text.TextUtils.isEmpty(mToUid) || android.text.TextUtils.equals("admin", mToUid)) {
            return;
        }
        ImHttpUtil.getIntimacyLevel(mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mIntimacyLevel = obj.getIntValue("intimacy_level");
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }
}
