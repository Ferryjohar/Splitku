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
import com.example.splitku.ui.InviteQrScreen
import com.example.splitku.ui.JoinGroupScreen
class MainActivity : ComponentActivity() {
    // fix: inisialisasi database di level Activity, bukan di dalam setContent
    // supaya tidak re-create setiap recompose
    private val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current

//                    val application = context.applicationContext as Application
                    val authViewModel: AuthViewModel = viewModel(
                        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                    )
//                    val database = AppDatabase.getDatabase(context)
                    val dashboardViewModel: DashboardViewModel = viewModel(
                        factory = DashboardViewModelFactory(
                            database.groupDao()
                        ))

                    val loginState by authViewModel.loginState.collectAsState()

                    // untuk kode qr
                    var currentScreen by remember { mutableStateOf("login") }
                    var inviteCode by remember { mutableStateOf<String?>(null) }

                    // Reaksi terhadap perubahan login state
                    LaunchedEffect(loginState) {
                        when (loginState) {
                            is LoginState.Success -> {
                                currentScreen = Screen.DASHBOARD
                            }

                            is LoginState.Error -> {
                                Toast.makeText(
                                    context,
                                    (loginState as LoginState.Error).message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            is LoginState.Idle -> {
                                currentScreen = Screen.LOGIN
                            }

                            else -> {}
                        }
                    }
                    // ---- Navigasi ----
                    when (currentScreen) {

                        Screen.LOGIN -> LoginScreen(
                            onLoginClick = { email, password ->
                                authViewModel.login(email, password)
                            },
                            onGoogleLoginClick = {
                                Toast.makeText(context, "Google Login diklik", Toast.LENGTH_SHORT).show()
                            },
                            onRegisterClick = { currentScreen = Screen.REGISTER },
                            onForgotPasswordClick = { currentScreen = Screen.FORGOT }
                        )

                        Screen.FORGOT -> ForgotPasswordScreen(
                            onSendResetClick = { email ->
                                authViewModel.resetPassword(email)
                                Toast.makeText(context, "Cek email untuk reset password", Toast.LENGTH_LONG).show()
                                currentScreen = Screen.LOGIN
                            },
                            onBackToLoginClick = { currentScreen = Screen.LOGIN }
                        )

                        Screen.REGISTER -> RegisterScreen(
                            onRegisterClick = { name, email, password ->
                                authViewModel.register(name, email, password)
                            },
                            onLoginClick = { currentScreen = Screen.LOGIN }
                        )

                        Screen.DASHBOARD -> DashboardScreen(
                            viewModel = dashboardViewModel,
                            onLogoutClick = { authViewModel.logout() },
                            onAddGroupClick = { currentScreen = Screen.CREATE_GROUP },
                            onJoinGroupClick = { currentScreen = Screen.JOIN_GROUP },
                            onNavigate = { screen -> currentScreen = screen },
                            currentScreen = currentScreen
                        )

                        Screen.CREATE_GROUP -> CreateGroupScreen(
                            viewModel = dashboardViewModel,
                            onBackClick = { currentScreen = Screen.DASHBOARD },
                            onGroupCreated = { code ->
                                inviteCode = code
                                currentScreen = Screen.INVITE_QR
                            }
                        )

                        Screen.INVITE_QR -> InviteQrScreen(
                            inviteCode = inviteCode ?: "",
                            onBackClick = { currentScreen = Screen.DASHBOARD } // fix: user bisa balik
                        )

                        Screen.JOIN_GROUP -> JoinGroupScreen(
                            onBackClick = { currentScreen = Screen.DASHBOARD }
                        )
                    }
                }
            }
        }
    }
}