package com.example.splitku.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitku.data.local.AppDatabase
import com.example.splitku.data.local.entity.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Menggunakan AndroidViewModel agar punya akses ke application context
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Inisialisasi DAO Room
    private val userDao = AppDatabase.getDatabase(application).userDao()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        checkActiveSession()
    }
    private fun checkActiveSession() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Jika user sudah pernah login sebelumnya, langsung arahkan ke Dashboard
            _loginState.value = LoginState.Success
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("Email dan Password harus diisi")
            return
        }
        _loginState.value = LoginState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Sinkronisasi: Ambil data dari Firestore lalu simpan ke Room
                        syncUserFromFirestore(userId)
                    }
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
                    if (userId != null) {

                        db.collection("users")
                            .get()

                            .addOnSuccessListener { documents ->

                                val totalUsers = documents.size()

                                val customUserId =
                                    "USR-" + String.format("%04d", totalUsers + 1)

                                val userProfile = hashMapOf(

                                    "uid" to userId,
                                    "userId" to customUserId,
                                    "name" to name,
                                    "email" to email,
                                    "createdAt" to System.currentTimeMillis()
                                )

                                db.collection("users")
                                    .document(userId)
                                    .set(userProfile)

                                    .addOnSuccessListener {

                                        viewModelScope.launch {

                                            userDao.insertUser(

                                                UserEntity(
                                                    uid = userId,
                                                    userId = customUserId,
                                                    name = name,
                                                    email = email,
                                                    createdAt = System.currentTimeMillis()
                                                )
                                            )

                                            _loginState.value =
                                                LoginState.Success
                                        }
                                    }

                                    .addOnFailureListener { e ->

                                        _loginState.value =
                                            LoginState.Error(
                                                "Gagal simpan profil: ${e.message}"
                                            )
                                    }
                            }


                    }

                } else {
                    _loginState.value = LoginState.Error(task.exception?.message ?: "Gagal mendaftar")
                }
            }
    }

    private fun syncUserFromFirestore(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.getString("name") ?: ""
                    val email = document.getString("email") ?: ""

                    val customUserId =
                        document.getString("userId") ?: ""

                    val createdAt = document.getLong("createdAt") ?: 0L

                    viewModelScope.launch {
                        userDao.insertUser(

                            UserEntity(
                                uid = userId,
                                userId = customUserId,
                                name = name,
                                email = email,
                                createdAt = createdAt
                            )
                        )
                        _loginState.value = LoginState.Success
                    }
                }
            }
    }

    suspend fun getCurrentUser(): UserEntity? {

        val firebaseUser = auth.currentUser

        return if (firebaseUser != null) {
            userDao.getCurrentUser(firebaseUser.uid)
        } else {
            null
        }
    }

    fun logout() {
        auth.signOut() // Memutuskan sesi Firebase

        viewModelScope.launch {
            userDao.clearAllUsers()
        }


        _loginState.value = LoginState.Idle // Mengembalikan status ke Idle (kembali ke layar login)
    }

    fun resetPassword(email: String) {
        if (email.isEmpty()) {
            _loginState.value = LoginState.Error("Email tidak boleh kosong")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Kita bisa menggunakan state khusus atau sekadar toast di UI
                    _loginState.value = LoginState.Idle // Reset state agar tidak loading terus
                } else {
                    _loginState.value = LoginState.Error(task.exception?.message ?: "Gagal mengirim email reset")
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