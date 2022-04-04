package com.aihuan.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.main.R;
import com.aihuan.main.views.MainListViewHolder;

/**
 * Created by debug on 2019/7/28.
 */

public class RankListActivity extends AbsActivity{
    private MainListViewHolder mMainListViewHolder;
    private int mDefault;
    public static void forward(Context context,int defaultPos){
        Intent intent=new Intent(context,RankListActivity.class);
        intent.putExtra(Constants.RANK_DEFAULT,defaultPos);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_rank_list;
    }

    @Override
    protected void main() {
        super.main();
        mDefault=getIntent().getIntExtra(Constants.RANK_DEFAULT,0);
        mMainListViewHolder=new MainListViewHolder(mContext, (ViewGroup) findViewById(R.id.container),mDefault);
        mMainListViewHolder.addToParent();
        mMainListViewHolder.subscribeActivityLifeCycle();
        mMainListViewHolder.loadData();
    }
}
