package com.prod.sudesi.lotusherbalsnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class ReturnQtyActivity extends Activity {

    TableLayout tl_return_calculation;
    TableRow tr;
    TextView titel;
    EditText edt_gross, edt_discount, edt_net;
    String old_stock_recive;
    String str_openingstock = "0";
    String str_price = "0";
    String str_grossamt = "0";
    String str_discount = "0", str_soldstock = "0";
    String str_close_bal = "0";
    String str_stockinhand;
    Button btn_save, btn_back, btn_home, btn_logout;
    int count = 0;
    String sclo = "";
    Dbcon db;
    String old_return;
    TextView edti;
    int current_return;
    String fresh_stock;
    Cursor mCursor = null;
    private static int ecolor;
    private static String namestring, fieldValue;
    private static ForegroundColorSpan fgcspan;
    private static SpannableStringBuilder ssbuilder;
    String rt_stk1 = "0";
    TextView tv_h_username;// -------
    String username, pass,role, outletcode, bdename;
    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;
    static Context context;
    String enacod[], catid[], pro_name[], show_pro_name[], size[], db_id[], mrp[], shadeno[], singleoffer[];
    String old_return_non_salable = "", old_return_salable = "";
    ScrollView scrv_sale;
    String rt_n_s_stk, rt_s_stk;
    ConnectionDetector cd;
    private ProgressDialog progressDialog;
    Cursor stock_array;
    ArrayList<String> uploadidlist;
    private JSONArray array;
    String ErroFlag = "";
    LotusWebservice service;

    //UAT Server
    /*public static String URL = "http://sandboxws.lotussmartforce.com/WebAPIStock/api/Stock/SaveStock";
    public static String CoCheckStockURL = "http://sandboxws.lotussmartforce.com/WebAPIStock/api/Stock/Check_AvailableStock";*/

    //Production Server
    public static String URL = "http://lotusws.lotussmartforce.com/WebAPIStock/api/Stock/SaveStock";
    public static String CoCheckStockURL = "http://lotusws.lotussmartforce.com/WebAPIStock/api/Stock/Check_AvailableStock";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_returns_qty);
        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        tl_return_calculation = (TableLayout) findViewById(R.id.tl_return_calculation);

        context = getApplicationContext();
        cd = new ConnectionDetector(this);
        service = new LotusWebservice(this);
        titel = (TextView) findViewById(R.id.textView1);
        titel.setText("" + ReturnsActivity.PMODE);
        edt_gross = (EditText) findViewById(R.id.edt_gross);
        edt_discount = (EditText) findViewById(R.id.edt_discount);
        edt_net = (EditText) findViewById(R.id.edt_net);

        btn_save = (Button) findViewById(R.id.btn_return_save);
        btn_back = (Button) findViewById(R.id.btn_return_back);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        db = new Dbcon(ReturnQtyActivity.this);
        shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
        shpeditor = shp.edit();

        username = shp.getString("username", "");
        pass = shp.getString("Password", "");
        role = shp.getString("Role", "");
        bdename = shp.getString("BDEusername", "");
        outletcode = shp.getString("FLRCode", "");

        Intent intent = getIntent();

        db_id = intent.getStringArrayExtra("db_id");
        pro_name = intent.getStringArrayExtra("pro_name");
        show_pro_name = intent.getStringArrayExtra("show_pro_name");
        mrp = intent.getStringArrayExtra("mrp");
        shadeno = intent.getStringArrayExtra("shadeNo");
        enacod = intent.getStringArrayExtra("encode");
        catid = intent.getStringArrayExtra("CAT");
        size = intent.getStringArrayExtra("Size");

        // ---------------------
        tv_h_username = (TextView) findViewById(R.id.tv_h_username);


        tv_h_username.setText(bdename);

        btn_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                finish();
                startActivity(new Intent(ReturnQtyActivity.this,
                        ReturnsActivity.class));

            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btn_home.setOnClickListener(new View.OnClickListener() {

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


        for (int i = 0; i < db_id.length; i++) {
            String s = "" + getLastInsertIDofStock1(catid[i], db_id[i], "", mrp[i]);

            if (db_id[i].equalsIgnoreCase("0")) {

            } else {

                tr = new TableRow(this);
                tr.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                tr.setWeightSum(6f);

                TableRow.LayoutParams lp;
                lp = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3f);
                TextView productname = new TextView(this);
                if (show_pro_name[i] != null &&
                        !show_pro_name[i].equalsIgnoreCase("")) {
                    productname.setText(show_pro_name[i]);
                } else {
                    productname.setText(pro_name[i]);
                }
                productname.setTextColor(Color.WHITE);
                productname.setMaxEms(12);
                productname.setTextSize(11);
                productname.setMaxLines(3);
                productname.setLayoutParams(lp);
                tr.addView(productname);// add the column to the table row here

                lp = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                EditText qty = new EditText(this);
                qty.setTextColor(Color.WHITE);
                qty.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                qty.setSingleLine(true);
                qty.setTextSize(15);
                qty.setMaxEms(5);
                qty.setLayoutParams(lp);
                qty.setGravity(Gravity.CENTER);
                qty.setInputType(InputType.TYPE_CLASS_NUMBER);
                tr.addView(qty);

                qty.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub

                    }
                });

                lp = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                TextView price = new TextView(this);
                price.setText(mrp[i]);
                price.setTextSize(15);
                price.setLayoutParams(lp);
                price.setGravity(Gravity.CENTER);
                price.setTextColor(Color.WHITE);
                tr.addView(price);

                TextView tv2 = new TextView(this);
                tv2.setText(db_id[i]);
                tv2.setGravity(Gravity.CENTER);
                tv2.setVisibility(View.GONE);
                tr.addView(tv2);

                TextView tv3 = new TextView(this);
                tv3.setText(shadeno[i]);
                tv3.setGravity(Gravity.CENTER);
                tv3.setVisibility(View.GONE);
                tr.addView(tv3);

                lp = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                edti = new TextView(this);
                edti.setText(s);
                edti.setTextSize(15);
                edti.setLayoutParams(lp);
                edti.setGravity(Gravity.CENTER);
                edti.setTextColor(Color.WHITE);
                tr.addView(edti);

                tl_return_calculation.addView(tr, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                tl_return_calculation.setShrinkAllColumns(true);

            }

        }


        btn_save.setOnClickListener(new View.OnClickListener() {
            int rcounter = 0;

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                btn_save.setEnabled(false);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        btn_save.setEnabled(true);
                        Log.d(TAG, "resend1");

                    }
                }, 5000);// set time as per your requirement

                if (titel.getText().toString().equalsIgnoreCase("Return to Company")) {
                    //new Check_AvailableStock_Server().execute();
                    Check_Stock_Server();
                } else {
                    savebuttonMethod();
                }

            }
        });

        // ----------------------------------------------------------------------------------

    }

    @SuppressLint("SimpleDateFormat")
    public String getLastInsertIDofStock1(String catid, String dbid,
                                          String eanid, String PRICE) {

        String catid1 = "", eanid1 = "", dbid1 = "", closebal = "", totalamount = "", sold = "", retn = "", retn_n_salebale = "", total_net_amt = "", price = "";
        ;
        String last_modified_date;
        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);

        String date = sdf.format(c.getTime());

        // 28.04.2015
        String[] day = date.split("-");

        String day1 = day[2];

        // 28.04.2015
        db.open();
        mCursor = db.getuniquedata1(catid, eanid, dbid, date);

        int count = mCursor.getCount();

        if (mCursor != null) {

            if (mCursor.moveToFirst()) {

                do {

                    closebal = mCursor.getString(mCursor
                            .getColumnIndex("close_bal"));


                    totalamount = mCursor.getString(mCursor
                            .getColumnIndex("total_gross_amount"));

                    catid1 = mCursor.getString(mCursor
                            .getColumnIndex("product_id"));

                    eanid1 = mCursor.getString(mCursor
                            .getColumnIndex("eancode"));

                    dbid1 = mCursor.getString(mCursor.getColumnIndex("db_id"));

                    sold = mCursor.getString(mCursor
                            .getColumnIndex("sold_stock"));

                    old_return_salable = mCursor.getString(mCursor
                            .getColumnIndex("return_saleable"));

                    old_return_non_salable = mCursor.getString(mCursor
                            .getColumnIndex("return_non_saleable"));

                    Log.e("dbold_return_nonsalable", old_return_non_salable);

                    total_net_amt = mCursor.getString(mCursor
                            .getColumnIndex("total_net_amount"));

                    old_stock_recive = mCursor.getString(mCursor
                            .getColumnIndex("stock_received"));


                    str_stockinhand = mCursor.getString(mCursor
                            .getColumnIndex("stock_in_hand"));

                    str_openingstock = mCursor.getString(mCursor
                            .getColumnIndex("opening_stock"));


                } while (mCursor.moveToNext());

                sclo = "" + closebal;// 28.04.2015//19.05.2015//23.05.2015//25.05.2015//22.06.2015
                old_return = "" + retn;


            } else {

                sclo = "0";// 28.04.2015//19.05.2015//23.05.2015//25.05.2015//22.06.2015
                old_return = "0";
                old_return_non_salable = "0";

            }

        }
        db.close();

        // ll.setVisibility(View.GONE);
        return closebal;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

    }

    public boolean isInteger(String str) {
        boolean result = true;
        try {
            int i = Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;

    }

    public void showAlertDialog(int c) {
        if (c == tl_return_calculation.getChildCount() - 1) {

            // mProgress.dismiss();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    ReturnQtyActivity.this);

            // set title
            alertDialogBuilder.setTitle("Saved Successfully!!");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Go  TO  :")
                    .setCancelable(false)

                    .setNegativeButton(
                            "Return Page",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int id) {

                                    dialog.cancel();
//*******************
                                    finish();
                                    if (role.equalsIgnoreCase("DUB")) {
                                        startActivity(new Intent(
                                                ReturnQtyActivity.this,
                                                StockSaleActivityForDubai.class));
                                    } else {
                                        startActivity(new Intent(
                                                ReturnQtyActivity.this,
                                                ReturnsActivity.class));
                                    }
                                }
                            })

                    .setPositiveButton(
                            "Home",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int id) {

                                    dialog.cancel();

                                    finish();
                                    startActivity(new Intent(
                                            ReturnQtyActivity.this,
                                            DashboardNewActivity.class));

                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder
                    .create();

            // show it
            alertDialog.show();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    ReturnQtyActivity.this);

            // set title
            alertDialogBuilder.setTitle("Data Not Saved!!!");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Problem with Insert")
                    .setCancelable(false)

                    .setNegativeButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int id) {

                                    dialog.cancel();

                                    finish();
                                    if (role.equalsIgnoreCase("DUB")) {
                                        startActivity(new Intent(
                                                ReturnQtyActivity.this,
                                                StockSaleActivityForDubai.class));
                                    } else {
                                        startActivity(new Intent(
                                                ReturnQtyActivity.this,
                                                StockNewActivity.class));
                                    }

                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder
                    .create();

            // show it
            alertDialog.show();
        }
    }

    public int saveData() {

        int count = 0;
        for (int i = 0; i < tl_return_calculation.getChildCount() - 1; i++) {


            TableRow t = (TableRow) tl_return_calculation
                    .getChildAt(i + 1);
            EditText edt_qty = (EditText) t.getChildAt(1);
            TextView tv_mrp = (TextView) t.getChildAt(2);
            TextView tv_dbID = (TextView) t.getChildAt(3);
            TextView tv_shadeno = (TextView) t
                    .getChildAt(4);
            TextView openingbal = (TextView) t
                    .getChildAt(5);

            String s = ""
                    + getLastInsertIDofStock1("", tv_dbID
                    .getText().toString(), "", "");

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
            String insert_timestamp = sdf.format(c
                    .getTime());

            String product_category = ReturnsActivity.selected_product_category;

            String product_type1 = ReturnsActivity.selected_product_type;

            String product_name = pro_name[i];// changed
            // 06.12.2014

            String opt = titel.getText().toString();
            Log.e("Title", titel.getText().toString());

            if (opt.equalsIgnoreCase("Return From Customer")) {
                fresh_stock = "0";
                rt_n_s_stk = "0";
                rt_s_stk = edt_qty.getText().toString();

                Log.e("fresh_stock", fresh_stock);
                Log.e("rt_n_s_stk", rt_n_s_stk);
                Log.e("rt_s_stk", rt_s_stk);


            } else if (opt.equalsIgnoreCase("Return to Company")) {
                fresh_stock = "0";
                rt_n_s_stk = edt_qty.getText().toString();
                rt_s_stk = "0";

                Log.e("fresh_stock", fresh_stock);
                Log.e("rt_n_s_stk", rt_n_s_stk);
                Log.e("rt_s_stk", rt_s_stk);


            }
            if (old_stock_recive != null) {

                if (old_stock_recive.equalsIgnoreCase("")
                        || old_stock_recive
                        .equalsIgnoreCase("null")
                        || old_stock_recive
                        .equalsIgnoreCase(" ")) {

                    old_stock_recive = "0";
                }
            } else {
                old_stock_recive = "0";
            }

            Log.e("old_stock_recive", old_stock_recive);

            if (old_return_salable != null) {

                if (old_return_salable.equalsIgnoreCase("")
                        || old_return_salable
                        .equalsIgnoreCase("null")
                        || old_return_salable
                        .equalsIgnoreCase(" ")) {

                    old_return_salable = "0";
                }
            } else {
                old_return_salable = "0";
            }

            Log.e("old_return_salable", old_return_salable);


            if (old_return_non_salable != null) {

                if (old_return_non_salable
                        .equalsIgnoreCase("")
                        || old_return_non_salable
                        .equalsIgnoreCase("null")
                        || old_return_non_salable
                        .equalsIgnoreCase(" ")) {

                    Log.e("if-statement", "executed");

                    old_return_non_salable = "0";
                }
            } else {
                old_return_non_salable = "0";
                Log.e("else-statement", "executed");
            }

            Log.e("old_return_non_salable", old_return_non_salable);


            int new_fresh_stock = Integer
                    .parseInt(fresh_stock)
                    + Integer.parseInt(old_stock_recive);

            int new_retrn_sale = Integer.parseInt(rt_s_stk)
                    + Integer.parseInt(old_return_salable);

            int new_retrn_non_sale = Integer
                    .parseInt(rt_n_s_stk)
                    + Integer
                    .parseInt(old_return_non_salable);

            Log.e("new_fresh_stock", String.valueOf(new_fresh_stock));
            Log.e("new_retrn_sale", String.valueOf(new_retrn_sale));
            Log.e("new_retrn_non_sale", String.valueOf(new_retrn_non_sale));

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat(
                    "MMMM",Locale.ENGLISH);
            String month_name = month_date.format(cal
                    .getTime());

            Calendar cal1 = Calendar.getInstance();
            SimpleDateFormat year_format = new SimpleDateFormat(
                    "yyyy",Locale.ENGLISH);
            String year_name = year_format.format(cal1
                    .getTime());

            String price = tv_mrp.getText().toString()
                    .trim();

            if (price.equalsIgnoreCase("")) {
                price = "0";
            }
            String eancode = "" + enacod[i];

            String db_id1 = tv_dbID.getText().toString()
                    .trim();

            String cat_id = catid[i];

            String size1 = "" + size[i];

            String shadenon = tv_shadeno.getText()
                    .toString().trim();

            if (shadenon.equalsIgnoreCase("")
                    || shadenon.equalsIgnoreCase("null")) {

                shadenon = "";
            }

            String solddd = "0";

            db.open();
            Cursor cur = db.fetchone(tv_dbID.getText()
                            .toString(), "stock", new String[]{
                            "sold_stock", "total_gross_amount",
                            "total_net_amount", "discount"},
                    "db_id");
            if (cur != null && cur.getCount() > 0) {
                cur.moveToFirst();

                if (cur.getString(0) != null) {

                    if (cur.getString(0).equalsIgnoreCase(
                            " ")
                            || cur.getString(0)
                            .equalsIgnoreCase("")) {
                        solddd = "0";
                    } else {
                        solddd = cur.getString(0)
                                .toString();
                    }
                } else {
                    solddd = "0";


                }

            } else {

                solddd = "0";


            }

            Log.e("solddd", solddd);


            // }
            db.close();

            Cursor mCursor;
            db.open();
            mCursor = db.getuniquedata1("", "", tv_dbID
                    .getText().toString(), "");


            if (mCursor != null && mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                str_openingstock = mCursor.getString(mCursor
                        .getColumnIndex("opening_stock"));
                if (str_openingstock != null) {

                    if (str_openingstock.equals("")) {

                        str_openingstock = "0";
                    }

                } else {
                    str_openingstock = "0";
                }

            } else {
                str_openingstock = "0";
            }

            Log.e("str_openingstock", str_openingstock);

            if (mCursor != null && mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                str_price = mCursor.getString(mCursor
                        .getColumnIndex("price"));
                if (str_price != null) {

                    if (str_price.equals("")) {

                        str_price = "0";
                    }

                } else {
                    str_price = "0";
                }

            } else {
                str_price = "0";
            }

            Log.e("str_price", str_price);

            if (mCursor != null && mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                str_grossamt = mCursor.getString(mCursor
                        .getColumnIndex("total_gross_amount"));
                if (str_grossamt != null) {

                    if (str_grossamt.equals("")) {

                        str_grossamt = "0";
                    }

                } else {
                    str_grossamt = "0";
                }

            } else {
                str_grossamt = "0";
            }

            Log.e("str_price", str_grossamt);

            if (mCursor != null && mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                str_discount = mCursor.getString(mCursor
                        .getColumnIndex("discount"));
                if (str_discount != null) {

                    if (str_discount.equals("")) {

                        str_discount = "0";
                    }

                } else {
                    str_discount = "0";
                }

            } else {
                str_discount = "0";
            }

            Log.e("str_discount", str_discount);

            if (mCursor != null && mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                str_soldstock = mCursor.getString(mCursor
                        .getColumnIndex("sold_stock"));
                if (str_soldstock != null) {

                    if (str_soldstock.equals("")) {

                        str_soldstock = "0";
                    }

                } else {
                    str_soldstock = "0";
                }

            } else {
                str_soldstock = "0";
            }

            Log.e("str_soldstock", str_soldstock);

            int soldstock = Integer.parseInt(str_soldstock) - Integer.parseInt(rt_s_stk);

            int pricecustomer = Integer.parseInt(price) * Integer.parseInt(rt_s_stk);

            int pricesold = Integer.parseInt(price) * Integer.parseInt(str_soldstock);

            int i_net_amt = pricesold - pricecustomer;

            int net_amt = Integer.parseInt(String.valueOf(i_net_amt)) - Integer.parseInt(str_discount);

            //--------Old production apk use
            int i_stkinand = Integer
                    .parseInt(str_openingstock)
                    + new_fresh_stock
                    + Integer.parseInt(rt_s_stk)
                    - new_retrn_non_sale;

            int i_stock_inand = Integer
                    .parseInt(str_openingstock)
                    + new_fresh_stock;

            Log.e("i_stkinand", String.valueOf(i_stkinand));

            //--------Old production apk use
            int i_close = i_stkinand - Integer.parseInt(solddd);

            Log.e("i_close", String.valueOf(i_close));

            int closeamt = Integer.parseInt(price) * i_close;

            int soldamt = Integer.parseInt(price) * soldstock;

            int freshamt = Integer.parseInt(price) * new_fresh_stock;

            if (mCursor.getCount() == 0) {

                db.open();
                db.Insertstock_Customer_Company(
                        product_category,
                        product_type1,
                        product_name,
                        username,
                        String.valueOf(i_stock_inand),
                        String.valueOf(i_close),
                        String.valueOf(closeamt),
                        String.valueOf(new_fresh_stock),
                        String.valueOf(freshamt),
                        price,
                        size1,
                        eancode,
                        db_id1,
                        cat_id,
                        insert_timestamp,
                        String.valueOf(soldstock),
                        String.valueOf(soldamt),
                        String.valueOf(new_retrn_sale),
                        String.valueOf(new_retrn_non_sale),
                        String.valueOf(i_net_amt),
                        String.valueOf(net_amt),
                        str_discount,
                        shadenon, insert_timestamp,
                        month_name, year_name);
                db.close();

                Toast.makeText(ReturnQtyActivity.this,
                        "Data save successfully",
                        Toast.LENGTH_SHORT).show();

                count++;

            } else {
                db.open();
                db.UpdateStock_Customer_Company(
                        product_category, product_type1,
                        product_name, username,
                        String.valueOf(i_stock_inand),
                        String.valueOf(i_close),
                        String.valueOf(closeamt),
                        String.valueOf(new_fresh_stock),
                        String.valueOf(freshamt),
                        price, size1, eancode, db_id1,
                        cat_id, insert_timestamp,
                        String.valueOf(soldstock),
                        String.valueOf(soldamt),
                        String.valueOf(new_retrn_sale),
                        String.valueOf(new_retrn_non_sale),
                        String.valueOf(i_net_amt),
                        String.valueOf(net_amt),
                        str_discount,
                        shadenon, insert_timestamp,
                        month_name, year_name);
                db.close();

                // Toast.makeText(StockAllActivity.this,
                // "Data save successfully",
                // Toast.LENGTH_SHORT).show();
                count++;
            }

        }

        return count;

    }

    private void uploaddata() {

        array = new JSONArray();
        uploadidlist = new ArrayList<>();
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

                        obj.put("id", cd.getNonNullValues(stock_array.getString(0)));
                        obj.put("Pid", cd.getNonNullValues(stock_array.getString(2)));
                        obj.put("CatCodeId", cd.getNonNullValues(stock_array.getString(1)));
                        obj.put("EANCode", cd.getNonNullValues(stock_array.getString(3)));
                        obj.put("empId", cd.getNonNullValues(username));
                        obj.put("ProductCategory", cd.getNonNullValues(stock_array.getString(4)));
                        obj.put("product_type", cd.getNonNullValues(stock_array.getString(5)));
                        obj.put("product_name", cd.getNonNullValues(stock_array.getString(6)));
                        obj.put("shadeno", cd.getNonNullValues(shad));
                        obj.put("Opening_Stock", cd.getNonNullValues(stock_array.getString(10)));
                        obj.put("FreshStock", cd.getNonNullValues(stock_array.getString(11)));
                        obj.put("Stock_inhand", cd.getNonNullValues(stock_array.getString(12)));
                        obj.put("SoldStock", cd.getNonNullValues(stock_array.getString(16)));
                        obj.put("S_Return_Saleable", cd.getNonNullValues(stock_array.getString(14)));
                        obj.put("S_Return_NonSaleable", cd.getNonNullValues(stock_array.getString(15)));
                        obj.put("ClosingBal", cd.getNonNullValues(stock_array.getString(13)));
                        obj.put("GrossAmount", cd.getNonNullValues(stock_array.getString(17)));
                        obj.put("Discount", cd.getNonNullValues(stock_array.getString(19)));
                        obj.put("NetAmount", cd.getNonNullValues(stock_array.getString(18)));
                        obj.put("Size", cd.getNonNullValues(stock_array.getString(7)));
                        obj.put("Price", cd.getNonNullValues(stock_array.getString(8)));
                        obj.put("AndroidCreatedDate", cd.getNonNullValues(stock_array.getString(21)));
                        obj.put("FLRCode", cd.getNonNullValues(outletcode));
                        obj.put("flag", cd.getNonNullValues(stock_array.getString(35)));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    array.put(obj);
//                }
                } while (stock_array.moveToNext());
                //stock_array.close();
            }

            if (cd.isConnectingToInternet()) {

                showProgreesDialog();
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, array, new Response.Listener<JSONArray>() {
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
                                        Log.e("Data is Updating here ",
                                                stockid);
                                        db.close();


                                    }

                                    Log.e("JSON_TRUE", flag + "_MSG_" + message);
                                    new CheckpasswordChange().execute();

                                } else {
                                    ErroFlag = "0";
                                    final Calendar calendar1 = Calendar
                                            .getInstance();
                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                            "MM/dd/yyyy HH:mm:ss",Locale.ENGLISH);
                                    String Createddate = formatter1
                                            .format(calendar1.getTime());

                                    int n = Thread.currentThread().getStackTrace()[2].getLineNumber();
                                    db.insertSyncLog(message, String.valueOf(n), "SaveStock()", Createddate,
                                            Createddate, username,
                                            "SaveStock()", "Fail");
                                    Log.e("JSON_FALSE", flag + "_MSG_" + message);
                                }
                                //dissmissDialog();
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
                                "MM/dd/yyyy HH:mm:ss",Locale.ENGLISH);
                        String Createddate = formatter1
                                .format(calendar1.getTime());

                        int n = Thread.currentThread()
                                .getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(error.getMessage(),
                                String.valueOf(n),
                                "SaveStock()", Createddate,
                                Createddate, shp.getString(
                                        "username", ""),
                                "SaveStock()", "Fail");

//                        Toast.makeText(context,"Stock Data not upload", Toast.LENGTH_SHORT).show();
                        //Log.e("JSON_ERROR", error.getMessage());

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

            } else {
                DisplayDialogMessage("Connectivity Error Please check internet");
            }

        } else {
            new CheckpasswordChange().execute();
            Toast.makeText(this, "No Stock For Data Upload", Toast.LENGTH_SHORT).show();
            Log.e("NoStock dataupload", String.valueOf(stock_array.getCount()));
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
        if (progressDialog != null && progressDialog.isShowing() && !ReturnQtyActivity.this.isFinishing()) {
            progressDialog.dismiss();
        }
    }

    private void DisplayDialogMessage(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ReturnQtyActivity.this);
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


    public class Check_AvailableStock_Server extends AsyncTask<String, Void, SoapObject> {

        private SoapPrimitive soap_result = null;

        String Flag = "";
        int protruecount = 0;
        int procount = 0;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            String msg = "Please Wait....";
            cd.showProgressDialog(msg);

        }

        @Override
        protected SoapObject doInBackground(String... params) {
            // TODO Auto-generated method stub

            if (!cd.isConnectingToInternet()) {

                Flag = "0";

            } else {
                try {
                    for (int i = 0; i < tl_return_calculation.getChildCount() - 1; i++) {

                        TableRow t = (TableRow) tl_return_calculation
                                .getChildAt(i + 1);
                        EditText edt_qty = (EditText) t.getChildAt(1);
                        TextView tv_dbID = (TextView) t.getChildAt(3);

                        procount++;
                        soap_result = service.Check_AvailableStock_Server(username, tv_dbID.getText().toString(),
                                edt_qty.getText().toString());

                        if (soap_result != null) {

                            if (soap_result.toString().equalsIgnoreCase("True")) {
                                Flag = "1";
                                protruecount++;
                            } else if (soap_result.toString().equalsIgnoreCase("False")) {
                                Flag = "2";
                            } else if (soap_result.toString().equalsIgnoreCase("No opening")) {
                                Flag = "3";
                            } else if (soap_result.toString().equalsIgnoreCase("SE")) {
                                Flag = "SE";

                            }
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

            if (ReturnQtyActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            cd.dismissProgressDialog();

            if (Flag.equalsIgnoreCase("0")) {

                Toast.makeText(getApplicationContext(), "Connectivity Error, Please check Internet connection!!", Toast.LENGTH_SHORT).show();

            } else if (procount == protruecount) {
                if (Flag.equalsIgnoreCase("1")) {

                    savebuttonMethod();

                } else if (Flag.equalsIgnoreCase("2")) {

                    DisplayDialogMessage("You don't have enough Stock to Return, Kindly check your Stock.");

                } else if (Flag.equalsIgnoreCase("3")) {

                    DisplayDialogMessage("You don't have Opening for this SKU, Kindly check your Openings.");

                } else if (Flag.equalsIgnoreCase("SE")) {

                    Toast.makeText(getApplicationContext(), "Connectivity server Error, Please Try Again!!", Toast.LENGTH_SHORT).show();

                }
            } else {

            }

        }


    }

    @Override
    protected void onDestroy() {
        cd.dismissProgressDialog();
        super.onDestroy();
    }

    public void savebuttonMethod() {
        if (cd.isCurrentDateMatchDeviceDate()) {
            try {

                int etcount = 0;
                int count = 0;
                boolean negative = false;
                if (tl_return_calculation.getChildCount() != 1) {
                    for (int i = 0; i < tl_return_calculation.getChildCount() - 1; i++) {

                        TableRow t = (TableRow) tl_return_calculation
                                .getChildAt(i + 1);
                        EditText edt_qty = (EditText) t.getChildAt(1);
                        TextView tv_dbID = (TextView) t.getChildAt(3);

                        if (edt_qty.getText().toString().trim()
                                .equalsIgnoreCase("0")
                                || edt_qty.getText().toString().trim()
                                .equalsIgnoreCase("")
                                || edt_qty.getText().toString().trim()
                                .equalsIgnoreCase(" ")) {
                            Toast.makeText(getApplicationContext(),
                                    "Please Enter in All Fields",
                                    Toast.LENGTH_SHORT);
                        } else {

                         /*   if (titel.getText().toString().equalsIgnoreCase("Return to Company")) {
                                int closBal, enteredQty, difference = 0;
//									int difference =
                                db.open();
                                Cursor cur = db.getuniquedata1("", "", tv_dbID.getText().toString().trim(), "");
                                if (cur != null && cur.getCount() > 0) {
                                    cur.moveToFirst();
                                    if (isInteger(cur.getString(cur.getColumnIndex("close_bal"))))
                                        closBal = Integer.parseInt(cur.getString(cur.getColumnIndex("close_bal")));
                                    else
                                        closBal = 0;
                                    if (isInteger(edt_qty.getText().toString().trim())) {
                                        enteredQty = Integer.parseInt(edt_qty.getText().toString().trim());
                                    } else {
                                        enteredQty = 0;
                                    }

                                    difference = closBal - enteredQty;
                                }
                                if (edt_qty.getText().toString()
                                        .matches("[a-zA-Z ]+")) {
                                    Toast.makeText(getApplicationContext(),
                                            "Please Enter Only Numbers",
                                            Toast.LENGTH_SHORT);
                                }

                                if (Integer.parseInt(edt_qty.getText()
                                        .toString().trim()) <= 0) {
                                    Toast.makeText(getApplicationContext(),
                                            "Please Enter in All Fields",
                                            Toast.LENGTH_SHORT);
                                } else if (difference <= 0) {
                                    negative = true;
                                } else {
                                    etcount++;
                                }
                            } else {*/
                            if (edt_qty.getText().toString()
                                    .matches("[a-zA-Z ]+")) {
                                Toast.makeText(getApplicationContext(),
                                        "Please Enter Only Numbers",
                                        Toast.LENGTH_SHORT);
                            }

                            if (Integer.parseInt(edt_qty.getText()
                                    .toString().trim()) <= 0) {
                                Toast.makeText(getApplicationContext(),
                                        "Please Enter in All Fields",
                                        Toast.LENGTH_SHORT);
                            } else

                                etcount++;
                            // }


                            Log.e("etcount", String.valueOf(etcount));
                        }

                    }
                }

                int numberofproduct = (tl_return_calculation.getChildCount() - 1);

                if (numberofproduct == etcount) {
                    if (tl_return_calculation.getChildCount() != 1) {

                        count = saveData();
                        if (cd.isConnectingToInternet()) {
                            uploaddata();

                            showAlertDialog(count);

                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReturnQtyActivity.this);
                            builder.setMessage("Your Data Save Locally, Please do Data Upload!!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                            Intent i = new Intent(ReturnQtyActivity.this, SyncMaster.class);
                                            startActivity(i);

                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }


                } else {
                    Toast.makeText(
                            ReturnQtyActivity.this,
                            "Please fill up all valid value in quantity fields",
                            Toast.LENGTH_LONG).show();
                }
                  /*          // mProgress.dismiss();

                            if (titel.getText().toString().equalsIgnoreCase("Return to Company")) {
                                if (negative == true) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                            ReturnQtyActivity.this);

                                    // set title
                                    alertDialogBuilder.setTitle("ERROR !!");

                                    // set dialog message
                                    alertDialogBuilder
                                            .setMessage("Closing Balance is going in negative. Do you want to continue ?")
                                            .setCancelable(false)

                                            .setNegativeButton(
                                                    "YES",
                                                    new DialogInterface.OnClickListener() {

                                                        public void onClick(

                                                                DialogInterface dialog,
                                                                int id) {

                                                            dialog.dismiss();
                                                            int count;
                                                            count = saveData();

                                                            showAlertDialog(count);
                                                        }
                                                    })

                                            .setPositiveButton(
                                                    "NO",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int id) {

                                                            dialog.dismiss();


                                                        }
                                                    });

                                    // create alert dialog
                                    AlertDialog alertDialog = alertDialogBuilder
                                            .create();

                                    // show it
                                    alertDialog.show();
                                } else {
                                    Toast.makeText(
                                            ReturnQtyActivity.this,
                                            "Please fill up all valid value in quantity fields",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(
                                        ReturnQtyActivity.this,
                                        "Please fill up all valid value in quantity fields",
                                        Toast.LENGTH_LONG).show();
                            }

                        }*/
            } catch (NumberFormatException e) {
                Toast.makeText(ReturnQtyActivity.this,
                        "Please enter Only numbers", Toast.LENGTH_LONG)
                        .show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(ReturnQtyActivity.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

        }
    }


    private void Check_Stock_Server() {

        JSONArray array = new JSONArray();

        for (int i = 0; i < tl_return_calculation.getChildCount() - 1; i++) {

            JSONObject obj = new JSONObject();
            TableRow t = (TableRow) tl_return_calculation
                    .getChildAt(i + 1);
            EditText edt_qty = (EditText) t.getChildAt(1);
            TextView tv_dbID = (TextView) t.getChildAt(3);
            try {
                obj.put("empId", cd.getNonNullValues(username));
                obj.put("Pid", cd.getNonNullValues(tv_dbID.getText().toString()));
                obj.put("S_Return_NonSaleable", cd.getNonNullValues(edt_qty.getText().toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
        }


        if (cd.isConnectingToInternet()) {
            try {
                showProgreesDialog();
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, CoCheckStockURL, array, new Response.Listener<JSONArray>() {
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

                                if (flag != null && flag.equalsIgnoreCase("True")) {

                                    savebuttonMethod();

                                } else if (flag != null && flag.equalsIgnoreCase("False")) {

                                    DisplayDialogMessage("You don't have enough Stock to Return, Kindly check your Stock.");

                                } else if (flag != null && flag.equalsIgnoreCase("No opening")) {

                                    DisplayDialogMessage("You don't have Opening for this SKU, Kindly check your Openings.");

                                } else {
                                    ErroFlag = "0";
                                    final Calendar calendar1 = Calendar
                                            .getInstance();
                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                            "MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
                                    String Createddate = formatter1
                                            .format(calendar1.getTime());

                                    int n = Thread.currentThread().getStackTrace()[2].getLineNumber();
                                    db.insertSyncLog("", String.valueOf(n), "Check_AvailableStock()", Createddate,
                                            Createddate, username,
                                            "Check_AvailableStock()", "Fail");
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
                                "MM/dd/yyyy HH:mm:ss",Locale.ENGLISH);
                        String Createddate = formatter1
                                .format(calendar1.getTime());

                        int n = Thread.currentThread()
                                .getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(error.getMessage(),
                                String.valueOf(n),
                                "Check_AvailableStock()", Createddate,
                                Createddate, shp.getString(
                                        "username", ""),
                                "Check_AvailableStock()", "Fail");

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DisplayDialogMessage("Connectivity Error Please check internet");
        }


    }

    public class CheckpasswordChange extends AsyncTask<String, Void, SoapObject> {

        private SoapPrimitive soap_result = null;

        String Flag = "";


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

        }

        @Override
        protected SoapObject doInBackground(String... params) {
            // TODO Auto-generated method stub

            if (!cd.isConnectingToInternet()) {

                Flag = "0";

            } else {
                try {

                    soap_result = service.CheckpasswordChange(username, pass);

                    if (soap_result != null) {

                        if (soap_result.toString().equalsIgnoreCase("Correct")) {
                            Flag = "1";
                        } else if (soap_result.toString().equalsIgnoreCase("Incorrect")) {
                            Flag = "2";
                        }else if (soap_result.toString().equalsIgnoreCase("SE")) {
                            Flag = "SE";

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

            if (ReturnQtyActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }

            dissmissDialog();

            if (Flag.equalsIgnoreCase("0")) {

                Toast.makeText(getApplicationContext(), "Connectivity Error, Please check Internet connection!!", Toast.LENGTH_SHORT).show();

            } else if (Flag.equalsIgnoreCase("1")) {

                //showAlertDialog(count);

            } else if (Flag.equalsIgnoreCase("2")) {

                Intent i = new Intent(ReturnQtyActivity.this, LoginActivity.class);
                startActivity(i);
            }
            else if (Flag.equalsIgnoreCase("SE")) {

                Toast.makeText(getApplicationContext(),"Please check Internet connection!! Try Again", Toast.LENGTH_SHORT).show();

            }

        }


    }

}
