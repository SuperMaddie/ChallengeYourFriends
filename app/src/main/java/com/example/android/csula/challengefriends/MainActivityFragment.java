package com.example.android.csula.challengefriends;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.com.google.gson.Gson;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.android.csula.challengefriends.models.Challenge;
import com.example.android.csula.challengefriends.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivityFragment extends Fragment {
    private static View rootView;
    private static ViewPager pager;
    private static ArrayAdapter<Challenge> challengeAdapter;
    private static Context context;
    private static List<Challenge> challenges;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        context = getActivity();
        updateChallenges();
    }

    public void updateChallenges(){
        challenges = new ArrayList();
        FetchChallengeTask task = new FetchChallengeTask();
        task.execute();
        //PreferenceUtils.setSharedValues("challenges", challenges);
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
        //LoginManager.getInstance().logOut();
        /* remove user's token */
        PreferenceUtils.clearUserToken(getActivity());
        /* remove */
        PreferenceUtils.clearCurrentUser(getActivity());

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();
        final String userToken = PreferenceUtils.getSharedValues(getString(R.string.user_token_key), getActivity());

        if(userToken == null) {
            /* redirect user to login */
            startLoginActivity();
        }

        challengeAdapter = new ChallengeAdapter(getActivity(), R.id.textview_challenge_item, new ArrayList<Challenge>());

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        pager = (ViewPager)rootView.findViewById(R.id.viewpager_main);
        pager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager()));

        return rootView;
    }

    public void startLoginActivity(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    /*----------------View Pager Custom Adapter----------------*/
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int pos) {
            SubFragment subFragment = new SubFragment();
            subFragment.setPosition(pos);
            subFragment.setContext(getActivity());
            List<String> data;
            switch(pos){
                case 0:
                    subFragment.setPosition(0);
                    break;
                case 1:
                    subFragment.setPosition(1);
                    break;
            }

            return subFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence result = "";
            switch (position) {
                case 0:
                    result =  "Challenges";
                    break;
                case 1:
                    result =  "Received Challenges";
                    break;
            }
            return  result;
        }
    }

    public static class SubFragment extends Fragment {
        private int position = 0;
        private Context context;
        public SubFragment(){}

        public void setContext(Context context) {this.context = context;}

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.sub_fragment_layout, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_sub_fragment);
            listView.setItemsCanFocus(true);

            switch(position){
                case 0:
                    listView.setAdapter(challengeAdapter);
                    break;
                case 1:
                    listView.setAdapter(challengeAdapter);
                    break;
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                    Challenge challenge = (Challenge)parent.getItemAtPosition(index);
                    switch(position){
                        /* if clicked on challenge get list of facebook friends from preferences here */
                        case 0:
                            Intent intent = new Intent(context, ContactActivity.class);
                            intent.putExtra("challenge", new Gson().toJson(challenge, Challenge.class));
                            startActivity(intent);
                            break;
                        /* if clicked on received challenge open video sharing */
                        case 1:
                            break;
                    }
                }
            });

            return rootView;
        }
    }
    /*----------------------------------------------------------*/

    public static class FetchChallengeTask extends AsyncTask<Void, Void, Challenge[]>{
        @Override
        protected Challenge[] doInBackground(Void... params) {
            /* test DynamoDB */
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    "us-east-1:764851a6-88d3-4ec3-932f-fa716472f6f8", // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            /*ScanRequest scanRequest = new ScanRequest()
                    .withTableName("challenges");
            ScanResult result = ddbClient.scan(scanRequest);
            for (Map<String, AttributeValue> item : result.getItems()) {
                Log.e("item", item.toString());
            }*/

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList result = mapper.scan(
                    Challenge.class,
                    scanExpression);
            for(int i = 0; i<result.size(); i++){
                challenges.add((Challenge)result.get(i));
            }

            return challenges.toArray(new Challenge[0]);
        }

        @Override
        protected void onPostExecute(Challenge[] challenges) {
            if(challenges != null) {
                challengeAdapter.clear();
                for(Challenge c: challenges) {
                    challengeAdapter.add(c);
                }
            }
        }
    }

    /* custom challenge adapter */
    public class ChallengeAdapter extends ArrayAdapter<Challenge> {
        private ViewHolder viewHolder;

        private class ViewHolder {
            private TextView itemView;
        }

        public ChallengeAdapter(Context context, int textViewResourceId, ArrayList<Challenge> items) {
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

            Challenge item = getItem(position);
            if (item != null) {
                viewHolder.itemView.setText(item.getDescription());
            }

            return convertView;
        }
    }
}
