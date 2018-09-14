package com.prod.sudesi.lotusherbalsnew;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.Models.OutletModel;
import com.prod.sudesi.lotusherbalsnew.adapter.ReportAttendance;
import com.prod.sudesi.lotusherbalsnew.adapter.ReportCategoryTypeAdapter;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryWiseDataActivity extends Activity{

    Button btn_home, btn_logout;
    TextView tv_h_username;
    AutoCompleteTextView category;
    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;
    LotusWebservice service;
    private ProgressDialog pd;
    ConnectionDetector cd;
    Dbcon db;
    ReportCategoryTypeAdapter adaptercategory;

    String flotername, bdename, role, check, currentdate;

    private ArrayList<String> categoryDetailsArraylist;
    String[] strCategoryArray = null;
    String categorystring,categoryname;
    Context context;
    ListView categorytypelist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_categorywise_data);

        context = CategoryWiseDataActivity.this;

        shp = getSharedPreferences("Lotus", MODE_PRIVATE);
        shpeditor = shp.edit();

        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        category = (AutoCompleteTextView) findViewById(R.id.spin_category);
        categorytypelist = (ListView) findViewById(R.id.categorytypelist);

        pd = new ProgressDialog(this);
        service = new LotusWebservice(this);
        cd = new ConnectionDetector(this);
        db = new Dbcon(this);

        flotername = shp.getString("username", "");
        bdename = shp.getString("BDEusername", "");
        role = shp.getString("Role", "");
        tv_h_username.setText(bdename);

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
            }
        });

        category.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (category.length() > 0) {
                    category.setError(null);
                }

            }
        });

        fetchCategoryDetails();
        /////////Details of Brand
        if (categoryDetailsArraylist.size() > 0) {
            strCategoryArray = new String[categoryDetailsArraylist.size()];
            for (int i = 0; i < categoryDetailsArraylist.size(); i++) {
                strCategoryArray[i] = categoryDetailsArraylist.get(i);
            }
        }
        if (categoryDetailsArraylist != null && categoryDetailsArraylist.size() > 0) {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strCategoryArray) {
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
            category.setAdapter(adapter1);
        }

        category.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                category.showDropDown();
                return false;
            }
        });

        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (strCategoryArray != null && strCategoryArray.length > 0) {

                    categorystring = parent.getItemAtPosition(position).toString();
                    for (int i = 0; i < categoryDetailsArraylist.size(); i++) {
                        String text = categoryDetailsArraylist.get(i);
                        if (text.equalsIgnoreCase(categorystring)) {
                            categoryname = text;
                        }
                    }
                    if (categorystring != null && categorystring.length() > 0) {
                        new ShowCategoryWiseData().execute(categoryname);

                    }
                }
            }
        });

    }

    public void fetchCategoryDetails() {

        String div = shp.getString("div", "");

        if (div.equalsIgnoreCase("LH & LHM") || div.equalsIgnoreCase("LH & LM")) {

            db.open();
            categoryDetailsArraylist = db.getproductcategory1(); // ------------
            //categoryDetailsArraylist.add("BABY CARE");

            // System.out.println(productArray);
            Log.e("", "kkkklklk111");

        }
        if (div.equalsIgnoreCase("LH")) {
            categoryDetailsArraylist.clear();
            categoryDetailsArraylist.add("Select");
            categoryDetailsArraylist.add("SKIN");
            //categoryDetailsArraylist.add("BABY CARE");

        }
        if (div.equalsIgnoreCase("LM")) {
            categoryDetailsArraylist.clear();
            categoryDetailsArraylist.add("Select");
            //categoryDetailsArraylist.add("COLOR");

        }
    }

    public class ShowCategoryWiseData extends AsyncTask<String, String, ArrayList<HashMap<String, String>>> {

        ArrayList<HashMap<String, String>> arr_category = new ArrayList<HashMap<String, String>>();

        String category = "";

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
            // TODO Auto-generated method stub

            category = params[0];
            db.open();

            Cursor c = db.getReportCategorydata(category);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("Type", c.getString(0));
                    map.put("Opening", c.getString(1));
                    map.put("Receive", c.getString(2));
                    map.put("closing", c.getString(3));
                    map.put("opening_amt", c.getString(4));
                    map.put("receive_amt", c.getString(5));
                    map.put("close_amt", c.getString(6));

                    arr_category.add(map);

                } while (c.moveToNext());
            }

            return arr_category;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            adaptercategory = new ReportCategoryTypeAdapter(context, result);
            categorytypelist.setAdapter(adaptercategory);

        }

    }
}
