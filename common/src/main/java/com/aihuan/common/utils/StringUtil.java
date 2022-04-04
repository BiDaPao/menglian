package com.aihuan.common.utils;

import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.R;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by cxf on 2018/9/28.
 */

public class StringUtil {
    private static DecimalFormat sDecimalFormat;
    private static DecimalFormat sDecimalFormat2;
    // private static Pattern sPattern;
    private static Pattern sIntPattern;
    private static Random sRandom;
    private static StringBuilder sStringBuilder;
    private static StringBuilder sTimeStringBuilder;


    static {
        sDecimalFormat = new DecimalFormat("#.#");
        sDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        sDecimalFormat2 = new DecimalFormat("#.##");
        sDecimalFormat2.setRoundingMode(RoundingMode.DOWN);
        //sPattern = Pattern.compile("[\u4e00-\u9fa5]");
        sIntPattern = Pattern.compile("^[-\\+]?[\\d]*$");
        sRandom = new Random();
        sStringBuilder = new StringBuilder();
        sTimeStringBuilder = new StringBuilder();
    }

    public static String format(double value) {
        return sDecimalFormat.format(value);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d) + "W";
    }


    /**
     * 把数字转化成多少万
     */
    public static String toWan2(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan3(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat2.format(num / 10000d) + "w";
    }

//    /**
//     * 判断字符串中是否包含中文
//     */
//    public static boolean isContainChinese(String str) {
//        Matcher m = sPattern.matcher(str);
//        if (m.find()) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 判断一个字符串是否是数字
     */
    public static boolean isInt(String str) {
        return sIntPattern.matcher(str).matches();
    }


    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        sTimeStringBuilder.delete(0, sTimeStringBuilder.length());
        if (hours > 0) {
            if (hours < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(hours));
            sTimeStringBuilder.append(":");
        }
        if (minutes > 0) {
            if (minutes < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(minutes));
            sTimeStringBuilder.append(":");
        } else {
            sTimeStringBuilder.append("00:");
        }
        if (seconds > 0) {
            if (seconds < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(seconds));
        } else {
            sTimeStringBuilder.append("00");
        }
        return sTimeStringBuilder.toString();
    }


    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText2(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        sTimeStringBuilder.delete(0, sTimeStringBuilder.length());
        if (hours < 10) {
            sTimeStringBuilder.append("0");
        }
        sTimeStringBuilder.append(String.valueOf(hours));
        sTimeStringBuilder.append(":");
        if (minutes > 0) {
            if (minutes < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(minutes));
            sTimeStringBuilder.append(":");
        } else {
            sTimeStringBuilder.append("00:");
        }
        if (seconds > 0) {
            if (seconds < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(seconds));
        } else {
            sTimeStringBuilder.append("00");
        }
        return sTimeStringBuilder.toString();
    }


    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText3(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        sTimeStringBuilder.delete(0, sTimeStringBuilder.length());
        if (hours > 0) {
            if (hours < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(hours));
            sTimeStringBuilder.append(":");
        }
        if (minutes > 0) {
            if (minutes < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(minutes));
            sTimeStringBuilder.append(":");
        } else {
            sTimeStringBuilder.append("00:");
        }
        if (seconds > 0) {
            if (seconds < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(seconds));
        } else {
            sTimeStringBuilder.append("00");
        }
        return sTimeStringBuilder.toString();
    }


    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText4(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        sTimeStringBuilder.delete(0, sTimeStringBuilder.length());
        if (hours > 0) {
            sTimeStringBuilder.append(String.valueOf(hours));
            sTimeStringBuilder.append(WordUtil.getString(R.string.time_hour));
        }
        if (minutes > 0) {
            sTimeStringBuilder.append(String.valueOf(minutes));
            sTimeStringBuilder.append(WordUtil.getString(R.string.time_minute));
        }
        if (seconds > 0) {
            sTimeStringBuilder.append(String.valueOf(seconds));
            sTimeStringBuilder.append(WordUtil.getString(R.string.time_second));
        }
        return sTimeStringBuilder.toString();
    }


    /**
     * 设置视频输出路径
     */
    public static String generateVideoOutputPath() {
        String outputDir = CommonAppConfig.VIDEO_PATH;
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        return outputDir + generateFileName() + ".mp4";
    }

    /**
     * 视频
     */
    public static String generateVideoPath() {
        String videoName = DateFormatUtil.getVideoCurTimeString() + sRandom.nextInt(9999);
        return "android_" + CommonAppConfig.getInstance().getUid() + "_" + videoName + ".mp4";
    }

    /**
     * 获取随机文件名
     */
    public static String generateFileName() {
        return "android_" + CommonAppConfig.getInstance().getUid() + "_" + DateFormatUtil.getVideoCurTimeString() + sRandom.nextInt(9999) + ".jpg";
    }

    /**
     * 获取随机文件名
     */
    public static String generateVoiceFileName() {
        return "android_" + CommonAppConfig.getInstance().getUid() + "_" + DateFormatUtil.getVideoCurTimeString() + sRandom.nextInt(9999) + ".mp3";
    }

    /**
     * 获取随机文件名
     */
    public static String generateVoiceM4aFileName() {
        return "android_" + CommonAppConfig.getInstance().getUid() + "_" + DateFormatUtil.getVideoCurTimeString() + sRandom.nextInt(9999) + ".m4a";
    }

    /**
     * 获取随机文件名
     */
    public static String generateCensorFileName() {
        return "android_" + CommonAppConfig.getInstance().getUid() + "_" + DateFormatUtil.getCurTimeString() + sRandom.nextInt(9999) + ".jpg";
    }

    /**
     * 多个字符串拼接
     */
    public static String contact(String... args) {
        sStringBuilder.delete(0, sStringBuilder.length());
        for (String s : args) {
            sStringBuilder.append(s);
        }
//        L.e("---contact--"+sStringBuilder.toString());
        return sStringBuilder.toString();
    }


    public static String getRandomName() {
        String name = DateFormatUtil.getVideoCurTimeString() + sRandom.nextInt(9999);
        return "android_" + CommonAppConfig.getInstance().getUid() + "_" + name;
    }


}
