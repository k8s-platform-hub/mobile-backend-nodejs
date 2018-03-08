import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import Index from './src/components/Index';

export default class App extends React.Component {
  render() {
    return (
      <View style={styles.container}>
        <Index />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
