package com.aihuan.video.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.Constants;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.dialog.AbsDialogFragment;
import com.aihuan.common.dialog.ChatFaceDialog;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.video.R;
import com.aihuan.video.activity.AbsVideoCommentActivity;
import com.aihuan.video.bean.VideoCommentBean;
import com.aihuan.video.custom.AtEditText;
import com.aihuan.video.event.VideoCommentEvent;
import com.aihuan.video.http.VideoHttpConsts;
import com.aihuan.video.http.VideoHttpUtil;
import com.aihuan.video.utils.VideoTextRender;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/12/3.
 * 视频评论输入框
 */

public class VideoInputDialogFragment2 extends AbsDialogFragment implements View.OnClickListener, ChatFaceDialog.ActionListener {

    private InputMethodManager imm;
    private AtEditText mInput;
    private boolean mOpenFace;
    private int mOriginHeight;
    private int mFaceHeight;
    private CheckBox mCheckBox;
    private ChatFaceDialog mChatFaceDialog;
    private Handler mHandler;
    private String mVideoId;
    private String mVideoUid;
    private VideoCommentBean mVideoCommentBean;
    private boolean mDarkStyle;

    @Override
    protected int getLayoutId() {
        return mDarkStyle ? R.layout.dialog_video_input_dark : R.layout.dialog_video_input_light;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        mOriginHeight = DpUtil.dp2px(48);
        params.height = mOriginHeight;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mHandler = new Handler();
        mInput = (AtEditText) mRootView.findViewById(R.id.input);
        mInput.setOnClickListener(this);
        mInput.setActionListener(new AtEditText.ActionListener() {
            @Override
            public void onAtClick() {
                clickAt();
            }

            @Override
            public void onContainsUid() {
                ToastUtil.show(WordUtil.getString(R.string.video_at_tip_1));
            }

            @Override
            public void onContainsName() {
                ToastUtil.show(WordUtil.getString(R.string.video_at_tip_2));
            }
        });
        mRootView.findViewById(R.id.btn_at).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_voice_input).setOnClickListener(this);
        mCheckBox = mRootView.findViewById(R.id.btn_face);
        mCheckBox.setOnClickListener(this);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendComment();
                    return true;
                }
                return false;
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            mOpenFace = bundle.getBoolean(Constants.VIDEO_FACE_OPEN, false);
            mFaceHeight = bundle.getInt(Constants.VIDEO_FACE_HEIGHT, 0);
            mVideoCommentBean = bundle.getParcelable(Constants.VIDEO_COMMENT_BEAN);
            if (mVideoCommentBean != null) {
                UserBean replyUserBean = mVideoCommentBean.getUserBean();//要回复的人
                if (replyUserBean != null) {
                    mInput.setHint(WordUtil.getString(R.string.video_comment_reply) + replyUserBean.getUserNiceName());
                }
            }
            String atUid = bundle.getString(Constants.TO_UID);
            String atName = bundle.getString(Constants.TO_NAME);
            if (!TextUtils.isEmpty(atUid) && !TextUtils.isEmpty(atName)) {
                addSpan(atUid, atName);
            }
        }
        if (mOpenFace) {
            if (mCheckBox != null) {
                mCheckBox.setChecked(true);
            }
            if (mFaceHeight > 0) {
                changeHeight(mFaceHeight);
                if (mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showFace();
                        }
                    }, 200);
                }
            }
        } else {
            if (mHandler != null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showSoftInput();
                    }
                }, 200);
            }
        }
    }


    public void setVideoInfo(String videoId, String videoUid) {
        mVideoId = videoId;
        mVideoUid = videoUid;
    }

    public void showSoftInputDelay() {
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSoftInput();
                }
            }, 200);
        }
    }


    private void showSoftInput() {
        //软键盘弹出
        if (imm != null) {
            imm.showSoftInput(mInput, InputMethodManager.SHOW_FORCED);
        }
        if (mInput != null) {
            mInput.requestFocus();
        }
    }

    public void hideSoftInput() {
        if (imm != null) {
            imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        }
    }


    @Override
    public void onDestroy() {
        if (mContext != null) {
            ((AbsVideoCommentActivity) mContext).releaseVideoInputDialog();
        }
        mContext = null;
        VideoHttpUtil.cancel(VideoHttpConsts.SET_COMMENT);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mChatFaceDialog != null) {
            mChatFaceDialog.dismiss();
        }
        mChatFaceDialog = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_face) {
            clickFace();
        } else if (i == R.id.input) {
            clickInput();
        } else if (i == R.id.btn_at) {
            clickAt();
        } else if (i == R.id.btn_voice_input) {
            clickVoice();
        }
    }


    /**
     * 点击语音
     */
    public void clickVoice() {
        dismiss();
        ((AbsVideoCommentActivity) mContext).showVoiceViewHolder(mVideoId, mVideoUid, mVideoCommentBean);
    }

    /**
     * 点击@
     */
    public void clickAt() {
        if (mCheckBox.isChecked()) {
            hideSoftInput();
        }
        ((AbsVideoCommentActivity) mContext).forwardAtFriend(mVideoId, mVideoUid);
    }


    private void clickInput() {
        hideFace();
        if (mCheckBox != null) {
            mCheckBox.setChecked(false);
        }
    }

    private void clickFace() {
        if (mCheckBox.isChecked()) {
            hideSoftInput();
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

    private void showFace() {
        if (mFaceHeight > 0) {
            changeHeight(mFaceHeight);
            View faceView = ((AbsVideoCommentActivity) mContext).getFaceView();
            if (faceView != null) {
                mChatFaceDialog = new ChatFaceDialog(mRootView, faceView, false, VideoInputDialogFragment2.this);
                mChatFaceDialog.show();
            }
        }
    }

    private void hideFace() {
        if (mChatFaceDialog != null) {
            mChatFaceDialog.dismiss();
        }
    }

    /**
     * 改变高度
     */
    private void changeHeight(int deltaHeight) {
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = mOriginHeight + deltaHeight;
        window.setAttributes(params);
    }

    @Override
    public void onFaceDialogDismiss() {
        changeHeight(0);
        mChatFaceDialog = null;
    }

    /**
     * 发表评论
     */
    public void sendComment() {
        if (TextUtils.isEmpty(mVideoId) || TextUtils.isEmpty(mVideoUid) || mInput == null || !canClick()) {
            return;
        }
        String content = mInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show(R.string.content_empty);
            return;
        }
        String toUid = mVideoUid;
        String commentId = "0";
        String parentId = "0";
        if (mVideoCommentBean != null) {
            toUid = mVideoCommentBean.getUid();
            commentId = mVideoCommentBean.getCommentId();
            parentId = mVideoCommentBean.getId();
        }
        String atInfo = mInput.getAtUserInfo();
        VideoHttpUtil.setComment(toUid, mVideoId, content, commentId, parentId, atInfo, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mInput != null) {
                        mInput.setText("");
                    }
                    JSONObject obj = JSON.parseObject(info[0]);
                    String commentNum = obj.getString("comments");
                    EventBus.getDefault().post(new VideoCommentEvent(mVideoId, commentNum));
                    ToastUtil.show(msg);
                    dismiss();
                    ((AbsVideoCommentActivity) mContext).refreshComment();
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext);
            }
        });
    }

    /**
     * 点击表情上面的删除按钮
     */
    public void onFaceDeleteClick() {
        if (mInput != null) {
            int selection = mInput.getSelectionStart();
            String text = mInput.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1, selection);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[", selection);
                    if (start >= 0) {
                        mInput.getText().delete(start, selection);
                    } else {
                        if (!mInput.deleteAtSpan()) {
                            mInput.getText().delete(selection - 1, selection);
                        }
                    }
                } else {
                    if (!mInput.deleteAtSpan()) {
                        mInput.getText().delete(selection - 1, selection);
                    }
                }
            }
        }
    }

    /**
     * 点击表情
     */
    public void onFaceClick(String str, int faceImageRes) {
        if (mInput != null) {
            Editable editable = mInput.getText();
            editable.insert(mInput.getSelectionStart(), VideoTextRender.getFaceImageSpan(str, faceImageRes));
        }
    }

    public void setDarkStyle(boolean darkStyle) {
        mDarkStyle = darkStyle;
    }


    public void addSpan(String uid, String name) {
        if (mInput != null) {
            mInput.addAtSpan(uid, name);
        }
    }

}
