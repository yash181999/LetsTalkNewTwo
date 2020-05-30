package com.lets.lettalknew;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {

    Context context;

    List<ModalUser> userList;

    private Map<String, String> lastMessageMap;

    Dialog myDialog;

    public AdapterChatList(Context context, List<ModalUser> userList) {
        this.context = context;
        this.userList = userList;
       lastMessageMap = new HashMap<> (  );
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.chat_list_layout , parent, false);
        return new MyHolder (    view );
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        //get Data;


        final String id = userList.get (position).getuId ();
        String userImage = userList.get ( position ).getProfilePic1 ();
        String userName = userList.get ( position).getNickName ();
        String lastMessage = lastMessageMap.get ( id );

        holder.chatProfileName.setText ( userName );

        if(lastMessage==null || lastMessage.equals ( "default" )) {

             holder.chatProfileLastMsg.setVisibility ( View.GONE );
        }
        else  {
            holder.chatProfileLastMsg.setText ( lastMessage );
            holder.chatProfileLastMsg.setVisibility ( View.VISIBLE );
        }

        try {
            Picasso.get ().load ( userImage ).placeholder ( R.drawable.dummyprofile ).fit ().into ( holder.chatProfileImage );
        }
        catch (Exception e) {
            Picasso.get ().load ( R.drawable.dummyprofile ).into ( holder.chatProfileImage );
        }


        if(userList.get ( position ).getUserStatus ().equals ( "online" ) ) {
            holder.chatProfileStatus.setText ( "online" );
        }
        else {
            holder.chatProfileStatus.setText ( "offline" );
        }

        //handle click
        holder.itemView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( context,ChatActivity.class );
                intent.putExtra ( "id",id );
                context.startActivity ( intent );
            }
        } );

        myDialog = new Dialog ( context );
        myDialog.setContentView ( R.layout.activity_another_user_profile );
        ImageView profileImage = myDialog.findViewById ( R.id.another_user_profilePic );
        TextView name = myDialog.findViewById ( R.id.another_user_name );
        TextView gender = myDialog.findViewById ( R.id.another_user_gender );

        try{
            Picasso.get ().load ( userImage ).placeholder ( R.drawable.dummyprofile ).fit ().into ( profileImage );
        }
        catch (Exception e) {
            Picasso.get ().load ( R.drawable.dummyprofile ).placeholder ( R.drawable.dummyprofile ).fit ().into ( profileImage );

        }

        name.setText ( userName + "(" + userList.get ( position ).getAge ()+ ")");

        gender.setText ( userList.get ( position ).getGender () );


        holder.chatProfileImage.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                myDialog.show ();
            }
        } );

    }

    public void setLastMessageMap(String userId, String lastMessage) {
        lastMessageMap.put ( userId,lastMessage );
    }

    @Override
    public int getItemCount() {
        return userList.size ();
    }



    class MyHolder extends RecyclerView.ViewHolder {
        ImageView chatProfileImage;
        TextView chatProfileName,chatProfileLastMsg,chatProfileStatus;
        public MyHolder(@NonNull View itemView) {
            super ( itemView );

            chatProfileImage = itemView.findViewById ( R.id.chat_profile_image );
            chatProfileName = itemView.findViewById ( R.id.chat_profile_name );
            chatProfileLastMsg = itemView.findViewById ( R.id.chat_profile_last_message );
            chatProfileStatus = itemView.findViewById ( R.id.chat_profile_status );

        }
    }
}
