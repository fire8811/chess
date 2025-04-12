package ui;

import serverfacade.ServerFacade;
import websocket.ServerMessageHandler;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;
import java.util.Scanner;



public class Repl implements ServerMessageHandler {
    private StageManager stageManager;
    private final ServerFacade server;

    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GamePlayClient gamePlayClient;

    public Repl(String url) {
        server = new ServerFacade(url);
        stageManager = new StageManager();

        preLoginClient = new PreLoginClient(url, server, stageManager);
        postLoginClient = new PostLoginClient(url, server, stageManager, this);
        gamePlayClient = new GamePlayClient(url, server, stageManager);
    }

    public void run(){
        System.out.println(SET_TEXT_COLOR_BLUE + "Welcome to chess! Register or sign in to begin.");
        System.out.println("Type h or help to see possible commands");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = getCorrectClient().eval(line);
                System.out.println(result);

            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public Client getCorrectClient(){
        switch (stageManager.getStage()) {
            case PRELOGIN -> {
                return preLoginClient;
            }
            case POSTLOGIN -> {
                return postLoginClient;
            }
            case IN_GAME -> {
                return gamePlayClient;
            }
            default -> throw new IllegalStateException("STAGE ERROR: " + stageManager.getStage());
        }
    }

    public void printPrompt() {
        System.out.print(SET_TEXT_COLOR_GREEN + ">>> ");
    }

    public void displayMessage(ServerMessage message){//display websocket messages from server to UI
        switch (message.getServerMessageType()){
            case NOTIFICATION -> printNotification((NotificationMessage) message);
            case LOAD_GAME -> printBoard((LoadGameMessage) message);
        }
    }

    private void printNotification(NotificationMessage message) {
        System.out.println(SET_TEXT_COLOR_MAGENTA + SET_TEXT_ITALIC + message.getMessage() + RESET_TEXT_ITALIC);
        printPrompt();
    }

    private void printBoard(LoadGameMessage message) {
        System.out.println("LOAD BOARD MESSAGE RECEIVED");
    }
}
