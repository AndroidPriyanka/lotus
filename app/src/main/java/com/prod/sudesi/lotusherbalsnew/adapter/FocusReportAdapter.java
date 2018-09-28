package com.prod.sudesi.lotusherbalsnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.Models.FocusModel;
import com.prod.sudesi.lotusherbalsnew.R;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FocusReportAdapter extends BaseAdapter {

    Context context1;
    private List<FocusModel> focusModelList;
    private LayoutInflater inflater1 = null;
    String receiver;

    ViewHolder viewHolder;

    public FocusReportAdapter(Context context, ArrayList d) {
        context1 = context;
        focusModelList = d;
        inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return focusModelList.size();
    }

    @Override
    public Object getItem(int i) {
//        return i;
        return focusModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder {
        TextView txt_prod_name;
        TextView txt_size;
        TextView txt_mrp;
        TextView tv_target_qty;
        TextView tv_target_rs;
        TextView tv_achivmnt_qty;
        TextView tv_achivmnt_rs;

    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        try {
            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = inflater1.inflate(R.layout.focusreport_row_item, null);

                viewHolder.txt_prod_name = (TextView) convertView.findViewById(R.id.txt_prod_name);
                viewHolder.txt_size = (TextView) convertView.findViewById(R.id.txt_size);
                viewHolder.txt_mrp = (TextView) convertView.findViewById(R.id.txt_mrp);
                viewHolder.tv_target_qty = (TextView) convertView.findViewById(R.id.tv_target_qty);
                viewHolder.tv_target_rs = (TextView) convertView.findViewById(R.id.tv_target_rs);
                viewHolder.tv_achivmnt_qty = (TextView) convertView.findViewById(R.id.tv_achivmnt_qty);
                viewHolder.tv_achivmnt_rs = (TextView) convertView.findViewById(R.id.tv_achivmnt_rs);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final FocusModel focusModel = focusModelList.get(i);

            viewHolder.txt_prod_name.setText(focusModel.getPro_name());
            viewHolder.txt_size.setText(focusModel.getSize());
            viewHolder.txt_mrp.setText(focusModel.getPrice());
            viewHolder.tv_target_qty.setText(focusModel.getTarget_qty());
            viewHolder.tv_target_rs.setText(focusModel.getTarget_amt());
            viewHolder.tv_achivmnt_qty.setText(focusModel.getAchievement_Unit());
            viewHolder.tv_achivmnt_rs.setText(focusModel.getAchievement_value());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
