package com.yeslabapps.friendb.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.activities.RegisterActivity;
import com.yeslabapps.friendb.adapters.ChatlistAdapter;
import com.yeslabapps.friendb.databinding.FragmentChatsBinding;
import com.yeslabapps.friendb.model.Chat;
import com.yeslabapps.friendb.model.Chatlist;
import com.yeslabapps.friendb.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private ChatlistAdapter userAdapter;
    private ArrayList<Chatlist> userArrayList;
    private FirebaseUser fuser;
    private DatabaseReference reference;

    private List<Chatlist> usersList;
    private ProgressDialog pd;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        pd = new ProgressDialog(getContext(), R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();


        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(binding.recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);

        binding.recyclerView.addItemDecoration(itemDecoration);


        usersList = new ArrayList<>();
        userArrayList = new ArrayList<>();


        /*reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (dataSnapshot.exists()){
                        Chatlist chatlist = snapshot.getValue(Chatlist.class);
                        usersList.add(chatlist);

                    }

                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/




        /*FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Chatlist").child(fuser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userArrayList.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            if (snapshot.exists()){
                                Chatlist chatlist = dataSnapshot.getValue(Chatlist.class);
                                userArrayList.add(chatlist);

                            }
                        }
                        readChats();
                        binding.chatListCounter.setText(userArrayList.size()+" friends");


                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

        readChats();



        userInfo();




        return binding.getRoot();

    }

    private void userInfo(){
        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);

                    if (user.getAccountStatus().equals("banned")){
                        Intent intent = new Intent(getContext(), RegisterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Toast.makeText(getContext(), "Your account disabled.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readChats(){
        userArrayList= new ArrayList<>();
        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference().child("Chatlist").child(fuser.getUid()).orderByChild("timeList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot np : snapshot.getChildren()) {
                    if (snapshot.exists()) {

                        Chatlist chatlist = np.getValue(Chatlist.class);
                        if (chatlist != null && chatlist.getId() != null) {
                            userArrayList.add(chatlist);
                            pd.dismiss();
                        }
                    }
                }
                Collections.reverse(userArrayList);
                userAdapter = new ChatlistAdapter(userArrayList,getContext(), true);
                binding.recyclerView.setAdapter(userAdapter);
                binding.chatListCounter.setText(userArrayList.size()+" friends");

                pd.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

   /* private void chatList() {
        userArrayList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Chatlist");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (dataSnapshot.exists()){
                        Chatlist user = snapshot.getValue(Chatlist.class);
                        for (Chatlist chatlist : usersList){
                            if (user!= null && user.getId()!=null && chatlist!=null && chatlist.getId()!= null &&
                                    user.getUserId().equals(chatlist.getId())){
                                userArrayList.add(user);
                                pd.dismiss();

                            }

                        }

                    }

                }
                //Collections.reverse(userArrayList);
                userAdapter = new ChatlistAdapter(userArrayList,getContext(), true);
                binding.recyclerView.setAdapter(userAdapter);
                pd.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }*/






    /*private void chatListA_Z() {
        userArrayList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (Chatlist chatlist : usersList){
                        if (user!= null && user.getUserId()!=null && chatlist!=null && chatlist.getId()!= null &&
                                user.getUserId().equals(chatlist.getId())){
                            userArrayList.add(user);

                        }

                    }

                }

                userAdapter = new FriendChatAdapter(userArrayList,getContext(), true);
                binding.recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/



}
