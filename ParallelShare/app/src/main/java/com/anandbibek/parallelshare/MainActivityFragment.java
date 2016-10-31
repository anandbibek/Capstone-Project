package com.anandbibek.parallelshare;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import static com.anandbibek.parallelshare.R.id.delete_btn;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    public static final String SAVED_FILE_PATH = "savedFilePath";

    public String filePath = "";

    CallbackManager callbackManager;
    LoginButton loginButton;

    ToggleHandler fbToggle = new ToggleHandler(),
            twiToggle = new ToggleHandler(),
            plusToggle = new ToggleHandler();
    Button twitterLoginButton, gPlusButton;
    EditText editText;
    TextView counter_textView;
    ImageView imageView;
    AlertDialog alertDialog;
    ProgressDialog pDialog;
    DialogHandler dialogHandler;
    View buttonView, deleteBtn, addImage;
    FloatingActionButton sendButton;

    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;

    SharedPreferences mSharedPreferences;

    public MainActivityFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        dialogHandler = new DialogHandler();
        dialogHandler.reset();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        buttonView = inflater.inflate(R.layout.login_button_layout, null);
        loginButton = (LoginButton) buttonView.findViewById(R.id.login_button);
        twitterLoginButton = (Button) buttonView.findViewById(R.id.twitter_button);
        gPlusButton = (Button) buttonView.findViewById(R.id.gplus_button);
        loginButton.setPublishPermissions("publish_actions");
        loginButton.setFragment(this);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setView(buttonView);
        alertDialog = dialogBuilder.create();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        twitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterLoginToggle();
            }
        });
        gPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                installPlus();
            }
        });
        upDateUi();

        fbToggle.initiate((ImageView)view.findViewById(R.id.fb_toggle),R.drawable.fb,R.drawable.fb_bw);
        twiToggle.initiate((ImageView)view.findViewById(R.id.twitter_toggle),R.drawable.twitter,R.drawable.twitter_bw);
        plusToggle.initiate((ImageView)view.findViewById(R.id.plus_toggle),R.drawable.plus,R.drawable.plus_bw);
        fbToggle.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSwitch(view);
            }
        });
        twiToggle.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSwitch(view);
            }
        });
        plusToggle.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSwitch(view);
            }
        });

        imageView = (ImageView) view.findViewById(R.id.image_view);
        editText = (EditText) view.findViewById(R.id.edit_text);
        sendButton = (FloatingActionButton)view.findViewById(R.id.sendButton);
        deleteBtn = view.findViewById(delete_btn);
        addImage = view.findViewById(R.id.image_toggle);
        counter_textView = (TextView)view.findViewById(R.id.text_view);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postAll();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage(view);
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
            @Override
            public void afterTextChanged(Editable editable) {
                counter_textView.setText(String.valueOf(editText.getText().toString().length()));
            }
        });

        if(savedInstanceState!=null){
            setImage(Uri.parse(savedInstanceState.getString(SAVED_FILE_PATH)), false);
        }

        //For ads
        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //Always display test ads on emulator
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //Specify device id by checking logcat to get test ads on mobiles
                .addTestDevice(getString(R.string.device_id))
                .build();
        mAdView.loadAd(adRequest);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_FILE_PATH, filePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isTwitterLoggedInAlready()) {
            Uri uri = getActivity().getIntent().getData();
            if (uri != null && uri.toString().startsWith(getString(R.string.twitter_callback_scheme)
                    +"://"+
                    getString(R.string.twitter_callback_host))) {
                String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
                new getTwitterCredentials().execute(verifier);
            }
        }
        handleIntent(getActivity().getIntent());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    public void handleIntent(final Intent intent){
        Log.wtf("INTENT",intent.toString());
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action)) {
            if(type.contains("image/")) {
                if (imageView==null)
                    setLayoutListener((Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM));
                else
                    setImage((Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM),true);
            }
            else if(!filePath.isEmpty())
                setImage(Uri.parse(filePath),false);
            if(intent.getStringExtra(Intent.EXTRA_TEXT)!= null)
                editText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
        getActivity().setIntent(new Intent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_accounts) {
            alertDialog.show();
        }
        if (id == R.id.action_save) {
            if(!editText.getText().toString().isEmpty()) {
                ContentValues values = new ContentValues();
                values.put(ProviderContract.DraftTable.COLUMN_NAME_CONTENT,
                        editText.getText().toString());
                values.put(ProviderContract.DraftTable.COLUMN_NAME_TIME,
                        DateUtils.formatDateTime(getActivity(),
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
                values.put(ProviderContract.DraftTable.COLUMN_NAME_SHARE, "1");
                Uri returnedUri = getActivity().getContentResolver()
                        .insert(ProviderContract.DraftTable.CONTENT_URI, values);
                if(returnedUri!=null) {
                    Toast.makeText(getApplicationContext(),
                            R.string.saved_to_drafts,
                            Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).notifyDrafts();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setLayoutListener(final Uri uri){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.getViewTreeObserver()
                        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
                                    imageView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                }
                                else
                                    imageView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);

                                setImage(uri,true);
                            }
                        });
            }
        },100);
    }

    private void postAll(){
        if(editText.getText().toString().isEmpty() && filePath.isEmpty())
            return;
        NetworkInfo networkInfo = ((ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            new android.app.AlertDialog.Builder(getActivity())
                    .setTitle(R.string.post_error_title)
                    .setMessage(R.string.no_network)
                    .setPositiveButton(getString(R.string.OK), null)
                    .show();
            return;
        }

        dialogHandler.reset();
        if(twiToggle.isChecked())
            new updateTwitterStatus().execute(editText.getText().toString());
        if(fbToggle.isChecked())
            if(hasPublishPermission())
                postToFacebook();
        if((plusToggle.isChecked()))
            postPlus();
    }

    private boolean isTwitterLoggedInAlready() {
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    private void upDateUi() {
        if(!isTwitterLoggedInAlready())
            twitterLoginButton.setText(getString(R.string.twitter_login));
        else
            twitterLoginButton.setText(R.string.logout_twitter);
    }

    //Twitter Login and Logout
    public void twitterLoginToggle() {
        // Check if already logged in
        if (!isTwitterLoggedInAlready()) {
            new twitterLogin().execute();

        } else {
            // user already logged into twitter
            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.remove(PREF_KEY_OAUTH_TOKEN);
            e.remove(PREF_KEY_OAUTH_SECRET);
            e.remove(PREF_KEY_TWITTER_LOGIN);
            e.apply();
            twitterLoginButton.setText(getString(R.string.twitter_login));
            Toast.makeText(getApplicationContext(),
                    R.string.Twitter_logged_out, Toast.LENGTH_LONG).show();
            twiToggle.setChecked(false);
        }
    }

    public void toggleSwitch(View view){
        switch (view.getId()){
            case R.id.fb_toggle :
                if(com.facebook.AccessToken.getCurrentAccessToken()!=null) {
                    if(hasPublishPermission())
                        fbToggle.toggle();
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.log_in_fb_req, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.twitter_toggle :
                if(isTwitterLoggedInAlready()) {
                    twiToggle.toggle();
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.log_in_twitter_req, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.plus_toggle :
                if(isPlusInstalled())
                    plusToggle.toggle();
                else
                    Toast.makeText(getApplicationContext(), R.string.install_gp_req, Toast.LENGTH_LONG).show();
                break;
        }
    }


    //Function to choose image
    public void setImage(Uri selectedImage, boolean process) {

        Log.wtf("RESULT",selectedImage+"");
        imageView.setVisibility(View.VISIBLE);
        PictureHandler pictureHandler = new PictureHandler();
        //after rotating device, processing has to be delayed to get correct dimensions
        if(process)
            imageView.setImageBitmap(pictureHandler.decodeSampledBitmapFromUri(
                    getActivity(), selectedImage, imageView.getWidth(), imageView.getWidth(), imageView));
        else
            imageView.setImageURI(selectedImage);
        filePath = selectedImage.toString();
        deleteBtn.setVisibility(View.VISIBLE);

    }

    public void selectImage() {
        ((MainActivity)getActivity()).selectImage();
    }

    public void removeImage(View v) {
        v.setVisibility(View.GONE);
        filePath = "";
        imageView.setImageURI(Uri.parse(""));
        imageView.setVisibility(View.INVISIBLE);
    }

    //Google Plus methods
    public void installPlus(){
        boolean installed = isPlusInstalled();
        String status = getString(R.string.gplus_msg) + "Google Plus app is " + (installed? "" : "not ") + "installed on your device.";
        if(installed)
            new android.app.AlertDialog.Builder(getActivity())
                    .setTitle("Posting to Google plus")
                    .setMessage(status)
                    .setPositiveButton("OK", null)
                    .show();
        else
            new android.app.AlertDialog.Builder(getActivity())
                    .setTitle("Posting to Google plus")
                    .setMessage(status)
                    .setPositiveButton("Get app", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.plus_link))));
                        }
                    })
                    .setNegativeButton("Cancel",null)
                    .show();
    }

    public boolean isPlusInstalled() {
        try{
            Intent i = new Intent(android.content.Intent.ACTION_SEND);
            i.setType("text/plain");
            List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(i,0);

            if(!list.isEmpty()) {
                for(ResolveInfo info : list){
                    if(info.activityInfo.packageName.toLowerCase().equals(getString(R.string.gplus_package_name))) {
                        return true;
                    }
                }
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }

    public void postPlus() {
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,editText.getText().toString());

        if(!filePath.equals(""))
            i.putExtra(Intent.EXTRA_STREAM,Uri.parse(filePath));

        i.setPackage(getString(R.string.gplus_package_name));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(Intent.createChooser(i,"Select"));
        startActivity(i);
    }


    //Twitter Login methods
    //to get auth url before authorization
    class twitterLogin extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.contacting_twitter));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... args) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(BuildConfig.TWITTER_API_KEY);
            builder.setOAuthConsumerSecret(BuildConfig.TWITTER_API_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();
            try {
                requestToken = twitter.getOAuthRequestToken(
                        getString(R.string.twitter_callback_scheme)+"://"+
                                getString(R.string.twitter_callback_host));
            } catch (TwitterException e) {
                Log.d("Twitter Login error",e.getErrorMessage());
                showLoginErrorDialog();
            }
            return "done";
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse(requestToken.getAuthenticationURL())));
        }

    }

    //to aSync retrieve credentials after auth
    class getTwitterCredentials extends AsyncTask<String, Void, AccessToken> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.finalizing));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected AccessToken doInBackground(String... args) {
            AccessToken accessToken = null;
            try {
                accessToken = twitter.getOAuthAccessToken(requestToken, args[0]);
            }catch (TwitterException e){
                Log.e("oauth verifying Error", e.getMessage());
                showLoginErrorDialog();
            }
            return accessToken;
        }

        protected void onPostExecute(final AccessToken accessToken) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            try{
                SharedPreferences.Editor e = mSharedPreferences.edit();

                e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                e.putString(PREF_KEY_OAUTH_SECRET,accessToken.getTokenSecret());
                e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                e.apply();
                twitterLoginButton.setText(R.string.logout_twitter);
            }catch (Exception npe){
                showLoginErrorDialog();
            }
        }

    }

    //Twitter posting
    class updateTwitterStatus extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(filePath.equals(""))
                dialogHandler.startTwit("Twitter : Posting...", getActivity());
            else
                dialogHandler.startTwit("Twitter : Uploading...", getActivity());
        }

        protected String doInBackground(String... args) {
            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(BuildConfig.TWITTER_API_KEY);
                builder.setOAuthConsumerSecret(BuildConfig.TWITTER_API_SECRET);
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                // Update status
                if(!filePath.equals("")) {
                    InputStream is = null;
                    try {
                        is = getActivity().getContentResolver()
                                .openInputStream(Uri.parse(filePath));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    twitter.updateStatus(new StatusUpdate(status).media(status,is));
                }else
                    twitter.updateStatus(status);
            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
                return  e.getMessage();
            }
            return "OK";
        }

        @Override
        protected void onPostExecute(final String res) {
            if(res.equals("OK"))
                dialogHandler.twitDone("Twitter : Posted successfully", getActivity());
            else
                dialogHandler.twitDone("Twitter : Error. " + res, getActivity());

        }
    }


    //facebook posting
    //Facebook posting aSync(fb SDK built in) way
    private void showPublishResult(boolean success, FacebookException error) {
        String alertMessage;
        alertMessage = (success)? getString(R.string.fb_successful_post)
                : getString(R.string.facebook)+ error.getMessage();
        dialogHandler.fbDone(alertMessage, getActivity());
    }

    private void postToFacebook(){
        final String message = editText.getText().toString();
        ShareApi shareApi;
        ShareContent content;
        if(filePath.equals("")){
            content = new ShareLinkContent.Builder().build();
        }
        else {
            Log.d("path", Uri.parse(filePath).getPath());
            Bitmap bmp = null;
            InputStream is;
            try {
                is = getActivity().getContentResolver().openInputStream(Uri.parse(filePath));
                bmp = BitmapFactory.decodeStream(is);
                Log.d("Bitmap method","success");
            } catch (FileNotFoundException fe) {
                Log.d("Bitmap method error",fe.getMessage());
                fe.printStackTrace();
            }

            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bmp)
                    .build();
            content = new SharePhotoContent.Builder()
                    .addPhoto(sharePhoto)
                    .build();

        }
        shareApi = new ShareApi(content);
        shareApi.setMessage(message);
        shareApi.share(new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                showPublishResult(true,null);
            }

            @Override
            public void onCancel() {
                showPublishResult(false,new FacebookException(getString(R.string.cancelled)));
            }

            @Override
            public void onError(FacebookException error) {
                showPublishResult(false, error);
            }
        });

        //show wait dialogue
        if(filePath.equals(""))
            dialogHandler.startFb("Facebook : Posting...", getActivity());
        else
            dialogHandler.startFb("Facebook : Uploading...", getActivity());
    }

    private boolean hasPublishPermission() {
        if(com.facebook.AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions"))
            return true;
        else
            LoginManager.getInstance().logInWithPublishPermissions(getActivity(),
                    Arrays.asList("publish_actions"));
        return false;
    }

    public void showLoginErrorDialog(){
        new android.app.AlertDialog.Builder(getActivity())
                .setTitle(R.string.done)
                .setMessage(getString(R.string.login_fail_msg))
                .setPositiveButton(R.string.OK, null)
                .show();
    }

}
