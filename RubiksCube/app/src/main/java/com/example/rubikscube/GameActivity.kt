package com.example.rubikscube

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class GameActivity : Activity() {

    private lateinit var cubeView: CubeView
    private lateinit var tvMoves: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnShuffle: Button
    private lateinit var btnReset: Button
    private lateinit var btnBack: Button
    private lateinit var moveButtonsContainer: LinearLayout

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

    private val moveButtonDefs = listOf(
        Triple("U",  FACE_UP,    true),
        Triple("U'", FACE_UP,    false),
        Triple("D",  FACE_DOWN,  true),
        Triple("D'", FACE_DOWN,  false),
        Triple("F",  FACE_FRONT, true),
        Triple("F'", FACE_FRONT, false),
        Triple("B",  FACE_BACK,  true),
        Triple("B'", FACE_BACK,  false),
        Triple("L",  FACE_LEFT,  true),
        Triple("L'", FACE_LEFT,  false),
        Triple("R",  FACE_RIGHT, true),
        Triple("R'", FACE_RIGHT, false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        cubeView = findViewById(R.id.cubeView) as CubeView
        tvMoves = findViewById(R.id.tvMoves) as TextView
        tvTimer = findViewById(R.id.tvTimer) as TextView
        btnShuffle = findViewById(R.id.btnShuffle) as Button
        btnReset = findViewById(R.id.btnReset) as Button
        btnBack = findViewById(R.id.btnBack) as Button
        moveButtonsContainer = findViewById(R.id.moveButtonsContainer) as LinearLayout

        setupCubeView()
        setupMoveButtons()
        setupControlButtons()
        updateMoveDisplay()
        updateTimerDisplay()

        btnShuffle.performClick()
    }

    private fun setupCubeView() {
        cubeView.cubeState = cubeState
        cubeView.onMoveListener = { face, cw -> applyMove(face, cw) }
    }

    private fun setupMoveButtons() {
        moveButtonsContainer.removeAllViews()
        for ((label, face, cw) in moveButtonDefs) {
            val btn = Button(this).apply {
                text = label
                textSize = 11f
                setBackgroundColor(0xFF37474F.toInt())
                setTextColor(Color.WHITE)
                val lp = LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                lp.setMargins(3, 3, 3, 3)
                layoutParams = lp
                setOnClickListener { applyMove(face, cw) }
            }
            moveButtonsContainer.addView(btn)
        }
    }

    private fun setupControlButtons() {
        btnShuffle.setOnClickListener {
            val (shuffled, _) = cubeState.shuffle(20)
            cubeState = shuffled
            moveCount = 0
            seconds = 0
            updateAll()
            startTimer()
        }

        btnReset.setOnClickListener {
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

        btnBack.setOnClickListener { finish() }
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
        cubeView.cubeState = cubeState
        updateMoveDisplay()
    }

    private fun updateMoveDisplay() {
        tvMoves.text = "Moves: $moveCount"
    }

    private fun updateTimerDisplay() {
        val m = seconds / 60
        val s = seconds % 60
        tvTimer.text = String.format("%02d:%02d", m, s)
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
            .setPositiveButton("New Game") { _, _ -> btnShuffle.performClick() }
            .setNegativeButton("Menu") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
}
