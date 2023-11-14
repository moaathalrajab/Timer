package com.zybooks.timer

import android.os.SystemClock
import java.util.Locale

class TimerModel {
    private var targetTime: Long = 0
    private var timeLeft: Long = 0
    private var running = false
    private var durationMillis: Long = 0

    val isRunning: Boolean
        get() {
            return running
        }

    fun start(millisLeft: Long) {
        durationMillis = millisLeft
        targetTime = SystemClock.uptimeMillis() + durationMillis
        running = true
    }

    fun start(hours: Int, minutes: Int, seconds: Int) {

        // Add 1 sec to duration so timer stays on current second longer
        durationMillis = ((hours * 60 * 60 + minutes * 60 + seconds + 1) * 1000).toLong()
        targetTime = SystemClock.uptimeMillis() + durationMillis
        running = true
    }

    fun stop() {
        running = false
    }

    fun pause() {
        timeLeft = targetTime - SystemClock.uptimeMillis()
        running = false
    }

    fun resume() {
        targetTime = SystemClock.uptimeMillis() + timeLeft
        running = true
    }

    val remainingMilliseconds: Long
        get() {
            return if (running) {
                0L.coerceAtLeast(targetTime - SystemClock.uptimeMillis())
            } else 0
        }

    val remainingSeconds: Int
        get() {
            return if (running) {
                (remainingMilliseconds / 1000 % 60).toInt()
            } else 0
        }

    val remainingMinutes: Int
        get() {
            return if (running) {
                (remainingMilliseconds / 1000 / 60 % 60).toInt()
            } else 0
        }

    val remainingHours: Int
        get() {
            return if (running) {
                (remainingMilliseconds / 1000 / 60 / 60).toInt()
            } else 0
        }

    val progressPercent: Int
        get() {
            return if (durationMillis != 1000L) {
                100.coerceAtMost(
                    100 - ((remainingMilliseconds - 1000) * 100 /
                            (durationMillis - 1000)).toInt()
                )
            } else 0
        }

    override fun toString(): String {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", remainingHours,
            remainingMinutes, remainingSeconds)
    }
}