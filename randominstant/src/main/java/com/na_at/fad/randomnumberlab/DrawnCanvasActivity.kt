package com.na_at.fad.randomnumberlab

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.na_at.fad.randomnumberlab.sign_drawn.SigningPanelView
import java.io.File

class DrawnCanvasActivity : AppCompatActivity() {


    companion object {
        val TAG: String = DrawnCanvasActivity::class.simpleName!!
    }

    // ui controls
    lateinit var signingCanvasView: SigningPanelView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawn_canvas)

        val prefix = "test"
        // setup camera options
        val signerVideoFile = File(filesDir, "${prefix}_signer.mp4")
        // setup signing view
        signingCanvasView = findViewById<SigningPanelView>(R.id.scv_signing_view)
        signingCanvasView.prefix = prefix
        signingCanvasView.signingViewPanelListener =
            object : SigningPanelView.SigningPanelViewListener {

                override fun onSigningStarted() {
                    Log.d(TAG, "FIRMANDO")
                }

                override fun onSigningRestarted() {
                    Log.d(TAG, "FIRMA DE NUEVO")

                }

                override fun onSigningCompleted() {
                    Log.d(TAG, "FIRMA DE COMPLETADA")

                }

                override fun onVideoSaving() {
                    Log.d(TAG, "GUARDANDO")

                }

                override fun onVideoSaved() {
                    Log.d(TAG, "GUARDADO")
                }

            }

    }


}