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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.xmliu.itravel.R;
import com.xmliu.itravel.utils.BaseRecyclerViewAdapter;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.DividerItemDecoration;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.RecyclerHolder;
import com.xmliu.itravel.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Date: 2016/2/29 14:00
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class SelectLocationActivity extends ToolbarActivity implements PoiSearch.OnPoiSearchListener, AMapLocationListener {

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //    private MapView mapView;
//    private AMap aMap;
    private double currentLat;
    private double currentLon;

    private int start = 0; // 当前页数
    private int limit = 10; // 为每页显示数据数目
    private int totalCount = Integer.MAX_VALUE;
    private List<PoiItem> mListData = new ArrayList<>();
    private List<PoiItem> mListDataMore = new ArrayList<>();
    private boolean isLoadingMore = false;
    private boolean isFirstLoad = true; // 首次进入要在定位成功以后再去搜索poi
    private String TAG = SelectLocationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate");
        setContentView(R.layout.activity_select_location);

        toolbar.setTitle("所在位置");
        recyclerView = (RecyclerView) findViewById(R.id.select_location_recycleView);

        initRecyclerView();
        initMap();
//        loadList();

    }

    private void initRecyclerView() {
        // RecycleView初始化配置
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(
                SelectLocationActivity.this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalCount = manager.getItemCount();
                    if (lastVisibleItem == (totalCount - 1) && isSlidingToLast) {
                        LogUtil.i(TAG, "loading more");
                        calculate();
                        isLoadingMore = true;
                        mListDataMore.clear();
                        loadList();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    isSlidingToLast = true;
                } else {
                    isSlidingToLast = false;
                }
            }
        });
    }

    private void initMap() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(60 * 1000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    private void loadList() {
        PoiSearch.Query query = new PoiSearch.Query("", "公共设施|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|" +
                "住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|" +
                "金融保险服务|道路附属设施|地名地址信息", "");//交通设施服务（里面包含了停车场）|汽车服务|汽车销售|汽车维修|摩托车服务|公司企业|
// keyWord表示搜索字符串，第二个参数表示POI搜索类型，默认为：生活服务、餐饮服务、商务住宅
//cityCode表示POI搜索区域，（这里可以传空字符串，空字符串代表全国在全国范围内进行搜索）
        query.setPageSize(limit);// 设置每页最多返回多少条poiitem
        query.setPageNum(start);//设置查第一页
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(currentLat,
                currentLon), 1000));//设置周边搜索的中心点以及区域
        poiSearch.setOnPoiSearchListener(this);//设置数据返回的监听器
        poiSearch.searchPOIAsyn();//开始搜索
    }

    private void loadSearchList(String keywords) {
        isLoadingMore = false;
        mListData.clear();
        PoiSearch.Query query = new PoiSearch.Query(keywords, "公共设施|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|" +
                "住宿服务|风景名胜|政府机构及社会团体|科教文化服务|", "");// 金融保险服务|道路附属设施|地名地址信息商务住宅| 交通设施服务（里面包含了停车场）|汽车服务|汽车销售|汽车维修|摩托车服务|公司企业|
// keyWord表示搜索字符串，第二个参数表示POI搜索类型，默认为：生活服务、餐饮服务、商务住宅
//cityCode表示POI搜索区域，（这里可以传空字符串，空字符串代表全国在全国范围内进行搜索）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);//设置查第一页
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(currentLat,
                currentLon), 50000));//设置周边搜索的中心点以及区域
        poiSearch.setOnPoiSearchListener(this);//设置数据返回的监听器
        poiSearch.searchPOIAsyn();//开始搜索
    }

    private void calculate() {
        int current = myAdapter.getItemCount();
        if (current + limit < totalCount) {
            start = current;
        } else {
            start = current;
            limit = totalCount - current;
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        LogUtil.i(TAG, "result:" + poiResult.getPois().size() + ";i=" + i);
        if (i == 0) {
            if (isLoadingMore) {
                mListDataMore.addAll(poiResult.getPois());
                mListData.addAll(mListDataMore);
                myAdapter.notifyDataSetChanged();
            } else {
                PoiItem item = new PoiItem(null, null, null, null);
                item.setAdName("");
                mListData.add(item);

                // 不需要获取当前城市的中心经纬度，到地址详情界面通过搜索接口获取数据
                LatLonPoint latLonPoint = new LatLonPoint(0,0);
                PoiItem item2 = new PoiItem(null, latLonPoint, poiResult.getPois().get(0).getCityName(), null);
                item2.setAdName("");
                mListData.add(item2);

                mListData.addAll(poiResult.getPois());
                myAdapter = new MyAdapter(SelectLocationActivity.this, mListData);
                recyclerView.setAdapter(myAdapter);

                myAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent();
                        if (position == 0) {
                            intent.putExtra("aName", "所在位置");
                            intent.putExtra("lat", 0);
                            intent.putExtra("lon", 0);
                        } else {
                            if(position == 1) {
                                intent.putExtra("aName", mListData.get(position).getTitle());
                            }else{
                                intent.putExtra("aName", mListData.get(position).getCityName() + "·" + mListData.get(position).getTitle());
                            }
                            intent.putExtra("lat", mListData.get(position).getLatLonPoint().getLatitude());
                            intent.putExtra("lon", mListData.get(position).getLatLonPoint().getLongitude());
                        }
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }

        } else {
            CommonUtils.showToast(SelectLocationActivity.this, "查询失败");
        }
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {
        LogUtil.i(TAG, "onPoiItemDetailSearched:" + i);
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        LogUtil.i(TAG, "onLocationChanged");
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                currentLat = amapLocation.getLatitude();//获取纬度
                currentLon = amapLocation.getLongitude();//获取经度
                LogUtil.i(TAG, "getLatitude:" + currentLat);
                LogUtil.i(TAG, "getLongitude:" + currentLon);
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                if (isFirstLoad) {
                    loadList();
                    isFirstLoad = false;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    private class MyAdapter extends BaseRecyclerViewAdapter {

        public static final int TYPE_HEAD = 0;
        public static final int TYPE_NORMAL = 1;


        @Override
        public int getItemViewType(int position) {

            if (position == 0) {
                return TYPE_HEAD;
            }
            return TYPE_NORMAL;
        }

        public MyAdapter(Context context, List<?> list) {
            super(context, list);
        }

        @Override
        public int onCreateViewLayoutID(int viewType) {
            if (viewType == TYPE_HEAD)
                return R.layout.item_select_location_recyclerview_header;

            return R.layout.item_select_location_recyclerview;

        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, int position) {
            if (getItemViewType(position) == TYPE_HEAD) {
                holder.getRelativeLayout(R.id.header_select_location_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.showToast(SelectLocationActivity.this, "选中头");
                    }
                });
            } else {
                final PoiItem item = (PoiItem) list.get(position);
//                LinearLayout linearLayout = holder.getLinearLayout(R.id.recyclerview_item_select_location_layout);
//                linearLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        CommonUtils.showToast(SelectLocationActivity.this, "点击，啦啦啦");
//                    }
//                });
                TextView titleTV = holder.getTextView(R.id.recyclerview_item_title);
                TextView contentTV = holder.getTextView(R.id.recyclerview_item_content);
                if(StringUtils.isBlank(item.getTitle())){
                    titleTV.setVisibility(View.GONE);
                }else{
                    titleTV.setVisibility(View.VISIBLE);
                    titleTV.setText(item.getTitle());
                }
                if(StringUtils.isBlank(item.getSnippet())){
                    contentTV.setVisibility(View.GONE);
                }else{
                    contentTV.setVisibility(View.VISIBLE);
                    contentTV.setText(item.getSnippet());
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_location, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("搜索附近位置");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LogUtil.i(TAG, "onQueryTextSubmit" + query);
//                loadSearchList(newText);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.length() > 0) {
                    LogUtil.i(TAG, "onQueryTextChange" + newText);
//                    currentSearchTip = newText;
                    loadSearchList(newText);
                }
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mListData.clear();
                loadList();
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();
    }
}
