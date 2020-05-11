package com.capostille.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capostille.android.R;
import com.capostille.android.adapters.CompleteAdapter;
import com.capostille.android.interfaces.SaveView;
import com.capostille.android.models.RequestModel;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.SaveImpl;
import com.capostille.android.utils.SharedPrefs;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CompleteActivity extends AppCompatActivity implements SaveView
{
    
    RecyclerView rv_complete;
    ProgressDialog progressDialog;
    SharedPrefs sharedPrefs;
    String id,token;
    ArrayList<RequestModel> al=new ArrayList<>();
    private SearchView searchView;
    AppCompatTextView tv_notFound,tv_com_notfound;
    private CompleteAdapter requestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        getSupportActionBar().setTitle("Completed Documents");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog= ApiConstants.showProgressDialog(this,"Please wait....");
        rv_complete = findViewById(R.id.rv_complete);
        tv_notFound = findViewById(R.id.tv_notFound);
        tv_com_notfound = findViewById(R.id.tv_com_notfound);
        rv_complete.setLayoutManager(new LinearLayoutManager(this));
        getLoginData();
        sharedPrefs.saveCompleteStatus("");
        //getData();
        getRequestData("");
    }

//    private void getData() {
//        rv_complete.setAdapter(new CompleteAdapter(this));
//    }

    private void getLoginData()
    {
        sharedPrefs=new SharedPrefs(this);
        try {
            if (sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA)!=null)
            {
                JSONObject js=new JSONObject(sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA));
                id= js.optString("id");
                token= js.optString("token");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRequestData(String type)
    {
        progressDialog.show();
        new SaveImpl(this).handleSave(new JSONObject(),"requests?saasUserId="+id,"GET",type,token);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        else if (item.getItemId()== R.id.notification_filter)
        {
            showBottomSheetDialog();
        }
        return true;
    }

    public void showBottomSheetDialog()
    {
        LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.sort_style, null,false);
        RelativeLayout rv_name=view.findViewById(R.id.name_sort);
        RelativeLayout rv_status=view.findViewById(R.id.status_sort);
        rv_status.setVisibility(View.GONE);
        RadioButton rb_name=view.findViewById(R.id.rb_name);
        RadioButton rb_status=view.findViewById(R.id.tb_status);
        String status=  sharedPrefs.getCompleteStatus().get(SharedPrefs.STATUS_DATA_COMPLETE);
        if (status.equalsIgnoreCase(""))
        {
            rb_name.setChecked(false);
            rb_status.setChecked(false);
        }
        else if (status.equalsIgnoreCase("name"))
        {
            rb_name.setChecked(true);
            rb_status.setChecked(false);
        }
        else if (status.equalsIgnoreCase("status"))
        {
            rb_name.setChecked(false);
            rb_status.setChecked(true);
        }
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        rv_name.setOnClickListener(v -> {
            dialog.dismiss();
            sharedPrefs.saveCompleteStatus("name");
            getRequestData("name");
        });
        rv_status.setOnClickListener(v -> {
            dialog.dismiss();
            sharedPrefs.saveCompleteStatus("status");
            getRequestData("status");
        });
        rb_name.setOnClickListener(v -> {
            dialog.dismiss();
            sharedPrefs.saveCompleteStatus("name");
            getRequestData("name");
        });
        rb_status.setOnClickListener(v -> {
            dialog.dismiss();
            sharedPrefs.saveCompleteStatus("status");
            getRequestData("status");
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.notification_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.notification_search);
        MenuItem notification_filter = menu.findItem(R.id.notification_filter);
        notification_filter.setVisible(false);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search...");
        //  searchView.setOnQueryTextListener(this);
        searchView.setIconified(true);

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
    @Override
    public void onSaveFailure(String error) {
        progressDialog.dismiss();
        Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onSaveSucess(String code, String response,String type) {
        progressDialog.dismiss();
        Log.e("RequestRes",response);
        if (response.equalsIgnoreCase("{\"status\":\"Error\",\"message\":\"User is in Inactive\"}"))
        {
            ApiConstants.showSubscriptionDialog(this);
        }else {
            if (code.equalsIgnoreCase("200"))
            {
                assignData(response,type);
            }else if (code.equalsIgnoreCase("401"))
            {
                ApiConstants.logOut(this);
            }
        }
    }

    private void assignData(String response, String type)
    {
        al.clear();
        try {
             JSONObject jss=new JSONObject(response);
            JSONArray ja=jss.getJSONArray("data");
            if (jss.optString("status").equalsIgnoreCase("Success")) {
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject json = ja.getJSONObject(i);
                    String status_ = json.optString("status");
                    if (status_.equalsIgnoreCase("Completed")) {
                        String userRequestDetailsId = json.optString("userRequestDetailsId");
                        String requestedDate = json.optString("requestedDate");
                        String customerId = json.optString("customerId");
                        String status = json.optString("status");
                        String assignedTo = json.optString("assignedTo");
                        String documentsCount = json.optString("documentsCount");
                        String name = json.optString("name");
                        String requestCode = json.optString("requestCode");
                        String fullAddress = json.optString("fullAddress");
                        String assignedToName = json.optString("assignedToName");
                        RequestModel requestModel = new RequestModel();
                        requestModel.setName(name);
                        requestModel.setRequestCode(requestCode);
                        requestModel.setAssignedTo(assignedTo);
                        requestModel.setUserRequestDetailsId(userRequestDetailsId);
                        requestModel.setAssignedToName(assignedToName);
                        requestModel.setCustomerId(customerId);
                        requestModel.setRequestedDate(requestedDate);
                        requestModel.setStatus(status);
                        requestModel.setFullAddress(fullAddress);
                        requestModel.setDocumentsCount(documentsCount);
                        al.add(requestModel);
                    }


                }
                if (al.size() == 0) {
                    tv_com_notfound.setVisibility(View.VISIBLE);
                } else {
                    tv_com_notfound.setVisibility(View.GONE);
                }

                requestAdapter = new CompleteAdapter(this, al, tv_notFound, tv_com_notfound);
                rv_complete.setAdapter(requestAdapter);


                // requestAdapter=  new CompleteAdapter(this,al,tv_notFound);
                rv_complete.setAdapter(requestAdapter);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        String text = s;
                        requestAdapter.filter(text);
                        return false;
                    }
                });
            }
            else {
                Toast.makeText(this, ""+jss.optString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        //getRequestData( sharedPrefs.getCompleteStatus().get(SharedPrefs.STATUS_DATA_COMPLETE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111)
        {
           // Toast.makeText(this, "Hiii", Toast.LENGTH_SHORT).show();
            if(resultCode == Activity.RESULT_OK)
            {
                getRequestData("");
                //Toast.makeText(this, "Hiii", Toast.LENGTH_SHORT).show();
            }
        }
    }
}