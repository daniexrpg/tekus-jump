package com.tekus.tekusjump.activity

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.baoyz.widget.PullRefreshLayout
import com.tekus.tekusjump.R
import com.tekus.tekusjump.adapter.NotificationAdapter
import com.tekus.tekusjump.model.Notification
import com.tekus.tekusjump.data.NotificationData
import com.tekus.tekusjump.service.MovementService
import java.util.*
import android.content.BroadcastReceiver


/**
 * Created by Jose Daniel on 21/01/2017.
 */
class MainActivity : Activity() {

    val activity: Activity = this

    val buttonSearch: View by lazy { findViewById(R.id.button_search) }
    val buttonService: Button by lazy { findViewById(R.id.button_service) as Button }
    val listNotification: ListView by lazy { findViewById(R.id.notification_list) as ListView }
    val refreshList: PullRefreshLayout by lazy { findViewById(R.id.refresh_list) as PullRefreshLayout }

    val notificationData: NotificationData = NotificationData()

    var notificationAdapter: NotificationAdapter? = null
    var notifications: ArrayList<Notification> = ArrayList()

    var movementService: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movementService = Intent(this, MovementService::class.java)

        LocalBroadcastManager.getInstance(this).registerReceiver(
                notifyReceiver, IntentFilter("createNotification"))

        setUpView()
    }

    private val notifyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            getNotifications()
        }
    }

    fun setUpView() {
        notificationAdapter = NotificationAdapter(this, notifications)
        listNotification.adapter = notificationAdapter

        notificationAdapter!!.onItemClickOption = object : NotificationAdapter.OnItemClickOption {
            override fun OnClickEdit(item: Notification) {
                showEditDialog(item)
            }

            override fun OnClickDelete(item: Notification) {
                showDeleteDialog(item)
            }
        }

        buttonSearch.setOnClickListener {
            MaterialDialog.Builder(activity)
                    .title("Buscar notification")
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input("Id", "", {
                        dialog, input ->
                        searchNotification(input.toString().toInt())
                    }).show()
        }

        buttonService.setOnClickListener {
            if (!isServiceRunning(MovementService::class.java)) {
                playService()
            } else {
                stopService()
            }
        }

        if (!isServiceRunning(MovementService::class.java)) {
            setUpButtonService(true)
        } else {
            setUpButtonService(false)
        }

        refreshList.setOnRefreshListener {
            getNotifications()
        }

        getNotifications()
    }

    fun getNotifications() {
        notificationData.getNotifications({ notifications: ArrayList<Notification>? ->
            if (notifications != null) {
                this.notifications = notifications
                this.notificationAdapter!!.setItems(this.notifications)
            } else {
                Toast.makeText(activity, resources.getString(R.string.generic_error), Toast.LENGTH_LONG).show()
            }

            refreshList.setRefreshing(false)
        })
    }

    fun updateNotification(item: Notification) {
        notificationData.updateNotifications(item.id, item, { notification: Notification? ->
            if (notification != null) {
                getNotifications()
            } else {
                Toast.makeText(activity, resources.getString(R.string.generic_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    fun deleteNotification(item: Notification) {
        notificationData.deleteNotifications(item.id, {
            getNotifications()
        })
    }

    fun searchNotification(id: Int) {
        notificationData.getNotification(id, { notification: Notification? ->
            if (notification != null) {
                showNotificationDialog(notification)
            } else {
                Toast.makeText(activity, resources.getString(R.string.generic_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    fun showNotificationDialog(item: Notification) {
        val dialog: MaterialDialog = MaterialDialog.Builder(activity)
                .title("Notificacion")
                .customView(R.layout.item_notification, true)
                .show()

        val view = dialog.customView
        if (view != null) {
            val id = view.findViewById(R.id.notification_id) as TextView
            val date = view.findViewById(R.id.notification_date) as TextView
            val duration = view.findViewById(R.id.notification_duration) as TextView

            id.text = item.id.toString()
            date.text = item.date
            duration.text = item.duration.toString() + " seg"

            val buttonEdit = view.findViewById(R.id.button_edit)
            val buttonDelete = view.findViewById(R.id.button_delete)

            buttonEdit.setOnClickListener { view ->
                dialog.dismiss()
                showEditDialog(item)
            }

            buttonDelete.setOnClickListener { view ->
                dialog.dismiss()
                showDeleteDialog(item)
            }
        }
    }

    fun showEditDialog(item: Notification) {
        MaterialDialog.Builder(activity)
                .title("Editar notificación: " + item.id)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("Duración", item.duration.toString(), {
                    dialog, input ->
                    item.duration = input.toString().toInt()
                    updateNotification(item)
                }).show()
    }

    fun showDeleteDialog(item: Notification) {
        MaterialDialog.Builder(activity)
                .title("Eliminar notificación")
                .content("¿Deseas elimiar esta notificación?")
                .positiveText("Si")
                .onPositive { materialDialog, dialogAction ->
                    deleteNotification(item)
                }
                .negativeText("No")
                .onNegative { materialDialog, dialogAction -> materialDialog.dismiss() }
                .show()
    }

    fun playService() {
        if (movementService != null) {
            Log.e("Service", "Play")
            startService(movementService)
            setUpButtonService(false)
        }
    }

    fun stopService() {
        if (movementService != null) {
            Log.e("Service", "Stop")
            stopService(movementService)
            setUpButtonService(true)
        }
    }

    fun setUpButtonService(on: Boolean) {
        if (on) {
            buttonService.setBackgroundColor(resources.getColor(R.color.colorButtonOn))
            buttonService.setText(R.string.button_service_on)
        } else {
            buttonService.setBackgroundColor(resources.getColor(R.color.colorButtonOff))
            buttonService.setText(R.string.button_service_off)
        }

    }


    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}