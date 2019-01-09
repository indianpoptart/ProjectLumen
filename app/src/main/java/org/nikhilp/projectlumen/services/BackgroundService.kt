package org.nikhilp.projectlumen.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log

import com.github.nisrulz.sensey.ChopDetector
import com.github.nisrulz.sensey.MovementDetector
import com.github.nisrulz.sensey.ProximityDetector
import com.github.nisrulz.sensey.Sensey

/**
 * Created by admin on 7/10/2017.
 */

class BackgroundService : IntentService("Chop Detection") {

    /**
     * Object for camera service
     */
    private lateinit var objCameraManager: CameraManager
    /**
     * id of current camera
     */
    private lateinit var mCameraId: String
    /**
     * for getting torch mode
     */
    var isTorchOn: Boolean? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d("ProjectLumen", "Service Started")
        Sensey.getInstance().init(applicationContext)
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = prefs.edit()

        val chopListener = ChopDetector.ChopListener {
            Log.d("Sensey", "Chop Detected")
            try {
                if (isTorchOn!!) {
                    turnOffLight()
                    isTorchOn = false
                } else {
                    turnOnLight()
                    isTorchOn = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isTorchOn = false

        objCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            mCameraId = objCameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        val proximityListener = object : ProximityDetector.ProximityListener {
            override fun onNear() {
                Log.d("Sensey", "In Pocket")
                Sensey.getInstance().stopChopDetection(chopListener)
                turnOffLight()
            }

            override fun onFar() {
                Sensey.getInstance().startChopDetection(5f, 1, chopListener)
            }
        }
        val movementListener = object : MovementDetector.MovementListener {
            override fun onMovement() {
                Sensey.getInstance().startProximityDetection(proximityListener)
            }

            override fun onStationary() {
                Sensey.getInstance().stopProximityDetection(proximityListener)
                Log.d("Sensey", "Stationary")
                turnOffLight()

            }

        }
        Sensey.getInstance().startProximityDetection(proximityListener)
        Sensey.getInstance().startMovementDetection(movementListener)
        editor.apply()
    }

    private fun turnOnLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                objCameraManager.setTorchMode(mCameraId, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun turnOffLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                objCameraManager.setTorchMode(mCameraId, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
