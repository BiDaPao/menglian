package com.aihuan.main.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.ChargeSuccessBean;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.custom.UPMarqueeView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.activity.RankListActivity;
import com.aihuan.main.adapter.MainHomeRecommendAdapter;
import com.aihuan.main.adapter.MainRankAdapter;
import com.aihuan.main.bean.ListBean;
import com.aihuan.main.dialog.MainFilterDialogFragment;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;
import com.aihuan.main.interfaces.OnAccostClick;
import com.aihuan.main.presenter.ChargeAnimPresenter;
import com.aihuan.one.bean.ChatLiveBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页 推荐
 */

public class MainHomeRecommendViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<ChatLiveBean>, MainFilterDialogFragment.ActionListener, OnAccostClick {

    private CommonRefreshView mRefreshView;
    //    private Banner mBanner;
    private boolean mBannerShowed;
    private MainHomeRecommendAdapter mAdapter;
    //    private List<BannerBean> mBannerList;
    private byte mSex;
    private byte mChatType;
    private ChargeAnimPresenter mChargeAnimPresenter;
    private boolean mPaused;

    private View mRankView;
    private UPMarqueeView mUPMarqueeView;
    private List<View> mViewList;
    private List<MainRankAdapter> mMainRankAdapterList;
    private View[] mRankNoData;
    private List<ListBean> mConsumeList;
    private List<ListBean> mProfitList;

    public MainHomeRecommendViewHolder(Context context, ViewGroup parentView) {
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
        } else {
            mSex = Constants.MAIN_SEX_NONE;
        }
        mChatType = Constants.CHAT_TYPE_NONE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_recommend;
    }

    @Override
    public void init() {
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                if (position == 0) {
//                    return 2;
//                }
//                return 1;
//            }
//        });
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mRefreshView.setLayoutManager(manager);
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 0, 5);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mAdapter = new MainHomeRecommendAdapter(mContext);
        mAdapter.setOnItemClickListener(MainHomeRecommendViewHolder.this);
        mRefreshView.setRecyclerViewAdapter(mAdapter);
        mAdapter.setOnAccostClick(MainHomeRecommendViewHolder.this);
        View headView = mAdapter.getHeadView();
        if (headView != null) {
//            mBanner = (Banner) headView.findViewById(R.id.banner);
//            mBanner.setImageLoader(new ImageLoader() {
//                @Override
//                public void displayImage(Context context, Object path, ImageView imageView) {
//                    ImgLoader.display(mContext, ((BannerBean) path).getImageUrl(), imageView);
//                }
//            });
//            mBanner.setOnBannerListener(new OnBannerListener() {
//                @Override
//                public void OnBannerClick(int p) {
//                    if (mBannerList != null) {
//                        if (p >= 0 && p < mBannerList.size()) {
//                            BannerBean bean = mBannerList.get(p);
//                            if (bean != null) {
//                                String link = bean.getLink();
//                                if (!TextUtils.isEmpty(link)) {
//                                    WebViewActivity.forward(mContext, link, false);
//                                }
//                            }
//                        }
//                    }
//                }
//            });

            mRankView = headView.findViewById(R.id.rank_view);
            mUPMarqueeView = headView.findViewById(R.id.upMarqueeView);
            mViewList = new ArrayList<>();
            mMainRankAdapterList = new ArrayList<>();
            mRankNoData = new View[2];
        }
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<ChatLiveBean>() {
            @Override
            public RefreshAdapter<ChatLiveBean> getAdapter() {
                return null;
            }
            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getHot(p, mSex, mChatType,callback);
            }
            @Override
            public List<ChatLiveBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
//                mBannerList = JSON.parseArray(obj.getString("slide"), BannerBean.class);
                mConsumeList = JSON.parseArray(obj.getString("consumetop"), ListBean.class);
                mProfitList = JSON.parseArray(obj.getString("profittop"), ListBean.class);
                return JSON.parseArray(JSON.parseObject(info[0]).getString("list"), ChatLiveBean.class);
            }

            @Override
            public void onRefreshSuccess(List<ChatLiveBean> list, int count) {
//                showBanner();
                showRankList();
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<ChatLiveBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        EventBus.getDefault().register(this);
    }

//    private void showBanner() {
//        if (mBannerList == null || mBannerList.size() == 0 || mBanner == null) {
//            return;
//        }
//        if (mBannerShowed) {
//            return;
//        }
//        mBannerShowed = true;
//        mBanner.setImages(mBannerList);
//        mBanner.start();
//    }

    @SuppressLint("ClickableViewAccessibility")
    private void showRankList() {
        if (mMainRankAdapterList == null) {
            return;
        }
        if (mMainRankAdapterList.size() > 0) {
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    mMainRankAdapterList.get(i).setList(mConsumeList);
                    if (mConsumeList.size() > 0) {
                        mRankNoData[i].setVisibility(View.GONE);
                    }
                } else if (i == 1) {
                    mMainRankAdapterList.get(i).setList(mProfitList);
                    if (mProfitList.size() > 0) {
                        mRankNoData[i].setVisibility(View.GONE);
                    }
                }
            }
            return;
        }
        mRankView.setVisibility(View.VISIBLE);
        for (int i = 0; i < 2; i++) {
            final View rankView = LayoutInflater.from(mContext).inflate(R.layout.view_main_rank, mUPMarqueeView, false);
            TextView rankType = rankView.findViewById(R.id.rank_type);
            if (i == 0) {
                rankType.setText(WordUtil.getString(R.string.consume_rank));
            } else if (i == 1) {
                rankType.setText(WordUtil.getString(R.string.profit_rank));
            }
            mRankNoData[i] = rankView.findViewById(R.id.no_data);
            RecyclerView recyclerView = rankView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    rankView.performClick();  //模拟父控件的点击
                }
                return false;
            });
            if (i == 0) {
                MainRankAdapter consumeAdapter = new MainRankAdapter(mContext, mConsumeList);
                recyclerView.setAdapter(consumeAdapter);
                mMainRankAdapterList.add(consumeAdapter);
                if (mConsumeList.size() > 0) {
                    mRankNoData[i].setVisibility(View.GONE);
                }
            } else if (i == 1) {
                MainRankAdapter profitAdapter = new MainRankAdapter(mContext, mProfitList);
                recyclerView.setAdapter(profitAdapter);
                mMainRankAdapterList.add(profitAdapter);
                if (mProfitList.size() > 0) {
                    mRankNoData[i].setVisibility(View.GONE);
                }
            }
            mViewList.add(rankView);
        }
        mUPMarqueeView.setViews(mViewList);
        mUPMarqueeView.setOnItemClickListener(new UPMarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                RankListActivity.forward(mContext, position);
            }
        });
    }

    @Override
    public void onItemClick(ChatLiveBean bean, int position) {
        if (bean.isAuth()) {
            forwardUserHome(bean.getUid());
        } else {
            onAccostClick(bean);
        }
    }



    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChargeSuccessBean(ChargeSuccessBean bean) {
        if (bean == null) {
            return;
        }
        if (mChargeAnimPresenter == null) {
            mChargeAnimPresenter = new ChargeAnimPresenter(mContext, mContentView);
        }
        if (mPaused) {
            mChargeAnimPresenter.save(bean);
        } else {
            mChargeAnimPresenter.showAnim(bean);
        }

    }

    @Override
    public void release() {
        EventBus.getDefault().unregister(this);
        MainHttpUtil.cancel(MainHttpConsts.GET_HOT);
        if (mChargeAnimPresenter != null) {
            mChargeAnimPresenter.release();
        }
        mChargeAnimPresenter = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    public void filter(byte sex, byte chatType) {

    }

    /**
     * 筛选
     */
    public void onFilterClick(boolean showCallType) {
        MainFilterDialogFragment fragment = new MainFilterDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("showCallType", showCallType);
        bundle.putByte(Constants.MAIN_SEX, mSex);
        bundle.putByte(Constants.CHAT_TYPE, mChatType);
        fragment.setArguments(bundle);
        fragment.setActionListener(this);
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "MainFilterDialogFragment");
    }

    @Override
    public void onFilter(byte sex, byte chatType) {
        mSex = sex;
        mChatType = chatType;
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (mPaused) {
//            if (mChargeAnimPresenter != null) {
//                ChargeSuccessBean bean = mChargeAnimPresenter.get();
//                if (bean != null) {
//                    mChargeAnimPresenter.showAnim(bean);
//                }
//            }
//        }
        mPaused = false;
    }
}
