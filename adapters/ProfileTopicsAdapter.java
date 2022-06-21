package com.yeslabapps.friendb.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.TopicTagViewBinding;
import com.yeslabapps.friendb.model.Topics;

import java.util.ArrayList;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class ProfileTopicsAdapter extends RecyclerView.Adapter<ProfileTopicsAdapter.MyHolder> {

    private ArrayList<Topics> topicsArrayList;
    private Context context;
    private FirebaseUser firebaseUser;

    public ProfileTopicsAdapter(ArrayList<Topics>topicsArrayList,Context context){
        this.topicsArrayList= topicsArrayList;
        this.context=context;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TopicTagViewBinding recyclerRowBinding;

        public MyHolder(@NonNull TopicTagViewBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;

        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TopicTagViewBinding recyclerRowBinding = TopicTagViewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final Topics topics = topicsArrayList.get(position);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        holder.recyclerRowBinding.topicText.setText(topics.getTopic());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (topics.getOwnerId().equals(firebaseUser.getUid())){


                    MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                            .setTitle("Remove")
                            .setMessage("Remove "+ holder.recyclerRowBinding.topicText.getText().toString() +" from my topics" )
                            .setCancelable(true)
                            .setPositiveButton("Remove", R.drawable.ic_baseline_delete_24, new AbstractDialog.OnClickListener() {
                                @Override
                                public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                                            .getReference().child("Topics").child(firebaseUser.getUid()).child(topics.getTopicId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                //Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
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


                }

                return true;
            }

        });


    }

    @Override
    public int getItemCount() {
        return null!=topicsArrayList?topicsArrayList.size():0;
    }


}