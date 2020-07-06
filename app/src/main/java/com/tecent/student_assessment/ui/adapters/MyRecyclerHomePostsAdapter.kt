package com.tecent.student_assessment.ui.adapters

import android.app.Activity
import android.app.Dialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader

import org.json.JSONObject

import java.io.File
import java.util.ArrayList
import java.util.HashMap

import cc.cloudist.acplibrary.ACProgressFlower
import com.tecent.student_assessment.ui.activity.CommentsPostHomeActivity
import com.tecent.student_assessment.ui.activity.ImagePdfWebViewActivity
import com.tecent.student_assessment.ui.activity.ImageViewerActivity
import com.tecent.student_assessment.ui.activity.ProfileActivity
import com.tecent.student_assessment.R.color
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.R.string
import com.tecent.student_assessment.ui.activity.SinglePostsActivity
import com.tecent.student_assessment.ui.adapters.MyRecyclerHomePostsAdapter.HomePostsHolder
import com.tecent.student_assessment.utils.ExtraFunctions
import java.io.ByteArrayOutputStream
import java.util.Locale

class MyRecyclerHomePostsAdapter(internal var mSharedPreferences: SharedPreferences, internal var mSharedPreferencesLike: SharedPreferences,
                                 internal var mDialog: ACProgressFlower, internal var mRequestQueue: RequestQueue, private val mContext: Context,
                                 internal var mMyuserid: String, private val museridlist: ArrayList<String>,
                                 private val muserdplist: ArrayList<String>, private val musernamelist: ArrayList<String>,
                                 private val muserbranchlist: ArrayList<String>, private val mposttimelist: ArrayList<String>,
                                 private val mpostfilelist: ArrayList<String>, private val mpostidlist: ArrayList<String>,
                                 private val mposttextlist: ArrayList<String>, private val msubjectlist: ArrayList<String>,
                                 private val mlikelist: ArrayList<String>, private val mcommentlist: ArrayList<String>) : RecyclerView.Adapter<HomePostsHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HomePostsHolder {
        val view = LayoutInflater.from(mContext).inflate(
            layout.indiview_posts, viewGroup, false)
        return HomePostsHolder(view)
    }

    override fun onBindViewHolder(homePostsHolder: HomePostsHolder, position: Int) {
        val mPostId = mpostidlist[position]
        val mUserId = museridlist[position]
        val mUserdp = muserdplist[position]
        val mUserName = musernamelist[position]
        val mPostDateTime = mposttimelist[position]
        val mBranch = muserbranchlist[position]
        val mPostText = mposttextlist[position]
        val mPostFile = mpostfilelist[position]
        val mSubject = msubjectlist[position]
        val mLikes = mlikelist[position]
        val mComments = mcommentlist[position]
        var viewLink = ""
        var type = ""

        mRequestQueue.add(
            ExtraFunctions.createImageRequestFromUrl(
                ExtraFunctions.serverurl + "userdp/" + mUserdp, homePostsHolder.iv_profile_image))
        mRequestQueue.add(
            ExtraFunctions.createImageRequestFromUrl(
                ExtraFunctions.serverurl + "userdp/" + mSharedPreferences.getString("userdp", ""), homePostsHolder.my_profile_image))
        homePostsHolder.iv_username.text = mUserName
        homePostsHolder.ivdate_and_branch_subject.text = mPostDateTime + "  " + "\u2022" + " " + mBranch.toUpperCase(Locale.getDefault()) + "  " + "\u2022" + " " + mSubject
        homePostsHolder.iv_post_text.text = mPostText
        //long press click copy text
        homePostsHolder.iv_post_text.setOnLongClickListener {
            val cm: ClipboardManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(homePostsHolder.iv_post_text.text.toString(),homePostsHolder.iv_post_text.text)
            cm.setPrimaryClip(clip)
            Toast.makeText(mContext, "Text Copied to clipboard", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        if (mSharedPreferencesLike.getString(mPostId, "") == "liked") {
            //            homePostsHolder.ivlike.setBackgroundResource(R.drawable.ic_thumb_up_blue);
            DrawableCompat.setTint(homePostsHolder.ivlike.drawable, ContextCompat.getColor(mContext,
                color.colorPrimary
            ))
        } else {
            DrawableCompat.setTint(homePostsHolder.ivlike.drawable, ContextCompat.getColor(mContext,
                color.vectordrawablelike
            ))
        }
        if (mPostFile != "") {
            if (mPostFile.substring(mPostFile.lastIndexOf('.') + 1) == "pdf" || mPostFile.substring(mPostFile.lastIndexOf('.') + 1) == "PDF") {
                type = "pdf"
                //                viewLink = "https://docs.google.com/viewer?url=" + ExtraFunctions.serverurl + "posts/" + mPostFile;
                viewLink = ExtraFunctions.serverurl + "pdfViewer/web/viewer.html?file=" + "/project/posts/" + mPostFile
                mRequestQueue.add<Bitmap>(
                    ExtraFunctions.createImageRequestFromUrl(
                        ExtraFunctions.serverurl +
                        "posts/pdfthumbnail/" + mPostFile.replace(mPostFile.substring(mPostFile.lastIndexOf('.') + 1), "") + "jpg", homePostsHolder.iv_post_image))
            } else {
                type = "image"
                viewLink = ExtraFunctions.serverurl + "posts/" + mPostFile
                mRequestQueue.add<Bitmap>(
                    ExtraFunctions.createImageRequestFromUrl(
                        ExtraFunctions.serverurl + "posts/" + mPostFile, homePostsHolder.iv_post_image))
            }

        } else {
            homePostsHolder.iv_post_image.visibility = View.GONE
        }
        //Onclick show profile start
        homePostsHolder.iv_username.setOnClickListener {
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
        homePostsHolder.iv_profile_image.setOnClickListener {
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
        homePostsHolder.ivdate_and_branch_subject.setOnClickListener {
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
        //Onclick show profile end
        //onClick post image start
        val finalViewLink = viewLink
        val finalType = type
        homePostsHolder.iv_post_image.setOnClickListener {
            if (finalType == "pdf") {
                val intent = Intent(mContext, ImagePdfWebViewActivity::class.java)
                intent.putExtra("viewLink", finalViewLink)
                mContext.startActivity(intent)
            } else {
//                val intent = Intent(mContext, ImagePdfWebView::class.java)
//                intent.putExtra("viewLink", finalViewLink)
//                mContext.startActivity(intent)
                  animateIntent(homePostsHolder.iv_post_image)
            }
        }
        //onclick post image end

        //menu part start
        homePostsHolder.iv_menu_btn.setOnClickListener {
            val dialogMenu = Dialog(mContext)
            // Include dialog.xml file
            dialogMenu.setContentView(
                layout.custom_dialog_menu_posts_home
            )
            // Set dialog title
            dialogMenu.setTitle("Custom Dialog")
            val tv_delete_post = dialogMenu.findViewById<View>(
                id.tv_delete_post
            ) as TextView
            val tv_share_post = dialogMenu.findViewById<View>(
                id.tv_share_post
            ) as TextView
            val save_to_notes = dialogMenu.findViewById<View>(
                id.save_to_notes
            ) as TextView
            val tv_turn_on_post_notif = dialogMenu.findViewById<View>(
                id.tv_turn_on_post_notif
            ) as TextView
            val tv_share_link = dialogMenu.findViewById<View>(
                id.tv_share_link
            ) as TextView
            val tv_report_post = dialogMenu.findViewById<View>(
                id.tv_report_post
            ) as TextView
            dialogMenu.show()
            if (mUserId != mMyuserid)
                tv_delete_post.visibility = View.GONE
            else {
                save_to_notes.visibility = View.GONE
                tv_turn_on_post_notif.visibility = View.GONE
                tv_report_post.visibility = View.GONE

            }
            tv_delete_post.setOnClickListener {
                if (ExtraFunctions.isNetworkStatusAvailable(mContext)) {
                    val url = ExtraFunctions.serverurl + "deleteHomePosts.php"
                    val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                        try {
                            val emp = JSONObject(response)
                            val result = emp.getString("result")
                            if (result == "successful") {
                                Toast.makeText(mContext, "Post Deleted successfully", Toast.LENGTH_SHORT).show()
                                val sharedPreferencesEditPosts = mSharedPreferences.edit()
                                sharedPreferencesEditPosts.putString("doubts", (Integer.parseInt(mSharedPreferences.getString("posts", "")!!) - 1).toString())
                                sharedPreferencesEditPosts.apply()
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
                            MyData["postid"] = mPostId
                            return MyData
                        }
                    }
                    mRequestQueue.add(stringRequest)
                } else {
                    Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show()
                }
                dialogMenu.dismiss()
            }

            tv_share_post.setOnClickListener {
                if (ExtraFunctions.isNetworkStatusAvailable(mContext)) {
                    mDialog.show()
                    val extrastring = "\n\nThis Post is posted by " + mUserName + " in \'Student Assessment\' app." +
                            "\nTo get such updates regularly download \'Student Assessment\' app now." +
                            "\nhttps://play.google.com/store/apps/details?id=com.tecent.student_assessment"
                    mDialog.dismiss()
                    val intentShareFile = Intent(Intent.ACTION_SEND)
                    intentShareFile.type = "text/plain"
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Sharing File...")
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, mPostText + extrastring)
                    mContext.startActivity(Intent.createChooser(intentShareFile, "Share"))
                } else {
                    Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show()
                }
                Handler().postDelayed({ dialogMenu.dismiss() }, 500)
            }

            tv_share_link.setOnClickListener {
                if (ExtraFunctions.isNetworkStatusAvailable(mContext)) {
                    mDialog.show()
                    val shareString = "https://sas.a3creators.co.in/StudentAssessment/post?id=$mPostId"
                    mDialog.dismiss()
                    val intentShareFile = Intent(Intent.ACTION_SEND)
                    intentShareFile.type = "text/plain"
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Sharing File...")
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, shareString)
                    mContext.startActivity(Intent.createChooser(intentShareFile, "Share"))
                } else {
                    Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show()
                }
                Handler().postDelayed({ dialogMenu.dismiss() }, 200)
            }

            tv_report_post.setOnClickListener {
                val dialogReport = Dialog(mContext)
                // Include dialog.xml file
                dialogReport.setContentView(
                    layout.report_post_home
                )
                // Set dialog title
                dialogReport.setTitle("Custom Dialog")
                val radioGroup = dialogReport.findViewById<RadioGroup>(
                    id.radiogroup
                )
                val spamnpromotion = dialogReport.findViewById<RadioButton>(
                    id.spamnpromotion
                )
                val violencenharassment = dialogReport.findViewById<RadioButton>(
                    id.violencenharassment
                )
                val wrong = dialogReport.findViewById<RadioButton>(
                    id.wrong
                )
                val copyrightviolation = dialogReport.findViewById<RadioButton>(
                    id.copyrightviolation
                )
                val shouldnotbe = dialogReport.findViewById<RadioButton>(
                    id.shouldnotbe
                )
                val et_explain = dialogReport.findViewById<EditText>(
                    id.et_explain
                )
                val tv_btn_cancel = dialogReport.findViewById<TextView>(
                    id.tv_btn_cancel
                )
                val tv_btn_report = dialogReport.findViewById<TextView>(
                    id.tv_btn_report
                )
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
                        val url = ExtraFunctions.serverurl + "reportPost.php"
                        val finalReportValue = reportValue
                        val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                            try {
                                val emp = JSONObject(response)
                                val result = emp.getString("result")
                                if (result == "successful") {
                                    dialogReport.dismiss()
                                    Toast.makeText(mContext, "Post reported successsfully.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(mContext, "An error occured.", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }, Response.ErrorListener {
                            mDialog.dismiss()
                            Toast.makeText(mContext, "Error! Please try again later...", Toast.LENGTH_SHORT).show()
                        }) {
                            override fun getParams(): Map<String, String> {
                                val MyData = HashMap<String, String>()
                                MyData["postid"] = mPostId
                                MyData["userid"] = mMyuserid
                                MyData["reason"] = finalReportValue
                                MyData["explanation"] = explainValue
                                return MyData
                            }
                        }
                        mRequestQueue.add(stringRequest)
                    } else {
                        Toast.makeText(mContext, "Please select a reason.", Toast.LENGTH_SHORT).show()
                    }
                }


                dialogMenu.dismiss()
                dialogReport.show()
            }
        }
        //menu part end

        //post text onClick
        homePostsHolder.iv_post_text.setOnClickListener {
            val intSp = Intent(mContext, SinglePostsActivity::class.java)
            intSp.putExtra("postid", mPostId)
            mContext.startActivity(intSp)
        }
        //get the length of words
        val getLen:Int=mPostText.split(" ").size
        homePostsHolder.iv_post_text_more.visibility=View.GONE
        if (getLen>30){
            homePostsHolder.iv_post_text_more.visibility=View.VISIBLE
        }
        //on click more
        homePostsHolder.iv_post_text_more.setOnClickListener {
            val intSp = Intent(mContext, SinglePostsActivity::class.java)
            intSp.putExtra("postid", mPostId)
            mContext.startActivity(intSp)
        }
        //post likes
        val likes = Integer.parseInt(mLikes)
        if (mLikes == "0" || mLikes == "1") {
            if (mSharedPreferencesLike.getString(mPostId, "") == "liked") {
                homePostsHolder.likes_count.text = "Liked by you and " + (likes - 1) + " others"
            } else {
                homePostsHolder.likes_count.text = "$mLikes Like"
            }
        } else {
            if (mSharedPreferencesLike.getString(mPostId, "") == "liked") {
                homePostsHolder.likes_count.text = "Liked by you and " + (likes - 1) + " others"
            } else {
                homePostsHolder.likes_count.text = "$mLikes Likes"
            }
        }
        //comments count
        if (mComments == "0") {
            homePostsHolder.comments_count.text = "No Comments"
        } else if (mComments == "1") {
            homePostsHolder.comments_count.text = "View 1 comment"
        } else {
            homePostsHolder.comments_count.text = "View all $mComments comments"
        }
        //comments click
        homePostsHolder.ll_view_comments.setOnClickListener {
            val postIntent = Intent(mContext, CommentsPostHomeActivity::class.java)
            postIntent.putExtra("postid", mPostId)
            mContext.startActivity(postIntent)
        }
        //like part start
        homePostsHolder.ivlike.setOnClickListener {
            if (ExtraFunctions.isNetworkStatusAvailable(mContext)) {
                if (mSharedPreferencesLike.getString(mPostId, "") == "liked") {
                    val url = ExtraFunctions.serverurl + "unLikePosts.php"
                    val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                        try {
                            val emp = JSONObject(response)
                            val result = emp.getString("result")
                            if (result == "successful") {
                                val speLike = mSharedPreferencesLike.edit()
                                speLike.putString(mPostId, "")
                                speLike.apply()
                                DrawableCompat.setTint(homePostsHolder.ivlike.drawable, ContextCompat.getColor(mContext,
                                    color.vectordrawablelike
                                ))
                                homePostsHolder.ivlike.setPadding(1, 0, 1, 0)
                                homePostsHolder.likes_count.text = "$mLikes Likes"
                                Toast.makeText(mContext, "Post Unliked", Toast.LENGTH_SHORT).show()
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
                            MyData["postid"] = mPostId
                            MyData["userid"] = mMyuserid
                            return MyData
                        }
                    }
                    mRequestQueue.add(stringRequest)
                } else {
                    val url = ExtraFunctions.serverurl + "likePosts.php"
                    val stringRequest = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                        try {
                            val emp = JSONObject(response)
                            val result = emp.getString("result")
                            if (result == "alreadyLiked") {
                                Toast.makeText(mContext, "Post already Liked by you", Toast.LENGTH_SHORT).show()
                            }
                            if (result == "successful") {
                                val speLike = mSharedPreferencesLike.edit()
                                speLike.putString(mPostId, "liked")
                                speLike.apply()
                                DrawableCompat.setTint(homePostsHolder.ivlike.drawable, ContextCompat.getColor(mContext,
                                    color.colorPrimary
                                ))
                                homePostsHolder.ivlike.setPadding(1, 0, 1, 0)
                                homePostsHolder.likes_count.text = "Liked by you and $mLikes others"
                                Toast.makeText(mContext, "Post Liked successfully", Toast.LENGTH_SHORT).show()
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
                            MyData["postid"] = mPostId
                            MyData["userid"] = mMyuserid
                            return MyData
                        }
                    }
                    mRequestQueue.add(stringRequest)
                }

            } else {
                Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show()
            }
        }
        //like part end

        //comment part start
        homePostsHolder.ivreply.setOnClickListener {
            val postIntent = Intent(mContext, CommentsPostHomeActivity::class.java)
            postIntent.putExtra("postid", mPostId)
            mContext.startActivity(postIntent)
        }
        //comment part end

        homePostsHolder.ivshare.setOnClickListener {
            if (ExtraFunctions.isNetworkStatusAvailable(mContext)) {
                mDialog.show()
                val extrastring = "\n\nThis Post is posted by " + mUserName + " in \'Student Assessment\' app." +
                        "\nTo get such updates regularly download \'Student Assessment\' app now." +
                        "\nhttps://play.google.com/store/apps/details?id=com.tecent.student_assessment"
                if (mPostFile == "") {
                    mDialog.dismiss()
                    val intentShareFile = Intent(Intent.ACTION_SEND)
                    intentShareFile.type = "text/plain"
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Sharing File...")
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, mPostText + extrastring)
                    mContext.startActivity(Intent.createChooser(intentShareFile, "Share"))
                } else {
                    val f: File
                    if (mPostFile.substring(mPostFile.lastIndexOf('.') + 1) == "pdf" || mPostFile.substring(mPostFile.lastIndexOf('.') + 1) == "PDF") {
                        f = File(
                            ExtraFunctions.serverurl +
                                "posts/pdfthumbnail/" + mPostFile.replace(mPostFile.substring(mPostFile.lastIndexOf('.') + 1), "") + "jpg")
                    } else {
                        f = File(
                            ExtraFunctions.serverurl + "posts/" + mPostFile)
                    }
                    if (f.exists()) {
                        mDialog.dismiss()
                        val intentShareFile = Intent(Intent.ACTION_SEND)
                        intentShareFile.type = "image/*"
                        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(
                            ExtraFunctions.rootdir + "posts/" + mPostFile))
                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                "New post from \'Hints\' app.")
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, mPostText + extrastring)
                        mContext.startActivity(Intent.createChooser(intentShareFile, "Share"))
                    } else {
                        val dUrl: String
                        val fDnld: String
                        if (mPostFile.substring(mPostFile.lastIndexOf('.') + 1) == "pdf" || mPostFile.substring(mPostFile.lastIndexOf('.') + 1) == "PDF") {
                            dUrl = ExtraFunctions.serverurl +
                                    "posts/pdfthumbnail/" + mPostFile.replace(mPostFile.substring(mPostFile.lastIndexOf('.') + 1), "") + "jpg"
                            fDnld = mPostFile.replace(mPostFile.substring(mPostFile.lastIndexOf('.') + 1), "") + "jpg"
                        } else {
                            dUrl = ExtraFunctions.serverurl + "posts/" + mPostFile
                            fDnld = mPostFile
                        }
                        PRDownloader.download(dUrl,
                                ExtraFunctions.rootdir + "posts/",
                                fDnld)
                                .build()
                                .start(object : OnDownloadListener {
                                    override fun onDownloadComplete() {
                                        mDialog.dismiss()
                                        val intentShareFile = Intent(Intent.ACTION_SEND)
                                        intentShareFile.type = "image/*"
                                        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(
                                            ExtraFunctions.rootdir + "posts/" + fDnld))
                                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                                "Sharing File...")
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
                Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getItemCount(): Int {
        return museridlist.size
    }

    inner class HomePostsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_profile_image: ImageView
        var iv_post_image: ImageView
        var iv_menu_btn: ImageView
        var ivlike: ImageView
        var ivreply: ImageView
        var ivshare: ImageView
        var my_profile_image: ImageView
        var iv_username: TextView
        var ivdate_and_branch_subject: TextView
        var iv_post_text: TextView
        var iv_post_text_more:TextView
        var likes_count: TextView
        var comments_count: TextView
        var ll_view_comments: LinearLayout

        init {
            iv_profile_image = itemView.findViewById(
                id.iv_profile_image
            )
            iv_post_image = itemView.findViewById(
                id.iv_post_image
            )
            iv_username = itemView.findViewById(
                id.ivusername
            )
            ivdate_and_branch_subject = itemView.findViewById(
                id.iv_datetime_branch_subject
            )
            iv_post_text = itemView.findViewById(
                id.iv_post_text
            )
            iv_post_text_more=itemView.findViewById(
                id.iv_post_text_more
            )
            iv_menu_btn = itemView.findViewById(
                id.iv_menu
            )
            ivlike = itemView.findViewById(
                id.ivlike
            )
            ivreply = itemView.findViewById(
                id.ivreply
            )
            ivshare = itemView.findViewById(
                id.ivshare
            )
            my_profile_image = itemView.findViewById(
                id.my_profile_image
            )
            likes_count = itemView.findViewById(
                id.count_likes
            )
            comments_count = itemView.findViewById(
                id.count_comments
            )
            ll_view_comments = itemView.findViewById(
                id.view_comments
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
        val options=ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as Activity,view as View,transitionName)
        ActivityCompat.startActivity(mContext, intent, options.toBundle())
    }
    fun getFileDataFromDrawable(context: Context, drawable: Drawable): ByteArray {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}

