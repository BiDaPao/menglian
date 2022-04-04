package com.aihuan.common.utils;

import com.aihuan.common.event.LocationGetCityEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.aihuan.common.CommonAppConfig;
import com.aihuan.common.CommonAppContext;
import com.aihuan.common.bean.TxLocationBean;
import com.aihuan.common.bean.TxLocationPoiBean;
import com.aihuan.common.event.LocationEvent;
import com.aihuan.common.http.CommonHttpConsts;
import com.aihuan.common.http.CommonHttpUtil;
import com.aihuan.common.http.HttpCallback;
import com.aihuan.common.interfaces.CommonCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class LocationUtil {
    private static final String TAG = "定位";
    private static LocationUtil sInstance;
    private TencentLocationManager mLocationManager;
    private boolean mLocationStarted;
    private boolean mNeedPostLocationEvent;//是否需要发送定位成功事件

    private LocationUtil() {
        mLocationManager = TencentLocationManager.getInstance(CommonAppContext.sInstance);
    }

    public static LocationUtil getInstance() {
        if (sInstance == null) {
            synchronized (LocationUtil.class) {
                if (sInstance == null) {
                    sInstance = new LocationUtil();
                }
            }
        }
        return sInstance;
    }


    private TencentLocationListener mLocationListener = new TencentLocationListener() {
        @Override
        public void onLocationChanged(TencentLocation location, int code, String reason) {
            L.e(TAG,"---code---"+code+"--reason---"+reason);
            if (code == TencentLocation.ERROR_OK) {
                double lng = location.getLongitude();//经度
                double lat = location.getLatitude();//纬度
                L.e(TAG, "获取经纬度成功------>经度：" + lng + "，纬度：" + lat);
                CommonHttpUtil.getAddressInfoByTxLocaitonSdk(lng, lat, 0, 1, CommonHttpConsts.GET_LOCAITON, mCallback);
                if (mNeedPostLocationEvent) {
                    EventBus.getDefault().post(new LocationEvent(lng, lat));
                }
            }
        }

        @Override
        public void onStatusUpdate(String s, int i, String s1) {

        }
    };

    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                if (obj != null) {
                    L.e(TAG, "获取位置信息成功---当前地址--->" + obj.getString("address"));
                    JSONObject location = obj.getJSONObject("location");
                    double lng = location.getDoubleValue("lng");
                    double lat = location.getDoubleValue("lat");
                    JSONObject addressComponent = obj.getJSONObject("address_component");
                    CommonAppConfig.getInstance().setLocationInfo(
                            lng, lat,
                            addressComponent.getString("province"),
                            addressComponent.getString("city"),
                            addressComponent.getString("district"));
                    CommonHttpUtil.setLocaiton(lng, lat);
                    EventBus.getDefault().post(new LocationGetCityEvent());
                }
            }
        }
    };


    //启动定位
    public void startLocation() {
        if (!mLocationStarted && mLocationManager != null) {
            mLocationStarted = true;
            L.e(TAG, "开启定位");
            TencentLocationRequest request = TencentLocationRequest
                    .create()
                    .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_GEO)
                    .setInterval(60 * 60 * 1000);//1小时定一次位

            //当定位周期大于0时, 不论是否有得到新的定位结果, 位置监听器都会按定位周期定时被回调;
            // 当定位周期等于0时, 仅当有新的定位结果时, 位置监听器才会被回调(即, 回调时机存在不确定性).
            // 如果需要周期性回调, 建议将 定位周期 设置为 5000-10000ms
           int code= mLocationManager.requestLocationUpdates(request, mLocationListener);
            L.e(TAG,"--code-"+code);
        }
    }

    //停止定位
    public void stopLocation() {
        CommonHttpUtil.cancel(CommonHttpConsts.GET_LOCAITON);
        if (mLocationStarted && mLocationManager != null) {
            L.e(TAG, "关闭定位");
            mLocationManager.removeUpdates(mLocationListener);
            mLocationStarted = false;
        }
    }

    public void setNeedPostLocationEvent(boolean needPostLocationEvent) {
        mNeedPostLocationEvent = needPostLocationEvent;
    }

}
