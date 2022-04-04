package cn.tillusory.tiui.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import cn.tillusory.sdk.bean.TiDistortionEnum;
import cn.tillusory.tiui.R;

/**
 * Created by Anko on 2018/11/26.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public enum TiDistortion {

    NO_DISTORTION(TiDistortionEnum.NO_DISTORTION,
        R.drawable.ic_ti_none, R.drawable.ic_ti_none_full, R.string.none),
    ET_DISTORTION(TiDistortionEnum.ET_DISTORTION,
        R.drawable.ic_ti_et, R.drawable.ic_ti_et_full, R.string.distortion_et),
    PEAR_FACE_DISTORTION(TiDistortionEnum.PEAR_FACE_DISTORTION,
        R.drawable.ic_ti_pear_face, R.drawable.ic_ti_pear_face_full,
        R.string.distortion_pear_face),
    SLIM_FACE_DISTORTION(TiDistortionEnum.SLIM_FACE_DISTORTION,
        R.drawable.ic_ti_slim_face, R.drawable.ic_ti_slim_face_full,
        R.string.distortion_slim_face),
    SQUARE_FACE_DISTORTION(TiDistortionEnum.SQUARE_FACE_DISTORTION,
        R.drawable.ic_ti_square_face, R.drawable.ic_ti_square_face_full,
        R.string.square_face);

    private final TiDistortionEnum distortionEnum;
    private final int imageId;
    private final int fullImgId;
    private final int stringId;

    TiDistortion(final TiDistortionEnum distortionEnum,
                 @DrawableRes final int imageId,
                 @DrawableRes final int fullImgId,
                 @StringRes int stringId) {
        this.distortionEnum = distortionEnum;
        this.imageId = imageId;
        this.fullImgId = fullImgId;
        this.stringId = stringId;
    }

    public TiDistortionEnum getDistortionEnum() {
        return distortionEnum;
    }

    public String getString(@NonNull Context context) {
        return context.getResources().getString(this.stringId);
    }

    public Drawable getImageDrawable(@NonNull Context context) {
        return context.getResources().getDrawable(imageId);
    }

    public Drawable getFullImageDrawable(@NonNull Context context) {
        return context.getResources().getDrawable(fullImgId);
    }


}


