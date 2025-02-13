package com.lets.lettalknew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;

    FirebaseAuth mAuth;
    String userId;
    FirebaseFirestore db;
    DocumentReference documentReferenceSent, documentReferenceRecieved;

    TextView chatWithName,chatWithStatus,textRecording;
    ImageView chatWithImage;
    EmojiconEditText chatMessage;
    Button chatAudioBtn, chatSendBtn;

    List<ArrayList> seenList;

    String id;

    FirebaseDatabase database;


    List<ModalChat> chatList;

    AdapterChat adapterChat;

    ArrayList<String> lastSeenList;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    ArrayList<String> sentMessage, receivedMessage;

    Uri selectImageUri;

//    RecyclerView  chatListView;

    RecyclerView chatListView;

    StorageReference storageReference;
    FirebaseStorage storage;
    String fileName = null;
    //checking seen and unseen
    EventListener seenListener;
    CollectionReference userRefForSeen;

    MediaRecorder recorder;

    ImageView emojy,imageSendBtn;

    String hisName="", chatWithGender = "", chatWIthProfileImage ="",chatWithAge="";

    NotificationApiService notificationApiService;
    boolean notify = false;

    EmojIconActions emojIconActions;

    ConstraintLayout rootView;
    Dialog warningDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_chat );

        mAuth = FirebaseAuth.getInstance ();
        db = FirebaseFirestore.getInstance ();
        userId = mAuth.getCurrentUser ().getUid ();

        chatWithName = findViewById ( R.id.chat_with_name );
        chatWithImage = findViewById ( R.id.profile_image_chat );

        chatMessage = findViewById ( R.id.chat_message );

        chatSendBtn = findViewById ( R.id.chat_send );
        chatAudioBtn = findViewById ( R.id.chat_audio );
        chatMessage.addTextChangedListener ( textWatcher );

        toolbar = findViewById ( R.id.topAppBar );

        sentMessage = new ArrayList<> (  );
        receivedMessage = new ArrayList<> (  );

        rootView = findViewById ( R.id.root_view );

        chatWithStatus = findViewById ( R.id.chat_with_status );

        chatListView =(RecyclerView) findViewById ( R.id.chat_list_view );

        seenList = new ArrayList<> (  );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager ( getApplicationContext () );

        linearLayoutManager.setStackFromEnd ( true );
        chatListView.setHasFixedSize ( true );

        chatListView.setLayoutManager ( linearLayoutManager );

        //create api service---
        notificationApiService = NotificationClient.getRetrofit ( "https://fcm.googleapis.com/" ).create ( NotificationApiService.class );
        //create api service---

        storage = FirebaseStorage.getInstance ();

        emojy = findViewById ( R.id.emojy );

        imageSendBtn = findViewById ( R.id.chat_camera );

        textRecording = findViewById ( R.id.text_recording );


        database = FirebaseDatabase.getInstance();

        lastSeenList = new ArrayList<> (  );

        id = getIntent ().getStringExtra ("id");


        emojIconActions = new EmojIconActions (getApplicationContext () ,rootView , (EmojiconEditText) chatMessage,emojy );

        emojIconActions.ShowEmojIcon ();

        //for audio chat ...
        chatAudioBtn.setOnTouchListener ( new View.OnTouchListener () {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction ()==MotionEvent.ACTION_DOWN) {

                    if(ContextCompat.checkSelfPermission ( getApplicationContext (), Manifest.permission.RECORD_AUDIO )
                            != PackageManager.PERMISSION_GRANTED ) {

                        ActivityCompat.requestPermissions ( ChatActivity.this,
                                new String[] {Manifest.permission.RECORD_AUDIO},REQUEST_CODE_STORAGE_PERMISSION );

                    }
                    else if(ContextCompat.checkSelfPermission ( getApplicationContext (), Manifest.permission.READ_EXTERNAL_STORAGE )
                            != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions ( ChatActivity.this,
                                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},05 );
                    }
                    else if(ContextCompat.checkSelfPermission ( getApplicationContext (), Manifest.permission.WRITE_EXTERNAL_STORAGE )
                            != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions ( ChatActivity.this,
                                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},06 );
                    }
                    else {
                        final ArrayList<String> blockedList = new ArrayList<> (  );
                        FirebaseDatabase.getInstance ().getReference ().child ( "BlockedUsers" ).child ( mAuth.getCurrentUser ().getUid () )
                                .addValueEventListener ( new ValueEventListener () {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        blockedList.clear();
                                        for(DataSnapshot ds: dataSnapshot.getChildren ()){
                                            blockedList.add ( ds.getKey () );
                                        }

                                        if(!blockedList.contains ( id )) {
                                            fileName = Environment.getExternalStorageDirectory ().getAbsolutePath ();
                                            fileName += "/recorded_audio.3gp";
                                            chatMessage.setVisibility ( View.GONE );
                                            emojy.setVisibility ( View.GONE );
                                            imageSendBtn.setVisibility ( View.GONE );
                                            textRecording.setVisibility ( View.VISIBLE );

                                            recordAudio ();
                                        }
                                        else{
                                            Toast.makeText ( ChatActivity.this, "User is blocked, unblock to Send Message", Toast.LENGTH_LONG ).show ();

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                } );

                    }

                }//for audio chat---

                else if(event.getAction ()==MotionEvent.ACTION_UP) {
                    chatMessage.setVisibility ( View.VISIBLE );
                    emojy.setVisibility ( View.VISIBLE );
                    imageSendBtn.setVisibility ( View.VISIBLE );
                    textRecording.setVisibility ( View.GONE );
                    stopAudio ();
                }

                return false;
            }
        } );


        FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" ).child ( userId ).child ( id )
                .addListenerForSingleValueEvent ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue () != null) {
                            toolbar.getMenu ().getItem ( 0 ).setIcon ( R.drawable.heart );
                        }
                        else{
                            toolbar.getMenu ().getItem (0 ).setIcon ( R.drawable.ic_favorite_border_black_24dp );

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );

        loadUserStatusTopBar ();
        seenMessage();
        loadMessages ();
        onTopBarMenu ();
    }//oncreate mehtod end



    public void onTopBarMenu( ) {
        toolbar.setOnMenuItemClickListener ( new Toolbar.OnMenuItemClickListener () {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId ()) {
                    case R.id.favorite:
                    case R.id.add_to_favorite:
                        FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" ).child ( userId ).child ( id )
                                .addListenerForSingleValueEvent ( new ValueEventListener () {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue () == null) {
                                            toolbar.getMenu ().getItem ( 0).setIcon ( R.drawable.heart );
                                            addToFavorites ();
                                        }
                                        else{
                                            Toast.makeText ( ChatActivity.this, "Already in Favorites", Toast.LENGTH_SHORT ).show ();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                } );
                        break;

                    case R.id.block_user:
                        FirebaseDatabase.getInstance ().getReference ().child ( "BlockedUsers" ).child ( userId ).child ( id )
                                .addListenerForSingleValueEvent ( new ValueEventListener () {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue () == null) {
                                            blockUser ();
                                        }
                                        else{
                                            Toast.makeText ( ChatActivity.this, "Already Blocked", Toast.LENGTH_SHORT ).show ();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                } );
                        break;

                    case R.id.view_contact:
                        AnotherUserProfileDialog anotherUserProfile = new AnotherUserProfileDialog (hisName,chatWIthProfileImage,chatWithGender,chatWithAge);
                        anotherUserProfile.show(getSupportFragmentManager (),"userProfile");
                        break;

                    case R.id.report_user:
                        blockUser ();
                        Map<String,String> reportedUserDetails = new HashMap<> (  );
                        reportedUserDetails.put ( "nickName",hisName );
                        reportedUserDetails.put ( "id",id );
                        reportedUserDetails.put ( "reportedBy",userId );
                        db.collection ( "ReportedUsers" ).document (userId).set ( reportedUserDetails )
                                .addOnSuccessListener ( new OnSuccessListener<Void> () {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText ( ChatActivity.this, "User Reported and Blocked", Toast.LENGTH_SHORT ).
                                                show ();
                                    }
                                } );
                        break;
//
//                    case R.id.delete_chat:
//
//                        break;

                }

                return false;
            }
        } );
    }



    public void blockUser() {

        FirebaseDatabase.getInstance ().getReference ().child ( "BlockedMe" ).child ( id ).child ( userId )
                .setValue ( "id",userId );

        FirebaseDatabase.getInstance ().getReference ().child ( "BlockedUsers" ).child ( userId )
                .child ( id ).setValue ( "id",id );

        try{
            FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" ).child ( userId )
                    .child ( id ).removeValue ();

            FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" ).child ( id )
                    .child ( userId ).removeValue ();

            toolbar.getMenu ().getItem (0 ).setIcon ( R.drawable.ic_favorite_border_black_24dp );
        }
        catch (Exception e){

        }
    }

//    public void removeChats() {
//        FirebaseDatabase.getInstance ().getReference ().child ( "Chats" ).addValueEventListener ( new ValueEventListener () {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//               for(DataSnapshot ds: dataSnapshot.getChildren ())   {
//                   ModalChat modalChat = ds.getValue (ModalChat.class);
//                   if(modalChat.getReceiver ()!=null) {
//                       if(modalChat.getReceiver ().equals ( id )) {
//                           FirebaseDatabase.getInstance ().getReference ().child ( "Chats" ).removeValue ();
//                           FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" ).child ( mAuth.getCurrentUser ().getUid () )
//                                   .child ( id ).removeValue ();
//                           FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" ).child ( id ).
//                                   child ( mAuth.getCurrentUser ().getUid () )
//                                   .removeValue ();
//                       }
//                   }
//               }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        } );
//    }

    public void addToFavorites() {

        Map<String, String> favoriteMap = new HashMap<> (  );
        favoriteMap.put ( "id",id );
        FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" ).child ( userId ).
                child ( id ).setValue ( favoriteMap ).addOnCompleteListener ( new OnCompleteListener<Void> () {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful ()) {
                    Toast.makeText ( ChatActivity.this, "added to favorites", Toast.LENGTH_SHORT ).show ();
                }
            }
        } );


    }


    //watching message empty or not
    private TextWatcher textWatcher = new TextWatcher () {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String message = chatMessage.getText ().toString ().trim ();

            if(!message.isEmpty ()) {
                chatAudioBtn.setVisibility ( View.GONE );
                chatSendBtn.setVisibility ( View.VISIBLE );
                db.collection ( "Users" ).document (userId).update ( "userStatus","typing.." );
            }
            else{
                chatAudioBtn.setVisibility ( View.VISIBLE );
                chatSendBtn.setVisibility ( View.GONE );
                db.collection ( "Users" ).document (userId).update ( "userStatus","online" );
            }

        }
        @Override
        public void afterTextChanged(Editable s) {


        }
    };
    //watching message empty or not




    //loading user Information to top bar---
    public void loadUserStatusTopBar() {

        db.collection ( "Users" ).document (id).addSnapshotListener ( new EventListener<DocumentSnapshot> () {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    String nickName = documentSnapshot.getString ( "nickName" );
                    if (nickName != null) {
                        chatWithName.setText ( nickName );
                        hisName = nickName;
                    }
                    String profilePic = documentSnapshot.getString ( "profilePic1" );
                    if(profilePic!=null) {
                        chatWIthProfileImage = profilePic;
                        try{
                            Picasso.get ().load ( profilePic ).placeholder ( R.drawable.dummyprofile ).fit ().into ( chatWithImage );
                        }
                        catch (Exception ex){
                            Picasso.get ().load (  R.drawable.dummyprofile  ).placeholder ( R.drawable.dummyprofile ).fit ().into ( chatWithImage );
                        }

                    }
                    String status = documentSnapshot.getString ( "userStatus" );
                    boolean lastSeenStatus = documentSnapshot.getBoolean ( "shareLastSeen" );
                    if(status!=null) {
                        if(status.equals ( "online" )|| status.equals ( "typing.." )) {
                            chatWithStatus.setText ( status );
                        }
                        else if(lastSeenStatus == true){
                            String lastSeen = documentSnapshot.getString ( "lastSeenTime" );
                            if(lastSeen!=null) {
                                chatWithStatus.setText ( lastSeen);
                            }

                        }
                        else{
                            chatWithStatus.setText ("");
                        }
                    }
                    String gender = documentSnapshot.getString ( "gender" );
                    if(gender!=null) {
                        chatWithGender = gender;
                    }
                    String age = documentSnapshot.getString ( "age" );
                    if(age!=null) {
                        chatWithAge = age;
                    }
                }
            }
        } );

    }    //loading user Information to top bar---


    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        finish ();
    }

    //record Audio--
    public void recordAudio() {
        recorder = new MediaRecorder ();
        recorder.setAudioSource ( MediaRecorder.AudioSource.MIC );
        recorder.setOutputFormat ( MediaRecorder.OutputFormat.THREE_GPP );
        recorder.setOutputFile ( fileName );
        recorder.setAudioEncoder ( MediaRecorder.AudioEncoder.AMR_NB );

        try {
            recorder.prepare ();
        } catch (IOException e) {

        }

        recorder.start ();

    }    //record Audio--




    //stop recording...
    public void stopAudio() {
        try {
            recorder.stop ();
            recorder.release ();
            recorder = null;
            startUploadingAudio();
        }
        catch (Exception e) {

        }


    }//stop recording...




    //sending Audio---
    public void startUploadingAudio() {

        Calendar calendar = Calendar.getInstance ();
        SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd-MM-yyyy HH:mm:ss" );
        final String currentDate = currentDateTime.format ( calendar.getTime () );


        final ProgressDialog progressDialog = new ProgressDialog (this  );
        progressDialog.setMessage ( "Sending audio" );
        progressDialog.show ();

        final StorageReference filePath = storage.getReference ().child ( "Audios" ).child ( userId ).child ( id ).child (currentDate);


        Uri uri = Uri.fromFile ( new File ( fileName ) );

        filePath.putFile ( uri ).addOnSuccessListener ( new OnSuccessListener<UploadTask.TaskSnapshot> () {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filePath.getDownloadUrl ().addOnSuccessListener ( new OnSuccessListener<Uri> () {
                    @Override
                    public void onSuccess(Uri uri) {

                        ModalChat messages = new ModalChat (null,id,userId,null,currentDate,"false",uri.toString (),"no","no"  );



                        db.collection ( "Chats" ).document (currentDate).set ( messages ).addOnSuccessListener ( new OnSuccessListener<Void> () {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressDialog.dismiss ();

                                notify = true;

                                final String msg = "PHOTO";
                                db.collection ( "Users" ).document (userId).addSnapshotListener ( new EventListener<DocumentSnapshot> () {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                        if(e==null) {
                                            if(notify) {
                                                String name =  documentSnapshot.getString ( "nickName" );
                                                sendNotification(id,name,msg);
                                            }
                                            notify = false;
                                        }
                                    }
                                } );

                            }
                        } );

                    }
                } );

            }
        } ).addOnFailureListener ( new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText (getApplicationContext (), "failed : " + e, Toast.LENGTH_SHORT ).show ();

            }
        } );
    }
    //sending Audio---



    //loading Messages--
    public void loadMessages() {

        chatList = new ArrayList<> (  );

//        DatabaseReference dRef = database.getReference ("Chats");
//
//        dRef.addValueEventListener ( new ValueEventListener () {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                chatList.clear ();
//                lastSeenList.clear ();
//                for(DataSnapshot ds : dataSnapshot.getChildren ()) {
//                    ModalChat chat = ds.getValue (ModalChat.class);
//                    if(chat!=null){
//
//                            if( (chat.getReceiver ().equals (userId) && chat.getSender ().equals (id) ) ||
//                                    ( chat.getSender ().equals (userId) && chat.getReceiver ().equals (id)) ) {
//
//                                chatList.add ( chat );
//
//                                String lastSeen = ds.child ( "isSeen" ).getValue (String.class);
//                                lastSeenList.add ( lastSeen );
//
//                            }
//
//                            adapterChat = new AdapterChat ( ChatActivity.this,
//                                    chatList,lastSeenList);
//                            chatListView.setAdapter ( adapterChat );
//                            adapterChat.notifyDataSetChanged ();
//
//                            chatListView.getLayoutManager ().scrollToPosition ( adapterChat.getItemCount ()-1 );
//
//
//                    }
//
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        } );


        db.collection ( "Chats" ).addSnapshotListener ( new EventListener<QuerySnapshot> () {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {

                    chatList.clear ();
                    lastSeenList.clear ();
                    for (DocumentSnapshot ds : queryDocumentSnapshots) {
                        ModalChat chat = ds.toObject ( ModalChat.class );
                        if (chat != null) {

                            if ((chat.getReceiver ().equals ( userId ) && chat.getSender ().equals ( id )) ||
                                    (chat.getSender ().equals ( userId ) && chat.getReceiver ().equals ( id ))) {

                                chatList.add ( chat );

                                String lastSeen = ds.getString ("seen");
                                lastSeenList.add ( lastSeen );


                            }

                            adapterChat = new AdapterChat ( ChatActivity.this,
                                    chatList, lastSeenList );
                            chatListView.setAdapter ( adapterChat );
                            adapterChat.notifyDataSetChanged ();

                            chatListView.getLayoutManager ().scrollToPosition ( adapterChat.getItemCount () - 1 );

                        }


                    }
                }
            }
            } );

    }
    //loading Messages--



    //checking seen message status---
    public void seenMessage() {
        userRefForSeen = db.collection ( "Chats" );

        userRefForSeen.addSnapshotListener ( new EventListener<QuerySnapshot> () {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot ds : queryDocumentSnapshots) {
                    ModalChat chat = ds.toObject (ModalChat.class);
                    if(chat!=null){
                        try{
                            if (chat.getReceiver ().equals ( userId ) && chat.getSender ().equals ( id )) {

                                HashMap<String, Object> hasSeenHasMap = new HashMap<> ();
                                hasSeenHasMap.put ( "seen", "true" );
                                ds.getReference ().update ( hasSeenHasMap );
                            }
                        }
                        catch (Exception ex){

                        }
                    }

                }
            }
        } );

    }    //checking seen message status---



    //on Pause..
    @Override
    protected void onPause() {
        super.onPause ();
        checkConnection ();
//        userRefForSeen.document ().update ( (Map<String, Object>) seenListener );

        if(mAuth.getCurrentUser ()!=null) {
            String currentDate;
            Calendar calendar = Calendar.getInstance ();
            SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd/MM/yyyy HH:mm:ss" );
            currentDate = currentDateTime.format ( calendar.getTime () );
            db.collection ( "Users" ).document ( userId ).update ( "lastSeenTime", currentDate );
            db.collection ( "Users" ).document ( userId ).update ( "userStatus", "paused" );
        }
    }    //on Pause..






    //sending Chat Message--
    public void sendChatMessage(View view){
        final ArrayList<String> blockedList = new ArrayList<> (  );
                            notify = true;
                            Calendar calendar = Calendar.getInstance ();
                            SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd-MM-yyyy HH:mm:ss" );
                            final String currentDate = currentDateTime.format ( calendar.getTime () );
                            final String message = chatMessage.getText ().toString ();


                            ModalChat messages = new ModalChat (message,id,userId,null,currentDate,"false",null,"no","no"  );


                            chatMessage.setText ( "" );

                        db.collection ( "Chats" ).document (currentDate).set ( messages );
                            final String msg = message;
                            db.collection ( "Users" ).document (userId).addSnapshotListener ( new EventListener<DocumentSnapshot> () {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if(e==null) {
                                        if(notify) {
                                            String name =  documentSnapshot.getString ( "nickName" );
                                            sendNotification(id,name,msg);
                                        }
                                        notify = false;
                                    }
                                }
                            } );


                            final DatabaseReference chatRef1 = FirebaseDatabase.getInstance ().getReference ("ChatList")
                                    .child ( userId )
                                    .child ( id );

                            chatRef1.addValueEventListener ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(!dataSnapshot.exists ()) {
                                        chatRef1.child ( "id" ).setValue ( id );
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            } );

                            final DatabaseReference chatRef2 = FirebaseDatabase.getInstance ().getReference ("ChatList")
                                    .child ( id )
                                    .child ( userId );

                            chatRef2.addValueEventListener ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(!dataSnapshot.exists ()) {
                                        chatRef2.child ( "id" ).setValue ( userId );
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            } );
    }    //sending Chat Message--




    //send Notificaton...
    private void sendNotification(final String id, final String name, final String msg) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance ().getReference ("Tokens");
        Query query  = allTokens.orderByKey ().equalTo ( id );
        query.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren ()) {
                    Token token = ds.getValue (Token.class);
                    NotificationData data = new NotificationData ( userId,name+":"+msg,
                            "New message",id,R.drawable.communication );

                    NotificationSender sender = new NotificationSender ( data,token.getToken () );
                    notificationApiService.sendNotification ( sender )
                            .enqueue ( new Callback<NotificationResponse> () {
                                @Override
                                public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                                    Toast.makeText ( ChatActivity.this, msg, Toast.LENGTH_SHORT ).show ();
                                }

                                @Override
                                public void onFailure(Call<NotificationResponse> call, Throwable t) {

                                }
                            } );

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }    //send Notificaton...


    //send image button click..
    public void sendImage(View view) {
        final ArrayList<String> blockedList = new ArrayList<> (  );
        if(ContextCompat.checkSelfPermission ( getApplicationContext (), Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions ( ChatActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION );
        }
        else{

            FirebaseDatabase.getInstance ().getReference ().child ( "BlockedUsers" )
                    .child ( mAuth.getCurrentUser ().getUid () ).addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   blockedList.clear ();
                   for(DataSnapshot ds: dataSnapshot.getChildren ()) {
                       blockedList.add ( ds.getKey () );
                   }
                   if(!blockedList.contains ( id )) {
                       CropImage.activity ().start ( ChatActivity.this);

                   }else{
                       Toast.makeText ( ChatActivity.this, "User blocked, unblock to continue", Toast.LENGTH_LONG ).show ();
                   }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );

        }
    }    //send image button click..


    //on perimisson image--
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult ( data );

            if(resultCode == RESULT_OK) {
                selectImageUri = result.getUri ();

                try {
                    uploadImageToStorage ();
                }
                catch (Exception e) {
                    Toast.makeText ( this, "e : " + e, Toast.LENGTH_SHORT ).show ();
                }
            }
        }
    }    //on perimisson image--



    //send Image---
    public void uploadImageToStorage() throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap ( this.getContentResolver (),selectImageUri );
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        bitmap.compress ( Bitmap.CompressFormat.JPEG, 50, baos );
        byte[] data = baos.toByteArray ();

        storageReference = storage.getReference ().child ( "SentImages" ).child ( userId ).child ( id ).child (selectImageUri.toString ());

        final ProgressDialog progressDialog = new ProgressDialog (this  );
        progressDialog.setMessage ( "Sending Image" );
        progressDialog.show ();


        UploadTask uploadTask = storageReference.putBytes ( data );
        uploadTask.addOnSuccessListener ( new OnSuccessListener<UploadTask.TaskSnapshot> () {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl ().addOnSuccessListener ( new OnSuccessListener<Uri> () {
                    @Override
                    public void onSuccess(Uri uri) {
                        Calendar calendar = Calendar.getInstance ();
                        SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd-MM-yyyy HH:mm:ss" );
                        final String currentDate = currentDateTime.format ( calendar.getTime () );

                        ModalChat messages = new ModalChat (null,id,userId,uri.toString (),currentDate,"false",null,"no","no"  );

                        db.collection ( "Chats" ).document (currentDate).set ( messages ).addOnSuccessListener ( new OnSuccessListener<Void> () {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressDialog.dismiss ();

                                notify = true;

                                final String msg = "PHOTO";
                                db.collection ( "Users" ).document (userId).addSnapshotListener ( new EventListener<DocumentSnapshot> () {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                        if(e==null) {
                                            if(notify) {
                                                String name =  documentSnapshot.getString ( "nickName" );
                                                sendNotification(id,name,msg);
                                            }
                                            notify = false;
                                        }
                                    }
                                } );

                            }
                        } );

                    }
                } );
            }
        } ).addOnFailureListener ( new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText ( getApplicationContext (), "some error occurred", Toast.LENGTH_SHORT ).show ();
            }
        } );


    }//send Image---




    //onStart--
    @Override
    protected void onStart() {
        super.onStart ();
        checkConnection ();
        if(mAuth.getCurrentUser ()!=null) {
            String currentDate;
            Calendar calendar = Calendar.getInstance ();
            SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd/MM/yyyy HH:mm:ss" );
            currentDate = currentDateTime.format ( calendar.getTime () );
            db.collection ( "Users" ).document ( userId ).update ( "lastSeenTime", currentDate );
            db.collection ( "Users" ).document ( userId ).update ( "userStatus", "online" );
        }

    }   //onStart--


//
//    //onStop--
//    @Override
//    protected void onStop() {
//        super.onStop ();
//
//        if(mAuth.getCurrentUser ()!=null) {
//            String currentDate;
//            Calendar calendar = Calendar.getInstance ();
//            SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd/MM/yyyy HH:mm:ss" );
//            currentDate = currentDateTime.format ( calendar.getTime () );
//            db.collection ( "Users" ).document ( userId ).update ( "lastSeenTime", currentDate );
//            db.collection ( "Users" ).document ( userId ).update ( "userStatus", "offline" );
//        }
//    }//onStop--
//


    //display profile--
    public void seeProfile(View view) {
        AnotherUserProfileDialog anotherUserProfile = new AnotherUserProfileDialog (hisName,chatWIthProfileImage,chatWithGender,chatWithAge);
        anotherUserProfile.show(getSupportFragmentManager (),"userProfile");
    }    //display profile--

//    @Override
//    protected void onDestroy() {
//        super.onDestroy ();
//        if(mAuth.getCurrentUser ()!=null) {
//            String currentDate;
//            Calendar calendar = Calendar.getInstance ();
//            SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd/MM/yyyy HH:mm:ss" );
//            currentDate = currentDateTime.format ( calendar.getTime () );
//            db.collection ( "Users" ).document ( userId ).update ( "lastSeenTime", currentDate );
//            db.collection ( "Users" ).document ( userId ).update ( "userStatus", "offline" );
//        }
//
//    }

    //back button ---
    public void chatBack(View view) {
        finish ();
    } //back button ---

    public void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext ().getSystemService ( Context.CONNECTIVITY_SERVICE );

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo ();

        if(activeNetwork!=null) {
            if(activeNetwork.getType () == ConnectivityManager.TYPE_WIFI) {

            }

            else if(activeNetwork.getType ()==ConnectivityManager.TYPE_MOBILE) {

            }
            else {
                Toast.makeText ( this, "no internet connection", Toast.LENGTH_SHORT ).show ();
            }

        }
        else {
            Toast.makeText ( this, "no internet connection", Toast.LENGTH_SHORT ).show ();
        }

    }

}