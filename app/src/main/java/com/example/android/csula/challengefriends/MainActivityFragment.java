package com.example.android.csula.challengefriends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.example.android.csula.challengefriends.models.Challenge;
import com.example.android.csula.challengefriends.utils.DynamoDbUtils;
import com.example.android.csula.challengefriends.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivityFragment extends Fragment {
    private static View rootView;
    private static ViewPager pager;
    private static ArrayAdapter<Challenge> challengeAdapter;
    private static ArrayAdapter<Challenge> receivedChallengeAdapter;
    private static Context context;

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
    }

    public void updateChallenges(){
        FetchChallengeTask task = new FetchChallengeTask();
        task.execute();
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
        //PreferenceUtils.clearCurrentUser(getActivity());

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final String userToken = PreferenceUtils.getSharedValues(getString(R.string.user_token_key), getActivity());

        if(userToken == null) {
            /* redirect user to login */
            startLoginActivity();
        }else {
            System.out.println("User token is: "+userToken);
            challengeAdapter = new ChallengeAdapter(getActivity(), R.id.textview_challenge_item, new ArrayList<Challenge>());
            receivedChallengeAdapter = new ChallengeAdapter(getActivity(), R.id.textview_challenge_item, new ArrayList<Challenge>());

            updateChallenges();

            pager = (ViewPager) rootView.findViewById(R.id.viewpager_main);
            pager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager()));
        }

        return rootView;
    }

    public void startLoginActivity(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
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
            subFragment.setContext(context);
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
                    listView.setAdapter(receivedChallengeAdapter);
                    break;
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                    Challenge challenge = (Challenge)parent.getItemAtPosition(index);
                    switch(position){
                        /* if clicked on a challenge, show list of facebook friends */
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

    public static class FetchChallengeTask extends AsyncTask<Void, Void, Object[]>{
        @Override
        protected Object[] doInBackground(Void... params) {
            /* retrieve challenges */
            List<Challenge> challenges;
            List<Challenge> receivedChallenges;
            CognitoCachingCredentialsProvider credentialsProvider = DynamoDbUtils.init(context);
            System.out.println("Credential Provider :"+credentialsProvider.toString());
            System.out.print("Current User is:"+PreferenceUtils.getCurrentUser(context).getName());
            challenges = DynamoDbUtils.getChallenges(credentialsProvider);

            receivedChallenges = DynamoDbUtils.getReceivedChallenges(credentialsProvider, PreferenceUtils.getCurrentUser(context));

            return new Object[]{challenges, receivedChallenges};
        }

        @Override
        protected void onPostExecute(Object[] result) {
            List<Challenge> challenges;
            List<Challenge> receivedChallenges;
            if(result[0] != null) {
                challenges = (ArrayList<Challenge>)result[0];
                challengeAdapter.clear();
                for(Challenge c: challenges) {
                    System.out.println("Challenge:"+c.getTitle());
                    challengeAdapter.add(c);
                }
            }
            if(result[1] != null){
                receivedChallenges = (ArrayList<Challenge>)result[1];
                receivedChallengeAdapter.clear();
                for(Challenge c: receivedChallenges) {
                    receivedChallengeAdapter.add(c);
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
