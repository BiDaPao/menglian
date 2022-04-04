package cn.tillusory.tiui.model;

import android.content.Context;
import androidx.annotation.StringRes;

import cn.tillusory.tiui.R;

public enum TiMakeupText {
    NO_MAKEUP(R.string.none),
    QINGSE(R.string.qingse),
    RIZA(R.string.riza),
    TIANCHENG(R.string.tiancheng),
    YOUYA(R.string.youya),
    WEIXUN(R.string.weixun),
    XINDONG(R.string.xindong),
    BIAOZHUN(R.string.biaozhunmei),
    JIAN(R.string.jianmei),
    LIUYE(R.string.liuyemei),
    PINGZHI(R.string.pingzhimei),
    LIUXING(R.string.liuxingmei),
    OUSHI(R.string.oushimei),
    ZIRAN(R.string.ziran),
    ROUHE(R.string.rouhe),
    NONGMI(R.string.nongmi),
    MEIHUO(R.string.meihuo),
    BABI(R.string.babi),
    WUMEI(R.string.wumei),
    DADI(R.string.dadi),
    NVTUAN(R.string.nvtuan),
    XIARI(R.string.xiari),
    TAOHUA(R.string.taohua),
    YANXUN(R.string.yanxun),
    YUANQI(R.string.yuanqi),

    SUYAN(R.string.suyan),
    ROUHUA(R.string.rouhua),
    SHENSUI(R.string.shensui),
    MEIHEI(R.string.meihei),
    GEXING(R.string.gexing),
    WUGU(R.string.wugu),
    QINGQIAO(R.string.qingqiao),

    PINGGUOHONG(R.string.pingguohong),
    FANQIEHONG(R.string.fanqiehong),
    NV_TUAN(R.string.nvtuan),
    ZHANLANSE(R.string.zhanlan),
    rouguimicha(R.string.rouguimicha),
    zhenggongse(R.string.zhenghongse);

    private int stringId;

    TiMakeupText(@StringRes int stringId) {
        this.stringId = stringId;
    }

    public String getString(Context context) {
        return context.getResources().getString(stringId);
    }



}
