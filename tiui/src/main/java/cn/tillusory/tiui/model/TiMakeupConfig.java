package cn.tillusory.tiui.model;

import cn.tillusory.sdk.TiSDK;
import cn.tillusory.tiui.custom.TiConfigTools;
import com.google.gson.Gson;
import java.io.File;
import java.util.List;

/**
 * @ClassName TiMakeupList
 * @Description TODO
 * @Author Spica2 7
 * @Date 2021/7/26 9:56
 */

public class TiMakeupConfig {

  /**
   * makeups
   */
  private List<TiMakeup> makeups;

  public List<TiMakeup> getMakeups() { return makeups;}

  public void setMakeups(List<TiMakeup> makeups) { this.makeups = makeups;}

  public static class TiMakeup {

    public static final TiMakeup NO_MAKEUP = new TiMakeup("", "", "", false);

    public TiMakeup(String name, String thumb, String type, boolean downloaded) {
      this.name = name;
      this.thumb = thumb;
      this.type = type;
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
     * type
     */
    private String type;
    /**
     * downloaded
     */
    private boolean downloaded;

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getThumb() { return TiSDK.getMakeupUrl() + File.separator + thumb;}

    public void setThumb(String thumb) { this.thumb = thumb;}

    public String getType() { return type;}

    public String getUrl() {
      return TiSDK.getMakeupUrl() + File.separator + this.name + ".png";
    }

    /**
     * 下载完成设置缓存文件
     */
    public void downloaded() {
      TiMakeupConfig makeupList = TiConfigTools.getInstance().getMakeupList();
      for (TiMakeup makeup : makeupList.getMakeups()) {
        if (makeup.name.equals(this.name) && makeup.type.equals(this.type)) {
          makeup.setDownloaded(true);
        }
      }
      TiConfigTools.getInstance().makeupDownLoad(new Gson().toJson(makeupList));
    }

    public void setType(String type) { this.type = type;}

    public boolean isDownloaded() { return downloaded;}

    public void setDownloaded(boolean downloaded) { this.downloaded = downloaded;}
  }

}
