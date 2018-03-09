import React from 'react';
import { StyleSheet, Text, View, Button, Alert } from 'react-native';
import {AppLoading} from 'expo';
import socketIOClient from 'socket.io-client';
import AuthScreen from './AuthScreen';
import FileComponent from './FileComponent';
import DataComponent from './DataComponent';
import SocketIO from './SocketIO';
import {clusterName} from '../../Hasura';
import {fetchSession} from '../actions';

export default class Index extends React.Component {

  state = {
    loading: false,
    session: {
      isLoggedIn: false,
      token: null
    },
    openAuth: false,
    openData: false,
    openFile: false,
    openSocket: false,
    socket: null,
    messageArray: []
  }

  async componentDidMount() {
    const session = await fetchSession();
    console.log("================");
    console.log(session);
    if (session) {
      this.setState({
        ...this.state,
        session: {
          isLoggedIn: true,
          token: session.token
        }
      })
    }
  }

  tryAuth = () => {
    if (this.state.session.isLoggedIn) {
      Alert.alert('Message', 'Already logged in');
    } else {
      this.setState({...this.state, openAuth: true});
    }
  }

  setSession = (session) => {
    this.setState({
      ...this.state,
      session,
      openAuth: false
    })
  }

  unsetSession = () => {
    this.setState({
      ...this.state,
      session: {
        isLoggedIn: false,
        token: null
      }
    })
  }

  openDataComponent = () => {
    if (this.state.session.isLoggedIn) {
      this.setState({
        ...this.state,
        openData: true
      })
    } else {
      Alert.alert('Unauthorized', 'Please login to access this section');
    }
  }

  openFileComponent = () => {
    if (this.state.session.isLoggedIn) {
      this.setState({
        ...this.state,
        openFile: true
      })
    } else {
      Alert.alert('Unauthorized', 'Please login to access this section');
    }
  }
  connectToSocket = () => {
    const socket = socketIOClient(`https://api.${clusterName}.hasura-app.io`);
    this.setState({...this.state, socket, openSocket: true});
    socket.on("message", msg => {
       this.receivedMessage(msg);
    });
    socket.on("connected", () => {
      this.setState({ ...this.state, openSocket: true});
    });
  }

  receivedMessage = (msg) => {
    const newMessageArray = this.state.messageArray;
    newMessageArray.push(msg);
    this.setState({...this.state, messageArray: newMessageArray})
  }

  sendMessage = (msg) => {
    console.log(this.state);
    console.log(msg);
    const {socket} = this.state;
    socket.emit('message', msg);
  }

  disconnectSocket = () => {
    const {socket} = this.state;
    socket.disconnect();
    this.setState({...this.state, messageArray: [], openSocket: false, socket:null})
  }

  backToHomeScreen = () => {
    this.setState({
      ...this.state,
      openAuth: null,
      openData: null,
      openFile: null,
      openSocket: null
    })
  }

  render() {
    const {
      loading,
      session,
      openAuth,
      openData,
      openFile,
      openSocket
    } = this.state;
    console.log(this.state);
    if (loading) {
      return <Expo.AppLoading />
    }

    if (openAuth) {
      return (
        <AuthScreen goBack={this.backToHomeScreen} setSession={this.setSession}/>
      )
    }

    if (openFile) {
      return (
        <FileComponent goBack={this.backToHomeScreen} token={session.token}/>
      )
    }

    if (openSocket) {
      console.log(this.state);
      return (
        <SocketIO messageArray={this.state.messageArray} sendMessageCallback={this.sendMessage} goBack={this.disconnectSocket}/>
      )
    }

    return (
      <View style={styles.container}>
        <View style={styles.button}>
          <Button title="Authentication" onPress={this.tryAuth} />
        </View>
        <View style={styles.button}>
          <Button title="Data" onPress={this.openDataComponent} />
        </View>
        <View style={styles.button}>
          <Button title="Files" onPress={this.openFileComponent} />
        </View>
        <View style={styles.button}>
          <Button title="Socket.io" onPress={this.connectToSocket} />
        </View>
        {
          session.isLoggedIn
          ?
          (
            <View style={styles.button}>
              <Button title="Logout" onPress={this.unsetSession} />
            </View>
          )
          :
          null
        }
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: 'center',
  },
  button: {
    marginVertical: 20,
    marginHorizontal: 40,
  }
});
