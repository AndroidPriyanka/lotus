package com.prod.sudesi.lotusherbalsnew;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.Models.DashboardModel;
import com.prod.sudesi.lotusherbalsnew.TableFixHeader.DashboardTableAdapter;
import com.prod.sudesi.lotusherbalsnew.TableFixHeader.TableFixHeaders;
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
import java.util.Map;

public class BocCumulativeDashboardActivityIndia extends Activity {

    TextView txt_boc, txt_year;

    String str_BOC, str_year;
    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;
    Dbcon dbcon;
    Button btn_home;
    TextView tv_h_username;
    String username, bdename, role, outletcode;
    ArrayList<String> dates_array;
    private Context context;
    LotusWebservice service;
    Date startdate, enddate;
    String year, year1;
    private Dbcon db;
    ConnectionDetector cd;

    private ArrayList<String> categoryDetailsArraylist;

    TableFixHeaders tableFixHeaders;
    ArrayList<DashboardModel> dashboardModelArrayList = new ArrayList<>();
    DashboardTableAdapter<String> dashboardTableAdapter;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard_cumulative_india);
        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        dbcon = new Dbcon(BocCumulativeDashboardActivityIndia.this);
        dbcon.open();
        context = getApplicationContext();
        shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
        shpeditor = shp.edit();

        username = shp.getString("username", "");
        bdename = shp.getString("BDEusername", "");
        role = shp.getString("Role", "");
        outletcode = shp.getString("FLRCode", "");

        txt_boc = (TextView) findViewById(R.id.txt_boc);
        txt_year = (TextView) findViewById(R.id.txt_year);

        tableFixHeaders = (TableFixHeaders) findViewById(R.id.dashboard_table);


        db = new Dbcon(BocCumulativeDashboardActivityIndia.this);
        cd = new ConnectionDetector(BocCumulativeDashboardActivityIndia.this);

        service = new LotusWebservice(BocCumulativeDashboardActivityIndia.this);


        Intent intent = getIntent();
        str_BOC = intent.getStringExtra("month");
        String y[] = intent.getStringExtra("year").split("-");

//		str_year = intent.getStringExtra("");
        Log.e("str_BOC", str_BOC);

        year = y[0];
        year1 = y[1];

        txt_boc.setText(str_BOC);
        if(year.equalsIgnoreCase(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))) {
            txt_year.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        }else{
            txt_year.setText(year);
        }

        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        tv_h_username.setText(bdename);

        btn_home = (Button) findViewById(R.id.btn_home);

        btn_home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent i = new Intent(getApplicationContext(),
                        DashboardNewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });


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
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

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

        categoryDetailsArraylist = new ArrayList<String>();


        db.open();
        categoryDetailsArraylist = db.getproductcategorywithoutselect(username);
        db.close();

        if (categoryDetailsArraylist.size() > 0 && categoryDetailsArraylist != null) {
            new DashbardData().execute();
        } else {
            Toast.makeText(this, "Record not found plz try again", Toast.LENGTH_SHORT).show();
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

        DashboardModel dashboardModel;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            String msg = "Please Wait....";
            cd.showProgressDialog(msg);
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                if (cd.isConnectingToInternet()) {
                    //db.open();
                    SoapObject resultsRequestSOAP = null;

                    String startdate[] = getStartEnd(str_BOC, year, year1);

                    resultsRequestSOAP = service.GetDashboradData("2", startdate[0], startdate[1], username, "ALL");
                    if (resultsRequestSOAP != null) {
                        for (int i = 0; i < resultsRequestSOAP.getPropertyCount(); i++) {
                            SoapObject getmessaage = (SoapObject) resultsRequestSOAP.getProperty(i);
                            dashboardModel = new DashboardModel();

                            dashboardModel.setDate(dates_array.get(i));

                            if (categoryDetailsArraylist != null) {
                                if (categoryDetailsArraylist.size() > 0) {

                                    if(categoryDetailsArraylist.size() == 5){
                                        dashboardModel.setFirstvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue1"))));
                                        dashboardModel.setFirstunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty1"))));
                                        dashboardModel.setSecondvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue2"))));
                                        dashboardModel.setSecondunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty2"))));
                                        dashboardModel.setThirdvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue3"))));
                                        dashboardModel.setThirdunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty3"))));
                                        dashboardModel.setFourthvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue4"))));
                                        dashboardModel.setFourthunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty4"))));
                                        dashboardModel.setFifthvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue5"))));
                                        dashboardModel.setFifthunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty5"))));

                                    }else if(categoryDetailsArraylist.size() == 4){
                                        dashboardModel.setFirstvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue1"))));
                                        dashboardModel.setFirstunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty1"))));
                                        dashboardModel.setSecondvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue2"))));
                                        dashboardModel.setSecondunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty2"))));
                                        dashboardModel.setThirdvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue3"))));
                                        dashboardModel.setThirdunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty3"))));
                                        dashboardModel.setFourthvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue4"))));
                                        dashboardModel.setFourthunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty4"))));

                                    }else if(categoryDetailsArraylist.size() == 3){
                                        dashboardModel.setFirstvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue1"))));
                                        dashboardModel.setFirstunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty1"))));
                                        dashboardModel.setSecondvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue2"))));
                                        dashboardModel.setSecondunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty2"))));
                                        dashboardModel.setThirdvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue3"))));
                                        dashboardModel.setThirdunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty3"))));

                                    }else if (categoryDetailsArraylist.size() == 2) {
                                        dashboardModel.setFirstvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue1"))));
                                        dashboardModel.setFirstunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty1"))));
                                        dashboardModel.setSecondvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue2"))));
                                        dashboardModel.setSecondunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty2"))));

                                    } else {
                                        dashboardModel.setFirstvalue(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldValue1"))));
                                        dashboardModel.setFirstunit(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("SoldQty1"))));

                                    }

                                }
                            }

                            dashboardModel.setTotalvalue(String.valueOf(getmessaage.getProperty("TotalValue")));
                            dashboardModel.setTotalunit(String.valueOf(getmessaage.getProperty("TotalQty")));

                            dashboardModelArrayList.add(dashboardModel);
                        }

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
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (BocCumulativeDashboardActivityIndia.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            cd.dismissProgressDialog();

            fetchCategoryDetails();

        }
    }

    public String getValue(String inputStr) {
        String value = "";
        if (inputStr.contains("=")) {
            String[] valueArr = inputStr.split("=");
            String subStr = valueArr[1];
            value = subStr.substring(0, subStr.indexOf("}"));
        }
        return value;
    }

    @Override
    protected void onDestroy() {
        cd.dismissProgressDialog();
        super.onDestroy();
    }

    public void fetchCategoryDetails() {
        //new changes

        tableFixHeaders.setVisibility(View.VISIBLE);
        DashboardModel dashboardModel;

        ArrayList<HashMap> reportList;
        List<String> list1 = new ArrayList<String>();
        Map<Integer, ArrayList<HashMap>> hashMapArrayList = new HashMap<>();
        HashMap<Integer, String> map1;

        dashboardModel = new DashboardModel();

        try {
            dashboardModel.setDate("Date");

            if (categoryDetailsArraylist != null) {
                if (categoryDetailsArraylist.size() > 0) {
                    if(categoryDetailsArraylist.size() == 5){
                        dashboardModel.setFirstvalue(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + " Value");
                        dashboardModel.setFirstunit(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + " Unit");
                        dashboardModel.setSecondvalue(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + " Value");
                        dashboardModel.setSecondunit(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + " Unit");
                        dashboardModel.setThirdvalue(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + " Value");
                        dashboardModel.setThirdunit(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + " Unit");
                        dashboardModel.setFourthvalue(cd.getNonNullValues(categoryDetailsArraylist.get(3)) + " Value");
                        dashboardModel.setFourthunit(cd.getNonNullValues(categoryDetailsArraylist.get(3)) + " Unit");
                        dashboardModel.setFifthvalue(cd.getNonNullValues(categoryDetailsArraylist.get(4)) + " Value");
                        dashboardModel.setFifthunit(cd.getNonNullValues(categoryDetailsArraylist.get(4)) + " Unit");

                    }else if(categoryDetailsArraylist.size() == 4){
                        dashboardModel.setFirstvalue(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + " Value");
                        dashboardModel.setFirstunit(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + " Unit");
                        dashboardModel.setSecondvalue(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + " Value");
                        dashboardModel.setSecondunit(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + " Unit");
                        dashboardModel.setThirdvalue(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + " Value");
                        dashboardModel.setThirdunit(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + " Unit");
                        dashboardModel.setFourthvalue(cd.getNonNullValues(categoryDetailsArraylist.get(3)) + " Value");
                        dashboardModel.setFourthunit(cd.getNonNullValues(categoryDetailsArraylist.get(3)) + " Unit");

                    }else if(categoryDetailsArraylist.size() == 3){
                        dashboardModel.setFirstvalue(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + " Value");
                        dashboardModel.setFirstunit(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + " Unit");
                        dashboardModel.setSecondvalue(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + " Value");
                        dashboardModel.setSecondunit(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + " Unit");
                        dashboardModel.setThirdvalue(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + " Value");
                        dashboardModel.setThirdunit(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + " Unit");

                    }else if (categoryDetailsArraylist.size() == 2) {
                        dashboardModel.setFirstvalue(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + " Value");
                        dashboardModel.setFirstunit(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + " Unit");
                        dashboardModel.setSecondvalue(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + " Value");
                        dashboardModel.setSecondunit(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + " Unit");

                    } else {
                        dashboardModel.setFirstvalue(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + " Value");
                        dashboardModel.setFirstunit(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + " Unit");

                    }

                }
            }

            dashboardModel.setTotalvalue("Total Value");
            dashboardModel.setTotalunit("Total Unit");

            dashboardModelArrayList.add(0, dashboardModel);


            for (int i = 0; i < dashboardModelArrayList.size(); i++) {
                final DashboardModel dashboardModel1 = dashboardModelArrayList.get(i);
                reportList = new ArrayList<>();
                list1 = new ArrayList<String>();
                list1.add(dashboardModel1.getDate());

                if (categoryDetailsArraylist != null) {
                    if (categoryDetailsArraylist.size() > 0) {
                        if(categoryDetailsArraylist.size() == 5){
                            list1.add(dashboardModel1.getFirstvalue());
                            list1.add(dashboardModel1.getFirstunit());
                            list1.add(dashboardModel1.getSecondvalue());
                            list1.add(dashboardModel1.getSecondunit());
                            list1.add(dashboardModel1.getThirdvalue());
                            list1.add(dashboardModel1.getThirdunit());
                            list1.add(dashboardModel1.getFourthvalue());
                            list1.add(dashboardModel1.getFourthunit());
                            list1.add(dashboardModel1.getFifthvalue());
                            list1.add(dashboardModel1.getFifthunit());

                        }else if(categoryDetailsArraylist.size() == 4){
                            list1.add(dashboardModel1.getFirstvalue());
                            list1.add(dashboardModel1.getFirstunit());
                            list1.add(dashboardModel1.getSecondvalue());
                            list1.add(dashboardModel1.getSecondunit());
                            list1.add(dashboardModel1.getThirdvalue());
                            list1.add(dashboardModel1.getThirdunit());
                            list1.add(dashboardModel1.getFourthvalue());
                            list1.add(dashboardModel1.getFourthunit());

                        }else if(categoryDetailsArraylist.size() == 3){
                            list1.add(dashboardModel1.getFirstvalue());
                            list1.add(dashboardModel1.getFirstunit());
                            list1.add(dashboardModel1.getSecondvalue());
                            list1.add(dashboardModel1.getSecondunit());
                            list1.add(dashboardModel1.getThirdvalue());
                            list1.add(dashboardModel1.getThirdunit());

                        }else if (categoryDetailsArraylist.size() == 2) {
                            list1.add(dashboardModel1.getFirstvalue());
                            list1.add(dashboardModel1.getFirstunit());
                            list1.add(dashboardModel1.getSecondvalue());
                            list1.add(dashboardModel1.getSecondunit());

                        } else {
                            list1.add(dashboardModel1.getFirstvalue());
                            list1.add(dashboardModel1.getFirstunit());

                        }

                    }
                }

                list1.add(dashboardModel1.getTotalvalue());
                list1.add(dashboardModel1.getTotalunit());


                for (int k = 0; k < list1.size(); k++) {
                    map1 = new HashMap<Integer, String>();
                    map1.put(k, list1.get(k));
                    reportList.add(map1);
                }
                hashMapArrayList.put(i, reportList);
            }
            String[] reportArr = new String[list1.size()];
            reportArr = list1.toArray(reportArr);
            String[][] reportArray;

            reportArray = new String[dashboardModelArrayList.size()][list1.size()];//{"Header 1","Header 2","Header 3","Header 4","Header 5","Header 6"});

            for (Integer key : hashMapArrayList.keySet()) {
                System.out.println("key : " + key);
                System.out.println("value : " + hashMapArrayList.get(key));

                for (int x = 0; x < hashMapArrayList.get(key).get(0).size(); x++) {
                    for (int y = 0; y < reportArray[x].length; y++) {
                        reportArray[key][y] = getValue(hashMapArrayList.get(key).get(y).toString());

                    }
                }
            }// end of outer for
            if (reportArray != null) {
                dashboardTableAdapter = new DashboardTableAdapter<String>(context, reportArray);
                tableFixHeaders.setAdapter(dashboardTableAdapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
