package com.aihuan.one.views;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.ViewGroup;

import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.one.R;

/**
 * Created by cxf on 2019/4/20.
 */

public abstract class AbsChatInviteViewHolder extends AbsViewHolder {

    private MediaPlayer mMediaPlayer;

    public AbsChatInviteViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    /**
     * 播放铃声
     */
    public void playRingMusic() {
        try {
            mMediaPlayer = MediaPlayer.create(mContext, R.raw.ring);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setVolume(1f, 1f);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放铃声
     */
    private void stopRingMusic() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        release();
        super.onDestroy();
    }

    public void hide() {
        release();
        removeFromParent();
    }

    public void release(){
        stopRingMusic();
    }
}
