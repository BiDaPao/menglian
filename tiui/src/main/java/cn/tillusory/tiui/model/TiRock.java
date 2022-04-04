package cn.tillusory.tiui.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import cn.tillusory.sdk.bean.TiRockEnum;
import cn.tillusory.tiui.R;

/**
 * Created by Anko on 2018/11/28.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public enum TiRock {
    NO_FILTER(TiRockEnum.NO_ROCK, R.drawable.ic_ti_rock_none, R.string.rock_no),//无抖动效果
    DAZZLED_COLOR_ROCK(TiRockEnum.DAZZLED_COLOR_ROCK, R.drawable.ic_ti_dazzled_color_rock, R.string.rock_dazzled_color),//炫彩抖动
    LIGHT_COLOR_ROCK(TiRockEnum.LIGHT_COLOR_ROCK, R.drawable.ic_ti_light_color_rock, R.string.rock_light_color),//轻彩抖动
    VISION_SHADOW_ROCK(TiRockEnum.VISION_SHADOW_ROCK, R.drawable.ic_vision_shadow_rock, R.string.rock_vision_shadow),//幻觉残影
    NEON_LIGHT_ROCK(TiRockEnum.NEON_LIGHT_ROCK, R.drawable.ic_neon_light_rock, R.string.rock_neon_light),//霓虹灯
    REFLECTION_MIRROR_ROCK(TiRockEnum.REFLECTION_MIRROR_ROCK, R.drawable.ic_ti_reflect_mirror_rock, R.string.rock_reflection_mirror),//反光镜
    VIRTUAL_MIRROR_ROCK(TiRockEnum.VIRTUAL_MIRROR_ROCK, R.drawable.ic_ti_virtual_mirror_rock, R.string.rock_virtual_mirror),//虚拟镜像
    FOUR_SCREEN_MIRROR_ROCK(TiRockEnum.FOUR_SCREEN_MIRROR_ROCK, R.drawable.ic_ti_four_mirror_rock, R.string.rock_four_mirror),//四屏镜像
    FUZZY_BORDER_ROCK(TiRockEnum.FUZZY_BORDER_ROCK, R.drawable.ic_ti_fuzzy_border_rock, R.string.rock_fuzzy_border),//边框模糊
    FUZZY_SPLIT_SCREEN_ROCK(TiRockEnum.FUZZY_SPLIT_SCREEN_ROCK, R.drawable.ic_ti_fuzzy_split_screen_rock, R.string.rock_fuzzy_split_screen),//模糊分屏
    FOUR_GRID_VIEW_ROCK(TiRockEnum.FOUR_GRID_VIEW_ROCK, R.drawable.ic_ti_four_grid_view_rock, R.string.rock_four_grid_view),//四分屏
    NINE_GRID_VIEW_ROCK(TiRockEnum.NINE_GRID_VIEW_ROCK, R.drawable.ic_ti_nine_grid_view_rock, R.string.rock_nine_grid_view),//九宫格
    ASTRAL_PROJECTION_ROCK(TiRockEnum.ASTRAL_PROJECTION_ROCK, R.drawable.ic_ti_astral_projection_rock, R.string.rock_astral_projection),//灵魂出鞘
    DIZZY_GIDDY_ROCK(TiRockEnum.DIZZY_GIDDY_ROCK, R.drawable.ic_ti_giddy_rock, R.string.rock_dizzy_giddy),//头晕目前
    // BLACK_MAGIC_ROCK(TiRockEnum.BLACK_MAGIC_ROCK, R.drawable.ic_ti_black_magic_rock, R.string.rock_black_magic), // 暗⿊魔法
    DYNAMIC_SPLIT_SCREEN_ROCK(TiRockEnum.DYNAMIC_SPLIT_SCREEN_ROCK, R.drawable.ic_ti_dynamic_split_screen_rock, R.string.rock_dynamic_split),//动感分屏
    BLACK_WHITE_FILM_ROCK(TiRockEnum.BLACK_WHITE_FILM_ROCK, R.drawable.ic_ti_black_white_rock, R.string.rock_black_white_film),//黑白电影
    BULGE_DISTORTION__ROCK(TiRockEnum.BULGE_DISTORTION__ROCK, R.drawable.ic_ti_magic_mirror_rock, R.string.rock_bulge_distortion),//魔法镜面
    GRAY_PETRIFACTION_ROCK(TiRockEnum.GRAY_PETRIFACTION_ROCK, R.drawable.ic_ti_gray_petrifaction_rock, R.string.rock_gray_petrifaction)//瞬间石化
    ;
    // BULGE_DISTORTION_ROCK(TiRockEnum.BULGE_DISTORTION_ROCK, R.drawable.ic_ti_rock_10, R.string.rock_bulge_distortion);

    private final TiRockEnum rockEnum;
    private final int imageId;
    private final int stringId;

    TiRock(final TiRockEnum rockEnum, final @DrawableRes int imageId, final int stringId) {
        this.rockEnum = rockEnum;
        this.imageId = imageId;
        this.stringId = stringId;
    }

    public TiRockEnum getRockEnum() {
        return rockEnum;
    }

    public String getString(@NonNull Context context) {
        return context.getResources().getString(this.stringId);
    }

    public Drawable getImageDrawable(@NonNull Context context) {
        return context.getResources().getDrawable(imageId);
    }
}