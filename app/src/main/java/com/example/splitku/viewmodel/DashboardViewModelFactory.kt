package com.example.splitku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitku.data.local.dao.GroupDao

class DashboardViewModelFactory(
    private val groupDao: GroupDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(groupDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}