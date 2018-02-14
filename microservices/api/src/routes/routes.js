const express = require("express");
const router = express.Router();

router.get("/", (req, resp) => {
  resp.send("Hello! The server is running.");
});

module.exports = router;
