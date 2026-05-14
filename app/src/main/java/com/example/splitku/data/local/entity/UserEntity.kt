package com.example.splitku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val userId: String, // USR-0001
    val name: String,
    val email: String,
    val createdAt: Long
)