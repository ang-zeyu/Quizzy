package com.example.quizapp

data class Question(
    val question: String,
    val options: List<String>,
    val answer: Int,
    val imageURL: String
)

object Questions {
    var questions: List<Question> = listOf()
}
