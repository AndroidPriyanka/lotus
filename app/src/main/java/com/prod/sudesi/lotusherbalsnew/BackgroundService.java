package com.prod.sudesi.lotusherbalsnew;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class BackgroundService extends Service {

    private boolean isRunning;
    private Context context;
    private ProgressDialog mProgress = null;
    ConnectionDetector cd;
    private Dbcon db;
    Cursor attendance_array, image_array, image_array1, test_array,stock_array, upload_image, upload_boc_daywise_data;
    LotusWebservice service;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    String username;
    ArrayList<HashMap<String, String>> listofimages = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> listofsyncerrorlog = new ArrayList<HashMap<String, String>>();
    Context this_context;
    int soapresultforvisibilityid;
    private Thread backgroundThread;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this_context=this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            // Do something here



            SoapPrimitive soap_result_img = null;

            SoapPrimitive soap_result_img_data = null;

            SoapPrimitive soap_result_stock = null;

            SoapPrimitive soap_result_attendance = null;

            SoapPrimitive soap_result_tester = null;

            SoapObject Soap_result_image_name = null;

            SoapObject Soap_result_image_name1 = null;

            SoapPrimitive soap_result_upload_data = null;

            SoapPrimitive soap_result_upload_bocdaywise_data = null;

            String Erro_function = "";
            String ErroFlag = "";
            String Flag;

            cd = new ConnectionDetector(this_context);
            db = new Dbcon(this_context);
            service = new LotusWebservice(this_context);
            sp = this_context.getSharedPreferences("Lotus", this_context.MODE_PRIVATE);
            spe = sp.edit();

            username = sp.getString("username", "");

//                Toast.makeText(this_context, "Upload BrodCast Receivers", Toast.LENGTH_SHORT).show();


            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                // Toast.makeText(getActivity(),"Check Your Internet Connection!!!",
                // Toast.LENGTH_LONG).show();

                Flag = "3";
                // stop executing code by return

            } else {

                try {
                    Flag = "1";
                    ErroFlag = "1";
                    /*try {
                        db.open();

                        test_array = db.getTesterdetails(); // --------------
                        // db.close();

                        Log.e("test_array", DatabaseUtils.dumpCursorToString(test_array));

                        if (test_array.getCount() > 0) {

                            if (test_array != null && test_array.moveToFirst()) {
                                test_array.moveToFirst();

                                do {

                                    soap_result_tester = service
                                            .SaveTesterData(

                                                    test_array.getString(9),// EMPID
                                                    test_array.getString(3),// DB_ID
                                                    test_array.getString(15),// CAT_ID
                                                    test_array.getString(4),// ENA_CODE

                                                    test_array.getString(5),// PRODUCT_CAT
                                                    test_array.getString(6),// PRODUCT_TYPE
                                                    test_array.getString(7),// PRODUCT_NAME
                                                    test_array.getString(14),// SHADE_NO

                                                    test_array.getString(10),// PRODUCT_STATUS
                                                    test_array.getString(11),// REQUEST_DATE
                                                    test_array.getString(12),// DELIVER_DATE

                                                    test_array.getString(8)// SIZE

                                            );

                                    String localid = test_array.getString(0);
                                    Log.e("", "localid=" + localid);
                                    if (soap_result_tester != null) {
                                        String string_tester = soap_result_tester
                                                .toString();
                                        Log.v("", "string_tester="
                                                + string_tester);
                                        if (string_tester
                                                .equalsIgnoreCase("TRUE")) {
                                            Log.e("",
                                                    "id="
                                                            + test_array
                                                            .getString(0));
                                            db.open();
                                            db.update_tester_data(test_array
                                                    .getString(0));
                                            db.close();

                                        } else if (string_tester
                                                .equalsIgnoreCase("SE")) {

                                            ErroFlag = "0";
                                            Erro_function = "SaveTesterData()_SE";
                                            final Calendar calendar1 = Calendar
                                                    .getInstance();
                                            SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                    "MM/dd/yyyy HH:mm:ss");
                                            String Createddate = formatter1
                                                    .format(calendar1.getTime());

                                            int n = Thread.currentThread()
                                                    .getStackTrace()[2]
                                                    .getLineNumber();
                                            db.insertSyncLog(
                                                    "SaveTesterData_SE",
                                                    String.valueOf(n),
                                                    "SaveTesterData()",
                                                    Createddate,
                                                    Createddate,
                                                    sp.getString("username", ""),
                                                    "SaveTesterData()", "Fail");
                                        }
                                    } else {
                                        ErroFlag = "0";
                                        Erro_function = "SaveTesterData()-null";
                                        // String errors =
                                        // "Soap in giving null while 'Tester' and 'checkSyncFlag = 2' in  data Sync";
                                        // we.writeToSD(errors.toString());
                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "Internet Connection Lost, Soap in giving null while 'SaveTesterData'",
                                                String.valueOf(n),
                                                "SaveTesterData()",
                                                Createddate, Createddate,
                                                sp.getString("username", ""),
                                                "SaveTesterData()", "Fail");

                                    }

                                } while (test_array.moveToNext());

                            } else {

                            }

                        } else if (test_array == null) {

                        } else {
                            Log.e("No any  Tester data for upload ",
                                    String.valueOf(test_array.getCount()));
                        }

                    } catch (Exception e) {
                        ErroFlag = "0";
                        Erro_function = "SaveTesterData()";
                        e.printStackTrace();

                        String Error = e.toString();

                        final Calendar calendar1 = Calendar.getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(calendar1
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(Error, String.valueOf(n),
                                "SaveTesterData()", Createddate, Createddate,
                                sp.getString("username", ""),
                                "SaveTesterData()", "Fail");

                    }*/

                    /*try {
                        db.open();
                        attendance_array = db.getAttendanceData();

                        if (attendance_array.getCount() > 0) {

                            if (attendance_array != null
                                    && attendance_array.moveToFirst()) {
                                attendance_array.moveToFirst();

                                do {

                                    String empid = attendance_array
                                            .getString(1);
                                    Log.e("", "empid=" + empid);
                                    String date = attendance_array.getString(2);
                                    Log.e("", "date=" + date);

                                    String attendance = attendance_array
                                            .getString(3);
                                    Log.e("", "attendance=" + attendance);

                                    String absent_type = attendance_array
                                            .getString(4);
                                    Log.e("", "absent_type=" + absent_type);

                                    String lat = attendance_array.getString(5);
                                    Log.e("", "lat=" + lat);

                                    String lon = attendance_array.getString(5);
                                    Log.e("", "lon=" + lon);

                                    soap_result_attendance = service
                                            .SaveAttendance(attendance_array
                                                            .getString(1), date,
                                                    attendance_array
                                                            .getString(3),
                                                    attendance_array
                                                            .getString(4),
                                                    attendance_array
                                                            .getString(5),
                                                    attendance_array
                                                            .getString(6));

                                    if (soap_result_attendance != null) {
                                        String t = soap_result_attendance
                                                .toString();
                                        Log.v("", "soap_result_attendance=" + t);
                                        if (t.equalsIgnoreCase("TRUE")) {

                                            db.update_Attendance_data(attendance_array
                                                    .getString(0));
                                            db.close();

                                        } else if (t.equalsIgnoreCase("SE")) {

                                            ErroFlag = "0";
                                            Erro_function = "SaveAttendace()_SE";
                                            final Calendar calendar1 = Calendar
                                                    .getInstance();
                                            SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                    "MM/dd/yyyy HH:mm:ss");
                                            String Createddate = formatter1
                                                    .format(calendar1.getTime());

                                            int n = Thread.currentThread()
                                                    .getStackTrace()[2]
                                                    .getLineNumber();
                                            db.insertSyncLog(
                                                    "SaveAttendace_SE",
                                                    String.valueOf(n),
                                                    "SaveAttendance()",
                                                    Createddate,
                                                    Createddate,
                                                    sp.getString("username", ""),
                                                    "SaveAttendance()", "Fail");

                                        }
                                    } else {
                                        ErroFlag = "0";
                                        Erro_function = "SaveAttendace()-null";
                                        // String errors =
                                        // "Soap in giving null while 'Attendance' and 'checkSyncFlag = 2' in  data Sync";
                                        // we.writeToSD(errors.toString());
                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "Internet Connection Lost, Soap in giving null while 'SaveAttendace'",
                                                String.valueOf(n),
                                                "SaveAttendance()",
                                                Createddate, Createddate,
                                                sp.getString("username", ""),
                                                "SaveAttendance()", "Fail");

                                    }

                                } while (attendance_array.moveToNext());

                            }
                        } else if (attendance_array == null) {

                        } else {
                            Log.e("No any  Attendance data for upload ", String.valueOf(attendance_array.getCount()));
                        }

                    } catch (Exception e) {
                        ErroFlag = "0";
                        Erro_function = "Attendance";
                        e.printStackTrace();
                        String Error = e.toString();

                        final Calendar calendar1 = Calendar.getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(calendar1
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(Error, String.valueOf(n),
                                "SaveAttendance()", Createddate, Createddate,
                                sp.getString("username", ""),
                                "SaveAttendance()", "Fail");

                    }*/

                    /*try {
                        Log.e("", "saveto server1-stcok");
                        db.open();
                        stock_array = db.getStockdetails();
                        // db.close();
                        // ------------------

                        if (stock_array.getCount() > 0) {

                            if (stock_array != null
                                    && stock_array.moveToFirst()) {
                                stock_array.moveToFirst();

                                String shad;
                                do {

                                    Log.v("",
                                            "dbid=" + stock_array.getString(2));
                                    Log.v("",
                                            "category_id="
                                                    + stock_array.getString(1));
                                    Log.v("",
                                            "enacode="
                                                    + stock_array.getString(3));
                                    Log.v("",
                                            "empid=" + stock_array.getString(9));
                                    Log.v("",
                                            "product_category="
                                                    + stock_array.getString(4));
                                    Log.v("",
                                            "product_type="
                                                    + stock_array.getString(5));
                                    Log.v("",
                                            "product_name="
                                                    + stock_array.getString(6));

                                    Log.v("",
                                            "opening_stock="
                                                    + stock_array.getString(10));
                                    Log.v("",
                                            "stock_receive="
                                                    + stock_array.getString(11));
                                    Log.v("",
                                            "stock_inhand="
                                                    + stock_array.getString(12));
                                    Log.v("",
                                            "sold=" + stock_array.getString(16));
                                    Log.v("",
                                            "return_s="
                                                    + stock_array.getString(14));
                                    Log.v("",
                                            "return_ns="
                                                    + stock_array.getString(15));
                                    Log.v("",
                                            "close_bal="
                                                    + stock_array.getString(13));
                                    Log.v("",
                                            "t_gross="
                                                    + stock_array.getString(17));
                                    Log.v("",
                                            "discount="
                                                    + stock_array.getString(19));
                                    Log.v("",
                                            "net_amount="
                                                    + stock_array.getString(18));
                                    Log.v("",
                                            "size=" + stock_array.getString(7));
                                    Log.v("",
                                            "price=" + stock_array.getString(8));
                                    Log.v("",
                                            "insert_date"
                                                    + stock_array.getString(21));

                                    if (stock_array.getString(23) != null
                                            || !stock_array.getString(23)
                                            .equalsIgnoreCase("null")) {

                                        Log.v("",
                                                "shadeno="
                                                        + stock_array
                                                        .getString(23));
                                        shad = stock_array.getString(23)
                                                .toString();

                                    } else {
                                        Log.v("",
                                                "shadeno="
                                                        + stock_array
                                                        .getString(23));
                                        shad = "";
                                    }

                                    soap_result_stock = service.SaveStock(
                                            stock_array.getString(0),
                                            stock_array.getString(2),
                                            stock_array.getString(1),
                                            stock_array.getString(3), username,
                                            stock_array.getString(4),
                                            stock_array.getString(5),
                                            stock_array.getString(6), shad,

                                            stock_array.getString(10),
                                            stock_array.getString(11),
                                            stock_array.getString(12),

                                            stock_array.getString(16),
                                            stock_array.getString(14),
                                            stock_array.getString(15),

                                            stock_array.getString(13),
                                            stock_array.getString(17),

                                            stock_array.getString(19),
                                            stock_array.getString(18),
                                            stock_array.getString(7),
                                            stock_array.getString(8),
                                            stock_array.getString(21)

                                    );

									*//*
									 * soap_result_stock = service.SaveStock(
									 * stock_array.getString(2),
									 * stock_array.getString(1), eancode_string,
									 * username, stock_array.getString(4),
									 * stock_array.getString(5),
									 * stock_array.getString(6), shad,
									 *
									 * opening_stock_string,
									 * stock_receive_string,
									 * stock_in_hand_string,
									 *
									 * sold_string, return_salable_string,
									 * return_non_salable_string,
									 *
									 * close_bal_string, gross_amount_string,
									 *
									 * discount_string, net_amount_string,
									 * size_string, price_string,
									 * stock_array.getString(21)
									 *
									 *
									 * );
									 *//*

                                    if (soap_result_stock != null) {
                                        String result_stock = soap_result_stock
                                                .toString();
                                        Log.v("", "result_stock="
                                                + result_stock);
                                        if (result_stock.matches(".*\\d+.*")) {
                                            Log.e("", "stock id for update=="
                                                    + stock_array.getString(0));
                                            db.open();
                                            db.update_stock_data(result_stock);
                                            Log.d("Data is Updating here ",
                                                    result_stock);
                                            db.close();

                                        } else if (result_stock
                                                .equalsIgnoreCase("SE")) {

                                            ErroFlag = "0";
                                            Erro_function = "SaveStock()_SE";
                                            final Calendar calendar1 = Calendar
                                                    .getInstance();
                                            SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                    "MM/dd/yyyy HH:mm:ss");
                                            String Createddate = formatter1
                                                    .format(calendar1.getTime());

                                            int n = Thread.currentThread()
                                                    .getStackTrace()[2]
                                                    .getLineNumber();
                                            db.insertSyncLog("SaveStock_SE",
                                                    String.valueOf(n),
                                                    "SaveStock()", Createddate,
                                                    Createddate, sp.getString(
                                                            "username", ""),
                                                    "SaveStock()", "Fail");

                                        }

                                    } else {

                                        ErroFlag = "0";
                                        Erro_function = "SaveStock()-null";
                                        // String errors =
                                        // "Soap in giving null while 'Stock' and 'checkSyncFlag = 2' in  data Sync";
                                        // we.writeToSD(errors.toString());
                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "Internet Connection Lost, Soap in giving null while 'SaveStock'",
                                                String.valueOf(n),
                                                "SaveStock()", Createddate,
                                                Createddate,
                                                sp.getString("username", ""),
                                                "SaveStock()", "Fail");

                                    }

                                } while (stock_array.moveToNext());

                            } else {
                                // Toast.makeText(getActivity(),
                                // "No Data Available or Check Connectivity",
                                // .LENGTH_SHORT).show();
                                Flag = "2";
                                Log.e("", "no data available");

                            }

                        } else if (stock_array == null) {

                        } else {
                            Log.e("No any  Stock data for upload ",
                                    String.valueOf(stock_array.getCount()));
                        }

                    } catch (Exception e) {
                        ErroFlag = "0";
                        Erro_function = "SaveStock()";
                        e.printStackTrace();

                        String Error = e.toString();

                        final Calendar cal = Calendar.getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(cal.getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(Error, String.valueOf(n),
                                "SaveStock()", Createddate, Createddate,
                                sp.getString("username", ""), "SaveStock()",
                                "Fail");

                    }*/
                    // -----------------------------------boc day wise data
                    // -----------------------------------------------//
                    final Calendar calendar123 = Calendar.getInstance();
                    SimpleDateFormat formatter123 = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    String currentdate = formatter123.format(calendar123
                            .getTime());

                    try {
                        db.open();
                        upload_boc_daywise_data = db
                                .getBocDateWiseData(currentdate);
                        // db.close();
                        // ------------------

                        if (upload_boc_daywise_data != null) {

                            if (upload_boc_daywise_data.getCount() > 0) {

                                upload_boc_daywise_data.moveToFirst();

                                String shad;
                                do {

                                    Log.v("",
                                            "dbid="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("db_id")));
                                    Log.v("",
                                            "category_id="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("product_id")));
                                    Log.v("",
                                            "enacode="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("eancode")));
                                    Log.v("",
                                            "empid="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("emp_id")));
                                    Log.v("",
                                            "product_category="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("product_category")));
                                    Log.v("",
                                            "product_type="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("product_type")));
                                    Log.v("",
                                            "product_name="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("product_name")));

                                    Log.v("",
                                            "opening_stock="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("opening_stock")));
                                    Log.v("",
                                            "stock_receive="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("stock_received")));
                                    Log.v("",
                                            "stock_inhand="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("stock_in_hand")));
                                    Log.v("",
                                            "sold_stock="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("sold_stock")));
                                    Log.v("",
                                            "return_s="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("return_saleable")));
                                    Log.v("",
                                            "return_ns="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("return_non_saleable")));
                                    Log.v("",
                                            "close_bal="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("close_bal")));
                                    Log.v("",
                                            "t_gross="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("total_gross_amount")));
                                    Log.v("",
                                            "discount="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("discount")));
                                    Log.v("",
                                            "net_amount="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("total_net_amount")));
                                    Log.v("",
                                            "size="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("size")));
                                    Log.v("",
                                            "price="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("price")));
                                    Log.v("",
                                            "insert_date="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("insert_date")));

                                    Log.v("",
                                            "date="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("date")));
                                    Log.v("",
                                            "boc="
                                                    + upload_boc_daywise_data
                                                    .getString(upload_boc_daywise_data
                                                            .getColumnIndex("boc")));

                                    if (upload_boc_daywise_data
                                            .getString(upload_boc_daywise_data
                                                    .getColumnIndex("shadeNo")) != null
                                            || !upload_boc_daywise_data
                                            .getString(
                                                    upload_boc_daywise_data
                                                            .getColumnIndex("shadeNo"))
                                            .equalsIgnoreCase("null")) {

                                        Log.v("",
                                                "shadeno="
                                                        + upload_boc_daywise_data
                                                        .getString(upload_boc_daywise_data
                                                                .getColumnIndex("shadeNo")));
                                        shad = upload_boc_daywise_data
                                                .getString(upload_boc_daywise_data
                                                        .getColumnIndex("shadeNo"));

                                    } else {
                                        Log.v("",
                                                "shadeno="
                                                        + upload_boc_daywise_data
                                                        .getString(upload_boc_daywise_data
                                                                .getColumnIndex("shadeNo")));
                                        shad = "";
                                    }

                                    soap_result_upload_bocdaywise_data = service
                                            .SaveStockEveryDayNetvalue(
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("db_id")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("product_id")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("eancode")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("emp_id")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("product_category")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("product_type")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("product_name")),
                                                    shad,
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("opening_stock")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("stock_received")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("stock_in_hand")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("sold_stock")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("return_saleable")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("return_non_saleable")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("close_bal")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("total_gross_amount")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("discount")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("total_net_amount")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("size")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("price")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("insert_date")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("date")),
                                                    upload_boc_daywise_data
                                                            .getString(upload_boc_daywise_data
                                                                    .getColumnIndex("boc")));

                                    if (soap_result_upload_bocdaywise_data != null) {

                                        String soap_result_upload_bocdaywise_data1 = soap_result_upload_bocdaywise_data
                                                .toString();

                                        Log.v("",
                                                "soap_result_upload_bocdaywise_data1="
                                                        + soap_result_upload_bocdaywise_data1);

                                        if (soap_result_upload_bocdaywise_data1
                                                .equalsIgnoreCase("TRUE")) {

                                            Log.e("",
                                                    "boc wise stock id for update=="
                                                            + upload_boc_daywise_data
                                                            .getString(0));
                                            db.open();
                                            db.update_boc_wise_stock_data(upload_boc_daywise_data
                                                    .getString(0));
                                            db.close();

                                        } else if (soap_result_upload_bocdaywise_data1
                                                .equalsIgnoreCase("SE")) {

                                            ErroFlag = "0";
                                            Erro_function = "SaveStockEveryDayNetvalue()_SE";
                                            final Calendar cal = Calendar
                                                    .getInstance();
                                            SimpleDateFormat formatter11 = new SimpleDateFormat(
                                                    "MM/dd/yyyy HH:mm:ss");
                                            String Createddate = formatter11
                                                    .format(cal.getTime());

                                            int n = Thread.currentThread()
                                                    .getStackTrace()[2]
                                                    .getLineNumber();
                                            db.insertSyncLog(
                                                    "SaveStockEveryDayNetvalue_SE",
                                                    String.valueOf(n),
                                                    "SaveStockEveryDayNetvalue()",
                                                    Createddate,
                                                    Createddate,
                                                    sp.getString("username", ""),
                                                    "SaveStockEveryDayNetvalue()",
                                                    "Fail");

                                        }

                                    } else {

                                        ErroFlag = "0";
                                        Erro_function = "SaveStockEveryDayNetvalue()-null";
                                        // String errors =
                                        // "Soap in giving null while 'Stock' and 'checkSyncFlag = 2' in  data Sync";
                                        // we.writeToSD(errors.toString());
                                        final Calendar cal = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter11 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter11
                                                .format(cal.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "Internet Connection Lost, Soap in giving null while 'SaveStockEveryDayNetvalue'",
                                                String.valueOf(n),
                                                "SaveStockEveryDayNetvalue()",
                                                Createddate, Createddate,
                                                sp.getString("username", ""),
                                                "SaveStockEveryDayNetvalue()",
                                                "Fail");

                                    }

                                } while (upload_boc_daywise_data.moveToNext());

                            } else {
                                // Toast.makeText(getActivity(),
                                // "No Data Available or Check Connectivity",
                                // .LENGTH_SHORT).show();
                                Flag = "2";
                                Log.e("", "no data available");

                            }

                        } else if (soap_result_upload_bocdaywise_data == null) {

                        } else {
                            Log.e("No any  SaveStockEveryDayNetvalue data for upload ",
                                    String.valueOf(stock_array.getCount()));
                        }

                    } catch (Exception e) {
                        ErroFlag = "0";
                        Erro_function = "SaveStockEveryDayNetvalue()";
                        e.printStackTrace();

                        String Error = e.toString();

                        final Calendar cal = Calendar.getInstance();
                        SimpleDateFormat formatter11 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter11.format(cal.getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(Error, String.valueOf(n),
                                "SaveStockEveryDayNetvalue()", Createddate,
                                Createddate, sp.getString("username", ""),
                                "SaveStockEveryDayNetvalue()", "Fail");

                    }

                    // ----------------------------visibility

                   /* try {

                        Log.e("in weservice", "statrt- visibility");

                        String saveserver = "0";
                        db.open();

                        image_array = db.getscanDetails(saveserver);

                        if (image_array.getCount() > 0) {

                            if (image_array != null
                                    && image_array.moveToFirst()) {
                                image_array.moveToFirst();

                                Log.e("in weservice", "statrt3");

                                do {
                                    Log.e("in weservice", "statrt4");
                                    soap_result_img = service.SaveVisibility(
                                            image_array.getString(2),// 2
                                            image_array.getString(1),// 1
                                            image_array.getString(3),// 3
                                            image_array.getString(6),// 6
                                            image_array.getString(7),// 7
                                            image_array.getString(8)// 8
                                    );
                                    Log.e("in weservice", "Result="
                                            + soap_result_img);
                                    if (soap_result_img != null) {

                                        db.update_scan_data(image_array
                                                .getString(0));

                                        String scanid = image_array
                                                .getString(0);

                                        image_array1 = db
                                                .getimageDetails(scanid);

                                        String spr = soap_result_img.toString();

                                        soapresultforvisibilityid = Integer
                                                .parseInt(spr);

                                        Log.e("in weservice", "image_array1 = "
                                                + image_array1.toString());

                                        if (image_array1 != null
                                                && image_array1.moveToFirst()) {

                                            image_array1.moveToFirst();

                                            do {

                                                db.update_visibility_id(spr,
                                                        image_array1
                                                                .getString(1));

                                                // //////////////////////new
                                                // code putting
                                                Log.v("", "test123");
                                                upload_image = db
                                                        .getimageDetails1();

												*//*
												 * if (upload_image != null &&
												 * upload_image.moveToFirst()) {
												 *
												 * upload_image.moveToFirst();
												 * do{ Log.v("",
												 * "visibility_id="
												 * +upload_image.getString(5));
												 * Log.v("",
												 * "visibility_id="+upload_image
												 * .getString(6));
												 *
												 * }while(upload_image.moveToNext
												 * ());
												 *
												 *
												 * }
												 *//*
                                                if (upload_image.getCount() > 0) {

                                                    if (upload_image != null
                                                            && upload_image
                                                            .moveToFirst()) {

                                                        upload_image
                                                                .moveToFirst();

                                                        do {

                                                            Log.e("image path.==",
                                                                    ""
                                                                            + upload_image
                                                                            .getString(2));

                                                            Log.e("imagecapture_date.==",
                                                                    ""
                                                                            + upload_image
                                                                            .getString(4));
                                                            Log.e("IMAGE.==",
                                                                    "IMAGE_NAME=="
                                                                            + upload_image
                                                                            .getString(5));

                                                            int visibilityid = 0;

                                                            if (upload_image
                                                                    .getString(
                                                                            6)
                                                                    .toString() != null) {

                                                                visibilityid = Integer
                                                                        .parseInt(upload_image
                                                                                .getString(
                                                                                        6)
                                                                                .toString());

                                                                soap_result_upload_data = service
                                                                        .UploadImage(
                                                                                upload_image
                                                                                        .getString(2),
                                                                                // image_array1.getString(2),
                                                                                upload_image
                                                                                        .getString(5),
                                                                                visibilityid,
                                                                                upload_image
                                                                                        .getString(4));

                                                                Log.e("in weservice",
                                                                        "Result="
                                                                                + soap_result_upload_data
                                                                                .toString());

                                                            } else {

                                                            }

                                                            if (soap_result_upload_data == null) {

                                                                // String errors
                                                                // =
                                                                // "Soap in giving null while 'IMAGE VISIBILITY2' and 'checkSyncFlag = 2' in  data Sync";
                                                                // we.writeToSD(errors.toString());

                                                                final Calendar calendar1 = Calendar
                                                                        .getInstance();
                                                                SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                                        "MM/dd/yyyy HH:mm:ss");
                                                                String Createddate = formatter1
                                                                        .format(calendar1
                                                                                .getTime());
                                                                ErroFlag = "0";
                                                                Erro_function = "Upload Image Data()- null";
                                                                int n = Thread
                                                                        .currentThread()
                                                                        .getStackTrace()[2]
                                                                        .getLineNumber();
                                                                db.insertSyncLog(
                                                                        "Internet Connection Lost, Soap in giving null while 'Upload Image Data'",
                                                                        String.valueOf(n),
                                                                        "UploadImage()",
                                                                        Createddate,
                                                                        Createddate,
                                                                        sp.getString(
                                                                                "username",
                                                                                ""),
                                                                        "Transaction Upload",
                                                                        "Fail");

                                                            } else if (soap_result_upload_data
                                                                    .toString()
                                                                    .equalsIgnoreCase(
                                                                            "SE")) {

                                                                final Calendar calendar1 = Calendar
                                                                        .getInstance();
                                                                SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                                        "MM/dd/yyyy HH:mm:ss");
                                                                String Createddate = formatter1
                                                                        .format(calendar1
                                                                                .getTime());
                                                                ErroFlag = "0";
                                                                Erro_function = "Upload Image Data()_SE";
                                                                int n = Thread
                                                                        .currentThread()
                                                                        .getStackTrace()[2]
                                                                        .getLineNumber();
                                                                db.insertSyncLog(
                                                                        "Visibility-Upload Image Data_SE",
                                                                        String.valueOf(n),
                                                                        "Upload Image Data()",
                                                                        Createddate,
                                                                        Createddate,
                                                                        sp.getString(
                                                                                "username",
                                                                                ""),
                                                                        "Transaction Upload",
                                                                        "Fail");

                                                            } else {

                                                                // String
                                                                // result_visibility
                                                                // =
                                                                // soap_result_img.toString();
                                                                String result_visibility1 = soap_result_upload_data
                                                                        .toString();
                                                                // Log.v("",
                                                                // "result_visibility="
                                                                // +
                                                                // result_visibility);
                                                                Log.v("",
                                                                        "result_visibility1="
                                                                                + result_visibility1);

                                                                if (result_visibility1
                                                                        .equalsIgnoreCase("TRUE")) {

                                                                    db.update_image_data(upload_image
                                                                            .getString(0));

                                                                }

                                                            }

                                                        } while (upload_image
                                                                .moveToNext());

                                                    }
                                                }
                                                // -----------------------------

                                                try {

                                                    Soap_result_image_name = service
                                                            .GetVisibilityNotReceivedImage(username);

                                                    Log.e("",
                                                            "Soap_result_image_name="
                                                                    + Soap_result_image_name
                                                                    .toString());

                                                    if (Soap_result_image_name != null) {

                                                        for (int i = 0; i < Soap_result_image_name
                                                                .getPropertyCount(); i++) {
                                                            Log.e("pm", "pm2");

                                                            Soap_result_image_name1 = (SoapObject) Soap_result_image_name
                                                                    .getProperty(i);

                                                            Log.e("pm", "pm3");

                                                            if (Soap_result_image_name1
                                                                    .getProperty("status") != null) {

                                                                if (Soap_result_image_name1
                                                                        .getProperty(
                                                                                "status")
                                                                        .toString()
                                                                        .equalsIgnoreCase(
                                                                                "C")) {
                                                                    Log.e("pm",
                                                                            "pm4");
                                                                    try {

                                                                        String VisibilityId = Soap_result_image_name1
                                                                                .getProperty(
                                                                                        "VisibilityId")
                                                                                .toString();
                                                                        String ImageName = Soap_result_image_name1
                                                                                .getProperty(
                                                                                        "ImageName")
                                                                                .toString();

                                                                        db.updateimageTable(
                                                                                VisibilityId,
                                                                                ImageName);

                                                                    } catch (Exception e) {

                                                                        e.printStackTrace();
                                                                    }

                                                                } else if (Soap_result_image_name1
                                                                        .getProperty(
                                                                                "status")
                                                                        .toString()
                                                                        .equalsIgnoreCase(
                                                                                "E")) {

                                                                    HashMap<String, String> map = new HashMap<String, String>();
                                                                    listofimages
                                                                            .clear();
                                                                    map.clear();

                                                                    db.open();
                                                                    listofimages = db
                                                                            .getimagesNOtUploaded();

                                                                    if (listofimages
                                                                            .size() > 0) {

                                                                        Log.i("No. of  Syn Error Log ",
                                                                                String.valueOf(listofimages
                                                                                        .size()));

                                                                        for (int m = 0; m < listofimages
                                                                                .size(); m++) {

                                                                            map = listofimages
                                                                                    .get(m);

                                                                            String image_name = map
                                                                                    .get("image_name");
                                                                            String imagePath = map
                                                                                    .get("imagePath");

                                                                            String id = map
                                                                                    .get("imageId");

                                                                            String image_name_return = ImageUtils
                                                                                    .getCompressedImagePath(
                                                                                            imagePath,
                                                                                            "",
                                                                                            "",
                                                                                            image_name);

                                                                            if (image_name_return == null) {

                                                                                Log.e("",
                                                                                        "image_name_return="
                                                                                                + image_name_return);
                                                                            } else {

                                                                                Log.e("",
                                                                                        "image_name_return="
                                                                                                + image_name_return);

                                                                                db.update_image_data(id);

                                                                            }

                                                                        }

                                                                    } else {
                                                                        Log.e("",
                                                                                "No Images for Upload");
                                                                    }

                                                                } else if (Soap_result_image_name1
                                                                        .getProperty(
                                                                                "status")
                                                                        .toString()
                                                                        .equalsIgnoreCase(
                                                                                "N")) {

                                                                }
                                                            } else {

                                                            }
                                                        }
                                                    } else {

                                                    }

                                                } catch (Exception e) {

                                                    e.printStackTrace();
                                                }

                                                // /////////////////////code
                                                // ends here

                                                Log.e("image path.==",
                                                        ""
                                                                + image_array1
                                                                .getString(2));

                                                Log.e("imagecapture_date.==",
                                                        ""
                                                                + image_array1
                                                                .getString(4));
                                                Log.e("IMAGE.==",
                                                        "IMAGE_NAME==5"
                                                                + image_array1
                                                                .getString(5));

                                                soap_result_img_data = service
                                                        .UploadImage(
                                                                image_array
                                                                        .getString(2),
                                                                // image_array1.getString(2),
                                                                image_array1
                                                                        .getString(5),
                                                                soapresultforvisibilityid,
                                                                image_array1
                                                                        .getString(4));

                                                Log.e("in weservice",
                                                        "Result="
                                                                + soap_result_img_data
                                                                .toString());

                                                if (soap_result_img_data == null) {

                                                    // String errors =
                                                    // "Soap in giving null while 'IMAGE VISIBILITY2' and 'checkSyncFlag = 2' in  data Sync";
                                                    // we.writeToSD(errors.toString());

                                                    final Calendar calendar1 = Calendar
                                                            .getInstance();
                                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                            "MM/dd/yyyy HH:mm:ss");
                                                    String Createddate = formatter1
                                                            .format(calendar1
                                                                    .getTime());
                                                    ErroFlag = "0";
                                                    Erro_function = "Upload Image Data()- null";
                                                    int n = Thread
                                                            .currentThread()
                                                            .getStackTrace()[2]
                                                            .getLineNumber();
                                                    db.insertSyncLog(
                                                            "Internet Connection Lost, Soap in giving null while 'Visibility-Upload Image Data'",
                                                            String.valueOf(n),
                                                            "UploadImage()",
                                                            Createddate,
                                                            Createddate,
                                                            sp.getString(
                                                                    "username",
                                                                    ""),
                                                            "Transaction Upload",
                                                            "Fail");

                                                } else if (soap_result_img_data
                                                        .toString()
                                                        .equalsIgnoreCase("SE")) {

                                                    final Calendar calendar1 = Calendar
                                                            .getInstance();
                                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                            "MM/dd/yyyy HH:mm:ss");
                                                    String Createddate = formatter1
                                                            .format(calendar1
                                                                    .getTime());
                                                    ErroFlag = "0";
                                                    Erro_function = "Upload Image Data()_SE";
                                                    int n = Thread
                                                            .currentThread()
                                                            .getStackTrace()[2]
                                                            .getLineNumber();
                                                    db.insertSyncLog(
                                                            "Visibility-Upload Image Data_SE",
                                                            String.valueOf(n),
                                                            "Upload Image Data()",
                                                            Createddate,
                                                            Createddate,
                                                            sp.getString(
                                                                    "username",
                                                                    ""),
                                                            "Transaction Upload",
                                                            "Fail");

                                                } else {

                                                    String result_visibility = soap_result_img
                                                            .toString();
                                                    String result_visibility1 = soap_result_img_data
                                                            .toString();
                                                    Log.v("",
                                                            "result_visibility="
                                                                    + result_visibility);
                                                    Log.v("",
                                                            "result_visibility1="
                                                                    + result_visibility1);

                                                    if (result_visibility1
                                                            .equalsIgnoreCase("TRUE")) {

                                                        db.update_image_data(image_array1
                                                                .getString(0));

                                                    }
                                                    //
                                                    String image_name_return = ImageUtils_btimap
                                                            .getCompressedImagePath(
                                                                    image_array1
                                                                            .getString(2),
                                                                    this_context);
                                                    // .getCompressedImagePath(
                                                    // image_array1.getString(2),
                                                    // username,
                                                    // producttype,image_array1.getString(5));

                                                }

												*//*
												 * String image_name_return =
												 * ImageUtils
												 * .getCompressedImagePath(
												 * image_array1 .getString(2),
												 * username, producttype); if
												 * (image_name_return==null) {
												 *
												 *
												 * Log.e("",
												 * "image_name_return=" +
												 * image_name_return); } else {
												 *
												 * Log.e("",
												 * "image_name_return=" +
												 * image_name_return);
												 *
												 *
												 *
												 * }
												 *//*

                                            } while (image_array1.moveToNext());

                                        }
                                    } else {

                                        // String errors =
                                        // "Soap in giving null while 'IMAGE VISIBILITY' and 'checkSyncFlag = 2' in  data Sync";
                                        // we.writeToSD(errors.toString());

                                        ErroFlag = "0";
                                        Erro_function = "visibility()";
                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "Internet Connection Lost, Soap in giving null while 'Visibility-SaveVisibility Data'",
                                                String.valueOf(n),
                                                "SaveVisibility()",
                                                Createddate, Createddate,
                                                sp.getString("username", ""),
                                                "Transaction Upload", "Fail");

                                    }

                                } while (image_array.moveToNext());

                            } else {

                            }
                        } else if (image_array == (null)) {

                        } else {
                            Log.e("No any  image data for upload ",
                                    String.valueOf(image_array.getCount()));
                        }

						*//*
						 * if(soap_result_img != null && soap_result_img_data
						 * !=null &&
						 * !soap_result_img_data.toString().equalsIgnoreCase
						 * ("SE") ){
						 *
						 * String result_visibility =
						 * soap_result_img.toString(); String result_visibility1
						 * = soap_result_img_data.toString(); Log.v("",
						 * "result_visibility=" + result_visibility); Log.v("",
						 * "result_visibility1=" + result_visibility1);
						 *
						 *
						 *
						 * if (result_visibility1.equalsIgnoreCase("TRUE")) {
						 *
						 * db.update_scan_data(); db.update_image_data();
						 *
						 * db.close();
						 *
						 * } }
						 *//*
                    } catch (Exception e) {

                        e.printStackTrace();

                        ErroFlag = "0";
                        // Erro_function = "StoreErrorLogTablettxt_Upload";
                        String Error = e.toString();

                        final Calendar calendar1 = Calendar.getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(calendar1
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();
                        db.insertSyncLog(Error, String.valueOf(n),
                                "Visibility()", Createddate, Createddate,
                                sp.getString("username", ""), "Visibility()",
                                "Fail");

                    }*/

                    // -------------------------

                    try {

                        ContentValues contentvalues = new ContentValues();
                        HashMap<String, String> map = new HashMap<String, String>();
                        listofsyncerrorlog.clear();
                        map.clear();
                        db.open();
                        listofsyncerrorlog = db.GETERRORLOGS();

                        if (listofsyncerrorlog.size() > 0) {

                            Log.i("No. of  Syn Error Log ",
                                    String.valueOf(listofsyncerrorlog.size()));

                            String eid;
                            String Exception;
                            String Lineno;
                            String Method;
                            String username_r;
                            String sync_method;
                            String status;
                            String errordate;

                            for (int m = 0; m < listofsyncerrorlog.size(); m++) {

                                map = listofsyncerrorlog.get(m);

                                if (map.get("ID") != null) {

                                    eid = map.get("ID");
                                } else {

                                    eid = "";
                                }
                                // String eid = map.get("ID");
                                if (map.get("EXCEPTION") != null) {

                                    Exception = map.get("EXCEPTION");
                                } else {
                                    Exception = "";
                                }
                                // String Exception = map.get("EXCEPTION");
                                if (map.get("LINE_NO") != null) {
                                    Lineno = map.get("LINE_NO");

                                } else {
                                    Lineno = "";
                                }
                                if (map.get("METHOD") != null) {

                                    Method = map.get("METHOD");
                                } else {
                                    Method = "";
                                }
                                // String Lineno = map.get("LINE_NO");
                                // String Method = map.get("METHOD");
                                if (map.get("USERNAME") != null) {

                                    username_r = map.get("USERNAME");

                                } else {

                                    username_r = "";
                                }
                                if (map.get("SYNCMETHOD") != null) {

                                    sync_method = map.get("SYNCMETHOD");
                                } else {

                                    sync_method = "";
                                }

                                if (map.get("RESULT") != null) {

                                    status = map.get("RESULT");
                                } else {

                                    status = "";
                                }

                                if (map.get("CREATED_DATE") != null) {

                                    errordate = map.get("CREATED_DATE");
                                } else {

                                    errordate = "";
                                }
                                // String username = map.get("USERNAME");
                                // String sync_method = map
                                // .get("SYNCMETHOD");
                                // String status = map.get("RESULT");

                                SoapPrimitive soapObj123;
                                soapObj123 = service.StoreErrorLogTablettxt(
                                        Exception, Lineno, Method, username_r,
                                        sync_method, status, errordate);

                                if (soapObj123 != null) {

                                    String statussss = soapObj123.toString();

                                    if (statussss.equalsIgnoreCase("True")) {

                                        final Calendar calendar = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String date = formatter.format(calendar
                                                .getTime());

                                        contentvalues.clear();
                                        // contentvalues.put("LAST_SYNC",
                                        // date);
                                        contentvalues.put("FLAG", "U");

                                        /*db.update("SYNC_LOG", "FLAG = ?", new String[]{"U"},
                                                new String[]{"ID"}, new String[]{ eid});*/

                                        db.updatevalues("SYNC_LOG",
                                                contentvalues,

                                                "ID", eid);

                                    } else if (statussss.equalsIgnoreCase("SE")) {

                                        ErroFlag = "0";
                                        Erro_function = "StoreErrorLogTablettxt_Upload_SE";

                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();

                                        db.insertSyncLog(
                                                "StoreErrorLogTablettxt_Upload_SE",
                                                String.valueOf(n),
                                                "StoreErrorLogTablettxt()",
                                                Createddate, Createddate,
                                                sp.getString("username", ""),
                                                "StoreErrorLogTablettxt()",
                                                "Fail");

                                    }

                                } else {
                                    ErroFlag = "0";
                                    Erro_function = "StoreErrorLogTablettxt_Upload";

                                    final Calendar calendar1 = Calendar
                                            .getInstance();
                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                            "MM/dd/yyyy HH:mm:ss");
                                    String Createddate = formatter1
                                            .format(calendar1.getTime());

                                    int n = Thread.currentThread()
                                            .getStackTrace()[2].getLineNumber();

                                    db.insertSyncLog(
                                            "Internet Connection Lost, Soap in giving null while 'StoreErrorLogTablettxt'",
                                            String.valueOf(n),
                                            "StoreErrorLogTablettxt()",
                                            Createddate, Createddate,
                                            sp.getString("username", ""),
                                            "StoreErrorLogTablettxt()", "Fail");

                                    // String errors=
                                    // "Internet Connection lost!! Soap in giving null while 'StoreErrorLogTablettxt_Upload' and 'checkSyncFlag = 0' in Upload data Sync";
                                    // we.writeToSD(errors.toString());

                                    Log.e("Error in Sync Error log ", String
                                            .valueOf(listofsyncerrorlog.size()));
                                }

                            }

                        } else if (listofsyncerrorlog == null) {

                        } else {
                            Log.e("No any  Sync error for upload ",
                                    String.valueOf(listofsyncerrorlog.size()));

                        }

                    } catch (Exception e) {

                        StringWriter errors = new StringWriter();
                        e.printStackTrace(new PrintWriter(errors));

                        e.printStackTrace();
                        // we.writeToSD(errors.toString());
                        String error = e.toString();

                        final Calendar calendar1 = Calendar.getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(calendar1
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2]
                                .getLineNumber();

                        db.insertSyncLog(error, String.valueOf(n),
                                "StoreErrorLogTablettxt()", Createddate,
                                Createddate, sp.getString("username", ""),
                                "StoreErrorLogTablettxt()", "Fail");

                    }

                    // new method
                    db.open();
                    Cursor cursor = db.fetchallSpecifyMSelect(
                            "stock_monthwise", null, "savedServer = 0 ", null,
                            null);
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        do {
                            try {
                                String Pid = cursor.getString(2);
                                String CatCodeId = cursor.getString(1);
                                String EANCode = cursor.getString(3);
                                String empId = cursor.getString(9);
                                String ProductCategory = cursor.getString(4);
                                String product_type = cursor.getString(5);
                                String product_name = cursor.getString(6);
                                String shadeno = cursor.getString(23);
                                String Opening_Stock = cursor.getString(10);
                                String FreshStock = cursor.getString(11);
                                String Stock_inhand = cursor.getString(12);
                                String SoldStock = cursor.getString(16);
                                String S_Return_Saleable = cursor.getString(14);
                                String S_Return_NonSaleable = cursor
                                        .getString(15);
                                String ClosingBal = cursor.getString(13);
                                String GrossAmount = cursor.getString(17);
                                String Discount = cursor.getString(19);
                                String NetAmount = cursor.getString(18);
                                String Size = cursor.getString(7);
                                String Price = cursor.getString(8);
                                String AndroidCreatedDate = cursor
                                        .getString(21);
                                String Month = cursor.getString(25);

                                SoapPrimitive soap_result_cumm = service
                                        .InsertStockCummData(Pid, CatCodeId,
                                                EANCode, empId,
                                                ProductCategory, product_type,
                                                product_name, shadeno,
                                                Opening_Stock, FreshStock,
                                                Stock_inhand, SoldStock,
                                                S_Return_Saleable,
                                                S_Return_NonSaleable,
                                                ClosingBal, GrossAmount,
                                                Discount, NetAmount, Size,
                                                Price, AndroidCreatedDate,
                                                Month);

                                if (soap_result_cumm != null) {

                                    String result_stock_cumm = soap_result_cumm
                                            .toString();

                                    Log.v("", "result_stock_cumm="
                                            + result_stock_cumm);

                                    if (result_stock_cumm
                                            .equalsIgnoreCase("TRUE")) {

                                        Log.e("", "stock_cumm id for update=="
                                                + cursor.getString(0));

                                        db.open();
                                        db.update_stock_cumm(cursor
                                                .getString(0));
                                        db.close();

                                    } else if (result_stock_cumm
                                            .equalsIgnoreCase("SE")) {

                                        ErroFlag = "0";
                                        Erro_function = "InsertStockCummData()_SE";
                                        final Calendar calendar1 = Calendar
                                                .getInstance();
                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                                "MM/dd/yyyy HH:mm:ss");
                                        String Createddate = formatter1
                                                .format(calendar1.getTime());

                                        int n = Thread.currentThread()
                                                .getStackTrace()[2]
                                                .getLineNumber();
                                        db.insertSyncLog(
                                                "InsertStockCummData_SE",
                                                String.valueOf(n),
                                                "InsertStockCummData()",
                                                Createddate, Createddate,
                                                sp.getString("username", ""),
                                                "Data Upload", "Fail");

                                    }

                                } else {

                                    ErroFlag = "0";
                                    Erro_function = "InsertStockCummData()-null";
                                    // String errors =
                                    // "Soap in giving null while 'Stock' and 'checkSyncFlag = 2' in  data Sync";
                                    // we.writeToSD(errors.toString());
                                    final Calendar calendar1 = Calendar
                                            .getInstance();
                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                                            "MM/dd/yyyy HH:mm:ss");
                                    String Createddate = formatter1
                                            .format(calendar1.getTime());

                                    int n = Thread.currentThread()
                                            .getStackTrace()[2].getLineNumber();
                                    db.insertSyncLog(
                                            "Internet Connection Lost, Soap in giving null while 'InsertStockCummData'",
                                            String.valueOf(n),
                                            "InsertStockCummData()",
                                            Createddate, Createddate,
                                            sp.getString("username", ""),
                                            "Data Upload", "Fail");

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } while (cursor.moveToNext());
                    }
                    db.close();

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

}