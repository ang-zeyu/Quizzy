package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

private fun getGPTPrompt(topic: String): String {
    return "[{\"role\": \"user\", \"content\": \"Help me create a JSON array of 10 quiz questions on the topic of $topic.\\n" +
            "\\n" +
            "The objects in this array should have the following fields:\\n" +
            "1. Question text as a string. Name this field 'question'.\\n" +
            "2. An array of options as a string. Name this field 'options'.\\n" +
            "3. A valid and related image URL, preferably in the past month, preferably from google. Name this field 'image'.\\n" +
            "4. Correct option as an integer. It should start counting from 0. Name this field 'correctOption'.\\n" +
            "\\n" +
            "Return me only the JSON code.\"}]"
}

class Loading : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        getChatGPTJSON()
    }

    private fun getChatGPTJSON() {
        val client = OkHttpClient.Builder()
            .readTimeout(300, TimeUnit.SECONDS)
            .build()
        val requestBody = "{ \"model\": ${Constants.GPT_MODEL}," +
                "\"messages\": ${getGPTPrompt(intent.getStringExtra(Constants.TOPIC)!!)}}"
        Log.i("...", requestBody)

        val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(requestBody.toRequestBody(JSON))
            .addHeader("Authorization", "Bearer ${Constants.GPT_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            val mainHandler: Handler = Handler(mainLooper)

            override fun onFailure(call: Call, e: IOException) {mainHandler.post {
                Log.e("network", e.message!!)
                Toast.makeText(this@Loading, "ChatGPT request failed!", Toast.LENGTH_SHORT)
                    .show()
            }}

            override fun onResponse(call: Call, response: Response) {mainHandler.post {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseStr = response.body.string()
                    Log.i("response", responseStr)
                    Toast.makeText(this@Loading, "ChatGPT request success!", Toast.LENGTH_SHORT)
                        .show()

                    val jsonResponse = JSONTokener(responseStr).nextValue() as JSONObject
                    val responseText = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    loadQuestions(responseText)
                }
            }}
        })
    }

    private fun getHardcodedQuestions() {
        val in_s: InputStream = resources.openRawResource(R.raw.questions)
        val b = ByteArray(in_s.available())
        in_s.read(b)
        loadQuestions(String(b))
    }

    private fun loadQuestions(rawQuestions: String) {
        val jsonQuestions = JSONTokener(rawQuestions).nextValue() as JSONArray

        Questions.questions = (0 until jsonQuestions.length())
            .map { i ->
                val obj = jsonQuestions.getJSONObject(i)
                val options = obj.getJSONArray("options")
                val optionsList: MutableList<String> = mutableListOf()
                for (j in 0 until options.length()) {
                    optionsList.add(options.getString(j))
                }

                Question(
                    obj.getString("question"),
                    optionsList,
                    obj.getInt("correctOption"),
                    obj.getString("image")
                )
            }
            .toList()

        val gotoLoadingIntent = Intent(this@Loading, QuizQuestion::class.java)
        gotoLoadingIntent.putExtra(Constants.NAME, intent.getStringExtra(Constants.NAME))
        startActivity(gotoLoadingIntent)
        finishAffinity()
    }
}