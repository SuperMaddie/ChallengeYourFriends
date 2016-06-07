package com.example.android.csula.challengefriends;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.example.android.csula.challengefriends.models.MyProfile;
import com.example.android.csula.challengefriends.models.User;
import com.example.android.csula.challengefriends.utils.DynamoDbUtils;
import com.example.android.csula.challengefriends.utils.JsonUtils;
import com.example.android.csula.challengefriends.utils.PreferenceUtils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment {

    LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker tokenTracker;
    private ProfileTracker profileTracker;
    private Context context;
    private User currentUser;
    private AccessToken accessToken;

    public LoginActivityFragment() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };

        tokenTracker.startTracking();
        profileTracker.startTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        currentUser = new User();

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("user_friends"));
        loginButton.setFragment(this);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                accessToken = AccessToken.getCurrentAccessToken();
                /* save token in shared values */
                PreferenceUtils.setSharedValues(getString(R.string.user_token_key), accessToken.getToken(), getActivity());

                AWSCognitoTask awsCognitoTask = new AWSCognitoTask();
                awsCognitoTask.execute();

                /* get fb friends, save in preferences */
                String fbId = AccessToken.getCurrentAccessToken().getUserId();
                new GraphRequest(
                        accessToken,
                        "/" + fbId + "/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                PreferenceUtils.setFriends(JsonUtils.getFriends(response.getRawResponse()), context);
                            }
                        }
                ).executeAsync();

                /* go back to main activity */
                startMainActivity();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
               Log.e("error", error.toString());
            }
        });



        return view;
    }

    public void startMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    public class AWSCognitoTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            CognitoCachingCredentialsProvider credentialsProvider = DynamoDbUtils.init(getContext());

            /* set current user in preferences */
            new GraphRequest(
                    accessToken,
                    "/me",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            currentUser = PreferenceUtils.getCurrentUser(context);
                            User user = JsonUtils.getUserInfo(response.getRawResponse());
                            currentUser.setName(user.getName());
                            currentUser.setFacebookId(user.getFacebookId());
                        }
                    }
            ).executeAndWait();

            currentUser.setCognitoId(credentialsProvider.getIdentityId());
            PreferenceUtils.setCurrentUser(currentUser, context);

            /* add user info to dynamoDB profiles table */
            /*Map<String, AttributeValue> info = new HashMap<>();
            AttributeValue attributeValue = new AttributeValue();
            attributeValue.setS(currentUser.getCognitoId());
            info.put("userId", attributeValue);
            attributeValue = new AttributeValue();
            //attributeValue.setS(((TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number().toString());
            attributeValue.setS(currentUser.getFacebookId());
            info.put("facebook_id", attributeValue);*/

            MyProfile profile = DynamoDbUtils.loadProfile(credentialsProvider, currentUser.getCognitoId());

            /* if user's profile does not exist in db save it */
            if(profile == null) {
                profile = DynamoDbUtils.saveProfile(credentialsProvider, currentUser);
            }

            /*CognitoSyncManager syncClient = new CognitoSyncManager(
                    getContext(),
                    Regions.US_EAST_1, // Region
                    credentialsProvider);*/

            // Create a record in a dataset and synchronize with the server
            /*Dataset dataset = syncClient.openOrCreateDataset("myDataset");
            dataset.put("myKey", "myValue");
            dataset.synchronize(new DefaultSyncCallback() {
                @Override
                public void onSuccess(Dataset dataset, List newRecords) {
                    Log.e("dataset", dataset.get("myKey"));
                }
            });*/

            String fbToken = PreferenceUtils.getSharedValues(context.getString(R.string.user_token_key), context);

            Map<String, String> logins = new HashMap<String, String>();
            logins.put("graph.facebook.com", fbToken);
            credentialsProvider.setLogins(logins);

            return null;
        }


        /*@Override
        protected void onPostExecute(Void result){
            if(isAdded()){
                getResources().getString(R.string.app_name);
            }
        }*/
    }

}
