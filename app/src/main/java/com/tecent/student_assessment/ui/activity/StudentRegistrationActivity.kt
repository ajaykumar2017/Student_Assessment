package com.tecent.student_assessment.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import org.json.JSONObject

import java.util.HashMap

import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.tecent.student_assessment.R.array
import com.tecent.student_assessment.R.drawable
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.utils.ExtraFunctions
import com.tecent.student_assessment.utils.ExtraFunctions.getSmallBranch

class StudentRegistrationActivity : AppCompatActivity() {

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
        setContentView(
            layout.activity_student_registration
        )

        requestQueue = Volley.newRequestQueue(this)
        sharedPreferences = getSharedPreferences("studentAssessment", Context.MODE_PRIVATE)

        etName = findViewById(id.et_name)
        etEmail = findViewById(id.et_email)
        etCreatePassword = findViewById(
            id.et_createpassw
        )
        etConfirmPassword = findViewById(
            id.et_confirmpassw
        )

        rgGender = findViewById(
            id.radioGroup_gender
        )
        rbMale = findViewById(id.rad_male)
        rbFemale = findViewById(id.rad_female)
        rbOthers = findViewById(id.rad_others)

        checkBox = findViewById<View>(
            id.checkbox_meat
        ) as CheckBox
        spinnerBranch = findViewById(
            id.sp_branch
        )
        val valuesBranch = resources.getStringArray(
            array.branches
        )
        val adapterBranch = ArrayAdapter(this,
            layout.spinner_item, valuesBranch)
        spinnerBranch.adapter = adapterBranch

        spinnerSemester = findViewById(
            id.sp_semester
        )
        val valuesSemesters = resources.getStringArray(
            array.semester
        )
        val adapterSemester = ArrayAdapter(this,
            layout.spinner_item, valuesSemesters)
        spinnerSemester.adapter = adapterSemester

        spinnerCollege = findViewById(
            id.sp_college
        )
        val valuesColleges = resources.getStringArray(
            array.collegelist
        )
        val adapterColleges = ArrayAdapter(this,
            layout.spinner_item, valuesColleges)
        spinnerCollege.adapter = adapterColleges

        spinnerUniversity = findViewById(
            id.sp_university
        )
        val valuesUniversity = resources.getStringArray(
            array.university
        )
        val adapterUniversity = ArrayAdapter(this,
            layout.spinner_item, valuesUniversity)
        spinnerUniversity.adapter = adapterUniversity

        buttonRegister = findViewById(
            id.btnRegister
        )
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
                            drawable.ic_invisible_eye,0)
                    }else{
                        createPasswordInvisible=true
                        etCreatePassword.transformationMethod= PasswordTransformationMethod()
                        etCreatePassword.setCompoundDrawablesWithIntrinsicBounds(0,0,
                            drawable.ic_visible_eye,0)
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
                            drawable.ic_invisible_eye,0)
                    }else{
                        confirmPasswordInvisible=true
                        etConfirmPassword.transformationMethod= PasswordTransformationMethod()
                        etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,
                            drawable.ic_visible_eye,0)
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
                Toast.makeText(this@StudentRegistrationActivity, "Please fill out all mandatary details!", Toast.LENGTH_SHORT).show()
            } else if (!ExtraFunctions.isValidEmailId(email)) {
                Toast.makeText(this@StudentRegistrationActivity, "Please enter valid email id!", Toast.LENGTH_SHORT).show()
            } else if (createPassword.length < 8) {
                Toast.makeText(this@StudentRegistrationActivity, "Password must be at least 8 characters long!", Toast.LENGTH_SHORT).show()
            } else if (!ExtraFunctions.isValidEmailId(email.trim { it <= ' ' })) {
                Toast.makeText(this@StudentRegistrationActivity, "Invalid email address!", Toast.LENGTH_SHORT).show()
            } else if (createPassword != confirmPassword) {
                Toast.makeText(this@StudentRegistrationActivity, "Passwords don't match!", Toast.LENGTH_SHORT).show()
            } else if (!checkBox.isChecked) {
                Toast.makeText(this@StudentRegistrationActivity, "Please check the box!", Toast.LENGTH_SHORT).show()
            } else {
                //                    Toast.makeText(StudentRegistration.this, "All are filled", Toast.LENGTH_SHORT).show();

                val nameValue = name
                val emailValue = email
                val passwordValue = createPassword
                val selectedIdRadio = rgGender.checkedRadioButtonId
                // find the radiobutton by returned id
                val radioButton = findViewById<View>(selectedIdRadio) as RadioButton
                val genderValue = radioButton.text.toString()
                val branchValue = getSmallBranch(spinnerBranch.selectedItem.toString())
                val semesterValue = ExtraFunctions.getSmallSemester(spinnerSemester.selectedItem.toString())
                val collegeValue = spinnerCollege.selectedItem.toString()
                val universityValue = ExtraFunctions.getSmallUniversity(spinnerUniversity.selectedItem.toString())

                //Toast.makeText(StudentRegistration.this, nameValue+" "+emailValue+" "+passwordValue+" "+" "+gender+" "+branchValue+" "+semesterValue+" "+collegeValue+" "+" "+universityValue, Toast.LENGTH_SHORT).show();
                if (ExtraFunctions.isNetworkStatusAvailable(this@StudentRegistrationActivity)) {
                    try {
                        dialog.show()
                        volleytest(nameValue, emailValue, passwordValue, genderValue, branchValue, semesterValue, collegeValue, universityValue)
                    } catch (e: Exception) {
                        dialog.dismiss()
                        Toast.makeText(this@StudentRegistrationActivity, "Error! Please try again later.", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this@StudentRegistrationActivity, "No Internet Connection!", Toast.LENGTH_SHORT).show()
                }

            }
        }


    }

    fun volleytest(nameValue: String, emailValue: String, passwordValue: String, genderValue: String, branchValue: String, semesterValue: String, collegeValue: String, universityValue: String) {
        val url = ExtraFunctions.serverurl + "studentRegistrationData.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response -> jsonParser(response) }, Response.ErrorListener {
            dialog.dismiss()
            Toast.makeText(this@StudentRegistrationActivity, "Error! Please try again later...", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@StudentRegistrationActivity, "Email already registered", Toast.LENGTH_SHORT).show()
            } else if (result == "successful") {
                dialog.dismiss()
                Toast.makeText(this@StudentRegistrationActivity, "Registration successful.", Toast.LENGTH_SHORT).show()
                val spe = sharedPreferences.edit()
                spe.putString("email", etEmail.text.toString())
                spe.putString("passw", etCreatePassword.text.toString())
                spe.putString("login", "true")
                spe.apply()
                Handler().postDelayed({
                    val intentNew = Intent(this@StudentRegistrationActivity, SetUpActivity::class.java)
                    startActivity(intentNew)
                    finish()
                }, 100)
            } else if (result == "error") {
                dialog.dismiss()
                Toast.makeText(this@StudentRegistrationActivity, "An error has occured. Please try again", Toast.LENGTH_SHORT).show()
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
