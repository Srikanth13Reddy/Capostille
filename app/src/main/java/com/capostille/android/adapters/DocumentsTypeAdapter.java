package com.capostille.android.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capostille.android.R;
import com.capostille.android.activities.ClientInfo;
import com.capostille.android.models.DocumentsDetailsModel;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DocumentsTypeAdapter extends BaseAdapter
{
    ArrayList<DocumentsDetailsModel> arrayList;
    Context context;
    String token;
    String status;

    public DocumentsTypeAdapter(ArrayList<DocumentsDetailsModel> arrayList, Context context, String token, String status) {
        this.arrayList = arrayList;
        this.context = context;
        this.token=token;
        this.status=status;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
       LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      @SuppressLint("ViewHolder") View v= layoutInflater.inflate(R.layout.document_type_style,parent,false);
        Switch aSwitch=v.findViewById(R.id.sw);
        if (arrayList.get(position).getStatus().equalsIgnoreCase("Unverified"))
        {
            aSwitch.setChecked(false);
        }
        else {
            aSwitch.setChecked(true);
        }

        if (status.equalsIgnoreCase("Completed")||status.equalsIgnoreCase("Rejected"))
        {
            aSwitch.setClickable(false);
        }
        else {
            aSwitch.setClickable(true);
        }
        AppCompatTextView fname=v.findViewById(R.id.fname);
        AppCompatTextView sname=v.findViewById(R.id.sname);
        AppCompatTextView service=v.findViewById(R.id.service);

        if (!arrayList.get(position).getShortName().isEmpty())
        {
            sname.setText(arrayList.get(position).getShortName());
        }else {
            sname.setText(ApiConstants.toTitleCase(arrayList.get(position).getStateName()));
        }

        String type_id= arrayList.get(position).getDocumentTypeID();

        if (type_id.equalsIgnoreCase("13")||type_id.equalsIgnoreCase("19")||type_id.equalsIgnoreCase("30"))
        {
            fname.setText(""+(position+1)+"."+ ApiConstants.toTitleCase(arrayList.get(position).getOtherDocumentName()));
        }else {
            fname.setText(""+(position+1)+"."+ ApiConstants.toTitleCase(arrayList.get(position).getDocumentName()));
        }

        service.setText(arrayList.get(position).getServiceName());

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                if (arrayList.get(position).getStatus().equalsIgnoreCase("Unverified"))
                 {
                     changeStatus(arrayList.get(position).getDocumentDetailsId(),"Verified","");
                 }
                 else {

                     changeStatus(arrayList.get(position).getDocumentDetailsId(),"Unverified","");
                      //showTextDialog(arrayList.get(position).getDocumentDetailsId(),"Unverified");
                 }

            }
        });
        return v;
    }

    private void showTextDialog(String documentDetailsId, String status)
    {
        AlertDialog.Builder alb=new AlertDialog.Builder(context);
       LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View v=layoutInflater.inflate(R.layout.pending_dialog,null,false);
       AppCompatEditText et=v.findViewById(R.id.et_text);
       Button btn_acpt=v.findViewById(R.id.btn_acpt);
       alb.setView(v);
       alb.setCancelable(false);
      AlertDialog ad= alb.create();
      ad.show();
      btn_acpt.setOnClickListener(v1 -> {
          if (et.getText().toString().equalsIgnoreCase(""))
          {
              Toast.makeText(context, "Please enter comments", Toast.LENGTH_SHORT).show();
          }
          else {
              ad.cancel();
              changeStatus(documentDetailsId,status,et.getText().toString());
          }
      });
    }

    private void changeStatus(String id,String status,String comments)
    {
        ProgressDialog progressDialog=ApiConstants.showProgressDialog(context,"Please wait.....");
        progressDialog.show();
        JSONObject js=new JSONObject();
        try {
            js.put("status",status);
            js.put("comments",comments);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody=js.toString();
        StringRequest stringRequest= new StringRequest(Request.Method.PUT, ApiConstants.BaseUrl + "/document?documentId="+id, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                progressDialog.dismiss();
                Log.e("Response",response);

                try {
                    JSONObject js=new JSONObject(response);
                   String status= js.optString("status");
                   if (status.equalsIgnoreCase("Success"))
                   {
                       if (context instanceof ClientInfo) {
                           ((ClientInfo)context).getRequestData();
                       }
                   }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                ApiConstants.parseVolleyError(context,error);
                progressDialog.dismiss();
                Log.e("Response",""+error);
            }
        })

        {
            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String,String> hm=new HashMap<>();
                hm.put("Authorization","Bearer "+token);
                return hm;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}
