package com.prod.sudesi.lotusherbalsnew;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
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

import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class FocusAllActivity extends Activity {

    TableLayout tl_sale_calculation;
    TableRow tr;
    EditText edt_target;
    Button btn_save, btn_home, btn_logout;
    int count = 0;
    Dbcon db;
    TextView tv_h_username;// -------
    String username, role, outletcode, bdename;
    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;
    static Context context;
    String enacod[], catid[], pro_name[], show_pro_name[], size[], db_id[], mrp[], shadeno[], singleoffer[];


    ScrollView scrv_sale;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_focus_all);
        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        tl_sale_calculation = (TableLayout) findViewById(R.id.tl_sale_calculation);

        context = getApplicationContext();
        cd = new ConnectionDetector(context);
        edt_target = (EditText) findViewById(R.id.edt_target);

        btn_save = (Button) findViewById(R.id.btn_sav);
        //btn_back = (Button) findViewById(R.id.btn_back);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        db = new Dbcon(FocusAllActivity.this);
        shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
        shpeditor = shp.edit();

        username = shp.getString("username", "");
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

        tv_h_username = (TextView) findViewById(R.id.tv_h_username);


        tv_h_username.setText(bdename);


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

            if (db_id[i].equalsIgnoreCase("0")) {

            } else {

                tr = new TableRow(this);
                tr.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                tr.setWeightSum(5f);

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
                        if (s.length() != 0 || s.length() == 0)
                            edt_target.setText("");
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

                tl_sale_calculation.addView(tr, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                tl_sale_calculation.setShrinkAllColumns(true);

            }

        }

        // -----------------------------shivani-----------------------------------------

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

                if (cd.isCurrentDateMatchDeviceDate()) {
                    try {

                        int etcount = 0;
                        int count = 0;
                        boolean negative = false;
                        if (tl_sale_calculation.getChildCount() != 1) {
                            for (int i = 0; i < tl_sale_calculation.getChildCount() - 1; i++) {

                                TableRow t = (TableRow) tl_sale_calculation
                                        .getChildAt(i + 1);
                                EditText edt_qty = (EditText) t.getChildAt(1);
                                TextView tv_dbID = (TextView) t.getChildAt(3);


                                if ((edt_qty.getText().toString().trim().equalsIgnoreCase("0")
                                        || edt_qty.getText().toString().trim().equalsIgnoreCase("")
                                        || edt_qty.getText().toString().trim().equalsIgnoreCase(" "))
                                        || edt_target.getText().toString().trim().equalsIgnoreCase("")) {
                                    Toast.makeText(getApplicationContext(),
                                            "Please Enter in All Fields",
                                            Toast.LENGTH_SHORT);
                                } else {

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

                                    Log.e("etcount", String.valueOf(etcount));

                                    if (edt_target.getText().toString().trim().equalsIgnoreCase("")) {
                                        Toast.makeText(getApplicationContext(), "Please Enter Target Amount", Toast.LENGTH_SHORT);
                                    }
                                }

                            }
                        }

                        int numberofproduct = (tl_sale_calculation.getChildCount() - 1);

                        if (numberofproduct == etcount) {
                            if (tl_sale_calculation.getChildCount() != 1) {
                                count = saveData();
                            }
                            /*if (count == tl_sale_calculation.getChildCount() - 1) {
                                Toast.makeText(FocusAllActivity.this, "Focus Data Saved Succesfully!!",
                                        Toast.LENGTH_LONG).show();

                                Intent i = new Intent(FocusAllActivity.this, FocusReportActivity.class);
                                startActivity(i);

                            } else {
                                Toast.makeText(FocusAllActivity.this, "Focus Data not Saved",
                                        Toast.LENGTH_LONG).show();
                            }*/

                        } else {

                            Toast.makeText(
                                    FocusAllActivity.this,
                                    "Please Enter all fields",
                                    Toast.LENGTH_LONG).show();

                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(FocusAllActivity.this,
                                "Please enter Only numbers", Toast.LENGTH_LONG)
                                .show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(FocusAllActivity.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

                }


            }
        });

        // ----------------------------------------------------------------------------------


        edt_target.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    try {
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
                                int_quantity = Integer.parseInt(edt_qty
                                        .getText().toString().trim());
                                int_mrp = Integer.parseInt(tv_mrp.getText()
                                        .toString());
                                int multiply = int_quantity * int_mrp;
                                total = total + multiply;
                                edt_target.setText(String.valueOf(total));
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
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(getApplicationContext(), FocusActivity.class);
        startActivity(i);
    }

    public int saveData() {

        String product_category[] = new String[tl_sale_calculation.getChildCount() - 1];
        String product_type1[] = new String[tl_sale_calculation.getChildCount() - 1];
        String product_name[] = new String[tl_sale_calculation.getChildCount() - 1];
        String price[] = new String[tl_sale_calculation.getChildCount() - 1];
        String insert_timestamp[] = new String[tl_sale_calculation.getChildCount() - 1];
        String db_id1[] = new String[tl_sale_calculation.getChildCount() - 1];
        String size1[] = new String[tl_sale_calculation.getChildCount() - 1];
        String target_qty[] = new String[tl_sale_calculation.getChildCount() - 1];
        String target_amt[] = new String[tl_sale_calculation.getChildCount() - 1];

        int count = 0;
        for (int i = 0; i < tl_sale_calculation.getChildCount() - 1; i++) {


            TableRow t = (TableRow) tl_sale_calculation
                    .getChildAt(i + 1);
            EditText edt_qty = (EditText) t.getChildAt(1);
            TextView tv_mrp = (TextView) t.getChildAt(2);
            TextView tv_dbID = (TextView) t.getChildAt(3);
            TextView tv_shadeno = (TextView) t
                    .getChildAt(4);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            insert_timestamp[i] = sdf.format(c.getTime());

            product_category[i] = FocusActivity.selected_product_category;

            product_type1[i] = FocusActivity.selected_product_type;

            product_name[i] = pro_name[i];// changed
            // 06.12.2014

            price[i] = mrp[i];

            db_id1[i] = db_id[i];

            size1[i] = "" + size[i];

            String QTY = edt_qty.getText().toString();
            String MRP = mrp[i];

            target_qty[i] = QTY;
            target_amt[i] = String.valueOf(Integer.parseInt(QTY) * Integer.parseInt(MRP));

            count++;

        }
            startActivity(new Intent(FocusAllActivity.this,FocusReportActivity.class)
                    .putExtra("db_id", db_id1)
                    .putExtra("proname", product_name)
                    .putExtra("proCategory", product_category)
                    .putExtra("protype", product_type1)
                    .putExtra("size", size1)
                    .putExtra("price", price)
                    .putExtra("target_qty", target_qty)
                    .putExtra("target_amt", target_amt)
                    .putExtra("username", username)
                    .putExtra("androiddate", insert_timestamp));

           /* db.open();
            db.InsertFocusReport(
                    db_id1,
                    product_type1,
                    product_category,
                    username,
                    product_name,
                    size1,
                    price,
                    target_qty,
                    target_amt,
                    insert_timestamp);
            db.close();
*/




        return count;

    }


}
