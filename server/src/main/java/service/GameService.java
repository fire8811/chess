package service;

import dataaccess.AuthDAO;
import dataaccess.BadRequestException;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import model.*;

import java.util.ArrayList;

public class GameService {
    private final GameDAO games;
    private final AuthDAO auth;

    public GameService(AuthDAO auth, GameDAO games){
        this.auth = auth;
        this.games = games;
    }

    public ListResult listGames(ListRequest request) throws UnauthorizedException {
        String authToken = request.authToken();
        auth.findAuthToken(authToken); //verify authToken

        return new ListResult(games.getGamesFromMemory());
    }

    public CreateResult createGame(CreateRequest request) throws UnauthorizedException, BadRequestException {
        if (request.gameName() == null){
            throw new BadRequestException("bad request");
        }

        String authToken = request.authToken();
        auth.findAuthToken(authToken);

        int gameID = games.createGame(request.gameName());
        return new CreateResult((gameID));
    }
}
