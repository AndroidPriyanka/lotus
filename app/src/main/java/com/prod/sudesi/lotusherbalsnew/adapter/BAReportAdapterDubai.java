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

      /*  viewHolder.tv_MonthsP.setText(py);
        viewHolder.tv_skinbalP.setText(samtp);
        viewHolder.tv_colorbalP.setText(camtp);

        viewHolder.tv_growthP_skin.setText(gp_skin);
        viewHolder.tv_growthP_color.setText(gp_color);

        viewHolder.tv_MonthsC.setText(cy);
        viewHolder.tv_skinbalC.setText(samtc);
        viewHolder.tv_colorbalC.setText(camtc);
        viewHolder.tv_growthC_skin.setText(gc_skin);
        viewHolder.tv_growthC_color.setText(gc_color);*/

        // RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        // 70, 70);
        // params.setMargins(5,15,0, 0);

        return convertView;

    }

}