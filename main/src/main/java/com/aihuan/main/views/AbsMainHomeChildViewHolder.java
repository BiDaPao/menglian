package com.aihuan.main.views;

import android.content.Context;
import android.view.ViewGroup;

import com.aihuan.common.bean.UserBean;
import com.aihuan.im.activity.ChatRoomActivity;
import com.aihuan.main.activity.MainActivity;
import com.aihuan.one.bean.ChatLiveBean;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity中的首页，附近 的子页面
 */

public abstract class AbsMainHomeChildViewHolder extends AbsMainViewHolder {


    public AbsMainHomeChildViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    /**
     * 观看直播
     */
    public void forwardUserHome(String toUid) {
        ((MainActivity) mContext).forwardUserHome(toUid);
    }

    //搭讪点击事件
    public void onAccostClick(ChatLiveBean bean) {
        UserBean userBean = new UserBean();
        userBean.setId(bean.getUid());
        userBean.setAvatar(bean.getAvatar());
        userBean.setUserNiceName(bean.getUserNiceName());
        userBean.setLevelAnchor(bean.getLevelAnchor());
        ChatRoomActivity.forwardAccost(mContext, userBean, true, true, false, true, true);
    }
}
