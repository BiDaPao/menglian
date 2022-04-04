package com.aihuan.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.adapter.RefreshAdapter;
import com.aihuan.common.bean.UserBean;
import com.aihuan.common.custom.CommonRefreshView;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.utils.RouteUtil;
import com.aihuan.common.utils.ToastUtil;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.adapter.ContactsAdapter;
import com.aihuan.main.http.MainHttpConsts;
import com.aihuan.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/3/4.
 */
@Route(path = RouteUtil.PATH_AT_FRIEND)
public class AtFriendActivity extends AbsActivity implements OnItemClickListener<UserBean> {

    private CommonRefreshView mRefreshView1;
    private CommonRefreshView mRefreshView2;
    private View mSearchGroup;
    private ContactsAdapter mAdapter;
    private ContactsAdapter mSearchAdapter;
    private EditText mEditText;
    private Handler mHandler;
    private InputMethodManager imm;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_at_friend;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.video_at_friend));
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchGroup = findViewById(R.id.search_group);
        mRefreshView1 = findViewById(R.id.refreshView1);
        mRefreshView1.setEmptyLayoutId(R.layout.view_empty_follow_1);
        mRefreshView1.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView1.setDataHelper(new CommonRefreshView.DataHelper<UserBean>() {
            @Override
            public RefreshAdapter<UserBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new ContactsAdapter(mContext);
                    mAdapter.setOnItemClickListener(AtFriendActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getFollowList(CommonAppConfig.getInstance().getUid(), p,  callback);
            }

            @Override
            public List<UserBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), UserBean.class);
            }

            @Override
            public void onRefreshSuccess(List<UserBean> list, int listCount) {

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
        mRefreshView2 = findViewById(R.id.refreshView2);
        mRefreshView2.setEmptyLayoutId(R.layout.view_empty_search);
        mRefreshView2.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView2.setDataHelper(new CommonRefreshView.DataHelper<UserBean>() {
            @Override
            public RefreshAdapter<UserBean> getAdapter() {
                if (mSearchAdapter == null) {
                    mSearchAdapter = new ContactsAdapter(mContext);
                    mSearchAdapter.setOnItemClickListener(AtFriendActivity.this);
                }
                return mSearchAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                search(p, callback);
            }

            @Override
            public List<UserBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), UserBean.class);
            }

            @Override
            public void onRefreshSuccess(List<UserBean> list, int listCount) {

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
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                doSearch();
            }
        };
        mEditText = findViewById(R.id.edit);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    MainHttpUtil.cancel(MainHttpConsts.GET_FOLLOW_LIST);
                    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                    if (mHandler != null) {
                        mHandler.removeMessages(0);
                    }
                    doSearch();
                    return true;
                }
                return false;
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainHttpUtil.cancel(MainHttpConsts.GET_FOLLOW_LIST);
                if (mHandler != null) {
                    mHandler.removeMessages(0);
                    if (!TextUtils.isEmpty(s)) {
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    } else {
                        if (mSearchGroup != null && mSearchGroup.getVisibility() == View.VISIBLE) {
                            mSearchGroup.setVisibility(View.INVISIBLE);
                        }
                        if (mSearchAdapter != null) {
                            mSearchAdapter.clearData();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mEditText.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                mEditText.requestFocus();
                imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
                mEditText.setText("");
            }
        });
        mRefreshView1.initData();
    }


    private void doSearch() {
        if (mSearchGroup != null && mSearchGroup.getVisibility() != View.VISIBLE) {
            mSearchGroup.setVisibility(View.VISIBLE);
        }
        if (mRefreshView2 != null) {
            mRefreshView2.initData();
        }
    }

    private void search(int p, HttpCallback callback) {
        String key = mEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(key)) {
            MainHttpUtil.searchUser(key, p, callback);
        } else {
            ToastUtil.show(WordUtil.getString(R.string.content_empty));
        }
    }

    @Override
    public void onItemClick(UserBean bean, int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.TO_UID, bean.getId());
        intent.putExtra(Constants.TO_NAME, bean.getUserNiceName());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        MainHttpUtil.cancel(MainHttpConsts.GET_FOLLOW_LIST);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (imm != null && mEditText != null) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
        super.onBackPressed();
    }

}
