package com.aihuan.main.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.ChatAnchorParam;
import com.aihuan.common.bean.ChatAudienceParam;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.utils.DialogUitl;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.ProcessResultUtil;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.activity.ChatRoomActivity;
import com.aihuan.im.http.ImHttpUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.MainHomeOnlineAdapter;
import com.aihuan.main.dialog.MainFilterDialogFragment;
import com.aihuan.main.http.MainHttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 首页 附近
 */

public class MainHomeOnlineViewHolder extends AbsMainHomeChildViewHolder implements MainHomeOnlineAdapter.ItemPartClickListener, MainFilterDialogFragment.ActionListener {

    private CommonRefreshView mRefreshView;
    private MainHomeOnlineAdapter mAdapter;
    private int mSex = 0;
    private byte mFilterSex;
    private byte mFilterChatType;
    private boolean mAuth;//是否已经认证
    private ProcessResultUtil mProcessResultUtil;

    private View mChooseCallTypeView;
    private PopupWindow mPopupWindow;
    private View mRoot;
    private UserBean mSelectedUserBean;

    public MainHomeOnlineViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
        UserBean user = CommonAppConfig.getInstance().getUserBean();
        if (user != null) {
            if (user.getSex() == 2) {
                mSex = Constants.MAIN_SEX_MALE;
            } else if (user.getSex() == 1) {
                mSex = Constants.MAIN_SEX_FAMALE;
            } else {
                mSex = Constants.MAIN_SEX_NONE;
            }
        }
        L.e("当前用户性别：" + mSex);
        mRoot = parentView;
        mFilterSex = Constants.MAIN_SEX_NONE;
        mFilterChatType = Constants.CHAT_TYPE_NONE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_near;
    }

    @Override
    public void init() {
        mProcessResultUtil = new ProcessResultUtil(((AbsActivity) mContext));
        mAuth = CommonAppConfig.getInstance().getUserBean().hasAuth();
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_no_online);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<UserBean>() {
            @Override
            public RefreshAdapter<UserBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeOnlineAdapter(mContext, mAuth);
                    mAdapter.setItemPartClickListener(MainHomeOnlineViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (mAdapter != null) {
                    mAdapter.updateIsAuth(mAuth);
                }
                MainHttpUtil.getOnlineList(mSex, p, callback);
            }

            @Override
            public List<UserBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), UserBean.class);
            }

            @Override
            public void onRefreshSuccess(List<UserBean> list, int count) {
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<UserBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }


    @Override
    public void loadData() {
        CommonAppConfig commonAppConfig = CommonAppConfig.getInstance();
        MainHttpUtil.getBaseInfo(commonAppConfig.getUid(), commonAppConfig.getToken(), new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if (bean != null) {
                    mAuth = CommonAppConfig.getInstance().getUserBean().hasAuth();
                }
                if (mRefreshView != null) {
                    mRefreshView.initData();
                }
            }
        });

    }


    @Override
    public void release() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        mPopupWindow = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    @Override
    public void onAvatarClick(UserBean u) {
        if (u != null) {
            ChatRoomActivity.forward(mContext, u, u.isFollowing(), u.isBlacking(), u.getAuth() == 1, false);
        }
    }

    @Override
    public void onInviteClick(UserBean bean) {
        if (bean != null) {
            mSelectedUserBean = bean;
            checkPermissions();
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
                    chooseCallType();
                }
            }
        });
    }


    /**
     * 通话时候 用于检测两个用户间关系 0用户邀主播，1主播邀用户
     */
    private void checkChatStatus() {
        if (mSelectedUserBean == null) {
            return;
        }
        ImHttpUtil.checkChatStatus(mSelectedUserBean.getId(), new HttpCallback() {
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
     * 主播向观众发起通话邀请，检测观众状态，同时获取自己的推拉流地址
     */
    public void chatAncToAudStart() {
        if (mSelectedUserBean == null) {
            return;
        }
        ImHttpUtil.chatAncToAudStart2(mSelectedUserBean.getId(), mChatType, new HttpCallback() {

            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0 && mSelectedUserBean != null) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        ChatAnchorParam param = new ChatAnchorParam();
                        param.setAudienceID(mSelectedUserBean.getId());
                        param.setAudienceName(mSelectedUserBean.getUserNiceName());
                        param.setAudienceAvatar(mSelectedUserBean.getAvatar());
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

    /**
     * 观众向主播发起通话邀请，检测主播状态，同时获取自己的推拉流地址
     */
    public void chatAudToAncStart() {
        if (mSelectedUserBean == null) {
            return;
        }
        ImHttpUtil.chatAudToAncStart(mSelectedUserBean.getId(), mChatType, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0 && mSelectedUserBean != null) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        ChatAudienceParam param = new ChatAudienceParam();
                        param.setAnchorID(mSelectedUserBean.getId());
                        param.setAnchorName(mSelectedUserBean.getUserNiceName());
                        param.setAnchorAvatar(mSelectedUserBean.getAvatar());
                        param.setAnchorLevel(mSelectedUserBean.getLevelAnchor());
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
                        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(com.aihuan.im.R.string.chat_not_response), new DialogUitl.SimpleCallback() {
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                ImHttpUtil.audSubscribeAnc(mSelectedUserBean.getId(), mChatType);
                                ToastUtil.show(com.aihuan.im.R.string.chat_subcribe_success);
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
     * 选择通话类型
     */
    private int mChatType = Constants.CHAT_TYPE_NONE;

    private void chooseCallType() {
        if (mChooseCallTypeView == null) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.view_main_home_online_choose_call, null, false);
            mChooseCallTypeView = v;
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = v.getId();
                    if (i == R.id.btn_video) {
                        mChatType = Constants.CHAT_TYPE_VIDEO;
                        checkChatStatus();
                    } else if (i == R.id.btn_voice) {
                        mChatType = Constants.CHAT_TYPE_VOICE;
                        checkChatStatus();
                    } else if (i == R.id.btn_cancel) {
                        //隐藏弹窗
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
     * 筛选
     */
    public void onFilterClick(boolean showCallType) {
        MainFilterDialogFragment fragment = new MainFilterDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("showCallType", showCallType);
        bundle.putByte(Constants.MAIN_SEX, mFilterSex);
        bundle.putByte(Constants.CHAT_TYPE, mFilterChatType);
        fragment.setArguments(bundle);
        fragment.setActionListener(this);
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "MainFilterDialogFragment");
    }

    @Override
    public void onFilter(byte sex, byte chatType) {
        mFilterSex = sex;
//        mSex = sex;
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }
}
