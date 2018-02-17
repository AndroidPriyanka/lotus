package com.prod.sudesi.lotusherbalsnew.tester;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.prod.sudesi.lotusherbalsnew.DashboardNewActivity;
import com.prod.sudesi.lotusherbalsnew.LoginActivity;
import com.prod.sudesi.lotusherbalsnew.R;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbhelper;
import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;


public class TesterSubmitActivity extends Activity {

	
	Context context;
	Spinner product_category, product_type, products;
	Button btn_save;
	

	TextView sizet, catidt, enaidt, dbidt,shadenon;

	SharedPreferences shp;
	SharedPreferences.Editor shpeditor;

	//com.sudesi.adapter.TesterListAdapter productadapter;
	com.prod.sudesi.lotusherbalsnew.adapter.LazyAdapter2 productadapter;
	

	ArrayList<HashMap<String, String>> productDetailsArray = new ArrayList<HashMap<String, String>>();
	ListView listView;

	TextView table_idlt, sizelt, pricelt, enalt, catlt, dbidlt;
	TableLayout tl_stock_calculation, Table_list_tages;

	ArrayList<String> productcategory = new ArrayList<String>();
	ArrayList<String> productArray = new ArrayList<String>();
	ArrayList<String> producttypeArray = new ArrayList<String>();
	Dbcon db;
	int selected_product_category_id, selected_product_type_id,
			selected_product_id;

	String selected_product_category, selected_product_type,
			selected_product_name, selected_product_id1;
	String cll = "0";

	private ProgressDialog mProgress;
	LotusWebservice service;
	Cursor test_array;
	LinearLayout ll;
	
	//RadioGroup rg_status_grp;
	//RadioButton rb_available,rb_na,rb_indent;
	CheckBox ch_status;
	
	ScrollView scrv_tester;
	HorizontalScrollView hscv_tester;

	String product_status="";
	String req_date="";
	String delv_date="";
	
	TextView tv_h_username;
	Button btn_home,btn_logout;
	String username,bdename;
	
	RadioGroup rg_old_new_price;
	RadioButton rb_old;
	RadioButton rb_new;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_tester_submit);
		//////////Crash Report
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		
		context = getApplicationContext();
		

		product_category = (Spinner) 
				findViewById(R.id.sp_product_category_test);
		product_type = (Spinner) findViewById(R.id.sp_product_type_test);
		products = (Spinner) findViewById(R.id.sp_products_test);

		btn_save = (Button)findViewById(R.id.btn_tester_save);
		
		sizet = (TextView)findViewById(R.id.tv_product_size_test);

		catidt = (TextView)findViewById(R.id.tv_category_code_test);
		enaidt = (TextView) findViewById(R.id.tv_enacode_test);
		dbidt = (TextView) findViewById(R.id.tv_db_id_test);
		shadenon = (TextView)findViewById(R.id.tv_shadetester);
		
		rg_old_new_price = (RadioGroup)findViewById(R.id.radioGroup1);
		
		rb_new =(RadioButton)findViewById(R.id.rb_new);
		rb_old =(RadioButton)findViewById(R.id.rb_old);


		
		Table_list_tages = (TableLayout)findViewById(R.id.Tablelisttages_tester);
		ll = (LinearLayout) findViewById(R.id.ll_spinerlayout_tester);
		listView = (ListView) findViewById(R.id.lv_product_list_tester);
		
		db = new Dbcon(this);
		mProgress = new ProgressDialog(this);
		service = new LotusWebservice(context);

		shp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
		shpeditor = shp.edit();
		
		tv_h_username = (TextView)findViewById(R.id.tv_h_username);
		btn_home = (Button)findViewById(R.id.btn_home);
		btn_logout =(Button)findViewById(R.id.btn_logout);
		username = shp.getString("username", "");
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
			startActivity(new Intent(getApplicationContext(), DashboardNewActivity.class));
			}
		});
	
		final Calendar c = Calendar.getInstance();
		
		final SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
		
			String div = shp.getString("div", "");
			if(div.equalsIgnoreCase("LH & LHM") || div.equalsIgnoreCase("LH & LM")){
				
				
				db.open();
				productcategory = db.getproductcategoryoftester(); // ------------
				db.close();
				// System.out.println(productArray);
				Log.e("", "kkkklklk111");
				
			}if(div.equalsIgnoreCase("LH")) {
				productcategory.clear();
				productcategory.add("Select");
				productcategory.add("SKIN");
				
			}
			//db.open();
		//productcategory = db.getproductcategoryoftester(); // --------------
		//db.close();
		// System.out.println(productArray);
		Log.e("", "kkkklklk111");
		ArrayAdapter<String> product_adapter = new ArrayAdapter<String>(
		// context, android.R.layout.simple_spinner_item,
				context, R.layout.custom_sp_item, productcategory);

		product_adapter
		// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				.setDropDownViewResource(R.layout.custom_spinner_dropdown_text);
		product_category.setAdapter(product_adapter);
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}

	product_category
			.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub

					selected_product_category = product_category
							.getItemAtPosition(arg2).toString();
					
					if (selected_product_category
							.equalsIgnoreCase("Select")
							|| selected_product_category
									.equalsIgnoreCase("")) {
					//	ch_status.setChecked(false);
					//	ch_status.setVisibility(View.GONE);
						btn_save.setVisibility(View.GONE);
						listView.setVisibility(View.GONE);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								context,
								android.R.layout.simple_spinner_dropdown_item,
								new String[] {});
						product_type.setAdapter(adapter);
						products.setAdapter(adapter);
						rg_old_new_price.clearCheck();

					} else {
						
						rg_old_new_price.clearCheck();
						
						db.open();

						producttypeArray = db
								.getproductypeoftester1(selected_product_category); // -------------
						System.out.println(producttypeArray);
						
						db.close();
						
						ArrayAdapter<String> product_adapter1 = new ArrayAdapter<String>(
								// context,
								// android.R.layout.simple_spinner_item,
								context, R.layout.custom_sp_item,
								producttypeArray);

						product_adapter1
						// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								.setDropDownViewResource(R.layout.custom_spinner_dropdown_text);
						product_type.setAdapter(product_adapter1);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

	product_type.setOnItemSelectedListener(new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub

			// ((TextView)arg0.getChildAt(0)).setTextColor(Color.rgb(249,
			// 249, 249));
			selected_product_type = product_type.getItemAtPosition(arg2)
					.toString();
			
			
			if (selected_product_type.equalsIgnoreCase("Select")
					|| selected_product_category.equalsIgnoreCase("")) {
			//	ch_status.setChecked(false);
			//	ch_status.setVisibility(View.GONE);
				btn_save.setVisibility(View.GONE);
				listView.setVisibility(View.GONE);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						context,
						android.R.layout.simple_spinner_dropdown_item,
						new String[] {});
				// product_type.setAdapter(adapter);
				products.setAdapter(adapter);

				Toast.makeText(getApplicationContext(), "Select Product type",
						Toast.LENGTH_SHORT).show();
			} else {

				String selected_category = product_category
						.getSelectedItem().toString();
				String selected_type = product_type.getSelectedItem()
						.toString();
				
				getallproductslist(selected_category, selected_type,"");
				
				Log.v("", "" + selected_category + " " + selected_type);
				
//				listView.setVisibility(View.GONE);
				//rb_new.setChecked(false);
				//rb_old.setChecked(false);
//				rg_old_new_price.clearCheck();
			
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	});
	
//	rg_old_new_price.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//		
//		@Override
//		public void onCheckedChanged(RadioGroup group, int checkedId) {
//			// TODO Auto-generated method stub
//			try{
//			String selected_category = product_category
//					.getSelectedItem().toString();
//			if(selected_category==null){
//				
//				
//			}
//			
//			String selected_type = product_type.getSelectedItem()
//					.toString();
//			if(selected_type==null){
//				
//				
//			}
//
//			Log.v("", "" + selected_category + " " + selected_type);
//
//			
//			if (selected_product_category
//					.equalsIgnoreCase("Select")
//					|| selected_product_category
//							.equalsIgnoreCase("")) {
//				listView.setVisibility(View.GONE);
//				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//						context,
//						android.R.layout.simple_spinner_dropdown_item,
//						new String[] {});
//				product_type.setAdapter(adapter);
//				products.setAdapter(adapter);
//
//				Toast.makeText(TesterSubmitActivity.this,
//						"Select Category", Toast.LENGTH_SHORT)
//						.show();
//			}  else 
//			if (selected_product_type.equalsIgnoreCase("Select")
//					|| selected_product_category.equalsIgnoreCase("")) {
//				listView.setVisibility(View.GONE);
//				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//						context,
//						android.R.layout.simple_spinner_dropdown_item,
//						new String[] {});
//				// product_type.setAdapter(adapter);
//				products.setAdapter(adapter);
//
//				Toast.makeText(TesterSubmitActivity.this, "Select Product type",
//						Toast.LENGTH_SHORT).show();
//			} else {
//		
//
//			if(rb_new.isChecked()){
//				
//				getallproductslist(selected_category, selected_type,"N");
//			}
//			
//			if(rb_old.isChecked()){
//				
//				getallproductslist(selected_category, selected_type,"O");	
//				
//				
//			}
//			
//			}
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
//	});
	
	
	btn_save.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		try{

	String chkid[] =productadapter.getcheckinsert();
				
	String checkedid="";	
				
		String chck_db_id[] = productadapter.getcheckID();
		
		String chck_ean[]= productadapter.getData("EAN");
		String chck_size[]=productadapter.getData("Size");
		String chck_cat_id[]=productadapter.getData("CAT");
		String chck_shade[]=productadapter.getData("Shade");
				
				
	int chk=0;
	
	for(int i=0;i<chkid.length;i++){
		
		if(chkid[i].equalsIgnoreCase("0")){
			
			chk++;
			//checkedid="1";
			
		}
	}
	Log.v("", "chkid.length=="+chkid.length);
	Log.v("", "chk=="+chk);
			if(chkid.length==chk){
				
				Toast.makeText(getApplicationContext(), "Please select at least one product", Toast.LENGTH_LONG).show();
				
			}
			
			/*else if(!ch_status.isChecked()){
				Toast.makeText(getApplicationContext(), "Please select Indent button", Toast.LENGTH_LONG).show();
				
			}*/
			
			else{
				
				
			for(int i=0;i<chkid.length;i++){
				Log.e("", "chkid==" + chkid[i]);
				if(!chkid[i].toString().equalsIgnoreCase("0")){
			String db_id1 = "";String cat_id = "";
				String emp_id = shp.getString("username", "");
				Log.e("", "emp_id==" + emp_id);

				String product_id = selected_product_id1;// selected_product_id1;
				String product_category = selected_product_category;
				String product_type1 = selected_product_type;
				String product_name = chkid[i];// selected_product_name;
				
			
				String size = "";
				size = chck_size[i];
				String eancode = "";
				eancode = chck_ean[i];
				Log.v("", "enacode==" + eancode);
				
				db_id1 = chck_db_id[i];
				
				cat_id = chck_cat_id[i];
				String shadeno = chck_shade[i];
				
				
				/*if(db_id1.equalsIgnoreCase("")){
					Log.v("", "db_id1==" + db_id1);
					Toast.makeText(getApplicationContext(), "select product from list first", Toast.LENGTH_SHORT).show();
				}else if(product_status.equalsIgnoreCase("")){
					Log.v("", "product_status==" + product_status);
					Toast.makeText(getApplicationContext(), "select radio button Indent", Toast.LENGTH_SHORT).show();
				}else{*/
				
				req_date = ff.format(c.getTime());

					db.open();
					db.InsertTester(
							// product_id,
							product_category, product_type1, product_name,
							emp_id, 
						//	op_stk, cl_stk, rt_stk, fresh_stock1,sold_stk, 
						//	size, eancode, db_id1, cat_id,product_status,req_date,delv_date); // ---------
					size, eancode, db_id1, product_id,"INDENT",req_date,delv_date,shadeno,cat_id); // ---------
					db.close();
					

					
				//	startActivity(new Intent(context,
					//		DashboardNewActivity.class));
				}
			}
			
			Toast t = Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
			startActivity(new Intent(context,
					DashboardNewActivity.class));
			}

		//	}
	//	}

		}catch(Exception e){
		
			e.printStackTrace();
		}
		}
	}
	);
	listView.setOnTouchListener(new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			// v.getParent().
			v.getParent().requestDisallowInterceptTouchEvent(true);

			return false;
		}
	});
	
	/*hscv_tester = (HorizontalScrollView)findViewById(R.id.lv_tester_hs);
	hscv_tester.setOnTouchListener(new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub

			int scrollX = v.getScrollX();
			int scrollY = v.getScrollY();

			hscv_tester.scrollTo(scrollX, scrollY);
			
			return false;

		}
	});*/

	
	}
	
	public void getallproductslist(String selected_category, String selected_type,String price_type
			) {
		// TODO Auto-generated method stub
		

		try {
			Cursor cursor = null;
			
			
			db.open();

			productDetailsArray.clear();

			cursor = db.fetchAlltesterlist(selected_category,
					selected_type,price_type);
			String products[] = new String[cursor.getCount()];
			String size []=new String[cursor.getCount()];
			String id []=new String[cursor.getCount()];
			String shade[] = new String[cursor.getCount()];
			String db_id[]= new String[cursor.getCount()];
			String eancode[]= new String[cursor.getCount()];
//			String size[]= new String[cursor.getCount()];
//			String shadeno[]= new String[cursor.getCount()];
			String cat_id[]= new String[cursor.getCount()];
			
			
			if(cursor!=null ){
				
				
					
					if(cursor.moveToFirst()){
					
						Log.v("","cursor.getCount()="+cursor.getCount());
				
					for(int i=0;i<cursor.getCount();i++){
					products[i] = cursor.getString(cursor.getColumnIndex(Dbhelper.KEY_PRODUCTS));
					size [i] = cursor.getString(cursor.getColumnIndex(Dbhelper.KEY_SIZE));
					id [i] = cursor.getString(cursor.getColumnIndex(Dbhelper.KEY_ID));
					shade[i] =cursor.getString(cursor.getColumnIndex(Dbhelper.KEY_SHADENO));
					db_id[i] = cursor.getString(cursor.getColumnIndex(Dbhelper.KEY_DB_ID));
					eancode[i]= cursor.getString(cursor.getColumnIndex(Dbhelper.KEY_EANCODE));
					cat_id[i] = cursor.getString(cursor.getColumnIndex(Dbhelper.KEY_CATEGROYID));
					
					
					Log.v("", "products[i]="+products[i]);
					cursor.moveToNext();
				
					//productDetailsArray.add(map);

					}
					}
				

				
			//	productadapter = new com.sudesi.adapter.TesterListAdapter(
			//			TesterFragment.this, products,size,id);
				
				productadapter = new com.prod.sudesi.lotusherbalsnew.adapter.LazyAdapter2(
						TesterSubmitActivity.this, products,size,id,id,"1",db_id,eancode,cat_id,shade);
				
				
				
				Table_list_tages.setVisibility(View.VISIBLE);
				listView.setVisibility(View.VISIBLE);
			
				//ch_status.setVisibility(View.VISIBLE);
			//	ch_status.setChecked(false);
				btn_save.setVisibility(View.VISIBLE);
			
				listView.setAdapter(productadapter);
				
					
			}
				
			

			cursor.close();
			db.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}
