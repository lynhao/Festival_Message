package com.demo.linhao.festival_sms;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by linhao on 16/7/14.
 */
public class MsgService extends Service {
    MainActivity m;
    public final IBinder iBinder = new MsgBinder();
    class MsgBinder extends Binder {
        MsgService getMsg(){
            return MsgService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("stop","onCreate+2");

        Notification notification = null;
         Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(this).setWhen(3000);
        notification = builder.setContentIntent(pendingIntent).setContentTitle("节日短息").setContentText("欢迎回来").setSmallIcon(R.mipmap.ic_launcher).build();

        startForeground(1, notification);



    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
