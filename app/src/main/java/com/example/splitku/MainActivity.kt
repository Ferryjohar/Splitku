package com.example.splitku

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect // <-- Ini yang akan menyelesaikan masalah
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitku.ui.DashboardScreen
import com.example.splitku.ui.ForgotPasswordScreen
import com.example.splitku.ui.LoginScreen
import com.example.splitku.ui.RegisterScreen
import com.example.splitku.viewmodel.AuthViewModel
import com.example.splitku.viewmodel.LoginState
import com.example.splitku.data.local.AppDatabase
import com.example.splitku.viewmodel.DashboardViewModel
import com.example.splitku.viewmodel.DashboardViewModelFactory
import com.example.splitku.ui.CreateGroupScreen
import com.example.splitku.ui.JoinGroupScreen
import com.example.splitku.viewmodel.ProfileViewModel
import com.example.splitku.ui.ProfileScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current

                    val application = context.applicationContext as Application
                    val authViewModel: AuthViewModel = viewModel(
                        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                    )
                    val database = AppDatabase.getDatabase(context)
                    val dashboardViewModel: DashboardViewModel = viewModel(
                        factory = DashboardViewModelFactory(
                            database.groupDao()
                        ))
                    //profile data bisa di simpan
                    val profileViewModel: ProfileViewModel = viewModel()

                    val loginState by authViewModel.loginState.collectAsState()

                    var savedName by remember {
                        mutableStateOf("")
                    }

                    var currentScreen by remember {
                        mutableStateOf(
                            if (authViewModel.loginState.value is LoginState.Success) "dashboard" else "login"
                        )
                    }

                    // INI PERBAIKANNYA: Menggunakan LaunchedEffect agar tidak terjadi "pemaksaan" state
                    LaunchedEffect(loginState) {
                        when (val state = loginState) {
                            is LoginState.Success -> {
                                currentScreen = "dashboard"
                            }
                            is LoginState.Idle -> {
                                // Hanya pindah ke login otomatis JIKA posisinya sedang di dashboard (sedang logout)
                                if (currentScreen == "dashboard") {
                                    currentScreen = "login"
                                }
                            }
                            is LoginState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            }
                            else -> { /* Loading */ }
                        }
                    }

                    // Logika Navigasi Antar Layar
                    if (currentScreen == "login") {
                        LoginScreen(
                            onLoginClick = { email, password ->

                                authViewModel.login(email, password)

                                profileViewModel.setProfileData(

                                    email = email,
                                    password = password
                                )
                            },
                            onGoogleLoginClick = { Toast.makeText(context, "Google Login diklik", Toast.LENGTH_SHORT).show() },
                            onRegisterClick = { currentScreen = "register" }, // Sekarang ini pasti berhasil merubah layar!
                            onForgotPasswordClick = { currentScreen = "forgot_password" }
                        )
                    } else if (currentScreen == "forgot_password") {
                        ForgotPasswordScreen(
                            onSendResetClick = { email ->
                                authViewModel.resetPassword(email)
                                Toast.makeText(context, "Cek email Anda untuk reset password", Toast.LENGTH_LONG).show()
                                currentScreen = "login" // Balik ke login setelah kirim
                            },
                            onBackToLoginClick = { currentScreen = "login" }
                        )
                    } else if (currentScreen == "register") {
                        RegisterScreen(
                            onRegisterClick = { name, email, password ->
                                savedName = name

                                authViewModel.register(name, email, password)

                                profileViewModel.setProfileData(
                                    name = name,
                                    email = email,
                                    password = password
                                )
                            },
                            onLoginClick = { currentScreen = "login" }
                        )
                    } else if (currentScreen == "dashboard") {
                        DashboardScreen(
                            viewModel = dashboardViewModel,
                            onLogoutClick = {
                                authViewModel.logout()
                            }
                            ,
                            onAddGroupClick = {
                                currentScreen = "create_group"
                            }
                            ,
                            onJoinGroupClick = {
                                currentScreen = Screen.JOIN_GROUP
                            },
                            currentScreen = currentScreen,
                            onNavigate = { screen ->
                                currentScreen = screen
                            }
                        )
                    }

                    else if (currentScreen == "create_group") {
                        CreateGroupScreen(
                            viewModel = dashboardViewModel,
                            onBackClick = {
                                currentScreen = "dashboard"
                            }
                        )
                    }
                    else if (currentScreen == "join_group") {
                        JoinGroupScreen(
                            onBackClick = {
                                currentScreen = "dashboard"
                            }
                        )
                    }

                    //TAMBAH
                    else if (currentScreen == Screen.ACCOUNT) {

                        val name by profileViewModel.name.collectAsState()
                        val email by profileViewModel.email.collectAsState()
                        val password by profileViewModel.password.collectAsState()
                        val userId by profileViewModel.userId.collectAsState()

                        ProfileScreen(
                            name = name,
                            email = email,
                            password = password,
                            userId = userId,
                            onLogoutClick = {
                                authViewModel.logout()
                            },
                            currentScreen = currentScreen,
                            onNavigate = { screen ->
                                currentScreen = screen
                            }
                        )
                    }

                }
            }
        }
    }
}