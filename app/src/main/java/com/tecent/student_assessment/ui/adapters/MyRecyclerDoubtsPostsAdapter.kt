package com.tecent.student_assessment.ui.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import cc.cloudist.acplibrary.ACProgressFlower
import coil.api.load
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.tecent.student_assessment.R
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.R.menu
import com.tecent.student_assessment.ui.activity.AnswersDoubtsPostsDoubtsActivity
import com.tecent.student_assessment.ui.activity.ProfileActivity
import com.tecent.student_assessment.ui.adapters.MyRecyclerDoubtsPostsAdapter.PostDoubtsHolder
import com.tecent.student_assessment.utils.DataUtils
import com.tecent.student_assessment.utils.DataUtils.serverurl
import com.tecent.student_assessment.utils.ExtraFunctions
import org.json.JSONObject
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class MyRecyclerDoubtsPostsAdapter(
  internal var mSharedPreferences: SharedPreferences,
  internal var mDialog: ACProgressFlower,
  internal var mRequestQueue: RequestQueue,
  private val mContext: Context,
  internal var mMyuserid: String,
  private val museridlist: ArrayList<String>,
  private val muserdplist: ArrayList<String>,
  private val musernamelist: ArrayList<String>,
  private val muserbranchlist: ArrayList<String>,
  private val mposttimelist: ArrayList<String>,
  private val mpostimagelist: ArrayList<String>,
  private val mpostdoubtidlist: ArrayList<String>,
  private val mposttextlist: ArrayList<String>,
  private val manswerslist: ArrayList<String>
) : RecyclerView.Adapter<PostDoubtsHolder>() {

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    i: Int
  ): PostDoubtsHolder {
    val view = LayoutInflater.from(mContext)
        .inflate(
            layout.indiview_doubts_post, viewGroup, false
        )
    return PostDoubtsHolder(view)
  }

  override fun onBindViewHolder(
    postDoubtsHolder: PostDoubtsHolder,
    position: Int
  ) {
    val mUserId = museridlist[position]
    val mUserdp = muserdplist[position]
    val mUserName = musernamelist[position]
    val mPostDateTime = mposttimelist[position]
    val mBranch = muserbranchlist[position]
    val mPostDoubtId = mpostdoubtidlist[position]
    val mPostText = mposttextlist[position]
    val mPostImage = mpostimagelist[position]
    val mPostAnswers = manswerslist[position]

    mRequestQueue.add<Bitmap>(
        DataUtils.createImageRequestFromUrl(
            DataUtils.serverurl + "userdp/" + mUserdp, postDoubtsHolder.iv_profile_image
        )
    )
    postDoubtsHolder.iv_username.text = mUserName
    postDoubtsHolder.ivdate_and_branch.text =
      mPostDateTime + "  " + "\u2022" + " " + mBranch.toUpperCase()
    postDoubtsHolder.iv_post_text.text = mPostText
    //long press click copy text
    postDoubtsHolder.iv_post_text.setOnLongClickListener {
      ExtraFunctions(mContext).copyTextToClipboard(
          postDoubtsHolder.iv_post_text.text.toString(), postDoubtsHolder.iv_post_text.text
      )
    }

    postDoubtsHolder.tv_no_of_answers.text = "$mPostAnswers Answers"
    if (mPostImage != "") {
      //image loading using coil
      postDoubtsHolder.iv_post_image.load(serverurl + "postdoubts/" + mPostImage) {
        placeholder(R.drawable.loading)
      }
    } else
      postDoubtsHolder.iv_post_image.visibility = View.GONE
    val viewLink = DataUtils.serverurl + "postdoubts/" + mPostImage
    postDoubtsHolder.iv_post_image.setOnClickListener {
      ExtraFunctions(mContext).animateIntent(postDoubtsHolder.iv_post_image)
    }

    postDoubtsHolder.iv_menu_btn.setOnClickListener {
      val popup = PopupMenu(mContext, postDoubtsHolder.iv_menu_btn)
      //inflating menu from xml resource
      popup.inflate(menu.doubts_post_menu)

      val popupMenu = popup.menu
      if (mUserId != mMyuserid)
        popupMenu.findItem(
            id.delete_post
        ).isVisible = false
      else {
        popupMenu.findItem(
            id.save_to_notes
        ).isVisible = false
        popupMenu.findItem(
            id.turn_on_post_notifi
        ).isVisible = false
        popupMenu.findItem(
            id.report_post
        ).isVisible = false

      }
      popup.setOnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {
          id.delete_post -> if (DataUtils.isNetworkStatusAvailable(mContext)) {
            val url = DataUtils.serverurl + "deleteDoubtPosts.php"
            val stringRequest =
              object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                try {
                  val emp = JSONObject(response)
                  val result = emp.getString("result")
                  if (result == "successful") {
                    Toast.makeText(mContext, "Post Deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    museridlist.removeAt(position)
                    val sharedPreferencesEditPostDoubts = mSharedPreferences.edit()
                    sharedPreferencesEditPostDoubts.putString(
                        "doubts", (Integer.parseInt(
                        mSharedPreferences.getString("doubts", "")!!
                    ) - 1).toString()
                    )
                    sharedPreferencesEditPostDoubts.apply()
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
                  MyData["postdoubtid"] = mPostDoubtId
                  return MyData
                }
              }
            mRequestQueue.add(stringRequest)
          } else {
            Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT)
                .show()
          }
          id.save_to_notes -> Toast.makeText(mContext, "save to notes", Toast.LENGTH_SHORT)
              .show()
          id.turn_on_post_notifi -> Toast.makeText(mContext, "turn on notif", Toast.LENGTH_SHORT)
              .show()
          id.share_post -> if (DataUtils.isNetworkStatusAvailable(mContext)) {
            mDialog.show()
            val extrastring =
              "\n\nThis doubt is posted by " + mUserName + " in \'Student Assessment\' app." +
                  "\nTo get such updates regularly download \'Student Assessment\' app now." +
                  "\nhttps://play.google.com/store/apps/details?id=com.tecent.student_assessment"
            if (mPostImage == "") {
              mDialog.dismiss()
              val intentShareFile = Intent(Intent.ACTION_SEND)
              intentShareFile.type = "text/plain"
              intentShareFile.putExtra(
                  Intent.EXTRA_SUBJECT,
                  "Sharing File..."
              )
              intentShareFile.putExtra(Intent.EXTRA_TEXT, mPostText + extrastring)
              mContext.startActivity(Intent.createChooser(intentShareFile, "Share"))
            } else {
              val f = File(
                  DataUtils.rootdir + "postdoubts/" + mPostImage
              )
              if (f.exists()) {
                mDialog.dismiss()
                val intentShareFile = Intent(Intent.ACTION_SEND)
                intentShareFile.type = "image/*"
                intentShareFile.putExtra(
                    Intent.EXTRA_STREAM, Uri.parse(
                    DataUtils.rootdir + "postdoubts/" + mPostImage
                )
                )
                intentShareFile.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "New post from \'Hints\' app."
                )
                intentShareFile.putExtra(Intent.EXTRA_TEXT, mPostText + extrastring)
                mContext.startActivity(Intent.createChooser(intentShareFile, "Share"))
              } else {
                PRDownloader.download(
                    DataUtils.serverurl + "postdoubts/" + mPostImage,
                    DataUtils.rootdir + "postdoubts/",
                    mPostImage
                )
                    .build()
                    .start(object : OnDownloadListener {
                      override fun onDownloadComplete() {
                        mDialog.dismiss()
                        val intentShareFile = Intent(Intent.ACTION_SEND)
                        intentShareFile.type = "image/*"
                        intentShareFile.putExtra(
                            Intent.EXTRA_STREAM, Uri.parse(
                            DataUtils.rootdir + "postdoubts/" + mPostImage
                        )
                        )
                        intentShareFile.putExtra(
                            Intent.EXTRA_SUBJECT,
                            "Sharing File..."
                        )
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, mPostText + extrastring)
                        mContext.startActivity(Intent.createChooser(intentShareFile, "Share"))
                      }

                      override fun onError(error: Error) {
                        mDialog.dismiss()
                      }

                    })
              }
            }
          } else {
            Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT)
                .show()
          }
          id.report_post -> {
            val dialogReport = Dialog(mContext)
            // Include dialog.xml file
            dialogReport.setContentView(
                layout.report_post_home
            )
            // Set dialog title
            dialogReport.setTitle("Custom Dialog")
            val radioGroup = dialogReport.findViewById<View>(
                id.radiogroup
            ) as RadioGroup
            val spamnpromotion = dialogReport.findViewById<View>(
                id.spamnpromotion
            ) as RadioButton
            val violencenharassment = dialogReport.findViewById<View>(
                id.violencenharassment
            ) as RadioButton
            val wrong = dialogReport.findViewById<View>(
                id.wrong
            ) as RadioButton
            val copyrightviolation = dialogReport.findViewById<View>(
                id.copyrightviolation
            ) as RadioButton
            val shouldnotbe = dialogReport.findViewById<View>(
                id.shouldnotbe
            ) as RadioButton
            val et_explain = dialogReport.findViewById<View>(
                id.et_explain
            ) as EditText
            val tv_btn_cancel = dialogReport.findViewById<View>(
                id.tv_btn_cancel
            ) as TextView
            val tv_btn_report = dialogReport.findViewById<View>(
                id.tv_btn_report
            ) as TextView
            val selectedIdRadio = radioGroup.checkedRadioButtonId
            // find the radiobutton by returned id
            tv_btn_cancel.setOnClickListener { dialogReport.dismiss() }
            tv_btn_report.setOnClickListener {
              var reportValue = ""
              if (spamnpromotion.isChecked) {
                reportValue = "Spam/Promotion"
              } else if (violencenharassment.isChecked) {
                reportValue = "Violence/Harassment"
              } else if (wrong.isChecked) {
                reportValue = "Wrong Information"
              } else if (copyrightviolation.isChecked) {
                reportValue = "Copyright Violation"
              } else if (shouldnotbe.isChecked) {
                reportValue = "Should not be in Student Assessment App"
              }
              val explainValue = et_explain.text.toString()
              if (radioGroup.checkedRadioButtonId != -1) {
                val url = DataUtils.serverurl + "reportDoubtsPost.php"
                val finalReportValue = reportValue
                val stringRequest =
                  object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                    try {
                      val emp = JSONObject(response)
                      val result = emp.getString("result")
                      if (result == "successful") {
                        dialogReport.dismiss()
                        Toast.makeText(mContext, "Post reported successsfully.", Toast.LENGTH_SHORT)
                            .show()
                      } else {
                        Toast.makeText(mContext, "An error occured.", Toast.LENGTH_SHORT)
                            .show()
                      }
                    } catch (e: Exception) {
                      Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT)
                          .show()
                    }
                  }, Response.ErrorListener {
                    mDialog.dismiss()
                    Toast.makeText(mContext, "Error! Please try again later...", Toast.LENGTH_SHORT)
                        .show()
                  }) {
                    override fun getParams(): Map<String, String> {
                      val MyData = HashMap<String, String>()
                      MyData["postdoubtid"] = mPostDoubtId
                      MyData["userid"] = mMyuserid
                      MyData["reason"] = finalReportValue
                      MyData["explanation"] = explainValue
                      return MyData
                    }
                  }
                mRequestQueue.add(stringRequest)
              } else {
                Toast.makeText(mContext, "Please select a reason.", Toast.LENGTH_SHORT)
                    .show()
              }
            }
            dialogReport.show()
          }
        }
        false
      }
      popup.show()
    }

    //answer now
    postDoubtsHolder.btn_answer_now.setOnClickListener {
      val doubtIntent = Intent(mContext, AnswersDoubtsPostsDoubtsActivity::class.java)
      doubtIntent.putExtra("postdoubtid", mPostDoubtId)
      mContext.startActivity(doubtIntent)
    }
    //profile
    postDoubtsHolder.iv_profile_image.setOnClickListener {
      if (mMyuserid == mUserId) {
        val intProfile = Intent(mContext, ProfileActivity::class.java)
        intProfile.putExtra("profile", "MyProfile")
        mContext.startActivity(intProfile)
      } else {
        val intProfile = Intent(mContext, ProfileActivity::class.java)
        intProfile.putExtra("userid", mUserId)
        intProfile.putExtra("username", mUserName)
        intProfile.putExtra("userdp", mUserdp)
        intProfile.putExtra("userbranch", mBranch)
        intProfile.putExtra("profile", "OtherProfile")
        mContext.startActivity(intProfile)
      }
    }
    postDoubtsHolder.iv_username.setOnClickListener {
      if (mMyuserid == mUserId) {
        val intProfile = Intent(mContext, ProfileActivity::class.java)
        intProfile.putExtra("profile", "MyProfile")
        mContext.startActivity(intProfile)
      } else {
        val intProfile = Intent(mContext, ProfileActivity::class.java)
        intProfile.putExtra("userid", mUserId)
        intProfile.putExtra("username", mUserName)
        intProfile.putExtra("userdp", mUserdp)
        intProfile.putExtra("userbranch", mBranch)
        intProfile.putExtra("profile", "OtherProfile")
        mContext.startActivity(intProfile)
      }
    }
    postDoubtsHolder.ivdate_and_branch.setOnClickListener {
      if (mMyuserid == mUserId) {
        val intProfile = Intent(mContext, ProfileActivity::class.java)
        intProfile.putExtra("profile", "MyProfile")
        mContext.startActivity(intProfile)
      } else {
        val intProfile = Intent(mContext, ProfileActivity::class.java)
        intProfile.putExtra("userid", mUserId)
        intProfile.putExtra("username", mUserName)
        intProfile.putExtra("userdp", mUserdp)
        intProfile.putExtra("userbranch", mBranch)
        intProfile.putExtra("profile", "OtherProfile")
        mContext.startActivity(intProfile)
      }
    }

  }

  override fun getItemCount(): Int {
    return museridlist.size
  }

  inner class PostDoubtsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var iv_profile_image: ImageView
    var iv_post_image: ImageView
    var iv_menu_btn: ImageView
    var iv_username: TextView
    var ivdate_and_branch: TextView
    var iv_post_text: TextView
    var tv_no_of_answers: TextView
    var btn_answer_now: Button

    init {
      iv_profile_image = itemView.findViewById(
          id.iv_profile_image
      )
      iv_post_image = itemView.findViewById(
          id.iv_post_image
      )
      iv_username = itemView.findViewById(
          id.tv_username
      )
      ivdate_and_branch = itemView.findViewById(
          id.ivdate_and_branch
      )
      iv_post_text = itemView.findViewById(
          id.iv_post_text
      )
      iv_menu_btn = itemView.findViewById(
          id.iv_menu
      )
      btn_answer_now = itemView.findViewById(
          id.btn_answer_now
      )
      tv_no_of_answers = itemView.findViewById(
          id.tv_no_of_answers
      )
    }
  }

  override fun getItemViewType(position: Int): Int {
    return position
  }
}
