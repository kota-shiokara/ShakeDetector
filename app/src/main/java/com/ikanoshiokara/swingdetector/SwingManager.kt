package com.ikanoshiokara.swingdetector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.pow
import kotlin.math.sqrt

class SwingManager(context: Context, listener: SwingListener) {
    private val mContext = context
    private val mListener = listener
    private val mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var mGravityX: Float = 1.0f
    private var mGravityY: Float = 1.0f
    private var mGravityZ: Float = 1.0f

    private var mLinearAccelX: Float = 0.0f
    private var mLinearAccelY: Float = 0.0f
    private var mLinearAccelZ: Float = 0.0f
    private var mLinearAccelTotal: Float = 0.0f;

    public fun startDetection(){

    }

    public fun stopDetection(){

    }

    val mAccelListener: SensorEventListener = object: SensorEventListener{
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor!!.type == Sensor.TYPE_GRAVITY) {
                mGravityX = event.values[0]
                mGravityY = event.values[1]
                mGravityZ = event.values[2]
            }

            if(event.sensor!!.type == Sensor.TYPE_LINEAR_ACCELERATION){
                mLinearAccelX = event.values[0]
                mLinearAccelY = event.values[1]
                mLinearAccelZ = event.values[2]
                mLinearAccelTotal = calcVectorLength(mLinearAccelX, mLinearAccelY, mLinearAccelZ)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    private fun calcVectorLength(valX: Float, valY: Float, valZ: Float): Float{
        val a = valX.toDouble()
        val b = valY.toDouble()
        val c = valZ.toDouble()
        return sqrt(a.pow(2.0) + b.pow(2.0) + c.pow(2.0)).toFloat()
    }

    interface SwingListener{
        abstract fun onSwingDetected(swingCount: Int)
        abstract fun onMessage(message: String)
    }
}