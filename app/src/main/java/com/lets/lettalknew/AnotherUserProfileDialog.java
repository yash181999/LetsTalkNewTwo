package com.lets.lettalknew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class AnotherUserProfileDialog extends DialogFragment {

    ImageView profileImage;
    TextView  name;
    TextView gender;

    FirebaseFirestore db;
    DocumentReference documentReference;
    FirebaseAuth mAUth;

    String chatWithname,profileImageUrl,chatWIthgender,age;

    public AnotherUserProfileDialog(String name, String profileImageUrl,String gender,String age) {
        this.chatWithname = name;
        this.profileImageUrl = profileImageUrl;
        this.chatWIthgender = gender;
        this.age = age;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder ( getActivity () );

        LayoutInflater inflater = getActivity ().getLayoutInflater ();
        View view = inflater.inflate ( R.layout.activity_another_user_profile ,null);

        builder.setView ( view );

        gender = view.findViewById ( R.id.another_user_gender );
        profileImage = view.findViewById ( R.id.another_user_profilePic );
        name = view.findViewById ( R.id.another_user_name );

        mAUth = FirebaseAuth.getInstance ();
        db = FirebaseFirestore.getInstance ();

        loadProfile ();

        return  builder.create();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach ( context );

    }

    public void loadProfile() {
       gender.setText ( chatWIthgender +" (" + age +")" );
       name.setText ( chatWithname );
       if(!profileImageUrl.equals ( "" ) || !profileImageUrl.isEmpty ()) {
           Picasso.get ().load ( profileImageUrl ).fit ().into ( profileImage );
       }

    }
}
