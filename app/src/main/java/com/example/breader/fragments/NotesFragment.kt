package com.example.breader.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.breader.Adapters.NotesAdpater
import com.example.breader.AppDatabase
import com.example.breader.PdfActivity
import com.example.breader.ViewModels.ToolsViewModel
import com.example.breader.ViewModels.ToolsViewModelFactory
import com.example.bReader.databinding.FragmentNotesBinding
import com.example.breader.entities.NotesEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotesFragment : BottomSheetDialogFragment() {
    private val binding by lazy {
        FragmentNotesBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        val mActivity = requireActivity() as PdfActivity
        ViewModelProvider(mActivity, ToolsViewModelFactory(mActivity))[ToolsViewModel::class.java]
    }
    private val notesList: MutableList<NotesEntity> = mutableListOf()
    private val adapter by lazy {
        NotesAdpater(notesList, requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            mNotesRv.adapter = adapter
            getData()
            addNote()
        }
    }

    private fun addNote() {
        binding.apply {
            btnAddNote.setOnClickListener {
                val note = edtNote.text.toString()
                if (note.isEmpty()) {
                    Toast.makeText(
                        requireActivity(),
                        "Please add note to continue",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.addNote(note)
                edtNote.text = null
                getData()


            }
        }
    }

    private fun getData() {
        val pdfActivity = requireActivity() as PdfActivity
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(requireActivity())
            notesList.clear()
            database?.notesDao()?.getNotes(pdfActivity.bookId)?.forEach {
                notesList.add(it)
            }
            pdfActivity.runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root


}




















