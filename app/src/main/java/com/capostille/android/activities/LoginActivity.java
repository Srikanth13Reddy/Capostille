package com.capostille.android.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.capostille.android.R;
import com.capostille.android.firebase.Config;
import com.capostille.android.interfaces.SaveView;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.SaveImpl;
import com.capostille.android.utils.SharedPrefs;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements SaveView
{

    ProgressDialog progressDialog;
    AppCompatEditText et_mail,et_pass;
    SharedPrefs sharedPrefs;
    CheckBox log_checkBox;
    public String refreshedToken;
    boolean doubleBackToExitPressedOnce=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        displayFirebaseRegId();
        getSupportActionBar().hide();
        progressDialog= ApiConstants.showProgressDialog(this,"Please wait....");
        findViews();
    }

    private void findViews()
    {
        sharedPrefs=new SharedPrefs(this);
        et_mail=findViewById(R.id.login_email_tv);
        et_pass=findViewById(R.id.logon_pass_tv);
        log_checkBox=findViewById(R.id.log_checkBox);
        log_checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (!isChecked) {
                et_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                et_pass.setSelection(et_pass.length());
            } else {
                et_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                et_pass.setSelection(et_pass.length());
            }
        });
        et_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                   log_checkBox.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private boolean isValidEmailId(String email)
    {

        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void login(View view)
    {

        if (et_mail.getText().toString().isEmpty())
        {
           // Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            et_mail.setError("Please enter email");
            et_mail.requestFocus();
        }
        else  if (!isValidEmailId(et_mail.getText().toString()))
        {
            // Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            et_mail.setError("Please enter valid email");
            et_mail.requestFocus();
        }
        else if (et_pass.getText().toString().isEmpty())
            {
               // Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                log_checkBox.setVisibility(View.GONE);
                et_pass.setError("Please enter password");
                et_pass.requestFocus();
            }
            else {
                hideKeyboard();
                //log_checkBox.setVisibility(View.VISIBLE);
                loginToNotary(et_mail.getText().toString().trim(),et_pass.getText().toString().trim());


        }



    }

    private void loginToNotary(String email, String pass)
    {
        progressDialog.show();
        JSONObject js=new JSONObject();
        try {
            js.put("email",email);
            js.put("password",pass);
            js.put("deviceType","android");
            js.put("deviceId",refreshedToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SaveImpl(this).handleSave(js,"auth/userlogin","POST","","");
    }

    @Override
    public void onSaveSucess(String code, String response,String type)
    {
        Log.e("DataLogin",response);
        progressDialog.dismiss();


//        try {
//            JSONObject js=new JSONObject(response);
//           String res= js.optString("status");
//           if (res.equalsIgnoreCase("Success"))
//           {
//
//           }else {
//               Toast.makeText(this, ""+js.optString("message"), Toast.LENGTH_SHORT).show();
//           }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }




            try {
                JSONObject js1=new JSONObject(response);
                String status= js1.optString("status");
                String message= js1.optString("message");
                if (status.equalsIgnoreCase("Success"))
                {
                    JSONObject js= js1.getJSONObject("data");
                    sharedPrefs.saveLoginData(js.toString());
                    String isActive= js.optString("status");
                    if (isActive.equalsIgnoreCase("inactive"))
                    {
                        ApiConstants.showSubscriptionDialog(this);

                    }else if (isActive.equalsIgnoreCase("trial"))
                    {
                       showtrailDialog();
                    }else {
                        sharedPrefs.LoginSuccess();
                        sharedPrefs.saveDeviceId(refreshedToken);
                        finish();
                        Intent i=new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
                }else {
                    Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



    }

    @Override
    public void onSaveFailure(String error) {
        progressDialog.dismiss();
        Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
    }

    public void forgotPassword(View view)
    {
        Intent i=new Intent(LoginActivity.this,ForgotPassword.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void displayFirebaseRegId()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("RegId",""+regId);
        // Toast.makeText(LoginActivity.this, " FIRRE BASE ID"+regId, Toast.LENGTH_SHORT).show();


        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("RegId",""+refreshedToken);

        if (refreshedToken!=null)
        {
            this.refreshedToken=refreshedToken;
        }
        else {
            this.refreshedToken=regId;
        }


    }

    void showtrailDialog()
    {
        AlertDialog.Builder alb=new AlertDialog.Builder(this).setTitle("Alert").setMessage("You are in 30 days trial period")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sharedPrefs.saveDeviceId(refreshedToken);
                        sharedPrefs.LoginSuccess();
                        finish();
                        Intent i=new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
                });
        alb.setCancelable(false);
        alb.create().show();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        this.finishAffinity();
//        if(doubleBackToExitPressedOnce){
//
//            moveTaskToBack(true);
//            this.finishAffinity();
//            return;
//        }
//        this.doubleBackToExitPressedOnce=true;
//        Toast.makeText(this,"Please Double click to exit app",Toast.LENGTH_LONG).show();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce=false;
//            }
//        },2000);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (imm != null) {
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
