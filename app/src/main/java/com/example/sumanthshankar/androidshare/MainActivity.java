package com.example.sumanthshankar.androidshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.facebook.FacebookSdk;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public int REQUEST_TAKE_GALLERY_VIDEO = 0;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("video/*");
                Uri uri = data.getData();
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share Video on"));
            }
        }
    }
}
