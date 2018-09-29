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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.prod.sudesi.lotusherbalsnew.Models.AttendanceModel;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

/*import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geofencing.model.GeofenceModel;
import io.nlopez.smartlocation.geofencing.utils.TransitionGeofence;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;*/

import static android.content.ContentValues.TAG;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
/*import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;*/


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AttendanceFragment extends AppCompatActivity implements OnClickListener {

    String attendance_flag;
    String leavetype_flag;
    String uploadflag;
    boolean flag;
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
    String username, bdename, role;
    ConnectionDetector cd;
    LotusWebservice service;
    private ProgressDialog pd;

    String month_h, year_h;

    TextView tv_h_username;
    Button btn_home, btn_logout;

    private ProgressDialog mProgress = null;

    SaveLogoutTime savelogout;

    /*private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final int LOCATION_PERMISSION_ID = 1001;
    private LocationGooglePlayServicesProvider provider;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";*/

    protected Location mLastLocation;

    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    String status, ADate, AttendanceValue, Aid, savedate, autoid;
    private Button gridcell;
    private ArrayList<AttendanceModel> presentList;
    AttendanceModel attendanceModel;


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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AttendanceFragment.this);

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
        role = sp.getString("Role", "");

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

        /*initGoogleAPIClient();//Init Google API Client
        checkPermissions();//Check Permission
        if (ContextCompat.checkSelfPermission(AttendanceFragment.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AttendanceFragment.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;
        }

        startLocation();*/
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

                String serverddate = sp.getString("currentdate", "");
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

                    final Button present;
                    final Button absent;
                    final Button out;
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
                                    dialog.setCancelable(false);
                                    dialog.setContentView(R.layout.layout_out_attendance);

                                    out = (Button) dialog.findViewById(R.id.btn_out);
                                    cancel = (Button) dialog.findViewById(R.id.btn_cancel);

                                    out.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            // TODO Auto-generated method stub
                                            out.setEnabled(false);

                                            new Handler().postDelayed(new Runnable() {

                                                @Override
                                                public void run() {
                                                    // This method will be executed once the timer is over
                                                    out.setEnabled(true);
                                                    Log.d(TAG, "resend1");

                                                }
                                            }, 5000);// set time as per your requirement

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
                                dialog.setCancelable(false);
                                dialog.setContentView(R.layout.layout_out_attendance);

                                out = (Button) dialog.findViewById(R.id.btn_out);
                                cancel = (Button) dialog.findViewById(R.id.btn_cancel);

                                out.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        out.setEnabled(false);

                                        new Handler().postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                // This method will be executed once the timer is over
                                                out.setEnabled(true);
                                                Log.d(TAG, "resend1");

                                            }
                                        }, 5000);// set time as per your requirement

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

                                    present.setEnabled(false);

                                    new Handler().postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            // This method will be executed once the timer is over
                                            present.setEnabled(true);
                                            Log.d(TAG, "resend1");

                                        }
                                    }, 5000);// set time as per your requirement

                                    attendance_flag = "P";
                                    leavetype_flag = "";
                                    if(flag==true){
                                        uploadflag = "1";
                                    }else {
                                        uploadflag = "0";
                                    }
                                    if (cd.isConnectingToInternet()) {
                                        //view.setBackgroundResource(R.drawable.green);
                                        if (dialog != null && dialog.isShowing() && !AttendanceFragment.this.isFinishing()) {
                                            dialog.dismiss();
                                        }

                                        try {
                                            // Production live now
                                           /* if(role.equalsIgnoreCase("DUB")){
                                                new SaveAttendanceForDubai().execute(attendance_flag, leavetype_flag);
                                            }else if(role.equalsIgnoreCase("FLR")){
                                                new SaveAttendanceForDubai().execute(attendance_flag, leavetype_flag);
                                            }else {
                                                new SaveAttendance().execute(attendance_flag, leavetype_flag);
                                            }*/
                                            //using below method for testing on UAT India and dubai
                                            new SaveAttendance().execute(attendance_flag, leavetype_flag,uploadflag);
                                            view.setBackgroundResource(R.drawable.green);
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

                                    absent.setEnabled(false);

                                    new Handler().postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            // This method will be executed once the timer is over
                                            absent.setEnabled(true);
                                            Log.d(TAG, "resend1");

                                        }
                                    }, 5000);// set time as per your requirement

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
                                                        if(flag==true){
                                                            uploadflag = "1";
                                                        }else {
                                                            uploadflag = "0";
                                                        }
                                                        if (rb_seek_leave.isChecked()) {

                                                            leavetype_flag = "Leave";

                                                            //view.setBackgroundResource(R.drawable.red);
                                                            if (d != null && d.isShowing() && !AttendanceFragment.this.isFinishing()) {
                                                                d.dismiss();
                                                            }
                                                            if (dialog != null && dialog.isShowing() && !AttendanceFragment.this.isFinishing()) {
                                                                dialog.dismiss();
                                                            }

                                                            try {
                                                                new SaveAttendance().execute(attendance_flag, leavetype_flag,uploadflag);
                                                                view.setBackgroundResource(R.drawable.red);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }

                                                        if (rb_weekly_off.isChecked()) {
                                                            leavetype_flag = "Weekly Off";

                                                            //view.setBackgroundResource(R.drawable.red);
                                                            if (d != null && d.isShowing() && !AttendanceFragment.this.isFinishing()) {
                                                                d.dismiss();
                                                            }
                                                            if (dialog != null && dialog.isShowing() && !AttendanceFragment.this.isFinishing()) {
                                                                dialog.dismiss();
                                                            }

                                                            try {
                                                                new SaveAttendance().execute(attendance_flag, leavetype_flag,uploadflag);
                                                                view.setBackgroundResource(R.drawable.red);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        if (rb_holiday.isChecked()) {
                                                            leavetype_flag = "Holiday";

                                                            //view.setBackgroundResource(R.drawable.red);
                                                            if (d != null && d.isShowing() && !AttendanceFragment.this.isFinishing()) {
                                                                d.dismiss();
                                                            }
                                                            if (dialog != null && dialog.isShowing() && !AttendanceFragment.this.isFinishing()) {
                                                                dialog.dismiss();
                                                            }

                                                            try {
                                                                new SaveAttendance().execute(attendance_flag, leavetype_flag,uploadflag);
                                                                view.setBackgroundResource(R.drawable.red);
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

    private class SaveAttendanceForDubai extends AsyncTask<String, Void, SoapObject> {


        SoapObject soap_result_attendance = null;

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

                Flag = "3";
                // stop executing code by return

            } else {


                Flag = "1";

                try {
                    if (attendance_flag.equalsIgnoreCase("P") &&
                            attendance_flag.length() > 0 && attendance_flag != null) {
                        soap_result_attendance = service.SaveAttendanceForDubai(username, attendanceDate1,
                                attendance_flag, "", String.valueOf(lat), String.valueOf(lon));
                    } else {
                        soap_result_attendance = service.SaveAttendanceForDubai(username, attendanceDate1,
                                attendance_flag, leavetype_flag, String.valueOf(lat), String.valueOf(lon));
                    }

                    if (soap_result_attendance != null) {
                        presentList = new ArrayList<AttendanceModel>();
                        for (int i = 0; i < soap_result_attendance.getPropertyCount(); i++) {

                            SoapObject soapObject = (SoapObject) soap_result_attendance.getProperty(i);

                            ADate = soapObject.getProperty("ADate").toString();


                            if (ADate == null) {
                                ADate = "";
                            }

                            status = soapObject.getProperty("status").toString();
                            if (status == null) {
                                status = "";
                            }

                            Aid = soapObject.getProperty("ID").toString();
                            if (Aid == null) {
                                Aid = "";
                            }
                            spe.putString("AttendAid", Aid);
                            spe.commit();

                            AttendanceValue = soapObject.getProperty("AttendanceValue").toString();
                            if (AttendanceValue == null) {
                                AttendanceValue = "";
                            }

                            attendanceModel = new AttendanceModel();
                            attendanceModel.setADate(ADate);
                            attendanceModel.setAttendanceValue(AttendanceValue);
                            attendanceModel.setAid(Aid);

                            presentList.add(attendanceModel);
                        }

                        Log.v("", "soap_result_attendance=" + status);
                        if (status.equalsIgnoreCase("TRUE")) {
                            ErroFlag = "1";

                            for (int j = 0; j < presentList.size(); j++) {
                                attendanceModel = presentList.get(j);

                                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
                                Date date = df.parse(attendanceModel.getADate());

                                SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                savedate = form.format(date);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String adate = sdf.format(date.getTime());

                                String sld[] = attendanceModel.getADate().split(" ");
                                final String sld1 = sld[0];

                                String ddd[] = sld1.split("/");
                                final String year = ddd[2];

                                String attendmonth1 = getmonthNo1(attendmonth);

                                db.open();
                                Cursor c = db.getuniquedataAttendance(username, adate);

                                int count = c.getCount();
                                Log.v("", "" + count);
                                db.close();
                                if (count > 0) {

                                } else {
                                    db.open();

                                    values = new String[]{username,
                                            savedate,
                                            attendanceModel.getAttendanceValue(),
                                            "",
                                            String.valueOf(lat),
                                            String.valueOf(lon),
                                            "1",
                                            attendmonth1,
                                            "",
                                            year};

                                    db.insert(values, columns, "attendance");

                                    db.close();

                                }

                            }
                        /* db.update_Attendance_data(attendance_array.getString(0));
                                        db.close();*/

                           /* String sld2[] = attendanceDate1.split(" ");
                            final String sld3 = sld2[0];

                            String ddd1[] = sld3.split("-");
                            final String year1 = ddd1[0];

                            String attendmonth2 = getmonthNo1(attendmonth);

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
                                        attendmonth2,
                                        "",
                                        year1};

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
                                        "1", attendmonth2,
                                        "", year1};

                                db.insert(values, columns,
                                        "attendance");

                                db.close();
                            }*/


                        } else if (status.equalsIgnoreCase("SE")) {
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
                        ErroFlag = "3";
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


                } catch (Exception e) {
                    //ErroFlag = "3";
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
            if (mProgress != null && mProgress.isShowing() && !AttendanceFragment.this.isFinishing()) {
                mProgress.dismiss();
            }
            if (Flag.equalsIgnoreCase("3")) {

                Toast.makeText(getApplicationContext(), "Connectivity Error Please check internet ", Toast.LENGTH_SHORT).show();
            }

            if (ErroFlag.equalsIgnoreCase("0")) {

                Toast.makeText(getApplicationContext(), "Soap response null", Toast.LENGTH_SHORT).show();
            }
            if (ErroFlag.equalsIgnoreCase("1")) {

                Toast.makeText(getApplicationContext(), "Attendance Successfully Sync", Toast.LENGTH_SHORT).show();


                if (role.equalsIgnoreCase("FLR")) {
                    if (attendance_flag.equalsIgnoreCase("A")) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    } else {
                        spe.putString("FLROutletSelect", "False");
                        spe.commit();
                        Intent i = new Intent(getApplicationContext(), OutletActivity.class);
                        //i.putExtra("FromAttendancefloter", "AF");
                        startActivity(i);
                    }
                } else if (role.equalsIgnoreCase("DUB")) {
                    if (attendance_flag.equalsIgnoreCase("A")) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    } else {
                        Intent i = new Intent(getApplicationContext(), OutletActivity.class);
                        startActivity(i);
                    }
                } else {
                    if (attendance_flag.equalsIgnoreCase("A")) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    } else {
                        Intent i = new Intent(getApplicationContext(), DashboardNewActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
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
            if (progress != null && progress.isShowing() && !AttendanceFragment.this.isFinishing()) {
                progress.dismiss();
            }
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

    // production live now this method
    private class SaveAttendance extends AsyncTask<String, Void, SoapPrimitive> {


        SoapPrimitive soap_attendance = null;

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
        protected SoapPrimitive doInBackground(String... params) {
            // TODO Auto-generated method stub

            attendance_flag = params[0];
            leavetype_flag = params[1];
            uploadflag = params[2];

            final String[] columns = new String[]{"emp_id", "Adate",
                    "attendance", "absent_type", "lat", "lon", "savedServer", "month",
                    "holiday_desc", "year"};

            if (!cd.isConnectingToInternet()) {

                Flag = "3";
                // stop executing code by return

            } else {


                Flag = "1";

                try {
                    if (attendance_flag.equalsIgnoreCase("P") &&
                            attendance_flag.length() > 0 && attendance_flag != null) {
                        soap_attendance = service.SaveAttendance(username, attendanceDate1,
                                attendance_flag, "", String.valueOf(lat), String.valueOf(lon),uploadflag);
                    } else {
                        soap_attendance = service.SaveAttendance(username, attendanceDate1,
                                attendance_flag, leavetype_flag, String.valueOf(lat), String.valueOf(lon),uploadflag);
                    }

                    if (soap_attendance != null) {
                        String t = soap_attendance.toString();
                        Log.v("", "soap_result_attendance=" + t);
                        if (t.equalsIgnoreCase("TRUE")) {
                            ErroFlag = "1";

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


                        } else if (t.equalsIgnoreCase("SE")) {
                            ErroFlag = "1";
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

                } catch (Exception e) {
                    ErroFlag = "2";
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
        protected void onPostExecute(SoapPrimitive result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (mProgress != null && mProgress.isShowing() && !AttendanceFragment.this.isFinishing()) {
                mProgress.dismiss();
            }
            if (Flag.equalsIgnoreCase("3")) {

                Toast.makeText(getApplicationContext(), "Connectivity Error Please check internet ", Toast.LENGTH_SHORT).show();
            }

            if (ErroFlag.equalsIgnoreCase("0")) {

                DisplayDialogMessage(" Save Attendance Incomplete, Please try again!!");
/*
                Toast.makeText(getApplicationContext(), "Attendance Sync Incomplete please try again!!", Toast.LENGTH_SHORT).show();
*/
            }
            if (ErroFlag.equalsIgnoreCase("1")) {

                flag = false;
                Toast.makeText(getApplicationContext(), "Attendance Successfully Sync", Toast.LENGTH_SHORT).show();

                if (role.equalsIgnoreCase("FLR")) {
                    if (attendance_flag.equalsIgnoreCase("A")) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    } else {
                        spe.putString("FLROutletSelect", "False");
                        spe.commit();
                        Intent i = new Intent(getApplicationContext(), OutletActivity.class);
                        //i.putExtra("FromAttendancefloter", "AF");
                        startActivity(i);
                    }
                } else if (role.equalsIgnoreCase("DUB")) {
                    if (attendance_flag.equalsIgnoreCase("A")) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    } else {
                        Intent i = new Intent(getApplicationContext(), OutletActivity.class);
                        startActivity(i);
                    }
                } else {
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

            if (ErroFlag.equalsIgnoreCase("2")) {

                Toast.makeText(getApplicationContext(), "Database getting null", Toast.LENGTH_SHORT).show();

            }


        }

    }

    // using for testing UAT India and dubai

   /* private class SaveAttendance extends AsyncTask<String, Void, SoapObject> {


        SoapObject soap_result_attendance = null;

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
                        presentList = new ArrayList<AttendanceModel>();
                        for (int i = 0; i < soap_result_attendance.getPropertyCount(); i++) {

                            SoapObject soapObject = (SoapObject) soap_result_attendance.getProperty(i);

                            ADate = soapObject.getProperty("ADate").toString();


                            if (ADate == null) {
                                ADate = "";
                            }

                            status = soapObject.getProperty("status").toString();
                            if (status == null) {
                                status = "";
                            }

                            Aid = soapObject.getProperty("ID").toString();
                            if (Aid == null) {
                                Aid = "";
                            }
                            spe.putString("AttendAid", Aid);
                            spe.commit();

                            AttendanceValue = soapObject.getProperty("AttendanceValue").toString();
                            if (AttendanceValue == null) {
                                AttendanceValue = "";
                            }

                            attendanceModel = new AttendanceModel();
                            attendanceModel.setADate(ADate);
                            attendanceModel.setAttendanceValue(AttendanceValue);
                            attendanceModel.setAid(Aid);

                            presentList.add(attendanceModel);
                        }

                        Log.v("", "soap_result_attendance=" + status);
                        if (status.equalsIgnoreCase("TRUE")) {
                            ErroFlag = "1";

                            for (int j = 0; j < presentList.size(); j++) {
                                attendanceModel = presentList.get(j);

                                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
                                Date date = df.parse(attendanceModel.getADate());

                                SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                savedate = form.format(date);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String adate = sdf.format(date.getTime());

                                String sld[] = attendanceModel.getADate().split(" ");
                                final String sld1 = sld[0];

                                String ddd[] = sld1.split("/");
                                final String year = ddd[2];

                                String attendmonth1 = getmonthNo1(attendmonth);

                                db.open();
                                Cursor c = db.getuniquedataAttendance(username, adate);

                                int count = c.getCount();
                                Log.v("", "" + count);
                                db.close();
                                if (count > 0) {
                                    db.open();
                                    db.update(adate,
                                            new String[]{
                                                    username,
                                                    savedate,
                                                    attendanceModel.getAttendanceValue(),
                                                    attendanceModel.getAbsentType(),
                                                    "0.0",
                                                    "0.0",
                                                    "1",
                                                    attendmonth1,
                                                    "",
                                                    year},
                                            new String[]{
                                                    "emp_id", "Adate",
                                                    "attendance", "absent_type",
                                                    "lat", "lon", "savedServer", "month",
                                                    "holiday_desc", "year"},
                                            "attendance", "Adate");
                                    db.close();
                                } else {
                                    db.open();

                                    values = new String[]{username,
                                            savedate,
                                            attendanceModel.getAttendanceValue(),
                                            "",
                                            String.valueOf(lat),
                                            String.valueOf(lon),
                                            "1",
                                            attendmonth1,
                                            "",
                                            year};

                                    db.insert(values, columns, "attendance");

                                    db.close();

                                }

                            }

                        } else if (status.equalsIgnoreCase("FAIL")) {
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
                        ErroFlag = "3";
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


                } catch (Exception e) {
                    //ErroFlag = "3";
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

                Toast.makeText(getApplicationContext(), "Getting null Response", Toast.LENGTH_SHORT).show();
            }
            if (ErroFlag.equalsIgnoreCase("1")) {

                Toast.makeText(getApplicationContext(), "Attendance Successfully Sync", Toast.LENGTH_SHORT).show();


                if (role.equalsIgnoreCase("FLR")) {
                    if (attendance_flag.equalsIgnoreCase("A")) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    } else {
                        spe.putString("FLROutletSelect", "False");
                        spe.commit();
                        Intent i = new Intent(getApplicationContext(), OutletActivity.class);
                        //i.putExtra("FromAttendancefloter", "AF");
                        startActivity(i);
                    }
                } else if (role.equalsIgnoreCase("DUB")) {
                    if (attendance_flag.equalsIgnoreCase("A")) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    } else {
                        Intent i = new Intent(getApplicationContext(), OutletActivity.class);
                        startActivity(i);
                    }
                } else {
                    if (attendance_flag.equalsIgnoreCase("A")) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    } else {
                        Intent i = new Intent(getApplicationContext(), DashboardNewActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
            }

        }

    }*/



    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.textwarn, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(AttendanceFragment.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.textwarn, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                            lat = mLastLocation.getLatitude();
                            lon = mLastLocation.getLongitude();

                           /* mLatitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
                                    mLatitudeLabel,
                                    mLastLocation.getLatitude()));
                            mLongitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
                                    mLongitudeLabel,
                                    mLastLocation.getLongitude()));*/
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());

                        }
                    }
                });
    }

    private void DisplayDialogMessage(String msg) {


        AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceFragment.this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        flag = true;
                        dialog.dismiss();
                        //new SaveAttendance().execute(attendance_flag, leavetype_flag,uploadflag);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
