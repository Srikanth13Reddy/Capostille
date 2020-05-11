package com.capostille.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.capostille.android.R;
import com.capostille.android.models.StatusModel;

import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyHolder>
{
   public static ArrayList<StatusModel> arrayList;
    Context context;

    public StatusAdapter(ArrayList<StatusModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View v= layoutInflater.inflate(R.layout.status_style,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position)
    {
        StatusModel obj= arrayList.get(position);
        holder.cb_status.setChecked(obj.getSelected());
        holder.tv_name.setText(arrayList.get(position).getStatus());
        holder.cb_status.setTag(position);
        holder.cb_status.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Integer pos = (Integer) holder.cb_status.getTag();
                //  Toast.makeText(activity, obj.getName() + " clicked!", Toast.LENGTH_SHORT).show();

                if (arrayList.get(pos).getSelected()) {
                    arrayList.get(pos).setSelected(false);
                } else {
                    arrayList.get(pos).setSelected(true);
                }


//                    ArrayList<String> al=new ArrayList<>();
//                    JSONArray jsonArray=new JSONArray();
//                    for (int i = 0; i < MultiAdapter.data.size(); i++){
//                        if(MultiAdapter.data.get(i).getSelected())
//                        {
//                            // tv.setText(tv.getText() + " " + CustomAdapter.imageModelArrayList.get(i).getAnimal());
//                            // al.add(CustomAdapter.imageModelArrayList.get(i));
//                            DisplayModelNew obj= data.get(i);
//                            String name= obj.getName();
//                            al.add(name);
//                            JSONObject js=new JSONObject();
//                            try {
//                                js.put("displayId",obj.getDisplayId());
//                                js.put("name",obj.getName());
//                                js.put("displayCharityId",obj.getDisplayCharityId());
//                                js.put("charityId",obj.getCharityId());
//                                js.put("charityName",obj.getCharityName());
//                                js.put("notes",obj.getNotes());
//                                js.put("latitude",obj.getLatitude());
//                                js.put("longitude",obj.getLongitude());
//                                js.put("country",obj.getCountry());
//                                js.put("cityName",obj.getCityName());
//                                js.put("address",obj.getAddress());
//                                js.put("markerType",obj.getMarkerType());
//                                js.put("isPrivate",obj.getIsPrivate());
//                                js.put("createdDate",obj.getCreatedDate());
//                                js.put("createdBy",obj.getCreatedBy());
//                                js.put("createdByName",obj.getCreatedByName());
//                                js.put("filePath",obj.getFilePath());
//                                js.put("viewCount",obj.getViewCount());
//                                js.put("markerUrl",obj.getMarkerUrl());
//                                jsonArray.put(js);
//
//                            } catch (JSONException e)
//                            {
//                                e.printStackTrace();
//                            }
//                            //  Log.e("check",""+name);
//                            //  Toast.makeText(activity, name + " clicked!", Toast.LENGTH_SHORT).show();
//
//
//                        }
//                    }
//
//                    new AppPreferenceHelper(activity).setMultiDisplayData(jsonArray.toString());
               // eventListener.onEvent(2);
                // Log.e("JOSNArray",""+jsonArray.length());
                // Log.e("JOSNArray",""+jsonArray.toString());
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder
    {
        AppCompatTextView tv_name;
        CheckBox cb_status;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_name=itemView.findViewById(R.id.tv_status_name);
            cb_status=itemView.findViewById(R.id.cb_status_name);
        }
    }

}
