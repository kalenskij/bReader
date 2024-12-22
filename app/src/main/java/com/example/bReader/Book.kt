package com.example.bReader

data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val pdfUrl: String = "",
    val imageUrl: String = "",
    var quantity: Long = 0
)
