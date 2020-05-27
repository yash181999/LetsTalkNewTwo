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



public class EditNickName extends DialogFragment  {
    private EditText editNickNameText;

     private NickNameListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder ( getActivity () );

        LayoutInflater inflater = getActivity ().getLayoutInflater ();
        View view = inflater.inflate ( R.layout.edit_nick_name,null);

        editNickNameText = view.findViewById ( R.id.nickNameText );

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
                     String nickNameText = editNickNameText.getText ().toString ();
                     listener.applyNickName  (nickNameText);
                  }
              } );
      return  builder.create();
    }

    public interface  NickNameListener {
        void applyNickName(String nickName);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach ( context );
        try {
            listener = (NickNameListener) context;
        }
        catch (ClassCastException  e) {
            throw  new ClassCastException ( context.toString () + "must implement listener" );
        }
    }
}
