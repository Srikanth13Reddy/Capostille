package com.capostille.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.capostille.android.R;
import com.capostille.android.utils.SharedPrefs;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Log.e("pName",""+getPackageName());

                if (new SharedPrefs(SplashActivity.this).isLoggedIn())
                {
                    finish();
                    Intent i=new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
                else
                {
                    finish();
                    Intent i=new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }



//                    if (new SharedPrefs(WelcomeActivity.this).isLoggedIn())
//                    {
//                        Intent i=new Intent(WelcomeActivity.this, HomeActivity.class);
//                        startActivity(i);
//                    }
//                    else {
//                        Intent i=new Intent(WelcomeActivity.this, LoginQZ.class);
//                        startActivity(i);
//                    }

            }
        },4000);
    }
}
