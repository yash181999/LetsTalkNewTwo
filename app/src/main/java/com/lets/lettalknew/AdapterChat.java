package com.lets.lettalknew;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private  static final int  MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModalChat> chatList;
    ArrayList<String> lastSeenList;


    FirebaseUser user;

    public AdapterChat(Context context, List<ModalChat> chatList,ArrayList<String> lastSeenList) {
        this.context = context;
        this.chatList = chatList;
        this.lastSeenList = lastSeenList;
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
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
      String message  = chatList.get ( position ).getMessage ();
      final String image = chatList.get ( position ).getImage ();
      String time= chatList.get ( position ).getTime ();
      final String audio = chatList.get ( position ).getAudio ();




      if(message!=null) {
          holder.messageText.setText ( message );
          holder.textMsgTime.setText ( time );
          holder.textMsgLayout.setVisibility ( View.VISIBLE );
          holder.imageMsgLayout.setVisibility ( View.GONE );
          holder.audioMsgLayout.setVisibility ( View.GONE );


          String isSeen = lastSeenList.get ( position);
          if(isSeen.equals ( "true" )) {
              holder.textMsgTicks.setImageResource ( R.drawable.ic_done_all_blue );
          }
          else {
              holder.textMsgTicks.setImageResource ( R.drawable.done_all_black );
          }


      }


      if(audio!=null) {
          holder.audioMsgTime.setText ( time );
          holder.textMsgLayout.setVisibility ( View.GONE );
          holder.imageMsgLayout.setVisibility ( View.GONE );
          holder.audioMsgLayout.setVisibility ( View.VISIBLE );

          holder.audioPlayBtn.setOnClickListener ( new View.OnClickListener () {
              @Override
              public void onClick(View v) {
                  try{
                      holder.playMusic ( audio );
                      holder.audioPauseBtn.setVisibility ( View.VISIBLE );
                      holder.audioPlayBtn.setVisibility ( View.GONE );
                  }
                  catch (Exception e) {
                      Toast.makeText ( context, "please wait", Toast.LENGTH_SHORT ).show ();
                  }

              }
          } );

          String isSeen = lastSeenList.get ( position);

          if(isSeen.equals ( "true" )) {
              holder.audioMsgTicks.setImageResource ( R.drawable.ic_done_all_blue );
          }
          else {
              holder.audioMsgTicks.setImageResource ( R.drawable.done_all_black );
          }
      }


      if(image!=null ) {

          Picasso.get ().load ( image )
                  .fit ().into (holder.messageImage);
          holder.imageMsgTime.setText ( time );
          holder.imageMsgLayout.setVisibility ( View.VISIBLE );
          holder.textMsgLayout.setVisibility ( View.GONE );
          holder.audioMsgLayout.setVisibility ( View.GONE );

          String isSeen = lastSeenList.get ( position);

              if(isSeen.equals ( "true" )) {
                  holder.imageMsgTicks.setImageResource ( R.drawable.ic_done_all_blue );
              }
              else{
                  holder.imageMsgTicks.setImageResource ( R.drawable.done_all_black );
              }
      }

      holder.imageMsgLayout.setOnClickListener ( new View.OnClickListener () {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent (context,ShowImage.class );
              intent.putExtra ( "imageUrl",image );
              context.startActivity ( intent );
          }
      } );

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
        LinearLayout imageMsgLayout,audioMsgLayout;
        ImageView messageImage;
        TextView messageText;
        TextView imageMsgTime;
        SeekBar seekBar;
        Button audioPlayBtn, audioPauseBtn;
        TextView textMsgTime,audioMsgTime;
        ImageView textMsgTicks,imageMsgTicks,audioMsgTicks;

        Handler handler;
        MediaPlayer mediaPlayer;




        public MyHolder(@NonNull View itemView) {
            super ( itemView );
            textMsgLayout = (LinearLayout) itemView.findViewById ( R.id.message_text_layout );
            audioMsgLayout = (LinearLayout) itemView.findViewById ( R.id.message_audio_layout );
            imageMsgLayout = (LinearLayout) itemView.findViewById ( R.id.message_image_layout );
            messageImage = (ImageView) itemView.findViewById ( R.id.msg_image );
            messageText = (TextView) itemView.findViewById ( R.id.msg_text );
            imageMsgTime = (TextView) itemView.findViewById ( R.id.image_time );
            textMsgTime = (TextView) itemView.findViewById ( R.id.text_time );
            textMsgTicks = (ImageView) itemView.findViewById ( R.id.msg_ticks );
            imageMsgTicks = (ImageView) itemView.findViewById ( R.id.img_ticks );
            audioMsgTicks = (ImageView) itemView.findViewById ( R.id.audio_ticks );
            audioMsgTime = (TextView) itemView.findViewById ( R.id.audio_time );
            audioPlayBtn = (Button) itemView.findViewById ( R.id.chat_audio_play );
            audioPauseBtn = (Button) itemView.findViewById ( R.id.chat_audio_pause );
            seekBar = (SeekBar) itemView.findViewById ( R.id.seek_bar );

            handler = new Handler ();

            mediaPlayer = new MediaPlayer ();

            audioPauseBtn.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    mediaPlayer.pause ();
                    audioPlayBtn.setVisibility ( View.VISIBLE );
                    audioPauseBtn.setVisibility ( View.GONE );
                }
            } );
        }

        public void playMusic(String musicUrl) {
                    mediaPlayer.setAudioStreamType ( AudioManager.STREAM_MUSIC );


                    try {
                        mediaPlayer.setDataSource ( musicUrl );
                    } catch (IOException ex) {
                        ex.printStackTrace ();
                    }
                    mediaPlayer.prepareAsync ();
                    mediaPlayer.setOnPreparedListener ( new MediaPlayer.OnPreparedListener () {


                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            seekBar.setMax ( 100);
                            mediaPlayer.start ();
                            changeSeekbar ( );
                        }
                    } );
                    mediaPlayer.setOnCompletionListener ( new MediaPlayer.OnCompletionListener () {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.release ();
                        }
                    } );

                    mediaPlayer.setOnCompletionListener ( new MediaPlayer.OnCompletionListener () {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            seekBar.setProgress ( 0 );
                            mediaPlayer.stop ();
                            mediaPlayer.reset ();
                            audioPlayBtn.setVisibility ( View.VISIBLE );
                            audioPauseBtn.setVisibility ( View.GONE );
                        }
                    } );

                    mediaPlayer.setOnErrorListener ( new MediaPlayer.OnErrorListener () {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            mediaPlayer.reset ();
                            return false;
                        }
                    } );

                }


        public void changeSeekbar() {

            if(mediaPlayer.isPlaying () && mediaPlayer!=null) {
                seekBar.setProgress ((int ) (((float) mediaPlayer.getCurrentPosition ()/ mediaPlayer.getDuration ())*100) );

                handler.postDelayed ( updater,1000 );
            }
        }

        private  Runnable updater = new Runnable () {
            @Override
            public void run() {

                try {
                    changeSeekbar ();
                }
                catch (Exception e) {

                }

            }
        };

    }

}
