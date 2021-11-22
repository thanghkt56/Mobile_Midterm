package com.example.loginauthfirebase;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView;
    private ArrayList<Item> savedItems;
    private ItemsAdapter savedItemsAdapter;
    FirebaseFirestore mStore;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog progressDialog;
    Button signOut;
    TextView infoView;

    public ProfileFragment() {
    }

    public ProfileFragment(FirebaseFirestore store, FirebaseAuth auth, FirebaseUser user) {
        mStore = store;
        mAuth = auth;
        mUser = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.savedItems);
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Loading profile ...");
        progressDialog.setTitle("Load");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        signOut = (Button) view.findViewById(R.id.idSignOut);
        signOut.setOnClickListener(this);
        infoView = (TextView) view.findViewById(R.id.emailText);
        prepareInfo();
        prepareRecyclerView();
        return view;
    }

    private void prepareInfo() {
        String userID = mUser.getUid();
        mStore.collection("users")
                .document(userID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.getString("email") != null)
                                    infoView.setText(document.getString("email"));
                            } else {
                                Log.d("LOGGER", "No such document");
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void prepareRecyclerView() {
        String userID = mUser.getUid();
        CollectionReference savedIMGRef=mStore
                .collection("users")
                .document(userID)
                .collection("favIMG");
        savedItems = new ArrayList<Item>();
        getSavedList(savedIMGRef, this.getContext());
        savedItemsAdapter = new ItemsAdapter(savedItems, this.getContext(), mUser, mAuth, mStore);

        recyclerView.setAdapter(savedItemsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void getSavedList(CollectionReference savedIMGRef, Context context) {
        Handler mainHandler = new Handler(this.getContext().getMainLooper());
//        Runnable removePB = new Runnable() {
//            @Override
//            public void run() {
//                progress(View.GONE)
//            }
//        };
        savedIMGRef.get()
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
                                    Item item = savedItemFromSnapshot(documentSnapshot);
                                    savedItems.add(item);
                                    savedItemsAdapter.notifyDataSetChanged();
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

    Item savedItemFromSnapshot(DocumentSnapshot documentSnapshot) {
        Item item = new SavedItem(String.valueOf(documentSnapshot.get("date")),
                String.valueOf(documentSnapshot.get("f")),
                String.valueOf(documentSnapshot.get("imageUrl")),
                String.valueOf(documentSnapshot.get("iso")),
                String.valueOf(documentSnapshot.get("mm")),
                String.valueOf(documentSnapshot.get("speed")));
        return item;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.idSignOut) {
            mAuth.signOut();
            Intent intent=new Intent(this.getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            //stop going back
            startActivity(intent);
        }
    }
}