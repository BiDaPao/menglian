package cn.tillusory.tiui.model;

import cn.tillusory.sdk.TiSDK;
import cn.tillusory.tiui.custom.TiConfigTools;
import com.google.gson.Gson;
import java.io.File;
import java.util.List;

/**
 * @ClassName TiGifList
 * @Description 礼物配置文件
 * @Author Spica2 7
 * @Date 2021/7/25 20:28
 */
@SuppressWarnings("unused")
public class TiGiftConfig {

  /**
   * gifts
   */
  private List<TiGift> gifts;

  public List<TiGift> getGifts() { return gifts;}

  public void setGifts(List<TiGift> gifts) { this.gifts = gifts;}

  public static class TiGift {

    public TiGift(String name, String dir, String category, String thumb, boolean voiced, boolean downloaded) {
      this.name = name;
      this.dir = dir;
      this.category = category;
      this.thumb = thumb;
      this.voiced = voiced;
      this.downloaded = downloaded;
    }

    public static final TiGift NO_GIFT = new TiGift("", "", "", "", false, true);

    /**
     * name
     */
    private String name;
    /**
     * dir
     */
    private String dir;
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
      return TiSDK.getGiftUrl() + File.separator + this.dir + ".zip";
    }

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getDir() { return dir;}

    public void setDir(String dir) { this.dir = dir;}

    public String getCategory() { return category;}

    public void setCategory(String category) { this.category = category;}

    public String getThumb() {
      return TiSDK.getGiftUrl() + File.separator + thumb;
    }

    public void setThumb(String thumb) { this.thumb = thumb;}

    public boolean isVoiced() { return voiced;}

    public void setVoiced(boolean voiced) { this.voiced = voiced;}

    public boolean isDownloaded() { return downloaded;}

    public void downLoaded() {
      TiGiftConfig tiGiftConfig = TiConfigTools.getInstance().getGifList();
      for (TiGift gift : tiGiftConfig.gifts) {
        if (gift.name.equals(this.name) && gift.thumb.equals(this.thumb)) {
          gift.setDownloaded(true);
        }
      }
      TiConfigTools.getInstance().giftDownload(new Gson().toJson(tiGiftConfig));
    }

    public void setDownloaded(boolean downloaded) { this.downloaded = downloaded;}
  }
}
