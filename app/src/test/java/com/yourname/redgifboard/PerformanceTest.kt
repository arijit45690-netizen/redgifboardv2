package com.yourname.redgifboard

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.system.measureTimeMillis
import kotlin.system.measureNanoTime

@RunWith(RobolectricTestRunner::class)
class PerformanceTest {

    @Test
    fun benchmarkRefreshKeys() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val keyboardView = LinearLayout(context)

        val rows = listOf(
            listOf("q","w","e","r","t","y","u","i","o","p"),
            listOf("a","s","d","f","g","h","j","k","l"),
            listOf("⇧","z","x","c","v","b","n","m","⌫"),
            listOf("123","Space","Search","⏎")
        )

        var isCaps = false

        fun refreshKeys() {
            keyboardView.removeAllViews()
            rows.forEach { row ->
                val rowLayout = LinearLayout(context)
                row.forEach { key ->
                    val btn = TextView(context)
                    val displayKey = if (isCaps && key.length == 1) key.uppercase() else key
                    btn.text = displayKey
                    rowLayout.addView(btn)
                }
                keyboardView.addView(rowLayout)
            }
        }

        // Warm up
        for (i in 0..10) {
            refreshKeys()
        }

        // Benchmark
        var timeNano = 0L
        val iterations = 100
        for (i in 0 until iterations) {
            timeNano += measureNanoTime {
                isCaps = !isCaps
                refreshKeys()
            }
        }

        val avgTimeMs = timeNano / iterations / 1_000_000.0
        println("Baseline - average refreshKeys time: $avgTimeMs ms")
    }

    @Test
    fun benchmarkOptimizedRefreshKeys() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val keyboardView = LinearLayout(context)

        val rows = listOf(
            listOf("q","w","e","r","t","y","u","i","o","p"),
            listOf("a","s","d","f","g","h","j","k","l"),
            listOf("⇧","z","x","c","v","b","n","m","⌫"),
            listOf("123","Space","Search","⏎")
        )

        var isCaps = false
        val keyButtons = mutableListOf<Pair<String, TextView>>()

        // One-time setup
        rows.forEach { row ->
            val rowLayout = LinearLayout(context)
            row.forEach { key ->
                val btn = TextView(context)
                val displayKey = if (isCaps && key.length == 1) key.uppercase() else key
                btn.text = displayKey
                keyButtons.add(Pair(key, btn))
                rowLayout.addView(btn)
            }
            keyboardView.addView(rowLayout)
        }

        fun optimizedRefreshKeys() {
            keyButtons.forEach { (k, b) ->
                if (k.length == 1) {
                    b.text = if (isCaps) k.uppercase() else k
                }
            }
        }

        // Warm up
        for (i in 0..10) {
            optimizedRefreshKeys()
        }

        // Benchmark
        var timeNano = 0L
        val iterations = 100
        for (i in 0 until iterations) {
            timeNano += measureNanoTime {
                isCaps = !isCaps
                optimizedRefreshKeys()
            }
        }

        val avgTimeMs = timeNano / iterations / 1_000_000.0
        println("Optimized - average optimizedRefreshKeys time: $avgTimeMs ms")
    }
}
