package com.android.nazli.challengeyourfriend;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ContactActivity extends AppCompatActivity {
 /*   private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }*/
    ListView listContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listContacts = (ListView) findViewById(R.id.listContact);
        displayContacts();

    }
       /* Uri queryUri = ContactsContract.Contacts.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,

        };


        String selection = ContactsContract.Contacts.DISPLAY_NAME + " IS NOT NULL";
        //String selection = ContactsContract.Contacts._ID + " IS NOT NULL";

        CursorLoader cursorLoader = new CursorLoader(
                this,
                queryUri,
                projection,
                selection,
                null,
                null);

        Cursor cursor = cursorLoader.loadInBackground();

       // String[] from = {ContactsContract.Contacts.DISPLAY_NAME};
        String[] from = {ContactsContract.Contacts._ID};
        int[] to = {android.R.id.text1};

        ListAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                cursor,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listContacts.setAdapter(adapter);
*/

    public void displayContacts() {


        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext())
        {
            // set
            //setName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.v("Phone",name+phoneNumber);
            //Adapter
            //what is from and to?
            // The desired columns to be bound
            String[] from = new String[] {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            //String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER};
            int[] to = {android.R.id.text1};
            ListAdapter contactAdapter=new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    phones,
                    from,
                    to,
                    0);
                    //CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            listContacts.setAdapter(contactAdapter);
            //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }





}

