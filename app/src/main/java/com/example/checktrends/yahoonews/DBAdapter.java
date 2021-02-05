package com.example.checktrends.yahoonews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SimpleCursorAdapter;

public class DBAdapter {
    private static final String DB_NAME = "yahoonews.db";
    private static final String DB_TABLE_ALREADY_READ = "already_read"; //既読テーブル
    private static final String DB_TABLE_BOOKMARK = "bookmark"; //ブックマークテーブル
    private static final int DB_VERSION = 1;

    //共通カラム
    public final static String COL_ID = "_id"; //ID
    public final static String COL_URL = "url"; //URL

    //ブックマークテーブルカラム
    public final static String COL_NEWS_TITLE = "news_title"; //ニュースタイトル

    private SQLiteDatabase db = null;
    private DatabaseHelper dbHelper;
    protected Context context;

    public DBAdapter(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }

    public DBAdapter openDB() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void closeDB() {
        db.close();
        db = null;
    }

    Cursor selectBookmark(){
        return db.query(DB_TABLE_BOOKMARK,null,null,null,null,null, null);
    }

    boolean insertBookmark(String title,String url){
        long rowID = -1;
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_NEWS_TITLE, title);
            values.put(COL_URL, url);
            rowID = db.insert(DB_TABLE_BOOKMARK, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            if (rowID >= 0) {
                //登録成功
                return true;
            } else {
                //登録失敗
                return false;
            }
        }
    }

    void deleteBookmark(String id){
        db.beginTransaction();
        try {
            db.delete(DB_TABLE_BOOKMARK, COL_ID + " = ?", new String[]{id});
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

            final String CREATE_TABLE_ALREADY_READ =
                    "create table " + DB_TABLE_ALREADY_READ + "("
                            + COL_ID + " integer primary key,"
                            + COL_URL +" text unique);";

            final String CREATE_TABLE_BOOKMARK =
                    "create table " + DB_TABLE_BOOKMARK + "("
                            + COL_ID + " integer primary key,"
                            + COL_NEWS_TITLE +" text,"
                            + COL_URL +" text unique);";

            db.execSQL(CREATE_TABLE_ALREADY_READ);
            db.execSQL(CREATE_TABLE_BOOKMARK);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_ALREADY_READ);
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BOOKMARK);
            onCreate(db);
        }
    }
}
