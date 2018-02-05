package com.malikbisic.sportapp.adapter.firebase;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.firebase.AddPhotoOrVideo;
import com.malikbisic.sportapp.activity.firebase.MainPage;
import com.malikbisic.sportapp.activity.firebase.SendImageChatActivity;

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

public class ImageAlbumAdapter extends BaseAdapter {

    private Activity _activity;
    private ArrayList<String> _filePaths = new ArrayList<String>();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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


        Glide.with(_activity).load(image).into(holder.image);

        // image view click listener
        holder.image.setOnClickListener(new OnImageClickListener(position));
        holder.sendButton.setOnClickListener(new OnSendImageClickListener(position));
        holder.sendButton.setVisibility(View.INVISIBLE);

        holder.sendButton.setTag("zatvoreno");

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

        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }

        @Override
        public void onClick(View v) {
            String tag = (String) ImageViewHolder.sendButton.getTag();
            ;

            if (tag.equals("zatvoreno")) {
                ImageViewHolder.sendButton.setVisibility(View.VISIBLE);
                ImageViewHolder.sendButton.setTag("otvoreno");

            } else if (tag.equals("otvoreno")) ;
            ImageViewHolder.sendButton.setVisibility(View.INVISIBLE);
            ImageViewHolder.sendButton.setTag("zatvoreno");


        }

    }

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
            StorageReference photoPost = mFilePath.child("Post_Photo").child(imagePath.getName());
            UploadTask uploadTask = photoPost.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();


                    Map messageMap = new HashMap();
                    messageMap.put("message", downloadUri.toString());
                    messageMap.put("seen", false);
                    messageMap.put("type", "image");
                    messageMap.put("time", FieldValue.serverTimestamp());
                    messageMap.put("from", myUID);
                    FirebaseFirestore mRootRef = FirebaseFirestore.getInstance();

                    mRootRef.collection("Messages").document(myUID).collection("chat-user").document(userID).collection("message").add(messageMap);
                    mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).collection("message").add(messageMap);

                    Map chatUser = new HashMap();
                    chatUser.put("to", userID);
                    Map mychatUser = new HashMap();
                    chatUser.put("to", myUID);
                    mRootRef.collection("Messages").document(myUID).collection("chat-user").document(userID).set(chatUser);
                    mRootRef.collection("Messages").document(userID).collection("chat-user").document(myUID).set(mychatUser);

                    Intent goToMain = new Intent(_activity, SendImageChatActivity.class);
                    goToMain.putExtra("userId", userID);
                    _activity.startActivity(goToMain);


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
     * Resizing image size
     */
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
    }

    static class ImageViewHolder {
        ImageView image;
        static ImageButton sendButton;
    }

}