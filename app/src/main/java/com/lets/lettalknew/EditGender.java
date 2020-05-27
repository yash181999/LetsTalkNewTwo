package com.lets.lettalknew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditGender extends DialogFragment {


    private GenderListener listener;

    RadioGroup radioGroup;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder ( getActivity () );

        LayoutInflater inflater = getActivity ().getLayoutInflater ();
        View view = inflater.inflate ( R.layout.edit_gender ,null);

        radioGroup = view.findViewById ( R.id.gender_choice );

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
                        int id = radioGroup.getCheckedRadioButtonId ();
                       RadioButton radioButton = radioGroup.findViewById ( id );

                       String genderChoiceText = radioButton.getText ().toString ();

                        listener.applyGender  (genderChoiceText);
                    }
                } );
        return  builder.create();
    }

    public interface  GenderListener {
        void applyGender(String gender);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach ( context );
        try {
            listener = (GenderListener) context;
        }
        catch (ClassCastException  e) {
            throw  new ClassCastException ( context.toString () + "must implement listener" );
        }
    }
}
