package com.lets.lettalknew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    CheckBox checkBox1,checkBox2,checkBox3;
    Button loginWithGoogleBtn;
    Button loginWithFacebookBtn;
    TextView checkAllBoxesText;
    ProgressBar  progressBar;


    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference documentReference;
    String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.login_activity );
        checkBox1 = findViewById ( R.id.materialCheckBox );
        checkBox2 = findViewById ( R.id.materialCheckBox2 );
        checkBox3 = findViewById ( R.id.materialCheckBox3 );

        loginWithGoogleBtn = findViewById ( R.id.google_login_btn );
        loginWithFacebookBtn = findViewById ( R.id.facebook_login_btn );
        checkAllBoxesText = findViewById ( R.id.checkAllBoxes );

        progressBar = findViewById ( R.id.progress_bar );

        mAuth = FirebaseAuth.getInstance ();
        db=  FirebaseFirestore.getInstance ();



        //google //

//        signInButton = findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        //google //



    }

    public void loginWithGoogle(View view) {

        if(checkBox1.isChecked ()&&checkBox2.isChecked ()&&checkBox3.isChecked ()) {

            loginWithGoogleBtn.setVisibility ( View.GONE );
            progressBar.setVisibility ( View.VISIBLE );
            loginWithFacebookBtn.setVisibility ( View.GONE );
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
        else {
            checkAllBoxesText.setVisibility ( View.VISIBLE );
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult( ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

                Toast.makeText ( getApplicationContext (), "success", Toast.LENGTH_SHORT ).show ();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText ( this, "failed " +e, Toast.LENGTH_SHORT ).show ();
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            userId = mAuth.getCurrentUser ().getUid ();

                            DocumentReference documentReference  = db.collection ( "Users" ).document (userId);

                            //updating UI
                            updateUi(documentReference);
                            //updating UI

                            Toast.makeText ( getApplicationContext (), "Sign in successfull", Toast.LENGTH_SHORT ).show ();


                        } else {
                            // If sign in fails, display a message to the user.+

                            Toast.makeText ( getApplicationContext (), "Sign in Failed", Toast.LENGTH_SHORT ).show ();
                        }

                        // ...
                    }
                }).addOnFailureListener ( new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText ( getApplicationContext (), e+" ", Toast.LENGTH_SHORT ).show ();

            }
        } );
    }

    //google sign in end;

    public void updateUi(final DocumentReference documentReference) {
        documentReference.get (  ).addOnSuccessListener ( new OnSuccessListener<DocumentSnapshot> () {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString ( "nickName" );
                if(name!=null) {
                    startActivity ( new Intent ( getApplicationContext (),Main.class ) );
                }

                else {
                    startActivity ( new Intent ( getApplicationContext (),BeforeStart.class ) );
                }
            }
        } );


    }


}
