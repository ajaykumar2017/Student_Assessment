package com.tecent.student_assessment.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import com.tecent.student_assessment.ui.activity.DashBoardMenuWebViewActivity
import com.tecent.student_assessment.R
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.ui.activity.ShowResultFromDashboardActivity

class DashboardFragment : Fragment() {
    lateinit var tableRow: TableRow
    lateinit var cv_prepare: CardView
    lateinit var cv_prep_materials: CardView
    lateinit var cv_results: CardView
    lateinit var cv_quizes: CardView
    lateinit var cv_tutorials: CardView
    lateinit var cv_useful_links: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(
            layout.dashboard_fragment, container, false)
        cv_prepare = view.findViewById(
            R.id.prepare
        )
        cv_prep_materials = view.findViewById(
            R.id.prep_materials
        )
        cv_results = view.findViewById(
            R.id.results
        )
        cv_quizes = view.findViewById(R.id.quizes)
        cv_tutorials = view.findViewById(
            R.id.tutorials
        )
        cv_useful_links = view.findViewById(
            R.id.useful_links
        )
        cv_prepare.setOnClickListener {
            val intDashBoardPrepare = Intent(activity, DashBoardMenuWebViewActivity::class.java)
            intDashBoardPrepare.putExtra("title", "Prepare")
            startActivity(intDashBoardPrepare)
        }
        cv_prep_materials.setOnClickListener {
            val intDashBoardPrepMaterials = Intent(activity, DashBoardMenuWebViewActivity::class.java)
            intDashBoardPrepMaterials.putExtra("title", "Preparation Materials")
            startActivity(intDashBoardPrepMaterials)
        }
        cv_results.setOnClickListener {
            val intSubResult = Intent(activity, ShowResultFromDashboardActivity::class.java)
            startActivity(intSubResult)
        }
        cv_quizes.setOnClickListener {
            val intDashBoardQuizes = Intent(activity, DashBoardMenuWebViewActivity::class.java)
            intDashBoardQuizes.putExtra("title", "Quizes")
            startActivity(intDashBoardQuizes)
        }
        cv_tutorials.setOnClickListener {
            val intDashBoardTut = Intent(activity, DashBoardMenuWebViewActivity::class.java)
            intDashBoardTut.putExtra("title", "Tutorials")
            startActivity(intDashBoardTut)
        }
        cv_useful_links.setOnClickListener {
            val intDashBoardUseFulLinks = Intent(activity, DashBoardMenuWebViewActivity::class.java)
            intDashBoardUseFulLinks.putExtra("title", "Useful Links")
            startActivity(intDashBoardUseFulLinks)
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
