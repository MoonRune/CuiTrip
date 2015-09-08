package com.cuitrip.app.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.UnitUtils;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;

import java.util.HashMap;
import java.util.Map;

import io.rong.message.LocationMessage;

/**
 * Created by baziii on 15/8/18.
 */
public class GaoDeMapActivity extends BaseActivity {
    public static final String VALUE_LAT = "VALUE_LAT";
    public static final String VALUE_LNG = "VALUE_LNG";
    public static final String VALUE_NAME = "VALUE_NAME";
    public static final String IM_RESULT = "IM_RESULT";
    public static final int REQUEST = 13;
    MapView mapView;
    AMap aMap;

    public static void returnForIM(Context activity) {
        activity.startActivity(new Intent(activity, GaoDeMapActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(IM_RESULT, true));

    }

    public static void startSearch(Activity activity) {
        activity.startActivityForResult(new Intent(activity, GaoDeMapActivity.class), REQUEST);
    }

    public static void startShow(Context context, double lat, double lng, String name) {
        context.startActivity(new Intent(context, GaoDeMapActivity.class)
                .putExtra(VALUE_LAT, lat).putExtra(VALUE_LNG, lng).putExtra(VALUE_NAME, name));
    }


    public static Intent getStartShow(Context context, double lat, double lng, String name) {
        return (new Intent(context, GaoDeMapActivity.class)
                .putExtra(VALUE_LAT, lat).putExtra(VALUE_LNG, lng).putExtra(VALUE_NAME, name));
    }

    public static boolean isSelected(int request, int response, Intent date) {
        return request == REQUEST && response == RESULT_OK;
    }

    public static double getLat(Intent intent) {
        return intent.getDoubleExtra(VALUE_LAT, 0);
    }

    public static double getLng(Intent intent) {
        return intent.getDoubleExtra(VALUE_LNG, 0);
    }

    public static String getName(Intent intent) {
        return intent.getStringExtra(VALUE_NAME);
    }

    double lat;
    double lng;
    String name;

    public boolean readIntent() {
        if (getIntent().hasExtra(VALUE_LAT) &&
                getIntent().hasExtra(VALUE_LNG) && getIntent().hasExtra(VALUE_NAME)) {
            lat = getIntent().getDoubleExtra(VALUE_LAT, 0);
            lng = getIntent().getDoubleExtra(VALUE_LNG, 0);
            name = getIntent().getStringExtra(VALUE_NAME);
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gaode_map);
        mapView = (MapView) findViewById(R.id.map);
        LogHelper.e("gaode", "create");
        mapView.onCreate(savedInstanceState);// 必须要写 init();
        init();
        LogHelper.e("gaode", "init");
        if (!readIntent()) {
            LogHelper.e("gaode", "select");
            getSupportActionBar().setCustomView(R.layout.ct_action_search);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().collapseActionView();
            SearchView searchView = (SearchView) getSupportActionBar().getCustomView().findViewById(R.id.search_view);
            searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            searchView.onActionViewCollapsed();
            getSupportActionBar().getCustomView().findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputAndSave();
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                public boolean onQueryTextSubmit(String query) {

                    search(query);
                    return true;
                }

                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
            move(25.044061, 121.510841);
            aMap.setInfoWindowAdapter(infoWindowAdapter);
            aMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
        } else {
            findViewById(R.id.center_point).setVisibility(View.GONE);
            LogHelper.e("gaode", "show");
            mapView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveAndMark(lat, lng, name);
                }
            }, 300);
            LogHelper.e("gaode", "show ok");
        }

        getSupportActionBar().show();
        LogHelper.e("gaode", "done");
    }

    private AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {
            View view = LayoutInflater.from(GaoDeMapActivity.this).inflate(R.layout.map_info_v, null);
            ((TextView) view.findViewById(R.id.title_v)).setText(marker.getSnippet());
            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return getInfoWindow(marker);
        }
    };
    private AMap.OnInfoWindowClickListener onInfoWindowClickListener = new AMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(final Marker marker) {
            MessageUtils.dialogBuilder(GaoDeMapActivity.this, true, "",
                    getString(R.string.set_address, marker.getSnippet()), "", getString(R.string.ct_confirm), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            queryAndSave(marker.getPosition().latitude,
                                    marker.getPosition().longitude,
                                    marker.getSnippet());
                        }
                    }
            );
        }
    };

    public void inputAndSave() {

        final double lat = aMap.getCameraPosition().target.latitude;
        final double lng = aMap.getCameraPosition().target.longitude;
        MessageUtils.dialogBuilderInput(this, true, "", getString(R.string.please_desc_address), getString(R.string.ct_confirm), new MessageUtils.setMessageListener() {
            @Override
            public void setMessage(String s) {
                queryAndSave(lat, lng, s);
            }
        });
    }


    /**
     * 初始化 AMap 对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
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
     * 此方法需要有
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 此方法需要有
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 此方法需要有
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

//销毁页面时候的清空回调和是否回调错误
        if (MainApplication.getCallback() != null) {
            MainApplication.getCallback().onFailure(getString(R.string.data_error));
            MainApplication.setCallback(null);
        }
    }

    public void queryAndSave(final double lat, final double lng, String addressName) {

        LogHelper.e("set map ", "" + lat + "|" + lng + "|" + addressName);
        if (getIntent().getBooleanExtra(IM_RESULT, false)) {
            //聊天界面选择地图
            Map.Entry<Double, Double> pointF = bd_encrypt(lat, lng);
            LogHelper.e("encry", " " + pointF.getKey() + "|" + pointF.getValue());
            String url = "http://restapi.amap.com/v3/staticmap?location=" + pointF.getValue() + "," + pointF.getKey() + "&zoom=16&size=300*300&key=8aa78f0b74184f42e6e620866ec13802";
            LogHelper.e("set map ", "" + url);
            Uri uri = Uri.parse(url);
            LocationMessage mMsg = LocationMessage.obtain(lat, lng,
                    addressName, uri);
            MainApplication.getCallback().onSuccess(mMsg);
            MainApplication.setCallback(null);
            finish();
//                           ?location=116.481485,39.990464&zoom=10&size=750*300&markers=mid,,A:116.481485,39.990464&key=ee95e52bf08006f63fd29bcfbcf21df0
        } else {
            Intent intent = new Intent();
            intent.putExtra(VALUE_LAT, lat);
            intent.putExtra(VALUE_LNG, lng);
            intent.putExtra(VALUE_NAME, addressName);

            setResult(RESULT_OK, intent);
        }
        finish();
    }

    final double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    Map.Entry<Double, Double> bd_encrypt(double gg_lat, double gg_lon) {
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        Double bd_lon = (Double) (z * Math.cos(theta) + 0.0065);
        Double bd_lat = (Double) (z * Math.sin(theta) + 0.006);
        return new HashMap.SimpleEntry<Double, Double>(gg_lat, gg_lon);
    }

    Map.Entry<Double, Double> bd_decrypt(double bd_lat, double bd_lon) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        Double gg_lon = (Double) (z * Math.cos(theta));
        Double gg_lat = (Double) (z * Math.sin(theta));
        return new HashMap.SimpleEntry<Double, Double>(gg_lat, gg_lon);
    }

    public void moveAndMark(double lat, double lng, String title) {
        move(lat, lng);
        mark(lat, lng, title);
    }

    public void mark(double lat, double lng, String title) {
        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(new LatLng(lat, lng
                )).snippet(title));

    }

    public void move(double lat, double lng) {
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 18));
    }

    public void search(String msg) {
        LogHelper.e("search", "asfsf" + msg);
        PoiSearch.Query query = new PoiSearch.Query(msg, "", UnitUtils.getDefaultCity());
        query.setPageSize(20);
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                LogHelper.e("search", "onPoiSearched " + i);
                aMap.getMapScreenMarkers().clear();
                if (poiResult == null || poiResult.getPois() == null || poiResult.getPois().isEmpty()) {
                    toastError();
                    return;
                }
                for (PoiItem item : poiResult.getPois()) {
                    mark(item.getLatLonPoint().getLatitude(),
                            item.getLatLonPoint().getLongitude(),
                            item.getTitle());
                }
                if (poiResult.getPois() != null && !poiResult.getPois().isEmpty()) {
                    move(
                            poiResult.getPois().get(0).getLatLonPoint().getLatitude(),
                            poiResult.getPois().get(0).getLatLonPoint().getLongitude()
                    );
                }
            }

            @Override
            public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {
                LogHelper.e("search", "onPoiItemDetailSearched " + i);
                toastError();
            }
        });
        poiSearch.searchPOIAsyn();
    }

    public void toastError() {
        MessageUtils.showToast(R.string.no_date);
    }
}
