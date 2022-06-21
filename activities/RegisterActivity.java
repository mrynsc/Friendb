package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivityRegisterBinding;
import com.yesterselga.countrypicker.CountryPicker;
import com.yesterselga.countrypicker.CountryPickerListener;
import com.yesterselga.countrypicker.Theme;

import org.aviran.cookiebar2.CookieBar;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog pd;
    private String gender = "";
    private DatePickerDialog datePickerDialog;
    private String locale;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();


        initDatePicker();

        binding.datePickerButton.setText(getTodaysDate());


        binding.countryPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CountryPicker picker = CountryPicker.newInstance("Select Your Country", Theme.DARK);  // dialog title and theme

                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {

                        // Implement your code here
                        locale = name;


                        binding.countryPickerButton.setText(name);

                        picker.dismiss();
                    }
                });
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });


        binding.genderGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                if (position==0){
                    gender="Male";
                }else if (position==1){
                    gender="Female";
                }else{
                    gender="";

                }
            }
        });



        binding.datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });



        binding.goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
        //Toast.makeText(this, age, Toast.LENGTH_SHORT).show();


        binding.createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivity.this,R.style.CustomDialogLogin);
                pd.setMessage("Please wait");
                pd.setCancelable(false);
                pd.show();
                final String email=binding.emailEdit.getText().toString().trim();
                final String username=binding.usernameEdit.getText().toString().trim();
                final String password=binding.passwordEdit.getText().toString().trim();


                String timeStamp = new SimpleDateFormat("dd MM yyyy HH mm",Locale.ENGLISH).format(Calendar.getInstance().getTime());



//                      String locale = RegisterActivity.this.getResources().getConfiguration()
//                        .locale.getDisplayCountry(Locale.ENGLISH);


                String date = binding.datePickerButton.getText().toString();
                int birth = Integer.parseInt(date.substring(date.length()-4));

                int year = Calendar.getInstance().get(Calendar.YEAR);

                int age = year - birth;

                Query usernameQuery= FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference().child("Users")
                        .orderByChild("username").equalTo(username);
                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount()>0){
                            pd.dismiss();
                            CookieBar.build(RegisterActivity.this)
                                    .setTitle("Username already exists.")
                                    .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                    .show();
                            //Toast.makeText(RegisterActivity.this, "Username already exist", Toast.LENGTH_SHORT).show();
                        }else if (binding.usernameEdit.getText().toString().trim().length()>1
                        &&!gender.equals("")&&age>10&&binding.passwordEdit.getText().toString().trim().length()>5
                        &&binding.emailEdit.getText().toString().trim().length()>1
                        &&!binding.countryPickerButton.getText().toString().equals("Country")){
                            firebaseAuth.createUserWithEmailAndPassword(binding.emailEdit.getText().toString().trim(), binding.passwordEdit.getText().toString().trim())
                                    .addOnCompleteListener(RegisterActivity.this,(task)->{
                                        if (!task.isSuccessful()){
                                            pd.dismiss();
                                            Toast.makeText(RegisterActivity.this,"Error",Toast.LENGTH_SHORT).show();
                                        } else{

                                            String user_id =firebaseAuth.getCurrentUser().getUid();
                                            final DatabaseReference databaseReference=FirebaseDatabase
                                                    .getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                                                    .getReference()
                                                    .child("Users").child(user_id);


                                            Map hashMap =new HashMap();
                                            hashMap.put("username",username.trim());
                                            hashMap.put("usernameLower",username.toLowerCase().trim());
                                            //newPost.put("password",password);
                                            hashMap.put("email",email.trim());
                                            hashMap.put("bio","");
                                            hashMap.put("status", "offline");
                                            hashMap.put("userId",user_id);
                                            hashMap.put("gender",gender);
                                            hashMap.put("registerDate","" + timeStamp);
                                            hashMap.put("country", locale);
                                            hashMap.put("mbti","");
                                            hashMap.put("birthDate",binding.datePickerButton.getText().toString());
                                            hashMap.put("age",age);
                                            hashMap.put("accountStatus","active");
                                            hashMap.put("dmPrefer","open");
                                            hashMap.put("seenPrefer","show");
                                            hashMap.put("isUsernameChanged","no");



                                            if (gender.equals("Male")){
                                                hashMap.put("avatar","maleDefault");
                                            }else if (gender.equals("Female")){
                                                hashMap.put("avatar","femaleDefault");
                                            }




                                            databaseReference.setValue(hashMap);
                                            pd.dismiss();
                                            Intent intent=new Intent(RegisterActivity.this,StartActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    });
                        }else{
                            CookieBar.build(RegisterActivity.this)
                                    .setTitle("Please fill in all fields!")
                                    .setMessage("Username\nEmail\nPassword\nGender\nCountry\nBirth Date (Make sure you don't enter the date of the last 10 years.)")
                                    .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                    .show();
                            //Toast.makeText(RegisterActivity.this,"Please fill in each field!",Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                binding.datePickerButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }



}