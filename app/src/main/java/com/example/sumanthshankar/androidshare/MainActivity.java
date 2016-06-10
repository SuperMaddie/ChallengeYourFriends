package com.example.sumanthshankar.androidshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 1;
    private CallbackManager callbackManager;
    private LoginManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Intent i = new Intent(Intent.ACTION_PICK);
        File p = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String p1 = p.getPath();
        Uri data = Uri.parse(p1);
        i.setDataAndType(data, "video/*");
        startActivityForResult(i, REQUEST_TAKE_GALLERY_VIDEO);
    }

}
