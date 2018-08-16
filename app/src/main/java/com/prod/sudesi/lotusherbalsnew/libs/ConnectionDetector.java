package com.prod.sudesi.lotusherbalsnew.libs;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConnectionDetector {
 
    private Context _context;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
 
    public ConnectionDetector(Context context){

        this._context = context;
        sp = _context.getSharedPreferences("Lotus", Context.MODE_PRIVATE);
        spe = sp.edit();
    }
 
    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }

    public boolean isCurrentDateMatchDeviceDate(){

        final Calendar calendar1 = Calendar
                .getInstance();
        SimpleDateFormat formatter1 = new SimpleDateFormat(
                "M/d/yyyy");
        String systemdate = formatter1.format(calendar1
                .getTime());

        String serverdate = sp.getString("SERVER_DATE","");
        //  String date = "8/29/2011 11:16:12 AM";
        String[] parts = serverdate.split(" ");
        String serverdd = parts[0];

        if (systemdate != null && serverdd != null
                && systemdate.equalsIgnoreCase(serverdd)) {

                return true;
        }

        return false;
    }

    public String getNonNullValues(String str) {
        if (str == null || str.isEmpty() || str.equalsIgnoreCase("null") || str.equalsIgnoreCase("")) {
            return "";
        } else {
            return str;
        }
    }
}
