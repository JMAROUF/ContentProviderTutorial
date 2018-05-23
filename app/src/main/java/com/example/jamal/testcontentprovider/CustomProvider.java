package com.example.jamal.testcontentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by jamal on 17/04/2018.
 */

public class CustomProvider extends ContentProvider {

    static final String PROVIDER_NAME ="tdi.contentProvider.provider.books";
    static final Uri CONTENT_URI=Uri.parse("content://"+PROVIDER_NAME+"/books");
    static final String _ID="_id";
    static final String TITLE="title";
    static final String ISBN="isbn";
    static final int BOOKS=1;
    static final int BOOK_ID=2;
    private static final  UriMatcher uriMatcher;
    static{
        uriMatcher= new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"books",BOOKS);
        uriMatcher.addURI(PROVIDER_NAME,"books/#",BOOK_ID);

    }
    SQLiteDatabase booksDB;
    static final String DATABASE_NAME="Books";
    static final String  DATABASE_TABLE="titles";
    static final int DATABASE_VERSION=2;
    static final String DATABASE_CREATE="create table "+DATABASE_TABLE+"(_id integer primary key autoincrement ," +
            "title text not null , isbn text not null)";

    // database class
    private static class DatabaseHelper extends SQLiteOpenHelper{

        DatabaseHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
                db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("databaseProvider","upgrading database from "+oldVersion +"to "+newVersion
            +"wich will destroy all old data ");
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper  dbHelper = new DatabaseHelper(context);
        booksDB=dbHelper.getWritableDatabase();
        return (booksDB==null) ? false :true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder sqlBuilder= new SQLiteQueryBuilder();
        sqlBuilder.setTables(DATABASE_TABLE);
        if(uriMatcher.match(uri)==BOOK_ID){
            sqlBuilder.appendWhere(_ID+"="+uri.getPathSegments().get(1));
        }
        if(sortOrder==null || sortOrder=="")
            sortOrder=TITLE;
        Cursor c = sqlBuilder.query(booksDB,projection,selection,selectionArgs,null,null,sortOrder);
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case BOOKS : return "vnd.android.cursor.dir/vnd.contentProvider.books";
            case BOOK_ID: return "vnd.android.cursor.item/vnd.contentProvider.books";
            default: throw new  IllegalArgumentException("UNKNOWN URI"+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Long rowID= booksDB.insert(DATABASE_TABLE,"",values);
        if(rowID>0){
            Uri _uri= ContentUris.withAppendedId(CONTENT_URI,rowID);
            getContext().getContentResolver().notifyChange(_uri,null);
            return _uri;
        }
        throw new SQLException("FAILED to insert row into "+uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count=0;
        switch(uriMatcher.match(uri)){
            case BOOKS: count =booksDB.delete(DATABASE_TABLE,selection,selectionArgs);
                 break;
            case BOOK_ID: String id = uri.getPathSegments().get(1);
                        count = booksDB.delete(DATABASE_TABLE,_ID+"="+id+(!TextUtils.isEmpty(selection) ?
                        "AND ("+selection+')' :"" ),selectionArgs);
                        break;
            default: throw new  IllegalArgumentException("UNKNOWN URI"+uri);


        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count =0;
        switch(uriMatcher.match(uri)){
            case BOOKS: count=booksDB.update(DATABASE_TABLE,values,_ID+"="+uri.getPathSegments().get(1)
            +(!TextUtils.isEmpty(selection) ? "AND ("+selection+')' :""),selectionArgs);
            break;
            default: throw new IllegalArgumentException("UNKNOWN URI"+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
}
