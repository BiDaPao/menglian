package com.aihuan.common.bean;

/**
 * Created by cxf on 2018/9/28.
 * 我的 页面的item
 */

public class UserItemBean {

    private int id;
    private String name;
    private String thumb;
    private String href;
    private boolean mRadioBtnChecked;
    private String priceText;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean isRadioBtnChecked() {
        return mRadioBtnChecked;
    }

    public void setRadioBtnChecked(boolean radioBtnChecked) {
        mRadioBtnChecked = radioBtnChecked;
    }


    public void toggleRadioBtn() {
        mRadioBtnChecked = !mRadioBtnChecked;
    }

    public String getPriceText() {
        return priceText;
    }

    public void setPriceText(String priceText) {
        this.priceText = priceText;
    }
}
