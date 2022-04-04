package cn.tillusory.tiui.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.adapter.TiFaceTrimAdapter;

/**
 * Created by Anko on 2018/11/25.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public enum TiFaceTrim {
    EYE_MAGNIFYING(R.string.eye_magnifying, R.drawable.ic_ti_eye_magnifying, R.drawable.ic_ti_eye_magnifying_full, TiFaceTrimAdapter.ITEM_COMMON, 0),
    CHIN_SLIMMING(R.string.chin_slimming, R.drawable.ic_ti_chin_slimming, R.drawable.ic_ti_chin_slimming_full, TiFaceTrimAdapter.ITEM_COMMON, 0),
    FACE_NARROWING(R.string.face_narrowing, R.drawable.ic_ti_face_narrowing, R.drawable.ic_ti_face_narrowing_full, TiFaceTrimAdapter.ITEM_COMMON, 0),

    CHEEKBONE_SLIMMING(R.string.cheekbone_slimming, R.drawable.ic_ti_cheekbone_slimming, R.drawable.ic_ti_cheekbone_slimming_full, TiFaceTrimAdapter.ITEM_CATEGORY_TITLE, R.string.category_face),
    JAWBONE_SLIMMING(R.string.jawbone_slimming, R.drawable.ic_ti_jawbone_slimming, R.drawable.ic_ti_jawbone_slimming_full, TiFaceTrimAdapter.ITEM_COMMON, 0),
    JAW_TRANSFORMING(R.string.jaw_transforming, R.drawable.ic_ti_jaw_transforming, R.drawable.ic_ti_jaw_transforming_full, TiFaceTrimAdapter.ITEM_COMMON, 0),
    JAW_SLIMMING(R.string.jaw_slimming, R.drawable.ic_ti_jaw_slimming, R.drawable.ic_ti_jaw_slimming_full, TiFaceTrimAdapter.ITEM_COMMON, 0),
    FOREHEAD_TRANSFORMING(R.string.forehead_transforming, R.drawable.ic_ti_forehead_transforming, R.drawable.ic_ti_forehead_transforming_full, TiFaceTrimAdapter.ITEM_COMMON, 0),

    EYE_INNER_CORNERS(R.string.eye_inner_corners, R.drawable.ic_ti_eye_inner_corners, R.drawable.ic_ti_eye_inner_corners_full, TiFaceTrimAdapter.ITEM_CATEGORY_TITLE, R.string.category_eye),
    EYE_OUTER_CORNERS(R.string.eye_outer_corners, R.drawable.ic_ti_eye_outer_corners, R.drawable.ic_ti_eye_outer_corners_full, TiFaceTrimAdapter.ITEM_COMMON, 0),
    EYE_SPACING(R.string.eye_spacing, R.drawable.ic_ti_eye_spacing, R.drawable.ic_ti_eye_spacing_full, TiFaceTrimAdapter.ITEM_COMMON, 0),
    EYE_CORNERS(R.string.eye_corners, R.drawable.ic_ti_eye_corners, R.drawable.ic_ti_eye_corners_full, TiFaceTrimAdapter.ITEM_COMMON, 0),

    NOSE_MINIFYING(R.string.nose_minifying, R.drawable.ic_ti_nose_minifying, R.drawable.ic_ti_nose_minifying_full, TiFaceTrimAdapter.ITEM_CATEGORY_TITLE, R.string.category_nose),
    NOSE_ELONGATING(R.string.nose_elongating, R.drawable.ic_ti_nose_elongating, R.drawable.ic_ti_nose_elongating_full, TiFaceTrimAdapter.ITEM_COMMON, 0),

    MOUTH_TRANSFORMING(R.string.mouth_transforming, R.drawable.ic_ti_mouth_transforming, R.drawable.ic_ti_mouth_transforming_full, TiFaceTrimAdapter.ITEM_CATEGORY_TITLE, R.string.category_mouth),
    MOUTH_HEIGHT(R.string.mouth_height, R.drawable.ic_ti_mouth_height, R.drawable.ic_ti_mouth_height_full, TiFaceTrimAdapter.ITEM_COMMON, 0),
    MOUTH_LIP_SIZE(R.string.mouth_lip_size, R.drawable.ic_ti_mouth_lip_size, R.drawable.ic_ti_mouth_lip_size_full, TiFaceTrimAdapter.ITEM_COMMON, 0),
    MOUTH_SMILING(R.string.mouth_smiling, R.drawable.ic_ti_mouth_smiling, R.drawable.ic_ti_mouth_smiling_full, TiFaceTrimAdapter.ITEM_COMMON, 0),

    BROW_HEIGHT(R.string.brow_height, R.drawable.ic_ti_brow_height, R.drawable.ic_ti_brow_height_full, TiFaceTrimAdapter.ITEM_CATEGORY_TITLE, R.string.category_brow),
    BROW_LENGTH(R.string.brow_length, R.drawable.ic_ti_brow_length, R.drawable.ic_ti_brow_length_full, TiFaceTrimAdapter.ITEM_COMMON, 0),
    BROW_SPACE(R.string.brow_space, R.drawable.ic_ti_brow_space, R.drawable.ic_ti_brow_space_full, TiFaceTrimAdapter.ITEM_COMMON, 0),
    BROW_SIZE(R.string.brow_size, R.drawable.ic_ti_brow_size, R.drawable.ic_ti_brow_size_full, TiFaceTrimAdapter.ITEM_COMMON, 0),
    BROW_CORNER(R.string.brow_corner, R.drawable.ic_ti_brow_corner, R.drawable.ic_ti_brow_corner_full, TiFaceTrimAdapter.ITEM_COMMON, 0);


    private int stringId;
    private int imageId;
    private int fullImgId;
    private int type;
    private int titleId;

    TiFaceTrim(int stringId, int imageId, int fullImgId, int type, int titleId) {
        this.stringId = stringId;
        this.imageId = imageId;
        this.fullImgId = fullImgId;
        this.type = type;
        this.titleId = titleId;
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

    public String getTitle(@NonNull Context context) {
        return context.getResources().getString(titleId);
    }
}


