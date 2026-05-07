package com.example.splitku.utils

fun generateInviteCode(lenght: Int = 6): String{
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    return (1..lenght)
        .map { chars.random() }
        .joinToString ("")
}