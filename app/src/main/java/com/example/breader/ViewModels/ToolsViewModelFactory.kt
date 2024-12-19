package com.example.breader.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.breader.PdfActivity

class ToolsViewModelFactory(
    private val activity: PdfActivity
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ToolsViewModel(activity) as T
    }
}