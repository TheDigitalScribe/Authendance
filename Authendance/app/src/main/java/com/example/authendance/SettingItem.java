package com.example.authendance;

import android.widget.ImageView;

public class SettingItem {

    private int mImageResource;
    private String mText1;
    private String mText2;

    public SettingItem(int imageResource, String text1, String text2) {
        mImageResource = imageResource;
        mText1 = text1;
        mText2 = text2;
    }

    public int getImageResource() {
        return mImageResource;
    }


    public String getmText1() {
        return mText1;
    }


    public String getmText2() {
        return mText2;
    }
}
