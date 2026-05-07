package com.example.splitku.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.widget.Toast
import com.example.splitku.utils.generatorQrBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteQrScreen(
    inviteCode: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Null-safe: kalau generatorQrBitmap gagal, tidak crash
    val qrBitmap = remember(inviteCode) {
        runCatching { generatorQrBitmap(inviteCode) }.getOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Undang Anggota") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bagikan QR Code atau kode undangan\nkepada anggota yang ingin bergabung",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // QR Code
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code undangan",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback kalau QR gagal di-generate
                    Text(
                        text = "QR tidak dapat dibuat",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Kode undangan
            Text(
                text = "Kode Undangan",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = inviteCode,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = Color(0xFF1C64F2),
                        letterSpacing = 4.sp
                    )
                    // Tombol copy kode
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(inviteCode))
                        Toast.makeText(context, "Kode disalin!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Salin kode",
                            tint = Color(0xFF1C64F2)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol share
            Button(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Gabung grup SplitKu ku! Gunakan kode: $inviteCode"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Bagikan kode"))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C64F2))
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Bagikan Kode")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tombol kembali ke dashboard
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Selesai")
            }
        }
    }
}