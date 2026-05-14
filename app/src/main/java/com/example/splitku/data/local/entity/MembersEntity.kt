package com.example.splitku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class MembersEntity(
    @PrimaryKey(autoGenerate = true)
    val memberId: Int = 0,
    val groupId: Int,
    val userId: String,
    val role: String = "member"
)
