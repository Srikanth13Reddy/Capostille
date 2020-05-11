package com.capostille.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.capostille.android.R;
import com.capostille.android.interfaces.SaveView;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.SaveImpl;
import com.capostille.android.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPassword extends AppCompatActivity implements SaveView
{
    AppCompatTextView tv_text;
    AppCompatEditText forgot_email_tv;
    SharedPrefs sharedPrefs;
    String id;
    ProgressDialog progressDialog;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        progressDialog= ApiConstants.showProgressDialog(this,"Please wait...");
        getSupportActionBar().setTitle("Forgot Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tv_text=findViewById(R.id.tv_text);
        forgot_email_tv=findViewById(R.id.forgot_email_tv);
        tv_text.setText(Html.fromHtml(getString(R.string.forgrt_text)));
        getLoginData();
    }

    private void getLoginData()
    {
        sharedPrefs=new SharedPrefs(this);
        try {
            if (sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA)!=null)
            {
                JSONObject js=new JSONObject(sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA));
                id= js.optString("id");
                token= js.optString("token");
                // agencyId= js.optString("agencyId");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return true;
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void getotp(View view)
    {
        if (forgot_email_tv.getText().toString().isEmpty())
        {
            forgot_email_tv.setError("Please enter email");
            forgot_email_tv.requestFocus();
        }
        else {
            sendOTP(forgot_email_tv.getText().toString());
        }
//        Intent i=new Intent(ForgotPassword.this,OtpVerificationActivity.class);
//        startActivity(i);
//        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void sendOTP(String email)
    {
       progressDialog.show();
       JSONObject js=new JSONObject();
        try {
            js.put("email",email);
            js.put("type","saasuser");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SaveImpl(this).handleSave(js,"auth/sendotp","POST","",token);
    }



    @Override
    public void onSaveSucess(String code, String response, String type)
    {
        progressDialog.dismiss();

//        {"status":"Error","message":"Invalid Email"}
//        {"status":"Success","message":"Otp successfully send"}

        try {
            JSONObject js=new JSONObject(response);
            if (js.optString("status").equalsIgnoreCase("Success"))
            {
                Toast.makeText(this, ""+js.optString("message"), Toast.LENGTH_SHORT).show();
                Intent i=new Intent(ForgotPassword.this, OtpVerificationActivity.class);
                i.putExtra("email",forgot_email_tv.getText().toString());
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
            else {
                Toast.makeText(this, ""+js.optString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        Log.e("OTPRES",response);

    }

    @Override
    public void onSaveFailure(String error) {
        progressDialog.dismiss();
        Log.e("OTPRES",error);
        Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
    }
}
