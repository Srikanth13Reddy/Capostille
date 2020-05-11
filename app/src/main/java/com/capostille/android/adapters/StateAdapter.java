package com.capostille.android.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.capostille.android.R;
import com.capostille.android.models.StateModel;

import java.util.ArrayList;
import java.util.Locale;


public class StateAdapter extends RecyclerView.Adapter<StateAdapter.MyHolder>
{
    ArrayList<StateModel> arrayList;
    AppCompatTextView tv_notfound;
    Context context;
    public EventListener eventListener;
    private ArrayList<StateModel> statelist;
    AlertDialog alertDialog;

    public interface EventListener
    {
        void onEventState(String cId, String name);

    }

    public StateAdapter(ArrayList<StateModel> statelist, Context context, AppCompatTextView tv_notFound, EventListener eventListener, AlertDialog ad) {
        this.context = context;
        this.statelist = statelist;
        this.arrayList = new ArrayList<StateModel>();
        this.arrayList.addAll(statelist);
        this.tv_notfound=tv_notFound;
        this.eventListener=eventListener;
        alertDialog=ad;
        tv_notFound.setText("No state found");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v= layoutInflater.inflate(R.layout.country_style,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        if (statelist.get(position).getStateName()!=null&&statelist.get(position).getShortName().isEmpty())
        {
            holder.tv_country.setText(statelist.get(position).getStateName());
        }else {
            holder.tv_country.setText(statelist.get(position).getShortName());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                eventListener.onEventState(statelist.get(position).getStateId(),statelist.get(position).getShortName().isEmpty() ? statelist.get(position).getStateName() :statelist.get(position).getShortName());
                //Toast.makeText(context, ""+statelist.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return statelist.size();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        statelist.clear();
        if (charText.length() == 0) {
            statelist.addAll(arrayList);
        } else {
            for (StateModel wp : arrayList) {
                if (wp.getStateName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    statelist.add(wp);
                }
            }
        }
        if (statelist.size()==0)
        {
            tv_notfound.setVisibility(View.VISIBLE);
        }
        else {
            tv_notfound.setVisibility(View.GONE);
        }
        notifyDataSetChanged();

    }

    public static class MyHolder extends RecyclerView.ViewHolder
    {
        AppCompatTextView tv_country;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_country= itemView.findViewById(R.id.tv_country);
        }
    }
}

