package cn.tillusory.tiui.model;

import cn.tillusory.sdk.TiSDK;
import cn.tillusory.tiui.custom.TiConfigTools;
import com.google.gson.Gson;
import java.io.File;
import java.util.List;

/**
 * @ClassName TiWatermark
 * @Description TODO
 * @Author Spica2 7
 * @Date 2021/7/25 22:03
 */
public class TiWatermarkConfig {
  /**
   * watermarks
   */
  private List<TiWatermark> watermarks;

  public List<TiWatermark> getWatermarks() { return watermarks;}

  public void setWatermarks(List<TiWatermark> tiWatermarks) { this.watermarks = tiWatermarks;}

  public static class TiWatermark {

    public static final TiWatermark NO_WATERMARK =
        new TiWatermark(0, 0, 0, "", "", true
        );

    public TiWatermark(int x, int y, int ratio, String name, String thumb, boolean downloaded) {
      this.x = x;
      this.y = y;
      this.ratio = ratio;
      this.name = name;
      this.thumb = thumb;
      this.downloaded = downloaded;
    }

    /**
     * x
     */
    private int x;
    /**
     * y
     */
    private int y;
    /**
     * ratio
     */
    private int ratio;
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

    public int getX() { return x;}

    public void setX(int x) { this.x = x;}

    public int getY() { return y;}

    public void setY(int y) { this.y = y;}

    public int getRatio() { return ratio;}

    public void setRatio(int ratio) { this.ratio = ratio;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getThumb() { return TiSDK.getWatermarkUrl() + File.separator + thumb;}

    public void setThumb(String thumb) { this.thumb = thumb;}

    public boolean isDownloaded() { return downloaded;}

    public void setDownloaded(boolean downloaded) { this.downloaded = downloaded;}

    /**
     * 下载完更新缓存
     */
    public void downloaded() {
      TiWatermarkConfig watermarkList = TiConfigTools.getInstance().getWatermarkList();

      for (TiWatermark watermark : watermarkList.watermarks) {
        if (watermark.name == this.name) {
          watermark.setDownloaded(true);
        }
      }

      TiConfigTools.getInstance().watermarkDownload(new Gson().toJson(watermarkList));

    }

  }

}
