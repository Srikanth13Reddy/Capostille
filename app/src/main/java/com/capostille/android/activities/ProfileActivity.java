package com.capostille.android.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.capostille.android.R;
import com.capostille.android.interfaces.SaveView;
import com.capostille.android.utils.ApiConstants;
import com.capostille.android.utils.SaveImpl;
import com.capostille.android.utils.SharedPrefs;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ProfileActivity extends AppCompatActivity implements SaveView
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static int RESULT_LOAD_IMAGE = 1012;
    SharedPrefs sharedPrefs;
    String id,token;
    CircleImageView iv;
    ProgressDialog progressDialog;
    AppCompatTextView tv_total,tv_pending,tv_process,tv_complete,tv_name,tv_type,tv_email,tv_mobile,tv_address;
    private String res;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog= ApiConstants.showProgressDialog(this,"Please wait....");
        findViews();
        getLoginData();
        getData();
    }

    private void findViews()
    {
        tv_total=findViewById(R.id.tv_total);
        iv=findViewById(R.id.profile_image);
        tv_complete=findViewById(R.id.tv_complete);
        tv_pending=findViewById(R.id.tv_pending);
        tv_process=findViewById(R.id.tv_process);
        tv_name=findViewById(R.id.tv_name);
        tv_type=findViewById(R.id.tv_type);
        tv_address=findViewById(R.id.tv_address);
        tv_email=findViewById(R.id.tv_email);
        tv_mobile=findViewById(R.id.tv_mobile);
    }

    private void getData()
    {
        progressDialog.show();
        new SaveImpl(this).handleSave(new JSONObject(),"saasuser?saasUserId="+id,"GET","",token);
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
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSaveSucess(String code, String response, String type)
    {
        progressDialog.dismiss();
        Log.e("ProfileStatus",response);
        if (code.equalsIgnoreCase("200"))
        {
           assignData(response);
        }else if (code.equalsIgnoreCase("401"))
        {
            ApiConstants.logOut(this);
        }

    }

    @SuppressLint("SetTextI18n")
    private void assignData(String response) {
        try {
            JSONObject jss=new JSONObject(response);
            if (jss.optString("status").equalsIgnoreCase("Success")) {
                JSONObject jsonObject =jss.getJSONObject("data");
                String totalRequest = jsonObject.optString("totalRequest");
                String pendingRequests = jsonObject.optString("pendingRequests");
                String inprogressRequests = jsonObject.optString("inprogressRequests");
                String completedRequests = jsonObject.optString("completedRequests");
                JSONObject js = jsonObject.getJSONObject("user");
                String profileImage = js.optString("profileImage");
                String email = js.optString("email");
                String phoneNumber = js.optString("phoneNumber");
                String name = js.optString("name");
                String roleName = js.optString("roleName");
                String fullAddress = js.optString("fullAddress");
                String countryCode = js.optString("countryCode");
                if (profileImage.equalsIgnoreCase("")) {
                    iv.setImageResource(R.drawable.profile_d);
                } else {
                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_d).into(iv);
                }

                tv_total.setText("" + totalRequest);
                tv_pending.setText("" + pendingRequests);
                tv_process.setText("" + inprogressRequests);
                tv_complete.setText("" + completedRequests);
                tv_name.setText(name);
                tv_email.setText(email);
                if (countryCode.contains("+")) {
                    tv_mobile.setText(countryCode + " " + phoneNumber);
                } else {
                    tv_mobile.setText("+" + countryCode + " " + phoneNumber);
                }

                tv_type.setText(roleName);
                tv_address.setText(fullAddress);
            }else {
                Toast.makeText(this, ""+jss.optString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveFailure(String error)
    {
        progressDialog.dismiss();
        Log.e("ProfileStatus",error);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        Intent returnIntent = new Intent(this,HomeActivity.class);
        returnIntent.putExtra("result","profile");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void uploadImage(String file, String id)
    {
         progressDialog.show();
        OkHttpClient myOkHttpClient = new OkHttpClient.Builder()
                .build();
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fileName","profile")
                .addFormDataPart("file", String.valueOf("profile"), RequestBody.create(MediaType.parse("image/png"), new File(file)))
                .build();

        okhttp3.Request request = null;

        request = new okhttp3.Request.Builder()
                .url(ApiConstants.BaseUrl+"profilechange?saasUserId="+id)
                .header("Content-Type","application/json; charset=utf-8")
                .addHeader("Authorization", "Bearer "+token)
                .post(body)
                .build();

        Callback updateUICallback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, final IOException e)
            {

                Log.d("Data", "onFailure called during authentication " + e.getMessage());

               looper("Fail");
            }


            @Override
            public void onResponse(@NonNull Call call, final okhttp3.Response response) throws IOException {
                Log.d("Data", "onFailure called during authentication " + response);
                res= response.body().string();
                if (response.code()==200)
                {
                   looper("Success");
                }
                else {
                   looper("500Error");
                }
            }
        };

        if (request != null) {
            myOkHttpClient.newCall(request).enqueue(updateUICallback);
        }
    }

    public void showDialogImage(View view)
    {
        final ArrayList<String> selectedItems = new ArrayList<String>();
        final CharSequence[] items = {"Photo Capture", "Choose From Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].toString().equalsIgnoreCase("Photo Capture")) {
                    //galleryPermission();
                    Camerapermissions();
                } else if (items[which].toString().equalsIgnoreCase("Choose From Gallery")) {
                    galleryPermission();
                }


            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void Camerapermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            openCamera();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            openCamera();
                            // permission is denied permenantly, navigate user to app settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    public void galleryPermission()
    {
        Dexter.withActivity(this)

                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                .withListener(new PermissionListener() {

                    @Override

                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        openGallery();

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

    private void openGallery() {

        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void openCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }
    private void looper(final String message) {
        Handler handler = new Handler(getMainLooper());
        handler.post(() -> {
            progressDialog.dismiss();
            if (message.equalsIgnoreCase("Success")) {

                   // progressDialog.dismiss();
                    JSONObject js= null;
                    try {
                        js = new JSONObject(res);
                        if (js.optString("status").equalsIgnoreCase("Success"))
                        {
                            //getData();
                            Toast.makeText(this, ""+js.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            } else if (message.equalsIgnoreCase("Fail"))
            {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            } else if (message.equalsIgnoreCase("500Error")) {
                Toast.makeText(this, "Some thing went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Uri getImageUri(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), src, "title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = null;
            if (extras != null) {
                imageBitmap = (Bitmap) extras.get("data");
            }
            // tv_path.setVisibility(View.VISIBLE);
            iv.setImageBitmap(imageBitmap);
            Uri uri = null;
            if (imageBitmap != null) {
                uri = getImageUri(imageBitmap, Bitmap.CompressFormat.PNG, 100);
            }
            String path = getRealPathFromURI(ProfileActivity.this, uri);
            uploadImage(path, id);


            // tv_path.setText(encoded);

        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();


            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            assert selectedImage != null;
            @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }

            assert cursor != null;
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            iv.setImageURI(selectedImage);

            // File file = new File(getCacheDir().getAbsolutePath()+"/"+picturePath);

            uploadImage(picturePath, id);
        }

    }
}
