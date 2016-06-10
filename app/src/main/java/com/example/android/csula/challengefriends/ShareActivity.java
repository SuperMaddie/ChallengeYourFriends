package com.example.android.csula.challengefriends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.amazonaws.com.google.gson.Gson;
import com.example.android.csula.challengefriends.utils.PreferenceUtils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ShareActivity extends Activity {

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 1;
    private static final int RESULT_OK = -1;
    private static Context context;

    @Override
    protected void onStart() {
        super.onStart();
        context = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        Intent i = new Intent(Intent.ACTION_PICK);
        File p = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String p1 = p.getPath();
        Uri data = Uri.parse(p1);
        i.setDataAndType(data, "video/*");
        startActivityForResult(i, REQUEST_TAKE_GALLERY_VIDEO);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("video/*");
                Uri uri = data.getData();
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share via"));

                /*ShareViedoTask task = new ShareViedoTask();
                task.execute(getRealPathFromURI(context, uri));*/
            }
        }
    }

    public byte[] readBytes(String dataPath) throws IOException {
        InputStream inputStream = new FileInputStream(dataPath);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    public class ShareViedoTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            AccessToken accessToken = new Gson().fromJson(PreferenceUtils.getSharedValues("facebook_access_token", context),
                    AccessToken.class);
            String fbId = PreferenceUtils.getCurrentUser(context).getFacebookId();

            /*new GraphRequest(
                    accessToken,
                    "/" + fbId + "/videos",
                    null,
                    HttpMethod.POST,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {

                        }
                    }
            ).executeAndWait();*/

            GraphRequest.Callback callback = new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {

                }
            };

            GraphRequest request = GraphRequest.newPostRequest(accessToken, "me/videos", null, callback);
            Bundle params2 = request.getParameters();
            try {
                String s = params[0];
                byte[] data = readBytes(params[0]);
                params2.putByteArray("video.mp4", data);
                params2.putString("title", "album");
                params2.putString("description", " #SomeTag");
                request.setParameters(params2);
                request.executeAndWait();
            }
            catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
