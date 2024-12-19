package com.example.breader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.breader.Adapters.CategoryAdapter
import com.example.breader.Models.BooksModel
import com.example.breader.Utils.SpringScrollHelper
import com.example.bReader.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCategoryBinding.inflate(layoutInflater)
    }
    private val activity = this

    private val list = ArrayList<BooksModel>()
    private val adapter = CategoryAdapter(list, activity)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            mRvCategory.adapter = adapter
            SpringScrollHelper().attachToRecyclerView(mRvCategory)
            val bookList = intent.getSerializableExtra("book_list") as ArrayList<BooksModel>
            bookList.forEach {
                list.add(it)
            }
        }
    }

    override fun onBackPressed() {
        finish()
        with(window) {
            sharedElementReenterTransition = null
            sharedElementReturnTransition = null
        }
        binding.mRvCategory.transitionName = null
    }
}