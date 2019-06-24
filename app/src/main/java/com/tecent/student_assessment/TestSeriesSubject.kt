package com.tecent.student_assessment

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.activity_create_post_home.*
import android.content.Intent
import kotlinx.android.synthetic.main.activity_test_series_subject.*
import kotlinx.android.synthetic.main.toolbar_main.*


class TestSeriesSubject : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_series_subject)
        setSupportActionBar(findViewById(R.id.toolbar_main))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_001_back)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        val intent = intent
        val subjectName: String = intent.getStringExtra("testsubject")
        val subjectNameShort: String = intent.getStringExtra("testSubjectShort")
        supportActionBar!!.setTitle(subjectName.trim())
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
        val intentSub = Intent(this, TestSubjectWebView::class.java)
        intentSub.putExtra("testSubject",subjectName)
        intentSub.putExtra("testSubjectShort",subjectNameShort)
        tv_practice_set1.setText(subjectName + " Practice Set 1")
        tv_practice_set2.setText(subjectName + " Practice Set 2")
        tv_practice_set3.setText(subjectName + " Practice Set 3")
        tv_practice_set4.setText(subjectName + " Practice Set 4")
        tv_practice_set5.setText(subjectName + " Practice Set 5")
        tv_practice_set6.setText(subjectName + " Practice Set 6")
        tv_practice_set7.setText(subjectName + " Practice Set 7")
        tv_practice_set8.setText(subjectName + " Practice Set 8")
        tv_practice_set9.setText(subjectName + " Practice Set 9")
        tv_practice_set10.setText(subjectName + " Practice Set 10")
        tv_testSeries1.setText(subjectName + " Test Series 1")
        tv_testSeries2.setText(subjectName + " Test Series 2")
        tv_testSeries3.setText(subjectName + " Test Series 3")
        tv_testSeries4.setText(subjectName + " Test Series 4")
        tv_testSeries5.setText(subjectName + " Test Series 5")
        tv_testSeries6.setText(subjectName + " Test Series 6")
        tv_testSeries7.setText(subjectName + " Test Series 7")
        tv_testSeries8.setText(subjectName + " Test Series 8")
        tv_testSeries9.setText(subjectName + " Test Series 9")
        tv_testSeries10.setText(subjectName + " Test Series 10")
        when (subjectNameShort) {
            "algo" -> {
                iv_practice_set1.setImageResource(R.drawable.ic_sub1)
                iv_practice_set2.setImageResource(R.drawable.ic_sub1)
                iv_practice_set3.setImageResource(R.drawable.ic_sub1)
                iv_practice_set4.setImageResource(R.drawable.ic_sub1)
                iv_practice_set5.setImageResource(R.drawable.ic_sub1)
                iv_practice_set6.setImageResource(R.drawable.ic_sub1)
                iv_practice_set7.setImageResource(R.drawable.ic_sub1)
                iv_practice_set8.setImageResource(R.drawable.ic_sub1)
                iv_practice_set9.setImageResource(R.drawable.ic_sub1)
                iv_practice_set10.setImageResource(R.drawable.ic_sub1)
                iv_testSeries1.setImageResource(R.drawable.ic_sub1)
                iv_testSeries2.setImageResource(R.drawable.ic_sub1)
                iv_testSeries3.setImageResource(R.drawable.ic_sub1)
                iv_testSeries4.setImageResource(R.drawable.ic_sub1)
                iv_testSeries5.setImageResource(R.drawable.ic_sub1)
                iv_testSeries6.setImageResource(R.drawable.ic_sub1)
                iv_testSeries7.setImageResource(R.drawable.ic_sub1)
                iv_testSeries8.setImageResource(R.drawable.ic_sub1)
                iv_testSeries9.setImageResource(R.drawable.ic_sub1)
                iv_testSeries10.setImageResource(R.drawable.ic_sub1)
            }
            "ds" -> {

                iv_practice_set1.setImageResource(R.drawable.ic_sub2)
                iv_practice_set2.setImageResource(R.drawable.ic_sub2)
                iv_practice_set3.setImageResource(R.drawable.ic_sub2)
                iv_practice_set4.setImageResource(R.drawable.ic_sub2)
                iv_practice_set5.setImageResource(R.drawable.ic_sub2)
                iv_practice_set6.setImageResource(R.drawable.ic_sub2)
                iv_practice_set7.setImageResource(R.drawable.ic_sub2)
                iv_practice_set8.setImageResource(R.drawable.ic_sub2)
                iv_practice_set9.setImageResource(R.drawable.ic_sub2)
                iv_practice_set10.setImageResource(R.drawable.ic_sub2)
                iv_testSeries1.setImageResource(R.drawable.ic_sub2)
                iv_testSeries2.setImageResource(R.drawable.ic_sub2)
                iv_testSeries3.setImageResource(R.drawable.ic_sub2)
                iv_testSeries4.setImageResource(R.drawable.ic_sub2)
                iv_testSeries5.setImageResource(R.drawable.ic_sub2)
                iv_testSeries6.setImageResource(R.drawable.ic_sub2)
                iv_testSeries7.setImageResource(R.drawable.ic_sub2)
                iv_testSeries8.setImageResource(R.drawable.ic_sub2)
                iv_testSeries9.setImageResource(R.drawable.ic_sub2)
                iv_testSeries10.setImageResource(R.drawable.ic_sub2)
            }
            "cd" -> {

                iv_practice_set1.setImageResource(R.drawable.ic_sub7)
                iv_practice_set2.setImageResource(R.drawable.ic_sub7)
                iv_practice_set3.setImageResource(R.drawable.ic_sub7)
                iv_practice_set4.setImageResource(R.drawable.ic_sub7)
                iv_practice_set5.setImageResource(R.drawable.ic_sub7)
                iv_practice_set6.setImageResource(R.drawable.ic_sub7)
                iv_practice_set7.setImageResource(R.drawable.ic_sub7)
                iv_practice_set8.setImageResource(R.drawable.ic_sub7)
                iv_practice_set9.setImageResource(R.drawable.ic_sub7)
                iv_practice_set10.setImageResource(R.drawable.ic_sub7)
                iv_testSeries1.setImageResource(R.drawable.ic_sub7)
                iv_testSeries2.setImageResource(R.drawable.ic_sub7)
                iv_testSeries3.setImageResource(R.drawable.ic_sub7)
                iv_testSeries4.setImageResource(R.drawable.ic_sub7)
                iv_testSeries5.setImageResource(R.drawable.ic_sub7)
                iv_testSeries6.setImageResource(R.drawable.ic_sub7)
                iv_testSeries7.setImageResource(R.drawable.ic_sub7)
                iv_testSeries8.setImageResource(R.drawable.ic_sub7)
                iv_testSeries9.setImageResource(R.drawable.ic_sub7)
                iv_testSeries10.setImageResource(R.drawable.ic_sub7)
            }
            "toc" -> {

                iv_practice_set1.setImageResource(R.drawable.ic_sub4)
                iv_practice_set2.setImageResource(R.drawable.ic_sub4)
                iv_practice_set3.setImageResource(R.drawable.ic_sub4)
                iv_practice_set4.setImageResource(R.drawable.ic_sub4)
                iv_practice_set5.setImageResource(R.drawable.ic_sub4)
                iv_practice_set6.setImageResource(R.drawable.ic_sub4)
                iv_practice_set7.setImageResource(R.drawable.ic_sub4)
                iv_practice_set8.setImageResource(R.drawable.ic_sub4)
                iv_practice_set9.setImageResource(R.drawable.ic_sub4)
                iv_practice_set10.setImageResource(R.drawable.ic_sub4)
                iv_testSeries1.setImageResource(R.drawable.ic_sub4)
                iv_testSeries2.setImageResource(R.drawable.ic_sub4)
                iv_testSeries3.setImageResource(R.drawable.ic_sub4)
                iv_testSeries4.setImageResource(R.drawable.ic_sub4)
                iv_testSeries5.setImageResource(R.drawable.ic_sub4)
                iv_testSeries6.setImageResource(R.drawable.ic_sub4)
                iv_testSeries7.setImageResource(R.drawable.ic_sub4)
                iv_testSeries8.setImageResource(R.drawable.ic_sub4)
                iv_testSeries9.setImageResource(R.drawable.ic_sub4)
                iv_testSeries10.setImageResource(R.drawable.ic_sub4)
            }
            "db" -> {

                iv_practice_set1.setImageResource(R.drawable.ic_sub12)
                iv_practice_set2.setImageResource(R.drawable.ic_sub12)
                iv_practice_set3.setImageResource(R.drawable.ic_sub12)
                iv_practice_set4.setImageResource(R.drawable.ic_sub12)
                iv_practice_set5.setImageResource(R.drawable.ic_sub12)
                iv_practice_set6.setImageResource(R.drawable.ic_sub12)
                iv_practice_set7.setImageResource(R.drawable.ic_sub12)
                iv_practice_set8.setImageResource(R.drawable.ic_sub12)
                iv_practice_set9.setImageResource(R.drawable.ic_sub12)
                iv_practice_set10.setImageResource(R.drawable.ic_sub12)
                iv_testSeries1.setImageResource(R.drawable.ic_sub12)
                iv_testSeries2.setImageResource(R.drawable.ic_sub12)
                iv_testSeries3.setImageResource(R.drawable.ic_sub12)
                iv_testSeries4.setImageResource(R.drawable.ic_sub12)
                iv_testSeries5.setImageResource(R.drawable.ic_sub12)
                iv_testSeries6.setImageResource(R.drawable.ic_sub12)
                iv_testSeries7.setImageResource(R.drawable.ic_sub12)
                iv_testSeries8.setImageResource(R.drawable.ic_sub12)
                iv_testSeries9.setImageResource(R.drawable.ic_sub12)
                iv_testSeries10.setImageResource(R.drawable.ic_sub12)
            }
            "os" -> {

                iv_practice_set1.setImageResource(R.drawable.ic_sub13)
                iv_practice_set2.setImageResource(R.drawable.ic_sub13)
                iv_practice_set3.setImageResource(R.drawable.ic_sub13)
                iv_practice_set4.setImageResource(R.drawable.ic_sub13)
                iv_practice_set5.setImageResource(R.drawable.ic_sub13)
                iv_practice_set6.setImageResource(R.drawable.ic_sub13)
                iv_practice_set7.setImageResource(R.drawable.ic_sub13)
                iv_practice_set8.setImageResource(R.drawable.ic_sub13)
                iv_practice_set9.setImageResource(R.drawable.ic_sub13)
                iv_practice_set10.setImageResource(R.drawable.ic_sub13)
                iv_testSeries1.setImageResource(R.drawable.ic_sub13)
                iv_testSeries2.setImageResource(R.drawable.ic_sub13)
                iv_testSeries3.setImageResource(R.drawable.ic_sub13)
                iv_testSeries4.setImageResource(R.drawable.ic_sub13)
                iv_testSeries5.setImageResource(R.drawable.ic_sub13)
                iv_testSeries6.setImageResource(R.drawable.ic_sub13)
                iv_testSeries7.setImageResource(R.drawable.ic_sub13)
                iv_testSeries8.setImageResource(R.drawable.ic_sub13)
                iv_testSeries9.setImageResource(R.drawable.ic_sub13)
                iv_testSeries10.setImageResource(R.drawable.ic_sub13)
            }
            "dl" -> {
                iv_practice_set1.setImageResource(R.drawable.ic_sub3)
                iv_practice_set2.setImageResource(R.drawable.ic_sub3)
                iv_practice_set3.setImageResource(R.drawable.ic_sub3)
                iv_practice_set4.setImageResource(R.drawable.ic_sub3)
                iv_practice_set5.setImageResource(R.drawable.ic_sub3)
                iv_practice_set6.setImageResource(R.drawable.ic_sub3)
                iv_practice_set7.setImageResource(R.drawable.ic_sub3)
                iv_practice_set8.setImageResource(R.drawable.ic_sub3)
                iv_practice_set9.setImageResource(R.drawable.ic_sub3)
                iv_practice_set10.setImageResource(R.drawable.ic_sub3)
                iv_testSeries1.setImageResource(R.drawable.ic_sub3)
                iv_testSeries2.setImageResource(R.drawable.ic_sub3)
                iv_testSeries3.setImageResource(R.drawable.ic_sub3)
                iv_testSeries4.setImageResource(R.drawable.ic_sub3)
                iv_testSeries5.setImageResource(R.drawable.ic_sub3)
                iv_testSeries6.setImageResource(R.drawable.ic_sub3)
                iv_testSeries7.setImageResource(R.drawable.ic_sub3)
                iv_testSeries8.setImageResource(R.drawable.ic_sub3)
                iv_testSeries9.setImageResource(R.drawable.ic_sub3)
                iv_testSeries10.setImageResource(R.drawable.ic_sub3)
            }
            "co" -> {
                iv_practice_set1.setImageResource(R.drawable.ic_sub5)
                iv_practice_set2.setImageResource(R.drawable.ic_sub5)
                iv_practice_set3.setImageResource(R.drawable.ic_sub5)
                iv_practice_set4.setImageResource(R.drawable.ic_sub5)
                iv_practice_set5.setImageResource(R.drawable.ic_sub5)
                iv_practice_set6.setImageResource(R.drawable.ic_sub5)
                iv_practice_set7.setImageResource(R.drawable.ic_sub5)
                iv_practice_set8.setImageResource(R.drawable.ic_sub5)
                iv_practice_set9.setImageResource(R.drawable.ic_sub5)
                iv_practice_set10.setImageResource(R.drawable.ic_sub5)
                iv_testSeries1.setImageResource(R.drawable.ic_sub5)
                iv_testSeries2.setImageResource(R.drawable.ic_sub5)
                iv_testSeries3.setImageResource(R.drawable.ic_sub5)
                iv_testSeries4.setImageResource(R.drawable.ic_sub5)
                iv_testSeries5.setImageResource(R.drawable.ic_sub5)
                iv_testSeries6.setImageResource(R.drawable.ic_sub5)
                iv_testSeries7.setImageResource(R.drawable.ic_sub5)
                iv_testSeries8.setImageResource(R.drawable.ic_sub5)
                iv_testSeries9.setImageResource(R.drawable.ic_sub5)
                iv_testSeries10.setImageResource(R.drawable.ic_sub5)
            }
            "cn" -> {
                iv_practice_set1.setImageResource(R.drawable.ic_sub14)
                iv_practice_set2.setImageResource(R.drawable.ic_sub14)
                iv_practice_set3.setImageResource(R.drawable.ic_sub14)
                iv_practice_set4.setImageResource(R.drawable.ic_sub14)
                iv_practice_set5.setImageResource(R.drawable.ic_sub14)
                iv_practice_set6.setImageResource(R.drawable.ic_sub14)
                iv_practice_set7.setImageResource(R.drawable.ic_sub14)
                iv_practice_set8.setImageResource(R.drawable.ic_sub14)
                iv_practice_set9.setImageResource(R.drawable.ic_sub14)
                iv_practice_set10.setImageResource(R.drawable.ic_sub14)
                iv_testSeries1.setImageResource(R.drawable.ic_sub14)
                iv_testSeries2.setImageResource(R.drawable.ic_sub14)
                iv_testSeries3.setImageResource(R.drawable.ic_sub14)
                iv_testSeries4.setImageResource(R.drawable.ic_sub14)
                iv_testSeries5.setImageResource(R.drawable.ic_sub14)
                iv_testSeries6.setImageResource(R.drawable.ic_sub14)
                iv_testSeries7.setImageResource(R.drawable.ic_sub14)
                iv_testSeries8.setImageResource(R.drawable.ic_sub14)
                iv_testSeries9.setImageResource(R.drawable.ic_sub14)
                iv_testSeries10.setImageResource(R.drawable.ic_sub14)
            }
            "dm" -> {
                iv_practice_set1.setImageResource(R.drawable.ic_sub10)
                iv_practice_set2.setImageResource(R.drawable.ic_sub10)
                iv_practice_set3.setImageResource(R.drawable.ic_sub10)
                iv_practice_set4.setImageResource(R.drawable.ic_sub10)
                iv_practice_set5.setImageResource(R.drawable.ic_sub10)
                iv_practice_set6.setImageResource(R.drawable.ic_sub10)
                iv_practice_set7.setImageResource(R.drawable.ic_sub10)
                iv_practice_set8.setImageResource(R.drawable.ic_sub10)
                iv_practice_set9.setImageResource(R.drawable.ic_sub10)
                iv_practice_set10.setImageResource(R.drawable.ic_sub10)
                iv_testSeries1.setImageResource(R.drawable.ic_sub10)
                iv_testSeries2.setImageResource(R.drawable.ic_sub10)
                iv_testSeries3.setImageResource(R.drawable.ic_sub10)
                iv_testSeries4.setImageResource(R.drawable.ic_sub10)
                iv_testSeries5.setImageResource(R.drawable.ic_sub10)
                iv_testSeries6.setImageResource(R.drawable.ic_sub10)
                iv_testSeries7.setImageResource(R.drawable.ic_sub10)
                iv_testSeries8.setImageResource(R.drawable.ic_sub10)
                iv_testSeries9.setImageResource(R.drawable.ic_sub10)
                iv_testSeries10.setImageResource(R.drawable.ic_sub10)
            }
            "c" -> {
                iv_practice_set1.setImageResource(R.drawable.ic_sub11)
                iv_practice_set2.setImageResource(R.drawable.ic_sub11)
                iv_practice_set3.setImageResource(R.drawable.ic_sub11)
                iv_practice_set4.setImageResource(R.drawable.ic_sub11)
                iv_practice_set5.setImageResource(R.drawable.ic_sub11)
                iv_practice_set6.setImageResource(R.drawable.ic_sub11)
                iv_practice_set7.setImageResource(R.drawable.ic_sub11)
                iv_practice_set8.setImageResource(R.drawable.ic_sub11)
                iv_practice_set9.setImageResource(R.drawable.ic_sub11)
                iv_practice_set10.setImageResource(R.drawable.ic_sub11)
                iv_testSeries1.setImageResource(R.drawable.ic_sub11)
                iv_testSeries2.setImageResource(R.drawable.ic_sub11)
                iv_testSeries3.setImageResource(R.drawable.ic_sub11)
                iv_testSeries4.setImageResource(R.drawable.ic_sub11)
                iv_testSeries5.setImageResource(R.drawable.ic_sub11)
                iv_testSeries6.setImageResource(R.drawable.ic_sub11)
                iv_testSeries7.setImageResource(R.drawable.ic_sub11)
                iv_testSeries8.setImageResource(R.drawable.ic_sub11)
                iv_testSeries9.setImageResource(R.drawable.ic_sub11)
                iv_testSeries10.setImageResource(R.drawable.ic_sub11)
            }
            "cpp" -> {
                iv_practice_set1.setImageResource(R.drawable.ic_sub6)
                iv_practice_set2.setImageResource(R.drawable.ic_sub6)
                iv_practice_set3.setImageResource(R.drawable.ic_sub6)
                iv_practice_set4.setImageResource(R.drawable.ic_sub6)
                iv_practice_set5.setImageResource(R.drawable.ic_sub6)
                iv_practice_set6.setImageResource(R.drawable.ic_sub6)
                iv_practice_set7.setImageResource(R.drawable.ic_sub6)
                iv_practice_set8.setImageResource(R.drawable.ic_sub6)
                iv_practice_set9.setImageResource(R.drawable.ic_sub6)
                iv_practice_set10.setImageResource(R.drawable.ic_sub6)
                iv_testSeries1.setImageResource(R.drawable.ic_sub6)
                iv_testSeries2.setImageResource(R.drawable.ic_sub6)
                iv_testSeries3.setImageResource(R.drawable.ic_sub6)
                iv_testSeries4.setImageResource(R.drawable.ic_sub6)
                iv_testSeries5.setImageResource(R.drawable.ic_sub6)
                iv_testSeries6.setImageResource(R.drawable.ic_sub6)
                iv_testSeries7.setImageResource(R.drawable.ic_sub6)
                iv_testSeries8.setImageResource(R.drawable.ic_sub6)
                iv_testSeries9.setImageResource(R.drawable.ic_sub6)
                iv_testSeries10.setImageResource(R.drawable.ic_sub6)
            }
            "java" -> {
                iv_practice_set1.setImageResource(R.drawable.ic_sub9)
                iv_practice_set2.setImageResource(R.drawable.ic_sub9)
                iv_practice_set3.setImageResource(R.drawable.ic_sub9)
                iv_practice_set4.setImageResource(R.drawable.ic_sub9)
                iv_practice_set5.setImageResource(R.drawable.ic_sub9)
                iv_practice_set6.setImageResource(R.drawable.ic_sub9)
                iv_practice_set7.setImageResource(R.drawable.ic_sub9)
                iv_practice_set8.setImageResource(R.drawable.ic_sub9)
                iv_practice_set9.setImageResource(R.drawable.ic_sub9)
                iv_practice_set10.setImageResource(R.drawable.ic_sub9)
                iv_testSeries1.setImageResource(R.drawable.ic_sub9)
                iv_testSeries2.setImageResource(R.drawable.ic_sub9)
                iv_testSeries3.setImageResource(R.drawable.ic_sub9)
                iv_testSeries4.setImageResource(R.drawable.ic_sub9)
                iv_testSeries5.setImageResource(R.drawable.ic_sub9)
                iv_testSeries6.setImageResource(R.drawable.ic_sub9)
                iv_testSeries7.setImageResource(R.drawable.ic_sub9)
                iv_testSeries8.setImageResource(R.drawable.ic_sub9)
                iv_testSeries9.setImageResource(R.drawable.ic_sub9)
                iv_testSeries10.setImageResource(R.drawable.ic_sub9)
            }
            "python" -> {
                iv_practice_set1.setImageResource(R.drawable.ic_sub16)
                iv_practice_set2.setImageResource(R.drawable.ic_sub16)
                iv_practice_set3.setImageResource(R.drawable.ic_sub16)
                iv_practice_set4.setImageResource(R.drawable.ic_sub16)
                iv_practice_set5.setImageResource(R.drawable.ic_sub16)
                iv_practice_set6.setImageResource(R.drawable.ic_sub16)
                iv_practice_set7.setImageResource(R.drawable.ic_sub16)
                iv_practice_set8.setImageResource(R.drawable.ic_sub16)
                iv_practice_set9.setImageResource(R.drawable.ic_sub16)
                iv_practice_set10.setImageResource(R.drawable.ic_sub16)
                iv_testSeries1.setImageResource(R.drawable.ic_sub16)
                iv_testSeries2.setImageResource(R.drawable.ic_sub16)
                iv_testSeries3.setImageResource(R.drawable.ic_sub16)
                iv_testSeries4.setImageResource(R.drawable.ic_sub16)
                iv_testSeries5.setImageResource(R.drawable.ic_sub16)
                iv_testSeries6.setImageResource(R.drawable.ic_sub16)
                iv_testSeries7.setImageResource(R.drawable.ic_sub16)
                iv_testSeries8.setImageResource(R.drawable.ic_sub16)
                iv_testSeries9.setImageResource(R.drawable.ic_sub16)
                iv_testSeries10.setImageResource(R.drawable.ic_sub16)
            }
            "android" -> {
                iv_practice_set1.setImageResource(R.drawable.ic_sub8)
                iv_practice_set2.setImageResource(R.drawable.ic_sub8)
                iv_practice_set3.setImageResource(R.drawable.ic_sub8)
                iv_practice_set4.setImageResource(R.drawable.ic_sub8)
                iv_practice_set5.setImageResource(R.drawable.ic_sub8)
                iv_practice_set6.setImageResource(R.drawable.ic_sub8)
                iv_practice_set7.setImageResource(R.drawable.ic_sub8)
                iv_practice_set8.setImageResource(R.drawable.ic_sub8)
                iv_practice_set9.setImageResource(R.drawable.ic_sub8)
                iv_practice_set10.setImageResource(R.drawable.ic_sub8)
                iv_testSeries1.setImageResource(R.drawable.ic_sub8)
                iv_testSeries2.setImageResource(R.drawable.ic_sub8)
                iv_testSeries3.setImageResource(R.drawable.ic_sub8)
                iv_testSeries4.setImageResource(R.drawable.ic_sub8)
                iv_testSeries5.setImageResource(R.drawable.ic_sub8)
                iv_testSeries6.setImageResource(R.drawable.ic_sub8)
                iv_testSeries7.setImageResource(R.drawable.ic_sub8)
                iv_testSeries8.setImageResource(R.drawable.ic_sub8)
                iv_testSeries9.setImageResource(R.drawable.ic_sub8)
                iv_testSeries10.setImageResource(R.drawable.ic_sub8)
            }
            else -> {
                iv_practice_set1.setImageResource(R.drawable.ic_sub15)
                iv_practice_set2.setImageResource(R.drawable.ic_sub15)
                iv_practice_set3.setImageResource(R.drawable.ic_sub15)
                iv_practice_set4.setImageResource(R.drawable.ic_sub15)
                iv_practice_set5.setImageResource(R.drawable.ic_sub15)
                iv_practice_set6.setImageResource(R.drawable.ic_sub15)
                iv_practice_set7.setImageResource(R.drawable.ic_sub15)
                iv_practice_set8.setImageResource(R.drawable.ic_sub15)
                iv_practice_set9.setImageResource(R.drawable.ic_sub15)
                iv_practice_set10.setImageResource(R.drawable.ic_sub15)
                iv_testSeries1.setImageResource(R.drawable.ic_sub15)
                iv_testSeries2.setImageResource(R.drawable.ic_sub15)
                iv_testSeries3.setImageResource(R.drawable.ic_sub15)
                iv_testSeries4.setImageResource(R.drawable.ic_sub15)
                iv_testSeries5.setImageResource(R.drawable.ic_sub15)
                iv_testSeries6.setImageResource(R.drawable.ic_sub15)
                iv_testSeries7.setImageResource(R.drawable.ic_sub15)
                iv_testSeries8.setImageResource(R.drawable.ic_sub15)
                iv_testSeries9.setImageResource(R.drawable.ic_sub15)
                iv_testSeries10.setImageResource(R.drawable.ic_sub15)
            }
        }

        practice_set1.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Practice Set 1")
            startActivity(intentSub)
        }
        practice_set2.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Practice Set 2")
            startActivity(intentSub)
        }
        practice_set3.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Practice Set 3")
            startActivity(intentSub)
        }
        practice_set4.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Practice Set 4")
            startActivity(intentSub)
        }
        practice_set5.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Practice Set 5")
            startActivity(intentSub)
        }
        practice_set6.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Practice Set 6")
            startActivity(intentSub)
        }
        practice_set7.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Practice Set 7")
            startActivity(intentSub)
        }
        practice_set8.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Practice Set 8")
            startActivity(intentSub)
        }
        practice_set9.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Practice Set 9")
            startActivity(intentSub)
        }
        practice_set10.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Practice Set 10")
            startActivity(intentSub)
        }


        testSeries1.setOnClickListener {
            intentSub.putExtra("test","TestSeries")
            intentSub.putExtra("testType", "Test Series 1")
            startActivity(intentSub)
        }
        testSeries2.setOnClickListener {
            intentSub.putExtra("test","TestSeries")
            intentSub.putExtra("testType", "Test Series 2")
            startActivity(intentSub)
        }
        testSeries3.setOnClickListener {
            intentSub.putExtra("test","TestSeries")
            intentSub.putExtra("testType", "Test Series 3")
            startActivity(intentSub)
        }
        testSeries4.setOnClickListener {
            intentSub.putExtra("test","TestSeries")
            intentSub.putExtra("testType", "Test Series 4")
            startActivity(intentSub)
        }
        testSeries5.setOnClickListener {
            intentSub.putExtra("test","TestSeries")
            intentSub.putExtra("testType", "Test Series 5")
            startActivity(intentSub)
        }
        testSeries6.setOnClickListener {
            intentSub.putExtra("test","TestSeries")
            intentSub.putExtra("testType", "Test Series 6")
            startActivity(intentSub)
        }
        testSeries7.setOnClickListener {
            intentSub.putExtra("test","TestSeries")
            intentSub.putExtra("testType", "Test Series 7")
            startActivity(intentSub)
        }
        testSeries8.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Test Series 8")
            startActivity(intentSub)
        }
        testSeries9.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Test Series 9")
            startActivity(intentSub)
        }
        testSeries10.setOnClickListener {
            intentSub.putExtra("test","Practice")
            intentSub.putExtra("testType", "Test Series 10")
            startActivity(intentSub)
        }


    }
}
