package com.prod.sudesi.lotusherbalsnew;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;
import com.prod.sudesi.lotusherbalsnew.image.ImageModel;
import com.prod.sudesi.lotusherbalsnew.image.ListAdapter;
import com.prod.sudesi.lotusherbalsnew.libs.ConnectionDetector;
import com.prod.sudesi.lotusherbalsnew.libs.LotusWebservice;


public class VisibilityImageFragment extends ListActivity implements
		OnClickListener {

	Context context;
	private Uri outputFileUri;

	private String pathCamera = "";

	// shredpreference
	private SharedPreferences sharedpre = null;

	private SharedPreferences.Editor saveuser = null;

	SharedPreferences sp;
	SharedPreferences.Editor spe;
	//

	ConnectionDetector cd;
	private File root = null;

	private String fname = "";

	private String scannedId;

	private Dbcon db;

	private TextView welcome;

	private ListView listView;

	private ArrayAdapter<ImageModel> adapter;

	private Button btn_back, btn_next, preview_button;

	Button back, home;

	String producttype;
	String username, imgpth;

	Cursor image_array, image_array1;
	LotusWebservice service;

	private EditText image_description;

	private ProgressDialog mProgress = null;

	int soapresultforvisibilityid;

	private double lon = 0.0, lat = 0.0;

	Fragment content;

	RadioGroup rg_image_category;
	RadioButton r_lh, r_lhm;
	
	List<ImageModel> list;
	
	TextView tv_h_username;
	Button btn_home,btn_logout;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_visibility);
		
		Log.v("","in visisisi");
		context = getApplicationContext();
		Log.v("","in visisisi");
		try {

			Log.v("","in visisisi");

			btn_back = (Button) findViewById(R.id.button_back);
			btn_next = (Button) findViewById(R.id.buttonNext);
			preview_button = (Button) findViewById(R.id.preview_button);
			image_description = (EditText) findViewById(R.id.et_image_description);
			Log.v("","in visisisi");
			cd = new ConnectionDetector(context);

			listView = (ListView) findViewById(android.R.id.list);

			btn_back.setOnClickListener(this);
			btn_next.setOnClickListener(this);
			preview_button.setOnClickListener(this);

			content = new Fragment();
			sharedpre = context.getSharedPreferences("Sudesi",context.MODE_PRIVATE);
			saveuser = sharedpre.edit();

			sp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
			spe = sp.edit();

			mProgress = new ProgressDialog(this);
			service = new LotusWebservice(this);

			 producttype = sharedpre.getString("producttype","");
			 Log.e("", "producttype=="+producttype);

			username = sp.getString("username", "");
			Log.e("", "username==" + username);

			//------------------

			tv_h_username = (TextView)findViewById(R.id.tv_h_username);
			btn_home = (Button)findViewById(R.id.btn_home);
			btn_logout =(Button)findViewById(R.id.btn_logout);
			
			tv_h_username.setText(username);
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
				//startActivity(new Intent(getApplicationContext(), DashboardNewActivity.class));	
				}
			});
			//---------------------

			db = new Dbcon(context);

		//	DisplayMetrics displayMetrics = new DisplayMetrics();
			//WindowManager wm = (WindowManager) getActivity().getSystemService(
			//		Context.WINDOW_SERVICE); // the results will
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

		} catch (Exception e) {

			e.printStackTrace();
		}

		

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_back:
			
			Intent i = new  Intent(getApplicationContext(), VisibilityFragment.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);	
			
			//startActivity(new Intent(getApplicationContext(),VisibilityFragment.class));

			break;

		case R.id.buttonNext:

			// imgpth = sharedpre.getString("ImgPath_","");
			// Log.e("", "ImgPath_=="+imgpth);
			try {
				// = sharedpre.getInt("iCount", 0);
				
				int count1 =  listView.getAdapter().getCount() ;

				Log.e("", "count1==" + count1);
				if (count1 == 0) {

					Toast.makeText(VisibilityImageFragment.this, "Please Capture images",
							Toast.LENGTH_SHORT).show();

				} else {

					String desc = image_description.getText().toString().trim();
					db.open();

					db.Scanimage(String.valueOf(lon), String.valueOf(lat),
							desc, username, producttype, String.valueOf(count1), getModel());

					db.close();
					Toast.makeText(VisibilityImageFragment.this, "Successfully images saved",
							Toast.LENGTH_SHORT).show();
					
					Intent ii = new  Intent(getApplicationContext(), DashboardNewActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(ii);	
					
					//startActivity(new Intent(context,
							//DashboardNewActivity.class));

				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			

			break;

		case R.id.preview_button:

			
			openImageIntent();
			break;
		}
	}

	

	@SuppressLint("NewApi")
	private void openImageIntent() {

		try {

			fname = username+"_"+System.currentTimeMillis() + ".jpeg";

			FileOutputStream fos = context.openFileOutput(fname,
					Context.MODE_WORLD_WRITEABLE);
			fos.close();

			root = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "sudesi" + File.separator);
			
			root.mkdirs();

			root.setWritable(true);

			File f = new File(root, fname);

			outputFileUri = Uri.fromFile(f);

			pathCamera = Environment.getExternalStorageDirectory() + "/sudesi/"
					+ fname;

			final Intent captureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);

			captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

			startActivityForResult(captureIntent, 202);
		} catch (Exception e) {

			Toast.makeText(context,
					"Something Wrong with The Camera. Try Again!!!",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		try {
			if (resultCode == RESULT_OK) {

				if (requestCode == 202) {

					String selectedImageUri = pathCamera;

					saveuser = sharedpre.edit();

					int count = sharedpre.getInt("iCount", 0);

					count++;

					saveuser.putString("ImgPath_" + String.valueOf(count),
							selectedImageUri.toString());

					saveuser.putInt("iCount", count);

					saveuser.commit();

					adapter = null;

					adapter = new ListAdapter(VisibilityImageFragment.this, getModel());
					setListAdapter(adapter);

				}
			}
		} catch (Exception e) {

			e.printStackTrace();

			Toast.makeText(VisibilityImageFragment.this,
					"Something Wrong with The Camera. Try Again!!! ",
					Toast.LENGTH_LONG).show();

		}
	}

	private List<ImageModel> getModel() {

				list = new ArrayList<ImageModel>();

		try {

			int count = sharedpre.getInt("iCount", 0);

			File fTemp = null;

			String pathLocal = "";

			int localCount = 0;

			for (int j = count; j > 0; j--) {
				pathLocal = sharedpre.getString("ImgPath_" + String.valueOf(j),"");

				if (!pathLocal.trim().equalsIgnoreCase("")) {
					fTemp = new File(pathLocal);

					if (fTemp.exists()) {
						list.add(get(pathLocal, j));

						localCount++;
					}
				}

			}

			if (localCount == 0) {
				// list.add(get("No Images",0));
			}

		} catch (Exception e) {
			list = null;

		}

		return list;
	}

	private ImageModel get(String s, int i) {
		return new ImageModel(s, i);
	}

	

	

	@Override
	public void onResume() {
		super.onResume();

		// cancel any notification we may have received from
		// TestBroadcastReceiver
		/*((NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1234);

		refreshDisplay();

		// This demonstrates how to dynamically create a receiver to listen to
		// the location updates.
		// You could also register a receiver in your manifest.
		final IntentFilter lftIntentFilter = new IntentFilter(
				LocationLibraryConstants
						.getLocationChangedPeriodicBroadcastAction());
		context.registerReceiver(lftBroadcastReceiver, lftIntentFilter);*/
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
	};

	private void refreshDisplay() {
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

	/*
	 * public void loadlist(Context context2){ Log.e("","in load list");
	 * 
	 * try{ Fragment frag = new VisibilityImageFragment();
	 * 
	 * FragmentTransaction ft = getFragmentManager().beginTransaction();
	 * ft.replace(R.id.activity_main_content_fragment, frag);
	 * ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	 * ft.addToBackStack(null); ft.commit(); }catch (Exception e) { // TODO:
	 * handle exception e.printStackTrace();
	 * 
	 * Toast.makeText(getActivity(), "kkkkk", Toast.LENGTH_SHORT).show(); }
	 * 
	 * }
	 */

}
