package cn.tillusory.tiui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import cn.tillusory.sdk.TiSDK;
import cn.tillusory.tiui.model.TiGestureConfig;
import cn.tillusory.tiui.model.TiGiftConfig;
import cn.tillusory.tiui.model.TiGreenScreenConfig;
import cn.tillusory.tiui.model.TiInteractionConfig;
import cn.tillusory.tiui.model.TiMakeupConfig;
import cn.tillusory.tiui.model.TiMaskConfig;
import cn.tillusory.tiui.model.TiPortraitConfig;
import cn.tillusory.tiui.model.TiStickerConfig;
import cn.tillusory.tiui.model.TiWatermarkConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName TiConfigTools
 * @Description 配置文件管理
 * @Author Spica2 7
 * @Date 2021/7/23 15:31
 */
@SuppressWarnings("unused")
public class TiConfigTools {

  private Context context;

  //贴纸配置的文件路径
  private String PATH_STICKER;
  //面具配置的文件路径
  private String PATH_MASK;
  //礼物问文件配置路径
  private String PATH_GIFT;
  //抠图配置文件
  private String PATH_PORTRAIT;
  //互动贴纸配置文件
  private String PATH_INTERACTION;
  //绿幕配置文件
  private String PATH_GREEN_SCREEN;
  //水印配置文件
  private String PATH_WATER_MARK;
  //手势配置文件
  private String PATH_GESTURE;
  //美妆配置文件
  private String PATH_MAKEUP;

  private TiMaskConfig maskList;
  private TiStickerConfig stickerConfig;
  private TiGiftConfig gifList;
  private TiInteractionConfig interactionList;
  private TiPortraitConfig portraitList;
  private TiGreenScreenConfig greenScreenList;
  private TiWatermarkConfig watermarkList;
  private TiGestureConfig gestureList;
  private TiMakeupConfig makeupList;

  private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

  final Handler uiHandler = new Handler(Looper.getMainLooper());

  public TiMakeupConfig getMakeupList() {
    return makeupList;
  }

  @SuppressLint("StaticFieldLeak")
  private static TiConfigTools instance;

  public void initTiConfigTools(Context context) {
    this.context = context;
    instance = this;

    //贴纸配置的文件路径
    PATH_STICKER = TiSDK.getStickerPath(context) + File.separator + "stickers.json";
    //面具配置的文件路径
    PATH_MASK = TiSDK.getMaskPath(context) + File.separator + "masks.json";
    //礼物问文件配置路径
    PATH_GIFT = TiSDK.getGiftPath(context) + File.separator + "gifts.json";
    //抠图配置文件
    PATH_PORTRAIT = TiSDK.getPortraitPath(context) + File.separator + "portraits.json";
    //互动贴纸配置文件
    PATH_INTERACTION = TiSDK.getInteractionPath(context) + File.separator + "interactions.json";
    //绿幕配置文件
    PATH_GREEN_SCREEN = TiSDK.getGreenScreenPath(context) + File.separator + "greenscreens.json";
    //水印配置文件
    PATH_WATER_MARK = TiSDK.getWatermarkPath(context) + File.separator + "watermarks.json";
    //手势配置文件
    PATH_GESTURE = TiSDK.getGesturePath(context) + File.separator + "gestures.json";
    //美妆配置文件
    PATH_MAKEUP = TiSDK.getMakeupPath(context) + File.separator + "makeups.json";
  }

  public TiInteractionConfig getInteractionList() {
    if (interactionList == null) return null;
    return interactionList;
  }

  public TiPortraitConfig getPortraitList() {
    if (portraitList == null) return null;
    return portraitList;
  }

  public TiGreenScreenConfig getGreenScreenList() {
    if (greenScreenList == null) return null;
    return greenScreenList;
  }

  public TiWatermarkConfig getWatermarkList() {
    if (watermarkList == null) return null;
    return watermarkList;
  }

  public TiGestureConfig getGestureList() {
    if (gestureList == null) return null;
    return gestureList;
  }

  public static TiConfigTools getInstance() {
    if (instance == null) instance = new TiConfigTools();
    return instance;
  }

  public TiGiftConfig getGifList() {
    if (gifList == null) return null;
    return gifList;
  }

  /**
   * 更新mask文件
   *
   * @param content json 内容
   */
  public void maskDownload(final String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_MASK);
      }
    });
  }

  public TiMaskConfig getMaskList() {
    if (maskList == null) return null;
    return maskList;
  }

  public TiStickerConfig getStickerConfig() {
    if (stickerConfig == null) return null;
    return stickerConfig;
  }

  /**
   * 从硬盘中获取Mask配置
   */
  public void getMaskConfig(TiConfigCallBack<List<TiMaskConfig.TiMask>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String result = getFileString(PATH_MASK);
          if (TextUtils.isEmpty(result)) {
            Log.i("读取Mask配置文件：", "读取内容为空");
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });
          } else {
            maskList = new Gson().fromJson(result,
                new TypeToken<TiMaskConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(maskList.getMasks());
              }
            });
          }

        } catch (IOException e) {
          uiHandler.post(new Runnable() {
            @Override public void run() {
              callBack.fail(e);
            }
          });
        }
      }
    });
  }

  /**
   * 获取缓存文件中贴纸配置
   */
  public void getStickersConfig(TiConfigCallBack<List<TiStickerConfig.TiSticker>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String res = getFileString(PATH_STICKER);
          if (TextUtils.isEmpty(res)) {
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });
          } else {
            stickerConfig = new Gson().fromJson(res, new TypeToken<TiStickerConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(stickerConfig.getStickers());
              }
            });
          }

        } catch (Exception e) {
          uiHandler.post(new Runnable() {
            @Override public void run() {
              callBack.fail(e);
            }
          });
        }
      }
    });
  }

  /**
   * 更新贴纸文件
   */
  public void stickerDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_STICKER);
      }
    });
  }

  /**
   * 从缓存文件中获取礼物配置文件
   */
  public void getGiftsConfig(TiConfigCallBack<List<TiGiftConfig.TiGift>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String result = getFileString(PATH_GIFT);

          if (TextUtils.isEmpty(result)) {
            Log.i("读取礼物配置文件：", "内容为空");
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });

          } else {
            gifList = new Gson().fromJson(result, new TypeToken<TiGiftConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(gifList.getGifts());
              }
            });
          }

        } catch (IOException e) {
          e.printStackTrace();
          callBack.fail(e);
        }
      }
    });
  }

  /**
   * 更新礼物缓存文件
   */
  public void giftDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_GIFT);
      }
    });
  }

  /**
   * 从缓存文件中获取绿幕配置文件
   */
  public void getGreenScreenConfig(TiConfigCallBack<List<TiGreenScreenConfig.TiGreenScreen>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {


        try {
          String result = getFileString(PATH_GREEN_SCREEN);

          if (TextUtils.isEmpty(result)) {
            Log.i("读取绿幕配置文件：", "内容为空");
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });

          } else {
            greenScreenList = new Gson().fromJson(result, TiGreenScreenConfig.class);
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(greenScreenList.getGreenScreens());
              }
            });
          }

        } catch (IOException e) {
          e.printStackTrace();
          callBack.fail(e);
        }


      }
    });
  }

  /**
   * 更新绿幕缓存文件
   */
  public void greenScreenDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_GREEN_SCREEN);
      }
    });
  }

  /**
   * 从缓存文件中获取抠图配置文件
   */
  public void getPortraitConfig(TiConfigCallBack<List<TiPortraitConfig.TiPortrait>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String result = getFileString(PATH_PORTRAIT);

          if (TextUtils.isEmpty(result)) {
            Log.i("读取抠图配置文件：", "内容为空");
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });

          } else {
            portraitList = new Gson().fromJson(result, new TypeToken<TiPortraitConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(portraitList.getPortraits());
              }
            });
          }

        } catch (IOException e) {
          e.printStackTrace();
          callBack.fail(e);
        }
      }
    });
  }

  /**
   * 更新抠图缓存文件
   */
  public void portraitDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_PORTRAIT);
      }
    });
  }

  /**
   * 从缓存文件中获取水印配置文件
   */
  public void getWaterMarkConfig(TiConfigCallBack<List<TiWatermarkConfig.TiWatermark>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String result = getFileString(PATH_WATER_MARK);

          if (TextUtils.isEmpty(result)) {
            Log.i("读取绿幕配置文件：", "内容为空");
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });

          } else {
            watermarkList = new Gson().fromJson(result, new TypeToken<TiWatermarkConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(watermarkList.getWatermarks());
              }
            });
          }

        } catch (IOException e) {
          e.printStackTrace();
          callBack.fail(e);
        }
      }
    });
  }

  /**
   * 更新抠图缓存文件
   */
  public void watermarkDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_WATER_MARK);
      }
    });
  }

  /**
   * 从缓存文件中获取水印配置文件
   */
  public void getGestureConfig(TiConfigCallBack<List<TiGestureConfig.TiGesture>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String result = getFileString(PATH_GESTURE);

          if (TextUtils.isEmpty(result)) {
            Log.i("读取绿幕配置文件：", "内容为空");
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });

          } else {
            gestureList = new Gson().fromJson(result, new TypeToken<TiGestureConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(gestureList.getGestures());
              }
            });
          }

        } catch (IOException e) {
          e.printStackTrace();
          callBack.fail(e);
        }
      }
    });
  }

  /**
   * 获取特定的Type的美妆配置文件
   */
  public List<TiMakeupConfig.TiMakeup> getMakeupsWithType(String type) {
    ArrayList<TiMakeupConfig.TiMakeup> items = new ArrayList<>();
    if (makeupList == null) {
      try {
        String res = getJsonString(context, "makeup/makeups.json");
        makeupList = new Gson().fromJson(res, new TypeToken<TiMakeupConfig>() {}.getType());
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    }
    for (TiMakeupConfig.TiMakeup makeup : makeupList.getMakeups()) {
      if (makeup.getType().equals(type)) {
        items.add(makeup);
      }
    }
    return items;
  }

  /**
   * 更新手势缓存文件
   */
  public void gestureDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_GESTURE);
      }
    });
  }

  /**
   * 获取缓存中的动态贴纸中配置文件
   */
  public void getInteractionConfig(TiConfigCallBack<List<TiInteractionConfig.TiInteraction>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String result = getFileString(PATH_INTERACTION);

          if (TextUtils.isEmpty(result)) {
            Log.i("读取动态贴纸配置文件：", "内容为空");
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });

          } else {
            interactionList = new Gson().fromJson(result, new TypeToken<TiInteractionConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(interactionList.getInteractions());
              }
            });
          }

        } catch (IOException e) {
          e.printStackTrace();
          callBack.fail(e);
        }
      }
    });
  }

  /**
   * 更新动态贴纸缓存文件
   */
  public void interactionDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_INTERACTION);
      }
    });
  }

  /**
   * 美妆的配置文件改写
   */
  public void makeupDownLoad(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_MAKEUP);
      }
    });
  }

  /**
   * 写入文件
   *
   * @param content 内容
   * @param filePath 地址
   */
  private void modifyFile(String content, String filePath) {
    try {
      FileWriter fileWriter = new FileWriter(filePath, false);
      BufferedWriter writer = new BufferedWriter(fileWriter);
      writer.append(content);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 读取assets下配置文件
   *
   * @param context 上下文
   * @param fileName 文件名
   * @return 内容
   */
  private String getJsonString(Context context, String fileName)
      throws IOException {
    BufferedReader br = null;
    StringBuilder sb = new StringBuilder();
    try {
      AssetManager manager = context.getAssets();
      br = new BufferedReader(new InputStreamReader(manager.open(fileName)));
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return sb.toString();
  }

  public void release() {
    this.context = null;
  }

  /**
   * 获取指定目录下的字符内容
   *
   * @param filePath 路径
   * @return 字符内容
   */
  private String getFileString(String filePath) throws IOException {
    BufferedReader br = null;
    StringBuilder sb = new StringBuilder();
    try {
      File file = new File(filePath);
      FileInputStream fis = new FileInputStream(file);
      br = new BufferedReader(new InputStreamReader(fis));
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      //            return sb.toString();
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return sb.toString();
  }

  /**
   * 重置配置文件
   */
  public void resetConfigFile() {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String newSticker = getJsonString(context, "sticker/stickers.json");
          modifyFile(newSticker, PATH_STICKER);
        } catch (IOException e) {
          e.printStackTrace();
        }

        String newGift;
        try {
          newGift = getJsonString(context, "gift/gift.json");
          modifyFile(newGift, PATH_GIFT);
        } catch (IOException e) {
          e.printStackTrace();
        }

        String newMask;
        try {
          newMask = getJsonString(context, "mask/masks.json");
          modifyFile(newMask, PATH_MASK);
        } catch (IOException e) {
          e.printStackTrace();
        }

        String newWatermark;
        try {
          newWatermark = getJsonString(context, "watermark/watermarks.json");
          modifyFile(newWatermark, PATH_WATER_MARK);
        } catch (IOException e) {
          e.printStackTrace();
        }

        String newPortraits;
        try {
          newPortraits = getJsonString(context, "portrait/portraits.json");
          modifyFile(newPortraits, PATH_PORTRAIT);
        } catch (IOException e) {
          e.printStackTrace();
        }

        String newGreenScreen;
        try {
          newGreenScreen = getJsonString(context, "greenscreen/greenscreens.json");
          modifyFile(newGreenScreen, PATH_GREEN_SCREEN);
        } catch (IOException e) {
          e.printStackTrace();
        }

        try {
          String newInteractions = getJsonString(context, "interaction/interactions.json");
          modifyFile(newInteractions, PATH_INTERACTION);
        } catch (IOException e) {
          e.printStackTrace();
        }

        try {
          String newInteractions = getJsonString(context, "gesture/gestures.json");
          modifyFile(newInteractions, PATH_GESTURE);
        } catch (IOException e) {
          e.printStackTrace();
        }

        try {
          String newMakeups = getJsonString(context, "makeup/makeups.json");
          modifyFile(newMakeups, PATH_MAKEUP);
          makeupList = new Gson().fromJson(newMakeups, new TypeToken<TiMakeupConfig>() {}.getType());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

}

