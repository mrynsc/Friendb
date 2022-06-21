package com.yeslabapps.friendb.notify;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {

                    "Content-Type:application/json",
                    "Authorization:key=AAAAZ2qTSPg:APA91bE3yqzj2EGdLwVy-wx3IaG9apGVO9iCj3IG3r1ZP6gib6eMnY_abcSkwdOeBSIMRuFOo-VTcEeNmzUjy1nY8Lbtcs7dMhGYS7PC3gT7Ohms1es1DlewhitF4dPPyeSkkjTlxWJi"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}