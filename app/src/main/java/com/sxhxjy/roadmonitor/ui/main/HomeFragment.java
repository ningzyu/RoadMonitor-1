package com.sxhxjy.roadmonitor.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.sxhxjy.roadmonitor.R;
import com.sxhxjy.roadmonitor.adapter.HomelistAdapter;
import com.sxhxjy.roadmonitor.base.BaseFragment;
import com.sxhxjy.roadmonitor.base.CacheManager;
import com.sxhxjy.roadmonitor.base.MyApplication;
import com.sxhxjy.roadmonitor.entity.HomeTheme;
import com.sxhxjy.roadmonitor.entity.LoginData;
import com.sxhxjy.roadmonitor.view.HorizontalListView;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 2016/9/26
 *  BPUBZ-HB3RQ-5SB5M-GLB4U-2A4QF-E7FT7
 * @author Michael Zhao
 */

public class HomeFragment extends BaseFragment{
    /**\
     * 首页——fragment页
     */
    private MapView mapview;
    private String path= MyApplication.BASE_URL + "points/findAppRootPoint?groupId=4028812c57b6993b0157b6aca4410004";
    private OkHttpClient okHttpClient;
    private Request request;
    private HorizontalListView lv_home;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, null);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolBar(view, "首页", false);
        init(view);
        getOkHttp();
        mapview.onCreate(savedInstanceState);
        TencentMap tencentMap = mapview.getMap();
        LoginData loginData = new Gson().fromJson(CacheManager.getInstance().get("login"), LoginData.class);
        if (loginData != null) {
            for (LoginData.UserGroupsBean groupsBean : loginData.getUserGroups()) {
                LatLng latLng = new LatLng(groupsBean.getLatitude(), groupsBean.getLongitude());
                tencentMap.setCenter(latLng);
                tencentMap.setZoom(7);
                Marker marker = tencentMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(groupsBean.getName())
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker())
                        .draggable(true));
                marker.showInfoWindow();
            }
        }
    }
    private void getOkHttp() {
        okHttpClient=new OkHttpClient();
        request=new Request.Builder().url(path).build();
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override//请求失败
            public void onFailure(Call call, IOException e) {
//                Toast.makeText(getActivity(),"请求失败",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result=response.body().string();//拿到json数据
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        json(result);
                        Log.i("oooooooooo",result);
                    }
                });
            }
        });
    }
    public void json(String s){
        final HomeTheme theme= JSON.parseObject(s,HomeTheme.class);
        List<HomeTheme.DataBean> list=theme.getData();
            HomelistAdapter adapter=new HomelistAdapter(getActivity(),list,R.layout.home_list_item);
            lv_home.setAdapter(adapter);
            lv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ((MonitorFragment) ((MainActivity)getActivity()).fragments.get(1)).changeMonitor(position);
                    ((MainActivity)getActivity()).selectedBar(1);

                }
            });
    }
    public void init(View view){
        lv_home= (HorizontalListView) view.findViewById(R.id.list_home);
        mapview = (MapView) view.findViewById(R.id.map_view);
    }

    @Override
    public void onDestroy() {
        mapview.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onPause() {
        mapview.onPause();
        super.onPause();
    }
    @Override
    public void onResume() {
        mapview.onResume();
        super.onResume();
    }
    @Override
    public void onStop() {
        mapview.onStop();
        super.onStop();
    }
}
