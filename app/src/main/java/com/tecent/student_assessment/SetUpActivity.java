package com.tecent.student_assessment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SetUpActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(SetUpActivity.this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Preparing to download...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        sharedPreferences = getSharedPreferences("studentAssessment", Context.MODE_PRIVATE);
        final String email = sharedPreferences.getString("email", "");
        final String password = sharedPreferences.getString("passw", "");
        //volley part start
        String url = ExtraFunctions.serverurl + "studentLoginData.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.dismiss();
                    JSONObject emp = (new JSONObject(response));
                    final SharedPreferences.Editor spe = sharedPreferences.edit();
                    spe.putString("userid", emp.getString("userid"));
                    spe.putString("name", emp.getString("name"));
                    spe.putString("gender", emp.getString("gender"));
                    spe.putString("branch", emp.getString("branch"));
                    spe.putString("semester", emp.getString("semester"));
                    spe.putString("college", emp.getString("college"));
                    spe.putString("university", emp.getString("university"));
                    spe.putString("verified", emp.getString("verified"));
                    spe.putString("ban", emp.getString("ban"));
                    spe.putString("joindate", emp.getString("joindate"));
                    spe.putString("userdp", emp.getString("userdp"));
                    spe.putString("posts", emp.getString("posts"));
                    spe.putString("doubts", emp.getString("doubts"));
                    spe.putString("answers", emp.getString("answers"));
                    spe.apply();
                    Intent intentNew = new Intent(SetUpActivity.this, MainActivity.class);
                    startActivity(intentNew);
                    finish();
                } catch (Exception exception) {
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(SetUpActivity.this, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("email", email);
                MyData.put("passw", password);
                return MyData;
            }
        };
        requestQueue.add(stringRequest);

    }
}
