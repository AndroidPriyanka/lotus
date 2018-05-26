package com.prod.sudesi.lotusherbalsnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.Models.AttendanceModel;
import com.prod.sudesi.lotusherbalsnew.Models.OutletModel;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mahesh on 5/9/2018.
 */

public class OutletActivity extends Activity {

    Button btn_proceed, btn_home, btn_logout;
    TextView tv_h_username;
    AutoCompleteTextView outlet;
    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;
    LotusWebservice service;
    private ProgressDialog pd;
    ConnectionDetector cd;
    Dbcon db;
    String flotername, bdename, check, flrid, lhrid, currentdate;
    OutletModel outletModel;
    private ArrayList<OutletModel> outletDetailsArraylist;
    String[] strOutletArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_outlet);

        shp = getSharedPreferences("Lotus", MODE_PRIVATE);
        shpeditor = shp.edit();

        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        outlet = (AutoCompleteTextView) findViewById(R.id.spin_outlet);

        pd = new ProgressDialog(this);
        service = new LotusWebservice(this);
        cd = new ConnectionDetector(this);
        db = new Dbcon(this);

        flotername = shp.getString("username", "");
        bdename = shp.getString("BDEusername", "");
        tv_h_username.setText(bdename);

        btn_home.setVisibility(View.GONE);

        btn_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        try {
            Intent i = getIntent();
            check = i.getStringExtra("FromAttendancefloter");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (check.equalsIgnoreCase("AF")) {
            try {
                new Check_outlet().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            fetchoutletdetails();
        }

        outlet.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                outlet.showDropDown();
                return false;
            }
        });


        outlet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (strOutletArray != null && strOutletArray.length > 0) {

                    String outletstring = parent.getItemAtPosition(position).toString();
                    for (int i = 0; i < outletDetailsArraylist.size(); i++) {
                        String text = outletDetailsArraylist.get(i).getOutletname() + "(" +
                                outletDetailsArraylist.get(i).getBAnameOutlet() + ")";
                        String floterid = outletDetailsArraylist.get(i).getFlotername();
                        String baid = outletDetailsArraylist.get(i).getBACodeOutlet();
                        if (text.equalsIgnoreCase(outletstring)) {
                            flrid = floterid;
                            lhrid = baid;

                        }
                    }

                }
            }
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                try {
                    new FLROutletAttendance().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public class Check_outlet extends AsyncTask<Void, Void, SoapObject> {

        ContentValues contentvalues = new ContentValues();
        private SoapObject soap_result = null;
        // private SoapPrimitive server_date_result = null;
        private SoapPrimitive soap_result2 = null;

        String Flag = "";

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            pd.setMessage("Please Wait....");
            pd.show();
            pd.setCancelable(false);

        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            // TODO Auto-generated method stub

            if (!cd.isConnectingToInternet()) {

                Flag = "0";

            } else {

                Flag = "1";

                try {

                    soap_result = service.GetFLROutlet(flotername);

                    if (soap_result != null) {

                        for (int i = 0; i < soap_result.getPropertyCount(); i++) {

                            SoapObject soapObject = (SoapObject) soap_result
                                    .getProperty(i);

                            String BACode = soapObject.getProperty("BACode").toString();

                            if (BACode == null) {
                                BACode = "";

                            }

                            String Baname = soapObject.getProperty("Baname").toString();

                            if (Baname == null) {
                                Baname = "";

                            }

                            String outletname = soapObject.getProperty("outletname").toString();

                            if (outletname == null) {
                                outletname = "";

                            }

                            contentvalues.put("baCodeOutlet", BACode);
                            contentvalues.put("banameOutlet", Baname);
                            contentvalues.put("outletname", outletname);
                            contentvalues.put("flotername", flotername);

                            db.open();
                            Cursor c1 = db.check_exist_bacode_outlet(flotername, BACode);
                            int count = c1.getCount();
                            db.close();

                            if (count > 0) {
                                db.open();
                                db.updatevalues("floteroutlet", contentvalues, "flotername", flotername);
                                db.close();

                            } else {

                                db.open();
                                db.insertOutlet(BACode, Baname, outletname, flotername);
                                db.close();

                            }

                        }
                    } else {

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(SoapObject result) {
            // TODO Auto-generated method stub

            if (pd != null && pd.isShowing() && !OutletActivity.this.isFinishing()) {
                pd.dismiss();
            }

            if (Flag.equalsIgnoreCase("0")) {

                Toast.makeText(
                        getApplicationContext(),
                        "Connectivity Error, Please check Internet connection!!",
                        Toast.LENGTH_SHORT).show();

            } else if (Flag.equalsIgnoreCase("1")) {

                try {
                    outletDetailsArraylist = new ArrayList<OutletModel>();
                    Cursor cursor;
                    db.open();
                    cursor = db.getdata_outlet(flotername);

                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        do {

                            outletModel = new OutletModel();
                            outletModel.setBACodeOutlet(cursor.getString(cursor.getColumnIndex("baCodeOutlet")));
                            outletModel.setBAnameOutlet(cursor.getString(cursor.getColumnIndex("banameOutlet")));
                            outletModel.setOutletname(cursor.getString(cursor.getColumnIndex("outletname")));
                            outletModel.setFlotername(cursor.getString(cursor.getColumnIndex("flotername")));

                            outletDetailsArraylist.add(outletModel);
                        } while (cursor.moveToNext());
                        cursor.close();
                    }
                    db.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (outletDetailsArraylist.size() > 0) {
                    strOutletArray = new String[outletDetailsArraylist.size() + 1];
                    strOutletArray[0] = "Office";
                    for (int i = 0; i < outletDetailsArraylist.size(); i++) {
                        strOutletArray[i + 1] = outletDetailsArraylist.get(i).getOutletname() + "(" +
                                outletDetailsArraylist.get(i).getBAnameOutlet() + ")";
                    }
                }
                if (outletDetailsArraylist != null && outletDetailsArraylist.size() > 0) {
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(OutletActivity.this, android.R.layout.simple_list_item_1, strOutletArray) {
                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View v = null;
                            // If this is the initial dummy entry, make it hidden
                            if (position == 0) {
                                TextView tv = new TextView(getContext());
                                tv.setHeight(0);
                                tv.setVisibility(View.GONE);
                                v = tv;
                            } else {
                                // Pass convertView as null to prevent reuse of special case views
                                v = super.getDropDownView(position, null, parent);
                            }
                            // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
                            parent.setVerticalScrollBarEnabled(false);
                            return v;
                        }
                    };

                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    outlet.setAdapter(adapter1);
                }
            }


        }


    }

    private void fetchoutletdetails() {
        try {
            outletDetailsArraylist = new ArrayList<OutletModel>();
            Cursor cursor;
            db.open();
            cursor = db.getdata_outlet(flotername);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {

                    outletModel = new OutletModel();
                    outletModel.setBACodeOutlet(cursor.getString(cursor.getColumnIndex("baCodeOutlet")));
                    outletModel.setBAnameOutlet(cursor.getString(cursor.getColumnIndex("banameOutlet")));
                    outletModel.setOutletname(cursor.getString(cursor.getColumnIndex("outletname")));
                    outletModel.setFlotername(cursor.getString(cursor.getColumnIndex("flotername")));

                    outletDetailsArraylist.add(outletModel);
                } while (cursor.moveToNext());
                cursor.close();
            }
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (outletDetailsArraylist.size() > 0) {
            strOutletArray = new String[outletDetailsArraylist.size() + 1];
            strOutletArray[0] = "Office";
            for (int i = 0; i < outletDetailsArraylist.size(); i++) {
                strOutletArray[i + 1] = outletDetailsArraylist.get(i).getOutletname() + "(" +
                        outletDetailsArraylist.get(i).getBAnameOutlet() + ")";
            }
        }
        if (outletDetailsArraylist != null && outletDetailsArraylist.size() > 0) {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(OutletActivity.this, android.R.layout.simple_list_item_1, strOutletArray) {
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View v = null;
                    // If this is the initial dummy entry, make it hidden
                    if (position == 0) {
                        TextView tv = new TextView(getContext());
                        tv.setHeight(0);
                        tv.setVisibility(View.GONE);
                        v = tv;
                    } else {
                        // Pass convertView as null to prevent reuse of special case views
                        v = super.getDropDownView(position, null, parent);
                    }
                    // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
                    parent.setVerticalScrollBarEnabled(false);
                    return v;
                }
            };

            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            outlet.setAdapter(adapter1);
        }

    }

    @Override
    public void onBackPressed() {

    }

    public class FLROutletAttendance extends AsyncTask<Void, Void, SoapObject> {

        ContentValues contentvalues = new ContentValues();
        private SoapPrimitive soap_result = null;

        String Flag = "";

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            pd.setMessage("Please Wait....");
            pd.show();
            pd.setCancelable(false);

        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            // TODO Auto-generated method stub

            if (!cd.isConnectingToInternet()) {

                Flag = "0";

            } else {
                try {

                    soap_result = service.FLROutletAttendance(shp.getString("AttendAid",""), lhrid);

                    if (soap_result != null) {

                        if (soap_result.toString().equalsIgnoreCase("True")) {
                            Flag = "1";
                        } else {
                            Flag = "2";
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(SoapObject result) {
            // TODO Auto-generated method stub

            if (pd != null && pd.isShowing() && !OutletActivity.this.isFinishing()) {
                pd.dismiss();
            }

            if (Flag.equalsIgnoreCase("0")) {

                Toast.makeText(
                        getApplicationContext(),
                        "Connectivity Error, Please check Internet connection!!",
                        Toast.LENGTH_SHORT).show();

            } else if (Flag.equalsIgnoreCase("1")) {

                try {
                    Intent i = new Intent(getApplicationContext(), DashboardNewActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Flag.equalsIgnoreCase("2")) {

                Toast.makeText(
                        getApplicationContext(),
                        "Soap Response getting False!!",
                        Toast.LENGTH_SHORT).show();
            }


        }


    }
}
