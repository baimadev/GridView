package com.baima.jetpack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        var extra = intent.getIntExtra("extra",0)
        extra = 4
        Log.e("xia","$extra")
    }
}