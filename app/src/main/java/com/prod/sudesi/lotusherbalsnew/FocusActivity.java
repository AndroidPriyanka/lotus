package com.prod.sudesi.lotusherbalsnew;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.Models.FocusModel;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class FocusActivity extends Activity implements View.OnClickListener {


    Spinner sp_prodt_category;//, sp_prodt_type; //sp_product_mode;

    Button btn_returnproced, btn_home, btn_logout;

    TableLayout tl_returnproductList;

    TableRow tr_cat, tr_sp_categry;//, tr_type, tr_sp_prodType;
    LinearLayout table_header;

    TableRow tr_header;

    TextView tv_h_username;

    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;

    Dbcon db;

    ArrayList<String> productcategory = new ArrayList<String>();
    ArrayList<String> producttypeArray = new ArrayList<String>();
    //ArrayList<HashMap<String, String[]>> productDetailsArray = new ArrayList<HashMap<String, String[]>>();

    ArrayList<String> arr_selectedType;
    ArrayList<String> arr_selectedQty;

    public static String selected_product_category, selected_product_type;

    String username, bdename;

    String sclo = "", mrpstring, columnname;
    String selected_type;

    RadioGroup radio_target_report;
    RadioButton radio_target, radio_report;
    boolean target = false, report = false;

    String[] values;

    LotusWebservice service;
    ConnectionDetector cd;

    private ArrayList<FocusModel> focusReportList;
    FocusModel focusModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_focus);

        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        sp_prodt_category = (Spinner) findViewById(R.id.sp_prodt_category);
        tl_returnproductList = (TableLayout) findViewById(R.id.tl_returnprodctList);

        btn_returnproced = (Button) findViewById(R.id.btn_returnproced);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tr_header = (TableRow) findViewById(R.id.tr_headr);
        tv_h_username = (TextView) findViewById(R.id.tv_h_username);

        tr_cat = (TableRow) findViewById(R.id.tr_cat);
        tr_sp_categry = (TableRow) findViewById(R.id.tr_sp_categry);
        table_header = (LinearLayout) findViewById(R.id.table_header);

        radio_target_report = (RadioGroup) findViewById(R.id.radio_target_report);
        radio_target = (RadioButton) findViewById(R.id.radio_target);
        radio_report = (RadioButton) findViewById(R.id.radio_report);

        btn_returnproced.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        btn_logout.setOnClickListener(this);

        shp = getSharedPreferences("Lotus", MODE_PRIVATE);
        shpeditor = shp.edit();

        service = new LotusWebservice(this);
        cd = new ConnectionDetector(this);
        db = new Dbcon(this);
        db.open();

        username = shp.getString("username", "");
        bdename = shp.getString("BDEusername", "");
        tv_h_username.setText(bdename);

        String div = shp.getString("div", "");

        final String BOC = cd.getBocName();

        if (div.equalsIgnoreCase("LH & LHM") || div.equalsIgnoreCase("LH & LM")) {

            db.open();
            productcategory = db.getproductcategory1(); // ------------
            if (productcategory.size() > 0) {
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

        }

        ArrayAdapter<String> product_adapter = new ArrayAdapter<String>(
                // context, android.R.layout.simple_spinner_item,
                FocusActivity.this, R.layout.custom_sp_item, productcategory);

        product_adapter
                // .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                .setDropDownViewResource(R.layout.custom_spinner_dropdown_text);

        sp_prodt_category.setAdapter(product_adapter);

        sp_prodt_category
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        try {
                            // TODO Auto-generated method stub

                            selected_product_category = sp_prodt_category
                                    .getItemAtPosition(position).toString().trim();

                            if (selected_product_category
                                    .equalsIgnoreCase("Select")
                                    || selected_product_category
                                    .equalsIgnoreCase("")) {

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        FocusActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        new String[]{});
                                //sp_prodt_type.setAdapter(adapter);

                            } else {

                                if (selected_product_category.equalsIgnoreCase("SKIN")
                                        || selected_product_category.equalsIgnoreCase("")) {

                                    columnname = "CategoryId";

                                } else {
                                    columnname = "ShadeNo";
                                }

                                if (selected_product_category.equalsIgnoreCase("BABY CARE")) {
                                    selected_product_category = "SKIN";
                                }

                                db.open();
                                if (sp_prodt_category.getItemAtPosition(position).toString().trim().equalsIgnoreCase("BABY CARE")) {
                                    producttypeArray.clear();
                                    producttypeArray = db.getproductypeforFocusBaby(selected_product_category, username, BOC);
                                } else {
                                    producttypeArray.clear();
                                    producttypeArray = db.getproductypeforfocus(selected_product_category, username, BOC); // -------------
                                }
                                System.out.println(producttypeArray);

                                tl_returnproductList.removeAllViews();
                                tl_returnproductList.addView(tr_header);
                                getallproductsType();

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


        radio_target_report.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String s;
                switch (checkedId) {
                    case R.id.radio_target:
                        /*target = true;
                        report = false;

                        table_header.setVisibility(View.VISIBLE);
                        btn_returnproced.setVisibility(View.VISIBLE);*/

                        break;

                    case R.id.radio_report:

                        report = true;
                        target = false;

                        Intent i = new Intent(getApplicationContext(), FocusReportActivity.class);
                        i.putExtra("reportclick", report);
                        startActivity(i);

                        table_header.setVisibility(View.GONE);
                        btn_returnproced.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_returnproced:

                if (cd.isCurrentDateMatchDeviceDate()) {
                    btn_returnproced.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // This method will be executed once the timer is over
                            btn_returnproced.setEnabled(true);
                            Log.d(TAG, "resend1");

                        }
                    }, 5000);

                    stockProceedData();
                } else {
                    Toast.makeText(FocusActivity.this, "Your Handset Date Not Match Current Date", Toast.LENGTH_LONG).show();

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

    public void getallproductsType() {

        for (int i = 0; i < producttypeArray.size(); i++) {

            View tr = (TableRow) View.inflate(FocusActivity.this, R.layout.inflate_focustype_row, null);

            CheckBox cb = (CheckBox) tr.findViewById(R.id.chck_producttype);

            EditText edt = (EditText) tr.findViewById(R.id.edt_qty);


            if (producttypeArray.get(i) != null &&
                    !producttypeArray.get(i).equalsIgnoreCase("")) {
                cb.setText(producttypeArray.get(i));
            }

            tl_returnproductList.addView(tr);

        }

        View tr1 = (TableRow) View.inflate(FocusActivity.this, R.layout.inflate_focustype_row, null);

        CheckBox cb = (CheckBox) tr1.findViewById(R.id.chck_producttype);

        EditText edt = (EditText) tr1.findViewById(R.id.edt_qty);

        tr1.setVisibility(View.INVISIBLE);

        tl_returnproductList.addView(tr1);
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

    }

    public void stockProceedData() {

        String check = "";
        arr_selectedType = new ArrayList<String>();
        arr_selectedQty = new ArrayList<String>();
        int chckCount = 0;

        if (sp_prodt_category.getSelectedItemPosition() != 0) {

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
                        "Please select atleast 1 Product Type",
                        Toast.LENGTH_LONG).show();
            } else {
                boolean edtvalue = true;

                EditText edt_qty = null;
                for (int i = 1; i < tl_returnproductList.getChildCount(); i++) {
                    TableRow tr = (TableRow) tl_returnproductList.getChildAt(i);
                    CheckBox cb = (CheckBox) tr.getChildAt(0);
                    edt_qty = (EditText) tr.getChildAt(1);

                    if (cb.isChecked()) {

                        arr_selectedType.add(cb.getText().toString());

                        if (!edt_qty.getText().toString().equals("")) {

                            arr_selectedQty.add(edt_qty.getText().toString());

                        } else {
                            edtvalue = false;
                        }
                    }
                }

                if (edtvalue == false) {
                    Toast.makeText(getApplicationContext(), "Please Enter Target Quantity", Toast.LENGTH_SHORT).show();
                } else {
                    String boc = cd.getBocName();

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    String insert_timestamp = sdf.format(c.getTime());

                    focusReportList = new ArrayList<FocusModel>();
                    for (int i = 0; i < arr_selectedType.size(); i++) {

                        db.open();
                        Integer achivement = db.getFocusStockSum(arr_selectedType.get(i));
                        db.close();

                        final String[] columns = new String[]{"Productid", "Type", "Category", "Empid", "ProName", "size", "MRP",
                                "Target_qty", "Target_amt", "AndroidCreateddate", "BOC"};

                        db.open();
                        Cursor cur = db.getuniquedataFocusData(arr_selectedType.get(i),
                                selected_product_category,
                                username, boc, "focus_data");

                        int count = cur.getCount();
                        Log.v("", "" + count);
                        db.close();
                        if (count > 0) {
                            db.open();
                            db.update(arr_selectedType.get(i),
                                    new String[]{
                                            "",
                                            arr_selectedType.get(i),
                                            selected_product_category,
                                            username,
                                            "",
                                            "",
                                            "",
                                            arr_selectedQty.get(i),
                                            String.valueOf(achivement),
                                            insert_timestamp,
                                            boc},
                                    new String[]{
                                            "Productid", "Type", "Category", "Empid", "ProName", "size", "MRP",
                                            "Target_qty", "Target_amt", "AndroidCreateddate", "BOC"},
                                    "focus_data", "Type");

                            db.close();
                        } else {
                            db.open();

                            values = new String[]{
                                    "",
                                    arr_selectedType.get(i),
                                    selected_product_category,
                                    username,
                                    "",
                                    "",
                                    "",
                                    arr_selectedQty.get(i),
                                    String.valueOf(achivement),
                                    insert_timestamp,
                                    boc};

                            db.insert(values, columns, "focus_data");

                            db.close();
                        }

                        focusModel = new FocusModel();
                        focusModel.setProduct_type(arr_selectedType.get(i));
                        focusModel.setProduct_category(selected_product_category);
                        focusModel.setTarget_qty(arr_selectedQty.get(i));
                        focusModel.setAchievement_Unit(String.valueOf(achivement));
                        focusModel.setUsername(username);
                        focusModel.setAndroid_created_date(insert_timestamp);
                        focusModel.setBocname(boc);

                        focusReportList.add(focusModel);

                    }

                    new FocusReportData().execute(focusReportList);

                }

            }

        } else {
            Toast.makeText(getApplicationContext(),
                    "Please select Category",
                    Toast.LENGTH_LONG).show();
        }
    }


    private class FocusReportData extends AsyncTask<ArrayList<FocusModel>, Void, SoapObject> {


        String ErroFlag;
        String Erro_function = "";

        Cursor attendance_array;

        String Flag;

        SoapPrimitive soapresultfocus = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected SoapObject doInBackground(ArrayList<FocusModel>... arrayLists) {
            // TODO Auto-generated method stub

            ArrayList<FocusModel> focuslist = arrayLists[0];

            if (!cd.isConnectingToInternet()) {

                Flag = "0";

            } else {
                try {

                    for (int i = 0; i < focuslist.size(); i++) {

                        focusModel = focuslist.get(i);

                        soapresultfocus = service.SaveIntoFOCUSReport(focusModel.getProduct_type(), focusModel.getProduct_category()
                                , focusModel.getTarget_qty(), focusModel.getBocname(), focusModel.getUsername()
                                , focusModel.getAndroid_created_date(), focusModel.getAchievement_Unit());


                        if (soapresultfocus != null) {

                            if (soapresultfocus.toString().equalsIgnoreCase("TRUE")) {
                                Flag = "1";
                            } else {
                                Flag = "2";
                            }
                        }
                    }

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

            try {

                if (Flag.equalsIgnoreCase("0")) {

                    Toast.makeText(getApplicationContext(), "Connectivity Error, Please check Internet connection!!", Toast.LENGTH_SHORT).show();

                } else if (Flag.equalsIgnoreCase("1")) {

                    Toast.makeText(getApplicationContext(), "Focus Data Saved Succesfully!!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), FocusReportActivity.class);
                    startActivity(i);

                } else if (Flag.equalsIgnoreCase("2")) {

                    Toast.makeText(getApplicationContext(), "Soap Response getting False!!", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
