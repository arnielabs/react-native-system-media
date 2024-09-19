import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-system-media' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const SystemMedia = NativeModules.SystemMedia
  ? NativeModules.SystemMedia
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function isPermissionGranted(): Promise<boolean> {
  return SystemMedia.isPermissionGranted();
}

export function requestPermissionAccess(): Promise<boolean> {
  return SystemMedia.requestPermissionAccess();
}

export function getActiveMediaSessions(): Promise<any> {
  return SystemMedia.getActiveMediaSessions();
}
getActiveMediaSessions;
