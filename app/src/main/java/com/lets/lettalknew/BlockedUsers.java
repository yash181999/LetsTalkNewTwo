package com.lets.lettalknew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BlockedUsers extends AppCompatActivity {

    RecyclerView recyclerViewBlockList;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<String> blockList;
    AdapterBlock adapterBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_blocked_users );

        mAuth = FirebaseAuth.getInstance ();
        db = FirebaseFirestore.getInstance ();
        database = FirebaseDatabase.getInstance ();

        recyclerViewBlockList = findViewById ( R.id.block_recycler_list );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager ( getApplicationContext () );
        recyclerViewBlockList.setLayoutManager ( linearLayoutManager );

        blockList = new ArrayList<> (  );

        getBlockList();

    }

    public void getBlockList() {
        database.getReference ().child ( "BlockedUsers" ).child ( mAuth.getCurrentUser ().getUid () )
                .addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        blockList.clear ();
                        for(DataSnapshot ds: dataSnapshot.getChildren ()) {
                            blockList.add ( ds.getKey () );
                        }

                        adapterBlock = new AdapterBlock ( getApplicationContext (),blockList );
                        recyclerViewBlockList.setAdapter ( adapterBlock );
                        adapterBlock.notifyDataSetChanged ();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
    }
}
