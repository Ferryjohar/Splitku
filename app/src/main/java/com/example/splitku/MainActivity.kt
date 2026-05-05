package com.example.splitku

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
// Pastikan import LoginScreen dan AuthViewModel Anda sesuai dengan folder yang dibuat
import com.example.splitku.ui.LoginScreen
import com.example.splitku.ui.RegisterScreen
import com.example.splitku.viewmodel.AuthViewModel
import com.example.splitku.viewmodel.LoginState


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Jika Anda punya theme bawaan (misal SplitKuTheme), gunakan itu.
            // Di sini kita gunakan MaterialTheme standar sebagai contoh.
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Inisialisasi ViewModel
                    val authViewModel: AuthViewModel = viewModel()
                    // Pantau status login
                    val loginState by authViewModel.loginState.collectAsState()
                    val context = LocalContext.current

                    // Reaksi terhadap status login
                    when (loginState) {
                        is LoginState.Success -> {
                            Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                            // Nanti di sini kita buat navigasi pindah ke Dashboard
                        }
                        is LoginState.Error -> {
                            val errorMessage = (loginState as LoginState.Error).message
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }
                        else -> { /* Idle atau Loading, biarkan UI Login yang menangani */ }
                    }

                    // Tampilkan Halaman Login
                    LoginScreen(
                        onLoginClick = { email, password ->
                            // Panggil fungsi login di ViewModel saat tombol diklik
                            authViewModel.login(email, password)
                        },
                        onGoogleLoginClick = {
                            // TODO: Nanti kita tambahkan logika Google Sign In di sini
                            Toast.makeText(context, "Google Login diklik", Toast.LENGTH_SHORT).show()
                        },
                        onRegisterClick = {
                            // TODO: Nanti kita tambahkan navigasi ke halaman Register
                            Toast.makeText(context, "Ke halaman daftar", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    val authViewModel: AuthViewModel = viewModel()
                    val loginState by authViewModel.loginState.collectAsState()
                    val context = LocalContext.current

                    // Variabel state untuk menentukan layar mana yang sedang aktif
                    var currentScreen by remember { mutableStateOf("login") }

                    when (loginState) {
                        is LoginState.Success -> {
                            Toast.makeText(context, "Berhasil masuk/daftar!", Toast.LENGTH_SHORT).show()
                        }
                        is LoginState.Error -> {
                            val errorMessage = (loginState as LoginState.Error).message
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }
                        else -> { }
                    }

                    // Logika untuk menampilkan layar berdasarkan 'currentScreen'
                    if (currentScreen == "login") {
                        LoginScreen(
                            onLoginClick = { email, password -> authViewModel.login(email, password) },
                            onGoogleLoginClick = { /* Logika Google */ },
                            onRegisterClick = {
                                // Jika tulisan "Daftar" diklik, ubah layar ke register
                                currentScreen = "register"
                            }
                        )
                    } else if (currentScreen == "register") {
                        RegisterScreen(
                            onRegisterClick = { name, email, password ->
                                // Panggil fungsi register di ViewModel
                                authViewModel.register(name, email, password)
                            },
                            onLoginClick = {
                                // Jika tulisan "Masuk" diklik, kembali ke login
                                currentScreen = "login"
                            }
                        )
                    }
                }
            }
        }
    }
}