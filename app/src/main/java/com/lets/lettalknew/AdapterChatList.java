package com.lets.lettalknew;

import android.annotation.SuppressLint;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;

    FirebaseUser user;


    public AdapterChatList(Context context, List<ModalUser> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<> (  );
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.chat_list_layout , parent, false);

         mAuth = FirebaseAuth.getInstance ();
         user = mAuth.getCurrentUser ();
         firebaseDatabase = FirebaseDatabase.getInstance ();

        return new MyHolder (    view );
    }

    @SuppressLint("ResourceAsColor")
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
        else if(userList.get ( position ).isShareLastSeen () == true) {

            String lastSeenTime[] = userList.get ( position ).getLastSeenTime ().split ( " " );
            if(lastSeenTime!=null) {
                holder.chatProfileStatus.setText (lastSeenTime[0]);
            }
        }
        else{
            holder.chatProfileStatus.setText ( "" );
        }


        //if present in favorites show heart
        firebaseDatabase.getReference ().child ( "Favorites" )
                .child ( user.getUid () ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                holder.favoriteIcon.setVisibility ( View.GONE );

                for(DataSnapshot ds : dataSnapshot.getChildren ()){
                    String favoriteId = ds.getKey ();
                    if(favoriteId.equals ( id )) {
                        holder.favoriteIcon.setVisibility ( View.VISIBLE );
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        //--if present in favorites show heart--


        holder.chatProfileImage.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                holder.myDialog.show ();
            }
        } );


        try{
            Picasso.get ().load ( userImage ).placeholder ( R.drawable.dummyprofile ).fit ().into ( holder.profileImage );
        }
        catch (Exception e) {
            Picasso.get ().load ( R.drawable.dummyprofile ).placeholder ( R.drawable.dummyprofile ).fit ().into ( holder.profileImage );

        }

        holder.name.setText ( userName + "(" + userList.get ( position ).getAge ()+ ")");

        holder.gender.setText ( userList.get ( position ).getGender () );



        //handle click
        holder.itemView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( context,ChatActivity.class );
                intent.putExtra ( "id",id );
                context.startActivity ( intent );
            }
        } );

        holder.itemView.setOnLongClickListener ( new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                holder.infoDialog.show ();
                return false;
            }
        } );

        holder.deleteChat.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance ().getReference ("ChatList")
                        .child ( FirebaseAuth.getInstance ().getCurrentUser ().getUid () )
                        .child ( id ).removeValue ().addOnSuccessListener ( new OnSuccessListener<Void> () {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText ( context, "removed", Toast.LENGTH_SHORT ).show ();
                        holder.infoDialog.dismiss ();
                    }
                } );
            }
        } );

        holder.addToFavorites.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                checkIfPresent (userList.get ( position ).getuId (),holder);
            }
        } );

        //block user --
        holder.blockUser.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
               blockUser ( id,holder );
            }
        } );
/*        block user --*/

    }

    public void blockUser(final String id, final MyHolder holder) {
        FirebaseDatabase.getInstance ().getReference ().child ( "BlockedUsers" )
                .child ( user.getUid () ).child ( id ).setValue ( "id",id ).addOnSuccessListener ( new OnSuccessListener<Void> () {
            @Override
            public void onSuccess(Void aVoid) {
               firebaseDatabase.getReference ().child ( "BlockedMe" ).child ( id ).child ( user.getUid () )
               .setValue ( "id",user.getUid () ).addOnSuccessListener ( new OnSuccessListener<Void> () {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Toast.makeText ( context, "userBlocked", Toast.LENGTH_SHORT ).show ();
                       FirebaseDatabase.getInstance ().getReference ("ChatList")
                               .child ( FirebaseAuth.getInstance ().getCurrentUser ().getUid () )
                               .child ( id ).removeValue ().addOnSuccessListener ( new OnSuccessListener<Void> () {
                           @Override
                           public void onSuccess(Void aVoid) {

                           }
                       } );


                    try {
                        FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" ).child ( mAuth.getCurrentUser ().getUid () )
                                .child ( id ).removeValue ();

                        FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" ).child ( id )
                                .child ( mAuth.getCurrentUser ().getUid () ).removeValue ();
                    }
                    catch (Exception e) {

                    }

                   }
               } );
            }
        } );

    }

    public void removeFromChatList(final String id, final MyHolder holder) {

    }


    public void checkIfPresent(final String id, final MyHolder holder) {
         FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" )
                 .child ( FirebaseAuth.getInstance ().getCurrentUser ().getUid () ).
                 child ( id ).addListenerForSingleValueEvent ( new ValueEventListener () {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 if(dataSnapshot.getValue ()!=null){
                     holder.infoDialog.dismiss ();
                     Toast.makeText ( context, "already in favorites", Toast.LENGTH_SHORT ).show ();

                 }
                 else{
                     addToFavorites ( id,holder );
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         } );
    }


    public void addToFavorites(String id, final MyHolder holder) {
        FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" )
                .child ( FirebaseAuth.getInstance ().getCurrentUser ().getUid () )
                .child ( id ).setValue ( "id",id ).addOnSuccessListener ( new OnSuccessListener<Void> () {
            @Override
            public void onSuccess(Void aVoid) {
                holder.infoDialog.dismiss ();
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

        Dialog myDialog;

        Dialog infoDialog;

        ImageView chatProfileImage,favoriteIcon;
        TextView chatProfileName,chatProfileLastMsg,chatProfileStatus;
        ImageView profileImage;
        TextView name;
        TextView gender;
        TextView blockUser,addToFavorites,deleteChat;


        public MyHolder(@NonNull View itemView) {
            super ( itemView );

            chatProfileImage = itemView.findViewById ( R.id.chat_profile_image );
            chatProfileName = itemView.findViewById ( R.id.chat_profile_name );
            chatProfileLastMsg = itemView.findViewById ( R.id.chat_profile_last_message );
            chatProfileStatus = itemView.findViewById ( R.id.chat_profile_status );
            favoriteIcon = itemView.findViewById ( R.id.favorite_icon_chat_list );

            myDialog = new Dialog ( itemView.getContext () );
            myDialog.setContentView ( R.layout.activity_another_user_profile );

            infoDialog = new Dialog ( itemView.getContext () );
            infoDialog.setContentView ( R.layout.chat_list_info_layout );

            profileImage = myDialog.findViewById ( R.id.another_user_profilePic );
            name = myDialog.findViewById ( R.id.another_user_name );
            gender = myDialog.findViewById ( R.id.another_user_gender );

            addToFavorites= infoDialog.findViewById ( R.id.add_to_favorite );
            deleteChat =  infoDialog.findViewById ( R.id.delete_chat );
            blockUser = infoDialog.findViewById ( R.id.block_user );

        }
    }

}
