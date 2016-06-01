package com.example.android.csula.challengefriends;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.csula.challengefriends.utils.PreferenceUtils;

import java.util.Arrays;

public class MainActivityFragment extends Fragment {
    private static View rootView;
    private static ViewPager pager;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        /*CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getContext(),
                "us-east-1:764851a6-88d3-4ec3-932f-fa716472f6f8", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        final String userId = credentialsProvider.getIdentityId();*/

        pager = (ViewPager)rootView.findViewById(R.id.viewpager_main);
        pager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager()));

        /*ListView listView = (ListView)rootView.findViewById(R.id.listview_challenges);
        listView.setAdapter(challengeArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String challenge = challengeArrayAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), ContactActivity.class);
                startActivity(intent);

                //Toast.makeText(getActivity(), challenge, Toast.LENGTH_SHORT).show();
            }
        });*/

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

            switch(pos){
                case 0:
                    String[] mockData = {"challenge1", "challenge2", "challenge3", "challenge4", "challenge5"};
                    subFragment.setAdapterData(mockData);
                    break;
                case 1:
                    String[] mockData2 = {"rchallenge1", "rchallenge2", "rchallenge3", "rchallenge4", "rchallenge5"};
                    subFragment.setAdapterData(mockData2);
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
                    result =  "List Of Challenges";
                    break;
                case 1:
                    result =  "Recieved Challenges";
                    break;
            }
            return  result;
        }
    }

    public static class SubFragment extends Fragment {
        private int position;
        private ArrayAdapter adapter;
        private Context context;

        public SubFragment(){}

        public void setContext(Context context) {this.context = context;}

        public void setPosition(int position) {
            this.position = position;
        }

        public void setAdapterData(String[] mockData) {
            ArrayAdapter<String> ad2 = new ArrayAdapter<String>(context, R.layout.listview_item_challenge,
                    R.id.textview_challenge_item, Arrays.asList(mockData));
            adapter = ad2;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.sub_fragment_layout, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listview_sub_fragment);
            listView.setItemsCanFocus(true);

            listView.setAdapter(adapter);

            return rootView;
        }
    }
    /*----------------------------------------------------------*/
}
