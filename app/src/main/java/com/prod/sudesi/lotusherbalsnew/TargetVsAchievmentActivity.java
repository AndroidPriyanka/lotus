package com.prod.sudesi.lotusherbalsnew;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.adapter.BAReportAdapter;
import com.prod.sudesi.lotusherbalsnew.adapter.BAReportAdapterDubai;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

public class TargetVsAchievmentActivity extends Activity {

    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;

    Context context;

    //private ProgressDialog prgdialog;

    private BAReportAdapter adapter;

    private BAReportAdapterDubai adapter1;

    LotusWebservice service;

    ListView lv_ba_report;

    static public ArrayList<HashMap<String, String>> todaymessagelist = new ArrayList<HashMap<String, String>>();

    TextView tv_h_username;
    Button btn_home, btn_logout;
    String username, bdename, role, outletcode;

    TextView tv_current_year_n1, tv_current_year_n2, tv_previous_year_p1, tv_previous_year_p2, tvPreviousyear, tvCurrentyear;

    String current_year_n1, current_year_n2, previous_year_p1, previous_year_p2;

    int int_current_year_n1, int_current_year_n2, int_previous_year_p1, int_previous_year_p2;

    String current_server_date;

    ConnectionDetector cd;

    //LinearLayout colorskinlayout;
    TableRow yearlayoutdubai, yearlayout;

    String PreviousYear, CurrentYear;

    TextView firstPAtxt,secondPAtxt,firstCTgtxt,secondCTgtxt,firstCAtxt,secondCAtxt,
            firstCGtxt,secondCGtxt,firstPAtxt1,firstCTgtxt1,firstCAtxt1,firstCGtxt1;
    private ArrayList<String> categoryDetailsArraylist;
    private Dbcon db;

    TableLayout onedivlayout, twodivlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_targetvsachivement);
        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        context = TargetVsAchievmentActivity.this;

        //prgdialog = new ProgressDialog(context);
        service = new LotusWebservice(TargetVsAchievmentActivity.this);
        cd = new ConnectionDetector(TargetVsAchievmentActivity.this);

        shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
        shpeditor = shp.edit();

        db = new Dbcon(TargetVsAchievmentActivity.this);

        role = shp.getString("Role", "");
        outletcode = shp.getString("FLRCode", "");

        lv_ba_report = (ListView) findViewById(R.id.listView_ba_year_report);

        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        yearlayoutdubai = (TableRow) findViewById(R.id.yearlayoutdubai);
        //colorskinlayout = (LinearLayout) findViewById(R.id.colorskinlayout);
        yearlayout = (TableRow) findViewById(R.id.yearlayout);

        if (role.equalsIgnoreCase("DUB")) {
            yearlayout.setVisibility(View.GONE);
            //colorskinlayout.setVisibility(View.GONE);
            yearlayoutdubai.setVisibility(View.VISIBLE);
        }

        username = shp.getString("username", "");
        Log.v("", "username==" + username);

        bdename = shp.getString("BDEusername", "");

        tv_h_username.setText(bdename);

        btn_logout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btn_home.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent i = new Intent(getApplicationContext(), DashboardNewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                //startActivity(new Intent(getApplicationContext(), DashboardNewActivity.class));
            }
        });
        //---------------------

        firstPAtxt = (TextView) findViewById(R.id.firstPAtxt);
        secondPAtxt = (TextView) findViewById(R.id.secondPAtxt);
        firstCTgtxt = (TextView) findViewById(R.id.firstCTgtxt);
        secondCTgtxt = (TextView) findViewById(R.id.secondCTgtxt);
        firstCAtxt = (TextView) findViewById(R.id.firstCAtxt);
        secondCAtxt = (TextView) findViewById(R.id.secondCAtxt);
        firstCGtxt = (TextView) findViewById(R.id.firstCGtxt);
        secondCGtxt = (TextView) findViewById(R.id.secondCGtxt);
        firstPAtxt1 = (TextView) findViewById(R.id.firstPAtxt1);
        firstCTgtxt1 = (TextView) findViewById(R.id.firstCTgtxt1);
        firstCAtxt1 = (TextView) findViewById(R.id.firstCAtxt1);
        firstCGtxt1 = (TextView) findViewById(R.id.firstCGtxt1);

        onedivlayout = (TableLayout) findViewById(R.id.onedivlayout);
        twodivlayout = (TableLayout) findViewById(R.id.twodivlayout);


        fetchCategoryDetails();

        if (categoryDetailsArraylist.size() > 0) {
            if (categoryDetailsArraylist.size() == 2) {
                firstPAtxt.setText(categoryDetailsArraylist.get(0));
                secondPAtxt.setText(categoryDetailsArraylist.get(1));
                firstCTgtxt.setText(categoryDetailsArraylist.get(0));
                secondCTgtxt.setText(categoryDetailsArraylist.get(1));
                firstCAtxt.setText(categoryDetailsArraylist.get(0));
                secondCAtxt.setText(categoryDetailsArraylist.get(1));
                firstCGtxt.setText(categoryDetailsArraylist.get(0));
                secondCGtxt.setText(categoryDetailsArraylist.get(1));

            } else {


                twodivlayout.setVisibility(View.GONE);
                onedivlayout.setVisibility(View.VISIBLE);
                firstPAtxt1.setText(categoryDetailsArraylist.get(0));
                firstCTgtxt1.setText(categoryDetailsArraylist.get(0));
                firstCAtxt1.setText(categoryDetailsArraylist.get(0));
                firstCGtxt1.setText(categoryDetailsArraylist.get(0));

            }

        }

        try {
            new GetBAreport().execute();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        tv_current_year_n1 = (TextView) findViewById(R.id.tv_bar_Current_year_n1);
        tv_current_year_n2 = (TextView) findViewById(R.id.tv_bar_Current_year_n2);
        tv_previous_year_p1 = (TextView) findViewById(R.id.tv_bar_Previous_year_p1);
        tv_previous_year_p2 = (TextView) findViewById(R.id.tv_bar_Previous_year_p2);

        tvPreviousyear = (TextView) findViewById(R.id.tvPreviousyear);
        tvCurrentyear = (TextView) findViewById(R.id.tvCurrentyear);


        current_year_n1 = tv_current_year_n1.getText().toString();
        current_year_n2 = tv_current_year_n2.getText().toString();

        previous_year_p1 = tv_previous_year_p1.getText().toString();
        previous_year_p2 = tv_previous_year_p2.getText().toString();

        //int_current_year_n2 = Integer.parseInt(current_year_n2);

        if (role.equalsIgnoreCase("DUB")) {
            try {
                String current_year_n2 = shp.getString("current_year", "");
                int int_current_year_n2 = Integer.parseInt(current_year_n2);

                if (!current_year_n2.equalsIgnoreCase("")) {

                    int int_previous_year_n22 = int_current_year_n2 - 1;

                    PreviousYear = String.valueOf(int_previous_year_n22);

                    CurrentYear = String.valueOf(int_current_year_n2);

                    tvPreviousyear.setText(PreviousYear);
                    tvCurrentyear.setText(CurrentYear);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                current_year_n2 = shp.getString("current_year", "");
                int_current_year_n2 = Integer.parseInt(current_year_n2);

                String comparedatewith = current_year_n2 + "-03-25";


                current_server_date = shp.getString("todaydate", "");

                Log.v("", "current_server_date=" + current_server_date);

                Log.v("", "comparedatewith=" + comparedatewith);


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = sdf.parse(current_server_date);
                    date2 = sdf.parse(comparedatewith);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (!current_year_n2.equalsIgnoreCase("") && date1.compareTo(date2) > 0) {

                    int int_current_year_n22 = int_current_year_n2 + 1;

                    String current_year_n2 = String.valueOf(int_current_year_n22);

                    tv_current_year_n2.setText(current_year_n2);

                    int_current_year_n1 = int_current_year_n22 - 1;

                    current_year_n1 = String.valueOf(int_current_year_n1);

                    tv_current_year_n1.setText(current_year_n1);


                    tv_previous_year_p2.setText(current_year_n1);

                    int_previous_year_p1 = int_current_year_n1 - 1;

                    previous_year_p1 = String.valueOf(int_previous_year_p1);

                    tv_previous_year_p1.setText(previous_year_p1);

                } else {

                    tv_current_year_n2.setText(current_year_n2);

                    int_current_year_n1 = int_current_year_n2 - 1;

                    current_year_n1 = String.valueOf(int_current_year_n1);

                    tv_current_year_n1.setText(current_year_n1);


                    tv_previous_year_p2.setText(current_year_n1);

                    int_previous_year_p1 = int_current_year_n1 - 1;

                    previous_year_p1 = String.valueOf(int_previous_year_p1);

                    tv_previous_year_p1.setText(previous_year_p1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    public class GetBAreport extends AsyncTask<Void, Void, String> {
        String returnMessage = null;

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub


            try {
                todaymessagelist.clear();

                if (!cd.isConnectingToInternet()) {

                    returnMessage = "1";
                } else {
                    if (role.equalsIgnoreCase("DUB")) {
                        returnMessage = getBAreportfromWebserviceForDubai();
                    } else {
                        returnMessage = getBAreportfromWebservice();
                    }
                }

            } catch (Exception e) {
                returnMessage = "1";
                e.getMessage();
            }
            return returnMessage;

        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            String msg = "Please Wait....";
            cd.showProgressDialog(msg);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (TargetVsAchievmentActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            cd.dismissProgressDialog();

            if (result.equalsIgnoreCase("1")) {

                Toast.makeText(TargetVsAchievmentActivity.this, "Connectivity Error!! Please try again", Toast.LENGTH_SHORT).show();
            } else {
                if (role.equalsIgnoreCase("DUB")) {
                    loadReportsforDubai();
                } else {
                    loadReports();
                }
            }
        }
    }


    private String getBAreportfromWebservice() {
        // TODO Auto-generated method stub
        String flag = "0";
        try {


            SoapObject resultsRequestSOAP = null;

            // //soap call
            Log.e("", "not2==" + username);
            resultsRequestSOAP = service.GetAchievementReport(username);
            //Log.e("","resultsRequestSOAP=="+resultsRequestSOAP.toString());

            if (resultsRequestSOAP != null) {
                Log.e("", "not3");
                //	todaymessagelist.clear();
                Log.e("", "not4");
                // soap response
                for (int i = 0; i < resultsRequestSOAP.getPropertyCount(); i++) {
                    Log.e("", "not5");

                    SoapObject getmessaage = (SoapObject) resultsRequestSOAP
                            .getProperty(i);
                    HashMap<String, String> map = new HashMap<String, String>();

                    // display messages by adding it to listview
                    //if (!String.valueOf(getmessaage.getProperty("CreatedDate"))
                    //		.equals("false")) {
                    if (getmessaage != null) {


                        map.put("MONTH", String.valueOf(getmessaage.getProperty("MONTH")));

                        map.put("NetAmountPFirst", String.valueOf(getmessaage.getProperty("NetAmountPFirst")));

                        map.put("NetAmountPSecond", String.valueOf(getmessaage.getProperty("NetAmountPSecond")));

                        map.put("NetAmountCFirst", String.valueOf(getmessaage.getProperty("NetAmountCFirst")));

                        map.put("NetAmountCSecond", String.valueOf(getmessaage.getProperty("NetAmountCSecond")));

                        map.put("TargetAmountCFirst", String.valueOf(getmessaage.getProperty("TargetAmountCFirst")));

                        map.put("TargetAmountCSecond", String.valueOf(getmessaage.getProperty("TargetAmountCSecond")));

                        map.put("GrowthFirst", String.valueOf(getmessaage.getProperty("GrowthFirst")));

                        map.put("GrowthSecond", String.valueOf(getmessaage.getProperty("GrowthSecond")));


                        todaymessagelist.add(map);
                    }
                }
            } else {

                //
                flag = "1";
            }

        } catch (Exception e) {
            // TODO: handle exception
            flag = "1";
            e.printStackTrace();
        }

        return flag;

    }

    private String getBAreportfromWebserviceForDubai() {
        // TODO Auto-generated method stub
        String flag = "0";
        try {

            SoapObject resultsRequestSOAP = null;

            // //soap call
            Log.e("", "not2==" + username);
            resultsRequestSOAP = service.DubaiBAOutletSale(username, outletcode);
            //Log.e("","resultsRequestSOAP=="+resultsRequestSOAP.toString());

            if (resultsRequestSOAP != null) {
                Log.e("", "not3");
                //	todaymessagelist.clear();
                Log.e("", "not4");
                // soap response
                for (int i = 0; i < resultsRequestSOAP.getPropertyCount(); i++) {
                    Log.e("", "not5");

                    SoapObject getmessaage = (SoapObject) resultsRequestSOAP
                            .getProperty(i);
                    HashMap<String, String> map = new HashMap<String, String>();

                    if (getmessaage != null) {

                        if (getmessaage.getProperty("GrowthCSkin") != null && !getmessaage.getProperty("GrowthCSkin").toString().equalsIgnoreCase("anyType{}")) {
                            map.put("GrowthCSkin", String.valueOf(getmessaage.getProperty("GrowthCSkin")));
                        } else {
                            map.put("GrowthCSkin", "0");
                        }

                        if (getmessaage.getProperty("GrowthPSkin") != null && !getmessaage.getProperty("GrowthPSkin").toString().equalsIgnoreCase("anyType{}")) {
                            map.put("GrowthPSkin", String.valueOf(getmessaage.getProperty("GrowthPSkin")));
                        } else {
                            map.put("GrowthPSkin", "0");
                        }

                        if (getmessaage.getProperty("NetAmountCSkin") != null && !getmessaage.getProperty("NetAmountCSkin").toString().equalsIgnoreCase("anyType{}")) {
                            map.put("NetAmountCSkin", String.valueOf(getmessaage.getProperty("NetAmountCSkin")));
                        } else {
                            map.put("NetAmountCSkin", "0");
                        }

                        if (getmessaage.getProperty("NetAmountPSkin") != null && !getmessaage.getProperty("NetAmountPSkin").toString().equalsIgnoreCase("anyType{}")) {
                            map.put("NetAmountPSkin", String.valueOf(getmessaage.getProperty("NetAmountPSkin")));
                        } else {
                            map.put("NetAmountPSkin", "0");
                        }

                        if (getmessaage.getProperty("years_MonthsC") != null && !getmessaage.getProperty("years_MonthsC").toString().equalsIgnoreCase("anyType{}")) {
                            map.put("years_MonthsC", String.valueOf(getmessaage.getProperty("years_MonthsC")));
                        } else {
                            map.put("years_MonthsC", "0");
                        }

                        if (getmessaage.getProperty("years_MonthsP") != null && !getmessaage.getProperty("years_MonthsP").toString().equalsIgnoreCase("anyType{}")) {
                            map.put("years_MonthsP", String.valueOf(getmessaage.getProperty("years_MonthsP")));
                        } else {
                            map.put("years_MonthsP", "0");
                        }

                        todaymessagelist.add(map);
                    }
                }
            } else {

                //
                flag = "1";
            }

        } catch (Exception e) {
            // TODO: handle exception
            flag = "1";
            e.printStackTrace();
        }

        return flag;

    }

    private void loadReports() {
        adapter = new BAReportAdapter(TargetVsAchievmentActivity.this, todaymessagelist);
        lv_ba_report.setAdapter(adapter);// add custom adapter to listview
    }

    private void loadReportsforDubai() {
        adapter1 = new BAReportAdapterDubai(TargetVsAchievmentActivity.this, todaymessagelist);
        lv_ba_report.setAdapter(adapter1);
    }

    @Override
    protected void onDestroy() {
        cd.dismissProgressDialog();
        super.onDestroy();
    }

    public void fetchCategoryDetails() {
        //new changes
        categoryDetailsArraylist = new ArrayList<String>();

        db.open();
        categoryDetailsArraylist = db.getproductcategorywithoutselect(username);
        db.close();

    }
}
