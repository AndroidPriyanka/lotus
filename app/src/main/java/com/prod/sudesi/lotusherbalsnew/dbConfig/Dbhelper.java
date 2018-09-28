package com.prod.sudesi.lotusherbalsnew.dbConfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Dbhelper extends SQLiteOpenHelper {

    public static final String KEY_ID = "id";
    public static final String KEY_SIZE = "Size";
    public static final String KEY_MRP = "MRP";
    public static final String KEY_EANCODE = "EANCode";
    public static final String KEY_CATEGROYID = "CategoryId";
    public static final String KEY_DB_ID = "db_id";

    public static final String DATABASE_NAME = "sudesi.sqlite";

    private static final int DATABASE_VERSION = 10;

    public static final String KEY_PRODUCT_TYPE = "ProductType";
    public static final String KEY_PRODUCT_Category = "ProductCategory";
    public static final String KEY_PRODUCT_OFFER = "SingleOffer";
    public static final String KEY_PRODUCTS = "ProductName";
    public static final String KEY_SHADENO = "ShadeNo";

    private static final String TABLE_CREATE_SCAN = "create table if not exists scan (scannedId integer primary key autoincrement,_empId text,product_type text,imagecount text,saveTime text,savedServer integer,image_desc text,latitude text , longitude text)";
    private static final String TABLE_CREATE_IMAGE = "create table if not exists image (imageId integer primary key autoincrement,scannedId integer,imagePath text not null,savedServer integer,saveTime text,image_name text,server_visiblity_id text)";
    private static final String TABLE_CREATE_ATTEDANCE = "create table if not exists attendance (id integer primary key autoincrement,emp_id text,Adate text,attendance text,absent_type text,lat text,lon text,savedServer integer,month text,holiday_desc text,year text)";
    private static final String TABLE_CREATE_STOCK = "create table if not exists stock (id integer primary key autoincrement,product_id text,db_id text,eancode text,product_category text,product_type text,product_name text, size text, price text, emp_id text,opening_stock text,stock_received text,stock_in_hand text,close_bal text,return_saleable text,return_non_saleable text,sold_stock text,total_gross_amount text,total_net_amount text,discount text,savedServer integer, insert_date text,last_modified_date text,shadeNo text default 0,month text,year text,updateDate text,db_stock_id varchar(100))";

    private static final String TABLE_CREATE_STOCK_MONTHLY = "CREATE TABLE if not exists stock_monthwise (id integer primary key autoincrement,product_id text,db_id text,eancode text,product_category text,product_type text,product_name text, size text, price text, emp_id text,opening_stock text,stock_received text,stock_in_hand text,close_bal text,return_saleable text,return_non_saleable text,sold_stock text,total_gross_amount text,total_net_amount text,discount text,savedServer integer, insert_date text,last_modified_date text,shadeNo text default 0, db_stock_id varchar(100),month_name text,year text)";

    private static final String TABLE_CREATE_TESTER = "create table if not exists tester (id integer primary key autoincrement,db_tester_id text, product_id text,db_id text,eancode text,product_category,product_type text,product_name text,size text,emp_id text,product_status text,requried_date text,delivered_date text,savedServer integer,shadeno text default 0,cat_id text)";

    // private static final String TABLE_PRODUCT_MASTER =
    // "create table product_master (id integer primary key autoincrement, ProductCategory text,ProductType text,ProductName text)";

    private static final String TABLE_PRODUCT_MASTER = "create table if not exists product_master (id integer primary key autoincrement, ProductCategory text,ProductType text,ProductName text,"
            + "											db_id text,CategoryId text,Category text,ShadeNo text,EANCode text,Size text,MRP text,MasterPackQty text,MonoPackQty text,InnerQty text,SKU_L text,SKU_B text,SKU_H text,price_type text,order_flag integer)";

    private static final String TABLE_DATE_SYNC = "create table if not exists date_sync (id integer primary key autoincrement, table_name text,date text)";

    private static final String INSERT_DATE_SYNC_TESTER = "insert into date_sync (table_name,date) values('tester','')";
    private static final String INSERT_DATE_SYNC = "insert into date_sync (table_name,date) values('product_master','')";
    private static final String INSERT_DATE_SYNC1 = "insert into date_sync (table_name,date) values('stock','')";
    private static final String INSERT_DATE_SYNC2 = "insert into date_sync (table_name,date) values('tester_master','')";
    private static final String INSERT_DATE_SYNC3 = "insert into date_sync (table_name,date) values('boc_wise_stock','')";

    private static final String INSERT_DATE_SYNC4 = "insert into date_sync (table_name,date) values('stock_monthwise','')";


    private static final String TABLE_CREATE_LOGIN = "create table if not exists login(Lid integer primary key autoincrement, username text, password text,android_uid text, created_date text,last_modified_date text ,status text, div text,flag text)";

    private static final String TABLE_CREATE_SYNC_LOG = "CREATE TABLE if not exists SYNC_LOG (ID INTEGER PRIMARY KEY, EXCEPTION,LINE_NO, METHOD,CREATED_DATE,LASTMODIFIED_DATE,USERNAME,SYNCMETHOD,RESULT,FLAG);";

//	private static final String ALTER_UPLOAD_TABLE = "ALTER TABLE image ADD server_visiblity_id varchar(128)";

    // ----------------
    private static final String TABLE_CREATE_BOC_WISE_STOCK = "create table if not exists boc_wise_stock (id integer primary key autoincrement,product_id text,db_id text,eancode text,product_category text,product_type text,product_name text, size text, price text, emp_id text,opening_stock text,stock_received text,stock_in_hand text,close_bal text,return_saleable text,return_non_saleable text,sold_stock text,total_gross_amount text,total_net_amount text,discount text,savedServer integer, insert_date text,last_modified_date text,shadeNo text default 0,month text,year text,updateDate text,db_stock_id varchar(100),date varchar(100),boc varchar(50))";

    private static final String TABLE_TESTER_MASTER = "create table if not exists tester_master (id integer primary key autoincrement, server_db_id text , ProductCategory text,ProductType text,ProductName text,"
            + "											db_id text,CategoryId text,Category text,ShadeNo text,EANCode text,Size text,MRP text,MasterPackQty text,MonoPackQty text,InnerQty text,SKU_L text,SKU_B text,SKU_H text,price_type text)";


    private static final String TABLE_DASHBOARD_DETAILS = "create table if not exists dashboard_details(id integer primary key autoincrement, BOC text,AndroidCreatedDate text,COLOR text,ColorSoldQty text,ColorSoldValue text,SKIN text,SkinSoldQty text,SkinSoldValue text,TOTAL text,TotalQty text,TotalValue text)";

    private static final String TABLE_CREATE_SUPERVISOR = "create table if not exists supervisor_attendance(id integer primary key autoincrement,BDE_CODE text,BA_id text,Adate text,Actual_date text,lat text,lon text,savedServer integer);";

    private static final String TABLE_ALTER_LOGIN1 = "alter table login add column bde_Code text";
    private static final String TABLE_ALTER_LOGIN2 = "alter table login add column bde_Name text";
    private static final String TABLE_ALTER_LOGIN3 = "alter table login add column Role text";

    private static final String TABLE_CREATE_STOCK1 = "alter table stock add column FLRCode text";
    private static final String TABLE_CREATE_STOCK2 = "alter table stock add column SingleOffer text";
    private static final String TABLE_CREATE_STOCK3 = "alter table stock add column opening_amt text";
    private static final String TABLE_CREATE_STOCK4 = "alter table stock add column close_amt text";
    private static final String TABLE_CREATE_STOCK5 = "alter table stock add column sold_amt text";
    private static final String TABLE_CREATE_STOCK6 = "alter table stock add column received_amt text";
    private static final String TABLE_CREATE_STOCK7 = "alter table stock add column uploadflag text";
    private static final String TABLE_PRODUCT_MASTER1 = "alter table product_master add column SingleOffer text";

    private static final String TABLE_ALTER_ATTENDANCE1 = "alter table attendance add column logout_status text;";
    private static final String TABLE_ALTER_ATTENDANCE2 = "alter table attendance add column logout_date text;";

    private static final String TABLE_CREATE_FLOTEROUTLET = "create table if not exists floteroutlet(id integer primary key autoincrement,baCodeOutlet text,banameOutlet text,outletname text,flotername text);";
    private static final String TABLE_DASHBOARD_DETAILS_DUBAI = "create table if not exists dashboard_details_dubai(id integer primary key autoincrement, BOC text,AndroidCreatedDate text,SoldQty text,Soldvalue text)";
    private static final String TABLE_FOCUS_DATA = "create table if not exists focus_data(id integer primary key autoincrement, Productid text,Type text,Category text,Empid text,ProName text,size text,MRP text,"
            + "   Target_qty text,Target_amt text,AndroidCreateddate text)";


    private static Dbhelper dbInstance = null;
    private static SQLiteDatabase database;


    public Dbhelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        //this.database = db;

        database.execSQL(TABLE_CREATE_SCAN);
        database.execSQL(TABLE_CREATE_IMAGE);
        database.execSQL(TABLE_CREATE_ATTEDANCE);
        database.execSQL(TABLE_CREATE_STOCK);
        database.execSQL(TABLE_CREATE_TESTER);

        database.execSQL(TABLE_PRODUCT_MASTER);
        database.execSQL(TABLE_DATE_SYNC);
        database.execSQL(INSERT_DATE_SYNC);
        database.execSQL(INSERT_DATE_SYNC1);
        database.execSQL(TABLE_CREATE_LOGIN);
        database.execSQL(TABLE_CREATE_SYNC_LOG);
        database.execSQL(INSERT_DATE_SYNC_TESTER);
        database.execSQL(TABLE_CREATE_STOCK_MONTHLY);
        database.execSQL(TABLE_CREATE_BOC_WISE_STOCK);
        database.execSQL(INSERT_DATE_SYNC2);
        database.execSQL(INSERT_DATE_SYNC3);
        database.execSQL(TABLE_TESTER_MASTER);
        database.execSQL(INSERT_DATE_SYNC4);
        database.execSQL(TABLE_DASHBOARD_DETAILS);


        onUpgrade(database, database.getVersion(), DATABASE_VERSION);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        Log.v("", "DATABASE_VERSION=" + DATABASE_VERSION);

        this.database = db;

//        onCreate(database);
        /*
         * if (DATABASE_VERSION > oldVersion) {
         *
         * if(oldVersion == 3){ Log.v("","ugrade if");
         * //db.execSQL("ALTER TABLE PRODUCT ADD PRODUCT_CATEGORY varchar(128)"
         * );//1.1
         *
         * }else{ Log.v("","upgrade else");
         *
         * database.execSQL("ALTER TABLE stock ADD db_stock_id varchar(100)");//1.1
         * database.execSQL(INSERT_DATE_SYNC1);
         * database.execSQL("ALTER TABLE image ADD image_name varchar(100);");
         *
         * } }
         */

        if (newVersion == 3) {


        } else if (newVersion == 4) {
            Log.e("", "upgrade v4");
            // database.execSQL(ALTER_UPLOAD_TABLE);
            // database.execSQL("ALTER TABLE stock ADD db_stock_id varchar(100)");//1.1
            // database.execSQL(INSERT_DATE_SYNC1);
            // database.execSQL("ALTER TABLE image ADD image_name varchar(100);");

            database.execSQL("ALTER TABLE stock ADD flag varchar(50);");
            database.execSQL(TABLE_CREATE_SUPERVISOR);
            database.execSQL(TABLE_ALTER_LOGIN1);
            database.execSQL(TABLE_ALTER_LOGIN2);
            database.execSQL(TABLE_ALTER_ATTENDANCE1);
            database.execSQL(TABLE_ALTER_ATTENDANCE2);
        } else if (newVersion == 5) {
            Log.e("", "upgrade v5");
            database.execSQL("ALTER TABLE stock ADD flag varchar(50);");
            database.execSQL(TABLE_CREATE_SUPERVISOR);
            database.execSQL(TABLE_ALTER_LOGIN1);
            database.execSQL(TABLE_ALTER_LOGIN2);
            database.execSQL(TABLE_ALTER_ATTENDANCE1);
            database.execSQL(TABLE_ALTER_ATTENDANCE2);
        } else if (newVersion == 6) {
            database.execSQL("ALTER TABLE stock ADD flag varchar(50);");
            database.execSQL(TABLE_CREATE_SUPERVISOR);
            database.execSQL(TABLE_ALTER_LOGIN1);
            database.execSQL(TABLE_ALTER_LOGIN2);
            database.execSQL(TABLE_ALTER_ATTENDANCE1);
            database.execSQL(TABLE_ALTER_ATTENDANCE2);
        } else if (newVersion == 8) {
            if (!isColumnExists("stock", "flag"))
                database.execSQL("ALTER TABLE stock ADD flag varchar(50);");
            database.execSQL(TABLE_CREATE_SUPERVISOR);

            if (!isColumnExists("login", "bde_Code"))
                database.execSQL(TABLE_ALTER_LOGIN1);
            if (!isColumnExists("login", "bde_Name"))
                database.execSQL(TABLE_ALTER_LOGIN2);
            if (!isColumnExists("attendance", "logout_status"))
                database.execSQL(TABLE_ALTER_ATTENDANCE1);
            if (!isColumnExists("attendance", "logout_date"))
                database.execSQL(TABLE_ALTER_ATTENDANCE2);
            if (!isColumnExists("login", "Role"))
                database.execSQL(TABLE_ALTER_LOGIN3);

            database.execSQL(TABLE_CREATE_FLOTEROUTLET);
            if (!isColumnExists("stock", "FLRCode"))
                database.execSQL(TABLE_CREATE_STOCK1);
            if (!isColumnExists("stock", "SingleOffer"))
                database.execSQL(TABLE_CREATE_STOCK2);
            if (!isColumnExists("product_master", "SingleOffer"))
                database.execSQL(TABLE_PRODUCT_MASTER1);
            database.execSQL(TABLE_DASHBOARD_DETAILS_DUBAI);
        }else if (newVersion == 9) {
            if (!isColumnExists("stock", "flag"))
                database.execSQL("ALTER TABLE stock ADD flag varchar(50);");
            database.execSQL(TABLE_CREATE_SUPERVISOR);

            if (!isColumnExists("login", "bde_Code"))
                database.execSQL(TABLE_ALTER_LOGIN1);
            if (!isColumnExists("login", "bde_Name"))
                database.execSQL(TABLE_ALTER_LOGIN2);
            if (!isColumnExists("attendance", "logout_status"))
                database.execSQL(TABLE_ALTER_ATTENDANCE1);
            if (!isColumnExists("attendance", "logout_date"))
                database.execSQL(TABLE_ALTER_ATTENDANCE2);
            if (!isColumnExists("login", "Role"))
                database.execSQL(TABLE_ALTER_LOGIN3);

            database.execSQL(TABLE_CREATE_FLOTEROUTLET);
            if (!isColumnExists("stock", "FLRCode"))
                database.execSQL(TABLE_CREATE_STOCK1);
            if (!isColumnExists("stock", "SingleOffer"))
                database.execSQL(TABLE_CREATE_STOCK2);
            if (!isColumnExists("product_master", "SingleOffer"))
                database.execSQL(TABLE_PRODUCT_MASTER1);
            database.execSQL(TABLE_DASHBOARD_DETAILS_DUBAI);
            if (!isColumnExists("stock", "opening_amt"))
                database.execSQL(TABLE_CREATE_STOCK3);
            if (!isColumnExists("stock", "close_amt"))
                database.execSQL(TABLE_CREATE_STOCK4);
            if (!isColumnExists("stock", "sold_amt"))
                database.execSQL(TABLE_CREATE_STOCK5);
            if (!isColumnExists("stock", "received_amt"))
                database.execSQL(TABLE_CREATE_STOCK6);
            if (!isColumnExists("stock", "uploadflag"))
                database.execSQL(TABLE_CREATE_STOCK7);
        }else if (newVersion == 10) {
            if (!isColumnExists("stock", "flag"))
                database.execSQL("ALTER TABLE stock ADD flag varchar(50);");
            database.execSQL(TABLE_CREATE_SUPERVISOR);

            if (!isColumnExists("login", "bde_Code"))
                database.execSQL(TABLE_ALTER_LOGIN1);
            if (!isColumnExists("login", "bde_Name"))
                database.execSQL(TABLE_ALTER_LOGIN2);
            if (!isColumnExists("attendance", "logout_status"))
                database.execSQL(TABLE_ALTER_ATTENDANCE1);
            if (!isColumnExists("attendance", "logout_date"))
                database.execSQL(TABLE_ALTER_ATTENDANCE2);
            if (!isColumnExists("login", "Role"))
                database.execSQL(TABLE_ALTER_LOGIN3);

            database.execSQL(TABLE_CREATE_FLOTEROUTLET);
            if (!isColumnExists("stock", "FLRCode"))
                database.execSQL(TABLE_CREATE_STOCK1);
            if (!isColumnExists("stock", "SingleOffer"))
                database.execSQL(TABLE_CREATE_STOCK2);
            if (!isColumnExists("product_master", "SingleOffer"))
                database.execSQL(TABLE_PRODUCT_MASTER1);
            database.execSQL(TABLE_DASHBOARD_DETAILS_DUBAI);
            if (!isColumnExists("stock", "opening_amt"))
                database.execSQL(TABLE_CREATE_STOCK3);
            if (!isColumnExists("stock", "close_amt"))
                database.execSQL(TABLE_CREATE_STOCK4);
            if (!isColumnExists("stock", "sold_amt"))
                database.execSQL(TABLE_CREATE_STOCK5);
            if (!isColumnExists("stock", "received_amt"))
                database.execSQL(TABLE_CREATE_STOCK6);
            if (!isColumnExists("stock", "uploadflag"))
                database.execSQL(TABLE_CREATE_STOCK7);
            database.execSQL(TABLE_FOCUS_DATA);
        }


    }


    public void dropandcreate(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS scan");
        database.execSQL("DROP TABLE IF EXISTS image");
        database.execSQL("DROP TABLE IF EXISTS attendance");
        database.execSQL("DROP TABLE IF EXISTS stock");
        database.execSQL("DROP TABLE IF EXISTS tester");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_MASTER);
        database.execSQL("DROP TABLE IF EXISTS  " + TABLE_DATE_SYNC);
        database.execSQL("DROP TABLE IF EXISTS login");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CREATE_SYNC_LOG);

        onCreate(database);
    }

    public boolean isColumnExists(String table, String column) {
        try {
            /*SQLiteDatabase database = null;
            if(database == null || !database.isOpen()) {
                database = getWritableDatabase();
            }*/
            Cursor cursor = database.rawQuery("PRAGMA table_info(" + table + ")", null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    if (column.equalsIgnoreCase(name)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}