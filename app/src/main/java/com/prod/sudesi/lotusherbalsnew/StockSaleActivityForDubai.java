package com.prod.sudesi.lotusherbalsnew;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class StockSaleActivityForDubai extends Activity implements View.OnClickListener {

    SharedPreferences sp;
    SharedPreferences.Editor spe;
    Context context = null;
    ConnectionDetector cd;

    AutoCompleteTextView brand, offerAuto;
    Button btn_proceed, btn_home, btn_logout;
    TableLayout tl_productList;
    TableRow tr_header;
    TextView tv_h_username, textView1;
    RadioGroup radio_stock_sale;
    RadioButton radio_stock, radio_sale;
    LinearLayout stock_salelayout;
    String username;
    boolean stock = false, sale = false;
    Dbcon db;
    ArrayList<String> productBrand;
    String[] strBrandArray = null;
    String[] strOfferArray = null;
    ArrayList<String> productoffer;
    ArrayList<HashMap<String, String[]>> productDetailsArray = new ArrayList<HashMap<String, String[]>>();
    ArrayList<String> arr_selectedDBids;


    public static String selected_product_brand, brandname, selected_product_offer, offername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stocksale_dubai);

        context = StockSaleActivityForDubai.this;

        cd = new ConnectionDetector(context);

        sp = context.getSharedPreferences("Lotus", Context.MODE_PRIVATE);
        spe = sp.edit();

        brand = (AutoCompleteTextView) findViewById(R.id.spin_brand);
        offerAuto = (AutoCompleteTextView) findViewById(R.id.spin_offer);
        tl_productList = (TableLayout) findViewById(R.id.tl_productList);
        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tr_header = (TableRow) findViewById(R.id.tr_header);
        tv_h_username = (TextView) findViewById(R.id.tv_h_username);

        //modecardview = (CardView) findViewById(R.id.modecardview);

        stock_salelayout = (LinearLayout) findViewById(R.id.stock_salelayout);

        radio_stock_sale = (RadioGroup) findViewById(R.id.radio_stock_sale);
        radio_stock = (RadioButton) findViewById(R.id.radio_stock);
        radio_sale = (RadioButton) findViewById(R.id.radio_sale);

        username = sp.getString("username", "");
        Log.v("", "username==" + username);

        tv_h_username.setText(username);

        db = new Dbcon(this);
        db.open();

        btn_proceed.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        btn_logout.setOnClickListener(this);


        radio_stock_sale.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_stock:
                        stock = true;
                        sale = false;
                        brand.setText("");
                        offerAuto.setText("");
                        tl_productList.removeAllViews();
                        break;

                    case R.id.radio_sale:
                        sale = true;
                        stock = false;
                        brand.setText("");
                        offerAuto.setText("");
                        tl_productList.removeAllViews();
                        break;
                }
            }
        });

        brand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (brand.length() > 0) {
                    brand.setError(null);
                }

            }
        });

        offerAuto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (offerAuto.length() > 0) {
                    offerAuto.setError(null);
                }

            }
        });

        try {
            db.open();
            productBrand = new ArrayList<String>();

            productBrand = db.fetchselectbrand();
            System.out.println(productBrand);

            if (productBrand.size() > 0) {
                strBrandArray = new String[productBrand.size()];
                for (int i = 0; i < productBrand.size(); i++) {
                    strBrandArray[i] = productBrand.get(i);
                }
            }

            if (productBrand != null && productBrand.size() > 0) {
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(StockSaleActivityForDubai.this, android.R.layout.simple_list_item_1, strBrandArray) {
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
                brand.setAdapter(adapter1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        brand.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //product_category.setText("");
                //product_subcategory.setText("");
                offerAuto.setText("");
                tl_productList.removeAllViews();
                brand.showDropDown();
                return false;
            }
        });

        brand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (strBrandArray != null && strBrandArray.length > 0) {

                    selected_product_brand = parent.getItemAtPosition(position).toString();
                    for (int i = 0; i < productBrand.size(); i++) {
                        String text = productBrand.get(i);
                        if (text.equalsIgnoreCase(selected_product_brand)) {
                            brandname = text;
                        }
                    }
                    if (selected_product_brand != null && selected_product_brand.length() > 0) {
                        try {
                            db.open();
                            productoffer = new ArrayList<String>();
                            productoffer = db.fetchselectoffer(brandname);
                            System.out.println(productoffer);

                            if (productoffer.size() > 0) {
                                strOfferArray = new String[productoffer.size()];
                                for (int i = 0; i < productoffer.size(); i++) {
                                    strOfferArray[i] = productoffer.get(i);
                                }
                            }
                            if (productoffer != null && productoffer.size() > 0) {
                                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(StockSaleActivityForDubai.this, android.R.layout.simple_list_item_1, strOfferArray) {
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
                                offerAuto.setAdapter(adapter1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        offerAuto.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tl_productList.removeAllViews();
                offerAuto.showDropDown();
                return false;
            }
        });

        offerAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (strOfferArray != null && strOfferArray.length > 0) {

                    selected_product_offer = parent.getItemAtPosition(position).toString();
                    for (int i = 0; i < productoffer.size(); i++) {
                        String text = productoffer.get(i);
                        if (text.equalsIgnoreCase(selected_product_offer)) {
                            offername = text;
                        }
                    }

                    try {
                        if (sale) {
                            tl_productList.addView(tr_header);
                            //getproductDetailsforSale(offername,subcategoryname,categoryname,brandname);
                            getallproductsforsale(brandname, offername);

                        } else {
                            tl_productList.removeAllViews();
                            tl_productList.addView(tr_header);

                            getproductsDetails(brandname, offername);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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

    public void getproductsDetails(String selected_brand, String selected_offer) {
        try {
            productDetailsArray.clear();
            db.open();
            Cursor cursor = db.fetchproductslistforproductMaster(selected_brand, selected_offer);
            if (cursor != null && cursor.getCount() > 0) {

                cursor.moveToFirst();

                do {
                    HashMap<String, String[]> map = new HashMap<String, String[]>();
                    db.open();
                    Cursor c = db.fetchallSpecifyMSelect("product_master", null,
                            "ProductName = ? and ProductCategory = ? and SingleOffer = ?",
                            new String[]{cursor.getString(cursor
                                    .getColumnIndex("ProductName")), selected_brand, selected_offer}, null);

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
                if (productDetailsArray.size() > 0) {
                    for (int i = 0; i < productDetailsArray.size(); i++) {

                        View tr = (TableRow) View.inflate(StockSaleActivityForDubai.this, R.layout.inflate_stocksale_row, null);

                        CheckBox cb = (CheckBox) tr.findViewById(R.id.chck_product);

                        final AutoCompleteTextView spin = (AutoCompleteTextView) tr.findViewById(R.id.spin_mrp);

                        TextView txtmrp = (TextView) tr.findViewById(R.id.txt_mrp);

                        cb.setText(productDetailsArray.get(i).get("PRODUCT")[0]);

                        final String mrps[] = productDetailsArray.get(i).get("MRPS");

                        if(mrps.length > 0) {

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

                                        // mrpstring = parent.getItemAtPosition(position).toString();
                                    }
                                }
                            });
                        }else{
                            spin.setVisibility(View.GONE);
                            txtmrp.setVisibility(View.VISIBLE);
                            txtmrp.setText(mrps[0]);
                        }

                        tl_productList.addView(tr);

                    }
                }

                View tr1 = (TableRow) View.inflate(StockSaleActivityForDubai.this, R.layout.inflate_stocksale_row, null);
                CheckBox cb = (CheckBox) tr1.findViewById(R.id.chck_product);

                AutoCompleteTextView spin = (AutoCompleteTextView) tr1.findViewById(R.id.spin_mrp);

                TextView txtmrp = (TextView) tr1.findViewById(R.id.txt_mrp);

                tr1.setVisibility(View.INVISIBLE);

                tl_productList.addView(tr1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getallproductsforsale(String selected_brand, String selected_offer) {
        productDetailsArray.clear();
        db.open();
        Cursor cursor = db.fetchAllproductslistforstocktable(selected_brand, selected_offer);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                HashMap<String, String[]> map = new HashMap<String, String[]>();
                db.open();
                Cursor c = db.fetchAllproductslistforstockforsale1(selected_brand, selected_offer,
                        cursor.getString(cursor.getColumnIndex("ProductName")));

                Log.e("c.getCount()", String.valueOf(c.getCount()));
                Log.e("product_name", cursor.getString(cursor.getColumnIndex("ProductName")));

                String comma_ids[] = null, comma_dbids[] = null, comma_mrps[] = null, comma_size[] = null, comma_catid[] = null, comma_eancode[] = null, comma_product[] = null, comma_shade[] = null;

                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();

                    comma_ids = new String[c.getCount()];
                    comma_dbids = new String[c.getCount()];
                    comma_mrps = new String[c.getCount()];
                    comma_size = new String[c.getCount()];
                    comma_catid = new String[c.getCount()];
                    comma_eancode = new String[c.getCount()];
                    comma_product = new String[c.getCount()];
                    comma_shade = new String[c.getCount()];

                    for (int i = 0; i < c.getCount(); i++) {
                        comma_ids[i] = c.getString(c.getColumnIndex("id"));
                        comma_dbids[i] = c.getString(c.getColumnIndex("db_id"));
                        comma_mrps[i] = c.getString(c.getColumnIndex("price"));
                        comma_size[i] = c.getString(c.getColumnIndex("size"));
                        comma_catid[i] = c.getString(c
                                .getColumnIndex("product_id"));
                        comma_eancode[i] = c.getString(c
                                .getColumnIndex("eancode"));
                        comma_product[i] = c.getString(c
                                .getColumnIndex("product_name"));
                        comma_shade[i] = c.getString(c
                                .getColumnIndex("shadeNo"));

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
                    map.put("PRODUCT", comma_product);
                    map.put("SHADENO", comma_shade);

                    productDetailsArray.add(map);

                }

            } while (cursor.moveToNext());

            if (productDetailsArray.size() > 0) {

                for (int i = 0; i < productDetailsArray.size(); i++) {
                    View tr = (TableRow) View.inflate(StockSaleActivityForDubai.this,
                            R.layout.inflate_stocksale_row, null);

                    CheckBox cb = (CheckBox) tr.findViewById(R.id.chck_product);

                    final AutoCompleteTextView spin = (AutoCompleteTextView) tr.findViewById(R.id.spin_mrp);

                    TextView txtmrp = (TextView) tr.findViewById(R.id.txt_mrp);

                    cb.setText(productDetailsArray.get(i).get("PRODUCT")[0]);

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
                                    //  mrpstring = parent.getItemAtPosition(position).toString();
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

                View tr1 = (TableRow) View.inflate(StockSaleActivityForDubai.this,
                        R.layout.inflate_stocksale_row, null);
                CheckBox cb = (CheckBox) tr1.findViewById(R.id.chck_product);

                AutoCompleteTextView spin = (AutoCompleteTextView) tr1.findViewById(R.id.spin_mrp);

                TextView txtmrp = (TextView) tr1.findViewById(R.id.txt_mrp);

                tr1.setVisibility(View.INVISIBLE);

                tl_productList.addView(tr1);

            }
        }

    }

    public void stockProceedData() {

        String check = "";
        arr_selectedDBids = new ArrayList<String>();
        int chckCount = 0;

        if (selected_product_brand != null && selected_product_brand.length() > 0) {

            if (selected_product_offer != null && selected_product_offer.length() > 0) {

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

                            if (txtmrp.getVisibility() == View.VISIBLE) {
                                arr_selectedDBids.add(db.fetchStockDbIDfordubai(cb.getText()
                                        .toString(), txtmrp.getText()
                                        .toString(), selected_product_offer));
                            } else {
                                if(!spin.getText().toString().equals("")) {
                                    arr_selectedDBids.add(db.fetchStockDbIDfordubai(cb.getText()
                                            .toString(), spin.getText().toString(),selected_product_offer));
                                }else{
                                    spinvalue = false;
                                }
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
                        String chck_singleoffer[] = new String[arr_selectedDBids.size()];
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
                                enacode[i] = cur.getString(cur.getColumnIndex("EANCode"));
                                chck_singleoffer[i] = cur.getString(cur.getColumnIndex("SingleOffer"));

                            }
                        }

                        if (sale) {
                            startActivity(new Intent(StockSaleActivityForDubai.this,
                                    SaleCalculation.class)
                                    .putExtra("pro_name", pro_name)
                                    .putExtra("db_id", chck_db_id)
                                    .putExtra("mrp", chck_mrp)
                                    .putExtra("Size", chck_size)
                                    .putExtra("encode", enacode)
                                    .putExtra("singleoffer", chck_singleoffer));
                        } else {
                            startActivity(new Intent(StockSaleActivityForDubai.this,
                                    StockAllActivity.class)
                                    .putExtra("pro_name", pro_name)
                                    .putExtra("db_id", chck_db_id)
                                    .putExtra("mrp", chck_mrp)
                                    .putExtra("Size", chck_size)
                                    .putExtra("encode", enacode)
                                    .putExtra("singleoffer", chck_singleoffer));
                        }

                    }

                }


            } else {
                Toast.makeText(getApplicationContext(),
                        "Please select Offer",
                        Toast.LENGTH_LONG).show();
            }


        } else {
            Toast.makeText(getApplicationContext(),
                    "Please select Brand",
                    Toast.LENGTH_LONG).show();
        }
    }
}
