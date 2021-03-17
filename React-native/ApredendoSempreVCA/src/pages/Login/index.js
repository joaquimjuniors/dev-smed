/* eslint-disable prettier/prettier */
import React, {useState} from 'react';
import {View, Text, StyleSheet, Dimensions, Image, TouchableOpacity, ImageBackground} from 'react-native';
import {Input} from 'react-native-elements';
import LinearGradient from 'react-native-linear-gradient';
import {useNavigation} from '@react-navigation/native';


export var width = Dimensions.get('window').width;
const logo = require('../../assets/aprendendo_sempre-logo-branca.png');
const backGroundImg = require('../../assets/bg-lines.png');

const Login = () => {
  const [numMatricula, setNumMatricula] = useState(null);
  const [errorNumMatricula, setErroNumMatricula] = useState(null);

  const navigation = useNavigation();

  const validarMatricula = () => {
    setNumMatricula(null);
    if (numMatricula !== null && numMatricula.length === 10){
      return true;
    } else {
      setErroNumMatricula('Preencha o campo corretamente!');
    }
    return false;
  };

  const handleNavigateToWebview = () => {
    if (validarMatricula()){
      navigation.navigate('Webview');
    }
  };

  return (
    <LinearGradient
      colors={['#42BCB4','#6E45E2']}
      style={styles.container}
    >
      <ImageBackground source={backGroundImg} style={styles.bgImage}>
        <View style={styles.header}>
        <View style={styles.logo}>
          <Image source={logo} width={178} height={179}/>
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
              <Input
                placeholder="Informe o número de matrícula"
                placeholderTextColor="#eee"
                onChangeText={value => {
                  setNumMatricula(value);
                  setErroNumMatricula(null);
                }}
                style={styles.inputStyle}
                keyboardType="numeric"
                maxLength={10}
                errorMessage={errorNumMatricula}
                // eslint-disable-next-line react-native/no-inline-styles
                errorStyle={{color: '#ff7777'}}
              />
            </View>
          </View>
        </View>

        <View style={styles.btnFormGroup}>
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
      </ImageBackground>
    </LinearGradient>
  );
};

export default Login;

const styles = StyleSheet.create({
  container:{
    flex: 1,
    alignItems: 'center',
    paddingLeft: 25,
    paddingRight: 25,
  },
  bgImage:{
    flex:1,
    justifyContent: 'center',
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
    color: '#eee',
  },
  welcomeSubtitle:{
    fontSize:25,
    fontWeight:'bold',
    color: '#eee',
  },
  form:{
    marginTop: 20,
  },
  btnFormGroup:{
    marginTop: 15,
    justifyContent: 'center',
    alignItems: 'center',
  },
  formLabel:{
    fontSize: 14,
    fontWeight: 'bold',
    color: '#eee',
  },
  inputStyle:{
    color: '#eee',
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
