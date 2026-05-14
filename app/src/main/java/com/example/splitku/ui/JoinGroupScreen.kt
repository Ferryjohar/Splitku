package com.example.splitku.ui

import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.widget.Toast
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinGroupScreen(
    onBackClick: () -> Unit
) {
    var inviteCode by remember {
        mutableStateOf("")
    }
    val qrScannerLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->

        if (result.contents != null) {

            inviteCode = result.contents
        }
    }
    val context = LocalContext.current
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                val options = ScanOptions().apply {

                    setPrompt("Scan QR Code Grup")
                    setBeepEnabled(true)
                    setOrientationLocked(false)
                }
                qrScannerLauncher.launch(options)
            } else {
                Toast.makeText(
                    context,
                    "Izin kamera ditolak. Gunakan kode manual.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gabung Grup",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(
                text = "Punya Kode Undangan?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C64F2)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Masukkan 6 digit kode yang dibagikan oleh temanmu.",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = inviteCode,
                onValueChange = {
                    inviteCode = it
                },
                placeholder = {
                    Text(
                        text = "ABC123",
                        color = Color.Gray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = {
                    val hasCamera = context.packageManager
                        .hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
                    if (!hasCamera) {

                        Toast.makeText(
                            context,
                            "Device tidak memiliki kamera. Gunakan kode manual.",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@OutlinedButton
                    }
                    val permissionGranted =
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                    if (permissionGranted) {

                        val options = ScanOptions().apply {
                            setPrompt("Scan QR Code Grup")
                            setBeepEnabled(true)
                            setOrientationLocked(false)
                        }

                        qrScannerLauncher.launch(options)

                    } else {
                        cameraPermissionLauncher.launch(
                            android.Manifest.permission.CAMERA
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Scan Qr Code",
                    color = Color(0xFF1C64F2)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1C64F2)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gabung ke Grup")
            }
        }
    }
}