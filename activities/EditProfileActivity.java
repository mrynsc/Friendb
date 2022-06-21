package com.yeslabapps.friendb.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivityEditProfileBinding;
import com.yeslabapps.friendb.model.User;

import org.aviran.cookiebar2.CookieBar;

import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class EditProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivityEditProfileBinding binding;
    private FirebaseUser fUser;
    private String GAME_ID = "4789422";
    private String AVATAR_INTERSTITIAL_ID ="GoAvatar";
    private boolean test = false;

    private String persona;

    private String usernameControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fUser= FirebaseAuth.getInstance().getCurrentUser();


        UnityAds.initialize(EditProfileActivity.this,GAME_ID,test);




        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.addFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EditProfileActivity.this,AddFavoritesActivity.class);
                startActivity(intent);
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
                StyleableToast.makeText(EditProfileActivity.this, "Saved.", R.style.customToast).show();

                /*Intent intent = new Intent(EditProfileActivity.this, StartActivity.class);
                intent.putExtra("PublisherId",fUser.getUid());
                startActivity(intent);
                finish();*/
            }
        });


        userInfo();



        binding.changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usernameControl.equals("no")){
                    changeUsername();
                }else{
                    CookieBar.build(EditProfileActivity.this)
                            .setTitle("You can't change your username!")
                            .setMessage("You changed your username before.")
                            .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                            .show();
                }
            }
        });



        SharedPreferences sharedPreferences=getSharedPreferences("save",MODE_PRIVATE);
        binding.switchBtn.setChecked(sharedPreferences.getBoolean("value",true));

        SharedPreferences sharedPreferences1=getSharedPreferences("saveSeen",MODE_PRIVATE);
        binding.showLastSeen.setChecked(sharedPreferences1.getBoolean("valueSeen",true));




        binding.switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.switchBtn.isChecked()){

                    binding.switchBtn.setChecked(true);
                    StyleableToast.makeText(EditProfileActivity.this, "Everyone can message you.", R.style.customToast).show();

                }else {
                    // When switch unchecked

                    binding.switchBtn.setChecked(false);
                    StyleableToast.makeText(EditProfileActivity.this, "New users cannot write to you. You can continue to talk to those on your chat list.", R.style.customToast).show();

                }
            }
        });

        binding.showLastSeen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.showLastSeen.isChecked()){

                    binding.showLastSeen.setChecked(true);
                    StyleableToast.makeText(EditProfileActivity.this, "Open.", R.style.customToast).show();

                }else {
                    // When switch unchecked

                    binding.showLastSeen.setChecked(false);
                    StyleableToast.makeText(EditProfileActivity.this, "Closed.", R.style.customToast).show();

                }
            }
        });



        binding.typeBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeBio();
            }
        });

        binding.changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UnityAds.isInitialized()){
                    UnityAds.show(EditProfileActivity.this,AVATAR_INTERSTITIAL_ID);

                }
                startActivity(new Intent(EditProfileActivity.this,ChangeAvatarActivity.class));
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tags, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);
        binding.spinner.setOnItemSelectedListener(this);

        loadInterstitialId();


    }



    private void loadInterstitialId(){
        if (UnityAds.isInitialized()){
            UnityAds.load(AVATAR_INTERSTITIAL_ID);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UnityAds.load(AVATAR_INTERSTITIAL_ID);
                }
            },5000);
        }
    }



    private void changeUsername(){


        final Dialog dialog = new Dialog(EditProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_change_username);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        EditText editText =dialog.findViewById(R.id.changeUsernameEt);
        Button button = dialog.findViewById(R.id.saveUsername);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = editText.getText().toString().trim();
                Query query = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Users").orderByChild("username").equalTo(userName);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount()>0){
                            StyleableToast.makeText(EditProfileActivity.this, "Username already exists.", R.style.customToast).show();

                        }else if(userName.trim().length()>1){
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("username", editText.getText().toString().trim());
                            hashMap.put("usernameLower", editText.getText().toString().trim().toLowerCase());

                            hashMap.put("isUsernameChanged","yes");

                            FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference().child("Users").child(fUser.getUid()).updateChildren(hashMap);
                            StyleableToast.makeText(EditProfileActivity.this, "Updated!", R.style.customToast).show();


                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }

    private void typeBio(){
        final Dialog dialog = new Dialog(EditProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_bio);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.getWindow().setGravity(Gravity.CENTER);

        EditText editText =dialog.findViewById(R.id.bioEdit);
        Button button = dialog.findViewById(R.id.saveBio);
        ImageView close = dialog.findViewById(R.id.closeBio);


        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                editText.setText(user.getBio());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("bio", editText.getText().toString().trim());

                    FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference().child("Users").child(fUser.getUid()).updateChildren(hashMap);
                    StyleableToast.makeText(EditProfileActivity.this, "Updated!", R.style.customToast).show();

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



    }


    private void updateProfile() {
        if (binding.switchBtn.isChecked()){
            SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
            editor.putBoolean("value",true);
            editor.apply();
            binding.switchBtn.setChecked(true);

        }else {
            // When switch unchecked
            SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
            editor.putBoolean("value",false);
            editor.apply();
            binding.switchBtn.setChecked(false);
        }

        boolean prefer = EditProfileActivity.this
                .getSharedPreferences("save", MODE_PRIVATE).getBoolean("value", false);


        if (binding.showLastSeen.isChecked()){
            SharedPreferences.Editor editor=getSharedPreferences("saveSeen",MODE_PRIVATE).edit();
            editor.putBoolean("valueSeen",true);
            editor.apply();
            binding.showLastSeen.setChecked(true);
        }else{
            SharedPreferences.Editor editor=getSharedPreferences("saveSeen",MODE_PRIVATE).edit();
            editor.putBoolean("valueSeen",false);
            editor.apply();
            binding.showLastSeen.setChecked(false);
        }
        boolean seenPrefer = EditProfileActivity.this
                .getSharedPreferences("saveSeen", MODE_PRIVATE).getBoolean("valueSeen", false);



        HashMap<String, Object> map = new HashMap<>();
        if (prefer==true){
            map.put("dmPrefer","open");
        }else{
            map.put("dmPrefer","close");
        }
        if (seenPrefer==true){
            map.put("seenPrefer","show");
        }else {
            map.put("seenPrefer","hide");
        }


        map.put("mbti",persona);


        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(fUser.getUid()).updateChildren(map);
    }


    private void userInfo(){
        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                persona = user.getMbti();

                usernameControl = user.getIsUsernameChanged();

                //binding.mbtiText.setText(user.getMbti());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.spinner:
                if (i!=1 && i!=0){
                    persona = adapterView.getItemAtPosition(i).toString();
                    //binding.mbtiText.setText(text);
                }else if (i==1){
                    persona = "";
                    //binding.mbtiText.setText("");
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
