package com.prod.sudesi.lotusherbalsnew;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.prod.sudesi.lotusherbalsnew.R;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class SaleCalculation extends Activity {

    TableLayout tl_sale_calculation;
    TableRow tr;
    TextView edti;
    Cursor mCursor = null;
    String old_return_non_salable = "", old_return_salable = "";
    String old_stock_recive;
    String str_openingstock = "0";
    String str_stockinhand;
    String sclo = "";
    String old_return;
    String str_price, str_grossamt, str_discount, str_soldstock;

    EditText edt_gross, edt_discount, edt_net;

    Button btn_save, btn_back, btn_home, btn_logout;

    Dbcon db;

    private static int ecolor;
    private static String namestring, fieldValue;
    private static ForegroundColorSpan fgcspan;
    private static SpannableStringBuilder ssbuilder;

    TextView tv_h_username;// -------
    String username, FLRCode, role, bdename;
    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;
    static Context context;

    ScrollView scrv_sale;
    ConnectionDetector cd;
    LinearLayout discountlayout;

    String enacod[], catid[], pro_name[],show_pro_name[], size[], db_id[], mrp[], shadeno[], singleoffer[];


    // private ProgressDialog mProgress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_saleall);

        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        tl_sale_calculation = (TableLayout) findViewById(R.id.tl_sale_calculation);
        discountlayout = (LinearLayout) findViewById(R.id.discountlayout);

        // scrv_sale = (ScrollView)findViewById(R.id.scrv_sale);

        context = getApplicationContext();
        cd = new ConnectionDetector(context);
        edt_gross = (EditText) findViewById(R.id.edt_gross);

        edt_net = (EditText) findViewById(R.id.edt_net);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        db = new Dbcon(SaleCalculation.this);
        shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
        shpeditor = shp.edit();

        username = shp.getString("username", "");
        FLRCode = shp.getString("FLRCode", "");
        role = shp.getString("Role", "");
        bdename = shp.getString("BDEusername", "");

        if (!role.equalsIgnoreCase("DUB")) {
            edt_discount = (EditText) findViewById(R.id.edt_discount);
        } else {
            discountlayout.setVisibility(View.GONE);
        }

        Intent intent = getIntent();

        if (role.equalsIgnoreCase("DUB")) {
            pro_name = intent.getStringArrayExtra("pro_name");
            db_id = intent.getStringArrayExtra("db_id");
            mrp = intent.getStringArrayExtra("mrp");
            size = intent.getStringArrayExtra("Size");
            enacod = intent.getStringArrayExtra("encode");
            singleoffer = intent.getStringArrayExtra("singleoffer");
        } else {
            db_id = intent.getStringArrayExtra("db_id");
            pro_name = intent.getStringArrayExtra("pro_name");
            show_pro_name = intent.getStringArrayExtra("show_pro_name");
            mrp = intent.getStringArrayExtra("mrp");
            shadeno = intent.getStringArrayExtra("shadeNo");
            enacod = intent.getStringArrayExtra("encode");
            size = intent.getStringArrayExtra("size");
            catid = intent.getStringArrayExtra("catid");
        }
        //String closing[] = intent.getStringArrayExtra("closing");
        // ---------------------

        tv_h_username = (TextView) findViewById(R.id.tv_h_username);

        tv_h_username.setText(bdename);

        // ----------------------------
        btn_save.setOnClickListener(new OnClickListener() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {

                btn_save.setEnabled(false);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        btn_save.setEnabled(true);
                        Log.d(TAG, "resend1");

                    }
                }, 5000);// set time as per your requirement

                if (role.equalsIgnoreCase("FLR")) {
                    saveMethodForFLR();
                } else if (role.equalsIgnoreCase("DUB")) {
                    saveMethodForDubai();
                } else {
                    saveMethodForLHR();
                }

            }
        });

        btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (role.equalsIgnoreCase("FLR")) {
                    finish();
                    startActivity(new Intent(SaleCalculation.this,
                            SaleActivityForFloter.class));
                } else if(role.equalsIgnoreCase("DUB")){
                    finish();
                    startActivity(new Intent(SaleCalculation.this,
                            StockSaleActivityForDubai.class));
                }else{
                    finish();
                    startActivity(new Intent(SaleCalculation.this,
                            SaleNewActivity.class));
                }
            }
        });

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

                Intent i = new Intent(getApplicationContext(),
                        DashboardNewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        Log.e("db_id.length", String.valueOf(db_id.length));

        if (role.equalsIgnoreCase("DUB")) {
            for (int i = 0; i < db_id.length; i++) {

                String s = getLastInsertIDofStockdubai(db_id[i], FLRCode);

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
                    productname.setText(pro_name[i]);
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

                    lp = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                    edti = new TextView(this);
                    edti.setText(s);
                    edti.setTextSize(15);
                    edti.setLayoutParams(lp);
                    edti.setGravity(Gravity.CENTER);
                    edti.setTextColor(Color.WHITE);
                    tr.addView(edti);

                    tl_sale_calculation.addView(tr, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    tl_sale_calculation.setShrinkAllColumns(true);

                }

            }
        } else {
            for (int i = 0; i < db_id.length; i++) {

                String s = "" + getLastInsertIDofStock1("", db_id[i], "", mrp[i]);


                tr = new TableRow(this);
                tr.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                tr.setWeightSum(6f);

                TableRow.LayoutParams lp;
                lp = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3f);
                TextView productname = new TextView(this);
                if(show_pro_name[i] != null &&
                        !show_pro_name[i].equalsIgnoreCase("")) {
                    productname.setText(show_pro_name[i]);
                }else{
                    productname.setText(pro_name[i]);
                }
                productname.setTextColor(Color.WHITE);
                productname.setMaxEms(12);
                productname.setTextSize(11);
                productname.setMaxLines(3);
                productname.setLayoutParams(lp);
                tr.addView(productname);// add the column to the table row here

                lp = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
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

                lp = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
                TextView price = new TextView(this);
                price.setText(mrp[i]);
                price.setTextSize(15);
                price.setLayoutParams(lp);
                price.setGravity(Gravity.CENTER);
                price.setTextColor(Color.WHITE);
                tr.addView(price);

                TextView tv2 = new TextView(this);
                tv2.setText(db_id[i]);
                tv2.setVisibility(View.GONE);
                tr.addView(tv2);

                TextView tv3 = new TextView(this);
                tv3.setText(shadeno[i]);
                tv3.setVisibility(View.GONE);
                tr.addView(tv3);

                lp = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
                edti = new TextView(this);
                edti.setText(s);
                edti.setTextSize(15);
                edti.setLayoutParams(lp);
                edti.setGravity(Gravity.CENTER);
                edti.setTextColor(Color.WHITE);
                tr.addView(edti);

                tl_sale_calculation.addView(tr, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                tl_sale_calculation.setShrinkAllColumns(true);

            }
        }

        edt_gross.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) try {
                    int total = 0;
                    Float Total = 0.0f;
                    for (int i = 0; i < tl_sale_calculation.getChildCount(); i++) {

                        TableRow t = (TableRow) tl_sale_calculation
                                .getChildAt(i + 1);

                        EditText edt_qty = (EditText) t.getChildAt(1);
                        TextView tv_mrp = (TextView) t.getChildAt(2);
                        int int_quantity, int_mrp;
                        Float fl_mrp;

                        if (!edt_qty.getText().toString().equals("")) {
                            if (role.equalsIgnoreCase("DUB")) {
                                int_quantity = Integer.parseInt(edt_qty
                                        .getText().toString().trim());
                                fl_mrp = Float.parseFloat(tv_mrp.getText().toString());
                                Float multiply = Float.parseFloat(String.valueOf(int_quantity)) * fl_mrp;
                                Total = Total + multiply;
                                edt_gross.setText(String.valueOf(Total));
                            } else {
                                int_quantity = Integer.parseInt(edt_qty
                                        .getText().toString().trim());
                                int_mrp = Integer.parseInt(tv_mrp.getText()
                                        .toString());
                                int multiply = int_quantity * int_mrp;
                                total = total + multiply;
                                edt_gross.setText(String.valueOf(total));
                            }
                        }

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                else {
                }
            }
        });

        edt_net.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                try {
                    if (hasFocus) {
                        if (role.equalsIgnoreCase("DUB")) {
                            if (edt_gross.getText().toString().equals("")) {

                            } else if (!edt_gross.getText().toString().equals("")) {

                                Float gross = Float.parseFloat(edt_gross.getText().toString());
                                edt_net.setText(String.valueOf(gross));

                            }
                        }else {
                            if (edt_gross.getText().toString().equals("")) {

                            } else if (edt_discount.getText().toString().equals("")) {
                                edt_net.setText(edt_gross.getText().toString());
                            } else if (!edt_gross.getText().toString().equals("")
                                    && !edt_discount.getText().toString().equals("")) {

                                int gross = Integer.parseInt(edt_gross.getText().toString());
                                int discount = Integer.parseInt(edt_discount.getText().toString());
                                String str_net = String.valueOf(gross - discount);
                                edt_net.setText(str_net);

                            }
                        }
                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
        if (!role.equalsIgnoreCase("DUB")) {
            edt_discount.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub

                    edt_net.setText("");
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

    }

    public boolean validateEdit(EditText edt, String errorString,
                                String valueString) {

        Boolean result = false;

        if (valueString != null) {
            if (!valueString.equals("")) {
                if (!valueString.equalsIgnoreCase(" ")) {
                    if (!valueString.equalsIgnoreCase("0")) {
                        if (Integer.parseInt(edt.getText().toString()) > Integer
                                .parseInt(valueString)) {
                            result = false;
                            ecolor = Color.RED; // whatever color you want
                            namestring = errorString;
                            fgcspan = new ForegroundColorSpan(ecolor);
                            ssbuilder = new SpannableStringBuilder(namestring);
                            ssbuilder.setSpan(fgcspan, 0, namestring.length(),
                                    0);
                            edt.setError(ssbuilder);
                        } else {
                            result = true;
                        }
                    } else {
                        result = false;
                        ecolor = Color.RED; // whatever color you want
                        namestring = "No Stock Available";
                        fgcspan = new ForegroundColorSpan(ecolor);
                        ssbuilder = new SpannableStringBuilder(namestring);
                        ssbuilder.setSpan(fgcspan, 0, namestring.length(), 0);
                        edt.setError(ssbuilder);
                    }
                } else {
                    result = false;
                    ecolor = Color.RED; // whatever color you want
                    namestring = "No Stock Available";
                    fgcspan = new ForegroundColorSpan(ecolor);
                    ssbuilder = new SpannableStringBuilder(namestring);
                    ssbuilder.setSpan(fgcspan, 0, namestring.length(), 0);
                    edt.setError(ssbuilder);
                }
            } else {
                result = false;
                ecolor = Color.RED; // whatever color you want
                namestring = "No Stock Available";
                ;
                fgcspan = new ForegroundColorSpan(ecolor);
                ssbuilder = new SpannableStringBuilder(namestring);
                ssbuilder.setSpan(fgcspan, 0, namestring.length(), 0);
                edt.setError(ssbuilder);
            }
        } else {
            result = false;
            ecolor = Color.RED; // whatever color you want
            namestring = errorString;
            fgcspan = new ForegroundColorSpan(ecolor);
            ssbuilder = new SpannableStringBuilder(namestring);
            ssbuilder.setSpan(fgcspan, 0, namestring.length(), 0);
            edt.setError(ssbuilder);
        }
        return result;
    }

    public static String between(Date date) {
        String bb = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            Calendar c = Calendar.getInstance();

            int dayofyear = c.get(Calendar.YEAR);

            // Toast.makeText(context, "year="+dayofyear,
            // Toast.LENGTH_LONG).show();

            Date BOC12 = null;
            Date BOC12a = null;

            Date BOC1 = null;
            Date BOC1a = null;

            Date BOC2 = null;
            Date BOC2a = null;

            Date BOC3 = null;
            Date BOC3a = null;

            Date BOC4 = null;
            Date BOC4a = null;

            Date BOC5 = null;
            Date BOC5a = null;

            Date BOC6 = null;
            Date BOC6a = null;

            Date BOC7 = null;
            Date BOC7a = null;

            Date BOC8 = null;
            Date BOC8a = null;

            Date BOC9 = null;
            Date BOC9a = null;

            Date BOC10 = null;
            Date BOC10a = null;

            Date BOC11 = null;
            Date BOC11a = null;

            String Boc12 = "26/02/" + dayofyear;
            String Boc12a = "25/03/" + dayofyear;

            String Boc1 = "26/03/" + dayofyear;
            String Boc1a = "25/04/" + dayofyear;

            String Boc2 = "26/04/" + dayofyear;
            String Boc2a = "25/05/" + dayofyear;

            String Boc3 = "26/05/" + dayofyear;
            String Boc3a = "25/06/" + dayofyear;

            String Boc4 = "26/06/" + dayofyear;
            String Boc4a = "25/07/" + dayofyear;

            String Boc5 = "26/07/" + dayofyear;
            String Boc5a = "25/08/" + dayofyear;

            String Boc6 = "26/08/" + dayofyear;
            String Boc6a = "25/09/" + dayofyear;

            String Boc7 = "26/09/" + dayofyear;
            String Boc7a = "25/10/" + dayofyear;

            String Boc8 = "26/10/" + dayofyear;
            String Boc8a = "25/11/" + dayofyear;

            String Boc9 = "26/11/" + dayofyear;
            String Boc9a = "25/12/" + dayofyear;

            String Boc10 = "26/12/" + dayofyear;

            String Boc10a = "25/01/" + dayofyear + 1;

            String Boc11 = "26/01/" + dayofyear + 1;
            String Boc11a = "25/02/" + dayofyear + 1;

            try {
                BOC12 = sdf.parse(Boc12);
                BOC12a = sdf.parse(Boc12a);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                BOC1 = sdf.parse(Boc1);
                BOC1a = sdf.parse(Boc1a);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                BOC2 = sdf.parse(Boc2);
                BOC2a = sdf.parse(Boc2a);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                BOC3 = sdf.parse(Boc3);
                BOC3a = sdf.parse(Boc3a);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                BOC4 = sdf.parse(Boc4);
                BOC4a = sdf.parse(Boc4a);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                BOC5 = sdf.parse(Boc5);
                BOC5a = sdf.parse(Boc5a);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                BOC6 = sdf.parse(Boc6);
                BOC6a = sdf.parse(Boc6a);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                BOC7 = sdf.parse(Boc7);
                BOC7a = sdf.parse(Boc7a);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                BOC8 = sdf.parse(Boc8);
                BOC8a = sdf.parse(Boc8a);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                BOC9 = sdf.parse(Boc9);
                BOC9a = sdf.parse(Boc9a);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                BOC10 = sdf.parse(Boc10);
                BOC10a = sdf.parse(Boc10a);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                BOC11 = sdf.parse(Boc11);
                BOC11a = sdf.parse(Boc11a);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (date.after(BOC12) && date.before(BOC12a) || date.equals(BOC12a)
                    || date.equals(BOC12)) {

                bb = "BOC12";
                // Toast.makeText(context, "boc12 = "+bb,
                // Toast.LENGTH_LONG).show();
            }

            if (date.after(BOC1) && date.before(BOC1a) || date.equals(BOC1a)
                    || date.equals(BOC1)) {

                bb = "BOC1";
                // Toast.makeText(context, "boc1 = "+bb,
                // Toast.LENGTH_LONG).show();
            }
            if (date.after(BOC2) && date.before(BOC2a) || date.equals(BOC2a)
                    || date.equals(BOC2)) {

                bb = "BOC2";
                // Toast.makeText(context, "boc2 = "+bb,
                // Toast.LENGTH_LONG).show();
            }
            if (date.after(BOC3) && date.before(BOC3a) || date.equals(BOC3a)
                    || date.equals(BOC3)) {

                bb = "BOC3";
                // Toast.makeText(context, "boc3 = "+bb,
                // Toast.LENGTH_LONG).show();
            }
            if (date.after(BOC4) && date.before(BOC4a) || date.equals(BOC4a)
                    || date.equals(BOC4)) {

                bb = "BOC4";
                // Toast.makeText(context, "boc4 = "+bb,
                // Toast.LENGTH_LONG).show();
            }
            if (date.after(BOC5) && date.before(BOC5a) || date.equals(BOC5a)
                    || date.equals(BOC5)) {

                bb = "BOC5";
                // Toast.makeText(context, "boc5 = "+bb,
                // Toast.LENGTH_LONG).show();
            }
            if (date.after(BOC6) && date.before(BOC6a) || date.equals(BOC6a)
                    || date.equals(BOC6)) {

                bb = "BOC6";
                // Toast.makeText(context, "boc6 = "+bb,
                // Toast.LENGTH_LONG).show();
            }

            if (date.after(BOC7) && date.before(BOC7a) || date.equals(BOC7a)
                    || date.equals(BOC7)) {

                bb = "BOC7";
                // Toast.makeText(context, "boc7 = "+bb,
                // Toast.LENGTH_LONG).show();
            }
            if (date.after(BOC8) && date.before(BOC8a) || date.equals(BOC8a)
                    || date.equals(BOC8)) {

                bb = "BOC8";
                // Toast.makeText(context, "boc8 = "+bb,
                // Toast.LENGTH_LONG).show();
            }
            if (date.after(BOC9) && date.before(BOC9a) || date.equals(BOC9a)
                    || date.equals(BOC9)) {

                bb = "BOC10";
                // Toast.makeText(context, "boc10 = "+bb,
                // Toast.LENGTH_LONG).show();
            }
            if (date.after(BOC10) && date.before(BOC10a) || date.equals(BOC10a)
                    || date.equals(BOC10)) {

                bb = "BOC10";
                // Toast.makeText(context, "boc10 = "+bb,
                // Toast.LENGTH_LONG).show();
            }
            if (date.after(BOC11) && date.before(BOC11a) || date.equals(BOC11a)
                    || date.equals(BOC11)) {

                bb = "BOC11";
                // Toast.makeText(context, "boc11 = "+bb,
                // Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bb;
    }

    public String getLastInsertIDofStock1(String catid, String dbid,
                                          String eanid, String PRICE) {

        String catid1 = "", eanid1 = "", dbid1 = "", closebal = "", totalamount = "", sold = "", retn = "", retn_n_salebale = "", total_net_amt = "", price = "";
        ;
        String last_modified_date;
        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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
//
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

    @SuppressLint("SimpleDateFormat")
    public String getLastInsertIDofStockdubai(String dbid, String outletcode) {

        String closebal = "", retn = "";
        db.open();
        mCursor = db.getuniquedatadubai(dbid, outletcode);

        if (mCursor != null) {

            if (mCursor.moveToFirst()) {

                do {

                    closebal = mCursor.getString(mCursor
                            .getColumnIndex("close_bal"));

                    old_stock_recive = mCursor.getString(mCursor
                            .getColumnIndex("stock_received"));


                    str_stockinhand = mCursor.getString(mCursor
                            .getColumnIndex("stock_in_hand"));

                    str_openingstock = mCursor.getString(mCursor
                            .getColumnIndex("opening_stock"));

                } while (mCursor.moveToNext());

                sclo = "" + closebal;

            } else {
                sclo = "0";
            }

        }
        db.close();

        return closebal;
    }

    private void saveMethodForLHR() {
        if (cd.isCurrentDateMatchDeviceDate()) {
            try {

                int count = 0;

                int etcount = 0;
                if (tl_sale_calculation.getChildCount() != 1) {
                    for (int i = 0; i < tl_sale_calculation.getChildCount() - 1; i++) {

                        TableRow t = (TableRow) tl_sale_calculation
                                .getChildAt(i + 1);
                        EditText edt_qty = (EditText) t.getChildAt(1);

                        if (edt_qty.getText().toString().trim()
                                .equalsIgnoreCase("0")
                                || edt_qty.getText().toString().trim()
                                .equalsIgnoreCase("")
                                || edt_qty.getText().toString().trim()
                                .equalsIgnoreCase(" ")
                                || Integer.parseInt(edt_qty.getText()
                                .toString().trim()) <= 0) {

                        } else {

                            etcount++;
                        }

                    }
                }

                Log.e("etcount", String.valueOf(etcount));
                int numberofproduct = (tl_sale_calculation.getChildCount() - 1);

                if (numberofproduct == etcount) {

                    if (!edt_gross.getText().toString().equals("")
                            && !edt_net.getText().toString().equals("")) {
                        float dis;
                        if (!edt_discount.getText().toString()
                                .equalsIgnoreCase(" ")
                                || !edt_discount.getText().toString()
                                .equalsIgnoreCase("")) {
                            dis = Integer.parseInt(edt_discount.getText()
                                    .toString());
                        } else {
                            dis = 0;
                        }

                        String tt = String.valueOf(tl_sale_calculation
                                .getChildCount());

                        float ttt = Float.parseFloat(tt);

                        float a1 = dis / (ttt - 1);

                        String adis = String.format("%.02f", a1);
                        float a = Float.parseFloat(adis);
                        float disss = 0;
                        float net1;

                        if (tl_sale_calculation.getChildCount() != 1) {
                            for (int i = 0; i < tl_sale_calculation
                                    .getChildCount() - 1; i++) {

                                TableRow t = (TableRow) tl_sale_calculation
                                        .getChildAt(i + 1);
                                EditText edt_qty = (EditText) t
                                        .getChildAt(1);
                                TextView tv_mrp = (TextView) t
                                        .getChildAt(2);
                                TextView tv_dbID = (TextView) t
                                        .getChildAt(3);
                                TextView tv_shadeno = (TextView) t
                                        .getChildAt(4);

                                // ---------
                                String shaddd = tv_shadeno.getText()
                                        .toString().trim();

                                if (shaddd != null) {

                                    if (shaddd.equalsIgnoreCase(" ")
                                            || shaddd.equalsIgnoreCase("")) {
                                        shaddd = "0";
                                    }

                                } else {
                                    shaddd = "0";

                                }
                                // ---------

                                int calc_gross = Integer.parseInt(tv_mrp
                                        .getText().toString())
                                        * Integer.parseInt(edt_qty
                                        .getText().toString());

                                float boc_date_net = calc_gross - a;

                                int gross = 0, net = 0, closing = 0, sold_stock = 0, discount = 0;
                                int stkinhand = 0;
                                int i_sold = 0;
                                db.open();
                                Cursor c = db.fetchallSpecifyMSelect(
                                        "stock", new String[]{
                                                "total_gross_amount",
                                                "total_net_amount",
                                                "discount", "close_bal",
                                                "sold_stock",
                                                "opening_stock",
                                                "stock_received",
                                                "return_saleable",
                                                "stock_in_hand",
                                                "return_non_saleable", "price"},
                                        "db_id = '"
                                                + tv_dbID.getText()
                                                .toString() + "'",
                                        null, null);
                                if (c != null && c.getCount() > 0) {
                                    c.moveToFirst();

                                    // opening stock
                                    Log.e("opening stock", c.getString(3));
                                    boolean boo = validateEdit(
                                            edt_qty,
                                            "Quantity is greater than available stock",
                                            c.getString(3));

                                    if (boo == true) {

                                        if (c.getString(4) != null) {
                                            if (c.getString(4).trim()
                                                    .equalsIgnoreCase("0")
                                                    || c.getString(4)
                                                    .trim()
                                                    .equalsIgnoreCase(
                                                            "")) {

                                                i_sold = 0;

                                            } else {

                                                i_sold = Integer.parseInt(c
                                                        .getString(4)
                                                        .trim());
                                            }
                                        }

                                        Log.e("old sold", String.valueOf(i_sold));

                                        i_sold = i_sold
                                                + Integer.parseInt(edt_qty
                                                .getText()
                                                .toString());

                                        Log.e("new sold", String.valueOf(i_sold));

                                        int i_stokinhand = 0;

                                        if (c.getString(8) != null) {
                                            if (c.getString(8).trim()
                                                    .equalsIgnoreCase("0")
                                                    || c.getString(8)
                                                    .trim()
                                                    .equalsIgnoreCase(
                                                            "")) {

                                                i_stokinhand = 0;

                                            } else {

                                                i_stokinhand = Integer
                                                        .parseInt(c
                                                                .getString(
                                                                        8)
                                                                .trim());
                                            }
                                        }


                                        Log.e("i_stokinhand", String.valueOf(i_stokinhand));

                                        /* ..............  New changes ...........................*/
                                        int i_return_customer = 0;

                                        if (c.getString(7) != null) {
                                            if (c.getString(7).trim()
                                                    .equalsIgnoreCase("0")
                                                    || c.getString(7).trim().equalsIgnoreCase("")) {

                                                i_return_customer = 0;

                                            } else {

                                                i_return_customer = Integer.parseInt(c.getString(7).trim());
                                            }
                                        }


                                        Log.e("i_return_customer", String.valueOf(i_return_customer));

                                        int i_return_company = 0;

                                        if (c.getString(9) != null) {
                                            if (c.getString(9).trim()
                                                    .equalsIgnoreCase("0")
                                                    || c.getString(9).trim().equalsIgnoreCase("")) {

                                                i_return_company = 0;

                                            } else {

                                                i_return_company = Integer.parseInt(c.getString(9).trim());
                                            }
                                        }


                                        Log.e("i_return_company", String.valueOf(i_return_company));

                                        /*...................End...................*/

                                        //New changes
                                        int i_clstk = i_stokinhand - i_sold + i_return_customer - i_return_company;

                                        //old apk 2.7
                                        // int i_clstk = i_stokinhand - i_sold;

                                        Log.e("i_clstk", String.valueOf(i_clstk));

                                        if (c.getString(0) != null) {
                                            if (!c.getString(0)
                                                    .equalsIgnoreCase("")) {

                                                if (!c.getString(0)
                                                        .equalsIgnoreCase(
                                                                " ")) {
                                                    int total_gross = Integer.parseInt(c
                                                            .getString(0));

                                                    gross = total_gross + calc_gross;

                                                } else {
                                                    gross = calc_gross;

                                                }

                                            } else {
                                                gross = calc_gross;

                                            }
                                        } else {
                                            gross = calc_gross;

                                        }

                                        if (c.getString(2) != null) {
                                            if (!c.getString(2)
                                                    .equalsIgnoreCase("")) {

                                                if (!c.getString(2)
                                                        .contains(" ")) {

                                                    disss = (Float.parseFloat(c
                                                            .getString(2)) + a);

                                                } else {
                                                    if (edt_discount
                                                            .getText()
                                                            .toString()
                                                            .equals("")) {
                                                        // discount = 0;//
                                                        disss = 0;
                                                    } else {

                                                        disss = a;

                                                    }

                                                }

                                            } else {
                                                if (edt_discount.getText()
                                                        .toString()
                                                        .equals("")) {

                                                    disss = 0;
                                                } else {

                                                    disss = 0;

                                                }
                                            }
                                        } else {
                                            if (edt_discount.getText()
                                                    .toString().equals("")) {
                                                // discount = 0;//
                                                disss = 0;
                                            } else {

                                                disss = a;
                                            }
                                        }

                                        if (c.getString(1) != null) {
                                            if (!c.getString(1)
                                                    .equalsIgnoreCase("")) {

                                                if (!c.getString(1)
                                                        .contains(" ")) {

                                                    String cal_gross = String
                                                            .valueOf(calc_gross);

                                                    net1 = Float.parseFloat(c
                                                            .getString(1))
                                                            + Float.parseFloat(cal_gross)
                                                            - a;

                                                } else {
                                                    String cal_gross = String
                                                            .valueOf(calc_gross);

                                                    net1 = (Float
                                                            .parseFloat(cal_gross) - a);

                                                }

                                            } else {
                                                String cal_gross = String
                                                        .valueOf(calc_gross);

                                                net1 = (Float
                                                        .parseFloat(cal_gross) - a);
                                            }

                                        } else {
                                            String cal_gross = String
                                                    .valueOf(calc_gross);

                                            net1 = (Float
                                                    .parseFloat(cal_gross) - a);
                                        }

                                        Calendar cal = Calendar
                                                .getInstance();
                                        SimpleDateFormat month_date = new SimpleDateFormat(
                                                "MMMM");
                                        String month_name = month_date
                                                .format(cal.getTime());

                                        Calendar cal1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat year_format = new SimpleDateFormat(
                                                "yyyy");
                                        String year_name = year_format
                                                .format(cal1.getTime());

                                        Calendar cal2 = Calendar
                                                .getInstance();
                                        SimpleDateFormat sdf = new SimpleDateFormat(
                                                "yyyy-MM-dd HH:mm:ss");
                                        String insert_timestamp = sdf
                                                .format(cal2.getTime());

                                        String[] insert_timestamps = insert_timestamp
                                                .split(" ");

                                        String check_timestamp = insert_timestamps[0];

                                        boolean bool = db
                                                .update(tv_dbID.getText()
                                                                .toString(),
                                                        new String[]{
                                                                shaddd,
                                                                String.valueOf(i_clstk),
                                                                String.valueOf(gross),
                                                                String.valueOf(disss),
                                                                String.valueOf(net1),
                                                                String.valueOf(i_sold),
                                                                "0",
                                                                month_name,
                                                                year_name,
                                                                insert_timestamp,
                                                                insert_timestamp,
                                                                "s"},
                                                        new String[]{
                                                                "shadeNo",
                                                                "close_bal",
                                                                "total_gross_amount",
                                                                "discount",
                                                                "total_net_amount",
                                                                "sold_stock",
                                                                "savedServer",
                                                                "month",
                                                                "year",
                                                                "updateDate",
                                                                "insert_date",
                                                                "flag"},
                                                        "stock", "db_id");

                                        Cursor mCursor1;
                                        // db.open();
                                        mCursor1 = db.fetchone_Boc_wise(
                                                tv_dbID.getText()
                                                        .toString(),
                                                check_timestamp);

                                        // db.close();
                                        SimpleDateFormat sdf123 = new SimpleDateFormat(
                                                "dd/MM/yyyy");
                                        String currentDateandTime = sdf123
                                                .format(new Date())
                                                .toString();

                                        Date cur = null;
                                        try {
                                            cur = sdf123
                                                    .parse(currentDateandTime
                                                            .trim());
                                        } catch (ParseException e) {
                                            // TODO Auto-generated catch
                                            // block
                                            e.printStackTrace();
                                        }
                                        String boc = between(cur);
                                        if (mCursor1.getCount() == 0) {
                                            try {
                                                Cursor mCursor12;
                                                // db.open();
                                                mCursor12 = db
                                                        .getdata_StockForBoc(tv_dbID
                                                                .getText()
                                                                .toString());

                                                // db.close();

                                                if (mCursor12 != null) {
                                                    if (mCursor12
                                                            .getCount() > 0) {

                                                        mCursor12
                                                                .moveToFirst();

                                                        String product_id = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("product_id"));
                                                        String db_id = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("db_id"));
                                                        String eancode = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("eancode"));
                                                        String product_category = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("product_category"));
                                                        String product_type = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("product_type"));
                                                        String product_name = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("product_name"));
                                                        String size = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("size"));
                                                        String price = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("price"));
                                                        String emp_id = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("emp_id"));
                                                        String opening_stock = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("opening_stock"));
                                                        String stock_received = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("stock_received"));
                                                        String stock_in_hand = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("stock_in_hand"));

                                                        String return_saleable = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("return_saleable"));
                                                        String return_non_saleable = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("return_non_saleable"));

                                                        String shadeNo = mCursor12
                                                                .getString(mCursor12
                                                                        .getColumnIndex("shadeNo"));
                                                        db.insertSaleCalcuationForDashboard(
                                                                product_id,
                                                                db_id,
                                                                eancode,
                                                                product_category,
                                                                product_type,
                                                                product_name,
                                                                size,
                                                                price,
                                                                emp_id,
                                                                opening_stock,
                                                                stock_received,
                                                                stock_in_hand,
                                                                return_saleable,
                                                                return_non_saleable,
                                                                String.valueOf(closing),
                                                                String.valueOf(gross),
                                                                String.valueOf(disss),
                                                                String.valueOf(boc_date_net),
                                                                String.valueOf(sold_stock),
                                                                month_name,
                                                                year_name,
                                                                insert_timestamp,
                                                                insert_timestamp,
                                                                check_timestamp,
                                                                boc,
                                                                shadeNo);

                                                    }

                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {

                                            db.updateSaleCalcuationForDashboard(
                                                    tv_dbID.getText()
                                                            .toString(),
                                                    check_timestamp,
                                                    new String[]{
                                                            String.valueOf(closing),
                                                            String.valueOf(gross),
                                                            String.valueOf(disss),
                                                            String.valueOf(net1),
                                                            String.valueOf(sold_stock),
                                                            String.valueOf(closing),
                                                            String.valueOf(closing),
                                                            "0",
                                                            "0",
                                                            month_name,
                                                            year_name,
                                                            insert_timestamp,
                                                            insert_timestamp,
                                                            check_timestamp,
                                                            boc});

                                        }
                                        if (bool == true) {
                                            count++;
                                        }
                                    }
                                } else {
                                    Toast.makeText(SaleCalculation.this,
                                            "Stock not available",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }

                            if (count == tl_sale_calculation.getChildCount() - 1) {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        SaleCalculation.this);

                                // set title
                                alertDialogBuilder
                                        .setTitle("Saved Successfully!!");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("Go  TO  :")
                                        .setCancelable(false)

                                        .setNegativeButton(
                                                "Sale Page",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int id) {

                                                        dialog.cancel();
                                                        finish();
                                                        startActivity(new Intent(
                                                                SaleCalculation.this,
                                                                SaleNewActivity.class));

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
                                                                SaleCalculation.this,
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
                                        SaleCalculation.this);

                                // set title
                                alertDialogBuilder
                                        .setTitle("Data Not Saved!!!");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage(
                                                "Please check the available stock for specified products")
                                        .setCancelable(false)

                                        .setNegativeButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int id) {

                                                        dialog.cancel();
                                                        finish();
                                                        startActivity(new Intent(
                                                                SaleCalculation.this,
                                                                SaleNewActivity.class));

                                                    }
                                                });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder
                                        .create();

                                // show it
                                alertDialog.show();
                            }

                            //

                        }

                    }

                } else {
                    // mProgress.dismiss();
                    Toast.makeText(
                            SaleCalculation.this,
                            "Please enter valid value in quantity fields",
                            Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                Toast.makeText(
                        SaleCalculation.this,
                        "Please fill up all valid value in quantity fields",
                        Toast.LENGTH_LONG).show();

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(SaleCalculation.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

        }

    }

    private void saveMethodForFLR() {

        if (cd.isCurrentDateMatchDeviceDate()) {
            try {

                int count = 0;

                int etcount = 0;
                if (tl_sale_calculation.getChildCount() != 1) {
                    for (int i = 0; i < tl_sale_calculation.getChildCount() - 1; i++) {

                        TableRow t = (TableRow) tl_sale_calculation
                                .getChildAt(i + 1);
                        EditText edt_qty = (EditText) t.getChildAt(1);

                        if (edt_qty.getText().toString().trim()
                                .equalsIgnoreCase("0")
                                || edt_qty.getText().toString().trim()
                                .equalsIgnoreCase("")
                                || edt_qty.getText().toString().trim()
                                .equalsIgnoreCase(" ")) {

                        } else {

                            etcount++;
                        }

                    }
                }

                Log.e("etcount", String.valueOf(etcount));
                int numberofproduct = (tl_sale_calculation.getChildCount() - 1);

                if (numberofproduct == etcount) {

                    if (!edt_gross.getText().toString().equals("")
                            && !edt_net.getText().toString().equals("")) {
                        float dis;
                        if (!edt_discount.getText().toString()
                                .equalsIgnoreCase(" ")
                                || !edt_discount.getText().toString()
                                .equalsIgnoreCase("")) {
                            dis = Integer.parseInt(edt_discount.getText()
                                    .toString());
                        } else {
                            dis = 0;
                        }

                        String tt = String.valueOf(tl_sale_calculation
                                .getChildCount());

                        float ttt = Float.parseFloat(tt);

                        float a1 = dis / (ttt - 1);

                        String adis = String.format("%.02f", a1);
                        float a = Float.parseFloat(adis);
                        float disss = 0;
                        float net1;

                        if (tl_sale_calculation.getChildCount() != 1) {
                            for (int i = 0; i < tl_sale_calculation
                                    .getChildCount() - 1; i++) {

                                TableRow t = (TableRow) tl_sale_calculation
                                        .getChildAt(i + 1);
                                EditText edt_qty = (EditText) t
                                        .getChildAt(1);
                                TextView tv_mrp = (TextView) t
                                        .getChildAt(2);
                                TextView tv_dbID = (TextView) t
                                        .getChildAt(3);
                                TextView tv_shadeno = (TextView) t
                                        .getChildAt(4);

                                // ---------
                                String shaddd = tv_shadeno.getText().toString().trim();

                                String product_category = SaleActivityForFloter.selected_product_category;
                                String product_type = SaleActivityForFloter.selected_product_type;
                                String product_name = pro_name[i];
                                String emp_id = shp.getString("username", "");
                                String price = tv_mrp.getText().toString().trim();
                                String size1 = "" + size[i];
                                String eancode;
                                if (enacod != null) {
                                    eancode = "" + enacod[i];
                                } else {
                                    eancode = "0";
                                }
                                String db_id1 = tv_dbID.getText().toString().trim();
                                String cat_id;
                                if (catid != null) {
                                    cat_id = "" + catid[i];
                                } else {
                                    cat_id = "0";
                                }


                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss");
                                String insert_timestamp = sdf.format(c
                                        .getTime());

                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat month_date = new SimpleDateFormat(
                                        "MMMM");
                                String month_name = month_date.format(cal
                                        .getTime());

                                Calendar cal1 = Calendar.getInstance();
                                SimpleDateFormat year_format = new SimpleDateFormat(
                                        "yyyy");
                                String year_name = year_format.format(cal1
                                        .getTime());

                                if (shaddd != null) {

                                    if (shaddd.equalsIgnoreCase(" ")
                                            || shaddd.equalsIgnoreCase("")) {
                                        shaddd = "0";
                                    }

                                } else {
                                    shaddd = "0";

                                }
                                // ---------

                                int calc_gross = Integer.parseInt(tv_mrp.getText().toString())
                                        * Integer.parseInt(edt_qty.getText().toString());

                                float boc_date_net = calc_gross - a;

                                int gross = 0, net = 0, closing = 0, sold_stock = 0, discount = 0;
                                int stkinhand = 0;
                                int i_sold = 0;


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

                                            str_discount = "0.0";
                                        }

                                    } else {
                                        str_discount = "0.0";
                                    }

                                } else {
                                    str_discount = "0.0";
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

                                int soldstock = Integer.parseInt(str_soldstock) + Integer.parseInt(edt_qty.getText().toString());

                                //int soldstock = Integer.parseInt(str_soldstock) - new_retrn_sale;

                                //int pricecustomer = Integer.parseInt(str_price) * new_retrn_sale;
                                int i_net_amt = 0;
                                if (str_price.equalsIgnoreCase("0")
                                        && str_soldstock.equalsIgnoreCase("0")) {
                                    i_net_amt = calc_gross;
                                } else {
                                    i_net_amt = Integer.parseInt(str_price) * soldstock;
                                }

                                float net_amt = Float.parseFloat(String.valueOf(i_net_amt)) - Float.parseFloat(str_discount);


                                if (mCursor.getCount() == 0) {

                                    db.open();
                                    db.InsertstockForFLR(
                                            cat_id,
                                            db_id1,
                                            eancode,
                                            product_category,
                                            product_type,
                                            product_name,
                                            size1,
                                            price,
                                            emp_id,
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            String.valueOf(soldstock),
                                            String.valueOf(i_net_amt),
                                            String.valueOf(net_amt),
                                            str_discount,
                                            insert_timestamp,
                                            shaddd,
                                            month_name,
                                            year_name,
                                            insert_timestamp,
                                            FLRCode);
                                    db.close();

                                    Toast.makeText(SaleCalculation.this,
                                            "Data save successfully",
                                            Toast.LENGTH_SHORT).show();

                                    count++;

                                } else {
                                    db.open();
                                    db.UpdateStockForFLR(
                                            "0",
                                            "0",
                                            "0",
                                            db_id1,
                                            insert_timestamp,
                                            String.valueOf(soldstock),
                                            "0",
                                            "0",
                                            String.valueOf(i_net_amt),
                                            String.valueOf(net_amt),
                                            str_discount,
                                            shaddd,
                                            insert_timestamp,
                                            month_name,
                                            year_name);
                                    db.close();

                                    // Toast.makeText(StockAllActivity.this,
                                    // "Data save successfully",
                                    // Toast.LENGTH_SHORT).show();
                                    count++;
                                }

                            }

                            if (count == tl_sale_calculation.getChildCount() - 1) {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        SaleCalculation.this);

                                // set title
                                alertDialogBuilder
                                        .setTitle("Saved Successfully!!");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("Go  TO  :")
                                        .setCancelable(false)

                                        .setNegativeButton(
                                                "Sale Page",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int id) {

                                                        dialog.cancel();
                                                        finish();
                                                        startActivity(new Intent(
                                                                SaleCalculation.this,
                                                                SaleActivityForFloter.class));

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
                                                                SaleCalculation.this,
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
                                        SaleCalculation.this);

                                // set title
                                alertDialogBuilder
                                        .setTitle("Data Not Saved!!!");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage(
                                                "Data Not Saved")
                                        .setCancelable(false)

                                        .setNegativeButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int id) {

                                                        dialog.cancel();
                                                        finish();
                                                        startActivity(new Intent(
                                                                SaleCalculation.this,
                                                                SaleActivityForFloter.class));

                                                    }
                                                });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder
                                        .create();

                                // show it
                                alertDialog.show();
                            }

                            //

                        }

                    }

                } else {
                    // mProgress.dismiss();
                    Toast.makeText(
                            SaleCalculation.this,
                            "Please enter valid value in quantity fields",
                            Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                Toast.makeText(
                        SaleCalculation.this,
                        "Please fill up all valid value in quantity fields",
                        Toast.LENGTH_LONG).show();

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(SaleCalculation.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

        }

    }

    private void saveMethodForDubai() {
        if (cd.isCurrentDateMatchDeviceDate()) {
            try {

                int count = 0;

                int etcount = 0;
                if (tl_sale_calculation.getChildCount() != 1) {
                    for (int i = 0; i < tl_sale_calculation.getChildCount() - 1; i++) {

                        TableRow t = (TableRow) tl_sale_calculation
                                .getChildAt(i + 1);
                        EditText edt_qty = (EditText) t.getChildAt(1);

                        if (edt_qty.getText().toString().trim()
                                .equalsIgnoreCase("0")
                                || edt_qty.getText().toString().trim()
                                .equalsIgnoreCase("")
                                || edt_qty.getText().toString().trim()
                                .equalsIgnoreCase(" ")
                                || Integer.parseInt(edt_qty.getText()
                                .toString().trim()) <= 0) {

                        } else {

                            etcount++;
                        }

                    }
                }

                Log.e("etcount", String.valueOf(etcount));
                int numberofproduct = (tl_sale_calculation.getChildCount() - 1);

                if (numberofproduct == etcount) {

                    if (!edt_gross.getText().toString().equals("")
                            && !edt_net.getText().toString().equals("")) {
                        float dis = 0;
                       /* if (!edt_discount.getText().toString()
                                .equalsIgnoreCase(" ")
                                || !edt_discount.getText().toString()
                                .equalsIgnoreCase("")) {
                            dis = Integer.parseInt(edt_discount.getText()
                                    .toString());
                        } else {
                            dis = 0;
                        }*/

                        String tt = String.valueOf(tl_sale_calculation
                                .getChildCount());

                        float ttt = Float.parseFloat(tt);

                        float a1 = dis / (ttt - 1);

                        String adis = String.format("%.02f", a1);
                        float a = Float.parseFloat(adis);
                        float disss = 0;
                        float net1;

                        if (tl_sale_calculation.getChildCount() != 1) {
                            for (int i = 0; i < tl_sale_calculation
                                    .getChildCount() - 1; i++) {

                                TableRow t = (TableRow) tl_sale_calculation.getChildAt(i + 1);
                                EditText edt_qty = (EditText) t.getChildAt(1);
                                TextView tv_mrp = (TextView) t.getChildAt(2);
                                TextView tv_dbID = (TextView) t.getChildAt(3);
                                TextView tv_shadeno = (TextView) t.getChildAt(4);

                                String shaddd = "0";

                                Float calc_gross = Float.parseFloat(tv_mrp.getText().toString())
                                        * Integer.parseInt(edt_qty.getText().toString());

                                float boc_date_net = calc_gross - a;
                                Float gross;
                                int net = 0, closing = 0, sold_stock = 0, discount = 0;
                                int stkinhand = 0;
                                int i_sold = 0;
                                db.open();
                                Cursor c = db.fetchallSpecifyMSelect(
                                        "stock", new String[]{
                                                "total_gross_amount",
                                                "total_net_amount",
                                                "discount", "close_bal",
                                                "sold_stock",
                                                "opening_stock",
                                                "stock_received",
                                                "return_saleable",
                                                "stock_in_hand",
                                                "return_non_saleable", "price"},
                                        "db_id = '"
                                                + tv_dbID.getText()
                                                .toString() + "'",
                                        null, null);
                                if (c != null && c.getCount() > 0) {
                                    c.moveToFirst();

                                    // opening stock
                                    Log.e("opening stock", c.getString(3));
                                    boolean boo = validateEdit(
                                            edt_qty,
                                            "Quantity is greater than available stock",
                                            c.getString(3));

                                    if (boo == true) {

                                        if (c.getString(4) != null) {
                                            if (c.getString(4).trim()
                                                    .equalsIgnoreCase("0")
                                                    || c.getString(4)
                                                    .trim()
                                                    .equalsIgnoreCase(
                                                            "")) {

                                                i_sold = 0;

                                            } else {

                                                i_sold = Integer.parseInt(c
                                                        .getString(4)
                                                        .trim());
                                            }
                                        }

                                        Log.e("old sold", String.valueOf(i_sold));

                                        i_sold = i_sold
                                                + Integer.parseInt(edt_qty
                                                .getText()
                                                .toString());

                                        Log.e("new sold", String.valueOf(i_sold));

                                        int i_stokinhand = 0;

                                        if (c.getString(8) != null) {
                                            if (c.getString(8).trim()
                                                    .equalsIgnoreCase("0")
                                                    || c.getString(8)
                                                    .trim()
                                                    .equalsIgnoreCase(
                                                            "")) {

                                                i_stokinhand = 0;

                                            } else {

                                                i_stokinhand = Integer
                                                        .parseInt(c
                                                                .getString(
                                                                        8)
                                                                .trim());
                                            }
                                        }


                                        Log.e("i_stokinhand", String.valueOf(i_stokinhand));

                                        /* ..............  New changes ...........................*/
                                        int i_return_customer = 0;

                                        if (c.getString(7) != null) {
                                            if (c.getString(7).trim()
                                                    .equalsIgnoreCase("0")
                                                    || c.getString(7).trim().equalsIgnoreCase("")) {

                                                i_return_customer = 0;

                                            } else {

                                                i_return_customer = Integer.parseInt(c.getString(7).trim());
                                            }
                                        }


                                        Log.e("i_return_customer", String.valueOf(i_return_customer));

                                        int i_return_company = 0;

                                        if (c.getString(9) != null) {
                                            if (c.getString(9).trim()
                                                    .equalsIgnoreCase("0")
                                                    || c.getString(9).trim().equalsIgnoreCase("")) {

                                                i_return_company = 0;

                                            } else {

                                                i_return_company = Integer.parseInt(c.getString(9).trim());
                                            }
                                        }


                                        Log.e("i_return_company", String.valueOf(i_return_company));

                                        /*...................End...................*/

                                        //New changes
                                        int i_clstk = i_stokinhand - i_sold + i_return_customer - i_return_company;

                                        //old apk 2.7
                                        // int i_clstk = i_stokinhand - i_sold;

                                        Log.e("i_clstk", String.valueOf(i_clstk));

                                        if (c.getString(0) != null) {
                                            if (!c.getString(0)
                                                    .equalsIgnoreCase("")) {

                                                if (!c.getString(0)
                                                        .equalsIgnoreCase(
                                                                " ")) {
                                                    Float total_gross = Float.parseFloat(c.getString(0));

                                                    gross = total_gross + calc_gross;

                                                } else {
                                                    gross = calc_gross;

                                                }

                                            } else {
                                                gross = calc_gross;

                                            }
                                        } else {
                                            gross = calc_gross;

                                        }

                                        if (c.getString(2) != null) {
                                            if (!c.getString(2)
                                                    .equalsIgnoreCase("")) {

                                                if (!c.getString(2)
                                                        .contains(" ")) {

                                                    disss = (Float.parseFloat(c
                                                            .getString(2)) + a);

                                                } else {

                                                        disss = a;
                                                }

                                            } else {

                                                    disss = 0;

                                            }
                                        } else {

                                            disss = a;

                                        }

                                        if (c.getString(1) != null) {
                                            if (!c.getString(1)
                                                    .equalsIgnoreCase("")) {

                                                if (!c.getString(1)
                                                        .contains(" ")) {

                                                    String cal_gross = String
                                                            .valueOf(calc_gross);

                                                    net1 = Float.parseFloat(c
                                                            .getString(1))
                                                            + Float.parseFloat(cal_gross)
                                                            - a;

                                                } else {
                                                    String cal_gross = String
                                                            .valueOf(calc_gross);

                                                    net1 = (Float
                                                            .parseFloat(cal_gross) - a);

                                                }

                                            } else {
                                                String cal_gross = String
                                                        .valueOf(calc_gross);

                                                net1 = (Float
                                                        .parseFloat(cal_gross) - a);
                                            }

                                        } else {
                                            String cal_gross = String
                                                    .valueOf(calc_gross);

                                            net1 = (Float
                                                    .parseFloat(cal_gross) - a);
                                        }

                                        Calendar cal = Calendar
                                                .getInstance();
                                        SimpleDateFormat month_date = new SimpleDateFormat(
                                                "MMMM");
                                        String month_name = month_date
                                                .format(cal.getTime());

                                        Calendar cal1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat year_format = new SimpleDateFormat(
                                                "yyyy");
                                        String year_name = year_format
                                                .format(cal1.getTime());

                                        Calendar cal2 = Calendar
                                                .getInstance();
                                        SimpleDateFormat sdf = new SimpleDateFormat(
                                                "yyyy-MM-dd HH:mm:ss");
                                        String insert_timestamp = sdf
                                                .format(cal2.getTime());

                                        String[] insert_timestamps = insert_timestamp
                                                .split(" ");

                                        String check_timestamp = insert_timestamps[0];

                                        boolean bool = db
                                                .update(tv_dbID.getText()
                                                                .toString(),
                                                        new String[]{
                                                                shaddd,
                                                                String.valueOf(i_clstk),
                                                                String.valueOf(String.format("%.2f", gross)),
                                                                String.valueOf(disss),
                                                                String.valueOf(String.format("%.2f", net1)),
                                                                String.valueOf(i_sold),
                                                                "0",
                                                                month_name,
                                                                year_name,
                                                                insert_timestamp,
                                                                insert_timestamp,
                                                                "s"},
                                                        new String[]{
                                                                "shadeNo",
                                                                "close_bal",
                                                                "total_gross_amount",
                                                                "discount",
                                                                "total_net_amount",
                                                                "sold_stock",
                                                                "savedServer",
                                                                "month",
                                                                "year",
                                                                "updateDate",
                                                                "insert_date",
                                                                "flag"},
                                                        "stock", "db_id");


                                        if (bool == true) {
                                            count++;
                                        }
                                    }
                                } else {
                                    Toast.makeText(SaleCalculation.this,
                                            "Stock not available",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }

                            if (count == tl_sale_calculation.getChildCount() - 1) {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        SaleCalculation.this);

                                // set title
                                alertDialogBuilder
                                        .setTitle("Saved Successfully!!");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("Go  TO  :")
                                        .setCancelable(false)

                                        .setNegativeButton(
                                                "Sale Page",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int id) {

                                                        dialog.cancel();
                                                        finish();
                                                        startActivity(new Intent(
                                                                SaleCalculation.this,
                                                                StockSaleActivityForDubai.class));

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
                                                                SaleCalculation.this,
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
                                        SaleCalculation.this);

                                // set title
                                alertDialogBuilder
                                        .setTitle("Data Not Saved!!!");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage(
                                                "Please check the available stock for specified products")
                                        .setCancelable(false)

                                        .setNegativeButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int id) {

                                                        dialog.cancel();
                                                        finish();
                                                        startActivity(new Intent(
                                                                SaleCalculation.this,
                                                                StockSaleActivityForDubai.class));

                                                    }
                                                });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder
                                        .create();

                                // show it
                                alertDialog.show();
                            }

                            //

                        }

                    }

                } else {
                    // mProgress.dismiss();
                    Toast.makeText(
                            SaleCalculation.this,
                            "Please enter valid value in quantity fields",
                            Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                Toast.makeText(
                        SaleCalculation.this,
                        "Please fill up all valid value in quantity fields",
                        Toast.LENGTH_LONG).show();

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(SaleCalculation.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

        }

    }

}
