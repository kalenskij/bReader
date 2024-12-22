package com.example.bReader

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BorrowedBookActivity : AppCompatActivity() {
    private lateinit var borrowedBooksRecyclerView: RecyclerView
    private lateinit var borrowedBooksAdapter: BorrowedBooksAdapter
    private val borrowedBooksList = mutableListOf<BorrowedBook>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrowed_book)

        borrowedBooksRecyclerView = findViewById(R.id.recyclerViewBorrowedBooks)
        borrowedBooksAdapter = BorrowedBooksAdapter(borrowedBooksList)
        borrowedBooksRecyclerView.layoutManager = LinearLayoutManager(this)
        borrowedBooksRecyclerView.adapter = borrowedBooksAdapter

        loadBorrowedBooks()
    }

    private fun loadBorrowedBooks() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val borrowedBooksRef = database.reference.child("borrowed_books")

        currentUser?.uid?.let { userId ->
            borrowedBooksRef.child(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    borrowedBooksList.clear()
                    for (childSnapshot in snapshot.children) {
                        val bookTitle = childSnapshot.child("book_title").value.toString()
                        val borrowedBy = childSnapshot.child("borrowed_by").value.toString()

                        borrowedBooksList.add(BorrowedBook(bookTitle, borrowedBy))
                    }
                    borrowedBooksAdapter.notifyDataSetChanged()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load borrowed books: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
