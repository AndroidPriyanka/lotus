package com.prod.sudesi.lotusherbalsnew;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class ReturnsActivity extends Activity implements View.OnClickListener {


    Spinner sp_prod_category, sp_prod_type; //sp_product_mode;

    Button btn_returnproceed, btn_home, btn_logout;

    TableLayout tl_returnproductList;

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

    String sclo = "", mrpstring, columnname;
    String selected_type;

    RadioGroup radio_cust_comp;
    RadioButton radio_cust, radio_comp;
    boolean cust = false, comp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_returns);

        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        sp_prod_category = (Spinner) findViewById(R.id.sp_prod_category);
        sp_prod_type = (Spinner) findViewById(R.id.sp_prod_type);
        tl_returnproductList = (TableLayout) findViewById(R.id.tl_returnproductList);
        btn_returnproceed = (Button) findViewById(R.id.btn_returnproceed);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tr_header = (TableRow) findViewById(R.id.tr_header);
        tv_h_username = (TextView) findViewById(R.id.tv_h_username);

        radio_cust_comp = (RadioGroup) findViewById(R.id.radio_cust_comp);
        radio_cust = (RadioButton) findViewById(R.id.radio_cust);
        radio_comp = (RadioButton) findViewById(R.id.radio_comp);

        btn_returnproceed.setOnClickListener(this);
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

        /*if (div.equalsIgnoreCase("LH & LHM") || div.equalsIgnoreCase("LH & LM")) {

            db.open();
            productcategory = db.getproductcategory1(); // ------------
            if(productcategory.size()>0) {
                productcategory.add("BABY CARE");
            }

            // System.out.println(productArray);
            Log.e("", "kkkklklk111");

        }
        if (div.equalsIgnoreCase("LH")) {
            productcategory.clear();
            productcategory.add("Select");
            productcategory.add("SKIN");
            productcategory.add("BABY CARE");

        }
        if (div.equalsIgnoreCase("LM")) {
            productcategory.clear();
            productcategory.add("Select");
            productcategory.add("COLOR");

        }*/

        db.open();
        productcategory = db.getproductcategory(username);
        db.close();

        ArrayAdapter<String> product_adapter = new ArrayAdapter<String>(
                // context, android.R.layout.simple_spinner_item,
                ReturnsActivity.this, R.layout.custom_sp_item, productcategory);

        product_adapter
                // .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                .setDropDownViewResource(R.layout.custom_spinner_dropdown_text);

        sp_prod_category.setAdapter(product_adapter);

        sp_prod_category
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        try {
                            // TODO Auto-generated method stub

                            selected_product_category = sp_prod_category
                                    .getItemAtPosition(position).toString().trim();

                            if (selected_product_category
                                    .equalsIgnoreCase("Select")
                                    || selected_product_category
                                    .equalsIgnoreCase("")) {

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        ReturnsActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        new String[]{});
                                sp_prod_type.setAdapter(adapter);

                            } else {

                                if (selected_product_category.equalsIgnoreCase("SKIN")
                                        || selected_product_category.equalsIgnoreCase("")) {

                                    columnname = "CategoryId";

                                } else {
                                    columnname = "ShadeNo";
                                }

                               /* if (selected_product_category.equalsIgnoreCase("BABY CARE")){
                                    selected_product_category = "SKIN";
                                }*/

                                db.open();
                               /* if(sp_prod_category.getItemAtPosition(position).toString().trim().equalsIgnoreCase("BABY CARE")){
                                    producttypeArray = db.getproductypeforBabyProduct(selected_product_category);
                                }else {
                                    producttypeArray = db.getproductype1(selected_product_category); // -------------
                                }*/

                                producttypeArray = db.getproductype1(selected_product_category);
                                System.out.println(producttypeArray);

                                ArrayAdapter<String> product_adapter1 = new ArrayAdapter<String>(
                                        // context,
                                        // android.R.layout.simple_spinner_item,
                                        ReturnsActivity.this,
                                        R.layout.custom_sp_item, producttypeArray);

                                product_adapter1
                                        // .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        .setDropDownViewResource(R.layout.custom_spinner_dropdown_text);
                                sp_prod_type.setAdapter(product_adapter1);
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

        sp_prod_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                selected_product_type = sp_prod_type.getItemAtPosition(arg2)
                        .toString().trim();

                if (selected_product_type.equalsIgnoreCase("Select")
                        || selected_product_category.equalsIgnoreCase("")) {

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            ReturnsActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            new String[]{});

                } else {
                    String selected_category;
                   /* if (sp_prod_category.getSelectedItem().toString().equalsIgnoreCase("BABY CARE")) {
                        selected_category = "SKIN";
                    } else {
                        selected_category = sp_prod_category.getSelectedItem().toString();
                    }*/

                    selected_category = sp_prod_category.getSelectedItem().toString();
                    selected_type = sp_prod_type.getSelectedItem()
                            .toString();

                    Log.v("", "" + selected_category + " " + selected_type);
                    productDetailsArray.clear();
                    tl_returnproductList.removeAllViews();
                    tl_returnproductList.addView(tr_header);
                    getallproducts(selected_category, selected_type, "N", columnname);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });

        radio_cust_comp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String s;
                switch (checkedId) {
                    case R.id.radio_cust:
                        cust = true;
                        comp = false;
                        s = "Return From Customer";
                        Log.e("s", s);
                        if (s.equalsIgnoreCase("Select Mode")
                                || s.equalsIgnoreCase("")) {
                        } else {
                            modecounter = 1;
                            PMODE = s;

                        }
                        break;

                    case R.id.radio_comp:
                        comp = true;
                        cust = false;
                        s = "Return to Company";
                        Log.e("s", s);
                        if (s.equalsIgnoreCase("Select Mode")
                                || s.equalsIgnoreCase("")) {
                        } else {
                            modecounter = 1;
                            PMODE = s;

                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_returnproceed:

                btn_returnproceed.setEnabled(false);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        btn_returnproceed.setEnabled(true);
                        Log.d(TAG, "resend1");

                    }
                }, 5000);

                stockProceedData();

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
                               String flag, String columnname) {
        productDetailsArray.clear();
        db.open();
        Cursor cursor = db.fetchAllproductslistforstock(selected_category,
                selected_type, flag, columnname);
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
                        comma_product_show[] = new String[c.getCount()],
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

                        String productname = c.getString(c.getColumnIndex("ProductName")).trim();
                        String[] arr = productname.split(" ", 2);
                        String firstword = arr[0];
                        String splitingword = arr[1];
                        String ProductName = "";
                        String firstword1 = firstword.replaceFirst("\\s++$", "");
                        if (selected_type.trim().contains(firstword1.trim())) {
                            ProductName = splitingword;
                        }
                        comma_product_show[i] = ProductName;

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
                map.put("PRODUCTSHOW", comma_product_show);
                map.put("SHADENO", comma_shade);

                productDetailsArray.add(map);


            } while (cursor.moveToNext());

            for (int i = 0; i < productDetailsArray.size(); i++) {

                View tr = (TableRow) View.inflate(ReturnsActivity.this, R.layout.inflate_stocksale_row, null);

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

                tl_returnproductList.addView(tr);

            }

            View tr1 = (TableRow) View.inflate(ReturnsActivity.this, R.layout.inflate_stocksale_row, null);
            CheckBox cb = (CheckBox) tr1.findViewById(R.id.chck_product);

            AutoCompleteTextView spin = (AutoCompleteTextView) tr1.findViewById(R.id.spin_mrp);

            TextView txtmrp = (TextView) tr1.findViewById(R.id.txt_mrp);

            tr1.setVisibility(View.INVISIBLE);

            tl_returnproductList.addView(tr1);
        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

    }

    public void stockProceedData() {

        String check = "";
        arr_selectedDBids = new ArrayList<String>();
        int chckCount = 0;

        if (sp_prod_category.getSelectedItemPosition() != 0) {

            if (sp_prod_type.getSelectedItemPosition() != 0) {

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

                        for (int i = 1; i < tl_returnproductList.getChildCount(); i++) {
                            TableRow tr = (TableRow) tl_returnproductList.getChildAt(i);
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

                            for (int i = 1; i < tl_returnproductList.getChildCount(); i++) {
                                TableRow tr = (TableRow) tl_returnproductList.getChildAt(i);
                                CheckBox cb = (CheckBox) tr.getChildAt(0);
                                TextView txtmrp = (TextView) tr.getChildAt(1);
                                AutoCompleteTextView spin = (AutoCompleteTextView) tr.getChildAt(2);

                                if (cb.isChecked()) {
                                    if (!spin.getText().toString().equals("")) {
                                        arr_selectedDBids.add(db.fetchStockDbID(cb.getText().toString(), spin.getText().toString(),
                                                selected_product_category));
                                    } else {
                                        spinvalue = false;
                                    }
                                }
                            }

                            if (spinvalue == false) {
                                Toast.makeText(getApplicationContext(), "Please select MRP", Toast.LENGTH_SHORT).show();
                            } else {
                                String show_pro_name[] = new String[arr_selectedDBids.size()];
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

                                        String productname = cur.getString(cur.getColumnIndex("ProductName")).trim();
                                        String[] arr = productname.split(" ", 2);
                                        String firstword = arr[0];
                                        String splitingword = arr[1];
                                        String ProductName = "";
                                        String firstword1 = firstword.replaceFirst("\\s++$", "");
                                        if (selected_type.trim().contains(firstword1.trim())) {
                                            ProductName = splitingword;
                                        }
                                        show_pro_name[i] = ProductName;

                                        pro_name[i] = cur.getString(cur.getColumnIndex("ProductName"));
                                        chck_db_id[i] = arr_selectedDBids.get(i);
                                        chck_mrp[i] = cur.getString(cur.getColumnIndex("MRP"));
                                        chck_size[i] = cur.getString(cur.getColumnIndex("Size"));
                                        chck_cat_id[i] = cur.getString(cur.getColumnIndex("CategoryId"));
                                        enacode[i] = cur.getString(cur.getColumnIndex("EANCode"));
                                        chck_shade[i] = cur.getString(cur.getColumnIndex("ShadeNo"));

                                    }
                                }
                                startActivity(new Intent(ReturnsActivity.this,
                                        ReturnQtyActivity.class)
                                        .putExtra("db_id", chck_db_id)
                                        .putExtra("show_pro_name", show_pro_name)
                                        .putExtra("pro_name", pro_name)
                                        .putExtra("mrp", chck_mrp)
                                        .putExtra("encode", enacode)
                                        .putExtra("catid", chck_cat_id)
                                        .putExtra("shadeNo", chck_shade)
                                        .putExtra("CAT", chck_cat_id)
                                        .putExtra("Size", chck_size));

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
