package com.aihuan.im.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.adapter.ImChatFacePagerAdapter;
import com.aihuan.common.bean.ConfigBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.event.BlackEvent;
import com.aihuan.common.event.FollowEvent;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.interfaces.KeyBoardHeightChangeListener;
import com.aihuan.common.interfaces.OnFaceClickListener;
import com.aihuan.common.upload.UploadBean;
import com.aihuan.common.upload.UploadCallback;
import com.aihuan.common.upload.UploadCallbackSignle;
import com.aihuan.common.upload.UploadCensorImpl;
import com.aihuan.common.utils.DateFormatUtil;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.FileUtil;
import com.aihuan.common.utils.ImgUtil;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.im.R;
import com.aihuan.im.activity.ChatRoomActivity;
import com.aihuan.im.adapter.ImRoomAdapter;
import com.aihuan.im.bean.CensorBean;
import com.aihuan.im.bean.ImMessageBean;
import com.aihuan.im.custom.MyImageView;
import com.aihuan.im.dialog.ChatImageDialog;
import com.aihuan.im.event.ImRemoveAllMsgEvent;
import com.aihuan.im.http.ImHttpConsts;
import com.aihuan.im.http.ImHttpUtil;
import com.aihuan.im.interfaces.ChatRoomActionListener;
import com.aihuan.im.utils.ImMessageUtil;
import com.aihuan.im.utils.ImTextRender;
import com.aihuan.im.utils.MediaRecordUtil;
import com.aihuan.im.utils.VoiceMediaPlayerUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.imsdk.TIMImageElem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import top.zibin.luban.OnCompressListener;


/**
 * Created by cxf on 2018/10/24.
 */

public class ChatRoomViewHolder extends AbsViewHolder implements
        View.OnClickListener, OnFaceClickListener,
        ImRoomAdapter.ActionListener {

    private InputMethodManager imm;
    private RecyclerView mRecyclerView;
    private ImRoomAdapter mAdapter;
    private EditText mEditText;
    private TextView mVoiceRecordEdit;
    private Drawable mVoiceUnPressedDrawable;
    private Drawable mVoicePressedDrawable;
    private TextView mTitleView;
    private TextView tvIntimacyLevel;
    private View mVip;
    private UserBean mUserBean;
    private String mToUid;
    private ChatRoomActionListener mActionListener;
    private ImMessageBean mCurMessageBean;
    private HttpCallback mCheckBlackCallback;
    private HttpCallback mChargeSendCallback;
    private CheckBox mBtnFace;
    private CheckBox mBtnVoice;
    private View mFaceView;//表情控件
    private View mMoreView;//更多控件
    private ViewGroup mFaceContainer;//表情弹窗
    private ViewGroup mMoreContainer;//更多弹窗
    private ChatImageDialog mChatImageDialog;//图片预览弹窗
    private boolean mFollowing;
    private boolean mBlacking;
    private boolean mToAuth;//对方是否认证了
    private View mFollowGroup;
    private String mPressSayString;
    private String mUnPressStopString;
    private MediaRecordUtil mMediaRecordUtil;
    private File mRecordVoiceFile;//录音文件
    private long mRecordVoiceDuration;//录音时长
    private Handler mHandler;
    private VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;

    //当前用户和对方亲密度等级
    private int mIntimacyLevel;

    //是否需要发送默认搭讪语
    private boolean needAccost;

    //当前用户备注设置
    private String remark;

    //内容安全鉴定 -- 上传七牛云存储后由七牛云审核处理
    private UploadCensorImpl uploadCensor;

    public ChatRoomViewHolder(Context context, ViewGroup parentView, UserBean userBean,
                              boolean following, boolean blacking, boolean auth, boolean needAccost) {
        super(context, parentView, userBean, following, blacking, auth, needAccost);
    }

    @Override
    protected void processArguments(Object... args) {
        mUserBean = (UserBean) args[0];
        mToUid = mUserBean.getId();
        mFollowing = (boolean) args[1];
        mBlacking = (boolean) args[2];
        mToAuth = (boolean) args[3];
        needAccost = (boolean) args[4];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_room;
    }

    @Override
    public void init() {
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mFaceContainer = (ViewGroup) findViewById(R.id.face_container);
        mMoreContainer = (ViewGroup) findViewById(R.id.more_container);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mTitleView = (TextView) findViewById(R.id.titleView);
        tvIntimacyLevel = (TextView) findViewById(R.id.tv_intimacy_level);
        mVip = findViewById(R.id.vip);
        mEditText = (EditText) findViewById(R.id.edit);
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        mEditText.setHint(configBean.getImTip());
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendText();
                    return true;
                }
                return false;
            }
        });
        mEditText.setOnClickListener(this);
        mVoiceRecordEdit = (TextView) findViewById(R.id.btn_voice_record_edit);
        if (mVoiceRecordEdit != null) {
            mVoiceUnPressedDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_chat_voice_record_0);
            mVoicePressedDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_chat_voice_record_1);
            mPressSayString = WordUtil.getString(R.string.im_press_say);
            mUnPressStopString = WordUtil.getString(R.string.im_unpress_stop);
            mVoiceRecordEdit.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    switch (e.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startRecordVoice();
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            stopRecordVoice();
                            break;
                    }
                    return true;
                }
            });
        }
        mFollowGroup = findViewById(R.id.btn_follow_group);
//        if (!mFollowing) {
//            mFollowGroup.setVisibility(View.VISIBLE);
//            mFollowGroup.findViewById(R.id.btn_close_follow).setOnClickListener(this);
//            mFollowGroup.findViewById(R.id.btn_follow).setOnClickListener(this);
//        }
        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnFace = (CheckBox) findViewById(R.id.btn_face);
        mBtnFace.setOnClickListener(this);
        View btnAdd = findViewById(R.id.btn_add);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(this);
        }
        mBtnVoice = (CheckBox) findViewById(R.id.btn_voice_record);
        if (mBtnVoice != null) {
            mBtnVoice.setOnClickListener(this);
        }
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return hideSoftInput() || hideFace() || hideMore();
                }
                return false;
            }
        });
        mCheckBlackCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                processCheckBlackData(code, msg, info);
            }
        };
        EventBus.getDefault().register(this);
        mHandler = new Handler();
        //mEditText.requestFocus();
        if (Constants.IM_MSG_ADMIN.equals(mToUid)) {
            findViewById(R.id.btn_option_more).setVisibility(View.INVISIBLE);
            findViewById(R.id.bottom).setVisibility(View.GONE);
        } else {
            findViewById(R.id.btn_option_more).setOnClickListener(this);
        }
        View layoutIntimacy = findViewById(R.id.layout_intimacy);
        if (mUserBean != null && !TextUtils.equals("admin", mUserBean.getId())) {
            layoutIntimacy.setVisibility(View.VISIBLE);
        } else {
            layoutIntimacy.setVisibility(View.GONE);
        }
        ImageView ivLeft = (ImageView) findViewById(R.id.left_avatar);
        ImageView ivRight = (ImageView) findViewById(R.id.right_avatar);


        if (mUserBean != null) {
            ImgLoader.displayAvatar(mContext, mUserBean.getAvatar(), ivLeft);
            //跳转主页；
            ivLeft.setOnClickListener(v -> {
                if (!mToAuth) {
                    ToastUtil.show(R.string.user_is_not_auth);
                } else {
                    RouteUtil.forwardUserHome(mUserBean.getId());
                }
            });
        }
        UserBean userBean = CommonAppConfig.getInstance().getUserBean();
        if (userBean != null) {
            ImgLoader.displayAvatar(mContext, userBean.getAvatar(), ivRight);

        }
        setAccost();
    }


    /**
     * 判断是否需要发送搭讪语，并检测条数限制
     */
    private void setAccost() {
        final String accostText = CommonAppConfig.getInstance().getUserBean().getAccost_text();
        if (CommonAppConfig.getInstance().getUserBean().getSex() == 1) {
            return;
        }
        if (needAccost && !TextUtils.isEmpty(accostText)) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendText(accostText);
                }
            }, 500);
        }
    }

    /**
     * 初始化表情控件
     */
    private View initFaceView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.view_chat_face, mFaceContainer, false);
        v.findViewById(R.id.btn_send).setOnClickListener(this);
        final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(10);
        ImChatFacePagerAdapter adapter = new ImChatFacePagerAdapter(mContext, this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0, pageCount = adapter.getCount(); i < pageCount; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_chat_indicator, radioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            radioGroup.addView(radioButton);
        }
        return v;
    }


    private View initMoreView() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.view_chat_more_2, null);
        v.findViewById(R.id.btn_img).setOnClickListener(this);
        v.findViewById(R.id.btn_camera).setOnClickListener(this);
        v.findViewById(R.id.btn_gift).setOnClickListener(this);
        v.findViewById(R.id.btn_video).setOnClickListener(this);
        v.findViewById(R.id.btn_voice).setOnClickListener(this);
        return v;
    }


    public void loadData() {
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mTitleView.setText(mUserBean.getUserNiceName());
        if (mUserBean.isVip()) {
            mVip.setVisibility(View.VISIBLE);
        }
        getRemark();
        getIntimacyLevel();
        mAdapter = new ImRoomAdapter(mContext, mToUid, mUserBean);
        mAdapter.setAuthed(mToAuth);
        mAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        ImMessageUtil.getInstance().getChatMessageList(mToUid, new CommonCallback<List<ImMessageBean>>() {
            @Override
            public void callback(List<ImMessageBean> list) {
                L.e("消息数量 " + list.size());
                if (mAdapter != null) {
                    mAdapter.setList(list);
                    mAdapter.scrollToBottom();
                }
            }
        });


    }


    public void setActionListener(ChatRoomActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void scrollToBottom() {
        if (mAdapter != null) {
            mAdapter.scrollToBottom();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_back) {
            back();

        } else if (i == R.id.btn_send) {
            sendText();

        } else if (i == R.id.btn_face) {
            faceClick();

        } else if (i == R.id.edit) {
            clickInput();

        } else if (i == R.id.btn_add) {
            clickMore();

        } else if (i == R.id.btn_voice_record) {
            if (IntimacyBelowLevel()) {
                mBtnVoice.setChecked(false);
                return;
            }
            if (mActionListener != null) {
                mActionListener.onVoiceClick();
            }
        } else if (i == R.id.btn_img) {
            if (IntimacyBelowLevel()) {
                return;
            }
            if (mActionListener != null) {
                mActionListener.onChooseImageClick();
            }

        } else if (i == R.id.btn_camera) {
            if (IntimacyBelowLevel()) {
                return;
            }
            if (mActionListener != null) {
                mActionListener.onCameraClick();
            }

        } else if (i == R.id.btn_location) {
            if (mActionListener != null) {
                mActionListener.onLocationClick();
            }

        } else if (i == R.id.btn_close_follow) {
            closeFollow();

        } else if (i == R.id.btn_follow) {
            follow();

        } else if (i == R.id.btn_option_more) {
            showMoreOptionDialog();
        } else if (i == R.id.btn_gift) {
            giftClick();
        } else if (i == R.id.btn_video) {
            if (IntimacyBelowLevel()) {
                return;
            }
            if (mActionListener != null) {
                mActionListener.onVideoChatClick();
            }
        } else if (i == R.id.btn_voice) {
            if (IntimacyBelowLevel()) {
                return;
            }
            if (mActionListener != null) {
                mActionListener.onVoiceChatClick();
            }
        }

    }

    /**
     * 点击礼物
     */
    private void giftClick() {
        if (!mToAuth) {
            ToastUtil.show(R.string.chat_gift_user_not_auth);
            return;
        }
        hideMore();
        if (mActionListener != null) {
            mActionListener.onGiftClick();
        }
    }


    /**
     * 更多操作弹窗
     */
    private void showMoreOptionDialog() {
        List<Integer> list = new ArrayList<>();
        list.add(R.string.im_forward_ta_home);
        if (mToAuth) {
            list.add(mFollowing ? R.string.following : R.string.follow);
        }
        list.add(mBlacking ? R.string.black_ing : R.string.black);
        if (TextUtils.isEmpty(remark)) {
            list.add(R.string.remark_setting);
        } else {
            list.add(R.string.remark_modify);
        }
        DialogUitl.showStringArrayDialog(mContext, list.toArray(new Integer[list.size()]), true, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.im_forward_ta_home) {
                    if (mContext instanceof ChatRoomActivity && ((ChatRoomActivity) mContext).isFromUserHome()) {
                        ((ChatRoomActivity) mContext).superBackPressed();
                    } else if (!mToAuth) {
                        ToastUtil.show(R.string.user_is_not_auth);
                    } else {
                        RouteUtil.forwardUserHome(mToUid);
                    }
                } else if (tag == R.string.following || tag == R.string.follow) {
                    CommonHttpUtil.setAttention(mToUid, null);
                } else if (tag == R.string.black_ing || tag == R.string.black) {
                    CommonHttpUtil.setBlack(mToUid);
                } else if (tag == R.string.remark_setting || tag == R.string.remark_modify) {
                    showInputRemark();
                }
            }
        });
    }

    /**
     * 关闭关注提示
     */
    private void closeFollow() {
        if (mFollowGroup != null && mFollowGroup.getVisibility() == View.VISIBLE) {
            mFollowGroup.setVisibility(View.GONE);
        }
    }

    /**
     * 关注
     */
    private void follow() {
        CommonHttpUtil.setAttention(mToUid, null);
    }

    /**
     * 返回
     */
    public void back() {
        if (hideMore() || hideFace() || hideSoftInput()) {
            return;
        }
        if (mActionListener != null) {
            mActionListener.onCloseClick();
        }
    }

    /**
     * 点击输入框
     */
    private void clickInput() {
        hideFace();
        hideMore();
    }


    /**
     * 显示软键盘
     */
    private void showSoftInput() {
        if (!((KeyBoardHeightChangeListener) mContext).isSoftInputShowed() && imm != null && mEditText != null) {
            imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
            mEditText.requestFocus();
        }
    }

    /**
     * 隐藏键盘
     */
    private boolean hideSoftInput() {
        if (((KeyBoardHeightChangeListener) mContext).isSoftInputShowed() && imm != null && mEditText != null) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    /**
     * 点击表情按钮
     */
    private void faceClick() {
        if (IntimacyBelowLevel()) {
            mBtnFace.setChecked(false);
            return;
        }
        hideMore();
        if (mBtnFace.isChecked()) {
            hideSoftInput();
            hideVoiceRecord();
            if (mHandler != null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showFace();
                    }
                }, 200);
            }
        } else {
            hideFace();
            showSoftInput();
        }
    }

    /**
     * 表情弹窗是否显示
     */
    private boolean isFaceShowing() {
        return mFaceContainer != null && mFaceContainer.getVisibility() != View.GONE;
    }

    /**
     * 更多弹窗是否显示
     */
    private boolean isMoreShowing() {
        return mMoreContainer != null && mMoreContainer.getVisibility() != View.GONE;
    }


    /**
     * 显示表情弹窗
     */
    private void showFace() {
        if (isFaceShowing()) {
            return;
        }
        hideMore();
        if (mFaceView == null) {
            mFaceView = initFaceView();
            mFaceContainer.addView(mFaceView);
        }
        mFaceContainer.setVisibility(View.VISIBLE);
        scrollToBottom();
    }

    /**
     * 隐藏表情弹窗
     */
    private boolean hideFace() {
        if (isFaceShowing()) {
            mFaceContainer.setVisibility(View.GONE);
            if (mBtnFace != null) {
                mBtnFace.setChecked(false);
            }
            return true;
        }
        return false;
    }


    /**
     * 点击更多按钮
     */
    private void clickMore() {

//        if (IntimacyBelowLevel()) {
//            return;
//        }
        hideFace();
        hideSoftInput();
        hideVoiceRecord();
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showMore();
                }
            }, 200);
        }
    }

    /**
     * 显示更多弹窗
     */
    private void showMore() {
        if (isMoreShowing()) {
            return;
        }
        hideFace();
        if (mMoreView == null) {
            mMoreView = initMoreView();
            mMoreContainer.addView(mMoreView);
        }
        mMoreContainer.setVisibility(View.VISIBLE);
        scrollToBottom();
    }

    /**
     * 隐藏更多弹窗
     */
    private boolean hideMore() {
        if (isMoreShowing()) {
            mMoreContainer.setVisibility(View.GONE);
            return true;
        }
        return false;
    }


    /**
     * 点击表情图标按钮
     */
    @Override
    public void onFaceClick(String str, int faceImageRes) {
        if (mEditText != null) {
            Editable editable = mEditText.getText();
            editable.insert(mEditText.getSelectionStart(), ImTextRender.getFaceImageSpan(str, faceImageRes));
        }
    }

    /**
     * 点击表情删除按钮
     */
    @Override
    public void onFaceDeleteClick() {
        if (mEditText != null) {
            int selection = mEditText.getSelectionStart();
            String text = mEditText.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1, selection);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[", selection);
                    if (start >= 0) {
                        mEditText.getText().delete(start, selection);
                    } else {
                        mEditText.getText().delete(selection - 1, selection);
                    }
                } else {
                    mEditText.getText().delete(selection - 1, selection);
                }
            }
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        ImHttpUtil.cancel(ImHttpConsts.CHECK_IM);
        ImHttpUtil.cancel(ImHttpConsts.CHARGE_SEND_IM);
        mAdapter = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mMediaRecordUtil != null) {
            mMediaRecordUtil.release();
        }
        mMediaRecordUtil = null;
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.destroy();
        }
        mVoiceMediaPlayerUtil = null;
        if (mAdapter != null) {
            mAdapter.release();
        }
        ImMessageUtil.getInstance().refreshAllUnReadMsgCount();
        EventBus.getDefault().unregister(this);
        mActionListener = null;
        if (mChatImageDialog != null) {
            mChatImageDialog.dismiss();
        }
        mChatImageDialog = null;
    }

    /**
     * 点击图片的回调，显示图片
     */
    @Override
    public void onImageClick(MyImageView imageView, int x, int y) {
        if (mAdapter == null || imageView == null) {
            return;
        }
        hideSoftInput();
        File imageFile = imageView.getFile();
        ImMessageBean imMessageBean = imageView.getImMessageBean();
        if (imageFile != null && imMessageBean != null) {
            mChatImageDialog = new ChatImageDialog(mContext, mParentView);
            mChatImageDialog.show(mAdapter.getChatImageBean(imMessageBean), imageFile, x, y, imageView.getWidth(), imageView.getHeight(), imageView.getDrawable());
        }
    }


    /**
     * 点击语音消息的回调，播放语音
     */
    @Override
    public void onVoiceStartPlay(File voiceFile) {
        if (mVoiceMediaPlayerUtil == null) {
            mVoiceMediaPlayerUtil = new VoiceMediaPlayerUtil(mContext);
            mVoiceMediaPlayerUtil.setActionListener(new VoiceMediaPlayerUtil.ActionListener() {
                @Override
                public void onPlayEnd() {
                    if (mAdapter != null) {
                        mAdapter.stopVoiceAnim();
                    }
                }
            });
        }
        mVoiceMediaPlayerUtil.startPlay(voiceFile.getAbsolutePath());
    }

    /**
     * 点击语音消息的回调，停止播放语音
     */
    @Override
    public void onVoiceStopPlay() {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.stopPlay();
        }
    }


    /**
     * 隐藏录音
     */
    private void hideVoiceRecord() {
        if (mBtnVoice != null && mBtnVoice.isChecked()) {
            mBtnVoice.setChecked(false);
            if (mEditText.getVisibility() != View.VISIBLE) {
                mEditText.setVisibility(View.VISIBLE);
                mEditText.requestFocus();
            }
            if (mVoiceRecordEdit.getVisibility() == View.VISIBLE) {
                mVoiceRecordEdit.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 点击录音
     */
    public void clickVoiceRecord() {
        if (mBtnVoice == null) {
            return;
        }
        if (mBtnVoice.isChecked()) {
            hideSoftInput();
            hideFace();
            hideMore();
            if (mEditText.getVisibility() == View.VISIBLE) {
                mEditText.setVisibility(View.INVISIBLE);
            }
            if (mVoiceRecordEdit != null && mVoiceRecordEdit.getVisibility() != View.VISIBLE) {
                mVoiceRecordEdit.setVisibility(View.VISIBLE);
            }
        } else {
            if (mVoiceRecordEdit != null && mVoiceRecordEdit.getVisibility() == View.VISIBLE) {
                mVoiceRecordEdit.setVisibility(View.INVISIBLE);
            }
            if (mEditText.getVisibility() != View.VISIBLE) {
                mEditText.setVisibility(View.VISIBLE);
                mEditText.requestFocus();
            }
        }
    }

    /**
     * 开始录音
     */
    public void startRecordVoice() {
        if (mVoiceRecordEdit == null) {
            return;
        }
        mVoiceRecordEdit.setBackground(mVoicePressedDrawable);
        mVoiceRecordEdit.setText(mUnPressStopString);
        if (mMediaRecordUtil == null) {
            mMediaRecordUtil = new MediaRecordUtil();
        }
        File dir = new File(CommonAppConfig.MUSIC_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mRecordVoiceFile = new File(dir, DateFormatUtil.getCurTimeString() + ".m4a");
        mMediaRecordUtil.startRecord(mRecordVoiceFile.getAbsolutePath());
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecordVoice();
                }
            }, 60000);
        }
    }

    /**
     * 结束录音
     */
    private void stopRecordVoice() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mVoiceRecordEdit == null) {
            return;
        }
        mVoiceRecordEdit.setBackground(mVoiceUnPressedDrawable);
        mVoiceRecordEdit.setText(mPressSayString);
        mRecordVoiceDuration = mMediaRecordUtil.stopRecord();
        if (mRecordVoiceDuration < 2000) {
            ToastUtil.show(WordUtil.getString(R.string.im_record_audio_too_short));
            deleteVoiceFile();
        } else {
            mCurMessageBean = ImMessageUtil.getInstance().createVoiceMessage(mToUid, mRecordVoiceFile, mRecordVoiceDuration);
            if (mCurMessageBean != null) {
                sendMessage();
            } else {
                deleteVoiceFile();
            }
        }
    }

    /**
     * 删除录音文件
     */
    private void deleteVoiceFile() {
        if (mRecordVoiceFile != null && mRecordVoiceFile.exists()) {
            mRecordVoiceFile.delete();
        }
        mRecordVoiceFile = null;
        mRecordVoiceDuration = 0;
    }

    /**************************************************************************************************/
    /*********************************以上是处理界面逻辑，以下是处理消息逻辑***********************************/
    /**************************************************************************************************/

    /**
     * 刷新最后一条聊天数据
     */
    public void refreshLastMessage() {
        if (mAdapter != null) {
            ImMessageBean bean = mAdapter.getLastMessage();
            if (bean != null) {
                ImMessageUtil.getInstance().refreshLastMessage(mToUid, bean);
            }
        }
    }


    /**
     * 收到消息的回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImMessageBean(ImMessageBean bean) {
        if (!bean.getUid().equals(mToUid)) {
            return;
        }
        if (mAdapter != null) {
            mAdapter.insertItem(bean);
            ImMessageUtil.getInstance().markAllMessagesAsRead(mToUid, false);
        }
        if (mContext instanceof ChatRoomActivity && bean.getGiftBean() != null) {
            ((ChatRoomActivity) mContext).showGift(bean.getGiftBean());
            //收到礼物消息后刷新亲密度等级
            getIntimacyLevel();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (e.getToUid().equals(mToUid)) {
            mFollowing = e.getIsAttention() == 1;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBlackEvent(BlackEvent e) {
        if (e.getToUid().equals(mToUid)) {
            mBlacking = e.getIsBlack() == 1;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImRemoveAllMsgEvent(ImRemoveAllMsgEvent e) {
        if (e.getToUid().equals(mToUid)) {
            if (mAdapter != null) {
                mAdapter.clear();
            }
        }
    }


    /**
     * 检查是否能够发送消息
     */
    private boolean isCanSendMsg() {
        if (!CommonAppConfig.getInstance().isLoginIM()) {
            ToastUtil.show("IM暂未接入，无法使用");
            return false;
        }
//        long curTime = System.currentTimeMillis();
//        if (curTime - mLastSendTime < 1500) {
//            ToastUtil.show(R.string.im_send_too_fast);
//            return false;
//        }
//        mLastSendTime = curTime;
        return true;
    }

    /**
     * 发送文本信息
     */
    public void sendText(String content) {
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show(R.string.content_empty);
            return;
        }
        ImMessageBean messageBean = ImMessageUtil.getInstance().createTextMessage(mToUid, content);
        if (messageBean == null) {
            ToastUtil.show(R.string.im_msg_send_failed);
            return;
        }
        mCurMessageBean = messageBean;
        sendMessage();
    }

    /**
     * 发送文本信息
     */
    private void sendText() {
        String content = mEditText.getText().toString().trim();
        checkText(content);
    }

    /**
     * 文本内容检测 -- 自定义功能看是否涉及内容违规
     */
    private void checkText(String content) {
        ImHttpUtil.checkImText(content, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                //0 内容正常
                if (code == 0) {
                    sendText(content);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 发送图片消息
     */
    public void sendImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        ImMessageBean messageBean = ImMessageUtil.getInstance().createImageMessage(mToUid, path);
        if (messageBean == null) {
            ToastUtil.show(R.string.im_msg_send_failed);
            return;
        }
        mCurMessageBean = messageBean;
        sendMessage();
    }

    /**
     * 发送位置消息
     */
    public void sendLocation(double lat, double lng, int scale, String address) {
        ImMessageBean messageBean = ImMessageUtil.getInstance().createLocationMessage(mToUid, lat, lng, scale, address);
        if (messageBean == null) {
            ToastUtil.show(R.string.im_msg_send_failed);
            return;
        }
        mCurMessageBean = messageBean;
        sendMessage();
    }


    /**
     * 发送消息
     */
    private void sendMessage() {
        if (!isCanSendMsg()) {
            return;
        }
        //当前用户为女用户时直接发送消息
        if (CommonAppConfig.getInstance().getUserBean().getSex() == 2) {
            sendCurMessage();
            return;
        }
        if (mCurMessageBean != null) {
            //判断自己有没有被对方拉黑
            ImHttpUtil.checkIm(mToUid, mCheckBlackCallback);
        } else {
            ToastUtil.show(R.string.im_msg_send_failed);
        }

    }


    private void sendCurMessage() {
        if (mCurMessageBean != null) {
            if (mCurMessageBean.getType() == ImMessageBean.TYPE_TEXT) {
                mEditText.setText("");
            }
            if (mCurMessageBean.getType() == ImMessageBean.TYPE_IMAGE) {
                String path = ((TIMImageElem) mCurMessageBean.getTimRawMessage().getElement(0)).getPath();

                L.e("消息体： " + path);

                uploadImage(mCurMessageBean, path);
            } else if (mAdapter != null) {
                mAdapter.insertSelfItem(mCurMessageBean);
            }
        } else {
            ToastUtil.show(WordUtil.getString(R.string.im_msg_send_failed));
        }
    }


    /**
     * 处理拉黑接口返回的数据
     */
    private void processCheckBlackData(int code, String msg, String[] info) {
        //0 正常状态
        if (code == 0) {
            chargeSendIm();
        } else {
            // 900 非VIP用户
            if (code == 900) {
                new DialogUitl.Builder(mContext)
                        .setContent(msg)
                        .setCancelable(true)
                        .setBackgroundDimEnabled(true)
                        .setCancelString(WordUtil.getString(R.string.cancel))
                        .setConfrimString(WordUtil.getString(R.string.im_charge_send))
                        .setClickCallback(new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {
//                                RouteUtil.forwardVip();
                            }

                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                chargeSendIm();
                            }
                        })
                        .build()
                        .show();
            } else {
                ToastUtil.show(msg);
            }
        }
    }

    /**
     * 付费发送
     */
    private void chargeSendIm() {
        if (mChargeSendCallback == null) {
            mChargeSendCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        sendCurMessage();
                    } else if (code == 1003) {
                        ToastUtil.show(R.string.chat_coin_not_enough);
                        RouteUtil.forwardMyCoin();
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            };
        }
        ImHttpUtil.chargeSendIm(mToUid, mChargeSendCallback);
    }


    @Override
    public void onPause() {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.pausePlay();
        }
    }

    @Override
    public void onResume() {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.resumePlay();
        }
    }

    /**
     * 获取当前用户和对方的亲密度等级
     */
    private void getIntimacyLevel() {
        if (TextUtils.isEmpty(mToUid) || TextUtils.equals("admin", mToUid)) {
            return;
        }
        ImHttpUtil.getIntimacyLevel(mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mIntimacyLevel = obj.getIntValue("intimacy_level");
                    tvIntimacyLevel.setText(WordUtil.getString(R.string.intimacy_level, mIntimacyLevel));
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 判断和对方用户亲密度高于低于 1 级
     * 亲密度低于 1 级仅能发送文字消息
     */
    private boolean IntimacyBelowLevel() {
        if (mIntimacyLevel < 1) {
            ToastUtil.show("和对方用户亲密度低于1级仅能发送文字消息");
            return true;
        }
        return false;
    }


    /**
     * 备注设置输入弹框
     */
    private void showInputRemark() {
        DialogUitl.showInputNick(mContext, "", remark, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(final Dialog dialog, final String content) {
                ImHttpUtil.settingRemark(mToUid, content, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            dialog.dismiss();
                            remark = content;
                            mTitleView.setText(remark);
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }
        });
    }

    /**
     * 获取用户备注
     */
    private void getRemark() {
        if (TextUtils.isEmpty(mToUid) || TextUtils.equals("admin", mToUid)) {
            return;
        }
        ImHttpUtil.getRemark(mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    remark = obj.getString("toremark");
                    if (!TextUtils.isEmpty(remark)) {
                        mTitleView.setText(remark);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    /**
     * 文件上传七牛云
     *
     * @param path 文件路径
     */
    private void uploadImage(ImMessageBean messageBean, String path) {
        UploadBean uploadBean = new UploadBean();
        uploadBean.setOriginFile(new File(path));
        uploadBean.setRemoteFileName(StringUtil.generateCensorFileName());
        if (uploadCensor == null) {
            uploadCensor = new UploadCensorImpl(mContext);
        }
        Dialog dialog = DialogUitl.loadingDialog(mContext);
        dialog.show();
        uploadCensor.upload(uploadBean, true, new UploadCallbackSignle() {
            @Override
            public void onFinish(UploadBean bean, boolean success) {
                if (success) {
                    L.e("上传文件完成---------> " + bean.getRemoteAccessUrl());
                    ImHttpUtil.getVerifyContent(bean.getRemoteAccessUrl(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                CensorBean censorBean = JSONObject.parseObject(info[0], CensorBean.class);
                                if (censorBean.getResult().isBlock()) {
                                    ToastUtil.show("内容违规");
                                } else if (mAdapter != null) {
                                    mAdapter.insertSelfItem(messageBean);
                                }
                            } else if (mAdapter != null) {
                                mAdapter.insertSelfItem(messageBean);
                            }
                            dialog.dismiss();
                        }
                    });
                } else {
                    dialog.dismiss();
                    L.e("文件上传七牛云失败");
                    ToastUtil.show("信息发送失败");
                }
            }
        });
    }
}
