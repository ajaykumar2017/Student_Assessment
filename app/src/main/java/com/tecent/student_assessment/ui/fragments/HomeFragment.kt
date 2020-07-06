package com.tecent.student_assessment.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.tecent.student_assessment.R
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.ui.activity.HomeActivity
import com.tecent.student_assessment.ui.adapters.MyRecyclerHomePostsAdapter
import com.tecent.student_assessment.utils.DataUtils
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class HomeFragment : Fragment() {
    lateinit var homeActivity: HomeActivity
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesLike: SharedPreferences
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var progressBar: ProgressBar
    lateinit var mRecyclerViewPostHome: RecyclerView
    lateinit var dialog: ACProgressFlower
    lateinit var tableRow: TableRow

    lateinit var useridlist: ArrayList<String>
    lateinit var userdplist: ArrayList<String>
    lateinit var usernamelist: ArrayList<String>
    lateinit var userbranchlist: ArrayList<String>
    lateinit var posttimelist: ArrayList<String>
    lateinit var postidlist: ArrayList<String>
    lateinit var posttextlist: ArrayList<String>
    lateinit var postfilelist: ArrayList<String>
    lateinit var subjectlist: ArrayList<String>
    lateinit var likeslist: ArrayList<String>
    lateinit var commentslist: ArrayList<String>
    var userid: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(
            layout.homefragment, container, false)
        homeActivity=activity as HomeActivity
        useridlist = ArrayList()
        userdplist = ArrayList()
        usernamelist = ArrayList()
        posttimelist = ArrayList()
        postidlist = ArrayList()
        userbranchlist = ArrayList()
        posttextlist = ArrayList()
        postfilelist = ArrayList()
        subjectlist = ArrayList()
        likeslist = ArrayList()
        commentslist = ArrayList()

        sharedPreferences = homeActivity.getSharedPreferences(
            DataUtils.sharedPreferencesId, Context.MODE_PRIVATE
        )
        sharedPreferencesLike = homeActivity.getSharedPreferences(
            DataUtils.sharedPreferencesLikeId, Context.MODE_PRIVATE
        )
        userid = sharedPreferences.getString("userid", "")
        swipeRefreshLayout = view.findViewById<View>(
            R.id.swipeRefreshLayout
        ) as SwipeRefreshLayout
        progressBar = view.findViewById(
            R.id.progress_bar
        )
        dialog = ACProgressFlower.Builder(homeActivity)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build()
        dialog.setCancelable(false)
        mRecyclerViewPostHome = view.findViewById(
            R.id.recycler_view_post_home
        )
        mRecyclerViewPostHome.setHasFixedSize(false)
        //        mRecyclerViewPostHome.setLayoutManager(new LinearLayoutManager(getActivity()));
        val layoutManager = object : LinearLayoutManager(homeActivity) {
            override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
                val smoothScroller = object : LinearSmoothScroller(homeActivity) {
                    private val SPEED = 300f

                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                        return SPEED / displayMetrics.densityDpi
                    }
                }
                smoothScroller.targetPosition = position
                startSmoothScroll(smoothScroller)
            }
        }
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerViewPostHome.layoutManager = layoutManager

        loadPostsFromSpf()
        try {
          if (DataUtils.isNetworkStatusAvailable(homeActivity)) {
            volleyPostDataRequest()
          } else {
            Toast.makeText(homeActivity, "No Internet Connection!", Toast.LENGTH_SHORT)
                .show()
          }
            swipeRefreshLayout.setOnRefreshListener {
              if (DataUtils.isNetworkStatusAvailable(homeActivity))
                volleyPostDataRequest()
              else {
                swipeRefreshLayout.isRefreshing = false
                progressBar.visibility = View.GONE
                Toast.makeText(homeActivity, "No Internet Connection!", Toast.LENGTH_SHORT)
                    .show()
              }
            }
        } catch (e: Exception) {
            Toast.makeText(homeActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }

        if (isAdded) {
            tableRow = TableRow(context)
            tableRow.layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, 1.0f)
        }

        return view
    }

    fun loadPostsFromSpf() {
        progressBar.visibility = View.GONE
        val response = sharedPreferences.getString("homeresponse", "")
        try {
            val emp = JSONObject(response)
            useridlist.clear()
            usernamelist.clear()
            userdplist.clear()
            posttimelist.clear()
            userbranchlist.clear()
            postidlist.clear()
            posttextlist.clear()
            postfilelist.clear()
            subjectlist.clear()
            likeslist.clear()
            commentslist.clear()

            val useridarray = emp.getJSONArray("userid")
            val usernamearray = emp.getJSONArray("name")
            val userbrancharray = emp.getJSONArray("branch")
            val userdparray = emp.getJSONArray("userdp")
            val posttimearray = emp.getJSONArray("posttime")
            val postdoubtidarray = emp.getJSONArray("postid")
            val posttextarray = emp.getJSONArray("posttext")
            val postfilearray = emp.getJSONArray("filename")
            val postsubjectarray = emp.getJSONArray("subject")
            val postlikesarray = emp.getJSONArray("likes")
            val postcommentsarray = emp.getJSONArray("comments")

            if (useridarray != null) {
                val len = useridarray.length()
                for (i in 0 until len) {
                    useridlist.add(useridarray.get(i).toString())
                    usernamelist.add(usernamearray.get(i).toString())
                    userdplist.add(userdparray.get(i).toString())
                    posttimelist.add(posttimearray.get(i).toString())
                    userbranchlist.add(userbrancharray.get(i).toString())
                    postidlist.add(postdoubtidarray.get(i).toString())
                    posttextlist.add(posttextarray.get(i).toString())
                    postfilelist.add(postfilearray.get(i).toString())
                    subjectlist.add(postsubjectarray.get(i).toString())
                    likeslist.add(postlikesarray.get(i).toString())
                    commentslist.add(postcommentsarray.get(i).toString())
                }
            }
            val homePostsAdapter =
              MyRecyclerHomePostsAdapter(
                  sharedPreferences, sharedPreferencesLike, dialog, homeActivity.requestQueue,
                  homeActivity, userid!!, useridlist, userdplist, usernamelist,
                  userbranchlist, posttimelist, postfilelist, postidlist, posttextlist, subjectlist,
                  likeslist, commentslist
              )
            mRecyclerViewPostHome.adapter = homePostsAdapter
        } catch (e: Exception) {

        }

    }

    fun volleyPostDataRequest() {
        swipeRefreshLayout.isRefreshing = true
        try {
          val url = DataUtils.serverurl + "postsHomeDataAdapter.php"
          val stringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->
              swipeRefreshLayout.isRefreshing = false
              progressBar.visibility = View.GONE
              try {
                val emp = JSONObject(response)
                val result = emp.getString("result")
                val spe = sharedPreferences.edit()
                spe.putString("homeresponse", response)
                spe.apply()
                if (result == "successful") {
                        useridlist.clear()
                        usernamelist.clear()
                        userdplist.clear()
                        posttimelist.clear()
                        userbranchlist.clear()
                        postidlist.clear()
                        posttextlist.clear()
                        postfilelist.clear()
                        subjectlist.clear()
                        likeslist.clear()
                        commentslist.clear()

                        val useridarray = emp.getJSONArray("userid")
                        val usernamearray = emp.getJSONArray("name")
                        val userbrancharray = emp.getJSONArray("branch")
                        val userdparray = emp.getJSONArray("userdp")
                        val posttimearray = emp.getJSONArray("posttime")
                        val postdoubtidarray = emp.getJSONArray("postid")
                        val posttextarray = emp.getJSONArray("posttext")
                        val postfilearray = emp.getJSONArray("filename")
                        val postsubjectarray = emp.getJSONArray("subject")
                        val postlikesarray = emp.getJSONArray("likes")
                        val postcommentsarray = emp.getJSONArray("comments")

                        if (useridarray != null) {
                            val len = useridarray.length()
                            for (i in 0 until len) {
                                useridlist.add(useridarray.get(i).toString())
                                usernamelist.add(usernamearray.get(i).toString())
                                userdplist.add(userdparray.get(i).toString())
                                posttimelist.add(posttimearray.get(i).toString())
                                userbranchlist.add(userbrancharray.get(i).toString())
                                postidlist.add(postdoubtidarray.get(i).toString())
                                posttextlist.add(posttextarray.get(i).toString())
                                postfilelist.add(postfilearray.get(i).toString())
                                subjectlist.add(postsubjectarray.get(i).toString())
                                likeslist.add(postlikesarray.get(i).toString())
                                commentslist.add(postcommentsarray.get(i).toString())
                            }
                        }
                        val homePostsAdapter =
                          MyRecyclerHomePostsAdapter(
                              sharedPreferences, sharedPreferencesLike, dialog,
                              homeActivity.requestQueue, homeActivity, userid!!, useridlist,
                              userdplist, usernamelist,
                              userbranchlist, posttimelist, postfilelist, postidlist, posttextlist,
                              subjectlist, likeslist, commentslist
                          )
                        homePostsAdapter.setHasStableIds(true)
                        mRecyclerViewPostHome.adapter = homePostsAdapter
                    }
                } catch (exception: Exception) {
                    Toast.makeText(homeActivity, exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                swipeRefreshLayout.isRefreshing = false
                progressBar.visibility = View.GONE
                //                    Toast.makeText(getActivity(), "Error! Please try again later...", Toast.LENGTH_SHORT).show();
            }) {
                override fun getParams(): Map<String, String> {
                    return HashMap()
                }
            }
            homeActivity.requestQueue.add(stringRequest)
        } catch (e: Exception) {
            Toast.makeText(homeActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }

    } override fun onResume() {
        super.onResume()
        if (sharedPreferences.getBoolean("newPost",false)){
            sharedPreferences.edit().putBoolean("newPost",false).apply()
            volleyPostDataRequest()
        }
    }
}
