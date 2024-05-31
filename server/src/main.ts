import path from "path";
import mysql from "mysql";
import express, { Express, NextFunction, Request, Response } from "express";

const app: Express = express();
app.use(express.json());
app.use("/", express.static(path.join(__dirname, "../../client/dist")));

// Enable CORS so that we can call the API even from anywhere.
app.use(function (
  inRequest: Request,
  inResponse: Response,
  inNext: NextFunction
) {
  inResponse.header("Access-Control-Allow-Origin", "*");
  inResponse.header("Access-Control-Allow-Methods", "GET,POST,DELETE,OPTIONS");
  inResponse.header(
    "Access-Control-Allow-Headers",
    "Origin,X-Requested-With,Content-Type,Accept"
  );
  inNext();
});

console.log(process.env.MYSQL_HOST);
console.log(process.env.MYSQL_PORT);
console.log(process.env.MYSQL_USER);
console.log(process.env.MYSQL_PASSWORD);


const my = mysql.createConnection({
  host: process.env.MYSQL_HOST,
  port: parseInt(process.env.MYSQL_PORT as string),
  user: process.env.MYSQL_USER,
  password: process.env.MYSQL_PASSWORD,
  database: "recall",
  timeout: 10000,
});

my.connect();
my.query("SELECT 1 + 1 AS solution", function (error, results, fields) {
  if (error) throw error;
  console.log("The solution is: ", results[0].solution);
});

my.end();
