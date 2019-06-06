package com.tecent.student_assessment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class DoubtsFragment extends Fragment {
    RequestQueue requestQueue;
    TextView post_name_with_text;
    ImageView profile_image;
    SharedPreferences sharedPreferences;
    LinearLayout ll_createpostquery;
    private RecyclerView mRecyclerViewPost;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    ACProgressFlower dialog;
    TableRow tableRow;

    ArrayList<String> useridlist;
    ArrayList<String> userdplist;
    ArrayList<String> usernamelist;
    ArrayList<String> userbranchlist;
    ArrayList<String> posttimelist;
    ArrayList<String> postdoubtidlist;
    ArrayList<String> posttextlist;
    ArrayList<String> postimagelist;
    ArrayList<String> noofanswerslist;
    String userid;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doubts_fragment, container, false);
        post_name_with_text = view.findViewById(R.id.textView1);
        ll_createpostquery = view.findViewById(R.id.ll_createpostquery);
        profile_image = view.findViewById(R.id.profile_image);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .fadeColor(Color.BLACK).build();
        dialog.setCancelable(false);

        mRecyclerViewPost = view.findViewById(R.id.recycler_view_post);
        mRecyclerViewPost.setHasFixedSize(false);
        mRecyclerViewPost.setLayoutManager(new LinearLayoutManager(getActivity()));

        useridlist = new ArrayList<String>();
        userdplist = new ArrayList<String>();
        usernamelist = new ArrayList<String>();
        posttimelist = new ArrayList<String>();
        postdoubtidlist = new ArrayList<String>();
        userbranchlist = new ArrayList<String>();
        posttextlist = new ArrayList<String>();
        postimagelist = new ArrayList<String>();
        noofanswerslist=new ArrayList<String>();

        requestQueue = Volley.newRequestQueue(getActivity());
        sharedPreferences = this.getActivity().getSharedPreferences(ExtraFunctions.sharedPreferencesId, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        userid=sharedPreferences.getString("userid","");
        post_name_with_text.setText("Hi " + name + " Do you want to ask a doubt?");
        ll_createpostquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreatePostQueryDoubts.class);
                startActivity(intent);
            }
        });

        String userdp = sharedPreferences.getString("userdp", "");
        requestQueue.add(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "userdp/" + userdp, profile_image));
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

        if(isAdded()){
            tableRow = new TableRow(getContext());
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        }
        return view;
    }

    public void volleyPostDataRequest() {
        try{
            String url = ExtraFunctions.serverurl + "doubtPostsDataAdapter.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject emp = (new JSONObject(response));
                        String result = emp.getString("result");
                        if (result.equals("successful")) {
                            useridlist.clear();
                            usernamelist.clear();
                            userdplist.clear();
                            posttimelist.clear();
                            userbranchlist.clear();
                            postdoubtidlist.clear();
                            posttextlist.clear();
                            postimagelist.clear();
                            noofanswerslist.clear();

                            JSONArray useridarray = emp.getJSONArray("userid");
                            JSONArray usernamearray = emp.getJSONArray("name");
                            JSONArray userbrancharray = emp.getJSONArray("branch");
                            JSONArray userdparray = emp.getJSONArray("userdp");
                            JSONArray posttimearray = emp.getJSONArray("posttime");
                            JSONArray postdoubtidarray = emp.getJSONArray("postdoubtid");
                            JSONArray posttextarray = emp.getJSONArray("posttext");
                            JSONArray postimagearray = emp.getJSONArray("postimage");
                            JSONArray postNoofAnswersarray=emp.getJSONArray("answers");

                            if (useridarray != null) {
                                int len = useridarray.length();
                                for (int i = 0; i < len; i++) {
                                    useridlist.add(useridarray.get(i).toString());
                                    usernamelist.add(usernamearray.get(i).toString());
                                    userdplist.add(userdparray.get(i).toString());
                                    posttimelist.add(posttimearray.get(i).toString());
                                    userbranchlist.add(userbrancharray.get(i).toString());
                                    postdoubtidlist.add(postdoubtidarray.get(i).toString());
                                    posttextlist.add(posttextarray.get(i).toString());
                                    postimagelist.add(postimagearray.get(i).toString());
                                    noofanswerslist.add(postNoofAnswersarray.get(i).toString());
                                }
                            }
                            MyRecyclerPostDoubtsAdapter postDoubtsAdapter = new MyRecyclerPostDoubtsAdapter(sharedPreferences, dialog,requestQueue,getActivity(), userid, useridlist, userdplist, usernamelist,
                                    userbranchlist, posttimelist, postimagelist, postdoubtidlist, posttextlist, noofanswerslist);
                            mRecyclerViewPost.setAdapter(postDoubtsAdapter);
                        }
                    } catch (Exception exception) {
                        Toast.makeText(getActivity(), "some error occured! try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
//                Toast.makeText(getActivity(), "Error! Please try again later...", Toast.LENGTH_SHORT).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    return MyData;
                }
            };
            requestQueue.add(stringRequest);
        }catch (Exception e){
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onResume() {
        volleyPostDataRequest();
        super.onResume();
    }
}
