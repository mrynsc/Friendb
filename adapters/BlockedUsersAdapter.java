package com.yeslabapps.friendb.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.activities.ProfileActivity;
import com.yeslabapps.friendb.databinding.UserItemBinding;
import com.yeslabapps.friendb.model.Block;
import com.yeslabapps.friendb.model.User;

import java.util.ArrayList;

public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.MyHolder> {

    private ArrayList<Block> blockArrayList;
    private Context context;
    private FirebaseUser firebaseUser;

    public BlockedUsersAdapter(ArrayList<Block>blockArrayList,Context context){
        this.blockArrayList= blockArrayList;
        this.context=context;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        UserItemBinding recyclerRowBinding;

        public MyHolder(@NonNull UserItemBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;

        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserItemBinding recyclerRowBinding = UserItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Block block = blockArrayList.get(position);


        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").child(block.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user.getAvatar().equals("maleDefault")){
                    holder.recyclerRowBinding.userProfileItem.setImageResource(R.drawable.cartoonify1);
                }else if (user.getAvatar().equals("femaleDefault")){
                    holder.recyclerRowBinding.userProfileItem.setImageResource(R.drawable.cartoonify2);
                }else{
                    Picasso.get().load(user.getAvatar()).into( holder.recyclerRowBinding.userProfileItem);
                }

                String username = user.getUsername();

                holder.recyclerRowBinding.usernameItem.setText(username+" "+ "("+(user.getAge())+")");

                holder.recyclerRowBinding.userCountryItem.setText(user.getCountry());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (block.getUid()!=null){
                    Intent intent = new Intent(context, ProfileActivity.class);
                    //intent.putExtra("name", user.getUsername());
                    intent.putExtra("someUserId",block.getUid());
                    context.startActivity(intent);
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return null!=blockArrayList?blockArrayList.size():0;
    }





}