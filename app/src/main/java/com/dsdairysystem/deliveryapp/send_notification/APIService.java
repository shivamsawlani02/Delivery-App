package com.dsdairysystem.deliveryapp.send_notification;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAjBDB0tk:APA91bEmhXKaUQZSiAbQqpF_0CuL74EnVtyRMcqP6GIhkdWzE2Z-60yVsBmAA5v_ntAzpv-ttzVRu5COAaeoH5xMAmXt9MKcqzPOWll9njIwCLpsfLdICsmfdOjU5qXKRKVdfGALtA6c"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender data);
}
