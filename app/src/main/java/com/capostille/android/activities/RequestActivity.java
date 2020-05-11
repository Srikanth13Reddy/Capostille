package com.capostille.android.activities;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
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
import com.capostille.android.adapters.RequestAdapter;
import com.capostille.android.interfaces.SaveView;
import com.capostille.android.models.RequestModel;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.MySingleton;
import com.capostille.android.utils.SaveImpl;
import com.capostille.android.utils.SharedPrefs;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestActivity extends AppCompatActivity implements SaveView
{

    private static final int LAUNCH_FILTER_ACTIVITY = 12;
    RecyclerView rv_notification;
    ProgressDialog progressDialog;
    SharedPrefs sharedPrefs;
    String id,token;
    ArrayList<RequestModel> al=new ArrayList<>();
    private SearchView searchView;
    AppCompatTextView tv_notFound,tv_req_notfound;
    private RequestAdapter requestAdapter;
    private String result;
    private ArrayList<String> filterarrayList;
    private String dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        getLoginData();
        getSupportActionBar().setTitle("Notary Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPrefs.saveStatus("");
        progressDialog= ApiConstants.showProgressDialog(this,"Please wait....");
        rv_notification = findViewById(R.id.rv_request);
        tv_notFound = findViewById(R.id.tv_notFound);
        tv_req_notfound = findViewById(R.id.tv_req_notfound);
        rv_notification.setLayoutManager(new LinearLayoutManager(this));

        //getData();
        getRequestData("");
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
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRequestData(String type)
    {
       progressDialog.show();
//       JSONObject js=new JSONObject();
//        try {
//            js.put("status","");
//            js.put("startDate","");
//            js.put("endDate","");
//            js.put("state","");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        new SaveImpl(this).handleSave(new JSONObject(),"requests?saasUserId="+id,"GET",type,token);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        else if (item.getItemId()== R.id.notification_filter)
        {
           // showBottomSheetDialog();
            Intent i=new Intent(RequestActivity.this, FilterActivity.class);
            if (result!=null)
            {
                i.putExtra("result",result);
                i.putExtra("date",dates);
            }
            startActivityForResult(i, LAUNCH_FILTER_ACTIVITY);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.notification_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.notification_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search...");
        //  searchView.setOnQueryTextListener(this);
        searchView.setIconified(true);



        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sharedPrefs.saveStatus("");
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void onSaveSucess(String code, String response,String type) {
        progressDialog.dismiss();
        Log.e("RequestRes",response);

        if (response.equalsIgnoreCase("{\"status\":\"Error\",\"message\":\"User is in Inactive\"}"))
        {
            ApiConstants.showSubscriptionDialog(this);
        }else{
            if (code.equalsIgnoreCase("401"))
            {
                ApiConstants.logOut(this);
            } else {
                assignData(response,type);
            }
        }



    }


    private void assignData(String response,String type)
    {
        tv_notFound.setVisibility(View.GONE);
        al.clear();
        try {
            JSONObject jss=new JSONObject(response);
            if (jss.optString("status").equalsIgnoreCase("Success"))
            {
                JSONArray ja =  jss.getJSONArray("data");
                if (ja.length() == 0) {
                    tv_req_notfound.setVisibility(View.VISIBLE);
                } else {
                    tv_req_notfound.setVisibility(View.GONE);
                }
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject json = ja.getJSONObject(i);
                    String userRequestDetailsId = json.optString("userRequestDetailsId");
                    String requestedDate = json.optString("requestedDate");
                    String customerId = json.optString("customerId");
                    String status = json.optString("status");
                    String assignedTo = json.optString("assignedTo");
                    String documentsCount = json.optString("documentsCount");
                    String name = json.optString("name");
                    String fullAddress = json.optString("fullAddress");
                    String requestCode = json.optString("requestCode");
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

                RequestAdapter requestAdapter = new RequestAdapter(this, al, tv_notFound, tv_req_notfound);
                rv_notification.setAdapter(requestAdapter);

//           else if (type.equalsIgnoreCase("name"))
//           {
//               ArrayList<RequestModel> sortedByName = new SortbyStatus(al).getSortedByName();
//               requestAdapter=  new RequestAdapter(this,sortedByName,tv_notFound);
//               rv_notification.setAdapter(requestAdapter);
//           }
//           else
//           {
//               ArrayList<RequestModel> sortedByName = new SortbyStatus(al).getSortedByStatus();
//               requestAdapter=  new RequestAdapter(this,sortedByName,tv_notFound);
//               rv_notification.setAdapter(requestAdapter);
//           }
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
            }else {
                Toast.makeText(this, ""+jss.optString("message"), Toast.LENGTH_SHORT).show();
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


    public void showBottomSheetDialog()
    {
        LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.sort_style, null,false);
        RelativeLayout rv_name=view.findViewById(R.id.name_sort);
        RelativeLayout rv_status=view.findViewById(R.id.status_sort);
        RadioButton rb_name=view.findViewById(R.id.rb_name);
        RadioButton rb_status=view.findViewById(R.id.tb_status);
       String status=  sharedPrefs.getStatus().get(SharedPrefs.STATUS_DATA);
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
             sharedPrefs.saveStatus("name");
            getRequestData("name");
        });
        rv_status.setOnClickListener(v -> {
            dialog.dismiss();
            sharedPrefs.saveStatus("status");
            getRequestData("status");
        });
        rb_name.setOnClickListener(v -> {
            dialog.dismiss();
            sharedPrefs.saveStatus("name");
            getRequestData("name");
        });
        rb_status.setOnClickListener(v -> {
            dialog.dismiss();
            sharedPrefs.saveStatus("status");
            getRequestData("status");
        });
        dialog.show();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
       // getRequestData("");
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_FILTER_ACTIVITY)
        {
            if(resultCode == Activity.RESULT_OK){
                if (filterarrayList!=null)
                {
                    filterarrayList.clear();
                }

                result=data.getStringExtra("result");
                dates=data.getStringExtra("date");

                Log.e("Result",result);
                Log.e("Dates",dates);

                try {
                    JSONArray ja=new JSONArray(result);
                    JSONArray jaa=new JSONArray(dates);
                    String startDate= jaa.getJSONObject(0).optString("startDate");
                    String endDate= jaa.getJSONObject(0).optString("endDate");
                    String[] elements = startDate.replaceAll("-", ",").split(",");
                    String[] elements1 = endDate.replaceAll("-", ",").split(",");
                    TextView tv_start=new TextView(this);
                    TextView tv_end=new TextView(this);
                    if (elements1.length==3)
                    {
                        tv_start.setText(""+elements[2]+"-"+elements[0]+"-"+elements[1]);
                        tv_end.setText(""+elements1[2]+"-"+elements1[0]+"-"+elements1[1]);
                    }else
                    {
                        tv_end.setText("");
                        tv_start.setText("");
                    }

                    Log.e("status",""+ja.length());
                    Log.e("DatesR",elements[0]);

                    if (ja.length()==0&&startDate.isEmpty()&&endDate.isEmpty())
                    {
                       getRequestData("");
                    }else {
                        filterarrayList=new ArrayList<>();
                        JSONArray jsonArray=new JSONArray();
                        for (int i=0;i<ja.length();i++)
                        {
                            String item=ja.getJSONObject(i).optString("name");
                            filterarrayList.add("\""+item+"\"");
                            jsonArray.put(""+item+"");

                        }
                        Log.e("filter",""+jsonArray);
                        Log.e("filter",""+tv_start.getText().toString());
                        Log.e("filter",""+jsonArray);
                        progressDialog.show();
                        JSONObject js=new JSONObject();
                        try {
                            js.put("status",jsonArray);
                            js.put("startDate",tv_start.getText().toString());
                            js.put("endDate",tv_end.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //filterData(js);

                        new SaveImpl(this).handleSave(js,"requestfilter?saasUserId="+id,"PUT","",token);

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }



//                if (result != null && result.equalsIgnoreCase("notificationResult")) {
//                    getNotificationCount();
//                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        else if (requestCode == 110)
        {
           // Toast.makeText(this, "Hiii", Toast.LENGTH_SHORT).show();
            if(resultCode == Activity.RESULT_OK)
            {
                getRequestData("");
                //Toast.makeText(this, "Hiii", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void filterData(JSONObject js)
    {
        progressDialog.show();

        final String requestBody=js.toString();
        Log.e("Response",requestBody);
        StringRequest stringRequest= new StringRequest(Request.Method.PUT, ApiConstants.BaseUrl + "requestfilter?saasUserId="+id, response -> {
            progressDialog.dismiss();
            Log.e("Response",response);
        }, error -> {
            ApiConstants.parseVolleyError(RequestActivity.this,error);
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
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String,String> hm=new HashMap<>();
                hm.put("Authorization","Bearer "+token);
                hm.put("Content-Type", "application/json; charset=utf-8");
                return hm;
            }

        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}