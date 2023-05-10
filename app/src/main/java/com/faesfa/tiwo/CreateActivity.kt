package com.faesfa.tiwo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

class CreateActivity : AppCompatActivity() {
    private lateinit var toolBar : Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        toolBar = findViewById(R.id.includeCreateBar)
        toolBar.title = ""
        toolBar.elevation = 5F
        setSupportActionBar(toolBar)


        
    }
}