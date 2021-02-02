package com.example.checktrends.bookmark;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

class DBAdapter {
    private static final String DB_NAME = "bookmark.db";
    private static final String DB_TABLE = "bookmark";
    private static final int DB_VERSION = 1;

    public final static String COL_ID = "_id"; //ID
    public final static String COL_TITLE = "title"; //ブックマーク　サイトタイトル
    public final static String COL_URL = "url"; //ブックマーク　URL
    public final static String COL_ACCESS_TIME = "access_time"; //アクセス日時

    private final String NOT_ACCESSED = "未接続";

    private SQLiteDatabase db = null;
    private DatabaseHelper dbHelper;
    protected Context context;

    protected DBAdapter(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }

    DBAdapter openDB() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    void closeDB() {
        db.close();
        db = null;
    }

    Cursor selectBookmark(){
        return db.query(DB_TABLE,null,null,null,null,null, COL_ACCESS_TIME + " = '" + NOT_ACCESSED + "' ASC," + COL_ACCESS_TIME + " DESC," + COL_TITLE + " ASC");
    }

    void insertBookmark(String title,String url){
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_TITLE, title);
            values.put(COL_URL, url);
            values.put(COL_ACCESS_TIME, NOT_ACCESSED);
            db.insert(DB_TABLE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    void updateAccessTime(String id){
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();

            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒");
            String accessTime = format.format(new Date());

            values.put(COL_ACCESS_TIME, accessTime);

            db.update(DB_TABLE, values, COL_ID + " = ?", new String[]{id});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    void deleteBookmark(String id){
        db.beginTransaction();
        try {
            db.delete(DB_TABLE, COL_ID + " = ?", new String[]{id});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            final String CREATE_TABLE =
                    "create table " + DB_TABLE + "("
                            + COL_ID + " integer primary key,"
                            + COL_TITLE + " text,"
                            + COL_URL + " text,"
                            + COL_ACCESS_TIME + " text);";

            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }
}
