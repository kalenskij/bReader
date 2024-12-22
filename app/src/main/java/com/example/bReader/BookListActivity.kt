package com.example.bReader

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch

import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*





class BookListActivity : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var bookRecyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private val bookList = mutableListOf<Book>()
    private val filteredBookList = mutableListOf<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)
        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)

        // Set the settings button click listener
        settingsButton.setOnClickListener {
            showSettingsDialog()
        }
        // Initialize Firebase Database reference
        databaseRef = FirebaseDatabase.getInstance().getReference("books")

        // Set up RecyclerView
        bookRecyclerView = findViewById(R.id.recyclerView)
        bookRecyclerView.layoutManager = LinearLayoutManager(this)
        bookAdapter = BookAdapter(filteredBookList, applicationContext)
        bookRecyclerView.adapter = bookAdapter
        val window = window
        window.statusBarColor = resources.getColor(R.color.status_bar_color, null)

        // Set up SearchView for searching books
        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission if needed
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the books based on the search query
                filterBooks(newText)
                return true
            }
        })

        // Retrieve book data from Firebase
        loadBooksFromFirebase()
    }

    private fun showSettingsDialog() {

        val switch = Switch(this)
        switch.isChecked = isDarkModeEnabled()

        // Create an AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setTitle("Settings")
            .setMessage("Toggle Dark Mode")
            .setView(switch) // Set the Switch inside the dialog
            .setPositiveButton("OK") { _, _ ->
                // When OK is clicked, apply the dark mode setting
                if (switch.isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show()
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    Toast.makeText(this, "Dark Mode Disabled", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null) // Simply dismiss on cancel
            .create()

        // Show the dialog
        dialog.show()
  
    }

    private fun isDarkModeEnabled(): Boolean {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun loadBooksFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                bookList.clear()
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null) {
                        bookList.add(book)
                    }
                }

                // Initially display all books in the RecyclerView
                filteredBookList.clear()
                filteredBookList.addAll(bookList)
                bookAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BookListActivity, "Failed to load books.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterBooks(query: String?) {
        // Clear the filtered list
        filteredBookList.clear()

        // If there is a query, filter the books based on title or author
        if (query != null && query.isNotEmpty()) {
            val lowerCaseQuery = query.toLowerCase()
            for (book in bookList) {
                if (book.title.toLowerCase().contains(lowerCaseQuery) ||
                    book.author.toLowerCase().contains(lowerCaseQuery)) {
                    filteredBookList.add(book)
                }
            }
        } else {
            // If query is empty, display all books
            filteredBookList.addAll(bookList)
        }

        // Notify the adapter that the data has changed
        bookAdapter.notifyDataSetChanged()
    }
}



//class BookListActivity : AppCompatActivity() {
//
//    private lateinit var databaseRef: DatabaseReference
//    private lateinit var bookRecyclerView: RecyclerView
//    private lateinit var bookAdapter: BookAdapter
//    private val bookList = mutableListOf<Book>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_book_list)
//        val window = window
//        window.statusBarColor = resources.getColor(R.color.status_bar_color, null)
//
//        // Initialize Firebase Database reference
//        databaseRef = FirebaseDatabase.getInstance().getReference("books")
//
//        // Set up RecyclerView
//        bookRecyclerView = findViewById(R.id.recyclerView)
//        bookRecyclerView.layoutManager = LinearLayoutManager(this)
//        bookAdapter = BookAdapter(bookList,applicationContext)
//        bookRecyclerView.adapter = bookAdapter
//
//        // Retrieve book data from Firebase
//        loadBooksFromFirebase()
//    }
//
//    private fun loadBooksFromFirebase() {
//        databaseRef.addValueEventListener(object : ValueEventListener {
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onDataChange(snapshot: DataSnapshot) {
//                bookList.clear()
//                for (bookSnapshot in snapshot.children) {
//                    val book = bookSnapshot.getValue(Book::class.java)
//                    if (book != null) {
//                        bookList.add(book)
//                    }
//                }
//                bookAdapter.notifyDataSetChanged()  // Update the RecyclerView
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@BookListActivity, "Failed to load books.", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//}
