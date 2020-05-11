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

import com.capostille.android.R;
import com.capostille.android.adapters.NotaryListAdapter;
import com.capostille.android.interfaces.SaveView;
import com.capostille.android.models.NotaryListModel;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.SaveImpl;
import com.capostille.android.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotariesListActivity extends AppCompatActivity implements SaveView
{
    SharedPrefs sharedPrefs;
    String id,agencyId;
    ProgressDialog progressDialog;
    RecyclerView rv_notary;
    String rId,assignedTo;
    private String token;
    AppCompatTextView tv_not_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notaries_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notary");
        Bundle b= getIntent().getExtras();
        if (b != null) {
            rId= b.getString("rId");
            assignedTo= b.getString("assignedTo");
        }
        rv_notary=findViewById(R.id.rv_notary);
        tv_not_found=findViewById(R.id.tv_not_found);
        rv_notary.setLayoutManager(new LinearLayoutManager(this));
        progressDialog= ApiConstants.showProgressDialog(this,"Please wait....");
        getLoginData();
        getData();
    }

    private void getData()
    {
        progressDialog.show();
        new SaveImpl(this).handleSave(new JSONObject(),"agencynotaries?agencyId="+agencyId,"GET","",token);
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
                agencyId= js.optString("agencyId");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveSucess(String code, String response, String type)
    {
       progressDialog.dismiss();
       if (type.equalsIgnoreCase("Assign"))
       {
           Log.e("ResponseStatus",response);
           try {
               JSONObject js1 =new JSONObject(response);
               String status= js1.optString("status");
               if (status.equalsIgnoreCase("Success"))
               {
                   Toast.makeText(this, ""+js1.optString("message"), Toast.LENGTH_SHORT).show();
                   finish();
               }
               else {
                   Toast.makeText(this, ""+js1.optString("message"), Toast.LENGTH_SHORT).show();
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }else {
           Log.e("Noaries",response);
           if (code.equalsIgnoreCase("200"))
           {
               assignData(response);
           }else if (code.equalsIgnoreCase("401"))
           {
               ApiConstants.logOut(this);
           }
       }

    }

    private void assignData(String response)
    {
        ArrayList<NotaryListModel> arrayList=new ArrayList<>();
        try {
            JSONObject js=new JSONObject(response);
            if (js.optString("status").equalsIgnoreCase("Success"))
            {
                JSONArray ja =js.getJSONArray("data");
                for (int i = 0; i < ja.length(); i++) {
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
                    if (!saasUserId.equalsIgnoreCase(assignedTo))
                    {
                        NotaryListModel notaryListModel = new NotaryListModel();
                        notaryListModel.setSaasUserId(saasUserId);
                        notaryListModel.setRoleId(roleId);
                        notaryListModel.setProfileImage(profileImage);
                        notaryListModel.setEmail(email);
                        notaryListModel.setPhoneNumber(phoneNumber);
                        notaryListModel.setName(name);
                        notaryListModel.setRoleName(roleName);
                        notaryListModel.setStateName(stateName);
                        notaryListModel.setCountryName(countryName);
                        arrayList.add(notaryListModel);
                    }


                }
                if (arrayList.size()==0)
                {
                    tv_not_found.setVisibility(View.VISIBLE);
                }else {
                    tv_not_found.setVisibility(View.GONE);
                }
                NotaryListAdapter notaryListAdapter = new NotaryListAdapter(arrayList, this, rId, id, token,progressDialog);
                rv_notary.setAdapter(notaryListAdapter);
            }
            else {
                Toast.makeText(this, ""+js.optString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveFailure(String error) {
        progressDialog.dismiss();
        Log.e("Noaries",error);
        Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
