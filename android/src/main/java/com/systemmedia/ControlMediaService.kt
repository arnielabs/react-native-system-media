package com.systemmedia


import android.service.notification.NotificationListenerService
import android.util.Log
import android.service.notification.StatusBarNotification
import android.content.Context
import android.content.ComponentName
import android.app.NotificationManager 
import android.os.Bundle
import android.provider.Settings

class ControlMediaService : NotificationListenerService() {
  private val TAG = "SystemMediaListener"

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
      Log.d(TAG, "Notification Changed")
  }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
      Log.d(TAG, "FromNotificationListener")
  }

}