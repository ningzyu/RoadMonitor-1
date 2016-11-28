package com.sxhxjy.roadmonitor.base;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.TimeUtils;

import com.sxhxjy.roadmonitor.R;
import com.sxhxjy.roadmonitor.ui.main.MainActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * 2016/11/28
 *
 * @author Michael Zhao
 */

public class SocketService extends Service {
    private static final String ADDRESS = "http://";
    private static final int PORT = 8000;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            socket = new Socket(ADDRESS, PORT);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    try {
                        socket.sendUrgentData(7);
                        in.readUTF();






                        Thread.sleep(5000);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendNtf() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("alert", true);
        intent.putExtra("alert_num", 3);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent p = PendingIntent.getService(this, 100,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setContentText("您有新的警告")
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentIntent(p)
                .setSmallIcon(R.mipmap.logo)
                .setAutoCancel(true)
                .setVibrate(new long[] {1500, 1000})
//                .setSound(Uri.parse("file:///android_asset/beep"))
                .build();
        notificationManager.notify(1, notification);
    }
}
