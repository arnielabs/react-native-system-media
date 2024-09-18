package com.systemmedia

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.NativeModule
import android.provider.Settings
import android.util.Log
import android.content.Context
import android.content.Intent
import android.media.session.MediaSessionManager
import android.content.ComponentName
import android.service.notification.NotificationListenerService
import android.media.session.MediaController;
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableArray
import android.media.MediaMetadata

class SystemMediaModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }
    // Method to access the application context
  private fun getContext(): Context {
      return reactApplicationContext // This is how you access the context
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
  fun isPermissionGranted(promise: Promise) {
    val context = getContext()
    val enabledListeners = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    Log.d("SystemMediaListener", enabledListeners)
    if (enabledListeners != null && enabledListeners.contains(context.packageName)) {
      promise.resolve(true)
    } else {
      promise.resolve(false)
    }
  }


  @ReactMethod
  fun requestPermissionAccess(promise: Promise){
    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    val context = getContext()

    if (intent.resolveActivity(context.packageManager) != null) {
      context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
      promise.resolve(true)
    } else {
      Log.d("SystemMediaListener", "Unable to open notification access settings")
      promise.reject("PERMISSION_ERROR", "Unable to launch Permission intent")
    }
  }


  @ReactMethod
  fun getActiveMediaSessions(promise: Promise){
    val context = getContext()

    val mediaInfo = StringBuilder()
    val mediaSessionManager: MediaSessionManager? = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
    // val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    val componentName = ComponentName.createRelative(context,".ControlMediaService")
    
    try{

      mediaSessionManager?.let { manager -> 
        val controllers = manager.getActiveSessions(componentName)
        val resultArray: WritableArray = Arguments.createArray()
        if(controllers.isEmpty()){
          promise.resolve(resultArray)
          return;
        }

        for (controller in controllers){
          val metadata: MediaMetadata? = controller.metadata
          val controllerInfo: WritableMap = Arguments.createMap()
          metadata?.keySet()?.forEach{
            controllerInfo.putString(it, metadata.getString(it) ?: "NOT_AVAILABLE") // Add identifier if needed

          }

          resultArray.pushMap(controllerInfo)     
        }
        promise.resolve(resultArray)
      }
    

    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
    }


  }

  companion object {
    const val NAME = "SystemMedia"
  }
}
