package com.example.android.csula.challengefriends;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.com.google.gson.Gson;
import com.amazonaws.com.google.gson.JsonObject;
import com.example.android.csula.challengefriends.data.ChallengeFriendContract;
import com.example.android.csula.challengefriends.data.ChallengeFriendHelper;
import com.example.android.csula.challengefriends.utils.DynamoDbUtils;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Savin on 6/4/2016.
 */
public class PushNotificationService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        JSONObject notification=(JSONObject) data.get("notification");
        String title=null,description=null;
        try {
            title=notification.getString("title");
            description=notification.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String id=data.getString("challenge");



        String message = data.getString("message");

       /*Update the database table received entry*/
        ContentValues receivedChallenge=new ContentValues();
        receivedChallenge.put(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_ID,id);
        receivedChallenge.put(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_TITLE,title);
        receivedChallenge.put(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_DESCRIPTION,description);

        /*Putting the values in the sqlLite Database*/
        ChallengeFriendHelper dbHelper = new ChallengeFriendHelper(getBaseContext());
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        long rowId=db.insert(ChallengeFriendContract.ReceivedChallenges.TABLE_NAME,null,receivedChallenge);
        Log.v("Recepient","Insertion Success "+rowId);
        db.close();
        //create a notification here
    }
}
