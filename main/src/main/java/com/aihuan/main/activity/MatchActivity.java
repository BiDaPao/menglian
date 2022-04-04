package com.aihuan.main.activity;

import android.view.ViewGroup;

import com.aihuan.common.activity.AbsActivity;
import com.aihuan.main.R;
import com.aihuan.main.views.MainFindMatchViewHolder;

/**
 * Created by debug on 2019/7/22.
 */

public class MatchActivity  extends AbsActivity{
    private MainFindMatchViewHolder mMainFindMatchViewHolder;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_match;
    }

    @Override
    protected void main() {
        super.main();
        mMainFindMatchViewHolder = new MainFindMatchViewHolder(mContext, (ViewGroup) findViewById(R.id.container));
        mMainFindMatchViewHolder.addToParent();
        mMainFindMatchViewHolder.subscribeActivityLifeCycle();
        mMainFindMatchViewHolder.loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMainFindMatchViewHolder!=null){
            mMainFindMatchViewHolder.release();
        }
        mMainFindMatchViewHolder=null;
    }
}
