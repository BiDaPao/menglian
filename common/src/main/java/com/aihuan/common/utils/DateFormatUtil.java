package com.aihuan.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cxf on 2018/7/19.
 */

public class DateFormatUtil {

    private static SimpleDateFormat sFormat;
    private static SimpleDateFormat sFormat1;
    private static SimpleDateFormat sFormat2;

    static {
        sFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        sFormat2 = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");

    }


    public static String getCurTimeString() {
        return sFormat.format(new Date());
    }

    public static String getVideoCurTimeString() {
        return sFormat2.format(new Date());
    }

    /**
     * @param dateString "yyyy-MM-dd" 格式日期
     * @return List<Int> 返回list：0:年 1:月 2:日
     */
    public static ArrayList<Integer> parseTimeList(String dateString) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (dateString.length() == 10) {
            try {
                Date date = sFormat1.parse(dateString);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                list.add(c.get(Calendar.YEAR));
                list.add(c.get(Calendar.MONTH));
                list.add(c.get(Calendar.DAY_OF_MONTH));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    /**
     * 由出生年月日获取年龄
     *
     * @param birthDay 出生年月日
     * @return 年龄
     */
    public static int getAge(String birthDay) {
        if (birthDay == null || birthDay.length() != 10) {
            return 0;
        }

        Date birthDate;
        try {
            birthDate = sFormat1.parse(birthDay);
        } catch (Exception e) {
            return 0;
        }
        L.e("birthDay: "+ birthDay  );
        int age = 0;
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            return age;
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDate);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayBirth = cal.get(Calendar.DAY_OF_MONTH);
        //年相减
        age = yearNow - yearBirth;
        //判断月份
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                //判断天
                if (dayNow < dayBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }
}
