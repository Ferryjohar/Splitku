package com.example.splitku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // State error diset true untuk mendemonstrasikan UI sesuai gambar Anda
    var showError by remember { mutableStateOf(false) }

    val bluePrimary = Color(0xFF1D4ED8) // Menyerupai biru pada gambar

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color.Black),
                    radius = 1500f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo & Header
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(bluePrimary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Gunakan icon dompet dari drawable jika ada (misal: R.drawable.ic_wallet)
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_myplaces), // Placeholder
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "SplitKu",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = bluePrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Atur keuangan bareng temen makin gampang.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("contoh@mahasiswa.ac.id") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        val icon = if (passwordVisible) android.R.drawable.ic_menu_view else android.R.drawable.ic_secure
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(painterResource(id = icon), "Toggle Password Visibility")
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.height(12.dp))

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
                            text = "Email atau password salah. Silakan coba lagi.",
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Lupa Password
                Text(
                    text = "Lupa Password?",
                    color = bluePrimary,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { /* Handle Forgot Password */ }
                        .padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Login Button
                Button(
                    onClick = { onLoginClick(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = bluePrimary)
                ) {
                    Text("Login", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Divider "atau"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    Text("atau", modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray, fontSize = 12.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Google Login Button
                OutlinedButton(
                    onClick = onGoogleLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF9FAFB))
                ) {
                    // Gunakan icon Google dari drawable
                    // Icon(painter = painterResource(id = R.drawable.ic_google), contentDescription = "Google Logo")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Masuk dengan Google", color = Color.DarkGray)
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Register Link
                Row {
                    Text("Belum punya akun? ", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        text = "Daftar",
                        color = bluePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onRegisterClick() }
                    )
                }
            }
        }
    }
}