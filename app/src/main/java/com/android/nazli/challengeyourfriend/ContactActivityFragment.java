package com.android.nazli.challengeyourfriend;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;


public class ContactActivityFragment extends Fragment {

    ListView contactsListView;

    public ContactActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        contactsListView = (ListView) rootView.findViewById(R.id.listview_contacts);
        displayContacts();

        return rootView;

    }

    public void displayContacts() {

        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.v("name and phone:", name + phoneNumber);
            String[] from = new String[] {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            int[] to = {android.R.id.text1};
            ListAdapter contactAdapter=new SimpleCursorAdapter(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    phones,
                    from,
                    to,
                    0);
            contactsListView.setAdapter(contactAdapter);
        }
    }
}
