package com.lets.lettalknew;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteAccountDialog  extends DialogFragment {

    FirebaseDatabase database;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance ();
        db = FirebaseFirestore.getInstance ();
        database = FirebaseDatabase.getInstance ();
        user = mAuth.getCurrentUser ();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       deleteAccount();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void deleteAccount() {


        db.collection ( "Users" ).document (user.getUid ())
                .get ().addOnSuccessListener ( new OnSuccessListener<DocumentSnapshot> () {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ModalUser userDetails = documentSnapshot.toObject ( ModalUser.class );

                db.collection ( "DeletedUsers" ).document (user.getUid ())
                        .set ( userDetails )
                        .addOnSuccessListener ( new OnSuccessListener<Void> () {
                            @Override
                            public void onSuccess(Void aVoid) {

                               deleteChatList();
                               deleteBlock();
                               deleteFavorites();
                               deleteAc();
                            }
                        } );

            }
        } );




    }


    public void deleteChatList(){
        try{
            database.getReference ().child ( "ChatList" ).addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds: dataSnapshot.getChildren ()){
                        if(ds.getChildren ().equals ( user.getUid () )){
                            ds.getRef ().removeValue ();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }
        catch (Exception e) {

        }

    }


    public void deleteFavorites(){
        try{
            database.getReference ().child ( "Favorites" ).child ( user.getUid () ).removeValue ();
            database.getReference ().child ( "Favorites" ).getDatabase ().getReference ().child ( user.getUid () )
                    .removeValue ();
        }
        catch (Exception e){

        }
    }

    public void deleteBlock() {
        try{
            database.getReference ().child ( "BlockedUsers" ).child ( user.getUid () ).removeValue ();
            database.getReference ().child ( "BlockedMe" ).child ( user.getUid () ).removeValue ();
            database.getReference ().child ( "BlockedUsers" ).getDatabase ().getReference ().
                    child ( user.getUid () ).removeValue ();
            database.getReference ().child ( "BlockedMe" ).getDatabase ().getReference ().child ( user.getUid () )
                    .removeValue ();
        }
        catch (Exception e){

        }

    }


    public void deleteAc() {
        user.delete ().addOnSuccessListener ( new OnSuccessListener<Void> () {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText ( getContext (), "account deleted", Toast.LENGTH_SHORT ).show ();
                startActivity (  new Intent ( getContext (),LoginActivity.class ) );

            }
        } );

    }


}
