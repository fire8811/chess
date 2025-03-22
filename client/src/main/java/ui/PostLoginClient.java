package ui;

import serverfacade.ServerFacade;

public class PostLoginClient {
    private final ServerFacade server;
    private final String url;

    public PostLoginClient(String url, ServerFacade server) {
        this.server = server;
        this.url = url;
    }
}
