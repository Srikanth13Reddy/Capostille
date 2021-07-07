package com.capostille.android.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.capostille.android.R;
import com.capostille.android.adapters.NotaryMembersAdapter;
import com.capostille.android.models.NotaryListModel;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.MySingleton;
import com.capostille.android.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NotaryMembersActivity extends AppCompatActivity {

    SharedPrefs sharedPrefs;
    ProgressDialog progressDialog;
    String id,token,roleId,agencyId;
    AppCompatTextView tv_notary_notfound;
    RecyclerView rv_ntary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notary_members);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notary Members");
        findViews();
        getLoginData();


    }

    private void findViews()
    {
        progressDialog= ApiConstants.showProgressDialog(this,"Please wait...");
        rv_ntary= findViewById(R.id.rv_notarymember);
        tv_notary_notfound= findViewById(R.id.tv_notary_notfound);
        rv_ntary.setLayoutManager(new LinearLayoutManager(this));
    }

    void notaryListCount()
    {
        progressDialog.show();
         StringRequest stringRequest=new StringRequest(Request.Method.GET, ApiConstants.BaseUrl +"agencynotaries?agencyId="+agencyId, response -> {
            progressDialog.dismiss();
            assignData(response);
           Log.e("Res",response);

        }, error -> {
            progressDialog.dismiss();
            ApiConstants.parseVolleyError(NotaryMembersActivity.this,error);
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String,String> hm=new HashMap<>();
                hm.put("Authorization","Bearer "+token);
                return hm;
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
                roleId= js.optString("roleId");
                agencyId= js.optString("agencyId");
                notaryListCount();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void assignData(String response)
    {
        Log.e("NotariesList",response);
        ArrayList<NotaryListModel> arrayList=new ArrayList<>();
        try {
            JSONObject js=new JSONObject(response);
            if (js.optString("status").equalsIgnoreCase("Success")) {
                JSONArray ja = js.getJSONArray("data");
                if (ja.length() == 0) {
                    tv_notary_notfound.setVisibility(View.VISIBLE);
                } else {
                    tv_notary_notfound.setVisibility(View.GONE);
                }


                for (int i = 0; i < ja.length(); i++)
                {
                    JSONObject json = ja.getJSONObject(i);
                    String saasUserId = json.optString("saasUserId");
                    String roleId = json.optString("roleId");
                    String profileImage = json.optString("profileImage");
                    String email = json.optString("email");
                    String phoneNumber = json.optString("phoneNumber");
                    String name = json.optString("name");
                    String roleName = json.optString("roleName");
                    String stateName = json.optString("stateName");
                    String countryName = json.optString("countryName");
                    String fullAddress = json.optString("fullAddress");
                    String countryCode = json.optString("countryCode");
                    NotaryListModel notaryListModel = new NotaryListModel();
                    notaryListModel.setSaasUserId(saasUserId);
                    notaryListModel.setCountryCode(countryCode);
                    notaryListModel.setRoleId(roleId);
                    notaryListModel.setProfileImage(profileImage);
                    notaryListModel.setEmail(email);
                    notaryListModel.setPhoneNumber(phoneNumber);
                    notaryListModel.setName(name);
                    notaryListModel.setRoleName(roleName);
                    notaryListModel.setStateName(stateName);
                    notaryListModel.setCountryName(countryName);
                    notaryListModel.setFullAddress(fullAddress);
                    arrayList.add(notaryListModel);

                }
                NotaryMembersAdapter notaryListAdapter = new NotaryMembersAdapter(arrayList, this);
                rv_ntary.setAdapter(notaryListAdapter);
            }
            else {
                Toast.makeText(this, ""+js.optString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
