package com.example.android.csula.challengefriends;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.com.google.gson.Gson;
import com.example.android.csula.challengefriends.models.Challenge;
import com.example.android.csula.challengefriends.models.User;
import com.example.android.csula.challengefriends.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactActivityFragment extends Fragment {

    ListView contactsListView;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Challenge challenge;

    public ContactActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        challenge = new Gson().fromJson(intent.getStringExtra("challenge").toString(), Challenge.class);

        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        contactsListView = (ListView) rootView.findViewById(R.id.listview_contacts);

        List<User> friends = PreferenceUtils.getFriends(getActivity());
        ArrayAdapter<User> adapter = new UserAdapter(getActivity(), R.id.textview_contact_item, (ArrayList<User>) friends);
        contactsListView.setAdapter(adapter);

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* save the challenge id in receivers received challenges and senders sent challenges */
                User user = (User)parent.getItemAtPosition(position);
                Log.e("user", user.getCognitoId() + " " + user.getFacebookId());
            }
        });

        return rootView;
    }

    /*private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            List<String> contacts = getContactNames();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.listview_item_contact, R.id.textview_contact_item, contacts);
            contactsListView.setAdapter(adapter);
        }
    }*/

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    /*private List<String> getContactNames() {
        List<String> contacts = new ArrayList<>();
        // Get the ContentResolver
        ContentResolver resolver = getActivity().getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        if (cursor.moveToFirst()) {
            // Iterate through the cursor
            do {
                // Get the contacts name
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contacts.add(name);
            } while (cursor.moveToNext());
        }
        // Close the curosor
        cursor.close();

        return contacts;
    }*/

    /* custom user adapter */
    public class UserAdapter extends ArrayAdapter<User> {
        private ViewHolder viewHolder;

        private class ViewHolder {
            private TextView itemView;
        }

        public UserAdapter(Context context, int textViewResourceId, ArrayList<User> items) {
            super(context, textViewResourceId, items);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.listview_item_contact, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.itemView = (TextView) convertView.findViewById(R.id.textview_contact_item);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            User item = getItem(position);
            if (item != null) {
                viewHolder.itemView.setText(item.getName());
            }

            return convertView;
        }
    }

}
