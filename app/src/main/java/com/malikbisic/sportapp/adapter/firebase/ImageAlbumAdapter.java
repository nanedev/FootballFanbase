package com.malikbisic.sportapp.adapter.firebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.firebase.AddPhotoOrVideo;
import com.malikbisic.sportapp.activity.firebase.MainPage;
import com.malikbisic.sportapp.activity.firebase.SendImageChatActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

/**
 * Created by malikbisic on 05/02/2018.
 */

public class ImageAlbumAdapter extends RecyclerView.Adapter<ImageAlbumAdapter.ImageViewHolder> {
    private Activity _activity;
    private ArrayList<String> _filePaths = new ArrayList<String>();
    boolean[] opened;
    boolean firstime = true;
    boolean secondtime = false;
    String myUID;
    String userID;


    public ImageAlbumAdapter(Activity _activity, ArrayList<String> _filePaths, String myUID, String userID) {
        this._activity = _activity;
        this._filePaths = _filePaths;
        this.opened = opened;

        this.myUID = myUID;
        this.userID = userID;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_grid, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        String image = _filePaths.get(position);
        Glide.with(_activity)
                .load(image)
                .override(250, 250)
                .centerCrop()

                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageHolder);
        //    Picasso.with(_activity).load(image).resize(250,250).centerCrop().into(holder.imageHolder);

        holder.sendButton.setVisibility(View.GONE);
        holder.sendlayout.setVisibility(View.GONE);

        holder.imageHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (holder.sendButton.getVisibility() == View.GONE) {
                    holder.sendButton.setVisibility(View.VISIBLE);
                    holder.sendlayout.setVisibility(View.VISIBLE);
                }else {
                    holder.sendButton.setVisibility(View.GONE);
                    holder.sendlayout.setVisibility(View.GONE);
                }
                holder.sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendImage(holder.getAdapterPosition());
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return _filePaths.size();
    }

    //neki kom
/*public class ImageAlbumAdapter extends BaseAdapter {

    private Activity _activity;
    private ArrayList<String> _filePaths = new ArrayList<String>();
    boolean[] opened;
    private int imageWidth;
    String myUID;
    String userID;
    ImageButton sendImageButton;

    public ImageAlbumAdapter(Activity activity, ArrayList<String> filePaths,
                             int imageWidth, String myUID, String userID) {
        this._activity = activity;
        this._filePaths = filePaths;
        this.imageWidth = imageWidth;
        this.myUID = myUID;
        this.userID = userID;
    }

    @Override
    public int getCount() {
        return this._filePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return this._filePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageViewHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = ((Activity) parent.getContext()).getLayoutInflater();
            view = inflater.inflate(R.layout.image_item_grid, parent, false);

            holder = new ImageViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.imageGrid);
            holder.sendButton = (ImageButton) view.findViewById(R.id.sendImageGrid);
            view.setTag(holder);

        } else {
            holder = (ImageViewHolder) view.getTag();

        }

        // get screen dimensions
        String image = _filePaths.get(position);
        holder.sendButton.setTag("zatvoreno");
        //holder.sendButton.setVisibility(View.GONE);


        Glide.with(_activity).load(image).into(holder.image);
        holder.sendButton.setId(position);
        holder.image.setId(position);
        opened = new boolean[getCount()];
        opened[position] = false;

        // image view click listener
        final ImageViewHolder finalHolder = holder;
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = ImageViewHolder.sendButton.getId();

                if (finalHolder.sendButton.getVisibility() == View.GONE) {
                    finalHolder.sendButton.setVisibility(View.VISIBLE);
                    // ImageViewHolder.sendButton.setTag("otvoreno");
                    Log.i("button", "otvori");
                    opened[id] = true;

                } else {
                    finalHolder.sendButton.setVisibility(View.GONE);
                    Log.i("button", "zatvori");
                    opened[id] = false;
                }
            }
        });
        holder.sendButton.setOnClickListener(new OnSendImageClickListener(position));



        return view;
    }

    class OnSendImageClickListener implements View.OnClickListener {

        int _postion;

        // constructor
        public OnSendImageClickListener(int position) {
            this._postion = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Log.i("Pos", _filePaths.get(_postion));
            sendImage(_postion);
        }

    }


    class OnImageClickListener implements View.OnClickListener {

        int _postion;
        String tag = (String) ImageViewHolder.sendButton.getTag();

        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }

        @Override
        public void onClick(View v) {
            ;




        }

    }
*/
    public void sendImage(int position) {
        try {

            File imagePath = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                imagePath = new File(_filePaths.get(position));
            }
            Log.i("imagePath", imagePath.getPath());

            final Bitmap imageCompressBitmap = new Compressor(_activity)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .compressToBitmap(imagePath);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageCompressBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();
            StorageReference mFilePath = FirebaseStorage.getInstance().getReference();
            StorageReference photoPost = mFilePath.child("Chat_Image").child(imagePath.getName());
            UploadTask uploadTask = photoPost.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();


                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("message", downloadUri.toString());
                    messageMap.put("seen", false);
                    messageMap.put("type", "image");
                    messageMap.put("time", FieldValue.serverTimestamp());
                    messageMap.put("from", myUID);
                    final FirebaseFirestore mRootRef = FirebaseFirestore.getInstance();

                    mRootRef.collection("Messages").document(myUID).collection("chat-user").document(userID).collection("message").add(messageMap);
                    mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).collection("message").add(messageMap);

                    final Map<String, Object> chatUser = new HashMap<>();
                    chatUser.put("to", userID);
                    chatUser.put("typing", false);
                    final Map<String, Object> mychatUser = new HashMap<>();
                    mychatUser.put("to", myUID);
                    mychatUser.put("typing", false);


                    mRootRef.collection("Messages").document(myUID).collection("chat-user").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()){
                                mRootRef.collection("Messages").document(myUID).collection("chat-user").document(userID).update(chatUser);
                            }else {
                                mRootRef.collection("Messages").document(myUID).collection("chat-user").document(userID).set(chatUser);
                            }
                        }
                    });
                    mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()){
                                mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).update(mychatUser);
                            } else {
                                mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).set(mychatUser);
                            }
                        }
                    });
                    Map timeMessage = new HashMap();
                    timeMessage.put("timenewMessage", FieldValue.serverTimestamp());
                    mRootRef.collection("Messages").document(userID).set(timeMessage);


                    mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.getResult().exists()){
                                boolean isInChat = task.getResult().getBoolean("isInChat");
                                if (!isInChat){
                                    Map<String, Object> notifMap = new HashMap<>();
                                    notifMap.put("action", "chat");
                                    notifMap.put("uid", myUID);
                                    notifMap.put("seen", false);
                                    notifMap.put("whatIS", "image");
                                    notifMap.put("timestamp", FieldValue.serverTimestamp());

                                    if (!userID.equals(myUID)) {
                                        CollectionReference notifSet = FirebaseFirestore.getInstance().collection("NotificationChat").document(userID).collection("notif-id");
                                        notifSet.add(notifMap);
                                    }
                                }
                            }
                        }
                    });

                 /*   Intent goToMain = new Intent(_activity, SendImageChatActivity.class);
                    goToMain.putExtra("userId", userID);
                    _activity.startActivity(goToMain);*/


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("errorPosting", e.getMessage());

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {

            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    } */

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public ImageButton sendButton;
        public RelativeLayout sendlayout;
        public ImageView imageHolder;


        public ImageViewHolder(View itemView) {
            super(itemView);
            sendlayout = (RelativeLayout) itemView.findViewById(R.id.sendlayout);
            image = (ImageView) itemView.findViewById(R.id.imageGrid);
            imageHolder = (ImageView) itemView.findViewById(R.id.nekiid);
            sendButton = (ImageButton) itemView.findViewById(R.id.sendImageGrid);

        }
    }

}