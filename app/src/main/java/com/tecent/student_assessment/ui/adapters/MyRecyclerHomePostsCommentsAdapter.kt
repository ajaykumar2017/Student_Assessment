package com.tecent.student_assessment.ui.adapters

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
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
import com.tecent.student_assessment.objects.CommentObject
import com.tecent.student_assessment.ui.activity.CommentsPostHomeActivity
import com.tecent.student_assessment.ui.activity.ImageViewerActivity
import com.tecent.student_assessment.R.color
import com.tecent.student_assessment.R.drawable
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.R.menu
import com.tecent.student_assessment.R.string
import com.tecent.student_assessment.ui.adapters.MyRecyclerHomePostsCommentsAdapter.PostsCommentsHolder
import com.tecent.student_assessment.utils.ExtraFunctions
import kotlinx.android.synthetic.main.custom_dialog_comments_reply.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.HashMap
import kotlin.collections.ArrayList

@Suppress("UNREACHABLE_CODE")
class MyRecyclerHomePostsCommentsAdapter(dialog: ACProgressFlower, requestQueue: RequestQueue, postid:String, userid: String, context: CommentsPostHomeActivity, commentObjectArrayList: ArrayList<CommentObject>) : RecyclerView.Adapter<PostsCommentsHolder>() {
    var mDialog: ACProgressFlower
    var mRequestQueue: RequestQueue
    var mPostid: String
    var mContext: CommentsPostHomeActivity
    var mMyuserid: String
    var mCommentObjectArrayList: ArrayList<CommentObject>

    init {
        this.mDialog = dialog
        this.mRequestQueue = requestQueue
        this.mPostid=postid
        this.mMyuserid = userid
        this.mContext = context
        this.mCommentObjectArrayList = commentObjectArrayList
    }


    override fun getItemCount(): Int {
        return mCommentObjectArrayList.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PostsCommentsHolder {
        val view = LayoutInflater.from(mContext).inflate(
            layout.indiview_post_comment, p0, false)
        return PostsCommentsHolder(view)
    }

    override fun onBindViewHolder(postCommentsHolder: PostsCommentsHolder, position: Int) {
        val commentObject = mCommentObjectArrayList[position]
        mRequestQueue.add(
            ExtraFunctions.createImageRequestFromUrl(
                ExtraFunctions.serverurl + "userdp/" + commentObject.userDp
                , postCommentsHolder.iv_profile_image))
        postCommentsHolder.username.text = commentObject.userName + " " + "\u2022" + " "
        postCommentsHolder.commentTime.text = commentObject.commentTime
        if (mMyuserid != commentObject.userId) {
            postCommentsHolder.iv_delete_post.visibility = View.GONE
        }
        postCommentsHolder.iv_delete_post.setOnClickListener {
            val popup = PopupMenu(mContext, postCommentsHolder.iv_delete_post)
            //inflating menu from xml resource
            popup.inflate(
                menu.menu_comments_answers_and_replies
            )
            val popupMenu = popup.menu
            popupMenu.findItem(id.delete_answer).isVisible = false
            popupMenu.findItem(id.delete_reply).isVisible = false
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    id.delete_comment ->if (ExtraFunctions.isNetworkStatusAvailable(mContext)) {
                        val url = ExtraFunctions.serverurl + "deleteCommentsOfPosts.php"
                        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                            try {
                                val emp = JSONObject(response)
                                val result = emp.getString("result")
                                if (result == "successful") {
                                    Toast.makeText(mContext, "Comment Deleted successfully", Toast.LENGTH_SHORT).show()
                                    mContext.volleyCommentDataRequest(mPostid)
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
                                MyData["commentid"] = commentObject.commentId
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
        postCommentsHolder.comment_image.setOnClickListener {
            animateIntent(postCommentsHolder.comment_image)
        }
        postCommentsHolder.comment_text.text = commentObject.commentText
        //long press click copy text
        postCommentsHolder.comment_text.setOnLongClickListener {
            var cm: ClipboardManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clip = ClipData.newPlainText(postCommentsHolder.comment_text.text.toString(),postCommentsHolder.comment_text.text)
            cm.setPrimaryClip(clip)
            Toast.makeText(mContext, "Text Copied to clipboard", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        if (commentObject.repliesCount == "0") {
            postCommentsHolder.view_all_replies.text = "No Replies"
        } else if (commentObject.repliesCount == "1") {
            postCommentsHolder.view_all_replies.text = " Hide " + commentObject.repliesCount + " Reply"
            postCommentsHolder.view_all_replies.setCompoundDrawablesWithIntrinsicBounds(
                drawable.ic_002_drop_up_arrow,0,0,0)
            postCommentsHolder.view_all_replies.setOnClickListener {
                if (postCommentsHolder.comments_recyclerview.visibility == View.VISIBLE) {
                    postCommentsHolder.comments_recyclerview.visibility = View.GONE
                    postCommentsHolder.view_all_replies.text = " Show " + commentObject.repliesCount + " Reply"
                    postCommentsHolder.view_all_replies.setCompoundDrawablesWithIntrinsicBounds(
                        drawable.ic_001_drop_down_arrow,0,0,0)
                } else {
                    postCommentsHolder.comments_recyclerview.visibility = View.VISIBLE
                    postCommentsHolder.view_all_replies.text = " Hide " + commentObject.repliesCount + " Reply"
                    postCommentsHolder.view_all_replies.setCompoundDrawablesWithIntrinsicBounds(
                        drawable.ic_002_drop_up_arrow,0,0,0)

                }
            }
        } else {
            postCommentsHolder.view_all_replies.text = " Hide " + commentObject.repliesCount + " Replies"
            postCommentsHolder.view_all_replies.setCompoundDrawablesWithIntrinsicBounds(
                drawable.ic_002_drop_up_arrow,0,0,0)
            postCommentsHolder.view_all_replies.setOnClickListener {
                if (postCommentsHolder.comments_recyclerview.visibility == View.VISIBLE) {
                    postCommentsHolder.comments_recyclerview.visibility = View.GONE
                    postCommentsHolder.view_all_replies.text = " Show " + commentObject.repliesCount + " Replies"
                    postCommentsHolder.view_all_replies.setCompoundDrawablesWithIntrinsicBounds(
                        drawable.ic_001_drop_down_arrow,0,0,0)
                } else {
                    postCommentsHolder.comments_recyclerview.visibility = View.VISIBLE
                    postCommentsHolder.view_all_replies.text = " Hide " + commentObject.repliesCount + " Replies"
                    postCommentsHolder.view_all_replies.setCompoundDrawablesWithIntrinsicBounds(
                        drawable.ic_002_drop_up_arrow,0,0,0)

                }
            }
        }
        if (!(commentObject.commentImage == "")) {
            mRequestQueue.add(
                ExtraFunctions.createImageRequestFromUrl(
                    ExtraFunctions.serverurl + "posts/posts_comments/" + commentObject.commentImage
                    , postCommentsHolder.comment_image))

        } else {
            postCommentsHolder.comment_image.visibility = View.GONE
        }
        postCommentsHolder.comments_recyclerview.setHasFixedSize(true)
        postCommentsHolder.comments_recyclerview.setLayoutManager(
            LinearLayoutManager(mContext)
        )
        val adapter =
            MyRecyclerHomePostsCommentsRepliesAdapter(
                mRequestQueue, mContext, mPostid, mMyuserid, commentObject.replyObjectArrayList
            )
        postCommentsHolder.comments_recyclerview.adapter = adapter

        postCommentsHolder.tv_reply_btn.setOnClickListener {
            val dialogReply = Dialog(mContext)
            // Include dialog.xml file
            dialogReply.setContentView(
                layout.custom_dialog_comments_reply
            )
            dialogReply.show()
            dialogReply.editText_reply.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (dialogReply.editText_reply.text.length >= 1) {
                        dialogReply.btn_reply.setBackgroundColor(mContext.getResources().getColor(
                            color.colorPrimary
                        ))
                    } else if (dialogReply.editText_reply.text.length < 1) {
                        dialogReply.btn_reply.setBackgroundColor(mContext.getResources().getColor(
                            color.smalldarkgrey
                        ))
                    }
                }

                override fun afterTextChanged(editable: Editable) {}
            })
            dialogReply.btn_reply.setOnClickListener {
                if (dialogReply.editText_reply.text.length < 1) {
                    Toast.makeText(mContext, "Please write something..", Toast.LENGTH_SHORT).show()
                } else {
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
                                    mContext.volleyCommentDataRequest(mPostid)
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


    inner class PostsCommentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            iv_profile_image = itemView.findViewById(
                id.iv_profile_image
            )
            comment_image = itemView.findViewById(
                id.comment_image
            )
            username = itemView.findViewById(
                id.username
            )
            commentTime = itemView.findViewById(
                id.commentTime
            )
            comment_text = itemView.findViewById(
                id.comment_text
            )
            view_all_replies = itemView.findViewById(
                id.view_all_replies
            )
            tv_reply_btn = itemView.findViewById(
                id.tv_reply_btn
            )
            comments_recyclerview = itemView.findViewById(
                id.comments_recyclerview
            )
            iv_delete_post = itemView.findViewById(
                id.iv_delete_post
            )
        }
    }


    fun animateIntent(view: ImageView) {
        val intent = Intent(mContext, ImageViewerActivity::class.java)
        intent.putExtra("intentType", "byteArray")
        intent.putExtra(
                "imageByteArray",
                getFileDataFromDrawable(mContext, view.drawable)
        )
        val transitionName = mContext.getString(
            string.transition_string
        )
        val options= ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity,view as View,transitionName)
        ActivityCompat.startActivity(mContext, intent, options.toBundle())
    }
    fun getFileDataFromDrawable(context: Context, drawable: Drawable): ByteArray {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}