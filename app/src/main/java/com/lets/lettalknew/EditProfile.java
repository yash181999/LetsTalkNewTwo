package com.lets.lettalknew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity implements EditNickName.NickNameListener, EditGender.GenderListener, EditAge.AgeListener,
        LookingForAge.LookingForAgeListener, LookingForGender.LookingForGenderListener, LookingForLanguage.LookingForLanguageListener {

    ImageView profilePickOne,profilePickTwo,profilePickThree,profilePickFour,profilePickFive, profilePickSix;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    Uri selectImageUrl;
    Uri[] profileImage;
    int flag = 0;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference documentReference;
    StorageReference storageReference;
    FirebaseStorage storage;
    ImageView[] profilePic;

    ConstraintLayout constraintLayout;
    String userId;

    boolean imageAvailable;

    ImageView addIconOne, addIconTwo, addIconThree, addIconFour, addIconFive,addIconSix;
    ImageView removeIconOne, removeIconTwo, removeIconThree,removeIconFour, removeIconFive, removeIconSix;
    TextView nickNameText, ageText, genderText;
    TextView lookingForGenderText,lookingForAgeText, lookingForLanguageText;

    int profileUploadedCount = 0;
    boolean profileUploadedBool = false;

    FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_edit_profile );

        mAuth = FirebaseAuth.getInstance ();
        db = FirebaseFirestore.getInstance ();
        storage = FirebaseStorage.getInstance ();

        profileImage  = new Uri[6];
        profilePic = new ImageView[6];

        profilePickOne = (ImageView) findViewById ( R.id.profile_pic_one );
        profilePickTwo = (ImageView) findViewById ( R.id.profile_pic_two );
        profilePickThree = (ImageView) findViewById ( R.id.profile_pic_three );
        profilePickFour = (ImageView) findViewById ( R.id.profile_pic_four );
        profilePickFive = (ImageView) findViewById ( R.id.profile_pic_five);
        profilePickSix = (ImageView) findViewById ( R.id.profile_pic_six );


        addIconOne = findViewById ( R.id.add_icon_one );
        addIconTwo = findViewById ( R.id.add_icon_two );
        addIconThree = findViewById ( R.id.add_icon_three );
        addIconFour = findViewById ( R.id.add_icon_four );
        addIconFive = findViewById ( R.id.add_icon_five );
        addIconSix = findViewById ( R.id.add_icon_six );


        removeIconOne = findViewById ( R.id.remove_icon_one );
        removeIconTwo = findViewById ( R.id.remove_icon_two );
        removeIconThree = findViewById ( R.id.remove_icon_three );
        removeIconFour = findViewById ( R.id.remove_icon_four );
        removeIconFive = findViewById ( R.id.remove_icon_five );
        removeIconSix = findViewById ( R.id.remove_icon_six );


        nickNameText = findViewById ( R.id.nickname_text );
        genderText = findViewById ( R.id.gender_text );
        ageText = findViewById ( R.id.age_text );

        userId = mAuth.getCurrentUser ().getUid ();

        lookingForAgeText = findViewById ( R.id.looking_for_age_range_text );
        lookingForGenderText = findViewById ( R.id.looking_for_gender_text );
        lookingForLanguageText = findViewById ( R.id.looking_for_language_text );

        constraintLayout = (ConstraintLayout) findViewById ( R.id.constraintLayout );

        floatingActionButton = (FloatingActionButton) findViewById ( R.id.floating_upload_btn );

        loadImagesFromFireStore ();

        checkConnection ();

                    db.collection ( "Users" ).
                    document ( mAuth.getCurrentUser ().getUid () ).
                    update ( "userStatus", "online" );

    }

    public void pickImageOne(View view) {
        if(ContextCompat.checkSelfPermission ( getApplicationContext (), Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions ( EditProfile.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION );
        }
        else{
            flag = 1;
            CropImage.activity ().start ( EditProfile.this );

//            Picasso.get().load(selectImageUrl.toString ()).centerInside ().resize ( 500,500 ).into(profilePickOne);
        }
    }



    public void pickImageTwo(View view) {
        if(ContextCompat.checkSelfPermission ( getApplicationContext (), Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions ( EditProfile.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION );
        }
        else{
            flag = 2;
            CropImage.activity ().start ( EditProfile.this );
//            Picasso.get().load(selectImageUrl.toString ()).centerInside ().resize ( 500,500 ).into(profilePickTwo);
        }
    }

    public void pickImageThree(View view) {
        if(ContextCompat.checkSelfPermission ( getApplicationContext (), Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions ( EditProfile.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION );
        }
        else{
            flag = 3;
            CropImage.activity ().start ( EditProfile.this );
//            Picasso.get().load(selectImageUrl.toString ()).centerInside ().resize ( 500,500 ).into(profilePickThree);
        }
    }

    public void pickImageFour(View view) {
        if(ContextCompat.checkSelfPermission ( getApplicationContext (), Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions ( EditProfile.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION );
        }
        else{
            flag = 4;
            CropImage.activity ().start ( EditProfile.this );
//            Picasso.get().load(selectImageUrl.toString ()).centerInside ().resize ( 500,500 ).into(profilePickFour);
        }
    }

    public void pickImageFive(View view) {
        if(ContextCompat.checkSelfPermission ( getApplicationContext (), Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions ( EditProfile.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION );
        }
        else{
            flag = 5;
            CropImage.activity ().start ( EditProfile.this );
//            Picasso.get().load(selectImageUrl.toString ()).centerInside ().resize ( 500,500 ).into(profilePickFive);
        }
    }

    public void pickImageSix(View view) {
        if(ContextCompat.checkSelfPermission ( getApplicationContext (), Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions ( EditProfile.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION );
        }
        else{
            flag = 6;
            CropImage.activity ().start ( EditProfile.this );
//            Picasso.get().load(selectImageUrl.toString ()).centerInside ().resize ( 500,500 ).into(profilePickSix);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult ( data );
            if(resultCode == RESULT_OK) {
               selectImageUrl = result.getUri ();

               if(flag==1) {
                    profileImage[0] = selectImageUrl;

                           Picasso.get()
                                   .load(selectImageUrl.toString ())
                                   .fit ()
                                   .into(profilePickOne);
                            addIconOne.setVisibility ( View.GONE );
                            removeIconOne.setVisibility ( View.VISIBLE );

               }
               else if(flag==2) {


                   profileImage[1] = selectImageUrl;
                   Picasso.get()
                           .load(selectImageUrl.toString ())
                           .centerInside ().resize ( 500,500 )
                           .into(profilePickTwo);
                   addIconTwo.setVisibility ( View.GONE );
                   removeIconTwo.setVisibility ( View.VISIBLE );
               }

               else if(flag==3){
                   profileImage[2] = selectImageUrl;
                   Picasso.get()
                           .load(selectImageUrl.toString ())
                           .fit ()
                           .into(profilePickThree);
                   addIconThree.setVisibility ( View.GONE );
                   removeIconThree.setVisibility ( View.VISIBLE );
               }

               else if(flag==4) {
                   profileImage[3] = selectImageUrl;

                  ExifTransformation exifTransformation = new ExifTransformation ( this,selectImageUrl );

                   Picasso.get()
                           .load(profileImage[3].toString ())
                           .fit ()
                           .into(profilePickFour);
                   addIconFour.setVisibility ( View.GONE );
                   removeIconFour.setVisibility ( View.VISIBLE );
               }
               else if(flag==5)  {
                   profileImage[4] = selectImageUrl;
                   Picasso.get()
                           .load(selectImageUrl.toString ())
                           .fit ()
                           .into(profilePickFive);
                   addIconFive.setVisibility ( View.GONE );
                   removeIconFive.setVisibility ( View.VISIBLE );


               }
               else  if(flag==6) {
                   profileImage[5] = selectImageUrl;
                   Picasso.get()
                           .load(selectImageUrl.toString ())
                           .fit ()
                           .into(profilePickSix);
                   addIconSix.setVisibility ( View.GONE );
                   removeIconSix.setVisibility ( View.VISIBLE );
               }


//                if(selectImageUrl!=null ) {
//                    try {
//                        InputStream inputStream = getContentResolver ().openInputStream ( selectImageUrl );
//                        Bitmap bitmap = BitmapFactory.decodeStream ( inputStream );
//                        profilePickTwo.setImageBitmap ( bitmap );
//                    }
//                    catch (Exception e ) {
//
//                    }
//                }
            }
        }

    }
    String imageUrl;


    public void removeImageOne(View view) {

          final String userId = mAuth.getCurrentUser ().getUid ();

        documentReference = db.collection ( "Users" ).document (userId);
        documentReference.get (  ).addOnSuccessListener ( new OnSuccessListener<DocumentSnapshot> () {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String url = documentSnapshot.getString ( "profilePic1" );
                if(url!=null) {
                    db.collection ( "Users" ).document ( userId ).
                            update ( "profilePic1", FieldValue.delete () ).
                            addOnSuccessListener ( new OnSuccessListener<Void> () {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    storage.getReference ().child ( userId ).child ( "ProfilePhotos" ).child ( "ProfilePic1" ).delete ();
                                    addIconOne.setVisibility ( View.VISIBLE );
                                    removeIconOne.setVisibility ( View.GONE );
                                    profilePickOne.setImageResource ( R.drawable.dummyprofile );
                                }
                            } );


                }
            }
        } );

    }

    public void removeImageTwo(View view) {


        final String userId = mAuth.getCurrentUser ().getUid ();

        documentReference = db.collection ( "Users" ).document (userId);
        documentReference.get (  ).addOnSuccessListener ( new OnSuccessListener<DocumentSnapshot> () {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String url = documentSnapshot.getString ( "profilePic2" );
                if(url!=null) {
                    db.collection ( "Users" ).document ( userId ).
                            update ( "profilePic2", FieldValue.delete () ).
                            addOnSuccessListener ( new OnSuccessListener<Void> () {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    storage.getReference ().child ( userId ).child ( "ProfilePhotos" ).child ( "ProfilePic2" ).delete ();
                                    addIconTwo.setVisibility ( View.VISIBLE );
                                    removeIconTwo.setVisibility ( View.GONE );
                                    profilePickTwo.setImageResource ( R.drawable.dummyprofile );
                                }
                            } );


                }
            }
        } );

    }

    public void removeImageThree(View view) {


        final String userId = mAuth.getCurrentUser ().getUid ();

        documentReference = db.collection ( "Users" ).document (userId);
        documentReference.get (  ).addOnSuccessListener ( new OnSuccessListener<DocumentSnapshot> () {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String url = documentSnapshot.getString ( "profilePic3" );
                if(url!=null) {
                    db.collection ( "Users" ).document ( userId ).
                            update ( "profilePic3", FieldValue.delete () ).
                            addOnSuccessListener ( new OnSuccessListener<Void> () {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    storage.getReference ().child ( userId ).child ( "ProfilePhotos" ).child ( "ProfilePic3" ).delete ();
                                    addIconThree.setVisibility ( View.VISIBLE );
                                    removeIconThree.setVisibility ( View.GONE );
                                    profilePickThree.setImageResource ( R.drawable.dummyprofile );
                                }
                            } );


                }
            }
        } );
    }

    public void removeImageFour(View view) {


        final String userId = mAuth.getCurrentUser ().getUid ();

        documentReference = db.collection ( "Users" ).document (userId);
        documentReference.get (  ).addOnSuccessListener ( new OnSuccessListener<DocumentSnapshot> () {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String url = documentSnapshot.getString ( "profilePic4" );
                if(url!=null) {
                    db.collection ( "Users" ).document ( userId ).
                            update ( "profilePic4", FieldValue.delete () ).
                            addOnSuccessListener ( new OnSuccessListener<Void> () {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    storage.getReference ().child ( userId ).child ( "ProfilePhotos" ).child ( "ProfilePic4" ).delete ();
                                    addIconFour.setVisibility ( View.VISIBLE );
                                    removeIconFour.setVisibility ( View.GONE );
                                    profilePickFour.setImageResource ( R.drawable.dummyprofile );
                                }
                            } );


                }
            }
        } );
    }

    public void removeImageFive(View view) {


        final String userId = mAuth.getCurrentUser ().getUid ();

        documentReference = db.collection ( "Users" ).document (userId);
        documentReference.get (  ).addOnSuccessListener ( new OnSuccessListener<DocumentSnapshot> () {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String url = documentSnapshot.getString ( "profilePic5" );
                if(url!=null) {
                    db.collection ( "Users" ).document ( userId ).
                            update ( "profilePic5", FieldValue.delete () ).
                            addOnSuccessListener ( new OnSuccessListener<Void> () {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    storage.getReference ().child ( userId ).child ( "ProfilePhotos" ).child ( "ProfilePic5" ).delete ();
                                    addIconFive.setVisibility ( View.VISIBLE );
                                    removeIconFive.setVisibility ( View.GONE );
                                    profilePickFive.setImageResource ( R.drawable.dummyprofile );
                                }
                            } );


                }
            }
        } );
    }

    public void removeImageSix(View view) {


        final String userId = mAuth.getCurrentUser ().getUid ();

        documentReference = db.collection ( "Users" ).document (userId);
        documentReference.get (  ).addOnSuccessListener ( new OnSuccessListener<DocumentSnapshot> () {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String url = documentSnapshot.getString ( "profilePic6" );
                if(url!=null) {
                    db.collection ( "Users" ).document ( userId ).
                            update ( "profilePic6", FieldValue.delete () ).
                            addOnSuccessListener ( new OnSuccessListener<Void> () {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    storage.getReference ().child ( userId ).child ( "ProfilePhotos" ).child ( "ProfilePic6" ).delete ();
                                    addIconSix.setVisibility ( View.VISIBLE );
                                    removeIconSix.setVisibility ( View.GONE );
                                    profilePickSix.setImageResource ( R.drawable.dummyprofile );
                                }
                            } );


                }
            }
        } );
    }



    public void uploadProfile(View view) throws IOException {

        final String userId = mAuth.getCurrentUser ().getUid ();
        documentReference = db.collection ( "Users" ).document (userId);
        for( int i=0;i<profileImage.length;i++) {

            if (profileImage[i] != null) {
                floatingActionButton.setClickable ( false );
                floatingActionButton.setAlpha ( (float) 0.5 );
                Toast.makeText ( getApplicationContext (), "uploading", Toast.LENGTH_SHORT ).show ();
                //compressing image size
                Bitmap bitmap = MediaStore.Images.Media.getBitmap ( this.getContentResolver (), profileImage[i] );
                ByteArrayOutputStream baos = new ByteArrayOutputStream ();
                bitmap.compress ( Bitmap.CompressFormat.JPEG, 50, baos );
                byte[] data = baos.toByteArray ();
                //compressing image size

                storageReference = storage.getReference ().child ( userId ).child ( "ProfilePhotos" ).child ( "ProfilePic" + (i + 1) );
                final String name = "profilePic" + (i+1);

                UploadTask uploadTask = storageReference.putBytes ( data );
                uploadTask.addOnSuccessListener ( new OnSuccessListener<UploadTask.TaskSnapshot> () {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       storageReference.getDownloadUrl ().addOnSuccessListener ( new OnSuccessListener<Uri> () {
                           @Override
                           public void onSuccess(Uri uri) {
                              documentReference.update ( name,uri.toString () );
                              profileUploadedCount = 1;


                           }
                       } );
                    }
                } ).addOnFailureListener ( new OnFailureListener () {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText ( EditProfile.this, "some error occurred", Toast.LENGTH_SHORT ).show ();
                    }
                } );

            }
        }

         uploadChoices(documentReference);

                    db.collection ( "Users" ).
                    document ( mAuth.getCurrentUser ().getUid () ).
                    update ( "userStatus", "online" );
    }


    public void uploadChoices(DocumentReference documentReference) {
        floatingActionButton.setClickable ( false );
        floatingActionButton.setAlpha ( (float) 0.5 );
        String nickName = nickNameText.getText ().toString ();
        String gender = genderText.getText ().toString ();
        String age = ageText.getText ().toString ();
        String lookingForGender = lookingForGenderText.getText ().toString ();
        String lookingForAgeRange = lookingForAgeText.getText ().toString ();
        String lookingForLanguage = lookingForLanguageText.getText ().toString ();
        Toast.makeText ( getApplicationContext (), "uploading", Toast.LENGTH_SHORT ).show ();

       Map<String , String> profileData = new HashMap<> (  );

       documentReference.update ( "nickName",nickName );
       documentReference.update ( "gender",gender );
       documentReference.update ( "age",age );
       documentReference.update ( "lookingForGender", lookingForGender );
       documentReference.update ( "lookingForAgeRange",lookingForAgeRange );
       documentReference.update ( "lookingForLanguage",lookingForLanguage ).addOnCompleteListener ( new OnCompleteListener<Void> () {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               floatingActionButton.setClickable ( true );
               floatingActionButton.setAlpha ( (float) 1 );
               Toast.makeText ( EditProfile.this, "uploaded", Toast.LENGTH_SHORT ).show ();
           }
       } );

    }


    public  void loadImagesFromFireStore() {
        String userId = mAuth.getCurrentUser ().getUid ();

        db.collection ( "Users" ).document (userId).addSnapshotListener ( new EventListener<DocumentSnapshot> () {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                try {
                    if (e == null) {
                        String urlOne = documentSnapshot.getString ( "profilePic1" );
                        if (urlOne != null) {
                            Picasso.get ()
                                    .load ( urlOne.toString () )
                                    .fit ()
                                    .into ( profilePickOne );
                            addIconOne.setVisibility ( View.GONE );
                            removeIconOne.setVisibility ( View.VISIBLE );
                        }


                     String urlTwo  = documentSnapshot.getString ( "profilePic2" );

                     if(urlTwo!=null) {
                         Picasso.get()
                            .load(urlTwo.toString ())
                            .fit ()
                            .into(profilePickTwo);
                         addIconTwo.setVisibility ( View.GONE );
                      removeIconTwo.setVisibility ( View.VISIBLE );
                     }

                     String urlThree  = documentSnapshot.getString ( "profilePic3" );

                     if(urlThree!=null) {
                         Picasso.get()
                            .load(urlThree.toString ())
                            .fit ()
                            .into(profilePickThree);
                         addIconThree.setVisibility ( View.GONE );
                         removeIconThree.setVisibility ( View.VISIBLE );
                  }

                     String urlFour  = documentSnapshot.getString ( "profilePic4" );

                        if(urlFour!=null) {
                            Picasso.get()
                                    .load(urlFour.toString ())
                                    .fit ()
                                    .into(profilePickFour);
                            addIconFour.setVisibility ( View.GONE );
                            removeIconFour.setVisibility ( View.VISIBLE );
                        }

                        String urlFive  = documentSnapshot.getString ( "profilePic5" );

                        if(urlFive!=null) {
                            Picasso.get()
                                    .load(urlFive.toString ())
                                    .fit ()
                                    .into(profilePickFive);
                            addIconFive.setVisibility ( View.GONE );
                            removeIconFive.setVisibility ( View.VISIBLE );
                        }

                        String urlSix  = documentSnapshot.getString ( "profilePic6" );

                        if(urlSix!=null) {
                            Picasso.get()
                                    .load(urlFour.toString ())
                                    .fit ()
                                    .into(profilePickSix);
                            addIconSix.setVisibility ( View.GONE );
                            removeIconSix.setVisibility ( View.VISIBLE );
                        }

                                String nickName = documentSnapshot.getString ( "nickName" );
                                String gender = documentSnapshot.getString ( "gender" );
                                String age = documentSnapshot.getString ( "age" );
                                String lookingForGender = documentSnapshot.getString ( "lookingForGender" );
                                String lookingForAgeRange = documentSnapshot.getString ( "lookingForAgeRange" );
                                String lookingForLanguage = documentSnapshot.getString ( "lookingForLanguage" );

                                if(lookingForGender!=null) {
                                    nickNameText.setText ( nickName );
                                    genderText.setText ( gender );
                                    ageText.setText ( age );
                                    lookingForGenderText.setText ( lookingForGender );
                                    lookingForAgeText.setText ( lookingForAgeRange );
                                    lookingForLanguageText.setText ( lookingForLanguage );
                                }

                    } else {
                        Toast.makeText ( EditProfile.this, "e : " + e, Toast.LENGTH_SHORT ).show ();
                    }
                } catch (Exception ex) {
                    Toast.makeText ( EditProfile.this, "e : " + ex, Toast.LENGTH_SHORT ).show ();
                }
            }

        } );

    }

  public void editNickname(View view) {
      EditNickName editNickName  =  new EditNickName ();
      editNickName.show(getSupportFragmentManager (),"editNickname");
  }

    public void editGender(View view) {
        EditGender editGender = new EditGender ();
        editGender.show ( getSupportFragmentManager (),"editGender" );
    }

    public void editAge(View view) {
        EditAge editAge = new EditAge ();
        editAge.show ( getSupportFragmentManager (),"age" );
    }

    public void lookingForGender(View view) {
        LookingForGender lookingForGender = new LookingForGender ();
        lookingForGender.show ( getSupportFragmentManager (),"gender" );
    }

    public void lookingForAgeRage(View view) {
           LookingForAge lookingForAge = new LookingForAge ();
           lookingForAge.show ( getSupportFragmentManager (),"agerange" );
    }

    public void lookingForLanguage(View view) {
        LookingForLanguage lookingForLanguage = new LookingForLanguage ();
        lookingForLanguage.show ( getSupportFragmentManager (),"lanugage" );
    }

    public void goBack(View view) {
        startActivity ( new Intent ( getApplicationContext (),Main.class ) );
        finish ();
    }


    @Override
    public void applyNickName(String nickName) {
         nickNameText.setText ( nickName.toString () );
    }

    @Override
    public void applyGender(String gender) {
        genderText.setText (  gender);
    }

    @Override
    public void applyAge(String age) {
        ageText.setText ( age );
    }

    @Override
    public void applyLookingForAge(String age) {
        lookingForAgeText.setText ( age );
    }

    @Override
    public void applyLookingForGender(String gender) {
        lookingForGenderText.setText ( gender );
    }

    @Override
    public void applyLang(String lang) {
        lookingForLanguageText.setText ( lang );
    }

    @Override
    protected void onStart() {
        super.onStart ();
        checkConnection ();
        if(mAuth.getCurrentUser ()!=null) {
            String currentDate;
            Calendar calendar = Calendar.getInstance ();
            SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd/MM/yyyy HH:mm:ss" );
            currentDate = currentDateTime.format ( calendar.getTime () );
            db.collection ( "Users" ).document ( userId ).update ( "lastSeenTime", currentDate );
            db.collection ( "Users" ).document ( userId ).update ( "userStatus", "online" );
        }
    }

    @Override
    protected void onResume() {
        super.onResume ();

        checkConnection ();
        if(mAuth.getCurrentUser ()!=null) {
            String currentDate;
            Calendar calendar = Calendar.getInstance ();
            SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd/MM/yyyy HH:mm:ss" );
            currentDate = currentDateTime.format ( calendar.getTime () );
            db.collection ( "Users" ).document ( userId ).update ( "lastSeenTime", currentDate );
            db.collection ( "Users" ).document ( userId ).update ( "userStatus", "online" );
        }

    }

    @Override
    protected void onPause() {
        super.onPause ();
        checkConnection ();
        if(mAuth.getCurrentUser ()!=null) {
            String currentDate;
            Calendar calendar = Calendar.getInstance ();
            SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd/MM/yyyy HH:mm:ss" );
            currentDate = currentDateTime.format ( calendar.getTime () );
            db.collection ( "Users" ).document ( userId ).update ( "lastSeenTime", currentDate );
            db.collection ( "Users" ).document ( userId ).update ( "userStatus", "paused" );
        }
    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy ();
//        if(mAuth.getCurrentUser ()!=null) {
//            String currentDate;
//            Calendar calendar = Calendar.getInstance ();
//            SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd/MM/yyyy HH:mm:ss" );
//            currentDate = currentDateTime.format ( calendar.getTime () );
//            db.collection ( "Users" ).document ( userId ).update ( "lastSeenTime", currentDate );
//            db.collection ( "Users" ).document ( userId ).update ( "userStatus", "offline" );
//        }
//    }
    int count = 0;
    @Override
    public void onBackPressed() {
        count++;

        if(count == 1) {
            Toast.makeText (getApplicationContext (), "Do you want to save cahnges", Toast.LENGTH_SHORT ).show ();
        }
        else{
            startActivity ( new Intent ( getApplicationContext (),Main.class ) );
            finish ();
        }

    }

    public void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext ().getSystemService ( Context.CONNECTIVITY_SERVICE );

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo ();

        if(activeNetwork!=null) {
            if(activeNetwork.getType () == ConnectivityManager.TYPE_WIFI) {

            }

            else if(activeNetwork.getType ()==ConnectivityManager.TYPE_MOBILE) {

            }
            else {
                Toast.makeText ( this, "not internet connection", Toast.LENGTH_SHORT ).show ();
            }

        }
        else {
            Toast.makeText ( this, "not internet connection", Toast.LENGTH_SHORT ).show ();
        }

    }

}


