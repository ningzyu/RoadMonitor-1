package com.sxhxjy.defensemonitor.base;


import com.sxhxjy.defensemonitor.entity.AlertData;
import com.sxhxjy.defensemonitor.entity.AlertTree;
import com.sxhxjy.defensemonitor.entity.ComplexData;
import com.sxhxjy.defensemonitor.entity.GroupTree;
import com.sxhxjy.defensemonitor.entity.LoginData;
import com.sxhxjy.defensemonitor.entity.Monitor;
import com.sxhxjy.defensemonitor.entity.MonitorTypeTree;
import com.sxhxjy.defensemonitor.entity.Station;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * http request goes here...
 *
 * BASE_URL:
 *
 * @author Michael Zhao
 *
 */
public interface HttpService {

    /////////////////////////////////////////////////////////////////////////
    ////  general
    /////////////////////////////////////////////////////////////////////////

    /**
     *
     * 首页返回地图上面监测项的接口（get）:
     http://192.168.1.172:8088/ClearPro/web/points/findAppRootPoint?groupId=4028812c57b6993b0157b6aca4410004
     参数：groupId：组织id
     */
// TODO: home
//    @GET("points/findAppRootPoint")
//    Observable<HttpResponse<List<>>> getHomeData(@Query("groupId") String groupId);

//    @FormUrlEncoded
//    @POST("stations/findPointByStationId")
    //设备状态列表
//    @GET("weixin/query")
//    Call<Entity_8> getGong1(@Query("key") String sort);//请求网络需要的参数（id，第几页，多少条数据）



@GET("stations/findPointByStationId")
    Observable<HttpResponse<List<Monitor>>> getMonitors(@Query("stationId") String stationId);

    @GET("stations/findStationByGroupId")
//    @FormUrlEncoded
    Observable<HttpResponse<List<Station>>> getStations(@Query("groupId") String groupId);


    @GET("userGroup/userGroupTreeList")
    Observable<HttpResponse<List<GroupTree>>> getGroups(@Query("gid") String gid);

    @GET("user/appLogin")
    Observable<HttpResponse<LoginData>> login(@Query("account") String username, @Query("password") String password);

    @GET("sensorData/dataList")
    Observable<HttpResponse<ComplexData>> getRealTimeData(@Query("code") String positionID, @Query("beforeTime") long start, @Query("afterTime") long end, @Query("timeState") int timeState);


    @GET("points/pointDetail")
    Observable<HttpResponse<Monitor>> getMonitor(@Query("id") String id);

    @GET("points/pointTreeList")
    Observable<HttpResponse<List<MonitorTypeTree>>> getMonitorTypeTree(@Query("groupId") String groupId);

    @GET("alarm/getAlermFilter")
    Observable<HttpResponse<AlertTree>> getAlertTree();

    @GET("stations/findStationByFilters")
    Observable<HttpResponse<List<MonitorPosition>>> getPositions(@Query("pointId") String pointId, @Query("gid") String gid);

    @GET("stations/stationDetail")
    Observable<HttpResponse<ParamInfo>> getParamInfo(@Query("id") String id);

    //告警的id : alarmId ；确认人id（登陆用户的id） : userId；确认信息 : confirmMsg；告警产生时间：stime；告警的最后时间：etime；等级id：levelId；类型id：typeId；可能原因：generationReason；告警内容：alarmContent（新增参数）


    @FormUrlEncoded
    @POST("alarm/confirmAlarmInfo")
    Observable<HttpResponse<Object>> confirmAlertMsg(@Field("alarmId") String alarmId, @Field("userId") String userId, @Field("confirmMsg") String confirmMsg,
                                                     @Field("stime") String startTime, @Field("etime") String endTime, @Field("levelId") String levelId, @Field("typeId") String typeId, @Field("generationReason") String generareason, @Field("alarmContent") String alarmCon);

    @FormUrlEncoded
    @POST("user/editPwd")
    Observable<HttpResponse<Object>> changePassword(@Field("userId") String uid, @Field("oldPwd") String old, @Field("newPwd") String newP);

    @Multipart

    @POST("jk/uploadPic.htm")
    Call<HttpResponse<String>> uploadImage(@Part MultipartBody.Part file);



    //监测点id：stationId；告警列表的产生时间：stime；告警列表的最后时间：etime；等级id：levelId；类型id：typeId；可能原因：generationReason；告警内容：alarmContent；确认人id：userId；确认信息：confirmMsg；确认时间：confirmTime（新增参数）

    @GET("alarm/findByStationId")
    Observable<HttpResponse<List<AlertData>>> getAlertDataDetail(@Query("stationId") String alarmId, @Query("userId") String userId, @Query("confirmMsg") String confirmMsg,
                                                                 @Query("stime") String startTime, @Query("etime") String endTime, @Query("levelId") String levelId, @Query("typeId") String typeId, @Query("generationReason") String generareason, @Query("alarmContent") String alarmCon, @Query("confirmTime") long confirmTime, @Query("confirmInfo") String confirmInfo);
    @GET("alarmUnionData/pageList")
    Observable<HttpResponse<List<AlertData>>> getAlertDataList(@Query("stationId") String stationId, @Query("orgId") String gid, @Query("level") String level, @Query("cStype") String type, @Query("beforeTime") long beforeTime, @Query("afterTime") long afterTime, @Query("timeState") String timeState, @Query("state") String stateCode);

    /////////////////////////////////////////////////////////////////////////
    ////  home
    /////////////////////////////////////////////////////////////////////////
}
