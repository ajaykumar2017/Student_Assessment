package com.tecent.student_assessment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class ExtraFunctions {
    public static String serverurl="https://www.sas.a3creators.co.in/project/";
    public static  String rootdir= Environment.getExternalStorageDirectory() +"/Android/data/com.tecent.studentAssessment/";
    public static String sharedPreferencesId="studentAssessment";
    public static String sharedPreferencesLikeId="postLikes";
    public static String ROOTMAIN=Environment.getExternalStorageDirectory()+"/";
    public static String getFullBranch(String branch){
        switch (branch){
            case "cse":
                branch="Computer Science and Engineering";
                break;
            case "ece":
                branch="Electronics and Communication Engineering";
                break;
            case "me":
                branch="Mechanical Engineering";
                break;
            case "ee":
                branch="Electrical Engineering";
                break;
            case "ce":
                branch="Civil Engineering";
                break;
            default:
                branch="Others";
                break;
        }
        return branch;

    }

    public static String getFullUniversity(String university){
        switch (university){
            case "KU":
                university="Kolhan University";
                break;
            case "JUT":
                university="Jharkhand University of Technology";
                break;
            default:
                university="Others";
                break;
        }
        return university;

    }

    public static String getFullSemester(String semester){
        switch (semester){
            case "1":
                semester="1st Semester";
                break;
            case "2":
                semester="2nd Semester";
                break;
            case "3":
                semester="3rd Semester";
                break;
            case "4":
                semester="4th Semester";
                break;
            case "5":
                semester="5th Semester";
                break;
            case "6":
                semester="6th Semester";
                break;
            case "7":
                semester="7th Semester";
                break;
            case "8":
                semester="8th Semester";
                break;
            default:
                semester="Others";
                break;
        }
        return semester;
    }

    public static String getSmallUniversity(String university){
        switch (university){
            case "Jharkhand University of Technology":
                university="JUT";
                break;
            case "Kolhan University":
                university="KU";
                break;
            case "All":
                university="All";
                break;
            case "Others":
                university="Others";
                break;
        }
        return university;
    }

    public static String getSmallBranch(String branch){
        switch (branch){
            case "Computer Science and Engineering":
                branch="cse";
                break;
            case "Electronics and Communication Engineering":
                branch="ece";
                break;
            case "Mechanical Engineering":
                branch="me";
                break;
            case "Electrical Engineering":
                branch="ee";
                break;
            case "Civil Engineering":
                branch="ce";
                break;
            default:
                break;
        }
        return branch;

    }

    public static String getSmallSemester(String semester){
        switch (semester){
            case "1st Semester":
                semester="1";
                break;
            case "2nd Semester":
                semester="2";
                break;
            case "3rd Semester":
                semester="3";
                break;
            case "4th Semester":
                semester="4";
                break;
            case "5th Semester":
                semester="5";
                break;
            case "6th Semester":
                semester="6";
                break;
            case "7th Semester":
                semester="7";
                break;
            case "8th Semester":
                semester="8";
                break;
            default:
                break;
        }
        return semester;
    }

    public static boolean isValidEmailId(String emailId){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (emailId.matches(emailPattern))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                if(netInfos.isConnected())
                    return true;
        }
        return false;
    }

    static ImageRequest createImageRequestFromUrl(String url, final ImageView imageView) {
        return new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, null,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
    }
    static boolean isValidPostId(String postId){
        return postId.matches("[0-9]+");
    }

    static void logOut(Activity context){
        context.getSharedPreferences(ExtraFunctions.sharedPreferencesId, 0)
                .edit().clear().apply();
        context.getSharedPreferences(ExtraFunctions.sharedPreferencesLikeId, 0)
                .edit().clear().apply();
        context.startActivity(new Intent(context,StudentLoginActivity.class));
        context.finish();
    }

}

