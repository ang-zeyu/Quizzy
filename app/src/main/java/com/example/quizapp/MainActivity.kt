package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var name: TextInputEditText
    private lateinit var topic: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        name = findViewById(R.id.nameInput)
        intent.getStringExtra(Constants.NAME)?.let {
            name.setText(it)
        }

        topic = findViewById(R.id.topicInput)

        findViewById<Button>(R.id.restartQuiz).setOnClickListener {
            if (name.text.isNullOrBlank()) {
                findViewById<TextInputLayout>(R.id.nameInputLayout).error = "Name cannot be empty!"
                return@setOnClickListener
            } else if (topic.text.isNullOrBlank()) {
                findViewById<TextInputLayout>(R.id.topicInputLayout).error = "Topic cannot be empty!"
                return@setOnClickListener
            }

            val gotoLoadingIntent = Intent(this, Loading::class.java)
            gotoLoadingIntent.putExtra(Constants.NAME, name.text.toString())
            gotoLoadingIntent.putExtra(Constants.TOPIC, topic.text.toString())
            startActivity(gotoLoadingIntent)
            finishAffinity()
        }
    }
}