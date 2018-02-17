package com.prod.sudesi.lotusherbalsnew;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ${Abhay} on ${5-08-2016}.
 */
public class UploadDataBrodcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

            Intent background = new Intent(context, BackgroundService.class);
            context.startService(background);

    }
}
