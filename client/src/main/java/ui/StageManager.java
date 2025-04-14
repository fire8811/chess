package ui;

import websocket.WebSocketFacade;

enum ClientStage {
    PRELOGIN, POSTLOGIN, IN_GAME;
}

public class StageManager {
    private ClientStage stage;
    private static String authToken;
    private int gameID;

    public StageManager(){
        stage = ClientStage.PRELOGIN;
    }

    public ClientStage getStage() {
        return stage;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getGameID(){
        return gameID;
    }

    public void setStage(ClientStage newStage){
        this.stage = newStage;
    }

    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    public String getAuthToken(){
        return authToken;
    }

    public void deleteToken(){
        setAuthToken(null);
    }
}
