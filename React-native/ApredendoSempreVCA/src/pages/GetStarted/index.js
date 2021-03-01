/* eslint-disable prettier/prettier */
import React from 'react';
import {View, StyleSheet, Text, Dimensions, Image, TouchableOpacity} from 'react-native';

export var width = Dimensions.get('window').width;
const getStarted = require('../../assets/logo.png');

const GetStarted = ({navigation}) => {
  function handleScreenLogin(){
    navigation.navigate('Login');
  }

  return (
    <View style={styles.container}>
      <View style={styles.image}>
        <Image source={getStarted} width={198} height={218}/>
      </View>
      <View style={styles.description}>
        <Text style={styles.title}>Seja bem vindo ao nosso app.</Text>
        <Text style={styles.subTitle}>Conheça todas as ferramentas que disponibilizamos para você</Text>
      </View>
      <View style={styles.button}>
        <TouchableOpacity style={styles.btnButton} onPress={handleScreenLogin}>
          <Text style={styles.btnText}>Começar</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

export default GetStarted;

const styles = StyleSheet.create({
  container:{
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingLeft: 25,
    paddingRight: 25,
  },
  description:{
    alignItems: 'center',
    marginTop: 20,
  },
  title:{
    fontSize: 18,
    fontWeight: 'bold',
  },
  subTitle:{
    fontSize: 18,
    marginTop: 25,
    textAlign: 'center',
  },
  button:{
    position: 'absolute',
    bottom: 30,
  },
  btnButton:{
    width: width - 50,
    paddingTop: 10,
    paddingBottom: 10,
    borderRadius: 7,
    borderWidth: 1,
    borderColor: '#3646d8',
  },
  btnText:{
    fontWeight: 'bold',
    fontSize: 18,
    textAlign: 'center',
    color: '#3646d8',
  },
});
