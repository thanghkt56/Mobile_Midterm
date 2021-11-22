package com.example.loginauthfirebase;

import static android.content.ContentValues.TAG;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private SearchView searchView;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ArrayList<MyMarker> myMarkers;
    private ProgressDialog progressDialog;
    private View mView;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            LatLng ChoDaLat = new LatLng(11.942636, 108.436915);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ChoDaLat, 18));
            Initialize(mView);
        }
    };

    private void setListener() {
        mMap.setOnMarkerClickListener(this);
    }

    public MapsFragment(FirebaseFirestore store, FirebaseAuth auth, FirebaseUser user) {
        mStore = store;
        mAuth = auth;
        mUser = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    private void Initialize(View view) {
        setListener();
        prepareSearchBox(view);
        prepareMarkers(view);
    }

    private void prepareMarkers(View view) {
        myMarkers = new ArrayList<MyMarker>();
        CollectionReference markersRef = mStore.collection("markers");
        Log.d(TAG, ":(((((((((((((((((((((((((((91");
        AddAllMarkers(view);
    }

    private void DisplayAllMarkers() {
        for(MyMarker myMarker: myMarkers) {
            LatLng pos = new LatLng(Double.valueOf(myMarker.X), Double.valueOf(myMarker.Y));
            MarkerOptions option = new MarkerOptions().position(pos)
                    .title(myMarker.name);
            Marker addedMarker = mMap.addMarker(option);
            addedMarker.showInfoWindow();
            addedMarker.setTag(myMarker.ID);
        }
        progressDialog.dismiss();
    }

    private void AddAllMarkers(View view) {
        mStore.collection("markers").get()
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
                                    MyMarker marker = new MyMarker(documentSnapshot);
                                    myMarkers.add(marker);
                                }
                            }
                            DisplayAllMarkers();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(view.getContext(), "Error getting data!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void prepareSearchBox(View view) {
        searchView = (SearchView) view.findViewById(R.id.idSearchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();

                List<Address> addressList = null;

                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {
                    // on below line we are creating and initializing a geo coder.
                    Geocoder geocoder = new Geocoder(view.getContext());
                    Address address = null;
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                        address = addressList.get(0);
                    } catch (Exception e) {
                        return false;
                    }
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Loading map ...");
        progressDialog.setTitle("Load");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mView = view;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        String markerID = (String) marker.getTag();
        if (marker.equals("")) {
            return false;
        }
        Intent intent = new Intent(this.getContext(), MarkerActivity.class);
        intent.putExtra("markerID", markerID);
        startActivity(intent);
        return false;
    }
}