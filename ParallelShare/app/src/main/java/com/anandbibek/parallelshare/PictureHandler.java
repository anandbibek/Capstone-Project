package com.anandbibek.parallelshare;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;


class PictureHandler {

    int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            //final int heightRatio = Math.round((float) height / (float) reqHeight);
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        Log.d("SampleSize", inSampleSize + "");
        return inSampleSize;
    }

    Bitmap decodeSampledBitmapFromUri(Context context, Uri uri, int reqWidth, int reqHeight, ImageView imgView) {

        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.d("not found","Exception");
        }
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is,null,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        //reopen to reset the inputStream
        try {
            is = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.d("not found","Exception");
        }
        ViewGroup.LayoutParams params = imgView.getLayoutParams();
        Log.d("samplesize msrs","width "+options.outWidth + " height "+ options.outHeight + " pheight " + reqWidth);
        params.height= (int)((double)(options.outHeight/options.inSampleSize)*((double)reqHeight/(options.outWidth/options.inSampleSize)));
        Log.d("samplesize msrs","width "+ (double)(reqHeight)/(options.outWidth/options.inSampleSize));
        imgView.setLayoutParams(params);
        return BitmapFactory.decodeStream(is,null,options);
    }

    public String uriToPath(Uri uri, Context mContext){
        String[] projection = {MediaStore.Images.Media.DATA};
        //CursorLoader loader = new CursorLoader(mContext,uri,proj,null,null,null);
        Cursor cursor = mContext.getContentResolver().query(uri,projection,null,null,null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }
}
