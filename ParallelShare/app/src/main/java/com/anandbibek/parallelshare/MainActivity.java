package com.anandbibek.parallelshare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity  implements DraftFragment.Communicator  {

    public static final String DRAFTFRAGMENT = "DRAFTFRAGMENT";
    public static final String POSTFRAGMENT = "POSTFRAGMENT";
    MainActivityFragment activityFragment;
    DraftFragment draftFragment;
    static final int SELECT_PHOTO = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        AppEventsLogger.activateApp(getApplication());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        activityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentByTag(POSTFRAGMENT);
        if(activityFragment==null)
            activityFragment = new MainActivityFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, activityFragment, POSTFRAGMENT)
                .commit();

        //add the drafts fragment if we are in two pane layout
        if (getResources().getBoolean(R.bool.has_two_panes)){
            draftFragment = (DraftFragment) getSupportFragmentManager().findFragmentByTag(DRAFTFRAGMENT);
            if(draftFragment==null)
                draftFragment = new DraftFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_second, draftFragment, DRAFTFRAGMENT)
                    .commit();
        }
        MobileAds.initialize(getApplicationContext(), getString(R.string.app_ad_id));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.action_drafts){
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("")
                    .replace(R.id.fragment, new DraftFragment())
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

    public void notifyDrafts(){
        if (getResources().getBoolean(R.bool.has_two_panes)){
            if(draftFragment!=null){
                draftFragment.restartLoader();
            }
        }
    }

    public void selectImage() {
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== SELECT_PHOTO && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            activityFragment.setImage(selectedImage, true);
        }
    }

    @Override
    public void onSendIntent(Intent i) {
        setIntent(i);
        activityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentByTag(POSTFRAGMENT);
        if(activityFragment==null)
            activityFragment = new MainActivityFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, activityFragment, POSTFRAGMENT)
                .commit();
        //activityFragment.handleIntent(i);
    }
}
