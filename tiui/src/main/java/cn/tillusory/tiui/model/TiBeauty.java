package cn.tillusory.tiui.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import cn.tillusory.tiui.R;

/**
 * Created by Anko on 2018/11/22.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public enum TiBeauty {

    WHITENING(R.string.skin_whitening, R.drawable.ic_ti_whitening, R.drawable.ic_ti_whitening_full),
    BLEMISH_REMOVAL(R.string.skin_blemish_removal, R.drawable.ic_ti_blemish_removal, R.drawable.ic_ti_blemish_removal_full),

    TENDERNESS(R.string.skin_tenderness, R.drawable.ic_ti_tenderness, R.drawable.ic_ti_tenderness_full),
    SHARPNESS(R.string.skin_sharpness, R.drawable.ic_ti_sharpness, R.drawable.ic_ti_sharpness_full),
    BRIGHTNESS(R.string.skin_brightness, R.drawable.ic_ti_brightness, R.drawable.ic_ti_brightness_full),

    LABEL_PRECISE_BEAUTY(R.string.skin_precise, 1),
    PRECISE_BEAUTY(R.string.skin_grinding, R.drawable.ic_ti_precise_beauty, R.drawable.ic_ti_precise_beauty_full),

    PRECISE_TENDERNESS(R.string.skin_precise_tenderness, R.drawable.ic_ti_precise_tenderness,
        R.drawable.ic_ti_precise_tenderness_full),
    HIGH_LIGHT(R.string.high_light, R.drawable.ic_ti_high_light, R.drawable.ic_ti_high_light_full),
    DARK_CIRCLE(R.string.skin_dark_circle,
        R.drawable.ic_ti_dark_circle,
        R.drawable.ic_ti_dark_circle_full),
    CROWS_FEET(R.string.skin_cows_feet,
        R.drawable.ic_ti_crows_feet, R.drawable.ic_ti_crows_feet_full),
    NASOLABIAL_FOLD(
        R.string.skin_nasolabial_fold,
        R.drawable.ic_ti_nasolabial_fold,
        R.drawable.ic_ti_nasolabial_fold_full);

    private final int stringId;
    private final int imageId;
    private final int fullImgId;
    private int type = 0;//ui类型 0：单元 1：标签

    TiBeauty(@StringRes int stringId, @DrawableRes int imageId, @DrawableRes int fullImgId) {
        this.stringId = stringId;
        this.imageId = imageId;
        this.fullImgId = fullImgId;
    }

    TiBeauty(@StringRes int stringId, final int type) {
        this.stringId = stringId;
        this.imageId = -1;
        this.fullImgId = -1;
        this.type = type;
    }

    public String getString(@NonNull Context context) {
        return context.getResources().getString(stringId);
    }

    public Drawable getImageDrawable(@NonNull Context context) {
        return context.getResources().getDrawable(imageId);
    }

    public Drawable getFullImageDrawable(@NonNull Context context) {
        return context.getResources().getDrawable(fullImgId);
    }

    public int getType() {
        return type;
    }
}



