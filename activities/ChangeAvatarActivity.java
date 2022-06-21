package com.yeslabapps.friendb.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseUser;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.adapters.AvatarAdapter;
import com.yeslabapps.friendb.databinding.ActivityChangeAvatarBinding;
import com.yeslabapps.friendb.model.Avatar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class ChangeAvatarActivity extends AppCompatActivity {

    private ActivityChangeAvatarBinding binding;
    private FirebaseUser fUser;
    private AvatarAdapter avatarAdapter;
    private ArrayList<Avatar> avatarArrayList;
    private RequestQueue requestQueue;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeAvatarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.avatarRecycler.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(ChangeAvatarActivity.this,2);
        binding.avatarRecycler.setLayoutManager(layoutManager);
        avatarArrayList=new ArrayList<>();


        pd= new ProgressDialog(ChangeAvatarActivity.this,R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();

        requestQueue = Volley.newRequestQueue(ChangeAvatarActivity.this);


        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }

        });

        binding.filterAvatars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangeAvatarActivity.this,R.style.CustomDialogFilter);
                builder.setTitle("Sort");
                builder.setIcon(R.drawable.ic_baseline_filter_list_24);
                String[] listPrefs = {"Male","Female"};
                builder.setItems(listPrefs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:readMaleAvatars();
                                break;
                            case 1:readFemaleAvatars();
                                break;


                        }

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog customAlertDialog = builder.create();
                customAlertDialog.show();
            }
        });

        parseJSON();


    }
    private void readMaleAvatars(){
        avatarArrayList.clear();

        String url = "https://api.npoint.io/6db75e46df685be01932";



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("content");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String imageUrl = jsonObject.getString("image");
                                String gender = jsonObject.getString("gender");

                                if (gender.equals("male")){
                                    avatarArrayList.add(new Avatar(imageUrl,gender));
                                }
                            }

                            //Collections.reverse(avatarArrayList);
                            avatarAdapter = new AvatarAdapter( avatarArrayList,ChangeAvatarActivity.this);
                            binding.avatarRecycler.setAdapter(avatarAdapter);
                            pd.dismiss();

                        } catch (JSONException e) {
                            pd.dismiss();
                            finish();
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    private void readFemaleAvatars(){
        avatarArrayList.clear();
        String url = "https://api.npoint.io/6db75e46df685be01932";


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("content");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String imageUrl = jsonObject.getString("image");
                                String gender = jsonObject.getString("gender");
                                if (gender.equals("female")){
                                    avatarArrayList.add(new Avatar(imageUrl,gender));
                                }
                            }

                            //Collections.reverse(avatarArrayList);
                            avatarAdapter = new AvatarAdapter( avatarArrayList,ChangeAvatarActivity.this);
                            binding.avatarRecycler.setAdapter(avatarAdapter);
                            pd.dismiss();

                        } catch (JSONException e) {
                            pd.dismiss();
                            finish();
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    private void parseJSON() {
        avatarArrayList.clear();

        String url = "https://api.npoint.io/6db75e46df685be01932";


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("content");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String imageUrl = jsonObject.getString("image");
                                String gender = jsonObject.getString("gender");

                                avatarArrayList.add(new Avatar(imageUrl,gender));
                            }

                            //Collections.reverse(avatarArrayList);
                            avatarAdapter = new AvatarAdapter( avatarArrayList,ChangeAvatarActivity.this);
                            binding.avatarRecycler.setAdapter(avatarAdapter);
                            pd.dismiss();

                        } catch (JSONException e) {
                            pd.dismiss();
                            finish();
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }
    /*
https://ibb.co/rcpmfLF
https://ibb.co/6sBSYZx
https://ibb.co/s6Tp8J4
https://ibb.co/Bnp6yjD
https://ibb.co/gFP32rL
https://ibb.co/x2BycQ4
https://ibb.co/WBMMkNK
https://ibb.co/Mg377f6
https://ibb.co/MCLRbfw
https://ibb.co/V3HqH0S
*/

}