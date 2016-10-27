package com.anandbibek.parallelshare;

import android.view.View;
import android.widget.ImageView;


public class ToggleHandler {

    private boolean checked;
    private ImageView imageView;
    private int imageId_c;
    private int imageId_u;

    public void setOnClick(View.OnClickListener listener){
        imageView.setOnClickListener(listener);
    }
    public ToggleHandler(){
        checked = false;
        imageView = null;
        imageId_c = 0;
        imageId_u = 0;
    }

    public void initiate(ImageView imgView, int imgId_checked, int imgId_unchecked){
        imageView = imgView;
        imageId_c = imgId_checked;
        imageId_u = imgId_unchecked;
        checked = false;
    }

    public boolean isChecked(){
        return checked;
    }

    public void setChecked(boolean b) {
        if(b) {
            checked = true;
            imageView.setImageResource(imageId_c);
        }
        else {
            checked = false;
            imageView.setImageResource(imageId_u);
        }
    }

    public void toggle(){
        if(checked) {
            checked = false;
            imageView.setImageResource(imageId_u);
        }
        else {
            checked = true;
            imageView.setImageResource(imageId_c);
        }
    }
}
