package com.capostille.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.capostille.android.R;
import com.capostille.android.interfaces.SaveView;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.SaveImpl;
import com.capostille.android.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class UpdatePasswordActivity extends AppCompatActivity implements SaveView
{
    CheckBox act_old_ps_check,act_new_ps_check,act_confirm_ps_check;
    AppCompatEditText et_new_pass,et_confirm_pass,act_phone;
    SharedPrefs sharedPrefs;
    String id,token;
    String email;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        progressDialog= ApiConstants.showProgressDialog(this,"Please wait...");
        getSupportActionBar().setTitle("Update Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       Bundle b= getIntent().getExtras();
        if (b != null) {
          email=  b.getString("email");
        }
        findViews();
        getLoginData();
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
    private void findViews()
    {

        et_new_pass=  findViewById(R.id.act_new_ps);
        et_confirm_pass=  findViewById(R.id.act_confirm_ps);
        act_new_ps_check=  findViewById(R.id.act_new_ps_check);
        act_confirm_ps_check=  findViewById(R.id.act_confirm_ps_check);


        et_new_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                act_new_ps_check.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_confirm_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                act_confirm_ps_check.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        act_new_ps_check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                et_new_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                et_new_pass.setSelection(et_new_pass.length());
            } else {
                et_new_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                et_new_pass.setSelection(et_new_pass.length());
            }
        });

        act_confirm_ps_check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                et_confirm_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                et_confirm_pass.setSelection(et_confirm_pass.length());
            } else {
                et_confirm_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                et_confirm_pass.setSelection(et_confirm_pass.length());
            }
        });





    }
    public void savePass(View view)
    {
       if (et_new_pass.getText().toString().isEmpty())
        {
            // Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show();
            act_new_ps_check.setVisibility(View.GONE);
            et_new_pass.setError("Please enter new password");
            et_new_pass.requestFocus();
        }

        else if(et_new_pass.getText().toString().length()<6){
            act_new_ps_check.setVisibility(View.GONE);
            et_new_pass.setError("Password requires  minimum 6 characters");
            et_new_pass.requestFocus();
            // Toast.makeText(getApplicationContext(), "Password requires  minimum 5 characters" , Toast.LENGTH_SHORT).show();
            //Snackbar.make(root," Enter Your Paasword!!!",Snackbar.LENGTH_SHORT).show();
        }
        else if(et_confirm_pass.getText().toString().isEmpty()){
            act_confirm_ps_check.setVisibility(View.GONE);
            et_confirm_pass.setError("Please enter confirm password");
            et_confirm_pass.requestFocus();
            // Toast.makeText(getApplicationContext(), "Password enter confirm password" , Toast.LENGTH_SHORT).show();
            // Snackbar.make(root," Enter Confirm Password  match!!!",Snackbar.LENGTH_SHORT).show();
        }
        else if( !et_confirm_pass.getText().toString().equals(et_new_pass.getText().toString())){
            // Toast.makeText(getApplicationContext(), "Confirm Password mismatch" , Toast.LENGTH_SHORT).show();
            act_confirm_ps_check.setVisibility(View.GONE);
            et_confirm_pass.setError("Password mismatch");
            et_confirm_pass.requestFocus();
            //Snackbar.make(root," Confirm Password not match!!!",Snackbar.LENGTH_SHORT).show();
        }
        else {
            updatePassword(email,et_confirm_pass.getText().toString());
        }
    }

    private void updatePassword(String email, String pass)
    {
        progressDialog.show();
        JSONObject js=new JSONObject();
        try {
            js.put("email",email);
            js.put("password",pass);
            js.put("type","saasuser");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SaveImpl(this).handleSave(js,"auth/updatepassword","POST","",token);
    }

    @Override
    public void onSaveSucess(String code, String response, String type)
    {
        progressDialog.dismiss();
        Log.e("PassRes",response);
        try {
            JSONObject js=new JSONObject(response);
            String status=js.optString("status");
            if (status.equalsIgnoreCase("Success"))
            {
               finish();
                Intent i=new Intent(UpdatePasswordActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
            else{
                Toast.makeText(this, ""+status, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveFailure(String error) {
        progressDialog.dismiss();
        Log.e("PassRes",error);
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
