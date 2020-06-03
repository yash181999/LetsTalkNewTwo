package com.lets.lettalknew;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class AdapterFavorites extends RecyclerView.Adapter<AdapterFavorites.MyHolder>  {
    Context context;
    ArrayList<String> id;
    Dialog infoDialog;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    DocumentReference documentReference;
    FirebaseDatabase firebaseDatabase;


    public AdapterFavorites(Context context, ArrayList<String> id) {
        this.context = context;
        this.id = id;

    }

    @NonNull
    @Override
    public AdapterFavorites.MyHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.favorite_list_layout , parent, false);

        mAuth = FirebaseAuth.getInstance ();
        db =FirebaseFirestore.getInstance ();

        return new AdapterFavorites.MyHolder ( view );
    }


    @Override
    public void onBindViewHolder(@NonNull final AdapterFavorites.MyHolder holder, final int position) {

        db.collection ( "Users" ).document (id.get ( position )).
                addSnapshotListener ( new EventListener<DocumentSnapshot> () {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                ModalUser user = documentSnapshot.toObject ( ModalUser.class );

                holder.favoriteName.setText ( user.getNickName () );
                String status = user.getUserStatus ();
                if(status.equals ( "offline" ) || status.equals ( "paused" )) {
                    holder.favoriteStatus.setText ( documentSnapshot.getString ( "lastSeenTime" ) );
                }
                else{
                    holder.favoriteStatus.setText ( user.getUserStatus () );
                }

                try{
                    Picasso.get ().load ( user.getProfilePic1 () ).placeholder ( R.drawable.dummyprofile ).fit ().into ( holder.profileImage );
                }
                catch (Exception ec) {
                    Picasso.get ().load ( R.drawable.dummyprofile ).placeholder ( R.drawable.dummyprofile ).fit ().into ( holder.profileImage );
                }



            }
        } );

        holder.itemView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( context,ChatActivity.class );
                intent.putExtra ( "id",id.get ( position ) );
                context.startActivity ( intent );
            }
        } );

        infoDialog = new Dialog ( context );
        infoDialog.setContentView ( R.layout.favorite_info_layout);

        final TextView removeFromFavorites = infoDialog.findViewById ( R.id.remove_from_favorites );
        TextView blockUser = infoDialog.findViewById ( R.id.favorite_block_user );


        holder.itemView.setOnLongClickListener ( new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {

                infoDialog.show ();

                return false;
            }
        } );

        removeFromFavorites.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                removeFromFavorites(id.get ( position ));

            }
        } );

    }

    public void removeFromFavorites(String id) {
        FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" )
                .child ( mAuth.getCurrentUser ().getUid () ).
                 child ( id ).removeValue ().
                addOnSuccessListener ( new OnSuccessListener<Void> () {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText ( context, "removed from favorites", Toast.LENGTH_SHORT ).show ();
                infoDialog.dismiss ();
            }
        } );
    }

    @Override
    public int getItemCount() {
        return  id.size ();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        RoundedImageView profileImage;
        TextView favoriteName;
        TextView favoriteStatus;
        public MyHolder(@NonNull View itemView) {
            super ( itemView );

           favoriteName = itemView.findViewById ( R.id.favorite_name );
            favoriteStatus = itemView.findViewById ( R.id.favorite_status );
           profileImage = itemView.findViewById ( R.id.favorite_profile_image );


        }
    }


}




