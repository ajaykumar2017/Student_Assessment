package com.tecent.student_assessment

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_comments_post_home.*
import kotlinx.android.synthetic.main.custom_dialog_comments_reply.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap
import kotlin.collections.ArrayList

@Suppress("UNREACHABLE_CODE")
class MyRecyclerPostsCommentsAdapter(dialog: ACProgressFlower, requestQueue: RequestQueue, userid: String, context: Context, commentObjectArrayList: ArrayList<CommentObject>) : RecyclerView.Adapter<MyRecyclerPostsCommentsAdapter.PostsCommentsHolder>() {
    var mDialog: ACProgressFlower
    var mRequestQueue: RequestQueue
    var mContext: Context
    var mMyuserid: String
    var mCommentObjectArrayList: ArrayList<CommentObject>

    init {
        this.mDialog = dialog
        this.mRequestQueue = requestQueue
        this.mMyuserid = userid
        this.mContext = context
        this.mCommentObjectArrayList = commentObjectArrayList
    }


    override fun getItemCount(): Int {
        return mCommentObjectArrayList.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PostsCommentsHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.indiview_post_comment, p0, false)
        return PostsCommentsHolder(view)
    }

    override fun onBindViewHolder(postCommentsHolder: PostsCommentsHolder, position: Int) {
        val commentObject = mCommentObjectArrayList[position]
        mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(
                ExtraFunctions.serverurl + "userdp/" + commentObject.userDp
                , postCommentsHolder.iv_profile_image))
        postCommentsHolder.username.text = commentObject.userName + " " + "\u2022" + " "
        postCommentsHolder.commentTime.text = commentObject.commentTime
        postCommentsHolder.comment_text.text = commentObject.commentText
        if (commentObject.repliesCount == "0") {
            postCommentsHolder.view_all_replies.text = "No Replies"
        } else if (commentObject.repliesCount == "1") {
            postCommentsHolder.view_all_replies.text = "Hide " + commentObject.repliesCount + " Reply"
            postCommentsHolder.view_all_replies.setOnClickListener {
                if (postCommentsHolder.comments_recyclerview.visibility == View.VISIBLE) {
                    postCommentsHolder.comments_recyclerview.visibility = View.GONE
                    postCommentsHolder.view_all_replies.text = "Show " + commentObject.repliesCount + " Reply"
                } else {
                    postCommentsHolder.comments_recyclerview.visibility = View.VISIBLE
                    postCommentsHolder.view_all_replies.text = "Hide " + commentObject.repliesCount + " Reply"

                }
            }
        } else {
            postCommentsHolder.view_all_replies.text = "Hide " + commentObject.repliesCount + " Replies"
            postCommentsHolder.view_all_replies.setOnClickListener {
                if (postCommentsHolder.comments_recyclerview.visibility == View.VISIBLE) {
                    postCommentsHolder.comments_recyclerview.visibility = View.GONE
                    postCommentsHolder.view_all_replies.text = "Show " + commentObject.repliesCount + " Replies"
                } else {
                    postCommentsHolder.comments_recyclerview.visibility = View.VISIBLE
                    postCommentsHolder.view_all_replies.text = "Hide " + commentObject.repliesCount + " Replies"

                }
            }
        }
        if (!(commentObject.commentImage == "")) {
            mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(
                    ExtraFunctions.serverurl + "posts/posts_comments/" + commentObject.commentImage
                    , postCommentsHolder.comment_image))

        } else {
            postCommentsHolder.comment_image.visibility = View.GONE
        }
        postCommentsHolder.comments_recyclerview.setHasFixedSize(true)
        postCommentsHolder.comments_recyclerview.setLayoutManager(LinearLayoutManager(mContext))
        val adapter = MyRecyclerPostsCommentsRepliesAdapter(mRequestQueue, mContext, commentObject.replyObjectArrayList)
        postCommentsHolder.comments_recyclerview.adapter = adapter

        postCommentsHolder.tv_reply_btn.setOnClickListener {
            val dialogReply = Dialog(mContext)
            // Include dialog.xml file
            dialogReply.setContentView(R.layout.custom_dialog_comments_reply)
            dialogReply.show()
            dialogReply.editText_reply.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (dialogReply.editText_reply.text.length >= 1) {
                        dialogReply.btn_reply.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary))
                    } else if (dialogReply.editText_reply.text.length < 1) {
                        dialogReply.btn_reply.setBackgroundColor(mContext.getResources().getColor(R.color.smalldarkgrey))
                    }
                }

                override fun afterTextChanged(editable: Editable) {}
            })
            dialogReply.btn_reply.setOnClickListener {
                if (dialogReply.editText_reply.text.length<1){
                    Toast.makeText(mContext, "Please write something..", Toast.LENGTH_SHORT).show()
                }else{
                    mDialog.show()
                    try {
                        val url = ExtraFunctions.serverurl + "PostsCommentsReplyData.php"
                        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
//                progressBar.setVisibility(View.GONE)
                            try {
                                val emp = JSONObject(response)
                                val result = emp.getString("result")
                                if (result == "successful") {
                                    Toast.makeText(mContext, "Replied successfully", Toast.LENGTH_SHORT).show()
                                    dialogReply.dismiss()
                                    mDialog.dismiss()
                                }
                            } catch (exception: Exception) {
                                mDialog.dismiss()
                                Toast.makeText(mContext, exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }, Response.ErrorListener {
                        }) {
                            override fun getParams(): Map<String, String> {
                                val MyData = HashMap<String, String>()
                                MyData["commentid"] = commentObject.commentId
                                MyData["userid"] = mMyuserid
                                MyData["replytext"] = dialogReply.editText_reply.text.toString()
                                return MyData
                            }
                        }
                        mRequestQueue.add(stringRequest)
                    } catch (e: Exception) {
                        mDialog.dismiss()
                        Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }


    inner class PostsCommentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_profile_image: ImageView
        var comment_image: ImageView
        var username: TextView
        var commentTime: TextView
        var comment_text: TextView
        var view_all_replies: TextView
        var tv_reply_btn: TextView
        var comments_recyclerview: RecyclerView

        init {
            iv_profile_image = itemView.findViewById(R.id.iv_profile_image)
            comment_image = itemView.findViewById(R.id.comment_image)
            username = itemView.findViewById(R.id.username)
            commentTime = itemView.findViewById(R.id.commentTime)
            comment_text = itemView.findViewById(R.id.comment_text)
            view_all_replies = itemView.findViewById(R.id.view_all_replies)
            tv_reply_btn = itemView.findViewById(R.id.tv_reply_btn)
            comments_recyclerview = itemView.findViewById(R.id.comments_recyclerview)
        }
    }
}