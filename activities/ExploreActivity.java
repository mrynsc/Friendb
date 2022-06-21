package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.adapters.UserAdapter;
import com.yeslabapps.friendb.databinding.ActivityExploreBinding;
import com.yeslabapps.friendb.model.User;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class ExploreActivity extends AppCompatActivity {

    private ActivityExploreBinding binding;
    private UserAdapter userAdapter;
    private ArrayList<User> userArrayList;

    private FirebaseUser firebaseUser;
    private ProgressDialog pd;

    private String profileId;

    private String showBio="anyone";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityExploreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolbarHome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        binding.toolbarHome.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }

        });


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = ExploreActivity.this.getSharedPreferences("Profile", MODE_PRIVATE).getString("ProfileId","");
        if (data.equals("")){
            profileId = firebaseUser.getUid();
        }else{
            profileId = data;
        }

        pd = new ProgressDialog(ExploreActivity.this,R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();

        binding.filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilter();
            }
        });

        binding.userRecycler.setHasFixedSize(true);

        userArrayList = new ArrayList<>();
        userAdapter = new UserAdapter(userArrayList,ExploreActivity.this);
        GridLayoutManager layoutManager=new GridLayoutManager(ExploreActivity.this,2);
        binding.userRecycler.setAdapter(userAdapter);


        binding.userRecycler.setLayoutManager(layoutManager);


        userInfo();

        readDefault();

    }

    private void userInfo(){
        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);


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
                .child("Users").orderByChild("registerDate").startAt("20 06 2022 13 00")
                .endAt("30 06 2022 13 00").limitToLast(30).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for (DataSnapshot np : snapshot.getChildren()){
                    if (snapshot.exists()){
                        User user = np.getValue(User.class);

                        if (!user.getUserId().equals(firebaseUser.getUid())
                                &&user.getUserId()!=null &&user.getAccountStatus().equals("active")) {
                            userArrayList.add(user);
                            pd.dismiss();

                        }
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


    private void showFilter(){
        final Dialog dialog = new Dialog(ExploreActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_filter);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        ListView listView = dialog.findViewById(R.id.countryFilterList);
        ArrayAdapter<String> arrayAdapter;

        String[] countries={"Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla",
                "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria",
                "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
                "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana",
                "Brazil", "British Indian Ocean Territory", "British Virgin Islands", "Brunei", "Bulgaria",
                "Burkina Faso", "Burma (Myanmar)", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde",
                "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island",
                "Cocos (Keeling) Islands", "Colombia", "Comoros", "Cook Islands", "Costa Rica",
                "Croatia", "Cuba", "Cyprus", "Czech Republic", "Democratic Republic of the Congo",
                "Denmark", "Djibouti", "Dominica", "Dominican Republic",
                "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",
                "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France", "French Polynesia",
                "Gabon", "Gambia", "Gaza Strip", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece",
                "Greenland", "Grenada", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana",
                "Haiti", "Holy See (Vatican City)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India",
                "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Ivory Coast", "Jamaica",
                "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kosovo", "Kuwait",
                "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein",
                "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia",
                "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mayotte", "Mexico",
                "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco",
                "Mozambique", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia",
                "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea",
                "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama",
                "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn Islands", "Poland",
                "Portugal", "Puerto Rico", "Qatar", "Republic of the Congo", "Romania", "Russia", "Rwanda",
                "Saint Barthelemy", "Saint Helena", "Saint Kitts and Nevis", "Saint Lucia", "Saint Martin",
                "Saint Pierre and Miquelon", "Saint Vincent and the Grenadines", "Samoa", "San Marino",
                "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone",
                "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Korea",
                "Spain", "Sri Lanka", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland",
                "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tokelau",
                "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands",
                "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay",
                "US Virgin Islands", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Wallis and Futuna", "West Bank",
                "Yemen", "Zambia", "Zimbabwe"};


        arrayAdapter= new ArrayAdapter<String>(ExploreActivity.this, android.R.layout.simple_list_item_multiple_choice
                ,countries);
        listView.setAdapter(arrayAdapter);


        ListView mbtiList = dialog.findViewById(R.id.mbtiFilterList);
        ArrayAdapter<String> arrayAdapter2;
        String[] mbti={"INTJ","INTP","ENTJ","ENTP","INFJ","INFP","ENFJ","ENFP","ISTJ",
                "ISFJ","ESTJ","ESFJ","ISTP","ISFP","ESTP","ESFP"};


        arrayAdapter2= new ArrayAdapter<String>(ExploreActivity.this, android.R.layout.simple_list_item_multiple_choice
                ,mbti);
        mbtiList.setAdapter(arrayAdapter2);

        ListView ageList = dialog.findViewById(R.id.agefilterList);
        ArrayAdapter<String> arrayAdapter3;
        String[] ages={"10-20","21-30","31-40","41-50"};


        SwitchCompat switchCompat = dialog.findViewById(R.id.switchBio);

        SharedPreferences sharedPreferences=getSharedPreferences("saveBio",MODE_PRIVATE);
        switchCompat.setChecked(sharedPreferences.getBoolean("valueBio",true));





        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (switchCompat.isChecked()){
                    SharedPreferences.Editor editor=getSharedPreferences("saveBio",MODE_PRIVATE).edit();
                    editor.putBoolean("valueBio",true);
                    editor.apply();
                    showBio = "onlyBio";
                    switchCompat.setChecked(true);
                }else {
                    SharedPreferences.Editor editor=getSharedPreferences("saveBio",MODE_PRIVATE).edit();
                    editor.putBoolean("valueBio",false);
                    editor.apply();
                    showBio = "anyone";
                    switchCompat.setChecked(false);
                }
            }
        });

        arrayAdapter3= new ArrayAdapter<String>(ExploreActivity.this, android.R.layout.simple_list_item_multiple_choice
                ,ages);
        ageList.setAdapter(arrayAdapter3);

        CheckBox maleCheck=dialog.findViewById(R.id.maleCheck);
        CheckBox femaleCheck=dialog.findViewById(R.id.femaleCheck);




        Button button = dialog.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String gender ="";

                if (maleCheck.isChecked()){
                    gender = "Male";
                }

                if (femaleCheck.isChecked()){
                    gender ="Female";
                }

                List<Integer> addAges = new ArrayList<>();

                if (ageList.isItemChecked(0)){
                    addAges.add(10);
                    addAges.add(11);
                    addAges.add(12);
                    addAges.add(13);
                    addAges.add(14);
                    addAges.add(15);
                    addAges.add(16);
                    addAges.add(17);
                    addAges.add(18);
                    addAges.add(19);
                    addAges.add(20);
                }else if (ageList.isItemChecked(1)){
                    addAges.add(21);
                    addAges.add(22);
                    addAges.add(23);
                    addAges.add(24);
                    addAges.add(25);
                    addAges.add(26);
                    addAges.add(27);
                    addAges.add(28);
                    addAges.add(29);
                    addAges.add(30);
                }else if (ageList.isItemChecked(2)){
                    addAges.add(31);
                    addAges.add(32);
                    addAges.add(33);
                    addAges.add(34);
                    addAges.add(35);
                    addAges.add(36);
                    addAges.add(37);
                    addAges.add(38);
                    addAges.add(39);
                    addAges.add(40);
                }else if (ageList.isItemChecked(3)){
                    addAges.add(41);
                    addAges.add(42);
                    addAges.add(43);
                    addAges.add(44);
                    addAges.add(45);
                    addAges.add(46);
                    addAges.add(47);
                    addAges.add(48);
                    addAges.add(49);
                    addAges.add(50);
                }



                List<String> addCountries= new ArrayList<>();


                for (int i = 0; i < listView.getCount(); i++) {
                    if (listView.isItemChecked(i)){
                        addCountries.add(listView.getItemAtPosition(i).toString());
                        //selected +=" " + listView.getItemAtPosition(i).toString() + "";
                    }
                }

                List<String> addTypes =new ArrayList<>();
                for (int i = 0; i < mbtiList.getCount(); i++) {
                    if (mbtiList.isItemChecked(i)){
                        addTypes.add(mbtiList.getItemAtPosition(i).toString());
                    }
                }

                //Toast.makeText(ExploreActivity.this, addAges + " are selected",Toast.LENGTH_LONG).show();
                //Toast.makeText(getContext(),itemSelected,Toast.LENGTH_LONG).show();

                //Toast.makeText(getContext(), addTypes.toString(), Toast.LENGTH_SHORT).show();

                String finalGender = gender;


                FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference()
                        .child("Users").orderByChild("registerDate").startAt("20 06 2022 13 00")
                        .endAt("30 06 2022 13 00").limitToLast(50).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userArrayList.clear();
                        for (DataSnapshot np : snapshot.getChildren()){
                            if (snapshot.exists()){
                                User user = np.getValue(User.class);
                                if (showBio.equals("anyone")){
                                    if (!user.getUserId().equals(firebaseUser.getUid()) &&user.getUserId()!=null &&user.getAccountStatus().equals("active")
                                            && finalGender.equals(user.getGender())
                                            ||!user.getUserId().equals(firebaseUser.getUid()) &&user.getAccountStatus().equals("active")
                                            && addCountries.contains(user.getCountry()) &&user.getUserId()!=null
                                            || !user.getUserId().equals(firebaseUser.getUid())&&user.getAccountStatus().equals("active")
                                            && addTypes.contains(user.getMbti()) &&user.getUserId()!=null
                                            || !user.getUserId().equals(firebaseUser.getUid())&&user.getAccountStatus().equals("active")
                                            && addAges.contains(user.getAge()) &&user.getUserId()!=null
                                            ||!user.getUserId().equals(firebaseUser.getUid()) &&user.getAccountStatus().equals("active")
                                            &&finalGender.equals(user.getGender())&&addCountries.contains(user.getCountry())&&
                                            addTypes.contains(user.getMbti()) &&user.getUserId()!=null
                                            ||!user.getUserId().equals(firebaseUser.getUid()) &&user.getAccountStatus().equals("active")
                                            &&finalGender.equals(user.getGender()) &&user.getUserId()!=null
                                            &&addCountries.contains(user.getCountry())&&addAges.contains(user.getAge())
                                            ||!user.getUserId().equals(firebaseUser.getUid()) &&user.getAccountStatus().equals("active")
                                            && finalGender.equals(user.getGender())&&addTypes.contains(user.getMbti())
                                            &&addAges.contains(user.getAge()) &&user.getUserId()!=null
                                            ||!user.getUserId().equals(firebaseUser.getUid())  &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            && addCountries.contains(user.getCountry())&&addTypes.contains(user.getMbti())&&addAges.contains(user.getAge())
                                            ||!user.getUserId().equals(firebaseUser.getUid()) &&user.getAccountStatus().equals("active")
                                            &&user.getUserId()!=null
                                            &&finalGender.equals(user.getGender())&&addCountries.contains(user.getCountry())
                                            ||!user.getUserId().equals(firebaseUser.getUid()) &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            && finalGender.equals(user.getGender())&&addTypes.contains(user.getMbti())
                                            || !user.getUserId().equals(firebaseUser.getUid())  &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            &&addCountries.contains(user.getCountry())&&addTypes.contains(user.getMbti())){
                                        userArrayList.add(user);
                                        pd.dismiss();
                                    }
                                }else{
                                    if (!user.getUserId().equals(firebaseUser.getUid()) &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            && finalGender.equals(user.getGender())&&user.getBio().length()>0
                                            ||!user.getUserId().equals(firebaseUser.getUid())  &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            && addCountries.contains(user.getCountry())&&user.getBio().length()>0
                                            || !user.getUserId().equals(firebaseUser.getUid())  &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            && addTypes.contains(user.getMbti())&&user.getBio().length()>0
                                            || !user.getUserId().equals(firebaseUser.getUid()) &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            && addAges.contains(user.getAge())&&user.getBio().length()>0

                                            ||!user.getUserId().equals(firebaseUser.getUid()) &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            &&finalGender.equals(user.getGender())&&addCountries.contains(user.getCountry())&&
                                            addTypes.contains(user.getMbti())&&user.getBio().length()>0
                                            ||!user.getUserId().equals(firebaseUser.getUid()) &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            &&finalGender.equals(user.getGender())&&addCountries.contains(user.getCountry())
                                            &&addAges.contains(user.getAge())&&user.getBio().length()>0
                                            ||!user.getUserId().equals(firebaseUser.getUid()) &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            && finalGender.equals(user.getGender())&&addTypes.contains(user.getMbti())
                                            &&addAges.contains(user.getAge())&&user.getBio().length()>0
                                            ||!user.getUserId().equals(firebaseUser.getUid()) &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            && addCountries.contains(user.getCountry())&&addTypes.contains(user.getMbti())
                                            &&addAges.contains(user.getAge())&&user.getBio().length()>0
                                            ||!user.getUserId().equals(firebaseUser.getUid())  &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            &&finalGender.equals(user.getGender())&&addCountries.contains(user.getCountry())&&user.getBio().length()>0
                                            ||!user.getUserId().equals(firebaseUser.getUid())  &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            && finalGender.equals(user.getGender())&&addTypes.contains(user.getMbti())&&user.getBio().length()>0
                                            || !user.getUserId().equals(firebaseUser.getUid()) &&user.getUserId()!=null
                                            &&user.getAccountStatus().equals("active")
                                            &&addCountries.contains(user.getCountry())&&addTypes.contains(user.getMbti())
                                            &&user.getBio().length()>0){
                                        userArrayList.add(user);
                                        pd.dismiss();
                                    }
                                }



                            }
                        }
                        if (userArrayList.size()==0){
                            readDefault();
                        }
                        userAdapter.notifyDataSetChanged();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                // Toast is created to display the
                // message using show() method.

                pd.show();
                dialog.dismiss();

            }
        });

    }



}