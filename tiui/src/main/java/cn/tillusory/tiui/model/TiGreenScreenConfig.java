package cn.tillusory.tiui.model;

import cn.tillusory.sdk.TiSDK;
import cn.tillusory.tiui.custom.TiConfigTools;
import com.google.gson.Gson;
import java.io.File;
import java.util.List;

/**
 * @ClassName TiGreenScreenList
 * @Description 绿幕配置文件
 * @Author Spica2 7
 * @Date 2021/7/25 22:01
 */

public class TiGreenScreenConfig {

  /**
   * greenScreens
   */
  private List<TiGreenScreen> greenScreens;

  public List<TiGreenScreen> getGreenScreens() { return greenScreens;}

  public void setGreenScreens(List<TiGreenScreen> greenScreens) { this.greenScreens = greenScreens;}

  public static class TiGreenScreen {

    public static final TiGreenScreen NO_GreenScreen = new TiGreenScreen("", "", true);

    public TiGreenScreen(String name, String thumb, boolean downloaded) {
      this.name = name;
      this.thumb = thumb;
      this.downloaded = downloaded;
    }

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

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getThumb() {
      return TiSDK.getGreenScreenUrl() + File.separator + thumb;
    }

    public void setThumb(String thumb) { this.thumb = thumb;}

    public boolean isDownloaded() { return downloaded;}

    public String getUrl() {
      return TiSDK.getGreenScreenUrl() + this.name + ".png";
    }

    public void downLoaded() {
      TiGreenScreenConfig greenScreenList = TiConfigTools.getInstance().getGreenScreenList();
      for (TiGreenScreen greenScreen : greenScreenList.greenScreens) {
        if (greenScreen.name.equals(this.name) && greenScreen.thumb.equals(this.name)) {
          greenScreen.setDownloaded(true);
        }
      }
      TiConfigTools.getInstance().greenScreenDownload(new Gson().toJson(greenScreenList));
    }

    public void setDownloaded(boolean downloaded) { this.downloaded = downloaded;}
  }

}
