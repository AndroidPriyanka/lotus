package com.prod.sudesi.lotusherbalsnew;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Mahesh on 12/25/2017.
 */

public class BackgroundServiceBOC extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
//    SharedPref pref;
//    private DataHelper dataHelper;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
        Log.v("BackgroundService ==>", " onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            // TODO something here
            try {

                sp = context.getSharedPreferences("Lotus", Context.MODE_PRIVATE);
                spe = sp.edit();
                spe.clear();

                Intent logoutIntent = new Intent(context, LoginActivity.class);
//                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                logoutIntent.putExtra("LogoutFlag", "SERVICE");

                context.startActivity(logoutIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }

            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }
}
