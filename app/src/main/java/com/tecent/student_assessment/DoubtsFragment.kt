package com.tecent.student_assessment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import org.json.JSONArray
import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap

import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower

class DoubtsFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var post_name_with_text: TextView
    lateinit var profile_image: ImageView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var ll_createpostquery: LinearLayout
    private var mRecyclerViewPost: RecyclerView? = null
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var progressBar: ProgressBar
    lateinit var dialog: ACProgressFlower
    lateinit var tableRow: TableRow

    lateinit var useridlist: ArrayList<String>
    lateinit var userdplist: ArrayList<String>
    lateinit var usernamelist: ArrayList<String>
    lateinit var userbranchlist: ArrayList<String>
    lateinit var posttimelist: ArrayList<String>
    lateinit var postdoubtidlist: ArrayList<String>
    lateinit var posttextlist: ArrayList<String>
    lateinit var postimagelist: ArrayList<String>
    lateinit var noofanswerslist: ArrayList<String>
    internal var userid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.doubts_fragment, container, false)
        mainActivity=activity as MainActivity
        post_name_with_text = view.findViewById(R.id.textView1)
        ll_createpostquery = view.findViewById(R.id.ll_createpostquery)
        profile_image = view.findViewById(R.id.profile_image)
        swipeRefreshLayout = view.findViewById<View>(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        progressBar = view.findViewById(R.id.progress_bar)
        dialog = ACProgressFlower.Builder(mainActivity)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build()
        dialog.setCancelable(false)

        mRecyclerViewPost = view.findViewById(R.id.recycler_view_post)
        mRecyclerViewPost!!.setHasFixedSize(false)
        mRecyclerViewPost!!.layoutManager = LinearLayoutManager(mainActivity)

        useridlist = ArrayList()
        userdplist = ArrayList()
        usernamelist = ArrayList()
        posttimelist = ArrayList()
        postdoubtidlist = ArrayList()
        userbranchlist = ArrayList()
        posttextlist = ArrayList()
        postimagelist = ArrayList()
        noofanswerslist = ArrayList()

        sharedPreferences = mainActivity.getSharedPreferences(ExtraFunctions.sharedPreferencesId, Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "")
        userid = sharedPreferences.getString("userid", "")
        post_name_with_text.text = "Hi $name Do you want to ask a doubt?"
        ll_createpostquery.setOnClickListener {
            val intent = Intent(mainActivity, CreatePostQueryDoubts::class.java)
            startActivity(intent)
        }

        val userdp = sharedPreferences.getString("userdp", "")
        mainActivity.requestQueue.add<Bitmap>(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "userdp/" + userdp, profile_image))
        loadDoubtsPostsFromSpf()
        if (ExtraFunctions.isNetworkStatusAvialable(mainActivity)) {
            volleyPostDataRequest()
        } else {
            Toast.makeText(mainActivity, "No Internet Connection!", Toast.LENGTH_SHORT).show()
        }
        swipeRefreshLayout.setOnRefreshListener {
            if (ExtraFunctions.isNetworkStatusAvialable(mainActivity))
                volleyPostDataRequest()
            else {
                swipeRefreshLayout.isRefreshing = false
                progressBar.visibility = View.GONE
                Toast.makeText(mainActivity, "No Internet Connection!", Toast.LENGTH_SHORT).show()
            }
        }

        if (isAdded) {
            tableRow = TableRow(context)
            tableRow.layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, 1.0f)
        }
        return view
    }

    fun loadDoubtsPostsFromSpf() {
        swipeRefreshLayout.isRefreshing = true
        progressBar.visibility = View.GONE
        val response = sharedPreferences.getString("doubtresponse", "")
        try {
            val emp = JSONObject(response)
            useridlist.clear()
            usernamelist.clear()
            userdplist.clear()
            posttimelist.clear()
            userbranchlist.clear()
            postdoubtidlist.clear()
            posttextlist.clear()
            postimagelist.clear()
            noofanswerslist.clear()

            val useridarray = emp.getJSONArray("userid")
            val usernamearray = emp.getJSONArray("name")
            val userbrancharray = emp.getJSONArray("branch")
            val userdparray = emp.getJSONArray("userdp")
            val posttimearray = emp.getJSONArray("posttime")
            val postdoubtidarray = emp.getJSONArray("postdoubtid")
            val posttextarray = emp.getJSONArray("posttext")
            val postimagearray = emp.getJSONArray("postimage")
            val postNoofAnswersarray = emp.getJSONArray("answers")

            if (useridarray != null) {
                val len = useridarray.length()
                for (i in 0 until len) {
                    useridlist.add(useridarray.get(i).toString())
                    usernamelist.add(usernamearray.get(i).toString())
                    userdplist.add(userdparray.get(i).toString())
                    posttimelist.add(posttimearray.get(i).toString())
                    userbranchlist.add(userbrancharray.get(i).toString())
                    postdoubtidlist.add(postdoubtidarray.get(i).toString())
                    posttextlist.add(posttextarray.get(i).toString())
                    postimagelist.add(postimagearray.get(i).toString())
                    noofanswerslist.add(postNoofAnswersarray.get(i).toString())
                }
            }
            val postDoubtsAdapter = MyRecyclerPostDoubtsAdapter(sharedPreferences, dialog, mainActivity.requestQueue, mainActivity, userid!!, useridlist, userdplist, usernamelist,
                    userbranchlist, posttimelist, postimagelist, postdoubtidlist, posttextlist, noofanswerslist)
            mRecyclerViewPost!!.adapter = postDoubtsAdapter
        } catch (e: Exception) {

        }

    }

    fun volleyPostDataRequest() {
        try {
            val url = ExtraFunctions.serverurl + "doubtPostsDataAdapter.php"
            val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
                swipeRefreshLayout.isRefreshing = false
                progressBar.visibility = View.GONE
                try {
                    val emp = JSONObject(response)
                    val result = emp.getString("result")
                    val spe = sharedPreferences.edit()
                    spe.putString("doubtresponse", response)
                    spe.apply()
                    if (result == "successful") {
                        useridlist.clear()
                        usernamelist.clear()
                        userdplist.clear()
                        posttimelist.clear()
                        userbranchlist.clear()
                        postdoubtidlist.clear()
                        posttextlist.clear()
                        postimagelist.clear()
                        noofanswerslist.clear()

                        val useridarray = emp.getJSONArray("userid")
                        val usernamearray = emp.getJSONArray("name")
                        val userbrancharray = emp.getJSONArray("branch")
                        val userdparray = emp.getJSONArray("userdp")
                        val posttimearray = emp.getJSONArray("posttime")
                        val postdoubtidarray = emp.getJSONArray("postdoubtid")
                        val posttextarray = emp.getJSONArray("posttext")
                        val postimagearray = emp.getJSONArray("postimage")
                        val postNoofAnswersarray = emp.getJSONArray("answers")

                        if (useridarray != null) {
                            val len = useridarray.length()
                            for (i in 0 until len) {
                                useridlist.add(useridarray.get(i).toString())
                                usernamelist.add(usernamearray.get(i).toString())
                                userdplist.add(userdparray.get(i).toString())
                                posttimelist.add(posttimearray.get(i).toString())
                                userbranchlist.add(userbrancharray.get(i).toString())
                                postdoubtidlist.add(postdoubtidarray.get(i).toString())
                                posttextlist.add(posttextarray.get(i).toString())
                                postimagelist.add(postimagearray.get(i).toString())
                                noofanswerslist.add(postNoofAnswersarray.get(i).toString())
                            }
                        }
                        val postDoubtsAdapter = MyRecyclerPostDoubtsAdapter(sharedPreferences, dialog, mainActivity.requestQueue, mainActivity, userid!!, useridlist, userdplist, usernamelist,
                                userbranchlist, posttimelist, postimagelist, postdoubtidlist, posttextlist, noofanswerslist)
                        postDoubtsAdapter.setHasStableIds(true)
                        mRecyclerViewPost!!.adapter = postDoubtsAdapter
                    }
                } catch (exception: Exception) {
                    Toast.makeText(mainActivity, "some error occured! try again", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                swipeRefreshLayout.isRefreshing = false
                progressBar.visibility = View.GONE
                //                Toast.makeText(getActivity(), "Error! Please try again later...", Toast.LENGTH_SHORT).show();
            }) {
                override fun getParams(): Map<String, String> {
                    return HashMap()
                }
            }
            mainActivity.requestQueue.add(stringRequest)
        } catch (e: Exception) {
            Toast.makeText(mainActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }


    }
}
