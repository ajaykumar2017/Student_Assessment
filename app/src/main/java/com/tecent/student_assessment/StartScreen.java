package com.tecent.student_assessment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class StartScreen extends AppCompatActivity {

    boolean backPressed=false;
    SharedPreferences spf;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        changeStatusBarColor();



        spf= getSharedPreferences("studentAssessment", Context.MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!backPressed){
                    if(spf.getString("login", "").equals("true")) {
                        startActivity(new Intent(StartScreen.this, MainActivity.class));
                    }else{
                        startActivity(new Intent(StartScreen.this, StudentLoginActivity.class));
                    }
                    finish();
                }
            }
        },2000);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed=true;
    }
}

