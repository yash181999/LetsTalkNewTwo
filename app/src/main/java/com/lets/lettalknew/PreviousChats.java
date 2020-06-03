package com.lets.lettalknew;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PreviousChats extends Fragment {

    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    List<ModalChatList> chatlistList;
    List<ModalUser> userList;
    DatabaseReference databaseReference;
    FirebaseUser user;
    DocumentReference documentReference;
    FirebaseFirestore db;

    AdapterChatList adapterChatList;

    public PreviousChats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate ( R.layout.fragment_previous_chats, container, false );

        mAuth = FirebaseAuth.getInstance ();
        user = mAuth.getCurrentUser ();
        db = FirebaseFirestore.getInstance ();
        databaseReference = FirebaseDatabase.getInstance ().getReference ().child ( "ChatList" ).child ( user.getUid () );
        chatlistList = new ArrayList<> ();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager ( getContext () );

        recyclerView = view.findViewById ( R.id.recyclerViewChatList );
        recyclerView.setHasFixedSize ( true );

        recyclerView.setLayoutManager ( linearLayoutManager );


        databaseReference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlistList.clear ();
                for (DataSnapshot ds : dataSnapshot.getChildren ()) {
                    ModalChatList chatList = ds.getValue ( ModalChatList.class );
                    chatlistList.add ( chatList );

                }

                loadChats ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );


        return view;
    }

    private void lastMessage(final  String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance ().getReference ("Chats");
        databaseReference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastMessage = null;
                String theLastPhoto = null ;
                String theLastAudio = null ;

                for(DataSnapshot ds : dataSnapshot.getChildren ()) {
                    ModalChat chat = ds.getValue (ModalChat.class);
                    if(chat==null) {
                        continue;
                    }
                    String sender = chat.getSender ();
                    String receiver = chat.getReceiver ();
                    if(sender == null || receiver==null) {
                        continue;
                    }
                    if(chat.getReceiver ().equals (user.getUid ()) &&
                            chat.getSender ().equals ( userId) ||
                            chat.getReceiver ().equals ( userId ) &&
                                    chat.getSender ().equals ( user.getUid () )) {

                          theLastMessage = chat.getMessage ();
                          theLastPhoto = chat.getImage ();
                          theLastAudio = chat.getAudio ();

                    }

                }

                if(theLastAudio!=null) {

                    adapterChatList.setLastMessageMap ( userId,"Audio" );
                    adapterChatList.notifyDataSetChanged ();
                }

                else if(theLastMessage!=null) {

                    adapterChatList.setLastMessageMap ( userId,theLastMessage );
                    adapterChatList.notifyDataSetChanged ();
                }
                else if(theLastPhoto!=null){
                    adapterChatList.setLastMessageMap ( userId,"Photo" );
                    adapterChatList.notifyDataSetChanged ();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    //load chats
    private void loadChats() {
        userList = new ArrayList<> ();
         db.collection ( "Users" ).addSnapshotListener ( new EventListener<QuerySnapshot> () {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {

                    userList.clear ();
                    for (DocumentSnapshot dataSnapshot : queryDocumentSnapshots.getDocuments ()) {

                        ModalUser modalUser = (ModalUser) dataSnapshot.toObject ( ModalUser.class );
                        for (ModalChatList chatList : chatlistList) {
                            if (modalUser.getuId () != null && modalUser.getuId ().equals ( chatList.getId () )) {
                                userList.add ( modalUser );
                                break;
                            }
                        }
                        adapterChatList = new AdapterChatList ( getContext (), userList );

                        recyclerView.setAdapter ( adapterChatList );
                        adapterChatList.notifyDataSetChanged ();

                        for (int i = 0; i < userList.size (); i++) {
                            lastMessage ( userList.get ( i ).getuId () );
                        }
                    }
                }
            }
        } );
    }//load chats


}
