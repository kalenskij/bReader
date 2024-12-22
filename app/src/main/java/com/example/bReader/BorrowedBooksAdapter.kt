package com.example.bReader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BorrowedBooksAdapter(private val borrowedBooks: List<BorrowedBook>) :
    RecyclerView.Adapter<BorrowedBooksAdapter.BorrowedBooksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowedBooksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_borrowed_book, parent, false)
        return BorrowedBooksViewHolder(view)
    }

    override fun onBindViewHolder(holder: BorrowedBooksViewHolder, position: Int) {
        val borrowedBook = borrowedBooks[position]
        holder.bookTitleTextView.text = borrowedBook.bookTitle
        holder.borrowedByTextView.text = "Borrowed by: ${borrowedBook.borrowedBy}"
    }

    override fun getItemCount(): Int = borrowedBooks.size

    class BorrowedBooksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookTitleTextView: TextView = itemView.findViewById(R.id.bookTitleTextView)
        val borrowedByTextView: TextView = itemView.findViewById(R.id.borrowedByTextView)
    }
}
