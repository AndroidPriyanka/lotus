package com.prod.sudesi.lotusherbalsnew;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.libs.ExceptionHandler;


public class VisibilityFragment extends Activity implements OnClickListener {

	Context context = null;
	Button btn_skincare;
	Button btn_bodycare;
	Button btn_haircare;
	Button btn_suncare;
	Button btn_makeup;

	EditText et_other;
	Button btn_other;

	Button btn_lh, btn_lhm;
	private SharedPreferences sharedpre = null;

	private SharedPreferences.Editor saveuser = null;

	SharedPreferences sp;
	SharedPreferences.Editor spe;

	TextView tv_h_username;
	Button btn_home, btn_logout;
	String username,bdename;

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.fragment_visibility);

		//////////Crash Report
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		context = getApplicationContext();

		// btn_skincare=(Button)view.findViewById(R.id.btn_skincare);
		// btn_bodycare=(Button)view.findViewById(R.id.btn_bodycare);
		// btn_haircare=(Button)view.findViewById(R.id.btn_haircare);
		// btn_suncare=(Button)view.findViewById(R.id.btn_suncare);
		// btn_makeup=(Button)view.findViewById(R.id.btn_makeup);

		btn_lh = (Button) findViewById(R.id.btn_lh);
		btn_lhm = (Button) findViewById(R.id.btn_lhm);

		// et_other=(EditText)findViewById(R.id.et_other_category);
		// btn_other=(Button)findViewById(R.id.btn_other_category);

		// btn_skincare.setOnClickListener(this);
		// btn_bodycare.setOnClickListener(this);
		// btn_haircare.setOnClickListener(this);
		// btn_suncare.setOnClickListener(this);
		// btn_makeup.setOnClickListener(this);
		// btn_other.setOnClickListener(this);

		btn_lh.setOnClickListener(this);
		btn_lhm.setOnClickListener(this);

		sharedpre = context
				.getSharedPreferences("Sudesi", context.MODE_PRIVATE);
		saveuser = sharedpre.edit();
		saveuser.clear().commit();

		sp = context.getSharedPreferences("Lotus", context.MODE_PRIVATE);
		spe = sp.edit();

		// ------------------

		tv_h_username = (TextView) findViewById(R.id.tv_h_username);
		btn_home = (Button) findViewById(R.id.btn_home);
		btn_logout = (Button) findViewById(R.id.btn_logout);
		username = sp.getString("username", "");

		Log.v("", "username at visi==" + username);
		bdename = sp.getString("BDEusername","");
		tv_h_username.setText(bdename);
		btn_logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						LoginActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});

		btn_home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						DashboardNewActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				// startActivity(new Intent(getApplicationContext(),
				// DashboardNewActivity.class));
			}
		});
		// ---------------------

		String div = sp.getString("div", "");
		if (div.equalsIgnoreCase("LH & LHM") || div.equalsIgnoreCase("LH & LM")) {

		} else if (div.equalsIgnoreCase("LM")) {

			btn_lh.setVisibility(View.GONE);

		} else if (div.equalsIgnoreCase("LH")) {

			btn_lhm.setVisibility(View.GONE);

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.btn_lhm:

			saveuser = sharedpre.edit();

			saveuser.putString("producttype", "LM");

			saveuser.commit();
			Log.e("", "LM");

			startActivity(new Intent(getApplicationContext(),
					VisibilityImageFragment.class));
			break;

		case R.id.btn_lh:

			saveuser = sharedpre.edit();

			saveuser.putString("producttype", "LH");

			saveuser.commit();
			Log.e("", "LH");
			startActivity(new Intent(getApplicationContext(),
					VisibilityImageFragment.class));
			break;

		}
	}

}
