package com.example.loginauthfirebase;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
                                infoText.setText(item.mDate + "        f" + item.mF + "  iso" + item.mIso + "  " + item.mSpeed + "s");
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

        public MarkerItemViewHolder(@NonNull View itemView) {
            super(itemView);
            infoText = (TextView) itemView.findViewById(R.id.infoText);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            likeButton = (Button) itemView.findViewById(R.id.likeButton);
            shareButton = (Button) itemView.findViewById(R.id.shareButton);
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
                                infoText.setText(item.mDate + "        f" + item.mF + "  iso" + item.mIso + "  " + item.mSpeed + "s");
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

    private List<Item> mItems;
    private Context context;

    public ItemsAdapter(List<Item> items, Context context) {
        mItems = items;
        this.context = context;
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
                ((MarkerItemViewHolder) holder).bind(item, context);
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
