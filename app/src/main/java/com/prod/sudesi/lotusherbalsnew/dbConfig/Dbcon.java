package com.prod.sudesi.lotusherbalsnew.dbConfig;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;


import com.prod.sudesi.lotusherbalsnew.image.ImageModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Dbcon {

    private Context context;
    private SQLiteDatabase database;
    private Dbhelper dbHelper;

    public Dbcon(Context context) {
        this.context = context;
    }


    public Dbcon open() throws SQLException {
        dbHelper = new Dbhelper(context);
        try {
            database = dbHelper.getWritableDatabase();
            //this.close();//-----21.02.2015
        } catch (Exception e) {
            dbHelper.onCreate(database);

        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(String values[], String names[], String tbl) {
        ContentValues initialValues = createContentValues(values, names);

        return database.insert(tbl, null, initialValues);
    }

    public boolean update(String rowId, String values[], String names[],
                          String tbl, String id) {
        ContentValues updateValues = createContentValues(values, names);

        return database.update(tbl, updateValues, id + "=" + rowId, null) > 0;
    }

    public boolean updateMulti(String rowId, String values[], String names[],
                               String tbl, String id[]) {
        ContentValues updateValues = createContentValues(values, names);

        return database.update(tbl, updateValues, rowId, id) > 0;
    }

    public void rawQueryDefined(String query) {

        database.rawQuery(query, null);
    }

    public Cursor rawQuery(String query) {

        return database.rawQuery(query, null);
    }

    public boolean delete(String tbl, String fName, String fValue) {

        return database.delete(tbl, fName + "=" + fValue, null) > 0;
    }

    public boolean deleteMulti(String tbl, String where, String fValue[]) {
        return database.delete(tbl, where, fValue) > 0;
    }

    public Cursor fetchallOrder(String tbl, String names[], String order) {
        return database.query(tbl, names, null, null, null, null, order);
    }

    public Cursor fetchallSpecify(String tbl, String names[], String fName,
                                  String fValue, String order) {

        Cursor mCursor = database.query(true, tbl, names, fName + "= '"
                + fValue + "'", null, null, null, order, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchallSpecifyMSelect(String tbl, String names[],
                                         String select, String args[], String order) {

        Cursor mCursor = database.query(true, tbl, names, select, args, null,
                null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchone(String fValue, String tbl, String names[],
                           String fName) throws SQLException {
        Cursor mCursor = database.query(true, tbl, names, fName + "='" + fValue
                + "'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    private ContentValues createContentValues(String values[], String names[]) {
        ContentValues values1 = new ContentValues();

        for (int i = 0; i < values.length; i++) {
            values1.put(names[i], values[i]);
        }

        return values1;
    }

    public int getScannedID(String name) {
        int scanedid = 0;

        String SQL = "SELECT scannedId from scan where saveName = '" + name
                + "'";

        Cursor cur = database.rawQuery(SQL, null);

        if (cur != null) {
            cur.moveToFirst();
            scanedid = cur.getInt(0);
        }

        return scanedid;
    }

    public void updateQuery(String empid, String productid, String soldStock) {
        try {

            String[] arr = new String[]{soldStock};
            String[] col = new String[]{"sold_stock"};
            ContentValues cv = createContentValues(arr, col);

            String[] whereArgs = new String[]{empid, productid};
            // Log.e("Update SQL", sql);
            long c = database.update("stock", cv, "emp_id=? and product_id=?",
                    whereArgs);
            Log.e("updated", String.valueOf(c));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateTester(String empid, String productid, String soldStock) {
        try {

            String[] arr = new String[]{soldStock};
            String[] col = new String[]{"sold_stock"};
            ContentValues cv = createContentValues(arr, col);

            String[] whereArgs = new String[]{empid, productid};
            // Log.e("Update SQL", sql);
            long c = database.update("tester", cv, "emp_id=? and product_id=?",
                    whereArgs);
            Log.e("updatedtester", String.valueOf(c));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void Scanimage(String longitude, String latitude, String desc,
                          String username, String product_type, String count,
                          List<ImageModel> list) {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String formattedDate = df.format(c.getTime());

        // int count = sharedpre.getInt("iCount", 0);

        Log.e("", "count==" + count);
        ContentValues values = new ContentValues();
        // for (int i = 0; i < count; i++) {

        Log.e("vlaue inserting==", "" + count);

        values.put("_empId", username);
        values.put("product_type", product_type);
        values.put("imagecount", count);
        values.put("saveTime", formattedDate);
        values.put("savedServer", "0");
        values.put("image_desc", desc);
        values.put("longitude", longitude);
        values.put("latitude", latitude);

        Log.e("", "values==" + values);

        database.insert("scan", null, values);

        // }
        getScannedID1(list, count, username);
        database.close(); // Closing database connection
    }

    public void getScannedID1(List<ImageModel> list, String count, String username) {
        int scanedid = 0;

        String SQL = "SELECT LAST_INSERT_ROWID();";

        Cursor cur = database.rawQuery(SQL, null);

        if (cur != null) {
            cur.moveToFirst();
            scanedid = cur.getInt(0);
        }
        Log.e("", "LastInsertId==" + scanedid);
        // return scanedid;

        insertimgaes(scanedid, list, count, username);
    }

    private void insertimgaes(int scanid, List<ImageModel> list, String count, String username) {
        // TODO Auto-generated method stub

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String formattedDate = df.format(c.getTime());

        int count1 = Integer.valueOf(count);
        ContentValues values = new ContentValues();
        for (int i = 0; i < count1; i++) {

            Log.e("vlaue inserting==", "" + count1);

            String[] IMN = list.get(i).getPath().split("/");

            for (int j = 1; j < IMN.length; j++) {

                Log.e("", "IMN.length=" + IMN.length);

                Log.e("", "IIMN[i]=" + IMN[j]);

                if (IMN[j].contains(".jpeg")) {

                    String image_name = IMN[j];

                    Log.e("", "image name=" + image_name);

                    values.put("image_name", image_name);

                }

            }

            values.put("scannedId", scanid);
            values.put("imagePath", list.get(i).getPath());
            //values.put("image_name", image_name);
            values.put("savedServer", "0");
            values.put("saveTime", formattedDate);

            Log.e("", "values==" + values);

            database.insert("image", null, values);

        }
        getdata();
        database.close();
    }

    private void getdata() {
        // TODO Auto-generated method stub

        String selectQuery = "SELECT  * FROM image where savedServer = '0'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // get the data into array,or class variable
                Log.e("", "cursor = " + cursor.toString());
                String a = cursor.getString(1);
                String b = cursor.getString(2);
                String c = cursor.getString(3);
                String d = cursor.getString(4);

                Log.e("", "image data==" + " scanid=" + a + " imagepath=" + b
                        + " savedserver=" + c + " savetime=" + d);
            } while (cursor.moveToNext());
        }
        database.close();

    }

    public Cursor getscanDetails(String saveserver) {
        Cursor imagedetails = null;
        try {
            Log.e("", "chchchchchchchch");
            String sql = "select * from scan where savedServer = " + saveserver;
            // String sql = "select * from scan ";
            Log.e("", "chhhhhhhhhh");
            imagedetails = database.rawQuery(sql, null);

            if (imagedetails.moveToFirst()) {
                do {

                    String product_type = imagedetails.getString(2);
                    Log.e("", "product_type=" + product_type);

                    String empid = imagedetails.getString(1);
                    Log.e("", "empid=" + empid);

                    String im_count = imagedetails.getString(3);
                    Log.e("", "im_count=" + im_count);

                    String description = imagedetails.getString(6);
                    Log.e("", "description=" + description);

                    String latitude = imagedetails.getString(7);
                    Log.e("", "latitude=" + latitude);

                    String longitude = imagedetails.getString(8);
                    Log.e("", "longitude=" + longitude);
                } while (imagedetails.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagedetails;
    }

    public Cursor getimageDetails(String scanid) {
        // TODO Auto-generated method stub
        Cursor imagedetails1 = null;
        try {

            String sql = "select * from image where scannedId = " + scanid + " and savedServer = '0'";

            imagedetails1 = database.rawQuery(sql, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagedetails1;

    }
/*
    @SuppressLint("ShowToast")
	public ArrayList<String> getproductcategory() {

		// TODO Auto-generated method stub
		Log.e("", "chch");

		String selectQuery = "SELECT  category FROM stock_product_category";
		Log.e("", "chch");

		Cursor cursor = database.rawQuery(selectQuery, null);
		Log.e("", "chch");
		ArrayList<String> data = new ArrayList<String>();
		Log.e("", "chch");
		if (cursor != null) {
			Log.e("", "chch");
			if (cursor.moveToFirst()) {
				Log.e("", "chch");
				do {
					Log.e("", "chch");
					// get the data into array,or class variable
					data.add(cursor.getString(cursor.getColumnIndex("category")));

					Log.e("", "data inserted=" + data);
				} while (cursor.moveToNext());
			}
		} else {

			Toast.makeText(context, "No data available", Toast.LENGTH_LONG);
		}
		database.close();
		return data;

	}*/

	/*@SuppressLint("ShowToast")
	public ArrayList<String> getproductype(String prodctcategory) {
		// TODO Auto-generated method stub
		ArrayList<String> product_type_data = new ArrayList<String>();

		String selectquery = " select product_type from stock_product_category_product_type_mapping where product_category_id = "
				+ prodctcategory;

		Cursor cursor = database.rawQuery(selectquery, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {

				do {
					product_type_data.add(cursor.getString(cursor
							.getColumnIndex(Dbhelper.KEY_PRODUCT_TYPE)));

					Log.e("", "data product_type=" + product_type_data);

				} while (cursor.moveToNext());

			}
		} else {

			Toast.makeText(context, "No data available", Toast.LENGTH_LONG);
		}
		database.close();
		return product_type_data;
	}
*/
	/*@SuppressLint("ShowToast")
	public ArrayList<String> getproducts(String selected_product_type1) {
		// TODO Auto-generated method stub
		ArrayList<String> product_data = new ArrayList<String>();

		String selectquery = " select products from stock_product_type_products_mapping where product_type = "
				+ "'" + selected_product_type1 + "'";

		Cursor cursor = database.rawQuery(selectquery, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {

				do {
					product_data.add(cursor.getString(cursor
							.getColumnIndex(Dbhelper.KEY_PRODUCTS)));

					Log.e("", "data product_type=" + product_data);

				} while (cursor.moveToNext());

			}
		} else {

			Toast.makeText(context, "No data available", Toast.LENGTH_LONG);
		}
		database.close();
		return product_data;
	}*/

    public void Insertstock(//String product_id,
                            String product_cat,
                            String product_type, String product_name, String emp_id,
                            String op_stk, String cl_stk, String fresher_stock, String ttl_amount,
                            String sld_stk, String price, String size, String eancode, String db_id1, String cat_id,

                            String date, String discount, String total_net_amt, String previous_stock, String return_saleable, String return_non_saleable, String shadno) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();
        // for (int i = 0; i < count1; i++) {

        Log.e("vlaue inserting into stock==", "");

        //values.put("product_id", product_id);
        values.put("product_category", product_cat);
        values.put("product_type", product_type);
        values.put("product_name", product_name);
        Log.e("vlaue inserting into stock==", "emp_id==" + emp_id);
        values.put("emp_id", emp_id);


        values.put("stock_in_hand", op_stk);
        values.put("close_bal", cl_stk);
        //	values.put("return", rt_stk);
        values.put("sold_stock", sld_stk);
        //	values.put("fresh_stock",fresher_stock);
        values.put("total_gross_amount", ttl_amount);
        values.put("savedServer", "0");
        values.put("price", price);
        values.put("size", size);
        values.put("db_id", db_id1);
        values.put("product_id", cat_id);
        values.put("eancode", eancode);


        values.put("opening_stock", previous_stock);
        values.put("stock_received", fresher_stock);

        values.put("return_non_saleable", return_non_saleable);
        values.put("return_saleable", return_saleable);

        values.put("total_net_amount", total_net_amt);
        values.put("discount", discount);
        values.put("insert_date", date);

        values.put("shadeNo", shadno);

        Log.e("", "values==" + values);

        database.insert("stock", null, values);

        // }
        // getStockdetails();
        database.close();
    }

    public Cursor getStockdetails() {
        // TODO Auto-generated method stub
        Cursor stockddetails1 = null;
        try {

            Log.e("", "getStockdetails==");
            String sql = "select * from stock where savedServer = '0'";

            stockddetails1 = database.rawQuery(sql, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stockddetails1;

    }

    public void InsertTester(
            String product_cat,
            String product_type, String product_name, String emp_id,

            String size, String eancode, String db_id1, String cat_id, String product_status, String req_date, String del_date, String shadeno, String category_id) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();


        Log.e("vlaue inserting into stock==", "");


        values.put("product_category", product_cat);
        values.put("product_type", product_type);
        values.put("product_name", product_name);
        values.put("emp_id", emp_id);


        values.put("savedServer", "0");

        values.put("size", size);
        values.put("db_id", db_id1);
        //values.put("product_id",cat);
        values.put("eancode", eancode);

        values.put("product_status", product_status);
        values.put("requried_date", req_date);
        values.put("delivered_date", del_date);
        values.put("shadeno", shadeno);
        values.put("cat_id", category_id);

        Log.e("", "values==" + values);

        database.insert("tester", null, values);


        database.close();
    }

    public Cursor getTesterdetails() {
        // TODO Auto-generated method stub
        Cursor testerdetails1 = null;
        try {

            Log.e("", "getTesterdetails==");
            String sql = "select * from tester where savedServer = '0' ";

            testerdetails1 = database.rawQuery(sql, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return testerdetails1;

    }

    public void update_tester_data(String id) {


        String sql = "UPDATE tester SET savedServer = '1' WHERE savedServer = '0' AND id= '" + id + "'";
        Log.e("local database", "sql=" + sql);
        database.execSQL(sql);
        Log.e("local database", "UPDATE tester table data");
    }

    public void update_stock_data(String id) {

        String sql = "UPDATE stock SET savedServer = '1' WHERE savedServer = '0'AND id= '" + id + "'";
        database.execSQL(sql);
        Log.e("local database", "UPDATE stock table data");
    }

    public void delete_stock_data() {

        String sql = "delete from stock where savedServer = '1'";
        database.execSQL(sql);
        Log.e("local database", "Delete stock table data");
    }

    public void delete_errorlog_data() {

        String sql = "delete from SYNC_LOG where FLAG = 'U'";
        database.execSQL(sql);
        Log.e("local database", "Delete log table data");
    }

    public void update_stock_cumm(String id) {

        String sql = "update stock_monthwise set savedServer = '1' where savedServer ='0' and id =  '" + id + "'";
        database.execSQL(sql);
        Log.e("local database", "UPDATE stock_monthwise table data");
    }

    public void update_image_data(String id) {

        String sql = "UPDATE image SET savedServer = '1' WHERE savedServer = '0' AND imageId= '" + id + "'";
        database.execSQL(sql);
        Log.e("local database", "UPDATE image table data");
    }

    public void update_scan_data(String id) {

        String sql = "UPDATE scan SET savedServer = '1' WHERE savedServer = '0' AND scannedId= '" + id + "'";
        database.execSQL(sql);
        Log.e("local database", "UPDATE scan table data");
    }

    public void update_boc_wise_stock_data(String id) {

        String sql = "UPDATE boc_wise_stock SET savedServer = '1' WHERE savedServer = '0'AND id= '" + id + "'";
        database.execSQL(sql);
        Log.e("local database", "UPDATE boc_wise_stock table data");
    }

    public Cursor getAttendanceData() {
        // TODO Auto-generated method stub

        String sql = "select * from attendance where savedServer = '0'";

        Cursor c = database.rawQuery(sql, null);

        return c;
    }

    public void update_Attendance_data(String id) {
        // TODO Auto-generated method stub

        String sql = "UPDATE attendance SET savedServer = '1' WHERE savedServer = '0' AND id= '" + id + "'";
        database.execSQL(sql);
        Log.e("local database", "UPDATE attendance table data");

    }

    /*------------------------------------------------------------*/
    // new changes for attendance 24 jan 2018
   /* public void updateAttendance_P_A(String attendance,String id) {
        // TODO Auto-generated method stub

        String sql = "UPDATE attendance SET attendance = '' WHERE attendance = 'P' AND id= '" + id + "'";
        database.execSQL(sql);
        Log.e("local database", "UPDATE attendance present or update data");

    }*/
   /*--------------------------------------------------*/

    @SuppressLint("ShowToast")
    public ArrayList<String> getproductcategory1() {

        // TODO Auto-generated method stub
        Log.e("", "chch");

        String selectQuery = "SELECT  DISTINCT ProductCategory FROM product_master";
        Log.e("", "chch");

        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("", "chch");
        ArrayList<String> data = new ArrayList<String>();
        Log.e("", "chch");
        if (cursor != null) {
            Log.e("", "chch");
            if (cursor.moveToFirst()) {
                data.add("Select");
                Log.e("", "chch");
                do {
                    Log.e("", "chch");
                    // get the data into array,or class variable
                    data.add(cursor.getString(cursor.getColumnIndex("ProductCategory")));

                    Log.e("", "data inserted=" + data);
                } while (cursor.moveToNext());
            }
        } else {

            //Toast.makeText(context, "No data available", Toast.LENGTH_LONG);
        }
        database.close();
        return data;

    }

    @SuppressLint("ShowToast")
    public ArrayList<String> getproductype1(String ProductCategory) {
        // TODO Auto-generated method stub
        ArrayList<String> product_type_data = new ArrayList<String>();

        String selectquery = " select DISTINCT ProductType from product_master where ProductCategory = "
                + "'" + ProductCategory + "'";

        Cursor cursor = database.rawQuery(selectquery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                product_type_data.add("Select");
                do {
                    product_type_data.add(cursor.getString(cursor
                            .getColumnIndex(Dbhelper.KEY_PRODUCT_TYPE)));

                    Log.e("", "data product_type=" + product_type_data);

                } while (cursor.moveToNext());

            }
        } else {

            //Toast.makeText(context, "No data available", Toast.LENGTH_LONG);
        }
        database.close();
        return product_type_data;
    }

    @SuppressLint("ShowToast")
    public ArrayList<String> getproducts1(String selected_product_type1) {
        // TODO Auto-generated method stub
        ArrayList<String> product_data = new ArrayList<String>();

        String selectquery = " SELECT DISTINCT ProductName FROM product_master WHERE ProductType = "
                + "'" + selected_product_type1 + "'";

        Cursor cursor = database.rawQuery(selectquery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                product_data.add("Select");
                do {
                    product_data.add(cursor.getString(cursor
                            .getColumnIndex(Dbhelper.KEY_PRODUCTS)));

                    Log.e("", "data product_type=" + product_data);

                } while (cursor.moveToNext());

            }
        } else {

            //	Toast.makeText(context, "No data available", Toast.LENGTH_LONG);
        }
        database.close();
        return product_data;
    }


    public String getLastSyncDate(String Tablename) {

        String date = null;
        String SQL = "SELECT date FROM  date_sync where table_name= '" + Tablename + "'";

        Cursor cur = database.rawQuery(SQL, null);

        if (cur != null) {
            if (cur.moveToFirst()) {

                do {
                    date = cur.getString(cur.getColumnIndex("date"));
                } while (cur.moveToNext());

            }

        }
        Log.e("", "LastInsertDate==" + date);
        return date;


    }

    public void insertProductMaster(String product_category,
                                    String product_type, String product, String d_id, String categoryid, String category, String shadeno, String eancode,
                                    String size, String mrp, String masterpackqty, String monopackqty, String innerqty, String sku_l, String sku_b,
                                    String sku_h, String price_type, String order_flag) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();
        // for (int i = 0; i < count1; i++) {

        Log.e("vlaue inserting into stock==", "");

        values.put("ProductCategory", product_category);
        values.put("ProductType", product_type);
        values.put("ProductName", product);
        values.put("db_id", d_id);
        values.put("CategoryId", categoryid);
        values.put("Category", category);
        values.put("ShadeNo", shadeno);
        values.put("EANCode", eancode);
        values.put("Size", size);
        values.put("MRP", mrp);
        values.put("MasterPackQty", masterpackqty);
        values.put("MonoPackQty", monopackqty);
        values.put("InnerQty", innerqty);
        values.put("SKU_L", sku_l);
        values.put("SKU_B", sku_b);
        values.put("SKU_H", sku_h);
        values.put("price_type", price_type);
        values.put("order_flag", order_flag);
        //values.put("LMD", lmd);
        Log.e("", "values==" + values);

        database.insert("product_master", null, values);

        // }
        // getStockdetails();
        database.close();

    }

    public void deletproductmaster(String categoryid,
                                   String eancode, String d_id, String table_name) {
        // TODO Auto-generated method stub
        Log.v("", "in delete");
        String sql = "delete  from " + table_name + " where  db_id =" + "'" + d_id + "'";
        Log.v("", "" + sql);

        database.execSQL(sql);


    }

    public void deleteTables(String table_name) {
        // TODO Auto-generated method stub
        Log.v("", "in delete");
        String sql = "delete  from " + table_name + "";
        Log.v("", "" + sql);

        database.execSQL(sql);


    }

    public void updateDateSync(String format, String table_name) {
        // TODO Auto-generated method stub
        Log.e("", "inside update date for " + table_name);
        String sql = "update date_sync set date = " + "'" + format + "'" + " where table_name = '" + table_name + "'";
        database.execSQL(sql);

    }


    public Cursor getReportforStock(String ProdCategory) {
        // TODO Auto-generated method stub


        Log.e("", "checkrrr1");
        //String selectquery = "select *  from stock where opening_stock IS NOT NULL AND opening_stock !=' '";
        //String selectquery = "select *  from stock where sold_stock  IS NOT NULL AND sold_stock  !=' ' OR opening_stock IS NOT NULL AND opening_stock !=' '"; //29.04.2015
        //String selectquery = "select * from stock order by cast(db_id as int)";

        String selectquery = "SELECT Customers.id, Orders.CategoryId ,Orders.db_id, Orders.EANCode, Orders.ProductCategory ," +
                "Orders.ProductType ,Orders.ProductName ,Orders.Size ,Orders.MRP,Customers.emp_id, Customers.opening_stock ," +
                "Customers.stock_received,Customers.stock_in_hand,Customers.close_bal,Customers.return_saleable," +
                "Customers.return_non_saleable,Customers.sold_stock ,Customers.total_gross_amount,Customers.total_net_amount," +
                "Customers.discount,Customers.savedServer,Customers. insert_date ,Customers.shadeNo FROM product_master Orders," +
                "stock Customers where Orders.db_id=Customers.db_id and Orders.ProductCategory = '" + ProdCategory + "' order by Orders.order_flag";  // new join query with Product Master table 11.05.2015//04.07.2015


				/*	String selectquery = "SELECT Customers.id, Orders.CategoryId ,Orders.db_id, Orders.EANCode, Orders.ProductCategory ," +
							"Orders.ProductType ,Orders.ProductName ,Orders.Size ,Orders.MRP,Customers.emp_id, Customers.opening_stock ," +
							"Customers.stock_received,Customers.stock_in_hand,Customers.close_bal,Customers.return_saleable," +
							"Customers.return_non_saleable,Customers.sold_stock ,Customers.total_gross_amount,Customers.total_net_amount," +
							"Customers.discount,Customers.savedServer,Customers. insert_date ,Customers.shadeNo FROM product_master Orders," +
							"stock Customers where "+ *//*Orders.db_id=Customers.db_id and*//* " Orders.ProductCategory = '"+ProdCategory+"' order by Orders.order_flag";
*/
        Cursor cursor = database.rawQuery(selectquery, null);

        Log.e("", "checkrrr2");


        return cursor;

    }

	/*public Cursor getReportforSale( ) {
		// TODO Auto-generated method stub


		Log.e("","checkrrr1");
			//String selectquery = "select *  from stock where opening_stock IS NOT NULL AND opening_stock !=' '";
	//	String selectquery = "select *  from stock where sold_stock  IS NOT NULL AND sold_stock  !=' ' OR opening_stock IS NOT NULL AND opening_stock !=' '";//28.04.2015
		String selectquery = "select *  from stock ";

			Cursor cursor = database.rawQuery(selectquery, null);
			Log.e("","checkrrr2");



			return cursor;

		}*/


    public Cursor getReportforTester() {
        // TODO Auto-generated method stub
        //String selectquery = "select * from tester ";
        //	String selectquery = "select * from tester where delivered_date = ' ' or delivered_date is null ";
        String selectquery = "select * from tester where  delivered_date ='' OR delivered_date = ' ' OR delivered_date IS NULL";
        Log.e("", "checkrrrt");
        Cursor cursort = database.rawQuery(selectquery, null);
        Log.e("", "checkrrr1t");

        return cursort;

    }

    public String getLastInsertIDofStock(String catid, String dbid, String eanid) {
        String cls_bal = "";

        String SQL = "SELECT close_bal FROM stock WHERE  db_id = " + "'" + dbid + "'" + " ORDER BY id DESC LIMIT 1";
        Cursor cur = database.rawQuery(SQL, null);

        if (cur != null) {
            Log.e("", "nullllll");
            if (cur.moveToFirst()) {
                Log.e("", "move to first");
                do {
                    cls_bal = cur.getString(cur.getColumnIndex("close_bal"));

                } while (cur.moveToNext());
            }
        } else {

            cls_bal = "0";
        }
        Log.e("", "cls_bal==" + cls_bal);
        // return scanedid;
        return cls_bal;
    }

    public String getLastInsertIDofTester() {
        String cls_bal = "";

        String SQL = "SELECT close_bal FROM tester ORDER BY id DESC LIMIT 1";

        Cursor cur = database.rawQuery(SQL, null);

        if (cur != null) {
            Log.e("", "nullllll");
            if (cur.moveToFirst()) {
                Log.e("", "move to first");
                do {
                    cls_bal = cur.getString(cur.getColumnIndex("close_bal"));

                } while (cur.moveToNext());
            }
        } else {

            cls_bal = "0";
        }
        Log.e("", "cls_bal==" + cls_bal);
        // return scanedid;
        return cls_bal;
    }

    //
    public Cursor fetchAllproducts(String selected_category,
                                   String selected_type, String selected_product) {
        // TODO Auto-generated method stub
        String selectquery = "select * from product_master where ProductCategory = " + "'" + selected_category + "'" + " AND ProductType = " + "'" + selected_type + "'" + " AND ProductName = " + "'" + selected_product + "'";
        Log.e("", "checkrrrt");
        Cursor cursort = database.rawQuery(selectquery, null);
        Log.e("", "checkrrr1t");

        return cursort;

    }


    public Cursor getuniquedata(String categoryid, String eancode, String d_id, String TableName) {
        // TODO Auto-generated method stub
        Log.v("", "check id available or not frm product_master");

        String sql = "select * from  " + TableName + "  where  db_id =" + "'" + d_id + "'";
        //String sql = "select * from product_master where CategoryId = "+"'"+categoryid +"'"+" and EANCode = "+"'"+eancode+"'"+" and db_id ="+"'"+d_id+"'";
        //String sql = "select * from product_master";

        Cursor c = database.rawQuery(sql, null);

        return c;
    }

    public Cursor getuniquedata1(String categoryid, String eancode, String d_id, String chk_date) {
        // TODO Auto-generated method stub

        String sql = "select * from stock where " + " db_id =" + "'" + d_id + "'";


        Cursor c = database.rawQuery(sql, null);

        return c;
    }

    public Cursor getuniquedata2(String d_id) {
        // TODO Auto-generated method stub

        String sql = null;
        try {
            sql = "select * from stock where " + " db_id =" + "'" + d_id + "'";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return database.rawQuery(sql, null);
    }

    public Cursor getuniquedataAttendance(String empid, String adate) {
        // TODO Auto-generated method stub

        String sql = null;
        try {
            sql = "select * from attendance where emp_id = " + "'" + empid + "'" + " AND Adate like " + "'%" + adate + "%'";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return database.rawQuery(sql, null);
    }

    public Cursor getuniquedata_tester(String categoryid, String eancode, String d_id) {
        // TODO Auto-generated method stub
        Log.v("", "check id available or not frm stock");
        String sql = "select * from tester where  db_id =" + "'" + d_id + "'";
        //String sql = "select * from tester where product_id = "+"'"+categoryid +"'"+" and eancode = "+"'"+eancode+"'"+" and db_id ="+"'"+d_id+"'";

        Cursor c = database.rawQuery(sql, null);

        return c;
    }

    public void UpdateStock(//String product_id,
                            String product_category,
                            String product_type1, String product_name, String emp_id,
                            String op_stk, String cl_stk, String fresh_stock1,
                            String ttl_amount, String sold_stk, String price, String size,
                            String eancode, String db_id1, String cat_id,
                            String insert_timestamp, String disc, String ttnamt, String p_balance, String new_retn, String new_rtn_n_sold) {

        // TODO Auto-generated method stub


        Log.e("", "inside update stock");

        String sql = "update stock set close_bal = " + "'" + cl_stk + "'" + ", stock_in_hand = " + "'" + op_stk + "'" +
                " ,return_saleable = " + "'" + new_retn + "'" + " ,return_non_saleable = " + "'" + new_rtn_n_sold + "'" +
                ",stock_received = " + "'" + fresh_stock1 + "'" + ",sold_stock = " + "'" + sold_stk + "'" + ",total_gross_amount = " + "'" + ttl_amount + "'" +
                ",total_net_amount = " + "'" + ttnamt + "'" + ",discount = " + "'" + disc + "'" + ",opening_stock = " + "'" + p_balance + "'" +
                ",insert_date = " + "'" + insert_timestamp + "'" + ", savedServer='0' where db_id = " + "'" + db_id1 + "'" + "";
        Log.v("", "update stock sql==" + sql);
        database.execSQL(sql);

    }


    public void UpdateTester(//String product_id,
                             String product_category,
                             String product_type1, String product_name, String emp_id,


                             String size,
                             String eancode, String db_id1, String cat_id, String product_status) {
        // TODO Auto-generated method stub


        Log.e("", "inside update stock");
        String sql = "update tester set product_status= " + "'" + product_status + "'" + " where  and db_id = " + "'" + db_id1 + "'" + "";
        database.execSQL(sql);

    }

    public Cursor getpreviousData(String ystdate, String username) {
        // TODO Auto-generated method stub

        String sql = "select * from attendance where emp_id = " + "'" + username + "'" + " and Adate=" + "'" + ystdate + "'";

        Log.v("", "sql==" + sql);
        Cursor c = database.rawQuery(sql, null);

        return c;
    }

    public String isholiday(String selecteddate) {
        // TODO Auto-generated method stub

        String desc = "";
        String selectquery = "select  *  from attendance where Adate Like '%" + selecteddate + "%'" + " and attendance = 'H'";

        Cursor cursor = database.rawQuery(selectquery, null);
        Log.v("", "currosr.count---" + cursor.getCount());
        if (cursor != null) {

            if (cursor.moveToFirst()) {

                do {
                    desc = cursor.getString(cursor.getColumnIndex("holiday_desc"));

                } while (cursor.moveToNext());

            }


        }

        return desc;

    }

    public String check_holiday_is_present_or_not(String month, String year) {
        // TODO Auto-generated method stub

        String desc = "";
        String selectquery = "select  *  from attendance where year = '" + year + "'" + "and month ='" + month + "'" + " and attendance = 'H'";

        Cursor cursor = database.rawQuery(selectquery, null);
        Log.v("", "currosr.count---" + cursor.getCount());
        if (cursor != null) {

            if (cursor.moveToFirst()) {

                do {
                    desc = cursor.getString(cursor.getColumnIndex("attendance"));

                } while (cursor.moveToNext());

            }


        }

        return desc;

    }

    public Cursor getpreviousData1(String ystdate, String username) {
        // TODO Auto-generated method stub

        String sql = "select * from attendance where emp_id = " + "'" + username + "'" + " and Adate LIKE " + "'%" + ystdate + "%'";

        Log.v("", "sql==" + sql);
        Cursor c = database.rawQuery(sql, null);

        return c;
    }

    public void UpdateTesterStatus(String dbid) {
        // TODO Auto-generated method stub
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String delv_date = sdf.format(c.getTime());


        //String sql = "update tester set savedServer = '0',product_status= 'Available' , delivered_date = "+"'"+ delv_date +"'"+" where product_id = "+"'"+ cat_id +"'"+" and eancode = "+"'"+ eancode +"'"+" and db_id = "+"'"+ db_id1 +"'"+"";
        String sql = "update tester set savedServer = '0',product_status= 'Available' , delivered_date = " + "'" + delv_date + "'" + " where db_id = " + "'" + dbid + "'";        //String sql = "update tester set product_status= 'Availabe' ";
        Log.e("", "inside update status==" + sql);
        database.execSQL(sql);

    }

    //Loging attendance1
    public boolean getpresentdate(String ystdate, String username) {
        // TODO Auto-generated method stub
        boolean a = false;
        String sql = "select * from attendance where emp_id = " + "'" + username + "'" + " and Adate=" + "'" + ystdate + "'";

        Log.v("", "sql==" + sql);
        Cursor c = database.rawQuery(sql, null);

        if (c != null) {
            c.moveToFirst();
            if (c.getCount() > 0) {

                a = true;
            } else {

                a = false;
            }

        }

        return a;
    }

    //Loging attendance check 2
    public String getdatepresentorabsent(String currentdate, String username) {
        // TODO Auto-generated method stub
        Log.v("", "in finding p or a");
        String a = "";
        String sql = "select attendance from attendance where emp_id = " + "'" + username + "'" + " and Adate LIKE " + "'%" + currentdate + "%'";

        Log.v("", "sql==" + sql);
        Cursor c = database.rawQuery(sql, null);

        if (c != null) {
            c.moveToFirst();
            if (c.getCount() > 0) {

                a = c.getString(c.getColumnIndex("attendance"));
            }
        }


        return a;
    }

    public Cursor fetchAllproductslist(String selected_category,
                                       String selected_type) {
        // TODO Auto-generated method stub
        String selectquery = "select * from product_master where ProductCategory = " + "'" + selected_category + "'" + " AND ProductType = " + "'" + selected_type + "'";
        Log.e("", "selectquery==" + selectquery);
        Cursor cursort = database.rawQuery(selectquery, null);
        Log.e("", "fetchAllproductslist");

        return cursort;

    }


    public Cursor fetchAllproductslistforstockforsale(String selected_category,
                                                      String selected_type, String flag, String product_name) {
        // TODO Auto-generated method stub

//	String selectquery = "select * from product_master where ProductCategory = "+"'"+ selected_category+"'"+" AND ProductType = "+"'"+selected_type+"' AND ProductName = '"+product_name+"'  AND db_id in (select db_id  from stock where close_bal > 0 and product_name = '"+product_name+"') ORDER BY order_flag ";

        String selectquery = "select * from stock where product_category = " + "'" + selected_category + "'" + " AND product_type = " + "'" + selected_type + "' AND product_name = '" + product_name + "'  AND  close_bal > 0";

        Log.e("selectquery", "==" + selectquery);
        Cursor cursort = database.rawQuery(selectquery, null);


        return cursort;

    }

    public Cursor fetchAllproductslistforstock(String selected_category,
                                               String selected_type, String flag,String columnname) {
        // TODO Auto-generated method stub
//		String selectquery = "select * from product_master where ProductCategory = "+"'"+ selected_category+"'"+" AND ProductType = "+"'"+selected_type+"' ORDER BY order_flag ";// ORDER BY order_flag +" AND price_type = "+"'"+flag+"'";

        String selectquery;
        if(columnname.equalsIgnoreCase("ShadeNo")
                || columnname.equalsIgnoreCase("")) {
            selectquery = "select distinct(ShadeNo),ProductName from product_master where ProductCategory = " + "'" + selected_category + "'" + " AND ProductType = " + "'" + selected_type + "' ORDER BY order_flag ";// ORDER BY order_flag +" AND price_type = "+"'"+flag+"'";
        }else{
            selectquery = "select distinct(CategoryId),ProductName from product_master where ProductCategory = " + "'" + selected_category + "'" + " AND ProductType = " + "'" + selected_type + "' ORDER BY order_flag ";// ORDER BY order_flag +" AND price_type = "+"'"+flag+"'";

        }

        //String selectquery = "select * from product_master where ProductCategory = "+"'"+ selected_category+"'"+" AND ProductType = "+"'"+selected_type+"'"+" AND price_type = "+"'"+flag+"'  AND db_id in (select db_id  from stock where close_bal > 0)";
//		Log.e("selectquery distinct","=="+selectquery);
        Cursor cursort = database.rawQuery(selectquery, null);


        return cursort;

    }

    public Cursor fetchAllproductslistforstock1(String selected_category,
                                                String selected_type, String flag) {
        // TODO Auto-generated method stub
//		String selectquery = "select * from product_master where ProductCategory = "+"'"+ selected_category+"'"+" AND ProductType = "+"'"+selected_type+"' ORDER BY order_flag ";// ORDER BY order_flag +" AND price_type = "+"'"+flag+"'";

        String selectquery = "select distinct(product_name) ProductName from stock where product_category = " + "'" + selected_category + "'" + " AND product_type = " + "'" + selected_type + "' ORDER BY flag ";// ORDER BY order_flag +" AND price_type = "+"'"+flag+"'";


        //String selectquery = "select * from product_master where ProductCategory = "+"'"+ selected_category+"'"+" AND ProductType = "+"'"+selected_type+"'"+" AND price_type = "+"'"+flag+"'  AND db_id in (select db_id  from stock where close_bal > 0)";
//		Log.e("selectquery distinct","=="+selectquery);
        Cursor cursort = database.rawQuery(selectquery, null);


        return cursort;

    }


    public int checkcount(String username, String pass) {
        // TODO Auto-generated method stub
        Cursor c;
        int count = 0;

        String sql = "select username,password from login where username = '" + username + "' and password ='" + pass + "'";
        c = database.rawQuery(sql, null);
        if (c != null && c.moveToFirst()) {
            count = c.getCount();
        }
        return count;
    }

    public int checkcount123() {
        // TODO Auto-generated method stub
        Cursor c;
        int count = 0;

        String sql = "select * from login ";
        c = database.rawQuery(sql, null);
        if (c != null && c.moveToFirst()) {
            count = c.getCount();
        }
        return count;
    }

    public void insertLogin(String username, String password,
                            String android_uid, String last_modified_date, String flag, String div,
                            String status, String bdename, String bdecode,String role) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();

        if (flag.equalsIgnoreCase("s")) {

            Log.e("vlaue inserting into login==", "");

            values.put("bde_Name", bdename);
            values.put("bde_Code", bdecode);

            values.put("last_modified_date", last_modified_date);
            values.put("div", div);


            Log.e("", "values==" + values);

            database.update("login", values, "username = '" + username + "'", null);
        } else {

            Log.e("vlaue inserting into login==", "");

            values.put("username", username);
            values.put("password", password);
            values.put("android_uid", android_uid);
            values.put("created_date", last_modified_date);
            values.put("last_modified_date", last_modified_date);
            values.put("status", status);
            values.put("div", div);
            values.put("bde_Name", bdename);
            values.put("bde_Code", bdecode);
            values.put("Role", role);

            database.insert("login", null, values);
            Log.e("", "values==" + values);
        }


        database.close();

    }

    public void insertOutlet(String baCodeOutlet,String banameOutlet,String outletname,String flotername) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();

            values.put("baCodeOutlet", baCodeOutlet);
            values.put("banameOutlet", banameOutlet);
            values.put("outletname", outletname);
            values.put("flotername", flotername);


            database.insert("floteroutlet", null, values);
            Log.e("", "values==" + values);

        database.close();

    }

    public int checkLoing(String username, String password) {
        // TODO Auto-generated method stub

        Cursor c;
        int count = 0;

        String sql = "select * from login where username = '" + username + "' and  password= '" + password + "'";
        c = database.rawQuery(sql, null);
        if (c != null && c.moveToFirst()) {
            count = c.getCount();
        }

        return count;
    }

    public void updateChangepassword(String username, String password) {
        // TODO Auto-generated method stub

        String sql = "update login set password = '" + password + "' where username='" + username + "'";
        Log.v("", "sql=" + sql);

        database.execSQL(sql);

        // and password ='password'


    }

    public String check_password_from_db(String username, String str_old_password) {
        // TODO Auto-generated method stub

        Cursor c;
        String count = "0";

        String sql = "select * from login where username = '" + username + "' and  password= '" + str_old_password + "'";
        c = database.rawQuery(sql, null);
        if (c != null)
            if (c.getCount() > 0 && c.moveToFirst()) {


                count = "1";

            }

        return count;
    }

   /* public String check_bacode_outlet(String username, String baCodeOutlet) {
        // TODO Auto-generated method stub

        Cursor c;
        String count = "0";

        String sql = "select * from floteroutlet where flotername = '" + username + "' and  baCodeOutlet= '" + baCodeOutlet + "'";
        c = database.rawQuery(sql, null);
        if (c != null)
            if (c.getCount() > 0 && c.moveToFirst()) {


                count = "1";

            }

        return count;
    }*/

    public Cursor check_exist_bacode_outlet(String username, String baCodeOutlet) {
        // TODO Auto-generated method stub
        Log.v("", "check id available or not frm product_master");
        String sql = "select * from floteroutlet where flotername = '" + username + "' and  baCodeOutlet= '" + baCodeOutlet + "'";

        Log.v("", "sql==" + sql);

        Cursor c = database.rawQuery(sql, null);

        return c;
    }

    public void insertProductMasterFirsttime(String db_stock_id, String id, String productId,
                                             String catCodeId, String eANCode, String empId,
                                             String productCategory, String productType, String productName,
                                             String opening_Stock, String freshStock, String stock_inhand,
                                             String soldStock, String s_Return_NonSaleable,
                                             String s_Return_Saleable, String closingBal, String grossAmount,
                                             String discount, String netAmount, String size, String price,
                                             String lMD, String androidCreatedDate, String month, String year) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();
        // for (int i = 0; i < count1; i++) {

        Log.e("vlaue inserting into stock==", "");

        values.put("product_category", productCategory);
        values.put("product_type", productType);
        values.put("product_name", productName);
        values.put("db_stock_id", db_stock_id);
        values.put("db_id", productId);

        values.put("product_id", catCodeId);
        //values.put("Category", productCategory);
        //values.put("ShadeNo", shadeno);
        values.put("eancode", eANCode);
        values.put("size", size);
        values.put("price", price);

        values.put("opening_stock", opening_Stock);

        values.put("stock_received", freshStock);

        values.put("stock_in_hand", stock_inhand);

        values.put("sold_stock", soldStock);

        values.put("return_non_saleable", s_Return_NonSaleable);
        values.put("return_saleable", s_Return_Saleable);
        values.put("close_bal", closingBal);
        values.put("total_gross_amount", grossAmount);
        values.put("discount", discount);
        values.put("emp_id", empId);
        values.put("total_net_amount", netAmount);
        values.put("last_modified_date", lMD);
        values.put("insert_date", androidCreatedDate);
        values.put("updateDate", androidCreatedDate);
        values.put("savedServer", "1");
        values.put("month", month);
        values.put("year", year);

		/*values.put("MasterPackQty", masterpackqty);
		values.put("MonoPackQty", monopackqty);
		values.put("InnerQty", innerqty);
		values.put("SKU_L", sku_l);
		values.put("SKU_B", sku_b);
		values.put("SKU_H", sku_h);
		*///values.put("LMD", lmd);
        Log.e("", "values==" + values);

        database.insert("stock", null, values);

        // }
        // getStockdetails();
        database.close();


    }

    public void insertSyncLog(String Exception, String LineNo, String methodname,
                              String createddate, String lastmodifieddate, String username,
                              String SyncMethod, String result) {
        // TODO Auto-generated method stub
        try {
            String values[] = {Exception, LineNo, methodname, createddate, lastmodifieddate, username, SyncMethod, result};
            String names[] = {"EXCEPTION", "LINE_NO", "METHOD", "CREATED_DATE", "LASTMODIFIED_DATE", "USERNAME", "SYNCMETHOD", "RESULT"};

            ContentValues insertvalues = createContentValues(values, names);


            database.insert("SYNC_LOG", null, insertvalues);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<HashMap<String, String>> GETERRORLOGS() {

        ArrayList<HashMap<String, String>> liveLIST = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map = null;

        try {
            String sql = "SELECT * from SYNC_LOG WHERE FLAG IS NULL";
            Cursor c = database.rawQuery(sql, null);

            if (c != null) {
                if (c.moveToFirst() && c.getCount() > 0) {

                    do {
                        map = new HashMap<String, String>();


                        // map.put("ID",c.getString(c.getColumnIndex("ID")));
                        map.put("ID", c.getString(c.getColumnIndex("ID")));
                        map.put("EXCEPTION", c.getString(c.getColumnIndex("EXCEPTION")));
                        map.put("LINE_NO", c.getString(c.getColumnIndex("LINE_NO")));
                        map.put("METHOD", c.getString(c.getColumnIndex("METHOD")));
                        map.put("USERNAME", c.getString(c.getColumnIndex("USERNAME")));
                        map.put("SYNCMETHOD", c.getString(c.getColumnIndex("SYNCMETHOD")));
                        map.put("RESULT", c.getString(c.getColumnIndex("RESULT")));
                        map.put("CREATED_DATE", c.getString(c.getColumnIndex("CREATED_DATE")));

                        liveLIST.add(map);
                    } while (c.moveToNext());
                    c.close();
                }
            } else {
//				map.put("", "");
            }

        } catch (SQLException mSQLException) {
            Log.e("", " Errorlogdetails  >>" + mSQLException.toString());
            throw mSQLException;
        }

        Log.v("", "liveLIST=" + liveLIST);

        return liveLIST;

    }

    //
    public void updatevalues(String tablename, ContentValues cv,
                             String whereColName, String whereColValue) {
        try {
            ContentValues contentvalues = cv;
            database.update(tablename, contentvalues, whereColName + "='"
                    + whereColValue + "'", null);
            Log.e("", "Update error log flag u");
            System.out.println("Update Succesfull");
        } catch (Exception e) {
            // TODO: handle exception
            e.toString();
            e.printStackTrace();
            System.out.println("Fail To Update");
        }

    }

    public boolean getUserAvailabe(String userId) {
        boolean result = false;
        try {
            String sql1 = "select username from login where username = '"
                    + userId + "';";
            Cursor c1 = null;
            Log.v("", "sql1=" + sql1);

            Log.v("", "sfsfsfssfsfsfsdfsdfsfs");

            c1 = database.rawQuery(sql1, null);


            Log.v("", "c1.getCount()=" + c1.getCount());

            if (c1 != null) {
                if (c1.moveToFirst() && c1.getCount() > 0) {
                    Log.v("", "true123");
                    result = true;
                    c1.close();
                } else {
                    result = false;
                }
            }
        } catch (Exception e) {
//				strCustTypeID = "";
        }


        return result;

    }

    // prashant
    //public void insertValues(ContentValues cv, String tablename) {
    public void insertValues(String username, String pass, String deviceId, String status, String Createddate, String div) {
        try {


            ContentValues values = new ContentValues();


            values.put("username", username);
            values.put("password", pass);
            values.put("android_uid", deviceId);
            values.put("created_date", Createddate);
            values.put("div", div);
            values.put("status", status);
            values.put("Lid", "1");

            Log.e("", "values==" + values);

            database.insert("login", null, values);


        } catch (Exception e) {

            System.out.println(e.toString());

        }

    }

    public Cursor getuniquedata_stock(String categoryid, String eancode, String d_id) {
        // TODO Auto-generated method stub
        Log.v("", "check id available or not frm product_master");
        String sql = "select * from stock where  db_id =" + "'" + d_id + "'";

        Log.v("", "sql==" + sql);

        Cursor c = database.rawQuery(sql, null);

        return c;
    }

    public void updateflag_disable_button() {
        // TODO Auto-generated method stub

        String sql = "update login set flag = 'D'";
        database.execSQL(sql);

    }

    public String check_flag_button_disable() {
        // TODO Auto-generated method stub
        String chk = "";
        String sql = "Select * from login where flag = 'D'";

        Cursor c = database.rawQuery(sql, null);

        if (c != null) {

            if (c.moveToFirst() && c.getCount() > 0) {

                chk = "D";
            }
        }

        return chk;
    }

    public Cursor CheckDataExist(String tablename, String db_id, String Product_Cat, String Product_type, String Product_name) {
        // TODO Auto-generated method stub
        Log.v("", "check id available or not frm product_master");
        String sql = "select * from  '" + tablename + "'  where  db_id =" + "'" + db_id + "' ";
        //	+" and product_category= '"+Product_Cat+"' and product_type= '"+Product_type+"' and product_name= '"+Product_name+"'";

        Log.v("", "sql==" + sql);

        Cursor c = database.rawQuery(sql, null);

        return c;
    }

    public void UpdateStockSync(//String product_id,
                                String product_category,
                                String product_type1,
                                String product_name,
                                String emp_id,
                                String op_stk,
                                String cl_stk,
                                String fresh_stock1,
                                String ttl_amount,
                                String sold_stk,
                                String price,
                                String size,
                                String db_id1,
                                String update_timestamp,
                                String disc,
                                String ttnamt,
                                String p_balance,
                                String new_retn,
                                String new_rtn_n_sold) {

        // TODO Auto-generated method stub


        Log.e("", "inside update stock");

        String sql = "update stock set close_bal = " + "'" + cl_stk + "'" + ", stock_in_hand = " + "'" + op_stk + "'" +
                " ,return_saleable = " + "'" + new_retn + "'" + " ,return_non_saleable = " + "'" + new_rtn_n_sold + "'" +
                ",stock_received = " + "'" + fresh_stock1 + "'" + ",sold_stock = " + "'" + sold_stk + "'" + ",total_gross_amount = " + "'" + ttl_amount + "'" +
                ",total_net_amount = " + "'" + ttnamt + "'" + ",discount = " + "'" + disc + "'" + ",opening_stock = " + "'" + p_balance + "'" +
                ",last_modified_date = " + "'" + update_timestamp + "'" + ", savedServer='1' where db_id = " + "'" + db_id1 + "'" + "";
        Log.v("", "update stock sql==" + sql);
        database.execSQL(sql);

    }

    public void UpdateStockSync1(//String product_id,
                                String product_category,
                                String product_type1,
                                String product_name,
                                String emp_id,
                                String op_stk,
                                 String stk_hand,
                                String cl_stk,
                                String fresh_stock1,
                                String ttl_amount,
                                String sold_stk,
                                String price,
                                String size,
                                String db_id1,
                                String update_timestamp,
                                String disc,
                                String ttnamt,
                                String new_retn,
                                String new_rtn_n_sold) {

        // TODO Auto-generated method stub


        Log.e("", "inside update stock");

        String sql = "update stock set close_bal = " + "'" + cl_stk + "'" + " ,opening_stock = " + "'" + op_stk + "'" + ", stock_in_hand = " + "'" + stk_hand + "'" +
                " ,return_saleable = " + "'" + new_retn + "'" + " ,return_non_saleable = " + "'" + new_rtn_n_sold + "'" +
                ",stock_received = " + "'" + fresh_stock1 + "'" + ",sold_stock = " + "'" + sold_stk + "'" + ",total_gross_amount = " + "'" + ttl_amount + "'" +
                ",total_net_amount = " + "'" + ttnamt + "'" + ",discount = " + "'" + disc + "'" +
                ",last_modified_date = " + "'" + update_timestamp + "'" +
                ", savedServer='1' where db_id = " + "'" + db_id1 + "'" + "";
        Log.v("", "update stock sql==" + sql);
        database.execSQL(sql);

    }

    public String getUserId(String user) {
        // TODO Auto-generated method stub

        Cursor c;
        String username = "no";

        String sql = "select username from login where username = '"+user+"'";
        c = database.rawQuery(sql, null);
        if (c != null)
            Log.e("", "getCount=" + c.getCount());
        if (c.getCount() > 0) {
            Log.e("", "log1u");
            c.moveToFirst();
            Log.e("", "log1u");


            username = c.getString(c.getColumnIndex("username"));
            Log.v("", "already locally login id=" + username);

        }

        return username;
    }

    public String getLastLogintime() {
        // TODO Auto-generated method stub

        Cursor c;
        String LastLoginTime = "";

        //String sql = "select last_modified_date from login";

        String sql = "select created_date from login"; //15.05.2015

        c = database.rawQuery(sql, null);

        if (c != null) {
            Log.e("", "getCount=" + c.getCount());
            if (c.getCount() > 0) {

                c.moveToFirst();


                LastLoginTime = c.getString(c.getColumnIndex("created_date"));

                Log.v("", "last login time=" + LastLoginTime);

            }
        } else {

            LastLoginTime = "";
        }
        return LastLoginTime;
    }

    public void updateimageTable(String visibilityId, String imageName) {
        // TODO Auto-generated method stub

        String sql = "UPDATE image SET savedServer = '0' WHERE scannedId = '" + visibilityId.trim() + "' AND image_name= '" + imageName.trim() + "'";

        Log.e("local database", "UPDATE image query=" + sql);
        database.execSQL(sql);
        Log.e("local database", "UPDATE image table data");


    }

    public ArrayList<HashMap<String, String>> getimagesNOtUploaded() {

        ArrayList<HashMap<String, String>> liveLIST = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map = null;

        try {
            String sql = "SELECT * from image WHERE  savedServer = '0'";
            Cursor c = database.rawQuery(sql, null);

            if (c != null) {
                if (c.moveToFirst() && c.getCount() > 0) {

                    do {
                        map = new HashMap<String, String>();


                        // map.put("ID",c.getString(c.getColumnIndex("ID")));
                        map.put("image_name", c.getString(c.getColumnIndex("image_name")));
                        map.put("imagePath", c.getString(c.getColumnIndex("imagePath")));
                        map.put("imageId", c.getString(c.getColumnIndex("imageId")));

                        liveLIST.add(map);
                    } while (c.moveToNext());
                    c.close();
                }
            } else {
//					map.put("", "");
            }

        } catch (SQLException mSQLException) {
            Log.e("", " Errorlogdetails  >>" + mSQLException.toString());
            throw mSQLException;
        }

        Log.v("", "liveLIST=" + liveLIST);

        return liveLIST;

    }

    public void update_visibility_id(String visibility_id, String scan_id) {

        String sql = "UPDATE image SET server_visiblity_id= '" + visibility_id + "' where scannedId= '" + scan_id + "'";
        Log.e("local database", "sql=" + sql);
        database.execSQL(sql);
        Log.e("local database", "UPDATE image table data with server visibility id");
    }


    public Cursor getimageDetails1() {
        // TODO Auto-generated method stub
        Cursor imagedetails1 = null;
        try {

            String sql = "select * from image where savedServer = '0'";

            imagedetails1 = database.rawQuery(sql, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagedetails1;

    }

    public void insertTesterDownloadedData(String db_tester_id, String db_id,
                                           String catCodeId, String eANCode, String empId,
                                           String productCategory, String productType, String productName,
                                           String requested_date, String delivered_date, String ShadeNo, String current_status,
                                           String size
    ) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();


        Log.e("vlaue inserting into tester data download==", "");

        values.put("product_category", productCategory);
        values.put("product_type", productType);
        values.put("product_name", productName);
        values.put("db_tester_id", db_tester_id);
        values.put("db_id", db_id);

        values.put("product_id", catCodeId);
        values.put("cat_id", catCodeId);

        values.put("eancode", eANCode);
        values.put("size", size);


        values.put("product_status", current_status);
        values.put("requried_date", requested_date);
        values.put("delivered_date", delivered_date);
        values.put("emp_id", empId);


        values.put("savedServer", "1");

        Log.e("", "values==" + values);

        database.insert("tester", null, values);

        // }
        // getStockdetails();
        database.close();


    }

    public void UpdateTesterSync(String db_Id,
                                 String product_category,
                                 String product_type,
                                 String product_name,
                                 String emp_id,
                                 String size,
                                 String requested_date,
                                 String delivered_date,
                                 String current_status,
                                 String ShadeNo
    ) {

        // TODO Auto-generated method stub


        Log.e("", "inside update tester data download");

        String sql = "update tester set product_category = " + "'" + product_category + "'" + ", product_type = " + "'" + product_type + "'" +
                " ,product_name = " + "'" + product_name + "'" + " ,emp_id = " + "'" + emp_id + "'" +
                ",size  = " + "'" + size + "'" + ",requried_date = " + "'" + requested_date + "'" + ",delivered_date = " + "'" + delivered_date + "'" +
                ",product_status = " + "'" + current_status + "'" + ",shadeno  = " + "'" + ShadeNo + "'" +
                ", savedServer='1' where db_id = " + "'" + db_Id + "'" + "";
        Log.v("", "update tester downoload sql==" + sql);
        database.execSQL(sql);

    }


//---------------------------------------------------------------------stock activity-------------------------------------------------//


    public void Insertstock_new(
            String product_cat,
            String product_type, String product_name, String emp_id,
            String stockinhand, String cl_stk, String fresher_stock,
            String price, String size, String eancode, String db_id1, String cat_id,
            String date,String soldstock,
            String return_saleable, String return_non_saleable, String i_netamt, String netamt,String discount,
            String shadno,String updateDate, String month_name, String year_name) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();

        values.put("product_category", product_cat);
        values.put("product_type", product_type);
        values.put("product_name", product_name);

        values.put("emp_id", emp_id);
        values.put("stock_in_hand", stockinhand);
        values.put("close_bal", cl_stk);

        values.put("savedServer", "0");
        values.put("price", price);
        values.put("size", size);
        values.put("db_id", db_id1);
        values.put("product_id", cat_id);
        values.put("eancode", eancode);


        values.put("stock_received", fresher_stock);
        values.put("sold_stock", soldstock);
        values.put("return_non_saleable", return_non_saleable);
        values.put("return_saleable", return_saleable);
        values.put("total_gross_amount", i_netamt);
        values.put("total_net_amount", netamt);
        values.put("discount", discount);
        values.put("insert_date", date);

        values.put("shadeNo", shadno);
        values.put("updateDate", updateDate);
        values.put("month", month_name);
        values.put("year", year_name);
        values.put("flag", "s");
        Log.e("InsertStock", values.toString());

        database.insert("stock", null, values);

        //}
        // getStockdetails();
        database.close();
    }

    public void UpdateStock_new(String product_cat,
                                String product_type, String product_name,
                                String emp_id,
                                String stockinhand,
                                String cl_stk,
                                String fresher_stock,
                                String price, String size, String eancode, String db_id1, String cat_id,
                                String date,String soldstock,
                                String return_saleable, String return_non_saleable, String i_netamt,String netamt,
                                String discount,String shadno, String updateDate, String month_name, String year_name) {

        // TODO Auto-generated method stub


        Log.e("", "inside update stock");


        String sql = "update stock set  stock_in_hand = " + "'" + stockinhand + "'" +
                " ,sold_stock = " + "'" + soldstock + "'" +
                " ,return_saleable = " + "'" + return_saleable + "'" +
                " ,return_non_saleable = " + "'" + return_non_saleable + "'" +
                " ,total_gross_amount = " + "'" + i_netamt + "'" +
                " ,total_net_amount = " + "'" + netamt + "'" +
                " ,discount = " + "'" + discount + "'" +
                ",stock_received = " + "'" + fresher_stock + "'" + "," +
                "close_bal = " + "'" + cl_stk + "'" + "," +
                "shadeNo = " + "'" + shadno + "'" + "," +
                "insert_date = " + "'" + date + "'" +
                ", flag = " + "'s'" +
                ", savedServer='0',updateDate ='" + updateDate + "' ,month='" + month_name + "', year='" + year_name + "' where db_id = " + "'" + db_id1 + "'" + "";
        Log.e("", "update stock sql==" + sql);
        database.execSQL(sql);

    }

    public String getproductname123(String ppid) {
        // TODO Auto-generated method stub
        Cursor c = null;

        String sql = "select * from product_master where db_id = '" + ppid + "'";

        c = database.rawQuery(sql, null);
        String sss = "";
        if (c != null) {

            if (c.getCount() > 0) {
                c.moveToFirst();
                sss = c.getString(c.getColumnIndex("ProductName"));
                Log.v("", "product_name=" + sss);
            } else {


            }

        }


        return sss;
    }

    public String getproductType123(String ppid) {
        // TODO Auto-generated method stub
        Cursor c = null;

        String sql = "select * from product_master where db_id = '" + ppid + "'";

        c = database.rawQuery(sql, null);
        String sss = "";
        if (c != null) {

            if (c.getCount() > 0) {
                c.moveToFirst();
                sss = c.getString(c.getColumnIndex("ProductType"));

            } else {


            }

        }


        return sss;
    }

    public String checkSoldOrNot(String dbid) {
        // TODO Auto-generated method stub
        String a = "0";
        String sql = "select sold_stock from stock where db_id = '" + dbid + "'";

        Log.e("", "sql=" + sql);

        Cursor c = null;
        c = database.rawQuery(sql, null);

        if (c != null) {

            if (c.getCount() > 0) {
                c.moveToFirst();
                //
                a = c.getString(c.getColumnIndex("sold_stock"));

            } else {

                a = "0";
            }

        }


        return a;
    }

    public boolean fetchmonthyear(String Month, String year) {
        Boolean result = false;

        String sql = "select * from stock_monthwise where month_name = '" + Month + "' and year = '" + year + "'";

        Cursor c = database.rawQuery(sql, null);
        if (c != null && c.getCount() > 0) {
            result = false;
        } else {
            result = true;
        }

        Log.e("fetchmonthyear", String.valueOf(result));
        return result;

    }

    //---------------------------------------------------------------------------- dashboard-------------------------------------------------------------------//
    public void Insertstock_new_BOC(
            String product_cat,
            String product_type, String product_name, String emp_id,
            String op_stk/*, String cl_stk*/, String fresher_stock,
				/*String ttl_amount,
				String sld_stk, */
            String price, String size, String eancode, String db_id1, String cat_id,

            String date,
            //String discount,
			/*String total_net_amt,*/
            String previous_stock,
            String return_saleable,
            String return_non_saleable,
            String shadno, String updateDate, String month_name, String year_name, String check_timestamp, String boc) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();


        Log.e("vlaue inserting into stock==", "");


        values.put("product_category", product_cat);
        values.put("product_type", product_type);
        values.put("product_name", product_name);
        Log.e("vlaue inserting into stock==", "emp_id==" + emp_id);
        values.put("emp_id", emp_id);


        values.put("stock_in_hand", op_stk);
//			values.put("close_bal", cl_stk);

//			values.put("sold_stock", sld_stk);
//
//			values.put("total_gross_amount", ttl_amount);
        values.put("savedServer", "0");
        values.put("price", price);
        values.put("size", size);
        values.put("db_id", db_id1);
        values.put("product_id", cat_id);
        values.put("eancode", eancode);


        values.put("opening_stock", previous_stock);
        values.put("stock_received", fresher_stock);

        values.put("return_non_saleable", return_non_saleable);
        values.put("return_saleable", return_saleable);

//			values.put("total_net_amount", total_net_amt);
        //values.put("discount", discount);

        values.put("insert_date", date);

        values.put("shadeNo", shadno);
        values.put("updateDate", updateDate);
        values.put("month", month_name);
        values.put("year", year_name);
        values.put("date", check_timestamp);
        //values.put("boc", boc);

        Log.e("", "values==" + values);

        database.insert("boc_wise_stock", null, values);

        //}
        // getStockdetails();
        database.close();
    }

    public void UpdateStock_new_BOC(//String product_id,
                                    String product_category,
                                    String product_type1, String product_name, String emp_id,
                                    String op_stk/*, String cl_stk*/, String fresh_stock1,
				/*String ttl_amount, String sold_stk,*/
                                    String price, String size,
                                    String eancode, String db_id1, String cat_id,
                                    String insert_timestamp,//String disc,
				/*String ttnamt,*/
                                    String p_balance, String new_retn, String new_rtn_n_sold,
                                    String updateDate, String month_name, String year_name, String check_timestamp, String boc) {

        // TODO Auto-generated method stub


        Log.e("", "inside update stock");

        String sql = "update boc_wise_stock set stock_in_hand = " + "'" + op_stk + "'" +
                " ,return_saleable = " + "'" + new_retn + "'" + " ,return_non_saleable = " + "'" + new_rtn_n_sold + "'" +
                ",stock_received = " + "'" + fresh_stock1 + "'" + "," +

                "opening_stock = " + "'" + p_balance + "'" +
                ",insert_date = " + "'" + insert_timestamp + "'" +
                ", savedServer='0',updateDate ='" + updateDate + "' ,month='" + month_name +
                "', year='" + year_name + "' " + ",date = '" + check_timestamp + "'" +
                //",date = '"+check_timestamp+"', boc = '"+boc+"' " +
                " where db_id = " + "'" + db_id1 + "'" + "";

        Log.v("", "update stock sql==" + sql);

        database.execSQL(sql);

    }


    public Cursor getuniquedata_for_BOC(String d_id, String chk_date) {
        // TODO Auto-generated method stub

        Log.v("", "check id available or not frm boc_wise_stock ");

        String sql = "select * from boc_wise_stock where db_id =" + "'" + d_id + "' and date = '" + chk_date + "'";

        Log.v("", "getunique boc_wise_stock data==" + sql);

        Cursor c = database.rawQuery(sql, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    public Cursor fetchone_Boc_wise_table(String d_id, String chk_date) {
        // TODO Auto-generated method stub

        Log.v("", "check id available or not frm boc_wise_stock ");

        String sql = "select sold_stock, total_gross_amount , total_net_amount, discount from boc_wise_stock where db_id =" + "'" + d_id + "' and date = '" + chk_date + "'";

        Log.v("", " boc_wise_stock sql==" + sql);

        Cursor c = database.rawQuery(sql, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }

    public void updateSaleCalcuationForDashboard(String dbid, String date, String[] arr1) {
        try {


            String[] col =

                    new String[]{
                            "close_bal",
                            "total_gross_amount",
                            "discount",
                            "total_net_amount",
                            "sold_stock",
                            "opening_stock",
                            "stock_in_hand",
                            "stock_received",
                            "savedServer",
                            "month", "year", "updateDate", "insert_date", "date", "boc"};


            ContentValues cv = createContentValues(arr1, col);

            String[] whereArgs = new String[]{dbid, date};
            // Log.e("Update SQL", sql);
            long c = database.update("boc_wise_stock", cv, "db_id=? and date=?",
                    whereArgs);
            Log.e("updated", String.valueOf(c));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Cursor fetchone_Boc_wise(String d_id, String chk_date) {
        // TODO Auto-generated method stub

        Log.v("", "check id available or not frm boc_wise_stock ");

        String sql = "select *  from boc_wise_stock where db_id =" + "'" + d_id + "' and date = '" + chk_date + "'";

        Log.v("", " boc_wise_stock sql==" + sql);

        Cursor c = database.rawQuery(sql, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }

    public Cursor getdata_StockForBoc(String d_id) {
        // TODO Auto-generated method stub

        Log.v("", "check id available or not frm boc_wise_stock ");

        String sql = "select  *  from stock where db_id =" + "'" + d_id + "'";

        Log.v("", " getdata_Boc_wise sql==" + sql);

        Cursor c = database.rawQuery(sql, null);


        return c;
    }

    public Cursor getdata_outlet(String flotername) {
        // TODO Auto-generated method stub

        String sql = "select Distinct(banameOutlet), * from floteroutlet where flotername =" + "'" + flotername + "'";

        Cursor c = database.rawQuery(sql, null);


        return c;
    }

    public void insertSaleCalcuationForDashboard(String product_id,
                                                 String db_id, String eancode, String product_category,
                                                 String product_type, String product_name, String size,
                                                 String price, String emp_id, String opening_stock,
                                                 String stock_received, String stock_in_hand,
                                                 String return_saleable, String return_non_saleable,
                                                 String close_bal, String gross,
                                                 String dis, String net, String sold_stock,

                                                 String month_name, String year_name,
                                                 String insert_timestamp, String insert_timestamp2,
                                                 String check_timestamp, String boc, String shadeNo) {
        // TODO Auto-generated method stub
        ContentValues values = new ContentValues();


        Log.e("vlaue inserting into boc_wise_stock==", "");

        values.put("product_id", product_id);
        values.put("db_id", db_id);
        values.put("eancode", eancode);

        values.put("product_category", product_category);
        values.put("product_type", product_type);
        values.put("product_name", product_name);

        values.put("emp_id", emp_id);


        values.put("stock_in_hand", stock_in_hand);
        values.put("close_bal", close_bal);

        values.put("sold_stock", sold_stock);
//
        values.put("total_gross_amount", gross);
        values.put("savedServer", "0");
        values.put("price", price);
        values.put("size", size);


        values.put("opening_stock", opening_stock);
        values.put("stock_received", stock_received);

        values.put("return_non_saleable", return_non_saleable);
        values.put("return_saleable", return_saleable);

        values.put("total_net_amount", net);


        values.put("insert_date", insert_timestamp);

        values.put("shadeNo", shadeNo);
        values.put("updateDate", insert_timestamp2);
        values.put("date", check_timestamp);
        values.put("month", month_name);
        values.put("year", year_name);
        values.put("date", check_timestamp);
        values.put("boc", boc);

        Log.e("", "values==" + values);

        database.insert("boc_wise_stock", null, values);

        //}
        // getStockdetails();
        database.close();
    }

    public ArrayList<String> getBocdates() {

        // TODO Auto-generated method stub
        Log.e("", "chch");

        String selectQuery = "SELECT  BOC12 FROM BOCDate";
        Log.e("", "chch");

        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("", "chch");
        ArrayList<String> data = new ArrayList<String>();
        Log.e("", "chch");
        if (cursor != null) {
            Log.e("", "chch");
            if (cursor.moveToFirst()) {
                Log.e("", "chch");
                do {
                    Log.e("", "chch");
                    // get the data into array,or class variable
                    data.add(cursor.getString(cursor.getColumnIndex("BOC12")));


                } while (cursor.moveToNext());
            }
        } else {

            Toast.makeText(context, "No data available", Toast.LENGTH_LONG);
        }
        database.close();
        return data;

    }


    public ArrayList<HashMap<String, String>> getBocDatadatewise(String boc, String FromDate, String ToDate, String type) {

        ArrayList<HashMap<String, String>> liveLIST = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map = null;
        String sql = "", total = "0";

        try {

            if (type.equalsIgnoreCase("All")) {

//					String sql1="select date from boc_wise_stock where boc='"+boc+"'";
//
//					Cursor c1 = database.rawQuery(sql1, null);
//					if(c1 != null && c1.getCount()>0)
//					{
//						c1.moveToFirst();
//						do
//						{
//							String sql2 ="select total_net_amount,product_category,date from boc_wise_stock where date = '"+c1.getString(c1.getColumnIndex("date"))+"' and boc='"+boc+"' group by product_category";
//
//							Cursor c2 = database.rawQuery(sql2, null);
//							if(c2 != null && c2.getCount() > 0)
//							{
//								c2.moveToFirst();
//								total =String.valueOf( Double.parseDouble(total)+Double.parseDouble(c2.getString(c2.getColumnIndex("total_net_amount"))));
//							}
//
//
//
//						}while(c1.moveToNext());
//					}


                sql = "select sum(total_net_amount) as t ,product_category ,date from boc_wise_stock where  date >= '" + FromDate + "' and date <= '" + ToDate + "' and boc='" + boc + "'   group by product_category,date  order by date desc";//
                Log.e("", "sql=" + sql);

            }
            if (type.equalsIgnoreCase("SKIN")) {

                sql = "select sum(total_net_amount) as t ,product_category ,date from boc_wise_stock where  date >= '" + FromDate + "' and date <= '" + ToDate + "' and boc='" + boc + "' and product_category= '" + type + "'   group by date  order by date desc";//
                Log.e("", "sql=" + sql);

            }

            if (type.equalsIgnoreCase("COLOR")) {

                sql = "select sum(total_net_amount) as t ,product_category ,date from boc_wise_stock where  date >= '" + FromDate + "' and date <= '" + ToDate + "' and boc='" + boc + "' and product_category= '" + type + "'  group by date  order by date desc";//
                Log.e("", "sql=" + sql);

            }
            //String sql = "select sum(total_net_amount) as t ,product_category ,date from boc_wise_stock where  date >= '"+FromDate+"' and date <= '"+ToDate+"' and boc='"+boc+"'   group by product_category,date  order by date";//
            //Log.e("", "sql="+sql);


            Cursor c = database.rawQuery(sql, null);

            if (c != null) {
                if (c.moveToFirst() && c.getCount() > 0) {

                    do {
                        map = new HashMap<String, String>();

                        map.put("Total", c.getString(c.getColumnIndex("t")));
                        map.put("Type", c.getString(c.getColumnIndex("product_category")));
                        map.put("Date", c.getString(c.getColumnIndex("date")));

                        liveLIST.add(map);


                    } while (c.moveToNext());

						/*for(int i=0; i<31 - c.getCount();i++)
						{
							map = new HashMap<String, String>();

							map.put("Total","");
							map.put("Type","");
							map.put("Date","");

							liveLIST.add(map);

						}*/


                    c.close();
                }
            } else {
//					map.put("", "");
            }

            Log.e("MapArray", liveLIST.toString());

        } catch (SQLException mSQLException) {
            Log.e("", " Errorlogdetails  >>" + mSQLException.toString());
            throw mSQLException;
        }

        Log.v("", "liveLIST=" + liveLIST);

        return liveLIST;

    }


//		public String getBocDataTotalmonthwise(String boc,String FromDate,String ToDate,String type) {
//
//
//			String total="";
//
//			String sql="";
//
//			try {
//
//
////				sql="select  sum(total_net_amount) as t from boc_wise_stock where date >= '"+FromDate+"' and date <= '"+ToDate+"' and boc = '"+boc+"'";
//					//sql = "select sum(total_net_amount) as t from boc_wise_stock where  date >= '"+FromDate+"' and date <= '"+ToDate+"' and boc='"+boc+"'   group by product_category,date  order by date";//
//
//
//			sql = 	"Select sum(total_net_amount) as t FROM boc_wise_stock WHERE db_id IN (SELECT db_id FROM boc_wise_stock AS d WHERE date >= '"+FromDate+"' and date <= '"+ToDate+"' and boc='"+boc+"' ORDER BY date DESC LIMIT -1 OFFSET 10)";
//
//
//					Log.e("", "sql="+sql);
//
//				Cursor c = database.rawQuery(sql, null);
//
//				if (c != null) {
//					c.moveToFirst();
//					if(c.getCount() > 0){
//
//
//							total = c.getString(c.getColumnIndex("t"));
//
//					}
//				} else {
//
//				}
//
//
//
//			} catch (SQLException mSQLException) {
//				Log.e("", " Errorlogdetails  >>" + mSQLException.toString());
//				throw mSQLException;
//			}
//
//
//
//			return total;
//
//		}


    public String getBocDataTotalmonthwise(String boc, String FromDate, String ToDate, String type) {


        String total = "0";

        String sql = "";

        try {


//				sql="select  sum(total_net_amount) as t from boc_wise_stock where date >= '"+FromDate+"' and date <= '"+ToDate+"' and boc = '"+boc+"'";
            //sql = "select sum(total_net_amount) as t from boc_wise_stock where  date >= '"+FromDate+"' and date <= '"+ToDate+"' and boc='"+boc+"'   group by product_category,date  order by date";//

//			sql = 	"Select sum(total_net_amount) as t FROM boc_wise_stock WHERE db_id IN (SELECT db_id FROM boc_wise_stock AS d WHERE date >= '"+FromDate+"' and date <= '"+ToDate+"' and boc='"+boc+"' ORDER BY date DESC LIMIT -1 OFFSET 10)";

            String sql1 = "select distinct(db_id) from boc_wise_stock where boc='" + boc + "'";

            Cursor c1 = database.rawQuery(sql1, null);
            if (c1 != null && c1.getCount() > 0) {
                c1.moveToFirst();
                do {
                    String sql2 = "select total_net_amount from boc_wise_stock where db_id = '" + c1.getString(c1.getColumnIndex("db_id")) + "' and boc='" + boc + "' order by date desc limit 1";

                    Cursor c2 = database.rawQuery(sql2, null);
                    if (c2 != null && c2.getCount() > 0) {
                        c2.moveToFirst();
                        total = String.valueOf(Double.parseDouble(total) + Double.parseDouble(c2.getString(c2.getColumnIndex("total_net_amount"))));
                    }


                } while (c1.moveToNext());
            }


//				sql = "select distinct(db_id),total_net_amount from boc_wise_stock where boc='"+boc+"' order by date desc";
//
//					Log.e("", "sql="+sql);
//
//				Cursor c = database.rawQuery(sql, null);
//
//				if (c != null && c.getCount() > 0) {
//					c.moveToFirst();
//					do
//					{
//
//						total =String.valueOf(Double.parseDouble(total) + Double.parseDouble(c.getString(c.getColumnIndex("total_net_amount"))));
//
//					}while(c.moveToNext());
////				}

        } catch (SQLException mSQLException) {
            Log.e("", " Errorlogdetails  >>" + mSQLException.toString());
            throw mSQLException;
        }


        return total;

    }

    public String getBocDataSkinmonthwise(String boc, String FromDate, String ToDate, String type) {


        String SKIN = "0";

        String sql1 = "select distinct(db_id) from boc_wise_stock where boc='" + boc + "' and product_category = 'SKIN'";

        Cursor c1 = database.rawQuery(sql1, null);
        if (c1 != null && c1.getCount() > 0) {
            c1.moveToFirst();
            do {

                String sql2 = "select total_net_amount from boc_wise_stock where db_id = '" + c1.getString(c1.getColumnIndex("db_id")) + "' and boc='" + boc + "' and product_category = 'SKIN' order by date desc limit 1";

                Cursor c2 = database.rawQuery(sql2, null);
                if (c2 != null && c2.getCount() > 0) {
                    c2.moveToFirst();
                    SKIN = String.valueOf(Double.parseDouble(SKIN) + Double.parseDouble(c2.getString(c2.getColumnIndex("total_net_amount"))));
                }


            } while (c1.moveToNext());
        }


//			String sql="";
//
//			try {	sql="select  sum(total_net_amount) as t from boc_wise_stock where date >= '"+FromDate+"' and date <= '"+ToDate+"' and boc='"+boc+"' and product_category = 'SKIN'";
//					//sql = "select sum(total_net_amount) as t from boc_wise_stock where  date >= '"+FromDate+"' and date <= '"+ToDate+"' and boc='"+boc+"'   group by product_category,date  order by date";//
//					Log.e("", "sql="+sql);
//
//				Cursor c = database.rawQuery(sql, null);
//
//				if (c != null) {
//					c.moveToFirst();
//					if(c.getCount() > 0){
//
//
//							SKIN = c.getString(c.getColumnIndex("t"));
//
//					}
//				} else {
//
//				}
//
//
//
//			} catch (SQLException mSQLException) {
//				Log.e("", " Errorlogdetails  >>" + mSQLException.toString());
//				throw mSQLException;
//			}
//
//

        return SKIN;

    }

    public String getBocDataColormonthwise(String boc, String FromDate, String ToDate, String type) {


        String COLOR = "0";


        String sql1 = "select distinct(db_id) from boc_wise_stock where boc='" + boc + "' and product_category = 'COLOR'";

        Cursor c1 = database.rawQuery(sql1, null);
        if (c1 != null && c1.getCount() > 0) {
            c1.moveToFirst();
            do {
                String sql2 = "select total_net_amount from boc_wise_stock where db_id = '" + c1.getString(c1.getColumnIndex("db_id")) + "' and boc='" + boc + "' and product_category = 'COLOR' order by date desc limit 1";

                Cursor c2 = database.rawQuery(sql2, null);
                if (c2 != null && c2.getCount() > 0) {
                    c2.moveToFirst();
                    COLOR = String.valueOf(Double.parseDouble(COLOR) + Double.parseDouble(c2.getString(c2.getColumnIndex("total_net_amount"))));
                }


            } while (c1.moveToNext());
        }


//	String sql="";

//	try {	sql="select  sum(total_net_amount) as t from boc_wise_stock where date >= '"+FromDate+"' and date <= '"+ToDate+"' and boc='"+boc+"' and product_category = 'COLOR'";
//			//sql = "select sum(total_net_amount) as t from boc_wise_stock where  date >= '"+FromDate+"' and date <= '"+ToDate+"' and boc='"+boc+"'   group by product_category,date  order by date";//
//			Log.e("", "sql="+sql);
//
//		Cursor c = database.rawQuery(sql, null);
//
//		if (c != null) {
//			c.moveToFirst();
//			if(c.getCount() > 0){
//
//
//					COLOR = c.getString(c.getColumnIndex("t"));
//
//			}
//		} else {
//
//		}
//
//
//
//	} catch (SQLException mSQLException) {
//		Log.e("", " Errorlogdetails  >>" + mSQLException.toString());
//		throw mSQLException;
//	}


        return COLOR;

    }


//----------------------------------------------------------------------end dashboard-------------------------------------------------------------------------------------------------------------//

    //-------------------------------------------testr--------------------------------------------//

    public void insertTesterMaster(String product_category,
                                   String product_type, String product, String server_db_id, String product_id, String categoryid, String category, String shadeno, String eancode,
                                   String size, String mrp, String masterpackqty, String monopackqty, String innerqty, String sku_l, String sku_b,
                                   String sku_h, String price_type) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();


        Log.e("vlaue inserting into tester master==", "");

        values.put("ProductCategory", product_category);
        values.put("ProductType", product_type);
        values.put("ProductName", product);
        values.put("db_id", product_id);
        values.put("server_db_id", server_db_id);
        values.put("CategoryId", categoryid);
        values.put("Category", category);
        values.put("ShadeNo", shadeno);
        values.put("EANCode", eancode);
        values.put("Size", size);
        values.put("MRP", mrp);
        values.put("MasterPackQty", masterpackqty);
        values.put("MonoPackQty", monopackqty);
        values.put("InnerQty", innerqty);
        values.put("SKU_L", sku_l);
        values.put("SKU_B", sku_b);
        values.put("SKU_H", sku_h);
        values.put("price_type", price_type);

        Log.e("", "values==" + values);

        database.insert("tester_master", null, values);


        database.close();

    }

    public ArrayList<String> getproductcategoryoftester() {

        // TODO Auto-generated method stub
        Log.e("", "chch");

        String selectQuery = "SELECT  DISTINCT ProductCategory FROM tester_master";
        Log.e("", "chch");

        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("", "chch");
        ArrayList<String> data = new ArrayList<String>();
        Log.e("", "chch");
        if (cursor != null) {
            Log.e("", "chch");
            if (cursor.moveToFirst()) {
                data.add("Select");
                Log.e("", "chch");
                do {
                    Log.e("", "chch");
                    // get the data into array,or class variable
                    data.add(cursor.getString(cursor.getColumnIndex("ProductCategory")));

                    Log.e("", "data inserted=" + data);
                } while (cursor.moveToNext());
            }
        } else {

            //Toast.makeText(context, "No data available", Toast.LENGTH_LONG);
        }
        database.close();
        return data;

    }

    public ArrayList<String> getproductypeoftester1(String ProductCategory) {
        // TODO Auto-generated method stub
        ArrayList<String> product_type_data = new ArrayList<String>();

        String selectquery = " select DISTINCT ProductType from tester_master where ProductCategory = "

                + "'" + ProductCategory + "'";

        Cursor cursor = database.rawQuery(selectquery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                product_type_data.add("Select");
                do {
                    product_type_data.add(cursor.getString(cursor
                            .getColumnIndex(Dbhelper.KEY_PRODUCT_TYPE)));

                    Log.e("", "data product_type=" + product_type_data);

                } while (cursor.moveToNext());

            }
        } else {

            //Toast.makeText(context, "No data available", Toast.LENGTH_LONG);
        }
        database.close();
        return product_type_data;
    }


    public Cursor fetchAlltesterlist(String selected_category,
                                     String selected_type, String price_type) {
        // TODO Auto-generated method stub
        //String selectquery = "select * from tester_master where ProductCategory = "+"'"+ selected_category+"'"+" AND ProductType = "+"'"+selected_type+"'";
        String selectquery = "select * from tester_master where db_id not in(select db_id from tester where product_status='INDENT') AND ProductType = " + "'" + selected_type + "' AND ProductCategory = " + "'" + selected_category + "'";
        Log.e("", "selectquery==" + selectquery);
        Cursor cursort = database.rawQuery(selectquery, null);
        Log.e("", "fetchAlltesterlist");

        //  AND price_type = "+"'"+price_type+"'

        return cursort;

    }
    //---------------------------------tester end ---------------------------------------------------------//

    public Cursor getBocDateWiseData(String date) {
        // TODO Auto-generated method stub
        Cursor stockddetails1 = null;
        try {


            //String sql = "select * from boc_wise_stock where savedServer = '0' ";
            String sql = "select * from boc_wise_stock where savedServer = '0' and date < '" + date + "'";
            Log.e("", "getBocDateWiseData for upload ==" + sql);

            stockddetails1 = database.rawQuery(sql, null);

            if (stockddetails1 != null) {

                stockddetails1.moveToFirst();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stockddetails1;

    }

    public Cursor CheckDataExistInBOCDayWise(String tablename, String db_id, String date) {
        // TODO Auto-generated method stub
        Log.v("", "check id available or not frm " + tablename);
        String sql = "select * from  '" + tablename + "'  where  db_id =" + "'" + db_id + "' and date= '" + date + "'";
        Log.v("", "sql==" + sql);

        Cursor c = database.rawQuery(sql, null);

        return c;
    }

    public void insertBocDateWise(String db_stock_id, String id, String productId,
                                  String catCodeId, String eANCode, String empId,
                                  String productCategory, String productType, String productName,
                                  String opening_Stock, String freshStock, String stock_inhand,
                                  String soldStock, String s_Return_NonSaleable,
                                  String s_Return_Saleable, String closingBal, String grossAmount,
                                  String discount, String netAmount, String size, String price,
                                  String lMD, String androidCreatedDate, String date, String boc) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();
        // for (int i = 0; i < count1; i++) {

        Log.e("vlaue inserting into boc_wise_stok==", "");

        values.put("product_category", productCategory);
        values.put("product_type", productType);
        values.put("product_name", productName);
        values.put("db_stock_id", db_stock_id);
        values.put("db_id", productId);

        values.put("product_id", catCodeId);
        //values.put("Category", productCategory);
        //values.put("ShadeNo", shadeno);
        values.put("eancode", eANCode);
        values.put("size", size);
        values.put("price", price);

        values.put("opening_stock", opening_Stock);

        values.put("stock_received", freshStock);

        values.put("stock_in_hand", stock_inhand);

        values.put("sold_stock", soldStock);

        values.put("return_non_saleable", s_Return_NonSaleable);
        values.put("return_saleable", s_Return_Saleable);
        values.put("close_bal", closingBal);
        values.put("total_gross_amount", grossAmount);
        values.put("discount", discount);
        values.put("emp_id", empId);
        values.put("total_net_amount", netAmount);
        values.put("last_modified_date", lMD);
        values.put("insert_date", androidCreatedDate);
        values.put("savedServer", "1");
        values.put("date", date);
        values.put("boc", boc);


			/*values.put("MasterPackQty", masterpackqty);
			values.put("MonoPackQty", monopackqty);
			values.put("InnerQty", innerqty);
			values.put("SKU_L", sku_l);
			values.put("SKU_B", sku_b);
			values.put("SKU_H", sku_h);
			*///values.put("LMD", lmd);
        Log.e("", "values==" + values);

        database.insert("boc_wise_stock", null, values);

        // }
        // getStockdetails();
        database.close();


    }

    public void UpdateBOCDAYStockSync(//String product_id,
                                      String product_category,
                                      String product_type1,
                                      String product_name,
                                      String emp_id,
                                      String op_stk,
                                      String cl_stk,
                                      String fresh_stock1,
                                      String ttl_amount,
                                      String sold_stk,
                                      String price,
                                      String size,
                                      String db_id1,
                                      String update_timestamp,
                                      String disc,
                                      String ttnamt,
                                      String p_balance,
                                      String new_retn,
                                      String new_rtn_n_sold,
                                      String Date, String BOC) {

        // TODO Auto-generated method stub


        Log.e("", "inside update Boc_wise_stock");

        String sql = "update boc_wise_stock set close_bal = " + "'" + cl_stk + "'" + ", stock_in_hand = " + "'" + op_stk + "'" +
                " ,return_saleable = " + "'" + new_retn + "'" + " ,return_non_saleable = " + "'" + new_rtn_n_sold + "'" +
                ",stock_received = " + "'" + fresh_stock1 + "'" + ",sold_stock = " + "'" + sold_stk + "'" + ",total_gross_amount = " + "'" + ttl_amount + "'" +
                ",total_net_amount = " + "'" + ttnamt + "'" + ",discount = " + "'" + disc + "'" + ",opening_stock = " + "'" + p_balance + "'" +
                ",last_modified_date = " + "'" + update_timestamp + "'" + ", date = '" + Date + "', boc = '" + BOC + "', savedServer='1' where db_id = " + "'" + db_id1 + "' and date = '" + Date + "' and boc = '" + BOC + "'";
        Log.v("", "update stock sql==" + sql);
        database.execSQL(sql);

    }

    public void insertBocMonthWise(String db_stock_id, String id, String productId,
                                   String catCodeId, String eANCode, String empId,
                                   String productCategory, String productType, String productName,
                                   String opening_Stock, String freshStock, String stock_inhand,
                                   String soldStock, String s_Return_NonSaleable,
                                   String s_Return_Saleable, String closingBal, String grossAmount,
                                   String discount, String netAmount, String size, String price,
                                   String lMD, String androidCreatedDate, String month, String year) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();
        // for (int i = 0; i < count1; i++) {

        Log.e("vlaue inserting into month_wise_stok==", "");

        values.put("product_category", productCategory);
        values.put("product_type", productType);
        values.put("product_name", productName);
        values.put("db_stock_id", db_stock_id);
        values.put("db_id", productId);

        values.put("product_id", catCodeId);
        //values.put("Category", productCategory);
        //values.put("ShadeNo", shadeno);
        values.put("eancode", eANCode);
        values.put("size", size);
        values.put("price", price);

        values.put("opening_stock", opening_Stock);

        values.put("stock_received", freshStock);

        values.put("stock_in_hand", stock_inhand);

        values.put("sold_stock", soldStock);

        values.put("return_non_saleable", s_Return_NonSaleable);
        values.put("return_saleable", s_Return_Saleable);
        values.put("close_bal", closingBal);
        values.put("total_gross_amount", grossAmount);
        values.put("discount", discount);
        values.put("emp_id", empId);
        values.put("total_net_amount", netAmount);
        values.put("last_modified_date", lMD);
        values.put("insert_date", androidCreatedDate);
        values.put("savedServer", "1");
        values.put("month_name", month);
        values.put("year", year);


        Log.e("", "values==" + values);

        database.insert("stock_monthwise", null, values);

        // }
        // getStockdetails();
        database.close();


    }

    public void UpdateProductMaster(String product_category, String product_type,
                                    String product, String d_id, String categoryid,
                                    String category, String shadeno, String eancode, String size,
                                    String mrp, String masterpackqty, String monopackqty,
                                    String innerqty, String sku_l, String sku_b, String sku_h,
                                    String price_type, String order_flag) {

        String sql = " update product_master set  ProductCategory = '" + product_category + "', ProductType = '" + product_type + "', ProductName = '" + product + "', CategoryId = '" + categoryid + "' ," +
                "Category = '" + category + "' , ShadeNo = '" + shadeno + "' , EANCode = '" + eancode + "' , Size = '" + size + "', MRP ='" + mrp + "', MasterPackQty = '" + masterpackqty + "', " +
                "MonoPackQty = '" + monopackqty + "' , InnerQty = '" + innerqty + "' , SKU_L = '" + sku_l + "' , SKU_B ='" + sku_b + "', SKU_H ='" + sku_h + "' , price_type = '" + price_type + "' ,order_flag = '" + order_flag + "'" +
                " Where db_id = '" + d_id + "'";

        Log.v("", "sql=" + sql);
        database.execSQL(sql);
        // TODO Auto-generated method stub

    }


    public void UpdateTesterMaster(String product_category, String product_type,
                                   String product, String server_db_id, String product_id,
                                   String categoryid, String category, String shadeno,
                                   String eancode, String size, String mrp, String masterpackqty,
                                   String monopackqty, String innerqty, String sku_l,
                                   String sku_b, String sku_h, String price_type) {
        // TODO Auto-generated method stub
        String sql = " update tester_master set  ProductCategory = '" + product_category + "', ProductType = '" + product_type + "', ProductName = '" + product + "', CategoryId = '" + categoryid + "' ," +
                "Category = '" + category + "' , ShadeNo = '" + shadeno + "' , EANCode = '" + eancode + "' , Size = '" + size + "', MRP ='" + mrp + "', MasterPackQty = '" + masterpackqty + "', " +
                "MonoPackQty = '" + monopackqty + "' , InnerQty = '" + innerqty + "' , SKU_L = '" + sku_l + "' , SKU_B ='" + sku_b + "', SKU_H ='" + sku_h + "' , price_type = '" + price_type + "' " +
                " Where db_id = '" + product_id + "'";

        Log.v("", "sql=" + sql);
        database.execSQL(sql);

    }

    public Cursor getBocMonthwisedata(String fromDate, String toDate) {
        // TODO Auto-generated method stub

        String sql = "Select db_id,	product_name,size,	price,	opening_stock,	stock_received,	return_saleable," +
                "stock_in_hand,	sold_stock,	close_bal from stock_monthwise where insert_date >= '" + fromDate + "' and insert_date <= '" + toDate + "'";

        Log.e("", "sql=" + sql);

        Cursor c = null;

        c = database.rawQuery(sql, null);

        return c;
    }

    public Cursor SetClosingISOpeningOnlyOnce() {

        Cursor c = null;
        String sql = "select * from stock where flag is null";
        //Log.e("","sql="+sql);

        c = database.rawQuery(sql, null);
        return c;
    }

    public String getPresentdate() {
        // TODO Auto-generated method stub

        Cursor c;
        String LastLoginTime = "";

        //String sql = "select last_modified_date from login";

        String sql = "select created_date from login"; //15.05.2015

        c = database.rawQuery(sql, null);

        if (c != null) {
            Log.e("", "getCount=" + c.getCount());
            if (c.getCount() > 0) {

                c.moveToFirst();


                LastLoginTime = c.getString(c.getColumnIndex("created_date"));

                Log.v("", "last login time=" + LastLoginTime);

            }
        } else {

            LastLoginTime = "";
        }
        return LastLoginTime;
    }

    public int updateAttendance(String wheredate, String BA_id, String logoutDate) {

        int status;

        String sql = "update attendance set logout_date = '" + logoutDate + "' ,logout_status ='OUT' where Adate LIKE '%" + wheredate + "%' and emp_id ='" + BA_id + "'";

        Cursor c = database.rawQuery(sql, null);
        if (c != null && c.getCount() > 0) {
            status = 1;
        } else {
            status = 0;
        }
        return status;


    }

    public boolean CheckSupervisor(String ActualDate, String BA_id) {
        boolean check;


        String sql = "select * from supervisor_attendance where Actual_date LIKE '%" + ActualDate + "%' and BA_id = '" + BA_id + "'";

        Cursor c = database.rawQuery(sql, null);

        if (c != null && c.getCount() > 0) {
            check = true;
        } else {
            check = false;
        }

        Log.e("check", String.valueOf(check));

        return check;


    }


    public HashMap<String, String> getCumulativeData(String date,
                                                     String type) {
        HashMap<String, String> result = new HashMap<String, String>();
        String sql;

        if (type.equals("")) {
            sql = "select sum(sold_stock),sum(total_net_amount) FROM stock where updateDate like '%"
                    + date + "%'";
        } else {
            sql = "select sum(sold_stock),sum(total_net_amount) FROM stock where updateDate like '%"
                    + date + "%' and product_category = '" + type + "'";
        }

        Log.e("sql", sql);

        Cursor c = database.rawQuery(sql, null);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            result.put("SaleUnit", c.getString(0));
            result.put("SaleValue", c.getString(1));
            result.put("Date", date);

        } else {
            result.put("SaleUnit", "0");
            result.put("SaleValue", "0");
            result.put("Date", date);
        }

        return result;

    }

    public boolean checkStockUploaded() {
        boolean result = false;

        try {

//				SELECT * from Table1 where myDate = (select max(myDate) from Table1 WHERE myDate < DATE('now') )

            Cursor c = database.rawQuery("select * from stock where savedServer = 0 and updateDate < DATE('now') ", null);
            if (c != null && c.getCount() > 0) {
                result = true;
            } else {
                result = false;
            }

            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

    public String fetchDbID(String productName, String mrp, String type) {
        //String sql = "select db_id from stock where product_name like '%"+productName+"%' and price = '"+mrp+"'";
        String sql = "select db_id from stock where product_name like '%" + productName + "%' and price = '" + mrp + "' and product_category='" + type + "'";

        Log.e("sql", sql);

        String result = "";

        Cursor c = database.rawQuery(sql, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            result = c.getString(0);


        }

        return result;


    }


    public String fetchStockDbID(String productName, String mrp, String type) {
//			String sql = "select db_id from stock where product_name like '%"+productName+"%' and price = '"+mrp+"'";

        //String sql = "select db_id from product_master where ProductName like '%"+productName+"%' and MRP = '"+mrp+"'";

        String result = null;
        try {
            Cursor c = null;
            String sql = "select db_id from product_master where ProductName like '%" + productName + "%' and MRP = '" + mrp + "' and ProductCategory ='" + type + "'";

            Log.e("sql", sql);

            //result = "";
            database = dbHelper.getReadableDatabase();
            c = database.rawQuery(sql, null);
            if (c != null && !c.isClosed()) {
                c.moveToFirst();

                result = c.getString(0);

                c.moveToNext();
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;


    }

    public Cursor getDashboardData(String boc) {
        // TODO Auto-generated method stub

        String sql = "select * from dashboard_details where BOC = '" + boc + "'";

        Cursor c = database.rawQuery(sql, null);

        return c;
    }

    public void InsertstockForFLR(
            String cat_id, String db_id1,String eancode,String product_cat,
            String product_type, String product_name, String size, String price,String emp_id,
            String stropening,String fresher_stock,String stockinhand, String cl_stk,
            String return_saleable, String return_non_saleable,String soldstock,
            String i_netamt, String netamt,String discount,
            String date,String shadno,String month_name, String year_name,
            String updateDate, String FLRCode) {
        // TODO Auto-generated method stub

        ContentValues values = new ContentValues();

        values.put("product_id", cat_id);
        values.put("db_id", db_id1);
        values.put("eancode", eancode);
        values.put("product_category", product_cat);
        values.put("product_type", product_type);
        values.put("product_name", product_name);
        values.put("size", size);
        values.put("price", price);
        values.put("emp_id", emp_id);
        values.put("opening_stock", stropening);
        values.put("stock_received", fresher_stock);
        values.put("stock_in_hand", stockinhand);
        values.put("close_bal", cl_stk);
        values.put("return_saleable", return_saleable);
        values.put("return_non_saleable", return_non_saleable);
        values.put("sold_stock", soldstock);
        values.put("total_gross_amount", i_netamt);
        values.put("total_net_amount", netamt);
        values.put("discount", discount);
        values.put("savedServer", "0");
        values.put("insert_date", date);
        values.put("shadeNo", shadno);
        values.put("month", month_name);
        values.put("year", year_name);
        values.put("updateDate", updateDate);
        values.put("flag", "s");
        values.put("FLRCode",FLRCode);
        Log.e("InsertStock", values.toString());

        database.insert("stock", null, values);

        //}
        // getStockdetails();
        database.close();
    }

    public void UpdateStockForFLR(String stockinhand,
                                String cl_stk,
                                String fresher_stock,String db_id1,
                                String date,String soldstock,
                                String return_saleable, String return_non_saleable, String i_netamt,String netamt,
                                String discount,String shadno, String updateDate, String month_name, String year_name) {

        // TODO Auto-generated method stub


        Log.e("", "inside update stock");


        String sql = "update stock set  stock_in_hand = " + "'" + stockinhand + "'" +
                " ,sold_stock = " + "'" + soldstock + "'" +
                " ,return_saleable = " + "'" + return_saleable + "'" +
                " ,return_non_saleable = " + "'" + return_non_saleable + "'" +
                " ,total_gross_amount = " + "'" + i_netamt + "'" +
                " ,total_net_amount = " + "'" + netamt + "'" +
                " ,discount = " + "'" + discount + "'" +
                ",stock_received = " + "'" + fresher_stock + "'" + "," +
                "close_bal = " + "'" + cl_stk + "'" + "," +
                "shadeNo = " + "'" + shadno + "'" + "," +
                "insert_date = " + "'" + date + "'" +
                ", flag = " + "'s'" +
                ", savedServer='0',updateDate ='" + updateDate + "' ,month='" + month_name + "', year='" + year_name + "' where db_id = " + "'" + db_id1 + "'" + "";
        Log.e("", "update stock sql==" + sql);
        database.execSQL(sql);

    }


}