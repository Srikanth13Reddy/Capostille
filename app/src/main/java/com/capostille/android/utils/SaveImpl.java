
package com.capostille.android.utils;


import android.os.Handler;
import android.util.Log;

import com.capostille.android.interfaces.SavePresenter;
import com.capostille.android.interfaces.SaveView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;

public class SaveImpl implements SavePresenter {
    private SaveView loginView;
    private String res;

    public SaveImpl(SaveView loginView) {
        this.loginView = loginView;


    }

    @Override
    public void handleSave(JSONObject jsonObject, String connectionId, String type,String typee,String auth)
    {

        onRegister(jsonObject, connectionId, type, typee,auth);
    }

    private void onRegister(JSONObject jsonObject, String connectionId, String type,String token,String auth) {

        OkHttpClient myOkHttpClient = new OkHttpClient.Builder()
                .build();
        MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request;
        if (type.equalsIgnoreCase("POST")) {
            request = new Request.Builder()
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", "Bearer "+auth)
                    .post(body)
                    .url(ApiConstants.BaseUrl + connectionId)
                    .build();
        } else if (type.equalsIgnoreCase("GET")) {
            request = new Request.Builder()
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", "Bearer "+auth)
                    .get()
                    .url(ApiConstants.BaseUrl + connectionId)
                    .build();
        } else {
            request = new Request.Builder()
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", "Bearer "+auth)
                    .put(body)
                    .url(ApiConstants.BaseUrl + connectionId)
                    .build();
        }


        Callback updateUICallback = new Callback() {
            @Override
            public void onResponse( Call call,  Response response) throws IOException {
                res = Objects.requireNonNull(response.body()).string();
                if (response.isSuccessful() && response.code() == 200) {

                    Log.e("Tag", "Successfully authenticated");
                    // Toast.makeText(LoginActivity.this, ""+res, Toast.LENGTH_SHORT).show();
                    looper("Success",token);

                } else { //called if the credentials are incorrect
                    Log.d("Tag", "Registration failed " + response.networkResponse());
                    looper(""+response.code(),token);

                }
            }

            @Override
            public void onFailure( Call call,  IOException e) {
                looper("Fail",token);
            }


        };

        myOkHttpClient.newCall(request).enqueue(updateUICallback);
    }

    private void looper(final String message,String type) {
        Handler handler = new Handler(getMainLooper());
        handler.post(() -> {
            //progressDialog.dismiss();
            if (message.equalsIgnoreCase("Success")) {
                loginView.onSaveSucess("200", res,type);
            } else if (message.equalsIgnoreCase("Fail")) {
                loginView.onSaveFailure("Something Went Wrong");
            } else  {
                loginView.onSaveSucess(message, res,type);
            }
        });
    }

    private void logOut(final String message,String type) {
        Handler handler = new Handler(getMainLooper());
        handler.post(() -> {
            //progressDialog.dismiss();
            if (message.equalsIgnoreCase("Success")) {
                loginView.onSaveSucess("200", res,type);
            } else if (message.equalsIgnoreCase("Fail")) {
                loginView.onSaveFailure("Something Went Wrong");
            } else  {
                loginView.onSaveSucess(message, res,type);
            }
        });
    }
}





