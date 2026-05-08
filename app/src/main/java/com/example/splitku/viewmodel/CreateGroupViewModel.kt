package com.example.splitku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitku.data.local.dao.GroupDao
import com.example.splitku.data.local.entity.GroupEntity
import com.example.splitku.utils.generateInviteCode
import kotlinx.coroutines.launch

class CreateGroupViewModel(
    private val groupDao: GroupDao
): ViewModel(){
    fun createGroup(groupName: String, ownerId: String) {
        viewModelScope.launch {
            val inviteCode = generateInviteCode()
            val group = GroupEntity(
                groupName = groupName,
                ownerId = ownerId,
                inviteCode = inviteCode
            )
            groupDao.insertGroup(group)
        }
    }
}