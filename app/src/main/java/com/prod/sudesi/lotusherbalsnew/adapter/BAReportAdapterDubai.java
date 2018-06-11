package com.prod.sudesi.lotusherbalsnew.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BAReportAdapterDubai extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    BAReportAdapterDubai.ViewHolder viewHolder;

    public BAReportAdapterDubai(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        TextView txtmonthc;
        TextView txtnetamtc;
        TextView txtgrowthc;
        TextView txtmonthp;
        TextView txtnetamtp;
        TextView txtgrowthp;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            viewHolder = new BAReportAdapterDubai.ViewHolder();// create new holder
            convertView = inflater.inflate(R.layout.layout_ba_year_wise_report_dubai,
                    null);// inflater for view
            viewHolder.txtmonthc = (TextView)convertView.findViewById(R.id.txtmonthc);
            viewHolder.txtnetamtc =(TextView)convertView.findViewById(R.id.txtnetamtc);
            viewHolder.txtgrowthc =(TextView)convertView.findViewById(R.id.txtgrowthc);
            viewHolder.txtmonthp = (TextView)convertView.findViewById(R.id.txtmonthp);
            viewHolder.txtnetamtp = (TextView)convertView.findViewById(R.id.txtnetamtp);
            viewHolder.txtgrowthp = (TextView)convertView.findViewById(R.id.txtgrowthp);


            convertView.setTag(viewHolder);


        } else {
            viewHolder = (BAReportAdapterDubai.ViewHolder) convertView.getTag();
        }

        // display the messages in view
        HashMap<String, String> map = new HashMap<String, String>();
        map = data.get(position);

        String GroCurSkin = map.get("GrowthCSkin");
        String GroPreSkin = map.get("GrowthPSkin");
        String NetAmtCurSkin = map.get("NetAmountCSkin");
        String NetAmtPreSkin = map.get("NetAmountPSkin");
        String yearMonthsC = map.get("years_MonthsC");
        String yearMonthsP = map.get("years_MonthsP");


        if (GroCurSkin.equalsIgnoreCase("anyType{}") || GroCurSkin==null ||GroCurSkin.equalsIgnoreCase("null") ) {

            GroCurSkin = "0";
        }

        if (GroPreSkin.equalsIgnoreCase("anyType{}")|| GroPreSkin==null|| GroPreSkin.equalsIgnoreCase("null")) {

            GroPreSkin = "0";
        }

        if (NetAmtCurSkin.equalsIgnoreCase("anyType{}")|| NetAmtCurSkin==null|| NetAmtCurSkin.equalsIgnoreCase("null")) {

            NetAmtCurSkin = "0";
        }

        if (NetAmtPreSkin.equalsIgnoreCase("anyType{}") ||NetAmtPreSkin ==null||NetAmtPreSkin.equalsIgnoreCase("null")) {

            NetAmtPreSkin = "0";
        }
        if (yearMonthsC.equalsIgnoreCase("anyType{}") ||yearMonthsC ==null||yearMonthsC.equalsIgnoreCase("null")) {

            yearMonthsC = "0";
        }

        if (yearMonthsP.equalsIgnoreCase("anyType{}")|| yearMonthsP==null||yearMonthsP.equalsIgnoreCase("null")) {

            yearMonthsP = "0";
        }



		viewHolder.txtmonthc.setText(yearMonthsC);
		viewHolder.txtnetamtc.setText(NetAmtCurSkin);
		viewHolder.txtgrowthc.setText(GroCurSkin);
		viewHolder.txtmonthp.setText(yearMonthsP);
		viewHolder.txtnetamtp.setText(NetAmtPreSkin);
		viewHolder.txtgrowthp.setText(GroPreSkin);


        return convertView;

    }

}