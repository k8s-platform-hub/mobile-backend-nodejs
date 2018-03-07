## Introduction

This is a tutorial aimed at mobile application developers, namely Android, iOS and React Native developers who want to build their own backends for their front end apps. By the end of this tutorial you will be able to build mobile applications with the following backend features:
- Authentication (Auth)
- Storing and retrieving data from a Database in the cloud (Data)
- Upload and download files to and from the cloud (Filestore)
- Create a server in nodejs (Custom Microservice):
  - To implement custom business logic
  - To handle Push notificaions
  - To handle realtime data using websockets

## Pre-requisites

- Basic knowledge of either Android, iOS or React Native application development. 
- A basic knowledge of Javascript or atleast the readiness to pick it up.
- A basic understanding of relational databases.
- `git` installed on your local machine and also a basic knowledge of using `git`

## Getting started

Before you begin, ensure that you have the **Hasura CLI** installed on your local machine. If not, you can find the instructions to install it [here](https://docs.hasura.io/0.15/manual/install-hasura-cli.html). Once you have the CLI tool installed, login or signup into Hasura by running the following command on your terminal:

```bash
$ hasura login
```

There are two steps required to get started with a project on Hasura.

**Step 1**: Get a Hasura project and a Hasura cluster

>A **hasura project** is a folder on your filesystem that contains all the source code and configuration for your application. A hasura project has a particular structure and the best way to create a hasura project is by cloning one from hasura.io/hub. Every project you see on hasura.io/hub is a `Hasura Project` with particular services or data added to it based on the type of project it is.

We are going to clone the `mobile-backend-nodejs` project which consists of:
- Boilerplate code for a nodejs server to handle push notifications and a websocket connection
- A basic Android, iOS and React Native application with working code implementing the various backend features covered in this tutorial

To get the project,

```bash
$ hasura quickstart hasura/mobile-backend-nodejs
```

The above command does the following:
- Creates a new directory in your current directory called `mobile-backend-nodejs` and clones the content of the `mobile-backend-nodejs` project from Hasura Hub into it.
- Makes this new directory a `git` repository and adds a remote called `hasura` to it.
- It also creates a free `Hasura Cluster` for you and `adds` this cluster to the cloned hasura project.

>A **Hasura cluster** is a cluster of nodes (VMs) on the cloud that can host any Hasura project. It has all the Hasura microservices running and the necessary tooling for you to deploy your Hasura project. Every Hasura cluster comes with a name and a domain attached to it as well. Eg: `awesome45.hasura-app.io`.

**Step 2**: Deploy the project to your cluster

```bash
$ # cd into the project directory
$ cd mobile-backend-nodejs
$ # Make your initial commit
$ git add . && git commit -m "Initial Commit"
$ # Push to the hasura remote
$ git push hasura master
```

## The API Console

Every Hasura cluster comes with an `API Console` that you can use to explore the various backend features provided by Hasura. To access the API console, 

```bash
$ # Run the following inside the project directory
$ hasura api-console
```

This will open up the console on your browser. You can access it at http://localhost:9695. We will be using the `API Console` extensively during this tutorial.

## Authentication

Every modern app almost always requires some form of authentication. This is useful to identify a user and provide some sort of personalized experience to the user. Hasura provides various types of authentication methods (username/password, mobile/otp, email/password, Google, Facebook etc).

In this tutorial, we are going to take a look at a simple username/password based authentication. Start by opening up the `API Console`. Ensure that you are on the `API Explorer` tab. 

### Signup

Let's first take a look at the signup endpoint. From the panel on the left, click on `SignUp` under `Username/Password`. Next, fill up your required username and password.

#### TODO - CHANGE IMAGES
![API Console](https://docs.hasura.io/0.15/_images/console-screenshot.png)

Once you have decided on your username and password, hit on the `Send` button to Sign Up. Your response would be similar to the following:

```json
{
    "auth_token": "9cea876c07de13d8336c4a6d80fa9f64648506bc20974fd2",
    "username": "johnsmith",
    "hasura_id": 2,
    "hasura_roles": [
        "user"
    ]
}
```

**auth_token** is the authorization token for this particular user, which we will use later to access authorized information. You should save this offline in your app to avoid making your user login each time. 
**hasura_id** is the id of the user that is automatically assigned by Hasura on signing up.
**hasura_roles** are the roles associated with this user. Keep in mind that the role associated with this user is `user`. This is default behaviour. We will get to where this comes into play in a bit. You can read more about roles [here](https://docs.hasura.io/0.15/manual/roles/index.html)


### Login

Now that we have created a user using the signup endpoint, we can now login with the same credentials. Click on `Login` under `Username/Password`. Enter in the same username and password that you used to sign up above and click on `Send`.

#### TODO: IMAGE

In the response that you get, you will see that the `hasura_id` key has the same value as the one you got after you signed up.

### Authenticated user requests

To perform any authenticated request, you need the user's authentication token (auth_token from the login/signup endpoint) and pass that as a header. In the `API Explorer` of the `API Console`, click on `User Information` under `Logged in User Actions` and hit the `Send` button. 

You will get the following error response

```json
{
    "code": "unauthorized",
    "message": "you have to be a logged in user",
    "detail": null
}
```

This is because we have not passed the auth_token in the header. Add a new header to the request with key `Authorization` and value `Bearer <auth_token>` (replace `<auth_token>` with the auth_token that you received from your login/signup request. If you did not save it, perform a login request with the same username and password to get an auth_token)

Hit the `Send` button after adding the `Authorization` header. You will receive a response similar to the one you received after login/signup.

### Code Generator

Next, let's take a look at how this will look in your respective client side code. Click on the `Code Generator` button on the top right.

#### TODO: IMAGE

Select your required language and library from the drop down on the left.
- For React Native select `Javascript React Native`
- For iOS select `Swift iOS Alamofire`
- For Android select `Java Android`

#### TODO: IMAGE

You can now copy and paste this into your client. 
#### TODO: ADD CODE REFERENCE

#### TODO: add links to docs
> For advanced use cases and to explore other providers, check out [docs](https://docs.hasura.io/0.15/manual/users/index.html).

## Database

Most apps require a database to store and retrieve information from. This can be user specific information or contextual data in general which you do not want to store locally in your app or you want to share this data with all the users of your app. Ideally, you would want to store this in a database on the cloud and access or modify it based on certain events on your app. 

Let's explore how we can do this on Hasura. Head back to the `API Console` and ensure that you are on the `Data` tab. 

### Creating a table

Click on the `Create Table` button. Let's start off with a table called `user_details` which we will use to store extra information about a user, like their name, age and gender. We will also be adding an additional column `user_id` to store the `hasura_id` of the user. `user_id` will also be our primary key as the `hasura_id` for every user is always unique.

#### TODO IMAGE

Click the `Create` button to create the table.

### Table Permissions & User Roles

Every table created on Hasura can only be accessed by users with an `admin` role. Ergo, the user we created earlier will not be able to access the `user_details` table (since the role associated with that user was `user`). This is done to ensure security on all tables, so that nobody can randomly access data from your database unless you specifically allow that.

In our case, `user_details` table is used to store user specific data. We want to give every user permission to insert and select their own data from the `user_details` table. Moreover, as an extra security measure, they should not be able to fetch another users data either.

Under the `Data` tab of the `API Console`, select `user_details` from the left panel and then click on the `Permissions` tab on the right to set permissions for the table. As you can see, an `admin` role has complete permission over the table. No other role has any permission. 

**First**, lets give the `user` role permission to insert data into the table as long as the `user_id` being inserted is the same as a the `hasura_id` of the user trying to insert this data. To do this, click on insert next to user row, check the `with custom check` option, choose `user_id` from the drop down and then select `$eq` and finally click on `X-Hasura-User-Id`

#### TODO: IMAGE

**Second**, lets give the `user` role permission to get their data from the table. Click on select next to the user row, check the `with same checks as insert`, also click on the `Toggle All` button next to `With Access to columns`. This basically means that a particular user can only get their own data from the table.

#### TODO: IMAGE

Do the same for Update and Delete permissions as well.

#### TODO IMAGE

### Inserting data into the table

Head to the `API Explorer` and click on `v1/query - Query Builder` on the left panel. Click on `type` and select `insert` to insert into a table. 

#### TODO IMAGE

Next, click on `table` and select `user_details` from the list. Fill in the `objects` array with data you want inserted into the table. In the picture shown below, we are adding data for the user we signed up with.

#### TODO IMAGE

Since we have given `user` role permission to the `user_details` table, we have to add the Authorization header to the insert query. (Add key `Authorization` and value `Bearer <auth_token>` to the header)

#### TODO IMAGE

> If you try to insert into the `user_details` table with a `user_id` which is not the same as the `hasura_id` as the user making the request, it will fail. This is because of the permissions we set on the `user_details` table.

### Selecting data from the table

Head to the `API Explorer` and click on `v1/query - Query Builder` on the left panel. Click on `type` and select `select` to select from a table. 

#### TODO IMAGE

Next, click on `table` and select `user_details` from the list. Select `user_id`, `name` and `gender` for the columns.

#### TODO IMAGE

Since we have given `user` role permission to the `user_details` table, we have to add the Authorization header to the select query (Add key `Authorization` and value `Bearer <auth_token>` to the header). Hit the `Send` button to make this request.

#### TODO IMAGE

### Relationships and Foreign Keys

One of the advantages of using a RDBMS is that you can create connections between various tables through foreign key constraints. These can be used to build more complex relationships, which can be used to fetch related data alongside the columns queried, as pseudo columns.

To explore this feature, let's create a new table called `user_education` to store information about each user's educational background like `institution_name` and `degree`. We will also have an additional column `id` of type `Integer (auto increment)` and a `user_id` column to store the `hasura_id` of the user. `id` will be the primary key for this table.

> It is not a good idea to set `user_id` as the primary key as a user can have multiple addresses and setting `user_id` as the primary key will not let us enter more than address for a particular user.

#### TODO IMAGE

Click on the create button.

Now, let's add a foreign key constraint from the `user_id` column to the `user_id` column of the `user_details` table. To do this, under the `Modify` tab, click on `edit` next to `user_id`, choose `user_details` as the reference table and `user_id` as the reference column. Click on `Save` to add this foreign key constraint.

Next, open up the `user_details` table from the left panel and click on the `Relationships` tab. If you have followed the instructions above correctly, you will now have an entry under the `Suggested Array Relationship` column. Click on `Add` and name the relationship `education` and hit `Save`. 

#### TODO IMAGE

Similarly, add `user` permissions for insert and select on the `user_education` table.

#### TODO IMAGE

Click on `Browse Rows` and you will now see another column called `education` for the `user_details` table.

#### TODO IMAGE

Head to the `API Explorer` and add some data into the `user_education` table for our user.

#### TODO IMAGE

#### Fetching relationship data 

We can now fetch the education details for each user from the `user_details` table like so:

#### TODO IMAGE

Your response will look like the following:

```json
[
    {
        "user_id": 2,
        "name": "Jack Smith",
        "gender": "Male",
        "education": [
            {
                "institution_name": "XYZ University",
                "degree": "BE",
                "id": 1,
                "user_id": 2
            },
            {
                "institution_name": "ABC University",
                "degree": "MS",
                "id": 2,
                "user_id": 2
            }
        ]
    }
]
```

### Code Generator

Similar to Authentication, you are encouraged to use the `Code Generator` to generate the client side code to make these requests.

#### TODO IMAGE


## Image Upload and Download

Some apps require the ability to upload and download files. Hasura provides easy to use APIs to upload and download files as well. Under the `API Explorer` tab, explore the APIs under `File`

#### TODO IMAGE

#### TODO Code reference












 





























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
