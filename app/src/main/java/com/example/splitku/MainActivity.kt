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
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.activity.compose.BackHandler
import com.example.splitku.ui.QrScreen

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

                    // Profile data bisa disimpan
                    val profileViewModel: ProfileViewModel = viewModel()

                    // 1. Konfigurasi Google Sign In (Sudah dipindahkan ke dalam scope yang benar)
                    val gso = remember {
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                    }

                    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

                    // 2. Launcher untuk menangkap hasil pop-up Google
                    val googleSignInLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                            try {
                                val account = task.getResult(ApiException::class.java)
                                account.idToken?.let { token ->
                                    // Jika berhasil dapat token, kirim ke ViewModel
                                    authViewModel.firebaseAuthWithGoogle(token)
                                }
                            } catch (e: ApiException) {
                                Toast.makeText(context, "Google Sign-In gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    // Tambahan dari room database agar profilviewmodel diisi otomatis dari room
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

                    // Invite dengan kode
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

                    // LaunchedEffect agar tidak terjadi "pemaksaan" state
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

                    // Mencegat tombol back bawaan HP
                    BackHandler(
                        // BackHandler hanya aktif jika kita BUKAN di layar Login atau Dashboard
                        enabled = currentScreen != Screen.LOGIN && currentScreen != Screen.DASHBOARD
                    ) {
                        // Atur ke mana aplikasi harus kembali saat tombol back ditekan
                        currentScreen = when (currentScreen) {
                            Screen.FORGOT -> Screen.LOGIN
                            Screen.REGISTER -> Screen.LOGIN
                            Screen.CREATE_GROUP -> Screen.DASHBOARD
                            Screen.JOIN_GROUP -> Screen.DASHBOARD
                            Screen.INVITE_QR -> Screen.DASHBOARD
                            Screen.ACCOUNT -> Screen.DASHBOARD
                            else -> currentScreen
                        }
                    }

                    when (currentScreen) {
                        Screen.LOGIN -> {
                            LoginScreen(
                                onLoginClick = { email, password ->
                                    authViewModel.login(email, password)
                                },
                                onGoogleLoginClick = {
                                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
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
                                    authViewModel.resetPassword(email) { isSuccess, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                        if (isSuccess) {
                                            currentScreen = Screen.LOGIN
                                        }
                                    }
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
                                profileViewModel = profileViewModel,
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

                        Screen.QR -> {
                            val name by profileViewModel.name.collectAsState()
                            QrScreen(
                                username = name,
                                onBackClick = {
                                    currentScreen = Screen.ACCOUNT
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}