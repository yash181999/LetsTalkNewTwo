package com.lets.lettalknew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;




public class LastChatList extends ArrayAdapter<String> {

    ArrayList<String> sentMessage,revievedMessage;

    private Context   mCtx;
    private int      listLayoutRes;

    int messageType;


    public static class ViewHolder {
        private TextView unitName;

        private TextView sentMessageText,recievedMessageText;

        ProgressBar progressBar;

    }


    public LastChatList(@NonNull Context context, int resource, ArrayList<String> sentMessage, int messageType) {
        super ( context, resource ,sentMessage);
        mCtx = context;
        listLayoutRes = resource;
        this.sentMessage = sentMessage;
        this.messageType = messageType;
    }

   public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if(convertView==null) {
            holder = new ViewHolder ();
            final LayoutInflater inflater = LayoutInflater.from(mCtx);
            convertView = inflater.inflate ( listLayoutRes,parent,false );

            holder.sentMessageText = convertView.findViewById ( R.id.sent_message );
            holder.recievedMessageText = convertView.findViewById ( R.id.received_message );

            if(messageType == 0) {
                holder.sentMessageText.setText ( sentMessage.get ( position ) );
            }
            if(messageType == 1){
                holder.recievedMessageText.setText ( sentMessage.get ( position ) );
            }

        }

       return convertView;
   }



}
