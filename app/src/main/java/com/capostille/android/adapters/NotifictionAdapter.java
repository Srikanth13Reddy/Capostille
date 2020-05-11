package com.capostille.android.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.capostille.android.R;
import com.capostille.android.activities.ClientInfo;
import com.capostille.android.activities.RequestActivity;
import com.capostille.android.models.NotificationModel;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.capostille.android.utils.ApiConstants.time;

public class NotifictionAdapter extends RecyclerView.Adapter<NotifictionAdapter.MyHolder>
{
    Context context;
    ArrayList<NotificationModel> arrayList;
    private String token;
    String role_id,u_id;

    public NotifictionAdapter(Context context, ArrayList<NotificationModel> arrayList, String token, String id,String u_id)
    {
        this.context = context;
        this.arrayList = arrayList;
        this.token=token;
        this.role_id=id;
        this.u_id=u_id;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View v=layoutInflater.inflate(R.layout.notification_style,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position)
    {
        if (arrayList.get(position)!=null)
        {

            NotificationModel obj= arrayList.get(position);
            if (obj.getStatus().equalsIgnoreCase("send"))
            {
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.background));
            }else {
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
            holder.tv_content.setText(obj.getContent());
            holder.tv_notificationType.setText(obj.getNotificationType());
            holder.tv_sendDate.setText(time(obj.getSendDate()));
            holder.itemView.setOnClickListener(v ->
            {
//                Intent i=new Intent(context, ClientInfo.class);
//                context.startActivity(i);
//                Activity mContext = (Activity) context;
//                mContext.overridePendingTransition(R.anim.right_in, R.anim.left_out);

                changenotificationStatusChange(obj.getNotificationTrackId(),obj.getRequestId(),obj.getRequestStatus(),obj.getNotificationType(),obj.getAssignedTo());
            });
        }

    }

    private void changenotificationStatusChange(String notificationTrackId,String rId,String status,String type,String assignTo)
    {
        ProgressDialog progressDialog= ApiConstants.showProgressDialog(context,"Please Wait....");
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.PATCH, ApiConstants.BaseUrl + "notificationstatuschange?notificationTrackId=" + notificationTrackId,
                response -> {
            progressDialog.dismiss();
                    Log.e("Res",response);
                    try {
                        JSONObject ja=new JSONObject(response);
                        if (ja.optString("status").equalsIgnoreCase("Success"))
                        {
                            if (!type.equalsIgnoreCase("Reassign Request")&&role_id.equalsIgnoreCase("1")||role_id.equalsIgnoreCase("3"))
                            {

                                Intent i=new Intent(context, ClientInfo.class);
                                i.putExtra("rId",rId);
                                i.putExtra("status",status);
                                context.startActivity(i);
                                Activity mContext = (Activity) context;
                                mContext.overridePendingTransition(R.anim.right_in, R.anim.left_out);

                            }else if (!type.equalsIgnoreCase("Reassign Request")&&assignTo.equalsIgnoreCase(u_id))
                            {
                                Intent i=new Intent(context, ClientInfo.class);
                                i.putExtra("rId",rId);
                                i.putExtra("status",status);
                                context.startActivity(i);
                                Activity mContext = (Activity) context;
                                mContext.overridePendingTransition(R.anim.right_in, R.anim.left_out);

                            }else
                                {
                                Intent i=new Intent(context, RequestActivity.class);
                                i.putExtra("rId",rId);
                                i.putExtra("status",status);
                                context.startActivity(i);
                                Activity mContext = (Activity) context;
                                mContext.overridePendingTransition(R.anim.right_in, R.anim.left_out);

                            }
                             }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {
            progressDialog.dismiss();
            ApiConstants.parseVolleyError(context,error);
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
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        AppCompatTextView tv_notificationType,tv_content,tv_sendDate;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_sendDate= itemView.findViewById(R.id.sendDate);
            tv_content= itemView.findViewById(R.id.content);
            tv_notificationType= itemView.findViewById(R.id.notificationType);
        }
    }
}
