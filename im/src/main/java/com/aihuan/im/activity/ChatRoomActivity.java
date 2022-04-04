package com.aihuan.im.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.bean.ChatAnchorParam;
import com.aihuan.common.bean.ChatAudienceParam;
import com.aihuan.common.bean.ChatReceiveGiftBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.ActivityResultCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.interfaces.ImageResultCallback;
import com.aihuan.common.interfaces.KeyBoardHeightChangeListener;
import com.aihuan.common.presenter.GiftAnimViewHolder;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.KeyBoardHeightUtil;
import com.aihuan.common.utils.ProcessImageUtil;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.R;
import com.aihuan.im.dialog.ChatGiftDialogFragment;
import com.aihuan.im.http.ImHttpUtil;
import com.aihuan.im.interfaces.ChatRoomActionListener;
import com.aihuan.im.utils.ImMessageUtil;
import com.aihuan.im.views.ChatRoomViewHolder;

import java.io.File;

/**
 * Created by cxf on 2018/10/24.
 */

public class ChatRoomActivity extends AbsActivity implements KeyBoardHeightChangeListener, ChatGiftDialogFragment.ActionListener {

    private ViewGroup mRoot;
    private ViewGroup mContianer;
    private ChatRoomViewHolder mChatRoomViewHolder;
    private KeyBoardHeightUtil mKeyBoardHeightUtil;
    private ProcessImageUtil mImageUtil;
    private boolean mPaused;
    private UserBean mToUserBean;
    private GiftAnimViewHolder mGiftAnimViewHolder;
    private int mChatType = Constants.CHAT_TYPE_NONE;
    private boolean mFromUserHome;

    public static void forward(Context context, UserBean userBean, boolean following, boolean blacking, boolean auth, boolean fromUserHome) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(Constants.USER_BEAN, userBean);
        intent.putExtra(Constants.FOLLOW, following);
        intent.putExtra(Constants.BLACK, blacking);
        intent.putExtra(Constants.AUTH_STATUS, auth);
        intent.putExtra(Constants.IM_FROM_HOME, fromUserHome);
        context.startActivity(intent);
    }

    public static void forwardAccost(Context context, UserBean userBean, boolean isAccost, boolean following, boolean blacking, boolean auth, boolean fromUserHome) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(Constants.USER_BEAN, userBean);
        intent.putExtra(Constants.FOLLOW, following);
        intent.putExtra(Constants.BLACK, blacking);
        intent.putExtra(Constants.AUTH_STATUS, auth);
        intent.putExtra(Constants.IM_FROM_HOME, fromUserHome);
        intent.putExtra(Constants.IS_ACCOST, isAccost);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_room;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        UserBean userBean = intent.getParcelableExtra(Constants.USER_BEAN);
        if (userBean == null) {
            return;
        }
        mToUserBean = userBean;
        boolean following = intent.getBooleanExtra(Constants.FOLLOW, false);
        boolean blacking = intent.getBooleanExtra(Constants.BLACK, false);
        boolean auth = intent.getBooleanExtra(Constants.AUTH_STATUS, false);
        boolean isAccost = intent.getBooleanExtra(Constants.IS_ACCOST, false);
        mFromUserHome = intent.getBooleanExtra(Constants.IM_FROM_HOME, false);
        mRoot = (ViewGroup) findViewById(R.id.root);
        mContianer = (ViewGroup) findViewById(R.id.container);
        mChatRoomViewHolder = new ChatRoomViewHolder(mContext, mContianer, userBean, following, blacking, auth, isAccost);
        mChatRoomViewHolder.setActionListener(new ChatRoomActionListener() {
            @Override
            public void onCloseClick() {
                superBackPressed();
            }

            @Override
            public void onPopupWindowChanged(final int height) {
                onKeyBoardChanged(height);
            }

            @Override
            public void onChooseImageClick() {
                checkReadWritePermissions();
                //chooseImage();
            }

            @Override
            public void onCameraClick() {
                takePhoto();
            }

            @Override
            public void onVoiceInputClick() {
//                checkVoiceRecordPermission(new Runnable() {
//                    @Override
//                    public void run() {
//                        openVoiceInputDialog();
//                    }
//                });
            }

            @Override
            public void onLocationClick() {
                checkLocationPermission();
            }

            @Override
            public void onVoiceClick() {
                checkVoiceRecordPermission(new CommonCallback<Boolean>() {
                    @Override
                    public void callback(Boolean result) {
                        if (result && mChatRoomViewHolder != null) {
                            mChatRoomViewHolder.clickVoiceRecord();
                        }
                    }
                });
            }

            @Override
            public ViewGroup getImageParentView() {
                return mRoot;
            }

            @Override
            public void onGiftClick() {
                if (mToUserBean == null) {
                    return;
                }
                ChatGiftDialogFragment fragment = new ChatGiftDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.LIVE_UID, mToUserBean.getId());
                bundle.putString(Constants.CHAT_SESSION_ID, "0");
                fragment.setArguments(bundle);
                fragment.setActionListener(ChatRoomActivity.this);
                fragment.show(getSupportFragmentManager(), "ChatGiftDialogFragment");
            }

            @Override
            public void onVideoChatClick() {
                mChatType = Constants.CHAT_TYPE_VIDEO;
                checkChatPermissions();
            }

            @Override
            public void onVoiceChatClick() {
                mChatType = Constants.CHAT_TYPE_VOICE;
                checkChatPermissions();
            }

//            @Override
//            public void onImageClick() {
//
//            }

        });
        mChatRoomViewHolder.addToParent();
        mChatRoomViewHolder.loadData();
        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (mChatRoomViewHolder != null) {
                    mChatRoomViewHolder.sendImage(file.getAbsolutePath());
                }
            }

            @Override
            public void onFailure() {

            }
        });
        mKeyBoardHeightUtil = new KeyBoardHeightUtil(mContext, findViewById(android.R.id.content), this);
        mRoot.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mKeyBoardHeightUtil != null) {
                    mKeyBoardHeightUtil.start();
                }
            }
        }, 500);
        ImMessageUtil.getInstance().setOpenChatActivity(true);
    }

    private void onKeyBoardChanged(int keyboardHeight) {

        if (mRoot != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRoot.getLayoutParams();
            if (params.bottomMargin != keyboardHeight) {
                params.bottomMargin = keyboardHeight;
                mRoot.setLayoutParams(params);
                if (mChatRoomViewHolder != null) {
                    mChatRoomViewHolder.scrollToBottom();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.back();
        } else {
            superBackPressed();
        }
        ImMessageUtil.getInstance().setOpenChatActivity(false);
    }

    private void release() {
        if (mKeyBoardHeightUtil != null) {
            mKeyBoardHeightUtil.release();
        }
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.refreshLastMessage();
            mChatRoomViewHolder.release();
        }
        if (mImageUtil != null) {
            mImageUtil.release();
        }
        mKeyBoardHeightUtil = null;
        mChatRoomViewHolder = null;
        mImageUtil = null;
    }

    public void superBackPressed() {
        release();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        release();
        ImMessageUtil.getInstance().setOpenChatActivity(false);
        super.onDestroy();
    }


    @Override
    public void onKeyBoardHeightChanged(int visibleHeight, int keyboardHeight) {
        if (mPaused) {
            return;
        }
        onKeyBoardChanged(keyboardHeight);
    }

    @Override
    public boolean isSoftInputShowed() {
        if (mKeyBoardHeightUtil != null) {
            return mKeyBoardHeightUtil.isSoftInputShowed();
        }
        return false;
    }

    /**
     * 聊天时候选择图片，检查读写权限
     */
    private void checkReadWritePermissions() {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.requestPermissions(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new CommonCallback<Boolean>() {
                    @Override
                    public void callback(Boolean result) {
                        if (result) {
                            forwardChooseImage();
                        }
                    }
                });
    }

    /**
     * 前往选择图片页面
     */
    private void forwardChooseImage() {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.startActivityForResult(new Intent(mContext, ChatChooseImageActivity.class), new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String imagePath = intent.getStringExtra(Constants.SELECT_IMAGE_PATH);
                    if (mChatRoomViewHolder != null) {
                        mChatRoomViewHolder.sendImage(imagePath);
                    }
                }
            }
        });
    }


    /**
     * 拍照
     */
    private void chooseImage() {
        if (mImageUtil != null) {
            mImageUtil.getImageByAlumb(false);
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        if (mImageUtil != null) {
            mImageUtil.getImageByCamera(false);
        }
    }

    /**
     * 发送位置的时候检查定位权限
     */
    private void checkLocationPermission() {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.requestPermissions(
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                new CommonCallback<Boolean>() {
                    @Override
                    public void callback(Boolean result) {
                        if (result) {
                            forwardLocation();
                        }
                    }
                });
    }

    /**
     * 前往发送位置页面
     */
    private void forwardLocation() {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.startActivityForResult(new Intent(mContext, LocationActivity.class), new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    double lat = intent.getDoubleExtra(Constants.LAT, 0);
                    double lng = intent.getDoubleExtra(Constants.LNG, 0);
                    int scale = intent.getIntExtra(Constants.SCALE, 0);
                    String address = intent.getStringExtra(Constants.ADDRESS);
                    if (lat > 0 && lng > 0 && scale > 0 && !TextUtils.isEmpty(address)) {
                        if (mChatRoomViewHolder != null) {
                            mChatRoomViewHolder.sendLocation(lat, lng, scale, address);
                        }
                    } else {
                        ToastUtil.show(WordUtil.getString(R.string.im_get_location_failed));
                    }
                }
            }
        });
    }

    /**
     * 检查录音权限
     */
    private void checkVoiceRecordPermission(CommonCallback<Boolean> commonCallback) {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.requestPermissions(
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO},
                commonCallback);
    }

    /**
     * 打开语音输入窗口
     */
    private void openVoiceInputDialog() {
    }

    public boolean isFromUserHome() {
        return mFromUserHome;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.onResume();
        }
    }

    public void showGift(ChatReceiveGiftBean giftBean) {
        if (mPaused) {
            return;
        }
        if (mGiftAnimViewHolder == null) {
            mGiftAnimViewHolder = new GiftAnimViewHolder(mContext, mRoot);
            mGiftAnimViewHolder.addToParent();
        }
        mGiftAnimViewHolder.showGiftAnim(giftBean);
    }

    /**
     * 检查通话权限
     */
    private void checkChatPermissions() {
        if (mImageUtil == null) {
            return;
        }
        mImageUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
        }, new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    checkChatStatus();
                }
            }
        });
    }

    /**
     * 通话时候 用于检测两个用户间关系 0用户邀主播，1主播邀用户
     */
    private void checkChatStatus() {
        if (mToUserBean == null) {
            return;
        }
        ImHttpUtil.checkChatStatus(mToUserBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (obj.getIntValue("status") == 1) {//1主播邀用户
                        chatAncToAudStart();
                    } else {//0用户邀主播
                        chatAudToAncStart();
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 观众向主播发起通话邀请，检测主播状态，同时获取自己的推拉流地址
     */
    public void chatAudToAncStart() {
        if (mToUserBean == null) {
            return;
        }
        ImHttpUtil.chatAudToAncStart(mToUserBean.getId(), mChatType, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0 && mToUserBean != null) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        ChatAudienceParam param = new ChatAudienceParam();
                        param.setAnchorID(mToUserBean.getId());
                        param.setAnchorName(mToUserBean.getUserNiceName());
                        param.setAnchorAvatar(mToUserBean.getAvatar());
                        param.setAnchorLevel(mToUserBean.getLevelAnchor());
                        param.setSessionId(obj.getString("showid"));
                        param.setAudiencePlayUrl(obj.getString("pull"));
                        param.setAudiencePushUrl(obj.getString("push"));
                        param.setAnchorPrice(obj.getString("total"));
                        param.setChatType(obj.getIntValue("type"));
                        param.setAudienceActive(true);
                        RouteUtil.forwardAudienceActivity(param);
                    }
                } else if (code == 800) {
                    if (mContext != null) {
                        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.chat_not_response), new DialogUitl.SimpleCallback() {
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                ImHttpUtil.audSubscribeAnc(mToUserBean.getId(), mChatType);
                                ToastUtil.show(R.string.chat_subcribe_success);
                            }
                        });
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 主播向观众发起通话邀请，检测观众状态，同时获取自己的推拉流地址
     */
    public void chatAncToAudStart() {
        if (mToUserBean == null) {
            return;
        }
        ImHttpUtil.chatAncToAudStart2(mToUserBean.getId(), mChatType, new HttpCallback() {

            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0 && mToUserBean != null) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        ChatAnchorParam param = new ChatAnchorParam();
                        param.setAudienceID(mToUserBean.getId());
                        param.setAudienceName(mToUserBean.getUserNiceName());
                        param.setAudienceAvatar(mToUserBean.getAvatar());
                        param.setSessionId(obj.getString("showid"));
                        param.setAnchorPlayUrl(obj.getString("pull"));
                        param.setAnchorPushUrl(obj.getString("push"));
                        param.setPrice(obj.getString("total"));
                        param.setChatType(obj.getIntValue("type"));
                        param.setAnchorActive(true);
                        RouteUtil.forwardAnchorActivity(param);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }

        });
    }


    @Override
    public void onChargeClick() {
        RouteUtil.forwardMyCoin();
    }
}
