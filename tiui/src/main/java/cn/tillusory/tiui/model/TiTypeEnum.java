package cn.tillusory.tiui.model;

import androidx.annotation.StringRes;
import cn.tillusory.tiui.R;

/**
 * @ClassName TiTypeEnum
 * @Description TODO
 * @Author Spica2 7
 * @Date 2021/7/25 22:05
 */
public enum TiTypeEnum {
  Beauty(R.string.beauty),
  FaceTrim(R.string.face_trim),

  Filter(R.string.filter),

  Rock(R.string.rock),

  Distortion(R.string.distortion),

  Sticker(R.string.sticker),

  Mask(R.string.mask),

  Gift(R.string.gift),

  Watermark(R.string.watermark),

  GreenScreen(R.string.green_screen),

  PORTRAIT(R.string.portrait),

  Makeup(R.string.makeup),

  HAIR(R.string.hair);

  private int stringId;

  TiTypeEnum(@StringRes final int stringId) {
    this.stringId = stringId;
  }

  public int getStringId() {
    return stringId;
  }
}
