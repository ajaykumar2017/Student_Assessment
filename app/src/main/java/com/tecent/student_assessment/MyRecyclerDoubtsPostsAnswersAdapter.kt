package com.tecent.student_assessment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
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
import java.io.ByteArrayOutputStream
import java.util.HashMap
import kotlin.collections.ArrayList

@Suppress("UNREACHABLE_CODE")
class MyRecyclerDoubtsPostsAnswersAdapter(dialog: ACProgressFlower, requestQueue: RequestQueue, userid: String, context: Context, answerObjectArrayList: ArrayList<AnswerObject>) : RecyclerView.Adapter<MyRecyclerDoubtsPostsAnswersAdapter.DoubtsPostsAnswersHolder>() {
    var mDialog: ACProgressFlower
    var mRequestQueue: RequestQueue
    var mContext: Context
    var mMyuserid: String
    var mAnswerObjectArrayList: ArrayList<AnswerObject>

    init {
        this.mDialog = dialog
        this.mRequestQueue = requestQueue
        this.mMyuserid = userid
        this.mContext = context
        this.mAnswerObjectArrayList = answerObjectArrayList
    }


    override fun getItemCount(): Int {
        return mAnswerObjectArrayList.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DoubtsPostsAnswersHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.indiview_post_comment, p0, false)
        return DoubtsPostsAnswersHolder(view)
    }

    override fun onBindViewHolder(postCommentsHolder: DoubtsPostsAnswersHolder, position: Int) {
        val answerObject = mAnswerObjectArrayList[position]
        mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(
                ExtraFunctions.serverurl + "userdp/" + answerObject.userDp
                , postCommentsHolder.iv_profile_image))
        postCommentsHolder.username.text = answerObject.userName + " " + "\u2022" + " "
        postCommentsHolder.commentTime.text = answerObject.answerTime
        if (mMyuserid != answerObject.userId) {
            postCommentsHolder.iv_delete_post.visibility = View.GONE
        }
        postCommentsHolder.iv_delete_post.setOnClickListener {
            val popup = PopupMenu(mContext, postCommentsHolder.iv_delete_post)
            //inflating menu from xml resource
            popup.inflate(R.menu.menu_comments_answers_and_replies)
            val popupMenu = popup.menu
            popupMenu.findItem(R.id.delete_comment).isVisible = false
            popupMenu.findItem(R.id.delete_reply).isVisible = false
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete_answer ->if (ExtraFunctions.isNetworkStatusAvialable(mContext)) {
                        val url = ExtraFunctions.serverurl + "deleteAnswersOfPostsDoubts.php"
                        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                            try {
                                val emp = JSONObject(response)
                                val result = emp.getString("result")
                                if (result == "successful") {
                                    Toast.makeText(mContext, "Answer Deleted successfully", Toast.LENGTH_SHORT).show()
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
                                MyData["answerid"] = answerObject.answerId
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
        postCommentsHolder.comment_text.text = answerObject.answerText
        postCommentsHolder.comment_image.setOnClickListener {
            animateIntent(postCommentsHolder.comment_image)
        }
        if (answerObject.repliesCount == "0") {
            postCommentsHolder.view_all_replies.text = "No Replies"
        } else if (answerObject.repliesCount == "1") {
            postCommentsHolder.view_all_replies.text = "Hide " + answerObject.repliesCount + " Reply"
            postCommentsHolder.view_all_replies.setOnClickListener {
                if (postCommentsHolder.comments_recyclerview.visibility == View.VISIBLE) {
                    postCommentsHolder.comments_recyclerview.visibility = View.GONE
                    postCommentsHolder.view_all_replies.text = "Show " + answerObject.repliesCount + " Reply"
                } else {
                    postCommentsHolder.comments_recyclerview.visibility = View.VISIBLE
                    postCommentsHolder.view_all_replies.text = "Hide " + answerObject.repliesCount + " Reply"

                }
            }
        } else {
            postCommentsHolder.view_all_replies.text = "Hide " + answerObject.repliesCount + " Replies"
            postCommentsHolder.view_all_replies.setOnClickListener {
                if (postCommentsHolder.comments_recyclerview.visibility == View.VISIBLE) {
                    postCommentsHolder.comments_recyclerview.visibility = View.GONE
                    postCommentsHolder.view_all_replies.text = "Show " + answerObject.repliesCount + " Replies"
                } else {
                    postCommentsHolder.comments_recyclerview.visibility = View.VISIBLE
                    postCommentsHolder.view_all_replies.text = "Hide " + answerObject.repliesCount + " Replies"

                }
            }
        }
        if (!(answerObject.answerImage == "")) {
            mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(
                    ExtraFunctions.serverurl + "postdoubts/postsDoubts_answers/" + answerObject.answerImage
                    , postCommentsHolder.comment_image))

        } else {
            postCommentsHolder.comment_image.visibility = View.GONE
        }
        postCommentsHolder.comments_recyclerview.setHasFixedSize(true)
        postCommentsHolder.comments_recyclerview.setLayoutManager(LinearLayoutManager(mContext))
        val adapter = MyRecyclerDoubtsPostsAnswersRepliesAdapter(mRequestQueue, mContext, mMyuserid, answerObject.replyObjectArrayList)
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
                        val url = ExtraFunctions.serverurl + "DoubtsPostsAnswersReplyData.php"
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
                                MyData["answerid"] = answerObject.answerId
                                MyData["userid"] = mMyuserid
                                MyData["replytext"] = dialogReply.editText_reply.text.toString().trim().replace("'", "\\'")
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


    inner class DoubtsPostsAnswersHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_profile_image: ImageView
        var comment_image: ImageView
        var iv_delete_post: ImageView
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
            iv_delete_post = itemView.findViewById(R.id.iv_delete_post)
        }
    }


    fun animateIntent(view: ImageView) {
        val intent = Intent(mContext, ImageViewerActivity::class.java)
        intent.putExtra("intentType", "byteArray")
        intent.putExtra(
                "imageByteArray",
                getFileDataFromDrawable(mContext, view.drawable)
        )
        val transitionName = mContext.getString(R.string.transition_string)
        val options= ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity,view as View,transitionName)
        ActivityCompat.startActivity(mContext, intent, options.toBundle())
    }
    fun getFileDataFromDrawable(context: Context, drawable: Drawable): ByteArray {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}