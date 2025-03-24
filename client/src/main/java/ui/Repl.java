package ui;

import serverfacade.ServerFacade;
import static ui.EscapeSequences.*;
import java.util.Scanner;



public class Repl {
    private StageManager stageManager;
    private final ServerFacade server;

    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GamePlayClient gamePlayClient;

    public Repl(String url) {
        server = new ServerFacade(url);
        stageManager = new StageManager();

        preLoginClient = new PreLoginClient(url, server, stageManager);
        postLoginClient = new PostLoginClient(url, server, stageManager);
        gamePlayClient = new GamePlayClient(url, server, stageManager);
    }

    public void run(){
        System.out.println("Welcome to chess! Register or sign in to begin.");
        System.out.println("Type h or help to see possible commands");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit") || !result.equals("q")) {
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
        System.out.println(">>> " + SET_TEXT_COLOR_GREEN);
    }


}
