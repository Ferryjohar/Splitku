package com.example.splitku.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE uid = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()
}