package com.aihuan.main.views;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.event.FollowEvent;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.HolderHandler;
import com.aihuan.im.activity.ChatRoomActivity;
import com.aihuan.im.adapter.ImIntimacyListAdapter;
import com.aihuan.im.adapter.ImListAdapter;
import com.aihuan.im.bean.ImUserBean;
import com.aihuan.im.event.ImRemoveAllMsgEvent;
import com.aihuan.im.event.ImUnReadIntimacyCountEvent;
import com.aihuan.im.event.ImUserMsgEvent;
import com.aihuan.im.http.ImHttpConsts;
import com.aihuan.im.http.ImHttpUtil;
import com.aihuan.im.utils.ImMessageUtil;
import com.aihuan.main.R;
import com.aihuan.main.activity.SubcribeAncActivity;
import com.aihuan.main.activity.SubcribeAudActivity;
import com.aihuan.main.interfaces.OnIntimacySet;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cxf on 2019/4/1.
 */

public class MainMessageIntimacyViewHolder extends AbsMainViewHolder implements View.OnClickListener, ImListAdapter.ActionListener {

    private RecyclerView mRecyclerView;
    private ImIntimacyListAdapter mAdapter;

    private List<ImUserBean> userBeanList;
    private int currentIndex = 0;
    private HolderHandler handler = new HolderHandler(this);
    private final int HANDLER_INTMACY = 1;

    public MainMessageIntimacyViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_msg_intimacy;
    }

    @Override
    public void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        initBanner();
        mAdapter = new ImIntimacyListAdapter(mContext);
        mAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        EventBus.getDefault().register(this);
    }


    private void initBanner() {

    }

    @Override
    public void loadData() {
        String uids = ImMessageUtil.getInstance().getConversationUids();
        if (!TextUtils.isEmpty(uids)) {
            ImHttpUtil.getImUserInfo(uids, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        userBeanList = JSON.parseArray(Arrays.toString(info), ImUserBean.class);
                        userIntimacySet();
                    }
                }
            });
        }

    }

    /**
     * 循环用户数据并请求用户亲密度
     */
    private void userIntimacySet() {
        if (currentIndex >= userBeanList.size()) {
            currentIndex = 0;
            showConversation();
            return;
        }

        ImUserBean userBean = userBeanList.get(currentIndex);
        if (!Constants.IM_MSG_ADMIN.equals(userBean.getId())) {
            getIntimacy(userBean, intimacyLevel -> {
                userBean.setIntimacyLevel(intimacyLevel);
                currentIndex++;
                handler.sendEmptyMessageDelayed(HANDLER_INTMACY, 50);
            });
        } else {
            currentIndex++;
            handler.sendEmptyMessageDelayed(HANDLER_INTMACY, 50);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == HANDLER_INTMACY) {
            userIntimacySet();
        }
    }

    /**
     * 根据用户ID获取亲密度等级
     *
     * @param imUserBean
     * @param intimacySet
     */
    private void getIntimacy(ImUserBean imUserBean, OnIntimacySet intimacySet) {
        ImHttpUtil.getIntimacyLevel(imUserBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    int mIntimacyLevel = obj.getIntValue("intimacy_level");
                    intimacySet.onSet(mIntimacyLevel);
                } else {
//                    ToastUtil.show(msg);
                    intimacySet.onSet(0);
                }
            }
        });
    }

    private void showConversation() {
        int intimacyUnreadCount = 0;
        List<ImUserBean> showList = new ArrayList<>();
        for (ImUserBean userBean : userBeanList) {
            if (userBean.getIntimacyLevel() > 1 || userBean.getAttent() > 0) {
                showList.add(userBean);
                intimacyUnreadCount += ImMessageUtil.getInstance().getUnReadMsgCount(userBean.getId());
            }
        }
        EventBus.getDefault().post(new ImUnReadIntimacyCountEvent(intimacyUnreadCount));
        showList = ImMessageUtil.getInstance().getLastMsgInfoList(showList);
        Collections.sort(showList, new Comparator<ImUserBean>() {
            @Override
            public int compare(ImUserBean o1, ImUserBean o2) {
                return o2.getIntimacyLevel() - o1.getIntimacyLevel();
            }
        });
        if (mRecyclerView != null && mAdapter != null) {
            mAdapter.setList(showList);
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_appointment) {
            forwardSubcribe();
        }
    }

    /**
     * 前往预约
     */
    private void forwardSubcribe() {
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        if (u.hasAuth()) {
            mContext.startActivity(new Intent(mContext, SubcribeAncActivity.class));
        } else {
            mContext.startActivity(new Intent(mContext, SubcribeAudActivity.class));
        }
    }


    @Override
    public void onItemClick(ImUserBean bean) {
        if (bean != null) {
            ImMessageUtil.getInstance().markAllMessagesAsRead(bean.getId(), true);
            //跳转聊天页面
            ChatRoomActivity.forward(mContext, bean, bean.isFollowing(), bean.isBlacking(), bean.getAuth() == 1, false);
        }
    }

    @Override
    public void onItemDelete(ImUserBean bean, int size) {
        ImMessageUtil.getInstance().removeConversation(bean.getId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (e != null) {
            if (mAdapter != null) {
                mAdapter.setFollow(e.getToUid(), e.getIsAttention());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUserMsgEvent(final ImUserMsgEvent e) {
        if (e != null && mRecyclerView != null && mAdapter != null) {
            int position = mAdapter.getPosition(e.getUid());
            if (position < 0) {
                ImHttpUtil.getImUserInfo(e.getUid(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            ImUserBean bean = JSON.parseObject(info[0], ImUserBean.class);
                            getIntimacy(bean, intimacyLevel -> {
                                bean.setIntimacyLevel(intimacyLevel);
                                if (bean.getIntimacyLevel() > 1 || bean.getAttent() > 0) {
                                    userBeanList.add(bean);
                                    showConversation();
                                }
                            });
                        }
                    }
                });
            } else {
                mAdapter.updateItem(e.getLastMessage(), e.getLastTime(), e.getUnReadCount(), position);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImRemoveAllMsgEvent(ImRemoveAllMsgEvent e) {
        if (mAdapter != null) {
            mAdapter.removeItem(e.getToUid());
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        ImHttpUtil.cancel(ImHttpConsts.GET_SYSTEM_MESSAGE_LIST);
        ImHttpUtil.cancel(ImHttpConsts.GET_IM_USER_INFO);
        OneHttpUtil.cancel(OneHttpConsts.GET_SUBSCRIBE_NUMS);
        super.onDestroy();
    }
}
