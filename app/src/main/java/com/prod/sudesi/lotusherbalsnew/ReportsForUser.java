package com.prod.sudesi.lotusherbalsnew;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.Models.OutletModel;
import com.prod.sudesi.lotusherbalsnew.adapter.ReportAdapter;
import com.prod.sudesi.lotusherbalsnew.adapter.ReportAttendance;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;

public class ReportsForUser extends Activity {

    Context context;


    RadioButton rb_s;
    //RadioButton rb_t;
    RadioButton rb_attendance;

    RadioGroup rg_lhm_choice;

    Dbcon db;
    Cursor cursor_stock;
    Cursor cursor_tester;
    Cursor cursor_sale;

    ReportAdapter adapter;
    ReportTester adapter1;
    ReportAttendance adapter_attend;
    //ReportAdapterSale adapterSale;
    ListView listview;
    ListView listview_t;
    ListView attendancelist;

    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;

    TableRow table_row_stock, table_row_tester, table_row_attend, tr_total_stock;
    //,table_row_sale;

    HorizontalScrollView horizantalscrollviewforstock;

    static public ArrayList<HashMap<String, String>> reportlist = new ArrayList<HashMap<String, String>>();
    static public ArrayList<HashMap<String, String>> reportlist1 = new ArrayList<HashMap<String, String>>();
    static public ArrayList<HashMap<String, String>> reportlist2 = new ArrayList<HashMap<String, String>>();
    static public ArrayList<HashMap<String, String>> reportlisttotalsum = new ArrayList<HashMap<String, String>>();


    private ProgressDialog mProgress = null;

    TextView tv_h_username;//, txt_lh, txt_lhm, txt_net_title;//txt_lh_clo, txt_lhm_clo,txt_closing_title
    Button btn_home, btn_logout;
    AutoCompleteTextView sp_outletName;
    CardView outletcardview;
    String username;
    String displayCategory, role, flotername, outletstring, outletName, outletCode;

    ShowReportofAttendance report_attendance;
    private ArrayList<OutletModel> outletDetailsArraylist;
    OutletModel outletModel;
    String[] strOutletArray = null;

    TextView op_total, sold_total, Closing_total, gross_total, net_total;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        context = ReportsForUser.this;
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
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strOutletArray) {
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
                sp_outletName.setAdapter(adapter1);
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
            setContentView(R.layout.fragment_report);
        }

        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        if (!role.equalsIgnoreCase("DUB")) {
            //rb_t = (RadioButton) findViewById(R.id.rb_tester);
            rg_lhm_choice = (RadioGroup) findViewById(R.id.rg_lhm_choice);
            //txt_lh = (TextView) findViewById(R.id.txt_lh);
            //txt_lhm = (TextView) findViewById(R.id.txt_lhm);
            //txt_lh_clo = (TextView) findViewById(R.id.txt_lh_clo);
            //txt_lhm_clo = (TextView) findViewById(R.id.txt_lhm_clo);
            //txt_net_title = (TextView) findViewById(R.id.txt_net_title);
            //txt_closing_title = (TextView) findViewById(R.id.txt_closing_title);

            tr_total_stock = (TableRow) findViewById(R.id.tr_total_stock);
            op_total = (TextView) findViewById(R.id.op_total);
            sold_total = (TextView) findViewById(R.id.sold_total);
            Closing_total = (TextView) findViewById(R.id.Closing_total);
            gross_total = (TextView) findViewById(R.id.gross_total);
            net_total = (TextView) findViewById(R.id.net_total);
        }
        rb_s = (RadioButton) findViewById(R.id.rb_stock);
        //
        rb_attendance = (RadioButton) findViewById(R.id.rb_attendance);
        //

        mProgress = new ProgressDialog(this);
        //------------------
        table_row_stock = (TableRow) findViewById(R.id.tr_label_stock);
        table_row_tester = (TableRow) findViewById(R.id.tr_label_tester);
        table_row_attend = (TableRow) findViewById(R.id.tr_label_attend);

        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        //tv_h_username.setText(username);

        btn_logout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btn_home.setOnClickListener(new OnClickListener() {

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

        // getActivity().setRequestedOrientation(
        // ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        horizantalscrollviewforstock = (HorizontalScrollView) findViewById(R.id.horizantal_scrollview_stock_report);

        horizantalscrollviewforstock.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                int scrollX = v.getScrollX();
                int scrollY = v.getScrollY();

                horizantalscrollviewforstock.scrollTo(scrollX, scrollY);

                return false;

            }
        });


        listview = (ListView) findViewById(R.id.stock_list);
        listview_t = (ListView) findViewById(R.id.testerlist);
        attendancelist = (ListView) findViewById(R.id.attendancelist);
        if (!role.equalsIgnoreCase("DUB")) {
            rb_s.setOnCheckedChangeListener(new OnCheckedChangeListener() {


                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (rb_s.isChecked()) {
                        //reportlist.clear();
                        tr_total_stock.setVisibility(View.VISIBLE);
                        listview.setVisibility(View.VISIBLE);
                        table_row_stock.setVisibility(View.VISIBLE);
                        table_row_attend.setVisibility(View.GONE);
                        attendancelist.setVisibility(View.GONE);
                        rb_attendance.setChecked(false);
                        rg_lhm_choice.setVisibility(View.VISIBLE);
                        //rg_lhm_choice.clearCheck();
                        //getNetAmountSum();
                        //txt_lh.setVisibility(View.VISIBLE);
                        //txt_lhm.setVisibility(View.VISIBLE);
                        //txt_lh_clo.setVisibility(View.VISIBLE);
                        //txt_lhm_clo.setVisibility(View.VISIBLE);
                        //txt_net_title.setVisibility(View.VISIBLE);
                        //txt_closing_title.setVisibility(View.VISIBLE);


                    }
                }
            });
        } else {
            rb_s.setOnCheckedChangeListener(new OnCheckedChangeListener() {


                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (rb_s.isChecked()) {
                        table_row_stock.setVisibility(View.VISIBLE);
                        table_row_attend.setVisibility(View.GONE);
                        attendancelist.setVisibility(View.GONE);
                        rb_attendance.setChecked(false);
                        outletcardview.setVisibility(View.VISIBLE);


                    }
                }
            });
        }
        if (!role.equalsIgnoreCase("DUB")) {
           /* rb_t.setOnCheckedChangeListener(new OnCheckedChangeListener() {

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
            });*/

            rg_lhm_choice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    RadioButton rb = (RadioButton) findViewById(checkedId);
                    //rb_t.setChecked(false);
                    table_row_tester.setVisibility(View.GONE);
                    tr_total_stock.setVisibility(View.VISIBLE);
                    table_row_stock.setVisibility(View.VISIBLE);
                    listview_t.setVisibility(View.GONE);
                    reportlist.clear();
                    if (rb != null) {
                        String s = rb.getText().toString();

                        if (s.equalsIgnoreCase("LH")) {

                            displayCategory = "SKIN";
                            ShowingReportTotalSum(displayCategory);
                            new ShowReportofStock().execute();

                        } else if (s.equalsIgnoreCase("LHM")) {

                            displayCategory = "COLOR";
                            ShowingReportTotalSum(displayCategory);
                            new ShowReportofStock().execute();

                        }

                    }

                }
            });
        }
        if (!role.equalsIgnoreCase("DUB")) {
            rb_attendance.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub

                    if (isChecked) {
                        table_row_stock.setVisibility(View.GONE);
                        table_row_tester.setVisibility(View.GONE);
                        tr_total_stock.setVisibility(View.GONE);
                        listview.setVisibility(View.GONE);
                        listview_t.setVisibility(View.GONE);
                        rb_s.setChecked(false);
                        //rb_t.setChecked(false);
                        rg_lhm_choice.setVisibility(View.GONE);
                        reportlist1.clear();
                        attendancelist.setVisibility(View.VISIBLE);
                        table_row_attend.setVisibility(View.VISIBLE);
                        report_attendance = new ShowReportofAttendance();
                        report_attendance.execute();
                        //txt_lh.setVisibility(View.GONE);
                        //txt_lhm.setVisibility(View.GONE);
                        //txt_lh_clo.setVisibility(View.GONE);
                        //txt_lhm_clo.setVisibility(View.GONE);
                        //txt_net_title.setVisibility(View.GONE);
                        //txt_closing_title.setVisibility(View.GONE);
                    } else {

                        attendancelist.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            rb_attendance.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub

                    if (isChecked) {
                        sp_outletName.setText("");
                        table_row_stock.setVisibility(View.GONE);
                        table_row_attend.setVisibility(View.VISIBLE);
                        listview.setVisibility(View.GONE);
                        outletcardview.setVisibility(View.GONE);
                        attendancelist.setVisibility(View.VISIBLE);
                        rb_s.setChecked(false);
                        reportlist1.clear();
                        report_attendance = new ShowReportofAttendance();
                        report_attendance.execute();
                    } else {

                        attendancelist.setVisibility(View.GONE);
                    }
                }
            });
        }

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                final TextView tv_type = (TextView) view.findViewById(R.id.tv_type);
                final TextView tv_product = (TextView) view.findViewById(R.id.tv_product);


                final Dialog d = new Dialog(ReportsForUser.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setContentView(R.layout.popup_report);


                final Dialog d2 = new Dialog(ReportsForUser.this);
                d2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d2.setContentView(R.layout.popup_report);
                final TextView productid = (TextView) view.findViewById(R.id.tv_productid);

                tv_type.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub


                        String ppid = productid.getText().toString();

                        db.open();
                        String produ = db.getproductType123(ppid);
                        db.close();

                        if (d2 != null && d2.isShowing() && !ReportsForUser.this.isFinishing()) {
                            d2.dismiss();
                        }

                        TextView text = (TextView) d.findViewById(R.id.textView1);

                        text.setText(produ);
                        d.show();
                    }
                });

                tv_product.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub


                        String ppid = productid.getText().toString();

                        db.open();
                        String produ = db.getproductname123(ppid);
                        db.close();

                        if (d != null && d.isShowing() && !ReportsForUser.this.isFinishing()) {
                            d.dismiss();
                        }

                        TextView text = (TextView) d2.findViewById(R.id.textView1);

                        text.setText(produ);
                        d2.show();
                    }
                });
				
				/*final Dialog d = new Dialog(ReportsForUser.this);
				d.requestWindowFeature(Window.FEATURE_NO_TITLE);
				d.setContentView(R.layout.popup_report);

				TextView text = (TextView)d.findViewById(R.id.textView1);
				
				text.setText(tv_type.getText().toString());
				d.show();*/

                Log.e("", "" + tv_type.getText().toString() + " " + tv_product.getText().toString());


            }

        });

        // return view;
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
                            listview.setAdapter(null);
                            if (adapter != null) {
                                listview.setVisibility(View.VISIBLE);
                                listview.setAdapter(adapter);
                            }
                        }
                    });

                }
                if (mProgress != null && mProgress.isShowing() && !ReportsForUser.this.isFinishing()) {
                    mProgress.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

			

			/*Log.e("", "reportlist==="+reportlist);
			mProgress.dismiss();
			// TODO Auto-generated method stub
			listview.setVisibility(View.VISIBLE);
			adapter = new ReportAdapter(getApplicationContext(), reportlist);
			listview.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			db.close();*/

            // }
        }

    }

    public class ShowReportofTester extends AsyncTask<String, String, ArrayList<HashMap<String, String>>> {

        String flag;
        String t_catid, t_dbid, t_eancode, t_status;

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

            Log.e("", "checkrrr2t");
            db.open();
            cursor_tester = db.getReportforTester();

            if (cursor_tester != null && cursor_tester.moveToFirst()) {
                cursor_tester.moveToFirst();

                do {
                    Log.e("", "checkrrr3t");

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put("product_id", cursor_tester.getString(2));
                    map.put("db_id", cursor_tester.getString(3));
                    map.put("eancode", cursor_tester.getString(4));

                    map.put("category", cursor_tester.getString(5));
                    map.put("type", cursor_tester.getString(6));
                    map.put("product", cursor_tester.getString(7));
                    // map.put("opening", cursor_tester.getString(9));
                    // map.put("sold", cursor_tester.getString(12));
                    // map.put("return", cursor_tester.getString(11));
                    // map.put("closing", cursor_tester.getString(10));
                    map.put("size", cursor_tester.getString(8));

                    map.put("product_status", cursor_tester.getString(10));
                    reportlist1.add(map);

                } while (cursor_tester.moveToNext());

            } else {
                flag = "1";

            }
            return reportlist1;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {

            HashMap<String, String> map = new HashMap<String, String>();

            Log.e("", "check3");
            if (mProgress != null && mProgress.isShowing() && !ReportsForUser.this.isFinishing()) {
                mProgress.dismiss();
            }
            // TODO Auto-generated method stub
            listview_t.setVisibility(View.VISIBLE);
            adapter1 = new ReportTester(getApplicationContext(), result);
            listview_t.setAdapter(adapter1);
            db.close();

            listview_t.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub

                    final TextView catid = (TextView) view.findViewById(R.id.tv_r_catid);
                    final TextView dbid = (TextView) view.findViewById(R.id.tv_r_db_id);
                    final TextView enacode = (TextView) view.findViewById(R.id.tv_r_eanid);

                    TextView product_cat = (TextView) view.findViewById(R.id.tv_category);
                    TextView product_type = (TextView) view.findViewById(R.id.tv_type);
                    TextView product_name = (TextView) view.findViewById(R.id.tv_product);

                    TextView product_status = (TextView) view.findViewById(R.id.tv_product_status);

                    //TextView status = (TextView)view.findViewById(R.id.tv_product_status);

                    final String p_cat = product_cat.getText().toString();
                    final String p_type = product_type.getText().toString();
                    final String p_name = product_name.getText().toString();

                    t_catid = catid.getText().toString();
                    t_dbid = dbid.getText().toString();
                    t_eancode = enacode.getText().toString();

                    product_status.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(
                                    ReportsForUser.this);
                            dlgAlert.setMessage("Do you get products as tester?");
                            dlgAlert.setTitle("Important");

                            dlgAlert.setCancelable(true);
                            dlgAlert.setPositiveButton("YES",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // dismiss the dialog
                                            //Log.v("","catid=="+t_catid+" dbid=="+t_dbid+" eancode=="+t_eancode);
                                            //update_tester(t_catid,t_dbid,t_eancode);
                                            Log.v("", "cat==" + p_cat + " p_type==" + p_type);
                                            update_tester(t_dbid);
                                            reportlist1.clear();
                                            new ShowReportofTester().execute();


                                        }
                                    });
                            dlgAlert.setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // dismiss the dialog
//											tabHost.getTabWidget().getChildTabViewAt(0).setEnabled(false);
                                            onResume();
//											
                                        }
                                    });


                            dlgAlert.create().show();//--------------------------06.11.2014
                        }
                    });

                    final Dialog d = new Dialog(ReportsForUser.this);
                    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    d.setContentView(R.layout.popup_report);

                    final Dialog d2 = new Dialog(ReportsForUser.this);
                    d2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    d2.setContentView(R.layout.popup_report);

                    product_type.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            String ppid = dbid.getText().toString();
                            Log.e("", "ppid=" + ppid);
                            db.open();
                            String produ = db.getproductType123(ppid);
                            db.close();

                            if (d2 != null && d2.isShowing() && !ReportsForUser.this.isFinishing()) {
                                d2.dismiss();
                            }

                            TextView text = (TextView) d.findViewById(R.id.textView1);
                            Log.e("", "produ=" + produ);
                            text.setText(produ);
                            d.show();
                        }
                    });

                    product_name.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            String ppid = dbid.getText().toString();
                            Log.e("", "ppid=" + ppid);
                            db.open();
                            String produ = db.getproductname123(ppid);
                            db.close();

                            if (d != null && d.isShowing() && !ReportsForUser.this.isFinishing()) {
                                d.dismiss();
                            }

                            TextView text = (TextView) d2.findViewById(R.id.textView1);
                            Log.e("", "produ=" + produ);
                            text.setText(produ);
                            d2.show();
                        }
                    });

                }


            });


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

    public void update_tester(String dbid) {
        Log.v("", "dfsfsfsf");

        db.open();
        db.UpdateTesterStatus(dbid);
        db.close();
    }

    public class ReportTester extends BaseAdapter {

        Context context1;
        private ArrayList<HashMap<String, String>> data;
        private LayoutInflater inflater1 = null;
        String receiver;

        ViewHolder viewHolder;

        ReportsForUser fff;

        public ReportTester(Context context, ArrayList<HashMap<String, String>> d) {

            context1 = context;
            data = d;
            inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            fff = new ReportsForUser();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        class ViewHolder {
            TextView category;
            TextView type;
            TextView product;
            TextView size;
            TextView product_status;
            Button btn_change_status;

            TextView catid, dbid, enacode;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            if (convertView == null) {
                Log.e("", "check1");
                viewHolder = new ViewHolder();//create new holder
                convertView = inflater1.inflate(R.layout.report_layout1, null);//inflater for view
                Log.e("", "check4");

                viewHolder.category = (TextView) convertView.findViewById(R.id.tv_category);
                viewHolder.type = (TextView) convertView.findViewById(R.id.tv_type);
                viewHolder.product = (TextView) convertView.findViewById(R.id.tv_product);
                //viewHolder.op = k(TextView)convertView.findViewById(R.id.tv_open);
                //viewHolder.sl = (TextView)convertView.findViewById(R.id.tv_sold);
                //viewHolder.rt = (TextView)convertView.findViewById(R.id.tv_return);
                //	viewHolder.cl = (TextView)convertView.findViewById(R.id.tv_close);
                viewHolder.size = (TextView) convertView.findViewById(R.id.tv_product_size_tester);
                viewHolder.product_status = (TextView) convertView.findViewById(R.id.tv_product_status);


                viewHolder.catid = (TextView) convertView.findViewById(R.id.tv_r_catid);
                viewHolder.dbid = (TextView) convertView.findViewById(R.id.tv_r_db_id);
                viewHolder.enacode = (TextView) convertView.findViewById(R.id.tv_r_eanid);

                convertView.setTag(viewHolder);


            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //display the messages in view
            HashMap<String, String> map = new HashMap<String, String>();
            map = data.get(position);


            viewHolder.category.setText(map.get("category"));
            viewHolder.type.setText(map.get("type"));
            viewHolder.product.setText(map.get("product"));
            //	viewHolder.op.setText(map.get("opening"));
            //	viewHolder.sl.setText(map.get("sold"));
            //	viewHolder.rt.setText(map.get("return"));
            //	viewHolder.cl.setText(map.get("closing"));
            //	viewHolder.size.setText(map.get("size"));

            viewHolder.catid.setText(map.get("product_id"));
            viewHolder.dbid.setText(map.get("db_id"));
            viewHolder.enacode.setText(map.get("eancode"));
            viewHolder.product_status.setText(map.get("product_status"));
            viewHolder.size.setText(map.get("size"));


            return convertView;

        }


    }

    private void getNetAmountSum() {
        db.open();
        int sum_lh = db.getReportSum("SKIN");
        int sum_lhm = db.getReportSum("COLOR");

        //txt_lh.setText("= "+"\u20B9 " +String.valueOf(sum_lh));
        //txt_lhm.setText("= "+"\u20B9 " +String.valueOf(sum_lhm));

    }

    private void ShowingReportTotalSum(String category) {
        try {
            db.open();

            Cursor c = db.getReportTotalSum(category);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String opening = c.getString(2);
                    String sold = c.getString(4);
                    String closing = c.getString(3);
                    String gross = c.getString(1);
                    String net = c.getString(0);
                    if (opening == null || opening.equalsIgnoreCase("null")) {
                        opening = "0";
                    } else {
                        opening = opening;
                    }
                    if (sold == null || sold.equalsIgnoreCase("null")) {
                        sold = "0";
                    } else {
                        sold = sold;
                    }
                    if (closing == null || closing.equalsIgnoreCase("null")) {
                        closing = "0";
                    } else {
                        closing = closing;
                    }
                    if (gross == null || gross.equalsIgnoreCase("null")) {
                        gross = "0";
                    } else {
                        gross = gross;
                    }
                    if (net == null || net.equalsIgnoreCase("null")) {
                        net = "0";
                    } else {
                        net = net;
                    }

                    op_total.setText("\u20B9 " + opening);
                    sold_total.setText("\u20B9 " + sold);
                    Closing_total.setText("\u20B9 " + closing);
                    gross_total.setText("\u20B9 " + gross);
                    net_total.setText("\u20B9 " + net);

                } while (c.moveToNext());
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
