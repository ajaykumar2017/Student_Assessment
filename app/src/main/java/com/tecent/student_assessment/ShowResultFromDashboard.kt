package com.tecent.student_assessment

import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import kotlinx.android.synthetic.main.toolbar_main.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*
import kotlin.collections.ArrayList
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.data.PieData





class ShowResultFromDashboard : AppCompatActivity() {
    lateinit var barChart:BarChart
    lateinit var dates:ArrayList<String>
    lateinit var random:Random
    lateinit var barEntries:ArrayList<BarEntry>
    lateinit var floatArray: FloatArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_result_from_dashboard)
        setSupportActionBar(findViewById(R.id.toolbar_main))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_001_back)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        supportActionBar!!.setTitle("Results")
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
        var pieChart=findViewById<PieChart>(R.id.pie_chart)
        var barChart = findViewById<BarChart>(R.id.barchart)
//        createRandomBarGraph("2016/05/05", "2016/06/01")

        val NoOfEmp = ArrayList<Entry>()

        NoOfEmp.add(Entry(94f, 0))
        NoOfEmp.add(Entry(10f, 1))
        NoOfEmp.add(Entry(84f, 2))
        NoOfEmp.add(Entry(72f, 3))
        NoOfEmp.add(Entry(63f, 4))
        NoOfEmp.add(Entry(89f, 5))
        NoOfEmp.add(Entry(54f, 6))
        NoOfEmp.add(Entry(39f, 7))
        NoOfEmp.add(Entry(46f, 8))
        NoOfEmp.add(Entry(77f, 9))
        NoOfEmp.add(Entry(29f, 10))
        NoOfEmp.add(Entry(62f, 11))
        NoOfEmp.add(Entry(49f, 12))
        NoOfEmp.add(Entry(38f, 13))
        NoOfEmp.add(Entry(92f, 14))

        val NoOfEmp1 = ArrayList<BarEntry>()

        NoOfEmp1.add(BarEntry(94f, 0))
        NoOfEmp1.add(BarEntry(10f, 1))
        NoOfEmp1.add(BarEntry(84f, 2))
        NoOfEmp1.add(BarEntry(72f, 3))
        NoOfEmp1.add(BarEntry(63f, 4))
        NoOfEmp1.add(BarEntry(89f, 5))
        NoOfEmp1.add(BarEntry(54f, 6))
        NoOfEmp1.add(BarEntry(39f, 7))
        NoOfEmp1.add(BarEntry(46f, 8))
        NoOfEmp1.add(BarEntry(77f, 9))
        NoOfEmp1.add(BarEntry(29f, 10))
        NoOfEmp1.add(BarEntry(62f, 11))
        NoOfEmp1.add(BarEntry(49f, 12))
        NoOfEmp1.add(BarEntry(38f, 13))
        NoOfEmp1.add(BarEntry(92f, 14))

        val dataSet = PieDataSet(NoOfEmp, "Marks Obtained by you in each Subject")

        val subject = ArrayList<String>()
        subject.add("Algo")
        subject.add("DS")
        subject.add("Compiler")
        subject.add("TOC")
        subject.add("DBMS")
        subject.add("OS")
        subject.add("Digital")
        subject.add("COA")
        subject.add("Network")
        subject.add("Discrete")
        subject.add("C")
        subject.add("C++")
        subject.add("Java")
        subject.add("Python")
        subject.add("Android")

        val data1 = PieData(subject, dataSet)
        pieChart.setData(data1)
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS)
        pieChart.animateXY(5000, 5000)

//        val bardataset = BarDataSet(NoOfEmp, "No Of Employee")
        val bardataset=BarDataSet(NoOfEmp1,"Marks Obtained by you")
        barChart.animateY(3000)
        val data2 = BarData(subject, bardataset)
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS)
        barChart.setData(data2)

    }

    fun createRandomBarGraph(Date1: String, Date2: String) {

        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")

        try {
            val date1 = simpleDateFormat.parse(Date1)
            val date2 = simpleDateFormat.parse(Date2)

            val mDate1 = Calendar.getInstance()
            val mDate2 = Calendar.getInstance()
            mDate1.clear()
            mDate2.clear()

            mDate1.time = date1
            mDate2.time = date2

            dates = ArrayList()
            dates = getList(mDate1, mDate2)

            barEntries = ArrayList()
            var max = 0f
            var value = 0f
            random = Random()
            for (j in dates.indices) {
                max = 100f
                value = random.nextFloat() * max
                barEntries.add(BarEntry(value, j))
            }

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val barDataSet = BarDataSet(barEntries, "Tests")
        val barData = BarData(dates, barDataSet)
        barChart.data = barData
        barChart.setDescription("Test result Bar Graph!")

    }

    fun getList(startDate: Calendar, endDate: Calendar): ArrayList<String> {
        val list = ArrayList<String>()
        while (startDate.compareTo(endDate) <= 0) {
            list.add(getDate(startDate))
            startDate.add(Calendar.DAY_OF_MONTH, 1)
        }
        return list
    }

    fun getDate(cld: Calendar): String {
        var curDate = (cld.get(Calendar.YEAR).toString() + "/" + (cld.get(Calendar.MONTH) + 1) + "/"
                + cld.get(Calendar.DAY_OF_MONTH))
        try {
            val date = SimpleDateFormat("yyyy/MM/dd").parse(curDate)
            curDate = SimpleDateFormat("yyy/MM/dd").format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return curDate
    }

}
