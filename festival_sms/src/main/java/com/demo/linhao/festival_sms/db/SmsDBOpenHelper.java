package com.demo.linhao.festival_sms.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.demo.linhao.festival_sms.bean.SendedMsg;


public class SmsDBOpenHelper extends SQLiteOpenHelper{
    private static final String DB_NAME="sms.db";
    private static final int DB_VERSION=1;

    private static SmsDBOpenHelper mHelper;

    private SmsDBOpenHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    //传入的context有可能是一个Activity
    //所以在构造方法中用context.getApplicationContext()尽量得到Application的context
    //避免造成内存泄露的问题
    public static SmsDBOpenHelper getInstance(Context context) {
        if(mHelper==null) {
            synchronized (SmsDBOpenHelper.class) {
                if(mHelper==null) {
                    mHelper=new SmsDBOpenHelper(context);
                }
            }
        }
        return mHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table "+ SendedMsg.TABLE_NAME+" ( "+
                "_id integer primary key autoincrement, "+
                SendedMsg.COLUMN_DATE+" integer, "+
                SendedMsg.COLUMN_FESTIVAL_NAME+" text,"+
                SendedMsg.COLUMN_CONTENT+" text,"+
                SendedMsg.COLUMN_NAMES+" text,"+
                SendedMsg.COLUMN_NUMBERS+" text )";
        db.execSQL(sql);

        //因为在SmsHistoryFragment的CursorAdapter涉及的cursor的必须要有个命名为"_id"的列
        //所以这里在建表的时候有一个"_id"的主键
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
