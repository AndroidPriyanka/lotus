package com.prod.sudesi.lotusherbalsnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.Models.OutletModel;
import com.prod.sudesi.lotusherbalsnew.Models.ReportModel;
import com.prod.sudesi.lotusherbalsnew.TableFixHeader.MatrixTableAdapter;
import com.prod.sudesi.lotusherbalsnew.TableFixHeader.TableFixHeaders;
import com.prod.sudesi.lotusherbalsnew.adapter.ReportAdapter;
import com.prod.sudesi.lotusherbalsnew.adapter.ReportAttendance;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportActivityNew extends Activity {

    Context context;

    //RadioButton rb_s, radio0, radio1;
    RadioButton rb_attendance;

    //RadioGroup rg_lhm_choice;

    EditText searchdata;

    Dbcon db;
    Cursor cursor_stock;

    ReportAdapter adapter;
    ReportAttendance adapter_attend;
    ListView attendancelist;

    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;

    TableRow table_row_stock, table_row_tester, table_row_attend;

    static public ArrayList<HashMap<String, String>> reportlist = new ArrayList<HashMap<String, String>>();

    ConnectionDetector cd;

    private ProgressDialog mProgress = null;

    TextView tv_h_username;
    Button btn_home, btn_logout;
    AutoCompleteTextView sp_outletName;
    CardView outletcardview;
    String username;
    String displayCategory, role, flotername, outletstring, outletName, outletCode;

    ShowReportofAttendance report_attendance;
    private ArrayList<OutletModel> outletDetailsArraylist;
    OutletModel outletModel;
    String[] strOutletArray = null;

    TableFixHeaders tableFixHeaders;
    MatrixTableAdapter<String> matrixTableAdapter;
    LinearLayout searchlayout;

    AutoCompleteTextView category;
    private ArrayList<String> categoryDetailsArraylist;
    String[] strCategoryArray = null;
    String categorystring,categoryname;

    ArrayAdapter<String> adapter1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        context = ReportActivityNew.this;
        /*
         * public onCreateView(LayoutInflater inflater, ViewGroup container,
         * Bundle savedInstanceState) {
         */
        // TODO Auto-generated method stub
        // return super.onCreateView(inflater, container, savedInstanceState);

        // View view = inflater.inflate(R.layout.fragment_report, null);
        // context = getActivity().getApplicationContext();
        shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
        shpeditor = shp.edit();
        db = new Dbcon(this);
        cd = new ConnectionDetector(context);
        role = shp.getString("Role", "");
        flotername = shp.getString("FLRCode", "");
        username = shp.getString("username", "");

        if (role.equalsIgnoreCase("DUB")) {
            setContentView(R.layout.fragment_report_dubai);
            sp_outletName = (AutoCompleteTextView) findViewById(R.id.spin_outletname);
            outletcardview = (CardView) findViewById(R.id.outletcardview);

            fetchOutletDetails();

            if (outletDetailsArraylist.size() > 0) {

                strOutletArray = new String[outletDetailsArraylist.size()];
                for (int i = 0; i < outletDetailsArraylist.size(); i++) {
                    strOutletArray[i] = outletDetailsArraylist.get(i).getOutletname();
                }
            }
            if (outletDetailsArraylist != null && outletDetailsArraylist.size() > 0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strOutletArray) {
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
                sp_outletName.setAdapter(adapter);
            }

            sp_outletName.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    sp_outletName.showDropDown();
                    return false;
                }
            });

            sp_outletName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (strOutletArray != null && strOutletArray.length > 0) {
                        reportlist.clear();
                        outletstring = parent.getItemAtPosition(position).toString();
                        String text = null, outletcode;
                        for (int i = 0; i < outletDetailsArraylist.size(); i++) {
                            text = outletDetailsArraylist.get(i).getOutletname();
                            outletcode = outletDetailsArraylist.get(i).getBACodeOutlet();
                            if (text.equalsIgnoreCase(outletstring)) {
                                outletName = text;
                                outletCode = outletcode;
                            }
                        }

                        if (text != null && text.length() > 0) {
                            new ShowReportofStock().execute();

                        } else {
                            Toast.makeText(getApplicationContext(), "Please Select Outlet", Toast.LENGTH_SHORT).show();
                        }


                    }
                }
            });


        } else {
            setContentView(R.layout.fragment_reportnew);
        }

        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        if (!role.equalsIgnoreCase("DUB")) {
            //rg_lhm_choice = (RadioGroup) findViewById(R.id.rg_lhm_choice);

            //searchdata = (EditText) findViewById(R.id.searchdata);


        }
       /* searchdata.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchdata.setFocusableInTouchMode(true);
                searchdata.setCursorVisible(true);
                return false;
            }
        });

        searchdata.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });*/
       /* searchdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchdata,InputMethodManager.SHOW_IMPLICIT);
            }
        });*/
        //rb_s = (RadioButton) findViewById(R.id.rb_stock);
        //radio0 = (RadioButton) findViewById(R.id.radio0);
        //radio1 = (RadioButton) findViewById(R.id.radio1);

        category = (AutoCompleteTextView) findViewById(R.id.spin_category);
        searchdata = (EditText) findViewById(R.id.searchdata);

        tableFixHeaders = (TableFixHeaders) findViewById(R.id.table);
        searchlayout = (LinearLayout) findViewById(R.id.searchlayout);
        rb_attendance = (RadioButton) findViewById(R.id.rb_attendance);
        //

        mProgress = new ProgressDialog(this);
        //------------------
        table_row_stock = (TableRow) findViewById(R.id.tr_label_stock);
        table_row_tester = (TableRow) findViewById(R.id.tr_label_tester);
        table_row_attend = (TableRow) findViewById(R.id.tr_label_attend);

        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);

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
                //startActivity(new Intent(getApplicationContext(), DashboardNewActivity.class));
            }
        });
        //---------------------


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
            adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strCategoryArray) {
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
                       // ShowingReportTotalSum(categoryname);
                       // new ShowCategoryWiseData().execute(categoryname);
                        table_row_attend.setVisibility(View.GONE);
                        attendancelist.setVisibility(View.GONE);
                        rb_attendance.setChecked(false);
                        searchdata.getText().clear();
                        showCategoryReport(categoryname);
                    }
                }
            }
        });

        attendancelist = (ListView) findViewById(R.id.attendancelist);

        searchdata.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                if(matrixTableAdapter != null) {
                    matrixTableAdapter.getFilter().filter(s.toString());
                }else {
                    category.setText("Select");

                    if (categoryDetailsArraylist.size() > 0) {
                        strCategoryArray = new String[categoryDetailsArraylist.size()];
                        for (int i = 0; i < categoryDetailsArraylist.size(); i++) {
                            strCategoryArray[i] = categoryDetailsArraylist.get(i);
                        }
                    }
                    if (categoryDetailsArraylist != null && categoryDetailsArraylist.size() > 0) {
                        adapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strCategoryArray) {
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
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*if (!role.equalsIgnoreCase("DUB")) {
            rb_s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (rb_s.isChecked()) {
                        table_row_attend.setVisibility(View.GONE);
                        attendancelist.setVisibility(View.GONE);
                        rb_attendance.setChecked(false);
                        rg_lhm_choice.setVisibility(View.VISIBLE);
                        //searchlayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            rb_s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (rb_s.isChecked()) {
                        table_row_attend.setVisibility(View.GONE);
                        attendancelist.setVisibility(View.GONE);
                        rb_attendance.setChecked(false);
                        outletcardview.setVisibility(View.VISIBLE);
                        rg_lhm_choice.setVisibility(View.VISIBLE);
                        //searchlayout.setVisibility(View.VISIBLE);


                    }
                }
            });
        }*/


   /*     if (!role.equalsIgnoreCase("DUB")) {
            //tester
           *//* rb_t.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (rb_t.isChecked()) {
                        rg_lhm_choice.setVisibility(View.GONE);
                        reportlist1.clear();
                        rb_s.setChecked(false);
                        table_row_tester.setVisibility(View.VISIBLE);
                        table_row_stock.setVisibility(View.GONE);
                        listview.setVisibility(View.GONE);
                        listview_t.setVisibility(View.VISIBLE);
                        table_row_attend.setVisibility(View.GONE);
                        attendancelist.setVisibility(View.GONE);
                        rb_attendance.setChecked(false);
                        new ShowReportofTester().execute();
                        txt_lh.setVisibility(View.GONE);
                        txt_lhm.setVisibility(View.GONE);
                    }
                }
            });*//*

            rg_lhm_choice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @SuppressLint("UseSparseArrays")
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    RadioButton rb = (RadioButton) findViewById(checkedId);
                    tableFixHeaders.setVisibility(View.VISIBLE);
                    searchlayout.setVisibility(View.VISIBLE);
                    reportlist.clear();

                    ArrayList<ReportModel> reportModelArrayList;
                    reportModelArrayList = new ArrayList<>();

                    List<String> list1;
                    list1 = new ArrayList<String>();
                    ArrayList<HashMap> reportList;
                    Map<Integer, ArrayList<HashMap>> hashMapArrayList = new HashMap<>();
                    HashMap<Integer, String> map1;

                    if (rb != null) {
                        String s = rb.getText().toString();

                        if (s.equalsIgnoreCase("LH")) {
                            searchdata.getText().clear();
                            displayCategory = "SKIN";

                        } else if (s.equalsIgnoreCase("LHM")) {
                            searchdata.getText().clear();
                            displayCategory = "COLOR";

                        }
                    }

                    ReportModel reportModel;

                    try {
                        db.open();

                        Cursor c = db.getReportTotalSum(displayCategory);
                        if (c != null && c.getCount() > 0) {
                            c.moveToFirst();
                            do {

                                reportModel = new ReportModel();

                                String opening = c.getString(2);
                                String sold = c.getString(4);
                                String closing = c.getString(3);
                                String gross = c.getString(1);
                                String net = c.getString(0);
                                String openingStock = c.getString(5);
                                String closeBal = c.getString(6);
                                String soldStock = c.getString(7);

                                reportModel.setProduct_id("");
                                reportModel.setDb_id("");
                                reportModel.setEancode("");
                                reportModel.setProduct_category("");
                                reportModel.setProduct_type("");
                                reportModel.setProduct_name("TOTAL VALUE IN RUPEES");
                                reportModel.setSize("");
                                reportModel.setPrice("");
                                reportModel.setEmp_id("");
                                reportModel.setOpening_stock(cd.getNonNullValues_Integer(openingStock));
                                reportModel.setOpening_amt(cd.getNonNullValues_Integer("\u20B9" + cd.getNonNullValues_Integer(opening)));
                                reportModel.setStock_received("");
                                reportModel.setStock_in_hand("");
                                reportModel.setClose_bal(cd.getNonNullValues_Integer(closeBal));
                                reportModel.setClose_amt(cd.getNonNullValues_Integer("\u20B9" + cd.getNonNullValues_Integer(closing)));
                                reportModel.setReturn_saleable("");
                                reportModel.setReturn_non_saleable("");
                                reportModel.setSold_stock(cd.getNonNullValues_Integer(soldStock));
                                reportModel.setSold_amt(cd.getNonNullValues_Integer("\u20B9" + cd.getNonNullValues_Integer(sold)));
                                reportModel.setTotal_gross_amount(cd.getNonNullValues_Integer("\u20B9" + cd.getNonNullValues_Integer(gross)));
                                reportModel.setTotal_net_amount(cd.getNonNullValues_Integer("\u20B9" + cd.getNonNullValues_Integer(net)));
                                reportModel.setDiscount("");
                                reportModel.setSavedServer("");
                                reportModel.setInsert_date("");
                                reportModel.setFLRCode("");

                                reportModelArrayList.add(0, reportModel);

                            } while (c.moveToNext());
                        }

                        db.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        db.open();
                        cursor_stock = db.getReportforStock(displayCategory);

                        if (cursor_stock != null && cursor_stock.moveToFirst()) {
                            cursor_stock.moveToFirst();

                            do {

                                reportModel = new ReportModel();

                                reportModel.setProduct_id(cd.getNonNullValues(cursor_stock.getString(1)));
                                reportModel.setDb_id(cd.getNonNullValues_Integer(cursor_stock.getString(2)));
                                reportModel.setEancode(cursor_stock.getString(3));
                                reportModel.setProduct_category(cursor_stock.getString(4));
                                reportModel.setProduct_type(cd.getNonNullValues(cursor_stock.getString(5)));
                                reportModel.setProduct_name(cd.getNonNullValues(cursor_stock.getString(6)));
                                reportModel.setSize(cursor_stock.getString(7));
                                reportModel.setPrice(cd.getNonNullValues_Integer(cursor_stock.getString(8)));
                                reportModel.setEmp_id(cursor_stock.getString(9));
                                reportModel.setOpening_stock(cd.getNonNullValues_Integer(cursor_stock.getString(10)));
                                reportModel.setOpening_amt(cd.getNonNullValues_Integer(cursor_stock.getString(11)));
                                reportModel.setStock_received(cd.getNonNullValues_Integer(cursor_stock.getString(12)));
                                reportModel.setStock_in_hand(cd.getNonNullValues_Integer(cursor_stock.getString(13)));
                                reportModel.setClose_bal(cd.getNonNullValues_Integer(cursor_stock.getString(14)));
                                reportModel.setClose_amt(cd.getNonNullValues_Integer(cursor_stock.getString(15)));
                                reportModel.setReturn_saleable(cd.getNonNullValues_Integer(cursor_stock.getString(16)));
                                reportModel.setReturn_non_saleable(cd.getNonNullValues_Integer(cursor_stock.getString(17)));
                                reportModel.setSold_stock(cd.getNonNullValues_Integer(cursor_stock.getString(18)));
                                reportModel.setSold_amt(cd.getNonNullValues_Integer(cursor_stock.getString(19)));
                                String gross = cursor_stock.getString(20);
                                if (gross == null) {
                                    gross = "0";
                                } else {
                                    gross = cursor_stock.getString(20);
                                }
                                reportModel.setTotal_gross_amount(gross);
                                String netamt = cursor_stock.getString(21);
                                if (netamt == null) {
                                    netamt = "0";
                                } else {
                                    netamt = cursor_stock.getString(21);
                                }
                                reportModel.setTotal_net_amount(netamt);
                                reportModel.setDiscount(cursor_stock.getString(22));

                                String status = cursor_stock.getString(23);
                                if (status.equalsIgnoreCase("0")) {
                                    status = "NU";
                                } else {
                                    status = "U";
                                }
                                reportModel.setSavedServer(status);
                                reportModel.setInsert_date(cursor_stock.getString(24));
                                reportModel.setFLRCode(cd.getNonNullValues(cursor_stock.getString(26)));

                                Log.e("savedServer", cursor_stock.getString(23));

                                reportModelArrayList.add(reportModel);

                            } while (cursor_stock.moveToNext());

                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }


                    for (int i = 0; i < reportModelArrayList.size(); i++) {
                        final ReportModel reportModel1 = reportModelArrayList.get(i);
                        reportList = new ArrayList<>();
                        list1 = new ArrayList<String>();
                        list1.add(reportModel1.getProduct_name());
                        list1.add(reportModel1.getProduct_type());
                        list1.add(reportModel1.getPrice());
                        list1.add(reportModel1.getSize());
                        list1.add(reportModel1.getOpening_stock());
                        list1.add(reportModel1.getOpening_amt());
                        list1.add(reportModel1.getStock_received());
                        list1.add(reportModel1.getReturn_saleable());
                        list1.add(reportModel1.getReturn_non_saleable());
                        list1.add(reportModel1.getSold_stock());
                        list1.add(reportModel1.getSold_amt());
                        list1.add(reportModel1.getClose_bal());
                        list1.add(reportModel1.getClose_amt());
                        list1.add(reportModel1.getTotal_gross_amount());
                        list1.add(reportModel1.getDiscount());
                        list1.add(reportModel1.getTotal_net_amount());
                        list1.add(reportModel1.getSavedServer());
                        list1.add(reportModel1.getDb_id());
                        list1.add(reportModel1.getFLRCode());

                        for (int k = 0; k < list1.size(); k++) {
                            map1 = new HashMap<Integer, String>();
                            map1.put(k, list1.get(k));
                            reportList.add(map1);
                        }
                        hashMapArrayList.put(i, reportList);
                    }
                    String[] reportArr = new String[list1.size()];
                    reportArr = list1.toArray(reportArr);
                    String[][] reportArray;

                    reportArray = new String[reportModelArrayList.size()][19];//{"Header 1","Header 2","Header 3","Header 4","Header 5","Header 6"});

                    for (Integer key : hashMapArrayList.keySet()) {
                        System.out.println("key : " + key);
                        System.out.println("value : " + hashMapArrayList.get(key));

                        for (int x = 0; x < hashMapArrayList.get(key).get(0).size(); x++) {
                            for (int y = 0; y < reportArray[x].length; y++) {
                                reportArray[key][y] = getValue(hashMapArrayList.get(key).get(y).toString());

                            }
                        }
                    }// end of outer for
                    if (reportArray != null) {
                        matrixTableAdapter = new MatrixTableAdapter<String>(context, reportArray);
                        tableFixHeaders.setAdapter(matrixTableAdapter);
                    }


                }
            });
        }*/
        if (!role.equalsIgnoreCase("DUB")) {
            rb_attendance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub

                    if (isChecked) {
                        //rb_s.setChecked(false);
                        //radio0.setChecked(false);
                        //radio1.setChecked(false);
                        //rg_lhm_choice.setVisibility(View.GONE);

                        category.setText("Select");

                        if (categoryDetailsArraylist.size() > 0) {
                            strCategoryArray = new String[categoryDetailsArraylist.size()];
                            for (int i = 0; i < categoryDetailsArraylist.size(); i++) {
                                strCategoryArray[i] = categoryDetailsArraylist.get(i);
                            }
                        }
                        if (categoryDetailsArraylist != null && categoryDetailsArraylist.size() > 0) {
                            adapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strCategoryArray) {
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
                        tableFixHeaders.setVisibility(View.GONE);
                        searchlayout.setVisibility(View.GONE);
                        attendancelist.setVisibility(View.VISIBLE);
                        table_row_attend.setVisibility(View.VISIBLE);
                        report_attendance = new ShowReportofAttendance();
                        report_attendance.execute();
                    } else {

                        attendancelist.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            rb_attendance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub

                    if (isChecked) {
                        sp_outletName.setText("");
                        table_row_attend.setVisibility(View.VISIBLE);
                        outletcardview.setVisibility(View.GONE);
                        attendancelist.setVisibility(View.VISIBLE);
                        tableFixHeaders.setVisibility(View.GONE);
                        searchlayout.setVisibility(View.GONE);
                        //rb_s.setChecked(false);
                        //radio0.setChecked(false);
                        //radio1.setChecked(false);
                        report_attendance = new ShowReportofAttendance();
                        report_attendance.execute();
                    } else {

                        attendancelist.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void fetchOutletDetails() {
        try {
            outletDetailsArraylist = new ArrayList<OutletModel>();
            Cursor cursor;
            db.open();
            cursor = db.getdata_outlet(username);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {

                    outletModel = new OutletModel();
                    outletModel.setBACodeOutlet(cursor.getString(cursor.getColumnIndex("baCodeOutlet")));
                    outletModel.setBAnameOutlet(cursor.getString(cursor.getColumnIndex("banameOutlet")));
                    outletModel.setOutletname(cursor.getString(cursor.getColumnIndex("outletname")));
                    outletModel.setFlotername(cursor.getString(cursor.getColumnIndex("flotername")));

                    outletDetailsArraylist.add(outletModel);
                } while (cursor.moveToNext());
                cursor.close();
            }
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ShowReportofStock extends AsyncTask<String, String, ArrayList<HashMap<String, String>>> {

        String flag;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            mProgress.setMessage("Please Wait");
            mProgress.show();
            mProgress.setCancelable(false);
        }


        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                db.open();
                if (!role.equalsIgnoreCase("DUB")) {
                    cursor_stock = db.getReportforStock(displayCategory);
                } else {
                    cursor_stock = db.getReportforStockDubai(outletCode);
                }
                if (cursor_stock != null && cursor_stock.moveToFirst()) {
                    cursor_stock.moveToFirst();

                    do {
                        Log.e("", "check2");

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put("ccc", "Categoryyy");

                        map.put("product_id", cursor_stock.getString(1));
                        map.put("db_id", cursor_stock.getString(2));
                        map.put("eancode", cursor_stock.getString(3));
                        map.put("product_category", cursor_stock.getString(4));
                        map.put("product_type", cursor_stock.getString(5));
                        map.put("product_name", cursor_stock.getString(6));
                        map.put("size", cursor_stock.getString(7));
                        map.put("price", cursor_stock.getString(8));
                        map.put("emp_id", cursor_stock.getString(9));
                        map.put("opening_stock", cursor_stock.getString(10));
                        map.put("opening_amt", cursor_stock.getString(11));
                        map.put("stock_received", cursor_stock.getString(12));
                        map.put("stock_in_hand", cursor_stock.getString(13));
                        map.put("close_bal", cursor_stock.getString(14));
                        map.put("close_amt", cursor_stock.getString(15));
                        map.put("return_saleable", cursor_stock.getString(16));
                        map.put("return_non_saleable", cursor_stock.getString(17));
                        map.put("sold_stock", cursor_stock.getString(18));
                        map.put("sold_amt", cursor_stock.getString(19));
                        String gross = cursor_stock.getString(20);
                        if (gross == null) {
                            gross = "0";
                        } else {
                            gross = cursor_stock.getString(20);
                        }
                        map.put("total_gross_amount", gross);
                        String netamt = cursor_stock.getString(21);
                        if (netamt == null) {
                            netamt = "0";
                        } else {
                            netamt = cursor_stock.getString(21);
                        }
                        map.put("total_net_amount", netamt);
                        map.put("discount", cursor_stock.getString(22));
                        map.put("savedServer", cursor_stock.getString(23));
                        map.put("insert_date", cursor_stock.getString(24));
                        map.put("FLRCode", cursor_stock.getString(26));

                        Log.e("savedServer", cursor_stock.getString(23));

                        reportlist.add(map);

                    } while (cursor_stock.moveToNext());

                } else {

                    flag = "1";

                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return reportlist;
        }


        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {

            try {
                if (result != null && result.size() >= 0) {
                    Log.e("", "reportlist===" + result);
                    adapter = new ReportAdapter(context, result);

                    runOnUiThread(new Runnable() {
                        public void run() {
                           /* listview.setAdapter(null);
                            if (adapter != null) {
                                listview.setVisibility(View.VISIBLE);
                                listview.setAdapter(adapter);
                            }*/
                        }
                    });

                }
                if (mProgress != null && mProgress.isShowing() && !ReportActivityNew.this.isFinishing()) {
                    mProgress.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class ShowReportofAttendance extends AsyncTask<String, String, ArrayList<HashMap<String, String>>> {

        ArrayList<HashMap<String, String>> arr_attendance = new ArrayList<HashMap<String, String>>();

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
            // TODO Auto-generated method stub

            db.open();

            Cursor c = db.fetchallOrder("attendance", new String[]{"Adate", "attendance", "absent_type"}, "Adate DESC");
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("ADATE", c.getString(c.getColumnIndex("Adate")));
                    map.put("ATTENDANCE", c.getString(c.getColumnIndex("attendance")));
                    map.put("ABSENTTYPE", c.getString(c.getColumnIndex("absent_type")));

                    arr_attendance.add(map);

                } while (c.moveToNext());
            }

            return arr_attendance;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            adapter_attend = new ReportAttendance(context, result);
            attendancelist.setAdapter(adapter_attend);

        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),
                DashboardNewActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public String getValue(String inputStr) {
        String value = "";
        if (inputStr.contains("=")) {
            String[] valueArr = inputStr.split("=");
            String subStr = valueArr[1];
            value = subStr.substring(0, subStr.indexOf("}"));
        }
        return value;
    }

    private void showCategoryReport(String displayCategory){
        tableFixHeaders.setVisibility(View.VISIBLE);
        searchlayout.setVisibility(View.VISIBLE);
        reportlist.clear();

        ArrayList<ReportModel> reportModelArrayList;
        reportModelArrayList = new ArrayList<>();

        List<String> list1;
        list1 = new ArrayList<String>();
        ArrayList<HashMap> reportList;
        Map<Integer, ArrayList<HashMap>> hashMapArrayList = new HashMap<>();
        HashMap<Integer, String> map1;


        ReportModel reportModel;

        try {
            db.open();

            Cursor c = db.getReportTotalSum(displayCategory);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {

                    reportModel = new ReportModel();

                    String opening = c.getString(2);
                    String sold = c.getString(4);
                    String closing = c.getString(3);
                    String gross = c.getString(1);
                    String net = c.getString(0);
                    String openingStock = c.getString(5);
                    String closeBal = c.getString(6);
                    String soldStock = c.getString(7);

                    reportModel.setProduct_id("");
                    reportModel.setDb_id("");
                    reportModel.setEancode("");
                    reportModel.setProduct_category("");
                    reportModel.setProduct_type("");
                    reportModel.setProduct_name("TOTAL VALUE IN RUPEES");
                    reportModel.setSize("");
                    reportModel.setPrice("");
                    reportModel.setEmp_id("");
                    reportModel.setOpening_stock(cd.getNonNullValues_Integer(openingStock));
                    reportModel.setOpening_amt(cd.getNonNullValues_Integer("\u20B9" + cd.getNonNullValues_Integer(opening)));
                    reportModel.setStock_received("");
                    reportModel.setStock_in_hand("");
                    reportModel.setClose_bal(cd.getNonNullValues_Integer(closeBal));
                    reportModel.setClose_amt(cd.getNonNullValues_Integer("\u20B9" + cd.getNonNullValues_Integer(closing)));
                    reportModel.setReturn_saleable("");
                    reportModel.setReturn_non_saleable("");
                    reportModel.setSold_stock(cd.getNonNullValues_Integer(soldStock));
                    reportModel.setSold_amt(cd.getNonNullValues_Integer("\u20B9" + cd.getNonNullValues_Integer(sold)));
                    reportModel.setTotal_gross_amount(cd.getNonNullValues_Integer("\u20B9" + cd.getNonNullValues_Integer(gross)));
                    reportModel.setTotal_net_amount(cd.getNonNullValues_Integer("\u20B9" + cd.getNonNullValues_Integer(net)));
                    reportModel.setDiscount("");
                    reportModel.setSavedServer("");
                    reportModel.setInsert_date("");
                    reportModel.setFLRCode("");

                    reportModelArrayList.add(0, reportModel);

                } while (c.moveToNext());
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            db.open();
            cursor_stock = db.getReportforStock(displayCategory);

            if (cursor_stock != null && cursor_stock.moveToFirst()) {
                cursor_stock.moveToFirst();

                do {

                    reportModel = new ReportModel();

                    reportModel.setProduct_id(cd.getNonNullValues(cursor_stock.getString(1)));
                    reportModel.setDb_id(cd.getNonNullValues_Integer(cursor_stock.getString(2)));
                    reportModel.setEancode(cursor_stock.getString(3));
                    reportModel.setProduct_category(cursor_stock.getString(4));
                    reportModel.setProduct_type(cd.getNonNullValues(cursor_stock.getString(5)));
                    reportModel.setProduct_name(cd.getNonNullValues(cursor_stock.getString(6)));
                    reportModel.setSize(cursor_stock.getString(7));
                    reportModel.setPrice(cd.getNonNullValues_Integer(cursor_stock.getString(8)));
                    reportModel.setEmp_id(cursor_stock.getString(9));
                    reportModel.setOpening_stock(cd.getNonNullValues_Integer(cursor_stock.getString(10)));
                    reportModel.setOpening_amt(cd.getNonNullValues_Integer(cursor_stock.getString(11)));
                    reportModel.setStock_received(cd.getNonNullValues_Integer(cursor_stock.getString(12)));
                    reportModel.setStock_in_hand(cd.getNonNullValues_Integer(cursor_stock.getString(13)));
                    reportModel.setClose_bal(cd.getNonNullValues_Integer(cursor_stock.getString(14)));
                    reportModel.setClose_amt(cd.getNonNullValues_Integer(cursor_stock.getString(15)));
                    reportModel.setReturn_saleable(cd.getNonNullValues_Integer(cursor_stock.getString(16)));
                    reportModel.setReturn_non_saleable(cd.getNonNullValues_Integer(cursor_stock.getString(17)));
                    reportModel.setSold_stock(cd.getNonNullValues_Integer(cursor_stock.getString(18)));
                    reportModel.setSold_amt(cd.getNonNullValues_Integer(cursor_stock.getString(19)));
                    String gross = cursor_stock.getString(20);
                    if (gross == null) {
                        gross = "0";
                    } else {
                        gross = cursor_stock.getString(20);
                    }
                    reportModel.setTotal_gross_amount(gross);
                    String netamt = cursor_stock.getString(21);
                    if (netamt == null) {
                        netamt = "0";
                    } else {
                        netamt = cursor_stock.getString(21);
                    }
                    reportModel.setTotal_net_amount(netamt);
                    reportModel.setDiscount(cursor_stock.getString(22));

                    String status = cursor_stock.getString(23);
                    if (status.equalsIgnoreCase("0")) {
                        status = "NU";
                    } else {
                        status = "U";
                    }
                    reportModel.setSavedServer(status);
                    reportModel.setInsert_date(cursor_stock.getString(24));
                    reportModel.setFLRCode(cd.getNonNullValues(cursor_stock.getString(26)));

                    Log.e("savedServer", cursor_stock.getString(23));

                    reportModelArrayList.add(reportModel);

                } while (cursor_stock.moveToNext());

            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }


        for (int i = 0; i < reportModelArrayList.size(); i++) {
            final ReportModel reportModel1 = reportModelArrayList.get(i);
            reportList = new ArrayList<>();
            list1 = new ArrayList<String>();
            list1.add(reportModel1.getProduct_name());
            list1.add(reportModel1.getProduct_type());
            list1.add(reportModel1.getPrice());
            list1.add(reportModel1.getSize());
            list1.add(reportModel1.getOpening_stock());
            list1.add(reportModel1.getOpening_amt());
            list1.add(reportModel1.getStock_received());
            list1.add(reportModel1.getReturn_saleable());
            list1.add(reportModel1.getReturn_non_saleable());
            list1.add(reportModel1.getSold_stock());
            list1.add(reportModel1.getSold_amt());
            list1.add(reportModel1.getClose_bal());
            list1.add(reportModel1.getClose_amt());
            list1.add(reportModel1.getTotal_gross_amount());
            list1.add(reportModel1.getDiscount());
            list1.add(reportModel1.getTotal_net_amount());
            list1.add(reportModel1.getSavedServer());
            list1.add(reportModel1.getDb_id());
            list1.add(reportModel1.getFLRCode());

            for (int k = 0; k < list1.size(); k++) {
                map1 = new HashMap<Integer, String>();
                map1.put(k, list1.get(k));
                reportList.add(map1);
            }
            hashMapArrayList.put(i, reportList);
        }
        String[] reportArr = new String[list1.size()];
        reportArr = list1.toArray(reportArr);
        String[][] reportArray;

        reportArray = new String[reportModelArrayList.size()][19];//{"Header 1","Header 2","Header 3","Header 4","Header 5","Header 6"});

        for (Integer key : hashMapArrayList.keySet()) {
            System.out.println("key : " + key);
            System.out.println("value : " + hashMapArrayList.get(key));

            for (int x = 0; x < hashMapArrayList.get(key).get(0).size(); x++) {
                for (int y = 0; y < reportArray[x].length; y++) {
                    reportArray[key][y] = getValue(hashMapArrayList.get(key).get(y).toString());

                }
            }
        }// end of outer for
        if (reportArray != null) {
            matrixTableAdapter = new MatrixTableAdapter<String>(context, reportArray);
            tableFixHeaders.setAdapter(matrixTableAdapter);
        }
    }


    public void fetchCategoryDetails() {
        //new changes
        categoryDetailsArraylist = new ArrayList<String>();

        db.open();
        categoryDetailsArraylist = db.getproductcategory(username);
        db.close();
    }

}
