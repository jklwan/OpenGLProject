package com.chends.opengl.view.advanced.opengl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.util.AttributeSet;

import com.chends.opengl.renderer.advanced.opengl.CubeMapsEnvironmentRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * 立方体贴图 环境效果
 * @author chends create on 2020/1/15.
 */
public class CubeMapsEnvironmentView extends BaseGLView implements SensorEventListener {
    private CubeMapsEnvironmentRenderer renderer;
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private float[] rotationMatrix = new float[16];

    public CubeMapsEnvironmentView(Context context) {
        this(context, null);
    }

    public CubeMapsEnvironmentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }
        Matrix.setIdentityM(rotationMatrix, 0);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(renderer = new CubeMapsEnvironmentRenderer(getContext()));
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (renderer != null) {
                    renderer.rotation(rotationMatrix);
                }
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}