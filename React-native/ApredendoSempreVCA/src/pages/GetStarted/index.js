/* eslint-disable prettier/prettier */
import React from 'react';
import {
  View,
  StyleSheet,
  Text,
  Dimensions,
  Image,
  TouchableOpacity,
  ImageBackground,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';

export var width = Dimensions.get('window').width;
const getStarted = require('../../assets/educar-e-conquista.png');
const backGroundImg = require('../../assets/bg-lines.png');

const GetStarted = ({navigation}) => {
  function handleNavigateToWebview() {
    navigation.navigate('Webview');
  }

  return (
    <LinearGradient colors={['#117a8b', '#03b6da']} style={styles.container}>
      <ImageBackground source={backGroundImg} style={styles.bgImage}>
        <View style={styles.imageLogo}>
          <Image source={getStarted} style={styles.logo} />
        </View>
        <View style={styles.description}>
          <Text style={styles.title}>Educar é Conquista</Text>
          <Text style={styles.subTitle}>
          Conheça nosso aplicativo de ensino que disponibilizamos para vocês!
          </Text>
        </View>
        <View style={styles.button}>
          <TouchableOpacity
            style={styles.btnButton}
            onPress={handleNavigateToWebview}>
            <Text style={styles.btnText}>Começar</Text>
          </TouchableOpacity>
        </View>
      </ImageBackground>
    </LinearGradient>
  );
};

export default GetStarted;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingLeft: 25,
    paddingRight: 25,
  },
  bgImage: {
    flex: 1,
    justifyContent: 'center',
  },
  imageLogo: {
    alignItems: 'center',
  },
  logo:{
    width: 180,
    height: 180,
    resizeMode: 'contain'
  },
  description: {
    alignItems: 'center',
    marginTop: 20,
  },
  title: {
    fontSize: 28,
    marginTop: -38,
    fontWeight: 'bold',
    color: '#eee',
  },
  subTitle: {
    fontSize: 19,
    marginTop: 100,
    textAlign: 'center',
    color: '#eee',
  },
  button: {
    position: 'absolute',
    bottom: 30,
  },
  btnButton: {
    width: width - 50,
    paddingTop: 10,
    paddingBottom: 10,
    borderRadius: 7,
    borderWidth: 1,
    borderColor: '#eee',
  },
  btnText: {
    fontWeight: 'bold',
    fontSize: 18,
    textAlign: 'center',
    color: '#eee',
  },
});
