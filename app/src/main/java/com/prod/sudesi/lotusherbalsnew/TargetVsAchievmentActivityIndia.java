package com.prod.sudesi.lotusherbalsnew;

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
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.Models.AchivementModel;
import com.prod.sudesi.lotusherbalsnew.Models.DashboardModel;
import com.prod.sudesi.lotusherbalsnew.TableFixHeader.AchievementTableAdapter;
import com.prod.sudesi.lotusherbalsnew.TableFixHeader.DashboardTableAdapter;
import com.prod.sudesi.lotusherbalsnew.TableFixHeader.TableFixHeaders;
import com.prod.sudesi.lotusherbalsnew.adapter.BAReportAdapter;
import com.prod.sudesi.lotusherbalsnew.adapter.BAReportAdapterDubai;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import org.ksoap2.serialization.SoapObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TargetVsAchievmentActivityIndia extends Activity {

    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;

    Context context;

    private BAReportAdapter adapter;

    LotusWebservice service;

    static public ArrayList<HashMap<String, String>> todaymessagelist = new ArrayList<HashMap<String, String>>();

    TextView tv_h_username;
    Button btn_home, btn_logout;
    String username, bdename, role, outletcode;

    //TextView tv_current_year_n1, tv_current_year_n2, tv_previous_year_p1, tv_previous_year_p2;//, tvPreviousyear, tvCurrentyear;

    //String current_year_n1, current_year_n2, previous_year_p1, previous_year_p2;

    //int int_current_year_n1, int_current_year_n2, int_previous_year_p1, int_previous_year_p2;

    String current_server_date;

    ConnectionDetector cd;

    TableRow yearlayout;

    String PreviousYear, CurrentYear;

    private ArrayList<String> categoryDetailsArraylist;
    private Dbcon db;

    TableFixHeaders tableFixHeaders;
    ArrayList<AchivementModel> achivementModelArrayList = new ArrayList<>();
    AchievementTableAdapter<String> achievementTableAdapter;

    //String currentyear,prevyear,preprevyear;

    String curryear,prepryear,preyear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_targetvsachivement_india);
        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        context = TargetVsAchievmentActivityIndia.this;

        service = new LotusWebservice(TargetVsAchievmentActivityIndia.this);
        cd = new ConnectionDetector(TargetVsAchievmentActivityIndia.this);

        shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
        shpeditor = shp.edit();

        db = new Dbcon(TargetVsAchievmentActivityIndia.this);

        role = shp.getString("Role", "");
        outletcode = shp.getString("FLRCode", "");

        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        yearlayout = (TableRow) findViewById(R.id.yearlayout);

        username = shp.getString("username", "");
        Log.v("", "username==" + username);

        bdename = shp.getString("BDEusername", "");

        tv_h_username.setText(bdename);

        tableFixHeaders = (TableFixHeaders) findViewById(R.id.trgtachiv_table);

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

       /* fetchCategoryDetails();


        try {
            new GetBAreport().execute();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }*/

        /*tv_current_year_n1 = (TextView) findViewById(R.id.tv_bar_Current_year_n1);
        tv_current_year_n2 = (TextView) findViewById(R.id.tv_bar_Current_year_n2);
        tv_previous_year_p1 = (TextView) findViewById(R.id.tv_bar_Previous_year_p1);
        tv_previous_year_p2 = (TextView) findViewById(R.id.tv_bar_Previous_year_p2);*/

        //tvPreviousyear = (TextView) findViewById(R.id.tvPreviousyear);
        //tvCurrentyear = (TextView) findViewById(R.id.tvCurrentyear);

       /* current_year_n1 = tv_current_year_n1.getText().toString();
        current_year_n2 = tv_current_year_n2.getText().toString();

        previous_year_p1 = tv_previous_year_p1.getText().toString();
        previous_year_p2 = tv_previous_year_p2.getText().toString();

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
        }*/

         curryear = shp.getString("current_year", "");

         prepryear = String.valueOf(Integer.parseInt(curryear) - 2);
         preyear = String.valueOf(Integer.parseInt(curryear) - 1);

        //currentyear = curryear.substring(2);
        //prevyear = preyear.substring(2);
        //preprevyear = prepryear.substring(2);


        categoryDetailsArraylist = new ArrayList<String>();


        db.open();
        categoryDetailsArraylist = db.getproductcategorywithoutselect(username);
        db.close();

        if(categoryDetailsArraylist.size()>0 && categoryDetailsArraylist != null) {
            new GetBAreport().execute();
        }else{
            Toast.makeText(this,"Record not found plz try again", Toast.LENGTH_SHORT).show();
        }


    }


    public class GetBAreport extends AsyncTask<Void, Void, String> {
        String returnMessage = null;

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
                todaymessagelist.clear();

                if (!cd.isConnectingToInternet()) {

                    returnMessage = "1";
                } else {

                    returnMessage = getBAreportfromWebservice();
                }

            } catch (Exception e) {
                returnMessage = "1";
                e.getMessage();
            }
            return returnMessage;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (TargetVsAchievmentActivityIndia.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            cd.dismissProgressDialog();

            if (result.equalsIgnoreCase("1")) {

                Toast.makeText(TargetVsAchievmentActivityIndia.this, "Connectivity Error!! Please try again", Toast.LENGTH_SHORT).show();
            } else {

                fetchCategoryDetails();

            }
        }
    }


    private String getBAreportfromWebservice() {
        // TODO Auto-generated method stub
        String flag = "0";
        AchivementModel achivementModel;
        try {

            SoapObject resultsRequestSOAP = null;

            // //soap call
            Log.e("", "not2==" + username);
            resultsRequestSOAP = service.GetAchievementReport(username);

            if (resultsRequestSOAP != null) {
                Log.e("", "not3");
                Log.e("", "not4");
                // soap response
                for (int i = 0; i < resultsRequestSOAP.getPropertyCount(); i++) {
                    Log.e("", "not5");

                    SoapObject getmessaage = (SoapObject) resultsRequestSOAP
                            .getProperty(i);

                    achivementModel = new AchivementModel();

                    if (getmessaage != null) {

                        achivementModel.setBoc(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("MONTH"))));

                        if (categoryDetailsArraylist != null) {
                            if (categoryDetailsArraylist.size() > 0) {
                                if (categoryDetailsArraylist.size() == 5) {
                                    achivementModel.setNAPrefirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP1"))));
                                    achivementModel.setNAPresecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP2"))));
                                    achivementModel.setNAPrethird(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP3"))));
                                    achivementModel.setNAPrefourth(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP4"))));
                                    achivementModel.setNAPrefifth(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP5"))));

                                    achivementModel.setTACurfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount1"))));
                                    achivementModel.setTACursecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount2"))));
                                    achivementModel.setTACurthird(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount3"))));
                                    achivementModel.setTACurfourth(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount4"))));
                                    achivementModel.setTACurfifth(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount5"))));

                                    achivementModel.setNACurfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC1"))));
                                    achivementModel.setNACursecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC2"))));
                                    achivementModel.setNACurthird(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC3"))));
                                    achivementModel.setNACurfourth(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC4"))));
                                    achivementModel.setNACurfifth(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC5"))));

                                    achivementModel.setGrowthfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth1"))));
                                    achivementModel.setGrowthsecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth2"))));
                                    achivementModel.setGrowththird(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth3"))));
                                    achivementModel.setGrowthfourth(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth4"))));
                                    achivementModel.setGrowthfifth(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth5"))));

                                }else if (categoryDetailsArraylist.size() == 4) {
                                    achivementModel.setNAPrefirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP1"))));
                                    achivementModel.setNAPresecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP2"))));
                                    achivementModel.setNAPrethird(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP3"))));
                                    achivementModel.setNAPrefourth(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP4"))));

                                    achivementModel.setTACurfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount1"))));
                                    achivementModel.setTACursecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount2"))));
                                    achivementModel.setTACurthird(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount3"))));
                                    achivementModel.setTACurfourth(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount4"))));

                                    achivementModel.setNACurfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC1"))));
                                    achivementModel.setNACursecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC2"))));
                                    achivementModel.setNACurthird(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC3"))));
                                    achivementModel.setNACurfourth(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC4"))));

                                    achivementModel.setGrowthfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth1"))));
                                    achivementModel.setGrowthsecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth2"))));
                                    achivementModel.setGrowththird(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth3"))));
                                    achivementModel.setGrowthfourth(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth4"))));

                                }else if (categoryDetailsArraylist.size() == 3) {
                                    achivementModel.setNAPrefirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP1"))));
                                    achivementModel.setNAPresecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP2"))));
                                    achivementModel.setNAPrethird(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP3"))));

                                    achivementModel.setTACurfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount1"))));
                                    achivementModel.setTACursecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount2"))));
                                    achivementModel.setTACurthird(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount3"))));

                                    achivementModel.setNACurfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC1"))));
                                    achivementModel.setNACursecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC2"))));
                                    achivementModel.setNACurthird(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC3"))));

                                    achivementModel.setGrowthfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth1"))));
                                    achivementModel.setGrowthsecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth2"))));
                                    achivementModel.setGrowththird(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth3"))));

                                }else if (categoryDetailsArraylist.size() == 2) {

                                    achivementModel.setNAPrefirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP1"))));
                                    achivementModel.setNAPresecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP2"))));
                                    achivementModel.setTACurfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount1"))));
                                    achivementModel.setTACursecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount2"))));
                                    achivementModel.setNACurfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC1"))));
                                    achivementModel.setNACursecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC2"))));
                                    achivementModel.setGrowthfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth1"))));
                                    achivementModel.setGrowthsecond(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth2"))));

                                }else{
                                    achivementModel.setNAPrefirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountP1"))));
                                    achivementModel.setTACurfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("TargetAmount1"))));
                                    achivementModel.setNACurfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("NetAmountC1"))));
                                    achivementModel.setGrowthfirst(cd.getNonNullValues(String.valueOf(getmessaage.getProperty("Growth1"))));
                                }
                            }
                        }

                        achivementModelArrayList.add(achivementModel);
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


    @Override
    protected void onDestroy() {
        cd.dismissProgressDialog();
        super.onDestroy();
    }

    public void fetchCategoryDetails() {

        tableFixHeaders.setVisibility(View.VISIBLE);
        AchivementModel achivementModel= new AchivementModel();

        ArrayList<HashMap> reportList;
        List<String> list1 = new ArrayList<String>();
        Map<Integer, ArrayList<HashMap>> hashMapArrayList = new HashMap<>();
        HashMap<Integer, String> map1;


        try {
            achivementModel.setBoc("BOC");

            if (categoryDetailsArraylist != null) {
                if (categoryDetailsArraylist.size() > 0) {
                    if (categoryDetailsArraylist.size() == 5) {
                        achivementModel.setNAPrefirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + " Achievement" + "\n" + prepryear + " - " + preyear);
                        achivementModel.setNAPresecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + " Achievement" + "\n" + prepryear + " - " + preyear);

                        achivementModel.setNAPrethird(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + "\n" + " Achievement" + "\n" + prepryear + " - " + preyear);
                        achivementModel.setNAPrefourth(cd.getNonNullValues(categoryDetailsArraylist.get(3)) + "\n" + " Achievement" + "\n" + prepryear + " - " + preyear);
                        achivementModel.setNAPrefifth(cd.getNonNullValues(categoryDetailsArraylist.get(4)) + "\n" + " Achievement" + "\n" + prepryear + " - " + preyear);

                        achivementModel.setTACurfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + " Target" + "\n" + preyear + " - " + curryear);
                        achivementModel.setTACursecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + " Target" + "\n" + preyear + " - " + curryear);

                        achivementModel.setTACurthird(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + "\n" + " Target" + "\n" + preyear + " - " + curryear);
                        achivementModel.setTACurfourth(cd.getNonNullValues(categoryDetailsArraylist.get(3)) + "\n" + " Target" + "\n" + preyear + " - " + curryear);
                        achivementModel.setTACurfifth(cd.getNonNullValues(categoryDetailsArraylist.get(4)) + "\n" + " Target" + "\n" + preyear + " - " + curryear);

                        achivementModel.setNACurfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + " Achievement" + "\n" + preyear + " - " + curryear);
                        achivementModel.setNACursecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + " Achievement" + "\n" + preyear + " - " + curryear);

                        achivementModel.setNACurthird(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + "\n" + " Achievement" + "\n" + preyear + " - " + curryear);
                        achivementModel.setNACurfourth(cd.getNonNullValues(categoryDetailsArraylist.get(3)) + "\n" + " Achievement" + "\n" + preyear + " - " + curryear);
                        achivementModel.setNACurfifth(cd.getNonNullValues(categoryDetailsArraylist.get(4)) + "\n" + " Achievement" + "\n" + preyear + " - " + curryear);

                        achivementModel.setGrowthfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + " Growth" + "\n" + preyear + " - " + curryear);
                        achivementModel.setGrowthsecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + " Growth" + "\n" + preyear + " - " + curryear);

                        achivementModel.setGrowththird(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + "\n" + " Growth" + "\n" + preyear + " - " + curryear);
                        achivementModel.setGrowthfourth(cd.getNonNullValues(categoryDetailsArraylist.get(3)) + "\n" + " Growth" + "\n" + preyear + " - " + curryear);
                        achivementModel.setGrowthfifth(cd.getNonNullValues(categoryDetailsArraylist.get(4)) + "\n" + " Growth" + "\n" + preyear + " - " + curryear);


                    }else if (categoryDetailsArraylist.size() == 4) {
                        achivementModel.setNAPrefirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + " Achievement" + "\n" + prepryear + " - " + preyear);
                        achivementModel.setNAPresecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + " Achievement" + "\n" + prepryear + " - " + preyear);

                        achivementModel.setNAPrethird(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + "\n" + " Achievement" + "\n" + prepryear + " - " + preyear);
                        achivementModel.setNAPrefourth(cd.getNonNullValues(categoryDetailsArraylist.get(3)) + "\n" + " Achievement" + "\n" + prepryear + " - " + preyear);

                        achivementModel.setTACurfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + " Target" + "\n" + preyear + " - " + curryear);
                        achivementModel.setTACursecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + " Target" + "\n" + preyear + " - " + curryear);

                        achivementModel.setTACurthird(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + "\n" + " Target" + "\n" + preyear + " - " + curryear);
                        achivementModel.setTACurfourth(cd.getNonNullValues(categoryDetailsArraylist.get(3)) + "\n" + " Target" + "\n" + preyear + " - " + curryear);

                        achivementModel.setNACurfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + " Achievement" + "\n" + preyear + " - " + curryear);
                        achivementModel.setNACursecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + " Achievement" + "\n" + preyear + " - " + curryear);

                        achivementModel.setNACurthird(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + "\n" + " Achievement" + "\n" + preyear + " - " + curryear);
                        achivementModel.setNACurfourth(cd.getNonNullValues(categoryDetailsArraylist.get(3)) + "\n" + " Achievement" + "\n" + preyear + " - " + curryear);

                        achivementModel.setGrowthfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + " Growth" + "\n" + preyear + " - " + curryear);
                        achivementModel.setGrowthsecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + " Growth" + "\n" + preyear + " - " + curryear);

                        achivementModel.setGrowththird(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + "\n" + " Growth" + "\n" + preyear + " - " + curryear);
                        achivementModel.setGrowthfourth(cd.getNonNullValues(categoryDetailsArraylist.get(3)) + "\n" + " Growth" + "\n" + preyear + " - " + curryear);


                    }else if (categoryDetailsArraylist.size() == 3) {
                        achivementModel.setNAPrefirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + " Achievement" + "\n" + prepryear + " - " + preyear);
                        achivementModel.setNAPresecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + " Achievement" + "\n" + prepryear + " - " + preyear);

                        achivementModel.setNAPrethird(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + "\n" + " Achievement" + "\n" + prepryear + " - " + preyear);

                        achivementModel.setTACurfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + " Target" + "\n" + preyear + " - " + curryear);
                        achivementModel.setTACursecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + " Target" + "\n" + preyear + " - " + curryear);

                        achivementModel.setTACurthird(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + "\n" + " Target" + "\n" + preyear + " - " + curryear);

                        achivementModel.setNACurfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + " Achievement" + "\n" + preyear + " - " + curryear);
                        achivementModel.setNACursecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + " Achievement" + "\n" + preyear + " - " + curryear);

                        achivementModel.setNACurthird(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + "\n" + " Achievement" + "\n" + preyear + " - " + curryear);

                        achivementModel.setGrowthfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + " Growth" + "\n" + preyear + " - " + curryear);
                        achivementModel.setGrowthsecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + " Growth" + "\n" + preyear + " - " + curryear);

                        achivementModel.setGrowththird(cd.getNonNullValues(categoryDetailsArraylist.get(2)) + "\n" + " Growth" + "\n" + preyear + " - " + curryear);


                    }else if (categoryDetailsArraylist.size() == 2) {

                        achivementModel.setNAPrefirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + "Achievement" + "\n" + prepryear + " - " + preyear);
                        achivementModel.setNAPresecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + "Achievement" + "\n" + prepryear + " - " + preyear);
                        achivementModel.setTACurfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + "Target" + "\n" + preyear + " - " + curryear);
                        achivementModel.setTACursecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + "Target" + "\n" + preyear + " - " + curryear);
                        achivementModel.setNACurfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + "Achievement" + "\n" + preyear + " - " + curryear);
                        achivementModel.setNACursecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + "Achievement" + "\n" + preyear + " - " + curryear);
                        achivementModel.setGrowthfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + "Growth" + "\n" + preyear + " - " + curryear);
                        achivementModel.setGrowthsecond(cd.getNonNullValues(categoryDetailsArraylist.get(1)) + "\n" + "Growth" + "\n" + preyear + " - " + curryear);

                    } else {

                        achivementModel.setNAPrefirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + "Achievement" + "\n" + prepryear + " - " + preyear);
                        achivementModel.setTACurfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + "Target" + "\n" + preyear + " - " + curryear);
                        achivementModel.setNACurfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + "Achievement" + "\n" + preyear + " - " + curryear);
                        achivementModel.setGrowthfirst(cd.getNonNullValues(categoryDetailsArraylist.get(0)) + "\n" + "Growth" + "\n" + preyear + " - " + curryear);

                    }

                }
            }

            achivementModelArrayList.add(0, achivementModel);


            for (int i = 0; i < achivementModelArrayList.size(); i++) {
                final AchivementModel achivementModel1 = achivementModelArrayList.get(i);
                reportList = new ArrayList<>();
                list1 = new ArrayList<String>();
                list1.add(achivementModel1.getBoc());

                if (categoryDetailsArraylist != null) {
                    if (categoryDetailsArraylist.size() > 0) {
                        if (categoryDetailsArraylist.size() == 5) {

                            list1.add(achivementModel1.getNAPrefirst());
                            list1.add(achivementModel1.getNAPresecond());

                            list1.add(achivementModel1.getNAPrethird());
                            list1.add(achivementModel1.getNAPrefourth());
                            list1.add(achivementModel1.getNAPrefifth());

                            list1.add(achivementModel1.getTACurfirst());
                            list1.add(achivementModel1.getTACursecond());

                            list1.add(achivementModel1.getTACurthird());
                            list1.add(achivementModel1.getTACurfourth());
                            list1.add(achivementModel1.getTACurfifth());

                            list1.add(achivementModel1.getNACurfirst());
                            list1.add(achivementModel1.getNACursecond());

                            list1.add(achivementModel1.getNACurthird());
                            list1.add(achivementModel1.getNACurfourth());
                            list1.add(achivementModel1.getNACurfifth());

                            list1.add(achivementModel1.getGrowthfirst());
                            list1.add(achivementModel1.getGrowthsecond());

                            list1.add(achivementModel1.getGrowththird());
                            list1.add(achivementModel1.getGrowthfourth());
                            list1.add(achivementModel1.getGrowthfifth());

                        }else if (categoryDetailsArraylist.size() == 4) {

                            list1.add(achivementModel1.getNAPrefirst());
                            list1.add(achivementModel1.getNAPresecond());

                            list1.add(achivementModel1.getNAPrethird());
                            list1.add(achivementModel1.getNAPrefourth());

                            list1.add(achivementModel1.getTACurfirst());
                            list1.add(achivementModel1.getTACursecond());

                            list1.add(achivementModel1.getTACurthird());
                            list1.add(achivementModel1.getTACurfourth());

                            list1.add(achivementModel1.getNACurfirst());
                            list1.add(achivementModel1.getNACursecond());

                            list1.add(achivementModel1.getNACurthird());
                            list1.add(achivementModel1.getNACurfourth());

                            list1.add(achivementModel1.getGrowthfirst());
                            list1.add(achivementModel1.getGrowthsecond());

                            list1.add(achivementModel1.getGrowththird());
                            list1.add(achivementModel1.getGrowthfourth());

                        }else if (categoryDetailsArraylist.size() == 3) {

                            list1.add(achivementModel1.getNAPrefirst());
                            list1.add(achivementModel1.getNAPresecond());

                            list1.add(achivementModel1.getNAPrethird());

                            list1.add(achivementModel1.getTACurfirst());
                            list1.add(achivementModel1.getTACursecond());

                            list1.add(achivementModel1.getTACurthird());

                            list1.add(achivementModel1.getNACurfirst());
                            list1.add(achivementModel1.getNACursecond());

                            list1.add(achivementModel1.getNACurthird());

                            list1.add(achivementModel1.getGrowthfirst());
                            list1.add(achivementModel1.getGrowthsecond());

                            list1.add(achivementModel1.getGrowththird());

                        }else if (categoryDetailsArraylist.size() == 2) {

                            list1.add(achivementModel1.getNAPrefirst());
                            list1.add(achivementModel1.getNAPresecond());
                            list1.add(achivementModel1.getTACurfirst());
                            list1.add(achivementModel1.getTACursecond());
                            list1.add(achivementModel1.getNACurfirst());
                            list1.add(achivementModel1.getNACursecond());
                            list1.add(achivementModel1.getGrowthfirst());
                            list1.add(achivementModel1.getGrowthsecond());

                        } else {

                            list1.add(achivementModel1.getNAPrefirst());
                            list1.add(achivementModel1.getTACurfirst());
                            list1.add(achivementModel1.getNACurfirst());
                            list1.add(achivementModel1.getGrowthfirst());

                        }

                    }
                }

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

            reportArray = new String[achivementModelArrayList.size()][list1.size()];//{"Header 1","Header 2","Header 3","Header 4","Header 5","Header 6"});

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
                achievementTableAdapter = new AchievementTableAdapter<String>(context, reportArray);
                tableFixHeaders.setAdapter(achievementTableAdapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
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
}
