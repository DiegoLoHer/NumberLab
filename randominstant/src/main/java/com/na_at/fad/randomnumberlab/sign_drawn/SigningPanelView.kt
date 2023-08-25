package com.na_at.fad.randomnumberlab.sign_drawn

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.Rect
import android.media.MediaRecorder
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import com.na_at.fad.randomnumberlab.R
import com.na_at.fad.randomnumberlab.sign_drawn.model.Signature
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class SigningPanelView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    companion object {
        val TAG: String = SigningPanelView::class.simpleName!!
        const val FRAME_RATE: Int = 30
        const val FRAME_SCHEDULE_MILLIS: Long = 30
    }

    // ui controls
    private var viewDrawing: View
    private var viewResult: View
    private var viewTouch: View
    private var viewCanvas: DrawingCanvasView
    private var btnFinish: Button
    private var imgSign: ImageView

    // recording & files stuff
    private var recording = false
    private var timerTask: TimerTask? = null
    private var timer: Timer? = null
    private var executor: Executor? = null
    private val frameFiles = ArrayList<String>()
    private var testDir: File? = null

    // result files
    var prefix: String = ""
    var signVideoFile: File? = null
    var signImageFile: File? = null

    init {
        val inflater = LayoutInflater.from(context)
        val containerView = inflater.inflate(R.layout.fad_view_signing_panel, this)

        prefix = "test"
        executor = Executors.newSingleThreadExecutor()
        testDir = File(context.filesDir, "${prefix}_frames")
        testDir!!.mkdir()

        // init views
        viewDrawing = containerView.findViewById(R.id.cl_drawing)
        viewResult = containerView.findViewById(R.id.cl_result)
        viewCanvas = containerView.findViewById(R.id.v_canvas)
        viewTouch = containerView.findViewById(R.id.ll_touch)
        btnFinish = containerView.findViewById(R.id.btn_finish)
        imgSign = containerView.findViewById(R.id.img_sign)

        // setup listeners
        viewDrawing.setOnClickListener {
            viewTouch.visibility = View.INVISIBLE
            btnFinish.visibility = View.VISIBLE
            viewCanvas.visibility = View.VISIBLE
        }

        viewCanvas.setDrawingSignerListener(object :
            DrawingCanvasListener {
            override fun onDrawDown(signature: Signature?) {
                if (!recording) {
                    recording = true
                    timerTask = object : TimerTask() {
                        override fun run() {
                            // write to filesystem
                            saveCanvasImage(viewCanvas.canvasBitmap)
                        }
                    }
                    timer = Timer()
                    timer!!.scheduleAtFixedRate(timerTask, 0, FRAME_SCHEDULE_MILLIS)
                    signingViewPanelListener?.onSigningStarted()
                }
            }

            override fun onDrawUp(signature: Signature?) {
                btnFinish.isEnabled = true
            }

        })

        containerView.findViewById<View>(R.id.btn_finish).setOnClickListener {

            imgSign.setImageBitmap(viewCanvas.canvasBitmap)

            // show result view
            viewDrawing.visibility = View.INVISIBLE
            viewResult.visibility = View.VISIBLE

            // stop frame saving
            timerTask!!.cancel()
            timer!!.cancel()

            signingViewPanelListener?.onSigningCompleted()
        }

        containerView.findViewById<View>(R.id.btn_clean_signature).setOnClickListener {
            signingViewPanelListener?.onSigningRestarted()
            restart()
        }

        containerView.findViewById<View>(R.id.btn_confirm).setOnClickListener {
            signingViewPanelListener?.onVideoSaving()
            saveSignVideo {
                signingViewPanelListener?.onVideoSaved()
            }
        }

        containerView.findViewById<View>(R.id.btn_retry).setOnClickListener {
            signingViewPanelListener?.onSigningRestarted()
            restart()
        }

        // first state
        init()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        viewDrawing.isClickable = enabled
    }

    fun getSignature(): Signature {
        return viewCanvas.signature
    }

    @Synchronized
    private fun saveCanvasImage(canvasBitmap: Bitmap) {
        val filename: String =
            testDir.toString() + "/video_frame_" + System.currentTimeMillis() + ".jpg"

        try {
            val out = FileOutputStream(filename)
            canvasBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                out
            ) // bmp is your Bitmap instance
            frameFiles.add(filename)
        } catch (e: IOException) {
            Log.wtf(TAG, "Error file, cause: ", e)
        }
    }

    private fun saveSignVideo(callback: (Bitmap) -> (Unit)) {
        executor!!.execute {
            try {
                signVideoFile = File(context.filesDir, "${prefix}_sign.mp4")
                val mediaRecorder = MediaRecorder()
                //                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                //                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)
                mediaRecorder.setOutputFile(signVideoFile!!.getAbsolutePath())
                mediaRecorder.setVideoSize(width, 720)
                //                mediaRecorderFirma.setVideoSize(getWidth(), getHeight());
                mediaRecorder.setVideoFrameRate(FRAME_RATE)
                mediaRecorder.prepare()
                val mSurface = mediaRecorder.surface
                mediaRecorder.start()
                val frames: List<String> = frameFiles
                for (frameFile in frames) {
                    // FIXME - use MediaMuxer
                    Thread.sleep(FRAME_SCHEDULE_MILLIS)
                    val canvas = mSurface.lockCanvas(null)
                    canvas.drawColor(0, PorterDuff.Mode.CLEAR)
                    //  canvas.drawColor(0, PorterDuff.Mode.OVERLAY)
                    val mCacheBitmap = BitmapFactory.decodeFile(frameFile)
                    canvas.drawBitmap(
                        mCacheBitmap,
                        Rect(0, 0, mCacheBitmap.width, mCacheBitmap.height),
                        Rect(
                            (canvas.width - mCacheBitmap.width) / 2,
                            (canvas.height - mCacheBitmap.height) / 2,
                            (canvas.width - mCacheBitmap.width) / 2 + mCacheBitmap.width,
                            (canvas.height - mCacheBitmap.height) / 2 + mCacheBitmap.height
                        ),
                        null
                    )
                    mSurface.unlockCanvasAndPost(canvas)
                }
                mediaRecorder.stop()
                mediaRecorder.release()
                val lastPositionFrames = frames.size - 1
                val lastFrame = frames[lastPositionFrames]
                val mCacheBitmap = BitmapFactory.decodeFile(lastFrame)
                signImageFile = File(context.filesDir, "${prefix}_sign.png")
                val out1 = FileOutputStream(signImageFile)
                mCacheBitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    out1
                ) // bmp is your Bitmap instance

                // delete temp files
                for (frameFile in frames) {
                    File(frameFile).delete()
                }
                testDir!!.delete()

                handler.post { callback(mCacheBitmap) }
            } catch (e: Exception) {
                Log.wtf(TAG, "Cannot save video file: ", e)
            }
        }
    }

    private fun init() {
        recording = false
        frameFiles.clear()
        viewDrawing.visibility = View.VISIBLE
        viewTouch.visibility = View.VISIBLE

        btnFinish.isEnabled = false
        btnFinish.visibility = View.INVISIBLE
        viewCanvas.visibility = View.INVISIBLE

        viewResult.visibility = View.INVISIBLE
    }

    fun restart() {
        viewCanvas.reset()
        viewCanvas.invalidate()
        init()
    }

    var signingViewPanelListener: SigningPanelViewListener? = null

    interface SigningPanelViewListener {
        fun onSigningStarted()
        fun onSigningRestarted()
        fun onSigningCompleted()
        fun onVideoSaving()
        fun onVideoSaved()
    }
}