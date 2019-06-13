package com.tecent.student_assessment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import org.json.JSONObject

import java.util.HashMap

import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressCustom
import cc.cloudist.acplibrary.ACProgressFlower
import kotlinx.android.synthetic.main.activity_student_login.*

class StudentRegistration : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etCreatePassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var rgGender: RadioGroup
    lateinit var rbMale: RadioButton
    lateinit var rbFemale: RadioButton
    lateinit var rbOthers: RadioButton
    lateinit var spinnerBranch: Spinner
    lateinit var spinnerSemester: Spinner
    lateinit var spinnerCollege: Spinner
    lateinit var spinnerUniversity: Spinner
    lateinit var checkBox: CheckBox
    lateinit var buttonRegister: Button
    lateinit var dialog: ACProgressFlower
    lateinit var sharedPreferences: SharedPreferences
    lateinit var requestQueue: RequestQueue

    lateinit var name: String
    lateinit var email: String
    lateinit var createPassword: String
    lateinit var confirmPassword: String
    var createPasswordInvisible:Boolean=true
    var confirmPasswordInvisible:Boolean=true


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_registration)

        requestQueue = Volley.newRequestQueue(this)
        sharedPreferences = getSharedPreferences("studentAssessment", Context.MODE_PRIVATE)

        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etCreatePassword = findViewById(R.id.et_createpassw)
        etConfirmPassword = findViewById(R.id.et_confirmpassw)

        rgGender = findViewById(R.id.radioGroup_gender)
        rbMale = findViewById(R.id.rad_male)
        rbFemale = findViewById(R.id.rad_female)
        rbOthers = findViewById(R.id.rad_others)

        checkBox = findViewById<View>(R.id.checkbox_meat) as CheckBox
        spinnerBranch = findViewById(R.id.sp_branch)
        val valuesBranch = resources.getStringArray(R.array.branches)
        val adapterBranch = ArrayAdapter(this, R.layout.spinner_item, valuesBranch)
        spinnerBranch.adapter = adapterBranch

        spinnerSemester = findViewById(R.id.sp_semester)
        val valuesSemesters = resources.getStringArray(R.array.semester)
        val adapterSemester = ArrayAdapter(this, R.layout.spinner_item, valuesSemesters)
        spinnerSemester.adapter = adapterSemester

        spinnerCollege = findViewById(R.id.sp_college)
        val valuesColleges = resources.getStringArray(R.array.collegelist)
        val adapterColleges = ArrayAdapter(this, R.layout.spinner_item, valuesColleges)
        spinnerCollege.adapter = adapterColleges

        spinnerUniversity = findViewById(R.id.sp_university)
        val valuesUniversity = resources.getStringArray(R.array.university)
        val adapterUniversity = ArrayAdapter(this, R.layout.spinner_item, valuesUniversity)
        spinnerUniversity.adapter = adapterUniversity

        buttonRegister = findViewById(R.id.btnRegister)
        //progress dialog
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE).text("Please wait....")
                .fadeColor(Color.BLACK).build()
        dialog.setCancelable(false)
        //view create password
        etCreatePassword.setOnTouchListener { view: View, event: MotionEvent ->
            if(event.action== MotionEvent.ACTION_UP){
                if(event.rawX >= (etCreatePassword.right - etCreatePassword.compoundDrawables[2].bounds.width())){
                    if(createPasswordInvisible){
                        createPasswordInvisible=false
                        etCreatePassword.transformationMethod=null
                        etCreatePassword.setCompoundDrawablesWithIntrinsicBounds(0,0,
                                R.drawable.ic_invisible_eye,0)
                    }else{
                        createPasswordInvisible=true
                        etCreatePassword.transformationMethod= PasswordTransformationMethod()
                        etCreatePassword.setCompoundDrawablesWithIntrinsicBounds(0,0,
                                R.drawable.ic_visible_eye,0)
                    }
                    true
                }
            }
            false
        }
        //view confirm password
        etConfirmPassword.setOnTouchListener { view: View, event: MotionEvent ->
            if(event.action== MotionEvent.ACTION_UP){
                if(event.rawX >= (etConfirmPassword.right - etConfirmPassword.compoundDrawables[2].bounds.width())){
                    if(confirmPasswordInvisible){
                        confirmPasswordInvisible=false
                        etConfirmPassword.transformationMethod=null
                        etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,
                                R.drawable.ic_invisible_eye,0)
                    }else{
                        confirmPasswordInvisible=true
                        etConfirmPassword.transformationMethod= PasswordTransformationMethod()
                        etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,
                                R.drawable.ic_visible_eye,0)
                    }
                    true
                }
            }
            false
        }

        buttonRegister.setOnClickListener {
            name = etName.text.toString()
            email = etEmail.text.toString()
            createPassword = etCreatePassword.text.toString()
            confirmPassword = etConfirmPassword.text.toString()
            if (name == "" || email == "" || createPassword == "" || confirmPassword == "" ||
                    rgGender.checkedRadioButtonId == -1 || spinnerBranch.selectedItem.toString().trim { it <= ' ' } == "Select Branch" ||
                    spinnerSemester.selectedItem.toString().trim { it <= ' ' } == "Select Semester" || spinnerCollege.selectedItem.toString().trim { it <= ' ' } == "Select College" ||
                    spinnerUniversity.selectedItem.toString().trim { it <= ' ' } == "Select University") {
                Toast.makeText(this@StudentRegistration, "Please fill out all mandatary details!", Toast.LENGTH_SHORT).show()
            } else if (!ExtraFunctions.isValidEmailId(email)) {
                Toast.makeText(this@StudentRegistration, "Please enter valid email id!", Toast.LENGTH_SHORT).show()
            } else if (createPassword.length < 8) {
                Toast.makeText(this@StudentRegistration, "Password must be at least 8 characters long!", Toast.LENGTH_SHORT).show()
            } else if (!ExtraFunctions.isValidEmailId(email.trim { it <= ' ' })) {
                Toast.makeText(this@StudentRegistration, "Invalid email address!", Toast.LENGTH_SHORT).show()
            } else if (createPassword != confirmPassword) {
                Toast.makeText(this@StudentRegistration, "Passwords don't match!", Toast.LENGTH_SHORT).show()
            } else if (!checkBox.isChecked) {
                Toast.makeText(this@StudentRegistration, "Please check the box!", Toast.LENGTH_SHORT).show()
            } else {
                //                    Toast.makeText(StudentRegistration.this, "All are filled", Toast.LENGTH_SHORT).show();

                val nameValue = name
                val emailValue = email
                val passwordValue = createPassword
                val selectedIdRadio = rgGender.checkedRadioButtonId
                // find the radiobutton by returned id
                val radioButton = findViewById<View>(selectedIdRadio) as RadioButton
                val genderValue = radioButton.text.toString()
                val branchValue = ExtraFunctions.getSmallBranch(spinnerBranch.selectedItem.toString())
                val semesterValue = ExtraFunctions.getSmallSemester(spinnerSemester.selectedItem.toString())
                val collegeValue = spinnerCollege.selectedItem.toString()
                val universityValue = ExtraFunctions.getSmallUniversity(spinnerUniversity.selectedItem.toString())

                //Toast.makeText(StudentRegistration.this, nameValue+" "+emailValue+" "+passwordValue+" "+" "+gender+" "+branchValue+" "+semesterValue+" "+collegeValue+" "+" "+universityValue, Toast.LENGTH_SHORT).show();
                if (ExtraFunctions.isNetworkStatusAvialable(this@StudentRegistration)) {
                    try {
                        dialog.show()
                        volleytest(nameValue, emailValue, passwordValue, genderValue, branchValue, semesterValue, collegeValue, universityValue)
                    } catch (e: Exception) {
                        dialog.dismiss()
                        Toast.makeText(this@StudentRegistration, "Error! Please try again later.", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this@StudentRegistration, "No Internet Connection!", Toast.LENGTH_SHORT).show()
                }

            }
        }


    }

    fun volleytest(nameValue: String, emailValue: String, passwordValue: String, genderValue: String, branchValue: String, semesterValue: String, collegeValue: String, universityValue: String) {
        val url = ExtraFunctions.serverurl + "studentRegistrationData.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response -> jsonParser(response) }, Response.ErrorListener {
            dialog.dismiss()
            Toast.makeText(this@StudentRegistration, "Error! Please try again later...", Toast.LENGTH_SHORT).show()
        }) {
            override fun getParams(): Map<String, String> {
                val MyData = HashMap<String, String>()
                MyData["name"] = nameValue
                MyData["email"] = emailValue
                MyData["passw"] = passwordValue
                MyData["gender"] = genderValue
                MyData["branch"] = branchValue
                MyData["semester"] = semesterValue
                MyData["college"] = collegeValue
                MyData["university"] = universityValue
                return MyData
            }
        }
        requestQueue.add(stringRequest)

        //volley part start

    }

    fun jsonParser(jsontext: String) {
        try {
            val emp = JSONObject(jsontext)
            val result = emp.getString("result")
            if (result == "alreadyExist") {
                dialog.dismiss()
                Toast.makeText(this@StudentRegistration, "Email already registered", Toast.LENGTH_SHORT).show()
            } else if (result == "successful") {
                dialog.dismiss()
                Toast.makeText(this@StudentRegistration, "Registration successful.", Toast.LENGTH_SHORT).show()
                val spe = sharedPreferences.edit()
                spe.putString("email", etEmail.text.toString())
                spe.putString("passw", etCreatePassword.text.toString())
                spe.putString("login", "true")
                spe.apply()
                Handler().postDelayed({
                    val intentNew = Intent(this@StudentRegistration, SetUpActivity::class.java)
                    startActivity(intentNew)
                    finish()
                }, 100)
            } else if (result == "error") {
                dialog.dismiss()
                Toast.makeText(this@StudentRegistration, "An error has occured. Please try again", Toast.LENGTH_SHORT).show()
            }
        } catch (exception: Exception) {
            dialog.dismiss()
            exception.printStackTrace()
        }

    }

    override fun onBackPressed() {
        val i = Intent(this, StudentLoginActivity::class.java)
        startActivity(i)
        finish()
    }
}
