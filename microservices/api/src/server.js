const express = require("express");
const http = require("http");
const app = express();
const server = http.createServer(app);

const bodyParser = require('body-parser');

app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
  extended: true
}));

const socketClient = require("socket.io");
const io = socketClient(server);

const serverRoutes = require("./routes/routes");
app.use(serverRoutes);

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

io.on("connection", socket => {
  console.log("New client connected")
  socket.emit('connected', 'Welcome to the socket server');
  socket.on("message", (msg) => {
    console.log('Received message: ' + msg);
    socket.emit('message', ('The message you sent is: ' + msg));
  })
  socket.on("disconnect", () => console.log("Client disconnected"));
});

server.listen(8080, () =>{
  console.log(`Listening on port 8080`);
});
