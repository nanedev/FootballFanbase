package com.malikbisic.sportapp.adapter.firebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.firebase.ChatMessageActivity;
import com.malikbisic.sportapp.listener.OnLoadMoreListener;
import com.malikbisic.sportapp.model.firebase.Messages;
import com.malikbisic.sportapp.model.firebase.UserChat;
import com.malikbisic.sportapp.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nane on 11.9.2017.
 */

public class MessageAdapter extends RecyclerView.Adapter {

    private List<Messages> mMessageList;
    FirebaseAuth mAutH;
    Context ctx;
    Activity activity;
    RecyclerView mMessagesList;

    private static final int ITEM_VIEW = 0;
    private static final int ITEM_LOADING = 1;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    boolean isLoading;
    boolean isAllLoaded = false;

    public MessageAdapter(List<Messages> mMessageList, Context ctx, Activity activity, RecyclerView recyclerView) {
        this.mMessageList = mMessageList;
        this.ctx = ctx;
        this.activity = activity;
        this.mMessagesList = recyclerView;


        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

                    lastVisibleItem = llm.findLastVisibleItemPosition();
                    totalItemCount = llm.getItemCount();

                    Log.i("paginacijaLASTVISIBLE", String.valueOf(lastVisibleItem));
                    Log.i("paginacijaTOTAL", String.valueOf(totalItemCount));
                    Log.i("paginacijaIsLOADING", String.valueOf(isLoading));
                    Log.i("paginacijaIsAllLoading", String.valueOf(isAllLoaded));


                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && !isAllLoaded && lastVisibleItem >= 9) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }

                }
            });


        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == ITEM_VIEW) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
            return new MessageViewHolder(v);
        } else if (viewType == ITEM_LOADING) {
            View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_item, parent, false);
            return new ProgressViewHolder(v1);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return mMessageList.get(position) != null ? ITEM_VIEW : ITEM_LOADING;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        mAutH = FirebaseAuth.getInstance();

        String current_user_id = mAutH.getCurrentUser().getUid();
        int getViewType = holder.getItemViewType();
        Messages model = mMessageList.get(position);


        if (getViewType == ITEM_VIEW) {
            String from_user = model.getFrom();
            String type = model.getType();
            if (from_user != null) {

                if (from_user.equals(current_user_id)) {
                    ((MessageAdapter.MessageViewHolder) holder).layoutToUser.setVisibility(View.VISIBLE);
                    ((MessageAdapter.MessageViewHolder) holder).layoutFromUser.setVisibility(View.GONE);
                    ((MessageAdapter.MessageViewHolder) holder).layoutImageFromUser.setVisibility(View.GONE);

                    if (type.equals("text")) {
                        ((MessageAdapter.MessageViewHolder) holder).layoutToUser.setVisibility(View.VISIBLE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutFromUser.setVisibility(View.GONE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutImageFromUser.setVisibility(View.GONE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutImageToUser.setVisibility(View.GONE);
                        ((MessageAdapter.MessageViewHolder) holder).messageTextTOUser.setText(model.getMessage());
                        if (model.getTime() != null) {
                            String time = DateUtils.formatDateTime(activity, model.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                            ((MessageAdapter.MessageViewHolder) holder).timeTextViewToUser.setText(time);
                        }
                    } else if (type.equals("image")) {
                        ((MessageAdapter.MessageViewHolder) holder).layoutImageToUser.setVisibility(View.VISIBLE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutFromUser.setVisibility(View.GONE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutImageFromUser.setVisibility(View.GONE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutToUser.setVisibility(View.GONE);
                        ((MessageAdapter.MessageViewHolder) holder).userProfileForIMage.setVisibility(View.GONE);
                        Picasso.with(activity).setIndicatorsEnabled(false);
                        Picasso.with(activity).load(model.getMessage()).transform(new RoundedTransformation(20, 3)).fit().centerCrop().into(((MessageAdapter.MessageViewHolder) holder).messageImageViewToUser);

                        if (model.getTime() != null) {
                            String time = DateUtils.formatDateTime(activity, model.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                            ((MessageAdapter.MessageViewHolder) holder).timeImageTOUser.setText(time);
                        }
                    }

                } else {
                    ((MessageAdapter.MessageViewHolder) holder).layoutFromUser.setVisibility(View.VISIBLE);
                    ((MessageAdapter.MessageViewHolder) holder).profileImageImg.setVisibility(View.VISIBLE);
                    ((MessageAdapter.MessageViewHolder) holder).layoutToUser.setVisibility(View.GONE);
                    ((MessageAdapter.MessageViewHolder) holder).layoutImageToUser.setVisibility(View.GONE);
                    ((MessageAdapter.MessageViewHolder) holder).layoutImageFromUser.setVisibility(View.GONE);
                    ((MessageAdapter.MessageViewHolder) holder).userProfileForIMage.setVisibility(View.GONE);

                    if (type.equals("text")) {
                        ((MessageAdapter.MessageViewHolder) holder).layoutFromUser.setVisibility(View.VISIBLE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutToUser.setVisibility(View.GONE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutImageToUser.setVisibility(View.GONE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutImageFromUser.setVisibility(View.GONE);
                        ((MessageAdapter.MessageViewHolder) holder).userProfileForIMage.setVisibility(View.GONE);
                        ((MessageAdapter.MessageViewHolder) holder).messageTextFromUser.setText(model.getMessage());
                        if (model.getTime() != null) {
                            String time = DateUtils.formatDateTime(activity, model.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                            ((MessageAdapter.MessageViewHolder) holder).timeTextViewFromUser.setText(time);
                        }
                    } else if (type.equals("image")) {
                        ((MessageAdapter.MessageViewHolder) holder).userProfileForIMage.setVisibility(View.VISIBLE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutImageFromUser.setVisibility(View.VISIBLE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutToUser.setVisibility(View.GONE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutImageToUser.setVisibility(View.GONE);
                        ((MessageAdapter.MessageViewHolder) holder).layoutFromUser.setVisibility(View.GONE);
                        Picasso.with(activity).setIndicatorsEnabled(false);
                        Picasso.with(activity).load(model.getMessage()).transform(new RoundedTransformation(20, 3)).fit().centerCrop().into(((MessageAdapter.MessageViewHolder) holder).messageImageViewFromUser);

                        if (model.getTime() != null) {
                            String time = DateUtils.formatDateTime(activity, model.getTime().getTime(), DateUtils.FORMAT_SHOW_TIME);
                            ((MessageAdapter.MessageViewHolder) holder).timeImageFromUser.setText(time);
                        }

                        FirebaseFirestore displayImage = FirebaseFirestore.getInstance();

                        displayImage.collection("Users").document(from_user).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                                UserChat model2 = dataSnapshot.toObject(UserChat.class);
                                String profileImage = model2.getProfileImage();

                                ((MessageAdapter.MessageViewHolder) holder).setProfileImageForImage(activity, profileImage);
                            }
                        });
                    }


                    FirebaseFirestore displayImage = FirebaseFirestore.getInstance();

                    displayImage.collection("Users").document(from_user).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot dataSnapshot, FirebaseFirestoreException e) {

                            UserChat model2 = dataSnapshot.toObject(UserChat.class);
                            String profileImage = model2.getProfileImage();

                            ((MessageAdapter.MessageViewHolder) holder).setProfileImageImg(activity, profileImage);
                        }
                    });
                }


            }


            mMessagesList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChatMessageActivity.emoticons.setVisibility(View.GONE);
                    ChatMessageActivity.recyclerViewAlbum.setVisibility(View.GONE);
                    Toast.makeText(activity, "desavaliseista", Toast.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ChatMessageActivity.mChatMessageView.getWindowToken(), 0);

                }
            });

            ((MessageAdapter.MessageViewHolder) holder).messageImageViewFromUser.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final String[] items = {"Save Image to Gallery", "Cancel"};
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(activity, R.style.AppTheme_Dark_Dialog);

                    dialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (items[i].equals("Save Image to Gallery")) {

                                ProgressDialog dialog1 = new ProgressDialog(activity);
                                dialog1.setMessage("Saving...");
                                dialog1.show();
                                ((MessageAdapter.MessageViewHolder) holder).messageImageViewFromUser.buildDrawingCache();
                                Bitmap imageChat = ((MessageAdapter.MessageViewHolder) holder).messageImageViewFromUser.getDrawingCache();
                                saveFile(imageChat);
                                dialog1.dismiss();
                            } else if (items[i].equals("Cancel")) {


                            }
                        }
                    });

                    dialog.create();
                    dialog.show();

                    return true;
                }
            });

            ((MessageAdapter.MessageViewHolder) holder).messageImageViewToUser.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final String[] items = {"Save Image to Gallery", "Cancel"};
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(activity, R.style.AppTheme_Dark_Dialog);

                    dialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (items[i].equals("Save Image to Gallery")) {

                                ProgressDialog dialog1 = new ProgressDialog(activity);
                                dialog1.setMessage("Saving...");
                                dialog1.show();
                                ((MessageAdapter.MessageViewHolder) holder).messageImageViewToUser.buildDrawingCache();
                                Bitmap imageChat = ((MessageAdapter.MessageViewHolder) holder).messageImageViewToUser.getDrawingCache();
                                saveFile(imageChat);
                                dialog1.dismiss();
                            } else if (items[i].equals("Cancel")) {


                            }
                        }
                    });

                    dialog.create();
                    dialog.show();

                    return true;
                }
            });
        } else if (getViewType == ITEM_LOADING) {


            if (isLoading && !isAllLoaded && lastVisibleItem >= 9) {

                ((MessageAdapter.ProgressViewHolder) holder).progressBar.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.spinnerLoad),
                        android.graphics.PorterDuff.Mode.MULTIPLY);
                ((MessageAdapter.ProgressViewHolder) holder).progressBar.setVisibility(
                        View.VISIBLE);
                ((MessageAdapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            } else if (!isLoading || isAllLoaded) {
                ((MessageAdapter.ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
            }

        }
    }


    public void setOnLoadMore(OnLoadMoreListener onLoadMore) {
        onLoadMoreListener = onLoadMore;
    }


    public void setIsLoading(boolean param) {
        isLoading = param;
    }

    public void isFullLoaded(boolean param) {
        isAllLoaded = param;
    }


    public void saveFile(Bitmap b) {
        try {

            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/FootballFanBase/");

            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            File imageFile = File.createTempFile(
                    String.valueOf(Calendar.getInstance().getTimeInMillis()),
                    ".jpeg",                     /* suffix */
                    storageDir                   /* directory */
            );


            FileOutputStream writeStream = new FileOutputStream(imageFile);

            b.compress(Bitmap.CompressFormat.JPEG, 100, writeStream);
            writeStream.flush();
            writeStream.close();

            addPicToGallery(imageFile);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void addPicToGallery(File imageFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        //text
        public TextView messageTextFromUser;
        public TextView messageTextTOUser;
        public CircleImageView profileImageImg;
        public TextView timeTextViewFromUser;
        public TextView timeTextViewToUser;

        //image
        public ImageView messageImageViewFromUser;
        public ImageView messageImageViewToUser;
        public RelativeLayout layoutFromUser;
        public RelativeLayout layoutToUser;
        public RelativeLayout layoutImageFromUser;
        public RelativeLayout layoutImageToUser;
        public TextView timeImageTOUser;
        public TextView timeImageFromUser;
        public CircleImageView userProfileForIMage;

        //gallery
        public RelativeLayout galleryLayoutToUser;
        public RecyclerView galleryRecViewToUser;
        public TextView timeforGridToUSer;
        public CircleImageView imageFromGrid;
        public RelativeLayout galleryLayoutFromUser;
        public TextView timeForGridFromUser;
        public RecyclerView galleryREcViewFromUSer;

        //audio
        public RelativeLayout layoutAudioFromUser;
        public RelativeLayout layoutAudioToUser;
        public CircleImageView imageFromAudio;
        public ImageView play_stopFromUser;
        public ImageView play_stopToUser;
        public TextView totalTimeFromUser;
        public TextView totalTimeToUser;
        public TextView messageTimeAudioFromUser;
        public TextView messageTimeAudioToUser;
        public SeekBar progressBarFromUser;
        public SeekBar progressBarToUser;
        public ProgressBar imageToUSerProgress;

        public MediaPlayer mPlayer;

        public TextView typing;

       public RelativeLayout seenLayoutMessage;
       public RelativeLayout seenLayoutImage;
       public RelativeLayout seenLayoutGridImage;
       public RelativeLayout seenLayoutAudio;

        public MessageViewHolder(View itemView) {
            super(itemView);

            //text
            messageTextFromUser = (TextView) itemView.findViewById(R.id.message_textFromUser);
            messageTextTOUser = (TextView) itemView.findViewById(R.id.message_textToUser);
            profileImageImg = (CircleImageView) itemView.findViewById(R.id.message_image);
            layoutFromUser = (RelativeLayout) itemView.findViewById(R.id.message_from_user);
            layoutToUser = (RelativeLayout) itemView.findViewById(R.id.message_layout_to_user);
            timeTextViewFromUser = (TextView) itemView.findViewById(R.id.timeMessageFromUser);
            timeTextViewToUser = (TextView) itemView.findViewById(R.id.timeMessageToUser);

            //image
            layoutImageFromUser = (RelativeLayout) itemView.findViewById(R.id.messageimagelayoutFromUser);
            layoutImageToUser = (RelativeLayout) itemView.findViewById(R.id.messageimagelayoutToUser);
            timeImageFromUser = (TextView) itemView.findViewById(R.id.timemessageImageFromUser);
            timeImageTOUser = (TextView) itemView.findViewById(R.id.timemessageImageToUser);
            messageImageViewFromUser = (ImageView) itemView.findViewById(R.id.imageMessageFromUser);
            messageImageViewToUser = (ImageView) itemView.findViewById(R.id.imageMessageToUser);
            userProfileForIMage = (CircleImageView) itemView.findViewById(R.id.message_imageImageFrom);

            //gallery
            galleryLayoutToUser = (RelativeLayout) itemView.findViewById(R.id.messageGalleryLayoutToUser);
            galleryRecViewToUser = (RecyclerView) itemView.findViewById(R.id.galleryRecViewTo);
            timeforGridToUSer = (TextView) itemView.findViewById(R.id.timemessageGridUserTo);
            imageFromGrid = (CircleImageView) itemView.findViewById(R.id.grid_imageImageFrom);
            galleryREcViewFromUSer = (RecyclerView) itemView.findViewById(R.id.galleryRecViewFrom);
            galleryLayoutFromUser = (RelativeLayout) itemView.findViewById(R.id.messageGalleryLayoutFromUser);
            timeForGridFromUser = (TextView) itemView.findViewById(R.id.timemessageGridUserFrom);

            //audio
            layoutAudioFromUser = (RelativeLayout) itemView.findViewById(R.id.messageAudioLayoutUserFrom);
            layoutAudioToUser = (RelativeLayout) itemView.findViewById(R.id.messageAudioLayoutUserTo);
            imageFromAudio = (CircleImageView) itemView.findViewById(R.id.message_audioImageFrom);
            play_stopFromUser = (ImageView) itemView.findViewById(R.id.play_stop_fromuser);
            play_stopToUser = (ImageView) itemView.findViewById(R.id.play_stop_touser);
            totalTimeFromUser = (TextView) itemView.findViewById(R.id.totaltimeaudio_fromuser);
            totalTimeToUser = (TextView) itemView.findViewById(R.id.totaltimeaudio_touser);
            messageTimeAudioFromUser = (TextView) itemView.findViewById(R.id.timemessage_fromuser_audio);
            messageTimeAudioToUser = (TextView) itemView.findViewById(R.id.timemessage_touser_audio);
            progressBarFromUser = (SeekBar) itemView.findViewById(R.id.progressBarFromUser);
            progressBarToUser = (SeekBar) itemView.findViewById(R.id.progressBarToUser);
            mPlayer = new MediaPlayer();

            //seenlayout

            seenLayoutMessage = (RelativeLayout) itemView.findViewById(R.id.seenlayoutformessage);
            seenLayoutImage = (RelativeLayout) itemView.findViewById(R.id.seenlayoutforimage);
            seenLayoutAudio = (RelativeLayout) itemView.findViewById(R.id.seenlayoutforaudio);
            seenLayoutGridImage = (RelativeLayout) itemView.findViewById(R.id.seenlayoutforgrid);

            //progressbar

            imageToUSerProgress = (ProgressBar) itemView.findViewById(R.id.progresBarToUserImage);


        }

        public void setProfileImageImg(Context ctx, String profileImage) {
            Picasso.with(ctx).load(profileImage).into(profileImageImg);
        }

        public void setProfileImageForImage(Context ctx, String profileImage) {
            Picasso.with(ctx).load(profileImage).into(userProfileForIMage);
        }

        public void setProfileImageForGrid(Context ctx, String profileImage) {
            Picasso.with(ctx).load(profileImage).into(imageFromGrid);
        }
        public void setProfileImageForAudio(Context ctx, String profileImage) {
            Picasso.with(ctx).load(profileImage).into(imageFromAudio);
        }
        public void setAudioFile(Context context, String audioFile) {

            if (audioFile != null) {
                mPlayer.reset();
                try {

                    mPlayer.setDataSource(context, Uri.parse(audioFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
            progressBar.setBackgroundColor(Color.parseColor("#cccccc"));
        }
    }

}