package com.tecent.student_assessment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class StudentLoginActivity extends AppCompatActivity {

    EditText emailId, password;
    SharedPreferences sharedPreferences;
    ACProgressFlower dialog;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);


        emailId = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE).text("Please wait....")
                .fadeColor(Color.BLACK).build();
        dialog.setCancelable(false);
        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("studentAssessment", Context.MODE_PRIVATE);

    }

    public void loginVerify(View view) {
        String email = emailId.getText().toString().trim();
        String passw = password.getText().toString().trim();
        if (!(email.equals("") || passw.equals(""))) {
            if (ExtraFunctions.isNetworkStatusAvialable(StudentLoginActivity.this)) {
                try {
                    dialog.show();
                    volleyLoginData(email, passw);
                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(StudentLoginActivity.this, "Error! Please try again later.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(StudentLoginActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid Credientials", Toast.LENGTH_SHORT).show();
        }

    }

    //google volley part start
    public void volleyLoginData(final String emailValue, final String passwordValue) {
        String url = ExtraFunctions.serverurl + "studentLoginVerify.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                jsonParser(response, emailValue, passwordValue);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(StudentLoginActivity.this, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("email", emailValue);
                MyData.put("passw", passwordValue);
                return MyData;
            }
        };
        requestQueue.add(stringRequest);

        //volley part start

    }

    public void jsonParser(String jsontext, String emailText, String passwordText) {
        try {
            JSONObject emp = (new JSONObject(jsontext));
            String result = emp.getString("result");
            if (result.equals("Email not Registered")) {
                dialog.dismiss();
                Toast.makeText(StudentLoginActivity.this, "Email-ID not registered", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intentNew = new Intent(StudentLoginActivity.this, StudentRegistration.class);
                        startActivity(intentNew);
                        finish();
                    }
                }, 1000);
            } else if (result.equals("Successful")) {
                dialog.dismiss();
                Toast.makeText(StudentLoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor spe = sharedPreferences.edit();
                spe.putString("email", emailText);
                spe.putString("passw", passwordText);
                spe.putString("login", "true");
                spe.commit();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intentNew = new Intent(StudentLoginActivity.this, SetUpActivity.class);
                        startActivity(intentNew);
                        finish();
                    }
                }, 100);
            } else if (result.equals("Invalid password")) {
                dialog.dismiss();
                Toast.makeText(StudentLoginActivity.this, "Invalid Password. Please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception exception) {
            dialog.dismiss();
            exception.printStackTrace();
        }
    }

    public void register(View view) {
        Intent intent = new Intent(this, StudentRegistration.class);
        startActivity(intent);
    }


    public void forgetPassword(View view) {

    }
}

