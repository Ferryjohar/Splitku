package com.example.splitku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitku.ui.components.BottomNavBar
import androidx.compose.material.icons.filled.QrCode
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun ProfileScreen(

    email: String,
    password: String,
    userId: String,
    onLogoutClick: () -> Unit,
    currentScreen: String,
    onNavigate: (String) -> Unit
) {

    val bluePrimary = Color(0xFF2563EB)
    val context = LocalContext.current

    Scaffold(

        containerColor = Color(0xFFF5F7FB),

        bottomBar = {
            BottomNavBar(
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FB))
                .padding(paddingValues)
                .padding(20.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Profil Saya",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = bluePrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(28.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E7FF)),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = bluePrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))



                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (email.isEmpty()) "No Email" else email,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(
                        color = Color(0xFFE5E7EB)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ProfileItem(
                        title = "Password",
                        value = if (password.isEmpty()) {
                            "••••••••"
                        } else {
                            "•".repeat(password.length)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ProfileItem(
                        title = "User ID",
                        value = userId,
                        showQr = true,
                        onQrClick = {
                            Toast.makeText(
                                context,
                                "QR Clicked!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = onLogoutClick,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        shape = RoundedCornerShape(16.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                            tint = Color.White
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Logout",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItem(
    title: String,
    value: String,
    showQr: Boolean = false,
    onQrClick: () -> Unit = {}
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9FAFB)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = title,
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = value,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF111827)
                )
            }

            // ICON QR
            if (showQr) {

                IconButton(
                    onClick = onQrClick
                ) {

                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "QR",
                        tint = Color(0xFF2563EB)
                    )
                }
            }
        }
    }
}