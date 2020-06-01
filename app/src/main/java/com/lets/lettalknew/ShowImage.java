package com.lets.lettalknew;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

public class ShowImage extends AppCompatActivity {

    MaterialToolbar toolbar;
    ImageView photo ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_show_image );
        toolbar = findViewById ( R.id.tool_bar );

        setSupportActionBar ( toolbar );
        getSupportActionBar().setDisplayShowTitleEnabled (false);

        photo = findViewById ( R.id.photo );

        String imageUrl = getIntent ().getStringExtra ( "imageUrl" );

        try {
            Picasso.get ().load ( imageUrl ).placeholder ( R.drawable.dummyprofile ).fit ().into ( photo );
        }
        catch (Exception e) {

        }

    }

    public void imageBack(View view) {
        finish ();
    }
}
