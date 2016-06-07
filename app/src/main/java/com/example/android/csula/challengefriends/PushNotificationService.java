package com.example.android.csula.challengefriends;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.android.csula.challengefriends.utils.DynamoDbUtils;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Savin on 6/4/2016.
 */
public class PushNotificationService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
       /*Update the database table received entry*/


        //create a notification here
    }
}
