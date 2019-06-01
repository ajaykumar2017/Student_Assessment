package com.tecent.student_assessment

import android.content.Context
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap
import kotlin.collections.ArrayList

@Suppress("UNREACHABLE_CODE")
class MyRecyclerDoubtsPostsAnswersRepliesAdapter(requestQueue: RequestQueue, context:Context,userid:String, replyObjectArrayList: ArrayList<ReplyObjectDoubtsPostsAnswers>) : RecyclerView.Adapter<MyRecyclerDoubtsPostsAnswersRepliesAdapter.DoubtsPostsAnswersRepliesHolder>() {
    var mRepliesObjectArrayList:ArrayList<ReplyObjectDoubtsPostsAnswers>
    var mRequestQueue:RequestQueue
    var mContext:Context
    var mMyuserid:String
    init {
        this.mRequestQueue=requestQueue
        this.mContext=context
        this.mMyuserid=userid
        this.mRepliesObjectArrayList=replyObjectArrayList
    }
    override fun onBindViewHolder(doubtsPostsAnswersRepliesHolder: DoubtsPostsAnswersRepliesHolder, position: Int) {
        val replyObject= mRepliesObjectArrayList[position]
        mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(
                ExtraFunctions.serverurl+"userdp/"+replyObject.userDp
                ,doubtsPostsAnswersRepliesHolder.iv_profile_image))
        doubtsPostsAnswersRepliesHolder.username.text=replyObject.userName+" "+"\u2022"+" "
        doubtsPostsAnswersRepliesHolder.replyText.text=replyObject.replyText
        doubtsPostsAnswersRepliesHolder.timeAgo.text=replyObject.replyTime
        if (mMyuserid!=replyObject.userId){
            doubtsPostsAnswersRepliesHolder.iv_delete_post_reply.visibility=View.GONE
        }
        doubtsPostsAnswersRepliesHolder.iv_delete_post_reply.setOnClickListener {
            val popup = PopupMenu(mContext, doubtsPostsAnswersRepliesHolder.iv_delete_post_reply)
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_comments_answers_and_replies)
            val popupMenu = popup.menu
            popupMenu.findItem(R.id.delete_answer).isVisible = false
            popupMenu.findItem(R.id.delete_comment).isVisible = false
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete_reply ->if (ExtraFunctions.isNetworkStatusAvialable(mContext)) {
                        val url = ExtraFunctions.serverurl + "deleteAnswersRepliesOfDoubtsPosts.php"
                        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                            try {
                                val emp = JSONObject(response)
                                val result = emp.getString("result")
                                if (result == "successful") {
                                    Toast.makeText(mContext, "Reply Deleted successfully", Toast.LENGTH_SHORT).show()
                                }
                                if (result == "error") {
                                    Toast.makeText(mContext, "Error! Please try again later...", Toast.LENGTH_SHORT).show()
                                }
                            } catch (exception: Exception) {
                                exception.printStackTrace()
                            }
                        }, Response.ErrorListener { error ->
                            Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show()
                            //                Toast.makeText(CreatePostQueryDoubts.this, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
                        }) {
                            override fun getParams(): Map<String, String> {
                                val MyData = HashMap<String, String>()
                                MyData["replyid"] = replyObject.replyId
                                return MyData
                            }
                        }
                        mRequestQueue.add(stringRequest)
                    } else {
                        Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show()
                    }
                }
                false
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int {
        return mRepliesObjectArrayList.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyRecyclerDoubtsPostsAnswersRepliesAdapter.DoubtsPostsAnswersRepliesHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.indiview_post_comment_replies, p0, false)
        return DoubtsPostsAnswersRepliesHolder(view)
    }




    inner class DoubtsPostsAnswersRepliesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_profile_image: ImageView
        var iv_delete_post_reply:ImageView
        var username: TextView
        var replyText: TextView
        var timeAgo: TextView

        init {
            iv_profile_image = itemView.findViewById(R.id.iv_profile_image)
            iv_delete_post_reply=itemView.findViewById(R.id.iv_delete_post_reply)
            username = itemView.findViewById(R.id.username)
            replyText = itemView.findViewById(R.id.replyText)
            timeAgo = itemView.findViewById(R.id.timeAgo)
        }
    }
}