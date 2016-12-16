package com.sxhxjy.defensemonitor.updateInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sxhxjy.defensemonitor.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Version Update
 *
 * @author Michael Zhao
 */

public class UpdateUtil {
    public static void update(final Context context, String url) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        final Request req = new Request.Builder()
                .post(new FormBody.Builder().build())
                .url(url)
                .build();

        okHttpClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("updateUtil", call.request().toString() + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) return;
                final String result = response.body().string();
                Log.i("aaaaaaaaaaaaaaaaaa",result);
                final JSONObject jsonObject = JSON.parseObject(result);
                final String version = jsonObject.getString("version");

                if (!version.equals(getVersion(context))) {
                    // update is available ! to ask user

                    // TODO: !!!!!!
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(context)
                                    .setMessage("检测到新的版本")
                                    .setNegativeButton("以后再说", null)
                                    .setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, "正在下载...", Toast.LENGTH_LONG).show();
                                            final String downloadUrl = jsonObject.getString("newVersionPath");
//                                            final String downloadUrl = "http://124.163.206.251:7066/CivilAirDE.apk";
                                            Request reqDownload = new Request.Builder()
                                                    .post(new FormBody.Builder().build())
                                                    .url(downloadUrl)
                                                    .build();

                                            okHttpClient.newCall(reqDownload).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    Log.e("updateUtil", call.request().toString() + e);
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    if (response.code() != 200) return;
                                                    String storagePath = context.getExternalCacheDir() + "/" + context.getPackageName() + ".apk";
                                                    FileOutputStream out = new FileOutputStream(storagePath);
                                                    InputStream in = response.body().byteStream();
                                                    long contentLength = response.body().contentLength();

                                                    byte[] buf = new byte[1024 * 5];
                                                    int len;
                                                    long downloaded = 0;
                                                    int i = 0;
                                                    while ((len = in.read(buf)) != -1) {
                                                        out.write(buf, 0, len);
                                                        downloaded += len;
                                                        if (i++ % 10 == 0) // reduce ...
                                                            refreshProgress(context, (double)downloaded / contentLength);
                                                    }
                                                    out.flush();
                                                    refreshProgress(context, 1d);

                                                    install(context, new File(storagePath));
                                                }
                                            });
                                        }
                                    }).show();
                        }
                    });
                }
            }
        });
    }

    private static void refreshProgress(Context context, double percentage) {
        int p = (int) (percentage * 100);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentInfo(p == 100 ? "下载完成" : "" + p + "%")
                .setSmallIcon(context.getApplicationInfo().icon)
                .setProgress(100, p, false)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(103, notification);
    }

    private static String getVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }

    private static void install(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//如果不加，最后安装完成，点打开，无法打开新版本应用。
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());//如果不加，最后不会提示完成、打开。
    }

    private UpdateUtil() {}
}
