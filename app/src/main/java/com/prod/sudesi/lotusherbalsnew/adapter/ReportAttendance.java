package com.prod.sudesi.lotusherbalsnew.adapter;

import java.util.ArrayList;
import java.util.HashMap;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.R;

public class ReportAttendance extends BaseAdapter {
	
	
	Context context1;
	private ArrayList<HashMap<String, String>> data;
	private  LayoutInflater inflater1=null;
	String receiver;

	ViewHolder viewHolder;
	
	public ReportAttendance(Context context, ArrayList<HashMap<String, String>> d) {
		
		context1 = context;
		data=d;
		inflater1 = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
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
	
	
	class ViewHolder{
		TextView txt_adate;
		TextView txt_attendance;
		TextView txt_atype;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if(convertView==null)
		{
			viewHolder=new ViewHolder();
			
			convertView = inflater1.inflate(R.layout.report_attendance_row, null);
			
		 viewHolder.txt_adate = (TextView) convertView.findViewById(R.id.txt_adate); 
		 viewHolder.txt_attendance= (TextView) convertView.findViewById(R.id.txt_attendance); 
		 viewHolder.txt_atype= (TextView) convertView.findViewById(R.id.txt_atype); 
		 
		 convertView.setTag(viewHolder);
		 
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		HashMap<String, String> map = new HashMap<String, String>();
		map = data.get(position);        

		String[] date = map.get("ADATE").split(" ");
		String adate = date[0];
		
		viewHolder.txt_adate.setText(adate);
		viewHolder.txt_attendance.setText(map.get("ATTENDANCE"));
		viewHolder.txt_atype.setText(map.get("ABSENTTYPE"));
		
		
		return convertView;
	}

}
