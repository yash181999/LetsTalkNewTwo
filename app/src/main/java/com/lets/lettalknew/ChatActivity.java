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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;

    FirebaseAuth mAuth;
    String userId;
    FirebaseFirestore db;
    DocumentReference documentReferenceSent, documentReferenceRecieved;

    TextView chatWithName,chatWithStatus;
    ImageView chatWithImage;
    EditText chatMessage;
    Button chatAudioBtn, chatSendBtn;

    String id;

    FirebaseDatabase database;



    private ChatAdapter chatAdapter;

    List<ModalChat> chatList;

    AdapterChat adapterChat;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    ArrayList<String> sentMessage, receivedMessage;

    Uri selectImageUri;

//    RecyclerView  chatListView;

    RecyclerView chatListView;

    StorageReference storageReference;
    FirebaseStorage storage;

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

        chatListView =(RecyclerView) findViewById ( R.id.chat_list_view );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager ( this );

        linearLayoutManager.setStackFromEnd ( true );
        chatListView.setHasFixedSize ( true );
        chatListView.setLayoutManager ( linearLayoutManager );


        storage = FirebaseStorage.getInstance ();


        database = FirebaseDatabase.getInstance();

        id = getIntent ().getStringExtra ("id");

        db.collection ( "Users" ).document (id).addSnapshotListener ( new EventListener<DocumentSnapshot> () {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    String nickName = documentSnapshot.getString ( "nickName" );
                    if (nickName != null) {
                        chatWithName.setText ( nickName );
                    }
                    String profilePic = documentSnapshot.getString ( "ProfilePic1" );
                    if(profilePic!=null) {
                        Picasso.get ().load ( profilePic ).fit ().into ( chatWithImage );
                    }
                }
            }
        } );

         loadMessages ();
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }


    public void sendChatMessage(View view){

        Calendar calendar = Calendar.getInstance ();
        SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd-MM-yyyy HH:mm:ss" );
        final String currentDate = currentDateTime.format ( calendar.getTime () );
        String message = chatMessage.getText ().toString ();
        Map<String , String> messages = new HashMap<> (  );
        messages.put ("sender", userId );
        messages.put ( "receiver", id );
        messages.put ( "message",message );

        DatabaseReference databaseReference = database.getReference ();

        databaseReference.child ( "Chats" ).push ().setValue ( messages );

        chatMessage.setText ( "" );

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


        UploadTask uploadTask = storageReference.putBytes ( data );
        uploadTask.addOnSuccessListener ( new OnSuccessListener<UploadTask.TaskSnapshot> () {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl ().addOnSuccessListener ( new OnSuccessListener<Uri> () {
                    @Override
                    public void onSuccess(Uri uri) {
                      Map<String, String> imageMessage = new HashMap<> (  );
                      imageMessage.put ( "image",uri.toString () );
                      imageMessage.put ( "sender",userId );
                      imageMessage.put ( "receiver",id );

                        DatabaseReference databaseReference = database.getReference ();

                        databaseReference.child ( "Chats" ).push ().setValue ( imageMessage );

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

}
