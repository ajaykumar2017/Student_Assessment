package com.tecent.student_assessment.objects

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class CommentObject internal constructor(
        @field:SerializedName("commentId")
        var commentId: String,

        @field:SerializedName("userId")
        var userId: String?,

        @field:SerializedName("userName")
        var userName: String?,

        @field:SerializedName("userDp")
        var userDp: String?,

        @field:SerializedName("commentText")
        var commentText: String?,

        @field:SerializedName("commentImage")
        var commentImage: String?,

        @field:SerializedName("commentTime")
        var commentTime: String?,

        @field:SerializedName("repliesCount")
        var repliesCount: String?,

        @field:SerializedName("replyList")
        var replyObjectArrayList: ArrayList<ReplyObject>
) : Serializable {

    override fun toString(): String {
        return commentId
    }

    fun equals(obj: CommentObject): Boolean {
        return obj.commentId == this.commentId
    }
}
