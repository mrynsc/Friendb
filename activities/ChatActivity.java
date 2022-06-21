package com.yeslabapps.friendb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yeslabapps.friendb.R;
import com.yeslabapps.friendb.adapters.ChatAdapter;
import com.yeslabapps.friendb.databinding.ActivityChatBinding;
import com.yeslabapps.friendb.databinding.ActivityProfileBinding;
import com.yeslabapps.friendb.model.Chat;
import com.yeslabapps.friendb.model.User;
import com.yeslabapps.friendb.notify.Data;
import com.yeslabapps.friendb.notify.Sender;
import com.yeslabapps.friendb.notify.Token;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private Intent intent;

    private ValueEventListener seenListener;

    private String userId;
    private boolean notify = false;
    private static final String TAG = "ChatActivity";


    private DatabaseReference userRefForSeen;

    private boolean isBlocked=false;
    private FirebaseAuth firebaseAuth;


    private String mUid;

    private RequestQueue requestQueue;

    private String userStatus;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }

        });

        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        requestQueue = Volley.newRequestQueue(ChatActivity.this);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        userId = intent.getStringExtra("chatUserId");

        binding.blockIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog ad =  new AlertDialog.Builder(ChatActivity.this, R.style.CustomDialogFilter).create();
                if (isBlocked){
                    ad.setTitle("Are you sure you want to unblock "+ binding.username.getText().toString() + "?");
                }else{
                    ad.setTitle("Are you sure you want to block "+ binding.username.getText().toString() + "?");
                }
                ad.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                ad.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isBlocked){
                            unBlockUser();
                        }else{
                            blockUser();
                        }
                        ad.dismiss();
                        finish();

                    }

                });

                ad.show();

            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("BlockedUsers");
                ref.child(userId).orderByChild("uid").equalTo(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            if (dataSnapshot.exists()){
                                CookieBar.build(ChatActivity.this)
                                        .setTitle("You're blocked by " + binding.username.getText().toString())
                                        .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                        .show();
                                //StyleableToast.makeText(ChatActivity.this, "You're blocked by " + username.getText().toString() + " :(", R.style.customToast).show();
                                return;
                            }
                        }
                        notify = true;
                        String msg = binding.chatEditText.getText().toString();
                        String time = String.valueOf(System.currentTimeMillis());
                        if (msg.trim().length()>0){
                            sendMessage(firebaseUser.getUid(),userId,msg,time);
                        }
                        binding.chatEditText.setText("");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                binding.username.setText(user.getUsername());

                if (user.getAvatar().equals("maleDefault")){
                    binding.profileImage.setImageResource(R.drawable.cartoonify1);
                }else if (user.getAvatar().equals("femaleDefault")){
                    binding.profileImage.setImageResource(R.drawable.cartoonify2);
                }else{
                    Picasso.get().load(user.getAvatar()).into(binding.profileImage);
                }


                readMessages(firebaseUser.getUid(),userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        seenMessage();

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                //intent.putExtra("name", user.getUsername());
                intent.putExtra("someUserId",userId);
                startActivity(intent);
            }
        });

        binding.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                //intent.putExtra("name", user.getUsername());
                intent.putExtra("someUserId",userId);
                startActivity(intent);
            }
        });

        checkIsBlock();
        checkUserStatus();

        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);

                userStatus=user.getStatus();

                if (user.getStatus().equals("online")){
                    binding.statusText.setText("Online");
                    binding.statusText.setTextColor(Color.GREEN);
                }else{
                    binding.statusText.setText("Offline");
                    binding.statusText.setTextColor(Color.RED);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void checkIsBlock(){
        DatabaseReference ref= FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("BlockedUsers");
        ref.child(firebaseUser.getUid()).orderByChild("uid").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            if (ds.exists()){
                                binding.blockIv.setImageResource(R.drawable.person_24);
                                isBlocked=true;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void blockUser( ) {

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("uid",userId);
        hashMap.put("myId",firebaseUser.getUid());



        DatabaseReference ref= FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("BlockedUsers");
        ref.child(firebaseUser.getUid()).child(userId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        /*CookieBar.build(ChatActivity.this)
                                .setTitle("Blocked Successfully!")
                                .setMessage("This user cannot message you until you unblock them.")
                                .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                .show();*/
                        StyleableToast.makeText(ChatActivity.this, "Blocked Successfully.This user cannot message you until you unblock them.", R.style.chatToast).show();
                        binding.blockIv.setImageResource(R.drawable.ic_baseline_person_off_24);


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void unBlockUser(){
        DatabaseReference ref= FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("BlockedUsers");
        ref.child(firebaseUser.getUid()).orderByChild("uid").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            if (ds.exists()){
                                ds.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                /*CookieBar.build(ChatActivity.this)
                                                        .setTitle("Unblocked Successfully!")
                                                        .setMessage("You can continue chatting with this user.")
                                                        .setCookiePosition(CookieBar.TOP)  // Cookie will be displayed at the bottom
                                                        .show();*/
                                                StyleableToast.makeText(ChatActivity.this, "Unblocked Successfully. You can continue chatting with this user.", R.style.chatToast).show();
                                                binding.blockIv.setImageResource(R.drawable.person_24);

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ChatActivity.this, "Failed.."+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void seenMessage(){
        userRefForSeen = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage(String sender, final String receiver, String message, String time){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Chats");

        String chatId= databaseReference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("time", time);
        hashMap.put("chatId",chatId);
        hashMap.put("isDeleted","no");



        databaseReference.child(chatId).setValue(hashMap);



//        HashMap<String, Object> map = new HashMap<>();
//
//        map.put("id",userId);
//        map.put("timeList",timeList);
//
//        FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
//                .getReference().child("Chatlist").child(firebaseUser.getUid()).child(userId).updateChildren(map);
//

        // add user to chat fragment
        DatabaseReference chatRef = FirebaseDatabase
                .getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userId);

        String timeList = String.valueOf(System.currentTimeMillis());

        chatRef.child("id").setValue(userId);
        chatRef.child("timeList").setValue(timeList);

        /*chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        DatabaseReference chatRefReceiver = FirebaseDatabase
                .getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Chatlist")
                .child(userId)
                .child(firebaseUser.getUid());

        chatRefReceiver.child("id").setValue(firebaseUser.getUid());
        chatRefReceiver.child("timeList").setValue(timeList);

        final String msg = message;

        reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (notify && userStatus.equals("offline")) {
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(final String receiver, final String username, final String message) {

        DatabaseReference allTokens = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(
                            "" + firebaseUser.getUid(),
                            username + ": " +  message,
                            "New Message From "+ username,
                            "" + userId,
                            "ChatNotification",
                            R.drawable.logotra);

                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSON_RESPONSE", "onResponse: " + response.toString());

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse: " + error.toString());


                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {

                                Map<String, String> header = new HashMap<>();
                                header.put("Content-Type", "application/json");
                                header.put("Authorization", "key=AAAAZ2qTSPg:APA91bE3yqzj2EGdLwVy-wx3IaG9apGVO9iCj3IG3r1ZP6gib6eMnY_abcSkwdOeBSIMRuFOo-VTcEeNmzUjy1nY8Lbtcs7dMhGYS7PC3gT7Ohms1es1DlewhitF4dPPyeSkkjTlxWJi");


                                return header;
                            }
                        };

                        requestQueue.add(jsonObjectRequest);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void readMessages(final String myId, final String userid){
        chatList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myId)){
                        chatList.add(chat);
                    }

                    chatAdapter = new ChatAdapter(ChatActivity.this, chatList);
                    binding.recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUser(String userId){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userId);
        editor.apply();
    }
    private void checkUserStatus(){

        FirebaseUser user=firebaseAuth.getCurrentUser();

        if (user!=null)
        {
            mUid=user.getUid();

            SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("Current_USERID",mUid);
            editor.apply();

            updateToken(FirebaseInstanceId.getInstance().getToken());
        }
    }

    private void updateToken(String token){

        DatabaseReference ref= FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Tokens");
        Token mToken=new Token(token);
        ref.child(mUid).setValue(mToken);
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance("https://friendb-76be3-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();

        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

}