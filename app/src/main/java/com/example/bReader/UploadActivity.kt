package com.example.bReader

import android.annotation.SuppressLint
import android.os.Bundle

import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import java.util.*

class UploadActivity : AppCompatActivity() {

    private lateinit var storageRef: StorageReference
    private lateinit var databaseRef: DatabaseReference

    private var pdfUri: Uri? = null
    private var imageUri: Uri? = null

    private lateinit var coverImageView: ImageView
    private lateinit var titleEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var quantityEditText: EditText

    // Activity result for selecting files
    private val imagePickLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        imageUri?.let {
            Glide.with(this).load(it).into(coverImageView)
        }
    }

    private val pdfPickLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        pdfUri = uri
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        val window = window
        window.statusBarColor = resources.getColor(R.color.status_bar_color, null)

        // Initialize Firebase
        storageRef = FirebaseStorage.getInstance().reference
        databaseRef = FirebaseDatabase.getInstance().getReference("books")

        // Bind UI components
        coverImageView = findViewById(R.id.coverImageView)
        titleEditText = findViewById(R.id.titleEditText)
        authorEditText = findViewById(R.id.authorEditText)
        quantityEditText = findViewById(R.id.quantityEditText)  // Bind the quantity EditText

        val uploadButton: Button = findViewById(R.id.uploadPdfButton)
        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        val brButton: Button = findViewById(R.id.br_books)

        brButton.setOnClickListener {
            val i = Intent(applicationContext, BorrowedBookActivity::class.java)
            startActivity(i)
        }

        // Upload the book to the database
        val uploadBookButton: Button = findViewById(R.id.uploadBookButton)

        uploadBookButton.setOnClickListener {
            uploadBook()
        }

        // Select book cover image
        selectImageButton.setOnClickListener {
            imagePickLauncher.launch("image/*")
        }

        // Select PDF file
        uploadButton.setOnClickListener {
            pdfPickLauncher.launch("application/pdf")
        }
    }

    // Upload book and its metadata to Firebase
    @SuppressLint("SuspiciousIndentation")
    private fun uploadBook() {
        val title = titleEditText.text.toString().trim()
        val author = authorEditText.text.toString().trim()
        val quantityString = quantityEditText.text.toString().trim()
        val quantity = if (quantityString.isNotEmpty()) quantityString.toInt() else 0

        if (title.isEmpty() || author.isEmpty() || pdfUri == null || imageUri == null || quantity <= 0) {
            Toast.makeText(this, "Please fill in all fields and select both PDF, image, and quantity.", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate unique file names for PDF and image
        val pdfFileName = UUID.randomUUID().toString() + ".pdf"
        val pdfRef = storageRef.child("books/$pdfFileName")

        val imageFileName = UUID.randomUUID().toString() + ".jpg"
        val imageRef = storageRef.child("books/images/$imageFileName")

        // Upload the PDF to Firebase Storage
        pdfRef.putFile(pdfUri!!).addOnSuccessListener { taskSnapshot ->
            pdfRef.downloadUrl.addOnSuccessListener { pdfDownloadUri ->
                // Upload the image to Firebase Storage
                imageRef.putFile(imageUri!!).addOnSuccessListener { imageTaskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { imageDownloadUri ->
                        // Save book metadata to Firebase Realtime Database
                        val book = Book(
                            title = title,
                            author = author,
                            pdfUrl = pdfDownloadUri.toString(),
                            imageUrl = imageDownloadUri.toString(),
                            quantity = quantity.toLong()
                        )

                        val bookId = databaseRef.push().key ?: return@addOnSuccessListener
                        databaseRef.child(bookId).setValue(book).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Book uploaded successfully!", Toast.LENGTH_SHORT).show()
                                clearFields()
                                val i = Intent(applicationContext, BookListActivity::class.java)
                                startActivity(i)
                            } else {
                                Toast.makeText(this, "Failed to upload book.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "PDF upload failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Clear fields after successful upload
    private fun clearFields() {
        titleEditText.text.clear()
        authorEditText.text.clear()
        quantityEditText.text.clear()
        coverImageView.setImageResource(0)
        pdfUri = null
        imageUri = null
    }
}
