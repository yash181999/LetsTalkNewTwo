package com.lets.lettalknew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LookingForAge extends DialogFragment {

    LookingForAgeListener listener;

    NumberPicker fromAge, toAge;

    int fromAgeSelected, toAgeSelected;




    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder ( getActivity () );

        LayoutInflater inflater = getActivity ().getLayoutInflater ();
        View view = inflater.inflate ( R.layout.looking_for_age ,null);

        fromAge = view.findViewById ( R.id.from_age );
        toAge = view.findViewById ( R.id.to_age );

        fromAge.setMinValue ( 18 );
        fromAge.setMaxValue ( 45 );

        toAge.setMinValue ( 18 );
        toAge.setMaxValue ( 45 );


        fromAge.setOnValueChangedListener ( new NumberPicker.OnValueChangeListener () {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                fromAgeSelected = newVal;
            }
        } );

        toAge.setOnValueChangedListener ( new NumberPicker.OnValueChangeListener () {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                toAgeSelected = newVal;
            }
        } );

        builder.setView ( view )
                .setTitle ( "Age Range" )
                .setNegativeButton ( "cancel", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                } )
                .setPositiveButton ( "ok", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      String  ageRange  = fromAgeSelected+"-"+toAgeSelected;
                        listener.applyLookingForAge  (ageRange);
                    }
                } );
        return  builder.create();
    }

    public interface  LookingForAgeListener {
        void applyLookingForAge(String age);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach ( context );
        try {
            listener = (LookingForAgeListener) context;
        }
        catch (ClassCastException  e) {
            throw  new ClassCastException ( context.toString () + "must implement listener" );
        }
    }

}
