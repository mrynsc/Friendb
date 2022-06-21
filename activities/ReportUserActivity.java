package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.databinding.ActivityReportUserBinding;
import com.yeslabapps.friendb.model.User;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import io.github.muddz.styleabletoast.StyleableToast;

public class ReportUserActivity extends AppCompatActivity {

    private ActivityReportUserBinding binding;
    private DatabaseReference refForReport;
    private String reportUserName;

    private FirebaseUser fUser;
    private String myUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityReportUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fUser= FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(binding.toolbarReport);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        binding.toolbarReport.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }

        });

        Intent intent = getIntent();
        reportUserName = intent.getStringExtra("userNameReport");

        binding.reasonText.setText("Select your report reason for " + reportUserName);


        userInfo();

        binding.groupradio.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override

                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    }
                });


        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialDialog materialDialog = new MaterialDialog.Builder(ReportUserActivity.this)
                        .setTitle("Report")
                        .setMessage("Are you sure you want to report "+ reportUserName)
                        .setCancelable(true)
                        .setPositiveButton("Yes", R.drawable.ic_baseline_send_24, new AbstractDialog.OnClickListener() {
                            @Override
                            public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                reportUser();
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

        });


    }

    private void reportUser() {
        int selectedId = binding.groupradio.getCheckedRadioButtonId();
        if (selectedId == -1) {
            StyleableToast.makeText(ReportUserActivity.this,
                    "No reason has been selected",
                    R.style.customToast)
                    .show();
        } else {

            RadioButton radioButton = binding.groupradio.findViewById(selectedId);
            refForReport = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference().child("UserReports");
            refForReport.push().setValue(""+myUserName+" reported "
                    +reportUserName +" "+ radioButton.getText() + " / " + binding.reportDetailsEt.getText().toString().trim());
            StyleableToast.makeText(ReportUserActivity.this, "Thanks for providing feedback!", R.style.customToast).show();
            finish();
            //Intent intent1 = new Intent(ReportUserActivity.this, StartActivity.class);
            //startActivity(intent1);
        }

    }

    private void userInfo(){
        FirebaseDatabase.getInstance("https://messageapp-f43a7-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);


                myUserName=user.getUsername();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
