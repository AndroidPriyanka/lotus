package com.prod.sudesi.lotusherbalsnew;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.ksoap2.serialization.SoapPrimitive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

public class ChangePassword extends Activity {

    EditText et_new_password;
    EditText et_old_password;
    EditText et_confirm_password;

    Button btn_submit;

    TextView tv_h_username;
    Button btn_home, btn_logout;
    String username;

    Context context;

    SharedPreferences shp;
    SharedPreferences.Editor shpeditor;

    Dbcon db;

    LotusWebservice service;

    SoapPrimitive soapObj = null;

    ContentValues contentvalues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_password);

        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        context = getApplicationContext();

        shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
        shpeditor = shp.edit();

        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        username = shp.getString("username", "");
        tv_h_username.setText(username);

        service = new LotusWebservice(ChangePassword.this);
        //we = new WriteErroLogs(ChangePassword.this);

        db = new Dbcon(this);


        et_old_password = (EditText) findViewById(R.id.et_old_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);

        btn_submit = (Button) findViewById(R.id.btn_change_password);


        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        username = shp.getString("username", "");
        tv_h_username.setText(username);

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
        //----

        btn_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String str_old_password = et_old_password.getText().toString().trim();
                String str_new_password = et_new_password.getText().toString().trim();
                String str_confirm_password = et_confirm_password.getText().toString().trim();

                Log.v("", "str_old_password=" + str_old_password);
                Log.v("", "str_new_password=" + str_new_password);
                Log.v("", "str_confirm_password=" + str_confirm_password);

                if (str_old_password.equalsIgnoreCase("")) {

                    et_old_password.setError("Please Enter Old Password");

                } else if (str_new_password.equalsIgnoreCase("")) {

                    et_new_password.setError("Please Enter New Password");

                } else if (str_confirm_password.equalsIgnoreCase("")) {

                    et_confirm_password.setError("Please Enter Confirm Password");


                } else {
                    String checkpassword = "";
                    db.open();
                    checkpassword = db.check_password_from_db(username, str_old_password);
                    db.close();

                    if (checkpassword.equalsIgnoreCase("0")) {

                        Toast.makeText(ChangePassword.this, "Please Enter Correct Old Password", Toast.LENGTH_LONG).show();

                    } else {
                        Log.v("", "str_confirm_password=" + str_confirm_password);
                        if (str_new_password.equals(str_confirm_password)) {


                            new ChnagePassword_Async().execute(str_new_password);

                        } else {

                            Toast.makeText(ChangePassword.this, "New Password and Confirm Password not matching", Toast.LENGTH_LONG).show();
                        }

                    }

                }


            }
        });


    }

    //public class ccc extends AsyncTask<Params, Progress, Result>

    public class ChnagePassword_Async extends AsyncTask<String, Void, SoapPrimitive> {

        private Context context;

        public void ChangePassword_Sync_Async(Context context) {
            this.context = context;
        }

        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        String date = formatter.format(calendar.getTime());

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ChangePassword.this, "",
                    "Please wait ");
        }

        @Override
        protected SoapPrimitive doInBackground(String... params) {
            // TODO Auto-generated method stub


            try {

                String usercode = shp.getString("username", "");
                String password = params[0];


                Log.i("change password start", "change password start");

                Log.v("", "in doinb=" + usercode + " pass=" + password);
                soapObj = service.ChangePassword(usercode, password);

                if (soapObj != null) {

                    String flag = soapObj.toString();
                    Log.v("", "responst changepasswor=" + flag);
                    if (flag.equalsIgnoreCase("TRUE")) {


                        db.open();
                        db.updateChangepassword(usercode, password);
                        db.close();
                    }


                } else {
                    String errors = "Soap in giving null while CHANGE PASSWORD data";
                    //we.writeToSD(errors.toString());

                }


            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));


                //we.writeToSD(errors.toString());
            }
            return soapObj;
        }

        @Override
        protected void onPostExecute(SoapPrimitive result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (progressDialog != null && progressDialog.isShowing() && !ChangePassword.this.isFinishing()) {
                    progressDialog.dismiss();
                }
                if (result.equals(null)) {

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(
                            ChangePassword.this);
                    dlgAlert.setMessage("Server Issue!");
                    dlgAlert.setTitle("Change Password");
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // dismiss the dialog


                                }
                            });
                    dlgAlert.create().show();
                } else {
                    String result1 = result.toString();


                    if (result1.equalsIgnoreCase("N")) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(
                                ChangePassword.this);
                        dlgAlert.setMessage("Invalid Username!");
                        dlgAlert.setTitle("Change Password");
                        dlgAlert.setCancelable(true);
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // dismiss the dialog


                                    }
                                });
                        dlgAlert.create().show();
                    } else if (result1.equalsIgnoreCase("SE")) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(
                                ChangePassword.this);
                        dlgAlert.setMessage("Server Error!!");
                        dlgAlert.setTitle("Change Password");
                        dlgAlert.setCancelable(true);
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // dismiss the dialog


                                    }
                                });
                        dlgAlert.create().show();
                    } else if (result1.equalsIgnoreCase("TRUE")) {


                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(
                                ChangePassword.this);
                        dlgAlert.setMessage("Password Successfully Changed!");
                        dlgAlert.setTitle("Change Password");
                        dlgAlert.setCancelable(true);
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // dismiss the dialog


                                    }
                                });
                        dlgAlert.create().show();
                    }


                }

            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));

                e.printStackTrace();
                //we.writeToSD(errors.toString());
            }
        }

    }


}
