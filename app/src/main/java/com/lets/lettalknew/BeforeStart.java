package com.lets.lettalknew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BeforeStart extends AppCompatActivity {

    CheckBox  checkBoxMan,checkBoxDog, checkBoxTime;
    TextView   checkWarningText;
    Button talkBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference documentReference;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_before_start );

        checkBoxDog = (CheckBox) findViewById ( R.id.check_box_dog );
        checkBoxTime = (CheckBox) findViewById ( R.id.check_box_good );
        checkBoxMan = (CheckBox) findViewById ( R.id.check_box_men );
        checkWarningText = (TextView) findViewById ( R.id.check_warning_text);

        mAuth = FirebaseAuth.getInstance ();
        FirebaseUser user = mAuth.getCurrentUser ();
        String userId = user.getUid ();

        db=  FirebaseFirestore.getInstance ();
        documentReference = db.collection ( "Users" ).document (userId);


    }


    public void startTalking(View view) {

        if(checkBoxTime.isChecked () && checkBoxDog.isChecked () && checkBoxMan.isChecked ()) {
            checkWarningText.setVisibility ( View.GONE );

            Map<String , String> userDetails = new HashMap<> (  );

            userDetails.put ( "nickName","annonymous" );
            userDetails.put ( "age","18" );
            userDetails.put ( "gender","male" );

            documentReference.set ( userDetails ).addOnSuccessListener ( new OnSuccessListener<Void> () {
                @Override
                public void onSuccess(Void aVoid) {

                    startActivity(new Intent ( getApplicationContext (),Main.class ) );
                    finish ();

                }
            } ).addOnFailureListener ( new OnFailureListener () {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText ( BeforeStart.this, "e : " +  e , Toast.LENGTH_SHORT ).show ();
                }
            } );

        }
        else{
            checkWarningText.setVisibility ( View.VISIBLE );
        }
    }





}
