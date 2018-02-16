const utils = require('../utils/utils');
const express = require("express");
const router = express.Router();
const request = require('request');

router.get("/", (req, resp) => {
  resp.send("Hello!! The server is running.");
});

router.post('/register_device', (req, resp) => {
  const identity = utils.getRequestIdentity(req.headers);

  if (identity.role === 'anonymous' ) {
    resp.status(401).send({ error: 'unauthorized'});
    return;
  }

  if(!req.body || (req.body && !req.body.token)){
    resp.status(400).send({
      'error': 'invalid payload'
    });
  }
  console.log(utils.dbUrl);
  console.log('Hey there');
  console.log(req.body);
  const options = {
    'url': utils.dbUrl,
    'headers' : utils.dbAdminHeaders,
    'method': 'POST',
    'body': JSON.stringify({
      'type': 'insert',
      'args': {
        'table': 'fcm_tokens',
        'objects': [
          {
            'user_id': identity.user_id,
            'token': req.body.token
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
      console.log('Error adding the token to databse for user_id ' + identity.user_id);
      console.log(error);
      resp.status(500).send({
        'error': 'Error adding the token to database'
      });
      return;
    }
    console.log(body);
    resp.status(200).send({
      'message': 'success',
      'db_response': body
    });
    return;
  });
});

router.post('/test_push', (req, resp) => {
  const identity = utils.getRequestIdentity(req.headers);

  if (identity.role === 'anonymous' ) {
    resp.status(401).send({ error: 'unauthorized'});
    return;
  }

  if (!req.body || !req.body.id) {
    resp.status(400).send({
      'error': 'invalid payload'
    });
    return;
  }

  const dataPayload = req.body.payload ? req.body.payload : {'title': 'NotifTitle', 'body': 'NotifBody'};

  if (utils.sendPushNotification(req.body.id, dataPayload) == false){
    resp.status(500).send({
      'error': 'either the user_id is invalid or the user has not registered'
    });
    return;
  }
  console.log(body);
  resp.send(201).send({
    'success': 'push notification queued'
  });
});

module.exports = router;
