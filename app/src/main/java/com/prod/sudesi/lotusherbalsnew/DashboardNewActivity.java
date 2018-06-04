package com.prod.sudesi.lotusherbalsnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.AlarmManagerBroadcastReceiver;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DashboardNewActivity extends Activity {

    private Context mContext;

    // today
    private Dbcon db = null;
    String[] values;
    String username, bdename;

    ConnectionDetector cd;
    LotusWebservice service;

    SharedPreferences sp;
    SharedPreferences.Editor spe;
    private double lon = 0.0, lat = 0.0;
    String attendanceDate = "", attendmonth;
    String yesterdaydate1 = "";

    Button btn_attendance, btn_stock, btn_tester, btn_visibility,
            btn_notification, btn_reports, btn_datasync, btn_BAreport,
            btn_BAMonthreport, btn_sale, btn_dashboard, btn_super_atten,btn_stock_sale,btn_outletwise;

    TextView tv_h_username;
    Button btn_home, btn_logout;

    String from26;
    Boolean boc26 = null;
    Boolean boolRecd = null;
    public static ProgressDialog mProgress;
    public static AlertDialog.Builder alertDialogBuilder = null;
    public static AlertDialog.Builder alertDialogBuilder1 = null;


    private AlarmManagerBroadcastReceiver alarm;

    @SuppressLint({"InflateParams", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_dashboard);

        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        mContext = getApplicationContext();
        db = new Dbcon(mContext);

        cd = new ConnectionDetector(mContext);
        mProgress = new ProgressDialog(DashboardNewActivity.this);
        service = new LotusWebservice(DashboardNewActivity.this);

        sp = mContext.getSharedPreferences("Lotus", Context.MODE_PRIVATE);
        spe = sp.edit();

        //enableBroadcastReceiver();

        // TODO: 2/20/2018  set Bocflag false for after boc dialog on 27 date
        Calendar calendar = Calendar.getInstance();
        Calendar setcalendar = Calendar.getInstance();
        setcalendar.setTimeInMillis(System.currentTimeMillis());
        setcalendar.set(Calendar.HOUR_OF_DAY, 0);
        setcalendar.set(Calendar.MINUTE, 0);
        setcalendar.set(Calendar.SECOND, 0);
        setcalendar.set(Calendar.DAY_OF_MONTH, 27);

        if (calendar.get(Calendar.DAY_OF_MONTH) == 27) {
            boolean bocflag = false;
            spe.putBoolean("Bocflag", bocflag);
            spe.commit();
        }

        //TODO: 2/20/2018  set Bocflag false for before boc dialog on 25 date
        Calendar calendar1 = Calendar.getInstance();
        Calendar setcalendar1 = Calendar.getInstance();
        setcalendar1.setTimeInMillis(System.currentTimeMillis());
        setcalendar1.set(Calendar.HOUR_OF_DAY, 0);
        setcalendar1.set(Calendar.MINUTE, 0);
        setcalendar1.set(Calendar.SECOND, 0);
        setcalendar1.set(Calendar.DAY_OF_MONTH, 25);

        if (calendar1.get(Calendar.DAY_OF_MONTH) == 25) {
            boolean bocflag = false;
            spe.putBoolean("Bocflag", bocflag);
            spe.commit();
        }

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("FROM") != null) {
                from26 = intent.getStringExtra("FROM");
                if (from26.equals("RECEIVER")) {
                    boc26 = sp.getBoolean("BOC26", false);

                    if (boc26) {

                        BocOpeningDialog();
                    }

                }
            }

        }

        btn_attendance = (Button) findViewById(R.id.btn_atten);
        btn_visibility = (Button) findViewById(R.id.btn_visibility);
        btn_stock = (Button) findViewById(R.id.btn_stock);
        btn_tester = (Button) findViewById(R.id.btn_tester);
        btn_reports = (Button) findViewById(R.id.btn_report);
        btn_notification = (Button) findViewById(R.id.btn_notifi);
        btn_datasync = (Button) findViewById(R.id.btn_master_sync);
        btn_BAreport = (Button) findViewById(R.id.btn_ba_sale_yr);
        btn_sale = (Button) findViewById(R.id.btn_sale);
        btn_super_atten = (Button) findViewById(R.id.btn_super_atten);

        btn_outletwise = (Button) findViewById(R.id.btn_outletwise);
        btn_stock_sale = (Button) findViewById(R.id.btn_stock_sale);

        btn_BAMonthreport = (Button) findViewById(R.id.btn_ba_sale_month_wise);

        btn_dashboard = (Button) findViewById(R.id.btn_dashboard);

        alarm = new AlarmManagerBroadcastReceiver();

        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        btn_home.setVisibility(View.INVISIBLE);
        Log.e("db.checkStockUploaded()", String.valueOf(db.checkStockUploaded()));

        if(sp.getString("Role","").equalsIgnoreCase("DUB")){
            btn_stock.setVisibility(View.GONE);
            btn_sale.setVisibility(View.GONE);
            btn_outletwise.setVisibility(View.VISIBLE);
            btn_stock_sale.setVisibility(View.VISIBLE);
        }


        if (db.checkStockUploaded()) {

            alertDialogBuilder1 = new AlertDialog.Builder(DashboardNewActivity.this);
            // set title
            alertDialogBuilder1.setTitle("SYNC DATA ALERT");
            // set dialog message
            alertDialogBuilder1
                    .setMessage(
                            "Kindly do a Data Upload")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder1.create();
            // show it
            alertDialog.show();


            btn_visibility.setClickable(false);
            btn_stock.setClickable(false);
            btn_sale.setClickable(false);

            btn_visibility.setEnabled(false);
            btn_stock.setEnabled(false);
            btn_sale.setEnabled(false);


        }

        btn_logout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        btn_tester.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                /*startActivity(new Intent(getApplicationContext(),
                        TesterSubmitActivity.class));*/
                Toast.makeText(mContext, "Coming Soon...!", Toast.LENGTH_LONG).show();
            }
        });

        btn_stock.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (sp.getString("Role", "").equalsIgnoreCase("FLR")) {
                    Toast.makeText(mContext, "This page not use for Floter", Toast.LENGTH_LONG).show();
                } else {
                    Calendar calendar = Calendar.getInstance();
                    Calendar setcalendar = Calendar.getInstance();
                    setcalendar.setTimeInMillis(System.currentTimeMillis());
                    setcalendar.set(Calendar.HOUR_OF_DAY, 7);
                    setcalendar.set(Calendar.MINUTE, 0);
                    setcalendar.set(Calendar.SECOND, 0);
                    setcalendar.set(Calendar.DAY_OF_MONTH, 26);

                    Date bocdate = setcalendar.getTime();
                    Date currentdate = calendar.getTime();


                    if (cd.isCurrentDateMatchDeviceDate()) {
                        if (calendar.get(Calendar.DAY_OF_MONTH) == 26) {
                            if (currentdate.after(bocdate)) {
                                if (sp.getBoolean("DialogDismiss", false) == false) {

                                    Toast.makeText(DashboardNewActivity.this, "Please Data Download First", Toast.LENGTH_LONG).show();

                                } else {
                                    startActivity(new Intent(getApplicationContext(),
                                            StockNewActivity.class));
                                }
                            } else {
                                startActivity(new Intent(getApplicationContext(),
                                        StockNewActivity.class));
                            }

                        } else {
                            startActivity(new Intent(getApplicationContext(),
                                    StockNewActivity.class));
                        }
                    } else {
                        Toast.makeText(DashboardNewActivity.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

                    }

                }
            }
        });

        btn_reports.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                startActivity(new Intent(getApplicationContext(),
                        ReportsForUser.class));

            }
        });
        btn_notification.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                startActivity(new Intent(getApplicationContext(),
                        NotificationFragment.class));

            }
        });

        btn_visibility.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

               /* startActivity(new Intent(getApplicationContext(),
                        VisibilityFragment.class));*/
                Toast.makeText(mContext, "Coming Soon...!", Toast.LENGTH_LONG).show();

            }
        });

        btn_datasync.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (cd.isCurrentDateMatchDeviceDate()) {

                    startActivity(new Intent(getApplicationContext(),
                            SyncMaster.class));
                } else {
                    Toast.makeText(DashboardNewActivity.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

                }


            }
        });
        btn_attendance.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent i = new Intent(getApplicationContext(),
                        AttendanceFragment.class);
                i.putExtra("FromLoginpage", "");
                startActivity(i);

            }
        });

        btn_BAreport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (sp.getString("Role", "").equalsIgnoreCase("FLR")) {
                    Toast.makeText(mContext, "This page not use for Floter", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(getApplicationContext(),
                            BAYearWiseReport.class);
                    startActivity(i);
                }

            }
        });

        btn_BAMonthreport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (sp.getString("Role", "").equalsIgnoreCase("FLR")) {
                    Toast.makeText(mContext, "This page not use for Floter", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(getApplicationContext(),
                            BAMonthWiseReport.class);
                    startActivity(i);
                }

            }
        });

        btn_sale.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar calendar = Calendar.getInstance();
                Calendar setcalendar = Calendar.getInstance();
                setcalendar.setTimeInMillis(System.currentTimeMillis());
                setcalendar.set(Calendar.HOUR_OF_DAY, 7);
                setcalendar.set(Calendar.MINUTE, 0);
                setcalendar.set(Calendar.SECOND, 0);
                setcalendar.set(Calendar.DAY_OF_MONTH, 26);

                Date bocdate = setcalendar.getTime();
                Date currentdate = calendar.getTime();


                if (cd.isCurrentDateMatchDeviceDate()) {
                    if (sp.getString("Role", "").equalsIgnoreCase("FLR")) {

                        startActivity(new Intent(getApplicationContext(),
                                SaleActivityForFloter.class));
                    } else {
                        if (calendar.get(Calendar.DAY_OF_MONTH) == 26) {
                            if (currentdate.after(bocdate)) {
                                if (sp.getBoolean("DialogDismiss", false) == false) {

                                    Toast.makeText(DashboardNewActivity.this, "Please Data Download First", Toast.LENGTH_LONG).show();

                                } else {
                                    startActivity(new Intent(getApplicationContext(),
                                            SaleNewActivity.class));
                                }
                            } else {
                                startActivity(new Intent(getApplicationContext(),
                                        SaleNewActivity.class));
                            }

                        } else {
                            startActivity(new Intent(getApplicationContext(),
                                    SaleNewActivity.class));
                        }
                    }
                } else {
                    Toast.makeText(DashboardNewActivity.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

                }

            }
        });

        btn_dashboard.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(getApplicationContext(),
                        BocDashBoardActivity.class));

            }
        });

        btn_super_atten.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                /*startActivity(new Intent(getApplicationContext(),
                        SupervisorAttendance.class));*/
                Toast.makeText(mContext,
                        "Coming Soon...!",
                        Toast.LENGTH_LONG).show();


            }
        });

        btn_outletwise.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(getApplicationContext(),
                        OutletWiseSaleActivity.class));

            }
        });

        btn_stock_sale.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(getApplicationContext(),
                        StockSaleActivityForDubai.class));

            }
        });


        try {
            username = sp.getString("username", "");
            bdename = sp.getString("BDEusername", "");
            tv_h_username.setText(bdename);
            yesterdaydate1 = getYesterdayDateString();

            String getmonth1 = getmonthforinsert(yesterdaydate1);

            Log.v("", "getmonth" + getmonth1);

            Cursor c = null;
            db.open();
            c = db.getpreviousData(yesterdaydate1, username);

            // (int i = 0;i<c.getCount();i++){

            Log.v("", "c.getcount=" + c.getCount());

            if (c != null && c.getCount() > 0) {
                db.close();

            } else {
                db.close();
                // insertappsent(yesterdaydate1,getmonth1);

            }

            // }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public static boolean beforedatevalidate(String selecteddate,
                                             String currentdate) {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date selectdate = sdf.parse(selecteddate);
            Date curntdate = sdf.parse(currentdate);

            Log.e("", "selecteddate=" + selecteddate);
            Log.e("", "currentdate=" + currentdate);
            // Log.v("befordatevalidation",""+selecteddate.compareTo(currentdate));
            // Log.v("befordatevalidation",""+selectdate.before(curntdate));
            if (selectdate.before(curntdate)) {

                Log.v("befordatevalidation", "" + result);
                result = true;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }

    @SuppressLint("SimpleDateFormat")
    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        return dateFormat.format(cal.getTime());
    }

    private void insertappsent(String yesterdaydate, String month) {

        try {
            final String[] columns = new String[]{"emp_id", "Adate",
                    "attendance", "lat", "lon", "savedServer", "month",
                    "holiday_desc"};
            db.open();

            values = new String[]{username, yesterdaydate, "A",
                    String.valueOf(lat), String.valueOf(lon), "0", month, ""};
            Log.v("", "values for insert==" + values);
            db.insert(values, columns, "attendance");

            db.close();
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

    }

    public String getmonthforinsert(String yesterd) {

        Log.v("", "date-ingetmonthfuction=" + yesterd);
        if (yesterd != null) {
            if (yesterd.contains("-")) {
                String d[] = yesterd.split("-");

                // attendanceDate= d[0]+"-"+getmonthNo(d[1])+"-"+d[2];
                Log.v("", "ingetmonthfuction =" + d[1].toString());
                attendmonth = getmonthNo(d[1]);

            }
        }

        return attendmonth;
    }


    public String getmonthNo(String monthName) {
        String month = "";

        if (monthName.equals("01")) {
            month = "1";
        } else if (monthName.equals("02")) {
            month = "2";
        } else if (monthName.equals("03")) {
            month = "3";
        } else if (monthName.equals("04")) {
            month = "4";
        } else if (monthName.equals("05")) {
            month = "5";
        } else if (monthName.equals("06")) {
            month = "6";
        } else if (monthName.equals("07")) {
            month = "7";
        } else if (monthName.equals("08")) {
            month = "8";
        } else if (monthName.equals("09")) {
            month = "9";
        } else if (monthName.equals("10")) {
            month = "10";
        } else if (monthName.equals("11")) {
            month = "11";
        } else if (monthName.equals("12")) {
            month = "12";
        }

        return month;
    }

    public void startRepeatingTimer() {
        Context context = this.getApplicationContext();

        if (alarm != null) {
            alarm.SetAlarm(context);
        } else {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }

    }

    public class InsertFirstTimeMaster extends AsyncTask<Void, Void, SoapObject> {

        private SoapObject soap_result = null;

        SoapObject soap_result1 = null;
        String Flag;

        SoapPrimitive soap_update_stock_row = null;

        // ---------
        SoapPrimitive soap_update_tester_row = null;
        private SoapObject soap_result_tester = null;
        SoapObject soap_result_tester1 = null;
        // --------

        // ------------------
        SoapPrimitive soap_update_boc_day_row = null;
        private SoapObject soap_result_boc_day = null;
        SoapObject soap_result_boc_day1 = null;
        // ------------------

        // ------------------
        SoapPrimitive soap_update_monthwise_row = null;
        private SoapObject soap_result_monthwise = null;
        SoapObject soap_result_monthwise1 = null;
        // ------------------

        int syncstockdata = 1, synctesterdata = 1, syncbocdaywise = 1,
                syncbocmonthwise = 1;

        String db_stock_id_array = "", db_tester_id_array = "",
                db_bocmonthstock_id_array = "", db_cumvalueid_array = "";

        String EmpId = sp.getString("username", "");
        String Erro_function = "";
        String ErroFlag = "";

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mProgress.setMessage("Receiving.....");
            mProgress.setCancelable(false);
            mProgress.show();

        }

        @Override
        protected SoapObject doInBackground(Void... params) {

            try {

                if (!cd.isConnectingToInternet()) {

                    Flag = "0";
                    // stop executing code by return

                } else {

                    // Flag = "1";

                    // ---------------------------

                    // --------------------------------upload
                    // stock-----------------------------------01.03.2016

           /*         try {
                        Log.e("", "saveto server1-stcok");
                        db.open();
                        stock_array = db.getStockdetails();
                        // db.close();
                        // ------------------

                        if (stock_array.getCount() > 0) {

                            if (stock_array != null
                                    && stock_array.moveToFirst()) {
                                stock_array.moveToFirst();

                                String shad;
                                do {

                                    Log.v("",
                                            "dbid=" + stock_array.getString(2));
                                    Log.v("",
                                            "category_id="
                                                    + stock_array.getString(1));
                                    Log.v("",
                                            "enacode="
                                                    + stock_array.getString(3));
                                    Log.v("",
                                            "empid=" + stock_array.getString(9));
                                    Log.v("",
                                            "product_category="
                                                    + stock_array.getString(4));
                                    Log.v("",
                                            "product_type="
                                                    + stock_array.getString(5));
                                    Log.v("",
                                            "product_name="
                                                    + stock_array.getString(6));

                                    Log.v("",
                                            "opening_stock="
                                                    + stock_array.getString(10));
                                    Log.v("",
                                            "stock_receive="
                                                    + stock_array.getString(11));
                                    Log.v("",
                                            "stock_inhand="
                                                    + stock_array.getString(12));
                                    Log.v("",
                                            "sold=" + stock_array.getString(16));
                                    Log.v("",
                                            "return_s="
                                                    + stock_array.getString(14));
                                    Log.v("",
                                            "return_ns="
                                                    + stock_array.getString(15));
                                    Log.v("",
                                            "close_bal="
                                                    + stock_array.getString(13));
                                    Log.v("",
                                            "t_gross="
                                                    + stock_array.getString(17));
                                    Log.v("",
                                            "discount="
                                                    + stock_array.getString(19));
                                    Log.v("",
                                            "net_amount="
                                                    + stock_array.getString(18));
                                    Log.v("",
                                            "size=" + stock_array.getString(7));
                                    Log.v("",
                                            "price=" + stock_array.getString(8));
                                    Log.v("",
                                            "insert_date"
                                                    + stock_array.getString(21));

                                    if (stock_array.getString(23) != null
                                            || !stock_array.getString(23)
                                            .equalsIgnoreCase("null")) {

                                        Log.v("",
                                                "shadeno="
                                                        + stock_array
                                                        .getString(23));
                                        shad = stock_array.getString(23)
                                                .toString();

                                    } else {
                                        Log.v("",
                                                "shadeno="
                                                        + stock_array
                                                        .getString(23));
                                        shad = "";
                                    }

                                    SoapPrimitive soap_result_stock = service
                                            .SaveStock(
                                                    stock_array.getString(0),
                                                    stock_array.getString(2),
                                                    stock_array.getString(1),
                                                    stock_array.getString(3),
                                                    username,
                                                    stock_array.getString(4),
                                                    stock_array.getString(5),
                                                    stock_array.getString(6),
                                                    shad,

                                                    stock_array.getString(10),
                                                    stock_array.getString(11),
                                                    stock_array.getString(12),

                                                    stock_array.getString(16),
                                                    stock_array.getString(14),
                                                    stock_array.getString(15),

                                                    stock_array.getString(13),
                                                    stock_array.getString(17),

                                                    stock_array.getString(19),
                                                    stock_array.getString(18),
                                                    stock_array.getString(7),
                                                    stock_array.getString(8),
                                                    stock_array.getString(21)

                                            );

									*//*
                     * soap_result_stock = service.SaveStock(
                     * stock_array.getString(2),
                     * stock_array.getString(1), eancode_string,
                     * username, stock_array.getString(4),
                     * stock_array.getString(5),
                     * stock_array.getString(6), shad,
                     *
                     * opening_stock_string,
                     * stock_receive_string,
                     * stock_in_hand_string,
                     *
                     * sold_string, return_salable_string,
                     * return_non_salable_string,
                     *
                     * close_bal_string, gross_amount_string,
                     *
                     * discount_string, net_amount_string,
                     * size_string, price_string,
                     * stock_array.getString(21)
                     *
                     *
                     * );
                     *//*

                                    if (soap_result_stock != null) {
                                        String result_stock = soap_result_stock
                                                .toString();
                                        Log.v("", "result_stock="
                                                + result_stock);
                                        if (result_stock.matches(".*\\d+.*")) {
                                            Log.e("", "stock id for update=="
                                                    + stock_array.getString(0));
                                            db.open();
                                            db.update_stock_data(result_stock);
                                            db.close();

                                        } else if (result_stock
                                                .equalsIgnoreCase("SE")) {

                                            ErroFlag = "0";
                                            Erro_function = "SaveStock()_SE";
                                            final Calendar calendar1 = Calendar
                                                    .getInstance();
                                            SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                    "MM/dd/yyyy HH:mm:ss");
                                            String Createddate = formatter1
                                                    .format(calendar1.getTime());

                                            int n = Thread.currentThread()
                                                    .getStackTrace()[2]
                                                    .getLineNumber();
                                            db.insertSyncLog("SaveStock_SE",
                                                    String.valueOf(n),
                                                    "SaveStock()", Createddate,
                                                    Createddate, sp.getString(
                                                            "username", ""),
                                                    "SaveStock()", "Fail");

                                        }

                                    } else {

                                        ErroFlag = "0";
                                        Erro_function = "SaveStock()-null";
                                        // String errors =
                                        // "Soap in giving null while 'Stock' and 'checkSyncFlag = 2' in  data Sync";
                                        // we.writeToSD(errors.toString());
                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "Internet Connection Lost, Soap in giving null while 'SaveStock'",
                                                String.valueOf(n),
                                                "SaveStock()", Createddate,
                                                Createddate,
                                                sp.getString("username", ""),
                                                "SaveStock()", "Fail");

                                    }

                                } while (stock_array.moveToNext());

                            } else {
                                // Toast.makeText(getActivity(),
                                // "No Data Available or Check Connectivity",
                                // .LENGTH_SHORT).show();
                                Flag = "2";
                                Log.e("", "no data available");

                            }

                        } else if (stock_array == null) {

                        } else {
                            Log.e("NoStock dataupload",
                                    String.valueOf(stock_array.getCount()));
                        }

                    } catch (Exception e) {
                        ErroFlag = "0";
                        Erro_function = "SaveStock()";
                        e.printStackTrace();

                        String Error = e.toString();

                        final Calendar cal = Calendar.getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(cal.getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(Error, String.valueOf(n),
                                "SaveStock()", Createddate, Createddate,
                                sp.getString("username", ""), "SaveStock()",
                                "Fail");

                    }*/

                    // -----------------------------------------------------------------------------01.03.2016

                    ClearLocalAppData();

                    String lastdate;
                    db.open();
                    String lastdatesync = db.getLastSyncDate("stock");

					/*if(lastdatesync != null && lastdatesync.length()>0) {
                        String[] dateParts = lastdatesync.split(" ");
						String date1 = dateParts[0];
						SimpleDateFormat sdf = new SimpleDateFormat(
								"MM/dd/yyyy", Locale.ENGLISH);//Using UAT server
						Date curntdte = null;
						try {
							curntdte = sdf.parse(date1);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						sdf.applyPattern("yyyy-MM-dd");
						lastdate = sdf.format(curntdte);
					}else{
						lastdate = "";
					}*/

                    db.close();

                    Calendar calendar1 = Calendar.getInstance();
                    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd ");
                    String strDate = mdformat.format(calendar1.getTime());

                    soap_result = service.DataDownload(
                            sp.getString("username", ""), strDate);//strDate

                    if (soap_result != null) {

                        for (int i = 0; i < soap_result.getPropertyCount(); i++) {

                            soap_result1 = (SoapObject) soap_result
                                    .getProperty(i);

                            Log.e("pm",
                                    "status="
                                            + soap_result1
                                            .getProperty("status")
                                            .toString());

                            if (soap_result1.getProperty("status").toString()
                                    .equalsIgnoreCase("C")) {

                                String db_stock_id = soap_result1.getProperty(
                                        "Id").toString();

                                String db_Id = soap_result1.getProperty(
                                        "ProductId").toString();

                                Log.v("", "db_Id=" + db_Id);

                                String CatCodeId = soap_result1.getProperty(
                                        "CatCodeId").toString();

                                if (CatCodeId == null) {
                                    CatCodeId = "";

                                }
                                Log.v("", "CatCodeId=" + CatCodeId);

                                String ProductId = soap_result1.getProperty(
                                        "ProductId").toString();
                                Log.v("", "ProductId=" + ProductId);

                                if (ProductId == null) {
                                    ProductId = "";

                                }

                                String EANCode = soap_result1.getProperty(
                                        "EANCode").toString();

                                if (EANCode == null) {
                                    EANCode = "";

                                }
                                EmpId = soap_result1.getProperty("EmpId")
                                        .toString();

                                if (EmpId == null) {
                                    EmpId = "";

                                }
                                String ProductCategory = soap_result1
                                        .getProperty("ProductCategory")
                                        .toString();

                                if (ProductCategory == null) {
                                    ProductCategory = "";

                                }
                                String ProductType = soap_result1.getProperty(
                                        "ProductType").toString();

                                if (ProductType == null) {
                                    ProductType = "";

                                }
                                String ProductName = soap_result1.getProperty(
                                        "ProductName").toString();
                                if (ProductName == null) {
                                    ProductName = "";

                                }

                                String Opening_Stock = soap_result1
                                        .getProperty("Opening_Stock")
                                        .toString();
                                if (Opening_Stock == null) {
                                    Opening_Stock = "";

                                }

                                String FreshStock = soap_result1.getProperty(
                                        "FreshStock").toString();
                                if (FreshStock == null) {
                                    FreshStock = "";

                                }

                                String Stock_inhand = soap_result1.getProperty(
                                        "Stock_inhand").toString();

                                if (Stock_inhand == null) {
                                    Stock_inhand = "";

                                }
                                String SoldStock = soap_result1.getProperty(
                                        "SoldStock").toString();

                                if (SoldStock == null) {
                                    SoldStock = "";

                                }
                                String S_Return_Saleable = soap_result1
                                        .getProperty("S_Return_Saleable")
                                        .toString();

                                if (S_Return_Saleable == null) {
                                    S_Return_Saleable = "";

                                }
                                String S_Return_NonSaleable = soap_result1
                                        .getProperty("S_Return_NonSaleable")
                                        .toString();

                                if (S_Return_NonSaleable == null) {
                                    S_Return_NonSaleable = "";

                                }
                                String ClosingBal = soap_result1.getProperty(
                                        "ClosingBal").toString();

                                if (ClosingBal == null) {
                                    ClosingBal = "";

                                }
                                String GrossAmount = soap_result1.getProperty(
                                        "GrossAmount").toString();

                                if (GrossAmount == null) {
                                    GrossAmount = "";

                                }
                                String Discount = soap_result1.getProperty(
                                        "Discount").toString();

                                if (Discount == null) {
                                    Discount = "";

                                }
                                String NetAmount = soap_result1.getProperty(
                                        "NetAmount").toString();

                                if (NetAmount == null) {
                                    NetAmount = "";

                                }
                                String Size = soap_result1.getProperty("Size")
                                        .toString();

                                if (Size == null) {
                                    Size = "";

                                }
                                String Price = soap_result1
                                        .getProperty("Price").toString();

                                if (Price == null) {
                                    Price = "";

                                }
                                String LMD = soap_result1.getProperty("LMD")
                                        .toString();

                                if (LMD == null) {
                                    LMD = "";

                                } else {

                                    try {
                                        String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                        String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                        DateFormat inputFormat = new SimpleDateFormat(
                                                inputPattern);
                                        SimpleDateFormat outputFormat = new SimpleDateFormat(
                                                outputPattern);

                                        Date date = inputFormat.parse(LMD);
                                        LMD = outputFormat.format(date);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                String AndroidCreatedDate = soap_result1
                                        .getProperty("AndroidCreatedDate")
                                        .toString();

                                String MONTH = "", YEAR = "";
                                if (AndroidCreatedDate == null) {
                                    AndroidCreatedDate = "";

                                } else {

                                    try {
                                        String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                        String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                        SimpleDateFormat inputFormat = new SimpleDateFormat(
                                                inputPattern);
                                        SimpleDateFormat outputFormat = new SimpleDateFormat(
                                                outputPattern);

                                        Date date = inputFormat
                                                .parse(AndroidCreatedDate);
                                        AndroidCreatedDate = outputFormat
                                                .format(date);

                                        String[] addd = AndroidCreatedDate
                                                .split(" ");
                                        String addd1 = addd[0];
                                        String[] addd2 = addd1.split("-");

                                        String month = addd2[1];
                                        YEAR = addd2[0];
                                        //
                                        SimpleDateFormat monthParse = new SimpleDateFormat(
                                                "MM");
                                        SimpleDateFormat monthDisplay = new SimpleDateFormat(
                                                "MMMM");
                                        MONTH = monthDisplay.format(monthParse
                                                .parse(month));
                                        //

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (db_Id.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for db_Id");
                                    db_Id = " ";
                                }
                                if (CatCodeId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for CatCodeId");
                                    CatCodeId = " ";
                                }

                                if (ProductId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductId");
                                    ProductId = " ";
                                }
                                if (EANCode.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for EANCode");
                                    EANCode = " ";
                                }

                                if (EmpId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for EmpId");
                                    EmpId = " ";
                                }
                                if (ProductCategory
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductCategory");
                                    ProductCategory = " ";
                                }
                                if (ProductType.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductType");
                                    ProductType = " ";
                                }

                                if (ProductName.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductName");
                                    ProductName = " ";
                                }
                                if (Opening_Stock.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Opening_Stock");
                                    Opening_Stock = " ";
                                }

                                if (FreshStock.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for FreshStock");
                                    FreshStock = " ";
                                }
                                if (Stock_inhand.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Stock_inhand");
                                    Stock_inhand = " ";
                                }
                                if (SoldStock.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for SoldStock");
                                    SoldStock = " ";
                                }
                                if (S_Return_Saleable
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for S_Return_Saleable");
                                    S_Return_Saleable = " ";
                                }
                                if (S_Return_NonSaleable
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("",
                                            "anytype for S_Return_NonSaleable");
                                    S_Return_NonSaleable = " ";
                                }
                                if (ClosingBal.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ClosingBal");
                                    ClosingBal = " ";
                                }
                                if (GrossAmount.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for GrossAmount");
                                    GrossAmount = " ";
                                }
                                if (Discount.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Discount");
                                    Discount = " ";
                                }
                                if (NetAmount.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for NetAmount");
                                    NetAmount = " ";
                                }
                                if (Size.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Size");
                                    Size = " ";
                                }
                                if (Price.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for sku_l");
                                    Price = " ";
                                }
                                if (LMD.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for LMD");
                                    LMD = " ";
                                }
                                if (AndroidCreatedDate
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for AndroidCreatedDate");
                                    AndroidCreatedDate = " ";
                                }

                                Log.e("pm", "pm5--");
                                db.open();

                                Cursor c1 = db.CheckDataExist("stock", db_Id,
                                        ProductCategory, ProductType,
                                        ProductName);


                                int count = c1.getCount();
                                Log.v("", "" + count);
                                db.close();
                                if (count > 0) {

                                    db.open();
                                    db.UpdateStockSync1(ProductCategory,
                                            ProductType, ProductName, EmpId,
                                            Opening_Stock, Stock_inhand, ClosingBal,
                                            FreshStock, GrossAmount, SoldStock,
                                            Price, Size, db_Id, LMD, Discount,
                                            NetAmount,
                                            S_Return_Saleable,
                                            S_Return_NonSaleable);
                                    db.close();

                                    db_stock_id_array = db_stock_id_array + ","
                                            + db_Id;

                                } else {

                                    Log.e("pm", "pm5");
                                    db.open();
                                    db.insertProductMasterFirsttime(
                                            db_stock_id, db_Id, ProductId,
                                            CatCodeId, EANCode, EmpId,
                                            ProductCategory, ProductType,
                                            ProductName, Opening_Stock,
                                            FreshStock, Stock_inhand,
                                            SoldStock, S_Return_NonSaleable,
                                            S_Return_Saleable, ClosingBal,
                                            GrossAmount, Discount, NetAmount,
                                            Size, Price, LMD,
                                            AndroidCreatedDate, MONTH, YEAR);
                                    db.close();

                                    db_stock_id_array = db_stock_id_array + ","
                                            + db_Id;

                                }

                            } else if (soap_result1.getProperty("status")
                                    .toString().equalsIgnoreCase("E")) {
                                Log.e("pm", "pm7");
                                // Flag = "1";

                                writeStringAsFile(db_stock_id_array);

                                soap_update_stock_row = service
                                        .UpdateTableData(db_stock_id_array,
                                                "S", EmpId);

                                SimpleDateFormat dateFormat = new SimpleDateFormat(
                                        "MM/dd/yyyy HH:mm:ss");
                                // get current date time with Date()
                                Calendar cal = Calendar.getInstance();
                                // dateFormat.format(cal.getTime())
                                db.open();
                                db.updateDateSync(
                                        dateFormat.format(cal.getTime()),
                                        "stock");
                                db.close();

                            } else if (soap_result1.getProperty("status")
                                    .toString().equalsIgnoreCase("N")) {

                                // Flag = "3";
                                Log.e("", "string ids== " + db_stock_id_array);
                                soap_update_stock_row = service
                                        .UpdateTableData(db_stock_id_array,
                                                "S", EmpId);
                                Log.e("", "soap_update_stock_row= "
                                        + soap_update_stock_row.toString());

                                syncstockdata = 1;
                            } else if (soap_result1.getProperty("status")
                                    .toString().equalsIgnoreCase("SE")) {

                                soap_update_stock_row = service
                                        .UpdateTableData(db_stock_id_array,
                                                "S", EmpId);
                                Log.e("", "soap_update_stock_row= "
                                        + soap_update_stock_row.toString());

                                // Flag="2";
                                Log.e("", "string ids== " + db_stock_id_array);
                                syncstockdata = 0;
                                final Calendar calendar = Calendar
                                        .getInstance();
                                SimpleDateFormat formatter = new SimpleDateFormat(
                                        "MM/dd/yyyy HH:mm:ss");
                                String Createddate = formatter.format(calendar
                                        .getTime());
                                Log.v("", "se error");
                                int n = Thread.currentThread().getStackTrace()[2]
                                        .getLineNumber();
                                db.open();
                                db.insertSyncLog("FirstTimeSync_SE",
                                        String.valueOf(n), "DataDownload()",
                                        Createddate, Createddate,
                                        sp.getString("username", ""),
                                        "DataDownload()", "Fail");
                                db.close();
                            }
                        }

                    } else {
                        Log.v("", "Soap result is null");

                        syncstockdata = 0;
                        final Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter.format(calendar
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog("Soup is null - DataDownload()",
                                String.valueOf(n), "DataDownload()",
                                Createddate, Createddate,
                                sp.getString("username", ""), "Data Download",
                                "Fail");

                    }

                    soap_result = service.DataDownloadForSale(
                            sp.getString("username", ""), strDate);//strDate

                    if (soap_result != null) {

                        for (int i = 0; i < soap_result.getPropertyCount(); i++) {

                            soap_result1 = (SoapObject) soap_result
                                    .getProperty(i);

                            Log.e("pm",
                                    "status="
                                            + soap_result1
                                            .getProperty("status")
                                            .toString());

                            if (soap_result1.getProperty("status").toString()
                                    .equalsIgnoreCase("C")) {

                                String db_stock_id = soap_result1.getProperty(
                                        "Id").toString();

                                String db_Id = soap_result1.getProperty(
                                        "ProductId").toString();

                                Log.v("", "db_Id=" + db_Id);

                                String CatCodeId = soap_result1.getProperty(
                                        "CatCodeId").toString();

                                if (CatCodeId == null) {
                                    CatCodeId = "";

                                }
                                Log.v("", "CatCodeId=" + CatCodeId);

                                String ProductId = soap_result1.getProperty(
                                        "ProductId").toString();
                                Log.v("", "ProductId=" + ProductId);

                                if (ProductId == null) {
                                    ProductId = "";

                                }

                                String EANCode = soap_result1.getProperty(
                                        "EANCode").toString();

                                if (EANCode == null) {
                                    EANCode = "";

                                }
                                EmpId = soap_result1.getProperty("EmpId")
                                        .toString();

                                if (EmpId == null) {
                                    EmpId = "";

                                }
                                String ProductCategory = soap_result1
                                        .getProperty("ProductCategory")
                                        .toString();

                                if (ProductCategory == null) {
                                    ProductCategory = "";

                                }
                                String ProductType = soap_result1.getProperty(
                                        "ProductType").toString();

                                if (ProductType == null) {
                                    ProductType = "";

                                }
                                String ProductName = soap_result1.getProperty(
                                        "ProductName").toString();
                                if (ProductName == null) {
                                    ProductName = "";

                                }

                                String Opening_Stock = soap_result1
                                        .getProperty("Opening_Stock")
                                        .toString();
                                if (Opening_Stock == null) {
                                    Opening_Stock = "";

                                }

                                String FreshStock = soap_result1.getProperty(
                                        "FreshStock").toString();
                                if (FreshStock == null) {
                                    FreshStock = "";

                                }

                                String Stock_inhand = soap_result1.getProperty(
                                        "Stock_inhand").toString();

                                if (Stock_inhand == null) {
                                    Stock_inhand = "";

                                }
                                String SoldStock = soap_result1.getProperty(
                                        "SoldStock").toString();

                                if (SoldStock == null) {
                                    SoldStock = "";

                                }
                                String S_Return_Saleable = soap_result1
                                        .getProperty("S_Return_Saleable")
                                        .toString();

                                if (S_Return_Saleable == null) {
                                    S_Return_Saleable = "";

                                }
                                String S_Return_NonSaleable = soap_result1
                                        .getProperty("S_Return_NonSaleable")
                                        .toString();

                                if (S_Return_NonSaleable == null) {
                                    S_Return_NonSaleable = "";

                                }
                                String ClosingBal = soap_result1.getProperty(
                                        "ClosingBal").toString();

                                if (ClosingBal == null) {
                                    ClosingBal = "";

                                }
                                String GrossAmount = soap_result1.getProperty(
                                        "GrossAmount").toString();

                                if (GrossAmount == null) {
                                    GrossAmount = "";

                                }
                                String Discount = soap_result1.getProperty(
                                        "Discount").toString();

                                if (Discount == null) {
                                    Discount = "";

                                }
                                String NetAmount = soap_result1.getProperty(
                                        "NetAmount").toString();

                                if (NetAmount == null) {
                                    NetAmount = "";

                                }
                                String Size = soap_result1.getProperty("Size")
                                        .toString();

                                if (Size == null) {
                                    Size = "";

                                }
                                String Price = soap_result1
                                        .getProperty("Price").toString();

                                if (Price == null) {
                                    Price = "";

                                }
                                String LMD = soap_result1.getProperty("LMD")
                                        .toString();

                                if (LMD == null) {
                                    LMD = "";

                                } else {

                                    try {
                                        String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                        String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                        DateFormat inputFormat = new SimpleDateFormat(
                                                inputPattern);
                                        SimpleDateFormat outputFormat = new SimpleDateFormat(
                                                outputPattern);

                                        Date date = inputFormat.parse(LMD);
                                        LMD = outputFormat.format(date);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                String AndroidCreatedDate = soap_result1
                                        .getProperty("AndroidCreatedDate")
                                        .toString();

                                String MONTH = "", YEAR = "";
                                if (AndroidCreatedDate == null) {
                                    AndroidCreatedDate = "";

                                } else {

                                    try {
                                        String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                        String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                        SimpleDateFormat inputFormat = new SimpleDateFormat(
                                                inputPattern);
                                        SimpleDateFormat outputFormat = new SimpleDateFormat(
                                                outputPattern);

                                        Date date = inputFormat
                                                .parse(AndroidCreatedDate);
                                        AndroidCreatedDate = outputFormat
                                                .format(date);

                                        String[] addd = AndroidCreatedDate
                                                .split(" ");
                                        String addd1 = addd[0];
                                        String[] addd2 = addd1.split("-");

                                        String month = addd2[1];
                                        YEAR = addd2[0];
                                        //
                                        SimpleDateFormat monthParse = new SimpleDateFormat(
                                                "MM");
                                        SimpleDateFormat monthDisplay = new SimpleDateFormat(
                                                "MMMM");
                                        MONTH = monthDisplay.format(monthParse
                                                .parse(month));
                                        //

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (db_Id.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for db_Id");
                                    db_Id = " ";
                                }
                                if (CatCodeId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for CatCodeId");
                                    CatCodeId = " ";
                                }

                                if (ProductId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductId");
                                    ProductId = " ";
                                }
                                if (EANCode.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for EANCode");
                                    EANCode = " ";
                                }

                                if (EmpId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for EmpId");
                                    EmpId = " ";
                                }
                                if (ProductCategory
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductCategory");
                                    ProductCategory = " ";
                                }
                                if (ProductType.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductType");
                                    ProductType = " ";
                                }

                                if (ProductName.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductName");
                                    ProductName = " ";
                                }
                                if (Opening_Stock.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Opening_Stock");
                                    Opening_Stock = " ";
                                }

                                if (FreshStock.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for FreshStock");
                                    FreshStock = " ";
                                }
                                if (Stock_inhand.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Stock_inhand");
                                    Stock_inhand = " ";
                                }
                                if (SoldStock.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for SoldStock");
                                    SoldStock = " ";
                                }
                                if (S_Return_Saleable
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for S_Return_Saleable");
                                    S_Return_Saleable = " ";
                                }
                                if (S_Return_NonSaleable
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("",
                                            "anytype for S_Return_NonSaleable");
                                    S_Return_NonSaleable = " ";
                                }
                                if (ClosingBal.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ClosingBal");
                                    ClosingBal = " ";
                                }
                                if (GrossAmount.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for GrossAmount");
                                    GrossAmount = " ";
                                }
                                if (Discount.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Discount");
                                    Discount = " ";
                                }
                                if (NetAmount.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for NetAmount");
                                    NetAmount = " ";
                                }
                                if (Size.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Size");
                                    Size = " ";
                                }
                                if (Price.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for sku_l");
                                    Price = " ";
                                }
                                if (LMD.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for LMD");
                                    LMD = " ";
                                }
                                if (AndroidCreatedDate
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for AndroidCreatedDate");
                                    AndroidCreatedDate = " ";
                                }

                                Log.e("pm", "pm5--");
                                db.open();

                                Cursor c1 = db.CheckDataExist("stock", db_Id,
                                        ProductCategory, ProductType,
                                        ProductName);


                                int count = c1.getCount();
                                Log.v("", "" + count);
                                db.close();
                                if (count > 0) {

                                    db.open();
                                    db.UpdateStockSync1(ProductCategory,
                                            ProductType, ProductName, EmpId,
                                            Opening_Stock, Stock_inhand, ClosingBal,
                                            FreshStock, GrossAmount, SoldStock,
                                            Price, Size, db_Id, LMD, Discount,
                                            NetAmount,
                                            S_Return_Saleable,
                                            S_Return_NonSaleable);
                                    db.close();

                                    db_stock_id_array = db_stock_id_array + ","
                                            + db_Id;

                                } else {

                                    Log.e("pm", "pm5");
                                    db.open();
                                    db.insertProductMasterFirsttime(
                                            db_stock_id, db_Id, ProductId,
                                            CatCodeId, EANCode, EmpId,
                                            ProductCategory, ProductType,
                                            ProductName, Opening_Stock,
                                            FreshStock, Stock_inhand,
                                            SoldStock, S_Return_NonSaleable,
                                            S_Return_Saleable, ClosingBal,
                                            GrossAmount, Discount, NetAmount,
                                            Size, Price, LMD,
                                            AndroidCreatedDate, MONTH, YEAR);
                                    db.close();

                                    db_stock_id_array = db_stock_id_array + ","
                                            + db_Id;

                                }

                            } else if (soap_result1.getProperty("status")
                                    .toString().equalsIgnoreCase("E")) {
                                Log.e("pm", "pm7");
                                // Flag = "1";

                                writeStringAsFile(db_stock_id_array);

                                soap_update_stock_row = service
                                        .UpdateTableData(db_stock_id_array,
                                                "S", EmpId);

                                SimpleDateFormat dateFormat = new SimpleDateFormat(
                                        "MM/dd/yyyy HH:mm:ss");
                                // get current date time with Date()
                                Calendar cal = Calendar.getInstance();
                                // dateFormat.format(cal.getTime())
                                db.open();
                                db.updateDateSync(
                                        dateFormat.format(cal.getTime()),
                                        "stock");
                                db.close();

                            } else if (soap_result1.getProperty("status")
                                    .toString().equalsIgnoreCase("N")) {

                                // Flag = "3";
                                Log.e("", "string ids== " + db_stock_id_array);
                                soap_update_stock_row = service
                                        .UpdateTableData(db_stock_id_array,
                                                "S", EmpId);
                                Log.e("", "soap_update_stock_row= "
                                        + soap_update_stock_row.toString());

                                syncstockdata = 1;
                            } else if (soap_result1.getProperty("status")
                                    .toString().equalsIgnoreCase("SE")) {

                                soap_update_stock_row = service
                                        .UpdateTableData(db_stock_id_array,
                                                "S", EmpId);
                                Log.e("", "soap_update_stock_row= "
                                        + soap_update_stock_row.toString());

                                // Flag="2";
                                Log.e("", "string ids== " + db_stock_id_array);
                                syncstockdata = 0;
                                final Calendar calendar = Calendar
                                        .getInstance();
                                SimpleDateFormat formatter = new SimpleDateFormat(
                                        "MM/dd/yyyy HH:mm:ss");
                                String Createddate = formatter.format(calendar
                                        .getTime());
                                Log.v("", "se error");
                                int n = Thread.currentThread().getStackTrace()[2]
                                        .getLineNumber();
                                db.open();
                                db.insertSyncLog("FirstTimeSync_SE",
                                        String.valueOf(n), "DataDownload()",
                                        Createddate, Createddate,
                                        sp.getString("username", ""),
                                        "DataDownload()", "Fail");
                                db.close();
                            }
                        }

                    } else {
                        Log.v("", "Soap result is null");

                        syncstockdata = 0;
                        final Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter.format(calendar
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog("Soup is null - DataDownload()",
                                String.valueOf(n), "DataDownload()",
                                Createddate, Createddate,
                                sp.getString("username", ""), "Data Download",
                                "Fail");

                    }

					/*soap_result = service.SyncStockData(
                            sp.getString("username", ""), lastdatesync);
					// soap_result =
					// service.SyncStockData(sp.getString("username",
					// ""),"05/01/2014 12:38:40");

					// Log.e("pm",
					// "pm1soap_result====+++---++__===____==="+soap_result.getPropertyCount());

					if (soap_result != null) {

						for (int i = 0; i < soap_result.getPropertyCount(); i++) {

							soap_result1 = (SoapObject) soap_result
									.getProperty(i);

							Log.e("pm",
									"status="
											+ soap_result1
											.getProperty("status")
											.toString());

							if (soap_result1.getProperty("status").toString()
									.equalsIgnoreCase("C")) {

								// try {
								// Log.v("", "soapresul--------" +
								// soap_result1.toString());

								String db_stock_id = soap_result1.getProperty(
										"Id").toString();

								String db_Id = soap_result1.getProperty(
										"ProductId").toString();

								Log.v("", "db_Id=" + db_Id);

								String CatCodeId = soap_result1.getProperty(
										"CatCodeId").toString();

								if (CatCodeId == null) {
									CatCodeId = "";

								}
								Log.v("", "CatCodeId=" + CatCodeId);

								String ProductId = soap_result1.getProperty(
										"ProductId").toString();
								Log.v("", "ProductId=" + ProductId);

								if (ProductId == null) {
									ProductId = "";

								}

								String EANCode = soap_result1.getProperty(
										"EANCode").toString();

								if (EANCode == null) {
									EANCode = "";

								}
								EmpId = soap_result1.getProperty("EmpId")
										.toString();

								if (EmpId == null) {
									EmpId = "";

								}
								String ProductCategory = soap_result1
										.getProperty("ProductCategory")
										.toString();

								if (ProductCategory == null) {
									ProductCategory = "";

								}
								String ProductType = soap_result1.getProperty(
										"ProductType").toString();

								if (ProductType == null) {
									ProductType = "";

								}
								String ProductName = soap_result1.getProperty(
										"ProductName").toString();
								if (ProductName == null) {
									ProductName = "";

								}

								String Opening_Stock = soap_result1
										.getProperty("Opening_Stock")
										.toString();
								if (Opening_Stock == null) {
									Opening_Stock = "";

								}

								String FreshStock = soap_result1.getProperty(
										"FreshStock").toString();
								if (FreshStock == null) {
									FreshStock = "";

								}

								String Stock_inhand = soap_result1.getProperty(
										"Stock_inhand").toString();

								if (Stock_inhand == null) {
									Stock_inhand = "";

								}
								String SoldStock = soap_result1.getProperty(
										"SoldStock").toString();

								if (SoldStock == null) {
									SoldStock = "";

								}
								String S_Return_Saleable = soap_result1
										.getProperty("S_Return_Saleable")
										.toString();

								if (S_Return_Saleable == null) {
									S_Return_Saleable = "";

								}
								String S_Return_NonSaleable = soap_result1
										.getProperty("S_Return_NonSaleable")
										.toString();

								if (S_Return_NonSaleable == null) {
									S_Return_NonSaleable = "";

								}
								String ClosingBal = soap_result1.getProperty(
										"ClosingBal").toString();

								if (ClosingBal == null) {
									ClosingBal = "";

								}
								String GrossAmount = soap_result1.getProperty(
										"GrossAmount").toString();

								if (GrossAmount == null) {
									GrossAmount = "";

								}
								String Discount = soap_result1.getProperty(
										"Discount").toString();

								if (Discount == null) {
									Discount = "";

								}
								String NetAmount = soap_result1.getProperty(
										"NetAmount").toString();

								if (NetAmount == null) {
									NetAmount = "";

								}
								String Size = soap_result1.getProperty("Size")
										.toString();

								if (Size == null) {
									Size = "";

								}
								String Price = soap_result1
										.getProperty("Price").toString();

								if (Price == null) {
									Price = "";

								}
								String LMD = soap_result1.getProperty("LMD")
										.toString();

								if (LMD == null) {
									LMD = "";

								} else {

									try {
										String inputPattern = "MM/dd/yyyy hh:mm:ss a";
										String outputPattern = "yyyy-MM-dd HH:mm:ss";

										DateFormat inputFormat = new SimpleDateFormat(
												inputPattern);
										SimpleDateFormat outputFormat = new SimpleDateFormat(
												outputPattern);

										Date date = inputFormat.parse(LMD);
										LMD = outputFormat.format(date);

									} catch (Exception e) {
										e.printStackTrace();
									}
								}

								String AndroidCreatedDate = soap_result1
										.getProperty("AndroidCreatedDate")
										.toString();

								String MONTH = "", YEAR = "";
								if (AndroidCreatedDate == null) {
									AndroidCreatedDate = "";

								} else {

									try {
										String inputPattern = "MM/dd/yyyy hh:mm:ss a";
										String outputPattern = "yyyy-MM-dd HH:mm:ss";

										SimpleDateFormat inputFormat = new SimpleDateFormat(
												inputPattern);
										SimpleDateFormat outputFormat = new SimpleDateFormat(
												outputPattern);

										Date date = inputFormat
												.parse(AndroidCreatedDate);
										AndroidCreatedDate = outputFormat
												.format(date);

										String[] addd = AndroidCreatedDate
												.split(" ");
										String addd1 = addd[0];
										String[] addd2 = addd1.split("-");

										String month = addd2[1];
										YEAR = addd2[0];
										//
										SimpleDateFormat monthParse = new SimpleDateFormat(
												"MM");
										SimpleDateFormat monthDisplay = new SimpleDateFormat(
												"MMMM");
										MONTH = monthDisplay.format(monthParse
												.parse(month));
										//

									} catch (Exception e) {
										e.printStackTrace();
									}
								}

								// String lmd =
								// soap_result1.getProperty("LMD").toString();

								if (db_Id.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for db_Id");
									db_Id = " ";
								}
								if (CatCodeId.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for CatCodeId");
									CatCodeId = " ";
								}

								if (ProductId.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for ProductId");
									ProductId = " ";
								}
								if (EANCode.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for EANCode");
									EANCode = " ";
								}

								if (EmpId.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for EmpId");
									EmpId = " ";
								}
								if (ProductCategory
										.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for ProductCategory");
									ProductCategory = " ";
								}
								if (ProductType.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for ProductType");
									ProductType = " ";
								}

								if (ProductName.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for ProductName");
									ProductName = " ";
								}
								if (Opening_Stock.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for Opening_Stock");
									Opening_Stock = " ";
								}

								if (FreshStock.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for FreshStock");
									FreshStock = " ";
								}
								if (Stock_inhand.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for Stock_inhand");
									Stock_inhand = " ";
								}
								if (SoldStock.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for SoldStock");
									SoldStock = " ";
								}
								if (S_Return_Saleable
										.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for S_Return_Saleable");
									S_Return_Saleable = " ";
								}
								if (S_Return_NonSaleable
										.equalsIgnoreCase("anyType{}")) {
									Log.e("",
											"anytype for S_Return_NonSaleable");
									S_Return_NonSaleable = " ";
								}
								if (ClosingBal.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for ClosingBal");
									ClosingBal = " ";
								}
								if (GrossAmount.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for GrossAmount");
									GrossAmount = " ";
								}
								if (Discount.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for Discount");
									Discount = " ";
								}
								if (NetAmount.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for NetAmount");
									NetAmount = " ";
								}
								if (Size.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for Size");
									Size = " ";
								}
								if (Price.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for sku_l");
									Price = " ";
								}
								if (LMD.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for LMD");
									LMD = " ";
								}
								if (AndroidCreatedDate
										.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for AndroidCreatedDate");
									AndroidCreatedDate = " ";
								}

								// if (flag.equalsIgnoreCase("e")) {

								Log.e("pm", "pm5--");
								db.open();
								// Cursor c = db.getuniquedata_stock(CatCodeId,
								// EANCode, db_Id);

								Cursor c1 = db.CheckDataExist("stock", db_Id,
										ProductCategory, ProductType,
										ProductName);

								// db_stock_id_array = db_stock_id;

								int count = c1.getCount();
								Log.v("", "" + count);
								db.close();
								if (count > 0) {

									db.open();
									db.UpdateStockSync(ProductCategory,
											ProductType, ProductName, EmpId,
											Stock_inhand, ClosingBal,
											FreshStock, GrossAmount, SoldStock,
											Price, Size, db_Id, LMD, Discount,
											NetAmount, Opening_Stock,
											S_Return_Saleable,
											S_Return_NonSaleable);
									db.close();

									// soap_update_stock_row =
									// service.UpdateTableData(
									// db_stock_id,
									// "S",
									// EmpId);
									// Log.e("",
									// "soap_update_stock_row= "+soap_update_stock_row.toString());
									db_stock_id_array = db_stock_id_array + ","
											+ db_Id;

								} else {

									Log.e("pm", "pm5");
									db.open();
									db.insertProductMasterFirsttime(
											db_stock_id, db_Id, ProductId,
											CatCodeId, EANCode, EmpId,
											ProductCategory, ProductType,
											ProductName, Opening_Stock,
											FreshStock, Stock_inhand,
											SoldStock, S_Return_NonSaleable,
											S_Return_Saleable, ClosingBal,
											GrossAmount, Discount, NetAmount,
											Size, Price, LMD,
											AndroidCreatedDate, MONTH, YEAR);
									db.close();

									// Log.e("",
									// "db_stock_id="+db_stock_id+" empid="+EmpId);
									db_stock_id_array = db_stock_id_array + ","
											+ db_Id;
									// Log.e("",
									// "string ids1== "+db_stock_id_array);

									// soap_update_stock_row =
									// service.UpdateTableData(
									// db_stock_id,
									// "S",
									// EmpId);
									// Log.e("",
									// "soap_update_stock_row= "+soap_update_stock_row.toString());

								}

								// }
								*//*
                     * } catch (Exception e) { // TODO: handle
                     * exception e.printStackTrace(); String Error =
                     * e.toString(); Log.v("","se2 error"); final
                     * Calendar calendar = Calendar .getInstance();
                     * SimpleDateFormat formatter = new
                     * SimpleDateFormat( "MM/dd/yyyy HH:mm:ss");
                     * String Createddate =
                     * formatter.format(calendar .getTime()); Flag =
                     * "4"; int n =
                     * Thread.currentThread().getStackTrace
                     * ()[2].getLineNumber(); db.open();
                     * db.insertSyncLog(Error,String.valueOf(n),
                     * "SyncStockData()"
                     * ,Createddate,Createddate,sp.getString
                     * ("username", ""),"SyncStockData()","Fail");
                     * db.close(); }
                     *//*

							} else if (soap_result1.getProperty("status")
									.toString().equalsIgnoreCase("E")) {
								Log.e("pm", "pm7");
								// Flag = "1";

								writeStringAsFile(db_stock_id_array);

								soap_update_stock_row = service
										.UpdateTableData(db_stock_id_array,
												"S", EmpId);
								*//*
                     * Log.e("",
                     * "soap_update_stock_row= "+soap_update_stock_row
                     * .toString());
                     *
                     * Log.e("", "string ids== "+db_stock_id_array);
                     *//*
                                SimpleDateFormat dateFormat = new SimpleDateFormat(
										"MM/dd/yyyy HH:mm:ss");
								// get current date time with Date()
								Calendar cal = Calendar.getInstance();
								// dateFormat.format(cal.getTime())
								db.open();
								db.updateDateSync(
										dateFormat.format(cal.getTime()),
										"stock");
								db.close();

							} else if (soap_result1.getProperty("status")
									.toString().equalsIgnoreCase("N")) {

								// Flag = "3";
								Log.e("", "string ids== " + db_stock_id_array);
								soap_update_stock_row = service
										.UpdateTableData(db_stock_id_array,
												"S", EmpId);
								Log.e("", "soap_update_stock_row= "
										+ soap_update_stock_row.toString());

								syncstockdata = 1;
							} else if (soap_result1.getProperty("status")
									.toString().equalsIgnoreCase("SE")) {

								soap_update_stock_row = service
										.UpdateTableData(db_stock_id_array,
												"S", EmpId);
								Log.e("", "soap_update_stock_row= "
										+ soap_update_stock_row.toString());

								// Flag="2";
								Log.e("", "string ids== " + db_stock_id_array);
								syncstockdata = 0;
								final Calendar calendar = Calendar
										.getInstance();
								SimpleDateFormat formatter = new SimpleDateFormat(
										"MM/dd/yyyy HH:mm:ss");
								String Createddate = formatter.format(calendar
										.getTime());
								Log.v("", "se error");
								int n = Thread.currentThread().getStackTrace()[2]
										.getLineNumber();
								db.open();
								db.insertSyncLog("FirstTimeSync_SE",
										String.valueOf(n), "SyncStockData()",
										Createddate, Createddate,
										sp.getString("username", ""),
										"SyncStockData()", "Fail");
								db.close();
							}
						}

					} else {
						Log.v("", "Soap result is null");
						// Toast.makeText(context,
						// "Data Not Available or Check Connectivity",
						// Toast.LENGTH_SHORT).show();

						syncstockdata = 0;
						final Calendar calendar = Calendar.getInstance();
						SimpleDateFormat formatter = new SimpleDateFormat(
								"MM/dd/yyyy HH:mm:ss");
						String Createddate = formatter.format(calendar
								.getTime());

						int n = Thread.currentThread().getStackTrace()[2]
								.getLineNumber();
						db.insertSyncLog("Soup is null - SyncStockData()",
								String.valueOf(n), "SyncStockData()",
								Createddate, Createddate,
								sp.getString("username", ""), "Data Download",
								"Fail");

					}*/

                    // -----------------Tester-------------//

                    db.open();
                    String lastdatesync1 = db.getLastSyncDate("tester");
                    db.close();

                    soap_result_tester = service.SyncGetTesterData(
                            sp.getString("username", ""), lastdatesync1);// 09.10.2015

                    Log.e("pm", "pm1");
                    if (soap_result_tester != null) {

                        for (int i = 0; i < soap_result_tester
                                .getPropertyCount(); i++) {

                            soap_result_tester1 = (SoapObject) soap_result_tester
                                    .getProperty(i);

                            Log.e("pm", "status="
                                    + soap_result_tester1.getProperty("status")
                                    .toString());

                            if (soap_result_tester1.getProperty("status")
                                    .toString().equalsIgnoreCase("C")) {


                                String db_tester_id = soap_result_tester1
                                        .getProperty("Id").toString();

                                String db_Id = soap_result_tester1.getProperty(
                                        "ProductId").toString();

                                Log.v("", "db_Id=" + db_Id);

                                String CatCodeId = soap_result_tester1
                                        .getProperty("CatCodeId").toString();

                                if (CatCodeId == null
                                        || CatCodeId.equalsIgnoreCase("null")) {
                                    CatCodeId = "";

                                }
                                Log.v("", "CatCodeId=" + CatCodeId);

                                String EANCode = soap_result_tester1
                                        .getProperty("EANCode").toString();

                                if (EANCode == null
                                        || EANCode.equalsIgnoreCase("null")) {
                                    EANCode = "";

                                }
                                String EmpId = soap_result_tester1.getProperty(
                                        "EmpId").toString();

                                if (EmpId == null
                                        || EmpId.equalsIgnoreCase("null")) {
                                    EmpId = "";

                                }
                                String ProductCategory = soap_result_tester1
                                        .getProperty("ProductCategory")
                                        .toString();

                                if (ProductCategory == null) {
                                    ProductCategory = "";

                                }
                                String ProductType = soap_result_tester1
                                        .getProperty("ProductType").toString();

                                if (ProductType == null) {
                                    ProductType = "";

                                }

                                String ProductName = soap_result_tester1
                                        .getProperty("ProductName").toString();

                                if (ProductName == null) {
                                    ProductName = "";

                                }

                                String Size = soap_result_tester1.getProperty(
                                        "Size").toString();

                                if (Size == null
                                        || Size.equalsIgnoreCase("null")) {
                                    Size = "";

                                }
                                String ShadeNo = soap_result_tester1
                                        .getProperty("ShadeNo").toString();

                                if (ShadeNo == null
                                        || ShadeNo.equalsIgnoreCase("null")
                                        || ShadeNo
                                        .equalsIgnoreCase("anyType{}")) {
                                    ShadeNo = "";

                                }

                                String LMD = soap_result_tester1.getProperty(
                                        "LMD").toString();

                                if (LMD == null) {
                                    LMD = "";

                                }

                                String Curr_Status = soap_result_tester1
                                        .getProperty("Curr_Status").toString();

                                if (Curr_Status == null) {
                                    Curr_Status = "";

                                }

                                String RequestDate = soap_result_tester1
                                        .getProperty("RequestDate").toString();

                                if (RequestDate == null) {
                                    RequestDate = "";

                                }

                                String DelieveredDate = soap_result_tester1
                                        .getProperty("DelieveredDate")
                                        .toString();

                                if (DelieveredDate == null
                                        || DelieveredDate
                                        .equalsIgnoreCase("null")) {
                                    DelieveredDate = "";

                                }

                                if (db_Id.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for db_Id");
                                    db_Id = " ";
                                }
                                if (CatCodeId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for CatCodeId");
                                    CatCodeId = " ";
                                }

                                if (EANCode.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for EANCode");
                                    EANCode = " ";
                                }

                                if (EmpId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for EmpId");
                                    EmpId = " ";
                                }
                                if (ProductCategory
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductCategory");
                                    ProductCategory = " ";
                                }
                                if (ProductType.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductType");
                                    ProductType = " ";
                                }

                                if (ProductName.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductName");
                                    ProductName = " ";
                                }

                                if (Size.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Size");
                                    Size = " ";
                                }

                                if (DelieveredDate
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for DelieveredDate");
                                    DelieveredDate = " ";
                                }

                                Log.e("pm", "pm5--");
                                db.open();

                                Cursor c1 = db.CheckDataExist("tester", db_Id,
                                        ProductCategory, ProductType,
                                        ProductName);

                                int count = c1.getCount();
                                Log.v("", "" + count);
                                db.close();
                                if (count > 0) {

                                    db.open();
                                    db.UpdateTesterSync(db_Id, ProductCategory,
                                            ProductType, ProductName, EmpId,
                                            Size, RequestDate, DelieveredDate,
                                            Curr_Status, ShadeNo);
                                    db.close();

                                    db_tester_id_array = db_tester_id_array
                                            + "," + db_tester_id;// 10.10.2015

                                } else {

                                    Log.e("pm", "pm5");
                                    db.open();
                                    db.insertTesterDownloadedData(db_tester_id,
                                            db_Id, CatCodeId, EANCode, EmpId,
                                            ProductCategory, ProductType,
                                            ProductName, RequestDate,
                                            DelieveredDate, ShadeNo,
                                            Curr_Status, Size);
                                    db.close();

                                    Log.e("", "db_tester_id=" + db_tester_id
                                            + " empid=" + EmpId);

                                    db_tester_id_array = db_tester_id_array
                                            + "," + db_tester_id;// 10.10.2015

                                }

                            } else if (soap_result_tester1
                                    .getProperty("status").toString()
                                    .equalsIgnoreCase("E")) {
                                Log.e("pm", "pm7");
                                // Flag = "1";

                                Log.e("", "db_tester_id_array="
                                        + db_tester_id_array);

                                soap_update_tester_row = service
                                        .UpdateTableData(db_tester_id_array,
                                                "T", EmpId);
                                Log.e("", "soap_update_tester_row= "
                                        + soap_update_tester_row.toString());

                                SimpleDateFormat dateFormat = new SimpleDateFormat(
                                        "MM/dd/yyyy HH:mm:ss");
                                // get current date time with Date()
                                Calendar cal = Calendar.getInstance();
                                // dateFormat.format(cal.getTime())
                                db.open();
                                db.updateDateSync(
                                        dateFormat.format(cal.getTime()),
                                        "tester");
                                db.close();

                            } else if (soap_result_tester1
                                    .getProperty("status").toString()
                                    .equalsIgnoreCase("N")) {
                                soap_update_tester_row = service
                                        .UpdateTableData(db_tester_id_array,
                                                "T", EmpId);
                                Log.e("", "soap_update_tester_row= "
                                        + soap_update_tester_row.toString());
                                // Flag = "3";
                                synctesterdata = 1;
                            } else if (soap_result_tester1
                                    .getProperty("status").toString()
                                    .equalsIgnoreCase("SE")) {

                                soap_update_tester_row = service
                                        .UpdateTableData(db_tester_id_array,
                                                "T", EmpId);
                                Log.e("", "soap_update_tester_row= "
                                        + soap_update_tester_row.toString());
                                // Flag="2";
                                synctesterdata = 0;
                                final Calendar calendar = Calendar
                                        .getInstance();
                                SimpleDateFormat formatter = new SimpleDateFormat(
                                        "MM/dd/yyyy HH:mm:ss");
                                String Createddate = formatter.format(calendar
                                        .getTime());
                                Log.v("", "se error");
                                int n = Thread.currentThread().getStackTrace()[2]
                                        .getLineNumber();
                                db.open();
                                db.insertSyncLog("SyncGetTesterData_SE",
                                        String.valueOf(n),
                                        "SyncGetTesterData()", Createddate,
                                        Createddate,
                                        sp.getString("username", ""),
                                        "Data Download", "Fail");
                                db.close();
                            }
                        }

                    } else {
                        Log.v("", "Soap result is null");

                        synctesterdata = 0;
                        final Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter.format(calendar
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.open();
                        db.insertSyncLog("Soup is null - SyncGetTesterData()",
                                String.valueOf(n), "SyncGetTesterData()",
                                Createddate, Createddate,
                                sp.getString("username", ""), "Data Download",
                                "Fail");
                        db.close();

                    }

                }
                // -------------------------------//
                // ------------------------------------ boc - day wise
                // data-----------------------//

                db.open();
                String lastdatesync_boc_day = db
                        .getLastSyncDate("boc_wise_stock");
                db.close();

                soap_result_boc_day = service.SyncStockNetvalue(
                        sp.getString("username", ""), lastdatesync_boc_day);// 09.10.2015

                Log.e("pm", "pm1");
                if (soap_result_boc_day != null) {

                    for (int i = 0; i < soap_result_boc_day.getPropertyCount(); i++) {

                        soap_result_boc_day1 = (SoapObject) soap_result_boc_day
                                .getProperty(i);

                        Log.e("pm", "status="
                                + soap_result_boc_day1.getProperty("status")
                                .toString());

                        if (soap_result_boc_day1.getProperty("status")
                                .toString().equalsIgnoreCase("C")) {

                            // try {

                            String db_stock_id = soap_result_boc_day1
                                    .getProperty("Id").toString();

                            String db_Id = soap_result_boc_day1.getProperty(
                                    "ProductId").toString();

                            Log.v("", "db_Id=" + db_Id);

                            String CatCodeId = soap_result_boc_day1
                                    .getProperty("CatCodeId").toString();

                            if (CatCodeId == null) {
                                CatCodeId = "";

                            }
                            Log.v("", "CatCodeId=" + CatCodeId);

                            String ProductId = soap_result_boc_day1
                                    .getProperty("ProductId").toString();
                            Log.v("", "ProductId=" + ProductId);

                            if (ProductId == null) {
                                ProductId = "";

                            }

                            String EANCode = soap_result_boc_day1.getProperty(
                                    "EANCode").toString();

                            if (EANCode == null) {
                                EANCode = "";

                            }
                            String EmpId = soap_result_boc_day1.getProperty(
                                    "EmpId").toString();

                            if (EmpId == null) {
                                EmpId = "";

                            }
                            String ProductCategory = soap_result_boc_day1
                                    .getProperty("ProductCategory").toString();

                            if (ProductCategory == null) {
                                ProductCategory = "";

                            }
                            String ProductType = soap_result_boc_day1
                                    .getProperty("ProductType").toString();

                            if (ProductType == null) {
                                ProductType = "";

                            }
                            String ProductName = soap_result_boc_day1
                                    .getProperty("ProductName").toString();
                            if (ProductName == null) {
                                ProductName = "";

                            }

                            String ShadeNo = soap_result_boc_day1.getProperty(
                                    "ShadeNo").toString();
                            if (ShadeNo == null) {
                                ShadeNo = "";

                            }

                            String Opening_Stock = soap_result_boc_day1
                                    .getProperty("Opening_Stock").toString();
                            if (Opening_Stock == null) {
                                Opening_Stock = "";

                            }

                            String FreshStock = soap_result_boc_day1
                                    .getProperty("FreshStock").toString();
                            if (FreshStock == null) {
                                FreshStock = "";

                            }

                            String Stock_inhand = soap_result_boc_day1
                                    .getProperty("Stock_inhand").toString();

                            if (Stock_inhand == null) {
                                Stock_inhand = "";

                            }
                            String SoldStock = soap_result_boc_day1
                                    .getProperty("SoldStock").toString();

                            if (SoldStock == null) {
                                SoldStock = "";

                            }
                            String S_Return_Saleable = soap_result_boc_day1
                                    .getProperty("S_Return_Saleable")
                                    .toString();

                            if (S_Return_Saleable == null) {
                                S_Return_Saleable = "";

                            }
                            String S_Return_NonSaleable = soap_result_boc_day1
                                    .getProperty("S_Return_NonSaleable")
                                    .toString();

                            if (S_Return_NonSaleable == null) {
                                S_Return_NonSaleable = "";

                            }
                            String ClosingBal = soap_result_boc_day1
                                    .getProperty("ClosingBal").toString();

                            if (ClosingBal == null) {
                                ClosingBal = "";

                            }
                            String GrossAmount = soap_result_boc_day1
                                    .getProperty("GrossAmount").toString();

                            if (GrossAmount == null) {
                                GrossAmount = "";

                            }
                            String Discount = soap_result_boc_day1.getProperty(
                                    "Discount").toString();

                            if (Discount == null) {
                                Discount = "";

                            }
                            String NetAmount = soap_result_boc_day1
                                    .getProperty("NetAmount").toString();

                            if (NetAmount == null) {
                                NetAmount = "";

                            }
                            String Size = soap_result_boc_day1.getProperty(
                                    "Size").toString();

                            if (Size == null) {
                                Size = "";

                            }
                            String Price = soap_result_boc_day1.getProperty(
                                    "Price").toString();

                            if (Price == null) {
                                Price = "";

                            }
                            String LMD = soap_result_boc_day1
                                    .getProperty("LMD").toString();

                            if (LMD == null) {
                                LMD = "";

                            } else {

                                try {
                                    String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                    String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                    DateFormat inputFormat = new SimpleDateFormat(
                                            inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(
                                            outputPattern);

                                    Date date = inputFormat.parse(LMD);
                                    LMD = outputFormat.format(date);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            // ---

                            String AndroidCreatedDate = soap_result_boc_day1
                                    .getProperty("AndroidCreatedDate")
                                    .toString();

                            if (AndroidCreatedDate == null) {
                                AndroidCreatedDate = "";

                            } else {

                                try {
                                    String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                    String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                    DateFormat inputFormat = new SimpleDateFormat(
                                            inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(
                                            outputPattern);

                                    Date date = inputFormat
                                            .parse(AndroidCreatedDate);
                                    AndroidCreatedDate = outputFormat
                                            .format(date);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            String Date = soap_result_boc_day1.getProperty(
                                    "Date").toString();

                            if (Date == null) {
                                Date = "";

                            }
                            String BOC = soap_result_boc_day1
                                    .getProperty("BOC").toString();

                            if (BOC == null) {
                                BOC = "";

                            }

                            if (db_Id.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for db_Id");
                                db_Id = " ";
                            }
                            if (CatCodeId.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for CatCodeId");
                                CatCodeId = " ";
                            }

                            if (ProductId.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductId");
                                ProductId = " ";
                            }
                            if (EANCode.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for EANCode");
                                EANCode = " ";
                            }

                            if (EmpId.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for EmpId");
                                EmpId = " ";
                            }
                            if (ProductCategory.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductCategory");
                                ProductCategory = " ";
                            }
                            if (ProductType.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductType");
                                ProductType = " ";
                            }

                            if (ProductName.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductName");
                                ProductName = " ";
                            }
                            if (Opening_Stock.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Opening_Stock");
                                Opening_Stock = " ";
                            }

                            if (FreshStock.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for FreshStock");
                                FreshStock = " ";
                            }
                            if (Stock_inhand.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Stock_inhand");
                                Stock_inhand = " ";
                            }
                            if (SoldStock.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for SoldStock");
                                SoldStock = " ";
                            }
                            if (S_Return_Saleable.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for S_Return_Saleable");
                                S_Return_Saleable = " ";
                            }
                            if (S_Return_NonSaleable
                                    .equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for S_Return_NonSaleable");
                                S_Return_NonSaleable = " ";
                            }
                            if (ClosingBal.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ClosingBal");
                                ClosingBal = " ";
                            }
                            if (GrossAmount.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for GrossAmount");
                                GrossAmount = " ";
                            }
                            if (Discount.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Discount");
                                Discount = " ";
                            }
                            if (NetAmount.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for NetAmount");
                                NetAmount = " ";
                            }
                            if (Size.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Size");
                                Size = " ";
                            }
                            if (Price.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for sku_l");
                                Price = " ";
                            }
                            if (LMD.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for LMD");
                                LMD = " ";
                            }
                            if (AndroidCreatedDate
                                    .equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for AndroidCreatedDate");
                                AndroidCreatedDate = " ";
                            }

                            if (BOC.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for BOC");
                                BOC = " ";
                            }

                            Log.e("pm", "pm5--");
                            db.open();

                            Cursor c1 = db.CheckDataExistInBOCDayWise(
                                    "boc_wise_stock", db_Id, Date);

                            if (c1 != null) {
                                c1.moveToFirst();
                                int count = c1.getCount();
                                Log.v("", "" + count);
                                db.close();
                                if (count > 0) {

                                    db.open();
                                    db.UpdateBOCDAYStockSync(ProductCategory,
                                            ProductType, ProductName, EmpId,
                                            Stock_inhand, ClosingBal,
                                            FreshStock, GrossAmount, SoldStock,
                                            Price, Size, db_Id, LMD, Discount,
                                            NetAmount, Opening_Stock,
                                            S_Return_Saleable,
                                            S_Return_NonSaleable, Date, BOC);
                                    db.close();

                                    db_bocmonthstock_id_array = db_bocmonthstock_id_array
                                            + "," + db_stock_id;// 10.10.2015

                                } else {

                                    Log.e("pm", "pm5");
                                    db.open();
                                    db.insertBocDateWise(db_stock_id, db_Id,
                                            ProductId, CatCodeId, EANCode,
                                            EmpId, ProductCategory,
                                            ProductType, ProductName,
                                            Opening_Stock, FreshStock,
                                            Stock_inhand, SoldStock,
                                            S_Return_NonSaleable,
                                            S_Return_Saleable, ClosingBal,
                                            GrossAmount, Discount, NetAmount,
                                            Size, Price, LMD,
                                            AndroidCreatedDate, Date, BOC);
                                    db.close();

                                    Log.e("", "db_stock_id=" + db_stock_id
                                            + " empid=" + EmpId);
                                    db_bocmonthstock_id_array = db_bocmonthstock_id_array
                                            + "," + db_stock_id;// 10.10.2015

                                }

                            }

                        } else if (soap_result_boc_day1.getProperty("status")
                                .toString().equalsIgnoreCase("E")) {
                            Log.e("pm", "pm7");
                            // Flag = "1";

                            soap_update_boc_day_row = service.UpdateTableData(
                                    db_bocmonthstock_id_array, "N", EmpId);
                            Log.e("", "soap_update_boc_day_row= "
                                    + soap_update_boc_day_row.toString());

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            // get current date time with Date()
                            Calendar cal = Calendar.getInstance();
                            // dateFormat.format(cal.getTime())
                            db.open();
                            db.updateDateSync(dateFormat.format(cal.getTime()),
                                    "boc_wise_stock");
                            db.close();

                        } else if (soap_result_boc_day1.getProperty("status")
                                .toString().equalsIgnoreCase("N")) {

                            soap_update_boc_day_row = service.UpdateTableData(
                                    db_bocmonthstock_id_array, "N", EmpId);
                            Log.e("", "soap_update_boc_day_row= "
                                    + soap_update_boc_day_row.toString());

                            // Flag = "3";
                            syncbocdaywise = 1;
                        } else if (soap_result_boc_day1.getProperty("status")
                                .toString().equalsIgnoreCase("SE")) {

                            soap_update_boc_day_row = service.UpdateTableData(
                                    db_bocmonthstock_id_array, "N", EmpId);
                            Log.e("", "soap_update_boc_day_row= "
                                    + soap_update_boc_day_row.toString());

                            // Flag="2";
                            syncbocdaywise = 0;
                            final Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat formatter = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            String Createddate = formatter.format(calendar
                                    .getTime());
                            Log.v("", "se error");
                            int n = Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber();
                            db.open();
                            db.insertSyncLog("SyncStockNetvalue_SE",
                                    String.valueOf(n),
                                    "SyncStockNetvalue_SE()", Createddate,
                                    Createddate, sp.getString("username", ""),
                                    "Data Download", "Fail");
                            db.close();
                        }
                    }

                } else {
                    Log.v("", "Soap result is null");

                    syncbocdaywise = 0;

                    final Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "MM/dd/yyyy HH:mm:ss");
                    String Createddate = formatter.format(calendar.getTime());

                    int n = Thread.currentThread().getStackTrace()[2]
                            .getLineNumber();
                    db.open();
                    db.insertSyncLog("Soap is null - SyncStockNetvalue()",
                            String.valueOf(n), "SyncStockNetvalue()",
                            Createddate, Createddate,
                            sp.getString("username", ""), "Data Download",
                            "Fail");
                    db.close();
                }
                // ----------------------------stock
                // monthwise------------------------------------//

                db.open();
                String lastdatesync_stock_mothwise = db
                        .getLastSyncDate("stock_monthwise");
                db.close();

                soap_result_monthwise = service.SyncStockCummData(
                        sp.getString("username", ""),
                        lastdatesync_stock_mothwise);// 09.10.2015

                Log.e("pm", "pm1");
                if (soap_result_monthwise != null) {

                    for (int i = 0; i < soap_result_monthwise
                            .getPropertyCount(); i++) {

                        soap_result_monthwise1 = (SoapObject) soap_result_monthwise
                                .getProperty(i);

                        Log.e("pm", "status="
                                + soap_result_monthwise1.getProperty("status")
                                .toString());

                        if (soap_result_monthwise1.getProperty("status")
                                .toString().equalsIgnoreCase("C")) {


                            String db_stock_id = soap_result_monthwise1
                                    .getProperty("Id").toString();

                            String db_Id = soap_result_monthwise1.getProperty(
                                    "ProductId").toString();

                            Log.v("", "db_Id=" + db_Id);

                            String CatCodeId = soap_result_monthwise1
                                    .getProperty("CatCodeId").toString();

                            if (CatCodeId == null) {
                                CatCodeId = "";

                            }
                            Log.v("", "CatCodeId=" + CatCodeId);

                            String ProductId = soap_result_monthwise1
                                    .getProperty("ProductId").toString();
                            Log.v("", "ProductId=" + ProductId);

                            if (ProductId == null) {
                                ProductId = "";

                            }

                            String EANCode = soap_result_monthwise1
                                    .getProperty("EANCode").toString();

                            if (EANCode == null) {
                                EANCode = "";

                            }
                            EmpId = soap_result_monthwise1.getProperty("EmpId")
                                    .toString();

                            if (EmpId == null) {
                                EmpId = "";

                            }
                            String ProductCategory = soap_result_monthwise1
                                    .getProperty("ProductCategory").toString();

                            if (ProductCategory == null) {
                                ProductCategory = "";

                            }
                            String ProductType = soap_result_monthwise1
                                    .getProperty("ProductType").toString();

                            if (ProductType == null) {
                                ProductType = "";

                            }
                            String ProductName = soap_result_monthwise1
                                    .getProperty("ProductName").toString();
                            if (ProductName == null) {
                                ProductName = "";

                            }

                            String ShadeNo = soap_result_monthwise1
                                    .getProperty("ShadeNo").toString();
                            if (ShadeNo == null) {
                                ShadeNo = "";

                            }

                            String Opening_Stock = soap_result_monthwise1
                                    .getProperty("Opening_Stock").toString();
                            if (Opening_Stock == null) {
                                Opening_Stock = "";

                            }

                            String FreshStock = soap_result_monthwise1
                                    .getProperty("FreshStock").toString();
                            if (FreshStock == null) {
                                FreshStock = "";

                            }

                            String Stock_inhand = soap_result_monthwise1
                                    .getProperty("Stock_inhand").toString();

                            if (Stock_inhand == null) {
                                Stock_inhand = "";

                            }
                            String SoldStock = soap_result_monthwise1
                                    .getProperty("SoldStock").toString();

                            if (SoldStock == null) {
                                SoldStock = "";

                            }
                            String S_Return_Saleable = soap_result_monthwise1
                                    .getProperty("S_Return_Saleable")
                                    .toString();

                            if (S_Return_Saleable == null) {
                                S_Return_Saleable = "";

                            }
                            String S_Return_NonSaleable = soap_result_monthwise1
                                    .getProperty("S_Return_NonSaleable")
                                    .toString();

                            if (S_Return_NonSaleable == null) {
                                S_Return_NonSaleable = "";

                            }
                            String ClosingBal = soap_result_monthwise1
                                    .getProperty("ClosingBal").toString();

                            if (ClosingBal == null) {
                                ClosingBal = "";

                            }
                            String GrossAmount = soap_result_monthwise1
                                    .getProperty("GrossAmount").toString();

                            if (GrossAmount == null) {
                                GrossAmount = "";

                            }
                            String Discount = soap_result_monthwise1
                                    .getProperty("Discount").toString();

                            if (Discount == null) {
                                Discount = "";

                            }
                            String NetAmount = soap_result_monthwise1
                                    .getProperty("NetAmount").toString();

                            if (NetAmount == null) {
                                NetAmount = "";

                            }
                            String Size = soap_result_monthwise1.getProperty(
                                    "Size").toString();

                            if (Size == null) {
                                Size = "";

                            }
                            String Price = soap_result_monthwise1.getProperty(
                                    "Price").toString();

                            if (Price == null) {
                                Price = "";

                            }
                            String LMD = soap_result_monthwise1.getProperty(
                                    "LMD").toString();


                            if (LMD == null) {
                                LMD = "";

                            } else {

                                try {
                                    String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                    String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                    DateFormat inputFormat = new SimpleDateFormat(
                                            inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(
                                            outputPattern);

                                    Date date = inputFormat.parse(LMD);
                                    LMD = outputFormat.format(date);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            String month = soap_result_monthwise1.getProperty(
                                    "Month").toString();

                            if (month == null) {
                                month = "";

                            }

                            String AndroidCreatedDate = soap_result_monthwise1
                                    .getProperty("AndroidCreatedDate")
                                    .toString();


                            String MONTH = "", YEAR = "";
                            if (AndroidCreatedDate == null) {
                                AndroidCreatedDate = "";

                            } else {

                                try {
                                    String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                    String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                    SimpleDateFormat inputFormat = new SimpleDateFormat(
                                            inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(
                                            outputPattern);

                                    Date date = inputFormat
                                            .parse(AndroidCreatedDate);
                                    AndroidCreatedDate = outputFormat
                                            .format(date);

                                    String[] addd = AndroidCreatedDate
                                            .split(" ");
                                    String addd1 = addd[0];
                                    String[] addd2 = addd1.split("-");

                                    // String month = addd2[1];
                                    YEAR = addd2[0];

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                            if (db_Id.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for db_Id");
                                db_Id = " ";
                            }
                            if (CatCodeId.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for CatCodeId");
                                CatCodeId = " ";
                            }

                            if (ProductId.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductId");
                                ProductId = " ";
                            }
                            if (EANCode.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for EANCode");
                                EANCode = " ";
                            }

                            if (EmpId.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for EmpId");
                                EmpId = " ";
                            }
                            if (ProductCategory.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductCategory");
                                ProductCategory = " ";
                            }
                            if (ProductType.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductType");
                                ProductType = " ";
                            }

                            if (ProductName.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductName");
                                ProductName = " ";
                            }
                            if (Opening_Stock.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Opening_Stock");
                                Opening_Stock = " ";
                            }

                            if (FreshStock.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for FreshStock");
                                FreshStock = " ";
                            }
                            if (Stock_inhand.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Stock_inhand");
                                Stock_inhand = " ";
                            }
                            if (SoldStock.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for SoldStock");
                                SoldStock = " ";
                            }
                            if (S_Return_Saleable.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for S_Return_Saleable");
                                S_Return_Saleable = " ";
                            }
                            if (S_Return_NonSaleable
                                    .equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for S_Return_NonSaleable");
                                S_Return_NonSaleable = " ";
                            }
                            if (ClosingBal.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ClosingBal");
                                ClosingBal = " ";
                            }
                            if (GrossAmount.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for GrossAmount");
                                GrossAmount = " ";
                            }
                            if (Discount.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Discount");
                                Discount = " ";
                            }
                            if (NetAmount.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for NetAmount");
                                NetAmount = " ";
                            }
                            if (Size.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Size");
                                Size = " ";
                            }
                            if (Price.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for sku_l");
                                Price = " ";
                            }
                            if (LMD.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for LMD");
                                LMD = " ";
                            }
                            if (AndroidCreatedDate
                                    .equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for AndroidCreatedDate");
                                AndroidCreatedDate = " ";
                            }

                            if (month.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for month");
                                month = " ";
                            }

                            // db_cumvalueid_array = db_stock_id;
                            Log.e("pm", "pm5--");

                            db.open();
                            db.insertBocMonthWise(db_stock_id, db_Id,
                                    ProductId, CatCodeId, EANCode, EmpId,
                                    ProductCategory, ProductType, ProductName,
                                    Opening_Stock, FreshStock, Stock_inhand,
                                    SoldStock, S_Return_NonSaleable,
                                    S_Return_Saleable, ClosingBal, GrossAmount,
                                    Discount, NetAmount, Size, Price, LMD,
                                    AndroidCreatedDate, month, YEAR);
                            db.close();

                            Log.e("", "db_stock_id=" + db_stock_id + " empid="
                                    + EmpId);

                            db_cumvalueid_array = db_cumvalueid_array + ","
                                    + db_stock_id;

                        } else if (soap_result_monthwise1.getProperty("status")
                                .toString().equalsIgnoreCase("E")) {
                            Log.e("pm", "pm7");
                            // Flag = "1";

                            soap_update_monthwise_row = service
                                    .UpdateTableData(db_cumvalueid_array, "C",
                                            EmpId);
                            Log.e("", "soap_update_monthwise_row= "
                                    + soap_update_monthwise_row.toString());

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            // get current date time with Date()
                            Calendar cal = Calendar.getInstance();
                            // dateFormat.format(cal.getTime())
                            db.open();
                            db.updateDateSync(dateFormat.format(cal.getTime()),
                                    "stock_monthwise");
                            db.close();

                        } else if (soap_result_monthwise1.getProperty("status")
                                .toString().equalsIgnoreCase("N")) {

                            soap_update_monthwise_row = service
                                    .UpdateTableData(db_cumvalueid_array, "C",
                                            EmpId);
                            Log.e("", "soap_update_monthwise_row= "
                                    + soap_update_monthwise_row.toString());

                            syncbocmonthwise = 1;
                            // Flag = "3";
                        } else if (soap_result_monthwise1.getProperty("status")
                                .toString().equalsIgnoreCase("SE")) {

                            soap_update_monthwise_row = service
                                    .UpdateTableData(db_cumvalueid_array, "C",
                                            EmpId);
                            Log.e("", "soap_update_monthwise_row= "
                                    + soap_update_monthwise_row.toString());

                            // Flag="2";
                            syncbocmonthwise = 0;
                            final Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat formatter = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            String Createddate = formatter.format(calendar
                                    .getTime());
                            Log.v("", "se error");
                            int n = Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber();
                            db.open();
                            db.insertSyncLog("SyncStockCummData_SE",
                                    String.valueOf(n),
                                    "SyncStockCummData_SE()", Createddate,
                                    Createddate, sp.getString("username", ""),
                                    "Data Download", "Fail");
                            db.close();
                        }
                    }

                } else {

                    Log.v("", "Soap result is null");

                    syncbocmonthwise = 0;
                    final Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "MM/dd/yyyy HH:mm:ss");
                    String Createddate = formatter.format(calendar.getTime());

                    int n = Thread.currentThread().getStackTrace()[2]
                            .getLineNumber();
                    db.open();
                    db.insertSyncLog("Soup is null - SyncStockCummData()",
                            String.valueOf(n), "SyncStockCummData()",
                            Createddate, Createddate,
                            sp.getString("username", ""), "Data Download",
                            "Fail");
                    db.close();
                }

                if (syncbocdaywise == 1 && syncbocmonthwise == 1
                        && syncstockdata == 1 && synctesterdata == 1) {

                    Flag = "1";
                } else {

                    Flag = "2";
                }

            } catch (Exception e) {

                e.printStackTrace();
                String Error = e.toString();

                final Calendar calendar = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "MM/dd/yyyy HH:mm:ss");
                String Createddate = formatter.format(calendar.getTime());
                Flag = "4";
                int n = Thread.currentThread().getStackTrace()[2]
                        .getLineNumber();
                db.open();
                db.insertSyncLog(Error, String.valueOf(n), "", Createddate,
                        Createddate, sp.getString("username", ""),
                        "Data Download", "Fail");
                db.close();

            }

            return soap_result;
        }


        @Override
        protected void onPostExecute(SoapObject result) {

            // TODO Auto-generated method stub
            // super.onPostExecute(result);

            db.close();
            mProgress.dismiss();

            if (Flag.equalsIgnoreCase("0")) {

                DisplayDialogMessage("Check Your Internet Connection!!!");

            } else if (Flag.equalsIgnoreCase("1")) {

                boolean boc26 = false;
                boolean bocflag = true;
                spe.putBoolean("BOC26", boc26);
                spe.putBoolean("Bocflag", bocflag);
                spe.putBoolean("DialogDismiss", true);
                spe.commit();
                //final boolean boolRecd = false;

                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardNewActivity.this);
                builder.setMessage("Data Download Completed Successfully!!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();

                                //disableBroadcastReceiver();

                                db.open();
                                String a = db.getdatepresentorabsent(sp.getString("todaydate", ""), username = sp.getString("username", ""));
                                db.close();

                                if (!a.equalsIgnoreCase("")) {
                                    Intent i = new Intent(DashboardNewActivity.this, DashboardNewActivity.class);
                                    //i.putExtra("Boc26",boolRecd);
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(DashboardNewActivity.this, AttendanceFragment.class);
                                    i.putExtra("FromLoginpage", "L");
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                }
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            } else if (Flag.equalsIgnoreCase("2")) {

               /* boolean bocflag = true;
                spe.putBoolean("Bocflag", bocflag);
                spe.putBoolean("DialogDismiss", true);
                spe.commit();*/

                // DisplayDialogMessage("Data Download Incomplete!!");
                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardNewActivity.this);
                builder.setMessage("Data Download Incomplete!!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();

                                BocOpeningDialog();

                             /*   db.open();
                                String a = db.getdatepresentorabsent(sp.getString("todaydate", ""), username = sp.getString("username", ""));
                                db.close();

                                if (!a.equalsIgnoreCase("")) {
                                    Intent i = new Intent(DashboardNewActivity.this, DashboardNewActivity.class);
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(DashboardNewActivity.this, AttendanceFragment.class);
                                    i.putExtra("FromLoginpage", "L");
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                }*/
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            } else if (Flag.equalsIgnoreCase("4")) {

               /* boolean bocflag = true;
                spe.putBoolean("Bocflag", bocflag);
                spe.putBoolean("DialogDismiss", true);
                spe.commit();*/

                // DisplayDialogMessage("Data Download Incomplete!!Please try again");

                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardNewActivity.this);
                builder.setMessage("Data Download Incomplete!!Please try again")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();

                                BocOpeningDialog();

                               /* db.open();
                                String a = db.getdatepresentorabsent(sp.getString("todaydate", ""), username = sp.getString("username", ""));
                                db.close();

                                if (!a.equalsIgnoreCase("")) {
                                    Intent i = new Intent(DashboardNewActivity.this, DashboardNewActivity.class);
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(DashboardNewActivity.this, AttendanceFragment.class);
                                    i.putExtra("FromLoginpage", "L");
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                }*/
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            } else {

              /*  boolean bocflag = true;
                spe.putBoolean("Bocflag", bocflag);
                spe.putBoolean("DialogDismiss", true);
                spe.commit();*/

                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardNewActivity.this);
                builder.setMessage("Data Download Incomplete!!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();

                                BocOpeningDialog();

                               /* db.open();
                                String a = db.getdatepresentorabsent(sp.getString("todaydate", ""), username = sp.getString("username", ""));
                                db.close();

                                if (!a.equalsIgnoreCase("")) {
                                    Intent i = new Intent(DashboardNewActivity.this, DashboardNewActivity.class);
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(DashboardNewActivity.this, AttendanceFragment.class);
                                    i.putExtra("FromLoginpage", "L");
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                }*/
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }

        }

        private void DisplayDialogMessage(String msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardNewActivity.this);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


    }

    public class InsertProductMaster extends AsyncTask<Void, Void, SoapObject> {

        private SoapObject soap_result = null;

        private SoapObject soap_result_tester = null;

        SoapObject soap_result1 = null;
        SoapObject soap_result_tester1 = null;

        String Flag;

        @SuppressLint("NewApi")
        @Override
        protected SoapObject doInBackground(Void... params) {

            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                // Toast.makeText(SyncMaster.this(),"Check Your Internet Connection!!!",
                // Toast.LENGTH_LONG).show();

                Flag = "0";
                // stop executing code by return

            } else {
                Flag = "1";
                // TODO Auto-generated method stub
                Log.e("pm", "pm0");
                db.open();
                String lastdatesync = db.getLastSyncDate("product_master");
                db.close();

                Log.e("pm", "lastdatesync=" + lastdatesync);

                soap_result = service.GetProducts(lastdatesync,
                        sp.getString("username", ""));
                // ,);

                Log.e("pm", "pm1");
                if (soap_result != null) {

                    Log.e("pm",
                            "count================="
                                    + soap_result.getPropertyCount());

                    for (int i = 0; i < soap_result.getPropertyCount(); i++) {

                        soap_result1 = (SoapObject) soap_result.getProperty(i);

                        Log.e("pm", "pm3");

                        if (soap_result1.getProperty("Flag") != null) {

                            if (soap_result1.getProperty("Flag").toString()
                                    .equalsIgnoreCase("E")) {
                                Log.e("pm", "pm4");
                                try {

                                    String d_id = soap_result1
                                            .getProperty("ID").toString();

                                    String product_category = soap_result1
                                            .getProperty("ProductCategory")
                                            .toString();
                                    String product_type = soap_result1
                                            .getProperty("ProductType")
                                            .toString();
                                    String product = soap_result1.getProperty(
                                            "ProductName").toString();

                                    String categoryid = soap_result1
                                            .getProperty("CategoryId")
                                            .toString();
                                    String category = soap_result1.getProperty(
                                            "Category").toString();
                                    String shadeno = soap_result1.getProperty(
                                            "ShadeNo").toString();
                                    String eancode = soap_result1.getProperty(
                                            "EANCode").toString();
                                    String size = soap_result1.getProperty(
                                            "Size").toString();
                                    String mrp = soap_result1
                                            .getProperty("MRP").toString();
                                    String masterpackqty = soap_result1
                                            .getProperty("MasterPackQty")
                                            .toString();
                                    String monopackqty = soap_result1
                                            .getProperty("MonoPackQty")
                                            .toString();
                                    String innerqty = soap_result1.getProperty(
                                            "InnerQty").toString();
                                    String sku_l = soap_result1.getProperty(
                                            "SKU_L").toString();
                                    String sku_b = soap_result1.getProperty(
                                            "SKU_B").toString();
                                    String sku_h = soap_result1.getProperty(
                                            "SKU_H").toString();
                                    String price_type = soap_result1
                                            .getPropertyAsString("OldNewStatus")
                                            .toString();
                                    String order_flag = soap_result1
                                            .getPropertyAsString("order_flag")
                                            .toString();
                                    String SingleOffer = soap_result1
                                            .getPropertyAsString("SingleOffer")
                                            .toString();
                                    // String lmd =
                                    // soap_result1.getProperty("LMD").toString();
                                    String flag = soap_result1.getProperty(
                                            "Flag").toString();

                                    if (d_id.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for d_id");
                                        d_id = " ";
                                    }
                                    if (product_category
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for product");
                                        product_category = " ";
                                    }
                                    if (product_type
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for producttype");
                                        product_type = " ";
                                    }
                                    if (product.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for product");
                                        product = " ";
                                    }
                                    if (categoryid
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for categoryid");
                                        categoryid = " ";
                                    }
                                    if (category.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for category");
                                        category = " ";
                                    }
                                    if (shadeno.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for shadeno");
                                        shadeno = " ";
                                    }
                                    if (eancode.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for eancode");
                                        eancode = " ";
                                    }
                                    if (size.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for size");
                                        size = " ";
                                    }
                                    if (mrp.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for mrp");
                                        mrp = " ";
                                    }
                                    if (masterpackqty
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for masterpackqty");
                                        masterpackqty = " ";
                                    }
                                    if (monopackqty
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for monopackqty");
                                        monopackqty = " ";
                                    }
                                    if (innerqty.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for innerqty");
                                        innerqty = " ";
                                    }
                                    if (sku_l.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_l");
                                        sku_l = " ";
                                    }
                                    if (sku_b.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_b");
                                        sku_b = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }
                                    if (SingleOffer.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_h");
                                        SingleOffer = " ";
                                    }

                                    if (d_id.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for d_id");
                                        d_id = " ";
                                    }
                                    if (product_category
                                            .equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for product");
                                        product_category = " ";
                                    }
                                    if (product_type.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for producttype");
                                        product_type = " ";
                                    }
                                    if (product.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for product");
                                        product = " ";
                                    }
                                    if (categoryid.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for categoryid");
                                        categoryid = " ";
                                    }
                                    if (category.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for category");
                                        category = " ";
                                    }
                                    if (shadeno.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for shadeno");
                                        shadeno = " ";
                                    }
                                    if (eancode.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for eancode");
                                        eancode = " ";
                                    }
                                    if (size.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for size");
                                        size = " ";
                                    }
                                    if (mrp.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for mrp");
                                        mrp = " ";
                                    }
                                    if (masterpackqty.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for masterpackqty");
                                        masterpackqty = " ";
                                    }
                                    if (monopackqty.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for monopackqty");
                                        monopackqty = " ";
                                    }
                                    if (innerqty.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for innerqty");
                                        innerqty = " ";
                                    }
                                    if (sku_l.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_l");
                                        sku_l = " ";
                                    }
                                    if (sku_b.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_b");
                                        sku_b = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }
                                    if (SingleOffer.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_h");
                                        SingleOffer = " ";
                                    }
                                    /*
                                     * if (order_flag!=null ||
                                     * order_flag.equalsIgnoreCase("NULL")) {
                                     * Log.e("", "anytype for sku_h");
                                     * order_flag = " "; }
                                     */

                                    Log.v("", "flag=" + flag);
                                    // if (flag.equalsIgnoreCase("E")) {

                                    Log.e("pm", "pm5--");
                                    db.open();
                                    Cursor c = db.getuniquedata(categoryid,
                                            eancode, d_id, "product_master");

                                    int count = c.getCount();
                                    Log.v("", "" + count);
                                    db.close();
                                    if (count > 0) {

                                        Log.v("", "data already available");

                                        db.open();
                                        db.UpdateProductMaster(
                                                product_category,
                                                product_type.trim(), product,
                                                d_id, categoryid, category,
                                                shadeno, eancode, size, mrp,
                                                masterpackqty, monopackqty,
                                                innerqty, sku_l, sku_b, sku_h,
                                                price_type, order_flag, SingleOffer);
                                        db.close();

                                    } else {

                                        Log.e("pm", "pm5");

                                        db.open();
                                        db.insertProductMaster(
                                                product_category,
                                                product_type.trim(), product,
                                                d_id, categoryid, category,
                                                shadeno, eancode, size, mrp,
                                                masterpackqty, monopackqty,
                                                innerqty, sku_l, sku_b, sku_h,
                                                price_type, order_flag, SingleOffer);
                                        db.close();

                                    }

                                    // }

                                } catch (Exception e) {
                                    // TODO: handle exception
                                    e.printStackTrace();
                                    String error = e.toString();
                                    final Calendar calendar1 = Calendar
                                            .getInstance();
                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                            "MM/dd/yyyy HH:mm:ss");
                                    String Createddate = formatter1
                                            .format(calendar1.getTime());

                                    int n = Thread.currentThread()
                                            .getStackTrace()[2].getLineNumber();
                                    db.insertSyncLog(error, String.valueOf(n),
                                            "GetProducts()", Createddate,
                                            Createddate,
                                            sp.getString("username", ""),
                                            "GetProducts", "Fail");
                                }

                            } else if (soap_result1.getProperty("Flag")
                                    .toString().equalsIgnoreCase("D")) {

                                Log.e("pm", "pm6");
                                db.open();
                                db.deletproductmaster("", "", soap_result1
                                                .getProperty("ID").toString(),
                                        "product_master");
                                db.close();

                            }
                        } else if (soap_result1.getProperty("status").toString()
                                .equalsIgnoreCase("C")) {
                            Log.e("pm", "pm7");

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            // get current date time with Date()
                            Calendar cal = Calendar.getInstance();
                            // dateFormat.format(cal.getTime())
                            db.open();
                            db.updateDateSync(dateFormat.format(cal.getTime()),
                                    "product_master");
                            db.close();

                        } else if (soap_result1.getProperty("status")
                                .toString().equalsIgnoreCase("SE")) {
                            Log.e("pm", "pm7");

                            final Calendar calendar1 = Calendar.getInstance();
                            SimpleDateFormat formatter1 = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            String Createddate = formatter1.format(calendar1
                                    .getTime());

                            int n = Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber();
                            db.insertSyncLog("GetProducts_SE",
                                    String.valueOf(n), "GetProducts()",
                                    Createddate, Createddate,
                                    sp.getString("username", ""),
                                    "GetProducts", "Fail");
                        }
                    }

                } else {
                    Log.v("", "Soap result is null");
                    // Toast.makeText(context,
                    // "Data Not Available or Check Connectivity",
                    // Toast.LENGTH_SHORT).show();

                    final Calendar calendar1 = Calendar.getInstance();
                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                            "MM/dd/yyyy HH:mm:ss");
                    String Createddate = formatter1.format(calendar1.getTime());

                    int n = Thread.currentThread().getStackTrace()[2]
                            .getLineNumber();
                    db.insertSyncLog(
                            "Internet Connection Lost, Soap in giving null while 'GetProducts'",
                            String.valueOf(n), "GetProducts()", Createddate,
                            Createddate, sp.getString("username", ""),
                            "GetProducts", "Fail");
                }

                db.open();
                String lastdatesyncdate_tester = db
                        .getLastSyncDate("tester_master");
                db.close();
                Log.e("pm", "lastdatesyncdate_tester="
                        + lastdatesyncdate_tester);

                soap_result_tester = service.GetTesterProducts(
                        lastdatesyncdate_tester, sp.getString("username", ""));
                // ,);

                Log.e("pm", "pm1");
                if (soap_result_tester != null) {

                    for (int i = 0; i < soap_result_tester.getPropertyCount(); i++) {
                        Log.e("pm", "pm2");

                        soap_result_tester1 = (SoapObject) soap_result_tester
                                .getProperty(i);

                        Log.e("pm", "pm3");

                        if (soap_result_tester1.getProperty("Flag") != null) {

                            if (soap_result_tester1.getProperty("Flag")
                                    .toString().equalsIgnoreCase("E")) {
                                Log.e("pm", "pm4");
                                try {

                                    String server_db_id = soap_result_tester1
                                            .getProperty("ID").toString();
                                    String product_id = soap_result_tester1
                                            .getPropertyAsString("ProductID");

                                    String product_category = soap_result_tester1
                                            .getProperty("ProductCategory")
                                            .toString();
                                    String product_type = soap_result_tester1
                                            .getProperty("ProductType")
                                            .toString();
                                    String product = soap_result_tester1
                                            .getProperty("ProductName")
                                            .toString();

                                    String categoryid = soap_result_tester1
                                            .getProperty("CategoryId")
                                            .toString();
                                    String category = soap_result_tester1
                                            .getProperty("Category").toString();
                                    String shadeno = soap_result_tester1
                                            .getProperty("ShadeNo").toString();
                                    String eancode = soap_result_tester1
                                            .getProperty("EANCode").toString();
                                    String size = soap_result_tester1
                                            .getProperty("Size").toString();
                                    String mrp = soap_result_tester1
                                            .getProperty("MRP").toString();
                                    String masterpackqty = soap_result_tester1
                                            .getProperty("MasterPackQty")
                                            .toString();
                                    String monopackqty = soap_result_tester1
                                            .getProperty("MonoPackQty")
                                            .toString();
                                    String innerqty = soap_result_tester1
                                            .getProperty("InnerQty").toString();
                                    String sku_l = soap_result_tester1
                                            .getProperty("SKU_L").toString();
                                    String sku_b = soap_result_tester1
                                            .getProperty("SKU_B").toString();
                                    String sku_h = soap_result_tester1
                                            .getProperty("SKU_H").toString();
                                    String price_type = soap_result_tester1
                                            .getPropertyAsString("OldNewStatus")
                                            .toString();
                                    // String lmd =
                                    // soap_result1.getProperty("LMD").toString();
                                    String flag = soap_result_tester1
                                            .getProperty("Flag").toString();

                                    if (server_db_id
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for server_db_id");
                                        server_db_id = " ";
                                    }

                                    if (product_id
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for server_db_id");
                                        product_id = " ";
                                    }

                                    if (product_category
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for product");
                                        product_category = " ";
                                    }
                                    if (product_type
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for producttype");
                                        product_type = " ";
                                    }
                                    if (product.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for product");
                                        product = " ";
                                    }
                                    if (categoryid
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for categoryid");
                                        categoryid = " ";
                                    }
                                    if (category.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for category");
                                        category = " ";
                                    }
                                    if (shadeno.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for shadeno");
                                        shadeno = " ";
                                    }
                                    if (eancode.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for eancode");
                                        eancode = " ";
                                    }
                                    if (size.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for size");
                                        size = " ";
                                    }
                                    if (mrp.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for mrp");
                                        mrp = " ";
                                    }
                                    if (masterpackqty
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for masterpackqty");
                                        masterpackqty = " ";
                                    }
                                    if (monopackqty
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for monopackqty");
                                        monopackqty = " ";
                                    }
                                    if (innerqty.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for innerqty");
                                        innerqty = " ";
                                    }
                                    if (sku_l.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_l");
                                        sku_l = " ";
                                    }
                                    if (sku_b.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_b");
                                        sku_b = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }

                                    if (server_db_id.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for server_db_id");
                                        server_db_id = " ";
                                    }
                                    if (product_id.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for product_id");
                                        product_id = " ";
                                    }
                                    if (product_category
                                            .equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for product");
                                        product_category = " ";
                                    }
                                    if (product_type.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for producttype");
                                        product_type = " ";
                                    }
                                    if (product.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for product");
                                        product = " ";
                                    }
                                    if (categoryid.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for categoryid");
                                        categoryid = " ";
                                    }
                                    if (category.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for category");
                                        category = " ";
                                    }
                                    if (shadeno.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for shadeno");
                                        shadeno = " ";
                                    }
                                    if (eancode.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for eancode");
                                        eancode = " ";
                                    }
                                    if (size.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for size");
                                        size = " ";
                                    }
                                    if (mrp.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for mrp");
                                        mrp = " ";
                                    }
                                    if (masterpackqty.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for masterpackqty");
                                        masterpackqty = " ";
                                    }
                                    if (monopackqty.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for monopackqty");
                                        monopackqty = " ";
                                    }
                                    if (innerqty.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for innerqty");
                                        innerqty = " ";
                                    }
                                    if (sku_l.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_l");
                                        sku_l = " ";
                                    }
                                    if (sku_b.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_b");
                                        sku_b = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }

                                    Log.v("", "flag=" + flag);
                                    if (flag.equalsIgnoreCase("E")) {

                                        Log.e("pm", "pm5--");
                                        db.open();
                                        Cursor c = db.getuniquedata(categoryid,
                                                eancode, product_id,
                                                "tester_master");

                                        int count = c.getCount();
                                        Log.v("", "" + count);
                                        db.close();
                                        if (count > 0) {

                                            Log.v("", "data already available");

                                            db.open();
                                            db.UpdateTesterMaster(
                                                    product_category,
                                                    product_type.trim(),
                                                    product, server_db_id,
                                                    product_id, categoryid,
                                                    category, shadeno, eancode,
                                                    size, mrp, masterpackqty,
                                                    monopackqty, innerqty,
                                                    sku_l, sku_b, sku_h,
                                                    price_type);
                                            db.close();

                                        } else {

                                            Log.e("pm", "pm5");
                                            db.open();
                                            db.insertTesterMaster(
                                                    product_category,
                                                    product_type.trim(),
                                                    product, server_db_id,
                                                    product_id, categoryid,
                                                    category, shadeno, eancode,
                                                    size, mrp, masterpackqty,
                                                    monopackqty, innerqty,
                                                    sku_l, sku_b, sku_h,
                                                    price_type);
                                            db.close();

                                        }

                                    }

                                } catch (Exception e) {
                                    // TODO: handle exception
                                    e.printStackTrace();
                                    String error = e.toString();
                                    final Calendar calendar1 = Calendar
                                            .getInstance();
                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                            "MM/dd/yyyy HH:mm:ss");
                                    String Createddate = formatter1
                                            .format(calendar1.getTime());

                                    int n = Thread.currentThread()
                                            .getStackTrace()[2].getLineNumber();
                                    db.insertSyncLog(error, String.valueOf(n),
                                            "GetTesterProducts()", Createddate,
                                            Createddate,
                                            sp.getString("username", ""),
                                            "GetTesterProducts()", "Fail");
                                }

                            } else if (soap_result_tester1.getProperty("Flag")
                                    .toString().equalsIgnoreCase("D")) {

                                Log.e("pm", "pm6");
                                db.open();
                                db.deletproductmaster("", "",
                                        soap_result_tester1.getProperty("ID")
                                                .toString(), "tester_master");
                                db.close();

                            }
                        } else if (soap_result_tester1.getProperty("status")
                                .toString().equalsIgnoreCase("C")) {
                            Log.e("pm", "pm7");

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            // get current date time with Date()
                            Calendar cal = Calendar.getInstance();
                            // dateFormat.format(cal.getTime())
                            db.open();
                            db.updateDateSync(dateFormat.format(cal.getTime()),
                                    "tester_master");
                            db.close();

                        } else if (soap_result_tester1.getProperty("status")
                                .toString().equalsIgnoreCase("SE")) {
                            Log.e("pm", "pm7");

                            final Calendar calendar1 = Calendar.getInstance();
                            SimpleDateFormat formatter1 = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            String Createddate = formatter1.format(calendar1
                                    .getTime());

                            int n = Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber();
                            db.insertSyncLog("GetTesterProducts_SE",
                                    String.valueOf(n), "GetTesterProducts()",
                                    Createddate, Createddate,
                                    sp.getString("username", ""),
                                    "GetTesterProducts()", "Fail");
                        }
                    }

                } else {
                    Log.v("", "Soap result is null");
                    // Toast.makeText(context,
                    // "Data Not Available or Check Connectivity",
                    // Toast.LENGTH_SHORT).show();

                    final Calendar calendar1 = Calendar.getInstance();
                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                            "MM/dd/yyyy HH:mm:ss");
                    String Createddate = formatter1.format(calendar1.getTime());

                    int n = Thread.currentThread().getStackTrace()[2]
                            .getLineNumber();
                    db.insertSyncLog(
                            "Internet Connection Lost, Soap in giving null while 'GetTesterProducts'",
                            String.valueOf(n), "GetTesterProducts()",
                            Createddate, Createddate,
                            sp.getString("username", ""),
                            "GetTesterProducts()", "Fail");
                }
            }
            return soap_result;
        }

        @Override
        protected void onPostExecute(SoapObject result) {

            // TODO Auto-generated method stub
            super.onPostExecute(result);

            mProgress.dismiss();
            if (Flag.equalsIgnoreCase("0")) {

                // DisplayDialogMessage("Check Your Internet Connection!!!");
                /*
                Toast.makeText(SyncMaster.this,
						"Check Your Internet Connection!!!", Toast.LENGTH_LONG)
						.show();*/
            } else {
                if (soap_result == null) {

                    // DisplayDialogMessage("Master Data Sync Incomplete, Please try again!!");
                    /*Toast.makeText(context,
                            "Master Data Sync Incomplete, Please try again!!",
							Toast.LENGTH_SHORT).show();*/

                } else if (soap_result1.getProperty("status").toString()
                        .equalsIgnoreCase("C")) {

                    new InsertFirstTimeMaster().execute();
                    //DisplayDialogMessage("Master Data Completed Successfully!!");

				/*	Toast.makeText(context,
							"Master Data Completed Successfully!!",
							Toast.LENGTH_SHORT).show();*/

                } else if (soap_result1.getProperty("status").toString()
                        .equalsIgnoreCase("SE")) {

                    // DisplayDialogMessage("Master Data Sync Incomplete please try again!!");
					/*Toast.makeText(context,
							"Master Data Sync Incomplete please try again!!",
							Toast.LENGTH_SHORT).show();*/
                }

            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mProgress.setMessage("Receiving.....");
            mProgress.show();
            mProgress.setCancelable(false);
        }

    }

    private void ClearLocalAppData() {
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

            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeStringAsFile(String fileContents) {
        Context context1 = mContext;
        try {
            File root;
            root = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "sudesi" + File.separator);

            root.mkdirs();

            FileWriter out = new FileWriter(new File(root, "sample.text"));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
            // Logger.logError(TAG, e);
        }

    }

    /**
     * This method enables the Broadcast receiver registered in the AndroidManifest file.
     */
    public void disableBroadcastReceiver() {
        ComponentName receiver = new ComponentName(this, BocBroadcastReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        spe.putString("Boardcast", "Disable");
        spe.commit();
        Toast.makeText(this, "Disabled broadcst receiver", Toast.LENGTH_SHORT).show();
    }

    private void BocOpeningDialog() {
        try {
            alertDialogBuilder = new AlertDialog.Builder(DashboardNewActivity.this);
            // set title
            alertDialogBuilder.setTitle("SYNC DATA ALERT");
            // set dialog message
            alertDialogBuilder
                    .setMessage(
                            "Welcome to New Boc Press 'OK' for Opening!!")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    spe.putBoolean("BOC26", false);
                                    spe.commit();
                                    boolRecd = false;
                                    // ClearLocalAppData();
                                    db.open();
                                    Cursor c = db.fetchallOrder("product_master", null, null);
                                    if (c.getCount() > 0) {
                                        new InsertFirstTimeMaster().execute();
                                    } else {
                                        new InsertProductMaster().execute();
                                    }
                                    //db.close();

                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
