package com.yeslabapps.friendb.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.activities.EditProfileActivity;
import com.yeslabapps.friendb.activities.ExploreActivity;
import com.yeslabapps.friendb.adapters.UserAdapter;
import com.yeslabapps.friendb.databinding.FragmentHomeBinding;
import com.yeslabapps.friendb.model.User;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private UserAdapter userAdapter;
    private ArrayList<User> userArrayList;

    private FirebaseUser firebaseUser;
    private String profileId;

    private String myCountry="";
    private String myMbti ="";
    private int myAge;
    private String myGender="";


    private ProgressDialog pd;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }

        pd = new ProgressDialog(getContext(),R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();


        binding.exploreMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ExploreActivity.class);
                startActivity(intent);
            }
        });


        binding.userRecycler.setHasFixedSize(true);

        userArrayList = new ArrayList<>();
        userAdapter = new UserAdapter(userArrayList,getContext());
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),2);
        binding.userRecycler.setAdapter(userAdapter);


        binding.userRecycler.setLayoutManager(layoutManager);

        binding.seeRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottom_rules);


                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });

        /*binding.sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(getContext(),R.style.CustomDialogFilter);
                builder.setTitle("Sort");
                builder.setIcon(R.drawable.ic_baseline_filter_list_24);
                String[] listPrefs={"Male","Female","From My Country - Male","From My Country - Female"};
                builder.setItems(listPrefs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:readMale();
                                break;
                            case 1:readFemale();
                                break;
                            case 2:fromMyCountryMale();
                                break;
                            case 3:fromMyCountryFemale();
                                break;


                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog customAlertDialog = builder.create();
                customAlertDialog.show();
            }
        });*/


        userInfo();
        readDefault();


        return binding.getRoot();


    }



    private void userInfo(){
        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);

                    myCountry = user.getCountry();
                    myMbti =user.getMbti();
                    myAge = user.getAge();
                    myGender = user.getGender();

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readDefault() {

        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").orderByChild("registerDate").startAt("21 06 2022 13 00")
                .endAt("30 07 2022 13 00").limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    if (snapshot.exists()){
                        User user = np.getValue(User.class);
                        if (!user.getUserId().equals(firebaseUser.getUid())
                            && user.getUserId()!=null
                            && user.getAccountStatus().equals("active")
                            && user.getDmPrefer().equals("open")
                            && user.getMbti().equals(myMbti)
                            && !user.getGender().equals(myGender)){
                            userArrayList.add(user);
                            pd.dismiss();
                        }

                        /*if (!user.getUserId().equals(firebaseUser.getUid())
                                &&user.getAccountStatus().equals("active")
                                &&myMbti.equals(user.getMbti())
                                ||!user.getUserId().equals(firebaseUser.getUid())
                                &&user.getAccountStatus().equals("active")&&myCountry.equals(user.getCountry())) {
                            userArrayList.add(user);
                            pd.dismiss();
                        }*/
                    }
                }
                pd.dismiss();
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*private void readMale(){
        FirebaseDatabase.getInstance("https://messageapp-f43a7-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").limitToFirst(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    if (snapshot.exists()){
                        User user = np.getValue(User.class);
                        if (!user.getUserId().equals(firebaseUser.getUid())
                        && user.getGender().equals("Male")) {
                            userArrayList.add(user);
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


    private void readFemale(){
        FirebaseDatabase.getInstance("https://messageapp-f43a7-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").limitToFirst(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    if (snapshot.exists()){
                        User user = np.getValue(User.class);
                        if (!user.getUserId().equals(firebaseUser.getUid())
                                &&user.getGender().equals("Female")) {
                            userArrayList.add(user);

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

    private void fromMyCountryMale(){
        FirebaseDatabase.getInstance("https://messageapp-f43a7-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").limitToFirst(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    if (snapshot.exists()){
                        User user = np.getValue(User.class);
                        if (!user.getUserId().equals(firebaseUser.getUid())
                                &&user.getCountry().equals(myCountry)&&user.getGender().equals("Male")) {
                            userArrayList.add(user);

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

    private void fromMyCountryFemale(){
        FirebaseDatabase.getInstance("https://messageapp-f43a7-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users").limitToFirst(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    if (snapshot.exists()){
                        User user = np.getValue(User.class);
                        if (!user.getUserId().equals(firebaseUser.getUid())
                                &&user.getCountry().equals(myCountry)&&user.getGender().equals("Female")) {
                            userArrayList.add(user);

                        }
                    }
                }
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/


}
