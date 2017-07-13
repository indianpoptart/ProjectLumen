package org.nikhilp.projectlumen.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.nisrulz.sensey.ChopDetector;
import com.github.nisrulz.sensey.MovementDetector;
import com.github.nisrulz.sensey.ProximityDetector;
import com.github.nisrulz.sensey.Sensey;

/**
 * Created by admin on 7/10/2017.
 */

public class BackgroundService extends IntentService {

    /**
     * Object for camera service
     */
    public CameraManager objCameraManager;
    /**
     * id of current camera
     */
    public String mCameraId;
    /**
     * for getting torch mode
     */
    public Boolean isTorchOn;

    public BackgroundService() {
        super("Chop Detection");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("ProjectLumen","Service Started");
        Sensey.getInstance().init(getApplicationContext());

        final ChopDetector.ChopListener chopListener=new ChopDetector.ChopListener() {
            @Override public void onChop() {
                Log.d("Sensey","Chop Detected");
                try {
                    if (isTorchOn) {
                        turnOffLight();
                        isTorchOn = false;
                    } else {
                        turnOnLight();
                        isTorchOn = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        isTorchOn = false;

        objCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = objCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }



        final ProximityDetector.ProximityListener proximityListener=new ProximityDetector.ProximityListener() {
            @Override public void onNear() {
                Sensey.getInstance().stopChopDetection(chopListener);
                turnOffLight();
            }

            @Override public void onFar() {
                Sensey.getInstance().startChopDetection((float) 0.625,1,chopListener);
            }
        };
        final MovementDetector.MovementListener movementListener=new MovementDetector.MovementListener() {
            @Override public void onMovement() {
                Sensey.getInstance().startProximityDetection(proximityListener);
            }

            @Override public void onStationary() {
                Sensey.getInstance().stopProximityDetection(proximityListener);
                Log.d("Sensey","Stationary");
            }

        };
        Sensey.getInstance().startProximityDetection(proximityListener);
        Sensey.getInstance().startMovementDetection(movementListener);
    }

    public void turnOnLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                objCameraManager.setTorchMode(mCameraId, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void turnOffLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                objCameraManager.setTorchMode(mCameraId, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
