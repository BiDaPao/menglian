package com.aihuan.one.views;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.one.R;
import com.aihuan.one.activity.ChatBaseActivity;
import com.aihuan.one.adapter.ChatEvaAdapter;
import com.aihuan.one.bean.ChatEvaBean;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/4/21.
 */

public class ChatEndAudienceViewHolder extends AbsChatEndViewHolder {

    private String mAnchorID;//主播ID
    private String mChatDurationString;//通话时长
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private ChatEvaAdapter mChatEvaAdapter;

    public ChatEndAudienceViewHolder(Context context, ViewGroup parentView, String anchorID, String chatDurationString) {
        super(context, parentView, anchorID, chatDurationString);
    }

    @Override
    protected void processArguments(Object... args) {
        mAnchorID = (String) args[0];
        mChatDurationString = (String) args[1];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_end_audience;
    }

    @Override
    public void init() {
        super.init();
        TextView chatDuration = (TextView) findViewById(R.id.chat_duration);
        chatDuration.setText(mChatDurationString);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mGridLayoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
    }


    public void loadData() {
        OneHttpUtil.getChatEvaList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mGridLayoutManager == null || mRecyclerView == null) {
                        return;
                    }
                    JSONObject obj = JSON.parseObject(info[0]);
                    final List<ChatEvaBean> list = new ArrayList<>();
                    ChatEvaBean goodTitleBean = new ChatEvaBean();
                    goodTitleBean.setTitle(true);
                    goodTitleBean.setTitleText(WordUtil.getString(R.string.chat_eva_good));
                    list.add(goodTitleBean);
                    List<ChatEvaBean> goodList = JSON.parseArray(obj.getString("good"), ChatEvaBean.class);
                    for (ChatEvaBean goodBean : goodList) {
                        goodBean.setGood(true);
                    }
                    list.addAll(goodList);
                    ChatEvaBean badTitleBean = new ChatEvaBean();
                    badTitleBean.setTitle(true);
                    badTitleBean.setTitleText(WordUtil.getString(R.string.chat_eva_bad));
                    list.add(badTitleBean);
                    List<ChatEvaBean> badList = JSON.parseArray(obj.getString("bad"), ChatEvaBean.class);
                    list.addAll(badList);
                    mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            ChatEvaBean bean = list.get(position);
                            if (bean.isTitle()) {
                                return 3;
                            }
                            return 1;
                        }
                    });
                    mChatEvaAdapter = new ChatEvaAdapter(mContext, list);
                    mRecyclerView.setAdapter(mChatEvaAdapter);
                }
            }
        });
    }

    @Override
    protected void confirmClick() {
        if (mChatEvaAdapter != null) {
            String evaIds = mChatEvaAdapter.getChooseEvaList();
            if (!TextUtils.isEmpty(evaIds)) {
                OneHttpUtil.setChatEvaList(mAnchorID, evaIds);
                ToastUtil.show(R.string.chat_eva_success);
            }
        }
        ((ChatBaseActivity) mContext).finish();
    }

    @Override
    public void onDestroy() {
        OneHttpUtil.cancel(OneHttpConsts.GET_CHAT_EVA_LIST);
        super.onDestroy();
    }


}
