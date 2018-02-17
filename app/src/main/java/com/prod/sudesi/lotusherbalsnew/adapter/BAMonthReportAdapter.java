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


public class BAMonthReportAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	String receiver;

	ViewHolder viewHolder;

	public BAMonthReportAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

		TextView tv_code_no;
		TextView tv_Product;
		TextView tv_size;

		TextView tv_mrp;
		TextView tv_opening_stock;
		TextView tv_receipt;
		TextView tv_return_stock;
		TextView tv_total_stock;
		TextView tv_sales;
		TextView tv_closing_stock;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			viewHolder = new ViewHolder();// create new holder
			convertView = inflater.inflate(R.layout.layout_ba_month_wise_report,
					null);// inflater for view
			viewHolder.tv_code_no = (TextView) convertView
					.findViewById(R.id.tv_barm_code_no);
			viewHolder.tv_Product = (TextView) convertView
					.findViewById(R.id.tv_barm_product);
			viewHolder.tv_size = (TextView) convertView
					.findViewById(R.id.tv_barm_size);

			viewHolder.tv_mrp = (TextView) convertView
					.findViewById(R.id.tv_barm_mrp);
			viewHolder.tv_opening_stock= (TextView) convertView
					.findViewById(R.id.tv_barm_opening_stck);
			viewHolder.tv_receipt = (TextView) convertView
					.findViewById(R.id.tv_barm_receipt);
			
			viewHolder.tv_return_stock = (TextView) convertView
					.findViewById(R.id.tv_barm_rtn_stock);
			viewHolder.tv_total_stock = (TextView) convertView
					.findViewById(R.id.tv_barm_total_stock);
			viewHolder.tv_sales = (TextView) convertView
					.findViewById(R.id.tv_barm_sales);
			viewHolder.tv_closing_stock = (TextView) convertView
					.findViewById(R.id.tv_barm_closing_stock);
			

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

		String codeno = map.get("ProductCode");
		String produt = map.get("ProductName");
		String size = map.get("Size");
		String mrp = map.get("MRP");
		String opnstck = map.get("OpeningStock");
		String receipt = map.get("Receipt");
		String retn_stock = map.get("ReturnStock");
		String total_stock = map.get("TotalStock");
		String sales = map.get("Sales");
		String closing_stck = map.get("ClosingStock");
		
		

		if (codeno.equalsIgnoreCase("anyType{}")) {

			codeno = "0";
		}

		if (produt.equalsIgnoreCase("anyType{}")) {

			produt = "0";
		}

		if (size.equalsIgnoreCase("anyType{}")) {

			size = "0";
		}
		if (mrp.equalsIgnoreCase("anyType{}")) {

			mrp = "0";
		}
		if (opnstck.equalsIgnoreCase("anyType{}")) {

			opnstck = "0";
		}
		if (receipt.equalsIgnoreCase("anyType{}")) {

			receipt = "0";
		}
		
		if (retn_stock.equalsIgnoreCase("anyType{}")) {

			retn_stock = "0";
		}
		if (total_stock.equalsIgnoreCase("anyType{}")) {

			total_stock = "0";
		}
		if (sales.equalsIgnoreCase("anyType{}")) {

			sales = "0";
		}
		
		if (closing_stck.equalsIgnoreCase("anyType{}")) {

			closing_stck = "0";
		}
		
		if (codeno.equalsIgnoreCase("null")) {

			codeno = "0";
		}

		if (produt.equalsIgnoreCase("null")) {

			produt = "0";
		}

		if (size.equalsIgnoreCase("null")) {

			size = "0";
		}
		if (mrp.equalsIgnoreCase("null")) {

			mrp = "0";
		}
		if (opnstck.equalsIgnoreCase("null")) {

			opnstck = "0";
		}
		if (receipt.equalsIgnoreCase("null")) {

			receipt = "0";
		}
		
		if (retn_stock.equalsIgnoreCase("null")) {

			retn_stock = "0";
		}
		if (total_stock.equalsIgnoreCase("null")) {

			total_stock = "0";
		}
		if (sales.equalsIgnoreCase("null")) {

			sales = "0";
		}
		
		if (closing_stck.equalsIgnoreCase("null")) {

			closing_stck = "0";
		}

		/*viewHolder.tv_MonthsP.setText(map.get("PreviousYearP"));
		viewHolder.tv_netbalP.setText(map.get("NetAmountP"));
		viewHolder.tv_growthP.setText(map.get("GrowthP"));
		viewHolder.tv_MonthsC.setText(map.get("CurrentYearC"));
		viewHolder.tv_netbalC.setText(map.get("NetAmountC"));
		viewHolder.tv_growthC.setText(map.get("GrowthC"));*/
		
		viewHolder.tv_code_no.setText(codeno);
		viewHolder.tv_Product.setText(produt);
		viewHolder.tv_size.setText(size);
		viewHolder.tv_mrp.setText(mrp);
		viewHolder.tv_opening_stock.setText(opnstck);
		viewHolder.tv_receipt.setText(receipt);
		viewHolder.tv_return_stock.setText(retn_stock);
		viewHolder.tv_total_stock.setText(total_stock);
		viewHolder.tv_sales.setText(sales);
		viewHolder.tv_closing_stock.setText(closing_stck);

		// RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
		// 70, 70);
		// params.setMargins(5,15,0, 0);

		return convertView;

	}

}
