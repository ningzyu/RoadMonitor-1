package com.sxhxjy.roadmonitor.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sxhxjy.roadmonitor.R;
import com.sxhxjy.roadmonitor.base.BaseFragment;
import com.sxhxjy.roadmonitor.base.CacheManager;
import com.sxhxjy.roadmonitor.entity.LoginData;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

/**
 * 2016/9/26
 *  BPUBZ-HB3RQ-5SB5M-GLB4U-2A4QF-E7FT7
 * @author Michael Zhao
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener{
    /**\
     * 首页——fragment页
     */
    private MapView mapview;
    private TextView tv0,tv1,tv2,tv3;
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
    public void init(View view){
        tv0= (TextView) view.findViewById(R.id.tv0_home);
        tv1= (TextView) view.findViewById(R.id.tv1_home);
        tv2= (TextView) view.findViewById(R.id.tv2_home);
        tv3= (TextView) view.findViewById(R.id.tv3_home);
        tv0.setOnClickListener(this);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv0_home:
                Toast.makeText(getActivity(),"0000000000",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv1_home:
                Toast.makeText(getActivity(),"11111111111",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv2_home:
                Toast.makeText(getActivity(),"222222222",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv3_home:
                Toast.makeText(getActivity(),"3333333333",Toast.LENGTH_SHORT).show();

                break;

        }
    }
}
