package com.tecent.student_assessment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.RequestQueue
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList

@Suppress("UNREACHABLE_CODE")
class MyRecyclerPostsCommentsRepliesAdapter(requestQueue: RequestQueue, context:Context, replyObjectArrayList: ArrayList<ReplyObject>) : RecyclerView.Adapter<MyRecyclerPostsCommentsRepliesAdapter.PostsCommentsRepliesHolder>() {
    var mRepliesObjectArrayList:ArrayList<ReplyObject>
    var mRequestQueue:RequestQueue
    var mContext:Context
    init {
        this.mRequestQueue=requestQueue
        this.mContext=context
        this.mRepliesObjectArrayList=replyObjectArrayList
    }
    override fun onBindViewHolder(postsCommentsRepliesHolder: PostsCommentsRepliesHolder, position: Int) {
        val replyObject= mRepliesObjectArrayList[position]
        mRequestQueue.add(ExtraFunctions.createImageRequestFromUrl(
                ExtraFunctions.serverurl+"userdp/"+replyObject.userDp
                ,postsCommentsRepliesHolder.iv_profile_image))
        postsCommentsRepliesHolder.username.text=replyObject.userName+" "+"\u2022"+" "
        postsCommentsRepliesHolder.replyText.text=replyObject.replyText
        postsCommentsRepliesHolder.timeAgo.text=replyObject.replyTime
    }

    override fun getItemCount(): Int {
        return mRepliesObjectArrayList.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyRecyclerPostsCommentsRepliesAdapter.PostsCommentsRepliesHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.indiview_post_comment_replies, p0, false)
        return PostsCommentsRepliesHolder(view)
    }




    inner class PostsCommentsRepliesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_profile_image: ImageView
        var username: TextView
        var replyText: TextView
        var timeAgo: TextView

        init {
            iv_profile_image = itemView.findViewById(R.id.iv_profile_image)
            username = itemView.findViewById(R.id.username)
            replyText = itemView.findViewById(R.id.replyText)
            timeAgo = itemView.findViewById(R.id.timeAgo)
        }
    }
}