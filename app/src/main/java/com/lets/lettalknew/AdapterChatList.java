package com.lets.lettalknew;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {

    Context context;

    List<ModalUser> userList;

    private Map<String, String> lastMessageMap;

    Dialog myDialog;

    Dialog infoDialog;

    public AdapterChatList(Context context, List<ModalUser> userList) {
        this.context = context;
        this.userList = userList;
       lastMessageMap = new HashMap<> (  );
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.chat_list_layout , parent, false);



        return new MyHolder (    view );
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        //get Data;


        final String id = userList.get (position).getuId ();
        String userImage = userList.get ( position ).getProfilePic1 ();
        String userName = userList.get ( position).getNickName ();
        String lastMessage = lastMessageMap.get ( id );

        holder.chatProfileName.setText ( userName );

        if(lastMessage==null || lastMessage.equals ( "default" )) {

             holder.chatProfileLastMsg.setVisibility ( View.GONE );
        }
        else  {
            if(lastMessage.equals ( "Photo" )){
                holder.chatProfileLastMsg.setText ( lastMessage );
                holder.chatProfileLastMsg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_black_24dp, 0, 0, 0);
            }
            else if(lastMessage.equals ( "Audio" )) {
                holder.chatProfileLastMsg.setText ( lastMessage );
                holder.chatProfileLastMsg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mic_black_24dp, 0, 0, 0);
            }
            else {
                holder.chatProfileLastMsg.setText ( lastMessage );
            }

            holder.chatProfileLastMsg.setVisibility ( View.VISIBLE );
        }

        try {
            Picasso.get ().load ( userImage ).placeholder ( R.drawable.dummyprofile ).fit ().into ( holder.chatProfileImage );
        }
        catch (Exception e) {
            Picasso.get ().load ( R.drawable.dummyprofile ).into ( holder.chatProfileImage );
        }


        if(userList.get ( position ).getUserStatus ().equals ( "online" ) ) {
            holder.chatProfileStatus.setText ( "online" );
        }
        else {

            String lastSeenTime[] = userList.get ( position ).getLastSeenTime ().split ( " " );
            if(lastSeenTime!=null) {
                holder.chatProfileStatus.setText (lastSeenTime[0]);
            }
        }




        myDialog = new Dialog ( context );
        myDialog.setContentView ( R.layout.activity_another_user_profile );
        ImageView profileImage = myDialog.findViewById ( R.id.another_user_profilePic );
        TextView name = myDialog.findViewById ( R.id.another_user_name );
        TextView gender = myDialog.findViewById ( R.id.another_user_gender );

        try{
            Picasso.get ().load ( userImage ).placeholder ( R.drawable.dummyprofile ).fit ().into ( profileImage );
        }
        catch (Exception e) {
            Picasso.get ().load ( R.drawable.dummyprofile ).placeholder ( R.drawable.dummyprofile ).fit ().into ( profileImage );

        }

        name.setText ( userName + "(" + userList.get ( position ).getAge ()+ ")");

        gender.setText ( userList.get ( position ).getGender () );


        holder.chatProfileImage.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                myDialog.show ();
            }
        } );


        //handle click
        holder.itemView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent ( context,ChatActivity.class );
                intent.putExtra ( "id",id );
                context.startActivity ( intent );
            }
        } );

//
//        //display heart icon to favorites
//
//        FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" )
//                .child ( FirebaseAuth.getInstance ().getCurrentUser ().getUid () ).
//                child ( id ).addListenerForSingleValueEvent ( new ValueEventListener () {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if(dataSnapshot.getValue ()!=null){
//
//                    holder.favoriteIcon.setVisibility ( View.VISIBLE );
//
//                }
//                else{
//                    holder.favoriteIcon.setVisibility ( View.GONE );
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        } );
//
//        //display heart icon to favorites



        infoDialog = new Dialog ( context );
        infoDialog.setContentView ( R.layout.chat_list_info_layout );

        final TextView blockUser,addToFavorites,deleteChat;

        blockUser = infoDialog.findViewById ( R.id.block_user );
        addToFavorites= infoDialog.findViewById ( R.id.add_to_favorite );
        deleteChat = infoDialog.findViewById ( R.id.delete_chat );

        holder.itemView.setOnLongClickListener ( new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                infoDialog.show ();
                return false;
            }
        } );

        deleteChat.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance ().getReference ("ChatList")
                        .child ( FirebaseAuth.getInstance ().getCurrentUser ().getUid () )
                        .child ( id ).removeValue ().addOnSuccessListener ( new OnSuccessListener<Void> () {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText ( context, "removed", Toast.LENGTH_SHORT ).show ();
                        infoDialog.dismiss ();
                    }
                } );
            }
        } );

        addToFavorites.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                checkIfPresent (id);
            }
        } );

        //block user --
        blockUser.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance ().getReference ().child ( "BlockedUsers" )
                        .child ( FirebaseAuth.getInstance ().getCurrentUser ().getUid () )
                        .child ( id ).setValue ( "id",id ).
                        addOnSuccessListener ( new OnSuccessListener<Void> () {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText ( context, "userBlocked", Toast.LENGTH_SHORT ).show ();
                        FirebaseDatabase.getInstance ().getReference ("ChatList")
                                .child (id).child ( FirebaseAuth.getInstance ().getCurrentUser ().getUid () )
                                .removeValue ();
                        FirebaseDatabase.getInstance ().getReference ("ChatList")
                                .child (FirebaseAuth.getInstance ().getCurrentUser ().getUid ())
                                 .child (id  )
                                .removeValue ();

                          infoDialog.dismiss ();
                    }
                } );
            }
        } );
        //block user --


    }

    public void checkIfPresent(final String id) {
         FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" )
                 .child ( FirebaseAuth.getInstance ().getCurrentUser ().getUid () ).child ( id ).addListenerForSingleValueEvent ( new ValueEventListener () {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 if(dataSnapshot.getValue ()!=null){
                     infoDialog.dismiss ();
                     Toast.makeText ( context, "already in favorites", Toast.LENGTH_SHORT ).show ();

                 }
                 else{
                     addToFavorites ( id );
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         } );
    }

    public void addToFavorites(String id) {
        FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" )
                .child ( FirebaseAuth.getInstance ().getCurrentUser ().getUid () )
                .child ( id ).setValue ( "id",id ).addOnSuccessListener ( new OnSuccessListener<Void> () {
            @Override
            public void onSuccess(Void aVoid) {
                infoDialog.dismiss ();
                Toast.makeText ( context, "Added to Favorites", Toast.LENGTH_SHORT ).show ();
            }
        } );
    }



    public void setLastMessageMap(String userId, String lastMessage) {
        lastMessageMap.put ( userId,lastMessage );
    }

    @Override
    public int getItemCount() {
        return userList.size ();
    }



    class MyHolder extends RecyclerView.ViewHolder {
        ImageView chatProfileImage,favoriteIcon;
        TextView chatProfileName,chatProfileLastMsg,chatProfileStatus;
        public MyHolder(@NonNull View itemView) {
            super ( itemView );

            chatProfileImage = itemView.findViewById ( R.id.chat_profile_image );
            chatProfileName = itemView.findViewById ( R.id.chat_profile_name );
            chatProfileLastMsg = itemView.findViewById ( R.id.chat_profile_last_message );
            chatProfileStatus = itemView.findViewById ( R.id.chat_profile_status );
            favoriteIcon = itemView.findViewById ( R.id.favorite_icon_chat_list );

        }
    }
}
