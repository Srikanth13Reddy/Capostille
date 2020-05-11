package com.capostille.android.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.capostille.android.R;
import com.capostille.android.interfaces.SaveView;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.MySingleton;
import com.capostille.android.utils.SaveImpl;
import com.capostille.android.utils.SharedPrefs;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.capostille.android.utils.ApiConstants.toTitleCase;

public class HomeActivity extends AppCompatActivity implements SaveView
{

    Toolbar toolbar;
    int LAUNCH_SECOND_ACTIVITY = 1;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    SharedPrefs sharedPrefs;
    String name;
    AppCompatTextView tv_name;
    String id;
    ProgressDialog progressDialog;
    AppCompatTextView tv_count;
    private CircleImageView profile_image_nav;
    private boolean doubleBackToExitPressedOnce=false;
    AppCompatTextView tv_not_count;
    private String token;
    private String roleId;
    RelativeLayout rl_notary;
    AppCompatTextView tv_notary_count;
    private String agencyId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimaryDark));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
      //  toggle.setDrawerIndicatorEnabled(false);
//        toolbar.setNavigationIcon(null);
      //  setSupportActionBar(toolbar);
       // getSupportActionBar().setElevation(0);
        findViews();
        navHeader();
        getLoginData();
        getCount();
        getNotificationCount();
        getProfile();

//        getSupportActionBar().hide();
    }

    private void navHeader()
    {
        View headerLayout = navigationView.getHeaderView(0);
        ///  ListView lv=headerLayout.findViewById(R.id.lv_nav);
        // lv.setAdapter(new NavMenuAdapter(this));
        LinearLayout ll_profile=headerLayout.findViewById(R.id.ll_profile);
         profile_image_nav= headerLayout.findViewById(R.id.profile_image_nav);
        LinearLayout ll_hlogout=headerLayout.findViewById(R.id.ll_hlogout);
        AppCompatTextView tv_n_name=headerLayout.findViewById(R.id.tv_n_name);
        LinearLayout ll_home=headerLayout.findViewById(R.id.ll_home);
        LinearLayout ll_account=headerLayout.findViewById(R.id.ll_account);
        tv_n_name.setText(toTitleCase(name));
        ll_profile.setOnClickListener(v -> {
            Intent i=new Intent(HomeActivity.this, ProfileActivity.class);
            startActivityForResult(i,3);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });
        ll_home.setOnClickListener(v -> drawer.closeDrawers());
        ll_hlogout.setOnClickListener(v -> logOut());
        ll_account.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, AccountActivity.class);
            HomeActivity.this.startActivity(i);
            HomeActivity.this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });
    }

    private void getLoginData()
    {
        sharedPrefs=new SharedPrefs(this);
        Log.e("LoginResponse",sharedPrefs.getDeviceID().get(SharedPrefs.DEVICE_ID));
        try {
            if (sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA)!=null)
            {
                JSONObject js=new JSONObject(Objects.requireNonNull(sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA)));
                id= js.optString("id");
                token= js.optString("token");
                roleId= js.optString("roleId");
                agencyId= js.optString("agencyId");
               boolean isActive= js.optBoolean("isActive");
//
                if (roleId.equalsIgnoreCase("1"))
                {
                    notaryListCount();
                }

                if (roleId!=null&&roleId.equalsIgnoreCase("1"))
                {
                    rl_notary.setVisibility(View.VISIBLE);
                }else {
                    rl_notary.setVisibility(View.GONE);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void findViews()
    {
        progressDialog= ApiConstants.showProgressDialog(this,"Please wait....");
        sharedPrefs=new SharedPrefs(this);
       String data= sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA);
        try {
            JSONObject js;
            if (data != null) {
                js = new JSONObject(data);
                name= js.optString("name");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv_notary_count=findViewById(R.id.tv_notary_count);
        rl_notary=findViewById(R.id.rv_notary);
        tv_name=findViewById(R.id.tv_name_home);
        tv_not_count=findViewById(R.id.not_count);
        tv_count=findViewById(R.id.tv_count);
        tv_name.setText(toTitleCase(name));
        tv_not_count.setVisibility(View.GONE);
        rl_notary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(HomeActivity.this,NotaryMembersActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

    }

    public void notification(View view) {


//        Intent i = new Intent(this, SecondActivity.class);
//        startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);

        Intent i=new Intent(HomeActivity.this, NotificationActivity.class);
        startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void request(View view)
    {
        if (ApiConstants.isNetworkConnected(this))
        {
            Intent i=new Intent(HomeActivity.this,RequestActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }else {
            ApiConstants.showNetworkMessage(this);
        }

    }

    public void process(View view) {
        if (ApiConstants.isNetworkConnected(this))
        {
            Intent i=new Intent(HomeActivity.this,InprogressActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }else {
            ApiConstants.showNetworkMessage(this);
        }
    }

    public void pending(View view) {

        if (ApiConstants.isNetworkConnected(this))
        {
            Intent i=new Intent(HomeActivity.this,PendingActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }else {
            ApiConstants.showNetworkMessage(this);
        }
    }

    public void complete(View view) {

        if (ApiConstants.isNetworkConnected(this))
        {
            Intent i=new Intent(HomeActivity.this, CompleteActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }else {
            ApiConstants.showNetworkMessage(this);
        }
    }


//
//    public void openDrawer(View view) {
//        drawer.openDrawer(Gravity.LEFT);
//    }

    void getCount()
    {
        progressDialog.show();
        @SuppressLint("SetTextI18n") StringRequest stringRequest=new StringRequest(Request.Method.GET, ApiConstants.BaseUrl + "requests?saasUserId=" + id, response -> {
            progressDialog.dismiss();
            Log.e("data_reqcount",response);
            try {
                JSONObject js=new JSONObject(response);
                JSONArray ja=js.getJSONArray("data");
               int count= ja.length();
                tv_count.setText(""+count);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            progressDialog.dismiss();
            ApiConstants.parseVolleyError(HomeActivity.this,error);
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

    void logOut()
    {
        AlertDialog.Builder alb=new AlertDialog.Builder(this);
        alb.setTitle("Logout");
        alb.setIcon(R.drawable.ic_exit_to_app_black_24dp);
        alb.setMessage("Do you want Logout from Application");
        alb.setPositiveButton("Yes", (dialog, which) -> {
            progressDialog.show();
            JSONObject js=new JSONObject();
            try {
                js.put("deviceId",sharedPrefs.getDeviceID().get(SharedPrefs.DEVICE_ID));
                js.put("saasUserId",id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new SaveImpl(this).handleSave(js,"auth/saasuserlogout","POST","logout",token);

        });
        alb.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        alb.create().show();

    }
    void getProfile()
    {
        new SaveImpl(this).handleSave(new JSONObject(),"saasuser?saasUserId="+id,"GET","",token);
    }

    @Override
    public void onSaveSucess(String code, String response, String type) {

       progressDialog.dismiss();
        if (type.equalsIgnoreCase("logout"))
        {
            try {
                JSONObject js=new JSONObject(response);
                String res= js.optString("status");
                if (res.equalsIgnoreCase("Success"))
                {
                    Toast.makeText(this, ""+js.optString("message"), Toast.LENGTH_SHORT).show();
                    sharedPrefs.logOut();
                    finish();
                    Intent i=new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }else {
                    Toast.makeText(this, ""+js.optString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            try {
                JSONObject js_=new JSONObject(response);
                JSONObject jsonObject=js_.getJSONObject("data");
                JSONObject js=jsonObject.getJSONObject("user");
                String profileImage=js.optString("profileImage");
                if (profileImage.equalsIgnoreCase(""))
                {
                    profile_image_nav.setImageResource(R.drawable.profile_d);
                }else {
                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_d).into(profile_image_nav);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }

    @Override
    public void onSaveFailure(String error)
    {
        progressDialog.dismiss();
        Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();

    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        getProfile();
//    }

    @Override
    public void onBackPressed() {

        if(doubleBackToExitPressedOnce){

            moveTaskToBack(true);
            this.finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce=true;
        Toast.makeText(this,"Please Double click to exit app",Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        },2000);
    }

    void getNotificationCount()
    {
        progressDialog.show();
        @SuppressLint("SetTextI18n") StringRequest stringRequest=new StringRequest(Request.Method.GET, ApiConstants.BaseUrl +"saasusernotification?saasUserId=" + id, response -> {
            progressDialog.dismiss();
            Log.e("Notification",response);
            if (response.equalsIgnoreCase("{\"status\":\"Error\",\"message\":\"User is in Inactive\"}"))
            {
                ApiConstants.showSubscriptionDialog(this);
            }else {
                ArrayList<Integer> arrayList=new ArrayList<>();
                try {
                    JSONObject js=new JSONObject(response);
                    JSONArray ja= js.getJSONArray("data");
                    for (int i=0;i<ja.length();i++)
                    {
                        JSONObject json= ja.getJSONObject(i);
                        if (json.optString("status").equalsIgnoreCase("send"))
                        {
                            arrayList.add(i);
                        }
                    }
                    if (arrayList.size()>0)
                    {
                        tv_not_count.setVisibility(View.VISIBLE);
                        tv_not_count.setText(""+arrayList.size());
                    }
                    else {
                        tv_not_count.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, error -> {
            progressDialog.dismiss();
            ApiConstants.parseVolleyError(HomeActivity.this,error);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                if (result != null && result.equalsIgnoreCase("notificationResult")) {
                    getNotificationCount();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        else if (requestCode==3)
        {


            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                if (result != null && result.equalsIgnoreCase("profile")) {
                    getProfile();
                }
            }
        }
    }

    void notaryListCount()
    {
        progressDialog.show();
        @SuppressLint("SetTextI18n") StringRequest stringRequest=new StringRequest(Request.Method.GET, ApiConstants.BaseUrl +"agencynotaries?agencyId="+agencyId, response -> {
            progressDialog.dismiss();
            Log.e("ResNotaries",response);
            if (response.equalsIgnoreCase("{\"status\":\"Error\",\"message\":\"User is in Inactive\"}"))
            {
                ApiConstants.showSubscriptionDialog(this);
            }else
            {

                try {
                    JSONObject js=new JSONObject(response);
                    JSONArray ja=js.getJSONArray("data");
                    tv_notary_count.setText(""+ja.length());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, error -> {
            progressDialog.dismiss();
            ApiConstants.parseVolleyError(HomeActivity.this,error);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        getNotificationCount();
    }
    //onActivityResult




}
