package com.tecent.student_assessment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import cc.cloudist.acplibrary.ACProgressCustom;
import cc.cloudist.acplibrary.ACProgressFlower;

public class StudentRegistration extends AppCompatActivity {

    EditText etName, etEmail, etCreatePassword, etConfirmPassword;
    RadioGroup rgGender;
    RadioButton rbMale, rbFemale, rbOthers;
    Spinner spinnerBranch, spinnerSemester, spinnerCollege, spinnerUniversity;
    CheckBox checkBox;
    Button buttonRegister;
    ACProgressFlower dialog;
    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;

    String name,email,createPassword,confirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("studentAssessment", Context.MODE_PRIVATE);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etCreatePassword = findViewById(R.id.et_createpassw);
        etConfirmPassword = findViewById(R.id.et_confirmpassw);

        rgGender = findViewById(R.id.radioGroup_gender);
        rbMale = findViewById(R.id.rad_male);
        rbFemale = findViewById(R.id.rad_female);
        rbOthers = findViewById(R.id.rad_others);

        checkBox = (CheckBox) findViewById(R.id.checkbox_meat);
        spinnerBranch = findViewById(R.id.sp_branch);
        final String[] valuesBranch = getResources().getStringArray(R.array.branches);
        final ArrayAdapter<String> adapterBranch = new ArrayAdapter<String>(this, R.layout.spinner_item, valuesBranch);
        spinnerBranch.setAdapter(adapterBranch);

        spinnerSemester = findViewById(R.id.sp_semester);
        String[] valuesSemesters = getResources().getStringArray(R.array.semester);
        ArrayAdapter<String> adapterSemester = new ArrayAdapter<String>(this, R.layout.spinner_item, valuesSemesters);
        spinnerSemester.setAdapter(adapterSemester);

        spinnerCollege = findViewById(R.id.sp_college);
        String[] valuesColleges = getResources().getStringArray(R.array.collegelist);
        ArrayAdapter<String> adapterColleges = new ArrayAdapter<String>(this, R.layout.spinner_item, valuesColleges);
        spinnerCollege.setAdapter(adapterColleges);

        spinnerUniversity = findViewById(R.id.sp_university);
        String[] valuesUniversity = getResources().getStringArray(R.array.university);
        ArrayAdapter<String> adapterUniversity = new ArrayAdapter<String>(this, R.layout.spinner_item, valuesUniversity);
        spinnerUniversity.setAdapter(adapterUniversity);

        buttonRegister = findViewById(R.id.btnRegister);
        //progress dialog
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE).text("Please wait....")
                .fadeColor(Color.BLACK).build();
        dialog.setCancelable(false);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                email = etEmail.getText().toString();
                createPassword = etCreatePassword.getText().toString();
                confirmPassword = etConfirmPassword.getText().toString();
                if (name.equals("") || email.equals("") || createPassword.equals("") || confirmPassword.equals("") ||
                        rgGender.getCheckedRadioButtonId() == -1 || spinnerBranch.getSelectedItem().toString().trim().equals("Select Branch") ||
                        spinnerSemester.getSelectedItem().toString().trim().equals("Select Semester") || spinnerCollege.getSelectedItem().toString().trim().equals("Select College") ||
                        spinnerUniversity.getSelectedItem().toString().trim().equals("Select University")) {
                    Toast.makeText(StudentRegistration.this, "Please fill out all mandatary details!", Toast.LENGTH_SHORT).show();
                } else if (!ExtraFunctions.isValidEmailId(email.toString())){
                    Toast.makeText(StudentRegistration.this, "Please enter valid email id!", Toast.LENGTH_SHORT).show();
                }
                else if (createPassword.length() < 8) {
                    Toast.makeText(StudentRegistration.this, "Password must be at least 8 characters long!", Toast.LENGTH_SHORT).show();
                } else if (!ExtraFunctions.isValidEmailId(email.toString().trim())) {
                    Toast.makeText(StudentRegistration.this, "Invalid email address!", Toast.LENGTH_SHORT).show();
                } else if (!createPassword.toString().equals(confirmPassword.toString())) {
                    Toast.makeText(StudentRegistration.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                } else if (!checkBox.isChecked()) {
                    Toast.makeText(StudentRegistration.this, "Please check the box!", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(StudentRegistration.this, "All are filled", Toast.LENGTH_SHORT).show();

                    String nameValue = name;
                    String emailValue = email;
                    String passwordValue = createPassword;
                    int selectedIdRadio = rgGender.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    RadioButton radioButton = (RadioButton) findViewById(selectedIdRadio);
                    String genderValue = radioButton.getText().toString();
                    String branchValue = ExtraFunctions.getSmallBranch(spinnerBranch.getSelectedItem().toString());
                    String semesterValue = ExtraFunctions.getSmallSemester(spinnerSemester.getSelectedItem().toString());
                    String collegeValue = spinnerCollege.getSelectedItem().toString();
                    String universityValue = ExtraFunctions.getSmallUniversity(spinnerUniversity.getSelectedItem().toString());

                    //Toast.makeText(StudentRegistration.this, nameValue+" "+emailValue+" "+passwordValue+" "+" "+gender+" "+branchValue+" "+semesterValue+" "+collegeValue+" "+" "+universityValue, Toast.LENGTH_SHORT).show();
                    if (ExtraFunctions.isNetworkStatusAvialable(StudentRegistration.this)) {
                        try {
                            dialog.show();
                            volleytest(nameValue, emailValue, passwordValue, genderValue, branchValue, semesterValue, collegeValue, universityValue);
                        } catch (Exception e) {
                            dialog.dismiss();
                            Toast.makeText(StudentRegistration.this, "Error! Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(StudentRegistration.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


    }

    public void volleytest(final String nameValue, final String emailValue, final String passwordValue, final String genderValue, final String branchValue, final String semesterValue, final String collegeValue, final String universityValue) {
        String url = ExtraFunctions.serverurl + "studentRegistrationData.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                jsonParser(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(StudentRegistration.this, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("name", nameValue);
                MyData.put("email", emailValue);
                MyData.put("passw", passwordValue);
                MyData.put("gender", genderValue);
                MyData.put("branch", branchValue);
                MyData.put("semester", semesterValue);
                MyData.put("college", collegeValue);
                MyData.put("university", universityValue);
                return MyData;
            }
        };
        requestQueue.add(stringRequest);

        //volley part start

    }

    public void jsonParser(String jsontext) {
        try {
            JSONObject emp = (new JSONObject(jsontext));
            String result = emp.getString("result");
            if (result.equals("alreadyExist")) {
                dialog.dismiss();
                Toast.makeText(StudentRegistration.this, "Email already registered", Toast.LENGTH_SHORT).show();
            } else if (result.equals("successful")) {
                dialog.dismiss();
                Toast.makeText(StudentRegistration.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor spe = sharedPreferences.edit();
                spe.putString("email", etEmail.getText().toString());
                spe.putString("passw", etCreatePassword.getText().toString());
                spe.putString("login", "true");
                spe.apply();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intentNew = new Intent(StudentRegistration.this, SetUpActivity.class);
                        startActivity(intentNew);
                        finish();
                    }
                }, 100);
            } else if (result.equals("error")) {
                dialog.dismiss();
                Toast.makeText(StudentRegistration.this, "An error has occured. Please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception exception) {
            dialog.dismiss();
            exception.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, StudentLoginActivity.class);
        startActivity(i);
        finish();
    }
}
