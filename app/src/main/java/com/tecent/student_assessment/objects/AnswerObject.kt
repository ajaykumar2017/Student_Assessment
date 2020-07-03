package com.tecent.student_assessment.objects

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class AnswerObject internal constructor(
        @field:SerializedName("answerId")
        var answerId: String,

        @field:SerializedName("userId")
        var userId: String?,

        @field:SerializedName("userName")
        var userName: String?,

        @field:SerializedName("userDp")
        var userDp: String?,

        @field:SerializedName("answerText")
        var answerText: String?,

        @field:SerializedName("answerImage")
        var answerImage: String?,

        @field:SerializedName("answerTime")
        var answerTime: String?,

        @field:SerializedName("repliesCount")
        var repliesCount: String?,

        @field:SerializedName("replyList")
        var replyForArrayListObject: ArrayList<ReplyForDoubtsPostsAnswersObject>
) : Serializable {

    override fun toString(): String {
        return answerId
    }

    fun equals(obj: AnswerObject): Boolean {
        return obj.answerId == this.answerId
    }
}
