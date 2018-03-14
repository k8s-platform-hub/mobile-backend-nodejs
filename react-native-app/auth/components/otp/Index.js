import React from 'react';
import { Container, Text, Header, Left, Right, Body, Button, Icon, Tab, Tabs } from 'native-base';
import OTP from './OTP';
import {loadFonts} from '../../actions';

export default class IndexOtp extends React.Component {

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
            <Text>OTP</Text>
          </Body>
          <Right />
        </Header>
        <Tabs initialPage={0} ref={(tabView) => {this.tabView=tabView}}>
          <Tab heading="Signup">
            <OTP loginCallback={this.props.loginCallback} task="signup"/>
          </Tab>
          <Tab heading="Login">
            <OTP loginCallback={this.props.loginCallback} task="login"/>
          </Tab>
        </Tabs>
      </Container>
    );
  }
}
