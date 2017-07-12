package com.example.ygl.baking.sql;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.litepal.crud.DataSupport;

/**
 * Created by YGL on 2017/6/18.
 */

/*
 * Define an implementation of ContentProvider that stubs out
 * all methods
 */
public class StubProvider extends ContentProvider {
    private static final String TAG = "StubProvider";
    public static Uri bakingUri=Uri.parse("content://com.example.ygl.baking/baking/Recipe");
    /*
     * Always return true, indicating that the
     * provider loaded correctly.
     */
    @Override
    public boolean onCreate() {
        return true;
    }
    /*
     * Return an empty String for MIME type
     */
    @Override
    public String getType(Uri uri) {
        return new String();
    }
    /*
     * query() always returns no results
     *
     */
    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {
        Log.i(TAG,"public Cursor query");
        Cursor cursor= DataSupport.findBySQL("SELECT * FROM Recipe");
        if (cursor != null){
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }
    /*
     * insert() always returns null (no URI)
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i(TAG,"StubProvider public Uri insert");
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }
    /*
     * delete() always returns "no rows affected" (0)
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }
    /*
     * update() always returns "no rows affected" (0)
     */
    @Override
    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }
}
