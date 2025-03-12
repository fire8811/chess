package service;

import dataaccess.*;
import model.*;

import java.util.ArrayList;

public class GameService {
    private final GameDAO games;
    private final AuthDAO auth;

    public GameService(AuthDAO auth, GameDAO games){
        this.auth = auth;
        this.games = games;
    }

//    public ListResult listGames(ListRequest request) throws UnauthorizedException {
//        String authToken = request.authToken();
//        auth.authTokenExists(authToken); //verify authToken
//
//        return new ListResult(games.listGames());
//    }
//
//    public CreateResult createGame(CreateRequest request) throws UnauthorizedException, BadRequestException {
//        if (request.gameName() == null){
//            throw new BadRequestException("bad request");
//        }
//
//        String authToken = request.authToken();
//        auth.authTokenExists(authToken);
//
//        int gameID = games.createGame(request.gameName());
//        return new CreateResult((gameID));
//    }
//
//    public JoinResult joinGame(JoinRequest request) throws UnauthorizedException, BadRequestException, AlreadyTakenException {
//        String authToken = request.authToken();
//        auth.authTokenExists(authToken);
//        String username = auth.getUsername(authToken);
//
//        Integer gameID = request.gameID();
//        if (gameID == null){
//            throw new BadRequestException("bad request");
//        }
//
//        boolean gameExists = games.findGame(gameID);
//        if (gameExists){
//            games.updateGame(gameID, request.playerColor(), username);
//        }
//        else {
//            throw new BadRequestException("bad request");
//        }
//
//        return new JoinResult(request.playerColor(), gameID);
//    }
}
