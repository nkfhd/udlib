package com.udlib.udlib

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PlayerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }
}