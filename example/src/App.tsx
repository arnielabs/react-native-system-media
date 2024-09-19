import { useState, useEffect } from 'react';
import { StyleSheet, View, Text, Button } from 'react-native';
import {
  isPermissionGranted,
  requestPermissionAccess,
  getActiveMediaSessions,
} from 'react-native-system-media';

export default function App() {
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
