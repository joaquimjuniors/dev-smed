/* eslint-disable prettier/prettier */
import React from 'react';
import {StyleSheet} from 'react-native';
import {  WebView  } from 'react-native-webview';

const Web = () =>{
    return (
        <WebView
            source={{ uri: 'http://smed.pmvc.ba.gov.br/'}}
            style={styles.webviewContainer}
        />
    );
};

export default Web;

const styles = StyleSheet.create({
  webviewContainer:{
    marginTop: 20,
  },
})
;
