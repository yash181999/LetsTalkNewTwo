package com.lets.lettalknew;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterBlock extends RecyclerView.Adapter<AdapterBlock.MyHolder> {

    Context context;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ArrayList<String> id;
    FirebaseDatabase database;


    public AdapterBlock(Context context,ArrayList<String>id) {
       this.context = context;
       this.id  = id;
    }

    @NonNull
    @Override
    public AdapterBlock.MyHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.block_list_layout , parent, false);

        mAuth = FirebaseAuth.getInstance ();
        db = FirebaseFirestore.getInstance ();
        database = FirebaseDatabase.getInstance ();

        return new AdapterBlock.MyHolder ( view );
    }


    @Override
    public void onBindViewHolder(@NonNull final AdapterBlock.MyHolder holder, final int position) {


        db.collection ( "Users" ).document (id.get ( position )).addSnapshotListener ( new EventListener<DocumentSnapshot> () {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                ModalUser user = documentSnapshot.toObject ( ModalUser.class );

                holder.blockUserName.setText ( user.getNickName () );


                try{
                    Picasso.get ().load ( user.getProfilePic1 () ).placeholder ( R.drawable.dummyprofile ).fit ().into ( holder.blockUserProfileImage );
                }
                catch (Exception ec) {
                    Picasso.get ().load ( R.drawable.dummyprofile ).placeholder ( R.drawable.dummyprofile ).fit ().into ( holder.blockUserProfileImage );
                }

            }
        } );

        holder.unBlockBtn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                unBlockUser(id.get ( position ));
            }
        } );
    }

    public void unBlockUser(String id) {
        database.getReference ().child ( "BlockedUsers" )
                .child ( mAuth.getCurrentUser ().getUid () )
                .child ( id ).removeValue ().addOnSuccessListener ( new OnSuccessListener<Void> () {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText ( context, "user unblocked", Toast.LENGTH_SHORT ).show ();
            }
        } );
        database.getReference ().child ( "BlockedMe" ).child ( id ).child ( mAuth.getCurrentUser ().getUid () )
                .removeValue ();
    }




    @Override
    public int getItemCount() {
        return  id.size ();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        RoundedImageView blockUserProfileImage;
        TextView blockUserName;
        ImageView unBlockBtn;
        public MyHolder(@NonNull View itemView) {
            super ( itemView );

            blockUserProfileImage = itemView.findViewById ( R.id.block_profile_image );
            blockUserName = itemView.findViewById ( R.id.block_user_name );
            unBlockBtn = itemView.findViewById ( R.id.remove_from_block );
        }
    }

}
