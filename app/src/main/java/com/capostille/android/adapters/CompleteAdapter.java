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


public class CompleteAdapter extends RecyclerView.Adapter<CompleteAdapter.MyHolder>
{
    Context context;
    ArrayList<RequestModel> arraylist;
    private ArrayList<RequestModel> requestlist;
    AppCompatTextView tv_notFound,tv_notFound_;

//    public CompleteAdapter(Context context) {
//        this.context = context;
//    }


    public CompleteAdapter(Context context, ArrayList<RequestModel> requestlist, AppCompatTextView tv_notFound,AppCompatTextView tv_notFound_) {
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
        View v=layoutInflater.inflate(R.layout.complete_style,parent,false);
        return new MyHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        if (requestlist.get(position)!=null)
        {

            RequestModel obj=requestlist.get(position);
            holder.tv_time_rqst.setText(time(obj.getRequestedDate()));
            holder.tv_documents_com.setText("Documents - "+obj.getDocumentsCount());
            holder.tv_address_com.setText(obj.getFullAddress());
            holder.name_tv_com.setText(obj.getRequestCode()+" - "+toTitleCase(obj.getName()));
           // holder.tv_notary_name.setText(obj.getAssignedToName());
            holder.tv_notary_name.setText(toTitleCase(obj.getAssignedToName()));
            holder.itemView.setOnClickListener(v ->
            {
                if (ApiConstants.isNetworkConnected(context))
                {
                    Intent i=new Intent(context, ClientInfo.class);
                    i.putExtra("rId",obj.getUserRequestDetailsId());
                    i.putExtra("status",obj.getStatus());
                    i.putExtra("notaryId",obj.getAssignedTo());
                    i.putExtra("notary",obj.getAssignedToName());
                    //context.startActivity(i);
                   // Activity mContext = (Activity) context;
                    Activity mContext = (Activity) context;
                    mContext.startActivityForResult(i, 111);
                    mContext.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }else {
                    ApiConstants.showNetworkMessage(context);
                }

            });
        }

    }

    public void filter(String charText) {
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
            if (arraylist.size()==0)
            {
                tv_notFound_.setVisibility(View.VISIBLE);
            }

        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return requestlist.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder
    {
        AppCompatTextView name_tv_com,tv_address_com,tv_documents_com,tv_time_rqst,tv_notary_name;

        public MyHolder(@NonNull View v) {
            super(v);
            name_tv_com= v.findViewById(R.id.name_tv_com);
            tv_address_com= v.findViewById(R.id.tv_address_com);
            tv_documents_com= v.findViewById(R.id.tv_documents_com);
            tv_time_rqst= v.findViewById(R.id.tv_time_rqst);
            tv_notary_name= v.findViewById(R.id.tv_notary_name);

        }
    }
}



