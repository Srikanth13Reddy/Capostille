package com.capostille.android.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capostille.android.R;
import com.capostille.android.adapters.StatusAdapter;
import com.capostille.android.interfaces.SaveView;
import com.capostille.android.models.StatusModel;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.SaveImpl;
import com.capostille.android.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class FilterActivity extends AppCompatActivity implements SaveView
{

    RecyclerView rv_status;
    String status[]={"New","Pending","Inprogress","Completed","Rejected"};
    String status_result;
    SearchView state_search;
    LinearLayout ll_state;
    AppCompatTextView tv_status,tv_date;
    SharedPrefs sharedPrefs;
    AppCompatEditText tv_startdate,tv_enddate;
    String id,token;
    ProgressDialog progressDialog;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String dates_result;
    List<String> fixedStart;
    List<String> fixedEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        getSupportActionBar().setTitle("Filters");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getLoginData();
        Bundle b=getIntent().getExtras();
        if (b != null) {
            status_result=b.getString("result");
            dates_result=b.getString("date");
            Log.e("dates",""+dates_result);
        }
        findViews();
        getStatusData();
        actions();
       // getStates();
    }

    private void findViews()
    {
        rv_status=findViewById(R.id.rv_status);
        state_search=findViewById(R.id.state_search);
        ll_state=findViewById(R.id.ll_state);
        tv_startdate=findViewById(R.id.tv_startDate);
        tv_enddate=findViewById(R.id.tv_endDate);
        // rv_state_search=findViewById(R.id.rv_state_search);
        tv_status=findViewById(R.id.tv_status);
        tv_date=findViewById(R.id.tv_date);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        LinearLayoutManager llm1 = new LinearLayoutManager(this);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_status.getContext(), llm.getOrientation());
//        rv_status.addItemDecoration(dividerItemDecoration);
        rv_status.setLayoutManager(llm);
        // rv_state_search.setLayoutManager(llm1);
         if (dates_result!=null)
         {
             JSONArray jaa= null;
             try {
                 jaa = new JSONArray(dates_result);
                 String startDate= jaa.getJSONObject(0).optString("startDate");
                 String endDate= jaa.getJSONObject(0).optString("endDate");
                 String[] elements = startDate.replaceAll("-", ",").split(",");
                 String[] elements1 = endDate.replaceAll("-", ",").split(",");
                 fixedStart = Arrays.asList(elements);
                 fixedEnd = Arrays.asList(elements1);
                 Log.e("DatesR",""+fixedStart+"\n"+endDate);
                 tv_enddate.setText(endDate);
                 tv_startdate.setText(startDate);
             } catch (JSONException e) {
                 e.printStackTrace();
             }
         }


    }

    private void actions()
    {
        tv_date.setOnClickListener(v -> {

            tv_date.setBackgroundColor(getResources().getColor(R.color.white));
            tv_status.setBackgroundColor(getResources().getColor(R.color.background));
            ll_state.setVisibility(View.VISIBLE);
            rv_status.setVisibility(View.GONE);
        });
        tv_status.setOnClickListener(v -> {
            tv_date.setBackgroundColor(getResources().getColor(R.color.background));
            tv_status.setBackgroundColor(getResources().getColor(R.color.white));
            ll_state.setVisibility(View.GONE);
            rv_status.setVisibility(View.VISIBLE);
        });
    }

    private void getLoginData()
    {
        progressDialog= ApiConstants.showProgressDialog(this,"Please wait....");
        sharedPrefs=new SharedPrefs(this);
        try {
            if (sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA)!=null)
            {
                JSONObject js=new JSONObject(sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA));
                id= js.optString("id");
                token= js.optString("token");
               // roleId= js.optString("roleId");
                Log.e("data",sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getStates()
    {
       progressDialog.show();
       new SaveImpl(this).handleSave(new JSONObject(),"usstates","GET","",token);
    }

    private void getStatusData()
    {
        ArrayList<StatusModel> modelArrayList=new ArrayList<>();
        for (int i=0;i<status.length;i++)
        {
            StatusModel statusModel=new StatusModel();
            if (status_result!=null)
            {
                try {
                    JSONArray jaa=new JSONArray(status_result);
                    for (int j=0;j<jaa.length();j++)
                    {
                      String name=  jaa.getJSONObject(j).optString("name");
                        if (status[i].equalsIgnoreCase(name))
                        {
                           statusModel.setSelected(true);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            statusModel.setStatus(status[i]);
            modelArrayList.add(statusModel);
        }
        StatusAdapter stateAdapter=new StatusAdapter(modelArrayList, FilterActivity.this);
        rv_status.setAdapter(stateAdapter);
    }

    public void apply(View view)
    {
        if (!tv_startdate.getText().toString().isEmpty()&&tv_enddate.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Choose End Date", Toast.LENGTH_SHORT).show();
        }else if (!tv_enddate.getText().toString().isEmpty()&&tv_startdate.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Choose Start Date", Toast.LENGTH_SHORT).show();
        }else {
            Intent returnIntent = new Intent(this, HomeActivity.class);
            returnIntent.putExtra("result",getSelectedData());
            returnIntent.putExtra("date",getDates());
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }

    }

   public String getSelectedData()
    {
        ArrayList<String> al=new ArrayList<>();
        JSONArray jsonArray=new JSONArray();
        for (int i = 0; i < StatusAdapter.arrayList.size(); i++)
        {
            if(StatusAdapter.arrayList.get(i).getSelected())
            {
                // tv.setText(tv.getText() + " " + CustomAdapter.imageModelArrayList.get(i).getAnimal());
                // al.add(CustomAdapter.imageModelArrayList.get(i));

                JSONObject js=new JSONObject();
                try {
                    js.put("name", StatusAdapter.arrayList.get(i).getStatus());
                    jsonArray.put(js);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                //  Log.e("check",""+name);
                //  Toast.makeText(activity, name + " clicked!", Toast.LENGTH_SHORT).show();


            }
        }




        return jsonArray.toString();

    }

    String getDates()
    {
        JSONArray ja=new JSONArray();
        JSONObject js1=new JSONObject();
        try {
            js1.put("startDate",tv_startdate.getText().toString());
            js1.put("endDate",tv_enddate.getText().toString());
            ja.put(js1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja.toString();
    }

    public void clear(View view)
    {
//        if (fixedStart!=null&&fixedEnd!=null)
//        {
//            fixedEnd.clear();
//            fixedStart.clear();
//        }
        status_result="";
        dates_result="";
        tv_startdate.setText("");
        tv_enddate.setText("");
        getStatusData();
    }

    @Override
    public void onSaveSucess(String code, String response, String type)
    {
        Log.e("States",response);
       progressDialog.dismiss();
       //assigndata(response);
    }

//    private void assigndata(String response)
//    {
//        ArrayList<StateModel> arrayList=new ArrayList<>();
//        try {
//            JSONArray ja=new JSONArray(response);
//            for (int i=0;i<ja.length();i++)
//            {
//               JSONObject json= ja.getJSONObject(i);
//                String  usStateId= json.optString("usStateId");
//                String stateName= json.optString("stateName");
//                StateModel stateModel=new StateModel();
//                stateModel.setStateName(stateName);
//                stateModel.setStateId(usStateId);
//                arrayList.add(stateModel);
//            }
//
//            StateFilterAdapter adapter=new StateFilterAdapter(arrayList,this);
//            rv_state_search.setAdapter(adapter);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onSaveFailure(String error) {
        progressDialog.dismiss();
        Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
    }

    public void startDate(View view)
    {
        getDate(tv_startdate,"start");
    }

    private void getDate(AppCompatEditText tv_date,String type){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    //dval =year + "/" + (monthOfYear + 1) + "/" +dayOfMonth ;
                    tv_date.setText(String.format("%02d",(monthOfYear + 1))+"-"+String.format("%02d", dayOfMonth)+"-"+year);

                }, mYear, mMonth , mDay);

        if (fixedStart!=null&&fixedStart.size()==3&&type.equalsIgnoreCase("start"))
        {
            datePickerDialog.updateDate(Integer.parseInt(fixedStart.get(2)), Integer.parseInt(fixedStart.get(0)) - 1, Integer.parseInt(fixedStart.get(1)));
        }else if (fixedEnd!=null&&fixedEnd.size()==3&&type.equalsIgnoreCase("end"))
        {
            datePickerDialog.updateDate(Integer.parseInt(fixedEnd.get(2)), Integer.parseInt(fixedEnd.get(0)) - 1, Integer.parseInt(fixedEnd.get(1)));

        }

        //datePickerDialog.updateDate(2019, 03 - 1, 3);

        // datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();

    }

    public void endDate(View view) {
        getDate(tv_enddate,"end");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
        Intent returnIntent = new Intent(this, HomeActivity.class);
        returnIntent.putExtra("result","");
        returnIntent.putExtra("date","");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
