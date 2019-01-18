package com.prod.sudesi.lotusherbalsnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.ContentValues.TAG;


public class SaleNewActivity extends Activity implements OnClickListener {

    Spinner sp_product_category, sp_product_type; //sp_product_mode;

    Button btn_proceed, btn_home, btn_logout, btnsave, btnback;

    TableLayout tl_productList, tlsaveback;

    TableRow tr_header;

    TextView tv_h_username, txt_header, typetxt; //txt_product_mode;
    LinearLayout productlinearlayout;

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

    String selected_type;

    String username, bdename, mrpstring = "";
    private ProgressDialog pd;
    ConnectionDetector cd;
    LotusWebservice service;

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

        pd = new ProgressDialog(this);
        service = new LotusWebservice(this);
        cd = new ConnectionDetector(this);

        sp_product_category = (Spinner) findViewById(R.id.sp_product_category);
        sp_product_type = (Spinner) findViewById(R.id.sp_product_type);
        //sp_product_mode = (Spinner) findViewById(R.id.sp_product_mode);
        tl_productList = (TableLayout) findViewById(R.id.tl_productList);
        tlsaveback = (TableLayout) findViewById(R.id.tlsaveback);
        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        btnsave = (Button) findViewById(R.id.bt_save);
        btnback = (Button) findViewById(R.id.bt_back);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tr_header = (TableRow) findViewById(R.id.tr_header);
        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        txt_header = (TextView) findViewById(R.id.txt_header);
        typetxt = (TextView) findViewById(R.id.typetxt);
        productlinearlayout = (LinearLayout) findViewById(R.id.productlinearlayout);
        //txt_product_mode = (TextView) findViewById(R.id.txt_product_mode);
        //txt_product_mode.setVisibility(View.GONE);
        txt_header.setText("SALE");

        btn_proceed.setOnClickListener(this);
        btnsave.setOnClickListener(this);
        btnback.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        //sp_product_mode.setVisibility(View.GONE);

        shp = getSharedPreferences("Lotus", MODE_PRIVATE);
        shpeditor = shp.edit();

        db = new Dbcon(this);
        db.open();

        username = shp.getString("username", "");
        bdename = shp.getString("BDEusername", "");
        tv_h_username.setText(bdename);

        Date date = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);

        String attendanceDate1 = form.format(date);
        Log.v("", "attendanceDate1=" + attendanceDate1);

        String sld[] = attendanceDate1.split(" ");
        final String currentdate = sld[0];

        db.open();
        Integer salecount = db.checkTodaySale(username, currentdate);
        db.close();

        String div = shp.getString("div", "");
        if (div.equalsIgnoreCase("LH & LHM") || div.equalsIgnoreCase("LH & LM")) {

            db.open();
            productcategory = db.getproductcategory1(); // ------------
            if (productcategory.size() > 0) {
                productcategory.add("BABY CARE");
                if (salecount == 0) {
                    productcategory.add("NO SALE");
                }
            }
            db.close();
            // System.out.println(productArray);
            Log.e("", "kkkklklk111");

        }
        if (div.equalsIgnoreCase("LH")) {
            productcategory.clear();
            productcategory.add("Select");
            productcategory.add("SKIN");
            productcategory.add("BABY CARE");
            if (salecount == 0) {
                productcategory.add("NO SALE");
            }

        }
        if (div.equalsIgnoreCase("LM")) {
            productcategory.clear();
            productcategory.add("Select");
            productcategory.add("COLOR");
            if (salecount == 0) {
                productcategory.add("NO SALE");
            }

        }

        ArrayAdapter<String> product_adapter = new ArrayAdapter<String>(
                // context, android.R.layout.simple_spinner_item,
                SaleNewActivity.this, R.layout.custom_sp_item, productcategory);

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
                                    .getItemAtPosition(position).toString()
                                    .trim();

                            if (selected_product_category
                                    .equalsIgnoreCase("Select")
                                    || selected_product_category
                                    .equalsIgnoreCase("")) {

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        SaleNewActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        new String[]{});
                                sp_product_type.setAdapter(adapter);

                            } else {

                                if (selected_product_category.equalsIgnoreCase("BABY CARE")) {
                                    selected_product_category = "SKIN";
                                }

                                if (selected_product_category.equalsIgnoreCase("NO SALE")) {
                                    tlsaveback.setVisibility(View.VISIBLE);
                                    typetxt.setVisibility(View.GONE);
                                    sp_product_type.setVisibility(View.GONE);
                                    productlinearlayout.setVisibility(View.GONE);
                                    btn_proceed.setVisibility(View.GONE);
                                } else {

                                    tlsaveback.setVisibility(View.GONE);
                                    typetxt.setVisibility(View.VISIBLE);
                                    sp_product_type.setVisibility(View.VISIBLE);
                                    productlinearlayout.setVisibility(View.VISIBLE);
                                    btn_proceed.setVisibility(View.VISIBLE);

                                    db.open();
                                    if (sp_product_category.getItemAtPosition(position).toString().trim().equalsIgnoreCase("BABY CARE")) {
                                        producttypeArray = db.getproductypeforBabyProduct(selected_product_category);
                                    } else {
                                        producttypeArray = db.getproductype1(selected_product_category); // -------------
                                    }

                                    System.out.println(producttypeArray);

                                    ArrayAdapter<String> product_adapter1 = new ArrayAdapter<String>(
                                            // context,
                                            // android.R.layout.simple_spinner_item,
                                            SaleNewActivity.this,
                                            R.layout.custom_sp_item,
                                            producttypeArray);

                                    product_adapter1
                                            // .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            .setDropDownViewResource(R.layout.custom_spinner_dropdown_text);
                                    sp_product_type.setAdapter(product_adapter1);
                                }
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
                            SaleNewActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            new String[]{});

                } else {
                    String selected_category;
                    if (sp_product_category.getSelectedItem().toString().equalsIgnoreCase("BABY CARE")) {
                        selected_category = "SKIN";
                    } else {
                        selected_category = sp_product_category.getSelectedItem().toString();
                    }
                    selected_type = sp_product_type.getSelectedItem()
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

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
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

            case R.id.bt_save:

                if (cd.isCurrentDateMatchDeviceDate()) {
                    btnsave.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // This method will be executed once the timer is over
                            btnsave.setEnabled(true);
                            Log.d(TAG, "resend1");

                        }
                    }, 5000);// set time as per your requirement
                    new InsertSaleRecord().execute();
                } else {
                    Toast.makeText(SaleNewActivity.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

                }
                /*Intent i1 = new Intent(getApplicationContext(), LoginActivity.class);
                i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i1);*/

                break;

            case R.id.bt_back:

                Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intent);

                break;

            case R.id.btn_proceed:

                if (cd.isCurrentDateMatchDeviceDate()) {
                    btn_proceed.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // This method will be executed once the timer is over
                            btn_proceed.setEnabled(true);
                            Log.d(TAG, "resend1");

                        }
                    }, 5000);// set time as per your requirement

                    arr_selectedDBids = new ArrayList<String>();
                    int chckCount = 0;

                    if (sp_product_category.getSelectedItemPosition() != 0) {

                        if (sp_product_type.getSelectedItemPosition() != 0) {

                            Log.e("tl_productList", String.valueOf(tl_productList.getChildCount()));

                            for (int i2 = 1; i2 < tl_productList.getChildCount(); i2++) {
                                TableRow tr = (TableRow) tl_productList.getChildAt(i2);
                                CheckBox cb = (CheckBox) tr.getChildAt(0);
                                if (cb.isChecked()) {
                                    chckCount++;

                                }
                            }

                            Log.e("chckCount", String.valueOf(chckCount));

                            if (chckCount == 0) {
                                Toast.makeText(getApplicationContext(),
                                        "Please select atleast 1 product",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                boolean spinvalue = true;
                                for (int i3 = 1; i3 < tl_productList.getChildCount(); i3++) {
                                    TableRow tr = (TableRow) tl_productList
                                            .getChildAt(i3);
                                    CheckBox cb = (CheckBox) tr.getChildAt(0);
                                    TextView txtmrp = (TextView) tr.getChildAt(1);
                                    AutoCompleteTextView spin = (AutoCompleteTextView) tr.getChildAt(2);
                                    if (cb.isChecked()) {

                                        Log.e("cb-db_id", cb.getText()
                                                .toString());
//
                                        if (txtmrp.getVisibility() == View.VISIBLE) {
                                            arr_selectedDBids.add(db.fetchDbID(cb.getText()
                                                    .toString(), txtmrp.getText()
                                                    .toString(), selected_product_category));
                                        } else {
                                            if (!spin.getText().toString().equals("")) {
                                                arr_selectedDBids.add(db.fetchDbID(cb.getText()
                                                        .toString(), spin.getText().toString(), selected_product_category));
                                            } else {
                                                spinvalue = false;
                                            }
                                        }
                                    }
                                }

                                Log.e("arr_selectedDBids", arr_selectedDBids.toString());

                                if (spinvalue == false) {
                                    Toast.makeText(getApplicationContext(), "Please select MRP", Toast.LENGTH_SHORT).show();
                                } else {
                                    String show_pro_name[] = new String[arr_selectedDBids.size()];
                                    String pro_name[] = new String[arr_selectedDBids.size()];
                                    String chck_db_id[] = new String[arr_selectedDBids.size()];
                                    String chck_mrp[] = new String[arr_selectedDBids.size()];
                                    /*String chck_closing[] = new String[arr_selectedDBids.size()];*/
                                    String chck_size[] = new String[arr_selectedDBids.size()];
                                    String chck_cat_id[] = new String[arr_selectedDBids.size()];
                                    String enacode[] = new String[arr_selectedDBids.size()];
                                    String chck_shade[] = new String[arr_selectedDBids.size()];

                                    for (int i4 = 0; i4 < arr_selectedDBids.size(); i4++) {
                                        Cursor cur = db.fetchallSpecifyMSelect(
                                                "product_master", null, "db_id = ? ",
                                                new String[]{arr_selectedDBids.get(i4)},
                                                null);
                                        if (cur != null && cur.getCount() > 0) {
                                            cur.moveToFirst();

                                            String productname = cur.getString(cur.getColumnIndex("ProductName")).trim();
                                            String[] arr = productname.split(" ", 2);
                                            String firstword = arr[0];
                                            String splitingword = arr[1];
                                            String ProductName = "";
                                            String firstword1 = firstword.replaceFirst("\\s++$", "");
                                            if (selected_type.trim().contains(firstword1.trim())) {
                                                ProductName = splitingword;
                                            }
                                            show_pro_name[i4] = ProductName;

                                            pro_name[i4] = cur.getString(cur
                                                    .getColumnIndex("ProductName"));
                                            chck_db_id[i4] = arr_selectedDBids.get(i4);
                                            chck_mrp[i4] = cur.getString(cur
                                                    .getColumnIndex("MRP"));
                            /*	chck_closing[i4] = cur.getString(cur
										.getColumnIndex("close_bal"));*/
                                            chck_size[i4] = cur.getString(cur
                                                    .getColumnIndex("Size"));
                                            chck_cat_id[i4] = cur.getString(cur
                                                    .getColumnIndex("CategoryId"));
                                            enacode[i4] = cur.getString(cur
                                                    .getColumnIndex("EANCode"));
                                            chck_shade[i4] = cur.getString(cur
                                                    .getColumnIndex("ShadeNo"));

                                        }
                                    }

                                    startActivity(new Intent(SaleNewActivity.this,
                                            SaleCalculation.class)
                                            .putExtra("db_id", chck_db_id)
                                            .putExtra("show_pro_name", show_pro_name)
                                            .putExtra("pro_name", pro_name)
                                            .putExtra("mrp", chck_mrp)
                                            .putExtra("enacode", enacode)
                                            /*.putExtra("closing", chck_closing)*/
                                            .putExtra("shadeNo", chck_shade));
                                }

                            }

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Please select Type", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please select Category", Toast.LENGTH_LONG).show();
                    }

                    break;
                } else {
                    Toast.makeText(SaleNewActivity.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

                }
        }


    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

    }

    public void getallproducts(String selected_category, String selected_type,
                               String flag) {
        productDetailsArray.clear();
        db.open();
        Cursor cursor = db.fetchAllproductslistforstock1(selected_category,
                selected_type, flag);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                HashMap<String, String[]> map = new HashMap<String, String[]>();
                db.open();
                Cursor c = db.fetchAllproductslistforstockforsale(
                        selected_category, selected_type, flag,
                        cursor.getString(cursor.getColumnIndex("ProductName")));

                Log.e("c.getCount()", String.valueOf(c.getCount()));
                Log.e("product_name", cursor.getString(cursor.getColumnIndex("ProductName")));

                String comma_ids[] = null, comma_dbids[] = null, comma_mrps[] = null,
                        comma_size[] = null, comma_catid[] = null, comma_eancode[] = null,
                        comma_product[] = null, comma_product_show[] = null, comma_shade[] = null;

                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();

                    comma_ids = new String[c.getCount()];
                    comma_dbids = new String[c.getCount()];
                    comma_mrps = new String[c.getCount()];
                    comma_size = new String[c.getCount()];
                    comma_catid = new String[c.getCount()];
                    comma_eancode = new String[c.getCount()];
                    comma_product = new String[c.getCount()];
                    comma_product_show = new String[c.getCount()];
                    comma_shade = new String[c.getCount()];

                    for (int i = 0; i < c.getCount(); i++) {
                        comma_ids[i] = c.getString(c.getColumnIndex("id"));
                        comma_dbids[i] = c.getString(c.getColumnIndex("db_id"));
                        comma_mrps[i] = c.getString(c.getColumnIndex("price"));
                        comma_size[i] = c.getString(c.getColumnIndex("size"));
                        comma_catid[i] = c.getString(c.getColumnIndex("product_id"));
                        comma_eancode[i] = c.getString(c.getColumnIndex("eancode"));

                        String productname = c.getString(c.getColumnIndex("product_name")).trim();
                        String[] arr = productname.split(" ", 2);
                        String firstword = arr[0];
                        String splitingword = arr[1];
                        String ProductName = "";
                        String firstword1 = firstword.replaceFirst("\\s++$", "");
                        if (selected_type.trim().contains(firstword1.trim())) {
                            ProductName = splitingword;
                        }
                        comma_product_show[i] = ProductName;

                        comma_product[i] = c.getString(c.getColumnIndex("product_name"));
                        comma_shade[i] = c.getString(c.getColumnIndex("shadeNo"));

                        c.moveToNext();

                    }

                }

                if (comma_ids != null) {

                    Log.e("Array", "Enter");
                    map.put("IDS", comma_ids);
                    map.put("SIZE", comma_size);
                    map.put("MRPS", comma_mrps);
                    map.put("DBIDS", comma_dbids);
                    map.put("CATID", comma_catid);
                    map.put("EANCODE", comma_eancode);
                    map.put("PRODUCTSHOW", comma_product_show);
                    map.put("PRODUCT", comma_product);
                    map.put("SHADENO", comma_shade);

                    productDetailsArray.add(map);

                }

            } while (cursor.moveToNext());

            if (productDetailsArray.size() > 0) {

                for (int i = 0; i < productDetailsArray.size(); i++) {
                    View tr = (TableRow) View.inflate(SaleNewActivity.this,
                            R.layout.inflate_stocksale_row, null);

                    CheckBox cb = (CheckBox) tr.findViewById(R.id.chck_product);

                    final AutoCompleteTextView spin = (AutoCompleteTextView) tr.findViewById(R.id.spin_mrp);

                    TextView txtmrp = (TextView) tr.findViewById(R.id.txt_mrp);

                    if (productDetailsArray.get(i).get("PRODUCTSHOW")[0] != null &&
                            !productDetailsArray.get(i).get("PRODUCTSHOW")[0].equalsIgnoreCase("")) {
                        cb.setText(productDetailsArray.get(i).get("PRODUCTSHOW")[0]);
                    } else {
                        cb.setText(productDetailsArray.get(i).get("PRODUCT")[0]);
                    }

                    final String mrps[] = productDetailsArray.get(i).get("MRPS");

                    if (mrps.length > 1) {

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
                    } else {
                        spin.setVisibility(View.GONE);
                        txtmrp.setVisibility(View.VISIBLE);
                        txtmrp.setText(mrps[0]);
                    }

                    tl_productList.addView(tr);

                }

                View tr1 = (TableRow) View.inflate(SaleNewActivity.this,
                        R.layout.inflate_stocksale_row, null);
                CheckBox cb = (CheckBox) tr1.findViewById(R.id.chck_product);

                AutoCompleteTextView spin = (AutoCompleteTextView) tr1.findViewById(R.id.spin_mrp);

                TextView txtmrp = (TextView) tr1.findViewById(R.id.txt_mrp);

                tr1.setVisibility(View.INVISIBLE);

                tl_productList.addView(tr1);

            }
        }

    }


    public class InsertSaleRecord extends AsyncTask<String, Void, SoapObject> {

        ContentValues contentvalues = new ContentValues();
        private SoapPrimitive soap_result = null;

        String Flag = "";

        String bocname = "";

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            pd.setMessage("Please Wait....");
            pd.show();
            pd.setCancelable(false);

        }

        @Override
        protected SoapObject doInBackground(String... params) {
            // TODO Auto-generated method stub

            if (!cd.isConnectingToInternet()) {

                Flag = "0";

            } else {
                try {

                    soap_result = service.InsertSaleRecord(username, selected_product_category);

                    if (soap_result != null) {

                        if (soap_result.toString().equalsIgnoreCase("TRUE")) {
                            Flag = "1";
                        } else if (soap_result.toString().equalsIgnoreCase("FALSE")) {
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

            if (pd != null && pd.isShowing() && !SaleNewActivity.this.isFinishing()) {
                pd.dismiss();
            }

            if (Flag.equalsIgnoreCase("0")) {

                Toast.makeText(getApplicationContext(),
                        "Connectivity Error, Please check Internet connection!!",
                        Toast.LENGTH_SHORT).show();

            } else if (Flag.equalsIgnoreCase("1")) {

               /* Toast.makeText(getApplicationContext(), "Data Save Succesfully", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),DashboardNewActivity.class);
                startActivity(i);*/

                Date date = new Date();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

                String attendanceDate1 = form.format(date);
                Log.v("", "attendanceDate1=" + attendanceDate1);

                String sld[] = attendanceDate1.split(" ");
                final String sld1 = sld[0];

                db.open();
                db.updateAttendance(username, sld1);
                db.close();

                new SaveLogoutTime().execute();


            } else if (Flag.equalsIgnoreCase("2")) {

                Toast.makeText(getApplicationContext(), "Data Not Save Plz Try Again", Toast.LENGTH_SHORT).show();
            }


        }


    }

    private class SaveLogoutTime extends AsyncTask<Void, Void, SoapPrimitive> {

        ProgressDialog progress;

        SoapPrimitive soap_result;

        @Override
        protected SoapPrimitive doInBackground(Void... params) {
            // TODO Auto-generated method stub

            Date date = new Date();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);

            String attendanceDate1 = form.format(date);
            soap_result = service.SaveLogoutTime(username, attendanceDate1);

            return soap_result;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(SaleNewActivity.this);
            progress.setTitle("Uploading");
            progress.setMessage("Please Wait.......");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(SoapPrimitive result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progress != null && progress.isShowing() && !SaleNewActivity.this.isFinishing()) {
                progress.dismiss();
            }
            if (result != null) {
                if (result.toString().equalsIgnoreCase("true")) {

                    Intent i = new Intent(getApplicationContext(),
                            LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                } else {
                    Toast.makeText(SaleNewActivity.this, "Data Not uploaded", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(SaleNewActivity.this, "Please check internet Connectivity", Toast.LENGTH_LONG).show();
            }
        }


    }

}
