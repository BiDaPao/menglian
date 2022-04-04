package cn.tillusory.tiui.model;

import cn.tillusory.sdk.TiSDK;
import cn.tillusory.tiui.custom.TiConfigTools;
import com.google.gson.Gson;
import java.io.File;
import java.util.List;

/**
 * @ClassName TiStickers
 * @Description 贴纸列表
 * @Author Spica2 7
 * @Date 2021/7/25 19:58
 */
@SuppressWarnings("unused")
public class TiStickerConfig {

  /**
   * stickers
   */
  private List<TiSticker> stickers;

  @Override public String toString() {
    return "TiStickerConfig{" +
        "tiStickers=" + stickers.size() +
        "个}";
  }

  public List<TiSticker> getStickers() {
    return stickers;
  }

  public TiStickerConfig(List<TiSticker> stickers) {
    this.stickers = stickers;
  }

  public void setStickers(List<TiSticker> tiStickers) { this.stickers = tiStickers;}

  public static class TiSticker {

    public static final TiSticker NO_STICKER = new TiSticker("", "", ",", "", false, true);

    /**
     * name
     */
    private String name;
    /**
     * dir
     */
    private String dir;

    public TiSticker(String name, String dir, String category, String thumb, boolean voiced, boolean downloaded) {
      this.name = name;
      this.dir = dir;
      this.category = category;
      this.thumb = thumb;
      this.voiced = voiced;
      this.downloaded = downloaded;
    }

    @Override public String toString() {
      return "TiSticker{" +
          "name='" + name + '\'' +
          ", dir='" + dir + '\'' +
          ", category='" + category + '\'' +
          ", thumb='" + thumb + '\'' +
          ", voiced=" + voiced +
          ", downloaded=" + downloaded +
          '}';
    }

    /**
     * category
     */
    private String category;
    /**
     * thumb
     */
    private String thumb;
    /**
     * voiced
     */
    private boolean voiced;
    /**
     * downloaded
     */
    private boolean downloaded;

    public String getUrl() {
      return TiSDK.getStickerUrl() + File.separator + dir + ".zip";
    }

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getDir() { return dir;}

    public void setDir(String dir) { this.dir = dir;}

    public String getCategory() { return category;}

    public void setCategory(String category) { this.category = category;}

    public String getThumb() { return TiSDK.getStickerUrl() + File.separator + thumb;}

    public void setThumb(String thumb) { this.thumb = thumb;}

    public boolean isVoiced() { return voiced;}

    public void setVoiced(boolean voiced) { this.voiced = voiced;}

    public boolean isDownloaded() { return downloaded;}

    public void setDownloaded(boolean downloaded) {
      this.downloaded = downloaded;
    }

    /**
     * 下载完成更新缓存数据
     */
    public void downloaded() {
      TiStickerConfig tiStickerConfig = TiConfigTools.getInstance().getStickerConfig();

      for (TiSticker sticker : tiStickerConfig.getStickers()) {
        if (this.name.equals(sticker.name) && sticker.thumb.equals(this.thumb)) {
          sticker.setDownloaded(true);
        }
      }
      TiConfigTools.getInstance().stickerDownload(new Gson().toJson(tiStickerConfig));
    }

  }

}
