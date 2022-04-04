package com.aihuan.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.Constants;
import com.aihuan.common.bean.ChatPriceBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.dialog.MainPriceDialogFragment;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.umeng.analytics.MobclickAgent;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.HtmlConfig;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.activity.WebViewActivity;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.http.CommonHttpConsts;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.GlideCatchUtil;
import com.aihuan.common.utils.SpUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.VersionUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.utils.ImMessageUtil;
import com.aihuan.main.R;

import java.io.File;

/**
 * Created by cxf on 2018/9/30.
 */

public class SettingActivity extends AbsActivity implements MainPriceDialogFragment.ActionListener {

    private ImageView mBtnRadioChatMusic;
    private TextView mVersion;
    private TextView mCacheSize;
    private Handler mHandler;
    private Drawable mRadioCheckDrawable;
    private Drawable mRadioUnCheckDrawable;
    private boolean mCloseChatMusic;//关闭聊天提示音
    private UserBean mUser;

    private ImageView ivVideoBtn, ivVoiceBtn, ivDisturbBtn;
    private TextView tvVidePrice, tvVoicePrice;

    private String mPriceSuffix;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.setting));
        mUser = CommonAppConfig.getInstance().getUserBean();


        View audioReceiveContainer =  findViewById(R.id.audio_receive_container);
        if (mUser.getSex()==1){
            audioReceiveContainer.setVisibility(View.GONE);
        }else{
            audioReceiveContainer.setVisibility(View.VISIBLE);
        }


        mVersion = findViewById(R.id.version);
        mCacheSize = findViewById(R.id.cache_size);
        mBtnRadioChatMusic = findViewById(R.id.btn_radio_chat_music);

        ivVideoBtn = findViewById(R.id.btn_video_radio);
        tvVidePrice = findViewById(R.id.tv_video_price);
        ivVoiceBtn = findViewById(R.id.btn_voice_radio);
        tvVoicePrice = findViewById(R.id.tv_voice_price);
        ivDisturbBtn = findViewById(R.id.btn_disturb_radio);

        mRadioCheckDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_btn_radio_1);
        mRadioUnCheckDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_btn_radio_0);
        mCloseChatMusic = SpUtil.getInstance().getBooleanValue(SpUtil.CHAT_MUSIC_CLOSE);
        mBtnRadioChatMusic.setImageDrawable(mCloseChatMusic ? mRadioUnCheckDrawable : mRadioCheckDrawable);
        mVersion.setText(VersionUtil.getVersion());
        mCacheSize.setText(getCacheSize());

        mPriceSuffix = String.format(WordUtil.getString(R.string.main_price), CommonAppConfig.getInstance().getCoinName());

        setUserConfig();
    }

    private void setUserConfig() {
        ivVideoBtn.setSelected(CommonAppConfig.getInstance().getUserSwitchVideo());
        ivVoiceBtn.setSelected(CommonAppConfig.getInstance().getUserSwitchVoice());
        ivDisturbBtn.setSelected(CommonAppConfig.getInstance().getUserSwitchDisturb());
        tvVidePrice.setText(CommonAppConfig.getInstance().getPriceVideo() + mPriceSuffix);
        tvVoicePrice.setText(CommonAppConfig.getInstance().getPriceVoice() + mPriceSuffix);
    }

    public void settingClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_about_us) {
            WebViewActivity.forward(mContext, HtmlConfig.ABOUT_US);
        } else if (i == R.id.btn_check_update) {
            checkVersion();
        } else if (i == R.id.btn_clear_cache) {
            clearCache();
        } else if (i == R.id.btn_logout) {
            logout();
        } else if (i == R.id.btn_black_list) {
            BlackListActivity.forward(mContext);
        } else if (i == R.id.btn_radio_chat_music) {
            toggleChatMusic();
        } else if (i == R.id.btn_video_radio) {
            setVideoSwitch(!ivVideoBtn.isSelected());
        } else if (i == R.id.btn_voice_radio) {
            setVoiceSwitch(!ivVoiceBtn.isSelected());
        } else if (i == R.id.btn_disturb_radio) {
            setDisturbSwitch(!ivDisturbBtn.isSelected());
        } else if (i == R.id.tv_video_price) {
            videoPriceClick();
        } else if (i == R.id.tv_voice_price) {
            voicePriceClick();
        } else if (i == R.id.group_beauty) {
            startActivity(new Intent(mContext, BeautySettingActivity.class));
        }
    }

    /**
     * 私信聊天提示音开关
     */
    private void toggleChatMusic() {
        mCloseChatMusic = !mCloseChatMusic;
        mBtnRadioChatMusic.setImageDrawable(mCloseChatMusic ? mRadioUnCheckDrawable : mRadioCheckDrawable);
        ImMessageUtil.getInstance().setCloseChatMusic(mCloseChatMusic);
        SpUtil.getInstance().setBooleanValue(SpUtil.CHAT_MUSIC_CLOSE, mCloseChatMusic);
    }


    /**
     * 检查更新
     */
    private void checkVersion() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (VersionUtil.isLatest(configBean.getVersion())) {
                        ToastUtil.show(R.string.version_latest);
                    } else {
                        VersionUtil.showDialog(mContext, configBean.getUpdateDes(), configBean.getDownloadApkUrl());
                    }
                }
            }
        });

    }

    /**
     * 退出登录
     */
    private void logout() {
        CommonAppConfig.getInstance().clearLoginInfo();
        //退出IM
        ImMessageUtil.getInstance().logoutImClient();
        //ImPushUtil.getInstance().logout();
        //友盟统计登出
        MobclickAgent.onProfileSignOff();
        LoginActivity.forward();
    }


    /**
     * 获取缓存
     */
    private String getCacheSize() {
        return GlideCatchUtil.getInstance().getCacheSize();
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        final Dialog dialog = DialogUitl.loadingDialog(mContext, getString(R.string.setting_clear_cache_ing));
        dialog.show();
        GlideCatchUtil.getInstance().clearImageAllCache();
        File gifGiftDir = new File(CommonAppConfig.GIF_PATH);
        if (gifGiftDir.exists() && gifGiftDir.length() > 0) {
            gifGiftDir.delete();
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (mCacheSize != null) {
                    mCacheSize.setText(getCacheSize());
                }
                ToastUtil.show(R.string.setting_clear_cache);
            }
        }, 2000);
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        MainHttpUtil.cancel(MainHttpConsts.SET_DISTURB_SWITCH);
        MainHttpUtil.cancel(MainHttpConsts.SET_VIDEO_SWITCH);
        MainHttpUtil.cancel(MainHttpConsts.SET_VOICE_SWITCH);
        MainHttpUtil.cancel(MainHttpConsts.SET_VIDEO_PRICE);
        MainHttpUtil.cancel(MainHttpConsts.SET_VOICE_PRICE);
        super.onDestroy();
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
                    setUserConfig();
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
                    setUserConfig();
                }
                ToastUtil.show(msg);
            }
        });
    }

    /**
     * 设置视频价格 点击事件
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
     * 设置语音价格 点击事件
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
                    setUserConfig();
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
                    setUserConfig();
                }
                ToastUtil.show(msg);
            }
        });
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
                    setUserConfig();
                }
                ToastUtil.show(msg);
            }
        });
    }

}
