/* eslint-disable prettier/prettier */
import React from 'react';
import {View, Text, StyleSheet, Dimensions, Image, TouchableOpacity} from 'react-native';
import {Input} from 'react-native-elements';
import LinearGradient from 'react-native-linear-gradient';
import {useNavigation} from '@react-navigation/native';


export var width = Dimensions.get('window').width;
const logo = require('../../assets/logo.png');

const Login = () => {
  const navigation = useNavigation();

  function handleNavigateToWebview(){
    navigation.navigate('Webview');
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
      <View style={styles.logo}>
        <Image source={logo} width={78} height={79}/>
      </View>
      </View>
      <View style={styles.main}>
        <View style={styles.welcome}>
          <View style={styles.welcomeDescription}>
            <Text style={styles.welcomeTitle}>Seja bem vindo,</Text>
            <Text style={styles.welcomeSubtitle}>Faça seu Login</Text>
          </View>
        </View>

        <View style={styles.form}>
          <View style={styles.formGroup}>
            <Text style={styles.formLabel}>Entrar</Text>
            <Input placeholder="Informe o número de matrícula" keyboardType="numeric"/>
          </View>
        </View>
      </View>

      <View style={styles.formGroup}>
        <LinearGradient
          start={{x: 0, y: 0}}
          end={{x: 1, y: 0}}
          colors={['#6e87fd','#48c0d5','#34e7ba']}
          style={styles.linearGradientButton}
        >
          <TouchableOpacity style={styles.formButton} onPress={handleNavigateToWebview}>
            <Text style={styles.formButtonText}>Login</Text>
          </TouchableOpacity>
        </LinearGradient>
      </View>
    </View>
  );
};

export default Login;

const styles = StyleSheet.create({
  container:{
    flex: 1,
    alignItems: 'center',
    paddingLeft: 25,
    paddingRight: 25,
    paddingTop: 25,
  },
  logo:{
    alignItems: 'center',
  },
  main:{
    width: width,
    paddingLeft: 25,
    paddingRight: 25,
  },
  welcome:{
    paddingTop: 45,
  },
  welcomeDescription:{

  },
  welcomeTitle:{
    fontSize:18,
    fontWeight:'bold',
  },
  welcomeSubtitle:{
    fontSize:25,
    fontWeight:'bold',
  },
  form:{
    marginTop: 20,
  },
  formGroup:{
    marginTop: 15,
  },
  formLabel:{
    fontSize: 14,
    fontWeight: 'bold',
  },
  linearGradientButton:{
    width: width - 50,
    paddingTop:12,
    paddingBottom: 12,
    borderRadius: 7,
  },
  formButton:{

  },
  formButtonText:{
    textAlign: 'center',
    fontWeight: 'bold',
    color: '#fff',
    fontSize: 18,
  },
});
