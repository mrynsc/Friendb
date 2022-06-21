package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivityAddFavoritesBinding;

import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;


public class AddFavoritesActivity extends AppCompatActivity {

    private ActivityAddFavoritesBinding binding;
    private FirebaseUser firebaseUser;

    private String favTv ="";
    private String favSummer="";
    private String favCat="";
    private String favSun ="";
    private String favPlan ="";
    private String favIntro ="";
    private String favMoney ="";
    private String favSuccess="";
    private String favForest ="";
    private String favTea = "";
    private String favTrain ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Questions");


        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }

        });

        binding.tvGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    favTv="TV Series";
                }else if (position==1){
                    favTv="Movies";
                }else{
                    favTv="";

                }
            }
        });

        binding.summerGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    favSummer="Summer";
                }else if (position==1){
                    favSummer="Winter";
                }else{
                    favSummer="";

                }
            }
        });

        binding.catGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    favCat="Cat";
                }else if (position==1){
                    favCat="Dog";
                }else{
                    favCat="";

                }
            }
        });

        binding.sunriseGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    favSun="Sunrise";
                }else if (position==1){
                    favSun="Sunset";
                }else{
                    favSun="";

                }
            }
        });

        binding.planGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    favPlan="Planning";
                }else if (position==1){
                    favPlan="Spontaneity";
                }else{
                    favPlan="";

                }
            }
        });


        binding.introGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    favIntro="Introvert";
                }else if (position==1){
                    favIntro="Extrovert";
                }else{
                    favIntro="";

                }
            }
        });

        binding.moneyGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    favMoney="Money";
                }else if (position==1){
                    favMoney="Love";
                }else{
                    favMoney="";

                }
            }
        });

        binding.successGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    favSuccess="Success";
                }else if (position==1){
                    favSuccess="Happiness";
                }else{
                    favSuccess="";

                }
            }
        });

        binding.seaGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    favForest="Forest";
                }else if (position==1){
                    favForest="Sea";
                }else{
                    favForest="";

                }
            }
        });

        binding.teaGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    favTea="Tea";
                }else if (position==1){
                    favTea="Coffee";
                }else{
                    favTea="";

                }
            }
        });

        binding.trainGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    favTea="Train";
                }else if (position==1){
                    favTea="Plane";
                }else{
                    favTea="";

                }
            }
        });


        binding.saveFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String control  = firebaseUser.getUid();
                Query query = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Favorites").child(firebaseUser.getUid())
                        .orderByChild("favOwnerId").equalTo(control);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount()>0){
                            StyleableToast.makeText(AddFavoritesActivity.this, "Reset previous answers.", R.style.customToast).show();
                        }else{
                            DatabaseReference reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference("Favorites");

                            String favId = reference.push().getKey();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("fav1",favTv);
                            hashMap.put("fav2",favSummer);
                            hashMap.put("fav3",favCat);
                            hashMap.put("fav4",favSun);
                            hashMap.put("fav5",favPlan);
                            hashMap.put("fav6",favIntro);
                            hashMap.put("fav7",favMoney);
                            hashMap.put("fav8",favSuccess);
                            hashMap.put("fav9",favForest);
                            hashMap.put("fav10",favTea);
                            hashMap.put("fav11",favTrain);

                            hashMap.put("fav12","");
                            hashMap.put("fav13","");
                            hashMap.put("fav14","");
                            hashMap.put("fav15", "");
                            hashMap.put("fav16", "");
                            hashMap.put("fav17", "");
                            hashMap.put("fav18", "");
                            hashMap.put("fav19", "");
                            hashMap.put("fav20", "");



                            hashMap.put("favId",favId);
                            hashMap.put("favOwnerId",firebaseUser.getUid());


                            StyleableToast.makeText(AddFavoritesActivity.this, "Saved!", R.style.customToast).show();
                            finish();

                            reference.child(firebaseUser.getUid()).child(favId).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //Toast.makeText(getContext(),   "Added!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddFavoritesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });


        binding.deleteFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Favorites").child(firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful()){
                             StyleableToast.makeText(AddFavoritesActivity.this, "Deleted your answers", R.style.customToast).show();
                         }
                    }
                });
            }
        });

    }
}