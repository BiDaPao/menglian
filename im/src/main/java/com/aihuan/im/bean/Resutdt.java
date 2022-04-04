package com.aihuan.im.bean;

/**
 * @author Saint  2022/3/18 15:05
 * @DESC:
 */
public class Resutdt {
    private String suggestion;

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public boolean isBlock() {
        //block 内容违规 pass 内容正常  review无法确认是否违规
        return "block".equals(suggestion);
    }
}
