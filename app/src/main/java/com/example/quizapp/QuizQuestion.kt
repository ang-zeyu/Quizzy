package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.quizapp.Questions.questions


class QuizQuestion : AppCompatActivity() {
    private lateinit var questionText: TextView
    private lateinit var questionImage: ImageView
    private lateinit var questionOptions: LinearLayout
    private lateinit var questionSubmit: Button

    private var currentQuestionNum: Int = -1
    private lateinit var currentQuestion: Question
    private var selectedOpt: Int? = null
    private var didSubmit: Boolean = false

    private var numCorrect = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)
        questionText = findViewById(R.id.questionText)
        questionImage = findViewById(R.id.questionImage)
        questionOptions = findViewById(R.id.questionOptions)
        questionSubmit = findViewById(R.id.questionSubmit)
        questionSubmit.setOnClickListener {
            if (didSubmit) {
                resetToNewQuestion()
            } else {
                checkAnswer()
            }
        }
        resetToNewQuestion()
    }

    private fun checkAnswer() {
        if (selectedOpt == null) {
            return
        }

        if (selectedOpt == currentQuestion.answer) {
            numCorrect += 1
        }

        didSubmit = true
        questionSubmit.text = "Next"
        renderQuestionOptions()
    }

    private fun resetToNewQuestion() {
        currentQuestionNum += 1
        if (currentQuestionNum >= questions.size) {
            val gotoScoreboardIntent = Intent(this, Scoreboard::class.java)
            gotoScoreboardIntent.putExtra(Constants.NUM_CORRECT, numCorrect)
            gotoScoreboardIntent.putExtra(Constants.NUM_TOTAL, questions.size)
            gotoScoreboardIntent.putExtra(Constants.NAME, intent.getStringExtra(Constants.NAME))
            startActivity(gotoScoreboardIntent)
            finish()
            return
        }

        currentQuestion = questions[currentQuestionNum]
        selectedOpt = null
        didSubmit = false
        questionSubmit.text = "Submit"

        renderQuestion()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun renderQuestion() {
        questionText.text = currentQuestion.question
        Glide.with(this).load(currentQuestion.imageURL).into(questionImage)

        renderQuestionOptions()
    }

    private fun renderQuestionOptions() {
        questionOptions.removeAllViews()
        for ((i, option) in currentQuestion.options.withIndex()) {
            val tv = TextView(if (didSubmit) {
                if (i == currentQuestion.answer) {
                    ContextThemeWrapper(this, R.style.questionOptionCorrect)
                } else if (i == selectedOpt) {
                    ContextThemeWrapper(this, R.style.questionOptionWrong)
                } else {
                    ContextThemeWrapper(this, R.style.questionOption)
                }
            } else if (selectedOpt == i) {
                ContextThemeWrapper(this, R.style.questionOptionSelected)
            } else {
                ContextThemeWrapper(this, R.style.questionOption)
            })
            tv.text = option

            // ------------------------------------------------------
            // https://github.com/google/flexbox-layout/issues/417
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.bottomMargin = dpToPx(16)
            tv.layoutParams = layoutParams
            // ------------------------------------------------------

            tv.setOnClickListener {
                selectedOpt = i
                renderQuestionOptions()
            }
            questionOptions.addView(tv)
        }
    }
}
