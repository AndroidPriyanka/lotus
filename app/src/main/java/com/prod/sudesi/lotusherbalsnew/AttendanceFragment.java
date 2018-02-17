package com.prod.sudesi.lotusherbalsnew;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geofencing.model.GeofenceModel;
import io.nlopez.smartlocation.geofencing.utils.TransitionGeofence;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
/*import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;*/


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AttendanceFragment extends Activity implements OnClickListener, OnLocationUpdatedListener, OnActivityUpdatedListener, OnGeofencingTransitionListener {

    String attendance_flag;
    String leavetype_flag;
    private String attendanceDate1;
    private TextView currentMonth;
    private Button selectedDayMonthYearButton;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private GridView calendarView;
    private GridCellAdapter adapter;
    private Dbcon db = null;
    private Calendar _calendar;
    @SuppressLint("NewApi")
    private int month, year;
    String[] values;
    @SuppressWarnings("unused")
    @SuppressLint({"NewApi", "NewApi", "NewApi", "NewApi"})
    private final DateFormat dateFormatter = new DateFormat();
    private static final String dateTemplate = "MMMM yyyy";
    private double lon = 0.0, lat = 0.0;
    Context context = null;
    String attendanceDate = "", attendmonth;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    int day1, year1, month1;
    String username, bdename;
    ConnectionDetector cd;
    LotusWebservice service;
    private ProgressDialog pd;

    String month_h, year_h;

    TextView tv_h_username;
    Button btn_home, btn_logout;

    private ProgressDialog mProgress = null;

    SaveLogoutTime savelogout;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int LOCATION_PERMISSION_ID = 1001;
    private LocationGooglePlayServicesProvider provider;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";


    @SuppressLint("WrongConstant")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_attendance);

        //////////Crash Report
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        context = getApplicationContext();
        db = new Dbcon(context);


        mProgress = new ProgressDialog(AttendanceFragment.this);
        //service = new LotusWebservice(AttendanceFragment.this);

        btn_home = (Button) findViewById(R.id.btn_home);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        cd = new ConnectionDetector(context);
        pd = new ProgressDialog(context);
        service = new LotusWebservice(context);

        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);
        Log.d("", "Calendar Instance:= " + "Month: " + month + " " + "Year: "
                + year);

        sp = context.getSharedPreferences("Lotus", Context.MODE_PRIVATE);
        spe = sp.edit();

        try {
            Intent i = getIntent();

            String Chek = i.getStringExtra("FromLoginpage");

            if (Chek.equalsIgnoreCase("L")) {

                btn_home.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            // TODO: handle exception

            e.printStackTrace();

        }
        username = sp.getString("username", "");
        bdename = sp.getString("BDEusername", "");

        selectedDayMonthYearButton = (Button) findViewById(R.id.selectedDayMonthYear);
        selectedDayMonthYearButton.setText("Selected: ");

        prevMonth = (ImageView) findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));

        nextMonth = (ImageView) findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);


        //------------------

        tv_h_username = (TextView) findViewById(R.id.tv_h_username);
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
                startActivity(new Intent(getApplicationContext(), DashboardNewActivity.class));
            }
        });
        //---------------------

        calendarView = (GridView) findViewById(R.id.calendar);

        // Initialised
        adapter = new GridCellAdapter(context, R.id.calendar_day_gridcell,
                month, year);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);

        //	return view;

        initGoogleAPIClient();//Init Google API Client
        checkPermissions();//Check Permission
        if (ContextCompat.checkSelfPermission(AttendanceFragment.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AttendanceFragment.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;
        }

        startLocation();
    }

    @SuppressLint("WrongConstant")
    private void setGridCellAdapterToDate(int month, int year) {
        adapter = new GridCellAdapter(context, R.id.calendar_day_gridcell,
                month, year);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));

        currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
        Log.v("", "ccccccccc1");
        // getholiday(month,year);
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {

    }

    @Override
    public void onGeofenceTransition(TransitionGeofence transitionGeofence) {

    }

    @Override
    public void onLocationUpdated(Location location) {
        showLocation(location);
    }


    public class GridCellAdapter extends BaseAdapter implements OnClickListener {
        private static final String tag = "GridCellAdapter";
        private final Context _context;

        private final List<String> list;
        private static final int DAY_OFFSET = 1;
        private final String[] weekdays = new String[]{"Sun", "Mon", "Tue",
                "Wed", "Thu", "Fri", "Sat"};
        private final String[] months = {"January", "February", "March",
                "April", "May", "June", "July", "August", "September",
                "October", "November", "December"};
        private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30,
                31, 30, 31};
        private int daysInMonth;
        private int currentDayOfMonth;
        private int currentWeekDay;
        private Button gridcell;
        private TextView num_events_per_day;
        private ArrayList<HashMap<String, String>> eventsPerMonthMap = new ArrayList<HashMap<String, String>>();
        private final ArrayList<String> attendance = new ArrayList<String>();
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
                "dd-MMM-yyyy");

        // Days in Current Month
        public GridCellAdapter(Context context, int textViewResourceId,
                               int month, int year) {
            super();
            this._context = context;
            this.list = new ArrayList<String>();
            Log.d(tag, "==> Passed in Date FOR Month: " + month + " "
                    + "Year: " + year);
            Calendar calendar = Calendar.getInstance();
            setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
            setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
            Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
            Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
            Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

            // Print Month
            printMonth(month, year);

            // Find Number of Events
            eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
        }

        private String getMonthAsString(int i) {
            return months[i];
        }

        private String getWeekDayAsString(int i) {
            return weekdays[i];
        }

        private int getNumberOfDaysOfMonth(int i) {
            return daysOfMonth[i];
        }

        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * Prints Month
         *
         * @param mm
         * @param yy
         */
        private void printMonth(int mm, int yy) {
            Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
            int trailingSpaces = 0;
            int daysInPrevMonth = 0;
            int prevMonth = 0;
            int prevYear = 0;
            int nextMonth = 0;
            int nextYear = 0;

            int currentMonth = mm - 1;
            String currentMonthName = getMonthAsString(currentMonth);
            daysInMonth = getNumberOfDaysOfMonth(currentMonth);

            Log.d(tag, "Current Month: " + " " + currentMonthName + " having "
                    + daysInMonth + " days.");

            GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
            Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

            if (currentMonth == 11) {
                prevMonth = currentMonth - 1;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 0;
                prevYear = yy;
                nextYear = yy + 1;
                Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:"
                        + prevMonth + " NextMonth: " + nextMonth
                        + " NextYear: " + nextYear);
            } else if (currentMonth == 0) {
                prevMonth = 11;
                prevYear = yy - 1;
                nextYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 1;
                Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:"
                        + prevMonth + " NextMonth: " + nextMonth
                        + " NextYear: " + nextYear);
            } else {
                prevMonth = currentMonth - 1;
                nextMonth = currentMonth + 1;
                nextYear = yy;
                prevYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:"
                        + prevMonth + " NextMonth: " + nextMonth
                        + " NextYear: " + nextYear);
            }

            int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            trailingSpaces = currentWeekDay;

            Log.d(tag, "Week Day:" + currentWeekDay + " is "
                    + getWeekDayAsString(currentWeekDay));
            Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
            Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

            if (cal.isLeapYear(cal.get(Calendar.YEAR)))
                if (mm == 2)
                    ++daysInMonth;
                else if (mm == 3)
                    ++daysInPrevMonth;

            // Trailing Month days
            for (int i = 0; i < trailingSpaces; i++) {
                Log.d(tag,
                        "PREV MONTH:= "
                                + prevMonth
                                + " => "
                                + getMonthAsString(prevMonth)
                                + " "
                                + String.valueOf((daysInPrevMonth
                                - trailingSpaces + DAY_OFFSET)
                                + i));
                list.add(String
                        .valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
                                + i)
                        + "-GREY"
                        + "-"
                        + getMonthAsString(prevMonth)
                        + "-"
                        + prevYear);
            }

            // Current Month Days
            for (int i = 1; i <= daysInMonth; i++) {
                Log.d(currentMonthName, String.valueOf(i) + " "
                        + getMonthAsString(currentMonth) + " " + yy);
                if (i == getCurrentDayOfMonth()) {
                    list.add(String.valueOf(i) + "-BLUE" + "-"
                            + getMonthAsString(currentMonth) + "-" + yy);
                } else {
                    list.add(String.valueOf(i) + "-WHITE" + "-"
                            + getMonthAsString(currentMonth) + "-" + yy);
                }
            }

            // Leading Month days
            for (int i = 0; i < list.size() % 7; i++) {
                Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
                list.add(String.valueOf(i + 1) + "-GREY" + "-"
                        + getMonthAsString(nextMonth) + "-" + nextYear);
            }
        }

        /**
         * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
         * ALL entries from a SQLite database for that month. Iterate over the
         * List of All entries, and get the dateCreated, which is converted into
         * day.
         *
         * @param year
         * @param month
         * @return
         */
        @SuppressWarnings({"deprecation", "static-access"})
        private ArrayList<HashMap<String, String>> findNumberOfEventsPerMonth(
                int year, int month) {
            ArrayList<HashMap<String, String>> arraymap = new ArrayList<HashMap<String, String>>();

            String mon = String.valueOf(month);
            if (mon.length() == 1) {
                mon = "0" + mon;
            }
            DateFormat dateFormatter2 = new DateFormat();
            Log.e("mon===", String.valueOf(mon));
            Log.e("month===", String.valueOf(month));
            try {

                String colnames[] = new String[]{"Adate", "attendance", "year"};
                db.open();
                // Cursor c=db.fetchallOrder("attendance", colnames, null);
                Cursor c = db.fetchallSpecify("attendance", colnames, "month",
                        String.valueOf(month), null);
                Log.v("", "c======" + c.getCount());
                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();
                    do {

                        HashMap<String, String> map = new HashMap<String, String>();
                        Log.e("c.getString(0)", String.valueOf(c.getString(0)));
                        String d[] = c.getString(0).split("-");
                        //	String date = getmonthName(d[1]) + "-" + d[0] + "-"
                        //			+ d[2]; ------27.10.2014

                        String date = getmonthNo1(d[1]) + "-" + d[0] + "-"
                                + d[2];

                        Log.e("date", date);
                        //	Date dateCreated = new Date(date);
                        //	Log.e("dateCreated", String.valueOf(dateCreated));
                        //	String day = dateFormatter2.format("dd", dateCreated)
                        //			.toString();
                        //	String month1 = DateFormat.format("MM", dateCreated)
                        //			.toString();
                        String[] ddd = d[2].split(" ");

                        Log.v("", "ddd[0]==" + ddd[0]);
                        Log.v("", "ddd[1]==" + ddd[1]);

                        String yyyy = c.getString(2);
                        Log.v("", "yyyy==" + yyyy);

                        Log.e("day", ddd[0]);
                        Log.e("month1", d[1]);
                        //map.put("day", day);
                        //	map.put("month", month1);
                        map.put("day", ddd[0]);
                        map.put("month", d[1]);
                        map.put("attendance", c.getString(1));
                        map.put("year", yyyy);
                        arraymap.add(map);

                        Log.e("selected DateMAp", arraymap.toString());
                    } while (c.moveToNext());
                }
                db.close();
                // String[] dates =new
                // String[]{"May-24-2014","May-22-2014","May-14-2014"};
                //
                // for(int i=0;i<dates.length;i++)
                // {
                // Date dateCreated=new Date(dates[i]);
                // Log.e("dateCreated",String.valueOf(dateCreated));
                // String day = dateFormatter2.format("dd",
                // dateCreated).toString();
                // String month1=dateFormatter2.format("M",
                // dateCreated).toString();
                // String monthname="";
                // Log.e("day",day);
                // Log.e("month1",month1);
                //
                // Log.e("monthname",monthname);
                // map.put(day, monthname);
                //
                // Log.e("selected DateMAp",map.toString());
                // }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return arraymap;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) _context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.screen_gridcell, parent, false);
            }

            // Get a reference to the Day gridcell
            gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
            gridcell.setOnClickListener(this);

            // ACCOUNT FOR SPACING

            Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
            String[] day_color = list.get(position).split("-");

            String theday = day_color[0];
            String themonth = day_color[2];
            String theyear = day_color[3];
            Log.e("theday", theday);
            Log.e("themonth", themonth);
            Log.e("theyear", "theyear=" + theyear);

            Log.v("", "eventsPerMonthMap=" + eventsPerMonthMap.toString());
            String day = theday;
            if (day.length() == 1) {
                day = "0" + day;
            }
            if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {

                for (int i = 0; i < eventsPerMonthMap.size(); i++) {

                    Log.v("", "eventsPerMonthMap- year)=" + eventsPerMonthMap.get(i).get("year"));
                    Log.v("", "theyear==" + theyear);
                    if (eventsPerMonthMap.get(i).get("year")
                            .equals(theyear)) {
                        Log.e("", "inside year true");


                        if (eventsPerMonthMap.get(i).get("month")
                                .equals(getmonthNo(themonth)))

                        {

                            if (eventsPerMonthMap.get(i).get("day").equals(day)) {
                                if (eventsPerMonthMap.get(i).get("attendance")
                                        .equals("P")) {
                                    gridcell.setBackgroundResource(R.drawable.green);
                                } else if (eventsPerMonthMap.get(i)
                                        .get("attendance").equals("A")) {
                                    gridcell.setBackgroundResource(R.drawable.red);
                                } else if (eventsPerMonthMap.get(i)
                                        .get("attendance").equals("H")) {
                                    gridcell.setBackgroundResource(R.color.sky);
                                }
                            }
                        }
                    } else {

                        gridcell.setBackgroundResource(R.drawable.calendar_tile_small);
                    }
                }

            }

            // Set the Day GridCell
            gridcell.setText(theday);
            gridcell.setTag(theday + "-" + themonth + "-" + theyear);
            Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-"
                    + theyear);

            if (day_color[1].equals("GREY")) {
                gridcell.setTextColor(getResources()
                        .getColor(R.color.lightgray));
            }
            if (day_color[1].equals("WHITE")) {
                gridcell.setTextColor(getResources().getColor(
                        R.color.lightgray02));
            }
            if (day_color[1].equals("BLUE")) {
                gridcell.setTextColor(getResources().getColor(R.color.orrange));
            }
            return row;
        }

        @Override
        public void onClick(final View view) {

            if (cd.isConnectingToInternet()) {

                String serverddate = sp.getString("currentdate","");
                String[] parts = serverddate.split(" ");
                String serverdd = parts[0];

                String date_month_year = (String) view.getTag();

                Calendar c = Calendar.getInstance();
                year1 = c.get(Calendar.YEAR);
                month1 = c.get(Calendar.MONTH);
                day1 = c.get(Calendar.DAY_OF_MONTH);
                String currentDate = String.valueOf(day1) + "-" + "0"
                        + String.valueOf(month1 + 1) + "-" + String.valueOf(year1);
                Log.e("Selected date", date_month_year);
                Log.e("currentDate", currentDate);


                if (date_month_year != null) {
                    if (date_month_year.contains("-")) {
                        String d[] = date_month_year.split("-");

                        Log.v("checklength==", "" + d[0].length());
                        if (d[0].length() < 2) {
                            Log.v("", "checklength1==");
                            attendanceDate = "0" + d[0] + "-" + getmonthNo(d[1])
                                    + "-" + d[2];
                            attendmonth = getmonthNo(d[1]);

                        } else {
                            attendanceDate = d[0] + "-" + getmonthNo(d[1]) + "-"
                                    + d[2];
                            attendmonth = getmonthNo(d[1]);

                        }

                    }
                }
                Log.e("attendanceDate", attendanceDate);


                String d[] = date_month_year.split("-");
                String daa = "";
                if (d[0].length() > 0) {

                    daa = "0" + d[0];
                }
                String aattddate = d[2] + "-" + getmonthNo(d[1]) + "-"
                        + daa;
                Log.e("aattddate==", aattddate);
                // if(checkholiday(attendanceDate)){
                db.open();
                //	if ((db.isholiday(attendanceDate)).toString().length() > 0) {
                if ((db.isholiday(aattddate)).toString().length() > 0) {

                    Toast.makeText(context,
                            "Its holliday for " + db.isholiday(aattddate),
                            Toast.LENGTH_SHORT).show();
                    db.close();
                } else if (afterdateValidate(attendanceDate)) {
                    db.close();
                    Toast.makeText(context, "Select Current Date only",
                            Toast.LENGTH_SHORT).show();

                } else if (beforedatevalidate(attendanceDate, currentDate)) {
                    db.close();
                    Toast.makeText(context, "Select Current Date only",
                            Toast.LENGTH_SHORT).show();

                } else if (afterdateChangeValidate(serverdd)) {
                    Toast.makeText(context, "Please Check your Handset Date",
                            Toast.LENGTH_SHORT).show();
                } else {
                    db.close();

                    Button present;
                    Button absent;
                    Button out;
                    Button cancel;
                    try {

                        Date date = new Date();
                        SimpleDateFormat form = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");

                        attendanceDate1 = form.format(date);
                        Log.v("", "attendanceDate1=" + attendanceDate1);

                        String sld[] = attendanceDate1.split(" ");
                        final String sld1 = sld[0];

                        String ddd[] = sld1.split("-");
                        final String year = ddd[0];

                        Cursor c1 = null;
                        db.open();
                        c1 = db.getpreviousData1(sld1, username);

                        Log.v("", "c.getcount=" + c1.getCount());

                        if (c1 != null && c1.getCount() > 0) {
//
                            c1.moveToFirst();
                            String logout_status = c1.getString(c1.getColumnIndex("logout_status"));
                            if (logout_status != null) {

                                Log.e("logout_status", logout_status);
                                if (logout_status.equalsIgnoreCase("OUT")) {
                                    Toast.makeText(AttendanceFragment.this, "Attendance is marked", Toast.LENGTH_LONG).show();
                                } else {
                                    final Dialog dialog = new Dialog(AttendanceFragment.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.layout_out_attendance);

                                    out = (Button) dialog.findViewById(R.id.btn_out);
                                    cancel = (Button) dialog.findViewById(R.id.btn_cancel);

                                    out.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            // TODO Auto-generated method stub
                                            db.updateAttendance(sld1, username, sld1);
                                            savelogout = new SaveLogoutTime();
                                            savelogout.execute();

                                        }
                                    });

                                    cancel.setOnClickListener(new OnClickListener() {


                                        @Override
                                        public void onClick(View v) {

                                            // TODO Auto-generated method stub

                                            dialog.cancel();

                                        }
                                    });

                                    dialog.show();

                                }
                            } else {
                                final Dialog dialog = new Dialog(AttendanceFragment.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.layout_out_attendance);

                                out = (Button) dialog.findViewById(R.id.btn_out);
                                cancel = (Button) dialog.findViewById(R.id.btn_cancel);

                                out.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        db.updateAttendance(sld1, username, sld1);
                                        dialog.cancel();
                                        savelogout = new SaveLogoutTime();
                                        savelogout.execute();
                                    }
                                });

                                cancel.setOnClickListener(new OnClickListener() {


                                    @Override
                                    public void onClick(View v) {

                                        // TODO Auto-generated method stub

                                        dialog.cancel();

                                    }
                                });

                                dialog.show();

                            }


                        } else {


                            final Dialog dialog = new Dialog(AttendanceFragment.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.popup_attendance);

                            present = (Button) dialog.findViewById(R.id.btn_present);
                            absent = (Button) dialog.findViewById(R.id.btn_absent);
                            final String[] columns = new String[]{"emp_id", "Adate",
                                    "attendance", "absent_type", "lat", "lon", "savedServer", "month",
                                    "holiday_desc", "year"};


                            present.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    attendance_flag = "P";
                                    leavetype_flag = "";
                                    if (cd.isConnectingToInternet()) {
                                        view.setBackgroundResource(R.drawable.green);
                                        dialog.dismiss();

                                        try {
                                            new SaveAttendance().execute(attendance_flag, leavetype_flag);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        Toast.makeText(AttendanceFragment.this, "Please check internet Connectivity & Try Again", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                            absent.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    if (cd.isConnectingToInternet()) {

                                        // TODO Auto-generated method stub

                                        final Dialog d = new Dialog(AttendanceFragment.this);
                                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        d.setContentView(R.layout.absent_popup);

                                        RadioGroup rg_attendance_type = (RadioGroup) d
                                                .findViewById(R.id.rg_absent_type);

                                        final RadioButton rb_seek_leave = (RadioButton) d
                                                .findViewById(R.id.rb_seek_leave);

                                        final RadioButton rb_weekly_off = (RadioButton) d
                                                .findViewById(R.id.rb_weekly_off);

                                        final RadioButton rb_holiday = (RadioButton) d
                                                .findViewById(R.id.rb_holiday);


                                        rg_attendance_type
                                                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                                                    @Override
                                                    public void onCheckedChanged(
                                                            RadioGroup group, int checkedId) {
                                                        // TODO Auto-generated method stub
                                                        attendance_flag = "A";
                                                        if (rb_seek_leave.isChecked()) {

                                                            leavetype_flag = "Leave";

                                                            view.setBackgroundResource(R.drawable.red);
                                                            d.dismiss();
                                                            dialog.dismiss();

                                                            try {
                                                                new SaveAttendance().execute(attendance_flag, leavetype_flag);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }

                                                        if (rb_weekly_off.isChecked()) {
                                                            leavetype_flag = "Weekly Off";

                                                            view.setBackgroundResource(R.drawable.red);
                                                            d.dismiss();
                                                            dialog.dismiss();

                                                            try {
                                                                new SaveAttendance().execute(attendance_flag, leavetype_flag);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        if (rb_holiday.isChecked()) {
                                                            leavetype_flag = "Holiday";

                                                            view.setBackgroundResource(R.drawable.red);
                                                            d.dismiss();
                                                            dialog.dismiss();

                                                            try {
                                                                new SaveAttendance().execute(attendance_flag, leavetype_flag);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }


                                                    }
                                                });
                                        d.show();

                                    } else {
                                        Toast.makeText(AttendanceFragment.this, "Please check internet Connectivity & Try Again", Toast.LENGTH_LONG).show();
                                    }

                                }


                                /////
                            });

                            dialog.show();

                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            } else {
                Toast.makeText(AttendanceFragment.this, "Please ON your Mobile Internet", Toast.LENGTH_LONG).show();
            }
        }

        public int getCurrentDayOfMonth() {
            return currentDayOfMonth;
        }

        private void setCurrentDayOfMonth(int currentDayOfMonth) {
            this.currentDayOfMonth = currentDayOfMonth;
        }

        public void setCurrentWeekDay(int currentWeekDay) {
            this.currentWeekDay = currentWeekDay;
        }

        public int getCurrentWeekDay() {
            return currentWeekDay;
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == prevMonth) {
            if (month <= 1) {
                month = 12;
                year--;
            } else {
                month--;
            }
            Log.d("", "Setting Prev Month in GridCellAdapter: " + "Month: "
                    + month + " Year: " + year);

            setGridCellAdapterToDate(month, year);
        }
        if (v == nextMonth) {
            if (month > 11) {
                month = 1;
                year++;
            } else {
                month++;
            }
            Log.d("", "Setting Next Month in GridCellAdapter: " + "Month: "
                    + month + " Year: " + year);

            setGridCellAdapterToDate(month, year);

        }
    }

   /* private void refreshDisplay() {
        refreshDisplay(new LocationInfo(context));
    }

    private void refreshDisplay(final LocationInfo locationInfo) {
        if (locationInfo.anyLocationDataReceived()) {
            lat = locationInfo.lastLat;
            lon = locationInfo.lastLong;
            Log.e("Longitude", String.valueOf(lon));
            Log.e("Latitude", String.valueOf(lat));
            // Toast.makeText(context, "Location Updated",
            // Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context,
                    "Unable to get GPS location! Try again later!!",
                    Toast.LENGTH_LONG).show();
        }

    }*/

    public String getmonthName(String monthName) {
        String month = "";

        if (monthName.equals("01")) {
            month = "January";
        } else if (monthName.equals("02")) {
            month = "February";
        } else if (monthName.equals("03")) {
            month = "March";
        } else if (monthName.equals("04")) {
            month = "April";
        } else if (monthName.equals("05")) {
            month = "May";
        } else if (monthName.equals("06")) {
            month = "June";
        } else if (monthName.equals("07")) {
            month = "July";
        } else if (monthName.equals("08")) {
            month = "August";
        } else if (monthName.equals("09")) {
            month = "September";
        } else if (monthName.equals("10")) {
            month = "October";
        } else if (monthName.equals("11")) {
            month = "November";
        } else if (monthName.equals("12")) {
            month = "December";
        }

        return month;
    }

    public String getmonthNo(String monthName) {
        String month = "";

        if (monthName.equals("January")) {
            month = "01";
        } else if (monthName.equals("February")) {
            month = "02";
        } else if (monthName.equals("March")) {
            month = "03";
        } else if (monthName.equals("April")) {
            month = "04";
        } else if (monthName.equals("May")) {
            month = "05";
        } else if (monthName.equals("June")) {
            month = "06";
        } else if (monthName.equals("July")) {
            month = "07";
        } else if (monthName.equals("August")) {
            month = "08";
        } else if (monthName.equals("September")) {
            month = "09";
        } else if (monthName.equals("October")) {
            month = "10";
        } else if (monthName.equals("November")) {
            month = "11";
        } else if (monthName.equals("December")) {
            month = "12";
        }

        return month;
    }

    /*@Override
    public void onResume() {
        super.onResume();

        // cancel any notification we may have received from
        // TestBroadcastReceiver
        ((NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1234);

        refreshDisplay();

        // This demonstrates how to dynamically create a receiver to listen to
        // the location updates.
        // You could also register a receiver in your manifest.
        final IntentFilter lftIntentFilter = new IntentFilter(
                LocationLibraryConstants
                        .getLocationChangedPeriodicBroadcastAction());
        context.registerReceiver(lftBroadcastReceiver, lftIntentFilter);
    }*/

    public static boolean afterdateValidate(String date) {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date parseddate = sdf.parse(date);
            Date dateObj2 = new Date(System.currentTimeMillis());
            if (parseddate.after(dateObj2)) {

                result = true;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }

    public static boolean beforedatevalidate(String selecteddate,
                                             String currentdate) {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date selectdate = sdf.parse(selecteddate);
            Date curntdate = sdf.parse(currentdate);

            Log.e("", "selecteddate=" + selecteddate);
            Log.e("", "currentdate=" + currentdate);
            // Log.v("befordatevalidation",""+selecteddate.compareTo(currentdate));
            // Log.v("befordatevalidation",""+selectdate.before(curntdate));
            if (selectdate.before(curntdate)) {

                Log.v("befordatevalidation", "" + result);
                result = true;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }

    public static boolean afterdateChangeValidate(String serverdate) {
        boolean result = false;

        try {
            final Calendar calendar1 = Calendar
                    .getInstance();
            SimpleDateFormat formatter1 = new SimpleDateFormat(
                    "M/d/yyyy");
            String currentdate = formatter1.format(calendar1
                    .getTime());

            if (!serverdate.equalsIgnoreCase(currentdate)) {

                result = true;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }

    public String getmonthNo1(String monthName) {
        String month = "";

        if (monthName.equals("01")) {
            month = "1";
        } else if (monthName.equals("02")) {
            month = "2";
        } else if (monthName.equals("03")) {
            month = "3";
        } else if (monthName.equals("04")) {
            month = "4";
        } else if (monthName.equals("05")) {
            month = "5";
        } else if (monthName.equals("06")) {
            month = "6";
        } else if (monthName.equals("07")) {
            month = "7";
        } else if (monthName.equals("08")) {
            month = "8";
        } else if (monthName.equals("09")) {
            month = "9";
        } else if (monthName.equals("10")) {
            month = "10";
        } else if (monthName.equals("11")) {
            month = "11";
        } else if (monthName.equals("12")) {
            month = "12";
        }

        return month;
    }


    /*private final BroadcastReceiver lftBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // extract the location info in the broadcast
            final LocationInfo locationInfo = (LocationInfo) intent
                    .getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
            // refresh the display with it
            refreshDisplay(locationInfo);
        }
    };*/

    private class SaveAttendance extends AsyncTask<String, Void, SoapObject> {


        SoapPrimitive soap_result_attendance = null;

        //SoapPrimitive soap_result_tester = null;

        String ErroFlag;
        String Erro_function = "";

        Cursor attendance_array;

        String Flag;

        String attendance_flag = "";
        String leavetype_flag = "";

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mProgress.setMessage("Please Wait");
            mProgress.show();
            mProgress.setCancelable(false);
        }

        @Override
        protected SoapObject doInBackground(String... params) {
            // TODO Auto-generated method stub

            attendance_flag = params[0];
            leavetype_flag = params[1];

            final String[] columns = new String[]{"emp_id", "Adate",
                    "attendance", "absent_type", "lat", "lon", "savedServer", "month",
                    "holiday_desc", "year"};

            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                // Toast.makeText(getActivity(),"Check Your Internet Connection!!!",
                // Toast.LENGTH_LONG).show();

                Flag = "3";
                // stop executing code by return

            } else {


                Flag = "1";

                try {
                    if (attendance_flag.equalsIgnoreCase("P") &&
                            attendance_flag.length() > 0 && attendance_flag != null) {
                        soap_result_attendance = service.SaveAttendance(username, attendanceDate1,
                                attendance_flag, "", String.valueOf(lat), String.valueOf(lon));
                    } else {
                        soap_result_attendance = service.SaveAttendance(username, attendanceDate1,
                                attendance_flag, leavetype_flag, String.valueOf(lat), String.valueOf(lon));
                    }

                    if (soap_result_attendance != null) {
                        String t = soap_result_attendance.toString();
                        Log.v("", "soap_result_attendance=" + t);
                        if (t.equalsIgnoreCase("TRUE")) {
                            ErroFlag = "1";
                                       /* db.update_Attendance_data(attendance_array.getString(0));
                                        db.close();*/

                            String sld[] = attendanceDate1.split(" ");
                            final String sld1 = sld[0];

                            String ddd[] = sld1.split("-");
                            final String year = ddd[0];

                            String attendmonth1 = getmonthNo1(attendmonth);

                            if (attendance_flag.equalsIgnoreCase("P") &&
                                    attendance_flag.length() > 0 && attendance_flag != null) {
                                db.open();

                                values = new String[]{username,
                                        attendanceDate1,
                                        attendance_flag,
                                        "",
                                        String.valueOf(lat),
                                        String.valueOf(lon),
                                        "1",
                                        attendmonth1,
                                        "",
                                        year};

                                db.insert(values, columns, "attendance");

                                db.close();
                            } else {
                                db.open();

                                values = new String[]{
                                        username,
                                        attendanceDate1,
                                        attendance_flag,
                                        leavetype_flag,
                                        String.valueOf(lat),
                                        String.valueOf(lon),
                                        "1", attendmonth1,
                                        "", year};

                                db.insert(values, columns,
                                        "attendance");

                                db.close();
                            }


                        } else if (t.equalsIgnoreCase("FAIL")) {
                            ErroFlag = "0";
                            final Calendar calendar1 = Calendar
                                    .getInstance();
                            SimpleDateFormat formatter1 = new SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm:ss");
                            String Createddate = formatter1.format(calendar1
                                    .getTime());

                            int n = Thread.currentThread().getStackTrace()[2].getLineNumber();
                            db.insertSyncLog("SaveAttendace_SE", String.valueOf(n), "SaveAttendance()", Createddate, Createddate, sp.getString("username", ""), "Transaction Upload", "Fail");

                        }
                    } else {
                        ErroFlag = "0";
                        //String errors = "Soap in giving null while 'Attendance' and 'checkSyncFlag = 2' in  data Sync";
                        //we.writeToSD(errors.toString());
                        final Calendar calendar1 = Calendar
                                .getInstance();
                        SimpleDateFormat formatter1 = new SimpleDateFormat(
                                "MM/dd/yyyy HH:mm:ss");
                        String Createddate = formatter1.format(calendar1
                                .getTime());

                        int n = Thread.currentThread().getStackTrace()[2].getLineNumber();
                        db.insertSyncLog("Internet Connection Lost, Soap in giving null while 'SaveAttendace'", String.valueOf(n), "SaveAttendance()", Createddate, Createddate, sp.getString("username", ""), "Transaction Upload", "Fail");

                    }
                    // db.open();
                    // attendance_array = db.getAttendanceData();
//                    if (attendance_array.getCount() > 0) {
//
//                        if (attendance_array != null && attendance_array.moveToFirst()) {
//                            attendance_array.moveToFirst();
//
//                            do {
//
//                                String empid = attendance_array.getString(1);
//                                Log.e("", "empid=" + empid);
//                                String date = attendance_array.getString(2);
//                                Log.e("", "date=" + date);
//
//                                String attendance = attendance_array.getString(3);
//                                Log.e("", "attendance=" + attendance);
//
//                                String absent_type = attendance_array.getString(4);
//                                Log.e("", "absent_type=" + absent_type);
//
//                                String lat = attendance_array.getString(5);
//                                Log.e("", "lat=" + lat);
//
//                                String lon = attendance_array.getString(5);
//                                Log.e("", "lon=" + lon);
//
//                                soap_result_attendance = service.SaveAttendance(
//                                        attendance_array.getString(1), date,
//                                        attendance_array.getString(3),
//                                        attendance_array.getString(4),
//                                        attendance_array.getString(5),
//                                        attendance_array.getString(6));
//
//                                if (soap_result_attendance != null) {
//                                    String t = soap_result_attendance.toString();
//                                    Log.v("", "soap_result_attendance=" + t);
//                                    if (t.equalsIgnoreCase("TRUE")) {
//                                        ErroFlag = "1";
//                                       /* db.update_Attendance_data(attendance_array.getString(0));
//                                        db.close();*/
//
//                                        String sld[] = attendanceDate1.split(" ");
//                                        final String sld1 = sld[0];
//
//                                        String ddd[] = sld1.split("-");
//                                        final String year = ddd[0];
//
//                                        db.close();
//
//                                        String attendmonth1 = getmonthNo1(attendmonth);
//                                        db.open();
//
//                                        values = new String[]{username,
//                                                attendanceDate1,
//                                                "P",
//                                                "",
//                                                String.valueOf(lat),
//                                                String.valueOf(lon),
//                                                "0",
//                                                attendmonth1,
//                                                "",
//                                                year};
//
//                                        db.insert(values, columns, "attendance");
//
//                                        db.close();
//
//                                    } else if (t.equalsIgnoreCase("FAIL")) {
//                                        ErroFlag = "0";
//                                        final Calendar calendar1 = Calendar
//                                                .getInstance();
//                                        SimpleDateFormat formatter1 = new SimpleDateFormat(
//                                                "MM/dd/yyyy HH:mm:ss");
//                                        String Createddate = formatter1.format(calendar1
//                                                .getTime());
//
//                                        int n = Thread.currentThread().getStackTrace()[2].getLineNumber();
//                                        db.insertSyncLog("SaveAttendace_SE", String.valueOf(n), "SaveAttendance()", Createddate, Createddate, sp.getString("username", ""), "Transaction Upload", "Fail");
//
//                                    }
//                                } else {
//                                    ErroFlag = "0";
//                                    //String errors = "Soap in giving null while 'Attendance' and 'checkSyncFlag = 2' in  data Sync";
//                                    //we.writeToSD(errors.toString());
//                                    final Calendar calendar1 = Calendar
//                                            .getInstance();
//                                    SimpleDateFormat formatter1 = new SimpleDateFormat(
//                                            "MM/dd/yyyy HH:mm:ss");
//                                    String Createddate = formatter1.format(calendar1
//                                            .getTime());
//
//                                    int n = Thread.currentThread().getStackTrace()[2].getLineNumber();
//                                    db.insertSyncLog("Internet Connection Lost, Soap in giving null while 'SaveAttendace'", String.valueOf(n), "SaveAttendance()", Createddate, Createddate, sp.getString("username", ""), "Transaction Upload", "Fail");
//
//                                }
//
//                            } while (attendance_array.moveToNext());
//
//
//                        }
//                    } else if (attendance_array == null) {
//
//                    } else {
//                        Log.e("NoAttendance dataupload",
//                                String.valueOf(attendance_array.getCount()));
//                    }


                } catch (Exception e) {
                    ErroFlag = "0";
                    Erro_function = "Attendance";
                    e.printStackTrace();
                    String Error = e.toString();

                    final Calendar calendar1 = Calendar
                            .getInstance();
                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                            "MM/dd/yyyy HH:mm:ss");
                    String Createddate = formatter1.format(calendar1
                            .getTime());

                    int n = Thread.currentThread().getStackTrace()[2].getLineNumber();
                    db.insertSyncLog(Error, String.valueOf(n), "SaveAttendance()", Createddate, Createddate, sp.getString("username", ""), "Transaction Upload", "Fail");


                }
                //}
            }

            return null;
        }

        @Override
        protected void onPostExecute(SoapObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            mProgress.dismiss();
            if (Flag.equalsIgnoreCase("3")) {

                Toast.makeText(getApplicationContext(), "Connectivity Error Please check internet ", Toast.LENGTH_SHORT).show();
            }

            if (ErroFlag.equalsIgnoreCase("0")) {

                Toast.makeText(getApplicationContext(), "Please Enter Today Date", Toast.LENGTH_SHORT).show();
            }
            if (ErroFlag.equalsIgnoreCase("1")) {

                Toast.makeText(getApplicationContext(), "Attendance Successfully Sync", Toast.LENGTH_SHORT).show();

                if (attendance_flag.equalsIgnoreCase("A")) {


                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                } else {

                    Intent i = new Intent(getApplicationContext(), DashboardNewActivity.class);
                    i.putExtra("FROM", "LOGIN");
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

            }

        }

    }


    private class SaveLogoutTime extends AsyncTask<Void, Void, SoapPrimitive> {

        ProgressDialog progress;

        SoapPrimitive soap_result;

        @Override
        protected SoapPrimitive doInBackground(Void... params) {
            // TODO Auto-generated method stub

            Date date = new Date();
            SimpleDateFormat form = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");

            attendanceDate1 = form.format(date);
            soap_result = service.SaveLogoutTime(username, attendanceDate1);

            return soap_result;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(AttendanceFragment.this);
            progress.setTitle("Uploading");
            progress.setMessage("Please Wait.......");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(SoapPrimitive result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            progress.dismiss();
            if (result != null) {
                if (result.toString().equalsIgnoreCase("true")) {
                    Toast.makeText(AttendanceFragment.this, "Uploaded Succesfully", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getApplicationContext(), DashboardNewActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    Toast.makeText(AttendanceFragment.this, "Data Not uploaded", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(AttendanceFragment.this, "Please check internet Connectivity", Toast.LENGTH_LONG).show();
            }
        }


    }

    // Getting lat and lon value using location liabarary and showing gps dialog

    private void showLocation(final Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            //  Toast.makeText(mContext, "latitude:"+lat+"longitude"+lang, Toast.LENGTH_SHORT).show();
            SmartLocation.with(AttendanceFragment.this).location().stop();

        }
    }

    /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(AttendanceFragment.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(AttendanceFragment.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialogbox();
        } else
            showSettingDialogbox();

    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AttendanceFragment.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(AttendanceFragment.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(AttendanceFragment.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /* Show Location Access Dialog */
    private void showSettingDialogbox() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        updateGPSStatus("GPS is Enabled in your device");
                        // startLocation();


                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(AttendanceFragment.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void startLocation() {

        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();

        smartLocation.location(provider).start(this);
        smartLocation.activity().start(this);

        // Create some geofences
        GeofenceModel mestalla = new GeofenceModel.Builder("1").setTransition(Geofence.GEOFENCE_TRANSITION_ENTER).setLatitude(39.47453120000001).setLongitude(-0.358065799999963).setRadius(500).build();
        smartLocation.geofencing().add(mestalla).start(this);
    }

    //Method to update GPS status text
    private void updateGPSStatus(String status) {
        //   gps_status.setText(status);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        updateGPSStatus("GPS is Enabled in your device");
                        //startLocationUpdates();
                        startLocation();

                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        updateGPSStatus("GPS is Disabled in your device");
                        break;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register broadcast receiver to check the status of GPS
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister receiver on destroy
        if (gpsLocationReceiver != null)
            unregisterReceiver(gpsLocationReceiver);
    }

    //Run on UI
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            //   showSettingDialogbox();
        }
    };

    /* Broadcast receiver to check status of GPS */
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Check if GPS is turned ON or OFF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("About GPS", "GPS is Enabled in your device");
                    updateGPSStatus("GPS is Enabled in your device");
                    //  startLocation();

                } else {
                    //If GPS turned OFF show Location Dialog
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    updateGPSStatus("GPS is Disabled in your device");
                    Log.e("About GPS", "GPS is Disabled in your device");
                }

            }
        }
    };

    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //If permission granted show location dialog if APIClient is not null
                    if (mGoogleApiClient == null) {
                        initGoogleAPIClient();
                    }

                } else {
                    updateGPSStatus("Location Permission denied.");
                    Toast.makeText(AttendanceFragment.this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

}
