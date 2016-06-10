package com.example.android.csula.challengefriends;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.com.google.gson.Gson;
import com.example.android.csula.challengefriends.models.Challenge;
import com.example.android.csula.challengefriends.models.MyProfile;
import com.example.android.csula.challengefriends.models.User;
import com.example.android.csula.challengefriends.utils.DynamoDbUtils;
import com.example.android.csula.challengefriends.utils.PreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
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
    private User currentUser;

    public ContactActivityFragment() {
    }
    //menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout){
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void logout() {
        /* remove user's token */
        PreferenceUtils.clearUserToken(getActivity());
        /* remove current user */
        PreferenceUtils.clearCurrentUser(getActivity());

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();

        Intent intent = getActivity().getIntent();
        challenge = new Gson().fromJson(intent.getStringExtra("challenge").toString(), Challenge.class);

        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        contactsListView = (ListView) rootView.findViewById(R.id.listview_contacts);

        List<User> friends = new ArrayList(PreferenceUtils.getFriends(getActivity()).values());
        ArrayAdapter<User> adapter = new UserAdapter(getActivity(), R.id.textview_contact_item, (ArrayList<User>) friends);
        contactsListView.setAdapter(adapter);

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* save the challenge id in receivers received challenges and senders sent challenges */
                user = (User) parent.getItemAtPosition(position);
                Toast.makeText(context, challenge.getTitle() + " to " + user.getName(), Toast.LENGTH_SHORT).show();
                /*Save the challenges id in the dynamo db and send the challenge to the another user using gcm */

                AWSTask task = new AWSTask();

                task.execute();
            }
        });

        return rootView;
    }

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

            /* add (name + receiverFacebookId + challengeId) senders list */
            MyProfile sendersProfile = DynamoDbUtils.loadProfile(DynamoDbUtils.init(context), PreferenceUtils.getCurrentUser(context).getCognitoId());
            MyProfile receiversProfile = DynamoDbUtils.loadProfileByFacebookId(DynamoDbUtils.init(context), user.getFacebookId());
            try {
                /* save in dynamo db */
                Set set = new HashSet(sendersProfile.getSentChallengesList());
                set.add(receiversProfile.getFacebookId() + " " + challenge.getId());
                List list = new ArrayList(set);
                sendersProfile.setSentChallengesList(list);
                DynamoDbUtils.saveProfile(DynamoDbUtils.init(context), sendersProfile);
            } catch (Exception e) {
                Log.e("Profile load exception", e.getMessage());
            }

            /* add (name + senderFacebookId + challengeId) to receivers list */
            try {
                Set set = new HashSet(receiversProfile.getReceivedChallengesList());
                set.add(sendersProfile.getFacebookId() + " " + challenge.getId());
                List list = new ArrayList(set);
                receiversProfile.setReceivedChallengesList(list);

                DynamoDbUtils.saveProfile(DynamoDbUtils.init(context), receiversProfile);
            } catch (Exception e) {
                Log.e("Profile Load Exception", "Error");
            }

            /*--------------------------------------------------------------------------*/

            /*make a post request to GCM */
            JSONObject obj1 = new JSONObject();
            JSONObject obj2 = new JSONObject();
            JSONObject obj3 = new JSONObject();
            try {
                obj2.put("title", challenge.getTitle());
                obj2.put("text", challenge.getDescription());
                obj1.put("notification", obj2);
                obj1.put("to", receiversProfile.getGCMID());

                obj3.put("senderName", PreferenceUtils.getCurrentUser(context).getName());
                obj3.put("senderCognitoId", PreferenceUtils.getCurrentUser(context).getCognitoId());
                obj3.put("senderFacebookId", PreferenceUtils.getCurrentUser(context).getFacebookId());
                obj3.put("challenge", challenge.getId());


                obj1.put("data", obj3);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection;
            String data = obj1.toString();
            String result = null;
            try {
                urlConnection = (HttpURLConnection) ((new URL("https://gcm-http.googleapis.com/gcm/send").openConnection()));
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                /*Replace the key later it's not secure*/
                urlConnection.setRequestProperty("Authorization", "key=AIzaSyB7fHG3C1WF3zM0F0ehzghbF2ULX0vaF7k");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                //Write
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(data);
                writer.close();
                outputStream.close();
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
            System.out.println("Output: " + data);
            System.out.println("Result: " + result);

            return null;
        }
    }

}
