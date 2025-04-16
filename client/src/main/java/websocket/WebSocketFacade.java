package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exceptions.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;
import static websocket.commands.UserGameCommand.CommandType.*;

//client side
public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;
    private static WebSocketFacade ws;
    //I'll need to serialize and send the game command to the server which then deserializes it
    //I then need to be able to parse serverMessages received here and broadcast them

    private WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException { //used in join or observe game
        try{
            url = url.replace("http", "ws");
            URI uri = new URI(url + "/ws");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    ServerMessage serverMessage = new Gson().fromJson(s, ServerMessage.class);

                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION -> {
                            NotificationMessage notificationMessage = new Gson().fromJson(s, NotificationMessage.class);
                            serverMessageHandler.displayMessage(notificationMessage); //goes directly to the REPL
                        }
                        case LOAD_GAME -> {
                            LoadGameMessage loadGameMessage = new Gson().fromJson(s, LoadGameMessage.class);
                            serverMessageHandler.displayMessage(loadGameMessage); //goes directly to the REPL
                        }
                        case ERROR -> {
                            ErrorMessage errorMessage = new Gson().fromJson(s, ErrorMessage.class);
                            serverMessageHandler.displayMessage(errorMessage);
                        }
                    }

                }
            });

        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public static void initWS(String url, ServerMessageHandler handler){
        if (ws == null){
            ws = new WebSocketFacade(url, handler);
        }
    }

    public static WebSocketFacade getWS(){
        return ws;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(Integer gameID, String authToken, ChessGame.TeamColor teamColor) throws ResponseException{
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            command.setTeamColor(teamColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public void observeGame(Integer gameID, String authToken) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            command.setObserverStatus(true);

            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            System.out.println(SET_TEXT_COLOR_RED + "ERRRRROR: " + e.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move, ChessGame.TeamColor teamColor,
                         String startMove, String endMove){
        try{
            var command = new MakeMoveCommand(MAKE_MOVE, authToken, gameID, move, startMove, endMove);
            command.setTeamColor(teamColor);

            this.session.getBasicRemote().sendText(new Gson().toJson(command));

        } catch (IOException e) {
            System.out.println(SET_TEXT_COLOR_RED + "makemoveerror: " + e.getMessage());
        }
    }

    public void resign(String authToken, int gameID)  {
        try {
            var command = new UserGameCommand(RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));

        } catch (IOException e) {
            System.out.print(SET_TEXT_COLOR_RED + "Resign Error: " + e.getMessage());
        }

    }

    public void leaveGame(String authToken, int gameID) {
        try {
            var command = new UserGameCommand(LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));

        } catch (IOException e){
            System.out.print(SET_TEXT_COLOR_RED + "Leave Error: " + e.getMessage());
        }
    }

//    public void getChessMoves(String authToken, int gameID) {
//        try {
//            this.session.getBasicRemote()
//        }
//    }
}
