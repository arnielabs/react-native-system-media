# React Native System Media

React Native System Media is a library for accessing system media related information in your react native app.


## About this Library

While developing an app for our product Soundframe, we ended up with a specific requirement, we had to get information about media being played on your device by apps such as Spotify, Youtube Music, Google CHrome and other media player applications. 

Android:
* Although Android does not allow third party apps to access information about other apps due to security reasons, there are workarounds with which we can get media information from the system.
* By using the [MediaSessionMananger](https://developer.android.com/reference/android/media/session/MediaSessionManager#getActiveSessions(android.content.ComponentName)) API, we can easily get infromation about system media.
* MediaSessionManager requires your app to have permission to read notifications.


IOS (Not Supported Currently):
* All the apps in your iphone run inside a sandbox, therefore there is no way to access media information from other apps as of now. This 

## Installation

The library supports Android Marshmallow (6.0) and onwards (minSDK>= 23) 

### Bare React-Native

1. Install the npm module in your project.
```sh
npm install react-native-system-media
```
2. Make sure that your project has a min SDK version of 23.
```groovy
buildscript {
    ext {
        ...
        minSdkVersion = 23
        ...
```

3. Add a `<service>` with name `.ControlMediaService` Service to your `AndroidManifest.xml` inside the `<application>` tag.
```xml

<manifest xmlns:android="http://schemas.android.com/apk/res/android" xmlns:tools="http://schemas.android.com/tools">
    
    ...
    
    <application
      ...
      android:name=".MainApplication">


      <service android:name=".ControlMediaService" android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" android:exported="true">
        <intent-filter>
          <action android:name="android.service.notification.NotificationListenerService"/>
        </intent-filter>
      </service>

    
    </application>
</manifest>
```

### Expo

> This library cannot be used with "Expo Go" app as it uses custom native code. The library supports expo Continuous Native Generation and can be used with Prebuild. 

1. Install the npm module in your project.
```sh
npm install react-native-system-media
```

2. After installing, add the npm package to the config plugin section of your `app.json` or `app.config.json`.

```js
{
  "expo": {
    ...

    "plugins": ["react-native-system-media"]

    ...
  }
}
```

## Usage

```js
import {
  isPermissionGranted,
  requestPermissionAccess,
  getActiveMediaSessions,
} from 'react-native-system-media';


export default function MusicPlayer() {
  const [perm, setPerm] = useState<boolean>();

  useEffect(() => {
    isPermissionGranted().then(setPerm);
  }, []);

  return (
    <View style={styles.container}>
      <Text>
        Result: {perm ? 'Permission Granted' : 'Permission Not Granted'}
      </Text>
      <Button
        title="Request Permission"
        onPress={() => requestPermissionAccess()}
      ></Button>
      <Button
        title="Request Song Info"
        onPress={() => getActiveMediaSessions().then((t) => console.log(t))}
      ></Button>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});

```


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
