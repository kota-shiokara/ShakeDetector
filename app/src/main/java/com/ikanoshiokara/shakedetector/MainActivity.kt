package com.ikanoshiokara.shakedetector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ikanoshiokara.shakedetector.ShakeManager.ShakeListener

class MainActivity : AppCompatActivity(){
    private lateinit var shakeManager: ShakeManager
    private val mListener: ShakeListener = object: ShakeListener{
        override fun onGestureDetected(ShakeType:Int, ShakeCount: Int){
            when(ShakeType){
                1 -> {
                    Log.d("App", "Type: SLASH_LEFT, Shake: ${ShakeCount}")
                }
                2 -> {
                    Log.d("App", "Type: SLASH_RIGHT, Shake: ${ShakeCount}")
                }
                3 -> {
                    Log.d("App", "Type: SLASH_UP, Shake: ${ShakeCount}")
                }
                4 -> {
                    Log.d("App", "Type: SLASH_DOWN, Shake: ${ShakeCount}")
                }
                5 -> {
                    Log.d("App", "Type: SHAKE, Shake: ${ShakeCount}")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shakeManager = ShakeManager(this, mListener)
    }

    override fun onResume() {
        super.onResume()
        shakeManager.startDetection()
    }

    override fun onPause() {
        super.onPause()
        shakeManager.stopDetection()
    }
}