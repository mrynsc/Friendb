package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.adapters.UserAdapter;
import com.yeslabapps.friendb.databinding.ActivitySearchBinding;
import com.yeslabapps.friendb.model.User;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private ArrayList<User> userArrayList;
    private UserAdapter userAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }

        });


        binding.recyclerviewUsers.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(SearchActivity.this,2);
        binding.recyclerviewUsers.setLayoutManager(layoutManager);
        userArrayList = new ArrayList<>();



        binding.searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                readUsers();
                searchUser(charSequence.toString());
                deleteUsers();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void deleteUsers(){

        DatabaseReference reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(binding.searchbar.getText().toString()))
                    if (snapshot.exists()) {
                        for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                            User user = npsnapshot.getValue(User.class);
                            userArrayList.clear();

                        }

                        userAdapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        userAdapter = new UserAdapter(userArrayList,SearchActivity.this);
        binding.recyclerviewUsers.setAdapter(userAdapter);

    }



    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(binding.searchbar.getText().toString()))
                    if (snapshot.exists()) {
                        for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                            User user = npsnapshot.getValue(User.class);
                            userArrayList.add(user);
                        }

                        userAdapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        userAdapter = new UserAdapter(userArrayList,SearchActivity.this);
        binding.recyclerviewUsers.setAdapter(userAdapter);
    }


    private void  searchUser(String s) {
        Query query = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users")
                .orderByChild("usernameLower").startAt(s).endAt(s);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding.searchbar.getText().toString().length() > 1){
                    userArrayList.clear();
                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                        User user = npsnapshot.getValue(User.class);
                        userArrayList.add(user);
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