package com.example.jamal.testcontentprovider;

import android.Manifest;
import android.app.ListActivity;
import android.content.CursorLoader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ContentProviderExample extends ListActivity {
    private static final  int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    Uri allContacts = Uri.parse("content://contacts/people");
    Cursor c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider_example);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case  MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (android.os.Build.VERSION.SDK_INT <11) {
//---before Honeycomb---
                        c = managedQuery(allContacts, null, null, null, null);
                    } else {


                        //---Honeycomb and later---
                        CursorLoader cursorLoader = new CursorLoader(
                                this,
                                allContacts,
                                null,
                                null,
                                null ,
                                null);
                        c = cursorLoader.loadInBackground();
                    }
                    String[] columns = new String[] {
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.Contacts._ID};
                    int[] views = new int[] {R.id.contactName, R.id.contactID};
                    SimpleCursorAdapter adapter;
                    if (android.os.Build.VERSION.SDK_INT <11) {
                        //---before Honeycomb---
                        adapter = new SimpleCursorAdapter(
                                this, R.layout.activity_main, c, columns, views);
                    } else {
                        //---Honeycomb and later---
                        adapter = new SimpleCursorAdapter(
                                this, R.layout.activity_main, c, columns, views,
                                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                    }
                    this.setListAdapter(adapter);

                } else {
                    Toast.makeText(this,"YOU DON'T HAVE PERMISSION TO ACCESS CONTACTS ! ",Toast.LENGTH_LONG);

                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}
