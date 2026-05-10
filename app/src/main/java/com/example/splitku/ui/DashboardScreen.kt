package com.example.splitku.ui

import android.R.attr.title
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitku.Screen
import com.example.splitku.data.local.entity.GroupEntity
import com.example.splitku.ui.components.BottomNavBar
import com.example.splitku.viewmodel.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.splitku.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    profileViewModel: ProfileViewModel,
    onLogoutClick: () -> Unit,
    onJoinGroupClick: () -> Unit,
    onAddGroupClick: () -> Unit,
    currentScreen: String,
    onNavigate: (String) -> Unit

) {
    val groups by viewModel.groups.collectAsState()
    val name by profileViewModel.name.collectAsState() //ambil nama user

    Scaffold(
        containerColor = Color(0xFFF9FAFB), // Warna background abu-abu sangat muda
        bottomBar = {
            BottomNavBar(
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAddGroupClick()
                },
                containerColor = Color(0xFFE5E7EB),
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // 1. Top Bar Custom
            TopSection(
                userName = name

            )
            Spacer(modifier = Modifier.height(24.dp))

            // 2. Card Total Saldo
            BalanceCard()
            Spacer(modifier = Modifier.height(16.dp))

            // 3. Card Gabung Grup
            JoinGroupCard(
                onClick = {
                    onJoinGroupClick()
                }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 4. Header Grup Aktif
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Grup Aktif",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151)
                )
                TextButton(onClick = { /* TODO: Lihat Semua */ }) {
                    Text(
                        text = "Lihat Semua",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C64F2)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            groups.forEach { group ->
                GroupItemCard(
                    title = group.groupName,
                    members = 0,
                    total = "Rp 0",
                    colorPlaceholder = Color(0xFF8B5A2B),

                    onDeleteClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deleteGroup(group)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Tombol logout sementara (bisa Anda pindahkan ke halaman Account nantinya)
            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout Sementara", color = Color.Red)
            }
        }
    }
}

@Composable
fun TopSection(
    userName: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Placeholder untuk Foto Profil
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Halo! ${userName.split(" ").firstOrNull() ?: ""}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF374151)
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifikasi",
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "SplitKu",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1C64F2)
        )
    }
}

@Composable
fun BalanceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C64F2)) // Warna Biru
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Total Saldo",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rp 450.000",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                // Badge "Anda ditagih"
                Surface(
                    color = Color(0xFFA7F3D0), // Hijau muda
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowOutward,
                            contentDescription = null,
                            tint = Color(0xFF047857),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Anda ditagih",
                            color = Color(0xFF047857),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JoinGroupCard(
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFF1C64F2))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Gabung Grup",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1C64F2)
                )
                Text(
                    text = "Punya kode undangan? Masuk di sini",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Arrow", tint = Color.Gray)
        }
    }
}

@Composable
fun GroupItemCard(
    title: String,
    members: Int,
    total: String,
    colorPlaceholder: Color,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder Gambar Grup
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorPlaceholder)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF374151)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Members",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$members Anggota", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Total:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    text = total,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1C64F2)
                )
            }
        }
    }
}