package com.tecent.student_assessment.ui.adapters

import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.cloudist.acplibrary.ACProgressFlower
import coil.api.load
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.tecent.student_assessment.R
import com.tecent.student_assessment.R.color
import com.tecent.student_assessment.R.drawable
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.R.menu
import com.tecent.student_assessment.objects.AnswerObject
import com.tecent.student_assessment.ui.activity.AnswersDoubtsPostsDoubtsActivity
import com.tecent.student_assessment.ui.adapters.MyRecyclerDoubtsPostsAnswersAdapter.DoubtsPostsAnswersHolder
import com.tecent.student_assessment.utils.DataUtils.createImageRequestFromUrl
import com.tecent.student_assessment.utils.DataUtils.isNetworkStatusAvailable
import com.tecent.student_assessment.utils.DataUtils.serverurl
import com.tecent.student_assessment.utils.ExtraFunctions
import kotlinx.android.synthetic.main.custom_dialog_comments_reply.btn_reply
import kotlinx.android.synthetic.main.custom_dialog_comments_reply.editText_reply
import org.json.JSONObject
import java.util.HashMap

@Suppress("UNREACHABLE_CODE")
class MyRecyclerDoubtsPostsAnswersAdapter(
  dialog: ACProgressFlower,
  requestQueue: RequestQueue,
  doubtPostId: String,
  userid: String,
  context: AnswersDoubtsPostsDoubtsActivity,
  answerObjectArrayList: ArrayList<AnswerObject>
) : RecyclerView.Adapter<DoubtsPostsAnswersHolder>() {
  var mDialog: ACProgressFlower
  var mRequestQueue: RequestQueue
  var doubtPostId: String
  var mContext: AnswersDoubtsPostsDoubtsActivity
  var mMyuserid: String
  var mAnswerObjectArrayList: ArrayList<AnswerObject>

  init {
    this.mDialog = dialog
    this.mRequestQueue = requestQueue
    this.doubtPostId = doubtPostId
    this.mMyuserid = userid
    this.mContext = context
    this.mAnswerObjectArrayList = answerObjectArrayList
  }

  override fun getItemCount(): Int {
    return mAnswerObjectArrayList.size
  }

  override fun onCreateViewHolder(
    p0: ViewGroup,
    p1: Int
  ): DoubtsPostsAnswersHolder {
    val view = LayoutInflater.from(mContext)
        .inflate(
            layout.indiview_post_comment, p0, false
        )
    return DoubtsPostsAnswersHolder(view)
  }

  override fun onBindViewHolder(
    postCommentsHolder: DoubtsPostsAnswersHolder,
    position: Int
  ) {
    val answerObject = mAnswerObjectArrayList[position]
    mRequestQueue.add(
        createImageRequestFromUrl(
            serverurl + "userdp/" + answerObject.userDp
            , postCommentsHolder.iv_profile_image
        )
    )
    postCommentsHolder.username.text = answerObject.userName + " " + "\u2022" + " "
    postCommentsHolder.commentTime.text = answerObject.answerTime
    if (mMyuserid != answerObject.userId) {
      postCommentsHolder.iv_delete_post.visibility = View.GONE
    }
    postCommentsHolder.iv_delete_post.setOnClickListener {
      val popup = PopupMenu(mContext, postCommentsHolder.iv_delete_post)
      //inflating menu from xml resource
      popup.inflate(
          menu.menu_comments_answers_and_replies
      )
      val popupMenu = popup.menu
      popupMenu.findItem(id.delete_comment).isVisible = false
      popupMenu.findItem(id.delete_reply).isVisible = false
      popup.setOnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {
          id.delete_answer -> if (isNetworkStatusAvailable(
                  mContext
              )
          ) {
            val url = serverurl + "deleteAnswersOfPostsDoubts.php"
            val stringRequest =
              object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                try {
                  val emp = JSONObject(response)
                  val result = emp.getString("result")
                  if (result == "successful") {
                    Toast.makeText(mContext, "Answer Deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    mContext.volleyAnswerDataRequest(doubtPostId)
                  }
                  if (result == "error") {
                    Toast.makeText(mContext, "Error! Please try again later...", Toast.LENGTH_SHORT)
                        .show()
                  }
                } catch (exception: Exception) {
                  exception.printStackTrace()
                }
              }, Response.ErrorListener { error ->
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT)
                    .show()
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
            Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT)
                .show()
          }
        }
        false
      }
      popup.show()
    }
    postCommentsHolder.comment_text.text = answerObject.answerText
    //long press click copy text
    postCommentsHolder.comment_text.setOnLongClickListener {
      ExtraFunctions(mContext).copyTextToClipboard(
          postCommentsHolder.comment_text.text.toString(), postCommentsHolder.comment_text.text
      )
    }
    postCommentsHolder.comment_image.setOnClickListener {
      ExtraFunctions(mContext).animateIntent(postCommentsHolder.comment_image)
    }
    when (answerObject.repliesCount) {
      "0" -> {
        postCommentsHolder.view_all_replies.text = "No Replies"
      }
      "1" -> {
        postCommentsHolder.view_all_replies.text = "Hide " + answerObject.repliesCount + " Reply"
        postCommentsHolder.view_all_replies.setCompoundDrawablesWithIntrinsicBounds(
            drawable.ic_002_drop_up_arrow, 0, 0, 0
        )
        postCommentsHolder.view_all_replies.setOnClickListener {
          if (postCommentsHolder.comments_recyclerview.visibility == View.VISIBLE) {
            postCommentsHolder.comments_recyclerview.visibility = View.GONE
            postCommentsHolder.view_all_replies.text =
              "Show " + answerObject.repliesCount + " Reply"
            postCommentsHolder.view_all_replies.setCompoundDrawablesWithIntrinsicBounds(
                drawable.ic_001_drop_down_arrow, 0, 0, 0
            )
          } else {
            postCommentsHolder.comments_recyclerview.visibility = View.VISIBLE
            postCommentsHolder.view_all_replies.text =
              "Hide " + answerObject.repliesCount + " Reply"
            postCommentsHolder.view_all_replies.setCompoundDrawablesWithIntrinsicBounds(
                drawable.ic_002_drop_up_arrow, 0, 0, 0
            )

          }
        }
      }
      else -> {
        postCommentsHolder.view_all_replies.text = "Hide " + answerObject.repliesCount + " Replies"
        postCommentsHolder.view_all_replies.setCompoundDrawablesWithIntrinsicBounds(
            drawable.ic_002_drop_up_arrow, 0, 0, 0
        )
        postCommentsHolder.view_all_replies.setOnClickListener {
          if (postCommentsHolder.comments_recyclerview.visibility == View.VISIBLE) {
            postCommentsHolder.comments_recyclerview.visibility = View.GONE
            postCommentsHolder.view_all_replies.text =
              "Show " + answerObject.repliesCount + " Replies"
            postCommentsHolder.view_all_replies.setCompoundDrawablesWithIntrinsicBounds(
                drawable.ic_001_drop_down_arrow, 0, 0, 0
            )
          } else {
            postCommentsHolder.comments_recyclerview.visibility = View.VISIBLE
            postCommentsHolder.view_all_replies.text =
              "Hide " + answerObject.repliesCount + " Replies"
            postCommentsHolder.view_all_replies.setCompoundDrawablesWithIntrinsicBounds(
                drawable.ic_002_drop_up_arrow, 0, 0, 0
            )

          }
        }
      }
    }
    if (answerObject.answerImage != "") {
      //Image loading using coil
      postCommentsHolder.comment_image.load(
          serverurl + "postdoubts/postsDoubts_answers/" + answerObject.answerImage
      ) {
        placeholder(R.drawable.loading2)
      }

    } else {
      postCommentsHolder.comment_image.visibility = View.GONE
    }
    postCommentsHolder.comments_recyclerview.setHasFixedSize(true)
    postCommentsHolder.comments_recyclerview.setLayoutManager(
        LinearLayoutManager(mContext)
    )
    val adapter =
      MyRecyclerDoubtsPostsAnswersRepliesAdapter(
          mRequestQueue, mContext, doubtPostId, mMyuserid, answerObject.replyForArrayListObject
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
        override fun beforeTextChanged(
          charSequence: CharSequence,
          i: Int,
          i1: Int,
          i2: Int
        ) {
        }

        override fun onTextChanged(
          charSequence: CharSequence,
          i: Int,
          i1: Int,
          i2: Int
        ) {
          if (dialogReply.editText_reply.text.length >= 1) {
            dialogReply.btn_reply.setBackgroundColor(
                mContext.getResources()
                    .getColor(
                        color.colorPrimary
                    )
            )
          } else if (dialogReply.editText_reply.text.length < 1) {
            dialogReply.btn_reply.setBackgroundColor(
                mContext.getResources()
                    .getColor(
                        color.smalldarkgrey
                    )
            )
          }
        }

        override fun afterTextChanged(editable: Editable) {}
      })
      dialogReply.btn_reply.setOnClickListener {
        if (dialogReply.editText_reply.text.length < 1) {
          Toast.makeText(mContext, "Please write something..", Toast.LENGTH_SHORT)
              .show()
        } else {
          mDialog.show()
          try {
            val url = serverurl + "DoubtsPostsAnswersReplyData.php"
            val stringRequest =
              object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                //                progressBar.setVisibility(View.GONE)
                try {
                  val emp = JSONObject(response)
                  val result = emp.getString("result")
                  if (result == "successful") {
                    Toast.makeText(mContext, "Replied successfully", Toast.LENGTH_SHORT)
                        .show()
                    mContext.volleyAnswerDataRequest(doubtPostId)
                    dialogReply.dismiss()
                    mDialog.dismiss()
                  }
                } catch (exception: Exception) {
                  mDialog.dismiss()
                  Toast.makeText(mContext, exception.toString(), Toast.LENGTH_SHORT)
                      .show()
                }
              }, Response.ErrorListener {
              }) {
                override fun getParams(): Map<String, String> {
                  val MyData = HashMap<String, String>()
                  MyData["answerid"] = answerObject.answerId
                  MyData["userid"] = mMyuserid
                  MyData["replytext"] = dialogReply.editText_reply.text.toString()
                      .trim()
                      .replace("'", "\\'")
                  return MyData
                }
              }
            mRequestQueue.add(stringRequest)
          } catch (e: Exception) {
            mDialog.dismiss()
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT)
                .show()
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

  override fun getItemViewType(position: Int): Int {
    return position
  }
}