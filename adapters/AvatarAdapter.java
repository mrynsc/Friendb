package com.yeslabapps.friendb.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.unity3d.ads.UnityAds;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.activities.SettingsActivity;
import com.yeslabapps.friendb.databinding.AvatarViewBinding;
import com.yeslabapps.friendb.databinding.TopicTagViewBinding;
import com.yeslabapps.friendb.model.Avatar;

import java.util.ArrayList;
import java.util.HashMap;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import io.github.muddz.styleabletoast.StyleableToast;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.MyHolder> {

    private ArrayList<Avatar> avatarArrayList;
    private Context context;
    private FirebaseUser firebaseUser;

    private String GAME_ID = "4789422";
    private String CHANGE_AVATAR_INTERSTITIAL_ID ="ChangeAvatar";
    private boolean test = false;



    public AvatarAdapter(ArrayList<Avatar>avatarArrayList,Context context){
        this.avatarArrayList= avatarArrayList;
        this.context=context;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        AvatarViewBinding recyclerRowBinding;

        public MyHolder(@NonNull AvatarViewBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;

        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AvatarViewBinding recyclerRowBinding = AvatarViewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final Avatar avatar = avatarArrayList.get(position);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        UnityAds.initialize(context,GAME_ID,test);

        loadInterstitialId();


        String image = avatar.getAvatarUrl();
        Picasso.get().load(image).into(holder.recyclerRowBinding.selectAvatar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                        .setTitle("Change Avatar")
                        .setMessage("This will be your new avatar.")
                        .setCancelable(true)
                        .setPositiveButton("Yes", R.drawable.ic_baseline_check_24, new AbstractDialog.OnClickListener() {
                            @Override
                            public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("avatar", avatar.getAvatarUrl());

                                FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                                        .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(hashMap);
                                StyleableToast.makeText(context, "Updated!", R.style.customToast).show();
                                dialogInterface.dismiss();
                                if (UnityAds.isInitialized()){
                                    UnityAds.show((Activity) context,CHANGE_AVATAR_INTERSTITIAL_ID);
                                }

                            }
                        }).setNegativeButton("Cancel", R.drawable.close_24, new AbstractDialog.OnClickListener() {
                            @Override
                            public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        }).build();

                materialDialog.show();




            }
        });


    }

    private void loadInterstitialId(){
        if (UnityAds.isInitialized()){
            UnityAds.load(CHANGE_AVATAR_INTERSTITIAL_ID);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UnityAds.load(CHANGE_AVATAR_INTERSTITIAL_ID);
                }
            },5000);
        }
    }

    @Override
    public int getItemCount() {
        return null!=avatarArrayList?avatarArrayList.size():0;
    }



}