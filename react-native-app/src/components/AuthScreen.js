import React from 'react';
import { StyleSheet, Text, View, Alert, Button, TextInput, TouchableWithoutFeedback, Keyboard } from 'react-native';
import {trySignup, tryLogin, storeSession} from '../actions'

export default class AuthScreen extends React.Component {

  state = {
      username: '',
      password: ''
    }

  handleUsernameChange = (text) => {
    this.setState({...this.state, username: text})
  }

  handlePasswordChange = (text) => {
    this.setState({...this.state, password: text})
  }

  handleSignupPress = async () => {
    const signupResp = await trySignup(this.state.username, this.state.password);
    if (signupResp.success) {
      await storeSession({id: signupResp.hasura_id, token: signupResp.auth_token});
      console.log(signupResp);
      Alert.alert('Success', 'Signup Successful');
      this.props.setSession({
        isLoggedIn: true,
        token: signupResp.auth_token
      });
    } else {
      Alert.alert('Request failed', signupResp.message);
    }
  }

  handleLoginPress = async () => {
    const { username, password} = this.state;
    const loginResp = await tryLogin(username, password);
    if (loginResp.success) {
      await storeSession({id: loginResp.hasura_id, token: loginResp.auth_token});
      console.log(loginResp);
      Alert.alert('Success', 'Login Successful');
      this.props.setSession({
        isLoggedIn: true,
        token: loginResp.auth_token
      });
    } else {
      Alert.alert('Request failed', loginResp.message);
    }
  }

  render() {
    return (
      <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
        <View style={styles.container}>
          <TextInput style={styles.textbox} value={this.state.username} onChangeText={this.handleUsernameChange} placeholder="Username" />
          <TextInput style={styles.textbox} value={this.state.password} onChangeText={this.handlePasswordChange} placeholder="Password" secureTextEntry />
          <View style={styles.buttonContainer}>
            <Button color="grey" title="Sign up" onPress={this.handleSignupPress} />
            <Button color="grey" title="Log in" onPress={this.handleLoginPress} />
            <Button color="grey" title="Cancel" onPress={this.props.goBack} />
          </View>
        </View>
      </TouchableWithoutFeedback>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: 'center',
  },
  textbox: {
    marginVertical: 40,
    marginHorizontal: 40,
    height: 50,
    borderWidth: 2,
    borderRadius: 20,
    borderColor: 'grey',
    padding:10,
    fontSize: 20
  },
  buttonContainer: {
    flexDirection: 'row',
    marginVertical: 40,
    marginHorizontal: 40,
    justifyContent: 'space-around',
  }
});
