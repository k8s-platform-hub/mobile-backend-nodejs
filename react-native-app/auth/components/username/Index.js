import React from 'react';
import { Container, Text, Header, Left, Right, Body, Button, Icon, Tab, Tabs } from 'native-base';
import UsernameLogin from './Login';
import UsernameSignup from './Signup';
import {loadFonts} from '../../actions';

export default class IndexUsername extends React.Component {

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
            <Text>Username</Text>
          </Body>
          <Right />
        </Header>
        <Tabs initialPage={0} ref={(tabView) => {this.tabView=tabView}}>
          <Tab heading="Signup">
            <UsernameSignup loginCallback={this.props.loginCallback}/>
          </Tab>
          <Tab heading="Login">
            <UsernameLogin loginCallback={this.props.loginCallback}/>
          </Tab>
        </Tabs>
      </Container>
    );
  }
}
