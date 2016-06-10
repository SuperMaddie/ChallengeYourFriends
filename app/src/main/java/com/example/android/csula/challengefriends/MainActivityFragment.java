package com.example.android.csula.challengefriends;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import com.example.android.csula.challengefriends.data.ChallengeFriendContract;
import com.example.android.csula.challengefriends.data.ChallengeFriendHelper;
import com.example.android.csula.challengefriends.models.Challenge;
import com.example.android.csula.challengefriends.utils.DynamoDbUtils;
import com.example.android.csula.challengefriends.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivityFragment extends Fragment {
    private static View rootView;
    private static ViewPager pager;
    private static ArrayAdapter<Challenge> challengeAdapter;
    private static ArrayAdapter<Challenge> receivedChallengeAdapter;
    private static Context context;
    private static ChallengeFriendHelper dbHelper;
    private static SQLiteDatabase db;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        context = getActivity();
        /*if(validUser()) {
            updateChallengesFromLocalDB();
        }*/
    }


    @Override
    public void onStart() {
        super.onStart();

        context = getActivity();
        /*if(validUser()) {
            updateChallengesFromLocalDB();
        }*/
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
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

    public boolean validUser() {
        final String userToken = PreferenceUtils.getSharedValues(getString(R.string.user_token_key), context);
        return (userToken == null) ? false : true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity();
        /*----------------------------------------*/
        rootView = inflater.inflate(R.layout.fragment_main, container, false);


        if (!validUser()) {
            /* redirect user to login */
            startLoginActivity();
        } else {
            /*Idea here is to read from SQLite database */
            updateChallengesFromLocalDB();

            pager = (ViewPager) rootView.findViewById(R.id.viewpager_main);
            pager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager()));
        }

        return rootView;
    }

    public void updateChallengesFromLocalDB(){
        challengeAdapter = new ChallengeAdapter(getActivity(), R.id.textview_challenge_item, new ArrayList<Challenge>());
        receivedChallengeAdapter = new ChallengeAdapter(getActivity(), R.id.textview_challenge_item, new ArrayList<Challenge>());

        dbHelper = new ChallengeFriendHelper(context);
        db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("select * from " + ChallengeFriendContract.Challenges.TABLE_NAME, null);
        if (c.moveToFirst()) {
            challengeAdapter.clear();
            do {
                int index = c.getColumnIndex(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_TITLE);
                String challengeTitle = c.getString(index);

                index = c.getColumnIndex(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_DESCRIPTION);
                String challengeDescription = c.getString(index);

                index = c.getColumnIndex(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_ID);
                String challengeId = c.getString(index);

                Challenge challenge = new Challenge();
                challenge.setId(challengeId);
                challenge.setTitle(challengeTitle);
                challenge.setDescription(challengeDescription);

                challengeAdapter.add(challenge);
            } while (c.moveToNext());
        } else {
            updateChallenges();
        }

        Cursor c2 = db.rawQuery("select * from " + ChallengeFriendContract.ReceivedChallenges.TABLE_NAME, null);
        if (c2.moveToFirst()) {
            receivedChallengeAdapter.clear();
            do {
                int index = c2.getColumnIndex(ChallengeFriendContract.ReceivedChallenges.COLUMN_SENDER_NAME);
                String senderName = c2.getString(index);

                index = c2.getColumnIndex(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_TITLE);
                String challengeTitle = c2.getString(index);

                index = c2.getColumnIndex(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_DESCRIPTION);
                String challengeDescription = c2.getString(index);

                index = c2.getColumnIndex(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_ID);
                String challengeId = c2.getString(index);

                Challenge challenge = new Challenge();
                challenge.setId(challengeId);
                challenge.setTitle(challengeTitle);
                challenge.setDescription(challengeDescription);
                challenge.setSenderName(senderName);

                receivedChallengeAdapter.add(challenge);
            } while (c2.moveToNext());
        } else {
            updateReceivedChallenges();
        }
    }

    public void updateChallenges() {
        FetchChallengeTask task = new FetchChallengeTask();
        task.execute();
    }

    public void updateReceivedChallenges() {
        FetchReceivedChallengeTask task = new FetchReceivedChallengeTask();
        task.execute();
    }

    public void startLoginActivity() {
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
            switch (pos) {
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
                    result = "Challenges";
                    break;
                case 1:
                    result = "Received Challenges";
                    break;
            }
            return result;
        }
    }

    public static class SubFragment extends Fragment {
        private int position = 0;
        private Context context;

        public SubFragment() {
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.sub_fragment_layout, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_sub_fragment);
            listView.setItemsCanFocus(true);

            switch (position) {
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
                    Challenge challenge = (Challenge) parent.getItemAtPosition(index);
                    switch (position) {
                        /* if clicked on a challenge, show list of facebook friends */
                        case 0:
                            Intent intent = new Intent(context, ContactActivity.class);
                            intent.putExtra("challenge", new Gson().toJson(challenge, Challenge.class));
                            startActivity(intent);
                            break;
                        /* if clicked on received challenge open video sharing */
                        case 1:
                            Intent intent2 = new Intent(context, ShareActivity.class);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent2);
                            break;
                    }
                }
            });

            return rootView;
        }
    }

    /*----------------------------------------------------------*/
    public static class FetchChallengeTask extends AsyncTask<Void, Void, List<Challenge>> {
        @Override
        protected List<Challenge> doInBackground(Void... params) {
            /* retrieve challenges */
            List<Challenge> challenges;
            CognitoCachingCredentialsProvider credentialsProvider = DynamoDbUtils.init(context);
            challenges = DynamoDbUtils.getChallenges(credentialsProvider);

            return challenges;
        }

        @Override
        protected void onPostExecute(List<Challenge> challenges) {
            challengeAdapter.clear();
            for (Challenge c : challenges) {
                challengeAdapter.add(c);
                /*Creating the values for database*/
                ContentValues challenge = new ContentValues();
                challenge.put(ChallengeFriendContract.Challenges.COLUMN_CHALLENGE_ID, c.getId());
                challenge.put(ChallengeFriendContract.Challenges.COLUMN_CHALLENGE_TITLE, c.getTitle());
                challenge.put(ChallengeFriendContract.Challenges.COLUMN_CHALLENGE_DESCRIPTION, c.getDescription());
                /*Putting the values in the sqlLite Database*/
                try {
                    long rowId = db.insert(ChallengeFriendContract.Challenges.TABLE_NAME, null, challenge);
                } catch (Exception e) {
                    Log.e("Insert Error", e.getMessage());
                }
            }
        }
    }

    public static class FetchReceivedChallengeTask extends AsyncTask<Void, Void, List<Challenge>> {
        @Override
        protected List<Challenge> doInBackground(Void... params) {
            /* retrieve received challenges */
            List<Challenge> receivedChallenges;
            CognitoCachingCredentialsProvider credentialsProvider = DynamoDbUtils.init(context);

            receivedChallenges = DynamoDbUtils.getReceivedChallenges(credentialsProvider, PreferenceUtils.getCurrentUser(context));

            return receivedChallenges;
        }

        @Override
        protected void onPostExecute(List<Challenge> receivedChallenges) {
            receivedChallengeAdapter.clear();
            for (Challenge c : receivedChallenges) {
                receivedChallengeAdapter.add(c);

                /*Creating the values for database*/
                ContentValues receivedChallenge = new ContentValues();
                receivedChallenge.put(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_ID, c.getSenderId() + c.getId());
                receivedChallenge.put(ChallengeFriendContract.ReceivedChallenges.COLUMN_SENDER_NAME, c.getSenderName());
                receivedChallenge.put(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_TITLE, c.getTitle());
                receivedChallenge.put(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_DESCRIPTION, c.getDescription());

                /*Putting the values in the sqlLite Database*/
                try {
                    long rowId = db.insert(ChallengeFriendContract.ReceivedChallenges.TABLE_NAME, null, receivedChallenge);
                } catch (Exception e) {
                    Log.e("Insert Error", e.getMessage());
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
                viewHolder.itemView.setText(item.toString());
            }

            return convertView;
        }
    }
}
