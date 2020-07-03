package com.tecent.student_assessment.objects

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class DiscussionForumObject internal constructor(
        @field:SerializedName("discussionId")
        var discussionId: String,

        @field:SerializedName("userId")
        var userId: String?,

        @field:SerializedName("userName")
        var userName: String?,

        @field:SerializedName("discussionText")
        var discussionText: String?,

        @field:SerializedName("discussionTime")
        var discussionTime: String?
) : Serializable {

    override fun toString(): String {
        return discussionId
    }

    fun equals(obj: DiscussionForumObject): Boolean {
        return obj.discussionId == this.discussionId
    }
}
