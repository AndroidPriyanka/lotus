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

import com.prod.sudesi.lotusherbalsnew.Models.AttendanceModel;
import com.prod.sudesi.lotusherbalsnew.Models.FocusModel;
import com.prod.sudesi.lotusherbalsnew.adapter.FocusReportAdapter;
import com.prod.sudesi.lotusherbalsnew.adapter.ReportCategoryTypeAdapter;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    String flotername, bdename, role, check, currentdate;

    private ArrayList<String> FocusReportDetailsArraylist;
    String[] strFocusReportArray = null;
    Context context;
    ListView reportlist;

    private ArrayList<FocusModel> focusReportList;
    FocusModel focusModel;
    String[] values;

    String db_id, pro_name, product_category, product_type, size, price, username, target_qty, target_amt, android_created_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_focus_report);

        context = FocusReportActivity.this;

        shp = getSharedPreferences("Lotus", MODE_PRIVATE);
        shpeditor = shp.edit();

        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        reportlist = (ListView) findViewById(R.id.reportlist);

        pd = new ProgressDialog(this);
        service = new LotusWebservice(this);
        cd = new ConnectionDetector(this);
        db = new Dbcon(this);

        flotername = shp.getString("username", "");
        bdename = shp.getString("BDEusername", "");
        role = shp.getString("Role", "");
        tv_h_username.setText(bdename);

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

        Intent intent = getIntent();

        db_id = intent.getStringExtra("db_id");
        pro_name = intent.getStringExtra("proname");
        product_category = intent.getStringExtra("proCategory");
        product_type = intent.getStringExtra("protype");
        size = intent.getStringExtra("size");
        price = intent.getStringExtra("price");
        username = intent.getStringExtra("username");
        target_qty = intent.getStringExtra("target_qty");
        target_amt = intent.getStringExtra("target_amt");
        android_created_date = intent.getStringExtra("androiddate");

        new FocusReportData().execute();

    }

    private class FocusReportData extends AsyncTask<String, Void, SoapObject> {


        String ErroFlag;
        String Erro_function = "";

        Cursor attendance_array;

        String Flag;

        SoapObject soapresultfocus = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pd.setMessage("Please Wait");
            pd.show();
            pd.setCancelable(false);
        }

        @Override
        protected SoapObject doInBackground(String... params) {
            // TODO Auto-generated method stub

            final String[] columns = new String[]{"Productid", "Type", "Category", "Empid", "ProName", "size", "MRP",
                    "Target_qty", "Target_amt", "AndroidCreateddate"};

            if (!cd.isConnectingToInternet()) {

                Flag = "3";
                // stop executing code by return

            } else {


                Flag = "1";

                try {
                    soapresultfocus = service.SaveIntoFOCUSReport(db_id, product_type, product_category,
                            pro_name, size, price, target_qty, target_amt, username, android_created_date);

                    if (soapresultfocus != null) {

                        focusReportList = new ArrayList<FocusModel>();
                        for (int i = 0; i < soapresultfocus.getPropertyCount(); i++) {

                            SoapObject soapObject = (SoapObject) soapresultfocus.getProperty(i);

                            String Product_id = soapObject.getProperty("Product_id").toString();
                            if (Product_id == null || Product_id.equals("anyType{}")) {
                                Product_id = "";
                            }
                            String Type = soapObject.getProperty("Type").toString();
                            if (Type == null || Type.equals("anyType{}")) {
                                Type = "";
                            }

                            String Category = soapObject.getProperty("Category").toString();
                            if (Category == null || Category.equals("anyType{}")) {
                                Category = "";
                            }

                            String Name = soapObject.getProperty("Name").toString();
                            if (Name == null || Name.equals("anyType{}")) {
                                Name = "";
                            }

                            String Size = soapObject.getProperty("Size").toString();
                            if (Size == null || Size.equals("anyType{}")) {
                                Size = "";
                            }

                            String MRP = soapObject.getProperty("MRP").toString();
                            if (MRP == null || MRP.equals("anyType{}")) {
                                MRP = "";
                            }

                            String Target_qty = soapObject.getProperty("Target_qty").toString();
                            if (Target_qty == null || Target_qty.equals("anyType{}")) {
                                Target_qty = "";
                            }

                            String Target_amount = soapObject.getProperty("Target_amount").toString();
                            if (Target_amount == null || Target_amount.equals("anyType{}")) {
                                Target_amount = "";
                            }

                            String Achievement_Unit = soapObject.getProperty("Achievement_Unit").toString();
                            if (Achievement_Unit == null || Achievement_Unit.equals("anyType{}")) {
                                Achievement_Unit = "";
                            }

                            String Achievement_value = soapObject.getProperty("Achievement_value").toString();
                            if (Achievement_value == null || Achievement_value.equals("anyType{}")) {
                                Achievement_value = "";
                            }

                            String emp_id = soapObject.getProperty("emp_id").toString();
                            if (emp_id == null || emp_id.equals("anyType{}")) {
                                emp_id = "";
                            }

                            String android_created_date = soapObject.getProperty("android_created_date").toString();
                            if (android_created_date == null || android_created_date.equals("anyType{}")) {
                                android_created_date = "";
                            }

                            focusModel = new FocusModel();
                            focusModel.setDb_id(Product_id);
                            focusModel.setProduct_type(Type);
                            focusModel.setProduct_category(Category);
                            focusModel.setPro_name(Name);
                            focusModel.setSize(Size);
                            focusModel.setPrice(MRP);
                            focusModel.setTarget_qty(Target_qty);
                            focusModel.setTarget_amt(Target_amount);
                            focusModel.setAchievement_Unit(Achievement_Unit);
                            focusModel.setAchievement_value(Achievement_value);
                            focusModel.setUsername(emp_id);
                            focusModel.setAndroid_created_date(android_created_date);

                            focusReportList.add(focusModel);
                        }


                        for (int j = 0; j < focusReportList.size(); j++) {
                            focusModel = focusReportList.get(j);

                            db.open();
                            Cursor c = db.getuniquefocusdata(username, focusModel.getDb_id());

                            int count = c.getCount();
                            Log.v("", "" + count);
                            db.close();
                            if (count > 0) {
                                db.open();

                                db.update(db_id,
                                        new String[]{
                                                focusModel.getDb_id(),
                                                focusModel.getProduct_type(),
                                                focusModel.getProduct_category(),
                                                focusModel.getUsername(),
                                                focusModel.getPro_name(),
                                                focusModel.getSize(),
                                                focusModel.getPrice(),
                                                focusModel.getTarget_qty(),
                                                focusModel.getTarget_amt(),
                                                focusModel.getAndroid_created_date()},
                                        new String[]{"Productid", "Type", "Category", "Empid", "ProName", "size", "MRP",
                                                "Target_qty", "Target_amt", "AndroidCreateddate"},
                                        "focus_data", "Productid");

                                db.close();
                            } else {
                                db.open();

                                values = new String[]{
                                        focusModel.getDb_id(),
                                        focusModel.getProduct_type(),
                                        focusModel.getProduct_category(),
                                        focusModel.getUsername(),
                                        focusModel.getPro_name(),
                                        focusModel.getSize(),
                                        focusModel.getPrice(),
                                        focusModel.getTarget_qty(),
                                        focusModel.getTarget_amt(),
                                        focusModel.getAndroid_created_date()};

                                db.insert(values, columns, "focus_data");

                                db.close();

                            }

                        }
                        ErroFlag = "1";
                    } else {
                        ErroFlag = "0";
                        //String errors = "Soap in giving null while 'Attendance' and 'checkSyncFlag = 2' in  data Sync";
                        //we.writeToSD(errors.toString());
                        final Calendar calendar1 = Calendar
                                .getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(calendar1
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2].getLineNumber();
                        db.insertSyncLog("Internet Connection Lost, Soap in giving null while 'Focus Report'", String.valueOf(n), "SaveAttendance()", Createddate, Createddate, shp.getString("username", ""), "Transaction Upload", "Fail");

                    }


                } catch (Exception e) {
                    //ErroFlag = "3";
                    Erro_function = "Attendance";
                    e.printStackTrace();
                    String Error = e.toString();

                    final Calendar calendar1 = Calendar
                            .getInstance();
                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                            "MM/dd/yyyy HH:mm:ss");
                    String Createddate = formatter1.format(calendar1
                            .getTime());

                    int n = Thread.currentThread().getStackTrace()[2].getLineNumber();
                    db.insertSyncLog(Error, String.valueOf(n), "Focus Report", Createddate, Createddate, shp.getString("username", ""), "Transaction Upload", "Fail");


                }
                //}
            }

            return null;
        }

        @Override
        protected void onPostExecute(SoapObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            pd.dismiss();
            try {
                if (Flag.equalsIgnoreCase("3")) {

                    Toast.makeText(getApplicationContext(), "Connectivity Error Please check internet ", Toast.LENGTH_SHORT).show();
                }

                if (ErroFlag.equalsIgnoreCase("0")) {

                    Toast.makeText(getApplicationContext(), "Getting null Response", Toast.LENGTH_SHORT).show();
                }
                if (ErroFlag.equalsIgnoreCase("1")) {

                    Toast.makeText(getApplicationContext(), "Focus Data Saved Succesfully!!", Toast.LENGTH_SHORT).show();

                    focusReportAdapter = new FocusReportAdapter(context, focusReportList);
                    reportlist.setAdapter(focusReportAdapter);
                }

            } catch (Exception e){
                e.printStackTrace();
        }
        }

    }

}
