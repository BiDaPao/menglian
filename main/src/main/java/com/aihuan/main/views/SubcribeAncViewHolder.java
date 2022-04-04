package com.aihuan.main.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.ChatAnchorParam;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.SubcribeAncAdapter;
import com.aihuan.main.bean.SubcribeAncBean;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/4/23.
 * 预约我的
 */

public class SubcribeAncViewHolder extends AbsMainViewHolder implements SubcribeAncAdapter.ActionListener {

    private CommonRefreshView mRefreshView;
    private SubcribeAncAdapter mAdapter;
    private HttpCallback mAncToAudCallback;
    private SubcribeAncBean mSubcribeAncBean;

    public SubcribeAncViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_subcribe_aud;
    }

    @Override
    public void init() {
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_subcribe_2);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<SubcribeAncBean>() {
            @Override
            public RefreshAdapter<SubcribeAncBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new SubcribeAncAdapter(mContext);
                    mAdapter.setActionListener(SubcribeAncViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                OneHttpUtil.getSubscribeMeList(p, callback);
            }

            @Override
            public List<SubcribeAncBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), SubcribeAncBean.class);
            }

            @Override
            public void onRefreshSuccess(List<SubcribeAncBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<SubcribeAncBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    @Override
    public void loadData() {
        if (isFirstLoadData()) {
            if (mRefreshView != null) {
                mRefreshView.initData();
            }
        }
    }

    @Override
    public void onDestroy() {
        OneHttpUtil.cancel(OneHttpConsts.GET_SUBCRIBE_ME_LIST);
        OneHttpUtil.cancel(OneHttpConsts.CHAT_ANC_TO_AUD_START);
        super.onDestroy();
    }

    @Override
    public void onItemClick(SubcribeAncBean u) {
        RouteUtil.forwardUserHome(u.getUid());
    }

    @Override
    public void onToSubcribeClick(SubcribeAncBean u) {
        mSubcribeAncBean = u;
        if (mAncToAudCallback == null) {
            mAncToAudCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        if (info.length > 0 && mSubcribeAncBean != null) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            ChatAnchorParam param = new ChatAnchorParam();
                            param.setAudienceID(mSubcribeAncBean.getUid());
                            param.setAudienceName(mSubcribeAncBean.getUserNiceName());
                            param.setAudienceAvatar(mSubcribeAncBean.getAvatar());
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
            };
        }
        OneHttpUtil.chatAncToAudStart(u.getSubscribeId(), mAncToAudCallback);
    }
}
