package com.example.splitku

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitku.data.local.AppDatabase
import com.example.splitku.ui.CreateGroupScreen
import com.example.splitku.ui.DashboardScreen
import com.example.splitku.ui.ForgotPasswordScreen
import com.example.splitku.ui.InviteQrScreen
import com.example.splitku.ui.JoinGroupScreen
import com.example.splitku.ui.LoginScreen
import com.example.splitku.ui.ProfileScreen
import com.example.splitku.ui.RegisterScreen
import com.example.splitku.viewmodel.AuthViewModel
import com.example.splitku.viewmodel.DashboardViewModel
import com.example.splitku.viewmodel.DashboardViewModelFactory
import com.example.splitku.viewmodel.LoginState
import com.example.splitku.viewmodel.ProfileViewModel

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

                    val authViewModel: AuthViewModel = viewModel(
                        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                    )
                    val database = AppDatabase.getDatabase(context)
                    val dashboardViewModel: DashboardViewModel = viewModel(
                        factory = DashboardViewModelFactory(
                            database.groupDao()
                        )
                    )

                    //profile data bisa di simpan
                    val profileViewModel: ProfileViewModel = viewModel()

                    //tambahan dari room database agar profilviewmodel diisi otomatis dari room
                    val loginState by authViewModel.loginState.collectAsState()

                    LaunchedEffect(loginState) {

                        if (loginState is LoginState.Success) {

                            val currentUser = authViewModel.getCurrentUser()

                            if (currentUser != null) {

                                profileViewModel.setProfileData(
                                    name = currentUser.name,
                                    email = currentUser.email,
                                    password = "",
                                    userId = currentUser.userId
                                )
                            }
                        }
                    }

                    // invite dengan kode
                    var inviteCode by remember {
                        mutableStateOf("")
                    }

                    var currentScreen by remember {
                        mutableStateOf(
                            if (authViewModel.loginState.value is LoginState.Success)
                                Screen.DASHBOARD
                            else
                                Screen.LOGIN
                        )
                    }

                    // INI PERBAIKANNYA: Menggunakan LaunchedEffect agar tidak terjadi "pemaksaan" state
                    LaunchedEffect(loginState) {
                        when (val state = loginState) {
                            is LoginState.Success -> {
                                currentScreen = Screen.DASHBOARD
                            }

                            is LoginState.Idle -> {
                                if (
                                    currentScreen == Screen.DASHBOARD ||
                                    currentScreen == Screen.ACCOUNT
                                ) {
                                    currentScreen = Screen.LOGIN
                                }
                            }

                            is LoginState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            }

                            else -> { /* Loading */
                            }
                        }
                    }

                    when (currentScreen) {

                        Screen.LOGIN -> {
                            LoginScreen(
                                onLoginClick = { email, password ->
                                    authViewModel.login(email, password)
                                },

                                onGoogleLoginClick = {
                                    Toast.makeText(
                                        context,
                                        "Google Login diklik",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onRegisterClick = {
                                    currentScreen = Screen.REGISTER
                                },
                                onForgotPasswordClick = {
                                    currentScreen = Screen.FORGOT
                                }
                            )
                        }

                        Screen.FORGOT -> {
                            ForgotPasswordScreen(
                                onSendResetClick = { email ->
                                    authViewModel.resetPassword(email)

                                    Toast.makeText(
                                        context,
                                        "Cek email Anda untuk reset password",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    currentScreen = Screen.LOGIN
                                },
                                onBackToLoginClick = {
                                    currentScreen = Screen.LOGIN
                                }
                            )
                        }

                        Screen.REGISTER -> {
                            RegisterScreen(
                                onRegisterClick = { name, email, password ->
                                    authViewModel.register(name, email, password)
                                },

                                onLoginClick = {
                                    currentScreen = Screen.LOGIN
                                }

                            )
                        }

                        Screen.DASHBOARD -> {
                            DashboardScreen(
                                viewModel = dashboardViewModel,
                                profileViewModel = profileViewModel,

                                onLogoutClick = {
                                    authViewModel.logout()
                                },

                                onAddGroupClick = {
                                    currentScreen = Screen.CREATE_GROUP
                                },

                                onJoinGroupClick = {
                                    currentScreen = Screen.JOIN_GROUP
                                },

                                currentScreen = currentScreen,

                                onNavigate = { screen ->
                                    currentScreen = screen
                                }
                            )
                        }

                        Screen.CREATE_GROUP -> {
                            CreateGroupScreen(
                                viewModel = dashboardViewModel,

                                onBackClick = {
                                    currentScreen = Screen.DASHBOARD
                                },

                                onGroupCreated = { code ->
                                    inviteCode = code
                                    currentScreen = Screen.INVITE_QR
                                }
                            )
                        }

                        Screen.JOIN_GROUP -> {
                            JoinGroupScreen(
                                onBackClick = {
                                    currentScreen = Screen.DASHBOARD
                                }
                            )
                        }

                        Screen.INVITE_QR -> {
                            InviteQrScreen(
                                inviteCode = inviteCode,

                                onBackClick = {
                                    currentScreen = Screen.DASHBOARD
                                }
                            )
                        }

                        Screen.ACCOUNT -> {

                            val email by profileViewModel.email.collectAsState()
                            val password by profileViewModel.password.collectAsState()
                            val userId by profileViewModel.userId.collectAsState()

                            ProfileScreen(

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
}