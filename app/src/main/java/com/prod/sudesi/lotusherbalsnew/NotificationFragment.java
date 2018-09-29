package com.prod.sudesi.lotusherbalsnew;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.adapter.NotificationAdapter;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;


public class NotificationFragment extends ListActivity {

    Context context;
    private Uri outputFileUri;

    private String pathCamera = "";

    //shredpreference
    private SharedPreferences sharedpre = null;

    private SharedPreferences.Editor saveuser = null;

    SharedPreferences sp;
    SharedPreferences.Editor spe;
    //

    private File root = null;

    private String fname = "";

    private String scannedId;

    static public ArrayList<HashMap<String, String>> todaymessagelist = new ArrayList<HashMap<String, String>>();


    private TextView welcome;

    private ListView listView;

    private NotificationAdapter adapter;

    //private Button btn_back, btn_next,preview_button;

    //Button back, home;


    String EmpID,bdename;


    LotusWebservice service;

    private EditText image_description;

    private ProgressDialog mProgress = null;

    int soapresultforvisibilityid;

    private double lon = 0.0, lat = 0.0;

    TextView tv_h_username;
    Button btn_home, btn_logout;
    String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_notification);
        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        context = getApplicationContext();

        listView = (ListView) findViewById(android.R.id.list);

        sp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
        spe = sp.edit();

        mProgress = new ProgressDialog(NotificationFragment.this);
        service = new LotusWebservice(NotificationFragment.this);


        EmpID = sp.getString("username", "");
        bdename = sp.getString("BDEusername","");
        Log.e("", "username==" + EmpID);

        //------------------

        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        username = sp.getString("username", "");
        tv_h_username.setText(bdename);

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

        //	DisplayMetrics displayMetrics = new DisplayMetrics();
        //WindowManager wm = (WindowManager) getActivity()
        //	.getSystemService(Context.WINDOW_SERVICE);  // the results will
        // be higher than
        // using the
        // activity context
        // object or the
        // getWindowManager()
        // shortcut
        //	wm.getDefaultDisplay().getMetrics(displayMetrics);

        //	int screenHeight = displayMetrics.heightPixels - 120;

        //	listView.setMinimumHeight(screenHeight);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        try {
            new SendToSeverImageData().execute();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }


    }


    public class SendToSeverImageData extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub


            String returnMessage = null;
            try {
                todaymessagelist.clear();
                getTodayMessageWebservice();//soap messag call
            } catch (Exception e) {
                returnMessage = e.getMessage();
            }
            return returnMessage;


        }

        @Override
        protected void onPreExecute() {

            //System.out.println("running in stage 1******");
            mProgress.setMessage("Sending..");
            mProgress.show();
            mProgress.setCancelable(false);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if (mProgress != null && mProgress.isShowing() && !NotificationFragment.this.isFinishing()) {
                mProgress.dismiss();
            }
            loadmessage();
        }

    }


    private void getTodayMessageWebservice() {
        try {


            // //soap call
            Log.e("", "not2");
            SoapObject resultsRequestSOAP = service.GetNotification(EmpID);

            if (resultsRequestSOAP != null) {
                Log.e("", "not3");
                //	todaymessagelist.clear();
                Log.e("", "not4");
                // soap response
                for (int i = 0; i < resultsRequestSOAP.getPropertyCount(); i++) {
                    Log.e("", "not5");

                    SoapObject getmessaage = (SoapObject) resultsRequestSOAP
                            .getProperty(i);
                    HashMap<String, String> map = new HashMap<String, String>();

                    // display messages by adding it to listview
                    if (!String.valueOf(getmessaage.getProperty("CreatedDate"))
                            .equals("false")) {

                        if (String.valueOf(getmessaage.getProperty("Message")).equals("anyType{}")) {

                            map.put("Message", "");
                        } else {


                            map.put("Message", String.valueOf(getmessaage.getProperty("Message")));
                        }

                        map.put("Receiver", String.valueOf(getmessaage.getProperty("Sender")));

                        map.put("Date", String.valueOf(getmessaage.getProperty("CreatedDate")));

                        todaymessagelist.add(map);
                    }
                }
            } else {

                Toast.makeText(NotificationFragment.this, "Connectivity Error!!", Toast.LENGTH_SHORT).show();

            }

            Log.e("", "todaymessagelist=" + todaymessagelist.toString());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void loadmessage() {
        adapter = new NotificationAdapter(NotificationFragment.this, todaymessagelist);
        setListAdapter(adapter);// add custom adapter to
        // listview
    }


}

	

