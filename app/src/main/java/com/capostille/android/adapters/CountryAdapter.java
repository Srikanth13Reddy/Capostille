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
import com.capostille.android.models.CountryModel;

import java.util.ArrayList;
import java.util.Locale;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyHolder>
{
    ArrayList<CountryModel> arrayList;
    AppCompatTextView tv_notfound;
    Context context;
    public EventListener eventListener;
    private ArrayList<CountryModel> countrylist;
    AlertDialog alertDialog;

    public interface EventListener
    {
        void onEvent(String cId, String name);

    }

    public CountryAdapter(ArrayList<CountryModel> countrylist, Context context, AppCompatTextView tv_notFound, EventListener eventListener, AlertDialog ad) {
        this.context = context;
        this.countrylist = countrylist;
        this.arrayList = new ArrayList<CountryModel>();
        this.arrayList.addAll(countrylist);
        this.tv_notfound=tv_notFound;
        this.eventListener=eventListener;
        alertDialog=ad;
        tv_notFound.setText("No country found");
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
           if (countrylist.get(position).getName()!=null)
           {
               holder.tv_country.setText(countrylist.get(position).getName());
           }
           holder.itemView.setOnClickListener(new View.OnClickListener()
           {
               @Override
               public void onClick(View v) {
                   alertDialog.dismiss();
                   eventListener.onEvent(countrylist.get(position).getCountryId(),countrylist.get(position).getName());
                   //Toast.makeText(context, ""+countrylist.get(position).getName(), Toast.LENGTH_SHORT).show();
               }
           });
    }

    @Override
    public int getItemCount() {
        return countrylist.size();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        countrylist.clear();
        if (charText.length() == 0) {
            countrylist.addAll(arrayList);
        } else {
            for (CountryModel wp : arrayList) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    countrylist.add(wp);
                }
            }
        }
        if (countrylist.size()==0)
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
