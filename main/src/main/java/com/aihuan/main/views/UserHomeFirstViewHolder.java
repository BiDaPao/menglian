package com.aihuan.main.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aihuan.common.utils.ToastUtil;
import com.aihuan.main.activity.UserHomeNewActivity;
import com.aihuan.common.utils.MediaManager;
import com.alibaba.android.arouter.utils.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.bean.LevelBean;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.utils.CommonIconUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.bean.WallBean;
import com.aihuan.one.views.AbsUserHomeViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/4/8.
 */

public class UserHomeFirstViewHolder extends AbsUserHomeViewHolder {

    private List<UserHomeWallViewHolder> mViewList;
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private TextView mName;
    private TextView mCity;
    private TextView mID;
    private TextView mFans;
    private TextView mVideoPrice;
    private TextView mVoicePrice;
    private ImageView mSex;
    private ImageView mLevelAnchor;
    private View mVip;
    private ImageView mOnLine;
    private ImageView mBtnFollow;
    private Drawable mUnFollowDrawable;
    private Drawable mFollowDrawable;
    private ImageView mBtnChat;
    private String mToUid;

    //认证时设置的语音
    private String voiceUrl;
    private View layoutVoice;
    private ImageView ivVoice;
    private TextView tvDuration;
    private int voiceDuration;
    private AnimationDrawable animationDrawable;


    public UserHomeFirstViewHolder(Context context, ViewGroup parentView, String toUid) {
        super(context, parentView, toUid);
    }

    @Override
    protected void processArguments(Object... args) {
        mToUid = (String) args[0];
    }


    @Override
    protected int getLayoutId() {
        return R.layout.view_user_home_first;
    }

    @Override
    public void init() {
        mUnFollowDrawable = ContextCompat.getDrawable(mContext, R.mipmap.o_user_btn_follow_1_0);
        mFollowDrawable = ContextCompat.getDrawable(mContext, R.mipmap.o_user_btn_follow_1_1);
        mBtnFollow = (ImageView) findViewById(R.id.btn_follow);
        mName = (TextView) findViewById(R.id.name);
        mCity = (TextView) findViewById(R.id.city);
        mID = (TextView) findViewById(R.id.id_val);
        mFans = (TextView) findViewById(R.id.fans);
        mVideoPrice = (TextView) findViewById(R.id.price_video);
        mVoicePrice = (TextView) findViewById(R.id.price_voice);
        layoutVoice = findViewById(R.id.layout_voice);
        layoutVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVoice();
            }
        });
        ivVoice = (ImageView) findViewById(R.id.iv_voice);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        mSex = (ImageView) findViewById(R.id.sex);
        mLevelAnchor = (ImageView) findViewById(R.id.level_anchor);
        mVip = findViewById(R.id.vip);
        mOnLine = (ImageView) findViewById(R.id.on_line);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mBtnChat = (ImageView) findViewById(R.id.btn_chat);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mRadioGroup != null) {
                    ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        if (mName != null) {
            mName.setText(obj.getString("user_nickname"));
        }
        if (mCity != null) {
            mCity.setText(obj.getString("city"));
        }
        if (mID != null) {
            mID.setText(StringUtil.contact("ID:", obj.getString("id")));
        }
        if (mFans != null) {
            mFans.setText(String.format(WordUtil.getString(R.string.user_home_fans), obj.getString("fans")));
        }
        CommonAppConfig appConfig = CommonAppConfig.getInstance();
        String coinName = appConfig.getCoinName();
        boolean openVideo = obj.getIntValue("isvideo") == 1;
        boolean openVoice = obj.getIntValue("isvoice") == 1;
        if (mBtnChat != null) {
            if (!openVideo && openVoice) {
                mBtnChat.setImageResource(R.mipmap.o_user_btn_chat_voice);
            } else if (!openVoice && openVideo) {
                mBtnChat.setImageResource(R.mipmap.o_user_btn_chat_video);
            }
        }
        if (mVideoPrice != null) {
            if (openVideo) {
                mVideoPrice.setText(String.format(WordUtil.getString(R.string.user_home_video_price)
                        , StringUtil.contact(obj.getString("video_value"), coinName)));
            } else {
                mVideoPrice.setText(R.string.user_home_price_close_video);
            }
        }
        if (mVoicePrice != null) {
            if (openVoice) {
                mVoicePrice.setText(String.format(WordUtil.getString(R.string.user_home_voice_price)
                        , StringUtil.contact(obj.getString("voice_value"), coinName)));
            } else {
                mVoicePrice.setText(R.string.user_home_price_close_voice);
            }
        }
        if (mSex != null) {
            mSex.setImageResource(CommonIconUtil.getSexIcon(obj.getIntValue("sex")));
        }
        if (mLevelAnchor != null) {
            LevelBean bean = appConfig.getAnchorLevel(obj.getIntValue("level_anchor"));
            if (bean != null) {
                ImgLoader.display(mContext, bean.getThumb(), mLevelAnchor);
            }
        }
        if (mVip != null) {
            if (obj.getIntValue("isvip") == 1) {
                if (mVip.getVisibility() != View.VISIBLE) {
                    mVip.setVisibility(View.VISIBLE);
                }
            } else {
                if (mVip.getVisibility() != View.GONE) {
                    mVip.setVisibility(View.GONE);
                }
            }
        }
        if (mOnLine != null) {
            mOnLine.setImageResource(CommonIconUtil.getOnLineIcon2(obj.getIntValue("online")));
        }

        //id: "375",
        //uid: "103184",
        //thumb: "http://cc.lcmdhr.top/android_103184_20220331_110402_6053695.jpg",
        //href: "",
        //type: "0"



        List<WallBean> photos = JSON.parseArray(obj.getString("photos_list"), WallBean.class);

        if (photos.isEmpty()){
            WallBean wallBean = new WallBean();
            wallBean.setThumb(obj.getString("avatar"));
            wallBean.setId("11");
            wallBean.setUid(mToUid);
            wallBean.setType(0);
            photos.add(wallBean);
        }
        setImageList(photos);
        setFollow(obj.getIntValue("isattent") == 1);
        voiceUrl = obj.getString("f_voice");
        voiceDuration = obj.getIntValue("f_voice_duration");
        if (TextUtils.isEmpty(voiceUrl)) {
            layoutVoice.setVisibility(View.GONE);
        } else {
            tvDuration.setText(WordUtil.getString(R.string.voice_duraion, (int) voiceDuration));
            layoutVoice.setVisibility(View.VISIBLE);
        }
    }

    private void setImageList(final List<WallBean> imageList) {
        if (imageList == null || mRadioGroup == null || mViewPager == null) {
            return;
        }
        final int size = imageList.size();
        if (size == 0) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0; i < size; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_user_indicator, mRadioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            mRadioGroup.addView(radioButton);
        }
        mViewList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            UserHomeWallViewHolder vh = new UserHomeWallViewHolder(mContext, mViewPager);
            mViewList.add(vh);
        }
        if (size > 1) {
            mViewPager.setOffscreenPageLimit(size - 1);
        }
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return size;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                UserHomeWallViewHolder vh = mViewList.get(position);
                vh.addToParent();
                vh.loadData(imageList.get(position));
                return vh.getContentView();
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setVideoPause(position != 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //判断有没有关注
    public void setFollow(boolean follow) {
        if (mBtnFollow != null) {
//            //问题  ↓↓↓
//            mBtnFollow.setImageDrawable(follow ?  mFollowDrawable : mUnFollowDrawable );
////            onResume();
            Log.e("sss","测试接口1  ");
            if (follow == true){
                Toast.makeText(mContext, "返回的true", Toast.LENGTH_SHORT).show();
                mBtnFollow.setImageDrawable(mFollowDrawable);
            }else if (follow == false){
                Toast.makeText(mContext, "返回的false", Toast.LENGTH_SHORT).show();
                mBtnFollow.setImageDrawable(mUnFollowDrawable);
            }
        }
    }




    public void setVideoPause(boolean pause) {
        if (mViewList != null && mViewList.size() > 0) {
            UserHomeWallViewHolder vh = mViewList.get(0);
            if (vh != null) {
                if (pause) {
                    vh.passivePause();
                } else {
                    vh.passiveResume();
                }
            }
        }
    }

    /**
     * 认证语音播放
     */
    private void playVoice() {
        if (!TextUtils.isEmpty(voiceUrl) && !MediaManager.getInstance().isPlaying()) {
            MediaManager.getInstance().playSound(voiceUrl, new MediaManager.Callback() {
                @Override
                public void onCompletion(Boolean success) {

                    if (!success) {
                        ToastUtil.show("播放失败");
                    }
                    if (animationDrawable != null) {
                        animationDrawable.stop();
                        ivVoice.setImageResource(R.mipmap.icon_voice_left_3);
                    }
                }
            });
            ivVoice.setImageResource(R.drawable.play_voice_message);
            animationDrawable = (AnimationDrawable) ivVoice.getDrawable();
            animationDrawable.start();
        }
    }

    private void stopPlayVoice() {
        if (MediaManager.getInstance().isPlaying()) {
            MediaManager.getInstance().stopPlayRecord();
            if (animationDrawable != null) {
                animationDrawable.stop();
                ivVoice.setImageResource(R.mipmap.icon_voice_left_3);
            }
        }
    }

    @Override
    public void onResume() {
//        JSONObject obj = ((UserHomeNewActivity) mContext).getUserObj();
//        Toast.makeText(mContext, "resume: "+obj.getIntValue("isattent"), Toast.LENGTH_SHORT).show();
//        if(obj.getIntValue("isattent") == 1){
//            mBtnFollow.setImageDrawable(mFollowDrawable);
//        }else{
//            mBtnFollow.setImageDrawable(mUnFollowDrawable);
//        }
    }

    @Override
    public void onPause() {
        stopPlayVoice();
        super.onPause();
    }
}
