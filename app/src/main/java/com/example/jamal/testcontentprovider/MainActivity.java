package com.example.jamal.testcontentprovider;

import android.Manifest;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final  int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        }

    public void onClickAddTitle(View view){
        ContentValues values = new ContentValues();
        values.put(CustomProvider.TITLE,((EditText)findViewById(R.id.txtTitle)).getText().toString());
        values.put(CustomProvider.ISBN,((EditText)findViewById(R.id.txtISBN)).getText().toString());
        Uri uri = getContentResolver().insert(CustomProvider.CONTENT_URI,values);
        Toast.makeText(getBaseContext(),uri.toString(), Toast.LENGTH_SHORT).show();
    }

    public void onClickRetrieveTitles(View view){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

    }

    public void onClickDeleteBook(View view){
        int count;
        String id=((EditText)findViewById(R.id.deletedBook)).getText().toString();
        Uri uri= Uri.parse("content://tdi.contentProvider.provider.books/books/"+id);
        count=getContentResolver().delete(uri,null,null);
        Toast.makeText(this,count+" book deleted ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                Uri allTitles = Uri.parse("content://tdi.contentProvider.provider.books/books");
                Cursor c;
                if(Build.VERSION.SDK_INT<11){
                    c= managedQuery(allTitles,null,null,null,"title desc");
                }else{
                    CursorLoader cursorLoader = new CursorLoader(this,allTitles,null,null,
                            null,"title desc");
                    c=cursorLoader.loadInBackground();
                }
                if(c.moveToFirst()){
                    do {
                        Toast.makeText(this,c.getString(c.getColumnIndex(CustomProvider._ID))+" , "+
                                c.getString(c.getColumnIndex(CustomProvider.TITLE))+" , "+
                                c.getString(c.getColumnIndex(CustomProvider.ISBN)),Toast.LENGTH_SHORT).show();
                    }while(c.moveToNext());
                }
            }
        }
    }




}
