package com.example.armansimonyan.slider

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.slider.Slider

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val slider = findViewById<Slider>(R.id.slider)
		findViewById<View>(R.id.button).setOnClickListener {
			slider.setValue(40.0f, true)
		}
		slider.setOnSliderReleaseListener(object : Slider.OnSliderReleaseListener {
			override fun onSliderReleased(value: Int) {
				Toast.makeText(this@MainActivity, "$value", Toast.LENGTH_SHORT).show()
			}
		})
	}
}
