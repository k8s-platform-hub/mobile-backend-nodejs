import React from 'react';
import { Container, Text, Header, Left, Right, Body, Button, Icon, Tab, Tabs } from 'native-base';
import EmailLogin from './Login';
import EmailSignup from './Signup';
import {loadFonts} from '../../actions';

export default class IndexEmail extends React.Component {

  async componentWillMount() {
    await loadFonts();
  }

  goToLoginScreen = () => {
    this.tabView.goToPage(1);
  }

  render() {
    return (
      <Container>
        <Header>
          <Left>
            <Button transparent onPress={this.props.homeCallback}>
              <Icon name="arrow-back" />
            </Button>
          </Left>
          <Body>
            <Text>Email</Text>
          </Body>
          <Right />
        </Header>
        <Tabs initialPage={0} ref={(tabView) => {this.tabView=tabView}}>
          <Tab heading="Signup">
            <EmailSignup loginCallback={this.props.loginCallback} loginScreenCallback={this.goToLoginScreen}/>
          </Tab>
          <Tab heading="Login">
            <EmailLogin loginCallback={this.props.loginCallback}/>
          </Tab>
        </Tabs>
      </Container>
    );
  }
}
