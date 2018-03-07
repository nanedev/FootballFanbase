package com.malikbisic.sportapp.fragment.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.malikbisic.sportapp.R;
import com.malikbisic.sportapp.activity.StopAppServices;
import com.malikbisic.sportapp.model.api.AboutMatchChatModel;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentChatAboutMatch extends Fragment implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private RecyclerView recViewAboutMatch;

    private ImageButton sendMessageBtn;
    private ImageButton openEmotijonBtn;
    public static EmojiconEditText messageEdittext;

    private boolean isOpenEmotijon = false;
    private FrameLayout emotijonLayout;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String fixturesID;


    public FragmentChatAboutMatch() {
        // Required empty public constructor
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Intent closeAPP = new Intent(getContext(), StopAppServices.class);
        getActivity().startService(closeAPP);
        View mView = inflater.inflate(R.layout.fragment_fragment_chat_about_match, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fixturesID = getActivity().getIntent().getStringExtra("idFixtures");

        openEmotijonBtn = (ImageButton) mView.findViewById(R.id.smileImageAboutMatch);
        sendMessageBtn = (ImageButton) mView.findViewById(R.id.send_messageAboutMatch);
        recViewAboutMatch = (RecyclerView) mView.findViewById(R.id.rec_viewChatAboutMatch);
        recViewAboutMatch.setLayoutManager(new LinearLayoutManager(getActivity()));
        emotijonLayout = (FrameLayout) mView.findViewById(R.id.emojiconsAboutMatch);
        messageEdittext = (EmojiconEditText) mView.findViewById(R.id.emojiconEditTextAboutMatch);
        messageEdittext.clearFocus();
        openEmotijonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpenEmotijon){
                    emotijonLayout.setVisibility(View.VISIBLE);
                    isOpenEmotijon = true;
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    messageEdittext.clearFocus();

                } else {
                    emotijonLayout.setVisibility(View.GONE);
                    isOpenEmotijon = false;
                    messageEdittext.clearFocus();
                }
            }
        });

        messageEdittext.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP){
                    emotijonLayout.setVisibility(View.GONE);
                    isOpenEmotijon = false;
                }

                return false;
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        //setEmojiconFragment(false);
        getData();
        return mView;
    }

    private void getData(){
        Query queryData = db.collection("ChatAboutMatch").document(fixturesID).collection("match-comments").orderBy("time", Query.Direction.DESCENDING);

        final FirestoreRecyclerOptions<AboutMatchChatModel> response = new FirestoreRecyclerOptions.Builder<AboutMatchChatModel>()
                .setQuery(queryData, AboutMatchChatModel.class)
                .build();

        FirestoreRecyclerAdapter<AboutMatchChatModel, ChatAboutMatchViewHolder> adapter = new FirestoreRecyclerAdapter<AboutMatchChatModel, ChatAboutMatchViewHolder>(response) {
            @Override
            protected void onBindViewHolder(ChatAboutMatchViewHolder holder, int position, AboutMatchChatModel model) {
                holder.setTextComment(model.getTextComment());
                holder.setUserInfo(model.getUid());
            }

            @Override
            public ChatAboutMatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.aboutmatchchat_item, parent, false);
                return new ChatAboutMatchViewHolder(view);
            }
        };

        recViewAboutMatch.setAdapter(adapter);
        adapter.startListening();
    }

    private void sendMessage() {
        String textComment = messageEdittext.getText().toString().trim();

        Map<String, Object> commentsMapChat = new HashMap<>();
        commentsMapChat.put("textComment", textComment);
        commentsMapChat.put("uid", mAuth.getCurrentUser().getUid());
        commentsMapChat.put("time", FieldValue.serverTimestamp());

        db.collection("ChatAboutMatch").document(fixturesID).collection("match-comments").add(commentsMapChat).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    messageEdittext.setText("");

                } else {
                    Log.e("errorMessage", task.getException().getLocalizedMessage());
                }
            }
        });
    }

    /**
     * Set the Emoticons in Fragment.
     *
     * @param useSystemDefault
     */
    private void setEmojiconFragment(boolean useSystemDefault) {

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojiconsAboutMatch, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(messageEdittext, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(messageEdittext);
    }


    public static class ChatAboutMatchViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImageImg;
        TextView usernameTxT;
        TextView commentsTxt;
        FirebaseFirestore db;

        public ChatAboutMatchViewHolder(View itemView) {
            super(itemView);

            profileImageImg = (CircleImageView) itemView.findViewById(R.id.profilechatAboutMatch);
            usernameTxT = (TextView) itemView.findViewById(R.id.username_chatAboutMatch);
            commentsTxt = (TextView) itemView.findViewById(R.id.textchatAboutMatch);
            db = FirebaseFirestore.getInstance();
        }

        public void setTextComment(String textComment){
            if (textComment != null){
                commentsTxt.setText(textComment);
            }
        }

        public void setUserInfo(String uid){
            db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()){
                        String userName = task.getResult().getString("username");
                        String profileImage = task.getResult().getString("profileImage");

                        usernameTxT.setText(userName);
                        Glide.with(profileImageImg.getContext()).load(profileImage).into(profileImageImg);
                    }
                }
            });
        }
    }


}
