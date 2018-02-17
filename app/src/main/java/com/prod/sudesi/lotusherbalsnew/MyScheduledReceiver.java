package com.prod.sudesi.lotusherbalsnew;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Mahesh on 12/25/2017.
 */

public class MyScheduledReceiver extends BroadcastReceiver {

    SharedPreferences sp;
    SharedPreferences.Editor spe;

    @Override
    public void onReceive(Context context, Intent intent) {

        sp = context.getSharedPreferences("Lotus", Context.MODE_PRIVATE);
        spe = sp.edit();
        spe.clear();

//        Intent background = new Intent(context, BackgroundServiceBOC.class);
//        context.startService(background);

        try {
            spe.clear();
            Intent i = new Intent(context, LoginActivity.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("LogoutFlag", "LOGOUTSERVICE");
            context.startActivity(i);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
