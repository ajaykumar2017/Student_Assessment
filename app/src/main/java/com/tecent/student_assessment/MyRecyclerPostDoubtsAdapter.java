package com.tecent.student_assessment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class MyRecyclerPostDoubtsAdapter extends RecyclerView.Adapter<MyRecyclerPostDoubtsAdapter.PostDoubtsHolder> {
    private Context mContext;
    private ArrayList<String> museridlist;
    private ArrayList<String> muserdplist;
    private ArrayList<String> musernamelist;
    private ArrayList<String> mposttimelist;
    private ArrayList<String> mpostdoubtidlist;
    private ArrayList<String> muserbranchlist;
    private ArrayList<String> mposttextlist;
    private ArrayList<String> mpostimagelist;
    String mMyuserid;
    RequestQueue mRequestQueue;
    ACProgressFlower mDialog;

    public MyRecyclerPostDoubtsAdapter(ACProgressFlower dialog, RequestQueue requestQueue, Context context, String myuserid, ArrayList<String> useridlist, ArrayList<String> userdplist, ArrayList<String> usernamelist, ArrayList<String> userbranchlist, ArrayList<String> posttimelist, ArrayList<String> postimagelist, ArrayList<String> postdoubtidlist, ArrayList<String> posttextlist) {
        mDialog = dialog;
        mRequestQueue = requestQueue;
        mContext = context;
        mMyuserid = myuserid;
        museridlist = useridlist;
        muserdplist = userdplist;
        musernamelist = usernamelist;
        mposttimelist = posttimelist;
        mpostdoubtidlist = postdoubtidlist;
        muserbranchlist = userbranchlist;
        mposttextlist = posttextlist;
        mpostimagelist = postimagelist;
    }

    @NonNull
    @Override
    public PostDoubtsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.indiview_doubts_post, viewGroup, false);
        return new PostDoubtsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostDoubtsHolder postDoubtsHolder, int position) {
        final String mUserId = museridlist.get(position);
        final String mUserdp = muserdplist.get(position);
        final String mUserName = musernamelist.get(position);
        String mPostDateTime = mposttimelist.get(position);
        final String mBranch = muserbranchlist.get(position);
        final String mPostDoubtId = mpostdoubtidlist.get(position);
        final String mPostText = mposttextlist.get(position);
        final String mPostImage = mpostimagelist.get(position);

        mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "userdp/" + mUserdp, postDoubtsHolder.iv_profile_image));
        postDoubtsHolder.iv_username.setText(mUserName);
        postDoubtsHolder.ivdate_and_branch.setText(mPostDateTime + "  " + "\u2022" + " " + mBranch.toUpperCase());
        postDoubtsHolder.iv_post_text.setText(mPostText);
        if (!mPostImage.equals(""))
            mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "postdoubts/" + mPostImage, postDoubtsHolder.iv_post_image));
        else
            postDoubtsHolder.iv_post_image.setVisibility(View.GONE);
        final String viewLink = ExtraFunctions.serverurl + "postdoubts/" + mPostImage;
        postDoubtsHolder.iv_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ImagePdfWebView.class);
                intent.putExtra("viewLink", viewLink);
                mContext.startActivity(intent);
            }
        });

        postDoubtsHolder.iv_menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(mContext, postDoubtsHolder.iv_menu_btn);
                //inflating menu from xml resource
                popup.inflate(R.menu.doubts_post_menu);

                final Menu popupMenu = popup.getMenu();
                if (!mUserId.equals(mMyuserid))
                    popupMenu.findItem(R.id.delete_post).setVisible(false);
                else {
                    popupMenu.findItem(R.id.save_to_notes).setVisible(false);
                    popupMenu.findItem(R.id.turn_on_post_notifi).setVisible(false);
                    popupMenu.findItem(R.id.report_post).setVisible(false);

                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.delete_post:
                                if (ExtraFunctions.isNetworkStatusAvialable(mContext)) {
                                    String url = ExtraFunctions.serverurl + "deleteDoubtPosts.php";
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject emp = (new JSONObject(response));
                                                String result = emp.getString("result");
                                                if (result.equals("successful")) {
                                                    Toast.makeText(mContext, "Post Deleted successfully", Toast.LENGTH_SHORT).show();
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
                                            Map<String, String> MyData = new HashMap<String, String>();
                                            MyData.put("postdoubtid", mPostDoubtId);
                                            return MyData;
                                        }
                                    };
                                    mRequestQueue.add(stringRequest);
                                } else {
                                    Toast.makeText(mContext, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case R.id.save_to_notes:
                                Toast.makeText(mContext, "save to notes", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.turn_on_post_notifi:
                                Toast.makeText(mContext, "turn on notif", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.share_post:
                                if (ExtraFunctions.isNetworkStatusAvialable(mContext)) {
                                    mDialog.show();
                                    final String extrastring = "\n\nThis doubt is posted by " + mUserName + " in \'Student Assessment\' app." +
                                            "\nTo get such updates regularly download \'Student Assessment\' app now." +
                                            "\nhttps://play.google.com/store/apps/details?id=com.tecent.student_assessment";
                                    if (mPostImage.equals("")) {
                                        mDialog.dismiss();
                                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                        intentShareFile.setType("text/plain");
                                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                                "Sharing File...");
                                        intentShareFile.putExtra(Intent.EXTRA_TEXT, mPostText + extrastring);
                                        mContext.startActivity(Intent.createChooser(intentShareFile, "Share"));
                                    } else {
                                        File f = new File(ExtraFunctions.rootdir + "postdoubts/" + mPostImage);
                                        if (f.exists()) {
                                            mDialog.dismiss();
                                            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                            intentShareFile.setType("image/*");
                                            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"
                                                    + ExtraFunctions.rootdir + "postdoubts/" + mPostImage));
                                            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                                    "New post from \'Hints\' app.");
                                            intentShareFile.putExtra(Intent.EXTRA_TEXT, mPostText + extrastring);
                                            mContext.startActivity(Intent.createChooser(intentShareFile, "Share"));
                                        } else {
                                            PRDownloader.download(ExtraFunctions.serverurl + "postdoubts/" + mPostImage,
                                                    ExtraFunctions.rootdir + "postdoubts/",
                                                    mPostImage)
                                                    .build()
                                                    .start(new OnDownloadListener() {
                                                        @Override
                                                        public void onDownloadComplete() {
                                                            mDialog.dismiss();
                                                            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                                            intentShareFile.setType("image/*");
                                                            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"
                                                                    + ExtraFunctions.rootdir + "postdoubts/" + mPostImage));
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
                                break;
                            case R.id.report_post:
                                final Dialog dialogReport = new Dialog(mContext);
                                // Include dialog.xml file
                                dialogReport.setContentView(R.layout.report_post_home);
                                // Set dialog title
                                dialogReport.setTitle("Custom Dialog");
                                final RadioGroup radioGroup = (RadioGroup) dialogReport.findViewById(R.id.radiogroup);
                                final RadioButton spamnpromotion = (RadioButton) dialogReport.findViewById(R.id.spamnpromotion);
                                final RadioButton violencenharassment = (RadioButton) dialogReport.findViewById(R.id.violencenharassment);
                                final RadioButton wrong = (RadioButton) dialogReport.findViewById(R.id.wrong);
                                final RadioButton copyrightviolation = (RadioButton) dialogReport.findViewById(R.id.copyrightviolation);
                                final RadioButton shouldnotbe = (RadioButton) dialogReport.findViewById(R.id.shouldnotbe);
                                final EditText et_explain = (EditText) dialogReport.findViewById(R.id.et_explain);
                                TextView tv_btn_cancel = (TextView) dialogReport.findViewById(R.id.tv_btn_cancel);
                                final TextView tv_btn_report = (TextView) dialogReport.findViewById(R.id.tv_btn_report);
                                int selectedIdRadio = radioGroup.getCheckedRadioButtonId();
                                // find the radiobutton by returned id
                                final RadioButton radioButton = (RadioButton) dialogReport.findViewById(selectedIdRadio);
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
                                            String url = ExtraFunctions.serverurl + "reportDoubtsPost.php";
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
                                                    Map<String, String> MyData = new HashMap<String, String>();
                                                    MyData.put("postdoubtid", mPostDoubtId);
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
                                dialogReport.show();
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        //answer now
        postDoubtsHolder.btn_answer_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent doubtIntent=new Intent(mContext,AnswersDoubtsPostsDoubts.class);
                doubtIntent.putExtra("postdoubtid",mPostDoubtId);
                mContext.startActivity(doubtIntent);
            }
        });
        //profile
        postDoubtsHolder.iv_profile_image.setOnClickListener(new View.OnClickListener() {
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
        postDoubtsHolder.iv_username.setOnClickListener(new View.OnClickListener() {
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
        postDoubtsHolder.ivdate_and_branch.setOnClickListener(new View.OnClickListener() {
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


    }

    @Override
    public int getItemCount() {
        return museridlist.size();
    }

    public class PostDoubtsHolder extends RecyclerView.ViewHolder {
        public ImageView iv_profile_image, iv_post_image, iv_menu_btn;
        public TextView iv_username, ivdate_and_branch, iv_post_text;
        public Button btn_answer_now;

        public PostDoubtsHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile_image = itemView.findViewById(R.id.iv_profile_image);
            iv_post_image = itemView.findViewById(R.id.iv_post_image);
            iv_username = itemView.findViewById(R.id.tv_username);
            ivdate_and_branch = itemView.findViewById(R.id.ivdate_and_branch);
            iv_post_text = itemView.findViewById(R.id.iv_post_text);
            iv_menu_btn = itemView.findViewById(R.id.iv_menu);
            btn_answer_now=itemView.findViewById(R.id.btn_answer_now);
        }
    }
}
