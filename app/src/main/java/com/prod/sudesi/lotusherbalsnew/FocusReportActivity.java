package com.prod.sudesi.lotusherbalsnew;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.Models.FocusModel;
import com.prod.sudesi.lotusherbalsnew.adapter.FocusReportAdapter;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;


import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class FocusReportActivity extends Activity {

    Button btn_home, btn_logout;
    TextView tv_h_username;
    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;
    LotusWebservice service;
    private ProgressDialog pd;
    ConnectionDetector cd;
    Dbcon db;
    FocusReportAdapter focusReportAdapter;

    String lhrname, bdename, role, check, currentdate;

    Context context;
    ListView reportlist;


    private ArrayList<FocusModel> focusReportList;
    FocusModel focusModel;
    String[] values;

    String product_type;
    //private ProgressDialog mProgress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_focus_report);

        context = FocusReportActivity.this;

        shp = getSharedPreferences("Lotus", MODE_PRIVATE);
        shpeditor = shp.edit();

        //mProgress = new ProgressDialog(FocusReportActivity.this);

        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        reportlist = (ListView) findViewById(R.id.reportlist);

        pd = new ProgressDialog(this);
        service = new LotusWebservice(this);
        cd = new ConnectionDetector(this);
        db = new Dbcon(this);

        lhrname = shp.getString("username", "");
        bdename = shp.getString("BDEusername", "");
        role = shp.getString("Role", "");
        tv_h_username.setText(bdename);

        String BOC = cd.getBocName();

        btn_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btn_home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent i = new Intent(getApplicationContext(), DashboardNewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });


//        fetchAchivementFocusData(BOC);

        new OnlineFetchFocusData().execute(BOC);

//        new uploadFocusReportData().execute(BOC);

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

    }

    public class OnlineFetchFocusData extends AsyncTask<String, Void, SoapObject> {

        String boc = "";
        private SoapObject soap_result = null;

        SoapObject soap_result1 = null;
        int focusflag = 1;
        String Flag;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            String msg = "Receiving.....";
            cd.showProgressDialog(msg);
        }

        @Override
        protected SoapObject doInBackground(String... params) {
            // TODO Auto-generated method stub

            boc = params[0];

            if (!cd.isConnectingToInternet()) {

                Flag = "0";
                // stop executing code by return

            } else {
                soap_result = service.GetFOCUSReport(boc, lhrname);//strDate

                    if (soap_result != null) {

                        if(soap_result.getPropertyCount() > 0) {
                            for (int i = 0; i < soap_result.getPropertyCount(); i++) {

                                soap_result1 = (SoapObject) soap_result.getProperty(i);

                                String Type = cd.getNonNullValues(soap_result1.getProperty("Type").toString());

                                String Category = cd.getNonNullValues(soap_result1.getProperty("Category").toString());

                                String Target_qty = cd.getNonNullValues_Integer(soap_result1.getProperty("Target_qty").toString());

                                String Target_BOC = cd.getNonNullValues(soap_result1.getProperty("Target_BOC").toString());

                                String Achievement_Unit = cd.getNonNullValues_Integer(soap_result1.getProperty("Achievement_Unit").toString());

                                String emp_id = cd.getNonNullValues(soap_result1.getProperty("emp_id").toString());

                                String android_created_date = cd.getNonNullValues(soap_result1.getProperty("android_created_date").toString());

                                db.open();

                                Cursor c1 = db.CheckFocusDataExist("focus_data", Type, Target_BOC);

                                int count = c1.getCount();
                                Log.v("", "" + count);
                                db.close();
                                if (count > 0) {

                                    db.open();
                                    db.UpdateFocusData(Type, Category, Target_qty, Target_BOC,
                                            Achievement_Unit, emp_id, android_created_date);

                                    db.close();

                                } else {

                                    Log.e("pm", "pm5");
                                    db.open();
                                    db.insertFocusData(Type, Category, Target_qty, Target_BOC,
                                            Achievement_Unit, emp_id, android_created_date);

                                    db.close();

                                }

                                focusflag = 1;
                            }
                        }else {

                            focusflag = 2;
                        }

                    } else {
                    Log.v("", "Soap result is null");

                    focusflag = 2;

                    final Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "MM/dd/yyyy HH:mm:ss");
                    String Createddate = formatter.format(calendar
                            .getTime());

                    int n = Thread.currentThread().getStackTrace()[2]
                            .getLineNumber();
                    db.insertSyncLog("Soup is null - focusData()",
                            String.valueOf(n), "focusData()",
                            Createddate, Createddate,
                            lhrname, "Focus Data",
                            "Fail");

                }

                if (focusflag == 1) {

                    Flag = "1";

                } else {

                    Flag = "2";

                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(SoapObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            try {

                if (Flag.equalsIgnoreCase("0")) {
                    if (FocusReportActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                        return;
                    }
                    cd.dismissProgressDialog();
                    Toast.makeText(getApplicationContext(), "Connectivity Error, Please check Internet connection!!", Toast.LENGTH_SHORT).show();

                } else if (Flag.equalsIgnoreCase("1")) {

//                    fetchAchivementFocusData(boc);
                    new uploadFocusReportData().execute(boc);

                } else if (Flag.equalsIgnoreCase("2")) {
                    if (FocusReportActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                        return;
                    }
                    cd.dismissProgressDialog();

                    Toast.makeText(getApplicationContext(), "No Record Found for FocusData", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    private class uploadFocusReportData extends AsyncTask<String, Void, SoapObject> {


        String ErroFlag;
        String Erro_function = "";

        Cursor attendance_array;

        String Flag;
        String boc = "";
        ArrayList<FocusModel> focusReportList = new ArrayList<FocusModel>();
        ArrayList<FocusModel> focusStockReportList = new ArrayList<FocusModel>();

        SoapPrimitive soapresultfocus = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected SoapObject doInBackground(String... params) {
            // TODO Auto-generated method stub

            boc = params[0];

            if (!cd.isConnectingToInternet()) {

                Flag = "0";

            } else {

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                String insert_timestamp = sdf.format(cal.getTime());

                db.open();

                Cursor c = db.getFocusReportAchivement(lhrname, boc);
                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();
                    do {

                        focusModel = new FocusModel();

                        focusModel.setProduct_type(cd.getNonNullValues_Integer(c.getString(0)));
                        focusModel.setProduct_category(cd.getNonNullValues_Integer(c.getString(3)));
                        focusModel.setTarget_qty(cd.getNonNullValues_Integer(c.getString(1)));
                        focusModel.setAchievement_Unit(cd.getNonNullValues_Integer(c.getString(2)));
                        focusModel.setUsername(lhrname);
                        focusModel.setAndroid_created_date(insert_timestamp);
                        focusModel.setBocname(boc);

                        focusStockReportList.add(focusModel);

                    } while (c.moveToNext());
                }

                try {
                    if (focusStockReportList.size() > 0) {
                        for (int i = 0; i < focusStockReportList.size(); i++) {

                            focusModel = focusStockReportList.get(i);

                            if (Integer.parseInt(focusModel.getAchievement_Unit()) != 0) {
                                db.updateFocusData(focusModel.getAchievement_Unit(), focusModel.getProduct_type(), lhrname);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Cursor c1 = db.fetchFocusAchivementDetails(lhrname, boc);
                if (c1 != null && c1.getCount() > 0) {
                    c1.moveToFirst();
                    do {

                        focusModel = new FocusModel();
                        focusModel.setProduct_type(cd.getNonNullValues(c1.getString(2)));
                        focusModel.setProduct_category(cd.getNonNullValues(c1.getString(3)));
                        focusModel.setTarget_qty(cd.getNonNullValues_Integer(c1.getString(8)));
                        focusModel.setAchievement_Unit(cd.getNonNullValues_Integer(c1.getString(9)));
                        focusModel.setUsername(cd.getNonNullValues(c1.getString(4)));
                        focusModel.setAndroid_created_date(insert_timestamp);
                        focusModel.setBocname(cd.getNonNullValues(c1.getString(11)));

                        focusReportList.add(focusModel);

                    } while (c1.moveToNext());
                }

                db.close();

                try {
                    if (focusReportList.size() > 0) {
                        for (int i = 0; i < focusReportList.size(); i++) {

                            focusModel = focusReportList.get(i);

                            soapresultfocus = service.SaveIntoFOCUSReport(focusModel.getProduct_type(), focusModel.getProduct_category()
                                    , focusModel.getTarget_qty(), focusModel.getBocname(), focusModel.getUsername()
                                    , focusModel.getAndroid_created_date(), focusModel.getAchievement_Unit());


                            if (soapresultfocus != null) {

                                if (soapresultfocus.toString().equalsIgnoreCase("TRUE")) {
                                    Flag = "1";

                                } else {
                                    Flag = "2";
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(SoapObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            try {

                if (Flag.equalsIgnoreCase("0")) {

                    Toast.makeText(getApplicationContext(), "Connectivity Error, Please check Internet connection!!", Toast.LENGTH_SHORT).show();

                } else if (Flag.equalsIgnoreCase("1")) {

                    new FetchFocusData().execute(boc);

                } else if (Flag.equalsIgnoreCase("2")) {

                    new FetchFocusData().execute(boc);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class FetchFocusData extends AsyncTask<String, String, ArrayList<HashMap<String, String>>> {

        ArrayList<HashMap<String, String>> arr_category = new ArrayList<HashMap<String, String>>();

        String boc = "";

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
            // TODO Auto-generated method stub

            boc = params[0];
            db.open();

            Cursor c = db.showFocusAchivementDetails(lhrname, boc);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {

                    HashMap<String, String> map = new HashMap<String, String>();
                    String protype = cd.getNonNullValues_Integer(c.getString(0));
                    String procategory = cd.getNonNullValues_Integer(c.getString(1));
                    String div = "";
                    if (procategory.equalsIgnoreCase("SKIN")) {
                        div = "LH";
                    } else {
                        div = "LM";
                    }

                    map.put("Type", protype + " (" + div + " )");
                    map.put("Target", cd.getNonNullValues_Integer(c.getString(2)));
                    map.put("Achievement", cd.getNonNullValues_Integer(c.getString(3)));


                    arr_category.add(map);

                } while (c.moveToNext());
            }

            return arr_category;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (FocusReportActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            cd.dismissProgressDialog();
            focusReportAdapter = new FocusReportAdapter(context, arr_category);
            reportlist.setAdapter(focusReportAdapter);

        }

    }


    private void fetchAchivementFocusData(String boc) {

        focusReportList = new ArrayList<FocusModel>();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String insert_timestamp = sdf.format(cal.getTime());

        db.open();

        Cursor c = db.getFocusReportAchivement(lhrname, boc);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {

                focusModel = new FocusModel();

                focusModel.setProduct_type(cd.getNonNullValues_Integer(c.getString(0)));
                focusModel.setProduct_category(cd.getNonNullValues_Integer(c.getString(3)));
                focusModel.setTarget_qty(cd.getNonNullValues_Integer(c.getString(1)));
                focusModel.setAchievement_Unit(cd.getNonNullValues_Integer(c.getString(2)));
                focusModel.setUsername(lhrname);
                focusModel.setAndroid_created_date(insert_timestamp);
                focusModel.setBocname(boc);

                focusReportList.add(focusModel);

            } while (c.moveToNext());
        }

        try {
            if (focusReportList.size() > 0) {
                for (int i = 0; i < focusReportList.size(); i++) {

                    focusModel = focusReportList.get(i);

                    db.open();
                    db.updateFocusData(focusModel.getAchievement_Unit(), focusModel.getProduct_type(), lhrname);
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        cd.dismissProgressDialog();
        super.onDestroy();
    }

}
