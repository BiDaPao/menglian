package cn.tillusory.tiui.model;

import cn.tillusory.sdk.TiSDK;
import cn.tillusory.tiui.custom.TiConfigTools;
import com.google.gson.Gson;
import java.io.File;
import java.util.List;

/**
 * @ClassName TiMaskConfig
 * @Description 面具的配置
 * @Author Spica2 7
 * @Date 2021/7/23 16:11
 */
@SuppressWarnings("unused")
public class TiMaskConfig {

  /**
   * masks
   */
  private List<TiMask> masks;

  public List<TiMask> getMasks() {
    return masks;
  }

  public void setMasks(List<TiMask> masks) {
    this.masks = masks;
  }

  public static class TiMask {

    public static final TiMask NO_MASK = new TiMask("", "", true);

    /**
     * name
     */
    private String name;
    /**
     * thumb
     */
    private String thumb;
    /**
     * downloaded
     */
    private boolean downloaded;

    public String getUrl() {
      return TiSDK.getMaskUrl() + File.separator + name + ".png";
    }

    public TiMask(String name, String thumb, boolean downloaded) {
      this.name = name;
      this.thumb = thumb;
      this.downloaded = downloaded;
    }

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getThumb() {
      return TiSDK.getMaskUrl() + File.separator + thumb;
    }

    public void setThumb(String thumb) { this.thumb = thumb;}

    public boolean isDownloaded() { return downloaded;}

    public void setDownloaded(boolean downloaded) {
      this.downloaded = downloaded;
    }

    /**
     * 下载完成更新缓存
     */
    public void downloaded() {
      this.downloaded = true;
      TiMaskConfig maskList = TiConfigTools.getInstance().getMaskList();
      for (TiMask mask : maskList.getMasks()) {
        if (mask.name.equals(this.name) && mask.thumb.equals(this.thumb)) {
          mask.setDownloaded(true);
        }
      }
      TiConfigTools.getInstance().maskDownload(new Gson().toJson(maskList));

    }

  }

}
