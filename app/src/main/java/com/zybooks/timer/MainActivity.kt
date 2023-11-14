package com.zybooks.timer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker
    private lateinit var secondPicker: NumberPicker
    private lateinit var goButton: Button
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var cancelButton: Button
    private lateinit var timeRemainingProgressBar: ProgressBar
    private lateinit var timeLeftTextView: TextView
    private val timerModel = TimerModel()
    private var timerJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initially hide the timer and progress bar
        timeLeftTextView = findViewById(R.id.time_left_text_view)
        timeLeftTextView.visibility = View.INVISIBLE
        timeRemainingProgressBar = findViewById(R.id.progress_bar)
        timeRemainingProgressBar.visibility = View.INVISIBLE

        goButton = findViewById(R.id.go_button)

        startButton = findViewById(R.id.start_button)
        pauseButton = findViewById(R.id.pause_button)
        cancelButton = findViewById(R.id.cancel_button)

        startButton.setOnClickListener { startButtonClick() }
        pauseButton.setOnClickListener { pauseButtonClick() }
        cancelButton.setOnClickListener { cancelButtonClick() }

        // Hide pause and cancel buttons until the timer starts
        pauseButton.visibility = View.GONE
        cancelButton.visibility = View.GONE

        // Show 2 digits in NumberPickers
        val numFormat = NumberPicker.Formatter { i: Int ->
            DecimalFormat("00").format(i)
        }

        // Set min and max values for all NumberPickers
        hourPicker = findViewById(R.id.hours_picker)
        hourPicker.minValue = 0
        hourPicker.maxValue = 99
        hourPicker.setFormatter(numFormat)

        minutePicker = findViewById(R.id.minutes_picker)
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.setFormatter(numFormat)

        secondPicker = findViewById(R.id.seconds_picker)
        secondPicker.minValue = 0
        secondPicker.maxValue = 59
        secondPicker.setFormatter(numFormat)

        goButton.setOnClickListener { startActivity(Intent(this,MainActivity2::class.java)) }
    }

    private fun startButtonClick() {

        // Get values from NumberPickers
        val hours = hourPicker.value
        val minutes = minutePicker.value
        val seconds = secondPicker.value

        if (hours + minutes + seconds > 0) {

            // Show progress
            timeLeftTextView.visibility = View.VISIBLE
            timeRemainingProgressBar.progress = 0
            timeRemainingProgressBar.visibility = View.VISIBLE

            // Show only Pause and Cancel buttons
            startButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
            pauseButton.setText(R.string.pause)
            cancelButton.visibility = View.VISIBLE

            // Start the model
            timerModel.start(hours, minutes, seconds)

            // Run coroutine on main thread
            timerJob = CoroutineScope(Dispatchers.Main).launch {
                updateTimer()
            }
        }
    }

    private suspend fun updateTimer() {
        while (timerModel.progressPercent < 100) {

            // Show remaining time and progress
            timeLeftTextView.text = timerModel.toString()
            timeRemainingProgressBar.progress = timerModel.progressPercent

            if (timerModel.progressPercent < 100) {
                delay(100)
            }
        }

        timerCompleted()
    }

    private fun pauseButtonClick() {
        if (timerModel.isRunning) {
            // Pause and change to resume button
            timerModel.pause()
            timerJob?.cancel()
            pauseButton.setText(R.string.resume)
        } else {
            // Resume and change to pause button
            timerModel.resume()
            timerJob = CoroutineScope(Dispatchers.Main).launch {
                updateTimer()
            }
            pauseButton.setText(R.string.pause)
        }
    }

    private fun cancelButtonClick() {
        timerJob?.cancel()
        timeLeftTextView.visibility = View.INVISIBLE
        timeRemainingProgressBar.visibility = View.INVISIBLE
        timerCompleted()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
    }

    private fun timerCompleted() {
        timerModel.stop()
        timeLeftTextView.text = getString(R.string.no_time)

        // Show only the start button
        startButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        cancelButton.visibility = View.GONE
    }
}