package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivityStartBinding;
import com.yeslabapps.friendb.fragments.ChatsFragment;
import com.yeslabapps.friendb.fragments.HomeFragment;
import com.yeslabapps.friendb.fragments.ProfileFragment;
import com.yeslabapps.friendb.notify.Token;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class StartActivity extends AppCompatActivity {

    private ActivityStartBinding binding;
    private Fragment selectorFragment;
    private FirebaseAuth auth;

    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        auth = FirebaseAuth.getInstance();






        binding.bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.navHome:
                        selectorFragment= new ChatsFragment();
                        break;

                    case R.id.navExplore:
                        selectorFragment= new HomeFragment();
                        break;


                    case R.id.navProfile:

                        getSharedPreferences("Profile",MODE_PRIVATE).edit().putString("ProfileId", FirebaseAuth.getInstance().getCurrentUser().getUid()).apply();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                        selectorFragment = null;

                        break;
                }
                if (selectorFragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
                }
                return true;
            }

        });

        Bundle intent = getIntent().getExtras();
        if (intent!=null){
            String profileId =intent.getString("PublisherId");
            getSharedPreferences("Profile",MODE_PRIVATE).edit().putString("ProfileId",profileId).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();

        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ChatsFragment()).commit();
        }

        updateToken(FirebaseInstanceId.getInstance().getToken());
        updateLastSeen();



    }
    public void updateToken(String token){
        DatabaseReference ref= FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Tokens");
        Token mToken=new Token(token);
        ref.child(firebaseUser.getUid()).setValue(mToken);
    }


    private void updateLastSeen(){


        HashMap<String, Object> map = new HashMap<>();

        map.put("lastSeen",String.valueOf(System.currentTimeMillis()));


        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map);
    }

}