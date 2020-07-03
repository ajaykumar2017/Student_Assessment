package com.tecent.student_assessment.ui.activity

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.tecent.student_assessment.objects.DiscussionForumObject
import com.tecent.student_assessment.R.color
import com.tecent.student_assessment.R.drawable
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.ui.adapters.MyRecyclerDiscussionForumAdapter
import com.tecent.student_assessment.utils.ExtraFunctions
import kotlinx.android.synthetic.main.activity_discussion_forum.*
import kotlinx.android.synthetic.main.toolbar_main.*
import org.json.JSONObject
import java.util.HashMap

@Suppress("DEPRECATION")
class DiscussionForumActivity : AppCompatActivity() {
    lateinit var requestQueue: RequestQueue
    lateinit var sharedPreferences: SharedPreferences
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var recyclerViewDiscusForum: RecyclerView
    lateinit var dialog: ACProgressFlower
    lateinit var userid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            layout.activity_discussion_forum
        )
        setSupportActionBar(findViewById(
            id.toolbar_main
        ))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(
            drawable.ic_001_back
        )
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(-0x1))
        supportActionBar!!.setTitle("Discussion Forum")
        toolbar_main.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
        requestQueue = Volley.newRequestQueue(this)
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.BLUE)
                .fadeColor(Color.WHITE).build()
        swipeRefreshLayout=findViewById(
            id.swipeRefreshLayout
        )
        sharedPreferences = this.getSharedPreferences(
            ExtraFunctions.sharedPreferencesId, Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("userid", "")

        recyclerViewDiscusForum=findViewById<RecyclerView>(
            id.recyclerViewDisForum
        )
        recyclerViewDiscusForum.setHasFixedSize(true)
        recyclerViewDiscusForum.setLayoutManager(LinearLayoutManager(this))
        if (ExtraFunctions.isNetworkStatusAvailable(this)){
            volleyDiscussionForumDataRequest()
        }else{
            Toast.makeText(this,"No Internet connection!", Toast.LENGTH_SHORT).show()
        }
        send_message.setOnClickListener {
            if (et_discussionText.text.trim().toString().length<1)
                Toast.makeText(this@DiscussionForumActivity, "Please write something", Toast.LENGTH_SHORT).show()
            else{
                dialog.show()
                volleyTestDiscussionForum()
            }
        }
        loadDiscussionForumDataRequestFromSpf()
        swipeRefreshLayout.setOnRefreshListener {
            if (ExtraFunctions.isNetworkStatusAvailable(this))
                volleyDiscussionForumDataRequest()
            else {
                swipeRefreshLayout.isRefreshing = false
//                progressBar.setVisibility(View.GONE)
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show()
            }
        }
        et_discussionText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (et_discussionText.text.toString().trim { it <= ' ' }.length >= 1) {
                    //ivVectorImage.setColorFilter(getResources().getColor(R.color.colorPrimary))
                    send_message.setColorFilter(resources.getColor(
                        color.colorPrimary
                    ))
                } else if (et_discussionText.text.toString().trim { it <= ' ' }.length < 1) {
                    send_message.setColorFilter(resources.getColor(
                        color.lightenblue
                    ))
                }
            }
            override fun afterTextChanged(editable: Editable) {}
        })

    }

    //Google volley
    fun volleyTestDiscussionForum() {
        val url = ExtraFunctions.serverurl + "discussionForumData.php"
        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response -> jsonParser(response) }, Response.ErrorListener { error ->
            dialog.dismiss()
            Toast.makeText(this@DiscussionForumActivity, error.toString(), Toast.LENGTH_SHORT).show()
            //                Toast.makeText(CreatePostQueryDoubts.this, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
        }) {
            override fun getParams(): Map<String, String> {
                val MyData = HashMap<String, String>()
                MyData["userid"] = userid
                MyData["discussionText"] = et_discussionText.text.toString().replace("'", "\\'")
                return MyData
            }
        }
        requestQueue.add(stringRequest)
    }

    fun jsonParser(jsontext: String) {
        try {
            val emp = JSONObject(jsontext)
            val result = emp.getString("result")
            if (result == "successful") {
                et_discussionText.setText("")
                dialog.dismiss()
                Toast.makeText(this@DiscussionForumActivity, "Message uploaded successfully", Toast.LENGTH_SHORT).show()
                volleyDiscussionForumDataRequest()
            }
            if (result == "error") {
                dialog.dismiss()
                Toast.makeText(this@DiscussionForumActivity, "Error! Please try again later...", Toast.LENGTH_SHORT).show()
            }
        } catch (exception: Exception) {
            dialog.dismiss()
            exception.printStackTrace()
        }
    }

    fun loadDiscussionForumDataRequestFromSpf(){
        val response = sharedPreferences.getString("discussionForumResponse", "")
        try {
            val emp = JSONObject(response)
            val result = emp.getString("result")
            val spe = sharedPreferences.edit()
            spe.putString("discussionForumResponse", response)
            spe.apply()
            if (result == "successful") {
                val json = emp.getString("discussionForumList")
                val builder = GsonBuilder()
                val gson = builder.create()
                val discussionForumObjectArrayList:ArrayList<DiscussionForumObject> = gson.fromJson(
                        json,
                        object : TypeToken<List<DiscussionForumObject>>() {
                        }.type
                )
                val adapter =
                  MyRecyclerDiscussionForumAdapter(
                      dialog, requestQueue, userid, this, discussionForumObjectArrayList
                  )
                adapter.setHasStableIds(true)
                recyclerViewDiscusForum.adapter = adapter
            }
        } catch (exception: Exception) {
            Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun volleyDiscussionForumDataRequest(){
        try {
            val url = ExtraFunctions.serverurl + "discussionForumDataAdapter.php"
            val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                swipeRefreshLayout.isRefreshing = false
//                progressBar.setVisibility(View.GONE)
                try {
                    val emp = JSONObject(response)
                    val result = emp.getString("result")
                    val spe = sharedPreferences.edit()
                    spe.putString("discussionForumResponse", response)
                    spe.apply()
                    if (result == "successful") {
                        val json = emp.getString("discussionForumList")
                        val builder = GsonBuilder()
                        val gson = builder.create()
                        val discussionForumObjectArrayList:ArrayList<DiscussionForumObject> = gson.fromJson(
                                json,
                                object : TypeToken<List<DiscussionForumObject>>() {
                                }.type
                        )
                        val adapter =
                          MyRecyclerDiscussionForumAdapter(
                              dialog, requestQueue, userid, this, discussionForumObjectArrayList
                          )
                        adapter.setHasStableIds(true)
                        recyclerViewDiscusForum.adapter = adapter
                        swipeRefreshLayout.isRefreshing=false
                    }
                } catch (exception: Exception) {
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                swipeRefreshLayout.isRefreshing = false
//                progressBar.setVisibility(View.GONE)
                //                    Toast.makeText(getActivity(), "Error! Please try again later...", Toast.LENGTH_SHORT).show();
            }) {
                override fun getParams(): Map<String, String> {
                    val MyData = HashMap<String, String>()
                    return MyData
                }
            }
            requestQueue.add(stringRequest)
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }

    }
}
