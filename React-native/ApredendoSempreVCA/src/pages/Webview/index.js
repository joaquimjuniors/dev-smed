import React from 'react';
import { ActivityIndicator } from 'react-native';
import { WebView } from 'react-native-webview';

const Web = () => {

  function LoadingIndicatorView() {
    return <ActivityIndicator
      color='#009b88'
      size='large'
      style={{ position: 'absolute', top: 0, bottom: 0, left: 0, right: 0 }} />;
  }

  return (
    <WebView
      originWhitelist={['*']}
      source={{ uri: 'http://smed.pmvc.ba.gov.br/estudoremoto/login-control/' }}
      renderLoading={LoadingIndicatorView}
      startInLoadingState={true}
      javaScriptEnabled={true}
      domStorageEnabled={true}
      allowFileAccess={true}
      allowUniversalAccessFromFileURLs={true}
    />
  );
};

export default Web;
