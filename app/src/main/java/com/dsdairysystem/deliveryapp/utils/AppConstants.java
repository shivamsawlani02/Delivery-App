package com.dsdairysystem.deliveryapp.utils;

import com.google.firebase.firestore.BuildConfig;

public class AppConstants {

    public static final String BASE_URL = "https://fcm.googleapis.com";

    /**
     * Add Server Key from Firebase console
     * <p>
     * Project setting -> Cloud messaging -> Server Key
     */
    public static final String SERVER_KEY = "AAAAjBDB0tk:APA91bEmhXKaUQZSiAbQqpF_0CuL74EnVtyRMcqP6GIhkdWzE2Z-60yVsBmAA5v_ntAzpv-ttzVRu5COAaeoH5xMAmXt9MKcqzPOWll9njIwCLpsfLdICsmfdOjU5qXKRKVdfGALtA6c";

    public static final String PROJECT_ID = BuildConfig.APPLICATION_ID;

}
