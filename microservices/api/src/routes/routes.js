const utils = require('../utils/utils');

const express = require("express");
const router = express.Router();
const request = require('request');

router.get("/", (req, resp) => {
  resp.send("Hello!! The server is running.");
});

router.post('/register_device', (req, resp) => {
  const reqHeaders = req.headers;
  const role = reqHeaders ? reqHeaders['x-hasura-role'] : 'anonymous';
  if (!role || role === 'anonymous') {
    resp.send({ error: 'unauthorized'});
    return;
  }
  const userId = reqHeaders['x-hasura-user-id'];
  const token = req.body.token;
  let dataUrl = 'http://data.hasura/v1/query';
  let headers = {
    'Content-Type': 'application/json',
    'X-Hasura-User-Id': '1',
    'X-Hasura-Role': 'admin'
  };
  const username = req.body.username;
  const options = {
    'url': dataUrl,
    'headers' : headers,
    'method': 'POST',
    'body': JSON.stringify({
      'type': 'insert',
      'args': {
        'table': 'fcm_tokens',
        'objects': [
          {
            'user_id': userId,
            'token': token
          }
        ],
        'on_conflict': {
          'action': 'update',
          'constraint_on': [
            'user_id'
          ]
        }
      }
    })
  };
  request(options, function (error, response, body) {
    if (error) {
      console.log('Error adding the token to databse for user_id ' + userId);
      console.log(error);
      resp.status(500).json({
        'error': 'Error adding the token to database'
      });
      return;
    }
    resp.status(500).send({
      'message': 'success',
      'db_response': body
    });
  });
});

router.post('/test_push', (req, resp) => {
  const reqHeaders = req.headers;
  const role = reqHeaders ? reqHeaders['x-hasura-role'] : 'anonymous';
  if (!role || role !== 'admin') {
    resp.status(404).send({ 'error': 'unauthorized'});
    return;
  }
  if (!req.body || !req.body.id) {
    resp.status(400).send({
      'error': 'absent or invalid payload'
    });
    return;
  }
  if (utils.sendPushNotification(id) == false){
    resp.status(500).send({
      'error': 'either the user_id is invalid or the user has not registered'
    });
    return;
  }
  resp.send(201).send({
    'success': 'push notification queued'
  });
});

module.exports = router;
