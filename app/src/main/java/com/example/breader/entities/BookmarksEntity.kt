package com.example.breader.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarksEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val bookId: String,
    val pageNo: Int
)