package com.prod.sudesi.lotusherbalsnew.TableFixHeader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.prod.sudesi.lotusherbalsnew.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MatrixTableAdapter<T> extends BaseTableAdapter implements Filterable {

    private final Context context;

    Integer opqty = 0, opvalue = 0, soldqty = 0, soldvalue = 0, closeqty = 0, closevalu = 0, grossamt = 0, netamt = 0;

    private T[][] table;
    private T[][] searchdata;
    private String headers1[];
    private String headers[] = {"Product Name", "Type", "MRP", "Size", "Op Qty", "Op \u20B9", "Stk recvd", "Rtn-From Cust",
            "Rtn-to Co.", "Sold Qty", "Sold \u20B9", "Close Qty", "Close \u20B9", "Gross Amt", "Dis", "Net Amt", "Status",
            "Id", "Outlet"};

    private final float density;

    private final int[] width = {
            300,
            150,
            70,
            70,
            70,
            70,
            90,
            60,
            70,
            60,
            70,
            60,
            80,
            70,
            60,
            70,
            70,
            60,
            130,
    };


    public MatrixTableAdapter(Context context) {
        this(context, null);
    }

    public MatrixTableAdapter(Context context, T[][] table) {
        this.context = context;
        Resources r = context.getResources();

        density = context.getResources().getDisplayMetrics().density;
        setInformation(table);
    }

    public void setInformation(T[][] table) {
        this.table = table;
        this.searchdata = table;
        this.headers1 = (String[]) table[0];
    }


    @Override
    public int getRowCount() {
        return table.length;
    }

    @Override
    public int getColumnCount() {
        return table[0].length - 1;
    }

    @Override
    public View getView(int row, int column, View convertView, ViewGroup parent) {
        final View view;
        switch (getItemViewType(row, column)) {
            case 0:
                view = getFirstHeader(row, column, convertView, parent);
                break;
            case 1:
                view = getHeader(row, column, convertView, parent);
                break;
            case 2:
                view = getsecondHeader(row, column, convertView, parent);
                break;
            case 3:
                view = getbodyrow(row, column, convertView, parent);
                break;
            default:
                throw new RuntimeException("wtf?");
        }
        return view;
    }

    private View getFirstHeader(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_header_first, null);
        }
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(headers[0].toString());
        return convertView;
    }

    private View getHeader(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_header_first, null);
        }
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(headers[column + 1].toString());
        return convertView;
    }

    private View getsecondHeader(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_header_new, null);
        }
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(headers1[column + 1].toString());

        return convertView;
    }

    private View getbodyrow(int row, int column, View convertView, ViewGroup parent) {
        row = row - 1;
        if (convertView == null) {
            convertView = new TextView(context);
            ((TextView) convertView).setGravity(Gravity.CENTER);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(0xFFffffff); // Changes this drawable to use a single color instead of a gradient
            gd.setCornerRadius(5);
            gd.setStroke(1, 0xFF000000);
            ((TextView) convertView).setBackground(gd);
            ((TextView) convertView).setTextColor(Color.BLACK);
        }
        ((TextView) convertView).setText(table[row + 1][column + 1].toString());
        return convertView;
    }

    @Override
    public int getHeight(int row) {
        final int height;
        if (row == -1) {
            height = 52;
        } else {
            height = 40;
        }
        return Math.round(height * density);
    }

    @Override
    public int getWidth(int column) {
        return Math.round(width[column + 1] * density);
    }

    @Override
    public int getItemViewType(int row, int column) {
        final int itemViewType;
        if (row == -1 && column == -1) {
            itemViewType = 0;
        } else if (row == -1) {
            itemViewType = 1;
        } else if (row == 0) {
            itemViewType = 2;
        } else {
            itemViewType = 3;
        }
        return itemViewType;
    }

    @Override
    public int getViewTypeCount() {
        return 17;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                table = (T[][]) results.values; // has the filtered values
                headers1 = (String[]) table[0];
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<HashMap<Integer, String>> FilteredArrList = null;
                HashMap<Integer, ArrayList<HashMap<Integer, String>>> map1 = null;
                ArrayList<String> list1 = null;

                boolean stringnotmatch = false;
                if (searchdata == null) {
                    searchdata = (T[][]) table; // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                try {
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = searchdata.length;
                        results.values = searchdata;
                    } else {
                        constraint = constraint.toString().toUpperCase();
                        map1 = new HashMap<>();
                        String searchrow[] = new String[0];
                        HashMap<Integer, String> map2 = null;

                        ArrayList<String> list3 = null;
                        int count = 1;
                        for (int i = 0; i < searchdata.length; i++) {
                            FilteredArrList = new ArrayList<HashMap<Integer, String>>();
                            searchrow = (String[]) searchdata[i];
                            list1 = new ArrayList<>();

                            String data = searchrow[1];
                            if (data.toUpperCase().startsWith(constraint.toString())) {
                                stringnotmatch = true;
                                opqty = 0;
                                opvalue = 0;
                                soldqty = 0;
                                soldvalue = 0;
                                closeqty = 0;
                                closevalu = 0;
                                grossamt = 0;
                                netamt = 0;

                                List<String> wordList = Arrays.asList(searchrow);

                                for (String e : wordList) {
                                    list1.add(e);
                                }
                            }

                            if (list1.size() > 0) {

                                for (int k = 0; k < list1.size(); k++) {
                                    map2 = new HashMap<Integer, String>();
                                    map2.put(k, list1.get(k));
                                    FilteredArrList.add(map2);
                                }
                                map1.put(count, FilteredArrList);
                                count++;
                            }
                        }
                        if (count > 0) {

                            if (stringnotmatch == true) {
                                for (int l = 0; l < map1.get(1).size(); l++) {
                                    FilteredArrList = new ArrayList<HashMap<Integer, String>>();
                                    if (l == 0) {
                                        for (int m = 0; m < map1.size(); m++) {
                                            opqty = opqty + Integer.valueOf(map1.get(m + 1).get(4).get(4).toString());
                                            opvalue = opvalue + Integer.valueOf((map1.get(m + 1).get(5).get(5).toString().replace("\u20B9", "")));
                                            soldqty = soldqty + Integer.valueOf(map1.get(m + 1).get(9).get(9).toString());
                                            soldvalue = soldvalue + Integer.valueOf((map1.get(m + 1).get(10).get(10).toString().replace("\u20B9", "")));
                                            closeqty = closeqty + Integer.valueOf(map1.get(m + 1).get(11).get(11).toString());
                                            closevalu = closevalu + Integer.valueOf((map1.get(m + 1).get(12).get(12).toString().replace("\u20B9", "")));
                                            grossamt = grossamt + Integer.valueOf((map1.get(m + 1).get(13).get(13).toString().replace("\u20B9", "")));
                                            netamt = netamt + Integer.valueOf((map1.get(m + 1).get(15).get(15).toString().replace("\u20B9", "")));
                                        }
                                    }
                                    list3 = new ArrayList<String>();
                                    list3.add("TOTAL VALUE IN RUPEES");
                                    list3.add("");
                                    list3.add("");
                                    list3.add("");
                                    list3.add(String.valueOf(opqty));
                                    list3.add("\u20B9" + String.valueOf(opvalue));
                                    list3.add("");
                                    list3.add("");
                                    list3.add("");
                                    list3.add(String.valueOf(soldqty));
                                    list3.add("\u20B9" + String.valueOf(soldvalue));
                                    list3.add(String.valueOf(closeqty));
                                    list3.add("\u20B9" + String.valueOf(closevalu));
                                    list3.add("\u20B9" + String.valueOf(grossamt));
                                    list3.add("");
                                    list3.add("\u20B9" + String.valueOf(netamt));
                                    list3.add("");
                                    list3.add("");
                                    list3.add("");

                                    for (int k = 0; k < list3.size(); k++) {
                                        map2 = new HashMap<Integer, String>();
                                        map2.put(k, list3.get(k));
                                        FilteredArrList.add(map2);
                                    }

                                }

                                map1.put(0, FilteredArrList);

                            }

                            String[][] reportArray = new String[count][19];
                            if (stringnotmatch == true) {
                                for (Integer key : map1.keySet()) {
                                    System.out.println("key : " + key);
                                    System.out.println("value : " + map1.get(key));

                                    try {
                                        if (map1.get(key) != null || (map1.get(key) == null && map1.containsKey(key))) {
                                            for (int x = 0; x < map1.get(key).get(0).size(); x++) {
                                                for (int y = 0; y < reportArray[x].length; y++) {
                                                    reportArray[key][y] = getValue(map1.get(key).get(y).toString());

                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                // set the Filtered result to return
                                results.count = reportArray.length;
                                results.values = reportArray;
                            } else {
                                results.count = searchdata.length;
                                results.values = searchdata;
                                showToastMessage("Entered value does not match with the ProductType. Please Enter proper ProductType", 4000);

                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return results;

            }
        };
        return filter;
    }

    public String getValue(String inputStr) {
        String value = "";
        if (inputStr.contains("=")) {
            String[] valueArr = inputStr.split("=");
            String subStr = valueArr[1];
            value = subStr.substring(0, subStr.indexOf("}"));
        }
        return value;
    }

    public void showToastMessage(String text, int duration) {
        final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
    }
}
