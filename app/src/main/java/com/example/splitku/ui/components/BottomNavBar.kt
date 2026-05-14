package com.example.splitku.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.splitku.Screen

@Composable
fun BottomNavBar(
    currentScreen: String,
    onNavigate: (String) -> Unit
){
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == Screen.DASHBOARD,
            onClick = { onNavigate(Screen.DASHBOARD) },
            icon = {Icon(Icons.Default.Home, null)},
            label = { Text("Dashboard") }
        )
        NavigationBarItem(
            selected = currentScreen == Screen.GROUPS,
            onClick = {onNavigate(Screen.GROUPS)},
            icon = {Icon(Icons.Default.Group, null)},
            label = {Text("Groups")}
        )
        NavigationBarItem(
            selected = currentScreen == Screen.ACTIVITY,
            onClick = {onNavigate(Screen.ACTIVITY)},
            icon = {Icon(Icons.Default.DateRange, null)},
            label = {Text("Activity")}
        )
        NavigationBarItem(
            selected = currentScreen == Screen.ACCOUNT,
            onClick = {onNavigate(Screen.ACCOUNT)},
            icon = {Icon(Icons.Default.Person, null)},
            label = {Text("Account")}
        )
    }
}

