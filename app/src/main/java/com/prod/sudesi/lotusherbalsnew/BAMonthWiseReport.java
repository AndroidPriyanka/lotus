package com.prod.sudesi.lotusherbalsnew;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.adapter.BAMonthReportAdapter;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

public class BAMonthWiseReport extends Activity {

	SharedPreferences shp;
	SharedPreferences.Editor shpeditor;

	Context context;

	private ProgressDialog prgdialog;

	TextView tv_h_username;
	Button btn_home, btn_logout;
	String username,bdename;
	
	BAMonthReportAdapter adapter;
	LotusWebservice service;
	
	ListView lv_bam_report;
	
	HorizontalScrollView horizantalscrollviewforbamonthreport;
	
	static private ArrayList<HashMap<String, String>> todaymessagelist = new ArrayList<HashMap<String, String>>();
	
	Button btn_search;
	
	Spinner sp_year,sp_month;
	
	String selected_month;
	String selected_year;
	
	//----------------
	String current_year_n1,current_year_n2,previous_year_p1,previous_year_p2;
	
	int int_current_year_n1,int_current_year_n2,int_previous_year_p1,int_previous_year_p2;
	
	String current_server_date;
	
	String firstyear,sencondyear;

	//----------------
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_bamonth_wise_report);
		//////////Crash Report
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		context = BAMonthWiseReport.this;

		prgdialog = new ProgressDialog(context);
		service = new LotusWebservice(BAMonthWiseReport.this);

		shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
		shpeditor = shp.edit();

	//	lv_bam_report = (ListView) findViewById(R.id.listView_ba_month_report);

		tv_h_username = (TextView) findViewById(R.id.tv_h_username);
		btn_home = (Button) findViewById(R.id.btn_home);
		btn_logout = (Button) findViewById(R.id.btn_logout);
		

		username = shp.getString("username", "");
		Log.v("", "username==" + username);

		bdename = shp.getString("BDEusername","");

		tv_h_username.setText(bdename);

		btn_logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new  Intent(getApplicationContext(), LoginActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});

		btn_home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new  Intent(getApplicationContext(), DashboardNewActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				//startActivity(new Intent(getApplicationContext(),
				//		DashboardNewActivity.class));
			}
		});
		
		
		//--------------/---------
		try{
		current_year_n2 = shp.getString("current_year", "");
		
		int_current_year_n2 = Integer.parseInt(current_year_n2);
		
		String comparedatewith = current_year_n2+"-03-25";
		
		
		current_server_date = shp.getString("todaydate", "");
		
		Log.v("","current_server_date="+current_server_date);
		
		Log.v("","comparedatewith="+comparedatewith);
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;Date date2 = null;
		try {
			date1 = sdf.parse(current_server_date);
			date2 = sdf.parse(comparedatewith);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    		
		if(!current_year_n2.equalsIgnoreCase("") && date1.compareTo(date2)>0){
			
			int int_current_year_n22 =  int_current_year_n2 + 1 ;
			
			String current_year_n2 = String.valueOf(int_current_year_n22);
			

			int_current_year_n1 = int_current_year_n22 - 1;
			
			current_year_n1 = String.valueOf(int_current_year_n1);
			

			int_previous_year_p1 = int_current_year_n1 - 1;
			
			previous_year_p1 = String.valueOf(int_previous_year_p1);

			
			firstyear = current_year_n1 + "-"+current_year_n2;//---------
			
			sencondyear = previous_year_p1 +"-"+ current_year_n1;
			
			
		}else{

			int_current_year_n1 = int_current_year_n2 - 1;
			
			current_year_n1 = String.valueOf(int_current_year_n1);
			

			int_previous_year_p1 = int_current_year_n1 - 1;
			
			previous_year_p1 = String.valueOf(int_previous_year_p1);
			

			firstyear = current_year_n1 +"-"+ current_year_n2;//---------
			
			sencondyear = previous_year_p1 +"-"+ current_year_n1;
		}
		
		//-------------/----------
		// ---------------------
		
	}catch(Exception e){
		e.printStackTrace();
	}
		
		
		//horizantalscrollviewforbamonthreport =(HorizontalScrollView)findViewById(R.id.horizantalview_for_bamonth_report);
		
		/*horizantalscrollviewforbamonthreport.setOnTouchListener(new OnTouchListener() {
			
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				int scrollX = v.getScrollX();
				int scrollY = v.getScrollY();

				horizantalscrollviewforbamonthreport.scrollTo(scrollX, scrollY);
				return false;
			}
		});*/
		
		sp_month  = (Spinner)findViewById(R.id.sp_bamr_month);
		sp_year = (Spinner)findViewById(R.id.sp_bamr_year);
		
		List<String> list_moth = new ArrayList<String>();
		list_moth.add("Select");
		list_moth.add("BOC 1");
		list_moth.add("BOC 2");
		list_moth.add("BOC 3");
		
		list_moth.add("BOC 4");
		list_moth.add("BOC 5");
		list_moth.add("BOC 6");
		
		list_moth.add("BOC 7");
		list_moth.add("BOC 8");
		list_moth.add("BOC 9");
		
		list_moth.add("BOC 10");
		list_moth.add("BOC 11");
		list_moth.add("BOC 12");
		
		//List<String> list_moth = new ArrayList<String>();
		
	/*	list_moth.add("January");
		list_moth.add("February");
		list_moth.add("March");
		
		list_moth.add("April");
		list_moth.add("May");
		list_moth.add("June");
		
		list_moth.add("July");
		list_moth.add("August");
		list_moth.add("September");
		
		list_moth.add("October");
		list_moth.add("November");
		list_moth.add("December");*/
		
		
		
		ArrayAdapter<String> adapter_month = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_sp_item, list_moth);
		adapter_month.setDropDownViewResource(R.layout.custom_spinner_dropdown_text);
		sp_month.setAdapter(adapter_month);
		
		List<String> list_year = new ArrayList<String>();
		
		list_year.add("Select");
		list_year.add(sencondyear);
		list_year.add(firstyear);
		
		
		ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_sp_item,list_year);
		adapter_year.setDropDownViewResource(R.layout.custom_spinner_dropdown_text);
		sp_year.setAdapter(adapter_year);
		
		
		sp_month.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				String selectedItem = sp_month.getItemAtPosition(position).toString();
				
				if(selectedItem.equalsIgnoreCase("Select")){
				
					selected_month = "Select"; 
				
				}else{
					
					//selected_month = getmonthNo1(selectedItem); 
					selected_month = selectedItem; 
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		
		});
		
		sp_year.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				 selected_year = sp_year.getItemAtPosition(position).toString();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		
		
		});
		
		btn_search = (Button)findViewById(R.id.btn_barm_search);
		
		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				/*try{
					
					new GetBAMonthreport().execute();
					
				}catch(Exception e){
					
					e.printStackTrace();
				}*/
				
				try{
					
					if(selected_month.equalsIgnoreCase("Select")){
						Log.v("", "sdfsfddfsf");
						Toast.makeText(getApplicationContext(), "Select Month", Toast.LENGTH_SHORT).show();
			
					}else if(selected_year.equalsIgnoreCase("Select")){
						Log.v("", "sdfsfddfsf1");
						Toast.makeText(getApplicationContext(), "Select Year", Toast.LENGTH_SHORT).show();
			
					}else{
						
						
						//checkandsendboc(selected_month,selected_year);
						
						Log.v("","month=="+selected_month);
						Log.v("","year=="+selected_year);

						Intent i = new Intent(getApplicationContext(), BAMonthReportPage.class);
							i.putExtra("month", selected_month);
							i.putExtra("year", selected_year);
							startActivity(i);
				
				//Intent i = new Intent(getApplicationContext(), BAMonthReportPage.class);
			//	i.putExtra("month", selected_month);
			//	i.putExtra("year", selected_year);
			//	startActivity(i);
					}
				}catch(Exception e){
					
					e.printStackTrace();
				}
			}
		});
		
	}
	
	
	protected void checkandsendboc(String selected_month,String selected_year) {
		// TODO Auto-generated method stub
		
		String s_year[] = selected_year.split("-");
		
		String s1 = s_year[0];
		String s2 = s_year[1];
		
		
		String ss = selected_month;
		String s3 = "";
		
		
	/*	if (selected_month.equals("04")) {
			s3 = s1;
		}  if (selected_month.equals("05")) {
			s3 = s1;
		}  if (selected_month.equals("06")) {
			s3 = s1;
		}  if (selected_month.equals("07")) {
			s3 = s1;
		}  if (selected_month.equals("08")) {
			s3 = s1;
		}  if (selected_month.equals("09")) {
			s3 = s1;
		}  if (selected_month.equals("10")) {
			s3 = s1;
		}  if (selected_month.equals("11")) {
			s3 = s1;
		}  if (selected_month.equals("12")) {
			s3 = s1;
		}  if (selected_month.equals("01")) {
			s3 = s2;
		} else if (selected_month.equals("02")) {
			s3 = s2;
		}  if (selected_month.equals("03")) {
			s3 = s2;
		}
		*/
		
		if (selected_month.equals("April")) {
			s3 = s1;
		}  if (selected_month.equals("May")) {
			s3 = s1;
		}  if (selected_month.equals("June")) {
			s3 = s1;
		}  if (selected_month.equals("July")) {
			s3 = s1;
		}  if (selected_month.equals("August")) {
			s3 = s1;
		}  if (selected_month.equals("September")) {
			s3 = s1;
		}  if (selected_month.equals("October")) {
			s3 = s1;
		}  if (selected_month.equals("November")) {
			s3 = s1;
		}  if (selected_month.equals("December")) {
			s3 = s1;
		}  if (selected_month.equals("January")) {
			s3 = s2;
		}  if (selected_month.equals("February")) {
			s3 = s2;
		}  if (selected_month.equals("March")) {
			s3 = s2;
		}
		
		Log.v("","month=="+selected_month);
		Log.v("","year=="+s3);

		Intent i = new Intent(getApplicationContext(), BAMonthReportPage.class);
			i.putExtra("month", selected_month);
			i.putExtra("year", s3);
			startActivity(i);
		
	}


	public String getmonthNo(String monthName) {
		String month = "";

		if (monthName.equals("January")) {
			month = "01";
		}  if (monthName.equals("February")) {
			month = "02";
		}  if (monthName.equals("March")) {
			month = "03";
		}  if (monthName.equals("April")) {
			month = "04";
		}  if (monthName.equals("May")) {
			month = "05";
		}  if (monthName.equals("June")) {
			month = "06";
		}  if (monthName.equals("July")) {
			month = "07";
		}  if (monthName.equals("August")) {
			month = "08";
		}  if (monthName.equals("September")) {
			month = "09";
		}  if (monthName.equals("October")) {
			month = "10";
		}  if (monthName.equals("November")) {
			month = "11";
		}  if (monthName.equals("December")) {
			month = "12";
		}

		return month;
	}
	
//	public String getmonthNo1(String monthName) {
//		String month = "";
//
//		if (monthName.equals("BOC 1")) {
//			month = "04";
//		}  if (monthName.equals("BOC 2")) {
//			month = "05";
//		}  if (monthName.equals("BOC 3")) {
//			month = "06";
//		}  if (monthName.equals("BOC 4")) {
//			month = "07";
//		}  if (monthName.equals("BOC 5")) {
//			month = "08";
//		}  if (monthName.equals("BOC 6")) {
//			month = "09";
//		}  if (monthName.equals("BOC 7")) {
//			month = "10";
//		}  if (monthName.equals("BOC 8")) {
//			month = "11";
//		}  if (monthName.equals("BOC 9")) {
//			month = "12";
//		}  if (monthName.equals("BOC 10")) {
//			month = "01";
//		}  if (monthName.equals("BOC 11")) {
//			month = "02";
//		}  if (monthName.equals("BOC 12")) {
//			month = "03";
//		}
//
//		return month;
//	}
	
	public String getmonthNo1(String monthName) {
		String month = "";

		if (monthName.equals("BOC 1")) {
			month = "April";
		}  if (monthName.equals("BOC 2")) {
			month = "May";
		}  if (monthName.equals("BOC 3")) {
			month = "June";
		}  if (monthName.equals("BOC 4")) {
			month = "July";
		}  if (monthName.equals("BOC 5")) {
			month = "August";
		}  if (monthName.equals("BOC 6")) {
			month = "September";
		}  if (monthName.equals("BOC 7")) {
			month = "October";
		}  if (monthName.equals("BOC 8")) {
			month = "November";
		}  if (monthName.equals("BOC 9")) {
			month = "December";
		}  if (monthName.equals("BOC 10")) {
			month = "January";
		}  if (monthName.equals("BOC 11")) {
			month = "February";
		}  if (monthName.equals("BOC 12")) {
			month = "March";
		}

		return month;
	}


	public class GetBAMonthreport extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			String returnMessage = null;
			try {
				todaymessagelist.clear();
				// getBAMonthreportfromWebservice();//soap messag call
			} catch (Exception e) {
				returnMessage = e.getMessage();
			}
			return returnMessage;
			
		}
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			prgdialog.setMessage("Please Wait...");
			prgdialog.show();
			prgdialog.setCancelable(false);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (prgdialog != null && prgdialog.isShowing() && !BAMonthWiseReport.this.isFinishing()) {
				prgdialog.dismiss();
			}
			loadMonthReports();
		}
	}


	/*public void getBAMonthreportfromWebservice() {
		// TODO Auto-generated method stub
		
		try{
		// //soap call
					Log.e("","not2=="+username);
					String month = "11";
					String year = "2014";
					SoapObject resultsRequestSOAP = service.GetBAOutletMothWiseSales(username,month,year);
					
					if (resultsRequestSOAP != null) {
						Log.e("","not3");
					//	todaymessagelist.clear();
						Log.e("","not4");
						// soap response
						for (int i = 0; i < resultsRequestSOAP.getPropertyCount(); i++) {
							Log.e("","not5");

							SoapObject getmessaage = (SoapObject) resultsRequestSOAP
									.getProperty(i);
							HashMap<String, String> map = new HashMap<String, String>();

							// display messages by adding it to listview
							//if (!String.valueOf(getmessaage.getProperty("CreatedDate"))
							//		.equals("false")) {
							if(getmessaage !=null){
								
								map.put("ProductCode", String.valueOf(getmessaage.getProperty("ProductCode")));
								
								map.put("ProductName", String.valueOf(getmessaage.getProperty("ProductName")));
								
								map.put("Size",String.valueOf(getmessaage.getProperty("Size")));
								
								map.put("MRP",String.valueOf(getmessaage.getProperty("MRP")));
								
								map.put("OpeningStock",String.valueOf(getmessaage.getProperty("OpeningStock")));
								
								map.put("Receipt",String.valueOf(getmessaage.getProperty("Receipt")));
								
								map.put("ReturnStock",String.valueOf(getmessaage.getProperty("ReturnStock")));
								
								map.put("TotalStock",String.valueOf(getmessaage.getProperty("TotalStock")));
								
								map.put("Sales",String.valueOf(getmessaage.getProperty("Sales")));
								
								map.put("ClosingStock",String.valueOf(getmessaage.getProperty("ClosingStock")));
								
								
								
								todaymessagelist.add(map);
							}
						}
					}else{
						
					//	Toast.makeText(NotificationFragment.this, "Connectivity Error!!", Toast.LENGTH_SHORT).show();
						
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
		
	}*/


	public void loadMonthReports() {
		// TODO Auto-generated method stub
		
		adapter = new BAMonthReportAdapter(BAMonthWiseReport.this, todaymessagelist);
	//	lv_bam_report.setAdapter(adapter);// add custom adapter to
													// listview
		
	}

}
