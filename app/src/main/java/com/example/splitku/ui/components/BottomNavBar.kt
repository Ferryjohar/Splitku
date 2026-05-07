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

@Composable
fun BottomNavBar(
    currentScreen: String,
    onNavigate: (String) -> Unit
){
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == "dashboard",
            onClick = { onNavigate("dashboard")},
            icon = {Icon(Icons.Default.Home, null)},
            label = { Text("Dashboard") }
        )
        NavigationBarItem(
            selected = currentScreen == "groups",
            onClick = {onNavigate("groups")},
            icon = {Icon(Icons.Default.Group, null)},
            label = {Text("Groups")}
        )
        NavigationBarItem(
            selected = currentScreen == "activity",
            onClick = {onNavigate("activity")},
            icon = {Icon(Icons.Default.DateRange, null)},
            label = {Text("Activity")}
        )
        NavigationBarItem(
            selected = currentScreen == "account",
            onClick = {onNavigate("account")},
            icon = {Icon(Icons.Default.Person, null)},
            label = {Text("Account")}
        )
    }
}

