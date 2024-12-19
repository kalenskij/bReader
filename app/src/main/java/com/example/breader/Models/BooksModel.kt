package com.example.breader.Models

import java.io.Serializable

data class BooksModel(
    val bookID: Int = 0,
    val image: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val bookPDF: String = "",
    val quantity: Int = 0,
):Serializable