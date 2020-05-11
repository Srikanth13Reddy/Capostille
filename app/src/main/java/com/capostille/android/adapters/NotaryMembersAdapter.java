package com.capostille.android.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.capostille.android.R;
import com.capostille.android.models.NotaryListModel;
import com.capostille.android.utils.ApiConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotaryMembersAdapter extends RecyclerView.Adapter<NotaryMembersAdapter.MyHolder>
{
    ArrayList<NotaryListModel> arrayList;
    Context context;

    public NotaryMembersAdapter(ArrayList<NotaryListModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;

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
        NotaryListModel obj= arrayList.get(position);
        if (obj!=null)
        {
            holder.iv_send.setVisibility(View.GONE);
            if (arrayList.get(position).getProfileImage()!=null)
            {
                if (arrayList.get(position).getProfileImage().equalsIgnoreCase(""))
                {
                    holder.iv_n_.setImageResource(R.drawable.profile_d);
                }
                else {
                    Picasso.get().load(arrayList.get(position).getProfileImage()).into(holder.iv_n_);
                }

            }
            holder.tv_n_name.setText(ApiConstants.toTitleCase(arrayList.get(position).getName()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProfile(obj);
                }
            });
        }


    }

    @SuppressLint("SetTextI18n")
    private void showProfile(NotaryListModel obj)
    {
       LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View v= inflater.inflate(R.layout.notary_dialog_style,null,false);
        AppCompatTextView tv_name_notary= v.findViewById(R.id.tv_name_notary);
        CircleImageView profile_image_notary= v.findViewById(R.id.profile_image_notary);
        AppCompatTextView tv_email_notary= v.findViewById(R.id.tv_email_notary);
        AppCompatTextView tv_mobile_notary= v.findViewById(R.id.tv_mobile_notary);
        AppCompatTextView tv_address_notary= v.findViewById(R.id.tv_address_notary);
        tv_address_notary.setText(obj.getFullAddress());
        tv_mobile_notary.setText(obj.getPhoneNumber());
        // tv_name_notary.setText(ApiConstants.toTitleCase(obj.getName()));
        tv_email_notary.setText(obj.getEmail());

        if (obj.getCountryCode().contains("+"))
        {
            tv_mobile_notary.setText(obj.getCountryCode() + " " + obj.getPhoneNumber());
        } else {
            tv_mobile_notary.setText("+" + obj.getCountryCode() + " " + obj.getPhoneNumber());
        }

        if (obj.getProfileImage()!=null)
        {
            if (obj.getProfileImage().equalsIgnoreCase(""))
            {
                profile_image_notary.setImageResource(R.drawable.profile_d);
            }
            else {
                Picasso.get().load(obj.getProfileImage()).into(profile_image_notary);
            }

        }

        AlertDialog.Builder alb=new AlertDialog.Builder(context);
        alb.setView(v);
       AlertDialog alertDialog= alb.create();

         ApiConstants.setAnimation(context,v,alertDialog);

              alertDialog .show();

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


}

