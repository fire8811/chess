package ui;

import serverfacade.ServerFacade;

public class GamePlayClient {
    private final ServerFacade server;
    private final String url;

    public GamePlayClient(String url, ServerFacade server) {
        this.server = server;
        this.url = url;
    }
}
