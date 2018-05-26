package com.prod.sudesi.lotusherbalsnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

@SuppressLint("SimpleDateFormat")
public class SyncMaster extends Activity {

    Context context;
    private ProgressDialog progressDialog;

    Button master_sync, data_sync, btn_first_time_sycn, btn_usermanual;
    // ,attendance, stock, tester, visibility;

    private Dbcon db;
    private ProgressDialog mProgress = null;
    Cursor attendance_array, image_array, image_array1, test_array,
            stock_array, upload_image, upload_boc_daywise_data;

    LotusWebservice service;
    int soapresultforvisibilityid;

    private double lon = 0.0, lat = 0.0;
    String username, bdename, imgpth, producttype;

    // shredpreference
    private SharedPreferences sharedpre = null;

    private SharedPreferences.Editor saveuser = null;

    SharedPreferences sp;
    SharedPreferences.Editor spe;
    //
    ConnectionDetector cd;

    Date d;

    TextView tv_h_username;
    Button btn_home, btn_logout;
    Button btn_ch;


    // WriteErroLogs we;

    ArrayList<HashMap<String, String>> listofsyncerrorlog = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> listofimages = new ArrayList<HashMap<String, String>>();

    public static String URL = "http://sandboxws.lotussmartforce.com/WebAPIStock/api/Stock/SaveStock";//UAT Server
//    public static String URL = "http://lotusws.lotussmartforce.com/WebAPIStock/api/Stock/SaveStock/";//Production Server
    private JSONArray array = new JSONArray();
    String flag;
    String ErroFlag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_stock_save_to_server);
        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        context = getApplicationContext();

        // attendance = (Button) findViewById(R.id.btn_server_attendance);
        // stock = (Button) findViewById(R.id.btn_server_stock);
        // tester = (Button) findViewById(R.id.btn_server_tester);
        // visibility = (Button) findViewById(R.id.btn_server_visibility);

        data_sync = (Button) findViewById(R.id.btn_server_data_sync);
        master_sync = (Button) findViewById(R.id.btn_server_master_sync);
        btn_ch = (Button) findViewById(R.id.btn_changePass);
        btn_first_time_sycn = (Button) findViewById(R.id.btn_syncfirst);

        btn_usermanual = (Button) findViewById(R.id.btn_usermanual);

        cd = new ConnectionDetector(context);
        db = new Dbcon(context);
        mProgress = new ProgressDialog(SyncMaster.this);
        service = new LotusWebservice(SyncMaster.this);

        sharedpre = context
                .getSharedPreferences("Sudesi", context.MODE_PRIVATE);
        saveuser = sharedpre.edit();

        sp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
        spe = sp.edit();

        producttype = sp.getString("producttype", "");
        Log.e("", "producttype==" + producttype);

        username = sp.getString("username", "");
        Log.e("", "username==" + username);

        bdename = sp.getString("BDEusername", "");

        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        tv_h_username.setText(bdename);

        btn_logout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btn_home.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(getApplicationContext(),
                        DashboardNewActivity.class));
            }
        });

        btn_ch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                startActivity(new Intent(SyncMaster.this, ChangePassword.class));
            }
        });

        btn_first_time_sycn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                btn_first_time_sycn.setEnabled(false);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        btn_first_time_sycn.setEnabled(true);
                        Log.d(TAG, "resend1");

                    }
                }, 5000);// set time as per your requirement

                if (sp.getString("Role", "").equalsIgnoreCase("FLR")) {
                    Toast.makeText(context, "Data Download not use for Floter", Toast.LENGTH_LONG).show();
                } else {
                    if (cd.isCurrentDateMatchDeviceDate()) {
                        new InsertFirstTimeMaster().execute();
                    } else {
                        Toast.makeText(SyncMaster.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });

        String chk = "";

        db.open();
        chk = db.check_flag_button_disable();
        db.close();

        if (chk.equalsIgnoreCase("D")) {

            // layout_relative.setVisibility(View.GONE);
        }

        btn_usermanual.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    File pdfFile = new File(Environment
                            .getExternalStorageDirectory(), "/sample.pdf");

                    readusermanual();
                    /*
                     * Intent i = new Intent(MainActivity.this,
					 * UserManual.class); startActivity(i);
					 */
                } catch (Exception e) {

                }
            }
        });

        data_sync.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                data_sync.setEnabled(false);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        data_sync.setEnabled(true);
                        Log.d(TAG, "resend1");

                    }
                }, 5000);// set time as per your requirement

                if(cd.isCurrentDateMatchDeviceDate()) {
                    uploaddata();
                }else{
                    Toast.makeText(SyncMaster.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

                }
                //new syncAllData(flag).execute();

            }
        });

        master_sync.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                master_sync.setEnabled(false);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        master_sync.setEnabled(true);
                        Log.d(TAG, "resend1");

                    }
                }, 5000);// set time as per your requirement

                if(cd.isCurrentDateMatchDeviceDate()) {
                    new InsertProductMaster().execute();
                }else{
                    Toast.makeText(SyncMaster.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        /*// cancel any notification we may have received from
        // TestBroadcastReceiver
        ((NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1234);

        refreshDisplay();

        // This demonstrates how to dynamically create a receiver to listen to
        // the location updates.
        // You could also register a receiver in your manifest.
        final IntentFilter lftIntentFilter = new IntentFilter(
                LocationLibraryConstants
                        .getLocationChangedPeriodicBroadcastAction());
        context.registerReceiver(lftBroadcastReceiver, lftIntentFilter);*/
    }

   /* private final BroadcastReceiver lftBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // extract the location info in the broadcast
            final LocationInfo locationInfo = (LocationInfo) intent
                    .getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
            // refresh the display with it
            refreshDisplay(locationInfo);
        }
    };

    private void refreshDisplay() {
        refreshDisplay(new LocationInfo(context));
    }

    private void refreshDisplay(final LocationInfo locationInfo) {
        if (locationInfo.anyLocationDataReceived()) {
            lat = locationInfo.lastLat;
            lon = locationInfo.lastLong;
            Log.e("Longitude", String.valueOf(lon));
            Log.e("Latitude", String.valueOf(lat));
            // Toast.makeText(context, "Location Updated",
            // Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context,
                    "Unable to get GPS location! Try again later!!",
                    Toast.LENGTH_LONG).show();
        }

    }*/

    public class InsertProductMaster extends AsyncTask<Void, Void, SoapObject> {

        private SoapObject soap_result = null;

        private SoapObject soap_result_tester = null;

        SoapObject soap_result1 = null;
        SoapObject soap_result_tester1 = null;

        String Flag;

        @SuppressLint("NewApi")
        @Override
        protected SoapObject doInBackground(Void... params) {

            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                // Toast.makeText(SyncMaster.this(),"Check Your Internet Connection!!!",
                // Toast.LENGTH_LONG).show();

                Flag = "0";
                // stop executing code by return

            } else {
                Flag = "1";
                // TODO Auto-generated method stub
                Log.e("pm", "pm0");
                db.open();
                String lastdatesync = db.getLastSyncDate("product_master");
                db.close();

                Log.e("pm", "lastdatesync=" + lastdatesync);

                soap_result = service.GetProducts(lastdatesync,
                        sp.getString("username", ""));
                // ,);

                Log.e("pm", "pm1");
                if (soap_result != null) {

                    Log.e("pm",
                            "count================="
                                    + soap_result.getPropertyCount());

                    for (int i = 0; i < soap_result.getPropertyCount(); i++) {

                        soap_result1 = (SoapObject) soap_result.getProperty(i);

                        Log.e("pm", "pm3");

                        if (soap_result1.getProperty("Flag") != null) {

                            if (soap_result1.getProperty("Flag").toString()
                                    .equalsIgnoreCase("E")) {
                                Log.e("pm", "pm4");
                                try {

                                    String d_id = soap_result1
                                            .getProperty("ID").toString();

                                    String product_category = soap_result1
                                            .getProperty("ProductCategory")
                                            .toString();
                                    String product_type = soap_result1
                                            .getProperty("ProductType")
                                            .toString();
                                    String product = soap_result1.getProperty(
                                            "ProductName").toString();

                                    String categoryid = soap_result1
                                            .getProperty("CategoryId")
                                            .toString();
                                    String category = soap_result1.getProperty(
                                            "Category").toString();
                                    String shadeno = soap_result1.getProperty(
                                            "ShadeNo").toString();
                                    String eancode = soap_result1.getProperty(
                                            "EANCode").toString();
                                    String size = soap_result1.getProperty(
                                            "Size").toString();
                                    String mrp = soap_result1
                                            .getProperty("MRP").toString();
                                    String masterpackqty = soap_result1
                                            .getProperty("MasterPackQty")
                                            .toString();
                                    String monopackqty = soap_result1
                                            .getProperty("MonoPackQty")
                                            .toString();
                                    String innerqty = soap_result1.getProperty(
                                            "InnerQty").toString();
                                    String sku_l = soap_result1.getProperty(
                                            "SKU_L").toString();
                                    String sku_b = soap_result1.getProperty(
                                            "SKU_B").toString();
                                    String sku_h = soap_result1.getProperty(
                                            "SKU_H").toString();
                                    String price_type = soap_result1
                                            .getPropertyAsString("OldNewStatus")
                                            .toString();
                                    String order_flag = soap_result1
                                            .getPropertyAsString("order_flag")
                                            .toString();
                                    // String lmd =
                                    // soap_result1.getProperty("LMD").toString();
                                    String flag = soap_result1.getProperty(
                                            "Flag").toString();

                                    if (d_id.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for d_id");
                                        d_id = " ";
                                    }
                                    if (product_category
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for product");
                                        product_category = " ";
                                    }
                                    if (product_type
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for producttype");
                                        product_type = " ";
                                    }
                                    if (product.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for product");
                                        product = " ";
                                    }
                                    if (categoryid
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for categoryid");
                                        categoryid = " ";
                                    }
                                    if (category.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for category");
                                        category = " ";
                                    }
                                    if (shadeno.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for shadeno");
                                        shadeno = " ";
                                    }
                                    if (eancode.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for eancode");
                                        eancode = " ";
                                    }
                                    if (size.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for size");
                                        size = " ";
                                    }
                                    if (mrp.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for mrp");
                                        mrp = " ";
                                    }
                                    if (masterpackqty
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for masterpackqty");
                                        masterpackqty = " ";
                                    }
                                    if (monopackqty
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for monopackqty");
                                        monopackqty = " ";
                                    }
                                    if (innerqty.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for innerqty");
                                        innerqty = " ";
                                    }
                                    if (sku_l.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_l");
                                        sku_l = " ";
                                    }
                                    if (sku_b.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_b");
                                        sku_b = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }

                                    if (d_id.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for d_id");
                                        d_id = " ";
                                    }
                                    if (product_category
                                            .equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for product");
                                        product_category = " ";
                                    }
                                    if (product_type.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for producttype");
                                        product_type = " ";
                                    }
                                    if (product.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for product");
                                        product = " ";
                                    }
                                    if (categoryid.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for categoryid");
                                        categoryid = " ";
                                    }
                                    if (category.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for category");
                                        category = " ";
                                    }
                                    if (shadeno.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for shadeno");
                                        shadeno = " ";
                                    }
                                    if (eancode.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for eancode");
                                        eancode = " ";
                                    }
                                    if (size.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for size");
                                        size = " ";
                                    }
                                    if (mrp.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for mrp");
                                        mrp = " ";
                                    }
                                    if (masterpackqty.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for masterpackqty");
                                        masterpackqty = " ";
                                    }
                                    if (monopackqty.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for monopackqty");
                                        monopackqty = " ";
                                    }
                                    if (innerqty.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for innerqty");
                                        innerqty = " ";
                                    }
                                    if (sku_l.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_l");
                                        sku_l = " ";
                                    }
                                    if (sku_b.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_b");
                                        sku_b = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }
                                    /*
                                     * if (order_flag!=null ||
									 * order_flag.equalsIgnoreCase("NULL")) {
									 * Log.e("", "anytype for sku_h");
									 * order_flag = " "; }
									 */

                                    Log.v("", "flag=" + flag);
                                    // if (flag.equalsIgnoreCase("E")) {

                                    Log.e("pm", "pm5--");
                                    db.open();
                                    Cursor c = db.getuniquedata(categoryid,
                                            eancode, d_id, "product_master");

                                    int count = c.getCount();
                                    Log.v("", "" + count);
                                    db.close();
                                    if (count > 0) {

                                        Log.v("", "data already available");

                                        db.open();
                                        db.UpdateProductMaster(
                                                product_category,
                                                product_type.trim(), product,
                                                d_id, categoryid, category,
                                                shadeno, eancode, size, mrp,
                                                masterpackqty, monopackqty,
                                                innerqty, sku_l, sku_b, sku_h,
                                                price_type, order_flag);
                                        db.close();

                                    } else {

                                        Log.e("pm", "pm5");

                                        db.open();
                                        db.insertProductMaster(
                                                product_category,
                                                product_type.trim(), product,
                                                d_id, categoryid, category,
                                                shadeno, eancode, size, mrp,
                                                masterpackqty, monopackqty,
                                                innerqty, sku_l, sku_b, sku_h,
                                                price_type, order_flag);
                                        db.close();

                                    }

                                    // }

                                } catch (Exception e) {
                                    // TODO: handle exception
                                    e.printStackTrace();
                                    String error = e.toString();
                                    final Calendar calendar1 = Calendar
                                            .getInstance();
                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                            "MM/dd/yyyy HH:mm:ss");
                                    String Createddate = formatter1
                                            .format(calendar1.getTime());

                                    int n = Thread.currentThread()
                                            .getStackTrace()[2].getLineNumber();
                                    db.insertSyncLog(error, String.valueOf(n),
                                            "GetProducts()", Createddate,
                                            Createddate,
                                            sp.getString("username", ""),
                                            "GetProducts", "Fail");
                                }

                            } else if (soap_result1.getProperty("Flag")
                                    .toString().equalsIgnoreCase("D")) {

                                Log.e("pm", "pm6");
                                db.open();
                                db.deletproductmaster("", "", soap_result1
                                                .getProperty("ID").toString(),
                                        "product_master");
                                db.close();

                            }
                        } else if (soap_result1.getProperty("status").toString()
                                .equalsIgnoreCase("C")) {
                            Log.e("pm", "pm7");

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            // get current date time with Date()
                            Calendar cal = Calendar.getInstance();
                            // dateFormat.format(cal.getTime())
                            db.open();
                            db.updateDateSync(dateFormat.format(cal.getTime()),
                                    "product_master");
                            db.close();

                        } else if (soap_result1.getProperty("status")
                                .toString().equalsIgnoreCase("SE")) {
                            Log.e("pm", "pm7");

                            final Calendar calendar1 = Calendar.getInstance();
                            SimpleDateFormat formatter1 = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            String Createddate = formatter1.format(calendar1
                                    .getTime());

                            int n = Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber();
                            db.insertSyncLog("GetProducts_SE",
                                    String.valueOf(n), "GetProducts()",
                                    Createddate, Createddate,
                                    sp.getString("username", ""),
                                    "GetProducts", "Fail");
                        }
                    }

                } else {
                    Log.v("", "Soap result is null");
                    // Toast.makeText(context,
                    // "Data Not Available or Check Connectivity",
                    // Toast.LENGTH_SHORT).show();

                    final Calendar calendar1 = Calendar.getInstance();
                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                            "MM/dd/yyyy HH:mm:ss");
                    String Createddate = formatter1.format(calendar1.getTime());

                    int n = Thread.currentThread().getStackTrace()[2]
                            .getLineNumber();
                    db.insertSyncLog(
                            "Internet Connection Lost, Soap in giving null while 'GetProducts'",
                            String.valueOf(n), "GetProducts()", Createddate,
                            Createddate, sp.getString("username", ""),
                            "GetProducts", "Fail");
                }

                db.open();
                String lastdatesyncdate_tester = db
                        .getLastSyncDate("tester_master");
                db.close();
                Log.e("pm", "lastdatesyncdate_tester="
                        + lastdatesyncdate_tester);

                soap_result_tester = service.GetTesterProducts(
                        lastdatesyncdate_tester, sp.getString("username", ""));
                // ,);

                Log.e("pm", "pm1");
                if (soap_result_tester != null) {

                    for (int i = 0; i < soap_result_tester.getPropertyCount(); i++) {
                        Log.e("pm", "pm2");

                        soap_result_tester1 = (SoapObject) soap_result_tester
                                .getProperty(i);

                        Log.e("pm", "pm3");

                        if (soap_result_tester1.getProperty("Flag") != null) {

                            if (soap_result_tester1.getProperty("Flag")
                                    .toString().equalsIgnoreCase("E")) {
                                Log.e("pm", "pm4");
                                try {

                                    String server_db_id = soap_result_tester1
                                            .getProperty("ID").toString();
                                    String product_id = soap_result_tester1
                                            .getPropertyAsString("ProductID");

                                    String product_category = soap_result_tester1
                                            .getProperty("ProductCategory")
                                            .toString();
                                    String product_type = soap_result_tester1
                                            .getProperty("ProductType")
                                            .toString();
                                    String product = soap_result_tester1
                                            .getProperty("ProductName")
                                            .toString();

                                    String categoryid = soap_result_tester1
                                            .getProperty("CategoryId")
                                            .toString();
                                    String category = soap_result_tester1
                                            .getProperty("Category").toString();
                                    String shadeno = soap_result_tester1
                                            .getProperty("ShadeNo").toString();
                                    String eancode = soap_result_tester1
                                            .getProperty("EANCode").toString();
                                    String size = soap_result_tester1
                                            .getProperty("Size").toString();
                                    String mrp = soap_result_tester1
                                            .getProperty("MRP").toString();
                                    String masterpackqty = soap_result_tester1
                                            .getProperty("MasterPackQty")
                                            .toString();
                                    String monopackqty = soap_result_tester1
                                            .getProperty("MonoPackQty")
                                            .toString();
                                    String innerqty = soap_result_tester1
                                            .getProperty("InnerQty").toString();
                                    String sku_l = soap_result_tester1
                                            .getProperty("SKU_L").toString();
                                    String sku_b = soap_result_tester1
                                            .getProperty("SKU_B").toString();
                                    String sku_h = soap_result_tester1
                                            .getProperty("SKU_H").toString();
                                    String price_type = soap_result_tester1
                                            .getPropertyAsString("OldNewStatus")
                                            .toString();
                                    // String lmd =
                                    // soap_result1.getProperty("LMD").toString();
                                    String flag = soap_result_tester1
                                            .getProperty("Flag").toString();

                                    if (server_db_id
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for server_db_id");
                                        server_db_id = " ";
                                    }

                                    if (product_id
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for server_db_id");
                                        product_id = " ";
                                    }

                                    if (product_category
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for product");
                                        product_category = " ";
                                    }
                                    if (product_type
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for producttype");
                                        product_type = " ";
                                    }
                                    if (product.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for product");
                                        product = " ";
                                    }
                                    if (categoryid
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for categoryid");
                                        categoryid = " ";
                                    }
                                    if (category.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for category");
                                        category = " ";
                                    }
                                    if (shadeno.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for shadeno");
                                        shadeno = " ";
                                    }
                                    if (eancode.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for eancode");
                                        eancode = " ";
                                    }
                                    if (size.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for size");
                                        size = " ";
                                    }
                                    if (mrp.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for mrp");
                                        mrp = " ";
                                    }
                                    if (masterpackqty
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for masterpackqty");
                                        masterpackqty = " ";
                                    }
                                    if (monopackqty
                                            .equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for monopackqty");
                                        monopackqty = " ";
                                    }
                                    if (innerqty.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for innerqty");
                                        innerqty = " ";
                                    }
                                    if (sku_l.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_l");
                                        sku_l = " ";
                                    }
                                    if (sku_b.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_b");
                                        sku_b = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("anyType{}")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }

                                    if (server_db_id.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for server_db_id");
                                        server_db_id = " ";
                                    }
                                    if (product_id.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for product_id");
                                        product_id = " ";
                                    }
                                    if (product_category
                                            .equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for product");
                                        product_category = " ";
                                    }
                                    if (product_type.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for producttype");
                                        product_type = " ";
                                    }
                                    if (product.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for product");
                                        product = " ";
                                    }
                                    if (categoryid.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for categoryid");
                                        categoryid = " ";
                                    }
                                    if (category.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for category");
                                        category = " ";
                                    }
                                    if (shadeno.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for shadeno");
                                        shadeno = " ";
                                    }
                                    if (eancode.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for eancode");
                                        eancode = " ";
                                    }
                                    if (size.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for size");
                                        size = " ";
                                    }
                                    if (mrp.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for mrp");
                                        mrp = " ";
                                    }
                                    if (masterpackqty.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for masterpackqty");
                                        masterpackqty = " ";
                                    }
                                    if (monopackqty.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for monopackqty");
                                        monopackqty = " ";
                                    }
                                    if (innerqty.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for innerqty");
                                        innerqty = " ";
                                    }
                                    if (sku_l.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_l");
                                        sku_l = " ";
                                    }
                                    if (sku_b.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_b");
                                        sku_b = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }
                                    if (sku_h.equalsIgnoreCase("NULL")) {
                                        Log.e("", "anytype for sku_h");
                                        sku_h = " ";
                                    }

                                    Log.v("", "flag=" + flag);
                                    if (flag.equalsIgnoreCase("E")) {

                                        Log.e("pm", "pm5--");
                                        db.open();
                                        Cursor c = db.getuniquedata(categoryid,
                                                eancode, product_id,
                                                "tester_master");

                                        int count = c.getCount();
                                        Log.v("", "" + count);
                                        db.close();
                                        if (count > 0) {

                                            Log.v("", "data already available");

                                            db.open();
                                            db.UpdateTesterMaster(
                                                    product_category,
                                                    product_type.trim(),
                                                    product, server_db_id,
                                                    product_id, categoryid,
                                                    category, shadeno, eancode,
                                                    size, mrp, masterpackqty,
                                                    monopackqty, innerqty,
                                                    sku_l, sku_b, sku_h,
                                                    price_type);
                                            db.close();

                                        } else {

                                            Log.e("pm", "pm5");
                                            db.open();
                                            db.insertTesterMaster(
                                                    product_category,
                                                    product_type.trim(),
                                                    product, server_db_id,
                                                    product_id, categoryid,
                                                    category, shadeno, eancode,
                                                    size, mrp, masterpackqty,
                                                    monopackqty, innerqty,
                                                    sku_l, sku_b, sku_h,
                                                    price_type);
                                            db.close();

                                        }

                                    }

                                } catch (Exception e) {
                                    // TODO: handle exception
                                    e.printStackTrace();
                                    String error = e.toString();
                                    final Calendar calendar1 = Calendar
                                            .getInstance();
                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                            "MM/dd/yyyy HH:mm:ss");
                                    String Createddate = formatter1
                                            .format(calendar1.getTime());

                                    int n = Thread.currentThread()
                                            .getStackTrace()[2].getLineNumber();
                                    db.insertSyncLog(error, String.valueOf(n),
                                            "GetTesterProducts()", Createddate,
                                            Createddate,
                                            sp.getString("username", ""),
                                            "GetTesterProducts()", "Fail");
                                }

                            } else if (soap_result_tester1.getProperty("Flag")
                                    .toString().equalsIgnoreCase("D")) {

                                Log.e("pm", "pm6");
                                db.open();
                                db.deletproductmaster("", "",
                                        soap_result_tester1.getProperty("ID")
                                                .toString(), "tester_master");
                                db.close();

                            }
                        } else if (soap_result_tester1.getProperty("status")
                                .toString().equalsIgnoreCase("C")) {
                            Log.e("pm", "pm7");

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            // get current date time with Date()
                            Calendar cal = Calendar.getInstance();
                            // dateFormat.format(cal.getTime())
                            db.open();
                            db.updateDateSync(dateFormat.format(cal.getTime()),
                                    "tester_master");
                            db.close();

                        } else if (soap_result_tester1.getProperty("status")
                                .toString().equalsIgnoreCase("SE")) {
                            Log.e("pm", "pm7");

                            final Calendar calendar1 = Calendar.getInstance();
                            SimpleDateFormat formatter1 = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            String Createddate = formatter1.format(calendar1
                                    .getTime());

                            int n = Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber();
                            db.insertSyncLog("GetTesterProducts_SE",
                                    String.valueOf(n), "GetTesterProducts()",
                                    Createddate, Createddate,
                                    sp.getString("username", ""),
                                    "GetTesterProducts()", "Fail");
                        }
                    }

                } else {
                    Log.v("", "Soap result is null");
                    // Toast.makeText(context,
                    // "Data Not Available or Check Connectivity",
                    // Toast.LENGTH_SHORT).show();

                    final Calendar calendar1 = Calendar.getInstance();
                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                            "MM/dd/yyyy HH:mm:ss");
                    String Createddate = formatter1.format(calendar1.getTime());

                    int n = Thread.currentThread().getStackTrace()[2]
                            .getLineNumber();
                    db.insertSyncLog(
                            "Internet Connection Lost, Soap in giving null while 'GetTesterProducts'",
                            String.valueOf(n), "GetTesterProducts()",
                            Createddate, Createddate,
                            sp.getString("username", ""),
                            "GetTesterProducts()", "Fail");
                }
            }
            return soap_result;
        }

        @Override
        protected void onPostExecute(SoapObject result) {

            // TODO Auto-generated method stub
            super.onPostExecute(result);

            mProgress.dismiss();
            if (Flag.equalsIgnoreCase("0")) {

                DisplayDialogMessage("Check Your Internet Connection!!!");
                /*
                Toast.makeText(SyncMaster.this,
						"Check Your Internet Connection!!!", Toast.LENGTH_LONG)
						.show();*/
            } else {
                if (soap_result == null) {

                    DisplayDialogMessage("Master Data Sync Incomplete, Please try again!!");
					/*Toast.makeText(context,
							"Master Data Sync Incomplete, Please try again!!",
							Toast.LENGTH_SHORT).show();*/

                } else if (soap_result1.getProperty("status").toString()
                        .equalsIgnoreCase("C")) {
                    DisplayDialogMessage("Master Data Completed Successfully!!");

				/*	Toast.makeText(context,
							"Master Data Completed Successfully!!",
							Toast.LENGTH_SHORT).show();*/

                } else if (soap_result1.getProperty("status").toString()
                        .equalsIgnoreCase("SE")) {

                    DisplayDialogMessage("Master Data Sync Incomplete please try again!!");
					/*Toast.makeText(context,
							"Master Data Sync Incomplete please try again!!",
							Toast.LENGTH_SHORT).show();*/
                }

            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mProgress.setMessage("Receiving.....");
            mProgress.show();
            mProgress.setCancelable(false);
        }

    }

    private void DisplayDialogMessage(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(SyncMaster.this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public class syncAllData extends AsyncTask<Void, Void, SoapObject> {

        public syncAllData(String flag) {
            super();
            // do stuff
            if(flag.equalsIgnoreCase("TRUE")){
                ErroFlag = "1";
            }else{
                ErroFlag = "0";
            }
        }

        private SoapPrimitive soap_result = null;

        SoapPrimitive soap_result_img = null;

        SoapPrimitive soap_result_img_data = null;

        //SoapPrimitive soap_result_stock = null;

        SoapPrimitive soap_result_attendance = null;

        SoapPrimitive soap_result_tester = null;

        SoapObject Soap_result_image_name = null;

        SoapObject Soap_result_image_name1 = null;

        SoapPrimitive soap_result_upload_data = null;

        SoapPrimitive soap_result_upload_bocdaywise_data = null;

        String Erro_function = "";

        String Flag;


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mProgress.setMessage("Please Wait");
            mProgress.show();
            mProgress.setCancelable(false);
        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            // TODO Auto-generated method stub

            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                // Toast.makeText(getActivity(),"Check Your Internet Connection!!!",
                // Toast.LENGTH_LONG).show();

                Flag = "3";
                // stop executing code by return

            } else {

                try {
                    Flag = "1";
                    //ErroFlag = "1";
/*
                    try {
                        db.open();

                        test_array = db.getTesterdetails(); // --------------
                        // db.close();

                        if (test_array.getCount() > 0) {

                            if (test_array != null && test_array.moveToFirst()) {
                                test_array.moveToFirst();

                                do {

                                    soap_result_tester = service
                                            .SaveTesterData(

                                                    test_array.getString(9),// EMPID
                                                    test_array.getString(3),// DB_ID
                                                    test_array.getString(15),// CAT_ID
                                                    test_array.getString(4),// ENA_CODE

                                                    test_array.getString(5),// PRODUCT_CAT
                                                    test_array.getString(6),// PRODUCT_TYPE
                                                    test_array.getString(7),// PRODUCT_NAME
                                                    test_array.getString(14),// SHADE_NO

                                                    test_array.getString(10),// PRODUCT_STATUS
                                                    test_array.getString(11),// REQUEST_DATE
                                                    test_array.getString(12),// DELIVER_DATE

                                                    test_array.getString(8)// SIZE

                                            );

                                    String localid = test_array.getString(0);
                                    Log.e("", "localid=" + localid);
                                    if (soap_result_tester != null) {
                                        String string_tester = soap_result_tester
                                                .toString();
                                        Log.v("", "string_tester="
                                                + string_tester);
                                        if (string_tester
                                                .equalsIgnoreCase("TRUE")) {
                                            Log.e("",
                                                    "id="
                                                            + test_array
                                                            .getString(0));
                                            db.open();
                                            db.update_tester_data(test_array
                                                    .getString(0));
                                            db.close();

                                        } else if (string_tester
                                                .equalsIgnoreCase("SE")) {

                                            ErroFlag = "0";
                                            Erro_function = "SaveTesterData()_SE";
                                            final Calendar calendar1 = Calendar
                                                    .getInstance();
                                            SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                    "MM/dd/yyyy HH:mm:ss");
                                            String Createddate = formatter1
                                                    .format(calendar1.getTime());

                                            int n = Thread.currentThread()
                                                    .getStackTrace()[2]
                                                    .getLineNumber();
                                            db.insertSyncLog(
                                                    "SaveTesterData_SE",
                                                    String.valueOf(n),
                                                    "SaveTesterData()",
                                                    Createddate,
                                                    Createddate,
                                                    sp.getString("username", ""),
                                                    "SaveTesterData()", "Fail");
                                        }
                                    } else {
                                        ErroFlag = "0";
                                        Erro_function = "SaveTesterData()-null";
                                        // String errors =
                                        // "Soap in giving null while 'Tester' and 'checkSyncFlag = 2' in  data Sync";
                                        // we.writeToSD(errors.toString());
                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "Internet Connection Lost, Soap in giving null while 'SaveTesterData'",
                                                String.valueOf(n),
                                                "SaveTesterData()",
                                                Createddate, Createddate,
                                                sp.getString("username", ""),
                                                "SaveTesterData()", "Fail");

                                    }

                                } while (test_array.moveToNext());

                            } else {

                            }

                        } else if (test_array == null) {

                        } else {
                            Log.e("NoTester data upload",
                                    String.valueOf(test_array.getCount()));
                        }

                    } catch (Exception e) {
                        ErroFlag = "0";
                        Erro_function = "SaveTesterData()";
                        e.printStackTrace();

                        String Error = e.toString();

                        final Calendar calendar1 = Calendar.getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(calendar1
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(Error, String.valueOf(n),
                                "SaveTesterData()", Createddate, Createddate,
                                sp.getString("username", ""),
                                "SaveTesterData()", "Fail");

                    }
*/

					/*try {
						db.open();
						attendance_array = db.getAttendanceData();

						if (attendance_array.getCount() > 0) {

							if (attendance_array != null
									&& attendance_array.moveToFirst()) {
								attendance_array.moveToFirst();

								do {

									String empid = attendance_array
											.getString(1);
									Log.e("", "empid=" + empid);
									String date = attendance_array.getString(2);
									Log.e("", "date=" + date);

									String attendance = attendance_array
											.getString(3);
									Log.e("", "attendance=" + attendance);

									String absent_type = attendance_array
											.getString(4);
									Log.e("", "absent_type=" + absent_type);

									String lat = attendance_array.getString(5);
									Log.e("", "lat=" + lat);

									String lon = attendance_array.getString(5);
									Log.e("", "lon=" + lon);

									soap_result_attendance = service
											.SaveAttendance(attendance_array
															.getString(1), date,
													attendance_array
															.getString(3),
													attendance_array
															.getString(4),
													attendance_array
															.getString(5),
													attendance_array
															.getString(6));

									if (soap_result_attendance != null) {
										String t = soap_result_attendance
												.toString();
										Log.v("", "soap_result_attendance=" + t);
										if (t.equalsIgnoreCase("TRUE")) {

											db.update_Attendance_data(attendance_array
													.getString(0));
											db.close();

										} else if (t.equalsIgnoreCase("SE")) {

											ErroFlag = "0";
											Erro_function = "SaveAttendace()_SE";
											final Calendar calendar1 = Calendar
													.getInstance();
											SimpleDateFormat formatter1 = new SimpleDateFormat(
													"MM/dd/yyyy HH:mm:ss");
											String Createddate = formatter1
													.format(calendar1.getTime());

											int n = Thread.currentThread()
													.getStackTrace()[2]
													.getLineNumber();
											db.insertSyncLog(
													"SaveAttendace_SE",
													String.valueOf(n),
													"SaveAttendance()",
													Createddate,
													Createddate,
													sp.getString("username", ""),
													"SaveAttendance()", "Fail");

										}
									} else {
										ErroFlag = "0";
										Erro_function = "SaveAttendace()-null";
										// String errors =
										// "Soap in giving null while 'Attendance' and 'checkSyncFlag = 2' in  data Sync";
										// we.writeToSD(errors.toString());
										final Calendar calendar1 = Calendar
												.getInstance();
										SimpleDateFormat formatter1 = new SimpleDateFormat(
												"MM/dd/yyyy HH:mm:ss");
										String Createddate = formatter1
												.format(calendar1.getTime());

										int n = Thread.currentThread()
												.getStackTrace()[2]
												.getLineNumber();
										db.insertSyncLog(
												"Internet Connection Lost, Soap in giving null while 'SaveAttendace'",
												String.valueOf(n),
												"SaveAttendance()",
												Createddate, Createddate,
												sp.getString("username", ""),
												"SaveAttendance()", "Fail");

									}

								} while (attendance_array.moveToNext());

							}
						} else if (attendance_array == null) {

						} else {
							Log.e("NoAttendance dataupload",
									String.valueOf(attendance_array.getCount()));
						}

					} catch (Exception e) {
						ErroFlag = "0";
						Erro_function = "Attendance";
						e.printStackTrace();
						String Error = e.toString();

						final Calendar calendar1 = Calendar.getInstance();
						SimpleDateFormat formatter1 = new SimpleDateFormat(
								"MM/dd/yyyy HH:mm:ss");
						String Createddate = formatter1.format(calendar1
								.getTime());

						int n = Thread.currentThread().getStackTrace()[2]
								.getLineNumber();
						db.insertSyncLog(Error, String.valueOf(n),
								"SaveAttendance()", Createddate, Createddate,
								sp.getString("username", ""),
								"SaveAttendance()", "Fail");

					}*/

                    /*try {
                        Log.e("", "saveto server1-stcok");
                        db.open();
                        stock_array = db.getStockdetails();
                        // db.close();
                        // ------------------

                        *//*for (stock_array.moveToFirst(); !stock_array.isAfterLast(); stock_array.moveToNext()) {
                            // do what you need with the cursor here
                            String shad;
                            for (int i = 0; i < stock_array.getCount(); i++) {
                                JSONObject obj = new JSONObject();
                                try {

                                    if (stock_array.getString(23) != null
                                            || !stock_array.getString(23).equalsIgnoreCase("null")) {

                                        Log.v("","shadeno="+ stock_array.getString(23));
                                        shad = stock_array.getString(23).toString();

                                    } else {
                                        Log.v("", "shadeno="+ stock_array.getString(23));
                                        shad = "";
                                    }

                                    obj.put("id", stock_array.getString(0));
                                    obj.put("Pid",  stock_array.getString(2));
                                    obj.put("CatCodeId", stock_array.getString(1));
                                    obj.put("EANCode", stock_array.getString(3));
                                    obj.put("empId", username);
                                    obj.put("ProductCategory", stock_array.getString(4));
                                    obj.put("product_type", stock_array.getString(5));
                                    obj.put("product_name", stock_array.getString(6));
                                    obj.put("shadeno", shad);
                                    obj.put("Opening_Stock", stock_array.getString(10));
                                    obj.put("FreshStock", stock_array.getString(11));
                                    obj.put("Stock_inhand", stock_array.getString(12));
                                    obj.put("SoldStock", stock_array.getString(16));
                                    obj.put("S_Return_Saleable", stock_array.getString(14));
                                    obj.put("S_Return_NonSaleable", stock_array.getString(15));
                                    obj.put("ClosingBal", stock_array.getString(13));
                                    obj.put("GrossAmount", stock_array.getString(17));
                                    obj.put("Discount", stock_array.getString(19));
                                    obj.put("NetAmount", stock_array.getString(18));
                                    obj.put("Size", stock_array.getString(7));
                                    obj.put("Price", stock_array.getString(8));
                                    obj.put("AndroidCreatedDate", stock_array.getString(21));


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                array.put(obj);
                            }
                        }*//*

                        if (stock_array.getCount() > 0) {

                            if (stock_array != null
                                    && stock_array.moveToFirst()) {
                                stock_array.moveToFirst();

                                String shad;
                                do {

                                    Log.v("",
                                            "dbid=" + stock_array.getString(2));
                                    Log.v("",
                                            "category_id="
                                                    + stock_array.getString(1));
                                    Log.v("",
                                            "enacode="
                                                    + stock_array.getString(3));
                                    Log.v("",
                                            "empid=" + stock_array.getString(9));
                                    Log.v("",
                                            "product_category="
                                                    + stock_array.getString(4));
                                    Log.v("",
                                            "product_type="
                                                    + stock_array.getString(5));
                                    Log.v("",
                                            "product_name="
                                                    + stock_array.getString(6));

                                    Log.v("",
                                            "opening_stock="
                                                    + stock_array.getString(10));
                                    Log.v("",
                                            "stock_receive="
                                                    + stock_array.getString(11));
                                    Log.v("",
                                            "stock_inhand="
                                                    + stock_array.getString(12));
                                    Log.v("",
                                            "sold=" + stock_array.getString(16));
                                    Log.v("",
                                            "return_s="
                                                    + stock_array.getString(14));
                                    Log.v("",
                                            "return_ns="
                                                    + stock_array.getString(15));
                                    Log.v("",
                                            "close_bal="
                                                    + stock_array.getString(13));
                                    Log.v("",
                                            "t_gross="
                                                    + stock_array.getString(17));
                                    Log.v("",
                                            "discount="
                                                    + stock_array.getString(19));
                                    Log.v("",
                                            "net_amount="
                                                    + stock_array.getString(18));
                                    Log.v("",
                                            "size=" + stock_array.getString(7));
                                    Log.v("",
                                            "price=" + stock_array.getString(8));
                                    Log.v("",
                                            "insert_date"
                                                    + stock_array.getString(21));

                                    if (stock_array.getString(23) != null
                                            || !stock_array.getString(23)
                                            .equalsIgnoreCase("null")) {

                                        Log.v("",
                                                "shadeno="
                                                        + stock_array
                                                        .getString(23));
                                        shad = stock_array.getString(23)
                                                .toString();

                                    } else {
                                        Log.v("",
                                                "shadeno="
                                                        + stock_array
                                                        .getString(23));
                                        shad = "";
                                    }

                                    soap_result_stock = service.SaveStock(
                                            stock_array.getString(0),
                                            stock_array.getString(2),
                                            stock_array.getString(1),
                                            stock_array.getString(3), username,
                                            stock_array.getString(4),
                                            stock_array.getString(5),
                                            stock_array.getString(6), shad,

                                            stock_array.getString(10),
                                            stock_array.getString(11),
                                            stock_array.getString(12),

                                            stock_array.getString(16),
                                            stock_array.getString(14),
                                            stock_array.getString(15),

                                            stock_array.getString(13),
                                            stock_array.getString(17),

                                            stock_array.getString(19),
                                            stock_array.getString(18),
                                            stock_array.getString(7),
                                            stock_array.getString(8),
                                            stock_array.getString(21)

                                    );

                                    if (soap_result_stock != null) {
                                        String result_stock = soap_result_stock
                                                .toString();
                                        Log.v("", "result_stock="
                                                + result_stock);
                                        if (result_stock.matches(".*\\d+.*")) {
                                            Log.e("", "stock id for update=="
                                                    + stock_array.getString(0));
                                            db.open();
                                            db.update_stock_data(result_stock);
                                            Log.d("Data is Updating here ",
                                                    result_stock);
                                            db.close();

                                        } else if (result_stock
                                                .equalsIgnoreCase("SE")) {

                                            ErroFlag = "0";
                                            Erro_function = "SaveStock()_SE";
                                            final Calendar calendar1 = Calendar
                                                    .getInstance();
                                            SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                    "MM/dd/yyyy HH:mm:ss");
                                            String Createddate = formatter1
                                                    .format(calendar1.getTime());

                                            int n = Thread.currentThread()
                                                    .getStackTrace()[2]
                                                    .getLineNumber();
                                            db.insertSyncLog("SaveStock_SE",
                                                    String.valueOf(n),
                                                    "SaveStock()", Createddate,
                                                    Createddate, sp.getString(
                                                            "username", ""),
                                                    "SaveStock()", "Fail");

                                        }

                                    } else {

                                        ErroFlag = "0";
                                        Erro_function = "SaveStock()-null";
                                        // String errors =
                                        // "Soap in giving null while 'Stock' and 'checkSyncFlag = 2' in  data Sync";
                                        // we.writeToSD(errors.toString());
                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "Internet Connection Lost, Soap in giving null while 'SaveStock'",
                                                String.valueOf(n),
                                                "SaveStock()", Createddate,
                                                Createddate,
                                                sp.getString("username", ""),
                                                "SaveStock()", "Fail");

                                    }

                                } while (stock_array.moveToNext());

                            } else {
                                // Toast.makeText(getActivity(),
                                // "No Data Available or Check Connectivity",
                                // .LENGTH_SHORT).show();
                                Flag = "2";
                                Log.e("", "no data available");

                            }

                        } else if (stock_array == null) {

                        } else {
                            Log.e("NoStock dataupload",
                                    String.valueOf(stock_array.getCount()));
                        }


                    } catch (Exception e) {
                        ErroFlag = "0";
                        Erro_function = "SaveStock()";
                        e.printStackTrace();

                        String Error = e.toString();

                        final Calendar cal = Calendar.getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(cal.getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(Error, String.valueOf(n),
                                "SaveStock()", Createddate, Createddate,
                                sp.getString("username", ""), "SaveStock()",
                                "Fail");

                    }*/
                    // -----------------------------------boc day wise data
                    // -----------------------------------------------//
                    final Calendar calendar123 = Calendar.getInstance();
                    SimpleDateFormat formatter123 = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    String currentdate = formatter123.format(calendar123
                            .getTime());

                    try {
                        db.open();
                        upload_boc_daywise_data = db
                                .getBocDateWiseData(currentdate);
                        // db.close();
                        // ------------------

                        if (upload_boc_daywise_data != null) {

                            if (upload_boc_daywise_data.getCount() > 0) {

                                upload_boc_daywise_data.moveToFirst();

                                String shad;
                                do {

                                    Log.v("",
                                            "dbid="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("db_id")));
                                    Log.v("",
                                            "category_id="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("product_id")));
                                    Log.v("",
                                            "enacode="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("eancode")));
                                    Log.v("",
                                            "empid="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("emp_id")));
                                    Log.v("",
                                            "product_category="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("product_category")));
                                    Log.v("",
                                            "product_type="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("product_type")));
                                    Log.v("",
                                            "product_name="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("product_name")));

                                    Log.v("",
                                            "opening_stock="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("opening_stock")));
                                    Log.v("",
                                            "stock_receive="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("stock_received")));
                                    Log.v("",
                                            "stock_inhand="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("stock_in_hand")));
                                    Log.v("",
                                            "sold_stock="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("sold_stock")));
                                    Log.v("",
                                            "return_s="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("return_saleable")));
                                    Log.v("",
                                            "return_ns="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("return_non_saleable")));
                                    Log.v("",
                                            "close_bal="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("close_bal")));
                                    Log.v("",
                                            "t_gross="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("total_gross_amount")));
                                    Log.v("",
                                            "discount="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("discount")));
                                    Log.v("",
                                            "net_amount="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("total_net_amount")));
                                    Log.v("",
                                            "size="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("size")));
                                    Log.v("",
                                            "price="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("price")));
                                    Log.v("",
                                            "insert_date="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("insert_date")));

                                    Log.v("",
                                            "date="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("date")));
                                    Log.v("",
                                            "boc="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("boc")));

                                    if (upload_boc_daywise_data
                                            .getString(upload_boc_daywise_data
                                                    .getColumnIndex("shadeNo")) != null
                                            || !upload_boc_daywise_data
                                            .getString(
                                                    upload_boc_daywise_data
                                                            .getColumnIndex("shadeNo"))
                                            .equalsIgnoreCase("null")) {

                                        Log.v("",
                                                "shadeno="
                                                        + upload_boc_daywise_data
                                                        .getString(upload_boc_daywise_data
                                                                .getColumnIndex("shadeNo")));
                                        shad = upload_boc_daywise_data
                                                .getString(upload_boc_daywise_data
                                                        .getColumnIndex("shadeNo"));

                                    } else {
                                        Log.v("",
                                                "shadeno="
                                                        + upload_boc_daywise_data
                                                        .getString(upload_boc_daywise_data
                                                                .getColumnIndex("shadeNo")));
                                        shad = "";
                                    }

                                    soap_result_upload_bocdaywise_data = service
                                            .SaveStockEveryDayNetvalue(
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("db_id")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("product_id")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("eancode")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("emp_id")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("product_category")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("product_type")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("product_name")),
                                                    shad,
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("opening_stock")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("stock_received")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("stock_in_hand")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("sold_stock")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("return_saleable")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("return_non_saleable")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("close_bal")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("total_gross_amount")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("discount")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("total_net_amount")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("size")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("price")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("insert_date")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("date")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("boc")));

                                    if (soap_result_upload_bocdaywise_data != null) {

                                        String soap_result_upload_bocdaywise_data1 = soap_result_upload_bocdaywise_data
                                                .toString();

                                        Log.v("",
                                                "soap_result_upload_bocdaywise_data1="
                                                        + soap_result_upload_bocdaywise_data1);

                                        if (soap_result_upload_bocdaywise_data1
                                                .equalsIgnoreCase("TRUE")) {

                                            Log.e("",
                                                    "boc wise stock id for update=="
                                                            + upload_boc_daywise_data
                                                            .getString(0));
                                            db.open();
                                            db.update_boc_wise_stock_data(upload_boc_daywise_data
                                                    .getString(0));
                                            db.close();

                                        } else if (soap_result_upload_bocdaywise_data1
                                                .equalsIgnoreCase("SE")) {

                                            ErroFlag = "0";
                                            Erro_function = "SaveStockEveryDayNetvalue()_SE";
                                            final Calendar cal = Calendar
                                                    .getInstance();
                                            SimpleDateFormat formatter11 = new SimpleDateFormat(
                                                    "MM/dd/yyyy HH:mm:ss");
                                            String Createddate = formatter11
                                                    .format(cal.getTime());

                                            int n = Thread.currentThread()
                                                    .getStackTrace()[2]
                                                    .getLineNumber();
                                            db.insertSyncLog(
                                                    "SaveStockEveryDayNetvalue_SE",
                                                    String.valueOf(n),
                                                    "SaveStockEveryDayNetvalue()",
                                                    Createddate,
                                                    Createddate,
                                                    sp.getString("username", ""),
                                                    "SaveStockEveryDayNetvalue()",
                                                    "Fail");

                                        }

                                    } else {

                                        ErroFlag = "0";
                                        Erro_function = "SaveStockEveryDayNetvalue()-null";
                                        // String errors =
                                        // "Soap in giving null while 'Stock' and 'checkSyncFlag = 2' in  data Sync";
                                        // we.writeToSD(errors.toString());
                                        final Calendar cal = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter11 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter11
                                                .format(cal.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "Internet Connection Lost, Soap in giving null while 'SaveStockEveryDayNetvalue'",
                                                String.valueOf(n),
                                                "SaveStockEveryDayNetvalue()",
                                                Createddate, Createddate,
                                                sp.getString("username", ""),
                                                "SaveStockEveryDayNetvalue()",
                                                "Fail");

                                    }

                                } while (upload_boc_daywise_data.moveToNext());

                            } else {
                                // Toast.makeText(getActivity(),
                                // "No Data Available or Check Connectivity",
                                // .LENGTH_SHORT).show();
                                Flag = "2";
                                Log.e("", "no data available");

                            }

                        } else if (soap_result_upload_bocdaywise_data == null) {

                        } else {
                            Log.e("NoStockEveryvaluedataup",
                                    String.valueOf(stock_array.getCount()));
                        }

                    } catch (Exception e) {
                        ErroFlag = "0";
                        Erro_function = "SaveStockEveryDayNetvalue()";
                        e.printStackTrace();

                        String Error = e.toString();

                        final Calendar cal = Calendar.getInstance();
                        SimpleDateFormat formatter11 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter11.format(cal.getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(Error, String.valueOf(n),
                                "SaveStockEveryDayNetvalue()", Createddate,
                                Createddate, sp.getString("username", ""),
                                "SaveStockEveryDayNetvalue()", "Fail");

                    }

                    // ----------------------------visibility

/*                    try {

                        Log.e("in weservice", "statrt- visibility");

                        String saveserver = "0";
                        db.open();

                        image_array = db.getscanDetails(saveserver);

                        if (image_array.getCount() > 0) {

                            if (image_array != null
                                    && image_array.moveToFirst()) {
                                image_array.moveToFirst();

                                Log.e("in weservice", "statrt3");

                                do {
                                    Log.e("in weservice", "statrt4");
                                    soap_result_img = service.SaveVisibility(
                                            image_array.getString(2),// 2
                                            image_array.getString(1),// 1
                                            image_array.getString(3),// 3
                                            image_array.getString(6),// 6
                                            image_array.getString(7),// 7
                                            image_array.getString(8)// 8
                                    );
                                    Log.e("in weservice", "Result="
                                            + soap_result_img);
                                    if (soap_result_img != null) {

                                        db.update_scan_data(image_array
                                                .getString(0));

                                        String scanid = image_array
                                                .getString(0);

                                        image_array1 = db
                                                .getimageDetails(scanid);

                                        String spr = soap_result_img.toString();

                                        soapresultforvisibilityid = Integer
                                                .parseInt(spr);

                                        Log.e("in weservice", "image_array1 = "
                                                + image_array1.toString());

                                        if (image_array1 != null
                                                && image_array1.moveToFirst()) {

                                            image_array1.moveToFirst();

                                            do {

                                                db.update_visibility_id(spr,
                                                        image_array1
                                                                .getString(1));

                                                // //////////////////////new
                                                // code putting
                                                Log.v("", "test123");
                                                upload_image = db
                                                        .getimageDetails1();

												*//*
												 * if (upload_image != null &&
												 * upload_image.moveToFirst()) {
												 *
												 * upload_image.moveToFirst();
												 * do{ Log.v("",
												 * "visibility_id="
												 * +upload_image.getString(5));
												 * Log.v("",
												 * "visibility_id="+upload_image
												 * .getString(6));
												 *
												 * }while(upload_image.moveToNext
												 * ());
												 *
												 *
												 * }
												 *//*
                                                if (upload_image.getCount() > 0) {

                                                    if (upload_image != null
                                                            && upload_image
                                                            .moveToFirst()) {

                                                        upload_image
                                                                .moveToFirst();

                                                        do {

                                                            Log.e("image path.==",
                                                                    ""
                                                                            + upload_image
                                                                            .getString(2));

                                                            Log.e("imagecapture_date.==",
                                                                    ""
                                                                            + upload_image
                                                                            .getString(4));
                                                            Log.e("IMAGE.==",
                                                                    "IMAGE_NAME=="
                                                                            + upload_image
                                                                            .getString(5));

                                                            int visibilityid = 0;

                                                            if (upload_image
                                                                    .getString(
                                                                            6)
                                                                    .toString() != null) {

                                                                visibilityid = Integer
                                                                        .parseInt(upload_image
                                                                                .getString(
                                                                                        6)
                                                                                .toString());

                                                                soap_result_upload_data = service
                                                                        .UploadImage(
                                                                                upload_image
                                                                                        .getString(2),
                                                                                // image_array1.getString(2),
                                                                                upload_image
                                                                                        .getString(5),
                                                                                visibilityid,
                                                                                upload_image
                                                                                        .getString(4));

                                                                Log.e("in weservice",
                                                                        "Result="
                                                                                + soap_result_upload_data
                                                                                .toString());

                                                            } else {

                                                            }

                                                            if (soap_result_upload_data == null) {

                                                                // String errors
                                                                // =
                                                                // "Soap in giving null while 'IMAGE VISIBILITY2' and 'checkSyncFlag = 2' in  data Sync";
                                                                // we.writeToSD(errors.toString());

                                                                final Calendar calendar1 = Calendar
                                                                        .getInstance();
                                                                SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                                        "MM/dd/yyyy HH:mm:ss");
                                                                String Createddate = formatter1
                                                                        .format(calendar1
                                                                                .getTime());
                                                                ErroFlag = "0";
                                                                Erro_function = "Upload Image Data()- null";
                                                                int n = Thread
                                                                        .currentThread()
                                                                        .getStackTrace()[2]
                                                                        .getLineNumber();
                                                                db.insertSyncLog(
                                                                        "Internet Connection Lost, Soap in giving null while 'Upload Image Data'",
                                                                        String.valueOf(n),
                                                                        "UploadImage()",
                                                                        Createddate,
                                                                        Createddate,
                                                                        sp.getString(
                                                                                "username",
                                                                                ""),
                                                                        "Transaction Upload",
                                                                        "Fail");

                                                            } else if (soap_result_upload_data
                                                                    .toString()
                                                                    .equalsIgnoreCase(
                                                                            "SE")) {

                                                                final Calendar calendar1 = Calendar
                                                                        .getInstance();
                                                                SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                                        "MM/dd/yyyy HH:mm:ss");
                                                                String Createddate = formatter1
                                                                        .format(calendar1
                                                                                .getTime());
                                                                ErroFlag = "0";
                                                                Erro_function = "Upload Image Data()_SE";
                                                                int n = Thread
                                                                        .currentThread()
                                                                        .getStackTrace()[2]
                                                                        .getLineNumber();
                                                                db.insertSyncLog(
                                                                        "Visibility-Upload Image Data_SE",
                                                                        String.valueOf(n),
                                                                        "Upload Image Data()",
                                                                        Createddate,
                                                                        Createddate,
                                                                        sp.getString(
                                                                                "username",
                                                                                ""),
                                                                        "Transaction Upload",
                                                                        "Fail");

                                                            } else {

                                                                // String
                                                                // result_visibility
                                                                // =
                                                                // soap_result_img.toString();
                                                                String result_visibility1 = soap_result_upload_data
                                                                        .toString();
                                                                // Log.v("",
                                                                // "result_visibility="
                                                                // +
                                                                // result_visibility);
                                                                Log.v("",
                                                                        "result_visibility1="
                                                                                + result_visibility1);

                                                                if (result_visibility1
                                                                        .equalsIgnoreCase("TRUE")) {

                                                                    db.update_image_data(upload_image
                                                                            .getString(0));

                                                                }

                                                            }

                                                        } while (upload_image
                                                                .moveToNext());

                                                    }
                                                }
                                                // -----------------------------

                                                try {

                                                    Soap_result_image_name = service
                                                            .GetVisibilityNotReceivedImage(username);

                                                    Log.e("",
                                                            "Soap_result_image_name="
                                                                    + Soap_result_image_name
                                                                    .toString());

                                                    if (Soap_result_image_name != null) {

                                                        for (int i = 0; i < Soap_result_image_name
                                                                .getPropertyCount(); i++) {
                                                            Log.e("pm", "pm2");

                                                            Soap_result_image_name1 = (SoapObject) Soap_result_image_name
                                                                    .getProperty(i);

                                                            Log.e("pm", "pm3");

                                                            if (Soap_result_image_name1
                                                                    .getProperty("status") != null) {

                                                                if (Soap_result_image_name1
                                                                        .getProperty(
                                                                                "status")
                                                                        .toString()
                                                                        .equalsIgnoreCase(
                                                                                "C")) {
                                                                    Log.e("pm",
                                                                            "pm4");
                                                                    try {

                                                                        String VisibilityId = Soap_result_image_name1
                                                                                .getProperty(
                                                                                        "VisibilityId")
                                                                                .toString();
                                                                        String ImageName = Soap_result_image_name1
                                                                                .getProperty(
                                                                                        "ImageName")
                                                                                .toString();

                                                                        db.updateimageTable(
                                                                                VisibilityId,
                                                                                ImageName);

                                                                    } catch (Exception e) {

                                                                        e.printStackTrace();
                                                                    }

                                                                } else if (Soap_result_image_name1
                                                                        .getProperty(
                                                                                "status")
                                                                        .toString()
                                                                        .equalsIgnoreCase(
                                                                                "E")) {

                                                                    HashMap<String, String> map = new HashMap<String, String>();
                                                                    listofimages
                                                                            .clear();
                                                                    map.clear();

                                                                    db.open();
                                                                    listofimages = db
                                                                            .getimagesNOtUploaded();

                                                                    if (listofimages
                                                                            .size() > 0) {

                                                                        Log.i("No. of  Syn Error Log ",
                                                                                String.valueOf(listofimages
                                                                                        .size()));

                                                                        for (int m = 0; m < listofimages
                                                                                .size(); m++) {

                                                                            map = listofimages
                                                                                    .get(m);

                                                                            String image_name = map
                                                                                    .get("image_name");
                                                                            String imagePath = map
                                                                                    .get("imagePath");

                                                                            String id = map
                                                                                    .get("imageId");

                                                                            String image_name_return = ImageUtils
                                                                                    .getCompressedImagePath(
                                                                                            imagePath,
                                                                                            "",
                                                                                            "",
                                                                                            image_name);

                                                                            if (image_name_return == null) {

                                                                                Log.e("",
                                                                                        "image_name_return="
                                                                                                + image_name_return);
                                                                            } else {

                                                                                Log.e("",
                                                                                        "image_name_return="
                                                                                                + image_name_return);

                                                                                db.update_image_data(id);

                                                                            }

                                                                        }

                                                                    } else {
                                                                        Log.e("",
                                                                                "No Images for Upload");
                                                                    }

                                                                } else if (Soap_result_image_name1
                                                                        .getProperty(
                                                                                "status")
                                                                        .toString()
                                                                        .equalsIgnoreCase(
                                                                                "N")) {

                                                                }
                                                            } else {

                                                            }
                                                        }
                                                    } else {

                                                    }

                                                } catch (Exception e) {

                                                    e.printStackTrace();
                                                }

                                                // /////////////////////code
                                                // ends here

                                                Log.e("image path.==",
                                                        ""
                                                                + image_array1
                                                                .getString(2));

                                                Log.e("imagecapture_date.==",
                                                        ""
                                                                + image_array1
                                                                .getString(4));
                                                Log.e("IMAGE.==",
                                                        "IMAGE_NAME==5"
                                                                + image_array1
                                                                .getString(5));

                                                soap_result_img_data = service
                                                        .UploadImage(
                                                                image_array
                                                                        .getString(2),
                                                                // image_array1.getString(2),
                                                                image_array1
                                                                        .getString(5),
                                                                soapresultforvisibilityid,
                                                                image_array1
                                                                        .getString(4));

                                                Log.e("in weservice",
                                                        "Result="
                                                                + soap_result_img_data
                                                                .toString());

                                                if (soap_result_img_data == null) {

                                                    // String errors =
                                                    // "Soap in giving null while 'IMAGE VISIBILITY2' and 'checkSyncFlag = 2' in  data Sync";
                                                    // we.writeToSD(errors.toString());

                                                    final Calendar calendar1 = Calendar
                                                            .getInstance();
                                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                            "MM/dd/yyyy HH:mm:ss");
                                                    String Createddate = formatter1
                                                            .format(calendar1
                                                                    .getTime());
                                                    ErroFlag = "0";
                                                    Erro_function = "Upload Image Data()- null";
                                                    int n = Thread
                                                            .currentThread()
                                                            .getStackTrace()[2]
                                                            .getLineNumber();
                                                    db.insertSyncLog(
                                                            "Internet Connection Lost, Soap in giving null while 'Visibility-Upload Image Data'",
                                                            String.valueOf(n),
                                                            "UploadImage()",
                                                            Createddate,
                                                            Createddate,
                                                            sp.getString(
                                                                    "username",
                                                                    ""),
                                                            "Transaction Upload",
                                                            "Fail");

                                                } else if (soap_result_img_data
                                                        .toString()
                                                        .equalsIgnoreCase("SE")) {

                                                    final Calendar calendar1 = Calendar
                                                            .getInstance();
                                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                            "MM/dd/yyyy HH:mm:ss");
                                                    String Createddate = formatter1
                                                            .format(calendar1
                                                                    .getTime());
                                                    ErroFlag = "0";
                                                    Erro_function = "Upload Image Data()_SE";
                                                    int n = Thread
                                                            .currentThread()
                                                            .getStackTrace()[2]
                                                            .getLineNumber();
                                                    db.insertSyncLog(
                                                            "Visibility-Upload Image Data_SE",
                                                            String.valueOf(n),
                                                            "Upload Image Data()",
                                                            Createddate,
                                                            Createddate,
                                                            sp.getString(
                                                                    "username",
                                                                    ""),
                                                            "Transaction Upload",
                                                            "Fail");

                                                } else {

                                                    String result_visibility = soap_result_img
                                                            .toString();
                                                    String result_visibility1 = soap_result_img_data
                                                            .toString();
                                                    Log.v("",
                                                            "result_visibility="
                                                                    + result_visibility);
                                                    Log.v("",
                                                            "result_visibility1="
                                                                    + result_visibility1);

                                                    if (result_visibility1
                                                            .equalsIgnoreCase("TRUE")) {

                                                        db.update_image_data(image_array1
                                                                .getString(0));

                                                    }
                                                    //
                                                    String image_name_return = ImageUtils_btimap
                                                            .getCompressedImagePath(
                                                                    image_array1
                                                                            .getString(2),
                                                                    context);
                                                    // .getCompressedImagePath(
                                                    // image_array1.getString(2),
                                                    // username,
                                                    // producttype,image_array1.getString(5));

                                                }

												*//*
												 * String image_name_return =
												 * ImageUtils
												 * .getCompressedImagePath(
												 * image_array1 .getString(2),
												 * username, producttype); if
												 * (image_name_return==null) {
												 *
												 *
												 * Log.e("",
												 * "image_name_return=" +
												 * image_name_return); } else {
												 *
												 * Log.e("",
												 * "image_name_return=" +
												 * image_name_return);
												 *
												 *
												 *
												 * }
												 *//*

                                            } while (image_array1.moveToNext());

                                        }
                                    } else {

                                        // String errors =
                                        // "Soap in giving null while 'IMAGE VISIBILITY' and 'checkSyncFlag = 2' in  data Sync";
                                        // we.writeToSD(errors.toString());

                                        ErroFlag = "0";
                                        Erro_function = "visibility()";
                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "Internet Connection Lost, Soap in giving null while 'Visibility-SaveVisibility Data'",
                                                String.valueOf(n),
                                                "SaveVisibility()",
                                                Createddate, Createddate,
                                                sp.getString("username", ""),
                                                "Transaction Upload", "Fail");

                                    }

                                } while (image_array.moveToNext());

                            } else {

                            }
                        } else if (image_array == (null)) {

                        } else {
                            Log.e("Noimage dataupload",
                                    String.valueOf(image_array.getCount()));
                        }

						*//*
						 * if(soap_result_img != null && soap_result_img_data
						 * !=null &&
						 * !soap_result_img_data.toString().equalsIgnoreCase
						 * ("SE") ){
						 *
						 * String result_visibility =
						 * soap_result_img.toString(); String result_visibility1
						 * = soap_result_img_data.toString(); Log.v("",
						 * "result_visibility=" + result_visibility); Log.v("",
						 * "result_visibility1=" + result_visibility1);
						 *
						 *
						 *
						 * if (result_visibility1.equalsIgnoreCase("TRUE")) {
						 *
						 * db.update_scan_data(); db.update_image_data();
						 *
						 * db.close();
						 *
						 * } }
						 *//*
                    } catch (Exception e) {

                        e.printStackTrace();

                        ErroFlag = "0";
                        // Erro_function = "StoreErrorLogTablettxt_Upload";
                        String Error = e.toString();

                        final Calendar calendar1 = Calendar.getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(calendar1
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(Error, String.valueOf(n),
                                "Visibility()", Createddate, Createddate,
                                sp.getString("username", ""), "Visibility()",
                                "Fail");

                    }
*/
                    // -------------------------

                    try {

                        ContentValues contentvalues = new ContentValues();
                        HashMap<String, String> map = new HashMap<String, String>();
                        listofsyncerrorlog.clear();
                        map.clear();
                        db.open();
                        listofsyncerrorlog = db.GETERRORLOGS();

                        if (listofsyncerrorlog.size() > 0) {

                            Log.i("No. of  Syn Error Log ",
                                    String.valueOf(listofsyncerrorlog.size()));

                            String eid;
                            String Exception;
                            String Lineno;
                            String Method;
                            String username_r;
                            String sync_method;
                            String status;
                            String errordate;

                            for (int m = 0; m < listofsyncerrorlog.size(); m++) {

                                map = listofsyncerrorlog.get(m);

                                if (map.get("ID") != null) {

                                    eid = map.get("ID");
                                } else {

                                    eid = "";
                                }
                                // String eid = map.get("ID");
                                if (map.get("EXCEPTION") != null) {

                                    Exception = map.get("EXCEPTION");
                                } else {
                                    Exception = "";
                                }
                                // String Exception = map.get("EXCEPTION");
                                if (map.get("LINE_NO") != null) {
                                    Lineno = map.get("LINE_NO");

                                } else {
                                    Lineno = "";
                                }
                                if (map.get("METHOD") != null) {

                                    Method = map.get("METHOD");
                                } else {
                                    Method = "";
                                }
                                // String Lineno = map.get("LINE_NO");
                                // String Method = map.get("METHOD");
                                if (map.get("USERNAME") != null) {

                                    username_r = map.get("USERNAME");

                                } else {

                                    username_r = "";
                                }
                                if (map.get("SYNCMETHOD") != null) {

                                    sync_method = map.get("SYNCMETHOD");
                                } else {

                                    sync_method = "";
                                }

                                if (map.get("RESULT") != null) {

                                    status = map.get("RESULT");
                                } else {

                                    status = "";
                                }

                                if (map.get("CREATED_DATE") != null) {

                                    errordate = map.get("CREATED_DATE");
                                } else {

                                    errordate = "";
                                }
                                // String username = map.get("USERNAME");
                                // String sync_method = map
                                // .get("SYNCMETHOD");
                                // String status = map.get("RESULT");

                                SoapPrimitive soapObj123;
                                soapObj123 = service.StoreErrorLogTablettxt(
                                        Exception, Lineno, Method, username_r,
                                        sync_method, status, errordate);

                                if (soapObj123 != null) {

                                    String statussss = soapObj123.toString();

                                    if (statussss.equalsIgnoreCase("True")) {

                                        final Calendar calendar = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String date = formatter.format(calendar
                                                .getTime());

                                        contentvalues.clear();
                                        // contentvalues.put("LAST_SYNC",
                                        // date);
                                        contentvalues.put("FLAG", "U");
                                        db.updatevalues("SYNC_LOG",
                                                contentvalues,"ID", eid);
                                        db.delete_errorlog_data();

                                    } else if (statussss.equalsIgnoreCase("SE")) {

                                        ErroFlag = "0";
                                        Erro_function = "StoreErrorLogTablettxt_Upload_SE";

                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();

                                        db.insertSyncLog(
                                                "StoreErrorLogTablettxt_Upload_SE",
                                                String.valueOf(n),
                                                "StoreErrorLogTablettxt()",
                                                Createddate, Createddate,
                                                sp.getString("username", ""),
                                                "StoreErrorLogTablettxt()",
                                                "Fail");

                                    }

                                } else {
                                    ErroFlag = "0";
                                    Erro_function = "StoreErrorLogTablettxt_Upload";

                                    final Calendar calendar1 = Calendar
                                            .getInstance();
                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                            "MM/dd/yyyy HH:mm:ss");
                                    String Createddate = formatter1
                                            .format(calendar1.getTime());

                                    int n = Thread.currentThread()
                                            .getStackTrace()[2].getLineNumber();

                                    db.insertSyncLog(
                                            "Internet Connection Lost, Soap in giving null while 'StoreErrorLogTablettxt'",
                                            String.valueOf(n),
                                            "StoreErrorLogTablettxt()",
                                            Createddate, Createddate,
                                            sp.getString("username", ""),
                                            "StoreErrorLogTablettxt()", "Fail");

                                    // String errors=
                                    // "Internet Connection lost!! Soap in giving null while 'StoreErrorLogTablettxt_Upload' and 'checkSyncFlag = 0' in Upload data Sync";
                                    // we.writeToSD(errors.toString());

                                    Log.e("Error in Sync Error log", String
                                            .valueOf(listofsyncerrorlog.size()));
                                }

                            }

                        } else if (listofsyncerrorlog == null) {

                        } else {
                            Log.e("NoSync errorupload",
                                    String.valueOf(listofsyncerrorlog.size()));

                        }

                    } catch (Exception e) {

                        StringWriter errors = new StringWriter();
                        e.printStackTrace(new PrintWriter(errors));

                        e.printStackTrace();
                        // we.writeToSD(errors.toString());
                        String error = e.toString();

                        final Calendar calendar1 = Calendar.getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(calendar1
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();

                        db.insertSyncLog(error, String.valueOf(n),
                                "StoreErrorLogTablettxt()", Createddate,
                                Createddate, sp.getString("username", ""),
                                "StoreErrorLogTablettxt()", "Fail");

                    }

                    // new method
                    db.open();
                    Cursor cursor = db.fetchallSpecifyMSelect(
                            "stock_monthwise", null, "savedServer = 0 ", null,
                            null);
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        do {
                            try {
                                String Pid = cursor.getString(2);
                                String CatCodeId = cursor.getString(1);
                                String EANCode = cursor.getString(3);
                                String empId = cursor.getString(9);
                                String ProductCategory = cursor.getString(4);
                                String product_type = cursor.getString(5);
                                String product_name = cursor.getString(6);
                                String shadeno = cursor.getString(23);
                                String Opening_Stock = cursor.getString(10);
                                String FreshStock = cursor.getString(11);
                                String Stock_inhand = cursor.getString(12);
                                String SoldStock = cursor.getString(16);
                                String S_Return_Saleable = cursor.getString(14);
                                String S_Return_NonSaleable = cursor
                                        .getString(15);
                                String ClosingBal = cursor.getString(13);
                                String GrossAmount = cursor.getString(17);
                                String Discount = cursor.getString(19);
                                String NetAmount = cursor.getString(18);
                                String Size = cursor.getString(7);
                                String Price = cursor.getString(8);
                                String AndroidCreatedDate = cursor
                                        .getString(21);
                                String Month = cursor.getString(25);

                                SoapPrimitive soap_result_cumm = service
                                        .InsertStockCummData(Pid, CatCodeId,
                                                EANCode, empId,
                                                ProductCategory, product_type,
                                                product_name, shadeno,
                                                Opening_Stock, FreshStock,
                                                Stock_inhand, SoldStock,
                                                S_Return_Saleable,
                                                S_Return_NonSaleable,
                                                ClosingBal, GrossAmount,
                                                Discount, NetAmount, Size,
                                                Price, AndroidCreatedDate,
                                                Month);

                                if (soap_result_cumm != null) {

                                    String result_stock_cumm = soap_result_cumm
                                            .toString();

                                    Log.v("", "result_stock_cumm="
                                            + result_stock_cumm);

                                    if (result_stock_cumm
                                            .equalsIgnoreCase("TRUE")) {

                                        Log.e("", "stock_cumm id for update=="
                                                + cursor.getString(0));

                                        db.open();
                                        db.update_stock_cumm(cursor
                                                .getString(0));
                                        db.close();

                                    } else if (result_stock_cumm
                                            .equalsIgnoreCase("SE")) {

                                        ErroFlag = "0";
                                        Erro_function = "InsertStockCummData()_SE";
                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "InsertStockCummData_SE",
                                                String.valueOf(n),
                                                "InsertStockCummData()",
                                                Createddate, Createddate,
                                                sp.getString("username", ""),
                                                "Data Upload", "Fail");

                                    }

                                } else {

                                    ErroFlag = "0";
                                    Erro_function = "InsertStockCummData()-null";
                                    // String errors =
                                    // "Soap in giving null while 'Stock' and 'checkSyncFlag = 2' in  data Sync";
                                    // we.writeToSD(errors.toString());
                                    final Calendar calendar1 = Calendar
                                            .getInstance();
                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                            "MM/dd/yyyy HH:mm:ss");
                                    String Createddate = formatter1
                                            .format(calendar1.getTime());

                                    int n = Thread.currentThread()
                                            .getStackTrace()[2].getLineNumber();
                                    db.insertSyncLog(
                                            "Internet Connection Lost, Soap in giving null while 'InsertStockCummData'",
                                            String.valueOf(n),
                                            "InsertStockCummData()",
                                            Createddate, Createddate,
                                            sp.getString("username", ""),
                                            "Data Upload", "Fail");

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } while (cursor.moveToNext());
                    }
                    db.close();

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

            mProgress.dismiss();
            if (Flag.equalsIgnoreCase("3")) {

                DisplayDialogMessage("Connectivity Error Please check internet");

            }
            Log.d("Errr flag is", "" + ErroFlag);
            if (ErroFlag.equalsIgnoreCase("0")) {

                DisplayDialogMessage("Uploading data Incomplete !! Check Data Network & Try again");

			/*	Toast.makeText(getApplicationContext(),
						"Uploading data Incomplete !! Try again",
						Toast.LENGTH_SHORT).show();*/
            }
            if (ErroFlag.equalsIgnoreCase("1")) {

                DisplayDialogMessage("Data Upload Completed Successfully!!");
				/*Toast.makeText(getApplicationContext(),
						"Data Upload Completed Successfully!!",
						Toast.LENGTH_SHORT).show();*/
            }

        }

        private void DisplayDialogMessage(String msg) {

            AlertDialog.Builder builder = new AlertDialog.Builder(SyncMaster.this);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public class InsertFirstTimeMaster extends
            AsyncTask<Void, Void, SoapObject> {

        private SoapObject soap_result = null;

        SoapObject soap_result1 = null;
        String Flag;

        SoapPrimitive soap_update_stock_row = null;

        // ---------
        SoapPrimitive soap_update_tester_row = null;
        private SoapObject soap_result_tester = null;
        SoapObject soap_result_tester1 = null;
        // --------

        // ------------------
        SoapPrimitive soap_update_boc_day_row = null;
        private SoapObject soap_result_boc_day = null;
        SoapObject soap_result_boc_day1 = null;
        // ------------------

        // ------------------
        SoapPrimitive soap_update_monthwise_row = null;
        private SoapObject soap_result_monthwise = null;
        SoapObject soap_result_monthwise1 = null;
        // ------------------

        int syncstockdata = 1, synctesterdata = 1, syncbocdaywise = 1,
                syncbocmonthwise = 1;

        String db_stock_id_array = "", db_tester_id_array = "",
                db_bocmonthstock_id_array = "", db_cumvalueid_array = "";

        String EmpId = sp.getString("username", "");
        String Erro_function = "";
        String ErroFlag = "";

        @Override
        protected SoapObject doInBackground(Void... params) {

            try {

                if (!cd.isConnectingToInternet()) {

                    Flag = "0";
                    // stop executing code by return

                } else {

                    // Flag = "1";

                    // ---------------------------

                    // --------------------------------upload
                    // stock-----------------------------------01.03.2016

                   /* try {
                        Log.e("", "saveto server1-stcok");
                        db.open();
                        stock_array = db.getStockdetails();
                        // db.close();
                        // ------------------

                        if (stock_array.getCount() > 0) {

                            if (stock_array != null
                                    && stock_array.moveToFirst()) {
                                stock_array.moveToFirst();

                                String shad;
                                do {

                                    Log.v("",
                                            "dbid=" + stock_array.getString(2));
                                    Log.v("",
                                            "category_id="
                                                    + stock_array.getString(1));
                                    Log.v("",
                                            "enacode="
                                                    + stock_array.getString(3));
                                    Log.v("",
                                            "empid=" + stock_array.getString(9));
                                    Log.v("",
                                            "product_category="
                                                    + stock_array.getString(4));
                                    Log.v("",
                                            "product_type="
                                                    + stock_array.getString(5));
                                    Log.v("",
                                            "product_name="
                                                    + stock_array.getString(6));

                                    Log.v("",
                                            "opening_stock="
                                                    + stock_array.getString(10));
                                    Log.v("",
                                            "stock_receive="
                                                    + stock_array.getString(11));
                                    Log.v("",
                                            "stock_inhand="
                                                    + stock_array.getString(12));
                                    Log.v("",
                                            "sold=" + stock_array.getString(16));
                                    Log.v("",
                                            "return_s="
                                                    + stock_array.getString(14));
                                    Log.v("",
                                            "return_ns="
                                                    + stock_array.getString(15));
                                    Log.v("",
                                            "close_bal="
                                                    + stock_array.getString(13));
                                    Log.v("",
                                            "t_gross="
                                                    + stock_array.getString(17));
                                    Log.v("",
                                            "discount="
                                                    + stock_array.getString(19));
                                    Log.v("",
                                            "net_amount="
                                                    + stock_array.getString(18));
                                    Log.v("",
                                            "size=" + stock_array.getString(7));
                                    Log.v("",
                                            "price=" + stock_array.getString(8));
                                    Log.v("",
                                            "insert_date"
                                                    + stock_array.getString(21));

                                    if (stock_array.getString(23) != null
                                            || !stock_array.getString(23)
                                            .equalsIgnoreCase("null")) {

                                        Log.v("",
                                                "shadeno="
                                                        + stock_array
                                                        .getString(23));
                                        shad = stock_array.getString(23)
                                                .toString();

                                    } else {
                                        Log.v("",
                                                "shadeno="
                                                        + stock_array
                                                        .getString(23));
                                        shad = "";
                                    }

                                    SoapPrimitive soap_result_stock = service
                                            .SaveStock(
                                                    stock_array.getString(0),
                                                    stock_array.getString(2),
                                                    stock_array.getString(1),
                                                    stock_array.getString(3),
                                                    username,
                                                    stock_array.getString(4),
                                                    stock_array.getString(5),
                                                    stock_array.getString(6),
                                                    shad,

                                                    stock_array.getString(10),
                                                    stock_array.getString(11),
                                                    stock_array.getString(12),

                                                    stock_array.getString(16),
                                                    stock_array.getString(14),
                                                    stock_array.getString(15),

                                                    stock_array.getString(13),
                                                    stock_array.getString(17),

                                                    stock_array.getString(19),
                                                    stock_array.getString(18),
                                                    stock_array.getString(7),
                                                    stock_array.getString(8),
                                                    stock_array.getString(21)

                                            );

									*//*
									 * soap_result_stock = service.SaveStock(
									 * stock_array.getString(2),
									 * stock_array.getString(1), eancode_string,
									 * username, stock_array.getString(4),
									 * stock_array.getString(5),
									 * stock_array.getString(6), shad,
									 *
									 * opening_stock_string,
									 * stock_receive_string,
									 * stock_in_hand_string,
									 *
									 * sold_string, return_salable_string,
									 * return_non_salable_string,
									 *
									 * close_bal_string, gross_amount_string,
									 *
									 * discount_string, net_amount_string,
									 * size_string, price_string,
									 * stock_array.getString(21)
									 *
									 *
									 * );
									 *//*

                                    if (soap_result_stock != null) {
                                        String result_stock = soap_result_stock
                                                .toString();
                                        Log.v("", "result_stock="
                                                + result_stock);
                                        if (result_stock.matches(".*\\d+.*")) {
                                            Log.e("", "stock id for update=="
                                                    + stock_array.getString(0));
                                            db.open();
                                            db.update_stock_data(result_stock);
                                            db.close();

                                        } else if (result_stock
                                                .equalsIgnoreCase("SE")) {

                                            ErroFlag = "0";
                                            Erro_function = "SaveStock()_SE";
                                            final Calendar calendar1 = Calendar
                                                    .getInstance();
                                            SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                    "MM/dd/yyyy HH:mm:ss");
                                            String Createddate = formatter1
                                                    .format(calendar1.getTime());

                                            int n = Thread.currentThread()
                                                    .getStackTrace()[2]
                                                    .getLineNumber();
                                            db.insertSyncLog("SaveStock_SE",
                                                    String.valueOf(n),
                                                    "SaveStock()", Createddate,
                                                    Createddate, sp.getString(
                                                            "username", ""),
                                                    "SaveStock()", "Fail");

                                        }

                                    } else {

                                        ErroFlag = "0";
                                        Erro_function = "SaveStock()-null";
                                        // String errors =
                                        // "Soap in giving null while 'Stock' and 'checkSyncFlag = 2' in  data Sync";
                                        // we.writeToSD(errors.toString());
                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "Internet Connection Lost, Soap in giving null while 'SaveStock'",
                                                String.valueOf(n),
                                                "SaveStock()", Createddate,
                                                Createddate,
                                                sp.getString("username", ""),
                                                "SaveStock()", "Fail");

                                    }

                                } while (stock_array.moveToNext());

                            } else {
                                // Toast.makeText(getActivity(),
                                // "No Data Available or Check Connectivity",
                                // .LENGTH_SHORT).show();
                                Flag = "2";
                                Log.e("", "no data available");

                            }

                        } else if (stock_array == null) {

                        } else {
                            Log.e("NoStock dataupload",
                                    String.valueOf(stock_array.getCount()));
                        }

                    } catch (Exception e) {
                        ErroFlag = "0";
                        Erro_function = "SaveStock()";
                        e.printStackTrace();

                        String Error = e.toString();

                        final Calendar cal = Calendar.getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(cal.getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(Error, String.valueOf(n),
                                "SaveStock()", Createddate, Createddate,
                                sp.getString("username", ""), "SaveStock()",
                                "Fail");

                    }*/

                    // -----------------------------------------------------------------------------01.03.2016

                    String lastdate;
                    db.open();
                    String lastdatesync = db.getLastSyncDate("stock");

					/*if(lastdatesync != null && lastdatesync.length()>0) {
						String[] dateParts = lastdatesync.split(" ");
						String date1 = dateParts[0];
						SimpleDateFormat sdf = new SimpleDateFormat(
								"MM/dd/yyyy", Locale.ENGLISH);//Using UAT server
						Date curntdte = null;
						try {
							curntdte = sdf.parse(date1);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						sdf.applyPattern("yyyy-MM-dd");
						lastdate = sdf.format(curntdte);
					}else{
						lastdate = "";
					}*/

                    db.close();

                    Calendar calendar1 = Calendar.getInstance();
                    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd ");
                    String strDate = mdformat.format(calendar1.getTime());

                    soap_result = service.DataDownload(
                            sp.getString("username", ""), strDate);//strDate

                    if (soap_result != null) {

                        for (int i = 0; i < soap_result.getPropertyCount(); i++) {

                            soap_result1 = (SoapObject) soap_result
                                    .getProperty(i);

                            Log.e("pm",
                                    "status="
                                            + soap_result1
                                            .getProperty("status")
                                            .toString());

                            if (soap_result1.getProperty("status").toString()
                                    .equalsIgnoreCase("C")) {

                                String db_stock_id = soap_result1.getProperty(
                                        "Id").toString();

                                String db_Id = soap_result1.getProperty(
                                        "ProductId").toString();

                                Log.v("", "db_Id=" + db_Id);

                                String CatCodeId = soap_result1.getProperty(
                                        "CatCodeId").toString();

                                if (CatCodeId == null) {
                                    CatCodeId = "";

                                }
                                Log.v("", "CatCodeId=" + CatCodeId);

                                String ProductId = soap_result1.getProperty(
                                        "ProductId").toString();
                                Log.v("", "ProductId=" + ProductId);

                                if (ProductId == null) {
                                    ProductId = "";

                                }

                                String EANCode = soap_result1.getProperty(
                                        "EANCode").toString();

                                if (EANCode == null) {
                                    EANCode = "";

                                }
                                EmpId = soap_result1.getProperty("EmpId")
                                        .toString();

                                if (EmpId == null) {
                                    EmpId = "";

                                }
                                String ProductCategory = soap_result1
                                        .getProperty("ProductCategory")
                                        .toString();

                                if (ProductCategory == null) {
                                    ProductCategory = "";

                                }
                                String ProductType = soap_result1.getProperty(
                                        "ProductType").toString();

                                if (ProductType == null) {
                                    ProductType = "";

                                }
                                String ProductName = soap_result1.getProperty(
                                        "ProductName").toString();
                                if (ProductName == null) {
                                    ProductName = "";

                                }

                                String Opening_Stock = soap_result1
                                        .getProperty("Opening_Stock")
                                        .toString();
                                if (Opening_Stock == null) {
                                    Opening_Stock = "";

                                }

                                String FreshStock = soap_result1.getProperty(
                                        "FreshStock").toString();
                                if (FreshStock == null) {
                                    FreshStock = "";

                                }

                                String Stock_inhand = soap_result1.getProperty(
                                        "Stock_inhand").toString();

                                if (Stock_inhand == null) {
                                    Stock_inhand = "";

                                }
                                String SoldStock = soap_result1.getProperty(
                                        "SoldStock").toString();

                                if (SoldStock == null) {
                                    SoldStock = "";

                                }
                                String S_Return_Saleable = soap_result1
                                        .getProperty("S_Return_Saleable")
                                        .toString();

                                if (S_Return_Saleable == null) {
                                    S_Return_Saleable = "";

                                }
                                String S_Return_NonSaleable = soap_result1
                                        .getProperty("S_Return_NonSaleable")
                                        .toString();

                                if (S_Return_NonSaleable == null) {
                                    S_Return_NonSaleable = "";

                                }
                                String ClosingBal = soap_result1.getProperty(
                                        "ClosingBal").toString();

                                if (ClosingBal == null) {
                                    ClosingBal = "";

                                }
                                String GrossAmount = soap_result1.getProperty(
                                        "GrossAmount").toString();

                                if (GrossAmount == null) {
                                    GrossAmount = "";

                                }
                                String Discount = soap_result1.getProperty(
                                        "Discount").toString();

                                if (Discount == null) {
                                    Discount = "";

                                }
                                String NetAmount = soap_result1.getProperty(
                                        "NetAmount").toString();

                                if (NetAmount == null) {
                                    NetAmount = "";

                                }
                                String Size = soap_result1.getProperty("Size")
                                        .toString();

                                if (Size == null) {
                                    Size = "";

                                }
                                String Price = soap_result1
                                        .getProperty("Price").toString();

                                if (Price == null) {
                                    Price = "";

                                }
                                String LMD = soap_result1.getProperty("LMD")
                                        .toString();

                                if (LMD == null) {
                                    LMD = "";

                                } else {

                                    try {
                                        String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                        String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                        DateFormat inputFormat = new SimpleDateFormat(
                                                inputPattern);
                                        SimpleDateFormat outputFormat = new SimpleDateFormat(
                                                outputPattern);

                                        Date date = inputFormat.parse(LMD);
                                        LMD = outputFormat.format(date);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                String AndroidCreatedDate = soap_result1
                                        .getProperty("AndroidCreatedDate")
                                        .toString();

                                String MONTH = "", YEAR = "";
                                if (AndroidCreatedDate == null) {
                                    AndroidCreatedDate = "";

                                } else {

                                    try {
                                        String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                        String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                        SimpleDateFormat inputFormat = new SimpleDateFormat(
                                                inputPattern);
                                        SimpleDateFormat outputFormat = new SimpleDateFormat(
                                                outputPattern);

                                        Date date = inputFormat
                                                .parse(AndroidCreatedDate);
                                        AndroidCreatedDate = outputFormat
                                                .format(date);

                                        String[] addd = AndroidCreatedDate
                                                .split(" ");
                                        String addd1 = addd[0];
                                        String[] addd2 = addd1.split("-");

                                        String month = addd2[1];
                                        YEAR = addd2[0];
                                        //
                                        SimpleDateFormat monthParse = new SimpleDateFormat(
                                                "MM");
                                        SimpleDateFormat monthDisplay = new SimpleDateFormat(
                                                "MMMM");
                                        MONTH = monthDisplay.format(monthParse
                                                .parse(month));
                                        //

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (db_Id.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for db_Id");
                                    db_Id = " ";
                                }
                                if (CatCodeId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for CatCodeId");
                                    CatCodeId = " ";
                                }

                                if (ProductId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductId");
                                    ProductId = " ";
                                }
                                if (EANCode.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for EANCode");
                                    EANCode = " ";
                                }

                                if (EmpId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for EmpId");
                                    EmpId = " ";
                                }
                                if (ProductCategory
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductCategory");
                                    ProductCategory = " ";
                                }
                                if (ProductType.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductType");
                                    ProductType = " ";
                                }

                                if (ProductName.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductName");
                                    ProductName = " ";
                                }
                                if (Opening_Stock.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Opening_Stock");
                                    Opening_Stock = " ";
                                }

                                if (FreshStock.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for FreshStock");
                                    FreshStock = " ";
                                }
                                if (Stock_inhand.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Stock_inhand");
                                    Stock_inhand = " ";
                                }
                                if (SoldStock.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for SoldStock");
                                    SoldStock = " ";
                                }
                                if (S_Return_Saleable
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for S_Return_Saleable");
                                    S_Return_Saleable = " ";
                                }
                                if (S_Return_NonSaleable
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("",
                                            "anytype for S_Return_NonSaleable");
                                    S_Return_NonSaleable = " ";
                                }
                                if (ClosingBal.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ClosingBal");
                                    ClosingBal = " ";
                                }
                                if (GrossAmount.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for GrossAmount");
                                    GrossAmount = " ";
                                }
                                if (Discount.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Discount");
                                    Discount = " ";
                                }
                                if (NetAmount.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for NetAmount");
                                    NetAmount = " ";
                                }
                                if (Size.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Size");
                                    Size = " ";
                                }
                                if (Price.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for sku_l");
                                    Price = " ";
                                }
                                if (LMD.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for LMD");
                                    LMD = " ";
                                }
                                if (AndroidCreatedDate
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for AndroidCreatedDate");
                                    AndroidCreatedDate = " ";
                                }

                                Log.e("pm", "pm5--");
                                db.open();

                                Cursor c1 = db.CheckDataExist("stock", db_Id,
                                        ProductCategory, ProductType,
                                        ProductName);


                                int count = c1.getCount();
                                Log.v("", "" + count);
                                db.close();
                                if (count > 0) {

                                    db.open();
                                    db.UpdateStockSync1(ProductCategory,
                                            ProductType, ProductName, EmpId,
                                            Opening_Stock, Stock_inhand, ClosingBal,
                                            FreshStock, GrossAmount, SoldStock,
                                            Price, Size, db_Id, LMD, Discount,
                                            NetAmount,
                                            S_Return_Saleable,
                                            S_Return_NonSaleable);
                                    db.close();

                                    db_stock_id_array = db_stock_id_array + ","
                                            + db_Id;

                                } else {

                                    Log.e("pm", "pm5");
                                    db.open();
                                    db.insertProductMasterFirsttime(
                                            db_stock_id, db_Id, ProductId,
                                            CatCodeId, EANCode, EmpId,
                                            ProductCategory, ProductType,
                                            ProductName, Opening_Stock,
                                            FreshStock, Stock_inhand,
                                            SoldStock, S_Return_NonSaleable,
                                            S_Return_Saleable, ClosingBal,
                                            GrossAmount, Discount, NetAmount,
                                            Size, Price, LMD,
                                            AndroidCreatedDate, MONTH, YEAR);
                                    db.close();

                                    db_stock_id_array = db_stock_id_array + ","
                                            + db_Id;

                                }

                            } else if (soap_result1.getProperty("status")
                                    .toString().equalsIgnoreCase("E")) {
                                Log.e("pm", "pm7");
                                // Flag = "1";

                                writeStringAsFile(db_stock_id_array);

                                soap_update_stock_row = service
                                        .UpdateTableData(db_stock_id_array,
                                                "S", EmpId);

                                SimpleDateFormat dateFormat = new SimpleDateFormat(
                                        "MM/dd/yyyy HH:mm:ss");
                                // get current date time with Date()
                                Calendar cal = Calendar.getInstance();
                                // dateFormat.format(cal.getTime())
                                db.open();
                                db.updateDateSync(
                                        dateFormat.format(cal.getTime()),
                                        "stock");
                                db.close();

                            } else if (soap_result1.getProperty("status")
                                    .toString().equalsIgnoreCase("N")) {

                                // Flag = "3";
                                Log.e("", "string ids== " + db_stock_id_array);
                                soap_update_stock_row = service
                                        .UpdateTableData(db_stock_id_array,
                                                "S", EmpId);
                                Log.e("", "soap_update_stock_row= "
                                        + soap_update_stock_row.toString());

                                syncstockdata = 1;
                            } else if (soap_result1.getProperty("status")
                                    .toString().equalsIgnoreCase("SE")) {

                                soap_update_stock_row = service
                                        .UpdateTableData(db_stock_id_array,
                                                "S", EmpId);
                                Log.e("", "soap_update_stock_row= "
                                        + soap_update_stock_row.toString());

                                // Flag="2";
                                Log.e("", "string ids== " + db_stock_id_array);
                                syncstockdata = 0;
                                final Calendar calendar = Calendar
                                        .getInstance();
                                SimpleDateFormat formatter = new SimpleDateFormat(
                                        "MM/dd/yyyy HH:mm:ss");
                                String Createddate = formatter.format(calendar
                                        .getTime());
                                Log.v("", "se error");
                                int n = Thread.currentThread().getStackTrace()[2]
                                        .getLineNumber();
                                db.open();
                                db.insertSyncLog("FirstTimeSync_SE",
                                        String.valueOf(n), "DataDownload()",
                                        Createddate, Createddate,
                                        sp.getString("username", ""),
                                        "DataDownload()", "Fail");
                                db.close();
                            }
                        }

                    } else {
                        Log.v("", "Soap result is null");

                        syncstockdata = 0;
                        final Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter.format(calendar
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog("Soup is null - DataDownload()",
                                String.valueOf(n), "DataDownload()",
                                Createddate, Createddate,
                                sp.getString("username", ""), "Data Download",
                                "Fail");

                    }

                    soap_result = service.DataDownloadForSale(
                            sp.getString("username", ""), strDate);//strDate

                    if (soap_result != null) {

                        for (int i = 0; i < soap_result.getPropertyCount(); i++) {

                            soap_result1 = (SoapObject) soap_result
                                    .getProperty(i);

                            Log.e("pm",
                                    "status="
                                            + soap_result1
                                            .getProperty("status")
                                            .toString());

                            if (soap_result1.getProperty("status").toString()
                                    .equalsIgnoreCase("C")) {

                                String db_stock_id = soap_result1.getProperty(
                                        "Id").toString();

                                String db_Id = soap_result1.getProperty(
                                        "ProductId").toString();

                                Log.v("", "db_Id=" + db_Id);

                                String CatCodeId = soap_result1.getProperty(
                                        "CatCodeId").toString();

                                if (CatCodeId == null) {
                                    CatCodeId = "";

                                }
                                Log.v("", "CatCodeId=" + CatCodeId);

                                String ProductId = soap_result1.getProperty(
                                        "ProductId").toString();
                                Log.v("", "ProductId=" + ProductId);

                                if (ProductId == null) {
                                    ProductId = "";

                                }

                                String EANCode = soap_result1.getProperty(
                                        "EANCode").toString();

                                if (EANCode == null) {
                                    EANCode = "";

                                }
                                EmpId = soap_result1.getProperty("EmpId")
                                        .toString();

                                if (EmpId == null) {
                                    EmpId = "";

                                }
                                String ProductCategory = soap_result1
                                        .getProperty("ProductCategory")
                                        .toString();

                                if (ProductCategory == null) {
                                    ProductCategory = "";

                                }
                                String ProductType = soap_result1.getProperty(
                                        "ProductType").toString();

                                if (ProductType == null) {
                                    ProductType = "";

                                }
                                String ProductName = soap_result1.getProperty(
                                        "ProductName").toString();
                                if (ProductName == null) {
                                    ProductName = "";

                                }

                                String Opening_Stock = soap_result1
                                        .getProperty("Opening_Stock")
                                        .toString();
                                if (Opening_Stock == null) {
                                    Opening_Stock = "";

                                }

                                String FreshStock = soap_result1.getProperty(
                                        "FreshStock").toString();
                                if (FreshStock == null) {
                                    FreshStock = "";

                                }

                                String Stock_inhand = soap_result1.getProperty(
                                        "Stock_inhand").toString();

                                if (Stock_inhand == null) {
                                    Stock_inhand = "";

                                }
                                String SoldStock = soap_result1.getProperty(
                                        "SoldStock").toString();

                                if (SoldStock == null) {
                                    SoldStock = "";

                                }
                                String S_Return_Saleable = soap_result1
                                        .getProperty("S_Return_Saleable")
                                        .toString();

                                if (S_Return_Saleable == null) {
                                    S_Return_Saleable = "";

                                }
                                String S_Return_NonSaleable = soap_result1
                                        .getProperty("S_Return_NonSaleable")
                                        .toString();

                                if (S_Return_NonSaleable == null) {
                                    S_Return_NonSaleable = "";

                                }
                                String ClosingBal = soap_result1.getProperty(
                                        "ClosingBal").toString();

                                if (ClosingBal == null) {
                                    ClosingBal = "";

                                }
                                String GrossAmount = soap_result1.getProperty(
                                        "GrossAmount").toString();

                                if (GrossAmount == null) {
                                    GrossAmount = "";

                                }
                                String Discount = soap_result1.getProperty(
                                        "Discount").toString();

                                if (Discount == null) {
                                    Discount = "";

                                }
                                String NetAmount = soap_result1.getProperty(
                                        "NetAmount").toString();

                                if (NetAmount == null) {
                                    NetAmount = "";

                                }
                                String Size = soap_result1.getProperty("Size")
                                        .toString();

                                if (Size == null) {
                                    Size = "";

                                }
                                String Price = soap_result1
                                        .getProperty("Price").toString();

                                if (Price == null) {
                                    Price = "";

                                }
                                String LMD = soap_result1.getProperty("LMD")
                                        .toString();

                                if (LMD == null) {
                                    LMD = "";

                                } else {

                                    try {
                                        String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                        String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                        DateFormat inputFormat = new SimpleDateFormat(
                                                inputPattern);
                                        SimpleDateFormat outputFormat = new SimpleDateFormat(
                                                outputPattern);

                                        Date date = inputFormat.parse(LMD);
                                        LMD = outputFormat.format(date);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                String AndroidCreatedDate = soap_result1
                                        .getProperty("AndroidCreatedDate")
                                        .toString();

                                String MONTH = "", YEAR = "";
                                if (AndroidCreatedDate == null) {
                                    AndroidCreatedDate = "";

                                } else {

                                    try {
                                        String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                        String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                        SimpleDateFormat inputFormat = new SimpleDateFormat(
                                                inputPattern);
                                        SimpleDateFormat outputFormat = new SimpleDateFormat(
                                                outputPattern);

                                        Date date = inputFormat
                                                .parse(AndroidCreatedDate);
                                        AndroidCreatedDate = outputFormat
                                                .format(date);

                                        String[] addd = AndroidCreatedDate
                                                .split(" ");
                                        String addd1 = addd[0];
                                        String[] addd2 = addd1.split("-");

                                        String month = addd2[1];
                                        YEAR = addd2[0];
                                        //
                                        SimpleDateFormat monthParse = new SimpleDateFormat(
                                                "MM");
                                        SimpleDateFormat monthDisplay = new SimpleDateFormat(
                                                "MMMM");
                                        MONTH = monthDisplay.format(monthParse
                                                .parse(month));
                                        //

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (db_Id.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for db_Id");
                                    db_Id = " ";
                                }
                                if (CatCodeId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for CatCodeId");
                                    CatCodeId = " ";
                                }

                                if (ProductId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductId");
                                    ProductId = " ";
                                }
                                if (EANCode.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for EANCode");
                                    EANCode = " ";
                                }

                                if (EmpId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for EmpId");
                                    EmpId = " ";
                                }
                                if (ProductCategory
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductCategory");
                                    ProductCategory = " ";
                                }
                                if (ProductType.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductType");
                                    ProductType = " ";
                                }

                                if (ProductName.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductName");
                                    ProductName = " ";
                                }
                                if (Opening_Stock.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Opening_Stock");
                                    Opening_Stock = " ";
                                }

                                if (FreshStock.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for FreshStock");
                                    FreshStock = " ";
                                }
                                if (Stock_inhand.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Stock_inhand");
                                    Stock_inhand = " ";
                                }
                                if (SoldStock.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for SoldStock");
                                    SoldStock = " ";
                                }
                                if (S_Return_Saleable
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for S_Return_Saleable");
                                    S_Return_Saleable = " ";
                                }
                                if (S_Return_NonSaleable
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("",
                                            "anytype for S_Return_NonSaleable");
                                    S_Return_NonSaleable = " ";
                                }
                                if (ClosingBal.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ClosingBal");
                                    ClosingBal = " ";
                                }
                                if (GrossAmount.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for GrossAmount");
                                    GrossAmount = " ";
                                }
                                if (Discount.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Discount");
                                    Discount = " ";
                                }
                                if (NetAmount.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for NetAmount");
                                    NetAmount = " ";
                                }
                                if (Size.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Size");
                                    Size = " ";
                                }
                                if (Price.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for sku_l");
                                    Price = " ";
                                }
                                if (LMD.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for LMD");
                                    LMD = " ";
                                }
                                if (AndroidCreatedDate
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for AndroidCreatedDate");
                                    AndroidCreatedDate = " ";
                                }

                                Log.e("pm", "pm5--");
                                db.open();

                                Cursor c1 = db.CheckDataExist("stock", db_Id,
                                        ProductCategory, ProductType,
                                        ProductName);


                                int count = c1.getCount();
                                Log.v("", "" + count);
                                db.close();
                                if (count > 0) {

                                    db.open();
                                    db.UpdateStockSync1(ProductCategory,
                                            ProductType, ProductName, EmpId,
                                            Opening_Stock, Stock_inhand, ClosingBal,
                                            FreshStock, GrossAmount, SoldStock,
                                            Price, Size, db_Id, LMD, Discount,
                                            NetAmount,
                                            S_Return_Saleable,
                                            S_Return_NonSaleable);
                                    db.close();

                                    db_stock_id_array = db_stock_id_array + ","
                                            + db_Id;

                                } else {

                                    Log.e("pm", "pm5");
                                    db.open();
                                    db.insertProductMasterFirsttime(
                                            db_stock_id, db_Id, ProductId,
                                            CatCodeId, EANCode, EmpId,
                                            ProductCategory, ProductType,
                                            ProductName, Opening_Stock,
                                            FreshStock, Stock_inhand,
                                            SoldStock, S_Return_NonSaleable,
                                            S_Return_Saleable, ClosingBal,
                                            GrossAmount, Discount, NetAmount,
                                            Size, Price, LMD,
                                            AndroidCreatedDate, MONTH, YEAR);
                                    db.close();

                                    db_stock_id_array = db_stock_id_array + ","
                                            + db_Id;

                                }

                            } else if (soap_result1.getProperty("status")
                                    .toString().equalsIgnoreCase("E")) {
                                Log.e("pm", "pm7");
                                // Flag = "1";

                                writeStringAsFile(db_stock_id_array);

                                soap_update_stock_row = service
                                        .UpdateTableData(db_stock_id_array,
                                                "S", EmpId);

                                SimpleDateFormat dateFormat = new SimpleDateFormat(
                                        "MM/dd/yyyy HH:mm:ss");
                                // get current date time with Date()
                                Calendar cal = Calendar.getInstance();
                                // dateFormat.format(cal.getTime())
                                db.open();
                                db.updateDateSync(
                                        dateFormat.format(cal.getTime()),
                                        "stock");
                                db.close();

                            } else if (soap_result1.getProperty("status")
                                    .toString().equalsIgnoreCase("N")) {

                                // Flag = "3";
                                Log.e("", "string ids== " + db_stock_id_array);
                                soap_update_stock_row = service
                                        .UpdateTableData(db_stock_id_array,
                                                "S", EmpId);
                                Log.e("", "soap_update_stock_row= "
                                        + soap_update_stock_row.toString());

                                syncstockdata = 1;
                            } else if (soap_result1.getProperty("status")
                                    .toString().equalsIgnoreCase("SE")) {

                                soap_update_stock_row = service
                                        .UpdateTableData(db_stock_id_array,
                                                "S", EmpId);
                                Log.e("", "soap_update_stock_row= "
                                        + soap_update_stock_row.toString());

                                // Flag="2";
                                Log.e("", "string ids== " + db_stock_id_array);
                                syncstockdata = 0;
                                final Calendar calendar = Calendar
                                        .getInstance();
                                SimpleDateFormat formatter = new SimpleDateFormat(
                                        "MM/dd/yyyy HH:mm:ss");
                                String Createddate = formatter.format(calendar
                                        .getTime());
                                Log.v("", "se error");
                                int n = Thread.currentThread().getStackTrace()[2]
                                        .getLineNumber();
                                db.open();
                                db.insertSyncLog("FirstTimeSync_SE",
                                        String.valueOf(n), "DataDownload()",
                                        Createddate, Createddate,
                                        sp.getString("username", ""),
                                        "DataDownload()", "Fail");
                                db.close();
                            }
                        }

                    } else {
                        Log.v("", "Soap result is null");

                        syncstockdata = 0;
                        final Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter.format(calendar
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog("Soup is null - DataDownload()",
                                String.valueOf(n), "DataDownload()",
                                Createddate, Createddate,
                                sp.getString("username", ""), "Data Download",
                                "Fail");

                    }

					/*soap_result = service.SyncStockData(
							sp.getString("username", ""), lastdatesync);
					// soap_result =
					// service.SyncStockData(sp.getString("username",
					// ""),"05/01/2014 12:38:40");

					// Log.e("pm",
					// "pm1soap_result====+++---++__===____==="+soap_result.getPropertyCount());

					if (soap_result != null) {

						for (int i = 0; i < soap_result.getPropertyCount(); i++) {

							soap_result1 = (SoapObject) soap_result
									.getProperty(i);

							Log.e("pm",
									"status="
											+ soap_result1
											.getProperty("status")
											.toString());

							if (soap_result1.getProperty("status").toString()
									.equalsIgnoreCase("C")) {

								// try {
								// Log.v("", "soapresul--------" +
								// soap_result1.toString());

								String db_stock_id = soap_result1.getProperty(
										"Id").toString();

								String db_Id = soap_result1.getProperty(
										"ProductId").toString();

								Log.v("", "db_Id=" + db_Id);

								String CatCodeId = soap_result1.getProperty(
										"CatCodeId").toString();

								if (CatCodeId == null) {
									CatCodeId = "";

								}
								Log.v("", "CatCodeId=" + CatCodeId);

								String ProductId = soap_result1.getProperty(
										"ProductId").toString();
								Log.v("", "ProductId=" + ProductId);

								if (ProductId == null) {
									ProductId = "";

								}

								String EANCode = soap_result1.getProperty(
										"EANCode").toString();

								if (EANCode == null) {
									EANCode = "";

								}
								EmpId = soap_result1.getProperty("EmpId")
										.toString();

								if (EmpId == null) {
									EmpId = "";

								}
								String ProductCategory = soap_result1
										.getProperty("ProductCategory")
										.toString();

								if (ProductCategory == null) {
									ProductCategory = "";

								}
								String ProductType = soap_result1.getProperty(
										"ProductType").toString();

								if (ProductType == null) {
									ProductType = "";

								}
								String ProductName = soap_result1.getProperty(
										"ProductName").toString();
								if (ProductName == null) {
									ProductName = "";

								}

								String Opening_Stock = soap_result1
										.getProperty("Opening_Stock")
										.toString();
								if (Opening_Stock == null) {
									Opening_Stock = "";

								}

								String FreshStock = soap_result1.getProperty(
										"FreshStock").toString();
								if (FreshStock == null) {
									FreshStock = "";

								}

								String Stock_inhand = soap_result1.getProperty(
										"Stock_inhand").toString();

								if (Stock_inhand == null) {
									Stock_inhand = "";

								}
								String SoldStock = soap_result1.getProperty(
										"SoldStock").toString();

								if (SoldStock == null) {
									SoldStock = "";

								}
								String S_Return_Saleable = soap_result1
										.getProperty("S_Return_Saleable")
										.toString();

								if (S_Return_Saleable == null) {
									S_Return_Saleable = "";

								}
								String S_Return_NonSaleable = soap_result1
										.getProperty("S_Return_NonSaleable")
										.toString();

								if (S_Return_NonSaleable == null) {
									S_Return_NonSaleable = "";

								}
								String ClosingBal = soap_result1.getProperty(
										"ClosingBal").toString();

								if (ClosingBal == null) {
									ClosingBal = "";

								}
								String GrossAmount = soap_result1.getProperty(
										"GrossAmount").toString();

								if (GrossAmount == null) {
									GrossAmount = "";

								}
								String Discount = soap_result1.getProperty(
										"Discount").toString();

								if (Discount == null) {
									Discount = "";

								}
								String NetAmount = soap_result1.getProperty(
										"NetAmount").toString();

								if (NetAmount == null) {
									NetAmount = "";

								}
								String Size = soap_result1.getProperty("Size")
										.toString();

								if (Size == null) {
									Size = "";

								}
								String Price = soap_result1
										.getProperty("Price").toString();

								if (Price == null) {
									Price = "";

								}
								String LMD = soap_result1.getProperty("LMD")
										.toString();

								if (LMD == null) {
									LMD = "";

								} else {

									try {
										String inputPattern = "MM/dd/yyyy hh:mm:ss a";
										String outputPattern = "yyyy-MM-dd HH:mm:ss";

										DateFormat inputFormat = new SimpleDateFormat(
												inputPattern);
										SimpleDateFormat outputFormat = new SimpleDateFormat(
												outputPattern);

										Date date = inputFormat.parse(LMD);
										LMD = outputFormat.format(date);

									} catch (Exception e) {
										e.printStackTrace();
									}
								}

								String AndroidCreatedDate = soap_result1
										.getProperty("AndroidCreatedDate")
										.toString();

								String MONTH = "", YEAR = "";
								if (AndroidCreatedDate == null) {
									AndroidCreatedDate = "";

								} else {

									try {
										String inputPattern = "MM/dd/yyyy hh:mm:ss a";
										String outputPattern = "yyyy-MM-dd HH:mm:ss";

										SimpleDateFormat inputFormat = new SimpleDateFormat(
												inputPattern);
										SimpleDateFormat outputFormat = new SimpleDateFormat(
												outputPattern);

										Date date = inputFormat
												.parse(AndroidCreatedDate);
										AndroidCreatedDate = outputFormat
												.format(date);

										String[] addd = AndroidCreatedDate
												.split(" ");
										String addd1 = addd[0];
										String[] addd2 = addd1.split("-");

										String month = addd2[1];
										YEAR = addd2[0];
										//
										SimpleDateFormat monthParse = new SimpleDateFormat(
												"MM");
										SimpleDateFormat monthDisplay = new SimpleDateFormat(
												"MMMM");
										MONTH = monthDisplay.format(monthParse
												.parse(month));
										//

									} catch (Exception e) {
										e.printStackTrace();
									}
								}

								// String lmd =
								// soap_result1.getProperty("LMD").toString();

								if (db_Id.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for db_Id");
									db_Id = " ";
								}
								if (CatCodeId.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for CatCodeId");
									CatCodeId = " ";
								}

								if (ProductId.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for ProductId");
									ProductId = " ";
								}
								if (EANCode.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for EANCode");
									EANCode = " ";
								}

								if (EmpId.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for EmpId");
									EmpId = " ";
								}
								if (ProductCategory
										.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for ProductCategory");
									ProductCategory = " ";
								}
								if (ProductType.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for ProductType");
									ProductType = " ";
								}

								if (ProductName.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for ProductName");
									ProductName = " ";
								}
								if (Opening_Stock.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for Opening_Stock");
									Opening_Stock = " ";
								}

								if (FreshStock.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for FreshStock");
									FreshStock = " ";
								}
								if (Stock_inhand.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for Stock_inhand");
									Stock_inhand = " ";
								}
								if (SoldStock.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for SoldStock");
									SoldStock = " ";
								}
								if (S_Return_Saleable
										.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for S_Return_Saleable");
									S_Return_Saleable = " ";
								}
								if (S_Return_NonSaleable
										.equalsIgnoreCase("anyType{}")) {
									Log.e("",
											"anytype for S_Return_NonSaleable");
									S_Return_NonSaleable = " ";
								}
								if (ClosingBal.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for ClosingBal");
									ClosingBal = " ";
								}
								if (GrossAmount.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for GrossAmount");
									GrossAmount = " ";
								}
								if (Discount.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for Discount");
									Discount = " ";
								}
								if (NetAmount.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for NetAmount");
									NetAmount = " ";
								}
								if (Size.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for Size");
									Size = " ";
								}
								if (Price.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for sku_l");
									Price = " ";
								}
								if (LMD.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for LMD");
									LMD = " ";
								}
								if (AndroidCreatedDate
										.equalsIgnoreCase("anyType{}")) {
									Log.e("", "anytype for AndroidCreatedDate");
									AndroidCreatedDate = " ";
								}

								// if (flag.equalsIgnoreCase("e")) {

								Log.e("pm", "pm5--");
								db.open();
								// Cursor c = db.getuniquedata_stock(CatCodeId,
								// EANCode, db_Id);

								Cursor c1 = db.CheckDataExist("stock", db_Id,
										ProductCategory, ProductType,
										ProductName);

								// db_stock_id_array = db_stock_id;

								int count = c1.getCount();
								Log.v("", "" + count);
								db.close();
								if (count > 0) {

									db.open();
									db.UpdateStockSync(ProductCategory,
											ProductType, ProductName, EmpId,
											Stock_inhand, ClosingBal,
											FreshStock, GrossAmount, SoldStock,
											Price, Size, db_Id, LMD, Discount,
											NetAmount, Opening_Stock,
											S_Return_Saleable,
											S_Return_NonSaleable);
									db.close();

									// soap_update_stock_row =
									// service.UpdateTableData(
									// db_stock_id,
									// "S",
									// EmpId);
									// Log.e("",
									// "soap_update_stock_row= "+soap_update_stock_row.toString());
									db_stock_id_array = db_stock_id_array + ","
											+ db_Id;

								} else {

									Log.e("pm", "pm5");
									db.open();
									db.insertProductMasterFirsttime(
											db_stock_id, db_Id, ProductId,
											CatCodeId, EANCode, EmpId,
											ProductCategory, ProductType,
											ProductName, Opening_Stock,
											FreshStock, Stock_inhand,
											SoldStock, S_Return_NonSaleable,
											S_Return_Saleable, ClosingBal,
											GrossAmount, Discount, NetAmount,
											Size, Price, LMD,
											AndroidCreatedDate, MONTH, YEAR);
									db.close();

									// Log.e("",
									// "db_stock_id="+db_stock_id+" empid="+EmpId);
									db_stock_id_array = db_stock_id_array + ","
											+ db_Id;
									// Log.e("",
									// "string ids1== "+db_stock_id_array);

									// soap_update_stock_row =
									// service.UpdateTableData(
									// db_stock_id,
									// "S",
									// EmpId);
									// Log.e("",
									// "soap_update_stock_row= "+soap_update_stock_row.toString());

								}

								// }
								*//*
								 * } catch (Exception e) { // TODO: handle
								 * exception e.printStackTrace(); String Error =
								 * e.toString(); Log.v("","se2 error"); final
								 * Calendar calendar = Calendar .getInstance();
								 * SimpleDateFormat formatter = new
								 * SimpleDateFormat( "MM/dd/yyyy HH:mm:ss");
								 * String Createddate =
								 * formatter.format(calendar .getTime()); Flag =
								 * "4"; int n =
								 * Thread.currentThread().getStackTrace
								 * ()[2].getLineNumber(); db.open();
								 * db.insertSyncLog(Error,String.valueOf(n),
								 * "SyncStockData()"
								 * ,Createddate,Createddate,sp.getString
								 * ("username", ""),"SyncStockData()","Fail");
								 * db.close(); }
								 *//*

							} else if (soap_result1.getProperty("status")
									.toString().equalsIgnoreCase("E")) {
								Log.e("pm", "pm7");
								// Flag = "1";

								writeStringAsFile(db_stock_id_array);

								soap_update_stock_row = service
										.UpdateTableData(db_stock_id_array,
												"S", EmpId);
								*//*
								 * Log.e("",
								 * "soap_update_stock_row= "+soap_update_stock_row
								 * .toString());
								 *
								 * Log.e("", "string ids== "+db_stock_id_array);
								 *//*
								SimpleDateFormat dateFormat = new SimpleDateFormat(
										"MM/dd/yyyy HH:mm:ss");
								// get current date time with Date()
								Calendar cal = Calendar.getInstance();
								// dateFormat.format(cal.getTime())
								db.open();
								db.updateDateSync(
										dateFormat.format(cal.getTime()),
										"stock");
								db.close();

							} else if (soap_result1.getProperty("status")
									.toString().equalsIgnoreCase("N")) {

								// Flag = "3";
								Log.e("", "string ids== " + db_stock_id_array);
								soap_update_stock_row = service
										.UpdateTableData(db_stock_id_array,
												"S", EmpId);
								Log.e("", "soap_update_stock_row= "
										+ soap_update_stock_row.toString());

								syncstockdata = 1;
							} else if (soap_result1.getProperty("status")
									.toString().equalsIgnoreCase("SE")) {

								soap_update_stock_row = service
										.UpdateTableData(db_stock_id_array,
												"S", EmpId);
								Log.e("", "soap_update_stock_row= "
										+ soap_update_stock_row.toString());

								// Flag="2";
								Log.e("", "string ids== " + db_stock_id_array);
								syncstockdata = 0;
								final Calendar calendar = Calendar
										.getInstance();
								SimpleDateFormat formatter = new SimpleDateFormat(
										"MM/dd/yyyy HH:mm:ss");
								String Createddate = formatter.format(calendar
										.getTime());
								Log.v("", "se error");
								int n = Thread.currentThread().getStackTrace()[2]
										.getLineNumber();
								db.open();
								db.insertSyncLog("FirstTimeSync_SE",
										String.valueOf(n), "SyncStockData()",
										Createddate, Createddate,
										sp.getString("username", ""),
										"SyncStockData()", "Fail");
								db.close();
							}
						}

					} else {
						Log.v("", "Soap result is null");
						// Toast.makeText(context,
						// "Data Not Available or Check Connectivity",
						// Toast.LENGTH_SHORT).show();

						syncstockdata = 0;
						final Calendar calendar = Calendar.getInstance();
						SimpleDateFormat formatter = new SimpleDateFormat(
								"MM/dd/yyyy HH:mm:ss");
						String Createddate = formatter.format(calendar
								.getTime());

						int n = Thread.currentThread().getStackTrace()[2]
								.getLineNumber();
						db.insertSyncLog("Soup is null - SyncStockData()",
								String.valueOf(n), "SyncStockData()",
								Createddate, Createddate,
								sp.getString("username", ""), "Data Download",
								"Fail");

					}*/

                    // -----------------Tester-------------//

                    db.open();
                    String lastdatesync1 = db.getLastSyncDate("tester");
                    db.close();

                    soap_result_tester = service.SyncGetTesterData(
                            sp.getString("username", ""), lastdatesync1);// 09.10.2015

                    Log.e("pm", "pm1");
                    if (soap_result_tester != null) {

                        for (int i = 0; i < soap_result_tester
                                .getPropertyCount(); i++) {

                            soap_result_tester1 = (SoapObject) soap_result_tester
                                    .getProperty(i);

                            Log.e("pm", "status="
                                    + soap_result_tester1.getProperty("status")
                                    .toString());

                            if (soap_result_tester1.getProperty("status")
                                    .toString().equalsIgnoreCase("C")) {

                                // try {
                                // Log.v("", "soapresul--------" +
                                // soap_result1.toString());

                                String db_tester_id = soap_result_tester1
                                        .getProperty("Id").toString();

                                String db_Id = soap_result_tester1.getProperty(
                                        "ProductId").toString();

                                Log.v("", "db_Id=" + db_Id);

                                String CatCodeId = soap_result_tester1
                                        .getProperty("CatCodeId").toString();

                                if (CatCodeId == null
                                        || CatCodeId.equalsIgnoreCase("null")) {
                                    CatCodeId = "";

                                }
                                Log.v("", "CatCodeId=" + CatCodeId);

                                String EANCode = soap_result_tester1
                                        .getProperty("EANCode").toString();

                                if (EANCode == null
                                        || EANCode.equalsIgnoreCase("null")) {
                                    EANCode = "";

                                }
                                String EmpId = soap_result_tester1.getProperty(
                                        "EmpId").toString();

                                if (EmpId == null
                                        || EmpId.equalsIgnoreCase("null")) {
                                    EmpId = "";

                                }
                                String ProductCategory = soap_result_tester1
                                        .getProperty("ProductCategory")
                                        .toString();

                                if (ProductCategory == null) {
                                    ProductCategory = "";

                                }
                                String ProductType = soap_result_tester1
                                        .getProperty("ProductType").toString();

                                if (ProductType == null) {
                                    ProductType = "";

                                }

                                String ProductName = soap_result_tester1
                                        .getProperty("ProductName").toString();

                                if (ProductName == null) {
                                    ProductName = "";

                                }

                                String Size = soap_result_tester1.getProperty(
                                        "Size").toString();

                                if (Size == null
                                        || Size.equalsIgnoreCase("null")) {
                                    Size = "";

                                }
                                String ShadeNo = soap_result_tester1
                                        .getProperty("ShadeNo").toString();

                                if (ShadeNo == null
                                        || ShadeNo.equalsIgnoreCase("null")
                                        || ShadeNo
                                        .equalsIgnoreCase("anyType{}")) {
                                    ShadeNo = "";

                                }

                                String LMD = soap_result_tester1.getProperty(
                                        "LMD").toString();

                                if (LMD == null) {
                                    LMD = "";

                                }

                                String Curr_Status = soap_result_tester1
                                        .getProperty("Curr_Status").toString();

                                if (Curr_Status == null) {
                                    Curr_Status = "";

                                }

                                String RequestDate = soap_result_tester1
                                        .getProperty("RequestDate").toString();

                                if (RequestDate == null) {
                                    RequestDate = "";

                                }

                                String DelieveredDate = soap_result_tester1
                                        .getProperty("DelieveredDate")
                                        .toString();

                                if (DelieveredDate == null
                                        || DelieveredDate
                                        .equalsIgnoreCase("null")) {
                                    DelieveredDate = "";

                                }

                                if (db_Id.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for db_Id");
                                    db_Id = " ";
                                }
                                if (CatCodeId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for CatCodeId");
                                    CatCodeId = " ";
                                }

                                if (EANCode.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for EANCode");
                                    EANCode = " ";
                                }

                                if (EmpId.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for EmpId");
                                    EmpId = " ";
                                }
                                if (ProductCategory
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductCategory");
                                    ProductCategory = " ";
                                }
                                if (ProductType.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductType");
                                    ProductType = " ";
                                }

                                if (ProductName.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for ProductName");
                                    ProductName = " ";
                                }

                                if (Size.equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for Size");
                                    Size = " ";
                                }

                                if (DelieveredDate
                                        .equalsIgnoreCase("anyType{}")) {
                                    Log.e("", "anytype for DelieveredDate");
                                    DelieveredDate = " ";
                                }

                                // if (flag.equalsIgnoreCase("e")) {

                                Log.e("pm", "pm5--");
                                db.open();
                                // Cursor c = db.getuniquedata_stock(CatCodeId,
                                // EANCode, db_Id);
                                // db_tester_id_array = db_tester_id;

                                Cursor c1 = db.CheckDataExist("tester", db_Id,
                                        ProductCategory, ProductType,
                                        ProductName);

                                int count = c1.getCount();
                                Log.v("", "" + count);
                                db.close();
                                if (count > 0) {

                                    db.open();
                                    db.UpdateTesterSync(db_Id, ProductCategory,
                                            ProductType, ProductName, EmpId,
                                            Size, RequestDate, DelieveredDate,
                                            Curr_Status, ShadeNo);
                                    db.close();

                                    db_tester_id_array = db_tester_id_array
                                            + "," + db_tester_id;// 10.10.2015

                                } else {

                                    Log.e("pm", "pm5");
                                    db.open();
                                    db.insertTesterDownloadedData(db_tester_id,
                                            db_Id, CatCodeId, EANCode, EmpId,
                                            ProductCategory, ProductType,
                                            ProductName, RequestDate,
                                            DelieveredDate, ShadeNo,
                                            Curr_Status, Size);
                                    db.close();

                                    Log.e("", "db_tester_id=" + db_tester_id
                                            + " empid=" + EmpId);

                                    db_tester_id_array = db_tester_id_array
                                            + "," + db_tester_id;// 10.10.2015

                                }

                                // }
								/*
								 * } catch (Exception e) { // TODO: handle
								 * exception e.printStackTrace(); String Error =
								 * e.toString(); Log.v("","se2 error"); final
								 * Calendar calendar = Calendar .getInstance();
								 * SimpleDateFormat formatter = new
								 * SimpleDateFormat( "MM/dd/yyyy HH:mm:ss");
								 * String Createddate =
								 * formatter.format(calendar .getTime()); Flag =
								 * "4"; int n =
								 * Thread.currentThread().getStackTrace
								 * ()[2].getLineNumber(); db.open();
								 * db.insertSyncLog(Error,String.valueOf(n),
								 * "SyncGetTesterData()"
								 * ,Createddate,Createddate,
								 * sp.getString("username",
								 * ""),"SyncGetTesterData()","Fail");
								 * db.close(); }
								 */

                            } else if (soap_result_tester1
                                    .getProperty("status").toString()
                                    .equalsIgnoreCase("E")) {
                                Log.e("pm", "pm7");
                                // Flag = "1";

                                Log.e("", "db_tester_id_array="
                                        + db_tester_id_array);

                                soap_update_tester_row = service
                                        .UpdateTableData(db_tester_id_array,
                                                "T", EmpId);
                                Log.e("", "soap_update_tester_row= "
                                        + soap_update_tester_row.toString());

                                SimpleDateFormat dateFormat = new SimpleDateFormat(
                                        "MM/dd/yyyy HH:mm:ss");
                                // get current date time with Date()
                                Calendar cal = Calendar.getInstance();
                                // dateFormat.format(cal.getTime())
                                db.open();
                                db.updateDateSync(
                                        dateFormat.format(cal.getTime()),
                                        "tester");
                                db.close();

                            } else if (soap_result_tester1
                                    .getProperty("status").toString()
                                    .equalsIgnoreCase("N")) {
                                soap_update_tester_row = service
                                        .UpdateTableData(db_tester_id_array,
                                                "T", EmpId);
                                Log.e("", "soap_update_tester_row= "
                                        + soap_update_tester_row.toString());
                                // Flag = "3";
                                synctesterdata = 1;
                            } else if (soap_result_tester1
                                    .getProperty("status").toString()
                                    .equalsIgnoreCase("SE")) {

                                soap_update_tester_row = service
                                        .UpdateTableData(db_tester_id_array,
                                                "T", EmpId);
                                Log.e("", "soap_update_tester_row= "
                                        + soap_update_tester_row.toString());
                                // Flag="2";
                                synctesterdata = 0;
                                final Calendar calendar = Calendar
                                        .getInstance();
                                SimpleDateFormat formatter = new SimpleDateFormat(
                                        "MM/dd/yyyy HH:mm:ss");
                                String Createddate = formatter.format(calendar
                                        .getTime());
                                Log.v("", "se error");
                                int n = Thread.currentThread().getStackTrace()[2]
                                        .getLineNumber();
                                db.open();
                                db.insertSyncLog("SyncGetTesterData_SE",
                                        String.valueOf(n),
                                        "SyncGetTesterData()", Createddate,
                                        Createddate,
                                        sp.getString("username", ""),
                                        "Data Download", "Fail");
                                db.close();
                            }
                        }

                    } else {
                        Log.v("", "Soap result is null");

                        synctesterdata = 0;
                        final Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter.format(calendar
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.open();
                        db.insertSyncLog("Soup is null - SyncGetTesterData()",
                                String.valueOf(n), "SyncGetTesterData()",
                                Createddate, Createddate,
                                sp.getString("username", ""), "Data Download",
                                "Fail");
                        db.close();

                    }

                }
                // -------------------------------//
                // ------------------------------------ boc - day wise
                // data-----------------------//

                db.open();
                String lastdatesync_boc_day = db
                        .getLastSyncDate("boc_wise_stock");
                db.close();

                soap_result_boc_day = service.SyncStockNetvalue(
                        sp.getString("username", ""), lastdatesync_boc_day);// 09.10.2015

                Log.e("pm", "pm1");
                if (soap_result_boc_day != null) {

                    for (int i = 0; i < soap_result_boc_day.getPropertyCount(); i++) {

                        soap_result_boc_day1 = (SoapObject) soap_result_boc_day
                                .getProperty(i);

                        Log.e("pm", "status="
                                + soap_result_boc_day1.getProperty("status")
                                .toString());

                        if (soap_result_boc_day1.getProperty("status")
                                .toString().equalsIgnoreCase("C")) {

                            // try {

                            String db_stock_id = soap_result_boc_day1
                                    .getProperty("Id").toString();

                            String db_Id = soap_result_boc_day1.getProperty(
                                    "ProductId").toString();

                            Log.v("", "db_Id=" + db_Id);

                            String CatCodeId = soap_result_boc_day1
                                    .getProperty("CatCodeId").toString();

                            if (CatCodeId == null) {
                                CatCodeId = "";

                            }
                            Log.v("", "CatCodeId=" + CatCodeId);

                            String ProductId = soap_result_boc_day1
                                    .getProperty("ProductId").toString();
                            Log.v("", "ProductId=" + ProductId);

                            if (ProductId == null) {
                                ProductId = "";

                            }

                            String EANCode = soap_result_boc_day1.getProperty(
                                    "EANCode").toString();

                            if (EANCode == null) {
                                EANCode = "";

                            }
                            String EmpId = soap_result_boc_day1.getProperty(
                                    "EmpId").toString();

                            if (EmpId == null) {
                                EmpId = "";

                            }
                            String ProductCategory = soap_result_boc_day1
                                    .getProperty("ProductCategory").toString();

                            if (ProductCategory == null) {
                                ProductCategory = "";

                            }
                            String ProductType = soap_result_boc_day1
                                    .getProperty("ProductType").toString();

                            if (ProductType == null) {
                                ProductType = "";

                            }
                            String ProductName = soap_result_boc_day1
                                    .getProperty("ProductName").toString();
                            if (ProductName == null) {
                                ProductName = "";

                            }

                            String ShadeNo = soap_result_boc_day1.getProperty(
                                    "ShadeNo").toString();
                            if (ShadeNo == null) {
                                ShadeNo = "";

                            }

                            String Opening_Stock = soap_result_boc_day1
                                    .getProperty("Opening_Stock").toString();
                            if (Opening_Stock == null) {
                                Opening_Stock = "";

                            }

                            String FreshStock = soap_result_boc_day1
                                    .getProperty("FreshStock").toString();
                            if (FreshStock == null) {
                                FreshStock = "";

                            }

                            String Stock_inhand = soap_result_boc_day1
                                    .getProperty("Stock_inhand").toString();

                            if (Stock_inhand == null) {
                                Stock_inhand = "";

                            }
                            String SoldStock = soap_result_boc_day1
                                    .getProperty("SoldStock").toString();

                            if (SoldStock == null) {
                                SoldStock = "";

                            }
                            String S_Return_Saleable = soap_result_boc_day1
                                    .getProperty("S_Return_Saleable")
                                    .toString();

                            if (S_Return_Saleable == null) {
                                S_Return_Saleable = "";

                            }
                            String S_Return_NonSaleable = soap_result_boc_day1
                                    .getProperty("S_Return_NonSaleable")
                                    .toString();

                            if (S_Return_NonSaleable == null) {
                                S_Return_NonSaleable = "";

                            }
                            String ClosingBal = soap_result_boc_day1
                                    .getProperty("ClosingBal").toString();

                            if (ClosingBal == null) {
                                ClosingBal = "";

                            }
                            String GrossAmount = soap_result_boc_day1
                                    .getProperty("GrossAmount").toString();

                            if (GrossAmount == null) {
                                GrossAmount = "";

                            }
                            String Discount = soap_result_boc_day1.getProperty(
                                    "Discount").toString();

                            if (Discount == null) {
                                Discount = "";

                            }
                            String NetAmount = soap_result_boc_day1
                                    .getProperty("NetAmount").toString();

                            if (NetAmount == null) {
                                NetAmount = "";

                            }
                            String Size = soap_result_boc_day1.getProperty(
                                    "Size").toString();

                            if (Size == null) {
                                Size = "";

                            }
                            String Price = soap_result_boc_day1.getProperty(
                                    "Price").toString();

                            if (Price == null) {
                                Price = "";

                            }
                            String LMD = soap_result_boc_day1
                                    .getProperty("LMD").toString();

							/*
							 * if(LMD == null){ LMD="";
							 *
							 * }
							 */
                            // ---
                            if (LMD == null) {
                                LMD = "";

                            } else {

                                try {
                                    String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                    String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                    DateFormat inputFormat = new SimpleDateFormat(
                                            inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(
                                            outputPattern);

                                    Date date = inputFormat.parse(LMD);
                                    LMD = outputFormat.format(date);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            // ---

                            String AndroidCreatedDate = soap_result_boc_day1
                                    .getProperty("AndroidCreatedDate")
                                    .toString();

                            if (AndroidCreatedDate == null) {
                                AndroidCreatedDate = "";

                            } else {

                                try {
                                    String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                    String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                    DateFormat inputFormat = new SimpleDateFormat(
                                            inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(
                                            outputPattern);

                                    Date date = inputFormat
                                            .parse(AndroidCreatedDate);
                                    AndroidCreatedDate = outputFormat
                                            .format(date);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            String Date = soap_result_boc_day1.getProperty(
                                    "Date").toString();

                            if (Date == null) {
                                Date = "";

                            }
                            String BOC = soap_result_boc_day1
                                    .getProperty("BOC").toString();

                            if (BOC == null) {
                                BOC = "";

                            }

                            // String lmd =
                            // soap_result1.getProperty("LMD").toString();

                            if (db_Id.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for db_Id");
                                db_Id = " ";
                            }
                            if (CatCodeId.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for CatCodeId");
                                CatCodeId = " ";
                            }

                            if (ProductId.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductId");
                                ProductId = " ";
                            }
                            if (EANCode.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for EANCode");
                                EANCode = " ";
                            }

                            if (EmpId.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for EmpId");
                                EmpId = " ";
                            }
                            if (ProductCategory.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductCategory");
                                ProductCategory = " ";
                            }
                            if (ProductType.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductType");
                                ProductType = " ";
                            }

                            if (ProductName.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductName");
                                ProductName = " ";
                            }
                            if (Opening_Stock.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Opening_Stock");
                                Opening_Stock = " ";
                            }

                            if (FreshStock.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for FreshStock");
                                FreshStock = " ";
                            }
                            if (Stock_inhand.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Stock_inhand");
                                Stock_inhand = " ";
                            }
                            if (SoldStock.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for SoldStock");
                                SoldStock = " ";
                            }
                            if (S_Return_Saleable.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for S_Return_Saleable");
                                S_Return_Saleable = " ";
                            }
                            if (S_Return_NonSaleable
                                    .equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for S_Return_NonSaleable");
                                S_Return_NonSaleable = " ";
                            }
                            if (ClosingBal.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ClosingBal");
                                ClosingBal = " ";
                            }
                            if (GrossAmount.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for GrossAmount");
                                GrossAmount = " ";
                            }
                            if (Discount.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Discount");
                                Discount = " ";
                            }
                            if (NetAmount.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for NetAmount");
                                NetAmount = " ";
                            }
                            if (Size.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Size");
                                Size = " ";
                            }
                            if (Price.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for sku_l");
                                Price = " ";
                            }
                            if (LMD.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for LMD");
                                LMD = " ";
                            }
                            if (AndroidCreatedDate
                                    .equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for AndroidCreatedDate");
                                AndroidCreatedDate = " ";
                            }

                            if (BOC.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for BOC");
                                BOC = " ";
                            }

                            // if (flag.equalsIgnoreCase("e")) {

                            Log.e("pm", "pm5--");
                            db.open();
                            // Cursor c = db.getuniquedata_stock(CatCodeId,
                            // EANCode, db_Id);

                            // Cursor c1 =
                            // db.CheckDataExist("boc_wise_stock",db_Id,ProductCategory,ProductType,ProductName);
                            // db_bocmonthstock_id_array = db_stock_id;
                            Cursor c1 = db.CheckDataExistInBOCDayWise(
                                    "boc_wise_stock", db_Id, Date);

                            if (c1 != null) {
                                c1.moveToFirst();
                                int count = c1.getCount();
                                Log.v("", "" + count);
                                db.close();
                                if (count > 0) {

                                    db.open();
                                    db.UpdateBOCDAYStockSync(ProductCategory,
                                            ProductType, ProductName, EmpId,
                                            Stock_inhand, ClosingBal,
                                            FreshStock, GrossAmount, SoldStock,
                                            Price, Size, db_Id, LMD, Discount,
                                            NetAmount, Opening_Stock,
                                            S_Return_Saleable,
                                            S_Return_NonSaleable, Date, BOC);
                                    db.close();

                                    db_bocmonthstock_id_array = db_bocmonthstock_id_array
                                            + "," + db_stock_id;// 10.10.2015

                                } else {

                                    Log.e("pm", "pm5");
                                    db.open();
                                    db.insertBocDateWise(db_stock_id, db_Id,
                                            ProductId, CatCodeId, EANCode,
                                            EmpId, ProductCategory,
                                            ProductType, ProductName,
                                            Opening_Stock, FreshStock,
                                            Stock_inhand, SoldStock,
                                            S_Return_NonSaleable,
                                            S_Return_Saleable, ClosingBal,
                                            GrossAmount, Discount, NetAmount,
                                            Size, Price, LMD,
                                            AndroidCreatedDate, Date, BOC);
                                    db.close();

                                    Log.e("", "db_stock_id=" + db_stock_id
                                            + " empid=" + EmpId);
                                    db_bocmonthstock_id_array = db_bocmonthstock_id_array
                                            + "," + db_stock_id;// 10.10.2015

                                }

                            }
                            // }
							/*
							 * } catch (Exception e) { // TODO: handle exception
							 * e.printStackTrace(); String Error = e.toString();
							 * Log.v("","se2 error"); final Calendar calendar =
							 * Calendar .getInstance(); SimpleDateFormat
							 * formatter = new SimpleDateFormat(
							 * "MM/dd/yyyy HH:mm:ss"); String Createddate =
							 * formatter.format(calendar .getTime()); Flag =
							 * "4"; int n =
							 * Thread.currentThread().getStackTrace(
							 * )[2].getLineNumber(); db.open();
							 * db.insertSyncLog(Error,String.valueOf(n),
							 * "SyncStockNetvalue()"
							 * ,Createddate,Createddate,sp.getString("username",
							 * ""),"SyncStockNetvalue()","Fail"); db.close(); }
							 */

                        } else if (soap_result_boc_day1.getProperty("status")
                                .toString().equalsIgnoreCase("E")) {
                            Log.e("pm", "pm7");
                            // Flag = "1";

                            soap_update_boc_day_row = service.UpdateTableData(
                                    db_bocmonthstock_id_array, "N", EmpId);
                            Log.e("", "soap_update_boc_day_row= "
                                    + soap_update_boc_day_row.toString());

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            // get current date time with Date()
                            Calendar cal = Calendar.getInstance();
                            // dateFormat.format(cal.getTime())
                            db.open();
                            db.updateDateSync(dateFormat.format(cal.getTime()),
                                    "boc_wise_stock");
                            db.close();

                        } else if (soap_result_boc_day1.getProperty("status")
                                .toString().equalsIgnoreCase("N")) {

                            soap_update_boc_day_row = service.UpdateTableData(
                                    db_bocmonthstock_id_array, "N", EmpId);
                            Log.e("", "soap_update_boc_day_row= "
                                    + soap_update_boc_day_row.toString());

                            // Flag = "3";
                            syncbocdaywise = 1;
                        } else if (soap_result_boc_day1.getProperty("status")
                                .toString().equalsIgnoreCase("SE")) {

                            soap_update_boc_day_row = service.UpdateTableData(
                                    db_bocmonthstock_id_array, "N", EmpId);
                            Log.e("", "soap_update_boc_day_row= "
                                    + soap_update_boc_day_row.toString());

                            // Flag="2";
                            syncbocdaywise = 0;
                            final Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat formatter = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            String Createddate = formatter.format(calendar
                                    .getTime());
                            Log.v("", "se error");
                            int n = Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber();
                            db.open();
                            db.insertSyncLog("SyncStockNetvalue_SE",
                                    String.valueOf(n),
                                    "SyncStockNetvalue_SE()", Createddate,
                                    Createddate, sp.getString("username", ""),
                                    "Data Download", "Fail");
                            db.close();
                        }
                    }

                } else {
                    Log.v("", "Soap result is null");

                    syncbocdaywise = 0;

                    final Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "MM/dd/yyyy HH:mm:ss");
                    String Createddate = formatter.format(calendar.getTime());

                    int n = Thread.currentThread().getStackTrace()[2]
                            .getLineNumber();
                    db.open();
                    db.insertSyncLog("Soap is null - SyncStockNetvalue()",
                            String.valueOf(n), "SyncStockNetvalue()",
                            Createddate, Createddate,
                            sp.getString("username", ""), "Data Download",
                            "Fail");
                    db.close();
                }
                // ----------------------------stock
                // monthwise------------------------------------//

                db.open();
                String lastdatesync_stock_mothwise = db
                        .getLastSyncDate("stock_monthwise");
                db.close();

                soap_result_monthwise = service.SyncStockCummData(
                        sp.getString("username", ""),
                        lastdatesync_stock_mothwise);// 09.10.2015

                Log.e("pm", "pm1");
                if (soap_result_monthwise != null) {

                    for (int i = 0; i < soap_result_monthwise
                            .getPropertyCount(); i++) {

                        soap_result_monthwise1 = (SoapObject) soap_result_monthwise
                                .getProperty(i);

                        Log.e("pm", "status="
                                + soap_result_monthwise1.getProperty("status")
                                .toString());

                        if (soap_result_monthwise1.getProperty("status")
                                .toString().equalsIgnoreCase("C")) {

                            // try {
                            // Log.v("", "soapresul--------" +
                            // soap_result1.toString());

                            String db_stock_id = soap_result_monthwise1
                                    .getProperty("Id").toString();

                            String db_Id = soap_result_monthwise1.getProperty(
                                    "ProductId").toString();

                            Log.v("", "db_Id=" + db_Id);

                            String CatCodeId = soap_result_monthwise1
                                    .getProperty("CatCodeId").toString();

                            if (CatCodeId == null) {
                                CatCodeId = "";

                            }
                            Log.v("", "CatCodeId=" + CatCodeId);

                            String ProductId = soap_result_monthwise1
                                    .getProperty("ProductId").toString();
                            Log.v("", "ProductId=" + ProductId);

                            if (ProductId == null) {
                                ProductId = "";

                            }

                            String EANCode = soap_result_monthwise1
                                    .getProperty("EANCode").toString();

                            if (EANCode == null) {
                                EANCode = "";

                            }
                            EmpId = soap_result_monthwise1.getProperty("EmpId")
                                    .toString();

                            if (EmpId == null) {
                                EmpId = "";

                            }
                            String ProductCategory = soap_result_monthwise1
                                    .getProperty("ProductCategory").toString();

                            if (ProductCategory == null) {
                                ProductCategory = "";

                            }
                            String ProductType = soap_result_monthwise1
                                    .getProperty("ProductType").toString();

                            if (ProductType == null) {
                                ProductType = "";

                            }
                            String ProductName = soap_result_monthwise1
                                    .getProperty("ProductName").toString();
                            if (ProductName == null) {
                                ProductName = "";

                            }

                            String ShadeNo = soap_result_monthwise1
                                    .getProperty("ShadeNo").toString();
                            if (ShadeNo == null) {
                                ShadeNo = "";

                            }

                            String Opening_Stock = soap_result_monthwise1
                                    .getProperty("Opening_Stock").toString();
                            if (Opening_Stock == null) {
                                Opening_Stock = "";

                            }

                            String FreshStock = soap_result_monthwise1
                                    .getProperty("FreshStock").toString();
                            if (FreshStock == null) {
                                FreshStock = "";

                            }

                            String Stock_inhand = soap_result_monthwise1
                                    .getProperty("Stock_inhand").toString();

                            if (Stock_inhand == null) {
                                Stock_inhand = "";

                            }
                            String SoldStock = soap_result_monthwise1
                                    .getProperty("SoldStock").toString();

                            if (SoldStock == null) {
                                SoldStock = "";

                            }
                            String S_Return_Saleable = soap_result_monthwise1
                                    .getProperty("S_Return_Saleable")
                                    .toString();

                            if (S_Return_Saleable == null) {
                                S_Return_Saleable = "";

                            }
                            String S_Return_NonSaleable = soap_result_monthwise1
                                    .getProperty("S_Return_NonSaleable")
                                    .toString();

                            if (S_Return_NonSaleable == null) {
                                S_Return_NonSaleable = "";

                            }
                            String ClosingBal = soap_result_monthwise1
                                    .getProperty("ClosingBal").toString();

                            if (ClosingBal == null) {
                                ClosingBal = "";

                            }
                            String GrossAmount = soap_result_monthwise1
                                    .getProperty("GrossAmount").toString();

                            if (GrossAmount == null) {
                                GrossAmount = "";

                            }
                            String Discount = soap_result_monthwise1
                                    .getProperty("Discount").toString();

                            if (Discount == null) {
                                Discount = "";

                            }
                            String NetAmount = soap_result_monthwise1
                                    .getProperty("NetAmount").toString();

                            if (NetAmount == null) {
                                NetAmount = "";

                            }
                            String Size = soap_result_monthwise1.getProperty(
                                    "Size").toString();

                            if (Size == null) {
                                Size = "";

                            }
                            String Price = soap_result_monthwise1.getProperty(
                                    "Price").toString();

                            if (Price == null) {
                                Price = "";

                            }
                            String LMD = soap_result_monthwise1.getProperty(
                                    "LMD").toString();

							/*
							 * if(LMD == null){ LMD="";
							 *
							 * }
							 */

                            if (LMD == null) {
                                LMD = "";

                            } else {

                                try {
                                    String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                    String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                    DateFormat inputFormat = new SimpleDateFormat(
                                            inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(
                                            outputPattern);

                                    Date date = inputFormat.parse(LMD);
                                    LMD = outputFormat.format(date);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            String month = soap_result_monthwise1.getProperty(
                                    "Month").toString();

                            if (month == null) {
                                month = "";

                            }

                            String AndroidCreatedDate = soap_result_monthwise1
                                    .getProperty("AndroidCreatedDate")
                                    .toString();

							/*
							 * if(AndroidCreatedDate == null){
							 * AndroidCreatedDate="";
							 *
							 * }
							 */

                            String MONTH = "", YEAR = "";
                            if (AndroidCreatedDate == null) {
                                AndroidCreatedDate = "";

                            } else {

                                try {
                                    String inputPattern = "MM/dd/yyyy hh:mm:ss a";
                                    String outputPattern = "yyyy-MM-dd HH:mm:ss";

                                    SimpleDateFormat inputFormat = new SimpleDateFormat(
                                            inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(
                                            outputPattern);

                                    Date date = inputFormat
                                            .parse(AndroidCreatedDate);
                                    AndroidCreatedDate = outputFormat
                                            .format(date);

                                    String[] addd = AndroidCreatedDate
                                            .split(" ");
                                    String addd1 = addd[0];
                                    String[] addd2 = addd1.split("-");

                                    // String month = addd2[1];
                                    YEAR = addd2[0];
                                    //
                                    // SimpleDateFormat monthParse = new
                                    // SimpleDateFormat("MM");
                                    // SimpleDateFormat monthDisplay = new
                                    // SimpleDateFormat("MMMM");
                                    // MONTH =
                                    // monthDisplay.format(monthParse.parse(month));
                                    //

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            // String lmd =
                            // soap_result1.getProperty("LMD").toString();

                            if (db_Id.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for db_Id");
                                db_Id = " ";
                            }
                            if (CatCodeId.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for CatCodeId");
                                CatCodeId = " ";
                            }

                            if (ProductId.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductId");
                                ProductId = " ";
                            }
                            if (EANCode.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for EANCode");
                                EANCode = " ";
                            }

                            if (EmpId.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for EmpId");
                                EmpId = " ";
                            }
                            if (ProductCategory.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductCategory");
                                ProductCategory = " ";
                            }
                            if (ProductType.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductType");
                                ProductType = " ";
                            }

                            if (ProductName.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ProductName");
                                ProductName = " ";
                            }
                            if (Opening_Stock.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Opening_Stock");
                                Opening_Stock = " ";
                            }

                            if (FreshStock.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for FreshStock");
                                FreshStock = " ";
                            }
                            if (Stock_inhand.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Stock_inhand");
                                Stock_inhand = " ";
                            }
                            if (SoldStock.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for SoldStock");
                                SoldStock = " ";
                            }
                            if (S_Return_Saleable.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for S_Return_Saleable");
                                S_Return_Saleable = " ";
                            }
                            if (S_Return_NonSaleable
                                    .equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for S_Return_NonSaleable");
                                S_Return_NonSaleable = " ";
                            }
                            if (ClosingBal.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for ClosingBal");
                                ClosingBal = " ";
                            }
                            if (GrossAmount.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for GrossAmount");
                                GrossAmount = " ";
                            }
                            if (Discount.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Discount");
                                Discount = " ";
                            }
                            if (NetAmount.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for NetAmount");
                                NetAmount = " ";
                            }
                            if (Size.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for Size");
                                Size = " ";
                            }
                            if (Price.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for sku_l");
                                Price = " ";
                            }
                            if (LMD.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for LMD");
                                LMD = " ";
                            }
                            if (AndroidCreatedDate
                                    .equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for AndroidCreatedDate");
                                AndroidCreatedDate = " ";
                            }

                            if (month.equalsIgnoreCase("anyType{}")) {
                                Log.e("", "anytype for month");
                                month = " ";
                            }

                            // db_cumvalueid_array = db_stock_id;
                            Log.e("pm", "pm5--");

                            db.open();
                            db.insertBocMonthWise(db_stock_id, db_Id,
                                    ProductId, CatCodeId, EANCode, EmpId,
                                    ProductCategory, ProductType, ProductName,
                                    Opening_Stock, FreshStock, Stock_inhand,
                                    SoldStock, S_Return_NonSaleable,
                                    S_Return_Saleable, ClosingBal, GrossAmount,
                                    Discount, NetAmount, Size, Price, LMD,
                                    AndroidCreatedDate, month, YEAR);
                            db.close();

                            Log.e("", "db_stock_id=" + db_stock_id + " empid="
                                    + EmpId);

                            db_cumvalueid_array = db_cumvalueid_array + ","
                                    + db_stock_id;

                        } else if (soap_result_monthwise1.getProperty("status")
                                .toString().equalsIgnoreCase("E")) {
                            Log.e("pm", "pm7");
                            // Flag = "1";

                            soap_update_monthwise_row = service
                                    .UpdateTableData(db_cumvalueid_array, "C",
                                            EmpId);
                            Log.e("", "soap_update_monthwise_row= "
                                    + soap_update_monthwise_row.toString());

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            // get current date time with Date()
                            Calendar cal = Calendar.getInstance();
                            // dateFormat.format(cal.getTime())
                            db.open();
                            db.updateDateSync(dateFormat.format(cal.getTime()),
                                    "stock_monthwise");
                            db.close();

                        } else if (soap_result_monthwise1.getProperty("status")
                                .toString().equalsIgnoreCase("N")) {

                            soap_update_monthwise_row = service
                                    .UpdateTableData(db_cumvalueid_array, "C",
                                            EmpId);
                            Log.e("", "soap_update_monthwise_row= "
                                    + soap_update_monthwise_row.toString());

                            syncbocmonthwise = 1;
                            // Flag = "3";
                        } else if (soap_result_monthwise1.getProperty("status")
                                .toString().equalsIgnoreCase("SE")) {

                            soap_update_monthwise_row = service
                                    .UpdateTableData(db_cumvalueid_array, "C",
                                            EmpId);
                            Log.e("", "soap_update_monthwise_row= "
                                    + soap_update_monthwise_row.toString());

                            // Flag="2";
                            syncbocmonthwise = 0;
                            final Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat formatter = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            String Createddate = formatter.format(calendar
                                    .getTime());
                            Log.v("", "se error");
                            int n = Thread.currentThread().getStackTrace()[2]
                                    .getLineNumber();
                            db.open();
                            db.insertSyncLog("SyncStockCummData_SE",
                                    String.valueOf(n),
                                    "SyncStockCummData_SE()", Createddate,
                                    Createddate, sp.getString("username", ""),
                                    "Data Download", "Fail");
                            db.close();
                        }
                    }

                } else {

                    Log.v("", "Soap result is null");
                    // Toast.makeText(context,
                    // "Data Not Available or Check Connectivity",
                    // Toast.LENGTH_SHORT).show();
                    syncbocmonthwise = 0;
                    final Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "MM/dd/yyyy HH:mm:ss");
                    String Createddate = formatter.format(calendar.getTime());

                    int n = Thread.currentThread().getStackTrace()[2]
                            .getLineNumber();
                    db.open();
                    db.insertSyncLog("Soup is null - SyncStockCummData()",
                            String.valueOf(n), "SyncStockCummData()",
                            Createddate, Createddate,
                            sp.getString("username", ""), "Data Download",
                            "Fail");
                    db.close();
                }

                if (syncbocdaywise == 1 && syncbocmonthwise == 1
                        && syncstockdata == 1 && synctesterdata == 1) {

                    Flag = "1";
                } else {

                    Flag = "2";
                }

            } catch (Exception e) {

                e.printStackTrace();
                String Error = e.toString();

                final Calendar calendar = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "MM/dd/yyyy HH:mm:ss");
                String Createddate = formatter.format(calendar.getTime());
                Flag = "4";
                int n = Thread.currentThread().getStackTrace()[2]
                        .getLineNumber();
                db.open();
                db.insertSyncLog(Error, String.valueOf(n), "", Createddate,
                        Createddate, sp.getString("username", ""),
                        "Data Download", "Fail");
                db.close();

            }

            return soap_result;
        }


        @Override
        protected void onPostExecute(SoapObject result) {

            // TODO Auto-generated method stub
            // super.onPostExecute(result);

            db.close();
            mProgress.dismiss();

            if (Flag.equalsIgnoreCase("0")) {

                DisplayDialogMessage("Check Your Internet Connection!!!");

				/*Toast.makeText(SyncMaster.this,
						"Check Your Internet Connection!!!", Toast.LENGTH_LONG)
						.show();*/
            } else
                // if(result != null){

                if (Flag.equalsIgnoreCase("1")) {

                    boolean boc26 = false;
                    spe.putBoolean("BOC26", boc26);
                    spe.putBoolean("DialogDismiss", true);
                    spe.commit();
                    //final boolean boolRecd = false;

                    AlertDialog.Builder builder = new AlertDialog.Builder(SyncMaster.this);
                    builder.setMessage("Data Download Completed Successfully!!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    dialog.dismiss();
                                   // boolean bocflag = true;
                                    Intent i = new Intent(SyncMaster.this, DashboardNewActivity.class);
                                 //   i.putExtra("Bocflag",bocflag);
                                    startActivity(i);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
/*
				Toast.makeText(context,
						"Data Download Completed Successfully!!",
						Toast.LENGTH_SHORT).show();*/
                    // db.open();
                    // db.updateflag_disable_button();
                    // db.close();

				/*	startActivity(new Intent(SyncMaster.this,
							DashboardNewActivity.class));*/

                } else if (Flag.equalsIgnoreCase("2")) {

                    DisplayDialogMessage("Data Download Incomplete!!");

				/*Toast.makeText(context, "Data Download Incomplete!!",
						Toast.LENGTH_SHORT).show();
*/
                } /*
			 * else if (Flag.equalsIgnoreCase("3")) {
			 *
			 *
			 * Toast.makeText(context, "Sync Successfully!!",
			 * Toast.LENGTH_SHORT).show(); startActivity(new
			 * Intent(SyncMaster.this, DashboardNewActivity.class));
			 *
			 * }
			 */
                // }
                else if (Flag.equalsIgnoreCase("4")) {

                    DisplayDialogMessage("Data Download Incomplete!!,Please try again after some time");

				/*Toast.makeText(
						context,
						"Data Download Incomplete!!,Please try again after some time",
						Toast.LENGTH_SHORT).show();*/
                } else {

                    DisplayDialogMessage("Data Download Incomplete!!");

				/*Toast.makeText(context, "Data Download Incomplete!!",
						Toast.LENGTH_SHORT).show();*/
                }

        }

        private void DisplayDialogMessage(String msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SyncMaster.this);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mProgress.setMessage("Receiving.....");
            mProgress.show();
            mProgress.setCancelable(false);
        }

    }

    public void readusermanual() {

        Log.v("", "u1");
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), "sample.pdf");
        Log.v("", "u1");
        try {
            in = assetManager.open("sample.pdf");
            out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);
            Log.v("", "u1");
            copyFile(in, out);
            Log.v("", "u1");
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));

            e.printStackTrace();

        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(
                    Uri.parse("file://" + getFilesDir() + "/sample.pdf"),
                    "application/pdf");

            startActivity(intent);

        } catch (Exception e) {

            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));

            e.printStackTrace();

        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

   /* public void test() {

        new syncAllData().execute();
    }*/

    public void writeStringAsFile(String fileContents) {
        Context context1 = context;
        try {
            File root;
            root = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "sudesi" + File.separator);

            root.mkdirs();

            FileWriter out = new FileWriter(new File(root, "sample.text"));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
            // Logger.logError(TAG, e);
        }
    }

    private void uploaddata() {

        db.open();
        stock_array = db.getStockdetails();

        if (stock_array.getCount() > 0) {
            if (stock_array != null && stock_array.moveToFirst()) {
                stock_array.moveToFirst();

                String shad;
                do {

                    JSONObject obj = new JSONObject();
                    try {

                        if (stock_array.getString(23) != null
                                || !stock_array.getString(23).equalsIgnoreCase("null")) {

                            Log.v("", "shadeno=" + stock_array.getString(23));
                            shad = stock_array.getString(23).toString();

                        } else {
                            Log.v("", "shadeno=" + stock_array.getString(23));
                            shad = "";
                        }

                        obj.put("id", stock_array.getString(0));
                        obj.put("Pid", stock_array.getString(2));
                        obj.put("CatCodeId", stock_array.getString(1));
                        obj.put("EANCode", stock_array.getString(3));
                        obj.put("empId", username);
                        obj.put("ProductCategory", stock_array.getString(4));
                        obj.put("product_type", stock_array.getString(5));
                        obj.put("product_name", stock_array.getString(6));
                        obj.put("shadeno", shad);
                        obj.put("Opening_Stock", stock_array.getString(10));
                        obj.put("FreshStock", stock_array.getString(11));
                        obj.put("Stock_inhand", stock_array.getString(12));
                        obj.put("SoldStock", stock_array.getString(16));
                        obj.put("S_Return_Saleable", stock_array.getString(14));
                        obj.put("S_Return_NonSaleable", stock_array.getString(15));
                        obj.put("ClosingBal", stock_array.getString(13));
                        obj.put("GrossAmount", stock_array.getString(17));
                        obj.put("Discount", stock_array.getString(19));
                        obj.put("NetAmount", stock_array.getString(18));
                        obj.put("Size", stock_array.getString(7));
                        obj.put("Price", stock_array.getString(8));
                        obj.put("AndroidCreatedDate", stock_array.getString(21));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    array.put(obj);
//                }
                } while (stock_array.moveToNext());
                stock_array.close();
            }

            if (cd.isConnectingToInternet()) {

                showProgreesDialog();
//                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, array, new Response.Listener<JSONArray>() {
                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,URL, array, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonResponse) {

                        Log.e("onResponse: ", jsonResponse.toString());
                        int indexOfOpenBracket = jsonResponse.toString().indexOf("[");
                        int indexOfLastBracket = jsonResponse.toString().lastIndexOf("]");

                        String jsonStr = jsonResponse.toString().substring(indexOfOpenBracket + 1, indexOfLastBracket);

                        if (jsonStr != null) {
                            try {
                                JSONObject jsonObj = new JSONObject(jsonStr);

                                // Getting JSON Array node
                                String flag = jsonObj.getString("Flag");
                                String message = jsonObj.getString("errormsg");
                                JSONArray id = jsonObj.getJSONArray("IdInserted");

                                if (flag != null && flag.equalsIgnoreCase("TRUE")) {
                                    for (int i = 0; i < id.length(); i++) {

                                        String stockid = id.get(i).toString();

                                        db.open();
                                        db.update_stock_data(stockid);
                                        Log.d("Data is Updating here ",
                                                stockid);
                                        db.close();


                                    }

                                    Log.e("JSON_TRUE",flag+"_MSG_"+message);
                                    new syncAllData(flag).execute();

                                } else {
                                    for (int i = 0; i < id.length(); i++) {

                                        String stockid = id.get(i).toString();

                                        db.open();
                                        db.update_stock_data(stockid);
                                        Log.d("Data is Updating here ",
                                                stockid);
                                        db.close();


                                    }
                                    ErroFlag = "0";
                                    final Calendar calendar1 = Calendar
                                            .getInstance();
                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                            "MM/dd/yyyy HH:mm:ss");
                                    String Createddate = formatter1
                                            .format(calendar1.getTime());

                                    int n = Thread.currentThread().getStackTrace()[2].getLineNumber();
                                    db.insertSyncLog(message, String.valueOf(n), "SaveStock()", Createddate,
                                            Createddate, sp.getString("username", ""),
                                            "SaveStock()", "Fail");
                                    Log.e("JSON_TRUE",flag+"_MSG_"+message);
                                    new syncAllData(ErroFlag).execute();
                                }
                                dissmissDialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dissmissDialog();
                        ErroFlag = "0";
                        final Calendar calendar1 = Calendar
                                .getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1
                                .format(calendar1.getTime());

                        int n = Thread.currentThread()
                                .getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(error.getMessage(),
                                String.valueOf(n),
                                "SaveStock()", Createddate,
                                Createddate, sp.getString(
                                        "username", ""),
                                "SaveStock()", "Fail");

//                        Toast.makeText(context,"Stock Data not upload", Toast.LENGTH_SHORT).show();
                        Log.e("JSON_ERROR","ERROR");
                        new syncAllData(ErroFlag).execute();

                    }
                }) {
                    @Override
                    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                jsonArrayRequest.setShouldCache(false);
                jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                        300000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                TestApplication.getInstance().addToRequestQueue(jsonArrayRequest);

            }else{
                DisplayDialogMessage("Connectivity Error Please check internet");
            }

        } else {
            Toast.makeText(this,"No Stock For Data Upload", Toast.LENGTH_SHORT).show();
            Log.e("NoStock dataupload",
                    String.valueOf(stock_array.getCount()));
        }
    }



    private void showProgreesDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait .."); // Setting Message
        progressDialog.setTitle("Uploading Data."); // Setting Title
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
    }

    private void dissmissDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

    }


}
