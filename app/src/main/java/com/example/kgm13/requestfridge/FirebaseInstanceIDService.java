package com.example.kgm13.requestfridge;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by kgm13 on 2017-05-25.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    String refreshedToken;

    FirebaseInstanceIDService() {
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public void onTokenRefresh() {
        try {
            refreshedToken = FirebaseInstanceId.getInstance().getToken();
            if (refreshedToken != null) {
                Log.i("FCM", String.format("Received new registration token from Firebase: token=\"%s\";", refreshedToken));
                // ... forward the new token to some point in your app to store it
            }
            sendRegistrationToServer(refreshedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .build();

        //request
        Request request = new Request.Builder()
                .url("http://13.124.64.178/fcm/register.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}