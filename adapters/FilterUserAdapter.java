package com.yeslabapps.friendb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
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
import com.yeslabapps.friendb.databinding.UserItemBinding;
import com.yeslabapps.friendb.model.Topics;
import com.yeslabapps.friendb.model.User;

import java.util.ArrayList;

public class FilterUserAdapter extends RecyclerView.Adapter<FilterUserAdapter.MyHolder> {

    private ArrayList<Topics> topicsArrayList;
    private Context context;
    private FirebaseUser firebaseUser;

    public FilterUserAdapter(ArrayList<Topics>topicsArrayList,Context context){
        this.topicsArrayList= topicsArrayList;
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

        final Topics topics = topicsArrayList.get(position);




        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users")
                .child(topics.getOwnerId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User user = snapshot.getValue(User.class);

                    if (user.getAvatar().equals("maleDefault")){
                        holder.recyclerRowBinding.userProfileItem.setImageResource(R.drawable.cartoonify1);
                    }else if (user.getAvatar().equals("femaleDefault")){
                        holder.recyclerRowBinding.userProfileItem.setImageResource(R.drawable.cartoonify2);
                    }else{
                        Picasso.get().load(user.getAvatar()).into(holder.recyclerRowBinding.userProfileItem);

                    }

                    holder.recyclerRowBinding.usernameItem.setText(user.getUsername());
                    holder.recyclerRowBinding.userCountryItem.setText(user.getCountry());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return null!=topicsArrayList?topicsArrayList.size():0;
    }





}