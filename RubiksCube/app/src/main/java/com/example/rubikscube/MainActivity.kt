package com.example.rubikscube

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var layoutHowToPlay: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPlay = findViewById(R.id.btnPlay) as Button
        val btnHowToPlay = findViewById(R.id.btnHowToPlay) as Button
        layoutHowToPlay = findViewById(R.id.layoutHowToPlay) as LinearLayout

        btnPlay.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        btnHowToPlay.setOnClickListener {
            layoutHowToPlay.visibility =
                if (layoutHowToPlay.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }
}
