package com.prod.sudesi.lotusherbalsnew.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.R;


public class NotificationAdapter extends BaseAdapter {

	
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater=null;
	String receiver;
	int[] color_arr={Color.BLUE,Color.CYAN,Color.DKGRAY,Color.GREEN,Color.RED};
	ViewHolder viewHolder;
	 Animation animation = null;
	 
	 
	
	public NotificationAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data=d;
		
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		 
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
	
	static class ViewHolder {
		TextView message;
		TextView date;
		TextView receiver;
		Button btnreceiver;
		
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView==null)
		{
			viewHolder=new ViewHolder();//create new holder
			
			
			
			convertView = inflater.inflate(R.layout.message_item_list_notify, null);//inflater for view
			 
			 animation = AnimationUtils.loadAnimation(activity, R.anim.hyperspace_in);
		
			  String fontPath = "museo-500.otf";
			  Typeface tf = Typeface.createFromAsset(activity.getAssets(), fontPath);
			  
			viewHolder.message = (TextView)convertView.findViewById(R.id.message); 
			viewHolder.date=(TextView)convertView.findViewById(R.id.date);
			viewHolder.receiver=(TextView)convertView.findViewById(R.id.btnReceiver);
			//viewHolder.btnreceiver=(Button)convertView.findViewById(R.id.button1);
			
			viewHolder.message.setTypeface(tf);
			 
			convertView.setTag(viewHolder);
			
			
			
		}else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// animation = AnimationUtils.loadAnimation(activity, R.anim.hyperspace_in);
		 animation = AnimationUtils.loadAnimation(activity, R.anim.slide_down);
		//display the messages in view
		HashMap<String, String> map = new HashMap<String, String>();
		map = data.get(position);   
		String m,d,r;
		
		if(map.get("Message").toString().equalsIgnoreCase("null")){
			
			m="";
		}else{
			
			m=map.get("Message");
		}
		if(map.get("Date").toString().equalsIgnoreCase("null")){
			d="";
		}else{
			
			d=map.get("Date");
		}
		if(map.get("Receiver").toString().equalsIgnoreCase("null")){
			r="";
		}else{
			
			r=map.get("Receiver");
		}

		
		viewHolder.message.setText(m);
		viewHolder.date.setText(d);
		viewHolder.receiver.setText(r);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				70, 70);
		params.setMargins(5,15,0, 0);
		
		
		
		Log.v("",""+(position % 2 == 0));
		
		if(position % 2 == 0){
			
			convertView.setBackgroundColor(Color.parseColor("#1569C7"));
			
		}else{
			
			convertView.setBackgroundColor(Color.parseColor("#357EC7"));
		}
		
		
		  animation.setDuration(500);
			 
		    convertView.startAnimation(animation);
		 
		    animation = null;
		return convertView;
		
	}

}
