package com.tecent.student_assessment.ui.adapters

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.tecent.student_assessment.ui.activity.DiscussionForumActivity
import com.tecent.student_assessment.objects.DiscussionForumObject
import com.tecent.student_assessment.utils.ExtraFunctions
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.ui.adapters.MyRecyclerDiscussionForumAdapter.DiscussionForumHolder
import org.json.JSONObject
import java.util.HashMap
import kotlin.collections.ArrayList

@Suppress("UNREACHABLE_CODE")
class MyRecyclerDiscussionForumAdapter(dialog: ACProgressFlower, requestQueue: RequestQueue, userid: String, context: DiscussionForumActivity, discussionForumObjectArrayList: ArrayList<DiscussionForumObject>) : RecyclerView.Adapter<DiscussionForumHolder>() {
    var mDialog: ACProgressFlower
    var mRequestQueue: RequestQueue
    var mContext: DiscussionForumActivity
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
        val view = LayoutInflater.from(mContext).inflate(
            layout.indiview_discussion_forum, p0, false)
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
                dialogMenu.setContentView(
                    layout.custom_dialog_discussion_forum
                )
                // Set dialog title
                dialogMenu.setTitle("Custom Dialog")
                val tv_delete_chat = dialogMenu.findViewById<View>(
                    id.tv_delete_chat
                ) as TextView
                val tv_copy_chat = dialogMenu.findViewById<View>(
                    id.tv_copy_chat
                ) as TextView
                dialogMenu.show()

                tv_delete_chat.setOnClickListener {
                    if (ExtraFunctions.isNetworkStatusAvailable(
                            mContext
                        )
                    ) {
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
            username = itemView.findViewById(
                id.username
            )
            discussionTime = itemView.findViewById(
                id.discussionTime
            )
            discussionText = itemView.findViewById(
                id.discussionText
            )
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}