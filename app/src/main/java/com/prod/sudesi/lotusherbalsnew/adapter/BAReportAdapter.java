package com.prod.sudesi.lotusherbalsnew.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.R;


public class BAReportAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	String receiver;

	ViewHolder viewHolder;

	public BAReportAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

		TextView tv_MonthsP;
		TextView tv_skinbalP;
		TextView tv_colorbalP;
		TextView tv_growthP_skin;
		TextView tv_growthP_color;

		TextView tv_MonthsC;
		TextView tv_skinbalC;
		TextView tv_colorbalC;
		TextView tv_growthC_skin;
		TextView tv_growthC_color;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			viewHolder = new ViewHolder();// create new holder
			convertView = inflater.inflate(R.layout.layout_ba_year_wise_report,
					null);// inflater for view
			viewHolder.tv_MonthsP = (TextView) convertView
					.findViewById(R.id.tv_bar_monthP);
			viewHolder.tv_skinbalP = (TextView) convertView
					.findViewById(R.id.tv_bar_SkinbalP);
			viewHolder.tv_colorbalP = (TextView) convertView
					.findViewById(R.id.tv_bar_ColorbalP);
			viewHolder.tv_growthP_skin = (TextView) convertView
					.findViewById(R.id.tv_bar_growthP_skin);
			viewHolder.tv_growthP_color = (TextView) convertView
					.findViewById(R.id.tv_bar_growthP_color);

			viewHolder.tv_MonthsC = (TextView) convertView
					.findViewById(R.id.tv_bar_monthC);
			viewHolder.tv_skinbalC = (TextView) convertView
					.findViewById(R.id.tv_bar_SkinbalC);
			
			viewHolder.tv_colorbalC = (TextView) convertView
					.findViewById(R.id.tv_bar_ColorbalC);
			
			viewHolder.tv_growthC_skin = (TextView) convertView
					.findViewById(R.id.tv_bar_growthC_skin);
			viewHolder.tv_growthC_color = (TextView) convertView
					.findViewById(R.id.tv_bar_growthC_color);

			convertView.setTag(viewHolder);

			/*
			 * viewHolder.browse.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { // TODO Auto-generated
			 * method stub
			 * 
			 * } });
			 */

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// display the messages in view
		HashMap<String, String> map = new HashMap<String, String>();
		map = data.get(position);

		String py = map.get("PreviousYearP");
		String samtp = map.get("SkinAmountP");
		String camtp = map.get("ColorAmountP");
		String gp_skin = map.get("GrowthPSkin");
		String gp_color = map.get("GrowthPColor");
		
		String cy = map.get("CurrentYearC");
		String samtc = map.get("SkinAmountC");
		String camtc = map.get("ColorAmountC");
		String gc_skin = map.get("GrowthCSkin");
		String gc_color = map.get("GrowthCColor");
		
		

		if (py.equalsIgnoreCase("anyType{}") || py==null ||py.equalsIgnoreCase("null") ) {

			py = "0";
		}

		if (samtp.equalsIgnoreCase("anyType{}")|| samtp==null|| samtp.equalsIgnoreCase("null")) {

			samtp = "0";
		}
		
		if (camtp.equalsIgnoreCase("anyType{}")|| camtp==null|| camtp.equalsIgnoreCase("null")) {

			camtp = "0";
		}

		if (gp_skin.equalsIgnoreCase("anyType{}") ||gp_skin ==null||gp_skin.equalsIgnoreCase("null")) {

			gp_skin = "0";
		}
		if (gp_color.equalsIgnoreCase("anyType{}") ||gp_color ==null||gp_color.equalsIgnoreCase("null")) {

			gp_color = "0";
		}
		
		
		if (cy.equalsIgnoreCase("anyType{}")|| cy==null||cy.equalsIgnoreCase("null")) {

			cy = "0";
		}
		if (samtc.equalsIgnoreCase("anyType{}") || samtc==null||samtc.equalsIgnoreCase("null")) {

			samtc = "0";
		}
		
		if (camtc.equalsIgnoreCase("anyType{}") || camtc==null||camtc.equalsIgnoreCase("null")) {

			camtc = "0";
		}
		if (gc_skin.equalsIgnoreCase("anyType{}")|| gc_skin==null||gc_skin.equalsIgnoreCase("null")) {

			gc_skin = "0";
		}
		if (gc_color.equalsIgnoreCase("anyType{}")|| gc_color==null||gc_color.equalsIgnoreCase("null")) {

			gc_color = "0";
		}
		
		

		/*viewHolder.tv_MonthsP.setText(map.get("PreviousYearP"));
		viewHolder.tv_netbalP.setText(map.get("NetAmountP"));
		viewHolder.tv_growthP.setText(map.get("GrowthP"));
		viewHolder.tv_MonthsC.setText(map.get("CurrentYearC"));
		viewHolder.tv_netbalC.setText(map.get("NetAmountC"));
		viewHolder.tv_growthC.setText(map.get("GrowthC"));*/
		
		viewHolder.tv_MonthsP.setText(py);
		viewHolder.tv_skinbalP.setText(samtp);
		viewHolder.tv_colorbalP.setText(camtp);
		
		viewHolder.tv_growthP_skin.setText(gp_skin);
		viewHolder.tv_growthP_color.setText(gp_color);
		
		viewHolder.tv_MonthsC.setText(cy);
		viewHolder.tv_skinbalC.setText(samtc);
		viewHolder.tv_colorbalC.setText(camtc);
		viewHolder.tv_growthC_skin.setText(gc_skin);
		viewHolder.tv_growthC_color.setText(gc_color);

		// RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
		// 70, 70);
		// params.setMargins(5,15,0, 0);

		return convertView;

	}

}
