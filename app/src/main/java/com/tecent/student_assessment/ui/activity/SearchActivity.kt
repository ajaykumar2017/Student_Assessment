package com.tecent.student_assessment.ui.activity

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.view.View
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.ui.adapters.MyRecyclerHomePostsAdapter
import com.tecent.student_assessment.ui.adapters.MyRecyclerPostDoubtsAdapter
import com.tecent.student_assessment.utils.ExtraFunctions
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class SearchActivity : AppCompatActivity() {
    internal lateinit var sharedPreferences: SharedPreferences
    internal lateinit var requestQueue: RequestQueue
    internal lateinit var dialog: ACProgressFlower
    lateinit var sharedPreferencesLike:SharedPreferences
    //posts
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
    //doubts
    lateinit var useridpostdoubtslist: ArrayList<String>
    lateinit var userdppostdoubtslist: ArrayList<String>
    lateinit var usernamepostdoubtslist: ArrayList<String>
    lateinit var userbranchpostdoubtslist: ArrayList<String>
    lateinit var posttimepostdoubtslist: ArrayList<String>
    lateinit var postdoubtidpostdoubtslist: ArrayList<String>
    lateinit var posttextpostdoubtslist: ArrayList<String>
    lateinit var postimagepostdoubtslist: ArrayList<String>
    lateinit var noofanswerspostdoubtslist: ArrayList<String>
    lateinit var userid:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_search)
        sharedPreferences = getSharedPreferences(
                ExtraFunctions.sharedPreferencesId,
                Context.MODE_PRIVATE
        )
        sharedPreferencesLike = getSharedPreferences(
                ExtraFunctions.sharedPreferencesLikeId,
                Context.MODE_PRIVATE
        )
        userid=sharedPreferences.getString("userid","")
        //posts
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
        //doubts
        useridpostdoubtslist = ArrayList()
        userdppostdoubtslist = ArrayList()
        usernamepostdoubtslist = ArrayList()
        posttimepostdoubtslist = ArrayList()
        postdoubtidpostdoubtslist = ArrayList<String>()
        userbranchpostdoubtslist = ArrayList()
        posttextpostdoubtslist = ArrayList()
        postimagepostdoubtslist = ArrayList<String>()
        noofanswerspostdoubtslist = ArrayList<String>()
        val linearLayoutManager =
            LinearLayoutManager(
                this, RecyclerView.VERTICAL, false
            )
        val linearLayoutManagerDoubtsPosts =
            LinearLayoutManager(
                this, RecyclerView.VERTICAL, false
            )

        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE).text("Uploading....")
                .fadeColor(Color.BLACK).build()
        recyclerView.layoutManager=linearLayoutManager
        recyclerViewDoubtsPosts.layoutManager=linearLayoutManagerDoubtsPosts
        requestQueue = Volley.newRequestQueue(this)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(string: String?): Boolean {
                val searchString=string!!.replace(" ","%")
                getPostsFromServer("%$searchString%")
                getPostsOfDoubtsFromServer("%$searchString%")
                return true
            }

            override fun onQueryTextChange(string: String?): Boolean {
                return false
            }
        })
    }

    fun getPostsFromServer(searchString:String){
        try {
            val url = ExtraFunctions.serverurl + "postsHomeDataAdapterBySearchActivity.php"
            val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                try {
                    val emp = JSONObject(response)
                    val result = emp.getString("result")
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
                        if (useridlist.size>0){
                            tvEmpty.visibility=View.GONE
                            recyclerView.visibility=View.VISIBLE
                            val homePostsAdapter =
                              MyRecyclerHomePostsAdapter(
                                  sharedPreferences, sharedPreferencesLike, dialog, requestQueue,
                                  this, userid, useridlist, userdplist, usernamelist,
                                  userbranchlist, posttimelist, postfilelist, postidlist,
                                  posttextlist, subjectlist, likeslist, commentslist
                              )
                            homePostsAdapter.setHasStableIds(true)
                            recyclerView.adapter = homePostsAdapter
                        }else{
//                            tvEmpty.visibility=View.VISIBLE
//                            recyclerView.visibility=View.GONE
                            makeGoneRecyclerView()
                        }
                    }
                } catch (exception: Exception) {
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                //                    Toast.makeText(getActivity(), "Error! Please try again later...", Toast.LENGTH_SHORT).show();
            }) {
                override fun getParams(): Map<String, String> {
                    val MyData = HashMap<String, String>()
                    MyData["searchText"] = searchString
                    return MyData
                }
            }
            requestQueue.add(stringRequest)
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun getPostsOfDoubtsFromServer(searchString: String){
        val url = ExtraFunctions.serverurl + "doubtPostsDataAdapterBySearchActivity.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
            try {
                val emp = JSONObject(response)
                val result = emp.getString("result")
                if (result == "successful") {
                    useridpostdoubtslist.clear()
                    usernamepostdoubtslist.clear()
                    userdppostdoubtslist.clear()
                    posttimepostdoubtslist.clear()
                    userbranchpostdoubtslist.clear()
                    postdoubtidpostdoubtslist.clear()
                    posttextpostdoubtslist.clear()
                    postimagepostdoubtslist.clear()
                    noofanswerspostdoubtslist.clear()

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
                            useridpostdoubtslist.add(useridarray.get(i).toString())
                            usernamepostdoubtslist.add(usernamearray.get(i).toString())
                            userdppostdoubtslist.add(userdparray.get(i).toString())
                            posttimepostdoubtslist.add(posttimearray.get(i).toString())
                            userbranchpostdoubtslist.add(userbrancharray.get(i).toString())
                            postdoubtidpostdoubtslist.add(postdoubtidarray.get(i).toString())
                            posttextpostdoubtslist.add(posttextarray.get(i).toString())
                            postimagepostdoubtslist.add(postimagearray.get(i).toString())
                            noofanswerspostdoubtslist.add(postNoofAnswersarray.get(i).toString())
                        }
                    }
                    if (useridpostdoubtslist.size>0){
                        tvEmpty.visibility=View.GONE
                        recyclerViewDoubtsPosts.visibility=View.VISIBLE
                        val postDoubtsAdapter =
                            MyRecyclerPostDoubtsAdapter(
                                sharedPreferences, dialog, requestQueue, this, userid,
                                useridpostdoubtslist, userdppostdoubtslist, usernamepostdoubtslist,
                                userbranchpostdoubtslist, posttimepostdoubtslist,
                                postimagepostdoubtslist, postdoubtidpostdoubtslist,
                                posttextpostdoubtslist, noofanswerspostdoubtslist
                            )
                        postDoubtsAdapter.setHasStableIds(true)
                        recyclerViewDoubtsPosts.setAdapter(postDoubtsAdapter)
                    }else{
                        makeGoneRecyclerView()
                    }
                }
            } catch (exception: Exception) {
                Toast.makeText(this, "some error occured! try again", Toast.LENGTH_SHORT).show()
            }
        }, Response.ErrorListener {
            //                Toast.makeText(getActivity(), "Error! Please try again later...", Toast.LENGTH_SHORT).show();
        }) {
            override fun getParams(): Map<String, String> {
                val MyData = HashMap<String, String>()
                MyData["searchText"] = searchString
                return MyData
            }
        }
        requestQueue.add(stringRequest)
    }

    fun makeGoneRecyclerView(){
        if (useridlist.size==0&&useridpostdoubtslist.size==0){
            tvEmpty.visibility=View.VISIBLE
            recyclerView.visibility=View.GONE
            recyclerViewDoubtsPosts.visibility=View.GONE
        }
    }
}
