package com.example.android.csula.challengefriends;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.com.google.gson.Gson;
import com.amazonaws.http.HttpClient;
import com.amazonaws.http.HttpResponse;
import com.example.android.csula.challengefriends.models.Challenge;
import com.example.android.csula.challengefriends.models.MyProfile;
import com.example.android.csula.challengefriends.models.User;
import com.example.android.csula.challengefriends.utils.DynamoDbUtils;
import com.example.android.csula.challengefriends.utils.PreferenceUtils;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactActivityFragment extends Fragment {

    ListView contactsListView;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Challenge challenge;
    private User user;
    private Context context;

    public ContactActivityFragment() {
    }

    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();

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
                user = (User)parent.getItemAtPosition(position);
                Toast.makeText(context,user.getName()+":"+challenge.getId(),Toast.LENGTH_SHORT).show();
                /*Save the challenges id in the dynamo db and send the challenge to the another user using gcm */

                AWSTask task = new AWSTask();

                task.execute();
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

    public class AWSTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            /* ...should prompt for confirmation (add later)...*/
            /* add challenge id to senders list */


           /* --------------------------**-----------------------------*/
            MyProfile sendersProfile;
            try {
              sendersProfile = DynamoDbUtils.loadProfile
                        (DynamoDbUtils.init(context), PreferenceUtils.getCurrentUser(context).getCognitoId());

                Set set = new HashSet(sendersProfile.getSentChallengesList());
                set.add(challenge.getId());
                List list = new ArrayList(set);
                sendersProfile.setSentChallengesList(list);
                DynamoDbUtils.saveProfile(DynamoDbUtils.init(context), sendersProfile);
            }catch(Exception e) {
                Log.e("Profile load exception", e.getMessage());
            }

            /* add challenge id to receivers list */
            MyProfile receiversProfile = DynamoDbUtils.loadProfileByFacebookId(DynamoDbUtils.init(context), user.getFacebookId());
            try {
                Set set = new HashSet(receiversProfile.getReceivedChallengesList());
                set.add(challenge.getId());
                List list = new ArrayList(set);
                receiversProfile.setReceivedChallengesList(list);

                DynamoDbUtils.saveProfile(DynamoDbUtils.init(context), receiversProfile);
            }catch(Exception e){
                Log.e("Profile Load Exception", e.getMessage());
            }

            /*--------------------------------------------------------------------------*/

            /*make a post request to GCM */



            JSONObject obj1=new JSONObject();
            JSONObject obj2=new JSONObject();
            try {
                obj2.put("title",challenge.getTitle());
                obj2.put("text",challenge.getDescription());
                obj1.put("notification",obj2);
                obj1.put("challenge",challenge.getId());
                obj1.put("sender",user.getName());

                obj1.put("to",user.getGCMId());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection;
            String url;
            String data = obj1.toString();
            String result = null;
            try {
                //Connect
                urlConnection = (HttpURLConnection) ((new URL("https://gcm-http.googleapis.com/gcm/send").openConnection()));
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                /*Replace the key later it's not secure*/
                urlConnection.setRequestProperty("Authorization","key=AIzaSyB7fHG3C1WF3zM0F0ehzghbF2ULX0vaF7k");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                //Write
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(data);
                writer.close();
                outputStream.close();

                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                result = sb.toString();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Output is:"+result);




            return null;
        }
    }

}
