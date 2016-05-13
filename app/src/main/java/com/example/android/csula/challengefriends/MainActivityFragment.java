package com.example.android.csula.challengefriends;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final int MY_PERMISSION_REQUEST_READ_SMS = 1;

    public MainActivityFragment() {
        new AWSCognitoTask().execute();
    }

/*    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_READ_SMS : {
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    *//* permission was granted *//*
                }else {
                    *//* permission was denied *//*
                }
            }
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public class AWSCognitoTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            /* Initialize the Amazon Cognito credentials provider */
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getContext(),
                    "us-east-1:764851a6-88d3-4ec3-932f-fa716472f6f8", // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );

            /* add user id to shared preference */
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.edit().putString("USER_ID", credentialsProvider.getIdentityId()).commit();


            /* add user info to dynamoDB */
            User user = new User(credentialsProvider.getIdentityId(), "11111111");
            /*Map<String, AttributeValue> info = new HashMap<>();
            AttributeValue attributeValue = new AttributeValue();
            attributeValue.setS(credentialsProvider.getIdentityId());
            info.put("user_id", attributeValue);
            attributeValue = new AttributeValue();
            //attributeValue.setS(((TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number().toString());
            attributeValue.setS("Phone Number");
            info.put("phone_number", attributeValue);*/

            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            mapper.save(user);

            return null;
        }
    }
}
