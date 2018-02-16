# Mobile Backend

## Using backend features

Every application almost always requires a database and some kind of authentication. However, if yours doesn't, you can skip this section.

Hasura makes it easy to add some common backend features to your apps:
- Add auth via different providers [[features](https://hasura.io/features/auth)]
- Integrate with a database [[features](https://hasura.io/features/data)]
- Add file upload/download[[features](https://hasura.io/features/filestore)]


### API console

Hasura gives you a web UI to manage your database and users. You can also explore the Hasura APIs and automatically generate API code in the language of your choice.

#### Run this command inside the project directory

```bash
$ hasura api-console
```

![api-explorer.png](https://filestore.hasura.io/v1/file/463f07f7-299d-455e-a6f8-ff2599ca8402)

## Adding Environment Variables

There are some keys/tokens that your server needs, but are not safe to put in your git repository. These are mainly your passwords, secret keys and third party API tokens.

Hasura gives you a simple way of managing your secrets directly on the cloud(via Kubernetes secrets).

Check the [docs](https://docs.hasura.io/0.15/manual/project/secrets.html) to see how to add secrets.

## Implementing Push Notifications

This project already contains boilerplate code to push notifications to your applications using [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging/). The workflow is as follows:

1. Create a project on Firebase Console and add your API key to the secrets. Run the following command from the project directory.

```bash
$ hasura secret update fcm.key <FCM_API_KEY>
```

2. When your user signs up on your application, you post the firebase registration token to the `/register_device` endpoint in the following way.

```javascript
// headers
{
  'Content-Type':'application/json',
  'Authorization':'Bearer <AUTH_TOKEN>'
}

// payload
{
  'token': '<FIREBASE_TOKEN>'
}
```

3. The server will store the the token and the user_id in the database.
4. Whenever you wish to send push the notification to the app being used by a user with a particular `user_id`, you simply call the function, `utils.sendPushNotification()` function which returns true on success and false on failure.

```javascript

const utils = require('./utils/utils');

const dataPayload = {
  'title': 'NotifTitle',
  'body': 'NotifBody'
};

const success = utils.sendPushNotification(user_id, dataPayload);
```

To learn more about Firebase Cloud Messaging, check out the [docs](https://firebase.google.com/docs/cloud-messaging/concept-options).

## Socket.IO

Sometimes you will want your client to open a full duplex connection with the server. Some of the use cases are chats, games, some realtime feature etc. What better way to do it than [Socket.IO](https://socket.io)!

We have also implemented socket.io in this server. You can simply open a socket connection to the url `https://api.<clustername>.hasura-app.io` and it will work.

To learn more about Socket.IO, please [check this out](https://socket.io/get-started/chat/).

## Deployment Guide

1. (Skip this step if you do not wish to use push notifications) Mention FCM_KEY as an environment variable in the k8s.yaml. To do this, run the following command from the root directory of the project.

```bash
$ cp microservices/api/k8s.fcm.yaml microservices/api/k8s.yaml
```

2. Deploy the server to cloud.
```
$ git add .
$ git commit -m "Deploying the server"
$ git push hasura master
```

## Modifying the code and adding dependencies

The sourcecode for this server lives in the `microservices/api/src` directory. You can modify it however you want.

To add dependencies, you can add the dependencies to `microservices/api/src/package.json` or just go to `microservices/api/src` directory and run `npm install <dependency> --save`.

For example, if you need to install `request`, you will run `$ npm install request --save` from the `microservices/api/src` directory

## View server logs

If the push fails with an error `Updating deployment failed`, or the URL is showing `502 Bad Gateway`/`504 Gateway Timeout`, follow the instruction on the page and check the logs to see what is going wrong with the microservice:

```bash
# see status of microservice app
$ hasura microservice list

# get logs for the api
$ hasura microservice logs api
```

## Support

If you find a bug, or wish to request a feature, please raise an issue [here](https://github.com/hasura/mobile-backend-nodejs)
