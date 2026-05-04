package com.example.rubikscube

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rubikscube.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPlay.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        binding.btnHowToPlay.setOnClickListener {
            binding.layoutHowToPlay.visibility =
                if (binding.layoutHowToPlay.visibility == android.view.View.VISIBLE)
                    android.view.View.GONE
                else android.view.View.VISIBLE
        }
    }
}
