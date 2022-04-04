package com.aihuan.dynamic.event;

/**
 * Created by debug on 2019/7/25.
 */

public class DynamicLikeEvent {
    private String mDynamicId;
    private int isLike;
    private String likes;

    public DynamicLikeEvent(String dynamicId, int isLike, String likes) {
        mDynamicId = dynamicId;
        this.isLike = isLike;
        this.likes = likes;
    }

    public String getDynamicId() {
        return mDynamicId;
    }

    public int getIsLike() {
        return isLike;
    }

    public String getLikes() {
        return likes;
    }
}
