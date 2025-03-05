package server;
import dataaccess.*;
import service.ClearService;
import spark.*;

public class Server {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private ClearService clearService;

    public Server(){
        this.userDAO = new MemoryUserDAO();
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();

        clearService = new ClearService(authDAO, userDAO, gameDAO);
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res)  {
        clearService.clear();
        res.status(200);
        return "";
    }
}
