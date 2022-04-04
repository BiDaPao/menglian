package com.aihuan.common.utils;

import static android.media.AudioManager.STREAM_MUSIC;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

public class MediaManager {
    private MediaPlayer mMediaPlayer;

    private boolean isPause;
    private Callback callback;
    private static MediaManager sInstance = new MediaManager();

    private MediaManager() {
    }

    public static MediaManager getInstance() {
        return sInstance;
    }

    //播放录音
    public void playSound(String filePath, final Callback callback) {
        this.callback = callback;
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            //播放错误 防止崩溃
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    if (callback != null) {
                        callback.onCompletion(false);
                    }
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (callback != null) {
                        callback.onCompletion(true);
                    }
                    release();
                }
            });
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
//            mMediaPlayer.setAudioAttributes();
            mMediaPlayer.prepareAsync();
//            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onCompletion(false);
            }
        }
    }

    //播放录音
    public void playSoundLoop(Context context, int rawId, final Callback callback) {
        this.callback = callback;
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            //播放错误 防止崩溃
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    if (callback != null) {
                        callback.onCompletion(false);
                    }
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (callback != null) {
                        callback.onCompletion(true);
                    }
                    release();
                }
            });
            Uri uri = Uri.parse("android.resource://com.menglian.live/" + rawId);
            mMediaPlayer.setDataSource(context, uri);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                    mMediaPlayer.setLooping(true);
                }
            });
//            mMediaPlayer.setAudioAttributes();
            mMediaPlayer.prepareAsync();
//            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onCompletion(false);
            }
        }
    }

    //播放录音
    public void playSound(Context context, String path, final Callback callback) {
        if (path == null || path.isEmpty()) {
            ToastUtil.show("文件为空");
            if (callback != null) {
                callback.onCompletion(false);
            }
            return;
        }
        this.callback = callback;
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            //播放错误 防止崩溃
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    if (callback != null) {
                        callback.onCompletion(false);
                    }
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (callback != null) {
                        callback.onCompletion(true);
                    }
                    release();
                }
            });
//            Uri uri;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                String authority = context.getPackageName() + ".fileProvider";
//                uri = FileProvider.getUriForFile(context, authority, file);
//            } else {
//                uri = Uri.fromFile(file);
//            }
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onCompletion(false);
            }
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        } else {
            return false;
        }
    }

    /**
     * 如果 播放时间过长,如30秒
     * 用户突然来电话了,则需要暂停
     */
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 播放
     */
    public void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    public void stopPlayRecord() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            release();
            L.e("音频播放销毁——————————————————————————————————————————————————————");
        }
    }

    /**
     * activity 被销毁  释放
     */
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (callback != null) callback.onCompletion(true);
    }

    public interface Callback {
        void onCompletion(Boolean success);
    }
}
