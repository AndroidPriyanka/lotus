package com.prod.sudesi.lotusherbalsnew;

import android.annotation.SuppressLint;
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
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
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


public class BocCumulativeDashboardActivity extends Activity {

    TextView txt_boc, txt_year;

    String str_BOC, str_year;
    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;
    Dbcon dbcon;
    Button btn_home;
    TextView tv_h_username;
    String username, bdename, role, outletcode;
    ArrayList<String> dates_array;
    ListView listView;
    private Context context;
    ArrayList<HashMap<String, String>> skin_array, color_array, total_array, final_array;
    LotusWebservice service;
    private ProgressDialog prgdialog;
    Date startdate, enddate;
    TableLayout tl_cumulative, tl_cumulativeD;
    String year, year1;
    private Dbcon db;
    ConnectionDetector cd;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard_cumulative);
        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        txt_boc = (TextView) findViewById(R.id.txt_boc);
        txt_year = (TextView) findViewById(R.id.txt_year);

        tl_cumulative = (TableLayout) findViewById(R.id.tl_cumulative);
        tl_cumulativeD = (TableLayout) findViewById(R.id.tl_cumulativeD);

        dbcon = new Dbcon(BocCumulativeDashboardActivity.this);
        dbcon.open();
        context = getApplicationContext();
        shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
        shpeditor = shp.edit();

        username = shp.getString("username", "");
        bdename = shp.getString("BDEusername", "");
        role = shp.getString("Role", "");
        outletcode = shp.getString("FLRCode", "");

        db = new Dbcon(BocCumulativeDashboardActivity.this);
        cd = new ConnectionDetector(BocCumulativeDashboardActivity.this);

        service = new LotusWebservice(BocCumulativeDashboardActivity.this);
        prgdialog = new ProgressDialog(BocCumulativeDashboardActivity.this);



        Intent intent = getIntent();
        str_BOC = intent.getStringExtra("month");
        String y[] = intent.getStringExtra("year").split("-");

//		str_year = intent.getStringExtra("");
        Log.e("str_BOC", str_BOC);
        if(role.equalsIgnoreCase("DUB")){
            year = y[0];

            txt_boc.setText(str_BOC);
            txt_year.setText(year);
        }else{
            year = y[0];
            year1 = y[1];

            txt_boc.setText(str_BOC);
            txt_year.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        }




        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        tv_h_username.setText(bdename);

        if (role.equalsIgnoreCase("DUB")) {
            tl_cumulative.setVisibility(View.GONE);
            tl_cumulativeD.setVisibility(View.VISIBLE);
        }

        btn_home = (Button) findViewById(R.id.btn_home);

        btn_home.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent i = new Intent(getApplicationContext(),
                        DashboardNewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                // startActivity(new Intent(getApplicationContext(),
                // DashboardNewActivity.class));
            }
        });

        if(role.equalsIgnoreCase("DUB")){
            System.out.println("   startdate--" + getStartEndForDubai(str_BOC, year)[0]);
            System.out.println("   enddate--" + getStartEndForDubai(str_BOC, year)[1]);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {

                startdate = format.parse(getStartEndForDubai(str_BOC, year)[0]);
                enddate = format.parse(getStartEndForDubai(str_BOC, year)[1]);

                System.out.println("   startdate1--" + startdate);
                System.out.println("   enddate1--" + enddate);

                List<Date> dates = getDaysBetweenDates(startdate, enddate);

                Log.e("dates", dates.toString());

                dates_array = new ArrayList<String>();

                for (int i = 0; i < dates.size(); i++) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

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
        }else {
            System.out.println("   startdate--" + getStartEnd(str_BOC, year, year1)[0]);
            System.out.println("   enddate--" + getStartEnd(str_BOC, year, year1)[1]);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {


                startdate = format.parse(getStartEnd(str_BOC, year, year1)[0]);
                enddate = format.parse(getStartEnd(str_BOC, year, year1)[1]);

                System.out.println("   startdate1--" + startdate);
                System.out.println("   enddate1--" + enddate);

                List<Date> dates = getDaysBetweenDates(startdate, enddate);

                Log.e("dates", dates.toString());

                dates_array = new ArrayList<String>();

                for (int i = 0; i < dates.size(); i++) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

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
        }


        if (role.equalsIgnoreCase("DUB")) {
            DashbardDataForDubai dashbardDataForDubai = new DashbardDataForDubai();
            dashbardDataForDubai.execute();
        } else {
            DashbardData dashbardData = new DashbardData();
            dashbardData.execute();
        }


    }

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


    public String[] getStartEnd(String BOC, String year, String year1) {
        String startend[] = new String[2];

        if (BOC.equalsIgnoreCase("BOC1")) {
            startend[0] = year + "-03-26";
            startend[1] = year + "-04-25";
        } else if (BOC.equalsIgnoreCase("BOC2")) {
            startend[0] = year + "-04-26";
            startend[1] = year + "-05-25";
        } else if (BOC.equalsIgnoreCase("BOC3")) {
            startend[0] = year + "-05-26";
            startend[1] = year + "-06-25";
        } else if (BOC.equalsIgnoreCase("BOC4")) {
            startend[0] = year + "-06-26";
            startend[1] = year + "-07-25";
        } else if (BOC.equalsIgnoreCase("BOC5")) {
            startend[0] = year + "-07-26";
            startend[1] = year + "-08-25";
        } else if (BOC.equalsIgnoreCase("BOC6")) {
            startend[0] = year + "-08-26";
            startend[1] = year + "-09-25";
        } else if (BOC.equalsIgnoreCase("BOC7")) {
            startend[0] = year + "-09-26";
            startend[1] = year + "-10-25";
        } else if (BOC.equalsIgnoreCase("BOC8")) {
            startend[0] = year + "-10-26";
            startend[1] = year + "-11-25";
        } else if (BOC.equalsIgnoreCase("BOC9")) {
            startend[0] = year + "-11-26";
            startend[1] = year + "-12-25";
        } else if (BOC.equalsIgnoreCase("BOC10")) {
            startend[0] = year + "-12-26";
            startend[1] = year1 + "-01-25";
        } else if (BOC.equalsIgnoreCase("BOC11")) {
            startend[0] = year1 + "-01-26";
            startend[1] = year1 + "-02-25";
        } else if (BOC.equalsIgnoreCase("BOC12")) {
            startend[0] = year1 + "-02-26";
            startend[1] = year1 + "-03-25";
        }

        return startend;
    }


    public class DashbardData extends AsyncTask<Void, Void, String> {
        String returnMessage = "";

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                if (cd.isConnectingToInternet()) {
                    db.open();
                    SoapObject resultsRequestSOAP = null;
                    final String[] columns = new String[]{"BOC", "AndroidCreatedDate", "COLOR", "ColorSoldQty", "ColorSoldValue", "SKIN", "SkinSoldQty", "SkinSoldValue", "TOTAL", "TotalQty", "TotalValue"};

                    DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");

                    String startdate[] = getStartEnd(str_BOC, year, year1);

//						if(Type_radio_button.equalsIgnoreCase("ALL")){

                    // resultsRequestSOAP = service.GetDashboradData(0,  FromDate_date,  ToDate_date,  username, Type_radio_button);
                    resultsRequestSOAP = service.GetDashboradData("2", startdate[0], startdate[1], username, "ALL");
                    if (resultsRequestSOAP != null) {
                        final_array = new ArrayList<HashMap<String, String>>();
                        for (int i = 0; i < resultsRequestSOAP.getPropertyCount(); i++) {
                            SoapObject getmessaage = (SoapObject) resultsRequestSOAP.getProperty(i);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("DATE", dates_array.get(i));

                            map.put("AndroidCreatedDate", String.valueOf(getmessaage.getProperty("AndroidCreatedDate")));
                            map.put("SKIN", String.valueOf(getmessaage.getProperty("SKIN")));
                            map.put("SkinSoldValue", String.valueOf(getmessaage.getProperty("SkinSoldValue")));
                            map.put("SkinSoldQty", String.valueOf(getmessaage.getProperty("SkinSoldQty")));

                            map.put("COLOR", String.valueOf(getmessaage.getProperty("COLOR")));
                            map.put("ColorSoldValue", String.valueOf(getmessaage.getProperty("ColorSoldValue")));
                            map.put("ColorSoldQty", String.valueOf(getmessaage.getProperty("ColorSoldQty")));

                            map.put("TOTAL", String.valueOf(getmessaage.getProperty("TOTAL")));
                            map.put("TotalValue", String.valueOf(getmessaage.getProperty("TotalValue")));
                            map.put("TotalQty", String.valueOf(getmessaage.getProperty("TotalQty")));

                            final_array.add(map);
                        }


                        Cursor cursor = db.getDashboardData(str_BOC);
                        {
                            if (cursor != null && cursor.getCount() > 0) {

                                db.deleteMulti("dashboard_details", "BOC=?", new String[]{str_BOC});
                                for (int i = 0; i < final_array.size(); i++) {
                                    String[] values = new String[]{str_BOC,
                                            String.valueOf(final_array.get(i).get("DATE")),
                                            String.valueOf(final_array.get(i).get("COLOR")),
                                            String.valueOf(final_array.get(i).get("ColorSoldQty")),
                                            String.valueOf(final_array.get(i).get("ColorSoldValue")),
                                            String.valueOf(final_array.get(i).get("SKIN")),
                                            String.valueOf(final_array.get(i).get("SkinSoldQty")),
                                            String.valueOf(final_array.get(i).get("SkinSoldValue")),
                                            String.valueOf(final_array.get(i).get("TOTAL")),
                                            String.valueOf(final_array.get(i).get("TotalQty")),
                                            String.valueOf(final_array.get(i).get("TotalValue"))};
                                    db.insert(values, columns, "dashboard_details");
                                }
                            } else {

                                for (int i = 0; i < final_array.size(); i++) {
                                    String[] values = new String[]{str_BOC,
                                            String.valueOf(final_array.get(i).get("DATE")),
                                            String.valueOf(final_array.get(i).get("COLOR")),
                                            String.valueOf(final_array.get(i).get("ColorSoldQty")),
                                            String.valueOf(final_array.get(i).get("ColorSoldValue")),
                                            String.valueOf(final_array.get(i).get("SKIN")),
                                            String.valueOf(final_array.get(i).get("SkinSoldQty")),
                                            String.valueOf(final_array.get(i).get("SkinSoldValue")),
                                            String.valueOf(final_array.get(i).get("TOTAL")),
                                            String.valueOf(final_array.get(i).get("TotalQty")),
                                            String.valueOf(final_array.get(i).get("TotalValue"))};
                                    db.insert(values, columns, "dashboard_details");
                                }

                            }

                        }

                        db.close();
                        Log.d("Count is", "" + final_array.size());
                        Log.e("final_array", final_array.toString());


                    }

                } else {


                }
            } catch (Exception e) {
                // TODO: handle exception
                //flag="1";
                e.printStackTrace();
            }

            return returnMessage;

        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            prgdialog.setMessage("Please Wait...");
            prgdialog.show();
            prgdialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            prgdialog.dismiss();

        /*    for (int i = 0; i < final_array.size(); i++) {
                Log.d("In ginalk loop", "" + i);

*/

            db.open();


            Cursor c = db.getDashboardData(str_BOC);
            if (c != null && c.getCount() > 0) {
                //  Log.e("Cursor", DatabaseUtils.dumpCursorToString(c));
                c.moveToFirst();
                do {
                    View tr = (TableRow) View.inflate(BocCumulativeDashboardActivity.this,
                            R.layout.row_cumulative_dashboard, null);

                    TextView txt_date = (TextView) tr.findViewById(R.id.txt_date);
                    TextView txt_lh_saleValue = (TextView) tr.findViewById(R.id.txt_lh_saleValue);
                    TextView txt_lh_saleUnit = (TextView) tr.findViewById(R.id.txt_lh_saleUnit);
                    TextView txt_lm_saleValue = (TextView) tr.findViewById(R.id.txt_lm_saleValue);
                    TextView txt_lm_saleUnit = (TextView) tr.findViewById(R.id.txt_lm_saleUnit);
                    TextView txt_cumu_saleValue = (TextView) tr.findViewById(R.id.txt_cumu_saleValue);
                    TextView txt_cumu_saleUnit = (TextView) tr.findViewById(R.id.txt_cumu_saleUnit);


                    txt_date.setText(c.getString(c.getColumnIndex("AndroidCreatedDate")));
                    txt_date.setGravity(1);
                    txt_lh_saleValue.setText(c.getString(c.getColumnIndex("SkinSoldValue")));
                    txt_lh_saleValue.setMaxEms(10);
                    txt_lh_saleValue.setGravity(1);
                    txt_lh_saleUnit.setText(c.getString(c.getColumnIndex("SkinSoldQty")));
                    txt_lh_saleUnit.setGravity(1);
                    txt_lm_saleValue.setText(c.getString(c.getColumnIndex("ColorSoldValue")));
                    txt_lm_saleValue.setGravity(1);
                    txt_lm_saleUnit.setText(c.getString(c.getColumnIndex("ColorSoldQty")));
                    txt_lm_saleUnit.setGravity(1);
                    txt_cumu_saleValue.setText(c.getString(c.getColumnIndex("TotalValue")));
                    txt_cumu_saleValue.setGravity(1);
                    txt_cumu_saleUnit.setText(c.getString(c.getColumnIndex("TotalQty")));
                    txt_cumu_saleUnit.setGravity(1);
                    tl_cumulative.addView(tr);

                } while (c.moveToNext());
            }


            db.close();

               /* txt_date.setText(final_array.get(i).get("DATE"));
                txt_date.setGravity(1);

                txt_lh_saleValue.setText(final_array.get(i).get("SkinSoldValue"));
                txt_lh_saleValue.setMaxEms(10);
                txt_lh_saleValue.setGravity(1);
                txt_lh_saleUnit.setText(final_array.get(i).get("SkinSoldQty"));
                txt_lh_saleUnit.setGravity(1);
                txt_lm_saleValue.setText(final_array.get(i).get("ColorSoldValue"));
                txt_lm_saleValue.setGravity(1);
                txt_lm_saleUnit.setText(final_array.get(i).get("ColorSoldQty"));
                txt_lm_saleUnit.setGravity(1);
                txt_cumu_saleValue.setText(final_array.get(i).get("TotalValue"));
                txt_cumu_saleValue.setGravity(1);
                txt_cumu_saleUnit.setText(final_array.get(i).get("TotalQty"));
                txt_cumu_saleUnit.setGravity(1);
                tl_cumulative.addView(tr);
*/

        }
    }

    public class DashbardDataForDubai extends AsyncTask<Void, Void, String> {
        String returnMessage = "";

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                if (cd.isConnectingToInternet()) {
                    db.open();
                    SoapObject resultsRequestSOAP = null;
                    final String[] columns = new String[]{"BOC", "AndroidCreatedDate", "SoldQty", "Soldvalue"};

                    DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");

                    String startdate[];
                    if(role.equalsIgnoreCase("DUB")){
                         startdate = getStartEndForDubai(str_BOC, year);
                    }else{
                         startdate = getStartEnd(str_BOC, year, year1);
                    }


//						if(Type_radio_button.equalsIgnoreCase("ALL")){

                    // resultsRequestSOAP = service.GetDashboradData(0,  FromDate_date,  ToDate_date,  username, Type_radio_button);
                    resultsRequestSOAP = service.DubaiGetDashboardData(startdate[0], startdate[1], outletcode, username);
                    if (resultsRequestSOAP != null) {
                        final_array = new ArrayList<HashMap<String, String>>();
                        for (int i = 0; i < resultsRequestSOAP.getPropertyCount(); i++) {
                            SoapObject getmessaage = (SoapObject) resultsRequestSOAP.getProperty(i);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("DATE", dates_array.get(i));

                            if(getmessaage.getProperty("Datenew")!=null &&
                                    !getmessaage.getProperty("Datenew").toString().equalsIgnoreCase("anyType{}")){

                                map.put("Datenew", String.valueOf(getmessaage.getProperty("Datenew")));

                            }else{
                                map.put("Datenew", "0");
                            }
                            if(getmessaage.getProperty("SoldQty")!=null &&
                                    !getmessaage.getProperty("SoldQty").toString().equalsIgnoreCase("anyType{}")){

                                map.put("SoldQty", String.valueOf(getmessaage.getProperty("SoldQty")));

                            }else{
                                map.put("SoldQty", "0");
                            }
                            if(getmessaage.getProperty("Soldvalue")!=null &&
                                    !getmessaage.getProperty("Soldvalue").toString().equalsIgnoreCase("anyType{}")){

                                map.put("Soldvalue", String.valueOf(getmessaage.getProperty("Soldvalue")));

                            }else{
                                map.put("Soldvalue", "0");
                            }

                            final_array.add(map);
                        }


                        Cursor cursor = db.getDashboardDataforDubai(str_BOC);
                        {
                            if (cursor != null && cursor.getCount() > 0) {

                                db.deleteMulti("dashboard_details_dubai", "BOC=?", new String[]{str_BOC});
                                for (int i = 0; i < final_array.size(); i++) {
                                    String[] values = new String[]{str_BOC,
                                            String.valueOf(final_array.get(i).get("DATE")),
                                            String.valueOf(final_array.get(i).get("SoldQty")),
                                            String.valueOf(final_array.get(i).get("Soldvalue"))};
                                    db.insert(values, columns, "dashboard_details_dubai");
                                }
                            } else {

                                for (int i = 0; i < final_array.size(); i++) {
                                    String[] values = new String[]{str_BOC,
                                            String.valueOf(final_array.get(i).get("DATE")),
                                            String.valueOf(final_array.get(i).get("SoldQty")),
                                            String.valueOf(final_array.get(i).get("Soldvalue"))};
                                    db.insert(values, columns, "dashboard_details_dubai");
                                }

                            }

                        }

                        db.close();
                        Log.d("Count is", "" + final_array.size());
                        Log.e("final_array", final_array.toString());


                    }

                } else {


                }
            } catch (Exception e) {
                // TODO: handle exception
                //flag="1";
                e.printStackTrace();
            }

            return returnMessage;

        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            prgdialog.setMessage("Please Wait...");
            prgdialog.show();
            prgdialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            prgdialog.dismiss();

            db.open();

            Cursor c = db.getDashboardDataforDubai(str_BOC);
            if (c != null && c.getCount() > 0) {
                //  Log.e("Cursor", DatabaseUtils.dumpCursorToString(c));
                c.moveToFirst();
                do {
                    View tr = (TableRow) View.inflate(BocCumulativeDashboardActivity.this,
                            R.layout.inflate_commulative_row, null);

                    TextView txt_date = (TextView) tr.findViewById(R.id.txtdate1);
                    TextView txtvalue = (TextView) tr.findViewById(R.id.txtvalue);
                    TextView txtunit = (TextView) tr.findViewById(R.id.txtunit);


                    txt_date.setText(c.getString(c.getColumnIndex("AndroidCreatedDate")));
                    txt_date.setGravity(1);
                    txtvalue.setText(c.getString(c.getColumnIndex("SoldQty")));
                    txtvalue.setGravity(1);
                    txtunit.setText(c.getString(c.getColumnIndex("Soldvalue")));
                    txtunit.setGravity(1);
                    tl_cumulativeD.addView(tr);

                } while (c.moveToNext());
            }


            db.close();

        }
    }


    public String[] getStartEndForDubai(String Month, String year) {
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


}
