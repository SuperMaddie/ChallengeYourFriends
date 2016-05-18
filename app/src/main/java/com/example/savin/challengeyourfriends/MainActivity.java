package com.example.savin.challengeyourfriends;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=(Button) findViewById(R.id.notify);
    }

    public void getNotification(View view){
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent=new Intent(this,ResultActivity.class);



        Notification notify=new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Hello Savin")
                .setContentText("This is my first notification service")
            //    .setContentIntent(pendingIntent)
                .build();

                notificationManager.notify(0,notify);




    }
}
