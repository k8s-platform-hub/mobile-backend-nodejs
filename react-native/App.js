import React from 'react';
import socketIOClient from "socket.io-client";
import {Container} from 'native-base';

import Home from './src/components/Home'

export default class App extends React.Component{
  render(){
    return(
      <Container>
        <Home />
      </Container>
    );
  }
}
