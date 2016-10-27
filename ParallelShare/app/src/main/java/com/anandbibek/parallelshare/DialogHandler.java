package com.anandbibek.parallelshare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;


class DialogHandler {
    private boolean fb,twitter,showFb,showTwit,dialog;
    private String fbString,twitString;
    private ProgressDialog pDialog;

    public void startFb(String arg, Context context){
        fb = true;
        showFb = true;
        fbString = arg;
        resetDialog(context);
    }

    public void startTwit(String arg, Context context){
        twitter = true;
        showTwit = true;
        twitString = arg;
        resetDialog(context);
    }

    public void twitDone(String arg, Context context){
        twitString = arg;
        twitter = false;
        if(fb)
            resetDialog(context);
        else
            endDialog(context);
    }

    public void fbDone(String arg, Context context){
        fbString = arg;
        fb = false;
        if(twitter)
            resetDialog(context);
        else
            endDialog(context);
    }

    void reset(){
        fb=false;
        twitter=false;
        showFb=false;
        showTwit=false;
        dialog=false;
        twitString="";
        fbString="";
    }

    public void resetDialog(Context context){
        if(dialog) {
            pDialog.dismiss();
            dialog=false;
        }
        pDialog = new ProgressDialog(context);
        dialog = true;

        String temp = "";
        if(showTwit && showFb)
            temp = twitString + "\n" + fbString;
        else if(showTwit)
            temp = twitString;
        else if(showFb)
            temp = fbString;

        pDialog.setMessage(temp);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void endDialog(Context context){

        String message = "";
        if(showTwit && showFb)
            message = twitString + "\n" + fbString;
        else if(showTwit)
            message = twitString;
        else if(showFb)
            message = fbString;

        if(dialog) {
            pDialog.dismiss();
            dialog=false;
        }

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.done))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.OK), null)
                .show();
    }
}
