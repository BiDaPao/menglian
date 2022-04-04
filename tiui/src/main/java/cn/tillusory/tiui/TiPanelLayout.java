package cn.tillusory.tiui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.tiui.custom.DrawableTextView;
import cn.tillusory.tiui.custom.TiConfigTools;
import cn.tillusory.tiui.custom.TiFileUtil;
import cn.tillusory.tiui.custom.TiSharePreferences;
import cn.tillusory.tiui.model.RxBusAction;
import cn.tillusory.tiui.model.TiMode;
import cn.tillusory.tiui.view.TiBeautyView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;

/**
 * Created by Anko on 2018/5/12.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiPanelLayout extends ConstraintLayout {

    private TiSDKManager tiSDKManager;
    private TiBeautyView tiBeautyView;

    private ImageView tiBeautyIV;
    private View tiBeautyModeContainer;
    private DrawableTextView tiModeBeauty;
    private DrawableTextView tiModeCute;
    private DrawableTextView tiModeFilter;
    private DrawableTextView tiModeMakeup;
    private ImageView tiBtnBack;
    private TextView tiInteractionHint;

    private boolean isLiveOrVideo = false;
    public static boolean isFullRatio = true;


    public TiPanelLayout(Context context) {
        super(context);
    }

    public TiPanelLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TiPanelLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TiPanelLayout init(@NonNull TiSDKManager tiSDKManager) {
        this.tiSDKManager = tiSDKManager;
        TiSharePreferences.getInstance().init(getContext(), tiSDKManager);
        RxBus.get().register(this);

        TiConfigTools.getInstance().initTiConfigTools(getContext());
        checkVersion();
        initView();

        initData();

        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        TiConfigTools.getInstance().release();
        RxBus.get().unregister(this);
    }

    public void setLiveOrVideo(boolean liveOrVideo) {
        isLiveOrVideo = liveOrVideo;
    }

    private void checkVersion() {
        String curVersion = cn.tillusory.tiui.BuildConfig.TIUI_VERSION;
        String oldVersion = TiSharePreferences.getInstance().getUIVersion();
        if (oldVersion.equals("") || Float.parseFloat(curVersion) > Float.parseFloat(oldVersion)) {
            TiConfigTools.getInstance().resetConfigFile();
            TiSharePreferences.getInstance().putUIVersion(curVersion);
            TiFileUtil.resetFile(getContext());
        }
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_ti_panel, this);

        tiBeautyIV = findViewById(R.id.tiBeautyIV);
        tiBeautyView = findViewById(R.id.tiBeautyView);
        tiBeautyModeContainer = findViewById(R.id.tiBeautyModeContainer);

        tiModeBeauty = findViewById(R.id.beauty);
        tiModeFilter = findViewById(R.id.filter);
        tiModeCute = findViewById(R.id.cute);
        tiModeMakeup = findViewById(R.id.makeup);
        tiBtnBack = findViewById(R.id.btn_back);
        tiInteractionHint = findViewById(R.id.interaction_hint);
    }

    private void initData() {
        tiBeautyView.init(tiSDKManager);
        TiSharePreferences.getInstance().initCacheValue();
        Log.e("TISDK", "initData:  --------------- TiPanelLayout");
        tiBeautyIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tiBeautyModeContainer != null && tiBeautyModeContainer.getVisibility() == GONE) {
                    tiBeautyModeContainer.setVisibility(VISIBLE);
                }
//                if (tiBeautyIV != null) {
//                    tiBeautyIV.setVisibility(GONE);
//                }
            }
        });

        tiModeBeauty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showBeautyView(TiMode.MODE_BEAUTY, false, 0);
            }
        });

        tiModeFilter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tiBeautyView.tiBarView.hideSeekBar();
                showBeautyView(TiMode.MODE_FILTER, false, 0);
            }
        });

        tiModeCute.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showBeautyView(TiMode.MODE_CUTE, false, 0);
            }
        });

        tiModeMakeup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showBeautyView(TiMode.MODE_MAKEUP, false, 0);
            }
        });

        tiBtnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideModeContainer(true);
            }
        });

        //空白处隐藏面板
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.performClick();
                if (tiBeautyView.hideMakeupView() || tiBeautyView.hideGreenScreenView()) {
                    return false;
                } else if (hideBeautyView()) {
                    return false;
                } else if (hideModeContainer(true)) {
                    return false;
                }
                return false;
            }
        });
        initBeautyCache();
    }

    /**
     * 美颜参数初始化
     */
    public void initBeautyCache() {
        if (tiSDKManager != null) {
            //美颜模块
            //开关
            Log.e("TISDK", "开关initBeautyCache: " + TiSharePreferences.getInstance().getBeautyEnable());
            tiSDKManager.setBeautyEnable(TiSharePreferences.getInstance().getBeautyEnable());
            //美白
            Log.e("TISDK", "美白initBeautyCache: " + TiSharePreferences.getInstance().getWhiteningVal());
            tiSDKManager.setSkinWhitening(TiSharePreferences.getInstance().getWhiteningVal());
            //磨皮
            Log.e("TISDK", "磨皮initBeautyCache: " + TiSharePreferences.getInstance().getBlemishRemovalVal());
            tiSDKManager.setSkinBlemishRemoval(TiSharePreferences.getInstance().getBlemishRemovalVal());
            //粉嫩
            Log.e("TISDK", "粉嫩initBeautyCache: " + TiSharePreferences.getInstance().getTendernessVal());
            tiSDKManager.setSkinTenderness(TiSharePreferences.getInstance().getTendernessVal());
            //清晰
            Log.e("TISDK", "清晰initBeautyCache: " + TiSharePreferences.getInstance().getSharpnessVal());
            tiSDKManager.setSkinSharpness(TiSharePreferences.getInstance().getSharpnessVal());
            //饱和
//            tiSDKManager.setSkinSaturation(TiSharePreferences.getInstance().getS);
        }
    }

    public void showBeautyView(int mode, boolean isFromHome, int subMode) {
        tiBeautyView.refreshData(mode, isFromHome, subMode);
        if (tiBeautyView != null && tiBeautyView.getVisibility() == GONE) {
            tiBeautyView.setVisibility(VISIBLE);
        }
        hideModeContainer(false);
    }

    private boolean hideBeautyView() {
        if (tiBeautyView != null && tiBeautyView.getVisibility() == VISIBLE) {
            tiBeautyView.setVisibility(GONE);
            //if live or video
            if (isLiveOrVideo) {
                RxBus.get().post("SHOW_LIVE_OR_VIDEO_CONTAINER", true);
            } else {
                tiBeautyModeContainer.setVisibility(VISIBLE);
            }
            return true;
        }
        return false;
    }

    private boolean hideModeContainer(boolean isFromBack) {
        if (tiBeautyModeContainer != null && tiBeautyModeContainer.getVisibility() == VISIBLE) {
            tiBeautyModeContainer.setVisibility(GONE);
            if (isFromBack) {

                if (tiBeautyIV != null) {
//                   tiBeautyIV.setVisibility(VISIBLE);
                }

                RxBus.get().post("CONTROL_BTN_CAPTURE", true);
            }
            return true;
        }
        return false;
    }

    public void showBeautyControl() {
        if (tiBeautyIV != null) {
            tiBeautyIV.setVisibility(VISIBLE);
        }
    }

    public void hideAllVisibleViews() {
        tiBeautyView.hideMakeupView();
        hideBeautyView();
        hideModeContainer(true);
    }

    public void changeViewByRatio(boolean isFull) {
        if (isFull) {
            isFullRatio = true;
            tiModeBeauty.setTopDrawable(getResources().getDrawable(R.drawable.ic_ti_mode_beauty_white));
            tiModeFilter.setTopDrawable(getResources().getDrawable(R.drawable.ic_ti_mode_filter_white));
            tiModeCute.setTopDrawable(getResources().getDrawable(R.drawable.ic_ti_mode_cute_white));
            tiModeMakeup.setTopDrawable(getResources().getDrawable(R.drawable.ic_ti_mode_makeup_white));
            tiModeBeauty.setTextColor(getResources().getColor(R.color.white));
            tiModeFilter.setTextColor(getResources().getColor(R.color.white));
            tiModeCute.setTextColor(getResources().getColor(R.color.white));
            tiModeMakeup.setTextColor(getResources().getColor(R.color.white));
            tiBtnBack.setImageResource(R.drawable.ic_ti_mode_back_white);
            //改变TiBeautyView背景
            tiBeautyView.changeViewByRatio(true);
        } else {
            isFullRatio = false;
            tiModeBeauty.setTopDrawable(getResources().getDrawable(R.drawable.ic_ti_mode_beauty_black));
            tiModeFilter.setTopDrawable(getResources().getDrawable(R.drawable.ic_ti_mode_filter_black));
            tiModeCute.setTopDrawable(getResources().getDrawable(R.drawable.ic_ti_mode_cute_black));
            tiModeMakeup.setTopDrawable(getResources().getDrawable(R.drawable.ic_ti_mode_makeup_black));
            tiModeBeauty.setTextColor(getResources().getColor(R.color.ti_unselected));
            tiModeFilter.setTextColor(getResources().getColor(R.color.ti_unselected));
            tiModeCute.setTextColor(getResources().getColor(R.color.ti_unselected));
            tiModeMakeup.setTextColor(getResources().getColor(R.color.ti_unselected));
            tiBtnBack.setImageResource(R.drawable.ic_ti_mode_back_black);
            //改变TiBeautyView背景
            tiBeautyView.changeViewByRatio(false);
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(RxBusAction.ACTION_SHOW_INTERACTION),
                    @Tag(RxBusAction.ACTION_SHOW_GESTURE)
            })
    public void showHint(String hint) {
        tiInteractionHint.setVisibility(TextUtils.isEmpty(hint) ? View.GONE : View.VISIBLE);
        tiInteractionHint.setText(hint);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(RxBusAction.ACTION_HIDE_MODE_CONTAINER)
            })
    public void onBeautyViewBackClick(Boolean bool) {
        hideBeautyView();
    }


    public void showBeautyView() {
        if (tiBeautyModeContainer != null && tiBeautyModeContainer.getVisibility() == GONE) {
            tiBeautyModeContainer.setVisibility(VISIBLE);
        }
    }

}



