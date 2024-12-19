package com.example.breader.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.breader.Repository.BookRepo
import com.example.breader.Repository.MainRepo

class MainViewModelFactory(private val repo: MainRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repo) as T
    }
}