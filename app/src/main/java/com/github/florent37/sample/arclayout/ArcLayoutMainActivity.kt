package com.github.florent37.sample.arclayout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.sample.arclayout.databinding.ArclayoutActivityMainBinding

class ArcLayoutMainActivity : AppCompatActivity() {

    private lateinit var binding: ArclayoutActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ArclayoutActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
