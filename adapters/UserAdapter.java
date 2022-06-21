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

import com.squareup.picasso.Picasso;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.activities.ProfileActivity;
import com.yeslabapps.friendb.databinding.AvatarViewBinding;
import com.yeslabapps.friendb.databinding.UserItemBinding;
import com.yeslabapps.friendb.model.User;

import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyHolder> {

    private ArrayList<User> userArrayList;
    private Context context;
    private FirebaseUser firebaseUser;

    public UserAdapter(ArrayList<User>userArrayList,Context context){
        this.userArrayList= userArrayList;
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

        final User user = userArrayList.get(position);

        String username = user.getUsername();

        holder.recyclerRowBinding.usernameItem.setText(username+" "+ "("+(user.getAge())+")");


        holder.recyclerRowBinding.userCountryItem.setText(user.getCountry());


        if (user.getAvatar().equals("maleDefault")){
            holder.recyclerRowBinding.userProfileItem.setImageResource(R.drawable.cartoonify1);
        }else if (user.getAvatar().equals("femaleDefault")){
            holder.recyclerRowBinding.userProfileItem.setImageResource(R.drawable.cartoonify2);
        }else{
            Picasso.get().load(user.getAvatar()).into(holder.recyclerRowBinding.userProfileItem);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getUserId()!=null && user.getAccountStatus().equals("active")){
                    Intent intent = new Intent(context, ProfileActivity.class);
                    //intent.putExtra("name", user.getUsername());
                    intent.putExtra("someUserId", user.getUserId());
                    context.startActivity(intent);
                }else{
                    StyleableToast.makeText(context, "This user's account has been disabled." , R.style.customToast).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return null!=userArrayList?userArrayList.size():0;
    }





}