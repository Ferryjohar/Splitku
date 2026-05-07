package com.example.splitku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitku.data.local.dao.GroupDao
import com.example.splitku.data.local.entity.GroupEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val groupDao: GroupDao
) : ViewModel() {
    private val _groups = MutableStateFlow<List<GroupEntity>>(emptyList())
    val groups: StateFlow<List<GroupEntity>> = _groups


    init {
        loadGroups()
    }
    private fun loadGroups() {
        viewModelScope.launch {
            groupDao.getAllGroups().collect {
                _groups.value = it
            }
        }
    }
    suspend fun addGroup(group: GroupEntity) {
        groupDao.insertGroup(group)
    }
    suspend fun deleteGroup(group: GroupEntity) {
        groupDao.deleteGroup(group)
    }
}