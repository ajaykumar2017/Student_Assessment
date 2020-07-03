package com.tecent.student_assessment.objects

import com.google.gson.annotations.SerializedName
import com.tecent.student_assessment.objects.ReplyObject

import java.io.Serializable

class ReplyForDoubtsPostsAnswersObject internal constructor(
        @field:SerializedName("answerId")
        var answerId: String?,

        @field:SerializedName("replyId")
        var replyId: String,

        @field:SerializedName("userId")
        var userId: String?,

        @field:SerializedName("userName")
        var userName: String?,

        @field:SerializedName("userDp")
        var userDp: String?,


        @field:SerializedName("replyText")
        var replyText: String?,

        @field:SerializedName("replyTime")
        var replyTime: String?

) : Serializable {

    override fun toString(): String {
        return replyId
    }

    fun equals(obj: ReplyObject): Boolean {
        return obj.replyId == this.replyId
    }
}
