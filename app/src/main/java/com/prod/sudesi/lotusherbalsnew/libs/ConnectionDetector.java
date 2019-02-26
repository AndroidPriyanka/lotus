package com.prod.sudesi.lotusherbalsnew.libs;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.prod.sudesi.lotusherbalsnew.FocusReportActivity;
import com.prod.sudesi.lotusherbalsnew.SyncMaster;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;

import org.ksoap2.serialization.SoapObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ConnectionDetector {

    private Context _context;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    private ProgressDialog mProgress = null;
    private Dbcon db;

    public ConnectionDetector(Context context) {

        this._context = context;
        db = new Dbcon(_context);
        sp = _context.getSharedPreferences("Lotus", Context.MODE_PRIVATE);
        spe = sp.edit();
    }

    /**
     * Checking for all possible internet providers
     **/
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public boolean isCurrentDateMatchDeviceDate() {

        final Calendar calendar1 = Calendar
                .getInstance();
        SimpleDateFormat formatter1 = new SimpleDateFormat(
                "M/d/yyyy");
        String systemdate = formatter1.format(calendar1
                .getTime());

        String serverdate = sp.getString("SERVER_DATE", "");
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
        if (str == null || str.isEmpty() || str.equalsIgnoreCase("null") || str.equalsIgnoreCase("") ||
                str.equalsIgnoreCase("anyType{}")) {
            return "";
        } else {
            return str;
        }
    }

    public String getNonNullValues_Integer(String str) {
        if (str == null || str.isEmpty() || str.equalsIgnoreCase("null") || str.equalsIgnoreCase("") ||
                str.equalsIgnoreCase("anyType{}")) {
            return "0";
        } else {
            return str;
        }
    }

    public void showProgressDialog(String msg) {
        if (mProgress == null) {

            mProgress = new ProgressDialog(_context);
            mProgress.setMessage(msg);
            mProgress.setIndeterminate(false);
            mProgress.setCancelable(false);
        }
        mProgress.show();
    }

    public void dismissProgressDialog() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    /*public String getBocName() {
        String bocname = "";
        String BOC = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            String oeStartDateStr = "26/";
            String oeEndDateStr = "25/";

            Calendar cal = Calendar.getInstance();
            Integer year = cal.get(Calendar.YEAR);
            Integer month1 = cal.get(Calendar.MONTH) + 1;
            Integer pmonth = cal.get(Calendar.MONTH);

            String stdate ="26/" +  month1.toString() + "/" + year.toString();
            String eddate ="31/" +  month1.toString() + "/" + year.toString();

            Date sDate = sdf.parse(stdate);
            Date eDate = sdf.parse(eddate);
            Date dat = new Date();

            if((dat.after(sDate) && (dat.before(eDate)))){
                pmonth = pmonth + 1;
            }

            oeStartDateStr = oeStartDateStr.concat(pmonth.toString()) + "/";
            Integer nextmonth;
            if(pmonth.toString().equalsIgnoreCase("12")){
                nextmonth = 1;
            }else {
                nextmonth = pmonth + 1;
            }
            oeEndDateStr = oeEndDateStr.concat(nextmonth.toString()) + "/";

            oeStartDateStr = oeStartDateStr.concat(year.toString());
            oeEndDateStr = oeEndDateStr.concat(year.toString());

            Date startDate = sdf.parse(oeStartDateStr);
            Date endDate = sdf.parse(oeEndDateStr);
            Date d = new Date();
            String currDt = sdf.format(d);


            if ((d.after(startDate) && (d.before(endDate))) || (currDt.equals(sdf.format(startDate)) || currDt.equals(sdf.format(endDate)))) {
                if(String.valueOf(month1).equalsIgnoreCase("1")){
                    bocname = "BOC11";
                }else if(String.valueOf(month1).equalsIgnoreCase("2")){
                    bocname = "BOC12";
                }else {
                    bocname = "BOC" + String.valueOf(pmonth - 2);
                }
            } else {
                //System.out.println("Date is not between 1st april to 14th nov...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return bocname;
    }*/

    public String getBocName() {
        String bocname = "";
        String BOC = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            String oeStartDateStr = "26/";
            String oeEndDateStr = "25/";

            Calendar cal = Calendar.getInstance();
            Integer year = cal.get(Calendar.YEAR);
            Integer month1 = cal.get(Calendar.MONTH) + 1;
            Integer pmonth = cal.get(Calendar.MONTH);

            String stdate = "26/" + month1.toString() + "/" + year.toString();
            String eddate = "31/" + month1.toString() + "/" + year.toString();

            Date sDate = sdf.parse(stdate);
            Date eDate = sdf.parse(eddate);
            Date dat = new Date();

            if ((dat.after(sDate) && (dat.before(eDate)))) {
                pmonth = pmonth + 1;
            }

            if (pmonth == 0) {
                pmonth = pmonth + 12;
            }

            oeStartDateStr = oeStartDateStr.concat(pmonth.toString()) + "/";
            Integer nextmonth;
            if (pmonth.toString().equalsIgnoreCase("12")) {
                nextmonth = 1;
            } else {
                nextmonth = pmonth + 1;
            }
            oeEndDateStr = oeEndDateStr.concat(nextmonth.toString()) + "/";

            if ((dat.after(sDate) && (dat.before(eDate)))) {
                if (pmonth == 12) {
                    oeStartDateStr = oeStartDateStr.concat(year.toString());
                    oeEndDateStr = oeEndDateStr.concat(String.valueOf(year + 1));
                } else {
                    oeStartDateStr = oeStartDateStr.concat(year.toString());
                    oeEndDateStr = oeEndDateStr.concat(year.toString());
                }
            } else if (month1 == 1) {
                oeStartDateStr = oeStartDateStr.concat(String.valueOf(year - 1));
                oeEndDateStr = oeEndDateStr.concat(year.toString());
            } else {
                oeStartDateStr = oeStartDateStr.concat(year.toString());
                oeEndDateStr = oeEndDateStr.concat(year.toString());
            }


            Date startDate = sdf.parse(oeStartDateStr);
            Date endDate = sdf.parse(oeEndDateStr);
            Date d = new Date();
            String currDt = sdf.format(d);


            if ((d.after(startDate) && (d.before(endDate))) || (currDt.equals(sdf.format(startDate)) || currDt.equals(sdf.format(endDate)))) {
                if (String.valueOf(month1).equalsIgnoreCase("1")) {
                    if ((dat.after(sDate) && (dat.before(eDate)))) {
                        bocname = "BOC11";
                    } else {
                        bocname = "BOC10";
                    }
                } else if (String.valueOf(month1).equalsIgnoreCase("2")) {
                    if ((dat.after(sDate) && (dat.before(eDate)))) {
                        bocname = "BOC12";
                    } else {
                        bocname = "BOC11";
                    }

                } else {
                    if (pmonth == 2) {
                        if ((dat.after(sDate) && (dat.before(eDate)))) {
                            bocname = "BOC" + String.valueOf(pmonth - 2);
                        } else {
                            bocname = "BOC12";
                        }
                    } else {
                        bocname = "BOC" + String.valueOf(pmonth - 2);
                    }
                }
            } else {
                //System.out.println("Date is not between 1st april to 14th nov...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return bocname;
    }

    public void ClearLocalAppData() {
        try {
            db.open();

            db.deleteTables("SYNC_LOG");
            db.deleteTables("boc_wise_stock");
            db.deleteTables("dashboard_details");
            db.deleteTables("image");
            db.deleteTables("scan");
            db.deleteTables("stock");
            db.deleteTables("stock_monthwise");
            db.deleteTables("supervisor_attendance");
            db.deleteTables("tester");
            db.deleteTables("focus_data");
            db.deleteTables("login");
            db.deleteTables("division_master");

            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
