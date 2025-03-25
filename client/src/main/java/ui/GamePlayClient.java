package ui;

import exceptions.ResponseException;
import serverfacade.ServerFacade;

import java.util.Arrays;

public class GamePlayClient implements Client {
    private final ServerFacade server;
    private final String url;
    private final  StageManager stageManager;

    public GamePlayClient(String url, ServerFacade server, StageManager stageManager) {
        this.server = server;
        this.url = url;
        this.stageManager = stageManager;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length); //parameters that follow the command (like a username)

            return switch(cmd){
                default -> help();
            };

        } catch (ResponseException e){
            return e.getMessage();
        }
    }

    private String help() {
        return "";
    }
}
