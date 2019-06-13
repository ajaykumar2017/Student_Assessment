package com.tecent.student_assessment

import android.content.ClipData
import android.content.ClipboardManager
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
class MyRecyclerPostsCommentsRepliesAdapterForSinglePosts(requestQueue: RequestQueue, context:Context, postid:String,userid:String,replyObjectArrayList: ArrayList<ReplyObject>) : RecyclerView.Adapter<MyRecyclerPostsCommentsRepliesAdapterForSinglePosts.PostsCommentsRepliesHolder>() {
    var mRepliesObjectArrayList:ArrayList<ReplyObject>
    var mRequestQueue:RequestQueue
    var mContext:Context
    var mPostid:String
    var mMyuserid:String
    init {
        this.mRequestQueue=requestQueue
        this.mContext=context
        mPostid=postid
        this.mMyuserid=userid
        this.mRepliesObjectArrayList=replyObjectArrayList
    }
    override fun onBindViewHolder(postsCommentsRepliesHolder: PostsCommentsRepliesHolder, position: Int) {
        val replyObject= mRepliesObjectArrayList[position]
        mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(
                ExtraFunctions.serverurl+"userdp/"+replyObject.userDp
                ,postsCommentsRepliesHolder.iv_profile_image))
        postsCommentsRepliesHolder.username.text=replyObject.userName+" "+"\u2022"+" "
        postsCommentsRepliesHolder.replyText.text=replyObject.replyText
        //long press click copy text
        postsCommentsRepliesHolder.replyText.setOnLongClickListener {
            var cm: ClipboardManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clip: ClipData = ClipData.newPlainText(postsCommentsRepliesHolder.replyText.text.toString(),postsCommentsRepliesHolder.replyText.text)
            cm.primaryClip=clip
            Toast.makeText(mContext, "Text Copied to clipboard", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        postsCommentsRepliesHolder.timeAgo.text=replyObject.replyTime
        if (mMyuserid!=replyObject.userId){
            postsCommentsRepliesHolder.iv_delete_post_reply.visibility=View.GONE
        }
        postsCommentsRepliesHolder.iv_delete_post_reply.setOnClickListener {
            val popup = PopupMenu(mContext, postsCommentsRepliesHolder.iv_delete_post_reply)
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_comments_answers_and_replies)
            val popupMenu = popup.menu
            popupMenu.findItem(R.id.delete_answer).isVisible = false
            popupMenu.findItem(R.id.delete_comment).isVisible = false
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete_reply ->if (ExtraFunctions.isNetworkStatusAvialable(mContext)) {
                        val url = ExtraFunctions.serverurl + "deleteCommentsRepliesOfPosts.php"
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

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyRecyclerPostsCommentsRepliesAdapterForSinglePosts.PostsCommentsRepliesHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.indiview_post_comment_replies, p0, false)
        return PostsCommentsRepliesHolder(view)
    }




    inner class PostsCommentsRepliesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
    override fun getItemViewType(position: Int): Int {
        return position
    }
}