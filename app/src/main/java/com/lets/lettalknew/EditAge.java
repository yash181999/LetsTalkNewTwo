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

public class EditAge extends DialogFragment {



    AgeListener listener;
    NumberPicker agePicker;
    int age;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder ( getActivity () );

        LayoutInflater inflater = getActivity ().getLayoutInflater ();
        View view = inflater.inflate ( R.layout.edit_age_layout ,null);

       agePicker = view.findViewById ( R.id.age_picker );
       agePicker.setMinValue ( 18 );
       agePicker.setMaxValue ( 45 );

       agePicker.setOnValueChangedListener ( new NumberPicker.OnValueChangeListener () {
           @Override
           public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
               age = newVal ;
           }
       } );

        builder.setView ( view )
                .setTitle ( "NickName" )
                .setNegativeButton ( "cancel", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                } )
                .setPositiveButton ( "ok", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        listener.applyAge  (age+"");
                    }
                } );
        return  builder.create();
    }

    public interface  AgeListener {
        void applyAge(String age);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach ( context );
        try {
            listener = (AgeListener) context;
        }
        catch (ClassCastException  e) {
            throw  new ClassCastException ( context.toString () + "must implement listener" );
        }
    }
}
