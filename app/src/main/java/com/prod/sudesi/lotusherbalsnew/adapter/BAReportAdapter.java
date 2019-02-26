package com.prod.sudesi.lotusherbalsnew.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.BocCumulativeDashboardActivity;
import com.prod.sudesi.lotusherbalsnew.R;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;


public class BAReportAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	String receiver;
	private ArrayList<String> categoryDetailsArraylist;
	private Dbcon db;
	SharedPreferences shp;
	SharedPreferences.Editor shpeditor;

	ViewHolder viewHolder;

	public BAReportAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		db = new Dbcon(activity);
		shp = activity.getSharedPreferences("Lotus", activity.MODE_PRIVATE);
		shpeditor = shp.edit();

		String username = shp.getString("username", "");

		categoryDetailsArraylist = new ArrayList<String>();

		db.open();
		categoryDetailsArraylist = db.getproductcategorywithoutselect(username);
		db.close();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size()-1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	static class ViewHolder {

		TextView tv_bar_month;
		TextView tv_bar_AchivlhP;
		TextView tv_bar_AchivlmP;
		TextView tv_bar_targetLH;
		TextView tv_bar_targetLM;
		TextView tv_bar_AchivlhC;
		TextView tv_bar_AchivlmC;
		TextView tv_bar_GrowlhC;
		TextView tv_bar_GrowlmC;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		// display the messages in view
		HashMap<String, String> map = new HashMap<String, String>();
		map = data.get(position);

		String MONTH = map.get("MONTH");
		String samtp = map.get("NetAmountPFirst");
		String camtp = map.get("NetAmountPSecond");
		String TargetSkin = map.get("TargetAmountCFirst");
		String TargetColor = map.get("TargetAmountCSecond");
		String samtc = map.get("NetAmountCFirst");
		String camtc = map.get("NetAmountCSecond");
		String GrowthSkin = map.get("GrowthFirst");
		String GrowthColor = map.get("GrowthSecond");



		if (MONTH.equalsIgnoreCase("anyType{}") || MONTH==null ||MONTH.equalsIgnoreCase("null") ) {

			MONTH = "0";
		}

		if (samtp.equalsIgnoreCase("anyType{}")|| samtp==null|| samtp.equalsIgnoreCase("null")) {

			samtp = "0";
		}
		
		if (camtp.equalsIgnoreCase("anyType{}")|| camtp==null|| camtp.equalsIgnoreCase("null")) {

			camtp = "0";
		}

		if (TargetSkin.equalsIgnoreCase("anyType{}") ||TargetSkin ==null||TargetSkin.equalsIgnoreCase("null")) {

			TargetSkin = "0";
		}
		if (TargetColor.equalsIgnoreCase("anyType{}") ||TargetColor ==null||TargetColor.equalsIgnoreCase("null")) {

			TargetColor = "0";
		}

		if (samtc.equalsIgnoreCase("anyType{}") || samtc==null||samtc.equalsIgnoreCase("null")) {

			samtc = "0";
		}
		
		if (camtc.equalsIgnoreCase("anyType{}") || camtc==null||camtc.equalsIgnoreCase("null")) {

			camtc = "0";
		}
		if (GrowthSkin.equalsIgnoreCase("anyType{}")|| GrowthSkin==null||GrowthSkin.equalsIgnoreCase("null")) {

			GrowthSkin = "0";
		}
		if (GrowthColor.equalsIgnoreCase("anyType{}")|| GrowthColor==null||GrowthColor.equalsIgnoreCase("null")) {

			GrowthColor = "0";
		}


		if (categoryDetailsArraylist.size() > 0) {
			if (categoryDetailsArraylist.size() == 2) {
				if (convertView == null) {
					viewHolder = new ViewHolder();// create new holder
					convertView = inflater.inflate(R.layout.layout_ba_year_wise_report_new,
							null);// inflater for view
					viewHolder.tv_bar_month = (TextView) convertView.findViewById(R.id.tv_bar_month);
					viewHolder.tv_bar_AchivlhP = (TextView) convertView.findViewById(R.id.tv_bar_AchivlhP);
					viewHolder.tv_bar_AchivlmP = (TextView) convertView.findViewById(R.id.tv_bar_AchivlmP);
					viewHolder.tv_bar_targetLH = (TextView) convertView.findViewById(R.id.tv_bar_targetLH);
					viewHolder.tv_bar_targetLM = (TextView) convertView.findViewById(R.id.tv_bar_targetLM);
					viewHolder.tv_bar_AchivlhC = (TextView) convertView.findViewById(R.id.tv_bar_AchivlhC);
					viewHolder.tv_bar_AchivlmC = (TextView) convertView.findViewById(R.id.tv_bar_AchivlmC);
					viewHolder.tv_bar_GrowlhC = (TextView) convertView.findViewById(R.id.tv_bar_GrowlhC);
					viewHolder.tv_bar_GrowlmC = (TextView) convertView.findViewById(R.id.tv_bar_GrowlmC);

					convertView.setTag(viewHolder);

				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}


				viewHolder.tv_bar_month.setText(MONTH);
				viewHolder.tv_bar_AchivlhP.setText(samtp);
				viewHolder.tv_bar_AchivlmP.setText(camtp);
				viewHolder.tv_bar_targetLH.setText(TargetSkin);
				viewHolder.tv_bar_targetLM.setText(TargetColor);
				viewHolder.tv_bar_AchivlhC.setText(samtc);
				viewHolder.tv_bar_AchivlmC.setText(camtc);
				viewHolder.tv_bar_GrowlhC.setText(GrowthSkin);
				viewHolder.tv_bar_GrowlmC.setText(GrowthColor);
			}else{

				if (convertView == null) {
					viewHolder = new ViewHolder();// create new holder
					convertView = inflater.inflate(R.layout.layout_ba_year_wise_report_new1,
							null);// inflater for view
					viewHolder.tv_bar_month = (TextView) convertView.findViewById(R.id.tv_bar_month);
					viewHolder.tv_bar_AchivlhP = (TextView) convertView.findViewById(R.id.tv_bar_AchivlhP);
					viewHolder.tv_bar_targetLH = (TextView) convertView.findViewById(R.id.tv_bar_targetLH);
					viewHolder.tv_bar_AchivlhC = (TextView) convertView.findViewById(R.id.tv_bar_AchivlhC);
					viewHolder.tv_bar_GrowlhC = (TextView) convertView.findViewById(R.id.tv_bar_GrowlhC);

					convertView.setTag(viewHolder);

				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}


				viewHolder.tv_bar_month.setText(MONTH);
				viewHolder.tv_bar_AchivlhP.setText(samtp);
				viewHolder.tv_bar_targetLH.setText(TargetSkin);
				viewHolder.tv_bar_AchivlhC.setText(samtc);
				viewHolder.tv_bar_GrowlhC.setText(GrowthSkin);

			}
		}


		return convertView;

	}

}
