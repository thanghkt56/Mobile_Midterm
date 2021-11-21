package com.example.loginauthfirebase;

public class MarkerItem extends Item {
    public MarkerItem(String date, String f, String imageUrl, String iso, String mm, String speed) {
        super(date, f, imageUrl, iso, mm, speed);
    }

    public MarkerItem(String imageUrl, String date) {
        super(imageUrl, date);
    }
}
