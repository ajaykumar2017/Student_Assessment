package com.tecent.student_assessment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressFlower;

public class MyRecyclerHomePostsAdapter extends RecyclerView.Adapter<MyRecyclerHomePostsAdapter.HomePostsHolder> {
    private Context mContext;
    private ArrayList<String> museridlist;
    private ArrayList<String> muserdplist;
    private ArrayList<String> musernamelist;
    private ArrayList<String> mposttimelist;
    private ArrayList<String> mpostidlist;
    private ArrayList<String> muserbranchlist;
    private ArrayList<String> mposttextlist;
    private ArrayList<String> mpostfilelist;
    private ArrayList<String> msubjectlist;
    private ArrayList<String> mlikelist;
    private ArrayList<String> mcommentlist;
    String mMyuserid;
    RequestQueue mRequestQueue;
    ACProgressFlower mDialog;
    SharedPreferences mSharedPreferencesLike, mSharedPreferences;

    public MyRecyclerHomePostsAdapter(SharedPreferences sharedPreferences, SharedPreferences sharedPreferencesLike, ACProgressFlower dialog, RequestQueue requestQueue, Context context, String myuserid, ArrayList<String> useridlist,
                                      ArrayList<String> userdplist, ArrayList<String> usernamelist,
                                      ArrayList<String> userbranchlist, ArrayList<String> posttimelist,
                                      ArrayList<String> postfilelist, ArrayList<String> postidlist,
                                      ArrayList<String> posttextlist, ArrayList<String> subjectlist,
                                      ArrayList<String> postlikelist, ArrayList<String> commentlist) {
        mSharedPreferences=sharedPreferences;
        mSharedPreferencesLike = sharedPreferencesLike;
        mDialog = dialog;
        mRequestQueue = requestQueue;
        mContext = context;
        mMyuserid = myuserid;
        museridlist = useridlist;
        muserdplist = userdplist;
        musernamelist = usernamelist;
        mposttimelist = posttimelist;
        mpostidlist = postidlist;
        muserbranchlist = userbranchlist;
        mposttextlist = posttextlist;
        mpostfilelist = postfilelist;
        msubjectlist = subjectlist;
        mlikelist=postlikelist;
        mcommentlist=commentlist;
    }

    @NonNull
    @Override
    public HomePostsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.indiview_posts, viewGroup, false);
        return new HomePostsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomePostsHolder homePostsHolder, int position) {
        final String mUserId = museridlist.get(position);
        final String mUserdp = muserdplist.get(position);
        final String mUserName = musernamelist.get(position);
        String mPostDateTime = mposttimelist.get(position);
        final String mBranch = muserbranchlist.get(position);
        final String mPostId = mpostidlist.get(position);
        final String mPostText = mposttextlist.get(position);
        final String mPostFile = mpostfilelist.get(position);
        final String mSubject = msubjectlist.get(position);
        final String mLikes=mlikelist.get(position);
        final String mComments=mcommentlist.get(position);
        String viewLink = "";
        String type = "";

        mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "userdp/" + mUserdp, homePostsHolder.iv_profile_image));
        mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl+"userdp/"+mSharedPreferences.getString("userdp",""),homePostsHolder.my_profile_image));
        homePostsHolder.iv_username.setText(mUserName);
        homePostsHolder.ivdate_and_branch_subject.setText(mPostDateTime + "  " + "\u2022" + " " + mBranch.toUpperCase() + "  " + "\u2022" + " " + mSubject);
        homePostsHolder.iv_post_text.setText(mPostText);
        if (mSharedPreferencesLike.getString(mPostId, "").equals("liked")) {
//            homePostsHolder.ivlike.setBackgroundResource(R.drawable.ic_thumb_up_blue);
            DrawableCompat.setTint(homePostsHolder.ivlike.getDrawable(), ContextCompat.getColor(mContext, R.color.colorPrimary));
        } else {
            DrawableCompat.setTint(homePostsHolder.ivlike.getDrawable(), ContextCompat.getColor(mContext, R.color.vectordrawablelike));
        }
        if (!mPostFile.equals("")) {
            if (mPostFile.substring(mPostFile.lastIndexOf('.') + 1).equals("pdf") ||
                    mPostFile.substring(mPostFile.lastIndexOf('.') + 1).equals("PDF")) {
                type = "pdf";
//                viewLink = "https://docs.google.com/viewer?url=" + ExtraFunctions.serverurl + "posts/" + mPostFile;
                viewLink = ExtraFunctions.serverurl + "pdfViewer/web/viewer.html?file=" + "/project/posts/" + mPostFile;
                mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl +
                        "posts/pdfthumbnail/" + mPostFile.replace(mPostFile.substring(mPostFile.lastIndexOf('.') + 1), "") + "jpg", homePostsHolder.iv_post_image));
            } else {
                type = "image";
                viewLink = ExtraFunctions.serverurl + "posts/" + mPostFile;
                mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "posts/" + mPostFile, homePostsHolder.iv_post_image));
            }

        } else {
            homePostsHolder.iv_post_image.setVisibility(View.GONE);
        }
        //Onclick show profile start
        homePostsHolder.iv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMyuserid.equals(mUserId)){
                    Intent intProfile=new Intent(mContext,Profile.class);
                    intProfile.putExtra("profile","MyProfile");
                    mContext.startActivity(intProfile);
                }else{
                    Intent intProfile=new Intent(mContext,Profile.class);
                    intProfile.putExtra("userid",mUserId);
                    intProfile.putExtra("username",mUserName);
                    intProfile.putExtra("userdp",mUserdp);
                    intProfile.putExtra("userbranch",mBranch);
                    intProfile.putExtra("profile","OtherProfile");
                    mContext.startActivity(intProfile);
                }
            }
        });
        homePostsHolder.iv_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMyuserid.equals(mUserId)){
                    Intent intProfile=new Intent(mContext,Profile.class);
                    intProfile.putExtra("profile","MyProfile");
                    mContext.startActivity(intProfile);
                }else{
                    Intent intProfile=new Intent(mContext,Profile.class);
                    intProfile.putExtra("userid",mUserId);
                    intProfile.putExtra("username",mUserName);
                    intProfile.putExtra("userdp",mUserdp);
                    intProfile.putExtra("userbranch",mBranch);
                    intProfile.putExtra("profile","OtherProfile");
                    mContext.startActivity(intProfile);
                }
            }
        });
        homePostsHolder.ivdate_and_branch_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMyuserid.equals(mUserId)){
                    Intent intProfile=new Intent(mContext,Profile.class);
                    intProfile.putExtra("profile","MyProfile");
                    mContext.startActivity(intProfile);
                }else{
                    Intent intProfile=new Intent(mContext,Profile.class);
                    intProfile.putExtra("userid",mUserId);
                    intProfile.putExtra("username",mUserName);
                    intProfile.putExtra("userdp",mUserdp);
                    intProfile.putExtra("userbranch",mBranch);
                    intProfile.putExtra("profile","OtherProfile");
                    mContext.startActivity(intProfile);
                }
            }
        });
        //Onclick show profile end
        //onClick post image start
        final String finalViewLink = viewLink;
        final String finalType = type;
        homePostsHolder.iv_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finalType.equals("pdf")) {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalViewLink));
//                    mContext.startActivity(browserIntent);
                    Intent intent = new Intent(mContext, ImagePdfWebView.class);
                    intent.putExtra("viewLink", finalViewLink);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, ImagePdfWebView.class);
                    intent.putExtra("viewLink", finalViewLink);
                    mContext.startActivity(intent);
                }


            }
        });
        //onclick post image end

        //menu part start
        homePostsHolder.iv_menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialogMenu = new Dialog(mContext);
                // Include dialog.xml file
                dialogMenu.setContentView(R.layout.custom_dialog_menu_posts_home);
                // Set dialog title
                dialogMenu.setTitle("Custom Dialog");
                TextView tv_delete_post = (TextView) dialogMenu.findViewById(R.id.tv_delete_post);
                TextView tv_share_post = (TextView) dialogMenu.findViewById(R.id.tv_share_post);
                TextView save_to_notes = (TextView) dialogMenu.findViewById(R.id.save_to_notes);
                TextView tv_turn_on_post_notif = (TextView) dialogMenu.findViewById(R.id.tv_turn_on_post_notif);
                TextView tv_share_link=(TextView)dialogMenu.findViewById(R.id.tv_share_link);
                TextView tv_report_post = (TextView) dialogMenu.findViewById(R.id.tv_report_post);
                dialogMenu.show();
                if (!mUserId.equals(mMyuserid))
                    tv_delete_post.setVisibility(View.GONE);
                else {
                    save_to_notes.setVisibility(View.GONE);
                    tv_turn_on_post_notif.setVisibility(View.GONE);
                    tv_report_post.setVisibility(View.GONE);

                }
                tv_delete_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ExtraFunctions.isNetworkStatusAvialable(mContext)) {
                            String url = ExtraFunctions.serverurl + "deleteHomePosts.php";
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject emp = (new JSONObject(response));
                                        String result = emp.getString("result");
                                        if (result.equals("successful")) {
                                            Toast.makeText(mContext, "Post Deleted successfully", Toast.LENGTH_SHORT).show();
                                            SharedPreferences.Editor sharedPreferencesEditPosts = mSharedPreferences.edit();
                                            sharedPreferencesEditPosts.putString("doubts",String.valueOf(Integer.parseInt(mSharedPreferences.getString("posts",""))-1));
                                            sharedPreferencesEditPosts.apply();
                                        }
                                        if (result.equals("error")) {
                                            Toast.makeText(mContext, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception exception) {
                                        exception.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(CreatePostQueryDoubts.this, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                protected Map<String, String> getParams() {
                                    Map<String, String> MyData = new HashMap<>();
                                    MyData.put("postid", mPostId);
                                    return MyData;
                                }
                            };
                            mRequestQueue.add(stringRequest);
                        } else {
                            Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                        }
                        dialogMenu.dismiss();
                    }
                });

                tv_share_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ExtraFunctions.isNetworkStatusAvialable(mContext)) {
                            mDialog.show();
                            final String extrastring = "\n\nThis Post is posted by " + mUserName + " in \'Student Assessment\' app." +
                                    "\nTo get such updates regularly download \'Student Assessment\' app now." +
                                    "\nhttps://play.google.com/store/apps/details?id=com.tecent.student_assessment";
                            mDialog.dismiss();
                            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                            intentShareFile.setType("text/plain");
                            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                    "Sharing File...");
                            intentShareFile.putExtra(Intent.EXTRA_TEXT, mPostText + extrastring);
                            mContext.startActivity(Intent.createChooser(intentShareFile, "Share"));
                        } else {
                            Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialogMenu.dismiss();
                            }
                        }, 500);
                    }
                });

                tv_share_link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ExtraFunctions.isNetworkStatusAvialable(mContext)) {
                            mDialog.show();
                            final String shareString = "https://sas.a3creators.co.in/StudentAssessment/post?id="+mPostId;
                            mDialog.dismiss();
                            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                            intentShareFile.setType("text/plain");
                            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                    "Sharing File...");
                            intentShareFile.putExtra(Intent.EXTRA_TEXT, shareString);
                            mContext.startActivity(Intent.createChooser(intentShareFile, "Share"));
                        } else {
                            Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialogMenu.dismiss();
                            }
                        }, 200);
                    }
                });

                tv_report_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialogReport = new Dialog(mContext);
                        // Include dialog.xml file
                        dialogReport.setContentView(R.layout.report_post_home);
                        // Set dialog title
                        dialogReport.setTitle("Custom Dialog");
                        final RadioGroup radioGroup = dialogReport.findViewById(R.id.radiogroup);
                        final RadioButton spamnpromotion = dialogReport.findViewById(R.id.spamnpromotion);
                        final RadioButton violencenharassment = dialogReport.findViewById(R.id.violencenharassment);
                        final RadioButton wrong = dialogReport.findViewById(R.id.wrong);
                        final RadioButton copyrightviolation = dialogReport.findViewById(R.id.copyrightviolation);
                        final RadioButton shouldnotbe = dialogReport.findViewById(R.id.shouldnotbe);
                        final EditText et_explain = dialogReport.findViewById(R.id.et_explain);
                        TextView tv_btn_cancel = dialogReport.findViewById(R.id.tv_btn_cancel);
                        final TextView tv_btn_report = dialogReport.findViewById(R.id.tv_btn_report);
                        int selectedIdRadio = radioGroup.getCheckedRadioButtonId();
                        // find the radiobutton by returned id
                        final RadioButton radioButton = dialogReport.findViewById(selectedIdRadio);
                        tv_btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogReport.dismiss();
                            }
                        });
                        tv_btn_report.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String reportValue = "";
                                if (spamnpromotion.isChecked()) {
                                    reportValue = "Spam/Promotion";
                                } else if (violencenharassment.isChecked()) {
                                    reportValue = "Violence/Harassment";
                                } else if (wrong.isChecked()) {
                                    reportValue = "Wrong Information";
                                } else if (copyrightviolation.isChecked()) {
                                    reportValue = "Copyright Violation";
                                } else if (shouldnotbe.isChecked()) {
                                    reportValue = "Should not be in Student Assessment App";
                                }
                                final String explainValue = et_explain.getText().toString();
                                if (!(radioGroup.getCheckedRadioButtonId() == -1)) {
                                    String url = ExtraFunctions.serverurl + "reportPost.php";
                                    final String finalReportValue = reportValue;
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject emp = (new JSONObject(response));
                                                String result = emp.getString("result");
                                                if (result.equals("successful")) {
                                                    dialogReport.dismiss();
                                                    Toast.makeText(mContext, "Post reported successsfully.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(mContext, "An error occured.", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (Exception e) {
                                                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            mDialog.dismiss();
                                            Toast.makeText(mContext, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                        protected Map<String, String> getParams() {
                                            Map<String, String> MyData = new HashMap<>();
                                            MyData.put("postid", mPostId);
                                            MyData.put("userid", mMyuserid);
                                            MyData.put("reason", finalReportValue);
                                            MyData.put("explanation", explainValue);
                                            return MyData;
                                        }
                                    };
                                    mRequestQueue.add(stringRequest);
                                } else {
                                    Toast.makeText(mContext, "Please select a reason.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                        dialogMenu.dismiss();
                        dialogReport.show();
                    }
                });
            }
        });
        //menu part end

        homePostsHolder.iv_post_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intSp=new Intent(mContext,SinglePostsActivity.class);
                intSp.putExtra("postid",mPostId);
                mContext.startActivity(intSp);
            }
        });
        //post likes
        int likes=Integer.parseInt(mLikes);
        if (mLikes.equals("0")||mLikes.equals("1")) {
            if (mSharedPreferencesLike.getString(mPostId, "").equals("liked")){
                homePostsHolder.likes_count.setText("Liked by you and "+(likes-1)+" Others");
            }else{
                homePostsHolder.likes_count.setText(mLikes + " Like");
            }
        } else{
            if (mSharedPreferencesLike.getString(mPostId, "").equals("liked")){
                homePostsHolder.likes_count.setText("Liked by you and "+(likes-1)+" Others");
            }else{
                homePostsHolder.likes_count.setText(mLikes + " Likes");
            }
        }
        //comments count
        if (mComments.equals("0")) {
            homePostsHolder.comments_count.setText("No Comments");
        }else if (mComments.equals("1")){
            homePostsHolder.comments_count.setText("View 1 comment");
        }
        else{
            homePostsHolder.comments_count.setText("View all "+mComments+" comments");
        }
        //comments click
        homePostsHolder.ll_view_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postIntent=new Intent(mContext,CommentsPostHome.class);
                postIntent.putExtra("postid",mPostId);
                mContext.startActivity(postIntent);
            }
        });
        //like part start
        homePostsHolder.ivlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ExtraFunctions.isNetworkStatusAvialable(mContext)) {
                    if (mSharedPreferencesLike.getString(mPostId, "").equals("liked")) {
                        String url = ExtraFunctions.serverurl + "unLikePosts.php";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject emp = (new JSONObject(response));
                                    String result = emp.getString("result");
                                    if (result.equals("successful")) {
                                        SharedPreferences.Editor speLike = mSharedPreferencesLike.edit();
                                        speLike.putString(mPostId, "");
                                        speLike.apply();
                                        DrawableCompat.setTint(homePostsHolder.ivlike.getDrawable(), ContextCompat.getColor(mContext, R.color.vectordrawablelike));
                                        homePostsHolder.ivlike.setPadding(1, 0, 1, 0);
                                        homePostsHolder.likes_count.setText(mLikes+" Likes");
                                        Toast.makeText(mContext, "Post Unliked", Toast.LENGTH_SHORT).show();
                                    }
                                    if (result.equals("error")) {
                                        Toast.makeText(mContext, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(CreatePostQueryDoubts.this, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            protected Map<String, String> getParams() {
                                Map<String, String> MyData = new HashMap<>();
                                MyData.put("postid", mPostId);
                                MyData.put("userid", mMyuserid);
                                return MyData;
                            }
                        };
                        mRequestQueue.add(stringRequest);
                    } else {
                        String url = ExtraFunctions.serverurl + "likePosts.php";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject emp = (new JSONObject(response));
                                    String result = emp.getString("result");
                                    if (result.equals("alreadyLiked")) {
                                        Toast.makeText(mContext, "Post already Liked by you", Toast.LENGTH_SHORT).show();
                                    }
                                    if (result.equals("successful")) {
                                        SharedPreferences.Editor speLike = mSharedPreferencesLike.edit();
                                        speLike.putString(mPostId, "liked");
                                        speLike.apply();
                                        DrawableCompat.setTint(homePostsHolder.ivlike.getDrawable(), ContextCompat.getColor(mContext, R.color.colorPrimary));
                                        homePostsHolder.ivlike.setPadding(1, 0, 1, 0);
                                        homePostsHolder.likes_count.setText("Liked by you and "+mLikes+" Others");
                                        Toast.makeText(mContext, "Post Liked successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    if (result.equals("error")) {
                                        Toast.makeText(mContext, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(CreatePostQueryDoubts.this, "Error! Please try again later...", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            protected Map<String, String> getParams() {
                                Map<String, String> MyData = new HashMap<>();
                                MyData.put("postid", mPostId);
                                MyData.put("userid", mMyuserid);
                                return MyData;
                            }
                        };
                        mRequestQueue.add(stringRequest);
                    }

                } else {
                    Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //like part end

        //comment part start
        homePostsHolder.ivreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postIntent=new Intent(mContext,CommentsPostHome.class);
                postIntent.putExtra("postid",mPostId);
                mContext.startActivity(postIntent);
            }
        });
        //comment part end

        homePostsHolder.ivshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ExtraFunctions.isNetworkStatusAvialable(mContext)) {
                    mDialog.show();
                    final String extrastring = "\n\nThis Post is posted by " + mUserName + " in \'Student Assessment\' app." +
                            "\nTo get such updates regularly download \'Student Assessment\' app now." +
                            "\nhttps://play.google.com/store/apps/details?id=com.tecent.student_assessment";
                    if (mPostFile.equals("")) {
                        mDialog.dismiss();
                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                        intentShareFile.setType("text/plain");
                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                "Sharing File...");
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, mPostText + extrastring);
                        mContext.startActivity(Intent.createChooser(intentShareFile, "Share"));
                    } else {
                        File f;
                        if (mPostFile.substring(mPostFile.lastIndexOf('.') + 1).equals("pdf") ||
                                mPostFile.substring(mPostFile.lastIndexOf('.') + 1).equals("PDF")) {
                            f = new File(ExtraFunctions.serverurl +
                                    "posts/pdfthumbnail/" + mPostFile.replace(mPostFile.substring(mPostFile.lastIndexOf('.') + 1), "") + "jpg");
                        } else {
                            f = new File(ExtraFunctions.serverurl + "posts/" + mPostFile);
                        }
                        if (f.exists()) {
                            mDialog.dismiss();
                            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                            intentShareFile.setType("image/*");
                            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"
                                    + ExtraFunctions.rootdir + "posts/" + mPostFile));
                            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                    "New post from \'Hints\' app.");
                            intentShareFile.putExtra(Intent.EXTRA_TEXT, mPostText + extrastring);
                            mContext.startActivity(Intent.createChooser(intentShareFile, "Share"));
                        } else {
                            String dUrl;
                            final String fDnld;
                            if (mPostFile.substring(mPostFile.lastIndexOf('.') + 1).equals("pdf") ||
                                    mPostFile.substring(mPostFile.lastIndexOf('.') + 1).equals("PDF")) {
                                dUrl = ExtraFunctions.serverurl +
                                        "posts/pdfthumbnail/" + mPostFile.replace(mPostFile.substring(mPostFile.lastIndexOf('.') + 1), "") + "jpg";
                                fDnld = mPostFile.replace(mPostFile.substring(mPostFile.lastIndexOf('.') + 1), "") + "jpg";
                            } else {
                                dUrl = ExtraFunctions.serverurl + "posts/" + mPostFile;
                                fDnld = mPostFile;
                            }
                            PRDownloader.download(dUrl,
                                    ExtraFunctions.rootdir + "posts/",
                                    fDnld)
                                    .build()
                                    .start(new OnDownloadListener() {
                                        @Override
                                        public void onDownloadComplete() {
                                            mDialog.dismiss();
                                            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                            intentShareFile.setType("image/*");
                                            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"
                                                    + ExtraFunctions.rootdir + "posts/" + fDnld));
                                            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                                    "Sharing File...");
                                            intentShareFile.putExtra(Intent.EXTRA_TEXT, mPostText + extrastring);
                                            mContext.startActivity(Intent.createChooser(intentShareFile, "Share"));
                                        }

                                        @Override
                                        public void onError(Error error) {
                                            mDialog.dismiss();
                                        }

                                    });
                        }
                    }
                } else {
                    Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return museridlist.size();
    }

    class HomePostsHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile_image, iv_post_image, iv_menu_btn, ivlike, ivreply, ivshare, my_profile_image;
        TextView iv_username, ivdate_and_branch_subject, iv_post_text, likes_count, comments_count;
        LinearLayout ll_view_comments;

        HomePostsHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile_image = itemView.findViewById(R.id.iv_profile_image);
            iv_post_image = itemView.findViewById(R.id.iv_post_image);
            iv_username = itemView.findViewById(R.id.ivusername);
            ivdate_and_branch_subject = itemView.findViewById(R.id.iv_datetime_branch_subject);
            iv_post_text = itemView.findViewById(R.id.iv_post_text);
            iv_menu_btn = itemView.findViewById(R.id.iv_menu);
            ivlike = itemView.findViewById(R.id.ivlike);
            ivreply = itemView.findViewById(R.id.ivreply);
            ivshare = itemView.findViewById(R.id.ivshare);
            my_profile_image=itemView.findViewById(R.id.my_profile_image);
            likes_count=itemView.findViewById(R.id.count_likes);
            comments_count=itemView.findViewById(R.id.count_comments);
            ll_view_comments=itemView.findViewById(R.id.view_comments);
        }
    }
}

