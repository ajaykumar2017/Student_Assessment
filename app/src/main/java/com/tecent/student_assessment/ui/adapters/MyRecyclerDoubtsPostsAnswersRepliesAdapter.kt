package com.tecent.student_assessment.ui.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.tecent.student_assessment.ui.activity.AnswersDoubtsPostsDoubtsActivity
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.R.menu
import com.tecent.student_assessment.objects.ReplyForDoubtsPostsAnswersObject
import com.tecent.student_assessment.ui.adapters.MyRecyclerDoubtsPostsAnswersRepliesAdapter.DoubtsPostsAnswersRepliesHolder
import com.tecent.student_assessment.utils.ExtraFunctions
import org.json.JSONObject
import java.util.HashMap
import kotlin.collections.ArrayList

@Suppress("UNREACHABLE_CODE")
class MyRecyclerDoubtsPostsAnswersRepliesAdapter(requestQueue: RequestQueue, context: AnswersDoubtsPostsDoubtsActivity, doubtPostid:String, userid:String, replyForArrayListObject: ArrayList<ReplyForDoubtsPostsAnswersObject>) : RecyclerView.Adapter<DoubtsPostsAnswersRepliesHolder>() {
    var mRepliesForArrayListObject:ArrayList<ReplyForDoubtsPostsAnswersObject>
    var mRequestQueue:RequestQueue
    var mContext: AnswersDoubtsPostsDoubtsActivity
    var mDoubtPostId:String
    var mMyuserid:String
    init {
        this.mRequestQueue=requestQueue
        this.mContext=context
        mDoubtPostId=doubtPostid
        this.mMyuserid=userid
        this.mRepliesForArrayListObject=replyForArrayListObject
    }
    override fun onBindViewHolder(doubtsPostsAnswersRepliesHolder: DoubtsPostsAnswersRepliesHolder, position: Int) {
        val replyObject= mRepliesForArrayListObject[position]
        mRequestQueue.add(
            ExtraFunctions.createImageRequestFromUrl(
                ExtraFunctions.serverurl+"userdp/"+replyObject.userDp
                ,doubtsPostsAnswersRepliesHolder.iv_profile_image))
        doubtsPostsAnswersRepliesHolder.username.text=replyObject.userName+" "+"\u2022"+" "
        doubtsPostsAnswersRepliesHolder.replyText.text=replyObject.replyText
        //long press click copy text
        doubtsPostsAnswersRepliesHolder.replyText.setOnLongClickListener {
            val cm = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText(doubtsPostsAnswersRepliesHolder.replyText.text.toString(),doubtsPostsAnswersRepliesHolder.replyText.text)
            cm.setPrimaryClip(clip)
            Toast.makeText(mContext, "Text Copied to clipboard", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        doubtsPostsAnswersRepliesHolder.timeAgo.text=replyObject.replyTime
        if (mMyuserid!=replyObject.userId){
            doubtsPostsAnswersRepliesHolder.iv_delete_post_reply.visibility=View.GONE
        }
        doubtsPostsAnswersRepliesHolder.iv_delete_post_reply.setOnClickListener {
            val popup = PopupMenu(mContext, doubtsPostsAnswersRepliesHolder.iv_delete_post_reply)
            //inflating menu from xml resource
            popup.inflate(
                menu.menu_comments_answers_and_replies
            )
            val popupMenu = popup.menu
            popupMenu.findItem(id.delete_answer).isVisible = false
            popupMenu.findItem(id.delete_comment).isVisible = false
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    id.delete_reply ->if (ExtraFunctions.isNetworkStatusAvailable(mContext)) {
                        val url = ExtraFunctions.serverurl + "deleteAnswersRepliesOfDoubtsPosts.php"
                        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                            try {
                                val emp = JSONObject(response)
                                val result = emp.getString("result")
                                if (result == "successful") {
                                    Toast.makeText(mContext, "Reply Deleted successfully", Toast.LENGTH_SHORT).show()
                                    mContext.volleyAnswerDataRequest(mDoubtPostId)
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
        return mRepliesForArrayListObject.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DoubtsPostsAnswersRepliesHolder {
        val view = LayoutInflater.from(mContext).inflate(
            layout.indiview_post_comment_replies, p0, false)
        return DoubtsPostsAnswersRepliesHolder(view)
    }




    inner class DoubtsPostsAnswersRepliesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_profile_image: ImageView
        var iv_delete_post_reply:ImageView
        var username: TextView
        var replyText: TextView
        var timeAgo: TextView

        init {
            iv_profile_image = itemView.findViewById(
                id.iv_profile_image
            )
            iv_delete_post_reply=itemView.findViewById(
                id.iv_delete_post_reply
            )
            username = itemView.findViewById(
                id.username
            )
            replyText = itemView.findViewById(
                id.replyText
            )
            timeAgo = itemView.findViewById(
                id.timeAgo
            )
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}