package com.tripco.www.tripco.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tripco.www.tripco.util.U;

public class DBOpenHelper extends SQLiteOpenHelper {
    public static DBOpenHelper dbOpenHelper = null;
    private static int DB_VERSION = 1;

    public static DBOpenHelper getInstance() {
        if(dbOpenHelper == null) dbOpenHelper = new DBOpenHelper();
        return dbOpenHelper;
    }

    private DBOpenHelper() {
        super(U.getInstance().getContext(), "TripcoDB", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE Trip_Table ( ");
        sb.append(" trip_no INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" trip_title TEXT, ");
        sb.append(" start_date TEXT, ");
        sb.append(" end_date TEXT, ");
        sb.append(" user_no INTEGER, ");
        sb.append(" partner_no INTEGER, ");
        sb.append(" hashtag TEXT ) ");
        db.execSQL(sb.toString());



        U.getInstance().log("테이블생성");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
