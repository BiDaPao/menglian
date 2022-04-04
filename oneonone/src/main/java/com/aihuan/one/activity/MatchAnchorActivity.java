package com.aihuan.one.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.aihuan.common.Constants;
import com.aihuan.common.activity.AbsActivity;
import com.aihuan.common.event.MatchSuccessEvent;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.utils.L;
import com.aihuan.common.utils.MediaManager;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.one.R;
import com.aihuan.one.http.OneHttpConsts;
import com.aihuan.one.http.OneHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by cxf on 2019/5/7.
 * 主播匹配
 */

public class MatchAnchorActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, int type) {
        Intent intent = new Intent(context, MatchAnchorActivity.class);
        intent.putExtra(Constants.CHAT_TYPE, type);
        context.startActivity(intent);
    }

    private TextView mQueueCount;
    private int mType;
    private boolean mStartMatch;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_match_anchor;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.match));
        mQueueCount = findViewById(R.id.queue_count);
        findViewById(R.id.btn_close).setOnClickListener(this);
        mType = getIntent().getIntExtra(Constants.CHAT_TYPE, Constants.CHAT_TYPE_VIDEO);
        EventBus.getDefault().register(this);
        startMatch();
        MediaManager.getInstance().playSoundLoop(mContext, com.aihuan.common.R.raw.match_music, new MediaManager.Callback() {
            @Override
            public void onCompletion(Boolean success) {

            }
        });
    }


    public void startMatch() {
        OneHttpUtil.matchAnchor(mType, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                mStartMatch = true;
                if (code == 0) {

                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_close) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        L.e("音频播放销毁———————————MatchAnchorActivity—————————————————————onBackPressed——————————————————————");
        MediaManager.getInstance().stopPlayRecord();
        if (mStartMatch) {
            OneHttpUtil.matchAnchorCancel();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (!mStartMatch) {
            OneHttpUtil.cancel(OneHttpConsts.MATCH_ANCHOR);
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMatchSuccessEvent(MatchSuccessEvent e) {
        finish();
    }
}
