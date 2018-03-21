package com.prod.sudesi.lotusherbalsnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


public class StockNewActivity extends Activity implements OnClickListener {

    Spinner sp_product_category, sp_product_type, sp_product_mode;

    Button btn_proceed, btn_home, btn_logout;

    TableLayout tl_productList;

    TableRow tr_header;

    TextView tv_h_username;

    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;

    Dbcon db;

    ArrayList<String> productcategory = new ArrayList<String>();
    ArrayList<String> producttypeArray = new ArrayList<String>();
    ArrayList<HashMap<String, String[]>> productDetailsArray = new ArrayList<HashMap<String, String[]>>();

    ArrayList<String> arr_selectedDBids;

    public static String selected_product_category, selected_product_type,
            selected_product_name, selected_product_id1;

    private String[] arraySpinner;

    int modecounter = 0;
    public static String PMODE;

    String username, bdename;

    String sclo = "", mrpstring;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_stock);
        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        sp_product_category = (Spinner) findViewById(R.id.sp_product_category);
        sp_product_type = (Spinner) findViewById(R.id.sp_product_type);
        sp_product_mode = (Spinner) findViewById(R.id.sp_product_mode);
        tl_productList = (TableLayout) findViewById(R.id.tl_productList);
        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tr_header = (TableRow) findViewById(R.id.tr_header);
        tv_h_username = (TextView) findViewById(R.id.tv_h_username);

        btn_proceed.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        btn_logout.setOnClickListener(this);

        shp = getSharedPreferences("Lotus", MODE_PRIVATE);
        shpeditor = shp.edit();

        db = new Dbcon(this);
        db.open();

        username = shp.getString("username", "");
        bdename = shp.getString("BDEusername", "");
        tv_h_username.setText(bdename);

        String div = shp.getString("div", "");

        if (div.equalsIgnoreCase("LH & LHM") || div.equalsIgnoreCase("LH & LM")) {

            db.open();
            productcategory = db.getproductcategory1(); // ------------

            // System.out.println(productArray);
            Log.e("", "kkkklklk111");

        }
        if (div.equalsIgnoreCase("LH")) {
            productcategory.clear();
            productcategory.add("Select");
            productcategory.add("SKIN");

        }
        if (div.equalsIgnoreCase("LM")) {
            productcategory.clear();
            productcategory.add("Select");
            productcategory.add("COLOR");

        }

        ArrayAdapter<String> product_adapter = new ArrayAdapter<String>(
                // context, android.R.layout.simple_spinner_item,
                StockNewActivity.this, R.layout.custom_sp_item, productcategory);

        product_adapter
                // .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                .setDropDownViewResource(R.layout.custom_spinner_dropdown_text);

        sp_product_category.setAdapter(product_adapter);

        sp_product_category
                .setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        try {
                            // TODO Auto-generated method stub

                            selected_product_category = sp_product_category
                                    .getItemAtPosition(position).toString().trim();

                            if (selected_product_category
                                    .equalsIgnoreCase("Select")
                                    || selected_product_category
                                    .equalsIgnoreCase("")) {

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        StockNewActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        new String[]{});
                                sp_product_type.setAdapter(adapter);

                            } else {
                                db.open();
                                producttypeArray = db
                                        .getproductype1(selected_product_category); // -------------
                                System.out.println(producttypeArray);

                                ArrayAdapter<String> product_adapter1 = new ArrayAdapter<String>(
                                        // context,
                                        // android.R.layout.simple_spinner_item,
                                        StockNewActivity.this,
                                        R.layout.custom_sp_item, producttypeArray);

                                product_adapter1
                                        // .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        .setDropDownViewResource(R.layout.custom_spinner_dropdown_text);
                                sp_product_type.setAdapter(product_adapter1);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub

                    }
                });

        sp_product_type.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                selected_product_type = sp_product_type.getItemAtPosition(arg2)
                        .toString().trim();

                if (selected_product_type.equalsIgnoreCase("Select")
                        || selected_product_category.equalsIgnoreCase("")) {

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            StockNewActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            new String[]{});

                } else {
                    String selected_category = sp_product_category
                            .getSelectedItem().toString();
                    String selected_type = sp_product_type.getSelectedItem()
                            .toString();

                    Log.v("", "" + selected_category + " " + selected_type);
                    productDetailsArray.clear();
                    tl_productList.removeAllViews();
                    tl_productList.addView(tr_header);
                    getallproducts(selected_category, selected_type, "N");

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });


        this.arraySpinner = new String[]{
                "Select Mode", "Stock Received", "Return From Customer", "Return to Company"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                // context, android.R.layout.simple_spinner_item,
                StockNewActivity.this, R.layout.custom_sp_item, arraySpinner);
        ;
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_text);


        sp_product_mode.setAdapter(adapter);

        sp_product_mode.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                String s = sp_product_mode.getSelectedItem().toString();

                Log.e("s", s);
                if (s.equalsIgnoreCase("Select Mode")
                        || s.equalsIgnoreCase("")) {
                } else {
                    modecounter = 1;
                    PMODE = s;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_proceed:

                btn_proceed.setEnabled(false);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        btn_proceed.setEnabled(true);
                        Log.d(TAG, "resend1");

                    }
                }, 5000);// set time as per your requirement

                if (sp_product_mode.getSelectedItem().toString().equalsIgnoreCase("Return From Customer")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Selected Mode");
                    builder.setMessage("You select Return From Customer Do you want to proceed!!!")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    stockProceedData();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                public void onClick(

                                        DialogInterface dialog,
                                        int id) {

                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (sp_product_mode.getSelectedItem().toString().equalsIgnoreCase("Return to Company")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Selected Mode");
                    builder.setMessage("You select Return to Company Do you want to proceed!!!")
                            .setCancelable(false)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    stockProceedData();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                public void onClick(

                                        DialogInterface dialog,
                                        int id) {

                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {

                    stockProceedData();

                }


                break;

            case R.id.btn_home:

                Intent i = new Intent(getApplicationContext(),
                        DashboardNewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

                break;

            case R.id.btn_logout:

                Intent i1 = new Intent(getApplicationContext(), LoginActivity.class);
                i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i1);

                break;

            default:
                break;
        }
    }

    public void getallproducts(String selected_category, String selected_type,
                               String flag) {
        productDetailsArray.clear();
        db.open();
        Cursor cursor = db.fetchAllproductslistforstock(selected_category,
                selected_type, flag);
        if (cursor != null && cursor.getCount() > 0) {

            cursor.moveToFirst();

            do {
                HashMap<String, String[]> map = new HashMap<String, String[]>();
                db.open();
                Cursor c = db.fetchallSpecifyMSelect("product_master", null,
                        "ProductName = ? and ProductCategory = ? and ProductType = ?",
                        new String[]{cursor.getString(cursor
                                .getColumnIndex("ProductName")), selected_category, selected_type}, null);

                String comma_ids[] = new String[c.getCount()],
                        comma_dbids[] = new String[c.getCount()],
                        comma_mrps[] = new String[c.getCount()],
                        comma_size[] = new String[c.getCount()],
                        comma_catid[] = new String[c.getCount()],
                        comma_eancode[] = new String[c.getCount()],
                        comma_product[] = new String[c.getCount()],
                        comma_shade[] = new String[c.getCount()];

                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();

                    for (int i = 0; i < c.getCount(); i++) {
                        comma_ids[i] = c.getString(c.getColumnIndex("id"));
                        comma_dbids[i] = c.getString(c.getColumnIndex("db_id"));
                        comma_mrps[i] = c.getString(c.getColumnIndex("MRP"));
                        comma_size[i] = c.getString(c.getColumnIndex("Size"));
                        comma_catid[i] = c.getString(c.getColumnIndex("CategoryId"));
                        comma_eancode[i] = c.getString(c.getColumnIndex("EANCode"));
                        comma_product[i] = c.getString(c.getColumnIndex("ProductName"));
                        comma_shade[i] = c.getString(c.getColumnIndex("ShadeNo"));

                        c.moveToNext();

                    }

                }
                map.put("IDS", comma_ids);
                map.put("SIZE", comma_size);
                map.put("MRPS", comma_mrps);
                map.put("DBIDS", comma_dbids);
                map.put("CATID", comma_catid);
                map.put("EANCODE", comma_eancode);
                map.put("PRODUCT", comma_product);
                map.put("SHADENO", comma_shade);

                productDetailsArray.add(map);


            } while (cursor.moveToNext());

            for (int i = 0; i < productDetailsArray.size(); i++) {

                View tr = (TableRow) View.inflate(StockNewActivity.this, R.layout.inflate_stocksale_row, null);

                CheckBox cb = (CheckBox) tr.findViewById(R.id.chck_product);

                final AutoCompleteTextView spin = (AutoCompleteTextView) tr.findViewById(R.id.spin_mrp);

                TextView txtmrp = (TextView) tr.findViewById(R.id.txt_mrp);

                cb.setText(productDetailsArray.get(i).get("PRODUCT")[0]);

                final String mrps[] = productDetailsArray.get(i).get("MRPS");

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mrps) {
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

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin.setAdapter(adapter);

                spin.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        spin.showDropDown();
                        return false;
                    }
                });

                spin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (mrps != null && mrps.length > 0) {

                            mrpstring = parent.getItemAtPosition(position).toString();
                        }
                    }
                });

                tl_productList.addView(tr);

            }

            View tr1 = (TableRow) View.inflate(StockNewActivity.this, R.layout.inflate_stocksale_row, null);
            CheckBox cb = (CheckBox) tr1.findViewById(R.id.chck_product);

            AutoCompleteTextView spin = (AutoCompleteTextView) tr1.findViewById(R.id.spin_mrp);

            TextView txtmrp = (TextView) tr1.findViewById(R.id.txt_mrp);

            tr1.setVisibility(View.INVISIBLE);

            tl_productList.addView(tr1);
        }

    }

    // New Changes
    @SuppressLint("SimpleDateFormat")
    public String getopening(String dbid, String PRICE) {

        String closebal = "", retn = "";
        Cursor mCursor;
        db.open();
        mCursor = db.getuniquedata2(dbid);

        int count = mCursor.getCount();

        if (mCursor != null) {

            if (mCursor.moveToFirst()) {

                do {

                    closebal = mCursor.getString(mCursor
                            .getColumnIndex("close_bal"));

                } while (mCursor.moveToNext());

                sclo = "" + closebal;
//
            } else {

                sclo = "0";

            }

        }
        mCursor.close();
        db.close();

        return closebal;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

    }

    public void stockProceedData() {

        String check = "";

        arr_selectedDBids = new ArrayList<String>();
        int chckCount = 0;

        if (sp_product_category.getSelectedItemPosition() != 0) {

            if (sp_product_type.getSelectedItemPosition() != 0) {


                if (modecounter == 1) {
                    if (PMODE != null) {
                        check = PMODE.trim();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please select Mode",
                                Toast.LENGTH_LONG).show();

                    }

                    if (check.contentEquals("Select Mode") || check.equals("")) {
                        Toast.makeText(getApplicationContext(),
                                "Please select Mode",
                                Toast.LENGTH_LONG).show();

                    } else {
                        for (int i = 1; i < tl_productList.getChildCount(); i++) {
                            TableRow tr = (TableRow) tl_productList.getChildAt(i);
                            CheckBox cb = (CheckBox) tr.getChildAt(0);
                            if (cb.isChecked()) {
                                chckCount++;
                                // break;
                            }
                        }

                        if (chckCount == 0) {
                            Toast.makeText(getApplicationContext(),
                                    "Please select atleast 1 product",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            boolean spinvalue = true;

                            for (int i = 1; i < tl_productList.getChildCount(); i++) {
                                TableRow tr = (TableRow) tl_productList.getChildAt(i);
                                CheckBox cb = (CheckBox) tr.getChildAt(0);
                                TextView txtmrp = (TextView) tr.getChildAt(1);
                                AutoCompleteTextView spin = (AutoCompleteTextView) tr.getChildAt(2);

                                if (cb.isChecked()) {
                                    if (!spin.getText().toString().equals("")) {
                                        arr_selectedDBids.add(db.fetchStockDbID(cb.getText().toString(), spin.getText().toString(),
                                                sp_product_category
                                                        .getSelectedItem().toString()));
                                    } else {
                                        spinvalue = false;
                                    }
                                }
                            }

                            if (spinvalue == false) {
                                Toast.makeText(getApplicationContext(), "Please select MRP", Toast.LENGTH_SHORT).show();
                            } else {
                                String pro_name[] = new String[arr_selectedDBids.size()];
                                String chck_db_id[] = new String[arr_selectedDBids.size()];
                                String chck_mrp[] = new String[arr_selectedDBids.size()];
                                String chck_size[] = new String[arr_selectedDBids.size()];
                                String chck_cat_id[] = new String[arr_selectedDBids.size()];
                                String enacode[] = new String[arr_selectedDBids.size()];
                                String chck_shade[] = new String[arr_selectedDBids.size()];

                                for (int i = 0; i < arr_selectedDBids.size(); i++) {
                                    Cursor cur = db.fetchallSpecifyMSelect("product_master", null, "db_id = ? ", new String[]{arr_selectedDBids.get(i)}, null);
                                    if (cur != null && cur.getCount() > 0) {
                                        cur.moveToFirst();

                                        pro_name[i] = cur.getString(cur.getColumnIndex("ProductName"));
                                        chck_db_id[i] = arr_selectedDBids.get(i);
                                        chck_mrp[i] = cur.getString(cur.getColumnIndex("MRP"));
                                        chck_size[i] = cur.getString(cur.getColumnIndex("Size"));
                                        chck_cat_id[i] = cur.getString(cur.getColumnIndex("CategoryId"));
                                        enacode[i] = cur.getString(cur.getColumnIndex("EANCode"));
                                        chck_shade[i] = cur.getString(cur.getColumnIndex("ShadeNo"));

                                    }
                                }
                                startActivity(new Intent(StockNewActivity.this,
                                        StockAllActivity.class)
                                        .putExtra("db_id", chck_db_id)
                                        .putExtra("pro_name", pro_name)
                                        .putExtra("mrp", chck_mrp).putExtra("encode", enacode).putExtra("catid", chck_cat_id)
                                        .putExtra("shadeNo", chck_shade).putExtra("CAT", chck_cat_id).putExtra("Size", chck_size));

                            }

                        }


                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please select Mode",
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        "Please select Type",
                        Toast.LENGTH_LONG).show();
            }


        } else {
            Toast.makeText(getApplicationContext(),
                    "Please select Category",
                    Toast.LENGTH_LONG).show();
        }
    }

}
