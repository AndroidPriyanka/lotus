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

public class OutletWiseSalesAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;


    ViewHolder viewHolder;

    public OutletWiseSalesAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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

        TextView txt_outletName;
        TextView txt_soldstock;
        TextView txt_amount;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        convertView = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();// create new holder
            convertView = inflater.inflate(R.layout.row_outletwisesales, null);// inflater for view

            viewHolder.txt_outletName = (TextView) convertView.findViewById(R.id.txt_outletName);
            viewHolder.txt_soldstock = (TextView) convertView.findViewById(R.id.txt_soldstock);
            viewHolder.txt_amount = (TextView) convertView.findViewById(R.id.txt_amount);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // display the messages in view
        HashMap<String, String> map = new HashMap<String, String>();

        map = data.get(position);

        String outletname = map.get("outletname");
        String SoldStock = map.get("SoldStock");
        String NetAmount = map.get("NetAmount");

        viewHolder.txt_outletName.setText(outletname);
        viewHolder.txt_soldstock.setText(SoldStock);
        viewHolder.txt_amount.setText(NetAmount);

        return convertView;

    }

}
