package com.aihuan.dynamic.activity;

import android.Manifest;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dynamic.R;
import com.aihuan.common.Constants;
import com.aihuan.common.adapter.ImChatFacePagerAdapter;
import com.aihuan.common.interfaces.CommonCallback;
import com.aihuan.common.interfaces.OnFaceClickListener;
import com.aihuan.common.utils.ProcessResultUtil;
import com.aihuan.dynamic.bean.DynamicCommentBean;
import com.aihuan.dynamic.dialog.DynamicInputDialogFragment;
import com.aihuan.dynamic.views.DynamicCommentViewHolder;
import com.aihuan.dynamic.views.DynamicCommentVoiceViewHolder;




/**
 * Created by debug on 2019/7/24.
 *
 */

public abstract class AbsDynamicCommentActivity extends AbsDynamicActivity implements View.OnClickListener, OnFaceClickListener {
    protected ProcessResultUtil mProcessResultUtil;
    protected DynamicCommentViewHolder mDynamicCommentViewHolder;
    protected DynamicInputDialogFragment mDynamicInputDialogFragmentiewHolder;
    private View mFaceView;//表情面板
    private int mFaceHeight;//表情面板高度
    protected DynamicCommentVoiceViewHolder mVideoCommentVoiceViewHolder;



    @Override
    protected void main() {
        super.main();
        mProcessResultUtil = new ProcessResultUtil(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_send) {
            if (mDynamicInputDialogFragmentiewHolder != null) {
                mDynamicInputDialogFragmentiewHolder.sendComment();
            }
        }
    }


    public void showVoiceViewHolder(final String dynamicId, final String dynamicUid, final DynamicCommentBean dynamicCommentBean) {
        if (mProcessResultUtil == null) {
            return;
        }
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean bean) {
                if (mDynamicInputDialogFragmentiewHolder != null) {
                    mDynamicInputDialogFragmentiewHolder.hideSoftInput();
                }
                if (mVideoCommentVoiceViewHolder == null) {
                    mVideoCommentVoiceViewHolder = new DynamicCommentVoiceViewHolder(mContext, (ViewGroup) findViewById(R.id.rootView));
                }
                mVideoCommentVoiceViewHolder.setVideoId(dynamicId);
                mVideoCommentVoiceViewHolder.setVideoUid(dynamicUid);
                mVideoCommentVoiceViewHolder.setVideoCommentBean(dynamicCommentBean);
                mVideoCommentVoiceViewHolder.addToParent();
            }
        });
    }

    /**
     * 打开评论输入框
     */
    public void openCommentInputWindow(boolean openFace, String dynamicId, String dynamicUid, DynamicCommentBean bean) {
        if (mFaceView == null) {
            mFaceView = initFaceView();
        }
        DynamicInputDialogFragment fragment = new DynamicInputDialogFragment();
        fragment.setDynamicInfo(dynamicId, dynamicUid);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.VIDEO_FACE_OPEN, openFace);
        bundle.putInt(Constants.VIDEO_FACE_HEIGHT, mFaceHeight);
        bundle.putParcelable(Constants.VIDEO_COMMENT_BEAN, bean);
        fragment.setArguments(bundle);
        mDynamicInputDialogFragmentiewHolder = fragment;
        fragment.show(getSupportFragmentManager(), "VideoInputDialogFragment");
    }


    public View getFaceView() {
        if (mFaceView == null) {
            mFaceView = initFaceView();
        }
        return mFaceView;
    }

    /**
     * 初始化表情控件
     */
    private View initFaceView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.view_chat_face, null);
        v.measure(0, 0);
        mFaceHeight = v.getMeasuredHeight();
        v.findViewById(R.id.btn_send).setOnClickListener(this);
        final RadioGroup radioGroup =  v.findViewById(R.id.radio_group);
        ViewPager viewPager =  v.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(10);
        ImChatFacePagerAdapter adapter = new ImChatFacePagerAdapter(mContext, this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0, pageCount = adapter.getCount(); i < pageCount; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_chat_indicator, radioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            radioGroup.addView(radioButton);
        }
        return v;
    }


    /**
     * 刷新评论
     */
    public void refreshComment() {
        if (mDynamicCommentViewHolder != null) {
            mDynamicCommentViewHolder.refreshComment();
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayView != null) {
            mPlayView.pausePlay();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayView != null) {
            mPlayView.resumePlay();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayView != null) {
            mPlayView.destroy();
        }
        mPlayView = null;
    }

    @Override
    public void onFaceClick(String str, int faceImageRes) {
        if (mDynamicInputDialogFragmentiewHolder != null) {
            mDynamicInputDialogFragmentiewHolder.onFaceClick(str, faceImageRes);
        }
    }

    @Override
    public void onFaceDeleteClick() {
        if (mDynamicInputDialogFragmentiewHolder != null) {
            mDynamicInputDialogFragmentiewHolder.onFaceDeleteClick();
        }
    }

    public void release() {
        if (mDynamicCommentViewHolder != null) {
            mDynamicCommentViewHolder.release();
        }
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        mDynamicCommentViewHolder = null;
        mDynamicInputDialogFragmentiewHolder = null;
        mProcessResultUtil = null;
    }

    public void releaseVideoInputDialog() {
        mDynamicInputDialogFragmentiewHolder = null;
    }

}
