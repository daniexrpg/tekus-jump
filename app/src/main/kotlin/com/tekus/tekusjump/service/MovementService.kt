package com.tekus.tekusjump.service

import android.app.Service
import android.content.Context
import android.widget.Toast
import com.tekus.tekusjump.R
import com.tekus.tekusjump.data.NotificationData
import com.tekus.tekusjump.model.Notification
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import android.support.v4.content.LocalBroadcastManager
import android.os.Bundle


/**
 * Created by Jose Daniel on 25/01/2017.
 */
class MovementService : Service(), SensorEventListener {

    var binder: IBinder? = null

    var sensorManager: SensorManager? = null

    var acceleration = DoubleArray(3)
    var gravity = DoubleArray(3)

    private var shakeTime: Long = 0
    private var startTime: Long = 0

    private var duration: Int = 0

    override fun onBind(intent: Intent?): IBinder {
        return binder!!
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Detectando movimiento", Toast.LENGTH_SHORT).show()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager!!.registerListener(this,
                sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Toast.makeText(this, "Se detuvo la detección de movimiento", Toast.LENGTH_SHORT).show()

        if (sensorManager != null) {
            sensorManager!!.unregisterListener(this)
        }

        super.onDestroy()
    }

    override fun onSensorChanged(event: SensorEvent) {
        val sensor = event.sensor

        if (sensor.type == Sensor.TYPE_ACCELEROMETER) {
            acceleration[0] = event.values[0].toDouble()
            acceleration[1] = event.values[1].toDouble()
            acceleration[2] = event.values[2].toDouble()

            detectShake(event)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun detectShake(event: SensorEvent) {
        val now = System.currentTimeMillis()
        if (now - shakeTime > 100) {
            shakeTime = now

            val gX = event.values[0] / SensorManager.GRAVITY_EARTH
            val gY = event.values[1] / SensorManager.GRAVITY_EARTH
            val gZ = event.values[2] / SensorManager.GRAVITY_EARTH
            val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble())

            if (gForce > 1.1f) {
                if (startTime.toInt() == 0) startTime = now

                if (now - startTime > 2000) {
                    duration = (now - startTime).toInt()
                }
            } else {
                if (duration > 0) {
                    Log.e("createNotification", "createNotification")

                    val calendar = Calendar.getInstance()
                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                    val notification = Notification()
                    notification.duration = duration / 2000
                    notification.date = format.format(calendar.time)

                    createNotification(notification)
                }

                startTime = 0
                duration = 0
            }
        }
    }

    val notificationData: NotificationData = NotificationData()

    fun createNotification(item: Notification) {
        notificationData.createNotifications(item, { notification: Notification? ->
            if (notification != null) {
                Toast.makeText(this, "Notificación creada", Toast.LENGTH_LONG).show()
                notifyCreation()
            } else {
                Toast.makeText(this, resources.getString(R.string.generic_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    fun notifyCreation() {
        val intent = Intent("createNotification")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}