package com.lets.lettalknew;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.local.QueryResult;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Favourites extends Fragment {



    RecyclerView favoritesList;
    List<ModalUser> userList;
    ArrayList<String> id;
    AdapterFavorites favoriteAdapter;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public Favourites() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate ( R.layout.fragment_favourites, container, false );

        favoritesList = view.findViewById ( R.id.favorite_list );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager ( getContext () );

        favoritesList.setHasFixedSize ( true );

        favoritesList.setLayoutManager ( linearLayoutManager );

        mAuth = FirebaseAuth.getInstance ();
        db = FirebaseFirestore.getInstance ();

        gettingData();




        return view;
    }

    public void gettingData() {

        id = new ArrayList<> (  );
        userList = new ArrayList<> (  );

        FirebaseDatabase.getInstance ().getReference ().child ( "Favorites" ).
                child ( mAuth.getCurrentUser ().getUid () )
                .addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren ()){
                    String userId = ds.getKey ();
                    id.add ( userId );
                }
                favoriteAdapter = new AdapterFavorites (getContext (),id );
                favoritesList.setAdapter ( favoriteAdapter );
                favoriteAdapter.notifyDataSetChanged ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }



}
