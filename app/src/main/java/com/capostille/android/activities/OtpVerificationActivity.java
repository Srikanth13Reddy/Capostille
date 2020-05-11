package com.capostille.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.capostille.android.R;
import com.capostille.android.interfaces.SaveView;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.MySingleton;
import com.capostille.android.utils.SaveImpl;
import com.capostille.android.utils.SharedPrefs;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OtpVerificationActivity extends AppCompatActivity implements SaveView
{

    AppCompatTextView tv_text;
    Button btn_verify;
    private OtpView otpView;
    String email;
    ProgressDialog progressDialog;
    private String token,id;
    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        getSupportActionBar().setTitle("OTP verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getLoginData();
        Bundle b= getIntent().getExtras();
        if (b != null) {
            email= b.getString("email");
        }
        otpView = findViewById(R.id.otp_view);
        tv_text= findViewById(R.id.tv_text);
        btn_verify= findViewById(R.id.btn_verify);
        tv_text.setText(Html.fromHtml("Enter the OTP sent to <b><font color=\"black\">"+email+"</font></b>"));
        btn_verify.setText("VERIFY & PROCEED");
        progressDialog= ApiConstants.showProgressDialog(this,"Please wait...");
        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                verifyotp(email,otpView.getText().toString());
            }
        });
    }

    private void verifyotp(String email,String otp)
    {
        progressDialog.show();
        JSONObject js=new JSONObject();
        try {
            js.put("email",email);
            js.put("otp",otp);
            js.put("type","saasuser");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody=js.toString();

        StringRequest stringRequest= new StringRequest(Request.Method.POST, ApiConstants.BaseUrl + "auth/verifyotp", response -> {
            progressDialog.dismiss();
            Log.e("VerificationResponse",response);
//            {"status":"Error","message":"Invalid Otp"}
//            {"status":"Success","message":"Valid Otp"}

            try {
                JSONObject jss=new JSONObject(response);
                if (jss.optString("status").equalsIgnoreCase("Success"))
                {
                    //Toast.makeText(this, ""+jss.optString("message"), Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(OtpVerificationActivity.this,UpdatePasswordActivity.class);
                    i.putExtra("email",email);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
                else {
                    Toast.makeText(this, ""+jss.optString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }





        }, error -> {
            ApiConstants.parseVolleyError(OtpVerificationActivity.this,error);
            progressDialog.dismiss();
            Log.e("Response",""+error);
        })


        {
            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
            @Override
            public String getBodyContentType()
            {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String,String> params=new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getLoginData()
    {
        sharedPrefs=new SharedPrefs(this);
        try {
            if (sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA)!=null)
            {
                JSONObject js=new JSONObject(Objects.requireNonNull(sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA)));
                id= js.optString("id");
                token= js.optString("token");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void verifyOTP(View view)
    {
        if (otpView.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
        }
        else {
            verifyotp(email,otpView.getText().toString());
        }
    }

    public void resendOTP(View view)
    {
        progressDialog.show();
        JSONObject js=new JSONObject();
        try {
            js.put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SaveImpl(this).handleSave(js,"auth/sendotp","POST","",token);
    }

    @Override
    public void onSaveSucess(String code, String response, String type)
    {
        progressDialog.dismiss();
        Log.e("OTPRES",response);
        if (code.equalsIgnoreCase("200"))
        {
            //  {"data":"OTP successfully send"}
            try {
                JSONObject js=new JSONObject(response);
                String data= js.optString("data");
                Toast.makeText(this, "OTP sent successfully", Toast.LENGTH_SHORT).show();;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onSaveFailure(String error) {
        progressDialog.dismiss();
        Log.e("OTPRES",error);
        Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
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

}
