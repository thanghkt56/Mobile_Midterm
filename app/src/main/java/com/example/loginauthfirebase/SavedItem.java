package com.example.loginauthfirebase;

public class SavedItem extends Item {
    public SavedItem(String imageUrl, String date, String f, String iso, String mm, String speed) {
        super(imageUrl, date, f, iso, mm, speed);
    }

    public SavedItem(String imageUrl, String date) {
        super(imageUrl, date);
    }
}
