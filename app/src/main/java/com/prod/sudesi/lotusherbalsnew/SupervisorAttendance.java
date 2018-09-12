package com.prod.sudesi.lotusherbalsnew;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import org.ksoap2.serialization.SoapPrimitive;

import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SupervisorAttendance extends Activity implements OnClickListener {

	TextView txt_currentDate, txt_BDE;

	TimePicker time_picker;
	Resources system;

	String BDEusername, username, BDE_CODE, Adate = "", Actual_date = "";
	SharedPreferences sp;
	SharedPreferences.Editor spe;

	Button btn_submit, btn_home, btn_logout;

	Dbcon dbcon;

	private double lon = 0.0, lat = 0.0;

	LotusWebservice service;

	AttendanceAsync upload_attendance;
	TextView tv_h_username;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_supervisor_attendance);

		sp = getSharedPreferences("Lotus", Context.MODE_PRIVATE);
		spe = sp.edit();

		dbcon = new Dbcon(getApplicationContext());
		dbcon.open();

		service = new LotusWebservice(SupervisorAttendance.this);

		BDEusername = sp.getString("BDEusername", "");

		BDE_CODE = sp.getString("BDE_Code", "");

		username = sp.getString("username", "");

		txt_currentDate = (TextView) findViewById(R.id.txt_currentDate);
		tv_h_username = (TextView)findViewById(R.id.tv_h_username);

		txt_BDE = (TextView) findViewById(R.id.txt_BDE);

		time_picker = (TimePicker) findViewById(R.id.timePicker1);
		time_picker.setIs24HourView(true);
		set_timepicker_text_colour();

		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);

		btn_home = (Button) findViewById(R.id.btn_home);
		btn_logout = (Button) findViewById(R.id.btn_logout);

		btn_home.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		
		tv_h_username = (TextView)findViewById(R.id.tv_h_username);
		
		
		username = sp.getString("username", "");
		Log.v("","username=="+username);
		
		tv_h_username.setText(BDEusername);
	

		Calendar cal = Calendar.getInstance();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		String date = sdf.format(cal.getTime());

		txt_currentDate.setText(date);

		txt_BDE.setText(BDEusername);

	}

	private void set_timepicker_text_colour() {
		system = Resources.getSystem();
		int hour_numberpicker_id = system
				.getIdentifier("hour", "id", "android");
		int minute_numberpicker_id = system.getIdentifier("minute", "id",
				"android");
		// int ampm_numberpicker_id = system
		// .getIdentifier("amPm", "id", "android");

		NumberPicker hour_numberpicker = (NumberPicker) time_picker
				.findViewById(hour_numberpicker_id);
		NumberPicker minute_numberpicker = (NumberPicker) time_picker
				.findViewById(minute_numberpicker_id);
		// NumberPicker ampm_numberpicker = (NumberPicker) time_picker
		// .findViewById(ampm_numberpicker_id);

		set_numberpicker_text_colour(hour_numberpicker);
		set_numberpicker_text_colour(minute_numberpicker);
		// set_numberpicker_text_colour(ampm_numberpicker);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void set_numberpicker_text_colour(NumberPicker number_picker) {
		final int count = number_picker.getChildCount();
		final int color = Color.parseColor("#ffffff");

		for (int i = 0; i < count; i++) {
			View child = number_picker.getChildAt(i);

			try {
				Field wheelpaint_field = number_picker.getClass()
						.getDeclaredField("mSelectorWheelPaint");
				wheelpaint_field.setAccessible(true);

				((Paint) wheelpaint_field.get(number_picker)).setColor(color);
				((EditText) child).setTextColor(color);
				number_picker.invalidate();
			} catch (NoSuchFieldException e) {
				Log.w("NumberPickerTextColor", e);
			} catch (IllegalAccessException e) {
				Log.w("NumberPickerTextColor", e);
			} catch (IllegalArgumentException e) {
				Log.w("NumberPickerTextColor", e);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_submit:

			int hr = time_picker.getCurrentHour();
			int mins = time_picker.getCurrentMinute();
			String d1;
			// if (time_picker.is24HourView()) {
			//
			// String d[] = txt_currentDate.getText().toString().split("-");
			//
			// d1 = d[1] +"-"+ d[0]+"-"+d[2];
			//
			// Adate = txt_currentDate + " " + String.valueOf(hr) + ":"
			// + String.valueOf(mins)+":"+"000";
			// }

			// else {

			String d[] = txt_currentDate.getText().toString().split("-");

			d1 = d[1] + "-" + d[0] + "-" + d[2];

			Adate = d1 + " " + String.valueOf(hr) + ":"
					+ String.valueOf(mins) + ":" + "00";
			// }

			Calendar c = Calendar.getInstance();

			SimpleDateFormat sdft = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

			Actual_date = sdft.format(c.getTime());

			if (dbcon.CheckSupervisor(d1, username)) {
				Toast.makeText(SupervisorAttendance.this,
						"Attendance marked already", Toast.LENGTH_LONG).show();
			} else {
				long rowid = dbcon
						.insert(new String[] { BDE_CODE, username, Adate,
								Actual_date, String.valueOf(lat),
								String.valueOf(lon), "0" }, new String[] {
								"BDE_CODE", "BA_id", "Adate", "Actual_date",
								"lat", "lon", "savedServer" },
								"supervisor_attendance");

				if (rowid > -1) {

					upload_attendance = new AttendanceAsync();
					upload_attendance.execute();

				}
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

//	private int get24hrTime(int time) {
//		int hr = 0;
//
//		hr = time + 12;
//
//		return hr;
//
//	}

	/*private void refreshDisplay() {
		refreshDisplay(new LocationInfo(getApplicationContext()));
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
			Toast.makeText(SupervisorAttendance.this,
					"Unable to get GPS location! Try again later!!",
					Toast.LENGTH_LONG).show();
		}

	}

	private final BroadcastReceiver lftBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// extract the location info in the broadcast
			final LocationInfo locationInfo = (LocationInfo) intent
					.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
			// refresh the display with it
			refreshDisplay(locationInfo);
		}
	};*/

	@Override
	public void onResume() {
		super.onResume();

		// cancel any notification we may have received from
		// TestBroadcastReceiver
	/*	((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
				.cancel(1234);

		refreshDisplay();

		// This demonstrates how to dynamically create a receiver to listen to
		// the location updates.
		// You could also register a receiver in your manifest.
		final IntentFilter lftIntentFilter = new IntentFilter(
				LocationLibraryConstants
						.getLocationChangedPeriodicBroadcastAction());
		registerReceiver(lftBroadcastReceiver, lftIntentFilter);*/
	}

	private class AttendanceAsync extends AsyncTask<Void, Void, SoapPrimitive> {

		SoapPrimitive soap_result;
		ProgressDialog progress;

		@Override
		protected SoapPrimitive doInBackground(Void... params) {
			// TODO Auto-generated method stub

			soap_result = service.SaveSupervisorAttendance(BDE_CODE, username,
					Adate, Actual_date, String.valueOf(lat),
					String.valueOf(lon));

			return soap_result;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(SupervisorAttendance.this);
			progress.setTitle("Status");
			progress.setMessage("Uploading....Please wait...");
			progress.show();
			progress.setCancelable(false);
		}

		@Override
		protected void onPostExecute(SoapPrimitive result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progress.dismiss();
			if (result != null) {
				if (result.toString().equalsIgnoreCase("true")) {
					Toast.makeText(SupervisorAttendance.this,
							"Data Uploaded Successfully!!", Toast.LENGTH_LONG)
							.show();
					startActivity(new Intent(SupervisorAttendance.this,
							DashboardNewActivity.class));

                    dbcon.update("savedServer", new String[] { "1" },
							new String[] { "savedServer" },
							"supervisor_attendance", "0");

				} else {
					Toast.makeText(SupervisorAttendance.this,
							"Data Not uploaded", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(SupervisorAttendance.this,
						"Please check internet Connectivity", Toast.LENGTH_LONG)
						.show();
			}
		}

	}

}
