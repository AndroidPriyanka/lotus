package com.prod.sudesi.lotusherbalsnew;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.adapter.BAMonthReportAdapter;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OutletWiseSaleActivity extends Activity {

    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;

    Context context;

    private ProgressDialog prgdialog;

    TextView tv_h_username;
    Button btn_home, btn_logout;
    String username, bdename;

    BAMonthReportAdapter adapter;
    LotusWebservice service;

    ListView lv_bam_report;

    HorizontalScrollView horizantalscrollviewforbamonthreport;

    static private ArrayList<HashMap<String, String>> todaymessagelist = new ArrayList<HashMap<String, String>>();

    Button btn_search;

    Spinner sp_year, sp_month;

    String selected_month;
    String selected_year;

    //----------------
    String current_year_n1, current_year_n2, previous_year_p1, previous_year_p2;

    int int_current_year_n1, int_current_year_n2, int_previous_year_p1, int_previous_year_p2;

    String current_server_date;

    String firstyear, sencondyear;

    String CurrentYear,NextYear,PreviousYear;
    int int_previous_year;

    //----------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_outletwise_sales);
        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        context = OutletWiseSaleActivity.this;

        prgdialog = new ProgressDialog(context);
        service = new LotusWebservice(OutletWiseSaleActivity.this);

        shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
        shpeditor = shp.edit();

        //	lv_bam_report = (ListView) findViewById(R.id.listView_ba_month_report);

        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        username = shp.getString("username", "");
        Log.v("", "username==" + username);

        bdename = shp.getString("BDEusername", "");

        tv_h_username.setText(bdename);

        sp_month = (Spinner) findViewById(R.id.spin_month);
        sp_year = (Spinner) findViewById(R.id.spin_year);


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
                //startActivity(new Intent(getApplicationContext(),
                //		DashboardNewActivity.class));
            }
        });

        try {
            current_year_n2 = shp.getString("current_year", "");
            int_current_year_n2 = Integer.parseInt(current_year_n2);

            if (!current_year_n2.equalsIgnoreCase("")) {

                int int_current_year_n22 = int_current_year_n2 + 1;

                NextYear = String.valueOf(int_current_year_n22);

                int_current_year_n1 = int_current_year_n22 - 1;

                CurrentYear = String.valueOf(int_current_year_n1);

                int_previous_year = int_current_year_n1 - 1;

                PreviousYear = String.valueOf(int_previous_year);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> list_moth = new ArrayList<String>();
        list_moth.add("January");
        list_moth.add("February");
        list_moth.add("March");

        list_moth.add("April");
        list_moth.add("May");
        list_moth.add("June");

        list_moth.add("July");
        list_moth.add("August");
        list_moth.add("September");

        list_moth.add("October");
        list_moth.add("November");
        list_moth.add("December");

        ArrayAdapter<String> adapter_month = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_sp_item, list_moth);
        adapter_month.setDropDownViewResource(R.layout.custom_spinner_dropdown_text);
        sp_month.setAdapter(adapter_month);

        List<String> list_year = new ArrayList<String>();

        list_year.add(PreviousYear);
        list_year.add(CurrentYear);
        list_year.add(NextYear);

        ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_sp_item, list_year);
        adapter_year.setDropDownViewResource(R.layout.custom_spinner_dropdown_text);
        sp_year.setAdapter(adapter_year);


        sp_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                String selectedItem = sp_month.getItemAtPosition(position).toString();

                if (selectedItem.equalsIgnoreCase("Select")) {

                    selected_month = "Select";

                } else {

                    //selected_month = getmonthNo1(selectedItem);

                    selected_month = sp_month.getItemAtPosition(position).toString();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });

        sp_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                selected_year = sp_year.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });

        btn_search = (Button) findViewById(R.id.btn_search);

        btn_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

				/*try{

					new GetBAMonthreport().execute();

				}catch(Exception e){

					e.printStackTrace();
				}*/

                try {

                    if (selected_month.equalsIgnoreCase("Select")) {
                        Log.v("", "sdfsfddfsf");
                        Toast.makeText(getApplicationContext(), "Select Month", Toast.LENGTH_SHORT).show();

                    } else if (selected_year.equalsIgnoreCase("Select")) {
                        Log.v("", "sdfsfddfsf1");
                        Toast.makeText(getApplicationContext(), "Select Year", Toast.LENGTH_SHORT).show();

                    } else {

                        //checkandsendboc(selected_month,selected_year);

                        //	Intent i = new Intent(getApplicationContext(), BocGridViewActivity.class);
                        Intent i = new Intent(getApplicationContext(), OutletWiseSalesCumulativeActivity.class);
                        i.putExtra("month", selected_month);
                        i.putExtra("year", selected_year);
                        startActivity(i);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        });
    }
}
