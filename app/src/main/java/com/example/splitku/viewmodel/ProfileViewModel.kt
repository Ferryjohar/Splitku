package com.example.splitku.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {


    //tempat menyimpan nama lengkap user
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    // EMAIL
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    // PASSWORD
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    // USER ID DUMMY
    private val _userId = MutableStateFlow("ID-ARI-2024")
    val userId: StateFlow<String> = _userId

    // SET DATA PROFILE
    fun setProfileData(
        name: String? = null,
        email: String,
        password: String
    ) {

        if (name != null) {
            _name.value = name
        }

        _email.value = email
        _password.value = password
    }
}