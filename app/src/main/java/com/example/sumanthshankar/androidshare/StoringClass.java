package com.example.sumanthshankar.androidshare;


public class StoringClass {
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionsNeeded = Array.asList("publish_actions");
        manager = LoginManager.getInstance();
        manager.logInWithPublishPermissions(this, "publish_actions");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        /*String urlToShare = "https://www.youtube.com/watch?v=ViwazAAR-vE";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }
        //startActivity(intent);
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
                startActivity(Intent.createChooser(share, "Share via"));
            }
        }
    }*/
}
