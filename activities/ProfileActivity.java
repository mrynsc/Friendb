package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.adapters.ProfileTopicsAdapter;
import com.yeslabapps.friendb.databinding.ActivityProfileBinding;
import com.yeslabapps.friendb.model.Topics;
import com.yeslabapps.friendb.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;


public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private String userId;
    private ArrayList<Topics> topicsArrayList;
    private ProfileTopicsAdapter topicsAdapter;

    private ProgressDialog pd;

    private String messagePrefer;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }

        });

        pd = new ProgressDialog(ProfileActivity.this,R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();

        Intent intent = ProfileActivity.this.getIntent();
        userId = intent.getStringExtra("someUserId");


        topicsAdapter = new ProfileTopicsAdapter(topicsArrayList, ProfileActivity.this);
        GridLayoutManager layoutManager=new GridLayoutManager( ProfileActivity.this,3);
        binding.profileTopicRecycler.setAdapter(topicsAdapter);
        binding.profileTopicRecycler.setLayoutManager(layoutManager);

        topicsArrayList = new ArrayList<>();

        userInfo();
        getTopics();


        binding.seeFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ProfileActivity.this,FavoritesActivity.class);
                intent1.putExtra("usersFavId",userId);
                startActivity(intent1);
            }
        });


        binding.sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userId!=null && messagePrefer.equals("open")){
                    Intent intent1 = new Intent(ProfileActivity.this,ChatActivity.class);
                    intent1.putExtra("chatUserId",userId);
                    startActivity(intent1);
                }else{
                    StyleableToast.makeText(ProfileActivity.this, "This user's message box is close!",R.style.customToast).show();
                }

            }
        });


        binding.seeMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(ProfileActivity.this,binding.seeMoreOptions);
                popupMenu.getMenuInflater().inflate(R.menu.popup_some_profile,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.reportUserItem:
                                Intent intent1 = new Intent(ProfileActivity.this, ReportUserActivity.class);
                                intent1.putExtra("userNameReport",binding.usernameText.getText().toString());
                                startActivity(intent1);
                                break;
                            case R.id.blockUserItem:
                                Intent intent2 = new Intent(ProfileActivity.this,ChatActivity.class);
                                intent2.putExtra("chatUserId",userId);

                                StyleableToast.makeText(ProfileActivity.this, "You can block users by pressing the icon in the upper right. If you block them, they won't be able to message you.", R.style.chatToast).show();
                                startActivity(intent2);
                                break;

                        }
                        return true;
                    }

                });

                popupMenu.show();
            }
        });

    }


    private void getTopics() {
        topicsArrayList= new ArrayList<>();
        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Topics").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topicsArrayList.clear();
                for (DataSnapshot np:snapshot.getChildren()){
                    Topics topics = np.getValue(Topics.class);

                    topicsArrayList.add(topics);
                    pd.dismiss();
                }

                pd.dismiss();
                topicsAdapter=new ProfileTopicsAdapter(topicsArrayList,ProfileActivity.this);
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
                .getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
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


                    messagePrefer = user.getDmPrefer();

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


                    /*String lastseen = user.getLastSeen().substring(0,Math.min(user.getLastSeen().length(),6));
                    String lastHour = user.getLastSeen().substring(user.getLastSeen().length()-5);

                    //String lastMonth = user.getLastSeen().substring(3,Math.min(user.getLastSeen().length(),6));

                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy k:mm",new Locale("en"));
                    String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(System.currentTimeMillis()))));

                    //String timeStamp = new SimpleDateFormat("dd MM yyyy HH mm").format(Calendar.getInstance().getTime());

                    String useTime = dateString.substring(0,Math.min(dateString.length(),6));


                    if (user.getSeenPrefer().equals("show")){
                        if (lastseen.equals(useTime)){
                            binding.userLastSeen.setText("Today "+ lastHour);
                        }else{
                            binding.userLastSeen.setText(user.getLastSeen());
                        }
                    }else{
                        binding.userLastSeen.setText("No Info");
                    }*/

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

}