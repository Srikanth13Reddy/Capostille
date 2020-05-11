package com.capostille.android.adapters;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;

import com.capostille.android.R;
import com.capostille.android.models.DocumentsModel;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DocumentsAdapter extends BaseAdapter
{

    String names[]={"Birth certificate","Death Certificate","Diploma"};
    int images[]={R.drawable.request, R.drawable.request, R.drawable.request};

    Context context;
    ArrayList<DocumentsModel> arrayList;
    private DownloadManager dm;
    long downloadID;

    public DocumentsAdapter(Context context, ArrayList<DocumentsModel> arrayList) {
        this.context = context;
        this.arrayList=arrayList;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
       LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View v=layoutInflater.inflate(R.layout.document_style,parent,false);
        ImageView doc_img= v.findViewById(R.id.doc_img);
        AppCompatTextView doc_tv_title= v.findViewById(R.id.doc_tv_title);
//        doc_img.setImageResource(images[position]);
        doc_tv_title.setText(arrayList.get(position).getFileName());
       String type= arrayList.get(position).getFileType();
          if (type.contains("pdf"))
          {
              doc_img.setImageResource(R.drawable.pdf);
          }else if (type.contains("jpg")||type.contains("png")||type.contains("jpeg"))
          {
              doc_img.setImageResource(R.drawable.photo);
          }
          else if (type.contains("doc")||type.contains("msword"))
          {
              doc_img.setImageResource(R.drawable.doc);
          }
          else if (type.contains("txt")||type.contains("plain"))
          {
              doc_img.setImageResource(R.drawable.document);
          }
          else if (type.contains("zip")||type.contains("x-zip-compressed"))
          {
              doc_img.setImageResource(R.drawable.compressed);
          }
          else if (type.contains("gif"))
          {
              doc_img.setImageResource(R.drawable.gif);
          }



//        doc_tv_title.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
////                Intent i=new Intent(context, DocumentViewActivity.class);
////                i.putExtra("url",arrayList.get(position).getUrl());
////                context.startActivity(i);
////                String name= arrayList.get(position).getFileName();
////                String filetype= arrayList.get(position).getFileType();
////                String url= arrayList.get(position).getUrl();
////
////                if (filetype.equalsIgnoreCase("zip")||filetype.contains("doc")||filetype.contains("pdf"))
////                {
////
////                }
////                else {
////
////                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
////                    context.startActivity(intent);
////                }
//
//            }
//        });
        return v;
    }

    public void shouldOverrideUrlLoading(String url, String extension, String name) {

        boolean value = true;
        //String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
              DownloadManager mdDownloadManager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(url));
                    // String name= URLUtil.guessFileName(url,null,MimeTypeMap.getFileExtensionFromUrl(url));
                    File destinationFile = new File(Environment.getExternalStorageDirectory(),name);
                    request.setDescription("Downloading...");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    // request.setDestinationUri(Uri.fromFile(destinationFile));
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name);
                    mdDownloadManager.enqueue(request);


        }


    }


        private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Fetching the download id received with the broadcast
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //Checking if the received broadcast is for our enqueued download by matching download id
                if (downloadID == id) {
                    Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
                }
            }
        };

}
