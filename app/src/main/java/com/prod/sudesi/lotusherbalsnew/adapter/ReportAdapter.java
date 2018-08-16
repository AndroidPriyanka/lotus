package com.prod.sudesi.lotusherbalsnew.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.prod.sudesi.lotusherbalsnew.R;

import java.util.ArrayList;
import java.util.HashMap;


public class ReportAdapter extends BaseAdapter {


    //private Activity activity;
    Context context1;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    String receiver;

    ViewHolder viewHolder;

    public ReportAdapter(Context context, ArrayList<HashMap<String, String>> d) {

        context1 = context;
        data = d;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        TextView category;
        TextView type;
        TextView product;
        TextView op;
        TextView sl;
        TextView rt;
        TextView rtns;
        TextView cl;
        TextView size;
        TextView price;
        TextView opening_stock;
        TextView stock_recieved;
        TextView totalamount;
        TextView totalnetamount;

        TextView tv_productid;

        TextView discount;
        TextView tv_status;
        TextView tv_outlet;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            Log.e("", "check1");
            viewHolder = new ViewHolder();//create new holder
            convertView = inflater.inflate(R.layout.report_layout, null);//inflater for view


            viewHolder.category = (TextView) convertView.findViewById(R.id.tv_category);
            viewHolder.type = (TextView) convertView.findViewById(R.id.tv_type);
            viewHolder.product = (TextView) convertView.findViewById(R.id.tv_product);
            viewHolder.op = (TextView) convertView.findViewById(R.id.tv_open);
            viewHolder.sl = (TextView) convertView.findViewById(R.id.tv_sold);
            viewHolder.rt = (TextView) convertView.findViewById(R.id.tv_return_saleable);
            viewHolder.rtns = (TextView) convertView.findViewById(R.id.tv_return_non_saleable);
            viewHolder.cl = (TextView) convertView.findViewById(R.id.tv_close);
            viewHolder.size = (TextView) convertView.findViewById(R.id.tv_report_size);
            viewHolder.price = (TextView) convertView.findViewById(R.id.tv_report_price);

            viewHolder.opening_stock = (TextView) convertView.findViewById(R.id.tv_r_open_stock);
            viewHolder.stock_recieved = (TextView) convertView.findViewById(R.id.tv_r_stock_received);

            viewHolder.totalamount = (TextView) convertView.findViewById(R.id.tv_report_total_amount);
            viewHolder.totalnetamount = (TextView) convertView.findViewById(R.id.tv_report_total_net_amount);
            viewHolder.discount = (TextView) convertView.findViewById(R.id.tv_report_discount);
            viewHolder.tv_productid = (TextView) convertView.findViewById(R.id.tv_productid);
            viewHolder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            viewHolder.tv_outlet = (TextView) convertView.findViewById(R.id.tv_outlet);


            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //display the messages in view
        HashMap<String, String> map = new HashMap<String, String>();
        map = data.get(position);


        int cont = data.size();

        //	for(int i=1;i<=cont;i++){


        map.get("product_type").toString().length();


        viewHolder.tv_productid.setText(map.get("db_id"));
        viewHolder.category.setText(map.get("product_category"));
        viewHolder.type.setText(map.get("product_type"));
        viewHolder.product.setText(map.get("product_name"));
        viewHolder.size.setText(map.get("size"));
        viewHolder.price.setText(map.get("price"));
        int openingstock;
        if (String.valueOf(map.get("opening_stock")).equalsIgnoreCase("null")) {
            openingstock = 0;
        } else {
            openingstock = Integer.parseInt(map.get("opening_stock"));
        }
        int mrp = Integer.parseInt(map.get("price"));
        String totalopeningamt = String.valueOf(openingstock * mrp);
        viewHolder.opening_stock.setText(openingstock + "(" + totalopeningamt + ")");

        viewHolder.stock_recieved.setText(map.get("stock_received"));
        viewHolder.op.setText(map.get("stock_in_hand"));
        int closingstock;
        if (String.valueOf(map.get("close_bal")).equalsIgnoreCase("null")) {
            closingstock = 0;
        } else {
            closingstock = Integer.parseInt(map.get("close_bal"));
        }
        String totalclosingamt = String.valueOf(closingstock * mrp);
        viewHolder.cl.setText(closingstock + "(" + totalclosingamt + ")");
        viewHolder.rt.setText(map.get("return_saleable"));
        viewHolder.rtns.setText(map.get("return_non_saleable"));
        viewHolder.sl.setText(map.get("sold_stock"));
        viewHolder.totalamount.setText(map.get("total_gross_amount"));
        viewHolder.totalnetamount.setText(map.get("total_net_amount"));
        viewHolder.discount.setText(map.get("discount"));
        viewHolder.tv_outlet.setText(map.get("FLRCode"));

        if (map.get("savedServer").equalsIgnoreCase("0")) {
            Log.e("---", "NU");
            viewHolder.tv_status.setText("NU");
        } else if (map.get("savedServer").equalsIgnoreCase("1")) {
            Log.e("---", "U");
            viewHolder.tv_status.setText("U");
        }

        //	}

        return convertView;

    }

}
