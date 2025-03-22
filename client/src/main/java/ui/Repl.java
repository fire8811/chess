package ui;

import serverfacade.ServerFacade;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final ServerFacade server;
    public Repl(String url) {
        server = new ServerFacade(url);
        preLoginClient = new PreLoginClient(url, server);

    }
}
