package com.yeslabapps.friendb.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.AvatarViewBinding;
import com.yeslabapps.friendb.databinding.ChatItemLeftBinding;
import com.yeslabapps.friendb.databinding.ChatItemRightBinding;
import com.yeslabapps.friendb.model.Chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;


    private Context context;
    private List<Chat> chatList;

    private FirebaseUser firebaseUser;



    public ChatAdapter(Context context, List<Chat> chatList){
        this.chatList = chatList;
        this.context = context;

    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ChatAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {

        Chat chat = chatList.get(position);



        holder.show_message.setText(chat.getMessage());

        if(chat.getTime()!=null && !chat.getTime().trim().equals("")) {
            holder.time_tv.setText(holder.convertTime(chat.getTime()));
        }

        if (chat.isIsseen()){
            holder.txt_seen.setText("✓✓");
        } else {
            holder.txt_seen.setText("✓");
        }

        /*if (position == chatList.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText("✓✓");
            } else {
                holder.txt_seen.setText("✓");
            }
        } else {

        }*/

        if (chat.getIsDeleted().equals("yes")){
            holder.show_message.setTextColor(Color.RED);
        }


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (chat.getSender().equals(firebaseUser.getUid())){

                    MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete this message?")
                            .setCancelable(true)
                            .setPositiveButton("Delete", R.drawable.ic_baseline_delete_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {

                                    HashMap<String,Object> map = new HashMap<>();

                                    map.put("message","This message has been deleted.");
                                    map.put("isDeleted","yes");

                                    FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                                            .getReference().child("Chats").child(chat.getChatId()).updateChildren(map);




                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton("Cancel", R.drawable.close_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            }).build();

                    materialDialog.show();


                }

                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public TextView txt_seen;
        public TextView time_tv;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            time_tv = itemView.findViewById(R.id.time_tv);
        }

        private String convertTime(String time){
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM k:mm");
            String dateString = formatter.format(new Date(Long.parseLong(time)));
            return dateString;
        }
    }


    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}