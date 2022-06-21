package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.friendb.databinding.ActivityChangeAvatarBinding;
import com.yeslabapps.friendb.databinding.ActivityFavoritesBinding;
import com.yeslabapps.friendb.model.Favorites;
import com.yeslabapps.friendb.model.User;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    private ActivityFavoritesBinding binding;
    private String userId;
    //private ArrayList<Favorites> favoritesArrayList;

    private FirebaseUser firebaseUser;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = FavoritesActivity.this.getIntent();

        userId= intent.getStringExtra("usersFavId");



        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }

        });


        userInfo();

        answers();
    }

    private void userInfo(){
        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);

                    binding.favUserText.setText(user.getUsername() + "'s answers");



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void answers(){
        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Favorites").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot np:snapshot.getChildren()){
                    Favorites favorites = np.getValue(Favorites.class);

                    if (favorites.getFav1().equals("")){
                        binding.favText.setText("TV Series or Movies • No Answer");
                        binding.favText.setTextColor(Color.RED);

                    }else{
                        binding.favText.setText("TV Series or Movies • "+favorites.getFav1());
                    }


                    if (favorites.getFav2().equals("")){
                        binding.favText2.setText("Winter or Summer • No Answer");
                        binding.favText2.setTextColor(Color.RED);
                    }else{
                        binding.favText2.setText("Winter or Summer • "+favorites.getFav2());
                    }


                    if (favorites.getFav3().equals("")){
                        binding.favText3.setText("Cat or Dog • No Answer");
                        binding.favText3.setTextColor(Color.RED);

                    }else{
                        binding.favText3.setText("Cat or Dog • "+favorites.getFav3());
                    }

                    if (favorites.getFav4().equals("")){
                        binding.favText4.setText("Sunrise or Sunset • No Answer");
                        binding.favText4.setTextColor(Color.RED);

                    }else{
                        binding.favText4.setText("Sunrise or Sunset • "+favorites.getFav4());
                    }


                    if (favorites.getFav5().equals("")){
                        binding.favText5.setText("Planning or Spontaneity • No Answer");
                        binding.favText5.setTextColor(Color.RED);
                    }else{
                        binding.favText5.setText("Planning or Spontaneity • "+favorites.getFav5());
                    }


                    if (favorites.getFav6().equals("")){
                        binding.favText6.setText("Introvert or Extrovert • No Answer");
                        binding.favText6.setTextColor(Color.RED);

                    }else{
                        binding.favText6.setText("Introvert or Extrovert • "+favorites.getFav6());
                    }


                    if (favorites.getFav7().equals("")){
                        binding.favText7.setText("Money or Love • No Answer");
                        binding.favText7.setTextColor(Color.RED);

                    }else{
                        binding.favText7.setText("Money or Love • "+favorites.getFav7());
                    }


                    if (favorites.getFav8().equals("")){
                        binding.favText8.setText("Success or Happiness • No Answer");
                        binding.favText8.setTextColor(Color.RED);

                    }else{
                        binding.favText8.setText("Success or Happiness • "+favorites.getFav8());
                    }

                    if (favorites.getFav9().equals("")){
                        binding.favText9.setText("Forest or Sea • No Answer");
                        binding.favText9.setTextColor(Color.RED);

                    }else{
                        binding.favText9.setText("Forest or Forest • "+favorites.getFav9());
                    }

                    if (favorites.getFav10().equals("")){
                        binding.favText10.setText("Tea or Coffee • No Answer");
                        binding.favText10.setTextColor(Color.RED);

                    }else{
                        binding.favText10.setText("Tea or Coffee • "+favorites.getFav10());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}