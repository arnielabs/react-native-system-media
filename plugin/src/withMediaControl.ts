import {
  type ConfigPlugin,
  withAndroidManifest,
  AndroidConfig,
} from '@expo/config-plugins';

type InnerManifest = AndroidConfig.Manifest.AndroidManifest['manifest'];

type ManifestPermission = InnerManifest['permission'];

// TODO: add to `AndroidManifestAttributes` in @expo/config-plugins
type ExtraTools = {
  // https://developer.android.com/studio/write/tool-attributes#toolstargetapi
  'tools:targetApi'?: string;
};

type ManifestUsesPermissionWithExtraTools = {
  $: AndroidConfig.Manifest.ManifestUsesPermission['$'] & ExtraTools;
};

type AndroidManifest = {
  manifest: InnerManifest & {
    'permission'?: ManifestPermission;
    'uses-permission'?: ManifestUsesPermissionWithExtraTools[];
    'uses-permission-sdk-23'?: ManifestUsesPermissionWithExtraTools[];
    'uses-feature'?: InnerManifest['uses-feature'];
  };
};

export const withMediaControlAndroidManifest: ConfigPlugin = (config) =>
  withAndroidManifest(config, (config) => {
    // config.modResults = addMediaControlPermissionToManifest(config.modResults)
    config.modResults = addForegroundPermissionToManifest(config.modResults);
    // config.modResults = addNotificationPermissionToManifest(config.modResults)
    config.modResults = addNotificationServiceToManifest(config.modResults);
    return config;
  });

/**
 * Add Media Control permissions
 *  - 'android.permission.MEDIA_CONTENT_CONTROL' for Android Media Control
 */
export function addMediaControlPermissionToManifest(
  androidManifest: AndroidManifest
) {
  if (!Array.isArray(androidManifest.manifest['uses-permission'])) {
    androidManifest.manifest['uses-permission'] = [];
  }

  if (
    !androidManifest.manifest['uses-permission'].find(
      (item) =>
        item.$['android:name'] === 'android.permission.MEDIA_CONTENT_CONTROL'
    )
  ) {
    AndroidConfig.Manifest.ensureToolsAvailable(androidManifest);
    androidManifest.manifest['uses-permission']?.push({
      $: {
        'android:name': 'android.permission.MEDIA_CONTENT_CONTROL',
      },
    });
  }
  return androidManifest;
}

/**
 * Add Media Control permissions
 *  - 'android.permission.BIND_NOTIFICATION_LISTENER_SERVICE' for Android Media Control
 */
export function addNotificationPermissionToManifest(
  androidManifest: AndroidManifest
) {
  if (!Array.isArray(androidManifest.manifest['uses-permission'])) {
    androidManifest.manifest['uses-permission'] = [];
  }

  if (
    !androidManifest.manifest['uses-permission'].find(
      (item) =>
        item.$['android:name'] ===
        'android.permission.BIND_NOTIFICATION_LISTENER_SERVICE'
    )
  ) {
    AndroidConfig.Manifest.ensureToolsAvailable(androidManifest);
    androidManifest.manifest['uses-permission']?.push({
      $: {
        'android:name': 'android.permission.BIND_NOTIFICATION_LISTENER_SERVICE',
      },
    });
  }
  return androidManifest;
}

/**
 * Add Foreground Service permissions
 *  - 'android.permission.BIND_NOTIFICATION_LISTENER_SERVICE' for Android Media Control
 */
export function addForegroundPermissionToManifest(
  androidManifest: AndroidManifest
) {
  if (!Array.isArray(androidManifest.manifest['uses-permission'])) {
    androidManifest.manifest['uses-permission'] = [];
  }

  if (
    !androidManifest.manifest['uses-permission'].find(
      (item) =>
        item.$['android:name'] === 'android.permission.FOREGROUND_SERVICE'
    )
  ) {
    AndroidConfig.Manifest.ensureToolsAvailable(androidManifest);
    androidManifest.manifest['uses-permission']?.push({
      $: {
        'android:name': 'android.permission.FOREGROUND_SERVICE',
      },
    });
  }
  return androidManifest;
}

export function addNotificationServiceToManifest(
  androidManifest: AndroidManifest
) {
  const ANDROID_PERMISSION =
    'android.permission.BIND_NOTIFICATION_LISTENER_SERVICE';
  const SERVICE_NAME = '.ControlMediaService';

  if (!Array.isArray(androidManifest.manifest['application'])) {
    androidManifest.manifest['application'] = [];
  }

  if (!androidManifest.manifest.application[0]?.service) {
    androidManifest.manifest.application[0]!.service = [];
  }

  const hasService = androidManifest.manifest.application[0]?.service.find(
    (s) => s.$['android:name'] === SERVICE_NAME
  );

  if (!hasService) {
    androidManifest.manifest.application[0]?.service.push({
      '$': {
        'android:name': SERVICE_NAME,
        'android:permission': ANDROID_PERMISSION,
        'android:exported': 'true',
      },
      'intent-filter': [
        {
          action: [
            {
              $: {
                'android:name':
                  'android.service.notification.NotificationListenerService',
              },
            },
          ],
        },
      ],
    });
  }

  return androidManifest;
}
