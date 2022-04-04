package cn.tillusory.tiui.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import cn.tillusory.sdk.bean.TiHairEnum;
import cn.tillusory.tiui.R;

//开始
public enum TiHair {
    NO_HAIR(TiHairEnum.NO_HAIR, R.drawable.ic_ti_hair_no, R.string.rock_no),
    MY_PURPLE_HAIR(TiHairEnum.MY_PURPLE_HAIR, R.drawable.ic_ti_hair_my_purple, R.string.hair_my_purple),
    CHOCOLATE_HAIR(TiHairEnum.CHOCOLATE_HAIR, R.drawable.ic_ti_hair_chocolate, R.string.hair_chocolate),
    //    AJ_BROWN_HAIR(TiHairEnum.AJ_BROWN_HAIR, R.drawable.ic_ti_hair_aj_brown),
    //    CA_BROWN_HAIR(TiHairEnum.CA_BROWN_HAIR, R.drawable.ic_ti_hair_ca_brown),
    //    HON_BROWN_HAIR(TiHairEnum.HON_BROWN_HAIR, R.drawable.ic_ti_hair_hon_brown),
    //    LTG_BROWN_HAIR(TiHairEnum.LTG_BROWN_HAIR, R.drawable.ic_ti_hair_ltg_brown),
    //    ROSE_GOLD_HAIR(TiHairEnum.ROSE_GOLD_HAIR, R.drawable.ic_ti_hair_rose_gold),
    //    FW_GOLD_HAIR(TiHairEnum.FW_GOLD_HAIR, R.drawable.ic_ti_hair_fw_gold),
    //    SS_ORANGE_HAIR(TiHairEnum.SS_ORANGE_HAIR, R.drawable.ic_ti_hair_ss_orange),
    //    FL_ORANGE_HAIR(TiHairEnum.FL_ORANGE_HAIR, R.drawable.ic_ti_hair_fl_orange),
    VINTAGE_ROSE_HAIR(TiHairEnum.VINTAGE_ROSE_HAIR, R.drawable.ic_ti_hair_vintage_rose,
        R.string.hair_vintage_rose),
    TENDER_ROSE_HAIR(TiHairEnum.TENDER_ROSE_HAIR, R.drawable.ic_ti_hair_tender_rose, R.string.hair_tender_rose),
    //    MG_PURPLE_HAIR(TiHairEnum.MG_PURPLE_HAIR, R.drawable.ic_ti_hair_mg_purple),
    //    SPR_BROWN_HAIR(TiHairEnum.SPR_BROWN_HAIR, R.drawable.ic_ti_hair_spr_brown),
    FROG_TARO_HAIR(TiHairEnum.FROG_TARO_HAIR, R.drawable.ic_ti_hair_frog_taro, R.string.hair_frog_taro),
    PEACOCK_BLUE_HAIR(TiHairEnum.PEACOCK_BLUE_HAIR, R.drawable.ic_ti_hair_peacock_blue, R.string.hair_peacock_blue),
    FB_GRAY_HAIR(TiHairEnum.FB_GRAY_HAIR, R.drawable.ic_ti_hair_fb_gray, R.string.hair_fb_gray);
    //    FG_BROWN_HAIR(TiHairEnum.FG_BROWN_HAIR, R.drawable.ic_ti_hair_fg_brown),
    //    FL_GRAY_HAIR(TiHairEnum.FL_GRAY_HAIR, R.drawable.ic_ti_hair_fl_gray);

    private final TiHairEnum hairEnum;
    private final int imageId;
    private final int stringId;

    TiHair(final TiHairEnum hairEnum, @DrawableRes int imageId, @StringRes int stringId) {
        this.hairEnum = hairEnum;
        this.imageId = imageId;
        this.stringId = stringId;
    }

    public TiHairEnum getHairEnum() {
        return hairEnum;
    }

    public String getString(@NonNull Context context) {
        return context.getResources().getString(this.stringId);
    }

    public Drawable getImageDrawable(@NonNull Context context) {
        return context.getResources().getDrawable(imageId);
    }


}

