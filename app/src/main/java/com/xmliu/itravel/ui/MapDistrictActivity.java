/*
 *  *
 *  *************************************************************************
 *  *
 *  *  Copyright (C) 2015 XMLIU diyangxia@163.com.
 *  *
 *  *                       All rights reserved.
 *  *
 *  **************************************************************************
 */

package com.xmliu.itravel.ui;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Polygon;
import com.amap.api.maps2d.model.PolygonOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.xmliu.itravel.R;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2016/3/14 15:52
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: 地图足迹，以地级市为单位
 */
public class MapDistrictActivity extends ToolbarActivity {

    /**
     * 警戒时间，单位为毫秒，设置为-1时表示没有时间限制。地理围栏有误差
     */
    private int expiration = 1000 * 60 * 1;
    /**
     * 警戒区域的半径，单位为米。区域半径不能太小，经测试必须大于等于100
     */
    private int radius = 101;

    /**
     * 围栏id
     */
    private String fenceId = "1001";

    private String GEOFENCE_BROADCAST_ACTION = "sssssssssssssssss";

    private AMap aMap;
    private MapView mapView;

    private List<Polygon> polygonList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_district);

        toolbar.setTitle("足迹地图");

        mapView = (MapView) findViewById(R.id.map_fence_map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
            aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
            aMap.setMyLocationEnabled(true);
        }

        searchCity("南京", 1001, Color.BLUE, ContextCompat.getColor(MapDistrictActivity.this, R.color.bluefour)); // 可以显示多个行政规划
        searchCity("盐城", 1002, ContextCompat.getColor(MapDistrictActivity.this, R.color.purple), ContextCompat.getColor(MapDistrictActivity.this, R.color.purplefour)); // 可以显示多个行政规划
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // 如果点击的经纬度在多边形区域内，则算点击了行政区
                for(Polygon polygon:polygonList){
                if (polygon.contains(latLng)) {
                    CommonUtils.showToast(MapDistrictActivity.this, "在盐城发了哪些状态"+polygon.getId());
                } else {
                    CommonUtils.showToast(MapDistrictActivity.this, "不提示用户2" +polygon.getId());
                }}
            }
        });
//        searchProvince("湖北",Color.BLUE,ContextCompat.getColor(MapFenceActivity.this,R.color.bluefour));//添加省，以省为单位，尽量不以市为单位
//        startFence();
    }

    private void startFence() {
        //实例化定位客户端
        AMapLocationClient mlocationClient = null;
        mlocationClient = new AMapLocationClient(getApplicationContext());

        //注册Receiver，设置过滤器
        IntentFilter fliter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        fliter.addAction(GEOFENCE_BROADCAST_ACTION);
        //mGeoFenceReceiver为自定义的广播接收器
        registerReceiver(mGeoFenceReceiver, fliter);

        //声明对应的intent对象
        Intent intent = new Intent(GEOFENCE_BROADCAST_ACTION);
        //创建PendingIntent对象
        PendingIntent mPendingIntent = null;
        mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        LatLng latLng = new LatLng(31.264193, 120.731466);
        LatLng latLng2 = new LatLng(31.333333, 120.444444);
        //添加地理围栏
        mlocationClient.addGeoFenceAlert(fenceId, latLng.latitude, latLng.longitude, radius, expiration, mPendingIntent);
        //启动定位
        mlocationClient.startLocation();

        // 添加地理围栏圆形覆盖物
        int circleColor = ContextCompat.getColor(MapDistrictActivity.this, R.color.reddeep);
        int circleColorFour = ContextCompat.getColor(MapDistrictActivity.this, R.color.reddeepfour);
        // 绘制一个圆形
        aMap.addCircle(new CircleOptions().center(latLng)
                .radius(radius).strokeColor(circleColor)
                .fillColor(circleColorFour).strokeWidth(2));

        int circleColor2 = ContextCompat.getColor(MapDistrictActivity.this, R.color.coral);
        int circleColorFour2 = ContextCompat.getColor(MapDistrictActivity.this, R.color.coralfour);
        aMap.addCircle(new CircleOptions().center(latLng2)
                .radius(500).strokeColor(circleColor2)
                .fillColor(circleColorFour2).strokeWidth(2));

        aMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(latLng, 17));// 设置指定的可视区域地图
    }

    private void searchCity(String result, final int cityCode, final int strokeColor, final int fillColor) {
        DistrictSearch search = new DistrictSearch(this);
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords(result);//传入关键字
        query.setKeywordsLevel(DistrictSearchQuery.KEYWORDS_CITY);
        query.setShowBoundary(true);//是否返回边界值
        search.setQuery(query);
        search.searchDistrictAnsy();//开始搜索
        search.setOnDistrictSearchListener(new DistrictSearch.OnDistrictSearchListener() {


            @Override
            public void onDistrictSearched(DistrictResult districtResult) {
                //在回调函数中解析districtResult获取行政区划信息
                //在districtResult.getAMapException().getErrorCode()=0时调用districtResult.getDistrict()方法
                //获取查询行政区的结果
                if (districtResult == null || districtResult.getDistrict() == null) {
                    return;
                }
                final DistrictItem item = districtResult.getDistrict().get(0);

                if (item == null) {
                    return;
                }
                LatLonPoint centerLatLng = item.getCenter();
                if (centerLatLng != null) {
                    aMap.moveCamera(

                            CameraUpdateFactory.newLatLngZoom(new LatLng(centerLatLng.getLatitude(), centerLatLng.getLongitude()), 8));
                }


                new Thread() {
                    public void run() {

                        String[] polyStr = item.districtBoundary();
                        if (polyStr == null || polyStr.length == 0) {
                            return;
                        }
                        LogUtil.i("TAG", cityCode + "*************length=" + polyStr.length);
                        for (String str : polyStr) {
                            String[] lat = str.split(";");
                            PolygonOptions polygonOption = new PolygonOptions();
                            boolean isFirst = true;
                            LatLng firstLatLng = null;
                            for (String latstr : lat) {
                                String[] lats = latstr.split(",");
                                if (isFirst) {
                                    isFirst = false;
                                    firstLatLng = new LatLng(Double
                                            .parseDouble(lats[1]), Double
                                            .parseDouble(lats[0]));
                                }
                                polygonOption.add(new LatLng(Double
                                        .parseDouble(lats[1]), Double
                                        .parseDouble(lats[0])));
                            }
                            if (firstLatLng != null) {
                                polygonOption.add(firstLatLng);
                            }

                            polygonOption.strokeWidth(2).strokeColor(strokeColor).fillColor(fillColor);
                            Message message = handler.obtainMessage();
                            message.obj = polygonOption;
                            message.arg1 = cityCode;
                            message.what = cityCode;
                            handler.sendMessage(message);

                        }
                    }
                }.start();
//
            }


        });


    }

    private void searchProvince(String result, final int cityCode, final int strokeColor, final int fillColor) {
        DistrictSearch search = new DistrictSearch(this);
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords(result);//传入关键字
        query.setKeywordsLevel(DistrictSearchQuery.KEYWORDS_PROVINCE);
        query.setShowBoundary(true);//是否返回边界值
        search.setQuery(query);
        search.searchDistrictAnsy();//开始搜索
        search.setOnDistrictSearchListener(new DistrictSearch.OnDistrictSearchListener() {

            @Override
            public void onDistrictSearched(DistrictResult districtResult) {
                //在回调函数中解析districtResult获取行政区划信息
                //在districtResult.getAMapException().getErrorCode()=0时调用districtResult.getDistrict()方法
                //获取查询行政区的结果
                if (districtResult == null || districtResult.getDistrict() == null) {
                    return;
                }
                final DistrictItem item = districtResult.getDistrict().get(0);

                if (item == null) {
                    return;
                }
                LatLonPoint centerLatLng = item.getCenter();
                if (centerLatLng != null) {
                    aMap.moveCamera(

                            CameraUpdateFactory.newLatLngZoom(new LatLng(centerLatLng.getLatitude(), centerLatLng.getLongitude()), 6));
                }


                new Thread() {
                    public void run() {

                        String[] polyStr = item.districtBoundary();
                        if (polyStr == null || polyStr.length == 0) {
                            return;
                        }
                        for (String str : polyStr) {
                            String[] lat = str.split(";");
                            PolygonOptions polygonOption = new PolygonOptions();
                            boolean isFirst = true;
                            LatLng firstLatLng = null;
                            for (String latstr : lat) {
                                String[] lats = latstr.split(",");
                                if (isFirst) {
                                    isFirst = false;
                                    firstLatLng = new LatLng(Double
                                            .parseDouble(lats[1]), Double
                                            .parseDouble(lats[0]));
                                }
                                polygonOption.add(new LatLng(Double
                                        .parseDouble(lats[1]), Double
                                        .parseDouble(lats[0])));
                            }
                            if (firstLatLng != null) {
                                polygonOption.add(firstLatLng);
                            }

                            polygonOption.strokeWidth(2).strokeColor(strokeColor).fillColor(fillColor);
                            Message message = handler.obtainMessage();
                            message.obj = polygonOption;
                            message.arg1 = cityCode;
                            handler.sendMessage(message);

                        }
                    }
                }.start();
//
            }


        });


    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

                PolygonOptions polygonOption = (PolygonOptions) msg.obj;
                Polygon polygon = aMap.addPolygon(polygonOption);
                polygonList.add(polygon);
                LogUtil.i("TAG","polygonList==" + polygonList.size());

        }
    };

    //自定义广播接收器
    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收广播
            if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
                Bundle bundle = intent.getExtras();
                // 根据广播的event来确定是在区域内还是在区域外
                int status = bundle.getInt("event");
                String geoFenceId = bundle.getString("fenceid");
                if (status == 1) {
                    // 进入围栏区域
                    // 可以自定义提醒方式,示例中使用的是文字方式
                    CommonUtils.showToast(MapDistrictActivity.this, "进入围栏区");
//                                        if (cbAlertIn.isChecked()) {
//                                                mHandler.sendEmptyMessage(1);
//                                        }
                } else if (status == 2) {
                    // 离开围栏区域
                    // 可以自定义提醒方式,示例中使用的是文字方式
                    CommonUtils.showToast(MapDistrictActivity.this, "离开围栏区");
//                                        if (cbAlertOut.isChecked()) {
//                                                mHandler.sendEmptyMessage(2);
//                                        }
                }
            }
        }
    };

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
