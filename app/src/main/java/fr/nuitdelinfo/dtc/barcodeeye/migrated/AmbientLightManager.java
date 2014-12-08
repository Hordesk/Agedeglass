/*
 * Copyright (C) 2012 ZXing authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.nuitdelinfo.dtc.barcodeeye.migrated;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import fr.nuitdelinfo.dtc.google.zxing.client.android.camera.CameraManager;


/**
 * Detects ambient light and switches on the front light when very dark, and off
 * again when sufficiently light.
 *
 */
public final class AmbientLightManager implements SensorEventListener {


    private final Context context;
    private CameraManager cameraManager;
    private Sensor lightSensor;

    public AmbientLightManager(Context context) {
        this.context = context;
    }

    public void start(CameraManager cameraManager) {
        this.cameraManager = cameraManager;

        SensorManager sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        if (lightSensor != null) {
            SensorManager sensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(this);
            cameraManager = null;
            lightSensor = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

}
