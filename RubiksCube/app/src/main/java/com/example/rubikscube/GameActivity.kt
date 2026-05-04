package com.example.rubikscube

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.rubikscube.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private var cubeState = CubeState()
    private var moveCount = 0
    private var seconds = 0
    private var timerRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            seconds++
            updateTimerDisplay()
            handler.postDelayed(this, 1000)
        }
    }

    // Move buttons: (face, clockwise)
    private val moveButtonDefs = listOf(
        Triple("U", FACE_UP, true),
        Triple("U'", FACE_UP, false),
        Triple("D", FACE_DOWN, true),
        Triple("D'", FACE_DOWN, false),
        Triple("F", FACE_FRONT, true),
        Triple("F'", FACE_FRONT, false),
        Triple("B", FACE_BACK, true),
        Triple("B'", FACE_BACK, false),
        Triple("L", FACE_LEFT, true),
        Triple("L'", FACE_LEFT, false),
        Triple("R", FACE_RIGHT, true),
        Triple("R'", FACE_RIGHT, false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCubeView()
        setupMoveButtons()
        setupControlButtons()
        updateMoveDisplay()
        updateTimerDisplay()

        binding.btnShuffle.performClick()
    }

    private fun setupCubeView() {
        binding.cubeView.cubeState = cubeState
        binding.cubeView.onMoveListener = { face, cw ->
            applyMove(face, cw)
        }
    }

    private fun setupMoveButtons() {
        val container = binding.moveButtonsContainer
        container.removeAllViews()
        for ((label, face, cw) in moveButtonDefs) {
            val btn = Button(this).apply {
                text = label
                textSize = 12f
                setBackgroundColor(0xFF37474F.toInt())
                setTextColor(Color.WHITE)
                val lp = android.widget.LinearLayout.LayoutParams(0,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                lp.setMargins(4, 4, 4, 4)
                layoutParams = lp
                setOnClickListener { applyMove(face, cw) }
            }
            container.addView(btn)
        }
    }

    private fun setupControlButtons() {
        binding.btnShuffle.setOnClickListener {
            val (shuffled, _) = cubeState.shuffle(20)
            cubeState = shuffled
            moveCount = 0
            seconds = 0
            updateAll()
            startTimer()
        }

        binding.btnReset.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Reset Cube")
                .setMessage("Reset to solved state?")
                .setPositiveButton("Reset") { _, _ ->
                    cubeState = CubeState()
                    moveCount = 0
                    seconds = 0
                    stopTimer()
                    updateAll()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun applyMove(face: Int, cw: Boolean) {
        val moveIdx = face * 2 + if (cw) 0 else 1
        cubeState = cubeState.applyMove(moveIdx)
        moveCount++
        updateAll()

        if (cubeState.isSolved()) {
            stopTimer()
            showSolvedDialog()
        }
    }

    private fun updateAll() {
        binding.cubeView.cubeState = cubeState
        updateMoveDisplay()
    }

    private fun updateMoveDisplay() {
        binding.tvMoves.text = "Moves: $moveCount"
    }

    private fun updateTimerDisplay() {
        val m = seconds / 60
        val s = seconds % 60
        binding.tvTimer.text = String.format("%02d:%02d", m, s)
    }

    private fun startTimer() {
        if (timerRunning) return
        timerRunning = true
        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        timerRunning = false
        handler.removeCallbacks(timerRunnable)
    }

    private fun showSolvedDialog() {
        val m = seconds / 60
        val s = seconds % 60
        AlertDialog.Builder(this)
            .setTitle("Solved!")
            .setMessage("Congratulations!\n\nMoves: $moveCount\nTime: ${String.format("%02d:%02d", m, s)}")
            .setPositiveButton("New Game") { _, _ -> binding.btnShuffle.performClick() }
            .setNegativeButton("Menu") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
}
