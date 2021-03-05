/* eslint-disable prettier/prettier */
import React from 'react';
import { View, StyleSheet, ActivityIndicator } from 'react-native';
import { WebView } from 'react-native-webview';

const Web = () => {

  function LoadingIndicatorView() { return <ActivityIndicator
     color='#009b88' 
     size='large'
     style={{ position: "absolute", top: 0, bottom: 0, left: 0, right: 0 }} /> }
  
  return (
    <View style={styles.container}>
      <WebView
           source={{ uri: 'http://smed.pmvc.ba.gov.br/estudoremoto/' }}
            style={styles.webviewContainer}
            renderLoading={LoadingIndicatorView} 
            startInLoadingState={true}
        />
    </View>
  );
};

export default Web;

const styles = StyleSheet.create({
  webviewContainer: {
    marginTop: 20,
    position: 'relative'
  },
  container: {
    flex: 1,
  },
});

 

        