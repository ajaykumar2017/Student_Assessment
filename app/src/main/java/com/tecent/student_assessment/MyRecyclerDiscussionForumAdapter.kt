package com.tecent.student_assessment

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.widget.RecyclerView
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.util.HashMap
import kotlin.collections.ArrayList

@Suppress("UNREACHABLE_CODE")
class MyRecyclerDiscussionForumAdapter(dialog: ACProgressFlower, requestQueue: RequestQueue, userid: String, context: DiscussionForum, discussionForumObjectArrayList: ArrayList<DiscussionForumObject>) : RecyclerView.Adapter<MyRecyclerDiscussionForumAdapter.DiscussionForumHolder>() {
    var mDialog: ACProgressFlower
    var mRequestQueue: RequestQueue
    var mContext: DiscussionForum
    var mMyuserid: String
    var mDiscussionForumObjectArrayList: ArrayList<DiscussionForumObject>

    init {
        this.mDialog = dialog
        this.mRequestQueue = requestQueue
        this.mMyuserid = userid
        this.mContext = context
        this.mDiscussionForumObjectArrayList = discussionForumObjectArrayList
    }


    override fun getItemCount(): Int {
        return mDiscussionForumObjectArrayList.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DiscussionForumHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.indiview_discussion_forum, p0, false)
        return DiscussionForumHolder(view)
    }

    override fun onBindViewHolder(discussionForumHolder: DiscussionForumHolder, position: Int) {
        val discussionForumObject = mDiscussionForumObjectArrayList[position]
        discussionForumHolder.username.text = discussionForumObject.userName + " " + "\u2022" + " "
        discussionForumHolder.discussionTime.text = discussionForumObject.discussionTime
        discussionForumHolder.discussionText.text = discussionForumObject.discussionText
        //long press click copy text
        discussionForumHolder.discussionText.setOnLongClickListener {
            if (discussionForumObject.userId.equals(mMyuserid)){
                val dialogMenu = Dialog(mContext)
                // Include dialog.xml file
                dialogMenu.setContentView(R.layout.custom_dialog_discussion_forum)
                // Set dialog title
                dialogMenu.setTitle("Custom Dialog")
                val tv_delete_chat = dialogMenu.findViewById<View>(R.id.tv_delete_chat) as TextView
                val tv_copy_chat = dialogMenu.findViewById<View>(R.id.tv_copy_chat) as TextView
                dialogMenu.show()

                tv_delete_chat.setOnClickListener {
                    if (ExtraFunctions.isNetworkStatusAvialable(mContext)) {
                        val url = ExtraFunctions.serverurl + "deleteDiscussionForumChats.php"
                        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                            try {
                                val emp = JSONObject(response)
                                val result = emp.getString("result")
                                if (result == "successful") {
                                    Toast.makeText(mContext, "Chat Deleted successfully", Toast.LENGTH_SHORT).show()
                                    mContext.volleyDiscussionForumDataRequest()
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
                                MyData["discussionId"] = discussionForumObject.discussionId
                                return MyData
                            }
                        }
                        mRequestQueue.add(stringRequest)
                    } else {
                        Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show()
                    }
                    dialogMenu.dismiss()
                }
                tv_copy_chat.setOnClickListener {
                    val cm: ClipboardManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText(discussionForumHolder.discussionText.text.toString(),discussionForumHolder.discussionText.text)
                    cm.primaryClip=clip
                    Toast.makeText(mContext, "Text Copied to clipboard", Toast.LENGTH_SHORT).show()
                    dialogMenu.dismiss()
                }

            }else{
                val cm: ClipboardManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(discussionForumHolder.discussionText.text.toString(),discussionForumHolder.discussionText.text)
                cm.primaryClip=clip
                Toast.makeText(mContext, "Text Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            return@setOnLongClickListener true
        }

    }


    inner class DiscussionForumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView
        var discussionTime: TextView
        var discussionText: TextView

        init {
            username = itemView.findViewById(R.id.username)
            discussionTime = itemView.findViewById(R.id.discussionTime)
            discussionText = itemView.findViewById(R.id.discussionText)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}