package com.example.android.csula.challengefriends;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.example.android.csula.challengefriends.data.ChallengeFriendContract;
import com.example.android.csula.challengefriends.data.ChallengeFriendHelper;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Savin on 6/4/2016.
 */
public class PushNotificationService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        System.out.println(data.toString());
        Bundle notification = (Bundle) data.get("notification");

        String title = notification.getString("title");
        String description = notification.getString("body");

        String senderName = data.getString("senderName");
        String senderFacebookId = data.getString("senderFacebookId");
        String id = data.getString("challenge");

       /* Update the database table received entry */
        ContentValues receivedChallenge = new ContentValues();
        receivedChallenge.put(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_ID, senderFacebookId + id);
        receivedChallenge.put(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_TITLE, title);
        receivedChallenge.put(ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_DESCRIPTION, description);
        receivedChallenge.put(ChallengeFriendContract.ReceivedChallenges.COLUMN_SENDER_NAME, senderName);

        /*Putting the values in the sqlLite Database*/
        ChallengeFriendHelper dbHelper = new ChallengeFriendHelper(getBaseContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            long rowId = db.insert(ChallengeFriendContract.ReceivedChallenges.TABLE_NAME, null, receivedChallenge);
        }catch(Exception e){
            Log.e("Insert Error: ", e.getMessage());
        }
        db.close();
    }
}
