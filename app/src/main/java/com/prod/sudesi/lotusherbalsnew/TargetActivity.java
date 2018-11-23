package com.prod.sudesi.lotusherbalsnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TargetActivity extends Activity {

    Button btn_home, btn_logout, btn_targetproceed;
    TextView tv_h_username, txt_bocname;
    AutoCompleteTextView category;
    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;
    LotusWebservice service;
    private ProgressDialog pd;
    ConnectionDetector cd;
    Dbcon db;

    String username, bdename, role, check, currentdate;

    private ArrayList<String> categoryDetailsArraylist;
    String[] strCategoryArray = null;
    String categorystring, categoryname, targetamt;
    Context context;

    EditText et_targetamt;

    String flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_target);

        context = TargetActivity.this;

        shp = getSharedPreferences("Lotus", MODE_PRIVATE);
        shpeditor = shp.edit();

        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        txt_bocname = (TextView) findViewById(R.id.txt_bocname);
        category = (AutoCompleteTextView) findViewById(R.id.spin_categorydrop);

        et_targetamt = (EditText) findViewById(R.id.et_targetamt);
        btn_targetproceed = (Button) findViewById(R.id.btn_targetproceed);

        pd = new ProgressDialog(this);
        service = new LotusWebservice(this);
        cd = new ConnectionDetector(this);
        db = new Dbcon(this);

        username = shp.getString("username", "");
        bdename = shp.getString("BDEusername", "");
        role = shp.getString("Role", "");
        tv_h_username.setText(bdename);

        String boc = cd.getBocName();
        txt_bocname.setText(boc);

        btn_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

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

        et_targetamt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_targetamt.setFocusableInTouchMode(true);
                et_targetamt.setCursorVisible(true);
                return false;
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

        try {
            fetchCategoryDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                et_targetamt.setText("");
                if (strCategoryArray != null && strCategoryArray.length > 0) {

                    categorystring = parent.getItemAtPosition(position).toString();
                    for (int i = 0; i < categoryDetailsArraylist.size(); i++) {
                        String text = categoryDetailsArraylist.get(i);
                        if (text.equalsIgnoreCase(categorystring)) {
                            categoryname = text;
                        }
                    }
                    if (categorystring != null && categorystring.length() > 0) {

                    }
                }
            }
        });

        btn_targetproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                targetamt = et_targetamt.getText().toString().trim();
                if (categorystring != null && categorystring.length() > 0) {
                    if (!categorystring.equalsIgnoreCase("Select")) {
                        if (!targetamt.equalsIgnoreCase("")) {
                            try {
                                new SaveBocTarget().execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Enter Target Amt", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Select Product Category", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Select Product Category", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fetchCategoryDetails() {
        //new changes
        categoryDetailsArraylist = new ArrayList<String>();
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
            categoryDetailsArraylist.add("COLOR");

        }
    }

    public class SaveBocTarget extends AsyncTask<Void, Void, SoapObject> {

        ContentValues contentvalues = new ContentValues();
        private SoapPrimitive soap_result = null;

        String Flag = "";

        String bocname = "Target_" + txt_bocname.getText().toString();

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            pd.setMessage("Please Wait....");
            pd.show();
            pd.setCancelable(false);

        }

        @Override
        protected SoapObject doInBackground(Void... params) {
            // TODO Auto-generated method stub

            if (!cd.isConnectingToInternet()) {

                Flag = "0";

            } else {
                try {

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    String insert_timestamp = sdf.format(c
                            .getTime());

                    soap_result = service.SaveBocTarget(bocname, username, categoryname, targetamt, insert_timestamp);

                    if (soap_result != null) {

                        if (soap_result.toString().equalsIgnoreCase("TRUE")) {
                            Flag = "1";
                        } else {
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

            if (pd != null && pd.isShowing() && !TargetActivity.this.isFinishing()) {
                pd.dismiss();
            }

            if (Flag.equalsIgnoreCase("0")) {

                Toast.makeText(getApplicationContext(),
                        "Connectivity Error, Please check Internet connection!!",
                        Toast.LENGTH_SHORT).show();

            } else if (Flag.equalsIgnoreCase("1")) {

                Toast.makeText(getApplicationContext(),"Boc Target Amount Save Succesfully!!",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(i);

            } else if (Flag.equalsIgnoreCase("2")) {

                Toast.makeText(getApplicationContext(), "Soap Response getting False!!", Toast.LENGTH_SHORT).show();
            }


        }

    }

}
