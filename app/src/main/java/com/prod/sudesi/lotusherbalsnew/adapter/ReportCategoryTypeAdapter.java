package com.prod.sudesi.lotusherbalsnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportCategoryTypeAdapter extends BaseAdapter {

    Context context1;
    private ArrayList<HashMap<String, String>> data;
    private LayoutInflater inflater1=null;
    String receiver;

    ViewHolder viewHolder;


    public ReportCategoryTypeAdapter(Context context, ArrayList<HashMap<String, String>> d) {

        context1 = context;
        data=d;
        inflater1 = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder{
        TextView txt_type;
        TextView tv_open_qty;
        TextView tv_open_rs;
        TextView tv_receive_qty;
        TextView tv_receive_rs;
        TextView tv_close_qty;
        TextView tv_close_rs;

    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if(convertView==null)
        {
            viewHolder=new ViewHolder();

            convertView = inflater1.inflate(R.layout.categorywisedata_row_item, null);

            viewHolder.txt_type = (TextView) convertView.findViewById(R.id.txt_type);
            viewHolder.tv_open_qty = (TextView) convertView.findViewById(R.id.tv_open_qty);
            viewHolder.tv_open_rs = (TextView) convertView.findViewById(R.id.tv_open_rs);
            viewHolder.tv_receive_qty = (TextView) convertView.findViewById(R.id.tv_receive_qty);
            viewHolder.tv_receive_rs = (TextView) convertView.findViewById(R.id.tv_receive_rs);
            viewHolder.tv_close_qty = (TextView) convertView.findViewById(R.id.tv_close_qty);
            viewHolder.tv_close_rs = (TextView) convertView.findViewById(R.id.tv_close_rs);

            convertView.setTag(viewHolder);

        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map = data.get(i);

        viewHolder.txt_type.setText(map.get("Type"));


        viewHolder.tv_open_qty.setText(map.get("Opening"));
        String opening = map.get("opening_amt");
        if(opening == null || opening.equalsIgnoreCase("null")){
            opening = "0";
        }else{
            opening = opening;
        }
        viewHolder.tv_open_rs.setText("\u20B9 " + opening);

        viewHolder.tv_receive_qty.setText(map.get("Receive"));
        String receive = map.get("receive_amt");
        if(receive == null || receive.equalsIgnoreCase("null")){
            receive = "0";
        }else{
            receive = receive;
        }
        viewHolder.tv_receive_rs.setText("\u20B9 " + receive);

        viewHolder.tv_close_qty.setText(map.get("closing"));
        String closing = map.get("close_amt");
        if(closing == null || closing.equalsIgnoreCase("null")){
            closing = "0";
        }else{
            closing = closing;
        }
        viewHolder.tv_close_rs.setText("\u20B9 " + closing);


        return convertView;
    }
}
