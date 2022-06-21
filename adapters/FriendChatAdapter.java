package com.yeslabapps.friendb.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.activities.ChatActivity;
import com.yeslabapps.friendb.activities.ProfileActivity;
import com.yeslabapps.friendb.databinding.FriendItemBinding;
import com.yeslabapps.friendb.model.Chat;
import com.yeslabapps.friendb.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import io.github.muddz.styleabletoast.StyleableToast;


public class FriendChatAdapter extends RecyclerView.Adapter<FriendChatAdapter.MyHolder> {

    private ArrayList<User> userArrayList;
    private Context context;
    private FirebaseUser firebaseUser;

    private String theLastMessage;
    private boolean isChat;
    private String lastTime;

    private boolean isSeen;

    public FriendChatAdapter(ArrayList<User>userArrayList,Context context,boolean isChat){
        this.userArrayList= userArrayList;
        this.context=context;
        this.isChat=isChat;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        FriendItemBinding recyclerRowBinding;

        public MyHolder(@NonNull FriendItemBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;

        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FriendItemBinding recyclerRowBinding = FriendItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = userArrayList.get(position);

        holder.recyclerRowBinding.username.setText(user.getUsername());

        if (user.getAvatar().equals("maleDefault")) {
            holder.recyclerRowBinding.profileImage.setImageResource(R.drawable.cartoonify1);
        } else if (user.getAvatar().equals("femaleDefault")) {
            holder.recyclerRowBinding.profileImage.setImageResource(R.drawable.cartoonify2);
        } else {
            Picasso.get().load(user.getAvatar()).into(holder.recyclerRowBinding.profileImage);

        }

        if (isChat) {
            lastMessage(user.getUserId(), holder.recyclerRowBinding.lastMsg);
        } else {
            holder.recyclerRowBinding.lastMsg.setVisibility(View.GONE);
        }

        if (isChat){
            lastMessageDate(user.getUserId(),holder.recyclerRowBinding.lastMsgDate);
        }else{
            holder.recyclerRowBinding.lastMsgDate.setVisibility(View.GONE);
        }



        seenControl(user.getUserId(),holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getUserId()!=null && user.getAccountStatus().equals("active") ){


                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("chatUserId", user.getUserId());
                    context.startActivity(intent);
                }else{
                    StyleableToast.makeText(context, "This user's account has been disabled" , R.style.customToast).show();

                }

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                        .setTitle("Remove from list")
                        .setMessage("Are you sure you want to remove this user from the list? Messages will not be deleted.")
                        .setCancelable(true)
                        .setPositiveButton("Delete", R.drawable.ic_baseline_delete_24, new AbstractDialog.OnClickListener() {
                            @Override
                            public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/").
                                        getReference().child("Chatlist").child(firebaseUser.getUid()).child(user.getUserId())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();

                                            dialogInterface.dismiss();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", R.drawable.close_24, new AbstractDialog.OnClickListener() {
                            @Override
                            public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        }).build();

                materialDialog.show();


                return true;


            }

        });

        holder.recyclerRowBinding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getUserId()!=null && user.getAccountStatus().equals("active")){
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("someUserId", user.getUserId());
                    context.startActivity(intent);
                }else{
                    StyleableToast.makeText(context, "This user's account has been disabled" , R.style.customToast).show();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return null!=userArrayList?userArrayList.size():0;
    }



    private String convertTime(String time){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM k:mm");
        String dateString = formatter.format(new Date(Long.parseLong(time)));
        return dateString;
    }

    private void seenControl(final String userid,final FriendChatAdapter.MyHolder holder){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){

                            isSeen = chat.isIsseen();

                            if (!isSeen && chat.getReceiver().equals(firebaseUser.getUid())){
                                holder.recyclerRowBinding.isSeenText.setVisibility(View.VISIBLE);
//                                Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
//                                animation.setDuration(1000); //1 second duration for each animation cycle
//                                animation.setInterpolator(new LinearInterpolator());
//                                animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
//                                animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
//                                holder.recyclerRowBinding.isSeenText.startAnimation(animation);
                            }

                        }

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void lastMessage(final String userid, final TextView last_msg){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                            //lastTime=chat.getTime();
                            //lastTime = convertTime(chat.getTime());


                        }

                    }

                }


                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage );
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void lastMessageDate(final String userId, final TextView last_msg_date){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                                chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                            lastTime= convertTime(chat.getTime());
                            //lastTime=chat.getTime();
                            //lastTime = convertTime(chat.getTime());


                        }

                    }

                }
                last_msg_date.setText(lastTime);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





}

