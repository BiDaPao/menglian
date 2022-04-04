package cn.tillusory.tiui.model;

import cn.tillusory.sdk.TiSDK;
import cn.tillusory.tiui.custom.TiConfigTools;
import com.google.gson.Gson;
import java.io.File;
import java.util.List;

/**
 * @ClassName TiGestureList
 * @Description 手势配置参数
 * @Author Spica2 7
 * @Date 2021/7/25 22:19
 */
@SuppressWarnings("unused")
public class TiGestureConfig {
  /**
   * gestures
   */
  private List<TiGesture> gestures;

  public List<TiGesture> getGestures() { return gestures;}

  public void setGestures(List<TiGesture> gestures) { this.gestures = gestures;}

  public static class TiGesture {

    public static final TiGesture NO_Gesture =
        new TiGesture("", "", "", "", false, true, "");

    public TiGesture(String name, String dir, String category, String thumb, boolean voiced, boolean downloaded, String hint) {
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

    public String getThumb() {
      return TiSDK.getGestureUrl()+File.separator+thumb; }

    public void setThumb(String thumb) { this.thumb = thumb;}

    public boolean isVoiced() { return voiced;}

    public void setVoiced(boolean voiced) { this.voiced = voiced;}

    public boolean isDownloaded() { return downloaded;}

    public String getUrl() {
      return TiSDK.getGestureUrl() + File.separator + this.dir + ".zip";
    }

    public void setDownloaded(boolean downloaded) {
      this.downloaded = downloaded;
    }

    /**
     * 下载完成更新对应的缓存
     */
    public void downloaded() {
      TiGestureConfig gestureList = TiConfigTools.getInstance().getGestureList();
      for (TiGesture gesture : gestureList.gestures) {
        if (gesture.name.equals(this.name) && gesture.dir.equals(this.dir)) {
          gesture.setDownloaded(true);
        }
      }
      TiConfigTools.getInstance().gestureDownload(new Gson().toJson(gestureList));
    }

    public String getHint() { return hint;}

    public void setHint(String hint) { this.hint = hint;}
  }
}
