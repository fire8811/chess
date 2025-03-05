package server;
import dataaccess.*;
import model.ErrorMessage;
import model.RegisterRequest;
import com.google.gson.Gson;
import service.ClearService;
import service.UserService;
import spark.*;

import javax.xml.crypto.Data;

public class Server {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private ClearService clearService;
    private UserService userService;

    public Server(){
        this.userDAO = new MemoryUserDAO();
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();

        clearService = new ClearService(authDAO, userDAO, gameDAO);
        userService = new UserService(authDAO, userDAO, gameDAO);
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
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
        else {
            res.status(500);
        }

        res.body(new Gson().toJson(new ErrorMessage("Error: " + ex.getMessage())));

        //make new exception files that inherit from DataAccessException. Each method will say they throw a DAE but
        // they actually throw the 'correct' error with corresponding message (check
        //doc to see what each message needs to be). Then here you check to see what was the 'type' of the error
        //and then set a status code variable to be the correct code in a series of if statements.
    }

    private Object register(Request req, Response res) throws DataAccessException{
        var registerRequest = new Gson().fromJson(req.body(), model.RegisterRequest.class);
        var registerResult = userService.register(registerRequest);
        res.status(200);
        return new Gson().toJson(registerResult);
    }

    private Object clear(Request req, Response res)  {
        clearService.clear();
        res.status(200);
        return "";
    }
}
