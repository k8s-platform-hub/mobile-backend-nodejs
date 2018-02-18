## Description

Mobile backend project written in NodeJS using Express. Deploy to cloud using a simple git push.

This is an ideal project to start with if:

- If you are a front-end mobile developer familiar with building UI
- If you are looking to easily deploy nodeJS server to cloud
- If you are looking to implement push notifications or socket.io with nodeJS.

Also a tutorial for mobile developers who are planning to go fullstack.

## Deployment guide

1. Quickstart this project. Run this command.

```bash
$ hasura quickstart mobile-backend-nodejs
```

2. Simply git push to the hasura remote from the project directory to deploy the mobile server.

```
$ git add .
$ git commit -m "First deployment"
$ git push hasura master
```

3. The server will be deployed to `https://api.<CLUSTER-NAME>.hasura-app.io/`. Run `hasura cluster status` to find your cluster name.

## API-Console

Hasura provides you with a web UI to manage your backend. Just run the following command from your backend.

```
$ hasura api-console
```

![api-explorer.png](https://filestore.hasura.io/v1/file/463f07f7-299d-455e-a6f8-ff2599ca8402)


## Using the database

1. Creating a table: Go to `Data` tab in the menu bar and click on create table as shown below
  ~[Image TODO]

2. Once you have created the table, go to the `API-Explorer` tab and try building a query with the query builder. Also add the admin token because you haven't added anonymous permissions on the table.
  ~[Image TODO]

3. Since you have not added any permissions for the table, you need to add admin token Hit `send`.

4. You have successfully made a data query without the hassle of configuring a database.

5. Now if you want to make this same query in your code, there is a code generator that builds code snippets of the exact same query in your preferred language. Click on `Generate API Code`
  ~[Image TODO]

6. Try playing around with the query builder. You can build a lot of complex queries. You can also add different permissions from `Modify Table`.

## Using Authentication

1. Go back to the API explorer. Click on `Username-Password > Signup` under `Auth` on the left panel.

2. Modify the username and password in the request body and hit send.
  ~[Image TODO]

3. You just created a user.

4. Now click on `Username-Password > Login`. Try logging in with the same username and password.

5. You will get success JSON body in response. You can use the auth token from this body to authenticate your users to the data requests. (Just like we added the admin token to authenticate the data query that we built in the data section)

6. Click on generate API code in the query builder to build the same query in your preferred language.

7. In your mobile application, you might want to store this token locally so that your users' session is persisted and they do not have to login everytime they open the app.

## Push Notifications

A push notification is a message that is pushed from the server to a mobile device. App managers can send messages to clients whenever they want. Push notifications are also used for having realtime support in your application.

### Android

We will show how to use push notifications to apps in Android.

1. Create a project on Firebase, add the android application to the project, add the firebase service to the android application and obtain the firebase API key. ([Follow the docs](https://firebase.google.com/docs/cloud-messaging/) you haven't done this before)

2. To add the firebase API key to environment variables, run the following commands from the project directory.

```bash
$ cp microservices/api/k8s.fcm.yaml microservices/api/k8s.yaml
$ hasura secret update fcm.key "<YOUR_FIREBASE_API_KEY>"
$ git add . && git commit -m "Mentionied the FIREBASE_KEY in k8s.yaml"
$ git push hasura master
```

3. Now whenever you sign up a user in your application, you should store their device's firebase token to database to associate it with their user id. Just send the following with payload with the following headers to the following endpoint.

{JAVA CODE SNIPPET Jaison TODO}

4. Now you can push data from your server to the application with a simple function call. Simply call the function `utils.sendPushNotifcation` with the 'user_id'. It returns true if the push was successful.

```javascript
const success = sendPushNotifcation(id);
```

### iOS

Jaison TODO

## Socket.io


Socket.IO enables real-time bidirectional event-based communication. It works on every platform, browser or device, focusing equally on reliability and speed.

### Android client

The mobile server in this project already has socket.io implemented. You simply have to connect to `https://api.<CLUSTER_NAME>.hasura-app.io` using the socket.io client to open a connection.

```java
socket = IO.socket("https://api.<CLUSTER_NAME>.hasura-app.io");
socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

  @Override
  public void call(Object... args) {
    socket.emit("message", "hi");
    socket.disconnect();
  }

}).on("message", new Emitter.Listener() {

  @Override
  public void call(Object... args) {}

}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

  @Override
  public void call(Object... args) {}

});
socket.connect();
```

### iOS client

swift code snipped Jaison TODO


## Modifying server code

The source code for the server lives in `microservices/api/server.js`. Modify it as desired and deploy the changes by running a git push again.

```
$ git add .
$ git commit -m "Modified server code"
$ git push hasura master
```

## Support

If you find any bugs, please feel free to raise an issue [here](https://github.com/hasura/mobile-backend-nodejs).
