package com.sxhxjy.roadmonitor.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Process;
import android.util.Log;



import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Extends application to add some methods
 *
 * password: funing4510
 * @author Michael Zhao
 */
public class MyApplication extends Application {
    private static MyApplication app;

    // socket Hostname in socket service

    public static String BASE_IP = "124.163.206.250:8080";
    public static String BASE_URL = "http://"+ BASE_IP + "/ClearPro/web/";
//    public static final String BASE_URL = "http://192.168.1.172:8088/ClearPro/web/";
//    public static final String BASE_URL = "http://192.168.1.123:8080/ClearPro/web/";
//    public static final String BASE_URL = "http://192.168.1.241:8090/ClearPro/web/";
//    public static final String BASE_URL = "http://192.168.1.131:8080/ClearPro/web/";
    private HttpService httpService;

    public static MyApplication getMyApplication() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
//        Fresco.initialize(this);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        httpService = retrofit.create(HttpService.class);

        // start service
        Intent intent = new Intent(this, SocketService.class);
        startService(intent);
    }

    public SharedPreferences getSharedPreference() {
        return MyApplication.getMyApplication().getSharedPreferences("app", Context.MODE_PRIVATE);
    }

    public HttpService getHttpService() {
        return httpService;
    }

    public void exit() {
        // close database
        CacheManager.getInstance().closeDB();
        System.exit(0);
//        Process.killProcess(Process.myPid());
    }
}
