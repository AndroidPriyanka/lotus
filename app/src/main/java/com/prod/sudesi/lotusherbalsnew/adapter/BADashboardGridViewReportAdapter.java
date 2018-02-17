package com.prod.sudesi.lotusherbalsnew.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.R;


public class BADashboardGridViewReportAdapter extends BaseAdapter{

	
	private Context context;
    //private final String[] gridValues;
    
    ViewHolder viewHolder;
    
    ArrayList<HashMap<String, String>> data;
    
    Animation animation = null;
 
    //Constructor to initialize values
    public BADashboardGridViewReportAdapter(Context context, // String[ ] gridValues, 
    		ArrayList<HashMap<String, String>> data1) {

        this.context        = context;
      //  this.gridValues     = gridValues;
        this.data = data1;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		 return data.size();
		
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	static class ViewHolder {
		TextView textView;
		TextView tvcolor;
		TextView tv_total;
		TextView tvdate;
		TableRow tr_Skin;
		TableRow tr_Color;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		 LayoutInflater inflater = (LayoutInflater) context
                 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  
//         View gridView = null;
       
         viewHolder=new ViewHolder();
         
         animation = AnimationUtils.loadAnimation(context, R.anim.hyperspace_in);

  
         if (convertView == null) {
  
//             gridView = new View(context);
  
             
             // get layout from grid_item.xml ( Defined Below )
             convertView = inflater.inflate( R.layout.inflate_dashboar_list , null);
            
             // set value into textview
              
          //   viewHolder.textView = (TextView) gridView
            //         .findViewById(R.id.tv);
             
//             viewHolder.tvcolor = (TextView) convertView
//                     .findViewById(R.id.tv_type);
             
             viewHolder.tv_total = (TextView) convertView
                     .findViewById(R.id.tv_total);

             viewHolder.tvdate = (TextView) convertView
                     .findViewById(R.id.tvdate);
             viewHolder.tvcolor = (TextView) convertView
                     .findViewById(R.id.tv_total1);
             
             viewHolder.tr_Skin = (TableRow) convertView
                     .findViewById(R.id.tr_Skin);
             
             viewHolder.tr_Color = (TableRow) convertView
                     .findViewById(R.id.tr_Color);
             
             convertView.setTag(viewHolder);
           
             //viewHolder.textView.setText(gridValues[position]);
              
         } else {
        	 
        	 viewHolder = (ViewHolder) convertView.getTag();
//             gridView = (View) convertView;
          }
         
         HashMap<String, String> map = new HashMap<String, String>();
 		map = data.get(position);   
 		String m,d,r;
 		
 		if(map.get("SKIN").toString().equalsIgnoreCase("null")|| map.get("SKIN").toString().equalsIgnoreCase("anyType{}")){
 			
 			m="";
 		}else{
 			
 			m= map.get("SKIN");
 		}
 		if(map.get("AndroidCreatedDate").toString().equalsIgnoreCase("null")|| map.get("AndroidCreatedDate").toString().equalsIgnoreCase("anyType{}")){
 			d="";
 		}else{
 			String date[] = map.get("AndroidCreatedDate").toString().split(" ");
 			d=date[0];
 		}
 		if(map.get("COLOR").toString().equalsIgnoreCase("null") || map.get("COLOR").toString().equalsIgnoreCase("anyType{}")){
 			r="";
 		}else{
 			
 			r=map.get("COLOR");
 		}

 		
 		viewHolder.tv_total.setText(m);
// 		viewHolder.tvcolor.setText(m+"=");
 		viewHolder.tvdate.setText(d);
 		viewHolder.tvcolor.setText(r);
 		
 		if(map.get("TYPE").equalsIgnoreCase("SKIN"))
 		{
 			viewHolder.tr_Color.setVisibility(View.GONE);
 			viewHolder.tr_Skin.setVisibility(View.VISIBLE);
 		}
 		else if(map.get("TYPE").equalsIgnoreCase("COLOR"))
 		{
 			viewHolder.tr_Skin.setVisibility(View.GONE);
 			viewHolder.tr_Color.setVisibility(View.VISIBLE);
 		}
 		else
 		{
 			viewHolder.tr_Skin.setVisibility(View.VISIBLE);
 			viewHolder.tr_Color.setVisibility(View.VISIBLE);
 		}
 		
		//viewholder.tv
		//viewHolder.tvskin.setText(text);
 		
 		  animation.setDuration(500);
			 
 		 convertView.startAnimation(animation);
		 
		    animation = null;
	
  
		return convertView;
	}

}
