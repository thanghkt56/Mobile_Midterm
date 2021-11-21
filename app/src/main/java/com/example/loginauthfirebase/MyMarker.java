package com.example.loginauthfirebase;

import com.google.firebase.firestore.DocumentSnapshot;

public class MyMarker {
    public String ID, name, X, Y;

    public MyMarker(String id, String name, String x, String y) {
        this.ID = id;
        this.name = name;
        this.X = x;
        this.Y = y;
    }

    public MyMarker(DocumentSnapshot documentSnapshots) {
        ID = documentSnapshots.getId();
        name = documentSnapshots.getString("name");
        X = documentSnapshots.getString("X");
        Y = documentSnapshots.getString("Y");
    }
}
