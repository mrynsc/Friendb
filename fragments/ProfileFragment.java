package com.yeslabapps.friendb.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.unity3d.ads.UnityAds;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.activities.EditProfileActivity;
import com.yeslabapps.friendb.activities.FavoritesActivity;
import com.yeslabapps.friendb.activities.SettingsActivity;
import com.yeslabapps.friendb.adapters.ProfileTopicsAdapter;
import com.yeslabapps.friendb.databinding.FragmentProfileBinding;
import com.yeslabapps.friendb.model.Topics;
import com.yeslabapps.friendb.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseUser firebaseUser;
    private String profileId;
    private ArrayList<Topics> topicsArrayList;
    private ProfileTopicsAdapter topicsAdapter;

    //private String gender;
    //private String country;

    private ProgressDialog pd;

    private String GAME_ID = "4789422";
    private String QUESTIONS_INTERSTITIAL_ID ="GoQuestions";
    private boolean test = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //String locale = getContext().getResources().getConfiguration().locale.getDisplayCountry(Locale.ENGLISH);

        String data = getContext().getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId", "");
        if (data.equals("")) {
            profileId = firebaseUser.getUid();
        } else {
            profileId = data;
        }

        UnityAds.initialize(getContext(),GAME_ID,test);

        loadInterstitialId();

        /*boolean country = getContext().getSharedPreferences("save", MODE_PRIVATE).getBoolean("value", false);

        if (country==true){
            Toast.makeText(getContext(),locale , Toast.LENGTH_SHORT).show();

        }*/

        pd = new ProgressDialog(getContext(),R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();



        topicsAdapter = new ProfileTopicsAdapter(topicsArrayList,getContext());
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),3);
        binding.profileTopicRecycler.setAdapter(topicsAdapter);
        binding.profileTopicRecycler.setLayoutManager(layoutManager);

        topicsArrayList = new ArrayList<>();


        binding.seeFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UnityAds.isInitialized()){
                    UnityAds.show((Activity) getContext(),QUESTIONS_INTERSTITIAL_ID);

                }
                Intent intent = new Intent(getContext(),FavoritesActivity.class);
                intent.putExtra("usersFavId",profileId);
                startActivity(intent);
            }
        });


        binding.moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu1 = new PopupMenu(getContext(),binding.moreOptions);
                popupMenu1.getMenuInflater().inflate(R.menu.popup_profile,popupMenu1.getMenu());
                popupMenu1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.editProfilePopItem:

                                Intent intent= new Intent(getContext(), EditProfileActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.editTopics:

                                editTopics();
                                break;

                            case R.id.goSettingsItem:
                                startActivity(new Intent(getContext(),SettingsActivity.class));

                        }
                        return true;
                    }

                });

                popupMenu1.show();
            }
        });

        userInfo();
        getTopics();



        return binding.getRoot();

    }



    private void loadInterstitialId(){
        if (UnityAds.isInitialized()){
            UnityAds.load(QUESTIONS_INTERSTITIAL_ID);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UnityAds.load(QUESTIONS_INTERSTITIAL_ID);
                }
            },5000);
        }
    }


    private void getTopics() {
        topicsArrayList= new ArrayList<>();
        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Topics").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topicsArrayList.clear();
                for (DataSnapshot np:snapshot.getChildren()){
                    Topics topics = np.getValue(Topics.class);

                    if (topics.getOwnerId().equals(profileId)){
                        topicsArrayList.add(topics);

                    }

                    pd.dismiss();
                }

                pd.dismiss();
                topicsAdapter=new ProfileTopicsAdapter(topicsArrayList,getContext());
                binding.profileTopicRecycler.setAdapter(topicsAdapter);
                topicsAdapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void userInfo() {
        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);

                    if (user.getAvatar().equals("maleDefault")){
                        binding.profileImage.setImageResource(R.drawable.cartoonify1);
                    }else if (user.getAvatar().equals("femaleDefault")){
                        binding.profileImage.setImageResource(R.drawable.cartoonify2);
                    }else{
                        Picasso.get().load(user.getAvatar()).into(binding.profileImage);

                    }



                    binding.usernameText.setText(user.getUsername());
                    binding.userCountry.setText(user.getCountry());
                    binding.userBio.setText(user.getBio());
                    binding.userGender.setText(user.getGender());
                    binding.userMbti.setText(user.getMbti());
                    //binding.userAge.setText(user.getBirthDate()+ String.valueOf(user.getAge()));

                    String age = user.getBirthDate().substring(0,Math.min(
                            user.getBirthDate().length(),6));

                    binding.userAge.setText(age +" "+ "("+(user.getAge())+")");

                    binding.userLastSeen.setText(convertTime(user.getLastSeen()));


                    // String lastseen = user.getLastSeen().substring(0,Math.min(user.getLastSeen().length(),6));
                    //String lastHour = user.getLastSeen().substring(user.getLastSeen().length()-5);

                    //String lastMonth = user.getLastSeen().substring(3,Math.min(user.getLastSeen().length(),6));

                    //SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy k:mm",new Locale("en"));
                    //String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(System.currentTimeMillis()))));

                    //String timeStamp = new SimpleDateFormat("dd MM yyyy HH mm").format(Calendar.getInstance().getTime());

                    //String useTime = dateString.substring(0,Math.min(dateString.length(),6));


                    /*if (user.getSeenPrefer().equals("show")){
                        if (lastseen.equals(useTime)){
                            binding.userLastSeen.setText("Today "+ lastHour);
                        }else{
                            binding.userLastSeen.setText(user.getLastSeen());
                        }
                    }else{
                        binding.userLastSeen.setText("No Info");
                    }*/



                    //gender=user.getGender();
                    //country=user.getCountry();


                    if (user.getGender().equals("Male")){
                        binding.userGender.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_baseline_male_24,0,0,0
                        );
                    }else if (user.getGender().equals("Female")){
                        binding.userGender.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_baseline_female_24,0,0,0
                        );
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private String convertTime(String time){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM k:mm");
        String dateString = formatter.format(new Date(Long.parseLong(time)));
        return dateString;
    }

    private void editTopics() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_topics);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        ListView listView = dialog.findViewById(R.id.listView);
        String topics[] = {"Anime","Art","Astrology","Astronomy","Cars","Coding","Cooking","Cosplay","Dancing",
                "Economics","Fantasy","Fashion","Fiction","Fitness","Gaming","History","Humor","Language",
                "Literature","Makeup","Movies","Music","Pets","Philosophy","Photography","Poetry","Politics",
                "Psychology","Reading","Religion","Sci-Fi","Science","Sports","TV Series","Technology","Theatre",
                "Travel","Writing"};
        ArrayAdapter<String> arr;
        arr = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item,
                topics);
        listView.setAdapter(arr);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                DatabaseReference reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("Topics");

                String topicId = reference.push().getKey();


                HashMap<String, Object> hashMap = new HashMap<>();


                hashMap.put("topic",listView.getItemAtPosition(i).toString());
                hashMap.put("ownerId",firebaseUser.getUid());
                hashMap.put("topicId",topicId);
                //hashMap.put("ownerGender",gender);
                //hashMap.put("ownerCountry",country);

                reference.child(firebaseUser.getUid()).child(topicId).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Toast.makeText(getContext(),   "Added!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


                /*String control  = listView.getItemAtPosition(i).toString();
                Query query = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Topics").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .orderByChild("topic").equalTo(control);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount()>0){
                            StyleableToast.makeText(getContext(), "Available in your topics.",R.style.customToast).show();
                        }


                        else if (topicsArrayList.size()<24){

                            DatabaseReference reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference("Topics");

                            String topicId = reference.push().getKey();


                            HashMap<String, Object> hashMap = new HashMap<>();


                            hashMap.put("topic",listView.getItemAtPosition(i).toString());
                            hashMap.put("ownerId",firebaseUser.getUid());
                            hashMap.put("topicId",topicId);
                            //hashMap.put("ownerGender",gender);
                            //hashMap.put("ownerCountry",country);

                            reference.child(firebaseUser.getUid()).child(topicId).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //Toast.makeText(getContext(),   "Added!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else{
                            StyleableToast.makeText(getContext(), "You can add up to 24 topics.", R.style.customToast ).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

            }


        });
    }
}








