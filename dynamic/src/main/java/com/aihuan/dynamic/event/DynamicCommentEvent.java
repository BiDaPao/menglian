package com.aihuan.dynamic.event;

/**
 * Created by debug on 2019/7/25.
 */

public class DynamicCommentEvent {
    private String mDynamicId;
    private String comments;

    public DynamicCommentEvent(String dynamicId, String comments) {
        mDynamicId = dynamicId;
        this.comments = comments;
    }

    public String getDynamicId() {
        return mDynamicId;
    }

    public String getComments() {
        return comments;
    }
}
