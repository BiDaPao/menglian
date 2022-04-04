package com.aihuan.dynamic.activity;


import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dynamic.R;
import com.aihuan.common.Constants;
import com.aihuan.common.custom.ItemDecoration;
import com.aihuan.common.glide.ImgLoader;
import com.aihuan.common.interfaces.OnItemClickListener;
import com.aihuan.common.utils.WordUtil;
import com.aihuan.dynamic.adapter.DynamicImgAdapter;
import com.aihuan.dynamic.bean.DynamicBean;
import com.aihuan.dynamic.custorm.DynamicVideoViewContainer;
import com.aihuan.dynamic.custorm.VideoPlayView;
import com.aihuan.dynamic.custorm.VoicePlayView;
import com.aihuan.dynamic.event.DynamicCommentEvent;
import com.aihuan.dynamic.event.DynamicDelEvent;
import com.aihuan.dynamic.inter.VoicePlayCallBack;
import com.aihuan.dynamic.views.DynamicCommentViewHolder;
import com.aihuan.im.utils.VoiceMediaPlayerUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;



/**
 * Created by debug on 2019/7/25.
 * 动态详情
 */

public class DynamicDetailsActivity extends AbsDynamicCommentActivity {
    private DynamicBean mDynamicBean;
    private boolean mBigPlay;
    private String mDynamicId;
    private String mDynamicUid;
    private boolean isLoadVideo;
    private boolean isLoadImg;
    private boolean isLoadVoice;
    private VoicePlayView mVoicePlayView;
    private boolean mIsFromUserCenter;

    public static void forward(Context context, DynamicBean dynamicBean) {
        Intent intent = new Intent(context, DynamicDetailsActivity.class);
        intent.putExtra(Constants.DYNAMIC_BEAN, dynamicBean);
        context.startActivity(intent);
    }
    public static void forward(Context context, DynamicBean dynamicBean,boolean isFromUserCenter) {
        Intent intent = new Intent(context, DynamicDetailsActivity.class);
        intent.putExtra(Constants.DYNAMIC_BEAN, dynamicBean);
        intent.putExtra(Constants.DYNAMIC_FROM_USER_CENTER, isFromUserCenter);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dynamic_details;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.dynamic_details));
        mDynamicBean = getIntent().getParcelableExtra(Constants.DYNAMIC_BEAN);
        mIsFromUserCenter=getIntent().getBooleanExtra(Constants.DYNAMIC_FROM_USER_CENTER,false);
        if (mDynamicBean == null) {
            return;
        }
        mDynamicId = mDynamicBean.getId();
        mDynamicUid = mDynamicBean.getUid();
        findViewById(R.id.btn_voice).setOnClickListener(this);
        findViewById(R.id.input).setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);
        mDynamicCommentViewHolder = new DynamicCommentViewHolder(mContext, (ViewGroup) findViewById(R.id.container), mDynamicBean,mIsFromUserCenter);
        mDynamicCommentViewHolder.addToParent();
        if (mDynamicBean.getType() == Constants.DYNAMIC_TYPE_VIDEO) {
            mPlayView = (VideoPlayView) LayoutInflater.from(mContext).inflate(R.layout.dynamic_videoplay_view, null);
            mPlayView.setLargePlayCallback(new VideoPlayView.LargePlayCallback() {
                @Override
                public void largePlay(boolean large) {
                    mBigPlay = large;
                }
            });
        }
        mVoicePlayCallBack = new VoicePlayCallBack() {
            @Override
            public void onPlayStart() {
                pauseDynamicVoice();
            }

            @Override
            public void onPlayResume() {
                pauseDynamicVoice();
            }

            @Override
            public void onPlayPause() {
                //resumeDynamicVoice();
            }
            @Override
            public void onPlayAutoEnd() {
                if (mDynamicCommentViewHolder != null) {
                    mDynamicCommentViewHolder.stopVoiceAnim();
                }
                // resumeDynamicVoice();
            }

            @Override
            public void onPlayEnd() {
                // resumeDynamicVoice();
            }
        };
        EventBus.getDefault().register(this);
    }

    public void loadImg(final FrameLayout container) {
        if (!isLoadImg) {
            isLoadImg = true;
            container.setVisibility(View.VISIBLE);
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_img_group, container, false);
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
            ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 3, 3);
            decoration.setOnlySetItemOffsetsButNoDraw(true);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.addItemDecoration(decoration);
            DynamicImgAdapter dynamicImgAdapter = new DynamicImgAdapter(mContext);
            dynamicImgAdapter.setOnItemClickListener(new OnItemClickListener<String>() {
                @Override
                public void onItemClick(String url, int position) {
                    lookImgs(mDynamicBean.getThumbs(), position);
                }
            });
            dynamicImgAdapter.setList(mDynamicBean.getThumbs());
            recyclerView.setAdapter(dynamicImgAdapter);
            container.addView(view);
        }
    }

    public void loadVideo(final FrameLayout container) {
        if (!isLoadVideo) {
            isLoadVideo = true;
            container.setVisibility(View.VISIBLE);
            final View view = LayoutInflater.from(mContext).inflate(R.layout.view_video_group, container, false);
            final DynamicVideoViewContainer dynamicVideoViewContainer = view.findViewById(R.id.playViewContainer);
            final ImageView ivCover = view.findViewById(R.id.cover);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dynamicVideoViewContainer.hasPlayView()) {
                        mPlayView.pausePlay();
                        mBigPlay = true;
                        ((AbsDynamicActivity) mContext).addPlayerGroup(container, view, mPlayView);
                    }
                }
            });
            container.setEnabled(false);
            dynamicVideoViewContainer.setPlayEventListener(new DynamicVideoViewContainer.PlayEventListener() {
                @Override
                public void onStartPlay() {
                    if (ivCover != null && ivCover.getVisibility() == View.VISIBLE) {
                        ivCover.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFirstFrame() {
                    if (ivCover != null && ivCover.getVisibility() == View.VISIBLE) {
                        ivCover.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onResumePlay() {

                }

                @Override
                public void onPausePlay() {

                }

                @Override
                public void onPausePlay2() {

                }

                @Override
                public void onRemoveView() {

                }

                @Override
                public void onVideoSize(int width, int height, boolean onlyimg) {
                    LinearLayout.LayoutParams mVideoLayoutParams = (LinearLayout.LayoutParams) container.getLayoutParams();
                    mVideoLayoutParams.width = width;
                    container.setLayoutParams(mVideoLayoutParams);
                    container.setEnabled(true);
                }
            });
            if (!dynamicVideoViewContainer.hasPlayView()) {
                dynamicVideoViewContainer.addPlayView(mPlayView);
                mPlayView.setDynamicBean(mDynamicBean);
                mPlayView.play();
            }
            ImgLoader.display(mContext, mDynamicBean.getVideo_thumb(), ivCover);
            container.addView(view);
        }
    }

    public void loadVoice(FrameLayout container) {
        if (!isLoadVoice) {
            isLoadVoice = true;
            container.setVisibility(View.VISIBLE);
            final View view = LayoutInflater.from(mContext).inflate(R.layout.voice_play_view, container, false);
            final VoicePlayView voicePlayView = view.findViewById(R.id.voice_view);
            voicePlayView.setVoiceInfo(mDynamicBean.getLength(), mDynamicBean.getVoice());
            voicePlayView.setVoiceMediaPlayerUtil(new VoiceMediaPlayerUtil(mContext));
            voicePlayView.setVoicePlayCallBack(new VoicePlayView.VoicePlayCallBack() {
                @Override
                public void play(VoicePlayView playView) {
                    pauseVoice();
                    if (mDynamicCommentViewHolder != null) {
                        mDynamicCommentViewHolder.stopVoiceAnim();
                    }
                }
            });
            container.addView(view);
            mVoicePlayView = voicePlayView;
        }
    }


    public void pauseDynamicVoice() {
        if (mDynamicBean.getType() == Constants.DYNAMIC_TYPE_VOICE) {
            if (mVoicePlayView != null) {
                mVoicePlayView.pause();
            }
        }
    }

    public void resumeDynamicVoice() {
        if (mDynamicBean.getType() == Constants.DYNAMIC_TYPE_VOICE) {
            if (mVoicePlayView != null) {
                mVoicePlayView.resume();
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.input) {
            if (!TextUtils.isEmpty(mDynamicId) && !TextUtils.isEmpty(mDynamicUid)) {
                ((AbsDynamicCommentActivity) mContext).openCommentInputWindow(false, mDynamicId, mDynamicUid, null);
            }
        } else if (i == R.id.btn_face) {
            openFace();
        } else if (i == R.id.btn_voice) {
            ((AbsDynamicCommentActivity) mContext).showVoiceViewHolder(mDynamicId, mDynamicUid, null);
        }

    }


    public void openFace() {
        if (!TextUtils.isEmpty(mDynamicId) && !TextUtils.isEmpty(mDynamicUid)) {
            ((AbsDynamicCommentActivity) mContext).openCommentInputWindow(true, mDynamicId, mDynamicUid, null);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicDel(DynamicDelEvent e) {
        if (e.getDynamicId().equals(mDynamicId)) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicComment(DynamicCommentEvent e) {
        if (mDynamicCommentViewHolder != null && e != null) {
            mDynamicCommentViewHolder.refreshComments(e.getComments());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (this.isFinishing()) {
            //进行数据操作及释放资源等操作
            if (mVoicePlayView != null) {
                mVoicePlayView.release();
            }
            EventBus.getDefault().unregister(this);
        } else {
            if (mVoicePlayView != null) {
                mVoicePlayView.resetView();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isBackVideo()) {
            videoBack();
        } else {
            super.onBackPressed();
        }

    }
}
