package com.aihuan.one.views;

import android.content.Context;
import android.view.ViewGroup;

import com.aihuan.common.views.AbsViewHolder;
import com.aihuan.one.R;

/**
 * Created by cxf on 2018/10/9.
 * 直播间公共逻辑
 */

public class ChatLiveRoomViewHolder extends AbsViewHolder {

    public ChatLiveRoomViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_live_room;
    }

    @Override
    public void init() {


    }



//    /**
//     * 请求计时收费的扣费接口
//     */
//    private void requestTimeCharge() {
//        if (mTimeChargeCallback == null) {
//            mTimeChargeCallback = new HttpCallback() {
//                @Override
//                public void onSuccess(int code, String msg, String[] info) {
//                    if (mContext instanceof ChatAudienceActivity) {
//                        final ChatAudienceActivity liveAudienceActivity = (ChatAudienceActivity) mContext;
//                        if (code == 0) {
//                            liveAudienceActivity.roomChargeUpdateVotes();
//                        } else {
//                            if (mLiveRoomHandler != null) {
//                                mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_TIME_CHARGE);
//                            }
//                            liveAudienceActivity.pausePlay();
//                            if (code == 1008) {//余额不足
//                                liveAudienceActivity.setCoinNotEnough(true);
//                                DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.live_coin_not_enough), false,
//                                        new DialogUitl.SimpleCallback2() {
//                                            @Override
//                                            public void onConfirmClick(Dialog dialog, String content) {
//                                                RouteUtil.forwardMyCoin();
//                                            }
//
//                                            @Override
//                                            public void onCancelClick() {
//                                                liveAudienceActivity.exitLiveRoom();
//                                            }
//                                        });
//                            }
//                        }
//                    }
//                }
//            };
//        }
//
//
//        if (!TextUtils.isEmpty(mLiveUid) && mTimeChargeCallback != null) {
//            OneHttpUtil.cancel(OneHttpConsts.TIME_CHARGE);
//            OneHttpUtil.timeCharge(mLiveUid, mStream, mTimeChargeCallback);
//            startRequestTimeCharge();
//        }
//    }

//    /**
//     * 开始请求计时收费的扣费接口
//     */
//    public void startRequestTimeCharge() {
//        if (mLiveRoomHandler != null) {
//            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_TIME_CHARGE, 60000);
//        }
//    }






}
