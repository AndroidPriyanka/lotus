package com.prod.sudesi.lotusherbalsnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.prod.sudesi.lotusherbalsnew.adapter.OutletWiseSalesAdapter;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import org.ksoap2.serialization.SoapObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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

    String str_Month;
    String year;
    Date startdate, enddate;
    ArrayList<String> dates_array;

    private OutletWiseSalesAdapter adapter;

    ArrayList<HashMap<String, String>> final_array;

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

        listView_outletwisesales_report = (ListView) findViewById(R.id.listView_outletwisesales_report);

        final_array = new ArrayList<HashMap<String, String>>();

        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        username = shp.getString("username", "");
        Log.v("", "username==" + username);

        bdename = shp.getString("BDEusername", "");

        tv_h_username.setText(bdename);

        Intent intent = getIntent();
        str_Month = intent.getStringExtra("month");
        String y[] = intent.getStringExtra("year").split("-");
//		str_year = intent.getStringExtra("");
        Log.e("str_Month", str_Month);
        year = y[0];

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

        System.out.println("   startdate--" + getStartEnd(str_Month, year)[0]);
        System.out.println("   enddate--" + getStartEnd(str_Month, year)[1]);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {


            startdate = format.parse(getStartEnd(str_Month, year)[0]);
            enddate = format.parse(getStartEnd(str_Month, year)[1]);

            System.out.println("   startdate1--" + startdate);
            System.out.println("   enddate1--" + enddate);

            List<Date> dates = getDaysBetweenDates(startdate, enddate);

            Log.e("dates", dates.toString());

            dates_array = new ArrayList<String>();

            for (int i = 0; i < dates.size(); i++) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

                String reportDate = df.format(dates.get(i));
                Log.d("Date is", " " + reportDate);
                dates_array.add(reportDate);

                // Print what date is today!
                System.out.println("Report Date: " + reportDate);
            }


        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            new DubaiTotalOutletSaleAPK().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("WrongConstant")
    public static List<Date> getDaysBetweenDates(Date startdate, Date enddate) {
        List<Date> dates = new ArrayList<Date>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate) || calendar.getTime().equals(enddate)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public String[] getStartEnd(String Month, String year) {
        String startend[] = new String[2];

        if (Month.equalsIgnoreCase("January")) {
            startend[0] = year + "-01-01";
            startend[1] = year + "-01-31";
        } else if (Month.equalsIgnoreCase("February")) {
            startend[0] = year + "-02-01";
            startend[1] = year + "-02-28";
        } else if (Month.equalsIgnoreCase("March")) {
            startend[0] = year + "-03-01";
            startend[1] = year + "-03-31";
        } else if (Month.equalsIgnoreCase("April")) {
            startend[0] = year + "-04-01";
            startend[1] = year + "-04-30";
        } else if (Month.equalsIgnoreCase("May")) {
            startend[0] = year + "-05-01";
            startend[1] = year + "-05-31";
        } else if (Month.equalsIgnoreCase("June")) {
            startend[0] = year + "-06-01";
            startend[1] = year + "-06-30";
        } else if (Month.equalsIgnoreCase("July")) {
            startend[0] = year + "-07-01";
            startend[1] = year + "-07-31";
        } else if (Month.equalsIgnoreCase("August")) {
            startend[0] = year + "-08-01";
            startend[1] = year + "-08-31";
        } else if (Month.equalsIgnoreCase("September")) {
            startend[0] = year + "-09-01";
            startend[1] = year + "-09-30";
        } else if (Month.equalsIgnoreCase("October")) {
            startend[0] = year + "-10-01";
            startend[1] = year + "-10-31";
        } else if (Month.equalsIgnoreCase("November")) {
            startend[0] = year + "-11-01";
            startend[1] = year + "-11-30";
        } else if (Month.equalsIgnoreCase("December")) {
            startend[0] = year + "-12-01";
            startend[1] = year + "-12-31";
        }

        return startend;
    }


    public class DubaiTotalOutletSaleAPK extends AsyncTask<Void, Void, SoapObject> {

        SoapObject soap_result;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            prgdialog.setTitle("Status");
            prgdialog.setMessage("Please wait...");
            prgdialog.show();
        }


        @Override
        protected SoapObject doInBackground(Void... params) {
            // TODO Auto-generated method stub

            if (!cd.isConnectingToInternet()) {
                soap_result = null;
            } else {
                String startdate[] = getStartEnd(str_Month, year);

                soap_result = service.DubaiTotalOutletSaleAPK(username, startdate[0], startdate[1]);
                if (soap_result != null) {
                    for (int i = 0; i < soap_result.getPropertyCount(); i++) {
                        SoapObject getmessaage = (SoapObject) soap_result.getProperty(i);

                        if (getmessaage != null) {
                            if (getmessaage.getProperty("outletname") != null && !getmessaage.getProperty("outletname").toString().equalsIgnoreCase("anyType{}")) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("outletname", String.valueOf(getmessaage.getProperty("outletname")));

                                if (getmessaage.getProperty("SoldStock") != null && !getmessaage.getProperty("SoldStock").toString().equalsIgnoreCase("anyType{}")) {
                                    map.put("SoldStock", String.valueOf(getmessaage.getProperty("SoldStock")));
                                } else {
                                    map.put("SoldStock", "0");
                                }

                                if (getmessaage.getProperty("NetAmount") != null && !getmessaage.getProperty("NetAmount").toString().equalsIgnoreCase("anyType{}")) {
                                    map.put("NetAmount", String.valueOf(getmessaage.getProperty("NetAmount")));
                                } else {
                                    map.put("NetAmount", "0");
                                }

                                final_array.add(map);
                            }
                        }
                    }


                }
            }

            return soap_result;
        }


        @Override
        protected void onPostExecute(SoapObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (prgdialog != null && prgdialog.isShowing() && !OutletWiseSalesCumulativeActivity.this.isFinishing()) {
                prgdialog.dismiss();
            }
            if (result != null) {
                adapter = new OutletWiseSalesAdapter(OutletWiseSalesCumulativeActivity.this, final_array);
                listView_outletwisesales_report.setAdapter(adapter);// add custom adapter to
                adapter.notifyDataSetChanged();


            } else {
                Toast.makeText(OutletWiseSalesCumulativeActivity.this, "Please Check Internet Connectivity", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
