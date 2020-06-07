package com.lets.lettalknew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Set;

public class Settings extends AppCompatActivity {

    MaterialCheckBox showMeInDiscovery,dontShareLastSeen;
    LinearLayout deleteMyAccount,logout,privacyAndTerms;
    FirebaseDatabase database;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_settings );
        mAuth = FirebaseAuth.getInstance ();
        db = FirebaseFirestore.getInstance ();
        database = FirebaseDatabase.getInstance ();
        user = mAuth.getCurrentUser ();
        showMeInDiscovery = findViewById ( R.id.show_me_indiscovery );
        dontShareLastSeen = findViewById ( R.id.lastSeen );
//        deleteMyAccount = findViewById ( R.id.delete_my_account );
        logout= findViewById ( R.id.logout );
        privacyAndTerms = findViewById ( R.id.privacy_and_terms );

        loadStatus();


        showMeInDiscovery.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    db.collection ( "Users" ).document (user.getUid ()).update ( "showMeInDiscovery" ,false);
                }
                else{
                    db.collection ( "Users" ).document (user.getUid ()).update ( "showMeInDiscovery" ,true);
                }
            }
        } );

        dontShareLastSeen.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    db.collection ( "Users" ).document ( user.getUid () ).update ( "shareLastSeen", false );
                }
                else{
                    db.collection ( "Users" ).document ( user.getUid () ).update ( "shareLastSeen", true );

                }
            }
        } );
    }

    public void deleteAccount(View view) {

        DeleteAccountDialog deleteAccountDialog = new DeleteAccountDialog ();
        deleteAccountDialog.show ( getSupportFragmentManager (),"" );




    }

    public void loadStatus() {
        db.collection ( "Users" ).document (user.getUid ())
                .addSnapshotListener ( new EventListener<DocumentSnapshot> () {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.getBoolean ( "showMeInDiscovery" ) == true) {
                            showMeInDiscovery.setChecked ( false );
                        }
                        else{
                            showMeInDiscovery.setChecked ( true );
                        }

                        if(documentSnapshot.getBoolean ( "shareLastSeen" ) == true){
                            dontShareLastSeen.setChecked ( false );
                        }
                        else{
                            dontShareLastSeen.setChecked ( true );
                        }
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



    public void logout(View view) {
        mAuth.signOut ();
        startActivity ( new Intent ( getApplication (),LoginActivity.class ) );
        finish ();
    }


    public void privacy(View view) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        startActivity(myIntent);
    }
    public void settingsBack(View view){
        finish ();
    }

}
