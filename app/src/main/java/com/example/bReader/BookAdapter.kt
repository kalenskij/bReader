package com.example.bReader


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BookAdapter(private val bookList: List<Book>, private val context: Context) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private val sharedPreferences = context.getSharedPreferences("borrowed_books", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]

        // Set up the title and author as usual
        holder.titleTextView.text = book.title
        holder.authorTextView.text = book.author
        holder.quantityTextView.text = "Available: ${book.quantity}"
        Glide.with(holder.itemView.context).load(book.imageUrl).into(holder.coverImageView)


        if (book.quantity < 1) {
            holder.quantityTextView.setTextColor(Color.RED)
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
            holder.quantityTextView.setText("No books are available at the moment")

        } else {
            holder.quantityTextView.setTextColor(ContextCompat.getColor(context,R.color.deep_green))
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
        }
        // Check if the book is borrowed and update button text
        isBookBorrowed(book) { isBorrowed ->
            if (isBorrowed) {
                holder.borrowButton.text = "Return Book"
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green))
            } else {
                holder.borrowButton.text = "Borrow Book"
            }
        }



        // Set onClickListener for "Borrow/Return" button
        holder.borrowButton.setOnClickListener {
            isBookBorrowed(book) { isBorrowed ->
                // Return the book and update the UI
                if (isBorrowed) {
                    returnBook(book, holder)
                } else {
                    // Check if there are copies available before allowing borrowing
                    if (book.quantity > 0) {
                        // Borrow the book and update the UI
                        borrowBook(book, holder)
                    } else {
                        Toast.makeText(context, "No Books available to borrow", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        // Set onClickListener for item view to navigate to PdfViewerActivity (Only if the book is borrowed)
        holder.itemView.setOnClickListener {
            isBookBorrowed(book) { isBorrowed ->
                if (isBorrowed) {
                    val intent = Intent(context, PdfViewerActivity::class.java)
                    val pdfUrl = book.pdfUrl  // Ensure this is not null
                    if (pdfUrl.isNotEmpty()) {
                        intent.putExtra("pdf_url", pdfUrl)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "PDF URL is missing", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "You must borrow the book first to read it.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Method to borrow a book
    private fun borrowBook(book: Book, holder: BookViewHolder) {
        // Decrease the quantity in Firebase
        updateQuantityInFirebase(book, book.quantity - 1)

        // Mark as borrowed and save to SharedPreferences
        markBookAsBorrowed(book)
        saveBorrowedBookToFirebase(book)

        // Update button text and show Toast
        holder.borrowButton.text = "Return Book"
        Toast.makeText(context, "You have borrowed the book: ${book.title}", Toast.LENGTH_SHORT).show()
    }

    // Method to return a borrowed book
    private fun returnBook(book: Book, holder: BookViewHolder) {
        // Increase the quantity in Firebase
        updateQuantityInFirebase(book, book.quantity + 1)

         unmarkBookAsBorrowed(book)

         removeBorrowedBookFromFirebase(book)

        holder.borrowButton.text = "Borrow Book"
        Toast.makeText(context, "You have returned the book: ${book.title}", Toast.LENGTH_SHORT).show()
    }

    private fun updateQuantityInFirebase(book: Book, newQuantity: Long) {
        val database = FirebaseDatabase.getInstance()
        val bookRef = database.reference.child("books").orderByChild("title").equalTo(book.title).limitToFirst(1)

        bookRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val bookKey = snapshot.children.first().key ?: return@addOnSuccessListener
                database.reference.child("books").child(bookKey).child("quantity").setValue(newQuantity)
                    .addOnSuccessListener {
                        // Quantity successfully updated
                        Toast.makeText(context, "Quantity updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Failed to update quantity: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Failed to fetch book data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to save borrowed book to Firebase
    private fun saveBorrowedBookToFirebase(book: Book) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email
        val uid = currentUser?.uid

        if (email == null) {
            Toast.makeText(context, "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Extract the username (before @gmail.com)
        val userName = email.split("@").get(0)

        // Get a reference to the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        val borrowedBooksRef = database.reference.child("borrowed_books")

        val borrowedBook = mapOf(
            "book_title" to book.title,
            "borrowed_by" to userName
        )

        currentUser.uid?.let { userId ->
            borrowedBooksRef.child(userId).child(book.title).setValue(borrowedBook)
                .addOnSuccessListener {
                    Toast.makeText(context, "Book saved to borrowed list.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to save borrowed book: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Method to remove borrowed book from Firebase
    private fun removeBorrowedBookFromFirebase(book: Book) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val borrowedBooksRef = database.reference.child("borrowed_books")

        // Remove the book from the borrowed books list in Firebase
        currentUser?.uid?.let { userId ->
            borrowedBooksRef.child(userId).child(book.title).removeValue()
                .addOnSuccessListener {
                    // Successfully removed from Firebase
                    Toast.makeText(context, "Book returned successfully.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    // Failed to remove from Firebase
                    Toast.makeText(context, "Failed to return book: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Method to check if the book is borrowed (using SharedPreferences)
    private fun isBookBorrowed(book: Book, callback: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val borrowedBooksRef = database.reference.child("borrowed_books")

        currentUser?.uid?.let { userId ->
            borrowedBooksRef.child(userId).child(book.title).get().addOnSuccessListener { snapshot ->
                callback(snapshot.exists())
            }.addOnFailureListener {
                callback(false)
            }
        }
    }

    // Method to mark the book as borrowed
    private fun markBookAsBorrowed(book: Book) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(book.title, true)
        editor.apply()
    }

    // Method to unmark the book as borrowed
    private fun unmarkBookAsBorrowed(book: Book) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(book.title, false)
        editor.apply()
    }

    override fun getItemCount(): Int = bookList.size

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coverImageView: ImageView = itemView.findViewById(R.id.bookCoverImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.bookTitleTextView)
        val authorTextView: TextView = itemView.findViewById(R.id.bookAuthorTextView)
        val borrowButton: Button = itemView.findViewById(R.id.borrowButton)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
        val itemBg:LinearLayout=itemView.findViewById(R.id.item_bg)
    }
}






