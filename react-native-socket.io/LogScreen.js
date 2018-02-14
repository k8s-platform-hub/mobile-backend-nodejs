import React from 'react';
import {View} from 'react-native';
import {Header, Left, Body, Title, Right, Content, Container, Button, Text, Input, Item, Card, CardItem} from 'native-base'


export default class LogScreen extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      messages: this.props.messages,
      msg: ''
    };
  }

  componentWillReceiveProps() {
    this.setState({...this.state, msg: ''});
  }

  handleUsernameChange = (msg) => {
    this.setState({...this.state, msg})
  }

  render(){
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
        <Content>
          <View style={{flex: 0.1}} />
          <View style={{flex: 0.2, flexDirection: 'row'}}>
            <View style={{flex:0.75}}>
              <Item regular>
                <Input placeholder='Message' value={this.state.msg} onChangeText={(text) => {
                  this.handleUsernameChange(text);
                }}
                />
              </Item>
            </View>
            <View style={{flex: 0.05}} />
            <View style={{flex: 0.20}}>
              <Button  onPress={this.props.sendMessageCallback(this.state.msg)} >
                <Text>Send</Text>
              </Button>
            </View>
          </View>
          <View style={{flex: 0.6}}>
            {renderMessages}
          </View>
          <View style={{flex: 0.1}} />
        </Content>
      </Container>
    );
  }
}
