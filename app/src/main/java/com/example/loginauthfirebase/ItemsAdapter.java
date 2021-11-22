package com.example.loginauthfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {
    abstract class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class SavedItemViewHolder extends ItemViewHolder {
        public ImageView imageView;
        public TextView infoText;
        public Bitmap bitmap;

        public SavedItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            infoText = (TextView) itemView.findViewById(R.id.infoText);
        }

        public void bind(Item item, Context context) throws IOException {
            Handler mainHandler = new Handler(context.getMainLooper());
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try  {
                        bitmap = BitmapFactory.
                                decodeStream((InputStream)new URL(item.mImageUrl)
                                        .getContent());
                        mainHandler.post(new Runnable() {
                         @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                                infoText.setText(item.toString());
                            }
                         });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    class MarkerItemViewHolder extends ItemViewHolder {
        public Button shareButton, likeButton;
        public ImageView imageView;
        public TextView infoText;
        public Bitmap bitmap;
        CallbackManager callbackManager;
        ShareDialog shareDialog;

        public MarkerItemViewHolder(@NonNull View itemView) {
            super(itemView);
            infoText = (TextView) itemView.findViewById(R.id.infoText);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            likeButton = (Button) itemView.findViewById(R.id.likeButton);
            shareButton = (Button) itemView.findViewById(R.id.shareButton);
        }

        public void bind(Item item, Context context, FirebaseAuth mAuth, FirebaseUser mUser, FirebaseFirestore mStore) throws IOException {
            Handler mainHandler = new Handler(context.getMainLooper());
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try  {
                        bitmap = BitmapFactory.
                                decodeStream((InputStream)new URL(item.mImageUrl)
                                        .getContent());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                                infoText.setText(item.toString());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model model = new Model(item.mImageUrl, item.mIso, item.mF, item.mMm, item.mSpeed, item.mDate); //lay 6 cai thong tin tuong ung nhet vao, class Model tren git
                    String userID = mUser.getUid();
                    CollectionReference favIMGRef = mStore.collection("users").document(userID).collection("favIMG");
                    favIMGRef.add(model);
                    Toast.makeText((Activity)context, "Image added to your Profile", Toast.LENGTH_SHORT).show();
                }
            });
            callbackManager = CallbackManager.Factory.create();
            ShareDialog shareDialog = new ShareDialog((Activity) context);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(item.mImageUrl))
                            .setShareHashtag(new ShareHashtag.Builder()
                                    .setHashtag("#Aesthetic").build())
                            .build();//Cai link dang set cung, thay bang URL hinh` nao do vao
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        shareDialog.show(shareLinkContent);
                    }
                }
            });
        }
    }



    private List<Item> mItems;
    private Context context;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;



    public ItemsAdapter(List<Item> items, Context context, FirebaseUser user, FirebaseAuth auth, FirebaseFirestore store) {
        mItems = items;
        this.context = context;
        mUser = user;
        mAuth = auth;
        mStore = store;
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof SavedItem)
            return 0;
        else if (mItems.get(position) instanceof MarkerItem) {
            return 1;
        }
        else {
            return -1;
        }
    }

    @NonNull
    @Override
    public ItemsAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == RowType.SAVED_ITEM_TYPE) {
            View itemView = inflater.inflate(R.layout.row_saved_item, parent, false);
            return new SavedItemViewHolder(itemView);
        } else if (viewType == RowType.MARER_ITEM_TYPE) {
            View itemView = inflater.inflate(R.layout.row_marker_item, parent, false);
            return new MarkerItemViewHolder(itemView);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(final ItemsAdapter.ItemViewHolder holder, int position) {
        Item item = mItems.get(position);
        if (holder instanceof SavedItemViewHolder) {
            try {
                ((SavedItemViewHolder) holder).bind(item, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (holder instanceof MarkerItemViewHolder) {
            try {
                ((MarkerItemViewHolder) holder).bind(item, context, mAuth, mUser, mStore);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
