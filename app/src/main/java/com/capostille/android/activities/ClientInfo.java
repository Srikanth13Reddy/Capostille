package com.capostille.android.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;

import com.capostille.android.R;
import com.capostille.android.adapters.DocumentsAdapter;
import com.capostille.android.adapters.DocumentsTypeAdapter;
import com.capostille.android.interfaces.SaveView;
import com.capostille.android.models.DocumentsDetailsModel;
import com.capostille.android.models.DocumentsModel;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.MyListView;
import com.capostille.android.utils.SaveImpl;
import com.capostille.android.utils.SharedPrefs;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static com.capostille.android.utils.ApiConstants.toTitleCase;

public class ClientInfo extends AppCompatActivity implements SaveView , PopupMenu.OnMenuItemClickListener
{

    MyListView lv_documents,lv_documents1;
    String rId,status;
    String id,roleId,token;
    ProgressDialog progressDialog;
    SharedPrefs sharedPrefs;
    AppCompatTextView tv_client_name,tv_client_shipping_address,tv_service,tv_assign_;
    private String userRequestDetailsId;
    AppCompatImageView edit_iv_status;
    private long downloadID;
    ProgressDialog progressDialog_;
    String assignToName;
    ArrayList<String> unverified=new ArrayList<>();
    ArrayList<String> verified=new ArrayList<>();
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                progressDialog_.dismiss();
                Toast.makeText(ClientInfo.this, "Downloaded Successfully in downloads", Toast.LENGTH_SHORT).show();
               // startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
            }
        }
    };
    private String assignTo;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_info);
        getSupportActionBar().setTitle("Client Info");
        progressDialog_=new ProgressDialog(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog= ApiConstants.showProgressDialog(this,"Please wait....");
        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
       // beginDownload("https://notaryfiles.s3.us-east-2.amazonaws.com/sample_20200331-113125","sri.zip");
        getLoginData();
        Bundle b= getIntent().getExtras();
        if (b != null) {
            rId= b.getString("rId");
            status= b.getString("status");
            assignToName=b.getString("notary");
            assignTo=b.getString("notaryId");
            getRequestData();
        }

        findViews();

        visibleViews();



        tv_assign_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ClientInfo.this,NotariesListActivity.class);
                i.putExtra("rId",rId);
                i.putExtra("assignedTo",assignTo);
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }
    private void visibleViews()
    {
        if (roleId!=null&&roleId.equalsIgnoreCase("1")&&status.equalsIgnoreCase("New")&&assignToName.isEmpty())
        {
            tv_assign_.setVisibility(View.VISIBLE);
            tv_assign_.setText("Assign");
        }
//        else if (roleId!=null&&roleId.equalsIgnoreCase("1")&&status.equalsIgnoreCase("Pending"))
//        {
//            tv_assign_.setVisibility(View.VISIBLE);
//            tv_assign_.setText("Re Assign");
//        }
        else if (roleId!=null&&roleId.equalsIgnoreCase("1")&&status.equalsIgnoreCase("New")&&!assignToName.isEmpty())
        {
            tv_assign_.setVisibility(View.VISIBLE);
            tv_assign_.setText("Re Assign");
        }
        else {
            tv_assign_.setVisibility(View.GONE);
        }

        if (status.equalsIgnoreCase("Completed")||status.equalsIgnoreCase("Rejected"))
        {
           // edit_iv_status.setVisibility(View.GONE);
            edit_iv_status.setEnabled(false);
            edit_iv_status.setImageResource(R.drawable.edit_);
        }
        else {
            edit_iv_status.setEnabled(true);
            edit_iv_status.setImageResource(R.drawable.edit);
           // edit_iv_status.setVisibility(View.VISIBLE);
        }
    }
    private void getLoginData()
    {
        sharedPrefs=new SharedPrefs(this);
        try {
            if (sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA)!=null)
            {
                JSONObject js=new JSONObject(sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA));
                id= js.optString("id");
                token= js.optString("token");
                roleId= js.optString("roleId");
                Log.e("data",sharedPrefs.getLoginData().get(SharedPrefs.LOGIN_DATA));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void findViews()
    {
        lv_documents= findViewById(R.id.lv_documents);
        edit_iv_status= findViewById(R.id.edit_iv_status);
        lv_documents1= findViewById(R.id.lv_documents1);
        tv_assign_= findViewById(R.id.tv_assign_);
        tv_client_name= findViewById(R.id.tv_client_name);
        tv_client_shipping_address= findViewById(R.id.tv_client_shipping_address);
       // tv_service= findViewById(R.id.tv_service);
       // lv_documents.setAdapter(new DocumentsAdapter(this));





    }
    public void getRequestData()
    {
        progressDialog.show();
        new SaveImpl(this).handleSave(new JSONObject(),"requestbyid?requestDetailsId="+rId,"GET","",token);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return true;
    }

    public void onBackPressed()
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result","notificationResult");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onSaveSucess(String code, String response,String type)
    {
        progressDialog.dismiss();
        Log.e("requestbyid",response);
        ArrayList<DocumentsModel> arrayList=new ArrayList<>();
        ArrayList<DocumentsDetailsModel> arrayList1=new ArrayList<>();
        verified.clear();
        unverified.clear();

        if (type.equalsIgnoreCase("status"))
        {
            try {
                JSONObject js=new JSONObject(response);
                String status= js.optString("status");
                if (status.equalsIgnoreCase("Success"))
                {
                   // finish();
                    onBackPressed();
                }else {
                    Toast.makeText(this, ""+js.optString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {

            if (code.equalsIgnoreCase("200"))
            {
                assignData(response);
            }
            else if (code.equalsIgnoreCase("500"))
            {
                try {
                    JSONObject js=new JSONObject(response);
                    Toast.makeText(this, ""+js.optString("error"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }



    }

    @SuppressLint("SetTextI18n")
    private void assignData(String response)
    {
        ArrayList<DocumentsModel> arrayList=new ArrayList<>();
        ArrayList<DocumentsDetailsModel> arrayList1=new ArrayList<>();
        verified.clear();
        unverified.clear();
        try {
            JSONObject jss=new JSONObject(response);
            if (jss.optString("status").equalsIgnoreCase("Success")) {
                JSONObject js = jss.getJSONObject("data");
                JSONObject jsonObject = js.getJSONObject("request");
                String fullAddress = jsonObject.optString("fullAddress");
                String name = jsonObject.optString("name");
                String requestCode = jsonObject.optString("requestCode");
                userRequestDetailsId = jsonObject.optString("userRequestDetailsId");
                tv_client_shipping_address.setText(toTitleCase(fullAddress));
                tv_client_name.setText(requestCode+" - "+toTitleCase(name));
                String documents = js.optString("documents");
                String documentdetails = js.optString("documentdetails");
                JSONArray jaa = new JSONArray(documentdetails);
                for (int j = 0; j < jaa.length(); j++) {
                    JSONObject json = jaa.getJSONObject(j);
                    String documentDetailsId = json.optString("documentDetailsId");
                    String serviceName = json.optString("serviceName");
                    String documentName = json.optString("documentName");
                    String stateName = json.optString("stateName");
                    String status = json.optString("status");
                    String shortName=json.optString("shortName");
                    String documentTypeId=json.optString("documentTypeId");
                    String otherDocumentName=json.optString("otherDocumentName");
                    if (status.equalsIgnoreCase("Unverified")) {
                        unverified.add(status);
                    } else if (status.equalsIgnoreCase("Verified")) {
                        verified.add(status);
                    }

                    DocumentsDetailsModel detailsModel = new DocumentsDetailsModel();
                    detailsModel.setDocumentDetailsId(documentDetailsId);
                    detailsModel.setServiceName(serviceName);
                    detailsModel.setStateName(stateName);
                    detailsModel.setShortName(shortName);
                    detailsModel.setStatus(status);
                    detailsModel.setOtherDocumentName(otherDocumentName);
                    detailsModel.setDocumentName(documentName);
                    detailsModel.setDocumentTypeID(documentTypeId);
                    arrayList1.add(detailsModel);
                }
                DocumentsTypeAdapter typeAdapter = new DocumentsTypeAdapter(arrayList1, this, token, status);
                JSONArray ja = new JSONArray(documents);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject json = ja.getJSONObject(i);
                    String fileName = json.optString("fileName");
                    String url = json.optString("url");
                    String fileType = json.optString("fileType");
                    //String status= json.optString("status");
                    // String stateId= json.optString("stateId");
                    // String serviceId= json.optString("serviceId");
                    String userDocumentId = json.optString("requestId");
                    String userRequestDetailsId = json.optString("documentId");
                    DocumentsModel documentsModel = new DocumentsModel();
                    documentsModel.setFileName(fileName);
                    documentsModel.setUrl(url);
                    documentsModel.setStatus(status);
                    documentsModel.setFileType(fileType);
                    //  documentsModel.setStateId(stateId);
                    // documentsModel.setServiceId(serviceId);
                    documentsModel.setUserDocumentId(userDocumentId);
                    documentsModel.setUserRequestDetailsId(userRequestDetailsId);
                    arrayList.add(documentsModel);
                }
                DocumentsAdapter documentsAdapter = new DocumentsAdapter(this, arrayList);
                lv_documents.setAdapter(documentsAdapter);
                lv_documents1.setAdapter(typeAdapter);

                lv_documents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String type = arrayList.get(position).getFileType();
                        String url = arrayList.get(position).getUrl();
                        String name1 = arrayList.get(position).getFileName();
                        if (type.contains("doc") || type.contains("zip") || type.contains("pdf")) {
                            AlertDialog.Builder alb = new AlertDialog.Builder(ClientInfo.this);
                            alb.setMessage("Do you want to download " + name1);
                            alb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //beginDownload(url,name1);
                                    galleryPermission(url, name1);
                                }
                            });
                            alb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alb.create().show();
                        } else if (type.contains("png") || type.contains("jpg") || type.contains("txt")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    url)));
                        } else {
//                            Intent i=new Intent(ClientInfo.this, DocumentViewActivity.class);
//                           i.putExtra("url",arrayList.get(position).getUrl());
//                          startActivity(i);

                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, ""+jss.optString("message"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveFailure(String error) {
        progressDialog.dismiss();
        Log.e("requestbyid",error);
    }
    public void edit(View view)
    {
      //  showPopup(view);


        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(ClientInfo.this);
        inflater.inflate(R.menu.edit_style, popup.getMenu());
        Menu popupmenu= popup.getMenu();
        MenuItem rejected= popupmenu.findItem(R.id.rejected);
        MenuItem complete= popupmenu.findItem(R.id.complete);
        MenuItem progress= popupmenu.findItem(R.id.progress);
        MenuItem pending= popupmenu.findItem(R.id.pending);


        SpannableString s = new SpannableString("Reject");
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), 0, s.length(), 0);
        rejected.setTitle(s);
        SpannableString s1 = new SpannableString("Pending");
        s1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pending)), 0, s1.length(), 0);
        pending.setTitle(s1);
        SpannableString s2 = new SpannableString("Inprogress");
        s2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.processing)), 0, s2.length(), 0);
        progress.setTitle(s2);
        SpannableString s3 = new SpannableString("Complete");
        s3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.completed)), 0, s3.length(), 0);
        complete.setTitle(s3);


       if (status.equalsIgnoreCase("New"))
       {
          rejected.setVisible(true);
           complete.setVisible(true);
           progress.setVisible(true);
           pending.setVisible(true);
       }
       else if (status.equalsIgnoreCase("Inprogress"))
       {
           rejected.setVisible(true);
           complete.setVisible(true);
           progress.setVisible(false);
           pending.setVisible(true);
       }
       else if (status.equalsIgnoreCase("Pending"))
       {
           rejected.setVisible(true);
           complete.setVisible(true);
           progress.setVisible(true);
           pending.setVisible(false);
       }
        popup.show();


    }
    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        //Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId())
        {
            case R.id.rejected:
                // do your code

                if (unverified.size()>0)
                {
                    showTextDialog("Rejected");
                }else {
                    showAlertDialogPending();
                }

                return true;
            case R.id.pending:
                // do your code


                if (unverified.size()>0)
                {
                    showTextDialog("Pending");
                }else {
                    showAlertDialogPending();
                }

                return true;
            case R.id.progress:
                // do your code

                if (unverified.size()==0&&verified.size()>0)
                {
                    changeStatus("Inprogress","");
                }else {
                    showAlertDialog();
                }
                return true;
            case R.id.complete:
                // do your code
                if (unverified.size()==0&&verified.size()>0)
                {
                    changeStatus("Completed","");
                }else {
                    showAlertDialog();
                }

                return true;
            default:
                return false;
        }
    }
    private void showAlertDialog()
    {
        AlertDialog.Builder alb=new AlertDialog.Builder(this);
        alb.setMessage("Please verify the toggle button");
        alb.setCancelable(false);
        alb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alb.create().show();
    }
    private void showAlertDialogPending()
    {
        AlertDialog.Builder alb=new AlertDialog.Builder(this);
        alb.setCancelable(false);
        alb.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
        alb.setMessage("Please verify the toggle button");
        alb.create().show();
    }
    private void showTextDialog(String status)
    {
        AlertDialog.Builder alb=new AlertDialog.Builder(this);
        LayoutInflater layoutInflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=layoutInflater.inflate(R.layout.pending_dialog,null,false);
        AppCompatEditText et=v.findViewById(R.id.et_text);
        Button btn_acpt=v.findViewById(R.id.btn_acpt);
        Button btn_cancel=v.findViewById(R.id.btn_cancel);
        if (status.equalsIgnoreCase("Pending"))
        {
           btn_acpt.setBackgroundResource(R.drawable.pending_drawable);
           btn_acpt.setText("Pending");
        }
        else if (status.equalsIgnoreCase("Rejected"))
        {
            btn_acpt.setBackgroundResource(R.drawable.reject_drawable);
            btn_acpt.setText("Reject");
        }

        alb.setView(v);
        alb.setCancelable(false);
        AlertDialog ad= alb.create();
        ad.show();
        btn_acpt.setOnClickListener(v1 -> {
            if (et.getText().toString().equalsIgnoreCase(""))
            {
                Toast.makeText(this, "Please enter comments", Toast.LENGTH_SHORT).show();
            }
            else {
                ad.cancel();
                changeStatus(status,et.getText().toString());
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });
    }
    private void changeStatus(String status,String comments)
    {
        progressDialog.show();
        JSONObject js=new JSONObject();
        try {
            js.put("status",status);
            js.put("comments",comments);
            js.put("saasUserId",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
          new SaveImpl(this).handleSave(js,"/request?requestId="+userRequestDetailsId,"PUT","status",token);

    }
    private void beginDownload(String url,String name)
    {
       // File file=new File(getExternalFilesDir(null),"Dummy");
        /*
        Create a DownloadManager.Request with all the information necessary to start the download
         */


        progressDialog_.setMessage("Downloading Please Wait.....");
        progressDialog_.show();
        DownloadManager mdDownloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));
        // String name= URLUtil.guessFileName(url,null,MimeTypeMap.getFileExtensionFromUrl(url));
        File destinationFile = new File(Environment.getExternalStorageDirectory(),name);
        request.setDescription("Downloading...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // request.setDestinationUri(Uri.fromFile(destinationFile));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name);
        downloadID= mdDownloadManager.enqueue(request);
       // Cursor c=mdDownloadManager.query(new DownloadManager.Query().setFilterById(downloadID));



    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
    public void galleryPermission(String url, String name1)
    {
        Dexter.withActivity(this)

                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                .withListener(new PermissionListener() {

                    @Override

                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        beginDownload(url,name1);

                    }

                    @Override

                    public void onPermissionDenied(PermissionDeniedResponse response) {

                        // check for permanent denial of permission

                        if (response.isPermanentlyDenied()) {

                            // navigate user to app settings

                        }

                    }

                    @Override

                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();

                    }

                }).check();
    }

}
