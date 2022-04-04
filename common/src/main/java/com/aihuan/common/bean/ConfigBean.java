package com.aihuan.common.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2017/8/5.
 */

public class ConfigBean {

    private String version;//Android apk安装包 版本号
    private String downloadApkUrl;//Android apk安装包 下载地址
    private String updateDes;//版本更新描述
    private String[] loginType;//三方登录类型
    private String[] shareType;//分享类型
    private String coinName;//钻石名称
    private String votesName;//映票名称
    private String videoShareTitle;//短视频分享标题
    private String videoShareDes;//短视频分享描述
    private String agentShareTitle;//全民赚钱分享标题
    private String agentShareDes;//全民赚钱分享描述
    private int videoAuditSwitch;//短视频审核是否开启
    private int videoCloudType;//短视频云储存类型 1七牛云 2腾讯云
    private String videoQiNiuHost;//短视频七牛云域名
    private String txCosAppId;//腾讯云存储appId
    private String txCosRegion;//腾讯云存储区域
    private String txCosBucketName;//腾讯云存储桶名字
    private String txCosVideoPath;//腾讯云存储视频文件夹
    private String txCosImagePath;//腾讯云存储图片文件夹
    private int maintainSwitch;//维护开关
    private String maintainTips;//维护提示
    private String mTxImGroupId;//腾讯IM大群ID
    private String beautyKey;//萌颜鉴权码
    private int beautyMeiBai;//萌颜美白数值
    private int beautyMoPi;//萌颜磨皮数值
    private int beautyBaoHe;//萌颜饱和数值
    private int beautyFenNen;//萌颜粉嫩数值
    private int beautyBigEye;//萌颜大眼数值
    private int beautyFace;//萌颜瘦脸数值
    private String mImTip;

    @JSONField(name = "apk_ver")
    public String getVersion() {
        return version;
    }

    @JSONField(name = "apk_ver")
    public void setVersion(String version) {
        this.version = version;
    }

    @JSONField(name = "apk_url")
    public String getDownloadApkUrl() {
        return downloadApkUrl;
    }

    @JSONField(name = "apk_url")
    public void setDownloadApkUrl(String downloadApkUrl) {
        this.downloadApkUrl = downloadApkUrl;
    }

    @JSONField(name = "apk_des")
    public String getUpdateDes() {
        return updateDes;
    }

    @JSONField(name = "apk_des")
    public void setUpdateDes(String updateDes) {
        this.updateDes = updateDes;
    }


    @JSONField(name = "name_coin")
    public String getCoinName() {
        return coinName;
    }

    @JSONField(name = "name_coin")
    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    @JSONField(name = "name_votes")
    public String getVotesName() {
        return votesName;
    }

    @JSONField(name = "name_votes")
    public void setVotesName(String votesName) {
        this.votesName = votesName;
    }


    @JSONField(name = "login_type")
    public String[] getLoginType() {
        return loginType;
    }

    @JSONField(name = "login_type")
    public void setLoginType(String[] loginType) {
        this.loginType = loginType;
    }

    @JSONField(name = "share_type")
    public String[] getShareType() {
        return shareType;
    }

    @JSONField(name = "share_type")
    public void setShareType(String[] shareType) {
        this.shareType = shareType;
    }

    @JSONField(name = "maintain_switch")
    public int getMaintainSwitch() {
        return maintainSwitch;
    }

    @JSONField(name = "maintain_switch")
    public void setMaintainSwitch(int maintainSwitch) {
        this.maintainSwitch = maintainSwitch;
    }

    @JSONField(name = "maintain_tips")
    public String getMaintainTips() {
        return maintainTips;
    }

    @JSONField(name = "maintain_tips")
    public void setMaintainTips(String maintainTips) {
        this.maintainTips = maintainTips;
    }

    @JSONField(name = "full_group_id")
    public String getTxImGroupId() {
        return mTxImGroupId;
    }

    @JSONField(name = "full_group_id")
    public void setTxImGroupId(String txImGroupId) {
        mTxImGroupId = txImGroupId;
    }

    @JSONField(name = "sprout_key")
    public String getBeautyKey() {
        return beautyKey;
    }

    @JSONField(name = "sprout_key")
    public void setBeautyKey(String beautyKey) {
        this.beautyKey = beautyKey;
    }

    @JSONField(name = "sprout_white")
    public int getBeautyMeiBai() {
        return beautyMeiBai;
    }

    @JSONField(name = "sprout_white")
    public void setBeautyMeiBai(int beautyMeiBai) {
        this.beautyMeiBai = beautyMeiBai;
    }

    @JSONField(name = "sprout_skin")
    public int getBeautyMoPi() {
        return beautyMoPi;
    }

    @JSONField(name = "sprout_skin")
    public void setBeautyMoPi(int beautyMoPi) {
        this.beautyMoPi = beautyMoPi;
    }

    @JSONField(name = "sprout_saturated")
    public int getBeautyBaoHe() {
        return beautyBaoHe;
    }

    @JSONField(name = "sprout_saturated")
    public void setBeautyBaoHe(int beautyBaoHe) {
        this.beautyBaoHe = beautyBaoHe;
    }

    @JSONField(name = "sprout_pink")
    public int getBeautyFenNen() {
        return beautyFenNen;
    }

    @JSONField(name = "sprout_pink")
    public void setBeautyFenNen(int beautyFenNen) {
        this.beautyFenNen = beautyFenNen;
    }

    @JSONField(name = "sprout_eye")
    public int getBeautyBigEye() {
        return beautyBigEye;
    }

    @JSONField(name = "sprout_eye")
    public void setBeautyBigEye(int beautyBigEye) {
        this.beautyBigEye = beautyBigEye;
    }

    @JSONField(name = "sprout_face")
    public int getBeautyFace() {
        return beautyFace;
    }

    @JSONField(name = "sprout_face")
    public void setBeautyFace(int beautyFace) {
        this.beautyFace = beautyFace;
    }


    public String[] getVideoShareTypes() {
        return shareType;
    }

    @JSONField(name = "share_video_title")
    public String getVideoShareTitle() {
        return videoShareTitle;
    }

    @JSONField(name = "share_video_title")
    public void setVideoShareTitle(String videoShareTitle) {
        this.videoShareTitle = videoShareTitle;
    }

    @JSONField(name = "share_video_des")
    public String getVideoShareDes() {
        return videoShareDes;
    }

    @JSONField(name = "share_video_des")
    public void setVideoShareDes(String videoShareDes) {
        this.videoShareDes = videoShareDes;
    }

    @JSONField(name = "share_agent_title")
    public String getAgentShareTitle() {
        return agentShareTitle;
    }

    @JSONField(name = "share_agent_title")
    public void setAgentShareTitle(String agentShareTitle) {
        this.agentShareTitle = agentShareTitle;
    }

    @JSONField(name = "share_agent_des")
    public String getAgentShareDes() {
        return agentShareDes;
    }

    @JSONField(name = "share_agent_des")
    public void setAgentShareDes(String agentShareDes) {
        this.agentShareDes = agentShareDes;
    }

    @JSONField(name = "video_audit_switch")
    public int getVideoAuditSwitch() {
        return videoAuditSwitch;
    }

    @JSONField(name = "video_audit_switch")
    public void setVideoAuditSwitch(int videoAuditSwitch) {
        this.videoAuditSwitch = videoAuditSwitch;
    }

    @JSONField(name = "cloudtype")
    public int getVideoCloudType() {
        return videoCloudType;
    }

    @JSONField(name = "cloudtype")
    public void setVideoCloudType(int videoCloudType) {
        this.videoCloudType = videoCloudType;
    }

    @JSONField(name = "qiniu_domain")
    public String getVideoQiNiuHost() {
        return videoQiNiuHost;
    }

    @JSONField(name = "qiniu_domain")
    public void setVideoQiNiuHost(String videoQiNiuHost) {
        this.videoQiNiuHost = videoQiNiuHost;
    }

    @JSONField(name = "txcloud_appid")
    public String getTxCosAppId() {
        return txCosAppId;
    }

    @JSONField(name = "txcloud_appid")
    public void setTxCosAppId(String txCosAppId) {
        this.txCosAppId = txCosAppId;
    }

    @JSONField(name = "txcloud_region")
    public String getTxCosRegion() {
        return txCosRegion;
    }

    @JSONField(name = "txcloud_region")
    public void setTxCosRegion(String txCosRegion) {
        this.txCosRegion = txCosRegion;
    }

    @JSONField(name = "txcloud_bucket")
    public String getTxCosBucketName() {
        return txCosBucketName;
    }

    @JSONField(name = "txcloud_bucket")
    public void setTxCosBucketName(String txCosBucketName) {
        this.txCosBucketName = txCosBucketName;
    }

    @JSONField(name = "txvideofolder")
    public String getTxCosVideoPath() {
        return txCosVideoPath;
    }

    @JSONField(name = "txvideofolder")
    public void setTxCosVideoPath(String txCosVideoPath) {
        this.txCosVideoPath = txCosVideoPath;
    }

    @JSONField(name = "tximgfolder")
    public String getTxCosImagePath() {
        return txCosImagePath;
    }

    @JSONField(name = "tximgfolder")
    public void setTxCosImagePath(String txCosImagePath) {
        this.txCosImagePath = txCosImagePath;
    }

    @JSONField(name = "im_tips")
    public String getImTip() {
        return mImTip;
    }

    @JSONField(name = "im_tips")
    public void setImTip(String imTip) {
        mImTip = imTip;
    }
}
