package com.tripco.www.tripco.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

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

        // Trip_Table 생성
        sb.append(" CREATE TABLE Trip_Table ( ");
        sb.append(" trip_no INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" trip_title TEXT, ");
        sb.append(" start_date TEXT, ");
        sb.append(" end_date TEXT, ");
        sb.append(" user_id TEXT, ");
        sb.append(" partner_id TEXT, ");
        sb.append(" hashtag TEXT ) ");
        db.execSQL(sb.toString());

        // ScheduleList_Table 생성
        sb = new StringBuffer();
        sb.append(" CREATE TABLE ScheduleList_Table ( " );
        sb.append(" trip_no INTEGER REFERENCES Trip_Table (trip_no) ");
        sb.append(" ON DELETE CASCADE, ");
        sb.append(" schedule_no INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" schedule_date INTEGER, ");
        sb.append(" item_url TEXT, ");
        sb.append(" cate_no INTEGER, ");
        sb.append(" item_lat TEXT, ");
        sb.append(" item_long TEXT, ");
        sb.append(" item_placeid TEXT, ");
        sb.append(" item_title TEXT, ");
        sb.append(" item_memo TEXT, ");
        sb.append(" item_check INTEGER DEFAULT 0, ");
        sb.append(" item_time TEXT )");
        db.execSQL(sb.toString());

        U.getInstance().log("테이블생성");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Toast.makeText(U.getInstance().getContext(), "DB UPGRADE!", Toast.LENGTH_SHORT).show();
    }
}
