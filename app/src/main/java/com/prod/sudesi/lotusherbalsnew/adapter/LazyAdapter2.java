package com.prod.sudesi.lotusherbalsnew.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.R;
import com.prod.sudesi.lotusherbalsnew.imageloder.ImageLoader1;

@SuppressLint("UseValueOf")
public class LazyAdapter2 extends BaseAdapter {

	String TAG1 = "Class:LazyAdapter2:";
	private Activity activity;
	private String[] data;
	private String[] stringdata;
	private String[] spId;
	private String[] mgroupId;
	private String[] mdb_id;
	private String[] meancode;
	private String[] mcat_id;
	private String[] mshade;

	private static LayoutInflater inflater = null;
	public ImageLoader1 imageLoader;
	boolean[] checkBoxState;
	CheckBox checkbox;
	TextView tv;
	// protected ArrayList<List> mStrings;
	String val;
	String flag1;
	int flag;
	Animation animation = null;//
	Context context;//

	public LazyAdapter2(Activity a, String[] d, String[] q, String[] spinnerId,
			String[] menuGroupId, String getflag, String[] db_id,
			String[] eancode, String[] cat_id, String[] shade) {
		Log.v(TAG1, "ELSE -----=================4" + d.toString());
		Log.v(TAG1, "ELSE -----=================6" + a);
		activity = a;
		data = d;
		stringdata = q;
		spId = spinnerId;
		mgroupId = menuGroupId;
		mdb_id = db_id;
		meancode = eancode;
		mcat_id = cat_id;
		mshade = shade;
		flag1 = getflag;

		context = a;//
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader1(activity.getApplicationContext());
		checkBoxState = new boolean[data.length];
		Log.v(TAG1, "ELSE -----=================5" + data.length);
		flag = Integer.parseInt(flag1);
		Log.v(TAG1, "Flag======" + flag);

	}

	public int getCount() {
		return data.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null)
			vi = inflater.inflate(R.layout.item1, null);
		animation = AnimationUtils.loadAnimation(context, R.anim.hyperspace_in);//

		if (flag == 1) {

			TextView text = (TextView) vi.findViewById(R.id.text);
			
			text.setVisibility(View.VISIBLE);
			TextView sp_id = (TextView) vi.findViewById(R.id.tv_sp_id);
			sp_id.setVisibility(View.GONE);
			TextView m_group_id = (TextView) vi.findViewById(R.id.tv_m_gr_id);
			m_group_id.setVisibility(View.GONE);
			ImageView image = (ImageView) vi.findViewById(R.id.image);
			image.setVisibility(View.GONE);
			checkbox = (CheckBox) vi.findViewById(R.id.checkBox2);
			TextView shadeno = (TextView) vi
					.findViewById(R.id.tv_shadeno_tester);
			
			// checkbox.setText("chb "+position);
			text.setText("item " + position);
			// sp_id.setText(position);
			// m_group_id.setText(position);
			imageLoader.DisplayImage(
					// data[position], image,
					stringdata[position], text, data[position], checkbox,
					shadeno, mshade[position]);

			tv = (TextView) vi.findViewById(R.id.text);
			checkbox.setTag(new Integer(position));

			checkbox.setOnCheckedChangeListener(null);
			if (checkBoxState[position])
				checkbox.setChecked(true);
			else
				checkbox.setChecked(false);

			checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					Integer pos = (Integer) buttonView.getTag();
					if (isChecked) {

						checkBoxState[pos.intValue()] = true;

					} else {
						checkBoxState[pos.intValue()] = false;
						Log.e("checked", "unchecked");

					}
				}
			});

		} else if (flag == 2) {

			Log.e("TAG1", "Flag======1:" + flag);

			TextView text = (TextView) vi.findViewById(R.id.text);
			TextView text1 = (TextView) vi.findViewById(R.id.text1);
			TextView sp_id = (TextView) vi.findViewById(R.id.tv_sp_id);
			TextView m_group_id = (TextView) vi.findViewById(R.id.tv_m_gr_id);
			TextView shadeno = (TextView) vi
					.findViewById(R.id.tv_shadeno_tester);

			// sp_id.setVisibility(View.GONE);

			ImageView image = (ImageView) vi.findViewById(R.id.image);
			image.setVisibility(View.GONE);
			checkbox = (CheckBox) vi.findViewById(R.id.checkBox2);
			checkbox.setVisibility(View.VISIBLE);

			Log.e("TAG1", "Flag======2:" + data[position]);

			imageLoader.DisplayMenu1(data[position], text1,
					stringdata[position], text, spId[position], sp_id,
					mgroupId[position], m_group_id, shadeno, mshade[position]);

		}

		animation.setDuration(500);

		vi.startAnimation(animation);

		animation = null;
		return vi;
	}

	public String getch() {

		String dataval = null;
		String[] dataa = null;
		String[] chapterFileName = new String[data.length];

		String fieldSql = "INSERT INTO menu_type_mapping(menu_master_id,menu_type_id,business_master_id,created_by,created_date)";
		String dataSql = " VALUES ";

		for (int i = 0; i < data.length; i++) {

			// Log.e("Tag", "chbox====:" + checkBoxState[i]);
			// Log.e("Tag", "chbox====id:" + i);

			if (checkBoxState[i] == true) {

				// dataSql = dataSql +"(`" + "$get_last_insert_id" + "`,";
				dataSql = dataSql + "(" + "?" + ",";
				dataSql = dataSql + "" + data[i].toString() + ",";
				dataSql = dataSql + "" + "1" + ","; // HardCode
				dataSql = dataSql + "" + "1" + ",";
				dataSql = dataSql + "" + "now()" + "),";

				Log.e("Tag", "chbox====id==:" + data[i].toString());
				chapterFileName[i] = data[i];

			}

			// Log.e("Tag", "chbox==========" + checkBoxState[i]);

		}

		dataSql = dataSql.substring(0, dataSql.length() - 1);
		String finalquery = fieldSql + dataSql;

		Log.e("Tag", "chbox====QUERY:" + finalquery);

		dataval = String.valueOf(chapterFileName);
		Log.e("Tag", "chbox====final value" + dataval.toString());

		return finalquery;

	}

	@SuppressWarnings("null")
	public String[] getcheckinsert() {

		String pro_name[] = new String[data.length];

		for (int i = 0; i < data.length; i++) {

			// Log.v("ccheck", "status[i]="+status[i].toString());
			if (checkBoxState[i] == true) {
				// if(status[i].equalsIgnoreCase("1")){
				Log.v("ccheck", "log3_i");

				pro_name[i] = data[i];

			} else {

				pro_name[i] = "0";

			}

		}
		return pro_name;

	}

	public String[] getcheckID() {
		String check_id[] = new String[mdb_id.length];

		for (int i = 0; i < mdb_id.length; i++) {

			// Log.v("ccheck", "status[i]="+status[i].toString());
			if (checkBoxState[i] == true) {
				// if(status[i].equalsIgnoreCase("1")){
				Log.v("ccheck", "log3_i");

				check_id[i] = mdb_id[i];

			} else {

				check_id[i] = "0";

			}

		}
		return check_id;
	}

	public String[] getData(String fieldname) {
		String check_id[] = null;
		if (fieldname.equalsIgnoreCase("EAN")) {
			check_id = new String[meancode.length];

			for (int i = 0; i < meancode.length; i++) {

				// Log.v("ccheck", "status[i]="+status[i].toString());
				if (checkBoxState[i] == true) {
					// if(status[i].equalsIgnoreCase("1")){

					check_id[i] = meancode[i];

				} else {

					check_id[i] = "0";

				}

			}
		}

		if (fieldname.equalsIgnoreCase("CAT")) {

			check_id = new String[mcat_id.length];

			for (int i = 0; i < mcat_id.length; i++) {

				// Log.v("ccheck", "status[i]="+status[i].toString());
				if (checkBoxState[i] == true) {
					// if(status[i].equalsIgnoreCase("1")){

					check_id[i] = mcat_id[i];

				} else {

					check_id[i] = "0";

				}

			}

		}

		if (fieldname.equalsIgnoreCase("Size")) {
			check_id = new String[stringdata.length];

			for (int i = 0; i < stringdata.length; i++) {

				// Log.v("ccheck", "status[i]="+status[i].toString());
				if (checkBoxState[i] == true) {
					// if(status[i].equalsIgnoreCase("1")){

					check_id[i] = stringdata[i];

				} else {

					check_id[i] = "0";

				}

			}
		}

		if (fieldname.equalsIgnoreCase("Shade")) {

			check_id = new String[mshade.length];

			for (int i = 0; i < mshade.length; i++) {

				// Log.v("ccheck", "status[i]="+status[i].toString());
				if (checkBoxState[i] == true) {
					// if(status[i].equalsIgnoreCase("1")){

					check_id[i] = mshade[i];

				} else {

					check_id[i] = "0";

				}

			}
		}

		if (fieldname.equalsIgnoreCase("MRP")) {
			check_id = new String[mgroupId.length];

			for (int i = 0; i < mgroupId.length; i++) {

				// Log.v("ccheck", "status[i]="+status[i].toString());
				if (checkBoxState[i] == true) {
					// if(status[i].equalsIgnoreCase("1")){

					check_id[i] = mgroupId[i];

				} else {

					check_id[i] = "0";

				}

			}
		}

		return check_id;
	}

}