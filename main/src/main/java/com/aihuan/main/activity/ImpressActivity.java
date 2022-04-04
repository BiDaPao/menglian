package com.aihuan.main.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.UserEvaAdapter;
import com.aihuan.main.bean.UserEvaBean;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/15.
 * 主播印象
 */
@Route(path = RouteUtil.PATH_IMPRESS)
public class ImpressActivity extends AbsActivity implements View.OnClickListener {

    private String mToUid;
    private CommonRefreshView mRefreshView;
    private UserEvaAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_impress;
    }

    @Override
    protected void main() {
        mToUid = getIntent().getStringExtra(Constants.TO_UID);
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        findViewById(R.id.btn_impress_calc).setOnClickListener(this);
        mRefreshView = findViewById(R.id.refreshView);
        if (mToUid.equals(CommonAppConfig.getInstance().getUid())) {
            setTitle(WordUtil.getString(R.string.impress));
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_impress_2);
        } else {
            setTitle(WordUtil.getString(R.string.impress_2));
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_impress);
        }
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<UserEvaBean>() {
            @Override
            public RefreshAdapter<UserEvaBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new UserEvaAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                OneHttpUtil.getUserEvaList(mToUid, p, callback);
            }

            @Override
            public List<UserEvaBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), UserEvaBean.class);
            }

            @Override
            public void onRefreshSuccess(List<UserEvaBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<UserEvaBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }

    @Override
    protected void onDestroy() {
        OneHttpUtil.cancel(OneHttpConsts.GET_USER_EVA_LIST);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_impress_calc) {
            RouteUtil.forwardImpressCalc(mToUid);
        }
    }
}
