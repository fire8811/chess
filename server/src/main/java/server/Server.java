package server;
import dataaccess.*;
import dataaccess.Exceptions.*;
import model.*;
import com.google.gson.Gson;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

import java.sql.SQLException;


public class Server {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private ClearService clearService;
    private UserService userService;
    private GameService gameService;

    public Server() {
        try {
            this.userDAO = new SqlUserDAO();
            this.gameDAO = new SqlGameDAO();
            this.authDAO = new SqlAuthDAO();
        }
        catch(Exception e) {
            System.err.printf("something bad happened: %s%n", e.getMessage());
        }

        clearService = new ClearService(authDAO, userDAO, gameDAO);
        userService = new UserService(authDAO, userDAO, gameDAO);
        gameService = new GameService(authDAO, gameDAO);
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(DataAccessException.class, this::exceptionHandler);



        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        if (ex instanceof BadRequestException){
            res.status(400);
        }
        else if (ex instanceof UsernameTakenException){
            res.status(403);
        }
        else if (ex instanceof UnauthorizedException){
            res.status(401);
        }
        else if (ex instanceof AlreadyTakenException){
            res.status(403);
        }
        else {
            res.status(500);
        }

        res.body(new Gson().toJson(new ErrorMessage("Error: " + ex.getMessage())));

        //make new exception files that inherit from DataAccessException. Each method will say they throw a DAE but
        // they actually throw the 'correct' error with corresponding message (check
        //doc to see what each message needs to be). Then here you check to see what was the 'type' of the error
        //and then set a status code variable to be the correct code in a series of if statements.
    }

    private Object register(Request req, Response res) throws DataAccessException, SQLException {
        var registerRequest = new Gson().fromJson(req.body(), model.RegisterRequest.class);
        var registerResult = userService.register(registerRequest);
        res.status(200);
        return new Gson().toJson(registerResult);
    }

    private Object login(Request req, Response res) throws DataAccessException, SQLException {
        var loginRequest = new Gson().fromJson(req.body(), model.LoginRequest.class);
        var loginResult = userService.login(loginRequest);
        res.status(200);
        return new Gson().toJson(loginResult);
    }

    private Object logout(Request req, Response res) throws DataAccessException {
        String authtoken = req.headers("authorization");
        userService.logout(new LogoutRequest(authtoken));
        res.status(200);
        return "";
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        ListResult listResult = gameService.listGames(new ListRequest(authToken));
        res.status(200);
        return new Gson().toJson(listResult);
    }

    private Object createGame(Request req, Response res) throws DataAccessException, SQLException {
        String authToken = req.headers("authorization");
        CreateRequest createRequest = new Gson().fromJson(req.body(), model.CreateRequest.class); //add gameName from JSON body
        createRequest = new CreateRequest(authToken, createRequest.gameName()); //add authToken from header to record

        CreateResult createResult = gameService.createGame(createRequest);

        res.status(200);
        return new Gson().toJson(createResult);
    }

    private Object joinGame(Request req, Response res) throws DataAccessException, SQLException {
        String authToken = req.headers("authorization");
        JoinRequest joinRequest = new Gson().fromJson(req.body(), model.JoinRequest.class);
        joinRequest = new JoinRequest(authToken, joinRequest.playerColor(), joinRequest.gameID());

        JoinResult joinResult = gameService.joinGame(joinRequest);
        res.status(200);
        return new Gson().toJson(joinResult);
    }

    private Object clear(Request req, Response res) throws SQLException, DataAccessException {
        clearService.clear();
        res.status(200);
        return "";
    }
}
