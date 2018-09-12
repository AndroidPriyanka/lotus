package com.prod.sudesi.lotusherbalsnew;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.prod.sudesi.lotusherbalsnew.adapter.BAMonthReportAdapter;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;

public class BAMonthReportPage extends Activity {

	SharedPreferences shp;
	SharedPreferences.Editor shpeditor;

	Context context;

	private ProgressDialog prgdialog;

	TextView tv_h_username;
	Button btn_home, btn_logout;
	String username;
	
	BAMonthReportAdapter adapter;
	LotusWebservice service;
	
	ListView lv_bam_report;
	Dbcon db;
	
	HorizontalScrollView horizantalscrollviewforbamonthreport;
	
	static private ArrayList<HashMap<String, String>> todaymessagelist = new ArrayList<HashMap<String, String>>();
	
	String month,year;
	
	String FromDate;
	String ToDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_bamonth_report_page);
		//////////Crash Report
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		
		context = BAMonthReportPage.this;

		prgdialog = new ProgressDialog(context);
		service = new LotusWebservice(BAMonthReportPage.this);

		shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
		shpeditor = shp.edit();

		username = shp.getString("username", "");
		Log.v("", "username==" + username);
		
		lv_bam_report = (ListView) findViewById(R.id.listView_ba_month_report);

		try{
			
			Intent i = getIntent();
			month = i.getStringExtra("month");
			year = i.getStringExtra("year");
			
			Get_dates_from_to(year, month);
			
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
		btn_home = (Button)findViewById(R.id.btn_home);
		btn_logout =(Button)findViewById(R.id.btn_logout);
		
		tv_h_username = (TextView)findViewById(R.id.tv_h_username);
		tv_h_username.setText(username);
		
		db = new Dbcon(this);

		btn_logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			//startActivity(new Intent(getApplicationContext(), LoginActivity.class));	
			}
		});
		
		btn_home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent i = new Intent(getApplicationContext(), DashboardNewActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
		//	startActivity(new Intent(getApplicationContext(), DashboardNewActivity.class));	
			}
		});
		
		
		horizantalscrollviewforbamonthreport =(HorizontalScrollView)findViewById(R.id.horizantalview_for_bamonth_report);
		
		horizantalscrollviewforbamonthreport.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				int scrollX = v.getScrollX();
				int scrollY = v.getScrollY();

				horizantalscrollviewforbamonthreport.scrollTo(scrollX, scrollY);
				return false;
			}
		});
		
		lv_bam_report.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				
				final TextView tv_product = (TextView)view.findViewById(R.id.tv_barm_product);
				
				
				final Dialog d = new Dialog(BAMonthReportPage.this);
				d.requestWindowFeature(Window.FEATURE_NO_TITLE);
				d.setContentView(R.layout.popup_report);
				
				
				final Dialog d2 = new Dialog(BAMonthReportPage.this);
				d2.requestWindowFeature(Window.FEATURE_NO_TITLE);
				d2.setContentView(R.layout.popup_report); 
				final TextView productid = (TextView)view.findViewById(R.id.tv_barm_code_no);
				
				Log.e("","id =="+productid.getText().toString());
				tv_product.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						
						String ppid = productid.getText().toString();
						
						db.open();
						String produ = db.getproductname123(ppid);
						db.close();
						
						d.dismiss();
						

						TextView text = (TextView)d2.findViewById(R.id.textView1);
						
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
				
				Log.e("", " "+tv_product.getText().toString());
				
				
			}
		
		});
	}
	
	
	public class GetBAMonthreport extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			String returnMessage = null;
			try {
				todaymessagelist.clear();
				Log.v("","GetBAMonthreport");
				 getBAMonthreportfromWebservice();//soap messag call
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
			
			prgdialog.dismiss();
			loadMonthReports();
		}
	}


//	public void getBAMonthreportfromWebservice() {
//		// TODO Auto-generated method stub
//		
//		try{
//		// //soap call
//					Log.e("","not2=="+username);
//				//	String month = "11";
//				//	String year = "2014";
//					SoapObject resultsRequestSOAP = service.GetBAOutletMothWiseSales(username,month,year);
//					
//					if (resultsRequestSOAP != null) {
//						Log.e("","not3");
//					//	todaymessagelist.clear();
//						Log.e("","not4");
//						// soap response
//						for (int i = 0; i < resultsRequestSOAP.getPropertyCount(); i++) {
//							Log.e("","not5");
//
//							SoapObject getmessaage = (SoapObject) resultsRequestSOAP
//									.getProperty(i);
//							HashMap<String, String> map = new HashMap<String, String>();
//
//							// display messages by adding it to listview
//							//if (!String.valueOf(getmessaage.getProperty("CreatedDate"))
//							//		.equals("false")) {
//							if(getmessaage !=null){
//								
//								map.put("ProductCode", String.valueOf(getmessaage.getProperty("ProductCode")));
//								
//								map.put("ProductName", String.valueOf(getmessaage.getProperty("ProductName")));
//								
//								map.put("Size",String.valueOf(getmessaage.getProperty("Size")));
//								
//								map.put("MRP",String.valueOf(getmessaage.getProperty("MRP")));
//								
//								map.put("OpeningStock",String.valueOf(getmessaage.getProperty("OpeningStock")));
//								
//								map.put("Receipt",String.valueOf(getmessaage.getProperty("Receipt")));
//								
//								map.put("ReturnStock",String.valueOf(getmessaage.getProperty("ReturnStock")));
//								
//								map.put("TotalStock",String.valueOf(getmessaage.getProperty("TotalStock")));
//								
//								map.put("Sales",String.valueOf(getmessaage.getProperty("Sales")));
//								
//								map.put("ClosingStock",String.valueOf(getmessaage.getProperty("ClosingStock")));
//								
//								
//								
//								todaymessagelist.add(map);
//							}
//						}
//					}else{
//						
//					//	Toast.makeText(NotificationFragment.this, "Connectivity Error!!", Toast.LENGTH_SHORT).show();
//						
//					}
//
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//		
//	}
	
	public void getBAMonthreportfromWebservice()
	{
		// TODO Auto-generated method stub
		
		try{
		// //soap call
					Log.e("","not2=="+username);
				//	String month = "11";
				//	String year = "2014";
//					SoapObject resultsRequestSOAP = service.GetBAOutletMothWiseSales(username,month,year);
					db.open();
					
					Log.e("month_name--year", month+"--"+year);
					/*Cursor c = db.fetchallSpecifyMSelect("stock_monthwise", new String[]{"db_id",
							"product_name",
							"size",
							"price",
							"opening_stock",
							"stock_received",
							"return_saleable",
							"stock_in_hand",
							"sold_stock",
							"close_bal", }, "emp_id = '"+username+"' and month_name = '"+month+"' and year = '"+year+"'", null, null);
				*/	
					
					Cursor c = db.getBocMonthwisedata(FromDate,ToDate);
					
					if(c != null && c.getCount() >0)
					{
						c.moveToFirst();
						
						do{
							
							HashMap<String, String> map = new HashMap<String, String>();

										
								map.put("ProductCode", String.valueOf(c.getString(0)));
								
								map.put("ProductName", String.valueOf(c.getString(1)));
								
								map.put("Size",String.valueOf(c.getString(2)));
								
								map.put("MRP",String.valueOf(c.getString(3)));
								
								map.put("OpeningStock",String.valueOf(c.getString(4)));
								
								map.put("Receipt",String.valueOf(c.getString(5)));
								
								map.put("ReturnStock",String.valueOf(c.getString(6)));
								if(c.getString(7) != null && c.getString(8) != null)
								{
									if(c.getString(7).equals("") || c.getString(8).equals(""))
									{
										map.put("TotalStock",c.getString(7));
									}
									else
									{
										map.put("TotalStock",String.valueOf(Integer.parseInt(c.getString(7))+Integer.parseInt(c.getString(8))));
									}
								}
								else
								{
									map.put("TotalStock",c.getString(7));
								}
								
								
								map.put("Sales",String.valueOf(c.getString(8)));
								
								map.put("ClosingStock",String.valueOf(c.getString(9)));
								
								todaymessagelist.add(map);
													
							
						}while(c.moveToNext());
						
					}
					db.close();


				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
		
	}


	public void loadMonthReports() {
		// TODO Auto-generated method stub
		
		adapter = new BAMonthReportAdapter(BAMonthReportPage.this, todaymessagelist);
		lv_bam_report.setAdapter(adapter);// add custom adapter to
													// listview
		
	}
	
	private void Get_dates_from_to(String year2, String month2) {
		// TODO Auto-generated method stub
		String yearsp[] = year2.split("-");
		String from_year = yearsp[0];
		String to_year = yearsp[1];

		String bb;

		if (month.equalsIgnoreCase("BOC 1")) {

			FromDate = from_year + "-" + "03" + "-" + "26";
			ToDate = from_year + "-" + "04" + "-" + "25";

			// Toast.makeText(context, "boc12 = "+bb, Toast.LENGTH_LONG).show();
		}

		if (month.equalsIgnoreCase("BOC 2")) {

			FromDate = from_year + "-" + "04" + "-" + "26";
			ToDate = from_year + "-" + "05" + "-" + "25";

			// Toast.makeText(context, "boc1 = "+bb, Toast.LENGTH_LONG).show();
		}
		if (month.equalsIgnoreCase("BOC 3")) {

			FromDate = from_year + "-" + "05" + "-" + "26";
			ToDate = from_year + "-" + "06" + "-" + "25";
			// Toast.makeText(context, "boc2 = "+bb, Toast.LENGTH_LONG).show();
		}
		if (month.equalsIgnoreCase("BOC 4")) {

			FromDate = from_year + "-" + "06" + "-" + "26";
			ToDate = from_year + "-" + "07" + "-" + "25";

		}
		if (month.equalsIgnoreCase("BOC 5")) {

			FromDate = from_year + "-" + "07" + "-" + "26";
			ToDate = from_year + "-" + "08" + "-" + "25";

		}
		if (month.equalsIgnoreCase("BOC 6")) {

			FromDate = from_year + "-" + "08" + "-" + "26";
			ToDate = from_year + "-" + "09" + "-" + "25";
		}
		if (month.equalsIgnoreCase("BOC 7")) {

			FromDate = from_year + "-" + "09" + "-" + "26";
			ToDate = from_year + "-" + "10" + "-" + "25";
		}

		if (month.equalsIgnoreCase("BOC 8")) {

			FromDate = from_year + "-" + "10" + "-" + "26";
			ToDate = from_year + "-" + "11" + "-" + "25";
		}
		if (month.equalsIgnoreCase("BOC 9")) {

			FromDate = from_year + "-" + "11" + "-" + "26";
			ToDate = from_year + "-" + "12" + "-" + "25";
		}
		if (month.equalsIgnoreCase("BOC 10")) {

			FromDate = from_year + "-" + "12" + "-" + "26";
			ToDate = to_year + "-" + "01" + "-" + "25";
		}
		if (month.equalsIgnoreCase("BOC 11")) {

			FromDate = to_year + "-" + "01" + "-" + "26";
			ToDate = to_year + "-" + "02" + "-" + "25";
		}
		if (month.equalsIgnoreCase("BOC 12")) {

			FromDate = to_year + "-" + "02" + "-" + "26";
			ToDate = to_year + "-" + "03" + "-" + "25";
		}
		
		new GetBAMonthreport().execute();

	}

}
