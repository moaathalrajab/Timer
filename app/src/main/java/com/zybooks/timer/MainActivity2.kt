package com.zybooks.timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class MainActivity2 : AppCompatActivity() {

    lateinit var button: Button
    lateinit var progressBar: ProgressBar
    lateinit var resultTextView: TextView
    lateinit var numberEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        resultTextView=findViewById(R.id.result_text_view)
        numberEditText=findViewById(R.id.edit_text)
        button=findViewById(R.id.button)
        progressBar=findViewById(R.id.progress_bar)
        button.setOnClickListener(this::fibonacciClick



        )
    }

    fun fibonacciClick(view: View) {
        progressBar.visibility = View.VISIBLE
        val num = numberEditText.text.toString().toLong()
        resultTextView.text = ""

        CoroutineScope(Dispatchers.Main).launch {

            // Use suspending function to find the Fibonacci number
            val fibNumber = fibonacciSuspend(num)

            resultTextView.text = "Result: " +
                    NumberFormat.getNumberInstance(Locale.US).format(fibNumber)

            progressBar.visibility = View.INVISIBLE
        }
    }

    suspend fun fibonacciSuspend(n: Long): Long =
        withContext(Dispatchers.Default) {
            return@withContext fibonacci(n)
        }

    fun fibonacci(n: Long): Long {
        return if (n <= 1) n else fibonacci(n - 1) + fibonacci(n - 2)
    }
}