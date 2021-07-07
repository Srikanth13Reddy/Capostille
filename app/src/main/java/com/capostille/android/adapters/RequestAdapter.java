package com.capostille.android.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.capostille.android.R;
import com.capostille.android.activities.ClientInfo;
import com.capostille.android.models.RequestModel;
import com.capostille.android.utils.ApiConstants;

import java.util.ArrayList;
import java.util.Locale;

import static com.capostille.android.utils.ApiConstants.time;
import static com.capostille.android.utils.ApiConstants.toTitleCase;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyHolder>
{
    Context context;
    ArrayList<RequestModel> arraylist;
    private ArrayList<RequestModel> requestlist;
    AppCompatTextView tv_notFound,tv_notFound_;

    public RequestAdapter(Context context, ArrayList<RequestModel> requestlist, AppCompatTextView tv_notFound,AppCompatTextView tv_notFound_) {
        this.context = context;
        this.requestlist = requestlist;
        this.arraylist = new ArrayList<RequestModel>();
        this.arraylist.addAll(requestlist);
        this.tv_notFound=tv_notFound;
        this.tv_notFound_=tv_notFound_;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=layoutInflater.inflate(R.layout.request_style,parent,false);
        return new MyHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {



         RequestModel obj= requestlist.get(position);
         if (obj!=null)
         {
             holder.tv_time_rqst.setText(time(obj.getRequestedDate()));
             holder.tv_documents_rqst.setText("Documents - "+obj.getDocumentsCount());
             holder.tv_address_rqst.setText(obj.getFullAddress());
             holder.name_tv_rqst.setText(obj.getRequestCode()+" - "+toTitleCase(obj.getName()));
             holder.tv_notary_name.setText(toTitleCase(obj.getAssignedToName()));
             if (obj.getStatus().equalsIgnoreCase("New"))
             {
                 holder.tv_status_rqst.setBackgroundResource(R.drawable.request_drawable);
                 holder.tv_status_rqst.setText("New");
             }
             else if (obj.getStatus().equalsIgnoreCase("Pending"))
             {
                 holder.tv_status_rqst.setBackgroundResource(R.drawable.pending_drawable);
                 holder.tv_status_rqst.setText("Pending");
             }
             else if (obj.getStatus().equalsIgnoreCase("Inprogress"))
             {
                 holder.tv_status_rqst.setBackgroundResource(R.drawable.inprogress_drawable);
                 holder.tv_status_rqst.setText("Inprogress");
             }
             else if (obj.getStatus().equalsIgnoreCase("Completed"))
             {
                 holder.tv_status_rqst.setBackgroundResource(R.drawable.complete_drawable);
                 holder.tv_status_rqst.setText("Completed");
             }
             else if (obj.getStatus().equalsIgnoreCase("Rejected"))
             {
                 holder.tv_status_rqst.setBackgroundResource(R.drawable.reject_drawable);
                 holder.tv_status_rqst.setText("Rejected");
             }

             holder.itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v)
                 {
                     if (ApiConstants.isNetworkConnected(context))
                     {
                         Intent i=new Intent(context, ClientInfo.class);
                         i.putExtra("rId",obj.getUserRequestDetailsId());
                         i.putExtra("status",obj.getStatus());
                         i.putExtra("notaryId",obj.getAssignedTo());
                         i.putExtra("notary",obj.getAssignedToName());
                        // context.startActivity(i);
                         Activity mContext = (Activity) context;
                         mContext.startActivityForResult(i, 110);
                         mContext.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                     }else {
                         ApiConstants.showNetworkMessage(context);
                     }
                 }
             });
         }

    }

    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        requestlist.clear();
        if (charText.length() == 0) {
            requestlist.addAll(arraylist);
        } else {
            for (RequestModel wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)||wp.getRequestCode().toLowerCase(Locale.getDefault()).contains(charText)) {
                    requestlist.add(wp);
                }
            }
        }
        if (requestlist.size()==0)
        {
            tv_notFound.setVisibility(View.VISIBLE);
            tv_notFound_.setVisibility(View.GONE);
        }
        else {
            tv_notFound.setVisibility(View.GONE);
            tv_notFound_.setVisibility(View.GONE);
        }
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount()
    {
        return requestlist.size();
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        AppCompatTextView name_tv_rqst,tv_address_rqst,tv_bus_rqst,tv_documents_rqst,tv_time_rqst,tv_status_rqst,tv_notary_name;

        MyHolder(@NonNull View v) {
            super(v);
            name_tv_rqst= v.findViewById(R.id.name_tv_rqst);
            tv_address_rqst= v.findViewById(R.id.tv_address_rqst);
            name_tv_rqst= v.findViewById(R.id.name_tv_rqst);
           // tv_bus_rqst= v.findViewById(R.id.tv_bus_rqst);
            tv_documents_rqst= v.findViewById(R.id.tv_documents_rqst);
            tv_time_rqst= v.findViewById(R.id.tv_time_rqst);
            tv_status_rqst= v.findViewById(R.id.tv_status_rqst);
            tv_notary_name= v.findViewById(R.id.tv_notary_name);
        }
    }




}

