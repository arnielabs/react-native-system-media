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
import android.media.session.PlaybackState

class SystemMediaModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }
    // Method to access the application context
  private fun getContext(): Context {
      return reactApplicationContext // This is how you access the context
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
          val metadata: MediaMetadata? = controller?.getMetadata()
          val playbackState: PlaybackState? = controller?.getPlaybackState()

          val controllerInfo: WritableMap = Arguments.createMap()
          
          getAllMetadata(metadata, controllerInfo)
          getPlaybackState(playbackState, controllerInfo)
          controllerInfo.putString("PACKAGE_NAME", controller?.getPackageName())
          resultArray.pushMap(controllerInfo)     
        }
        promise.resolve(resultArray)
      }
    

    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
    }


  }


  private fun getAllMetadata(md: MediaMetadata?, md_map: WritableMap){

    md_map.putString("METADATA_KEY_ALBUM", md?.getString(MediaMetadata.METADATA_KEY_ALBUM))
    md_map.putString("METADATA_KEY_TITLE", md?.getString(MediaMetadata.METADATA_KEY_TITLE))
    md_map.putString("METADATA_KEY_ARTIST", md?.getString(MediaMetadata.METADATA_KEY_ARTIST))
    md_map.putString("METADATA_KEY_AUTHOR", md?.getString(MediaMetadata.METADATA_KEY_AUTHOR))
    md_map.putString("METADATA_KEY_WRITER", md?.getString(MediaMetadata.METADATA_KEY_WRITER))
    md_map.putString("METADATA_KEY_COMPOSER", md?.getString(MediaMetadata.METADATA_KEY_COMPOSER))
    md_map.putString("METADATA_KEY_COMPILATION", md?.getString(MediaMetadata.METADATA_KEY_COMPILATION))
    md_map.putString("METADATA_KEY_DATE", md?.getString(MediaMetadata.METADATA_KEY_DATE))
    md_map.putString("METADATA_KEY_GENRE", md?.getString(MediaMetadata.METADATA_KEY_GENRE))
    md_map.putString("METADATA_KEY_ALBUM_ARTIST", md?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST))
    md_map.putString("METADATA_KEY_ART_URI", md?.getString(MediaMetadata.METADATA_KEY_ART_URI))
    md_map.putString("METADATA_KEY_ALBUM_ART_URI", md?.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI))
    md_map.putString("METADATA_KEY_DISPLAY_TITLE", md?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE))
    md_map.putString("METADATA_KEY_DISPLAY_TITLE", md?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE))
    md_map.putString("METADATA_KEY_DISPLAY_SUBTITLE", md?.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE))
    md_map.putString("METADATA_KEY_DISPLAY_DESCRIPTION", md?.getString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION))
    md_map.putString("METADATA_KEY_DISPLAY_ICON_URI", md?.getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI))
    md_map.putString("METADATA_KEY_MEDIA_ID", md?.getString(MediaMetadata.METADATA_KEY_MEDIA_ID))
    md_map.putString("METADATA_KEY_MEDIA_URI", md?.getString(MediaMetadata.METADATA_KEY_MEDIA_URI))

  
    md_map.putDouble("METADATA_KEY_DURATION", md?.getLong(MediaMetadata.METADATA_KEY_DURATION)?.toDouble()?: -1.0)
    md_map.putDouble("METADATA_KEY_YEAR", md?.getLong(MediaMetadata.METADATA_KEY_YEAR)?.toDouble()?: -1.0)
    md_map.putDouble("METADATA_KEY_TRACK_NUMBER", md?.getLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER)?.toDouble()?: -1.0)
    md_map.putDouble("METADATA_KEY_NUM_TRACKS", md?.getLong(MediaMetadata.METADATA_KEY_NUM_TRACKS)?.toDouble()?: -1.0)
    md_map.putDouble("METADATA_KEY_DISC_NUMBER", md?.getLong(MediaMetadata.METADATA_KEY_DISC_NUMBER)?.toDouble()?: -1.0)
    md_map.putDouble("METADATA_KEY_BT_FOLDER_TYPE", md?.getLong(MediaMetadata.METADATA_KEY_BT_FOLDER_TYPE)?.toDouble()?: -1.0)
  
  }

  private fun getPlaybackState(playbackState: PlaybackState?, map: WritableMap){
        val state = playbackState?.getState()
        val position = playbackState?.getPosition()
        val bufferedPosition = playbackState?.getBufferedPosition()
        val playbackSpeed = playbackState?.getPlaybackSpeed()
        val lastPositionUpdate = playbackState?.getLastPositionUpdateTime()
        val actions = playbackState?.getActions()
        val errorMessage = playbackState?.getErrorMessage()

        // val isActive = playbackState?.isActive()   //only availbble on API>31


        map.putInt("playbackState", state?:0)
        map.putDouble("playbackPosition", position?.toDouble()?: 0.0)
        map.putDouble("playbackBufferedPosition", bufferedPosition?.toDouble()?: 0.0)
        map.putDouble("playbackSpeed", playbackSpeed?.toDouble()?: 1.0)
        map.putDouble("playbackActions", actions?.toDouble()?: 0.0)
        map.putDouble("playbackLastPositionUpdate", lastPositionUpdate?.toDouble()?: 0.0)
        map.putString("playbackErrorMessage", errorMessage?.toString())
        // map.putBoolean("isActive", isActive?:false)
  }


  companion object {
    const val NAME = "SystemMedia"
  }
}
