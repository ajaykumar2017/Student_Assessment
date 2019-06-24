package com.tecent.student_assessment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast

class Test_Series_Fragment : Fragment() {
    lateinit var tableRow: TableRow
    lateinit var algorithm: TextView
    lateinit var data_structure: TextView
    lateinit var compiler_design: TextView
    lateinit var theory_of_computation: TextView
    lateinit var database: TextView
    lateinit var operating_system: TextView
    lateinit var digital_logic: TextView
    lateinit var computer_organization: TextView
    lateinit var computer_network: TextView
    lateinit var discrete_math: TextView
    lateinit var c_Lang: TextView
    lateinit var cpp_Lang: TextView
    lateinit var java_lang: TextView
    lateinit var python: TextView
    lateinit var androids: TextView
    lateinit var others: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.test_series_fragment, container, false)
        algorithm = view.findViewById(R.id.algo)
        data_structure = view.findViewById(R.id.datastr)
        data_structure = view.findViewById(R.id.datastr)
        compiler_design = view.findViewById(R.id.compiler)
        theory_of_computation = view.findViewById(R.id.toc)
        database = view.findViewById(R.id.dbms)
        operating_system = view.findViewById(R.id.operating)
        digital_logic = view.findViewById(R.id.digital)
        computer_organization = view.findViewById(R.id.coa)
        computer_network = view.findViewById(R.id.cn)
        discrete_math = view.findViewById(R.id.dm)
        c_Lang = view.findViewById(R.id.clang)
        cpp_Lang = view.findViewById(R.id.cpplang)
        java_lang = view.findViewById(R.id.javalang)
        python = view.findViewById(R.id.python)
        androids = view.findViewById(R.id.androids)
        others = view.findViewById(R.id.others)

        val intent = Intent(activity, TestSeriesSubject::class.java)
        algorithm.setOnClickListener {
            intent.putExtra("testSubjectShort", "algo")
            intent.putExtra("testsubject", algorithm.text.toString())
            startActivity(intent)
        }
        data_structure.setOnClickListener {
            intent.putExtra("testSubjectShort", "ds")
            intent.putExtra("testsubject", data_structure.text.toString())
            startActivity(intent)
        }
        compiler_design.setOnClickListener {
            intent.putExtra("testSubjectShort", "cd")
            intent.putExtra("testsubject", compiler_design.text.toString())
            startActivity(intent)
        }
        theory_of_computation.setOnClickListener {
            intent.putExtra("testSubjectShort", "toc")
            intent.putExtra("testsubject", theory_of_computation.text.toString())
            startActivity(intent)
        }
        database.setOnClickListener {
            intent.putExtra("testSubjectShort", "dbms")
            intent.putExtra("testsubject", database.text.toString())
            startActivity(intent)
        }
        operating_system.setOnClickListener {
            intent.putExtra("testSubjectShort", "os")
            intent.putExtra("testsubject", operating_system.text.toString())
            startActivity(intent)
        }
        digital_logic.setOnClickListener {
            intent.putExtra("testSubjectShort", "dl")
            intent.putExtra("testsubject", digital_logic.text.toString())
            startActivity(intent)
        }
        computer_organization.setOnClickListener {
            intent.putExtra("testSubjectShort", "co")
            intent.putExtra("testsubject", computer_organization.text.toString())
            startActivity(intent)
        }
        computer_network.setOnClickListener {
            intent.putExtra("testSubjectShort", "cn")
            intent.putExtra("testsubject", computer_network.text.toString())
            startActivity(intent)
        }
        discrete_math.setOnClickListener {
            intent.putExtra("testSubjectShort", "dm")
            intent.putExtra("testsubject", discrete_math.text.toString())
            startActivity(intent)
        }
        c_Lang.setOnClickListener {
            intent.putExtra("testSubjectShort", "c")
            intent.putExtra("testsubject", c_Lang.text.toString())
            startActivity(intent)
        }
        cpp_Lang.setOnClickListener {
            intent.putExtra("testSubjectShort", "cpp")
            intent.putExtra("testsubject", cpp_Lang.text.toString())
            startActivity(intent)
        }
        java_lang.setOnClickListener {
            intent.putExtra("testSubjectShort", "java")
            intent.putExtra("testsubject", java_lang.text.toString())
            startActivity(intent)
        }
        python.setOnClickListener {
            intent.putExtra("testSubjectShort", "python")
            intent.putExtra("testsubject", python.text.toString())
            startActivity(intent)
        }
        androids.setOnClickListener {
            intent.putExtra("testSubjectShort", "android")
            intent.putExtra("testsubject", androids.text.toString())
            startActivity(intent)
        }
        others.setOnClickListener {
            intent.putExtra("testSubjectShort", "other")
            intent.putExtra("testsubject", others.text.toString())
            startActivity(intent)
        }

        if (isAdded) {
            tableRow = TableRow(context)
            tableRow.layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, 1.0f)
        }

        return view
    }
}
