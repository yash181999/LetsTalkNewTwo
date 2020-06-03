package com.lets.lettalknew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Set;

public class Main extends AppCompatActivity implements
        LookingForGender.LookingForGenderListener,LookingForAge.LookingForAgeListener{
     FirebaseAuth mAuth;
     ImageView editProfileIcon;
     NavigationView navigationView;
     ImageView editProfileICon;
     FirebaseFirestore db;
     DocumentReference documentReference;

     TextView genderDetail, name;

     String userId;

     private ListView chatListView;

     ArrayList<String> blockList;

     String userStatus;

     FloatingActionButton searchRandomChat;

     RoundedImageView roundedImageView;

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main2 );

        db = FirebaseFirestore.getInstance ();
        mAuth = FirebaseAuth.getInstance ();

        FirebaseUser user = mAuth.getCurrentUser ();
        userId = user.getUid ();

        documentReference = db.collection ( "Users" ).document (userId);

        tabLayout = (TabLayout) findViewById ( R.id.tab_layout );

        viewPager  = findViewById ( R.id.view_pager );

        toolbar = findViewById ( R.id.toolBar );

        setSupportActionBar ( toolbar );
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setUpViewPager ( viewPager );

        checkConnection();

        tabLayout.setupWithViewPager ( viewPager );

        final DrawerLayout drawerLayout = findViewById ( R.id.drawer_layout );

        findViewById ( R.id.menu_icon ).setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer ( GravityCompat.START );
            }
        } );

        navigationView = (NavigationView) findViewById ( R.id.navigation_view );

        View headerView = navigationView.getHeaderView(0);

        roundedImageView = navigationView.getHeaderView ( 0 ).findViewById ( R.id.roundedImageView );

        genderDetail = navigationView.getHeaderView ( 0 ).findViewById ( R.id.gender );
        name = navigationView.getHeaderView ( 0 ).findViewById ( R.id.name );

        searchRandomChat = findViewById ( R.id.search_random_user );

        blockList = new ArrayList<> (  );


        //edit profile
        headerView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent ( getApplicationContext (),EditProfile.class) );
                finish ();
            }
        } );
        //edit profile

        loadProfileDetails ();

        /**navigation View**/
        navigationView.setNavigationItemSelectedListener ( new NavigationView.OnNavigationItemSelectedListener () {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId ()) {
                    case R.id.nav_gender:
                        lookingForGender();
                        break;
                    case R.id.nav_age_range:
                        lookingForAgeRange();
                        break;
                    case R.id.nav_blocked_users:
                        navBlockedUsers();
                        break;
                    case R.id.nav_settings:
                        navSettings();
                        break;
                }
                return false;
            }
        } );
        /**navigation View**/


                    db.collection ( "Users" ).
                    document ( mAuth.getCurrentUser ().getUid () ).
                    update ( "userStatus", "online" );

    }



   /**Loading Profile**/
    public void loadProfileDetails() {
        String userId = mAuth.getCurrentUser ().getUid ();
        documentReference = db.collection ( "Users" ).document ( userId );

        documentReference.addSnapshotListener ( new EventListener<DocumentSnapshot> () {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e==null) {
                   String profilePic =  documentSnapshot.getString ("profilePic1");
                    if (profilePic != null) {

                        Picasso.get ().
                                load ( profilePic )
                                .into ( roundedImageView );
                    }
                    String nickName = documentSnapshot.getString ( "nickName" );
                    if(nickName!= null ) {
                        name.setText (  nickName);
                    }
                    String gender = documentSnapshot.getString ( "gender" );
                    if(gender!=null) {
                        genderDetail.setText(gender);
                    }
                    String lookingForGenderDetails = documentSnapshot.getString ( "lookingForGender" );
                    if(lookingForGenderDetails!=null) {
                        navigationView.getMenu ().getItem ( 0 ).getSubMenu ().getItem (0).setTitle ( "Gender   " + lookingForGenderDetails );
                    }
                    String lookingForAgeRange = documentSnapshot.getString ( "lookingForAgeRange" );
                    if(lookingForAgeRange !=null) {
                        navigationView.getMenu ().getItem ( 0 ).getSubMenu ().getItem (1).setTitle ( "Age range   " + lookingForAgeRange );
                    }

                }
            }
        } );
    }  /**Loading Profile**/


    public void lookingForGender() {
        LookingForGender lookingForGender= new LookingForGender ();
        lookingForGender.show ( getSupportFragmentManager (),"lookingForGender" );
    }

    public void lookingForAgeRange() {
        LookingForAge lookingForAge = new LookingForAge ();
        lookingForAge.show ( getSupportFragmentManager (),"lookingForAge" );


    }

    public void navBlockedUsers() {
        startActivity ( new Intent ( getApplicationContext (),BlockedUsers.class ) );
    }

    public void navSettings() {
        startActivity ( new Intent ( getApplicationContext (), Settings.class ) );
    }


    @Override
    public void applyLookingForGender(String gender) {
         navigationView.getMenu ().getItem ( 0 ).getSubMenu ().getItem (0).setTitle ( "Gender   " + gender );
          documentReference.update ( "lookingForGender",gender);
    }

    @Override
    public void applyLookingForAge(String age) {
        navigationView.getMenu ().getItem ( 0 ).getSubMenu ().getItem (1).setTitle ( "Age range   " + age );
        documentReference.update ( "lookingForAgeRange",age);
    }

    @Override
    protected void onStart() {
        super.onStart ();
        checkConnection ();

        if(mAuth.getCurrentUser ()!=null) {
            userStatus = "online";
            db.collection ( "Users" ).
                    document ( mAuth.getCurrentUser ().getUid () ).
                    update ( "userStatus", "online" );
        }

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy ();
//        checkConnection ();
//        userStatus = "offline";
//        if(mAuth.getCurrentUser ()!=null) {
//            String currentDate;
//            Calendar calendar = Calendar.getInstance ();
//            SimpleDateFormat currentDateTime = new SimpleDateFormat ( "dd/MM/yyyy HH:mm:ss" );
//            currentDate = currentDateTime.format ( calendar.getTime () );
//            db.collection ( "Users" ).document ( userId ).update ( "lastSeenTime", currentDate );
//            db.collection ( "Users" ).document ( userId ).update ( "userStatus", "offline" );
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause ();
        super.onStop ();
        userStatus = "offline";
        if(mAuth.getCurrentUser ()!=null) {
            db.collection ( "Users" ).document ( userId ).update ( "userStatus", "paused" );
        }
    }

    String nameRandomUser = "";
    String idRandom  = "";
    int flag = 0;
    public void searchRandomUser(View view) {

        searchRandomChat.setVisibility ( View.GONE );
        final ProgressDialog progressDialog = new ProgressDialog ( this );
        progressDialog.setMessage ( "Searching the Random user" );
        progressDialog.show ();

        db.collection ( "Users" ).addSnapshotListener ( new EventListener<QuerySnapshot> () {
            @Override
            public void onEvent(@Nullable final QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e==null) {
                    final ArrayList<String> randomId = new ArrayList<> (  );


                    FirebaseDatabase.getInstance ().getReference ()
                            .child ( "BlockedMe" ).child ( mAuth.getCurrentUser ().getUid () )
                            .addValueEventListener ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    blockList.clear ();
                                    for(DataSnapshot ds : dataSnapshot.getChildren ()){
                                        blockList.add (  ds.getKey ());
                                    }


                                    for (final DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments ()) {
                                        if (!documentSnapshot.getId ().equals (userId ) && !blockList.contains ( documentSnapshot.getId () )) {
                                            randomId.add ( documentSnapshot.getId () );
                                        }

                                    }

                                    final String idRandom = randomId.get ( new Random (  ).nextInt (randomId.size ()) ) ;

                                    flag++;
                                    if(flag == 1) {
                                        if(!idRandom.isEmpty () ){

                                            Intent intent = new Intent ( getApplicationContext (), ChatActivity.class );
                                            intent.putExtra ( "id", idRandom );
                                            startActivity ( intent );
                                            searchRandomChat.setVisibility ( View.VISIBLE );
                                            progressDialog.dismiss ();
                                        }
                                        else{

                                            Toast.makeText ( Main.this, "no user found", Toast.LENGTH_SHORT ).show ();
                                            searchRandomChat.setVisibility ( View.VISIBLE );
                                        }
                                    }

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            } );
                }
                else{
                    Toast.makeText ( Main.this, "e : "  +  e, Toast.LENGTH_SHORT ).show ();
                }
            }
        });
        flag = 0;
    }

    private void setUpViewPager(ViewPager viewPager) {
        PageAdapter pageAdapter = new PageAdapter ( getSupportFragmentManager () );

        pageAdapter.addFragment ( new PreviousChats (), "TALKS" );
        pageAdapter.addFragment ( new Favourites (),"FAVORITES" );
        viewPager.setAdapter ( pageAdapter );
    }

    @Override
    protected void onResume() {
        super.onResume ();
        checkConnection ();
    }

    public void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext ().getSystemService ( Context.CONNECTIVITY_SERVICE );

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
