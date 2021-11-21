package com.example.loginauthfirebase;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddImageActivity extends AppCompatActivity {
    Button uploadBtn,shareBtn;
    EditText inputISO,inputF,inputMM,inputSpeed,inputDate;
    ImageView imageView;
    ProgressDialog progressDialog;
    FirebaseFirestore mStore;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseStorage mImageStore;
    Uri imageUri;
    String userID;

    //Khai bao de share
    CallbackManager callbackManager;
    ShareDialog shareDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);


        uploadBtn = findViewById(R.id.btnUpload);
        inputISO = findViewById(R.id.inputISO);
        inputF = findViewById(R.id.inputF);
        inputMM = findViewById(R.id.inputMM);
        inputSpeed = findViewById(R.id.inputSpeed);
        inputDate=findViewById(R.id.inputDate);
        progressDialog = new ProgressDialog(this);
        imageView=findViewById(R.id.imageView);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStore = FirebaseFirestore.getInstance();
        mImageStore = FirebaseStorage.getInstance();


        //Share link
        callbackManager = CallbackManager.Factory.create();
        shareBtn=(Button) findViewById(R.id.btnShareFB);
        shareDialog =new ShareDialog(this);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLinkContent shareLinkContent =new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://firebasestorage.googleapis.com/v0/b/login-auth-firebase-40e1b.appspot.com/o/1637418199115.jpg?alt=media&token=a63170b7-7579-43bb-a39b-cb9d31188ad2"))
                        .setShareHashtag(new ShareHashtag.Builder()
                                .setHashtag("#Aesthetic").build())
                        .build();//Cai link dang set cung, thay bang URL hinh` nao do vao
                if(ShareDialog.canShow(ShareLinkContent.class))
                {
                    shareDialog.show(shareLinkContent);
                }
            }
        });// Het share Link


        ActivityResultLauncher<Intent> imageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            // There are no request codes
                            Intent data = result.getData();
                            imageUri = data.getData();
                            imageView.setImageURI(imageUri);

                        }
                    }
                });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                imageResultLauncher.launch(galleryIntent);
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToFirebase(imageUri);
            }
        });
    }

    private void uploadToFirebase(Uri imageUri) {
        String ISO = inputISO.getText().toString();
        String f = inputF.getText().toString();
        String MM = inputMM.getText().toString();
        String Speed = inputSpeed.getText().toString();
        String Date = inputDate.getText().toString();

        if (imageUri == null) {
            Toast.makeText(AddImageActivity.this, "Please select Image", Toast.LENGTH_SHORT).show();
        } else if (ISO.isEmpty() || f.isEmpty() || MM.isEmpty() || Date.isEmpty()) {
            Toast.makeText(AddImageActivity.this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("Uploading Image...");
            progressDialog.setTitle("Upload");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            StorageReference fileRef = mImageStore.getReference().child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();
                            Model model = new Model(uri.toString(), ISO, f, MM, Speed, Date);
                            String markerID = "TestMarkerID";
                            //DocumentReference markerRef = mStore.collection("markers").document(markerID);
                            CollectionReference IMGRef = mStore.collection("markers").document(markerID).collection("IMG");

                            /*
                            Map<String, Object> marker = new HashMap<>();
                            marker.put("name", "Cho Da Lat");
                            marker.put("X","11.9428117");
                            marker.put("Y","108.4366088");
                            markerRef.set(marker).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AddImageActivity.this, "Add Marker Successful", Toast.LENGTH_SHORT).show();
                                }
                            });*/


                            IMGRef.add(model);
                            Toast.makeText(AddImageActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AddImageActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private String getFileExtension(Uri imageUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(imageUri));
    }

}