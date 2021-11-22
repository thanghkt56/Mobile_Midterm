package com.example.loginauthfirebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MarkerActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private ArrayList<Item> markerItems;
    private ItemsAdapter markerItemsAdapter;
    private ProgressDialog progressDialog;
    private FloatingActionButton floatingActionButton;
    FirebaseFirestore mStore;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String markerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        Initialize();
    }

    private void Initialize() {
        prepareProgressDialog();
        prepareAccount();
        prepareID();
        prepareView();
    }

    private void prepareProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading map ...");
        progressDialog.setTitle("Load");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void prepareID() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            markerID = extras.getString("markerID");
        }
    }

    private void prepareView() {
        floatingActionButton = (FloatingActionButton) findViewById(R.id.addButton);
        floatingActionButton.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.markerItems);
        prepareRecyclerView();
    }

    private void prepareRecyclerView() {
        CollectionReference itemRef=mStore
                .collection("markers")
                .document(markerID)
                .collection("IMG");
        markerItems = new ArrayList<Item>();
        getMarkerItemList(itemRef, this);
        markerItemsAdapter = new ItemsAdapter(markerItems, this, mUser, mAuth, mStore);

        recyclerView.setAdapter(markerItemsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getMarkerItemList(CollectionReference itemRef, Context context) {
        itemRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            progressDialog.dismiss();
                            return;
                        } else {
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                if (documentSnapshot.exists()) {
                                    Log.d(TAG, "onSuccess: DOCUMENT" + documentSnapshot.getId() + " ; " + documentSnapshot.getData());
                                    Log.d(TAG, String.valueOf(documentSnapshot.getData()));
                                    Item item = MarkerItemFromSnapshot(documentSnapshot);
                                    markerItems.add(item);
                                    markerItemsAdapter.notifyDataSetChanged();
                                }
                            }
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error getting data!", Toast.LENGTH_LONG).show();
            }
        });
    }

    Item MarkerItemFromSnapshot(DocumentSnapshot documentSnapshot) {
        Item item = new MarkerItem(String.valueOf(documentSnapshot.get("date")),
                String.valueOf(documentSnapshot.get("f")),
                String.valueOf(documentSnapshot.get("imageUrl")),
                String.valueOf(documentSnapshot.get("iso")),
                String.valueOf(documentSnapshot.get("mm")),
                String.valueOf(documentSnapshot.get("speed")));
        return item;
    }

    private void prepareAccount() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(this, AddImageActivity.class);
        startActivity(intent);
    }
}