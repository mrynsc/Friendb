package com.yeslabapps.friendb.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.unity3d.ads.UnityAds;
import com.yeslabapps.friendb.MainActivity;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivitySettingsBinding;
import com.yeslabapps.friendb.model.Block;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    private String GAME_ID = "4789422";
    private String INTERSTITIAL_ID ="Interstitial_Android";
    private boolean test = false;
    private String SEARCH_INTERSTITIAL_ID ="goSearch";
    private String QUESTIONS_REWARDED_ID = "Rewarded_Android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Settings");

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }

        });

        UnityAds.initialize(SettingsActivity.this,GAME_ID,test);

        loadInterstitialId();
        loadInterstitialId2();
        loadInterstitialIdSearch();


        /*SharedPreferences sp = SettingsActivity.this.getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        final boolean isDarkModeOn = sp.getBoolean("isDarkModeOn",false);


        if (isDarkModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            binding.darkModeBtn.setText("Disable");
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            binding.darkModeBtn.setText("Enable");
        }

        binding.darkModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDarkModeOn) {

                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_NO);
                    // it will set isDarkModeOn
                    // boolean to false
                    editor.putBoolean(
                            "isDarkModeOn", false);
                    editor.apply();

                    // change text of Button
                    binding.darkModeBtn.setText(
                            "Enable Dark Mode");
                } else {

                    // if dark mode is off
                    // it will turn it on
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_YES);

                    // it will set isDarkModeOn
                    // boolean to true
                    editor.putBoolean(
                            "isDarkModeOn", true);
                    editor.apply();

                    // change text of Button
                    binding.darkModeBtn.setText(
                            "Disable Dark Mode");
                }

            }
        });*/

        binding.searchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UnityAds.isInitialized()){
                    UnityAds.show(SettingsActivity.this,SEARCH_INTERSTITIAL_ID);
                }
                Intent intent = new Intent(SettingsActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        binding.shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent =   new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"");
                String app_url = "https://play.google.com/store/apps/details?id=com.yeslabapps.friendb";
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,app_url);
                startActivity(Intent.createChooser(shareIntent,"Share Via"));
            }
        });

        binding.rateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.yeslabapps.friendb")));
            }
        });


        binding.seePrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(SettingsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottom_privacy);

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });

//https://pages.flycricket.io/friendb/privacy.html

//https://pages.flycricket.io/friendb/terms.html

        binding.seeTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(SettingsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottom_terms);

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });


        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UnityAds.isInitialized()){
                    UnityAds.show(SettingsActivity.this,INTERSTITIAL_ID);

                }
                Intent intent= new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.seeBlockedUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UnityAds.isInitialized()){
                    UnityAds.show(SettingsActivity.this,QUESTIONS_REWARDED_ID);

                }
                Intent intent= new Intent(SettingsActivity.this, BlockedUsersActivity.class);
                startActivity(intent);
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

    }

    private void logOut(){
        MaterialDialog materialDialog = new MaterialDialog.Builder(SettingsActivity.this)
                .setTitle("Sign out")
                .setMessage("Are you sure you want to sign out?")
                .setCancelable(true)
                .setPositiveButton("Yes", R.drawable.ic_baseline_exit_to_app_24, new AbstractDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent= new Intent(SettingsActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Cancel", R.drawable.close_24, new AbstractDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                }).build();

        materialDialog.show();
    }
    private void loadInterstitialId(){
        if (UnityAds.isInitialized()){
            UnityAds.load(INTERSTITIAL_ID);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UnityAds.load(INTERSTITIAL_ID);
                }
            },5000);
        }
    }


    private void loadInterstitialIdSearch(){
        if (UnityAds.isInitialized()){
            UnityAds.load(SEARCH_INTERSTITIAL_ID);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UnityAds.load(SEARCH_INTERSTITIAL_ID);
                }
            },5000);
        }
    }

    private void loadInterstitialId2(){
        if (UnityAds.isInitialized()){
            UnityAds.load(QUESTIONS_REWARDED_ID);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UnityAds.load(QUESTIONS_REWARDED_ID);
                }
            },5000);
        }
    }


}