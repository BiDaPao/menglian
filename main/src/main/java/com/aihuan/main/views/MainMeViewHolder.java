package com.aihuan.main.views;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.activity.WebViewActivity;
import com.aihuan.common.bean.ChatPriceBean;
import com.aihuan.common.bean.LevelBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.bean.UserItemBean;
import com.aihuan.common.dialog.MainPriceDialogFragment;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.presenter.ServiceP;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.main.R;
import com.aihuan.main.activity.AuthActivity;
import com.aihuan.main.activity.AuthBasicActivity;
import com.aihuan.main.activity.EditProfileActivity;
import com.aihuan.main.activity.FansActivity;
import com.aihuan.main.activity.FollowActivity;
import com.aihuan.main.activity.GiftCabActivity;
import com.aihuan.main.activity.MyDynamicActivity;
import com.aihuan.main.activity.MyPhotoActivity;
import com.aihuan.main.activity.MyVideoActivity;
import com.aihuan.main.activity.MyWallActivity;
import com.aihuan.main.activity.BeautySettingActivity;
import com.aihuan.main.activity.SettingActivity;
import com.aihuan.main.activity.WalletActivity;
import com.aihuan.main.adapter.MainMeAdapter;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.aihuan.main.presenter.UserStatusP;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by cxf on 2018/9/22.
 * 我的
 */

public class MainMeViewHolder extends AbsMainViewHolder implements View.OnClickListener, MainMeAdapter.ActionListener, MainPriceDialogFragment.ActionListener {

    private RecyclerView mRecyclerView;
    private MainMeAdapter mAdapter;
    private ImageView mAvatar;
    private TextView mName;
    private ImageView mLevel;
    private View mVip;
    private TextView mID;
    private TextView mFollow;
    private TextView mFans;
    private TextView mCoin;
    private TextView mCoinName;
    private View mBtnFans;
    private boolean mPaused;


    public MainMeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_me;
    }

    @Override
    public void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MainMeAdapter(mContext);
        mAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        View headView = mAdapter.getHeadView();
        mAvatar = headView.findViewById(R.id.avatar);
        mName = headView.findViewById(R.id.name);
        mLevel = headView.findViewById(R.id.level);
        mVip = headView.findViewById(R.id.vip);
        mID = headView.findViewById(R.id.id_val);
        mFollow = headView.findViewById(R.id.follow);
        mFans = headView.findViewById(R.id.fans);
        mCoin = headView.findViewById(R.id.coin);
        mCoinName = headView.findViewById(R.id.coin_name);
        headView.findViewById(R.id.btn_edit).setOnClickListener(this);
        headView.findViewById(R.id.btn_follow).setOnClickListener(this);
        headView.findViewById(R.id.btn_coin).setOnClickListener(this);
        headView.findViewById(R.id.btn_charge).setOnClickListener(this);
        mBtnFans = headView.findViewById(R.id.btn_fans);
        CommonAppConfig appConfig = CommonAppConfig.getInstance();
        UserBean u = appConfig.getUserBean();
        List<UserItemBean> list = appConfig.getUserItemList();
        if (u != null) {
            if (u.hasAuth()) {
                if (mBtnFans.getVisibility() != View.VISIBLE) {
                    mBtnFans.setVisibility(View.VISIBLE);
                    mBtnFans.setOnClickListener(this);
                }
            }
            showData(u, list);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
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
    public void loadData() {
        MainHttpUtil.getBaseInfo(mCallback);
        new UserStatusP().checkStatus(mContext, () -> {

        });
    }

    private CommonCallback<UserBean> mCallback = new CommonCallback<UserBean>() {
        @Override
        public void callback(UserBean bean) {
            List<UserItemBean> list = CommonAppConfig.getInstance().getUserItemList();
            if (bean != null) {
                showData(bean, list);
            }
        }
    };

    private void showData(UserBean u, List<UserItemBean> list) {
        ImgLoader.displayAvatar(mContext, u.getAvatarThumb(), mAvatar);
        mName.setText(u.getUserNiceName());
        mID.setText(StringUtil.contact("ID:", u.getId()));
        mFollow.setText(StringUtil.toWan(u.getFollows()));
        mCoin.setText(u.getCoin());
        mCoinName.setText(CommonAppConfig.getInstance().getCoinName());
        if (u.isVip()) {
            if (mVip.getVisibility() != View.VISIBLE) {
                mVip.setVisibility(View.VISIBLE);
            }
        } else {
            if (mVip.getVisibility() == View.VISIBLE) {
                mVip.setVisibility(View.INVISIBLE);
            }
        }
        if (u.hasAuth()) {
            if (mBtnFans.getVisibility() != View.VISIBLE) {
                mBtnFans.setVisibility(View.VISIBLE);
                mBtnFans.setOnClickListener(this);
            }
            mFans.setText(StringUtil.toWan(u.getFans()));
            LevelBean levelBean = CommonAppConfig.getInstance().getAnchorLevel(u.getLevelAnchor());
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumb(), mLevel);
            }
        } else {
            if (mBtnFans.getVisibility() == View.VISIBLE) {
                mBtnFans.setVisibility(View.GONE);
            }
            LevelBean levelBean = CommonAppConfig.getInstance().getLevel(u.getLevel());
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumb(), mLevel);
            }
        }
        if (mAdapter != null && list != null && list.size() > 0) {
            mAdapter.setList(list);
        }
    }


    @Override
    public void onItemClick(UserItemBean bean) {
        String url = bean.getHref();
        if (!TextUtils.isEmpty(url)) {
            WebViewActivity.forward(mContext, url);
        } else {
            switch (bean.getId()) {
                case Constants.MAIN_ME_WALLET:
                    forwardWallet();
                    break;
                case Constants.MAIN_ME_AUTH:
                    forwardAuth();
                    break;
                case Constants.MAIN_ME_IMPRESS:
                    forwardMyImpress();
                    break;
                case Constants.MAIN_ME_GIFI_CAB:
                    forwardGiftGab();
                    break;
                case Constants.MAIN_ME_SETTING:
                    forwardSetting();
                    break;
                case Constants.MAIN_ME_VIDEO:
                    videoPriceClick();
                    break;
                case Constants.MAIN_ME_VOICE:
                    voicePriceClick();
                    break;
                case Constants.MAIN_ME_MY_VIDEO:
                    forwardMyVideo();
                    break;
                case Constants.MAIN_ME_MY_ALBUM:
                    forwardMyAlbum();
                    break;
                case Constants.MAIN_ME_VIP:
                    RouteUtil.forwardVip();
                    break;
                case Constants.MAIN_ME_WALL:
                    MyWallActivity.forward(mContext);
                    break;
                case Constants.MAIN_ME_MY_DYNAMIC:
                    MyDynamicActivity.forward(mContext);
                    break;
                case Constants.MAIN_ME_MY_MENGYAN:
                    mContext.startActivity(new Intent(mContext, BeautySettingActivity.class));
                    break;
                case Constants.MAIN_ME_MY_SERVICE:
//                    ServiceActivity.forward(mContext);
                    new ServiceP(mContext).openWXService();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRadioBtnChanged(UserItemBean bean) {
        switch (bean.getId()) {
            case Constants.MAIN_ME_DISTURB:
                setDisturbSwitch(bean.isRadioBtnChecked());
                break;
            case Constants.MAIN_ME_VIDEO:
                setVideoSwitch(bean.isRadioBtnChecked());
                break;
            case Constants.MAIN_ME_VOICE:
                setVoiceSwitch(bean.isRadioBtnChecked());
                break;
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_edit) {
            forwardEditProfile();
        } else if (i == R.id.btn_follow) {
            forwardFollow();
        } else if (i == R.id.btn_fans) {
            forwardFans();
        } else if (i == R.id.btn_coin || i == R.id.btn_charge) {
            RouteUtil.forwardMyCoin();
        }
    }

    /**
     * 编辑个人资料
     */
    private void forwardEditProfile() {
        mContext.startActivity(new Intent(mContext, EditProfileActivity.class));
    }

    /**
     * 我的关注
     */
    private void forwardFollow() {
        FollowActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 我的粉丝
     */
    private void forwardFans() {
        FansActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 我的印象
     */
    private void forwardMyImpress() {
        RouteUtil.forwardImpress(CommonAppConfig.getInstance().getUid());
    }

    /**
     * 我要认证
     */
    private void forwardAuth() {
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        int auth = u.getAuth();
        if (auth == Constants.AUTH_WAITING) {
            ToastUtil.show(R.string.auth_tip_34);
            return;
        }
        if (auth == Constants.AUTH_FAILED) {
            ToastUtil.show(R.string.auth_tip_35);
        }
        AuthBasicActivity.forward(mContext, auth);
    }


    /**
     * 我的钱包
     */
    private void forwardWallet() {
        mContext.startActivity(new Intent(mContext, WalletActivity.class));
    }


    /**
     * 设置
     */
    private void forwardSetting() {
        mContext.startActivity(new Intent(mContext, SettingActivity.class));
    }

    /**
     * 礼物柜
     */
    private void forwardGiftGab() {
        GiftCabActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 我的视频
     */
    private void forwardMyVideo() {
        mContext.startActivity(new Intent(mContext, MyVideoActivity.class));
    }

    /**
     * 我的相册
     */
    private void forwardMyAlbum() {
        mContext.startActivity(new Intent(mContext, MyPhotoActivity.class));
    }

    /**
     * 设置勿扰开关
     */
    private void setDisturbSwitch(final boolean open) {
        MainHttpUtil.setDisturbSwitch(open, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    CommonAppConfig.getInstance().setUserSwitchDisturb(open);
                }
                ToastUtil.show(msg);
            }
        });
    }


    /**
     * 设置视频接听开关
     */
    private void setVideoSwitch(final boolean open) {
        MainHttpUtil.setVideoSwitch(open, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    CommonAppConfig.getInstance().setUserSwitchVideo(open);
                }
                ToastUtil.show(msg);
            }
        });
    }

    /**
     * 设置语音接听开关
     */
    private void setVoiceSwitch(final boolean open) {
        MainHttpUtil.setVoiceSwitch(open, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    CommonAppConfig.getInstance().setUserSwitchVoice(open);
                }
                ToastUtil.show(msg);
            }
        });
    }

    /**
     * 设置视频价格
     */
    private void videoPriceClick() {
        MainPriceDialogFragment fragment = new MainPriceDialogFragment();
        CommonAppConfig appConfig = CommonAppConfig.getInstance();
        fragment.setPriceList(appConfig.getVideoPriceList());
        fragment.setNowPrice(appConfig.getPriceVideo());
        fragment.setFrom(Constants.MAIN_ME_VIDEO);
        fragment.setActionListener(this);
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "MainPriceDialogFragment");
    }

    /**
     * 设置语音价格
     */
    private void voicePriceClick() {
        MainPriceDialogFragment fragment = new MainPriceDialogFragment();
        CommonAppConfig appConfig = CommonAppConfig.getInstance();
        fragment.setPriceList(appConfig.getVoicePriceList());
        fragment.setNowPrice(appConfig.getPriceVoice());
        fragment.setFrom(Constants.MAIN_ME_VOICE);
        fragment.setActionListener(this);
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "MainPriceDialogFragment");
    }

    @Override
    public void onPriceSelected(int from, ChatPriceBean bean) {
        if (from == Constants.MAIN_ME_VIDEO) {
            setVideoPrice(bean);
        } else if (from == Constants.MAIN_ME_VOICE) {
            setVoicePrice(bean);
        }
    }

    /**
     * 设置视频价格
     */
    private void setVideoPrice(final ChatPriceBean bean) {
        MainHttpUtil.setVideoPrice(bean.getCoin(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    CommonAppConfig.getInstance().setPriceVideo(bean.getCoin());
                    if (mAdapter != null) {
                        mAdapter.updateVideoPrice(bean.getCoin());
                    }
                }
                ToastUtil.show(msg);
            }
        });
    }

    /**
     * 设置语音价格
     */
    private void setVoicePrice(final ChatPriceBean bean) {
        MainHttpUtil.setVoicePrice(bean.getCoin(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    CommonAppConfig.getInstance().setPriceVoice(bean.getCoin());
                    if (mAdapter != null) {
                        mAdapter.updateVoicePrice(bean.getCoin());
                    }
                }
                ToastUtil.show(msg);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        MainHttpUtil.cancel(MainHttpConsts.SET_DISTURB_SWITCH);
        MainHttpUtil.cancel(MainHttpConsts.SET_VIDEO_SWITCH);
        MainHttpUtil.cancel(MainHttpConsts.SET_VOICE_SWITCH);
        MainHttpUtil.cancel(MainHttpConsts.SET_VIDEO_PRICE);
        MainHttpUtil.cancel(MainHttpConsts.SET_VOICE_PRICE);
    }
}
