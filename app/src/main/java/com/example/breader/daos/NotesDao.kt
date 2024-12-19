package com.example.breader.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.breader.entities.BookmarksEntity
import com.example.breader.entities.NotesEntity

@Dao
interface NotesDao {

    @Query("SELECT * from notes WHERE bookId == :bookId")
    suspend fun getNotes(bookId: String): List<NotesEntity>

    @Insert
    suspend fun addNote(entity: NotesEntity)

}