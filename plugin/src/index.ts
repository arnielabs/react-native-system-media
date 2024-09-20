import { type ConfigPlugin, createRunOncePlugin } from '@expo/config-plugins';
import { withMediaControlAndroidManifest } from './withMediaControl';
const pkg = { name: 'react-native-system-media', version: 'UNVERSIONED' }; // require('expo-android-mediamanager/package.json')

const withMediaControl: ConfigPlugin = (config) => {
  config = withMediaControlAndroidManifest(config);

  return config;
};

export default createRunOncePlugin(withMediaControl, pkg.name, pkg.version);
