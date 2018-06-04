package com.prod.sudesi.lotusherbalsnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.adapter.OutletWiseSalesAdapter;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import java.util.ArrayList;
import java.util.HashMap;

public class OutletWiseSalesCumulativeActivity extends Activity {

    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;

    Context context;

    private ProgressDialog prgdialog;

    private OutletWiseSalesAdapter outletWiseSalesAdapter;

    LotusWebservice service;

    ListView listView_outletwisesales_report;

    static public ArrayList<HashMap<String, String>> todaymessagelist = new ArrayList<HashMap<String, String>>();

    TextView tv_h_username;
    Button btn_home, btn_logout;
    String username, bdename;

    ConnectionDetector cd;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_outletwisesales_cumulative);
        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        context = OutletWiseSalesCumulativeActivity.this;

        prgdialog = new ProgressDialog(context);
        service = new LotusWebservice(OutletWiseSalesCumulativeActivity.this);
        cd = new ConnectionDetector(OutletWiseSalesCumulativeActivity.this);

        shp = context.getSharedPreferences("Lotus", MODE_PRIVATE);
        shpeditor = shp.edit();

        listView_outletwisesales_report = findViewById(R.id.listView_outletwisesales_report);

        tv_h_username = findViewById(R.id.tv_h_username);
        btn_home = findViewById(R.id.btn_home);
        btn_logout = findViewById(R.id.btn_logout);

        username = shp.getString("username", "");
        Log.v("", "username==" + username);

        bdename = shp.getString("BDEusername", "");

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
                //startActivity(new Intent(getApplicationContext(), DashboardNewActivity.class));
            }
        });
        //---------------------

    }

    private void loadReports() {
        outletWiseSalesAdapter = new OutletWiseSalesAdapter(OutletWiseSalesCumulativeActivity.this, todaymessagelist);
        listView_outletwisesales_report.setAdapter(outletWiseSalesAdapter);// add custom adapter to
        // listview
    }
}
