package com.aihuan.dynamic.views;


import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dynamic.R;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.dynamic.activity.AbsDynamicCommentActivity;
import com.aihuan.dynamic.adapter.DynamicDetailsCommentAdapter;
import com.aihuan.dynamic.bean.DynamicBean;
import com.aihuan.dynamic.bean.DynamicCommentBean;
import com.aihuan.dynamic.event.DynamicCommentEvent;
import com.aihuan.dynamic.http.DynamicHttpConsts;
import com.aihuan.dynamic.http.DynamicHttpUtil;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/12/3.
 * 动态详情及评论相关
 */

public class DynamicCommentViewHolder extends AbsViewHolder implements View.OnClickListener, OnItemClickListener<DynamicCommentBean>, DynamicDetailsCommentAdapter.ActionListener {


    private CommonRefreshView mRefreshView;
    private TextView mCommentNum;
    private DynamicDetailsCommentAdapter mDynamicDetailsCommentAdapter;
    private String mDynamicId;
    private String mDynamicUid;
    private String mCommentString;
    private boolean mVideoIdChanged;//视频id是否发生变化了
    private boolean mShowed;
    private DynamicBean mDynamicBean;
    private  boolean mIsFromUserCenter;

    public DynamicCommentViewHolder(Context context, ViewGroup parentView, DynamicBean dynamicBean,boolean isFromUserCenter) {
        super(context, parentView, dynamicBean,isFromUserCenter);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        mDynamicBean = (DynamicBean) args[0];
        mIsFromUserCenter = (boolean) args[1];
        mDynamicId = mDynamicBean.getId();
        mDynamicUid = mDynamicBean.getUid();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_dynamic_comment;
    }

    @Override
    public void init() {
        findViewById(R.id.root).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.input).setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);
        mCommentString = WordUtil.getString(R.string.video_comment);
        mCommentNum = (TextView) findViewById(R.id.comment_num);
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_empty);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception e) {
                    //L.e("onLayoutChildren------>" + e.getMessage());
                }
            }
        });
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<DynamicCommentBean>() {
            @Override
            public RefreshAdapter<DynamicCommentBean> getAdapter() {
                if (mDynamicDetailsCommentAdapter == null) {
                    mDynamicDetailsCommentAdapter = new DynamicDetailsCommentAdapter(mContext, mDynamicBean,mIsFromUserCenter);
                    mDynamicDetailsCommentAdapter.setOnItemClickListener(DynamicCommentViewHolder.this);
                    mDynamicDetailsCommentAdapter.setActionListener(DynamicCommentViewHolder.this);
                }
                return mDynamicDetailsCommentAdapter;
            }
            @Override
            public void loadData(int p, HttpCallback callback) {
                if (!TextUtils.isEmpty(mDynamicId)) {
                    DynamicHttpUtil.getDynamicCommentList(mDynamicId, p, callback);
                }
            }

            @Override
            public List<DynamicCommentBean> processData(String[] info) {
                List<DynamicCommentBean> list = new ArrayList<>();
                if (info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String commentNum = obj.getString("comments");
                    EventBus.getDefault().post(new DynamicCommentEvent(mDynamicId, commentNum));
                    list = JSON.parseArray(obj.getString("commentlist"), DynamicCommentBean.class);
                    for (DynamicCommentBean bean : list) {
                        if (bean != null) {
                            bean.setParentNode(true);
                        }
                    }
                }
                return list;

            }

            @Override
            public void onRefreshSuccess(List<DynamicCommentBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<DynamicCommentBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }


    public boolean isShowed() {
        return mShowed;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }

    public void refreshComments(String comments){
        if (mDynamicDetailsCommentAdapter != null ) {
            mDynamicDetailsCommentAdapter.notifyComments(comments);
        }
    }


    public void release() {
        DynamicHttpUtil.cancel(DynamicHttpConsts.DYNAMIC_GET_COMMENTS);
        DynamicHttpUtil.cancel(DynamicHttpConsts.DYNAMIC_COMMENTS_LIKE);
        DynamicHttpUtil.cancel(DynamicHttpConsts.DYNAMIC_GET_REPLYS);
    }

    @Override
    public void onItemClick(DynamicCommentBean bean, int position) {
        if (!TextUtils.isEmpty(mDynamicId) && !TextUtils.isEmpty(mDynamicUid)) {
            ((AbsDynamicCommentActivity) mContext).openCommentInputWindow(false, mDynamicId, mDynamicUid, bean);
        }
    }

    @Override
    public void onExpandClicked(final DynamicCommentBean commentBean) {
        final DynamicCommentBean parentNodeBean = commentBean.getParentNodeBean();
        if (parentNodeBean == null) {
            return;
        }
        DynamicHttpUtil.getCommentReply(parentNodeBean.getId(),commentBean.getId(), parentNodeBean.getChildPage(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {

                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    List<DynamicCommentBean> list = JSON.parseArray(obj.getString("lists"), DynamicCommentBean.class);
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (DynamicCommentBean bean : list) {
                        bean.setParentNodeBean(parentNodeBean);
                    }
                    List<DynamicCommentBean> childList = parentNodeBean.getChildList();
                    if (childList != null) {
                        childList.addAll(list);
                        if (mDynamicDetailsCommentAdapter != null) {
                            mDynamicDetailsCommentAdapter.insertReplyList(commentBean, list.size());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onCollapsedClicked(DynamicCommentBean commentBean) {
        DynamicCommentBean parentNodeBean = commentBean.getParentNodeBean();
        if (parentNodeBean == null) {
            return;
        }
        List<DynamicCommentBean> childList = parentNodeBean.getChildList();
        DynamicCommentBean node0 = childList.get(0);
        int orignSize = childList.size();
        parentNodeBean.removeChild();
        parentNodeBean.setChildPage(1);
        if (mDynamicDetailsCommentAdapter != null) {
            mDynamicDetailsCommentAdapter.removeReplyList(node0, orignSize - childList.size());
        }
    }

    /**
     * 刷新评论
     */
    public void refreshComment() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }


    @Override
    public void onVoicePlay(DynamicCommentBean commentBean,TextView time) {
        if (mContext != null && mContext instanceof AbsDynamicCommentActivity) {
            if (commentBean != null && commentBean.isVoice()) {
                ((AbsDynamicCommentActivity) mContext).setVoiceInfo(Integer.parseInt(commentBean.getVoiceDuration()),time);
                ((AbsDynamicCommentActivity) mContext).playVoice(commentBean.getVoiceLink());
            }

        }
    }

    @Override
    public void onVoicePause(DynamicCommentBean commentBean) {
        if (mContext != null && mContext instanceof AbsDynamicCommentActivity) {
            ((AbsDynamicCommentActivity) mContext).pauseVoice();
        }
    }

    @Override
    public void onVoiceResume(DynamicCommentBean commentBean) {
        if (mContext != null && mContext instanceof AbsDynamicCommentActivity) {
            if (commentBean != null && commentBean.isVoice()) {
                ((AbsDynamicCommentActivity) mContext).resumeVoice(commentBean.getVoiceLink());
            }
        }
    }

    @Override
    public void onVoiceStop(DynamicCommentBean commentBean) {
        if (mContext != null && mContext instanceof AbsDynamicCommentActivity) {
            ((AbsDynamicCommentActivity) mContext).stopVoice();
        }
    }

    /**
     * 停止语音播放的动画
     */
    public void stopVoiceAnim() {
        if (mDynamicDetailsCommentAdapter != null) {
            mDynamicDetailsCommentAdapter.stopVoiceAnim();
        }
    }

}
