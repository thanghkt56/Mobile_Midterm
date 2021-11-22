package com.example.loginauthfirebase;

public class Item {
    public String mDate, mF, mIso, mMm, mSpeed, mImageUrl;

    public Item(String date, String f, String imageUrl, String iso, String mm, String speed) {
        mDate= date;
        mF = f;
        mIso = iso;
        mMm = mm;
        mSpeed = speed;
        mImageUrl = imageUrl;
    }

    public Item(String imageUrl, String date) {
        mImageUrl = imageUrl;
        mDate = date;
    }

    public String toString() {
        return mDate + "            f" + mF + "  iso" + mIso + "  " + mSpeed + "s  " + mMm + "mm";
    }
}
