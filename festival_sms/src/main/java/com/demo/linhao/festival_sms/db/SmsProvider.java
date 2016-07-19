package com.demo.linhao.festival_sms.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.demo.linhao.festival_sms.bean.SendedMsg;


//需要在AndroidMainfest.xml中注册
public class SmsProvider extends ContentProvider{
    private static final String AUTHORITY="com.demo.linhao.provider.SmsProvider";
    public static final Uri URI_SMS_ALL=Uri.parse("content://"+AUTHORITY+"/sms");

    private static UriMatcher mMatcher;

    private static final int SMS_ALL=0;//表示访问表中的所有数据
    private static final int SMS_ONE=1;//表示访问表中的单条数据

    //在静态代码块中完成mMatcher的初始化及uri的添加
    static {
        mMatcher=new UriMatcher(UriMatcher.NO_MATCH);

        //addURI接收三个参数，可以分别把权限、路径和一个自定义代码传进去
        //*：表示匹配任意长度的任意字符；#：表示匹配任意长度的数字
        mMatcher.addURI(AUTHORITY,"sms",SMS_ALL);
        mMatcher.addURI(AUTHORITY,"sms/#",SMS_ONE);
    }

    private SmsDBOpenHelper mHelper;
    private SQLiteDatabase mDB;

    @Override
    public boolean onCreate() {
        mHelper=SmsDBOpenHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match=mMatcher.match(uri);
        switch (match) {
            case SMS_ALL:
                break;
            case SMS_ONE://本案例用不到，但展示一下代码逻辑
                long id=ContentUris.parseId(uri);//从路径中中获取id
                selection="_id = ?";
                selectionArgs=new String[]{String.valueOf(id)};
                break;
            default:
                throw new IllegalArgumentException("Wrong URI:"+uri.toString());
        }
        mDB=mHelper.getReadableDatabase();
        Cursor cursor=mDB.query(SendedMsg.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

        //用来在后台检测数据的变化，如果有变化就会有返回（因为在SmsHistoryFragment中使用了Loader）
        cursor.setNotificationUri(getContext().getContentResolver(),URI_SMS_ALL);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match=mMatcher.match(uri);
        if(match!=SMS_ALL) {
            throw new IllegalArgumentException("Wrong URI:"+uri.toString());
        }
        mDB=mHelper.getWritableDatabase();

        //第二个参数用于在未指定添加数据的情况下给某些可为空的列自动赋值NULL，一般我们用不到这个功能，直接传入null 即可
        long rowId=mDB.insert(SendedMsg.TABLE_NAME,null,values);
        if(rowId>0) {//如果添加数据成功
            notifyDataSetChanged();
            return ContentUris.withAppendedId(uri,rowId);//为传入的uri加上id
        }
        return uri;
    }

    private void notifyDataSetChanged() {
        getContext().getContentResolver().notifyChange(URI_SMS_ALL,null);//通知监听器关于数据更新的信息
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
