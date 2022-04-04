package com.aihuan.main.views;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aihuan.common.activity.WebViewActivity;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.interfaces.HolderHandler;
import com.aihuan.common.utils.DpUtil;
import com.aihuan.common.utils.L;
import com.aihuan.im.event.ImUnReadIntimacyCountEvent;
import com.aihuan.main.bean.BannerBean;
import com.aihuan.main.interfaces.OnIntimacySet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.event.FollowEvent;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.SpUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.im.activity.ChatRoomActivity;
import com.aihuan.im.activity.SystemMessageActivity;
import com.aihuan.im.adapter.ImListAdapter;
import com.aihuan.im.bean.ImUserBean;
import com.aihuan.im.bean.SystemMessageBean;
import com.aihuan.im.event.ImRemoveAllMsgEvent;
import com.aihuan.im.event.ImUserMsgEvent;
import com.aihuan.im.event.SystemMsgEvent;
import com.aihuan.im.http.ImHttpConsts;
import com.aihuan.im.http.ImHttpUtil;
import com.aihuan.im.utils.ImMessageUtil;
import com.aihuan.main.R;
import com.aihuan.main.activity.SubcribeAncActivity;
import com.aihuan.main.activity.SubcribeAudActivity;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

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

public class MainMessageMsgViewHolder extends AbsMainViewHolder implements View.OnClickListener, ImListAdapter.ActionListener {

    private View mBtnSystemMsg;
    private RecyclerView mRecyclerView;
    private ImListAdapter mAdapter;
    private View mSystemMsgRedPoint;//系统消息的红点
    private TextView mSystemMsgContent;
    private TextView mSystemTime;
    private TextView mSubcribMsg;
    private HttpCallback mSystemMsgCallback;

    private Banner mBanner;
    private List<BannerBean> mBannerList;

    private List<ImUserBean> userBeanList;
    private int currentIndex = 0;
    private HolderHandler handler = new HolderHandler(this);
    private final int HANDLER_INTMACY = 1;

    public MainMessageMsgViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_msg_msg;
    }

    @Override
    public void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        initBanner();
        mAdapter = new ImListAdapter(mContext);
        mAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        View headView = mAdapter.getHeadView();
        mBtnSystemMsg = headView.findViewById(R.id.btn_system_msg);
        mBtnSystemMsg.setOnClickListener(this);
        mSubcribMsg = headView.findViewById(R.id.msg_appointment);
        headView.findViewById(R.id.btn_appointment).setOnClickListener(this);
        mSystemMsgRedPoint = headView.findViewById(R.id.red_point);
        mSystemMsgContent = headView.findViewById(R.id.msg);
        mSystemTime = headView.findViewById(R.id.time);
        mSystemMsgCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    SystemMessageBean bean = JSON.parseObject(info[0], SystemMessageBean.class);
                    if (mSystemMsgContent != null) {
                        mSystemMsgContent.setText(bean.getContent());
                    }
                    if (mSystemTime != null) {
                        mSystemTime.setText(bean.getAddtime());
                    }
                    if (SpUtil.getInstance().getBooleanValue(SpUtil.HAS_SYSTEM_MSG)) {
                        if (mSystemMsgRedPoint != null && mSystemMsgRedPoint.getVisibility() != View.VISIBLE) {
                            mSystemMsgRedPoint.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };
        EventBus.getDefault().register(this);
    }


    private void initBanner() {
        mBanner = (Banner) findViewById(R.id.banner);
        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                ImgLoader.display(mContext, ((BannerBean) path).getImageUrl(), imageView);
            }

            @Override
            public ImageView createImageView(Context context) {
                RoundedImageView view = new RoundedImageView(context);
                int temp = DpUtil.dp2px(5);
                view.setCornerRadius(temp);
                return view;
            }
        });
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int p) {
                if (mBannerList != null) {
                    if (p >= 0 && p < mBannerList.size()) {
                        BannerBean bean = mBannerList.get(p);
                        if (bean != null) {
                            String link = bean.getLink();
                            if (!TextUtils.isEmpty(link)) {
                                WebViewActivity.forward(mContext, link, false);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void loadData() {
        getSystemMessageList();
        getBanner();
        OneHttpUtil.getSubscribeNums(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mSubcribMsg != null) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        if (obj != null) {
                            mSubcribMsg.setText(String.format(WordUtil.getString(R.string.chat_subcribe_count), obj.getString("nums")));
                        }
                    }
                }
            }
        });
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
        int intimacyUnreadCount = 0;
        if (currentIndex >= userBeanList.size()) {
            currentIndex = 0;
            List<ImUserBean> showList = new ArrayList<>();
            for (ImUserBean userBean : userBeanList) {
                if (userBean.getIntimacyLevel() < 1 && userBean.getAttent() == 0) {
                    showList.add(userBean);
                } else {
                    intimacyUnreadCount += ImMessageUtil.getInstance().getUnReadMsgCount(userBean.getId());
                }
            }
            EventBus.getDefault().post(new ImUnReadIntimacyCountEvent(intimacyUnreadCount));
            showConversation(showList);
            return;
        }

        ImUserBean userBean = userBeanList.get(currentIndex);
        L.e("index: " + currentIndex + "  ID:" + userBean.getId());
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

    private void showConversation(List<ImUserBean> list) {
        list = ImMessageUtil.getInstance().getLastMsgInfoList(list);
        Collections.sort(list, new Comparator<ImUserBean>() {
            @Override
            public int compare(ImUserBean o1, ImUserBean o2) {
                if (Constants.IM_MSG_ADMIN.equals(o1.getId())) {
                    return -1;
                } else if (Constants.IM_MSG_ADMIN.equals(o2.getId())) {
                    return 1;
                } else {
                    return (int) (o2.getLastTimeStamp() - o1.getLastTimeStamp());
                }
            }
        });
        if (mRecyclerView != null && mAdapter != null) {
            mAdapter.setList(list);
        }
    }


    /**
     * 获取系统消息
     */
    private void getSystemMessageList() {
        ImHttpUtil.getSystemMessageList(1, mSystemMsgCallback);
    }

    /**
     * 获取Banner列表
     */
    private void getBanner() {
        ImHttpUtil.getBanners(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                mBannerList = JSON.parseArray(obj.getString("slide"), BannerBean.class);
                showBanner();
            }
        });
    }

    private void showBanner() {
        if (mBannerList == null || mBannerList.size() == 0 || mBanner == null) {
            return;
        }
        mBanner.setImages(mBannerList);
        mBanner.start();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_system_msg) {
            forwardSystemMessage();
        } else if (i == R.id.btn_appointment) {
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

    /**
     * 前往系统消息
     */
    private void forwardSystemMessage() {
        SpUtil.getInstance().setBooleanValue(SpUtil.HAS_SYSTEM_MSG, false);
        if (mSystemMsgRedPoint != null && mSystemMsgRedPoint.getVisibility() == View.VISIBLE) {
            mSystemMsgRedPoint.setVisibility(View.INVISIBLE);
        }
        SystemMessageActivity.forward(mContext);
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
    public void onSystemMsgEvent(SystemMsgEvent e) {
        getSystemMessageList();
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
                            L.e("接口数据：" + info[0].toString());
                            ImUserBean bean = JSON.parseObject(info[0], ImUserBean.class);
                            getIntimacy(bean, intimacyLevel -> {
                                bean.setIntimacyLevel(intimacyLevel);
                                if (bean.getIntimacyLevel() < 1 && bean.getAttent() == 0) {
                                    bean.setLastMessage(e.getLastMessage());
                                    bean.setUnReadCount(e.getUnReadCount());
                                    bean.setLastTime(e.getLastTime());
                                    mAdapter.insertItem(bean);
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
