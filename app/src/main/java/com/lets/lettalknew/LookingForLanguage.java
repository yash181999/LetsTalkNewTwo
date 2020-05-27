package com.lets.lettalknew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LookingForLanguage extends DialogFragment {

    private EditText lookingForLanguageTexView;

   LookingForLanguageListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder ( getActivity () );

        LayoutInflater inflater = getActivity ().getLayoutInflater ();
        View view = inflater.inflate ( R.layout.looking_for_language,null);

        lookingForLanguageTexView = view.findViewById ( R.id.looking_for_lang );

        builder.setView ( view )
                .setTitle ( "Language" )
                .setNegativeButton ( "cancel", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                } )
                .setPositiveButton ( "ok", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String languageText = lookingForLanguageTexView.getText ().toString ();
                        listener.applyLang  (languageText);
                    }
                } );
        return  builder.create();
    }

    public interface  LookingForLanguageListener {
        void applyLang(String lang);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach ( context );
        try {
            listener = (LookingForLanguageListener) context;
        }
        catch (ClassCastException  e) {
            throw  new ClassCastException ( context.toString () + "must implement listener" );
        }
    }

}
