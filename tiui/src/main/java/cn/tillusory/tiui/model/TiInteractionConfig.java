package cn.tillusory.tiui.model;

import cn.tillusory.sdk.TiSDK;
import cn.tillusory.tiui.custom.TiConfigTools;
import com.google.gson.Gson;
import java.io.File;
import java.util.List;

/**
 * @ClassName TiInteractionList
 * @Description 动态标签配置
 * @Author Spica2 7
 * @Date 2021/7/25 22:02
 */
public class TiInteractionConfig {
  /**
   * interactions
   */
  private List<TiInteraction> interactions;

  public List<TiInteraction> getInteractions() { return interactions;}

  public void setInteractions(List<TiInteraction> interactions) { this.interactions = interactions;}

  public static class TiInteraction {

    public static final TiInteraction NO_INTERACTION =
        new TiInteraction("", "", "", "",
            false, true, "");

    public TiInteraction(String name, String dir, String category, String thumb, boolean voiced, boolean downloaded, String hint) {
      this.name = name;
      this.dir = dir;
      this.category = category;
      this.thumb = thumb;
      this.voiced = voiced;
      this.downloaded = downloaded;
      this.hint = hint;
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
    /**
     * hint
     */
    private String hint;

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getDir() { return dir;}

    public void setDir(String dir) { this.dir = dir;}

    public String getCategory() { return category;}

    public void setCategory(String category) { this.category = category;}

    public String getThumb() { return TiSDK.getInteractionUrl() + File.separator + thumb;}

    public void setThumb(String thumb) { this.thumb = thumb;}

    public boolean isVoiced() { return voiced;}

    public void setVoiced(boolean voiced) { this.voiced = voiced;}

    public boolean isDownloaded() { return downloaded;}

    public String getUrl() {
      return TiSDK.getInteractionUrl() + File.separator + this.dir + ".zip";
    }

    /**
     * 下载完成更新缓存文件
     */
    public void downloaded() {
      TiInteractionConfig interactionList = TiConfigTools.getInstance().getInteractionList();
      for (TiInteraction interaction : interactionList.getInteractions()) {
        if (interaction.dir == this.dir && interaction.name == this.name) {
          interaction.setDownloaded(true);
        }
      }
      TiConfigTools.getInstance().interactionDownload(new Gson().toJson(interactionList));
    }

    public void setDownloaded(boolean downloaded) { this.downloaded = downloaded;}

    public String getHint() { return hint;}

    public void setHint(String hint) { this.hint = hint;}
  }
}
