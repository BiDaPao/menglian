package com.aihuan.main.activity;

import androidx.recyclerview.widget.GridLayoutManager;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.StringUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.UserEvaCalcAdapter;
import com.aihuan.one.bean.ImpressBean;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/15.
 * 印象统计
 */
@Route(path = RouteUtil.PATH_IMPRESS_CALC)
public class ImpressCalcActivity extends AbsActivity {

    private String mToUid;
    private CommonRefreshView mRefreshView;
    private UserEvaCalcAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_impress_calc;
    }

    @Override
    protected void main() {
        mToUid = getIntent().getStringExtra(Constants.TO_UID);
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mRefreshView = findViewById(R.id.refreshView);
        if (mToUid.equals(CommonAppConfig.getInstance().getUid())) {
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_impress_2);
        } else {
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_impress);
        }
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<ImpressBean>() {
            @Override
            public RefreshAdapter<ImpressBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new UserEvaCalcAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                OneHttpUtil.getUserEvaCalc(mToUid, p, callback);
            }

            @Override
            public List<ImpressBean> processData(String[] info) {
                List<ImpressBean> list = JSON.parseArray(Arrays.toString(info), ImpressBean.class);
                for (ImpressBean bean : list) {
                    bean.setChecked(true);
                    bean.setName(StringUtil.contact(bean.getName(), " ", bean.getCount()));
                }
                return list;
            }

            @Override
            public void onRefreshSuccess(List<ImpressBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<ImpressBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }

    @Override
    protected void onDestroy() {
        OneHttpUtil.cancel(OneHttpConsts.GET_USER_EVA_CALC);
        super.onDestroy();
    }


}
