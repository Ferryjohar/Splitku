package com.example.splitku.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    // TAMBAHKAN BARIS INI UNTUK FIRESTORE
    private val db = FirebaseFirestore.getInstance()

    // State untuk mengatur UI (Loading, Success, Error)
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("Email dan Password tidak boleh kosong")
            return
        }

        _loginState.value = LoginState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = LoginState.Success
                    // Setelah sukses, di Activity/Fragment arahkan ke Dashboard
                    // dan mulai sinkronisasi data dari Firebase Firestore ke Room Database
                } else {
                    _loginState.value = LoginState.Error(task.exception?.message ?: "Login gagal")
                }
            }
    }

    fun register(name: String, email: String, password: String) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            _loginState.value = LoginState.Error("Semua data harus diisi")
            return
        }

        _loginState.value = LoginState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid

                    // Membuat objek data user
                    val userProfile = hashMapOf(
                        "uid" to userId,
                        "name" to name,
                        "email" to email,
                        "createdAt" to System.currentTimeMillis()
                    )

                    // Menyimpan ke Cloud Firestore
                    if (userId != null) {
                        db.collection("users").document(userId)
                            .set(userProfile)
                            .addOnSuccessListener {
                                _loginState.value = LoginState.Success
                            }
                            .addOnFailureListener { e ->
                                _loginState.value = LoginState.Error("Gagal simpan profil: ${e.message}")
                            }
                    }
                } else {
                    _loginState.value = LoginState.Error(task.exception?.message ?: "Gagal mendaftar")
                }
            }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}