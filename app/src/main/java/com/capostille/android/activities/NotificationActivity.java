package com.capostille.android.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.capostille.android.R;
import com.capostille.android.adapters.NotifictionAdapter;
import com.capostille.android.interfaces.SaveView;
import com.capostille.android.models.NotificationModel;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.MySingleton;
import com.capostille.android.utils.SaveImpl;
import com.capostille.android.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity implements SaveView
{

    RecyclerView rv_notification;
    SharedPrefs sharedPrefs;
    ProgressDialog progressDialog;
    String id,roleId;
    AppCompatTextView tv_noti_notfound;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();
        rv_notification=findViewById(R.id.rv_notification);
        tv_noti_notfound=findViewById(R.id.tv_noti_notfound);
       // rv_notification.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_notification.getContext(), llm.getOrientation());
        rv_notification.addItemDecoration(dividerItemDecoration);
        rv_notification.setLayoutManager(llm);

        progressDialog= ApiConstants.showProgressDialog(this,"Please wait....");
        getLoginData();
        getData();
    }

    private void getData()
    {
        progressDialog.show();
        new SaveImpl(this).handleSave(new JSONObject(),"saasusernotification?saasUserId="+id,"GET","",token);
        //rv_notification.setAdapter(new NotifictionAdapter(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void getLoginData()
    {
        sharedPrefs=new SharedPrefs(this);
        try {
            if (sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA)!=null)
            {
                JSONObject js=new JSONObject(sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA));
                id= js.optString("id");
                roleId= js.optString("roleId");
                token= js.optString("token");
               // agencyId= js.optString("agencyId");
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
        }else if (item.getItemId()== R.id.readAll)
        {
            AlertDialog.Builder alb=new AlertDialog.Builder(this);
            alb.setTitle("Confirmation");
            alb.setMessage("Do You Want to Read All ");
            alb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    readAll();
                }
            });
           alb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
               }
           });
           alb.create().show();

        }
        return true;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//       MenuInflater menuInflater= getMenuInflater();
//       menuInflater.inflate(R.menu.notification_menu,menu);
//        return super.onCreateOptionsMenu(menu);
//    }


    public void onBackPressed() {
        //super.onBackPressed();
        Intent returnIntent = new Intent(this,HomeActivity.class);
        returnIntent.putExtra("result","notificationResult");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void onSaveSucess(String code, String response, String type)
    {
        Log.e("ResNotification",response);
       progressDialog.dismiss();

           if (code.equalsIgnoreCase("200"))
           {
               assignData(response);
           }else if (code.equalsIgnoreCase("401"))
           {
               ApiConstants.logOut(this);
           }
           else {
               Toast.makeText(this, ""+response, Toast.LENGTH_SHORT).show();
           }


    }

    private void assignData(String response)
    {
        ArrayList<NotificationModel> arrayList=new ArrayList<>();
        try {
            JSONObject jss=new JSONObject(response);
            if (jss.optString("status").equalsIgnoreCase("Success")){
            JSONArray ja=jss.getJSONArray("data");
            if (ja.length()==0)
            {
                tv_noti_notfound.setVisibility(View.VISIBLE);
            }else {
                tv_noti_notfound.setVisibility(View.GONE);
            }
            for (int i=0;i<ja.length();i++)
            {
                JSONObject js= ja.getJSONObject(i);
                String notificationTrackId= js.optString("notificationTrackId");
                String sendDate= js.optString("sendDate");
                String requestId= js.optString("requestId");
                String content= js.optString("content");
                String notificationType= js.optString("notificationType");
                String status= js.optString("status");
                String requestStatus= js.optString("requestStatus");
                String assignedTo=js.optString("assignedTo");
                NotificationModel notificationModel=new NotificationModel();
                notificationModel.setContent(content);
                notificationModel.setNotificationTrackId(notificationTrackId);
                notificationModel.setSendDate(sendDate);
                notificationModel.setRequestId(requestId);
                notificationModel.setNotificationType(notificationType);
                notificationModel.setStatus(status);
                notificationModel.setRequestStatus(requestStatus);
                notificationModel.setAssignedTo(assignedTo);
                arrayList.add(notificationModel);
            }
            NotifictionAdapter notifictionAdapter=new NotifictionAdapter(this,arrayList,token,roleId,id);
            rv_notification.setAdapter(notifictionAdapter);
            }
            else {
                Toast.makeText(this, ""+jss.optString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveFailure(String error) {
        Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        Log.e("ResNotification",error);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getData();
    }

    void readAll()
    {
        progressDialog.show();
        @SuppressLint("SetTextI18n") StringRequest stringRequest=new StringRequest(Request.Method.PUT, ApiConstants.BaseUrl +"usernotificationreadall?saasUserId=" + id, response -> {
            progressDialog.dismiss();
            Log.e("RES",response);
            try {
                JSONObject js=new JSONObject(response);
                String status= js.optString("status");
                String message= js.optString("message");
                if (status.equalsIgnoreCase("Success"))
                {
                    getData();
                    Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            progressDialog.dismiss();
            ApiConstants.parseVolleyError(NotificationActivity.this,error);
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


    public void readAll(View view)
    {
        AlertDialog.Builder alb=new AlertDialog.Builder(this);
        alb.setTitle("Confirmation");
        alb.setMessage("Do You Want to Read All ");
        alb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                readAll();
            }
        });
        alb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alb.create().show();
    }

    public void back(View view) {
        onBackPressed();
    }
}
