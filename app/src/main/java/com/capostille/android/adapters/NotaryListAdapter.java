package com.capostille.android.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.capostille.android.R;
import com.capostille.android.activities.NotariesListActivity;
import com.capostille.android.models.NotaryListModel;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.SaveImpl;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotaryListAdapter extends RecyclerView.Adapter<NotaryListAdapter.MyHolder>
{
    ArrayList<NotaryListModel> arrayList;
    Context context;
    String rId;
    String id;
    private String token;
    ProgressDialog progressDialog;

    public NotaryListAdapter(ArrayList<NotaryListModel> arrayList, Context context, String rId, String id,String token,ProgressDialog progressDialog) {
        this.arrayList = arrayList;
        this.context = context;
        this.rId=rId;
        this.id=id;
        this.token=token;
        this.progressDialog=progressDialog;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
       LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View v=layoutInflater.inflate(R.layout.notary_list_style,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position)
    {
        if (arrayList.get(position).getProfileImage()!=null)
        {
            if (arrayList.get(position).getProfileImage().equalsIgnoreCase(""))
            {
                holder.iv_n_.setImageResource(R.drawable.profile_d);
               // Picasso.get().load("https://notaryfiles.s3.us-east-2.amazonaws.com/profile_20200327-065759").into(holder.iv_n_);
            }
            else {
                Picasso.get().load(arrayList.get(position).getProfileImage()).into(holder.iv_n_);
               // Picasso.get().load("https://notaryfiles.s3.us-east-2.amazonaws.com/profile_20200327-065759").into(holder.iv_n_);
            }

        }
        holder.tv_n_name.setText(ApiConstants.toTitleCase(arrayList.get(position).getName()));
        holder.iv_send.setOnClickListener(v -> {
            AlertDialog.Builder alb=new AlertDialog.Builder(context);
            alb.setTitle("Confirmation");
            alb.setMessage("Do you want assign to "+arrayList.get(position).getName());
            alb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    assignData(arrayList.get(position).getSaasUserId());
                }
            });
            alb.create().show();

        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder
    {
        CircleImageView iv_n_;
        AppCompatTextView tv_n_name;
        AppCompatImageView iv_send;

        public MyHolder(@NonNull View itemView)
        {
            super(itemView);
            iv_n_=itemView.findViewById(R.id.img);
            tv_n_name=itemView.findViewById(R.id.tv_n_name);
            iv_send=itemView.findViewById(R.id.iv_send);
        }
    }

    private void assignData(String id)
    {
        progressDialog.show();
        JSONObject js=new JSONObject();
        try {
            js.put("assignedTo",id);
            js.put("saasUserId",this.id);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        new SaveImpl((NotariesListActivity)context).handleSave(js,"/request?requestId="+rId,"PUT","Assign",token);

    }
}
