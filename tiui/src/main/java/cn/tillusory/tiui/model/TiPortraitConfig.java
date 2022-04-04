package cn.tillusory.tiui.model;

import cn.tillusory.sdk.TiSDK;
import cn.tillusory.tiui.custom.TiConfigTools;
import com.google.gson.Gson;
import java.io.File;
import java.util.List;

/**
 * @ClassName TiPortrait
 * @Description TODO
 * @Author Spica2 7
 * @Date 2021/7/25 21:01
 */

@SuppressWarnings("unused")
public class TiPortraitConfig {
  /**
   * portraits
   */
  private List<TiPortrait> portraits;

  public List<TiPortrait> getPortraits() { return portraits;}

  public void setPortraits(List<TiPortrait> portraits) { this.portraits = portraits;}

  public static class TiPortrait {

    public static final TiPortrait NO_Portrait = new TiPortrait("", "", "", "", false, true);

    public TiPortrait(String name, String dir, String category, String thumb, boolean voiced, boolean downloaded) {
      this.name = name;
      this.dir = dir;
      this.category = category;
      this.thumb = thumb;
      this.voiced = voiced;
      this.downloaded = downloaded;
    }

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

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getDir() { return dir;}

    public void setDir(String dir) { this.dir = dir;}

    public String getCategory() { return category;}

    public void setCategory(String category) { this.category = category;}

    public String getThumb() { return TiSDK.getPortraitUrl() + File.separator + thumb;}

    public void setThumb(String thumb) { this.thumb = thumb;}

    public boolean isVoiced() { return voiced;}

    public void setVoiced(boolean voiced) { this.voiced = voiced;}

    public boolean isDownloaded() { return downloaded;}

    public String getUrl() {
      return TiSDK.getPortraitUrl() + File.separator + this.dir + ".zip";
    }

    public void downloaded() {
      TiPortraitConfig portraitList = TiConfigTools.getInstance().getPortraitList();

      for (TiPortrait portrait : portraitList.getPortraits()) {
        if (portrait.name.equals(this.name) && portrait.dir.equals(this.dir)) {
          portrait.setDownloaded(true);
        }
      }

      TiConfigTools.getInstance().portraitDownload(new Gson().toJson(portraitList));

    }

    public void setDownloaded(boolean downloaded) {
      this.downloaded = downloaded;
    }
  }
}
