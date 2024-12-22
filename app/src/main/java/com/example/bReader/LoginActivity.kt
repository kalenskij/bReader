package com.example.bReader

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    private lateinit var tvRedirectSignUp: TextView
    lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    lateinit var btnLogin: Button
//    lateinit var adminText :TextView
    // Creating firebaseAuth object
    lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // View Binding
        tvRedirectSignUp = findViewById(R.id.tvRedirectSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        etEmail = findViewById(R.id.etEmailAddress)
        etPass = findViewById(R.id.etPassword)
//        adminText= findViewById(R.id.tvAdminPanel)
        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()

        val window = window
        window.statusBarColor = resources.getColor(R.color.status_bar_color, null)

//        adminText.setOnClickListener {
//            val i =Intent(applicationContext,UploadActivity::class.java)
//            startActivity(i)
//        }

        btnLogin.setOnClickListener {
            login()
        }

        tvRedirectSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
           finish()
        }
    }

    private fun login() {


        val email = etEmail.text.toString()
        val pass = etPass.text.toString()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "please enter the both email and password to continue ",
                Toast.LENGTH_SHORT
            ).show()
        } else {


            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                    val i = Intent(applicationContext, BookListActivity::class.java)
                    startActivity(i)
                } else
                    Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
