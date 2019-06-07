package com.tecent.student_assessment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.downloader.Progress;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class HomeFragment extends Fragment {
    TextView textView;
    SharedPreferences sharedPreferences, sharedPreferencesLike;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    RequestQueue requestQueue;
    RecyclerView mRecyclerViewPostHome;
    ACProgressFlower dialog;
    TableRow tableRow;

    ArrayList<String> useridlist;
    ArrayList<String> userdplist;
    ArrayList<String> usernamelist;
    ArrayList<String> userbranchlist;
    ArrayList<String> posttimelist;
    ArrayList<String> postidlist;
    ArrayList<String> posttextlist;
    ArrayList<String> postfilelist;
    ArrayList<String> subjectlist;
    ArrayList<String> likeslist;
    ArrayList<String> commentslist;
    String userid;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homefragment, container, false);
        textView = view.findViewById(R.id.textView);

        useridlist = new ArrayList<String>();
        userdplist = new ArrayList<String>();
        usernamelist = new ArrayList<String>();
        posttimelist = new ArrayList<String>();
        postidlist = new ArrayList<String>();
        userbranchlist = new ArrayList<String>();
        posttextlist = new ArrayList<String>();
        postfilelist = new ArrayList<String>();
        subjectlist = new ArrayList<String>();
        likeslist = new ArrayList<String>();
        commentslist = new ArrayList<String>();

        sharedPreferences = this.getActivity().getSharedPreferences(ExtraFunctions.sharedPreferencesId, Context.MODE_PRIVATE);
        sharedPreferencesLike = this.getActivity().getSharedPreferences(ExtraFunctions.sharedPreferencesLikeId, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String passw = sharedPreferences.getString("passw", "");
        String name = sharedPreferences.getString("name", "");
        userid = sharedPreferences.getString("userid", "");
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progress_bar);
        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.setCancelable(false);
        mRecyclerViewPostHome = view.findViewById(R.id.recycler_view_post_home);
        mRecyclerViewPostHome.setHasFixedSize(false);
//        mRecyclerViewPostHome.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {
                    private static final float SPEED = 300f;

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewPostHome.setLayoutManager(layoutManager);

        requestQueue = Volley.newRequestQueue(getActivity());

        loadPostsFromSpf();
        try {
            if (ExtraFunctions.isNetworkStatusAvialable(getActivity())) {
                volleyPostDataRequest();
            } else {
                Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (ExtraFunctions.isNetworkStatusAvialable(getActivity()))
                        volleyPostDataRequest();
                    else {
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        if (isAdded()) {
            tableRow = new TableRow(getContext());
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        }

        return view;
    }

    public void loadPostsFromSpf() {
        progressBar.setVisibility(View.GONE);
        String response=sharedPreferences.getString("homeresponse","");
        try {
            JSONObject emp = (new JSONObject(response));
            useridlist.clear();
            usernamelist.clear();
            userdplist.clear();
            posttimelist.clear();
            userbranchlist.clear();
            postidlist.clear();
            posttextlist.clear();
            postfilelist.clear();
            subjectlist.clear();
            likeslist.clear();
            commentslist.clear();

            JSONArray useridarray = emp.getJSONArray("userid");
            JSONArray usernamearray = emp.getJSONArray("name");
            JSONArray userbrancharray = emp.getJSONArray("branch");
            JSONArray userdparray = emp.getJSONArray("userdp");
            JSONArray posttimearray = emp.getJSONArray("posttime");
            JSONArray postdoubtidarray = emp.getJSONArray("postid");
            JSONArray posttextarray = emp.getJSONArray("posttext");
            JSONArray postfilearray = emp.getJSONArray("filename");
            JSONArray postsubjectarray = emp.getJSONArray("subject");
            JSONArray postlikesarray = emp.getJSONArray("likes");
            JSONArray postcommentsarray = emp.getJSONArray("comments");

            if (useridarray != null) {
                int len = useridarray.length();
                for (int i = 0; i < len; i++) {
                    useridlist.add(useridarray.get(i).toString());
                    usernamelist.add(usernamearray.get(i).toString());
                    userdplist.add(userdparray.get(i).toString());
                    posttimelist.add(posttimearray.get(i).toString());
                    userbranchlist.add(userbrancharray.get(i).toString());
                    postidlist.add(postdoubtidarray.get(i).toString());
                    posttextlist.add(posttextarray.get(i).toString());
                    postfilelist.add(postfilearray.get(i).toString());
                    subjectlist.add(postsubjectarray.get(i).toString());
                    likeslist.add(postlikesarray.get(i).toString());
                    commentslist.add(postcommentsarray.get(i).toString());
                }
            }
            MyRecyclerHomePostsAdapter homePostsAdapter = new MyRecyclerHomePostsAdapter(sharedPreferences, sharedPreferencesLike, dialog, requestQueue, getActivity(), userid, useridlist, userdplist, usernamelist,
                    userbranchlist, posttimelist, postfilelist, postidlist, posttextlist, subjectlist, likeslist, commentslist);
            mRecyclerViewPostHome.setAdapter(homePostsAdapter);
        } catch (Exception e) {

        }
    }

    public void volleyPostDataRequest() {
        swipeRefreshLayout.setRefreshing(true);
        try {
            String url = ExtraFunctions.serverurl + "postsHomeDataAdapter.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject emp = (new JSONObject(response));
                        String result = emp.getString("result");
                        SharedPreferences.Editor spe = sharedPreferences.edit();
                        spe.putString("homeresponse", response);
                        spe.apply();
                        if (result.equals("successful")) {
                            useridlist.clear();
                            usernamelist.clear();
                            userdplist.clear();
                            posttimelist.clear();
                            userbranchlist.clear();
                            postidlist.clear();
                            posttextlist.clear();
                            postfilelist.clear();
                            subjectlist.clear();
                            likeslist.clear();
                            commentslist.clear();

                            JSONArray useridarray = emp.getJSONArray("userid");
                            JSONArray usernamearray = emp.getJSONArray("name");
                            JSONArray userbrancharray = emp.getJSONArray("branch");
                            JSONArray userdparray = emp.getJSONArray("userdp");
                            JSONArray posttimearray = emp.getJSONArray("posttime");
                            JSONArray postdoubtidarray = emp.getJSONArray("postid");
                            JSONArray posttextarray = emp.getJSONArray("posttext");
                            JSONArray postfilearray = emp.getJSONArray("filename");
                            JSONArray postsubjectarray = emp.getJSONArray("subject");
                            JSONArray postlikesarray = emp.getJSONArray("likes");
                            JSONArray postcommentsarray = emp.getJSONArray("comments");

                            if (useridarray != null) {
                                int len = useridarray.length();
                                for (int i = 0; i < len; i++) {
                                    useridlist.add(useridarray.get(i).toString());
                                    usernamelist.add(usernamearray.get(i).toString());
                                    userdplist.add(userdparray.get(i).toString());
                                    posttimelist.add(posttimearray.get(i).toString());
                                    userbranchlist.add(userbrancharray.get(i).toString());
                                    postidlist.add(postdoubtidarray.get(i).toString());
                                    posttextlist.add(posttextarray.get(i).toString());
                                    postfilelist.add(postfilearray.get(i).toString());
                                    subjectlist.add(postsubjectarray.get(i).toString());
                                    likeslist.add(postlikesarray.get(i).toString());
                                    commentslist.add(postcommentsarray.get(i).toString());
                                }
                            }
                            MyRecyclerHomePostsAdapter homePostsAdapter = new MyRecyclerHomePostsAdapter(sharedPreferences, sharedPreferencesLike, dialog, requestQueue, getActivity(), userid, useridlist, userdplist, usernamelist,
                                    userbranchlist, posttimelist, postfilelist, postidlist, posttextlist, subjectlist, likeslist, commentslist);
                            mRecyclerViewPostHome.setAdapter(homePostsAdapter);
                        }
                    } catch (Exception exception) {
                        Toast.makeText(getActivity(), exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(getActivity(), "Error! Please try again later...", Toast.LENGTH_SHORT).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    return MyData;
                }
            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onResume() {
//        volleyPostDataRequest();
        super.onResume();
    }
}
