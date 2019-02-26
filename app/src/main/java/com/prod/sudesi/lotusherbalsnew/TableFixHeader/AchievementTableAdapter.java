package com.prod.sudesi.lotusherbalsnew.TableFixHeader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.R;

public class AchievementTableAdapter<T> extends BaseTableAdapter {
    private final Context context;

    private T[][] table;
    private String headers[];
    private final float density;
    private final int width;


    public AchievementTableAdapter(Context context) {

        this(context, null);
    }

    public AchievementTableAdapter(Context context, T[][] table) {
        this.context = context;
        Resources r = context.getResources();

        density = context.getResources().getDisplayMetrics().density;
        setInformation(table);

        width = context.getResources().getDimensionPixelSize(R.dimen.table_width);
    }

    public void setInformation(T[][] table) {
        this.table = table;
        this.headers = (String[]) table[0];
    }


    @Override
    public int getRowCount() {

        return table.length - 1;
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
                view = getHeader(row, column, convertView, parent);
                break;
            case 1:
                view = getbodyrow(row, column, convertView, parent);
                break;

            default:
                throw new RuntimeException("wtf?");
        }
        return view;
    }

    private View getHeader(int row, int column, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_header_first, null);
        }
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(headers[column + 1].toString());
        return convertView;
    }

    private View getbodyrow(int row, int column, View convertView, ViewGroup parent) {
        //row = row - 1;
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

        try {
            if (row != -1) {
                ((TextView) convertView).setText(table[row + 1][column + 1].toString());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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

        return width;
    }

    @Override
    public int getItemViewType(int row, int column) {
        final int itemViewType;
        if (row == -1) {
            itemViewType = 0;
        } else {
            itemViewType = 1;
        }
        return itemViewType;
    }

    @Override
    public int getViewTypeCount() {
        return headers.length - 2;
    }


}
