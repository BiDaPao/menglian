package com.aihuan.main.activity;

import android.view.ViewGroup;

import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.main.R;
import com.aihuan.main.views.SubcribeAudViewHolder;

/**
 * Created by cxf on 2019/4/23.
 */
public class SubcribeAudActivity extends AbsActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_subcribe_aud;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.chat_subcribe));
        SubcribeAudViewHolder viewHolder = new SubcribeAudViewHolder(mContext, (ViewGroup) findViewById(R.id.container));
        viewHolder.addToParent();
        viewHolder.subscribeActivityLifeCycle();
        viewHolder.loadData();
    }
}
