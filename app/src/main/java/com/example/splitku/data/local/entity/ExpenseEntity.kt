package com.example.splitku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int =0,
    val groupId: Int,
    val title: String,
    val amount: Double,
    val paidBy: String
)