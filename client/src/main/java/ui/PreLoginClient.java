package ui;

import serverfacade.ServerFacade;

public class PreLoginClient {
    private final ServerFacade server;
    private final String url;

    public PreLoginClient(String url, ServerFacade server) {
        this.server = server;
        this.url = url;
    }
}
