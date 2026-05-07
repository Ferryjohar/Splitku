package com.example.splitku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "membbers")
data class MembersEntity(
    @PrimaryKey(autoGenerate = true)
    val mamberId: Int = 0,
    val groupId: Int,
    val userId: String,
    val role: String = "member"
)
