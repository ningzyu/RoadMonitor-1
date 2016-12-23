package com.sxhxjy.defensemonitor.ui.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sxhxjy.defensemonitor.R;
import com.sxhxjy.defensemonitor.base.HttpService;
import com.sxhxjy.defensemonitor.base.MyApplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LookStateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_state);
    }




    private void getRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://v.juhe.cn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HttpService userBiz = retrofit.create(HttpService.class);
//        Call<Entity_8> call = userBiz.getGong2("query","78f723dccf85aea324a3cf0daac97f35");
//        Call<Entity_8> call = userBiz.getGong1(MyApplication.getMyApplication().getSharedPreference().getString("uid",""));
//        call.enqueue(new Callback<Entity_8>()
//        {
//            @Override
//            public void onResponse(Call<Entity_8> call, retrofit2.Response<Entity_8> response) {
//                Entity_8 e8= response.body();
//                List<Entity_8.ResultBean.ListBean> data=e8.getResult().getList();
//                Adapter08 ada=new Adapter08(Activity08.this,data,R.layout.lv8_item);
//                lv.setAdapter(ada);
//            }
//
//            @Override
//            public void onFailure(Call<Entity_8> call, Throwable t) {
//
//            }
//        });
    }
}
