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

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
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
    private static ArrayAdapter<String> adapter;
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
        PreferenceUtils.setSharedValues(getString(R.string.user_token_key), null, getActivity());
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final String userToken = PreferenceUtils.getSharedValues(getString(R.string.user_token_key), getActivity());

        if(userToken == null) {
            /* redirect user to login */
            startLoginActivity();
        }

        adapter = new ArrayAdapter(getActivity(), R.layout.listview_item_challenge,
                R.id.textview_challenge_item, new ArrayList<String>());

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
                    //String[] mockData = {"challenge1", "challenge2", "challenge3", "challenge4", "challenge5"};
                    //subFragment.setAdapter(adapter);
                    break;
                case 1:
                    //String[] mockData2 = {"rchallenge1", "rchallenge2", "rchallenge3", "rchallenge4", "rchallenge5"};
                    //subFragment.setAdapter(adapter);
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
        private int position;
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

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });

            return rootView;
        }
    }
    /*----------------------------------------------------------*/

    public static class FetchChallengeTask extends AsyncTask<Void, Void, String[]>{
        @Override
        protected String[] doInBackground(Void... params) {
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

            List<String> data = new ArrayList<>();
            for(Challenge ch: challenges){
                data.add(ch.toString());
            }

            return data.toArray(new String[0]);
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if(strings != null) {
                adapter.clear();
                for(String s: strings) {
                    adapter.add(s);
                }
            }
        }
    }
}
