package com.ikanoshiokara.swingdetector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class SwingManager(context: Context, listener: SwingListener) {
    private val mContext = context
    private val mListener = listener
    private val mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    //重力
    private var mGravityX: Float = 1.0f
    private var mGravityY: Float = 1.0f
    private var mGravityZ: Float = 1.0f

    //線形加速度
    private var mLinearAccelX: Float = 0.0f
    private var mLinearAccelY: Float = 0.0f
    private var mLinearAccelZ: Float = 0.0f
    private var mLinearAccelTotal: Float = 0.0f
    private var mOldLinearAccelZ: Float = 0.0f

    //各種フラグ
    private var mGestureType: Int = GESTURE_TYPE_NONE
    private var mSlashFlag: Boolean = false
    private var mShakeAfterFlag: Boolean = false
    private var mSwingDetecting: Boolean = false


    //定数
    companion object {
        //ジェスチャータイプ用定数
        private const val GESTURE_TYPE_NONE: Int = 0
        private const val GESTURE_TYPE_SLASH_LEFT: Int = 1
        private const val GESTURE_TYPE_SLASH_RIGHT: Int = 2
        private const val GESTURE_TYPE_SLASH_UP: Int = 3
        private const val GESTURE_TYPE_SLASH_DOWN: Int = 4
        private const val GESTURE_TYPE_SWING: Int = 5

        // スラッシュのしきい値となる加速度
        private const val SLASH_ACCEL: Float = 40.0f
        // Swingの検出閾値
        private const val SWING_ACCEL: Float = 15.0f
    }

    fun startDetection(){
        val sensors: List<Sensor> = mSensorManager.getSensorList(Sensor.TYPE_ALL)
        for (s in sensors){
            when(s.type){
                Sensor.TYPE_GRAVITY-> {
                    mSensorManager.registerListener(mAccelListener, s, SensorManager.SENSOR_DELAY_UI)
                }
                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    mSensorManager.registerListener(mAccelListener, s, SensorManager.SENSOR_DELAY_UI)
                }
            }
        }
    }

    public fun stopDetection(){
        mSensorManager.unregisterListener(mAccelListener)
    }

    private val mAccelListener: SensorEventListener = object: SensorEventListener{
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
                if(mLinearAccelTotal > SLASH_ACCEL){
                    if (!mSlashFlag && !mShakeAfterFlag && mGestureType == GESTURE_TYPE_NONE){
                        //重力の角度
                        val gravityAngle: Int = Math.toDegrees(atan2(mGravityZ, mGravityX).toDouble()).toInt()
                        val deviceRoll = 90 - gravityAngle

                        val accelAngle: Int = Math.toDegrees(atan2(mLinearAccelZ, mLinearAccelX).toDouble()).toInt()
                        var slashAngle: Int = if(accelAngle + deviceRoll >= 0) accelAngle + deviceRoll else accelAngle + deviceRoll + 360
                        if (slashAngle > 360) slashAngle -= 360

                        when{
                            slashAngle in 0..45 -> {
                                mGestureType = GESTURE_TYPE_SLASH_RIGHT
                            }
                            slashAngle <= 135 -> {
                                mGestureType = GESTURE_TYPE_SLASH_UP
                            }
                            slashAngle <= 225 -> {
                                mGestureType = GESTURE_TYPE_SLASH_LEFT
                            }
                            slashAngle <= 315 -> {
                                mGestureType = GESTURE_TYPE_SLASH_DOWN
                            }
                            slashAngle <= 360 -> {
                                mGestureType = GESTURE_TYPE_SLASH_RIGHT
                            }
                        }
                        mSlashFlag = true
                    }
                }

                if (mLinearAccelTotal > SWING_ACCEL && !mSwingDetecting) {
                    mSwingDetecting = true
                }
                else if (mLinearAccelTotal < SWING_ACCEL && mSwingDetecting){
                    mSwingDetecting = false
                    if(mGestureType != GESTURE_TYPE_SWING){
                        mListener.onSwingDetected(mGestureType, 1)
                    }
                    mGestureType = GESTURE_TYPE_NONE
                    mShakeAfterFlag = true
                }

                if (mSwingDetecting){
                    if (mOldLinearAccelZ > mLinearAccelZ && mLinearAccelZ > 0) {

                    }
                }
                mOldLinearAccelZ = mLinearAccelZ
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
        fun onSwingDetected(swingType:Int, swingCount: Int)
        fun onMessage(message: String)
    }
}