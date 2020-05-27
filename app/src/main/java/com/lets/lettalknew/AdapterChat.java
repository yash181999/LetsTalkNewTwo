package com.lets.lettalknew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private  static final int  MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModalChat> chatList;


    FirebaseUser user;

    public AdapterChat(Context context, List<ModalChat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from ( context )
                    .inflate (R.layout.chat_item_recieved,parent,false );

            return new MyHolder ( view );

        }
        else {
            View view = LayoutInflater.from ( context )
                    .inflate (R.layout.chat_item_sent,parent,false );

            return new MyHolder ( view );
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
      String message  = chatList.get ( position ).getMessage ();
      String image = chatList.get ( position ).getImage ();
      String time= chatList.get ( position ).getTime ();
      if(message!=null) {
          holder.messageText.setText ( message );
          holder.textMsgTime.setText ( time );
          holder.textMsgLayout.setVisibility ( View.VISIBLE );

          if(position == chatList.size ()-1) {

              if(chatList.get ( position ).isSeen == true) {
                  holder.textMsgTicks.setImageResource (R.drawable.ic_done_all_blue);
              }else {
                  holder.textMsgTicks.setImageResource ( R.drawable.done_all_black );
              }
          }
          else {
              holder.textMsgTicks.setVisibility ( View.GONE );
          }

      }



      if(image!=null ) {

          Picasso.get ().load ( image )
                  .fit ().into (holder.messageImage);
          holder.imageMsgTime.setText ( time );
          holder.imageMsgLayout.setVisibility ( View.VISIBLE );

          if(position == chatList.size ()-1) {
              if(chatList.get ( position ).isSeen == true) {
                  holder.imageMsgTicks.setImageResource ( R.drawable.ic_done_all_blue );
              }
              else {
                  holder.imageMsgTicks.setImageResource ( R.drawable.done_all_black );
              }
          }
          else {
              holder.textMsgTicks.setVisibility ( View.GONE );
          }
      }
    }

    @Override
    public int getItemCount() {
        return chatList.size ();
    }

    @Override
    public int getItemViewType(int position) {
        //get current signed user;
        user = FirebaseAuth.getInstance ().getCurrentUser ();
        if(chatList.get ( position ).getSender ().equals ( user.getUid () )) {
            return MSG_TYPE_RIGHT;

        }
        else{
            return MSG_TYPE_LEFT;
        }

    }


    public class MyHolder extends RecyclerView.ViewHolder {

        LinearLayout textMsgLayout;
        LinearLayout imageMsgLayout;
        ImageView messageImage;
        TextView messageText;
        TextView imageMsgTime;
        TextView textMsgTime;
        ImageView textMsgTicks,imageMsgTicks;


        public MyHolder(@NonNull View itemView) {
            super ( itemView );
            textMsgLayout = (LinearLayout) itemView.findViewById ( R.id.message_text_layout );
            imageMsgLayout = (LinearLayout) itemView.findViewById ( R.id.message_image_layout );
            messageImage = (ImageView) itemView.findViewById ( R.id.msg_image );
            messageText = (TextView) itemView.findViewById ( R.id.msg_text );
            imageMsgTime = (TextView) itemView.findViewById ( R.id.image_time );
            textMsgTime = (TextView) itemView.findViewById ( R.id.text_time );
            textMsgTicks = (ImageView) itemView.findViewById ( R.id.msg_ticks );
            imageMsgTicks = (ImageView) itemView.findViewById ( R.id.img_ticks );

        }
    }


}
