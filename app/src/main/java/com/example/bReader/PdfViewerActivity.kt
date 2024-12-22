package com.example.bReader

import android.graphics.drawable.Drawable
import android.os.Bundle

import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.joanzapata.pdfview.PDFView

import java.io.File





class PdfViewerActivity : AppCompatActivity() {

    private lateinit var pdfView: PDFView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        // Initialize the PDFView
        pdfView = findViewById(R.id.pdfView)
        val window = window
        window.statusBarColor = resources.getColor(R.color.status_bar_color, null)

        // Get the PDF URL from the Intent
        val pdfUrl = intent.getStringExtra("pdf_url")

        if (pdfUrl != null) {
            loadPdf(pdfUrl)
        } else {
            Toast.makeText(this, "No PDF URL provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPdf(pdfUrl: String) {
        // Load PDF using Glide or a similar method
        Glide.with(this)
            .asFile()
            .load(pdfUrl)

            .into(object : CustomTarget<File>() {
                override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                    // Open the PDF file in the PDFView
                    pdfView.fromFile(resource)
                        .load()

                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle clearing resources if necessary
                }
            })
   }
}
