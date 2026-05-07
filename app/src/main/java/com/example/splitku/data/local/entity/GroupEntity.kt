package com.example.splitku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val groupName: String,
    val ownerId: String, // user yang membuat group
    val invateCode: String, // untuk qr atau join ke group
    val createdAt: Long = System.currentTimeMillis()
)