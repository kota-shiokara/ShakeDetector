package com.ikanoshiokara.shakedetector

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ikanoshiokara.shakedetector.ShakeManager.ShakeListener

class MainActivity : AppCompatActivity(){

    // 揺れ検知用Manager
    private lateinit var shakeManager: ShakeManager

    // 音に関する変数
    private lateinit var sp: SoundPool
    private var sound = 0

    // 独自リスナーを作成
    private val mListener: ShakeListener = object: ShakeListener{
        // ジェスチャー時に呼ばれる場所
        override fun onGestureDetected(ShakeType:Int, ShakeCount: Int){
            when(ShakeType){
                // 左に振る
                1 -> {
                    Log.d("App", "Type: SLASH_LEFT, Shake: ${ShakeCount}")
                }
                // 右に振る
                2 -> {
                    Log.d("App", "Type: SLASH_RIGHT, Shake: ${ShakeCount}")
                }
                // 上に振る
                3 -> {
                    Log.d("App", "Type: SLASH_UP, Shake: ${ShakeCount}")
                }
                // 下に振る
                4 -> {
                    Log.d("App", "Type: SLASH_DOWN, Shake: ${ShakeCount}")
                    sp.play(sound, 1.0f, 1.0f, 0, 0, 1.0f)
                }
                // シェイク
                5 -> {
                    Log.d("App", "Type: SHAKE, Shake: ${ShakeCount}")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 音に関する処理
        val audioAttributes = AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        sp = SoundPool
                .Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(2)
                .build()
        // 音源をロード、魔王魂さんとかから数秒程度のSEを落としてください
        sound = sp.load(this, R.raw.se_maoudamashii_voice_human03, 1)

        // ShakeManagerにリスナーの登録
        shakeManager = ShakeManager(this, mListener)
    }

    override fun onResume() {
        super.onResume()
        // 振り検知開始
        shakeManager.startDetection()
    }

    override fun onPause() {
        super.onPause()
        // 振り検知終わり
        shakeManager.stopDetection()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 音源用メモリ開放
        sp.release()
    }
}