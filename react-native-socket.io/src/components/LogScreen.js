import React from 'react';
import {View} from 'react-native';
import {Header, Left, Body, Title, Right, Content, Container, Button, Text, Input, Item, Card, CardItem, Spinner} from 'native-base'

export default class LogScreen extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      messages: this.props.messages,
      msg: '',
      fontsLoaded: false
    };
  }

  async componentWillMount() {
    await Expo.Font.loadAsync({
      'Roboto': require('native-base/Fonts/Roboto.ttf'),
      'Roboto_medium': require('native-base/Fonts/Roboto_medium.ttf'),
    });
    this.setState({ ...this.state, fontsLoaded: true});
  }

  componentWillReceiveProps() {
    this.setState({...this.state, msg: ''});
  }

  handleUsernameChange = (msg) => {
    this.setState({...this.state, msg})
  }

  render(){
    if (!this.state.fontsLoaded) {
      return (
        <Container>
          <Spinner color='black' />
        </Container>
      )
    }

    const renderMessages = this.state.messages.map((msg, i) => {
      return (
        <Card key={i}>
          <CardItem>
            <Text>{msg}</Text>
          </CardItem>
        </Card>
      );
    });

    return (
      <Container>
        <Header>
          <Left>
            <Button transparent dark onPress={this.props.disconnectCallback()}>
              <Text>Leave</Text>
            </Button>
          </Left>
          <Body>
            <Title>Open Socket</Title>
          </Body>
          <Right />
        </Header>
        <Content padder>
          <View style={{flex: 0.25, flexDirection: 'row'}}>
            <View style={{flex:0.78}}>
              <Item rounded>
                <Input placeholder='Message' value={this.state.msg} onChangeText={(text) => {
                  this.handleUsernameChange(text);
                }}
                />
              </Item>
            </View>
            <View style={{flex: 0.02}} />
            <View style={{flex: 0.20}}>
              <Button dark onPress={this.props.sendMessageCallback(this.state.msg)} >
                <Text>Send</Text>
              </Button>
            </View>
          </View>
          <View style={{flex: 0.75}}>
            {renderMessages}
          </View>
        </Content>
      </Container>
    );
  }
}
