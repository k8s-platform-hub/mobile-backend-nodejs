import React from 'react';
import socketIOClient from "socket.io-client";
import LogScreen from './LogScreen'
import { Container, Button, Header, Left, Right, Body, Title, Content, Text, Spinner} from 'native-base';

export default class Home extends React.Component {
  constructor() {
    super();
    this.state = {
      response: false,
      endpoint: "https://api.afterbirth27.hasura-app.io",
      connected: false,
      socket: null,
      received: [],
      fontsLoaded: false
    };
    this.receivedMessage = this.receivedMessage.bind(this);
  }

  async componentWillMount() {
    await Expo.Font.loadAsync({
      'Roboto': require('native-base/Fonts/Roboto.ttf'),
      'Roboto_medium': require('native-base/Fonts/Roboto_medium.ttf'),
    });
    this.setState({ ...this.state, fontsLoaded: true});
  }
  receivedMessage(msg) {
    const newMsgArray = this.state.received;
    newMsgArray.push(msg);
    this.setState({...this.state, received: newMsgArray});
  }

  connectToSocket = () => {
    const socket = socketIOClient(this.state.endpoint);
    this.setState({...this.state, socket});
    socket.on("message", msg => {
      this.receivedMessage(msg);
    });
    socket.on("connected", () => {
      this.setState({ ...this.state, connected: true});
    });
  }

  sendMessage = (msg) => {
    return () => {
      const {socket} = this.state;
      socket.emit('message', msg);
    }
  }

  disconnect = (msg) => {
    return () => {
      const {socket} = this.state;
      socket.disconnect();
      this.setState({...this.state, received:[], socket: null, connected: false});
    };
  }

  render() {
    if (!this.state.fontsLoaded) {
      return (
        <Container>
          <Spinner color='black' />
        </Container>
      )
    }
    if (this.state.connected) {
      return (
        <Container>
          <LogScreen sendMessageCallback = {this.sendMessage} disconnectCallback={this.disconnect} messages={this.state.received}/>
        </Container>
      );
    }
    return (
        <Container>
          <Header>
            <Left />
            <Body>
              <Title>Home</Title>
            </Body>
            <Right />
          </Header>
          <Content padder contentContainerStyle={{justifyContent:'center', alignItems:'center', margin: 20}}>
            <Button block dark onPress={this.connectToSocket}>
              <Text>Connect to socket</Text>
            </Button>
          </Content>
        </Container>
    );
  }
}
