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

import android.os.Bundle;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.xmliu.itravel.R;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.LogUtil;

/**
 * Date: 2016/3/1 9:30
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: 地址详情，类似微信、微博等
 */
public class MapDetailActivity extends ToolbarActivity implements PoiSearch.OnPoiSearchListener,GeocodeSearch.OnGeocodeSearchListener {

    private TextView titleTV;
    private TextView addressTV;
    private TextView labelTV;
    private MapView mapView;
    private AMap aMap;

    private String addressStr;
    private double currLat;
    private double currLon;
    private GeocodeSearch geocoderSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);
        LogUtil.i(TAG, "onCreate");
        toolbar.setTitle("地址详情");
        mapView = (MapView) findViewById(R.id.map_detail_map);
        titleTV = (TextView) findViewById(R.id.map_detail_title);
        addressTV = (TextView) findViewById(R.id.map_detail_address);
        labelTV = (TextView) findViewById(R.id.map_detail_label);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
//            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
            aMap.setMyLocationEnabled(true);
        }

        addressStr = this.getIntent().getStringExtra("address");
        if(addressStr.contains("·")){
            addressStr = addressStr.split("·")[1];
        }
        currLat = this.getIntent().getDoubleExtra("latitude", 0);
        currLon = this.getIntent().getDoubleExtra("longitude", 0);

        LogUtil.i(TAG, "addressStr:" + addressStr);
        if(currLat !=0 && currLon!=0){
            LatLng latLng = new LatLng(currLat, currLon);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            aMap.clear();
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_location_icon));
            aMap.addMarker(markerOptions);
            aMap.animateCamera(cameraUpdate, 1000, new AMap.CancelableCallback() {
                @Override
                public void onFinish() {

                }

                @Override
                public void onCancel() {

                }
            });

            // 反地理编码
            geocoderSearch = new GeocodeSearch(this);
            geocoderSearch.setOnGeocodeSearchListener(this);
//latLonPoint参数表示一个Latlng，第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系
            LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);

        }else {
            searchKeywords(" ");
        }
    }

    private void searchKeywords(String cityName) {
        LogUtil.i(TAG, "searchKeywords");
        PoiSearch.Query query = new PoiSearch.Query(addressStr, "公共设施|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|" +
                "住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|" +
                "金融保险服务|道路附属设施|地名地址信息", cityName);//交通设施服务（里面包含了停车场）|汽车服务|汽车销售|汽车维修|摩托车服务|公司企业|
// keyWord表示搜索字符串，第二个参数表示POI搜索类型，默认为：生活服务、餐饮服务、商务住宅
//cityCode表示POI搜索区域，（这里可以传空字符串，空字符串代表全国在全国范围内进行搜索）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);//设置查第一页
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);//设置数据返回的监听器
        poiSearch.searchPOIAsyn();//开始搜索
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        LogUtil.i(TAG, "onPoiSearched" + i);
        if (i == 0) {
            LogUtil.i(TAG, "result = " + poiResult.getPois().size());
            titleTV.setText(addressStr);
            if (poiResult.getPois().size() == 0 && poiResult.getSearchSuggestionCitys().size() != 0) {
                searchKeywords(poiResult.getSearchSuggestionCitys().get(0).getCityName());
            } else {
                PoiItem poiItem = poiResult.getPois().get(0);
                addressTV.setText(poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getAdName()+poiItem.getSnippet());
                LogUtil.i(TAG, "Snippet = " + poiItem.getSnippet());
                LogUtil.i(TAG, "getLatitude = " + poiItem.getLatLonPoint().getLatitude());
                labelTV.setText("标签：" + poiItem.getTypeDes());
                LatLng latLng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                aMap.clear();
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_location_icon));
                aMap.addMarker(markerOptions);
                aMap.animateCamera(cameraUpdate, 1000, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        } else {
            CommonUtils.showToast(MapDetailActivity.this, "查询失败");
        }
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        //解析result获取逆地理编码结果
        titleTV.setText(addressStr);
        addressTV.setText(regeocodeResult.getRegeocodeAddress().getFormatAddress());
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}

