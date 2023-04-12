package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Scoreboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)
        val numCorrect = intent.getIntExtra(Constants.NUM_CORRECT, 0)
        val numTotal = intent.getIntExtra(Constants.NUM_TOTAL, 0)
        val name = intent.getStringExtra(Constants.NAME)

        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        nameTextView.text = name
        val scoreTextView = findViewById<TextView>(R.id.scoreTextView)
        scoreTextView.text = "$numCorrect / $numTotal"

        findViewById<Button>(R.id.restartQuiz).setOnClickListener {
            val restartIntent = Intent(this, MainActivity::class.java)
            restartIntent.putExtra(Constants.NAME, name)
            startActivity(restartIntent)
            finish()
        }
    }
}