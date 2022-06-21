package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.friendb.adapters.BlockedUsersAdapter;
import com.yeslabapps.friendb.databinding.ActivityBlockedUsersBinding;
import com.yeslabapps.friendb.model.Block;

import java.util.ArrayList;

public class BlockedUsersActivity extends AppCompatActivity {

    private ActivityBlockedUsersBinding binding;
    private BlockedUsersAdapter userAdapter;
    private ArrayList<Block> userArrayList;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlockedUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Blocked Users");

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }

        });


        binding.userRecycler.setHasFixedSize(true);
        userArrayList = new ArrayList<>();
        userAdapter = new BlockedUsersAdapter(userArrayList,BlockedUsersActivity.this);
        GridLayoutManager layoutManager=new GridLayoutManager(BlockedUsersActivity.this,2);
        binding.userRecycler.setAdapter(userAdapter);

        binding.userRecycler.setLayoutManager(layoutManager);


        readDefault();

    }

    private void readDefault() {

        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("BlockedUsers").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    if (snapshot.exists()){
                        Block block = np.getValue(Block.class);

                        if (block.getMyId().equals(firebaseUser.getUid())) {
                            userArrayList.add(block);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}