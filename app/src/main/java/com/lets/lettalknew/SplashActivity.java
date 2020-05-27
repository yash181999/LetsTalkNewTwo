package com.lets.lettalknew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_splash );

        mAuth = FirebaseAuth.getInstance ();
        FirebaseUser user = mAuth.getCurrentUser ();
        db = FirebaseFirestore.getInstance ();



        checkLogin ();



    }

    public void checkLogin() {
        if(mAuth.getCurrentUser ()!=null) {


            documentReference = db.collection ( "Users" ).document (mAuth.getCurrentUser ().getUid ());
            documentReference.get (  ).addOnSuccessListener ( new OnSuccessListener<DocumentSnapshot> () {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String name = documentSnapshot.getString ( "nickName" );
                    if(name==null) {

                        new Handler ().postDelayed ( new Runnable () {
                            @Override
                            public void run() {
                                Intent intent = new Intent ( SplashActivity.this, BeforeStart.class );
                                startActivity ( intent );
                                finish ();
                            }
                        }, 2000 );

                    }
                    else{

                        new Handler ().postDelayed ( new Runnable () {
                            @Override
                            public void run() {
                                Intent intent = new Intent ( SplashActivity.this, Main.class );
                                startActivity ( intent );
                                finish ();
                            }
                        }, 2000 );

                    }
                }
            } );
        }
        else{

            new Handler ().postDelayed ( new Runnable () {
                @Override
                public void run() {
                    Intent intent = new Intent ( SplashActivity.this, LoginActivity.class );
                    startActivity ( intent );
                    finish ();                }
            }, 2000 );

        }
    }


}
