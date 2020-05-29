package com.lets.lettalknew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
    EditText chatMessage;
    Button chatAudioBtn, chatSendBtn;

    String id;

    FirebaseDatabase database;

    private LastChatList chatAdapter;

    List<ModalChat> chatList;

    AdapterChat adapterChat;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    ArrayList<String> sentMessage, receivedMessage;

    Uri selectImageUri;

//    RecyclerView  chatListView;

    RecyclerView chatListView;

    StorageReference storageReference;
    FirebaseStorage storage;
    String fileName = null;
    //checking seen and unseen
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    MediaRecorder recorder;

    ImageView emojy,imageSendBtn;

    String hisName="", chatWithGender = "", chatWIthProfileImage ="",chatWithAge="";

    NotificationApiService notificationApiService;
    boolean notify = false;


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

        sentMessage = new ArrayList<> (  );
        receivedMessage = new ArrayList<> (  );

        chatWithStatus = findViewById ( R.id.chat_with_status );

        chatListView =(RecyclerView) findViewById ( R.id.chat_list_view );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager ( this );

        linearLayoutManager.setStackFromEnd ( true );
        chatListView.setHasFixedSize ( true );

        chatListView.setLayoutManager ( linearLayoutManager );


        //create api service---
        notificationApiService = NotificationClient.getRetrofit ( "https://fcm.googleapis.com/" ).create ( NotificationApiService.class );



        storage = FirebaseStorage.getInstance ();

        emojy = findViewById ( R.id.emojy );
        imageSendBtn = findViewById ( R.id.chat_camera );

        textRecording = findViewById ( R.id.text_recording );


        database = FirebaseDatabase.getInstance();

        id = getIntent ().getStringExtra ("id");

        db.collection ( "Users" ).document (id).addSnapshotListener ( new EventListener<DocumentSnapshot> () {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    String nickName = documentSnapshot.getString ( "nickName" );
                    if (nickName != null) {
                        chatWithName.setText ( nickName );
                        hisName = nickName;
                    }
                    String profilePic = documentSnapshot.getString ( "ProfilePic1" );
                    if(profilePic!=null) {
                        chatWIthProfileImage = profilePic;
                        Picasso.get ().load ( profilePic ).fit ().into ( chatWithImage );
                    }
                    String status = documentSnapshot.getString ( "userStatus" );
                    if(status!=null) {
                        if(status=="online") {
                            chatWithStatus.setText ( "online" );
                        }
                        else{
                            String lastSeen = documentSnapshot.getString ( "lastSeenTime" );
                            if(lastSeen!=null) {
                                chatWithStatus.setText ( lastSeen);
                            }

                        }
                    }
                    String gender = documentSnapshot.getString ( "gender" );
                    if(gender!=null) {
                        chatWithGender = gender;
                    }
                    String age = documentSnapshot.getString ( "age" );
                    if(age!=null) {
                      chatWithAge = documentSnapshot.getString ( age );
                    }
                }
            }
        } );

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
                       fileName = Environment.getExternalStorageDirectory ().getAbsolutePath ();
                       fileName += "/recorded_audio.3gp";
                        chatMessage.setVisibility ( View.GONE );
                        emojy.setVisibility ( View.GONE );
                        imageSendBtn.setVisibility ( View.GONE );
                        textRecording.setVisibility ( View.VISIBLE );

                        recordAudio ();
                   }

                }

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

         loadMessages ();
         seenMessage();
    }//oncreate mehtod end

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
            }
            else{
                chatAudioBtn.setVisibility ( View.VISIBLE );
                chatSendBtn.setVisibility ( View.GONE );
            }

        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    //watching message empty or not


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

    }

    public void stopAudio() {
        try {
            recorder.stop ();
            recorder.release ();
            recorder = null;
            startUploadingAudio();
        }
        catch (Exception e) {

        }


    }

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


                        Map<String, Object> imageMessage = new HashMap<> (  );
                        imageMessage.put ( "audio",uri.toString () );
                        imageMessage.put ( "sender",userId );
                        imageMessage.put ( "receiver",id );
                        imageMessage.put("time",currentDate);
                        imageMessage.put ( "isSeen",false );




                        DatabaseReference databaseReference = database.getReference ();

                        databaseReference.child ( "Chats" ).push ().setValue ( imageMessage ).addOnSuccessListener ( new OnSuccessListener<Void> () {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss ();
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




    public void loadMessages() {

        chatList = new ArrayList<> (  );

        DatabaseReference dRef = database.getReference ("Chats");

        dRef.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear ();
                for(DataSnapshot ds : dataSnapshot.getChildren ()) {
                    ModalChat chat = ds.getValue (ModalChat.class);

                    if( (chat.getReceiver ().equals (userId) && chat.getSender ().equals (id) ) ||
                            ( chat.getSender ().equals (userId) && chat.getReceiver ().equals (id)) ) {

                        chatList.add ( chat );

                    }

                    adapterChat = new AdapterChat ( ChatActivity.this,
                            chatList);

                    adapterChat.notifyDataSetChanged ();


                    chatListView.setAdapter ( adapterChat );
                    chatListView.getLayoutManager ().scrollToPosition ( adapterChat.getItemCount ()-1 );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    public void seenMessage() {
        userRefForSeen = database.getReference ("Chats");

        seenListener = userRefForSeen.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot ds : dataSnapshot.getChildren ()) {
                   ModalChat chat = ds.getValue (ModalChat.class);
                   if(chat.getReceiver ().equals ( userId ) && chat.getSender ().equals (id) ) {
                        HashMap<String , Object > hasSeenHasMap = new HashMap<> (  );
                        hasSeenHasMap.put ( "isSeen",true );
                        ds.getRef ().updateChildren ( hasSeenHasMap );
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }


    @Override
    protected void onPause() {
        super.onPause ();
        userRefForSeen.removeEventListener ( seenListener );
    }

    public void sendChatMessage(View view){

        notify = true;

        Calendar calendar = Calendar.getInstance ();
        SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd-MM-yyyy HH:mm:ss" );
        final String currentDate = currentDateTime.format ( calendar.getTime () );
        final String message = chatMessage.getText ().toString ();
        Map<String , Object> messages = new HashMap<> (  );
        messages.put ("sender", userId );
        messages.put ( "receiver", id );
        messages.put ( "message",message );
        messages.put("time",currentDate);
        messages.put ( "isSeen",false );


        DatabaseReference databaseReference = database.getReference ();

        databaseReference.child ( "Chats" ).push ().setValue ( messages );

        chatMessage.setText ( "" );

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


    }

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

    }

    public void sendImage(View view) {
        if(ContextCompat.checkSelfPermission ( getApplicationContext (), Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions ( ChatActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION );
        }
        else{

            CropImage.activity ().start ( ChatActivity.this);

        }
    }



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
    }

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
                      Map<String, Object> imageMessage = new HashMap<> (  );
                      imageMessage.put ( "image",uri.toString () );
                      imageMessage.put ( "sender",userId );
                      imageMessage.put ( "receiver",id );
                      imageMessage.put("time",currentDate);
                      imageMessage.put ( "isSeen",false );


                        DatabaseReference databaseReference = database.getReference ();

                        databaseReference.child ( "Chats" ).push ().setValue ( imageMessage ).addOnSuccessListener ( new OnSuccessListener<Void> () {
                            @Override
                            public void onSuccess(Void aVoid) {
                              progressDialog.dismiss ();
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


    }

    @Override
    protected void onStart() {
        super.onStart ();

        if(mAuth.getCurrentUser ()!=null) {
            db.collection ( "Users" ).
                    document ( mAuth.getCurrentUser ().getUid () ).
                    update ( "userStatus", "online" );
        }

    }

    @Override
    protected void onStop() {
        super.onStop ();

        if(mAuth.getCurrentUser ()!=null) {
            String currentDate;
            Calendar calendar = Calendar.getInstance ();
            SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd/MM/yyyy HH:mm:ss" );
            currentDate = currentDateTime.format ( calendar.getTime () );
            db.collection ( "Users" ).document ( userId ).update ( "lastSeenTime", currentDate );
            db.collection ( "Users" ).document ( userId ).update ( "userStatus", "offline" );
        }
    }

    public void seeProfile(View view) {
        AnotherUserProfileDialog anotherUserProfile = new AnotherUserProfileDialog (hisName,chatWIthProfileImage,chatWithGender,chatWithAge);
        anotherUserProfile.show(getSupportFragmentManager (),"userProfile");
    }

    public void chatBack(View view) {

    }

}
