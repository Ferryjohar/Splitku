package com.example.splitku.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onGoogleLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) } // Set ke true untuk test UI error

    // Warna yang konsisten dengan RegisterScreen
    val bluePrimary = Color(0xFF1D4ED8)
    val bgTop = Color(0xFFBFDBFE) // Biru muda gradient atas
    val bgBottom = Color(0xFFF3F4F6) // Abu-abu muda gradient bawah

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(bgTop, bgBottom)
                )
            )
    ) {
        // Scrollable Column agar aman saat keyboard muncul
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Logo & Header (Berada di luar Card putih)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(bluePrimary, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_myplaces), // Placeholder logo
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Masuk ke SplitKu",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = bluePrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selamat datang kembali! Yuk lanjut catat dan pantau pengeluaranmu.",
                fontSize = 14.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Card Form Login
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Input Email
                    Text("Email", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("nama@email.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Input Password
                    Text("Kata Sandi", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Masukkan kata sandi") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            val icon = if (passwordVisible) android.R.drawable.ic_menu_view else android.R.drawable.ic_secure
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(painterResource(id = icon), "Toggle Password")
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    // Lupa Password Link
                    Text(
                        text = "Lupa Kata Sandi?",
                        color = bluePrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onForgotPasswordClick() } // HUBUNGKAN DI SINI
                            .padding(vertical = 12.dp)
                    )

                    // Error Message
                    if (showError) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "Error", tint = Color.Red, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Email atau kata sandi salah. Silakan coba lagi.",
                                color = Color(0xFFD32F2F),
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Tombol Login
                    Button(
                        onClick = { onLoginClick(email, password) },
                        enabled = email.isNotEmpty() && password.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = bluePrimary)
                    ) {
                        Text("Masuk", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Divider "atau"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
                        Text("atau", modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray, fontSize = 12.sp)
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Google Login Button
                    OutlinedButton(
                        onClick = onGoogleLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Text("Masuk dengan Google", color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Teks Navigasi ke Register (Dengan area klik lebar yang sudah kita perbaiki)
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onRegisterClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Belum punya akun? ", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = "Daftar Sekarang",
                    color = bluePrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}